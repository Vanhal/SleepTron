package tv.vanhal.sleeptron.world;

import java.util.UUID;

import tv.vanhal.sleeptron.STConfig;
import tv.vanhal.sleeptron.SleepTron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

public class SleptEvent {
	@SubscribeEvent
	public void onWakeUp(PlayerWakeUpEvent event) {
		if (STConfig.FREE_TRON) {
			//shouldSetSpawn should be true if sleeping was a success
			if (event.shouldSetSpawn()) {
				EntityPlayer sleeper = event.getEntityPlayer();
				if (!sleeper.worldObj.isRemote) {
					IBlockState iblockstate = sleeper.worldObj.getBlockState(sleeper.playerLocation);
					if (sleeper.playerLocation != null && 
							iblockstate.getBlock().isBed(iblockstate, sleeper.worldObj, sleeper.playerLocation, sleeper)) {
						//player is in a bed so we can incr the sleep count
						SleepStatus.getInstance(sleeper.worldObj).slept(sleeper.getUniqueID());
						this.giveFreeTron(sleeper);
					}
				}
			}
		}
	}
	
	public void giveFreeTron(EntityPlayer player) {
		UUID uuid = player.getUniqueID();
		if (!SleepStatus.getInstance(player.worldObj).givenST(uuid)) {
			int sleepCount = SleepStatus.getInstance(player.worldObj).sleepCount(uuid);
			SleepTron.logger.info(player.getDisplayNameString()+" sleep count: "+sleepCount + " / "+STConfig.FREE_NIGHTS);
			if (sleepCount>=STConfig.FREE_NIGHTS) {
				//find nearby air block
				BlockPos chestPos = null;
				BlockPos secondBed = null;
				for (EnumFacing side : EnumFacing.HORIZONTALS) {
					BlockPos testPos = player.playerLocation.offset(side);
					if (player.worldObj.isAirBlock(testPos)) {
						boolean nearChest = false;
						for (EnumFacing chestSide : EnumFacing.HORIZONTALS) {
							BlockPos placePos = testPos.offset(chestSide);
							if (player.worldObj.getBlockState(placePos).getBlock() == Blocks.CHEST)
								nearChest = true;
						}
						if (!nearChest) {
							chestPos = new BlockPos(testPos);
						}
					} else if (secondBed == null) {
						IBlockState iblockstate = player.worldObj.getBlockState(testPos);
						if (iblockstate.getBlock().isBed(iblockstate, player.worldObj, testPos, player)) {
							secondBed = new BlockPos(testPos);
						}
					}
				}
				if ( (chestPos==null) && (secondBed!=null) ) {
					for (EnumFacing side : EnumFacing.HORIZONTALS) {
						BlockPos testPos = secondBed.offset(side);
						if (player.worldObj.isAirBlock(testPos)) {
							boolean nearChest = false;
							for (EnumFacing chestSide : EnumFacing.HORIZONTALS) {
								BlockPos placePos = testPos.offset(chestSide);
								if (player.worldObj.getBlockState(placePos).getBlock() == Blocks.CHEST)
									nearChest = true;
							}
							if (!nearChest) {
								chestPos = new BlockPos(testPos);
							}
						}
					}
				}
				
				if (chestPos!=null) {
					//Place chest
					player.worldObj.setBlockState(chestPos, Blocks.CHEST.getDefaultState());
					//put the items into the chest
					TileEntity tile = player.worldObj.getTileEntity(chestPos);
					if ( (tile!=null) && (tile instanceof TileEntityChest) ) {
						TileEntityChest chest = (TileEntityChest)tile;
						chest.setInventorySlotContents(11, new ItemStack(Blocks.LEVER));
						chest.setInventorySlotContents(13, new ItemStack(SleepTron.sleepTronBlock));
						if (STConfig.INVERTABLE)
							chest.setInventorySlotContents(22, new ItemStack(Blocks.REDSTONE_TORCH));
						
						chest.setInventorySlotContents(15, this.makeInstructionsBook());
					}
					
					//show chat message
					player.addChatComponentMessage(new TextComponentTranslation("chat.nightTime.text", new Object[0]));
					player.addChatComponentMessage(new TextComponentTranslation("chat.somethingleft.text", new Object[0]));
					
					//toggle given
					SleepStatus.getInstance(player.worldObj).markGiven(uuid);
				}
			}
		}
	}
	
	public ItemStack makeInstructionsBook() {
		ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
		if (book.getTagCompound()==null) book.setTagCompound(new NBTTagCompound());
		book.getTagCompound().setString("title", I18n.translateToLocal("book.title.data"));
		book.getTagCompound().setString("author", I18n.translateToLocal("book.author.data"));
		//book.getTagCompound().setBoolean("resolved", true);
		
		NBTTagList pages = new NBTTagList();
		String pageName = "book.contents.page.";
		int pageCount = 1;
		boolean finished = false;
		while (!finished) {
			String pageKey = ""+pageName+pageCount;
			String translatedText = I18n.translateToLocal(pageKey);
			if (!translatedText.equals(pageKey)) {
				translatedText = translatedText.replaceAll("<br>", "\n");
				TextComponentString page = new TextComponentString(translatedText);
				NBTTagString tag = new NBTTagString(ITextComponent.Serializer.componentToJson(page));
				pages.appendTag(tag);
			} else {
				finished = true;
			}
			pageCount++;
		}
		book.getTagCompound().setTag("pages", pages);
		
		return book;
	}
	
}
