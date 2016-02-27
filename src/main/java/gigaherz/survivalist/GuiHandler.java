package gigaherz.survivalist;

import gigaherz.survivalist.rack.ContainerRack;
import gigaherz.survivalist.rack.GuiRack;
import gigaherz.survivalist.rack.TileRack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
    public static final int GUI_RACK = 0;

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
        }

        return null;
    }

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
        }

        return null;
    }
}
