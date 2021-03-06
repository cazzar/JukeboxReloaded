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

package net.cazzar.mods.jukeboxreloaded.util

import net.cazzar.corelib.lib.SoundSystemHelper
import net.cazzar.mods.jukeboxreloaded.JukeboxReloaded
import net.cazzar.mods.jukeboxreloaded.Util._
import net.cazzar.mods.jukeboxreloaded.api.IJukeboxPlayable
import net.minecraft.item.{ItemRecord, ItemStack}
import net.minecraft.util.BlockPos

object PlayUtil {
  def canBePlayed(item: ItemStack, pos: BlockPos): Boolean = Option(item).map(_.getItem).orNull match {
    case _: ItemRecord => true
    case playable: IJukeboxPlayable => playable.canPlay(item)
    case _ => false
  }

  def stop(item: ItemStack, pos: BlockPos) = Option(item).map(_.getItem).orNull match {
    case record: ItemRecord => SoundSystemHelper.stop(record.identifier(pos))
    case playable: IJukeboxPlayable => playable.stop(item, pos)
    case _ => if (item != null) JukeboxReloaded.logger.error("Attempted to stop an item of {} class", item.getClass)
  }

  def play(item: ItemStack, pos: BlockPos) = Option(item).map(_.getItem).orNull match {
    case record: ItemRecord => SoundSystemHelper.playRecord(record, pos.x, pos.y, pos.z, record.identifier(pos))
    case playable: IJukeboxPlayable => playable.play(item, pos)
    case _ => if (item != null) JukeboxReloaded.logger.error("Attempted to play an item of {} class", item.getClass)
  }

  def isPlaying(item: ItemStack, pos: BlockPos) = Option(item).map(_.getItem).orNull match {
    case record: ItemRecord => SoundSystemHelper.isPlaying(record.identifier(pos))
    case playable: IJukeboxPlayable => playable.isPlaying(item, pos);
    case _ =>
      if (item != null) JukeboxReloaded.logger.error("Incorrect type to play: {}", item.getClass)
      false
  }
}
