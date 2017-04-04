package tv.vanhal.sleeptron.world;

import tv.vanhal.sleeptron.SleepTron;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;

public class WorldTicker {
	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event) {
		if ( (event.side == Side.SERVER) && (event.phase == Phase.END) ) {
			if (this.areAllPlayersAsleep(event.world)) {
				if (event.world.getGameRules().getBoolean("doDaylightCycle")) {
		            long delta = event.world.getWorldInfo().getWorldTime() % 24000L;
		            long baseDay = event.world.getWorldInfo().getWorldTime() - delta;
		            long j = 24000L;
		            if (event.world.isDaytime()) {
		            	j = 12550L;
		            	if (delta>=j) j += 24000L;
		            }
		            event.world.getWorldInfo().setWorldTime(baseDay + j);
		            this.wakeAllPlayers(event.world);
		        }
			}
		}
	}
	
	public boolean areAllPlayersAsleep(World worldObj) {
		if (worldObj.playerEntities.isEmpty()) return false;
        for (EntityPlayer entityplayer : worldObj.playerEntities) {
            if (!entityplayer.isSpectator() && !entityplayer.isPlayerFullyAsleep()) {
            	//check the sleep tron
            	if (!SleepStatus.getInstance(worldObj).isActive(entityplayer.getUniqueID())) {
            		return false;
            	}
            }
        }
        return true;
	}
	
	public void wakeAllPlayers(World worldObj) {
		for (EntityPlayer entityplayer : worldObj.playerEntities) {
            if (entityplayer.isPlayerSleeping()) {
                entityplayer.wakeUpPlayer(false, false, true);
            } else if (SleepStatus.getInstance(worldObj).isActive(entityplayer.getUniqueID())) {
            	entityplayer.addChatComponentMessage(new TextComponentTranslation("chat.active.text", new Object[0]));
            	SleepStatus.getInstance(worldObj).markInActive(entityplayer.getUniqueID());
            }
        }
		worldObj.provider.resetRainAndThunder();
	}
}
