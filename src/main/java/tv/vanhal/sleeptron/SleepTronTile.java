package tv.vanhal.sleeptron;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.TextComponentTranslation;

public class SleepTronTile extends TileEntity implements ITickable {
	protected EntityPlayer owner = null;
	protected UUID ownerUUID = null;
	
	public SleepTronTile() {
		SleepTron.logger.info("Init");
	}
	
	public void setOwner(EntityPlayer inPlayer) {
		if (!worldObj.isRemote) {
			SleepTron.logger.info("Owner: "+inPlayer.getDisplayNameString());
			if (this.ownerUUID==null) {
				this.owner = inPlayer;
				this.ownerUUID = inPlayer.getUniqueID();
				inPlayer.addChatComponentMessage(new TextComponentTranslation("Sleep Tron Online", new Object[0]));
			} else {
				this.owner = worldObj.getPlayerEntityByUUID(this.ownerUUID);
			}
		}
	}
	
	@Override
	public final NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);
		if (this.ownerUUID!=null) {
			nbt.setUniqueId("OwnerUUID", this.ownerUUID);
		}
		return nbt;
	}
	
	@Override
	public final void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if (nbt.hasUniqueId("OwnerUUID")) {
			this.ownerUUID = nbt.getUniqueId("OwnerUUID");
		}
	}
	
	public boolean isActive() {
		if ( (this.owner == null) && (this.ownerUUID!=null) ) {
			this.owner = worldObj.getPlayerEntityByUUID(this.ownerUUID);
		}
		if (this.owner == null) {
			return false;
		}
		if (worldObj.getBlockState(this.pos.down()).getBlock() != Blocks.BED) {
			return false;
		}
		if (worldObj.getStrongPower(pos) < 15) {
			return false;
		}
		return true;
	}
	
	public boolean isInverted() {
		return ( (worldObj.getBlockState(this.pos.up()).getBlock() == Blocks.REDSTONE_TORCH) ||
				(worldObj.getBlockState(this.pos.up()).getBlock() == Blocks.UNLIT_REDSTONE_TORCH) );
	}
	
	@Override
	public void update() {
		//check if night time
		if (!worldObj.isDaytime()) {
			if ( (this.isActive()) && (!this.isInverted()) ) {
				SleepTron.logger.info("Night Sleeping");
				this.doSleepCycle();
			}
		} else {
			//if inverted sleep through the day
			if ( (this.isActive()) && (this.isInverted()) ) {
				SleepTron.logger.info("Day Sleeping");
				this.doSleepCycle();
			}
		}
	}
	
	protected boolean doSleepCycle() {
		this.owner.addChatComponentMessage(new TextComponentTranslation("Sleep Tron Activated", new Object[0]));
		net.minecraftforge.event.ForgeEventFactory.onPlayerSleepInBed(this.owner, this.owner.playerLocation);
		if (worldObj.getGameRules().getBoolean("doDaylightCycle")) {
            long delta = worldObj.getWorldInfo().getWorldTime() % 24000L;
            long baseDay = worldObj.getWorldInfo().getWorldTime() - delta;
            long j = 24000L;
            if (this.isInverted()) {
            	j = 12550L;
            	if (delta>=j) j += 24000L;
            }
            worldObj.getWorldInfo().setWorldTime(baseDay + j);
        }

		for (EntityPlayer entityplayer : worldObj.playerEntities) {
            if (entityplayer.isPlayerSleeping()) {
                entityplayer.wakeUpPlayer(false, false, true);
            }
        }
		worldObj.provider.resetRainAndThunder();
		return false;
	}

}
