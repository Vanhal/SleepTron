package tv.vanhal.sleeptron;

import tv.vanhal.sleeptron.util.Ref;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SleepTronBlock extends BlockContainer {
	public final String blockName = "sleeptron2000";

	public SleepTronBlock() {
		super(Material.IRON);
		setHardness(1.0f);
		this.setUnlocalizedName(blockName);
		this.setRegistryName(blockName);
		this.setCreativeTab(SleepTron.STTab);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new SleepTronTile();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
	
	@SideOnly(Side.CLIENT)
	public void postInit() {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
			.register(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":" + blockName, "inventory"));
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if ( (tile != null) && (tile instanceof SleepTronTile) ) {
			if (placer instanceof EntityPlayer) {
				((SleepTronTile)tile).setOwner((EntityPlayer)placer);
			}
		}
	}
}
