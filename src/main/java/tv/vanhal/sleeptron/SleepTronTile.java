package tv.vanhal.sleeptron;

import java.util.UUID;

import tv.vanhal.sleeptron.world.SleepStatus;
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
	protected String lastKnownName = "";
	
	public SleepTronTile() {
	}
	
	public void setOwner(EntityPlayer inPlayer) {
		if (!worldObj.isRemote) {
			//SleepTron.logger.info("Owner: "+inPlayer.getDisplayNameString());
			if (this.ownerUUID==null) {
				this.owner = inPlayer;
				this.ownerUUID = inPlayer.getUniqueID();
				this.lastKnownName = inPlayer.getDisplayNameString();
				inPlayer.addChatComponentMessage(new TextComponentTranslation("chat.online.text", new Object[0]));
			} else {
				this.owner = worldObj.getPlayerEntityByUUID(this.ownerUUID);
			}
		}
	}
	
	@Override
	public final NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);
		if (this.ownerUUID!=null) 
			nbt.setUniqueId("OwnerUUID", this.ownerUUID);
		if (this.lastKnownName!="")
			nbt.setString("LastUserName", this.lastKnownName);
		return nbt;
	}
	
	@Override
	public final void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if (nbt.hasUniqueId("OwnerUUID"))
			this.ownerUUID = nbt.getUniqueId("OwnerUUID");
		if (nbt.hasKey("LastUserName"))
			this.lastKnownName = nbt.getString("LastUserName");
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
				this.doSleepCycle();
			} else {
				this.markInactive();
			}
		} else {
			
			//if inverted sleep through the day
			if ( (this.isActive()) && (this.isInverted()) && (STConfig.INVERTABLE) ) {
				this.doSleepCycle();
			} else {
				this.markInactive();
			}
		}
	}
	
	protected void doSleepCycle() {
		if (!SleepStatus.getInstance(worldObj).isActive(this.ownerUUID)) {
			net.minecraftforge.event.ForgeEventFactory.onPlayerSleepInBed(this.owner, this.owner.playerLocation);
			SleepStatus.getInstance(worldObj).markActive(this.ownerUUID);
		}
	}

	protected void markInactive() {
		if (SleepStatus.getInstance(worldObj).isActive(this.ownerUUID)) {
			SleepStatus.getInstance(worldObj).markInActive(this.ownerUUID);
		}
	}

	public String getOwnerName() {
		if (this.owner==null) {
			if (this.ownerUUID!=null) {
				this.owner = worldObj.getPlayerEntityByUUID(this.ownerUUID);
			}
			
		}
		if (this.owner==null) {
			if (this.lastKnownName=="")
				return "Unknown Player";
			else
				return this.lastKnownName;
		}
		this.lastKnownName = this.owner.getDisplayNameString();
		return this.lastKnownName;
	}
}
