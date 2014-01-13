package net.cazzar.mods.jukeboxreloaded.network.packets;

import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.cazzar.corelib.lib.SoundSystemHelper;
import net.cazzar.corelib.util.ClientUtil;
import net.cazzar.mods.jukeboxreloaded.blocks.TileJukebox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;

/**
 * @author Cayde
 */
public class PacketStopPlaying extends PacketJukebox {

    public int x, y, z;

    public PacketStopPlaying() {
    }

    public PacketStopPlaying(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void execute(EntityPlayer player, Side side) throws ProtocolException {
        final TileEntity te = player.worldObj.func_147438_o(x, y, z);
        if (te instanceof TileJukebox) {
            ((TileJukebox) te).setPlaying(false);
            ((TileJukebox) te).waitTicks = 20;
            ((TileJukebox) te).markForUpdate();
        }

        if (side.isServer()) {
            sendToAllPlayers();
            return;
        }


        SoundSystemHelper.stop(ClientUtil.mc().renderGlobal, new ChunkCoordinates(x, y, z));
    }

    @Override
    public void read(ByteBuf in) {
        x = in.readInt();
        y = in.readInt();
        z = in.readInt();
    }

    @Override
    public void write(ByteBuf out) {
        out.writeInt(x);
        out.writeInt(y);
        out.writeInt(z);
    }

}
