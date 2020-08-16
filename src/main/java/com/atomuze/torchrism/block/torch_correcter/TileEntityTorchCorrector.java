package com.atomuze.torchrism.block.torch_correcter;

import com.atomuze.torchrism.ModConfig;
import com.atomuze.torchrism.block.ModBlocks;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public class TileEntityTorchCorrector  extends TileEntity implements ITickable {
	
	private boolean checking = false;
	private int runCount = 1;
	int torchCount = 0;
	BlockPos activatepos = pos;
	int offset = ModConfig.offset + 1;
	int outlay = 1;
	
	@Override
	public void update() {
		if (!world.isRemote) {
			if (checking) {
				if(outlay <= 64) {
					movePos1(outlay);
					outlay++;
				}else if(outlay >= 64 && outlay <= 128) {
					movePos2(outlay - 64);
					outlay++;
				}else {
					checking = false;
					outlay = 0;
					if(BlockTorchCorrector.player != null) {
						BlockTorchCorrector.player.addItemStackToInventory(new ItemStack(Blocks.TORCH, torchCount));	
					}
					torchCount = 0;
				}
			}
		}
	}
	
	private void movePos1(int outLay) {
		for (int i = -outLay; i <= outLay; i++) {
			this.activatepos = new BlockPos(pos.getX() + i, pos.getY(), pos.getZ() + outLay);
			removeTorch();
		}
		for (int i = -outLay; i <= outLay; i++) {
			this.activatepos = new BlockPos(pos.getX() + i, pos.getY(), pos.getZ() - outLay);
			removeTorch();
		}
		for (int i = -outLay; i <= outLay; i++) {
			this.activatepos = new BlockPos(pos.getX() + outLay, pos.getY(), pos.getZ() + i);
			removeTorch();
		}
		for (int i = -outLay; i <= outLay; i++) {
			this.activatepos = new BlockPos(pos.getX() - outLay, pos.getY(), pos.getZ() + i);
			removeTorch();
		}
	}
	
	private void movePos2(int outLay) {
		for (int i = -outLay; i <= outLay; i++) {
			this.activatepos = new BlockPos(pos.getX() + i, pos.getY(), pos.getZ() + outLay);
			placeTorch();
		}
		for (int i = -outLay; i <= outLay; i++) {
			this.activatepos = new BlockPos(pos.getX() + i, pos.getY(), pos.getZ() - outLay);
			placeTorch();
		}
		for (int i = -outLay; i <= outLay; i++) {
			this.activatepos = new BlockPos(pos.getX() + outLay, pos.getY(), pos.getZ() + i);
			placeTorch();
		}
		for (int i = -outLay; i <= outLay; i++) {
			this.activatepos = new BlockPos(pos.getX() - outLay, pos.getY(), pos.getZ() + i);
			placeTorch();
		}
	}

	public void removeTorch() {
		int placePosX = activatepos.getX();
		int placePosZ = activatepos.getZ();
		for (int placePosY = 255; placePosY >= 0; placePosY--) {
			BlockPos PlacePos = new BlockPos(placePosX, placePosY, placePosZ);

			IBlockState iblockstate = world.getBlockState(PlacePos);
			Material m = world.getBlockState(PlacePos).getMaterial();
			
			if (m == Material.LEAVES) {

			} else if (iblockstate == Blocks.TORCH.getBlockState()) {
				if((activatepos.getX()-pos.getX())%offset == 0 && (activatepos.getZ()-pos.getZ())%offset == 0) {
					break;
				}
				world.setBlockState(PlacePos, Blocks.AIR.getDefaultState());
				torchCount++;
				break;
			} else if(m != Material.AIR){
				break;
			}
		}
	}
	
	public void placeTorch() {
		if((activatepos.getX()-pos.getX())%offset == 0 && (activatepos.getZ()-pos.getZ())%offset == 0 && torchCount>0) {
			int placePosX = activatepos.getX();
			int placePosZ = activatepos.getZ();
			for (int placePosY = 255; placePosY >= 0; placePosY--) {
				BlockPos findPlacePos = new BlockPos(placePosX, placePosY, placePosZ);
				BlockPos PlacePos = new BlockPos(placePosX, placePosY + 1, placePosZ);

				Material m = world.getBlockState(findPlacePos).getMaterial();
				Material m2 = world.getBlockState(PlacePos).getMaterial();
				IBlockState iblockstate = world.getBlockState(findPlacePos);

				if (m == Material.VINE || m == Material.SNOW || m == Material.AIR) {
					world.setBlockState(findPlacePos, Blocks.AIR.getDefaultState());
					continue;
				} else if (iblockstate.isNormalCube() && m2 == Material.AIR) {
					world.setBlockState(PlacePos, Blocks.TORCH.getDefaultState());
					torchCount--;
					break;
				}
			}
		}
	}
	
	public boolean isUsableByPlayer(EntityPlayer player) {
		return this.world.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
	}
	
	public void toggleActive() {
		if (this.checking) {
			this.checking = false;
		}else {
			this.checking = true;
		}
	}
}
