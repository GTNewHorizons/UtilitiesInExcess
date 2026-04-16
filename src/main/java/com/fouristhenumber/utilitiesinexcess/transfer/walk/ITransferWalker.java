package com.fouristhenumber.utilitiesinexcess.transfer.walk;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.Sys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

//TODO: implent into code
//MAYBE: Make the tile entity search in beginning of search ONLY reset if it is currently a path it is using

// Template class I guess..

public class ITransferWalker {
    TileEntity original;
    TileEntity current;
    String action;

    ArrayList<TileEntity> chests=new ArrayList<TileEntity>();
    ArrayList<TileEntity> tanks=new ArrayList<TileEntity>();
    ArrayList<TileEntity> ignore=new ArrayList<TileEntity>(); // may want to use Map
    ArrayList<TileEntity> lastChoice=new ArrayList<TileEntity>();

    public ArrayList<TileEntity> getAdjacentEntities(TileEntity scan)
    {
        ArrayList<TileEntity> tiles=new ArrayList<TileEntity>();
        World world=scan.getWorldObj();
        tiles.add(world.getTileEntity(scan.xCoord,scan.yCoord-1, scan.zCoord));
        tiles.add(world.getTileEntity(scan.xCoord,scan.yCoord+1, scan.zCoord));
        tiles.add(world.getTileEntity(scan.xCoord,scan.yCoord, scan.zCoord-1));
        tiles.add(world.getTileEntity(scan.xCoord,scan.yCoord, scan.zCoord+1));
        tiles.add(world.getTileEntity(scan.xCoord-1,scan.yCoord, scan.zCoord));
        tiles.add(world.getTileEntity(scan.xCoord+1,scan.yCoord, scan.zCoord));

        return tiles;
    }
    public int[] getRelative(TileEntity main, TileEntity sub)
    {
        return new int[] {main.xCoord-sub.xCoord,main.yCoord-sub.yCoord,main.zCoord-sub.zCoord};
    }

    /**
     * SHOULD BE CALLED FIRST (basically the constructor of the code)
     * The TileEntity of the placed Transfer/Recv
     * @param main TileEntity
     */
    public void setOriginal(TileEntity main) {
        original=main;
        current=original;
        lastChoice.add(main);
    }

    public void step()
    {
    }

    public ArrayList<TileEntity> getItemEntities()
    {
        Collections.shuffle(chests);
        return chests;
    }

    public ArrayList<TileEntity> getFluidEntities()
    {
        Collections.shuffle(tanks);
        return tanks;
    }
    public TileEntity getCurrentTileEntity()
    {
        return current;
    }

    public ForgeDirection getDirectionFromCurrent(TileEntity chest)
    {
        int relX=current.xCoord-chest.xCoord;
        int relY=current.yCoord-chest.yCoord;
        int relZ=current.zCoord-chest.zCoord;
        if (relX>0) {return ForgeDirection.EAST;}
        if (relX<0) {return ForgeDirection.WEST;}
        if (relY>0) {return ForgeDirection.UP;}
        if (relY<0) {return ForgeDirection.DOWN;}
        if (relZ>0) {return ForgeDirection.SOUTH;}
        if (relZ<0) {return ForgeDirection.NORTH;}
        return ForgeDirection.UNKNOWN;
    }

    public String getAction()
    {
        return action;
    }

    public void reset()
    {
        //tanks.clear();
        //chests.clear();
        lastChoice.clear();
        ignore.clear();
        lastChoice.add(original);
        //current=original;
    }

}
