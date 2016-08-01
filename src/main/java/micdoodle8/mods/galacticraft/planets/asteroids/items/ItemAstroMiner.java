package micdoodle8.mods.galacticraft.planets.asteroids.items;

import micdoodle8.mods.galacticraft.core.items.ISortableItem;
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.api.item.IHoldableItem;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderOrbit;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.planets.asteroids.ConfigManagerAsteroids;
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks;
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityAstroMiner;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityMinerBase;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import java.util.List;

public class ItemAstroMiner extends Item implements IHoldableItem, ISortableItem
{
    public ItemAstroMiner(String assetName)
    {
        super();
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
        this.setUnlocalizedName(assetName);
        //this.setTextureName("arrow");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack par1ItemStack)
    {
        return ClientProxyCore.galacticraftItem;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public CreativeTabs getCreativeTab()
    {
        return GalacticraftCore.galacticraftItemsTab;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
    	TileEntity tile = null;

        if (worldIn.isRemote || playerIn == null)
        {
            return false;
        }
        else
        {
            final Block id = worldIn.getBlockState(pos).getBlock();

            if (id == AsteroidBlocks.minerBaseFull)
            {
                tile = worldIn.getTileEntity(pos);
            }

        	if (tile instanceof TileEntityMinerBase)
        	{
    			if (worldIn.provider instanceof WorldProviderOrbit)
    			{
                    playerIn.addChatMessage(new ChatComponentText(GCCoreUtil.translate("gui.message.astro_miner7.fail")));
        			return false;    				
    			}
    			
        		if (((TileEntityMinerBase)tile).getLinkedMiner() != null)
        		{
                    playerIn.addChatMessage(new ChatComponentText(GCCoreUtil.translate("gui.message.astro_miner.fail")));
        			return false;
        		}
        		
        		//Gives a chance for any loaded Astro Miner to link itself
        		if (((TileEntityMinerBase) tile).ticks < 15L)
        		{
        			return false;
        		}
        		
        		EntityPlayerMP playerMP = (EntityPlayerMP) playerIn;
        		
               	int astroCount = GCPlayerStats.get(playerMP).astroMinerCount;
               	if (astroCount >= ConfigManagerAsteroids.astroMinerMax && (!playerIn.capabilities.isCreativeMode))
               	{
                    playerIn.addChatMessage(new ChatComponentText(GCCoreUtil.translate("gui.message.astro_miner2.fail")));
               		return false;
               	}

        		if (!((TileEntityMinerBase)tile).spawnMiner(playerMP))
        		{
                    playerIn.addChatMessage(new ChatComponentText(GCCoreUtil.translate("gui.message.astro_miner1.fail") + " " + GCCoreUtil.translate(EntityAstroMiner.blockingBlock.toString())));
        			return false;
        		}
        	        			
                if (!playerIn.capabilities.isCreativeMode)
                {
                	GCPlayerStats.get(playerMP).astroMinerCount++;
                	--stack.stackSize;
                }
        		return true;
        	}
    	}
    	return false;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer player, List par2List, boolean b)
    {
        //TODO
    }

    @Override
    public boolean shouldHoldLeftHandUp(EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean shouldHoldRightHandUp(EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean shouldCrouch(EntityPlayer player)
    {
        return true;
    }

    @Override
    public EnumSortCategoryItem getCategory(int meta)
    {
        return EnumSortCategoryItem.GENERAL;
    }
}
