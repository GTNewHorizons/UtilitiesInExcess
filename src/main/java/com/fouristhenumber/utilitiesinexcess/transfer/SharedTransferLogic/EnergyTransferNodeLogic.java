package com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic;

import static com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.IConnectable.getConnectables;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.InvWrapper;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import com.fouristhenumber.utilitiesinexcess.common.blocks.transfer.IConnectable;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.IWirelessNode;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityEnergyTransferNode;
import com.fouristhenumber.utilitiesinexcess.transfer.upgrade.WirelessNetworkManager;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.EnergyWalker;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.WalkerBase;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.stepper.RandomStepper;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting.TargetResolver;
import com.gtnewhorizon.gtnhlib.util.CoordinatePacker;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class EnergyTransferNodeLogic extends BaseNodeLogic implements IEnergyHandler {

    public EnergyWalker walker;

    // Because I can't find a cleaner way to do this, I'm packing generation and side in this same
    // map. First 3 bits are the side to push/pull from and the rest of the bits are the generation of scan it's on.
    // Generation of scan is used for invalidating old items. If they haven't been seen between generations of the
    // walker then we remove them from the map.
    public Long2IntOpenHashMap sources = new Long2IntOpenHashMap();
    public Long2IntOpenHashMap sinks = new Long2IntOpenHashMap();
    public int containedEnergy = 0;

    private int MAX_CAPACITY = 10000;
    private int MAX_TRANSFER = 10000;
    private final int WIRELESS_TRANSFER_SPEED = 250;

    private boolean init = false;
    private int scanGeneration = 0;

    // Upgrades
    private boolean isCreative = false;
    private final Object2IntOpenHashMap<String> pushingFrequencies = new Object2IntOpenHashMap<>();

    public EnergyTransferNodeLogic(IWalkingComponent host) {
        super(host);
        walker = new EnergyWalker(host);
        sources.defaultReturnValue((byte) -1);
        sinks.defaultReturnValue((byte) -1);
    }

    // Energy nodes seem to have a few rules.
    // 1. If the walker finds a IEnergyReceiver on a normal pipe it's treated as a receiver even if it's an
    // IEnergyProvider.
    // 2. If the walker finds a IEnergyProvider adjacent to the node, it's treated as a provider even if it's also
    // an IEnergyReceiver.
    // 3. If the walker finds a IEnergyProvider adjacent to an energy extraction pipe it's treated as a
    // provider even if it's an IEnergyReceiver.
    // 4. If the walker finds a IEnergyReceiver that is not an IEnergyProvider adjacent to a node it's treated
    // as a receiver.
    // 5. Walkers of any type may walk through energy nodes in any valid direction.
    // 6. If the walker finds a IEnergyReceiver that is not an IEnergyProvider adjacent to an energy extraction pipe
    // it does not supply it power.
    @Override
    public void updateEntity() {
        if (host.getWorld().isRemote) {
            return;
        }

        if (!init) {
            if (host.getMeta() == 1) {
                MAX_CAPACITY = 1000000;
                MAX_TRANSFER = 25000;
            }
            walker.init();
            upgrades.init();
            init = true;
        }

        if (!sources.isEmpty()) {
            importEnergy();
        }

        if (!sinks.isEmpty() || !pushingFrequencies.isEmpty()) {
            exportEnergy();
        }

        int actionsThisTick = actionsThisTick();
        for (int i = 0; i < actionsThisTick; i++) {
            List<TargetResolver.Target<IEnergyConnection>> targets = walker.getValidTargets(host.getWorld());
            if (!targets.isEmpty()) {
                for (TargetResolver.Target<IEnergyConnection> target : targets) {
                    if (!(target.handler instanceof TileEntityEnergyTransferNode)) {
                        if (walker.isOnExtractionPipe(host.getWorld())) {
                            if (target.handler instanceof IEnergyProvider) {
                                sources.put(
                                    CoordinatePacker.pack(target.x, target.y, target.z),
                                    pack(scanGeneration, target.side));
                            }
                        } else if (walker.isAtOrigin()) // Means we're on the node
                        {
                            if (target.handler instanceof IEnergyProvider) {
                                sources.put(
                                    CoordinatePacker.pack(target.x, target.y, target.z),
                                    pack(scanGeneration, target.side));
                            } else if (target.handler instanceof IEnergyReceiver) {
                                sinks.put(
                                    CoordinatePacker.pack(target.x, target.y, target.z),
                                    pack(scanGeneration, target.side));
                            }
                        } else if (target.handler instanceof IEnergyReceiver) {
                            sinks.put(
                                CoordinatePacker.pack(target.x, target.y, target.z),
                                pack(scanGeneration, target.side));
                        }
                    }
                }
            }
            walker.step(host.getWorld());
            if (walker.isAtOrigin()) {
                sources.long2IntEntrySet()
                    .removeIf(entry -> getGeneration(entry.getIntValue()) != scanGeneration);
                sinks.long2IntEntrySet()
                    .removeIf(entry -> getGeneration(entry.getIntValue()) != scanGeneration);
                scanGeneration++;
            }
        }
    }

    private static int pack(int generation, int side) {
        return (generation << 8) | (side & 0xFF);
    }

    private static int getGeneration(int packed) {
        return packed >>> 8;
    }

    private static byte getSide(int packed) {
        return (byte) (packed & 0xFF);
    }

    public void importEnergy() {
        if (sources.isEmpty() || containedEnergy >= MAX_CAPACITY) {
            return;
        }

        int remaining = Math.min(MAX_CAPACITY - containedEnergy, MAX_TRANSFER);
        boolean changed = false;

        LongArrayList active = new LongArrayList(sources.keySet());

        while (!active.isEmpty() && remaining > 0) {
            for (int i = 0; i < active.size(); i++) {
                long coord = active.getLong(i);

                TileEntity te = host.getWorld()
                    .getTileEntity(
                        CoordinatePacker.unpackX(coord),
                        CoordinatePacker.unpackY(coord),
                        CoordinatePacker.unpackZ(coord));

                if (!(te instanceof IEnergyProvider provider)) {
                    active.removeLong(i--);
                    sources.remove(coord);
                    continue;
                }

                ForgeDirection dir = ForgeDirection.getOrientation(getSide(sources.get(coord)));
                int chunk = Math.max(1, remaining / active.size());

                int extracted = provider.extractEnergy(dir, chunk, false);

                if (extracted > 0) {
                    containedEnergy += extracted;
                    remaining -= extracted;
                    changed = true;
                } else {
                    active.removeLong(i--);
                }

                if (remaining <= 0 || containedEnergy >= MAX_CAPACITY) {
                    if (changed) {
                        host.markHostDirty();
                    }
                    return;
                }
            }
        }

        if (changed) {
            host.markHostDirty();
        }
    }

    // Wireless networks are second to wired networks
    public void exportEnergy() {
        if ((sinks.isEmpty() && pushingFrequencies.isEmpty()) || containedEnergy <= 0) {
            return;
        }

        int remaining = Math.min(containedEnergy, MAX_TRANSFER);
        boolean changed = false;

        LongArrayList active = new LongArrayList(sinks.keySet());

        while (!active.isEmpty() && remaining > 0) {
            for (int i = 0; i < active.size(); i++) {
                long coord = active.getLong(i);

                TileEntity te = host.getWorld()
                    .getTileEntity(
                        CoordinatePacker.unpackX(coord),
                        CoordinatePacker.unpackY(coord),
                        CoordinatePacker.unpackZ(coord));

                if (!(te instanceof IEnergyReceiver receiver)) {
                    active.removeLong(i--);
                    sinks.remove(coord);
                    continue;
                }

                ForgeDirection dir = ForgeDirection.getOrientation(getSide(sinks.get(coord)));
                int chunk = Math.max(1, remaining / active.size());
                int accepted = receiver.receiveEnergy(dir, chunk, false);

                if (accepted > 0) {
                    remaining -= accepted;

                    if (!isCreative) {
                        containedEnergy -= accepted;
                        changed = true;
                    }
                } else {
                    active.removeLong(i--);
                }

                if (remaining <= 0) {
                    if (changed) {
                        host.markHostDirty();
                    }
                    return;
                }
            }
        }

        for (String frequency : pushingFrequencies.keySet()) {
            Int2ObjectOpenHashMap<LongOpenHashSet> receiversByDim = WirelessNetworkManager
                .getReceiversByFrequency(frequency);

            if (receiversByDim == null || receiversByDim.isEmpty()) {
                continue;
            }

            int power = pushingFrequencies.getInt(frequency);
            for (int dim : receiversByDim.keySet()) {
                LongOpenHashSet receivers = receiversByDim.get(dim);
                for (long coord : receivers) {
                    World world = DimensionManager.getWorld(dim);
                    if (world == null) {
                        continue;
                    }

                    int x = CoordinatePacker.unpackX(coord);
                    int y = CoordinatePacker.unpackY(coord);
                    int z = CoordinatePacker.unpackZ(coord);
                    List<IConnectable> connectables = getConnectables(world, x, y, z);

                    // again here we only have to check first becuase the energy nodes are the only possible
                    // connectable in a block if there is on.
                    if (connectables.getFirst() instanceof IWirelessNode receivingNode) {
                        int maxSend = Math.min(remaining, power * WIRELESS_TRANSFER_SPEED);
                        int lossySend = (int) (maxSend * 0.9);
                        int accepted = receivingNode.receiveEnergy(ForgeDirection.UNKNOWN, lossySend, true);
                        if (accepted > 0) {
                            receivingNode.receiveEnergy(ForgeDirection.UNKNOWN, accepted, false);
                            int extracted = (int) Math.ceil(accepted / 0.9);
                            if (!isCreative) {
                                containedEnergy -= extracted;
                                changed = true;
                            }
                            remaining -= extracted;
                        }
                        if (remaining <= 0) {
                            if (changed) {
                                host.markHostDirty();
                            }
                            return;
                        }
                    } else {
                        WirelessNetworkManager.deregisterReceiver(dim, x, y, z);
                    }
                }
            }
        }

        if (changed) {
            host.markHostDirty();
        }
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        int acceptedAmount = Math.min(maxReceive, MAX_CAPACITY - containedEnergy);
        if (!simulate && acceptedAmount > 0) {
            containedEnergy += acceptedAmount;
            host.markHostDirty();
        }
        return acceptedAmount;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        int extractedAmount = Math.min(maxExtract, containedEnergy);
        if (!simulate && extractedAmount > 0) {
            containedEnergy -= extractedAmount;
            host.markHostDirty();
        }
        return extractedAmount;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return containedEnergy;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return MAX_CAPACITY;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    @Override
    public Integer getWalkingObject() {
        return containedEnergy;
    }

    @Override
    public WalkerBase<?> getWalker() {
        return walker;
    }

    @Override
    public void initFromOtherLogic(NetworkLogic logic) {
        super.initFromOtherLogic(logic);
        if (logic instanceof EnergyTransferNodeLogic energyNodeLogic) {
            this.containedEnergy = energyNodeLogic.containedEnergy;
            this.sources = energyNodeLogic.sources;
            this.sinks = energyNodeLogic.sinks;
        }
    }

    // ======================================= Upgrades =======================================
    // Applicable upgrades: Speed, Creative, Transmitter, Receiver
    // Note speed is just changing the stepping speed of the node, nothing to do with the energy transfer
    @Override
    public void resetUpgrades() {
        super.resetUpgrades();
        this.walker.setStepper(new RandomStepper());
        this.isCreative = false;
        if (host.getWorld() != null) {
            WirelessNetworkManager
                .deregisterReceiver(host.getWorld().provider.dimensionId, host.getX(), host.getY(), host.getZ());
            WirelessNetworkManager
                .deregisterTransmitter(host.getWorld().provider.dimensionId, host.getX(), host.getY(), host.getZ());
        }
        pushingFrequencies.clear();
    }

    @Override
    public void applyCreativeUpgrade(ItemStack stack) {
        this.isCreative = true;
    }

    @Override
    public void applyEnderTransmitterUpgrade(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound compound = stack.getTagCompound();
            if (compound.hasKey("Frequency")) {
                String frequency = compound.getString("Frequency");
                WirelessNetworkManager.registerTransmitter(
                    frequency,
                    host.getWorld().provider.dimensionId,
                    host.getX(),
                    host.getY(),
                    host.getZ());
                pushingFrequencies.addTo(frequency, stack.stackSize);
            }
        }
    }

    @Override
    public void applyEnderReceiverUpgrade(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound compound = stack.getTagCompound();
            if (compound.hasKey("Frequency")) {
                WirelessNetworkManager.registerReceiver(
                    compound.getString("Frequency"),
                    host.getWorld().provider.dimensionId,
                    host.getX(),
                    host.getY(),
                    host.getZ());
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagInt energy = new NBTTagInt(containedEnergy);
        nbt.setTag("Energy", energy);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        containedEnergy = nbt.getInteger("Energy");
    }

    @Override
    public void writeDesc(MCDataOutput output) {
        super.writeDesc(output);
        output.writeInt(containedEnergy);
    }

    @Override
    public void readDesc(MCDataInput input) {
        super.readDesc(input);
        containedEnergy = input.readInt();
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        StringSyncValue searchLocationSyncer = new StringSyncValue(
            () -> "Holding: " + containedEnergy
                + " RF\n"
                + "Powering: "
                + (sources.size() + sinks.size())
                + " Connections\n"
                + "Search Location:\n"
                + walker.getLocationString());
        syncManager.syncValue("searchLocationSyncer", searchLocationSyncer);

        SlotGroup upgradeSlotGroup = new SlotGroup("energy_node_upgrades", 1);

        ModularPanel panel = new ModularPanel("panel");
        panel.bindPlayerInventory();

        panel.child(
            new ParentWidget<>().coverChildren()
                .topRelAnchor(0, 1)
                .child(
                    IKey.str(StatCollector.translateToLocal(upgrades.getInventoryName()))
                        .asWidget()
                        .marginLeft(5)
                        .marginRight(5)
                        .marginTop(5)
                        .marginBottom(-15)));

        IItemHandler itemHandler = new InvWrapper(upgrades);

        panel.child(
            IKey.dynamic(searchLocationSyncer::getStringValue)
                .asWidget()
                .marginTop(20)
                .horizontalCenter()
                .textAlign(Alignment.CENTER));

        Flow flow = Flow.row();
        flow.pos(34, 60)
            .size(108, 18);
        for (int i = 0; i < upgrades.getSizeInventory(); i++) {
            flow.child(
                new ItemSlot().slot(
                    new ModularSlot(itemHandler, i).slotGroup(upgradeSlotGroup)
                        .changeListener(upgrades)));
        }
        panel.child(flow);

        return panel;
    }

    public String getInventoryName() {
        return MAX_CAPACITY == 1000000 ? "uie.gui.title.hyper_energy_transfer_node.name"
            : "uie.gui.title.energy_transfer_node.name";
    }
}
