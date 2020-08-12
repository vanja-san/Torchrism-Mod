package com.atomuze.torchrism.block.torch_altar;

import java.util.Random;

import javax.annotation.Nullable;

import com.atomuze.torchrism.ModConfig;
import com.atomuze.torchrism.block.BlockTileEntity;
import com.atomuze.torchrism.block.torch_placer.BlockTorchPlacer;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class BlockAltarPedestal extends BlockTileEntity<TileEntityTorchAltar> {

	World world;
	BlockPos pos;
	EnumFacing side;
	
	public BlockAltarPedestal(String name) {
		super(Material.ROCK, name);

		setHardness(3f);
		setResistance(5f);
	}
	
	@Override
	public BlockAltarPedestal setCreativeTab(CreativeTabs tab) {
		super.setCreativeTab(tab);
		return this;
	}
	
	@Override
	public Class<TileEntityTorchAltar> getTileEntityClass() {
		return TileEntityTorchAltar.class;
	}
	
	@Nullable
	@Override
	public TileEntityTorchAltar createTileEntity(World world, IBlockState state) {
		return new TileEntityTorchAltar();
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			TileEntityTorchAltar tile = getTileEntity(world, pos);
			IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
			this.world = world;
			this.pos = pos;
			this.side = side;
			if (player.getHeldItem(hand).isEmpty() || player.getHeldItem(hand).getItem() == itemHandler.getStackInSlot(0).getItem()) {
				player.addItemStackToInventory(itemHandler.extractItem(0, 1, false));
			} else if (itemHandler.getStackInSlot(0).isEmpty()){
				ItemStack giveBackItem = player.getHeldItem(hand);
				player.setHeldItem(hand, itemHandler.insertItem(0, new ItemStack(player.getHeldItem(hand).getItem(),  1, player.getHeldItem(hand).getMetadata()), false));
				player.setHeldItem(hand, new ItemStack(giveBackItem.getItem(),  giveBackItem.getCount() - 1, giveBackItem.getMetadata()));
			}
			tile.markDirty();
		}
		return true;
	}
	

	public void extractItem() {
		TileEntityTorchAltar tile = getTileEntity(world, pos);
		IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
		itemHandler.extractItem(0, 1, false);
		tile.markDirty();
	}
	
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntityTorchAltar tile = getTileEntity(world, pos);
		IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
		ItemStack stack = itemHandler.getStackInSlot(0);
		if (!stack.isEmpty()) {
			EntityItem item = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack);
			world.spawnEntity(item);
		}
		super.breakBlock(world, pos, state);
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
}