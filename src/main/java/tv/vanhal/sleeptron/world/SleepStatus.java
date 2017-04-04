package tv.vanhal.sleeptron.world;

import java.util.UUID;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import tv.vanhal.sleeptron.util.Ref;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

//has a map of  players and records how many times they have slept,
//if they have been given a sleep tron and if their sleep tron is active
public class SleepStatus extends WorldSavedData {
	public static String KEY = Ref.MODID+":SleepData";
	public static SleepStatus instance = null;
	
	public static SleepStatus getInstance(World world) {
		if (instance == null) {
			MapStorage storage = world.getPerWorldStorage();
			if (storage != null) {
				instance = (SleepStatus) storage.getOrLoadData(SleepStatus.class, KEY);
				if (instance == null) {
					SleepStatus newStatus = new SleepStatus(KEY);
					storage.setData(KEY, newStatus);
				}
				instance = (SleepStatus) storage.getOrLoadData(SleepStatus.class, KEY);
			}
		}
		return instance;
	}
	
	protected TMap<UUID, PlayerStatus> players = new THashMap<UUID, PlayerStatus>();

	public SleepStatus(String name) {
		super(name);
	}
	
	public boolean isActive(UUID player) {
		if (players.containsKey(player)) {
			return players.get(player).active;
		}
		return false;
	}
	
	public void markActive(UUID player) {
		this.initialisePlayer(player);
		players.get(player).markActive();
		this.markDirty();
	}
	
	public void markInActive(UUID player) {
		this.initialisePlayer(player);
		players.get(player).markInActive();
		this.markDirty();
	}
	
	public void slept(UUID player) {
		this.initialisePlayer(player);
		players.get(player).incrSleep();
		this.markDirty();
	}
	
	public int sleepCount(UUID player) {
		if (players.containsKey(player)) {
			return players.get(player).sleeptimes;
		}
		return 0;
	}
	
	public boolean givenST(UUID player) {
		if (players.containsKey(player)) {
			return players.get(player).givenST;
		}
		return false;
	}
	
	public void markGiven(UUID player) {
		this.initialisePlayer(player);
		players.get(player).givenST = true;
		this.markDirty();
	}
	
	protected void initialisePlayer(UUID player) {
		if (!players.containsKey(player)) {
			PlayerStatus status = new PlayerStatus();
			players.put(player, status);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("SleepStatusList")) {
			players.clear();
			NBTTagList contents = nbt.getTagList("SleepStatusList", 10);
			for (int i = 0; i < contents.tagCount(); i++) {
				NBTTagCompound tag = (NBTTagCompound) contents.getCompoundTagAt(i);
				UUID key = tag.getUniqueId("UUID");
				PlayerStatus status = new PlayerStatus(tag.getCompoundTag("Status"));
				players.put(key, status);
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		NBTTagList contents = new NBTTagList();
		for (UUID uuid : players.keySet()) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setUniqueId("UUID", uuid);
			tag.setTag("Status", players.get(uuid).getNBT());
		}
		nbt.setTag("SleepStatusList", contents);
		return nbt;
	}

}
