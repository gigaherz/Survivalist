package gigaherz.survivalist;

import gigaherz.survivalist.rack.ContainerRack;
import gigaherz.survivalist.rack.GuiRack;
import gigaherz.survivalist.rack.TileRack;
import gigaherz.survivalist.sawmill.TileSawmill;
import gigaherz.survivalist.sawmill.gui.ContainerSawmill;
import gigaherz.survivalist.sawmill.gui.GuiSawmill;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler
{
    public static final int GUI_RACK = 0;
    public static final int GUI_SAWMILL = 1;

    @Nullable
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

        switch (id)
        {
            case GUI_RACK:
                if (tileEntity instanceof TileRack)
                {
                    return new ContainerRack((TileRack) tileEntity, player.inventory);
                }
                break;
            case GUI_SAWMILL:
                if (tileEntity instanceof TileSawmill)
                {
                    return new ContainerSawmill((TileSawmill) tileEntity, player.inventory);
                }
                break;
        }

        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

        switch (id)
        {
            case GUI_RACK:
                if (tileEntity instanceof TileRack)
                {
                    return new GuiRack((TileRack) tileEntity, player.inventory);
                }
                break;
            case GUI_SAWMILL:
                if (tileEntity instanceof TileSawmill)
                {
                    return new GuiSawmill((TileSawmill) tileEntity, player.inventory);
                }
                break;
        }

        return null;
    }
}
