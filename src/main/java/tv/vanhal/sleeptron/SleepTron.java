package tv.vanhal.sleeptron;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tv.vanhal.sleeptron.core.Proxy;
import tv.vanhal.sleeptron.util.Ref;
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
		
		//initialise the block
		sleepTronBlock = new SleepTronBlock();
		GameRegistry.register(sleepTronBlock);
		GameRegistry.register(new ItemBlock(sleepTronBlock).setRegistryName(sleepTronBlock.getRegistryName()));
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.registerEntities();
	}

}
