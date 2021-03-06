/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Cayde Dixon
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.cazzar.mods.jukeboxreloaded.network.message

import io.netty.buffer.ByteBuf
import net.cazzar.mods.jukeboxreloaded.Util._
import net.cazzar.mods.jukeboxreloaded.blocks.tileentity.TileJukebox
import net.cazzar.mods.jukeboxreloaded.network.NetworkHandler
import net.minecraft.util.BlockPos
import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}

class ClientActionMessage(var action: Action.Value, var pos: BlockPos) extends IMessage {
  var currentRecord = 0

  def this() = this(null, null)

  override def fromBytes(buf: ByteBuf): Unit = {
    pos = buf.readPos()
    action = Action(buf.readByte())
  }

  override def toBytes(buf: ByteBuf): Unit = {
    buf.writePos(pos)
    buf.writeByte(action.id)
  }
}

object ClientActionMessage {

  class Handler extends IMessageHandler[ClientActionMessage, IMessage] {
    override def onMessage(message: ClientActionMessage, ctx: MessageContext): IMessage = {
      val newMessage = new ServerActionMessage(message.action, message.pos)
      newMessage.currentRecord = message.currentRecord

      val world = ctx.getServerHandler.playerEntity.worldObj
      NetworkHandler.INSTANCE.sendToWorld(newMessage, world)

      val maybeJukebox = world.getTile[TileJukebox](message.pos)
      if (message.action == Action.PLAY) maybeJukebox.foreach(_.setServerPlayingStatus(true))
      if (message.action == Action.STOP) maybeJukebox.foreach(_.setServerPlayingStatus(false))

      null
    }
  }

}
