package micdoodle8.mods.galacticraft.core.items;

import micdoodle8.mods.galacticraft.core.blocks.BlockEnclosed;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockEnclosed extends ItemBlockDesc
{
    public ItemBlockEnclosed(Block block)
    {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        String name;

        switch (par1ItemStack.getItemDamage())
        {
        case 0:
            name = "null";
            break;
        case 1:
            name = "oxygen_pipe";
            break;
        case 2:
            name = "copper_cable";
            break;
        case 3:
            name = "gold_cable";
            break;
        case 4:
            name = "hv_cable";
            break;
        case 5:
            name = "glass_fibre_cable";
            break;
        case 6:
            name = "lv_cable";
            break;
        case 13:
            name = "me_cable";
            break;
        case 14:
            name = "aluminum_wire";
            break;
        case 15:
            name = "aluminum_wire_heavy";
            break;
        default:
        	//The BuildCraft pipes
            try
            {
                name = BlockEnclosed.EnumEnclosedBlockType.byMetadata(par1ItemStack.getItemDamage()).getBCPipeType();
            }
            catch (Exception e)
            {
                name = "null";
            }
            break;
        }

        return this.getBlock().getUnlocalizedName() + "." + name;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        int metadata = this.getMetadata(stack.getItemDamage());
        BlockEnclosed.EnumEnclosedBlockType type = BlockEnclosed.EnumEnclosedBlockType.byMetadata(metadata);

        if (type != null && type.getBCPipeType() != null)
        {
            Block block = worldIn.getBlockState(pos).getBlock();

            if (block == Blocks.snow)
            {
                side = EnumFacing.UP;
            }
            else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && !block.isReplaceable(worldIn, pos))
            {
                pos = pos.offset(side);
//                if (side == EnumFacing.DOWN)
//                {
//                    j--;
//                }
//                if (side == EnumFacing.UP)
//                {
//                    j++;
//                }
//                if (side == EnumFacing.NORTH)
//                {
//                    k--;
//                }
//                if (side == EnumFacing.SOUTH)
//                {
//                    k++;
//                }
//                if (side == EnumFacing.WEST)
//                {
//                    i--;
//                }
//                if (side == EnumFacing.EAST)
//                {
//                    i++;
//                }
            }

            if (stack.stackSize == 0)
            {
                return false;
            }

            if (!playerIn.canPlayerEdit(pos, side, stack))
            {
                return false;
            }
            else if (pos.getY() == 255 && this.getBlock().getMaterial().isSolid())
            {
                return false;
            }
            else if (worldIn.canBlockBePlaced(block, pos, false, side, playerIn, stack))
            {
//                public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
            	IBlockState j1 = this.getBlock().onBlockPlaced(worldIn, pos, side, hitX, hitY, hitZ, metadata, playerIn);
                block.onBlockPlacedBy(worldIn, pos, j1, playerIn, stack);

                if (placeBlockAt(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ, j1))
                {
                    worldIn.playSoundEffect(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, this.getBlock().stepSound.getPlaceSound(), (this.getBlock().stepSound.getVolume() + 1.0F) / 2.0F, 1.0F * 0.8F);
                    --stack.stackSize;

                    if (metadata >= BlockEnclosed.EnumEnclosedBlockType.BC_ITEM_STONEPIPE.getMeta() && metadata <= BlockEnclosed.EnumEnclosedBlockType.BC_POWER_GOLDPIPE.getMeta())
                    {
                        if (CompatibilityManager.isBCraftLoaded())
                        {
                            BlockEnclosed.initialiseBCPipe(worldIn, pos, metadata);
                        }
                    }
                    
                    else if (metadata == BlockEnclosed.EnumEnclosedBlockType.ME_CABLE.getMeta())
                    {
//                    	ItemStack itemME = new ItemStack(Block.getBlockFromName("appliedenergistics2:tile.BlockCableBus"), 16);
//                    	try
//                    	{
//                    		Class clazz = Class.forName("appeng.tile.networking.TileCableBus");
//                    		Method m = clazz.getMethod("addPart", ItemStack.class, ForgeDirection.class, EntityPlayer.class);
//                    		m.invoke(world.getTileEntity(i, j, k), itemME, ForgeDirection.UNKNOWN, entityplayer);
//                    	}
//                    	catch (Exception e) { e.printStackTrace(); }
                    }
                }

                return true;

            }
            else
            {
                return false;
            }
        }
        else
        {
            return super.onItemUse(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack par1ItemStack)
    {
        return ClientProxyCore.galacticraftItem;
    }

    @Override
    public int getMetadata(int damage)
    {
        //TE_CONDUIT (item damage 0: currently unused) and HV_CABLE (item damage 4) have had to have swapped metadata in 1.7.10 because IC2's TileCable tile entity doesn't like a block with metadata 4
    	if (damage == 4) return 0;
        if (damage == 0) return 4;
    	return damage;
    }
}
