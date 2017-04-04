package tv.vanhal.sleeptron;

import net.minecraftforge.common.config.Configuration;

public class STConfig {
	public static Configuration config;
	
	public static boolean CRAFTABLE;
	public static boolean INVERTABLE;
	
	public static boolean FREE_TRON;
	public static int FREE_NIGHTS;
	
	
	public static void init(Configuration handle) {
		config = handle;
		config.load();
		
		config.addCustomCategoryComment("general", "Sleep Tron 2000 Settings");
		
		
		syncConfig();
	}
	
	public static void syncConfig() {
		CRAFTABLE = config.getBoolean("Craftable", "general", true, "Should Sleep Trons be craftable?");
		INVERTABLE = config.getBoolean("Invertable", "general", true, "Can the Sleep Trons be used to sleep through the day?");
		
		FREE_TRON = config.getBoolean("FreeTron", "general", true, "Should the players be given a free Sleep Tron after sleeping normally for a while");
		FREE_NIGHTS = config.getInt("FreeNights", "general", 10, 1, 100, "After how many nights should the Sleep Tron be given?");
		
		//save if changed
		if (config.hasChanged()) save();
	}
	
	public static void save() {
		config.save();
	}
	
	public static void postInit() {
		save();
	}
}
