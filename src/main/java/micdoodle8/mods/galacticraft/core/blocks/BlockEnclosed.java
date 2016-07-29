package micdoodle8.mods.galacticraft.core.blocks;

import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock;
import micdoodle8.mods.galacticraft.api.transmission.tile.IConductor;
import micdoodle8.mods.galacticraft.api.transmission.tile.INetworkConnection;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.items.ItemBlockDesc;
import micdoodle8.mods.galacticraft.core.tile.TileEntityAluminumWire;
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenPipe;
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager;
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public class BlockEnclosed extends Block implements IPartialSealableBlock, ITileEntityProvider, ItemBlockDesc.IBlockShiftDesc, ISortableBlock
{
//    private IIcon[] enclosedIcons;
    public static Item[] pipeItemsBC = new Item[6];
    public static BlockContainer blockPipeBC = null;
    public static Method onBlockNeighbourChangeIC2 = null;

    public static final PropertyEnum TYPE = PropertyEnum.create("type", EnumEnclosedBlockType.class);

    public enum EnumEnclosedBlockType implements IStringSerializable
    {
        IC2_HV_CABLE(0, 6, "enclosed_hv_cable"),
        OXYGEN_PIPE(1, "enclosed_oxygen_pipe"),
        IC2_COPPER_CABLE(2, 0, "enclosed_copper_cable"),
        IC2_GOLD_CABLE(3, 3, "enclosed_gold_cable"),
        TE_CONDUIT(4, "enclosed_te_conduit"), //CURRENTLY UNUSED
        IC2_GLASS_FIBRE_CABLE(5, 9, "enclosed_glassfibre_cable"),
        IC2_LV_CABLE(6, 13, "enclosed_lv_cable"),
        BC_ITEM_STONEPIPE(7, "PipeItemsStone", "enclosed_itempipe_stone"),
        BC_ITEM_COBBLESTONEPIPE(8, "PipeItemsCobblestone", "enclosed_itempipe_cobblestone"),
        BC_FLUIDS_STONEPIPE(9, "PipeFluidsStone", "enclosed_liquidpipe_stone"),
        BC_FLUIDS_COBBLESTONEPIPE(10, "PipeFluidsCobblestone", "enclosed_liquidpipe_cobblestone"),
        BC_POWER_STONEPIPE(11, "PipePowerStone", "enclosed_powerpipe_stone"),
        BC_POWER_GOLDPIPE(12, "PipePowerGold", "enclosed_powerpipe_gold"),
        ME_CABLE(13, "enclosed_me_cable"),
        ALUMINUM_WIRE(14, "enclosed_aluminum_wire"),
        ALUMINUM_WIRE_HEAVY(15, "enclosed_heavy_aluminum_wire");

        private final int meta;
        private final String name;
        private final int ic2Meta;
        private final String bcPipeType;

        EnumEnclosedBlockType(int meta, String bcPipeType, String name)
        {
            this(meta, -1, bcPipeType, name);
        }

        EnumEnclosedBlockType(int meta, String name)
        {
            this(meta, -1, null, name);
        }

        EnumEnclosedBlockType(int meta, int ic2Meta, String name)
        {
            this(meta, ic2Meta, null, name);
        }

        EnumEnclosedBlockType(int meta, int ic2Meta, String bcPipeType, String name)
        {
            this.meta = meta;
            this.ic2Meta = ic2Meta;
            this.bcPipeType = bcPipeType;
            this.name = name;
        }

        public int getMeta()
        {
            return this.meta;
        }

        public int getIC2Meta()
        {
            return ic2Meta;
        }

        public String getBCPipeType()
        {
            return bcPipeType;
        }

        public static EnumEnclosedBlockType byMetadata(int meta)
        {
            return values()[meta];
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    public BlockEnclosed(String assetName)
    {
        super(Material.clay);
        this.setResistance(0.2F);
        this.setHardness(0.4f);
        this.setStepSound(Block.soundTypeStone);
        //this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + assetName);
        this.setUnlocalizedName(assetName);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List)
    {
        par3List.add(new ItemStack(par1, 1, EnumEnclosedBlockType.ALUMINUM_WIRE.getMeta()));
        par3List.add(new ItemStack(par1, 1, EnumEnclosedBlockType.ALUMINUM_WIRE_HEAVY.getMeta()));
        par3List.add(new ItemStack(par1, 1, EnumEnclosedBlockType.OXYGEN_PIPE.getMeta()));

        if (CompatibilityManager.isTELoaded())
        {
            // par3List.add(new ItemStack(par1, 1, 0));
        }

        if (CompatibilityManager.isIc2Loaded())
        {
            par3List.add(new ItemStack(par1, 1, EnumEnclosedBlockType.IC2_COPPER_CABLE.getMeta()));
            par3List.add(new ItemStack(par1, 1, EnumEnclosedBlockType.IC2_GOLD_CABLE.getMeta()));
            par3List.add(new ItemStack(par1, 1, 4));  //Damage value not same as metadata for HV_CABLE
            par3List.add(new ItemStack(par1, 1, EnumEnclosedBlockType.IC2_GLASS_FIBRE_CABLE.getMeta()));
            par3List.add(new ItemStack(par1, 1, EnumEnclosedBlockType.IC2_LV_CABLE.getMeta()));
        }

        if (CompatibilityManager.isBCraftLoaded())
        {
            par3List.add(new ItemStack(par1, 1, EnumEnclosedBlockType.BC_ITEM_COBBLESTONEPIPE.getMeta()));
            par3List.add(new ItemStack(par1, 1, EnumEnclosedBlockType.BC_ITEM_STONEPIPE.getMeta()));
            par3List.add(new ItemStack(par1, 1, EnumEnclosedBlockType.BC_FLUIDS_COBBLESTONEPIPE.getMeta()));
            par3List.add(new ItemStack(par1, 1, EnumEnclosedBlockType.BC_FLUIDS_STONEPIPE.getMeta()));
            par3List.add(new ItemStack(par1, 1, EnumEnclosedBlockType.BC_POWER_STONEPIPE.getMeta()));
            par3List.add(new ItemStack(par1, 1, EnumEnclosedBlockType.BC_POWER_GOLDPIPE.getMeta()));
        }

        if (CompatibilityManager.isAppEngLoaded())
        {
//            par3List.add(new ItemStack(par1, 1, EnumEnclosedBlockType.ME_CABLE.getMetadata()));
        }
    }

    public static void initialiseBC()
    {
        for (int i = 0; i < 6; i++)
        {
            try {
                Class<?> clazzBC = Class.forName("buildcraft.BuildCraftTransport");
                String pipeName = EnumEnclosedBlockType.values()[i+7].getBCPipeType();
                pipeName = pipeName.substring(0, 1).toLowerCase() + pipeName.substring(1);
                pipeItemsBC[i] = (Item) clazzBC.getField(pipeName).get(null);
            }
            catch (Exception e) { e.printStackTrace(); }
        }
        //Now update the cached classes in CompatibilityManager
        //This is needed for BCCompat compatibility, as that overrides the basic BlockGenericPipe
        if (pipeItemsBC[0] != null)
        {
            try {
                Class<?> clazzBC = Class.forName("buildcraft.BuildCraftTransport");
                blockPipeBC = (BlockContainer) clazzBC.getField("genericPipeBlock").get(null);
                CompatibilityManager.classBCBlockGenericPipe = blockPipeBC.getClass();
                for (Method m : CompatibilityManager.classBCBlockGenericPipe.getDeclaredMethods())
                {
                    if (m.getName().equals("getPipe"))
                    {
                        CompatibilityManager.methodBCBlockPipe_getPipe = m;
                        break;
                    }
                }
            }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    @Override
    public CreativeTabs getCreativeTabToDisplayOn()
    {
        return GalacticraftCore.galacticraftBlocksTab;
    }

    /*@Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int par1, int meta)
    {
        if (meta == 4) meta = 0;  //Deal with item rendering for the HV block
        return meta >= this.enclosedIcons.length ? this.blockIcon : this.enclosedIcons[meta];
    }*/

    @Override
    public int damageDropped(IBlockState state)
    {
        int meta = state.getBlock().getMetaFromState(state);
        //TE_CONDUIT and HV_CABLE have had to have swapped metadata in 1.7.10 because IC2's TileCable tile entity doesn't like a block with metadata 4
        if (meta == 0) return 4;
        if (meta == 4) return 0;
        return meta;
    }

    /*@Override
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        this.enclosedIcons = new IIcon[16];

        for (int i = 0; i < EnumEnclosedBlockType.values().length; i++)
        {
            this.enclosedIcons[EnumEnclosedBlockType.values()[i].getMetadata()] = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + EnumEnclosedBlockType.values()[i].getTexture());
        }

        this.blockIcon = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "" + EnumEnclosedBlockType.OXYGEN_PIPE.getTexture());
    }*/

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block)
    {
        int metadata = state.getBlock().getMetaFromState(state);
        final TileEntity tileEntity = world.getTileEntity(pos);

        if (metadata == EnumEnclosedBlockType.TE_CONDUIT.getMeta())
        {
            super.onNeighborBlockChange(world, pos, state, block);
        }
        else if (metadata == EnumEnclosedBlockType.OXYGEN_PIPE.getMeta())
        {
            super.onNeighborBlockChange(world, pos, state, block);

            if (tileEntity instanceof INetworkConnection)
            {
                ((INetworkConnection) tileEntity).refresh();
            }
        }
        else if (metadata <= 6)
        {
            super.onNeighborBlockChange(world, pos, state, block);
            if (CompatibilityManager.isIc2Loaded() && tileEntity != null)
            {
                try
                {
                    onBlockNeighbourChangeIC2.invoke(tileEntity);
                    return;
                }
                catch (Exception e) { e.printStackTrace(); }
            }
        }
        else if (metadata <= 12)
        {
            if (CompatibilityManager.isBCraftLoaded())
            {
                if (blockPipeBC != null)
                {
                    try {
                        blockPipeBC.onNeighborBlockChange(world, pos, state, block);
                    }
                    catch (Exception e) { e.printStackTrace(); }
                    return;
                }
            }

            super.onNeighborBlockChange(world, pos, state, block);
        }
        else if (metadata <= EnumEnclosedBlockType.ME_CABLE.getMeta())
        {
            super.onNeighborBlockChange(world, pos, state, block);
            if (CompatibilityManager.isAppEngLoaded())
            {
                world.markBlockForUpdate(pos);
            }
        }
        else if (metadata <= EnumEnclosedBlockType.ALUMINUM_WIRE.getMeta())
        {
            super.onNeighborBlockChange(world, pos, state, block);
            if (tileEntity instanceof IConductor)
            {
                ((IConductor) tileEntity).refresh();
            }
        }
        else if (metadata <= EnumEnclosedBlockType.ALUMINUM_WIRE_HEAVY.getMeta())
        {
            super.onNeighborBlockChange(world, pos, state, block);
            if (tileEntity instanceof IConductor)
            {
                ((IConductor) tileEntity).refresh();
            }
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        if (metadata == EnumEnclosedBlockType.TE_CONDUIT.getMeta())
        {
            //TODO
        }
        else if (metadata == EnumEnclosedBlockType.OXYGEN_PIPE.getMeta())
        {
            return new TileEntityOxygenPipe();
        }
        else if (metadata <= 6)
        {
            if (CompatibilityManager.isIc2Loaded())
            {
                try
                {
                    Class<?> clazz = Class.forName("ic2.core.block.wiring.TileEntityCable");
                    Constructor<?>[] constructors = clazz.getDeclaredConstructors();
                    Constructor<?> constructor = null;

                    for (Constructor<?> constructor2 : constructors)
                    {
                        constructor = constructor2;

                        if (constructor.getGenericParameterTypes().length == 1)
                        {
                            break;
                        }
                    }

                    constructor.setAccessible(true);

                    return (TileEntity) constructor.newInstance(EnumEnclosedBlockType.byMetadata(metadata).getIC2Meta());
                }
                catch (Exception e) { e.printStackTrace(); }
            }
        }
        else if (metadata <= 12)
        {
            if (CompatibilityManager.isBCraftLoaded())
            {
                try
                {
                    return blockPipeBC.createNewTileEntity(world, 0);
                }
                catch (Exception e) { e.printStackTrace(); }
            }
        }
        else if (metadata <= EnumEnclosedBlockType.ME_CABLE.getMeta())
        {
//            if (CompatibilityManager.isAppEngLoaded())
//            {
//                try
//                {
//                    Class<?> clazz = Class.forName("appeng.tile.networking.TileCableBus");
//                    return (TileEntity) clazz.newInstance();
//                }
//                catch (Exception e) { e.printStackTrace(); }
//            }
        }
        else if (metadata <= EnumEnclosedBlockType.ALUMINUM_WIRE.getMeta())
        {
            return new TileEntityAluminumWire(1);
        }
        else if (metadata <= EnumEnclosedBlockType.ALUMINUM_WIRE_HEAVY.getMeta())
        {
            return new TileEntityAluminumWire(2);
        }

        return null;
    }

    @Override
    public boolean isSealed(World world, BlockPos pos, EnumFacing direction)
    {
        return true;
    }

    @Override
    public String getShiftDescription(int meta)
    {
        return GCCoreUtil.translate(this.getUnlocalizedName() + ".description");
    }

    @Override
    public boolean showDescription(int meta)
    {
        return true;
    }

    public static void initialiseBCPipe(World world, BlockPos pos, int metadata)
    {
        try
        {
            //------
            //This section makes these three calls to initialise the TileEntity:
            //	Pipe pipe = BlockGenericPipe.createPipe(Item);
            //  tilePipe.initialize(pipe);
            //	and optionally: tilePipe.sendUpdateToClient();

            Item pipeItem = pipeItemsBC[metadata-7];
            Class<?> clazzBlockPipe = CompatibilityManager.classBCBlockGenericPipe;
            TileEntity tilePipe = world.getTileEntity(pos);
            Class<?> clazzTilePipe = tilePipe.getClass();

            Method createPipe = null;
            for (Method m : clazzBlockPipe.getDeclaredMethods())
            {
                if (m.getName().equals("createPipe") && m.getParameterTypes().length == 1)
                {
                    createPipe = m;
                    break;
                }
            }
            if (createPipe != null)
            {
                Object pipe = createPipe.invoke(null, pipeItem);
                Method initializePipe = null;
                for (Method m : clazzTilePipe.getDeclaredMethods())
                {
                    if (m.getName().equals("initialize") && m.getParameterTypes().length == 1)
                    {
                        initializePipe = m;
                        break;
                    }
                }
                if (initializePipe != null)
                {
                    initializePipe.invoke(tilePipe, pipe);

                    //Legacy compatibility: TileGenericPipe.sendUpdateToClient() is not in recent BC versions
                    Method m = null;
                    try
                    {
                        m = clazzTilePipe.getMethod("sendUpdateToClient");
                    }
                    catch (Exception e) { }
                    if (m != null) m.invoke(tilePipe);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public IBlockState getStateFromMeta(int meta)
    {
        EnumEnclosedBlockType type = EnumEnclosedBlockType.byMetadata(meta);
        return this.getDefaultState().withProperty(TYPE, type);
    }

    public int getMetaFromState(IBlockState state)
    {
        return ((EnumEnclosedBlockType)state.getValue(TYPE)).getMeta();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, TYPE);
    }

    @Override
    public EnumSortCategoryBlock getCategory(int meta)
    {
        return EnumSortCategoryBlock.TRANSMITTER;
    }
}
