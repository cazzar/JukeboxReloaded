package cazzar.mods.jukeboxreloaded.lib.util;

import cazzar.mods.jukeboxreloaded.JukeboxReloaded;
import cazzar.mods.jukeboxreloaded.blocks.TileJukeBox;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.ItemRecord;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.PlayStreamingEvent;
import net.minecraftforge.client.event.sound.PlayStreamingSourceEvent;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import de.cuina.fireandfuel.CodecJLayerMP3;

public class SoundSystemHelper {
	private static boolean		registeredCodecs	= false;
	private static GameSettings	options				= Minecraft.getMinecraft().gameSettings;
	
	public static SoundManager getSoundManager() {
		return Minecraft.getMinecraft().sndManager;
	}
	
	public static SoundSystem getSoundSystem() {
		return SoundManager.sndSystem;
	}
	
	public static void registerCodecs() {
		if (registeredCodecs)
			throw new RuntimeException(
					"We cannot register the codecs more than once!");
		SoundSystemConfig.setCodec("mp3", CodecJLayerMP3.class);
	}
	
	TileJukeBox	tile;
	Side		side;
	
	public SoundSystemHelper(TileJukeBox tile) {
		this.tile = tile;
		side = JukeboxReloaded.instance().proxy().getEffectiveSide();
	}
	
	public void playRecord(String record, World world, float x, float y,
			float z, float volume) {
		if (side.isServer()) return;
		
		SoundSystem sndSystem = getSoundSystem();
		
		ItemRecord itemrecord = ItemRecord.getRecord(record);
		if (itemrecord == null) return;
		
		Minecraft.getMinecraft().ingameGUI.setRecordPlayingMessage(itemrecord
				.getRecordTitle());
		if (sndSystem.playing(tile.getIdentifier())) sndSystem.stop(tile.getIdentifier());
		
		SoundPoolEntry song = getSoundManager().soundPoolStreaming
				.getRandomSoundFromSoundPool(record);
		song = SoundEvent.getResult(new PlayStreamingEvent(getSoundManager(),
				song, tile.getIdentifier(), x, y, z));
		
		if (song == null) return;
		if (sndSystem.playing("BgMusic")) sndSystem.stop("BgMusic");
		
		float f3 = 16.0F;
		sndSystem.newStreamingSource(true, tile.getIdentifier(), song.soundUrl,
				song.soundName, false, x, y, z, 2, f3 * 4.0F);
		sndSystem.setVolume(tile.getIdentifier(), volume * options.soundVolume);
		MinecraftForge.EVENT_BUS.post(new PlayStreamingSourceEvent(
				getSoundManager(), tile.getIdentifier(), x, y, z));
		sndSystem.play(tile.getIdentifier());
	}
	
	public void pause() {
		if (side.isServer()) return;
		
		getSoundSystem().pause(tile.getIdentifier());
	}
	
	public void resume() {
		if (side.isServer()) return;
		
		getSoundSystem().play(tile.getIdentifier());
	}
	
	public void stop() {
		if (side.isServer()) return;
		
		getSoundSystem().stop(tile.getIdentifier());
	}
	
	public static void stop(String identifier) {
		if (JukeboxReloaded.instance().proxy().getEffectiveSide().isServer()) return;
		
		getSoundSystem().stop(identifier);
	}
	
	public Boolean isPlaying() {
		if (side.isServer()) return null;
		
		return getSoundSystem().playing(tile.getIdentifier());
	}
}
