package tv.vanhal.sleeptron.world;

import net.minecraft.nbt.NBTTagCompound;

public class PlayerStatus {
	public int sleeptimes;
	public boolean givenST;
	public boolean active;
	
	public PlayerStatus() {
		this.sleeptimes = 0;
		this.givenST = false;
		this.active = false;
	}
	
	public PlayerStatus(NBTTagCompound tag) {
		super();
		this.loadNBT(tag);
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public void markActive() {
		this.active = true;
	}
	
	public void markInActive() {
		this.active = false;
	}
	
	public void incrSleep() {
		this.sleeptimes++;
	}
	
	public NBTTagCompound getNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("given", this.givenST);
		nbt.setBoolean("active", this.active);
		nbt.setInteger("times", this.sleeptimes);
		return nbt;
	}
	
	public void loadNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("given")) this.givenST = nbt.getBoolean("given");
		if (nbt.hasKey("active")) this.active = nbt.getBoolean("active");
		if (nbt.hasKey("times")) this.sleeptimes = nbt.getInteger("times");
	}
}
