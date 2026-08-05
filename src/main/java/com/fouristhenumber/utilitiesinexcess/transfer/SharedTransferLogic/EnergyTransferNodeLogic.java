package com.fouristhenumber.utilitiesinexcess.transfer.SharedTransferLogic;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.InvWrapper;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import com.fouristhenumber.utilitiesinexcess.UtilitiesInExcess;
import com.fouristhenumber.utilitiesinexcess.common.tileentities.transfer.TileEntityEnergyTransferNode;
import com.fouristhenumber.utilitiesinexcess.transfer.upgrade.UpgradeInventory;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.EnergyWalker;
import com.fouristhenumber.utilitiesinexcess.transfer.walk.targeting.TargetResolver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.StatCollector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnergyTransferNodeLogic extends BaseNodeLogic<TileEntityEnergyTransferNode>
{
    UpgradeInventory upgrades;


    public EnergyWalker walker;
    public Set<IEnergyProvider> sources = new HashSet<IEnergyProvider>();
    public Set<IEnergyReceiver> sinks = new HashSet<IEnergyReceiver>();
    public int containedEnergy = 0;

    private int MAX_CAPACITY = 10000;
    private int MAX_TRANSFER = 10000;

    boolean hyperInit = false;
    public EnergyTransferNodeLogic(TileEntityEnergyTransferNode host)
    {
        super(host);
        walker = new EnergyWalker(host);
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
    public void updateEntity()
    {
        if (host.getWorld().isRemote)
        {
            return;
        }

        if (!hyperInit)
        {
            if (host.getWorld().getBlockMetadata(host.xCoord, host.yCoord, host.zCoord) == 1)
            {
                MAX_CAPACITY = 1000000;
                MAX_TRANSFER = 25000;
            }
            hyperInit = true;
        }
        if (!sources.isEmpty())
        {
            importEnergy();
        }

        if (!sinks.isEmpty())
        {
            exportEnergy();
        }

        if (host.getWorld().getTotalWorldTime() % 20 != 0)
        {
            return;
        }

        List<TargetResolver.Target<IEnergyConnection>> targets = walker.getValidTargets(host.getWorld());
        if (!targets.isEmpty())
        {
            for (TargetResolver.Target<IEnergyConnection> target : targets)
            {
                if (walker.isOnExtractionPipe(host.getWorld()))
                {
                    if (target.handler instanceof IEnergyProvider source)
                    {

                        sources.add(source);
                    }
                }
                else if (walker.isAtOrigin()) // Means we're on the node
                {
                    if (target.handler instanceof IEnergyProvider source)
                    {
                        sources.add(source);
                    }
                    else if (target.handler instanceof IEnergyReceiver sink)
                    {
                        sinks.add(sink);
                    }
                }
                else if (target.handler instanceof IEnergyReceiver sink)
                {
                    sinks.add(sink);
                }
            }
        }
        walker.step(host.getWorld());
    }


    public void importEnergy()
    {
        if (sources.isEmpty() || containedEnergy >= MAX_CAPACITY)
        {
            return;
        }

        int space = MAX_CAPACITY - containedEnergy;
        int totalTransfer = Math.min(space, MAX_TRANSFER);

        int count = sources.size();
        int perSource = totalTransfer / count;
        int remainder = totalTransfer % count;

        for (IEnergyProvider provider : sources)
        {
            if (provider == null)
            {
                continue;
            }

            int request = perSource;

            if (remainder > 0)
            {
                request++;
                remainder--;
            }

            if (request <= 0)
            {
                continue;
            }

            int extracted = provider.extractEnergy(null, request, false);

            if (extracted > 0)
            {
                containedEnergy += extracted;
            }
        }
    }

    public void exportEnergy()
    {
        if (sinks.isEmpty() || containedEnergy <= 0)
        {
            return;
        }

        int totalTransfer = Math.min(containedEnergy, MAX_TRANSFER);

        int count = sinks.size();
        int perSink = totalTransfer / count;
        int remainder = totalTransfer % count;

        for (IEnergyReceiver receiver : sinks)
        {
            if (receiver == null)
            {
                continue;
            }

            int offer = perSink;

            if (remainder > 0)
            {
                offer++;
                remainder--;
            }

            if (offer <= 0)
            {
                continue;
            }

            int accepted = receiver.receiveEnergy(null, offer, false);

            if (accepted > 0)
            {
                containedEnergy -= accepted;
            }
        }
    }

    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        NBTTagInt energy = new NBTTagInt(containedEnergy);
        nbt.setTag("Energy", energy);
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        containedEnergy = nbt.getInteger("Energy");
    }

    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings)
    {
        StringSyncValue searchLocationSyncer = new StringSyncValue(() ->
            "Holding: " + containedEnergy + " RF\n" +
            "Powering: " + (sources.size() + sinks.size()) + " Connections\n" +
            "Search Location: " + walker.getLocationString());
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
        );

        Flow flow = Flow.row();
        flow.pos(34,60).size(108,18);
        for (int i = 0; i < upgrades.getSizeInventory(); i++)
        {
            flow.child(new ItemSlot().slot(new ModularSlot(itemHandler,i).slotGroup(upgradeSlotGroup).changeListener(upgrades)));
        }
        panel.child(flow);


        return panel;
    }

    @SideOnly(Side.CLIENT)
    public ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(UtilitiesInExcess.MODID, mainPanel);
    }
}
