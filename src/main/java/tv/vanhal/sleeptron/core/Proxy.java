package tv.vanhal.sleeptron.core;

import tv.vanhal.sleeptron.SleepTronTile;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Proxy {

	public void registerEntities() {
		GameRegistry.registerTileEntity(SleepTronTile.class, "SleepTronTile");
	}

	public boolean isClient() {
		return false;
	}

	public boolean isServer() {
		return true;
	}
}
