package gigaherz.survivalist.client;

import gigaherz.survivalist.IModProxy;
import gigaherz.survivalist.network.UpdateFields;
import gigaherz.survivalist.sawmill.gui.ContainerSawmill;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Quaternion;

import javax.vecmath.Matrix3d;

public class ClientProxy implements IModProxy
{
    public void preInit()
    {
    }

    @Override
    public void handleUpdateField(final UpdateFields message)
    {
        Minecraft.getMinecraft().addScheduledTask(() ->
        {
            Minecraft gameController = Minecraft.getMinecraft();

            EntityPlayer entityplayer = gameController.player;

            if (entityplayer.openContainer != null && entityplayer.openContainer.windowId == message.windowId)
            {
                ((ContainerSawmill) entityplayer.openContainer).updateFields(message.fields);
            }
        });
    }
}
