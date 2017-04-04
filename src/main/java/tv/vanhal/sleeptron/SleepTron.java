package tv.vanhal.sleeptron;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tv.vanhal.sleeptron.core.Proxy;
import tv.vanhal.sleeptron.util.Ref;
import tv.vanhal.sleeptron.world.SleptEvent;
import tv.vanhal.sleeptron.world.WorldTicker;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;


@Mod(modid = Ref.MODID, name = Ref.MODNAME, version = Ref.Version)
public class SleepTron {
	@Instance(Ref.MODID)
	public static SleepTron instance;

	@SidedProxy(clientSide = "tv.vanhal."+Ref.MODID+".core.ClientProxy", serverSide = "tv.vanhal."+Ref.MODID+".core.Proxy")
	public static Proxy proxy;

	//logger
	public static final Logger logger = LogManager.getLogger(Ref.MODID);
	
	public static SleepTronBlock sleepTronBlock;
	
	//Creative Tab
	public static CreativeTabs STTab = new CreativeTabs("STTab") {
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(sleepTronBlock);
		}
	};

	public SleepTron() {
		logger.info("Sleep Tron Online");
	}


	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		STConfig.init(new Configuration(event.getSuggestedConfigurationFile()));
		
		//initialise the block
		sleepTronBlock = new SleepTronBlock();
		GameRegistry.register(sleepTronBlock);
		GameRegistry.register(new ItemBlock(sleepTronBlock).setRegistryName(sleepTronBlock.getRegistryName()));
		
		if (STConfig.CRAFTABLE) {
			ShapedOreRecipe recipe = new ShapedOreRecipe(new ItemStack(sleepTronBlock), new Object[]{
					"wiw", "ibi", "wiw", 'b', Blocks.WOOL, 'i', Blocks.IRON_BARS, 'w', Blocks.END_STONE
			});
			GameRegistry.addRecipe(recipe);
		}
		
		STConfig.save();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new WorldTicker());
		MinecraftForge.EVENT_BUS.register(new SleptEvent());
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.registerEntities();
		
		if (event.getSide() == Side.CLIENT) {
			sleepTronBlock.postInit();
		}
		
		STConfig.postInit();
	}

	@SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
        if(eventArgs.getModID().equals(Ref.MODID))
        	STConfig.syncConfig();
    }
}
