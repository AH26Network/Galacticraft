package micdoodle8.mods.galacticraft.core.proxy;

import com.google.common.base.*;
import com.google.common.collect.*;
import micdoodle8.mods.galacticraft.api.client.tabs.InventoryTabVanilla;
import micdoodle8.mods.galacticraft.api.client.tabs.TabRegistry;
import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityTieredRocket;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.client.DynamicTextureProper;
import micdoodle8.mods.galacticraft.core.client.FootprintRenderer;
import micdoodle8.mods.galacticraft.core.client.fx.EffectHandler;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import micdoodle8.mods.galacticraft.core.client.gui.screen.InventoryTabGalacticraft;
import micdoodle8.mods.galacticraft.core.client.model.ModelRocketTier1;
import micdoodle8.mods.galacticraft.core.client.render.entities.*;
import micdoodle8.mods.galacticraft.core.client.render.item.*;
import micdoodle8.mods.galacticraft.core.client.render.tile.*;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderMoon;
import micdoodle8.mods.galacticraft.core.entities.*;
import micdoodle8.mods.galacticraft.core.entities.player.IPlayerClient;
import micdoodle8.mods.galacticraft.core.entities.player.PlayerClient;
import micdoodle8.mods.galacticraft.core.inventory.InventoryExtended;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.tick.KeyHandlerClient;
import micdoodle8.mods.galacticraft.core.tick.TickHandlerClient;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.core.util.ClientUtil;
import micdoodle8.mods.galacticraft.core.wrappers.BlockMetaList;
import micdoodle8.mods.galacticraft.core.wrappers.ModelTransformWrapper;
import micdoodle8.mods.galacticraft.core.wrappers.PlayerGearData;
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.entity.RenderTier3Rocket;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.FloatBuffer;
import java.util.*;

public class ClientProxyCore extends CommonProxyCore
{
    public static FootprintRenderer footprintRenderer = new FootprintRenderer();

    public static List<String> flagRequestsSent = new ArrayList<String>();

    public static Set<BlockVec3> valueableBlocks = Sets.newHashSet();
    public static HashSet<BlockMetaList> detectableBlocks = Sets.newHashSet();
	public static List<BlockVec3> leakTrace;
	
    public static Map<String, PlayerGearData> playerItemData = Maps.newHashMap();

    public static double playerPosX;
    public static double playerPosY;
    public static double playerPosZ;
    public static float playerRotationYaw;
    public static float playerRotationPitch;

    public static boolean lastSpacebarDown;

    public static HashMap<Integer, Integer> clientSpaceStationID = Maps.newHashMap();

    public static MusicTicker.MusicType MUSIC_TYPE_MARS;

    public static EnumRarity galacticraftItem = EnumHelper.addRarity("GCRarity", EnumChatFormatting.BLUE, "Space");

    public static Map<String, String> capeMap = new HashMap<String, String>();

    public static InventoryExtended dummyInventory = new InventoryExtended();

    private static final ResourceLocation underOilTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/misc/underoil.png");

    private static float numbers[] = { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };
    private static FloatBuffer scaleup = BufferUtils.createFloatBuffer(16 * Float.SIZE);
    public static float globalRadius = Float.MAX_VALUE;
    public static double offsetY = 0D;
    public static float terrainHeight = Float.MAX_VALUE;
    private static boolean smallMoonActive = false;
    private static Map<String, ResourceLocation> capesMap = Maps.newHashMap();

    public static IPlayerClient playerClientHandler = new PlayerClient();
    public static Minecraft mc = FMLClientHandler.instance().getClient();
    public static List<String> gearDataRequests = Lists.newArrayList();
    //private static int playerList;

    public static DynamicTextureProper overworldTextureClient;
    public static DynamicTextureProper overworldTextureWide;
    public static DynamicTextureProper overworldTextureLarge;
    public static boolean overworldTextureRequestSent;
    public static boolean overworldTexturesValid;

    public static float PLAYER_Y_OFFSET = 1.6200000047683716F;

    private static final ResourceLocation saturnRingTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/celestialbodies/saturnRings.png");    
    private static final ResourceLocation uranusRingTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/celestialbodies/uranusRings.png");

    private static List<Item> itemsToRegisterJson = Lists.newArrayList();

    private static ModelResourceLocation fuelLocation = new ModelResourceLocation(GalacticraftCore.TEXTURE_PREFIX + "fuel", "fluid");
    private static ModelResourceLocation oilLocation = new ModelResourceLocation(GalacticraftCore.TEXTURE_PREFIX + "oil", "fluid");

    @SubscribeEvent
    public void onTextureStitchedPre(TextureStitchEvent.Pre event)
    {
        event.map.registerSprite(new ResourceLocation("galacticraftcore:blocks/assembly"));
        event.map.registerSprite(new ResourceLocation("galacticraftcore:model/rocketT1"));
        event.map.registerSprite(new ResourceLocation("galacticraftcore:model/buggyMain"));
        event.map.registerSprite(new ResourceLocation("galacticraftcore:model/buggyStorage"));
        event.map.registerSprite(new ResourceLocation("galacticraftcore:model/buggyWheels"));
        event.map.registerSprite(new ResourceLocation("galacticraftcore:model/flag0"));
    }

    public static void reset()
    {
        ClientProxyCore.playerItemData.clear();
        ClientProxyCore.overworldTextureRequestSent = false;
        ClientProxyCore.flagRequestsSent.clear();
    }

    @Override
    public void registerVariants()
    {
        Item fuel = Item.getItemFromBlock(GCBlocks.fuel);
        ModelBakery.registerItemVariants(fuel);
        ModelBakery.addVariantName(fuel, "galacticraftcore:fuel");
        ModelLoader.setCustomMeshDefinition(fuel, new ItemMeshDefinition()
        {
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return fuelLocation;
            }
        });
        ModelLoader.setCustomStateMapper(GCBlocks.fuel, new StateMapperBase()
        {
            protected ModelResourceLocation getModelResourceLocation(IBlockState state)
            {
                return fuelLocation;
            }
        });
        Item oil = Item.getItemFromBlock(GCBlocks.crudeOil);
        ModelBakery.registerItemVariants(oil);
        ModelBakery.addVariantName(oil, "galacticraftcore:oil");
        ModelLoader.setCustomMeshDefinition(oil, new ItemMeshDefinition()
        {
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return oilLocation;
            }
        });
        ModelLoader.setCustomStateMapper(GCBlocks.crudeOil, new StateMapperBase()
        {
            protected ModelResourceLocation getModelResourceLocation(IBlockState state)
            {
                return oilLocation;
            }
        });

        Item nasaWorkbench = Item.getItemFromBlock(GCBlocks.nasaWorkbench);
        ModelResourceLocation modelResourceLocation = new ModelResourceLocation("galacticraftcore:rocket_workbench", "inventory");
        ModelLoader.setCustomModelResourceLocation(nasaWorkbench, 0, modelResourceLocation);

        modelResourceLocation = new ModelResourceLocation("galacticraftcore:rocket_t1", "inventory");
        for (int i = 0; i < 5; ++i)
        {
            ModelLoader.setCustomModelResourceLocation(GCItems.rocketTier1, i, modelResourceLocation);
        }

        for (int i = 0; i < 4; ++i)
        {
            modelResourceLocation = new ModelResourceLocation("galacticraftcore:buggy_" + i, "inventory");
            ModelLoader.setCustomModelResourceLocation(GCItems.buggy, i, modelResourceLocation);
        }

        modelResourceLocation = new ModelResourceLocation("galacticraftcore:oil_canister_partial_0", "inventory");
        for (int i = 0; i < GCItems.oilCanister.getMaxDamage(); ++i)
        {
            ModelLoader.setCustomModelResourceLocation(GCItems.oilCanister, i, modelResourceLocation);
        }

        modelResourceLocation = new ModelResourceLocation("galacticraftcore:flag", "inventory");
        ModelLoader.setCustomModelResourceLocation(GCItems.flag, 0, modelResourceLocation);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onModelBakeEvent(ModelBakeEvent event)
    {
        replaceModelDefault(event, "rocket_workbench", "block/workbench.obj", ImmutableList.of("Cube"), ItemModelWorkbench.class, new ItemTransformVec3f(new Vector3f(), new Vector3f(0.0F, -0.48F, 0.0F), new Vector3f(0.42F, 0.42F, 0.42F)));
        replaceModelDefault(event, "rocket_t1", "rocketT1.obj", ImmutableList.of("Rocket"), ItemModelRocket.class, TRSRTransformation.identity());

        for (int i = 0; i < 4; ++i)
        {
            ImmutableList<String> objects = ImmutableList.of("MainBody", "RadarDish_Dish", "Wheel_Back_Left", "Wheel_Back_Right", "Wheel_Front_Left", "Wheel_Front_Right");
            switch (i)
            {
            case 0:
                break;
            case 1:
                objects = ImmutableList.of("MainBody", "RadarDish_Dish", "Wheel_Back_Left", "Wheel_Back_Right", "Wheel_Front_Left", "Wheel_Front_Right", "CargoLeft");
                break;
            case 2:
                objects = ImmutableList.of("MainBody", "RadarDish_Dish", "Wheel_Back_Left", "Wheel_Back_Right", "Wheel_Front_Left", "Wheel_Front_Right", "CargoLeft", "CargoMid");
                break;
            case 3:
                objects = ImmutableList.of("MainBody", "RadarDish_Dish", "Wheel_Back_Left", "Wheel_Back_Right", "Wheel_Front_Left", "Wheel_Front_Right", "CargoLeft", "CargoMid", "CargoRight");
                break;
            }
            replaceModelDefault(event, "buggy_" + i, "buggyInv.obj", objects, ItemModelBuggy.class, TRSRTransformation.identity());
        }

        replaceModelDefault(event, "flag", "flag.obj", ImmutableList.of("Flag", "Pole"), ItemModelFlag.class, TRSRTransformation.identity());

        for (int i = 0; i < 7; ++i)
        {
            ModelResourceLocation modelResourceLocation = new ModelResourceLocation("galacticraftcore:oil_canister_partial_" + i, "inventory");
            IBakedModel object = event.modelRegistry.getObject(modelResourceLocation);
            if (object != null)
            {
                ItemLiquidCanisterModel modelFinal = new ItemLiquidCanisterModel(object);
                event.modelRegistry.putObject(modelResourceLocation, modelFinal);
            }
            modelResourceLocation = new ModelResourceLocation("galacticraftcore:fuel_canister_partial_" + i, "inventory");
            object = event.modelRegistry.getObject(modelResourceLocation);
            if (object != null)
            {
                ItemLiquidCanisterModel modelFinal = new ItemLiquidCanisterModel(object);
                event.modelRegistry.putObject(modelResourceLocation, modelFinal);
            }
        }
    }

    private void replaceModelDefault(ModelBakeEvent event, String resLoc, String objLoc, List<String> visibleGroups, Class<? extends ModelTransformWrapper> clazz, IModelState parentState)
    {
        ClientUtil.replaceModel(GalacticraftCore.ASSET_PREFIX, event, resLoc, objLoc, visibleGroups, clazz, parentState);
    }
    
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        ClientProxyCore.scaleup.put(ClientProxyCore.numbers, 0, 16);

        OBJLoader.instance.addDomain(GalacticraftCore.ASSET_PREFIX);

        if (Loader.isModLoaded("PlayerAPI"))
        {
//            ClientPlayerAPI.register(Constants.MOD_ID_CORE, GCPlayerBaseSP.class);
        }
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        Class[][] commonTypes =
                {
                        { MusicTicker.MusicType.class, ResourceLocation.class, int.class, int.class },
                };
        MUSIC_TYPE_MARS = EnumHelper.addEnum(commonTypes, MusicTicker.MusicType.class, "MARS_JC", new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "galacticraft.musicSpace"), 12000, 24000);
        ClientProxyCore.registerHandlers();
        ClientProxyCore.registerTileEntityRenderers();
        ClientProxyCore.setupCapes();
        ClientProxyCore.registerInventoryJsons();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
        ClientProxyCore.registerInventoryTabs();
        ClientProxyCore.registerEntityRenderers();
        ClientProxyCore.addVariants();

        MinecraftForge.EVENT_BUS.register(new TabRegistry());
        //ClientProxyCore.playerList = GLAllocation.generateDisplayLists(1);
        
//        if (Loader.isModLoaded("craftguide"))
//        	CraftGuideIntegration.register(); TODO
        
//        try {
//			Field ftc = Minecraft.getMinecraft().getClass().getDeclaredField(VersionUtil.getNameDynamic(VersionUtil.KEY_FIELD_MUSICTICKER));
//			ftc.setAccessible(true);
//			ftc.set(Minecraft.getMinecraft(), new MusicTickerGC(Minecraft.getMinecraft()));
//        } catch (Exception e) {e.printStackTrace();}
    }

    @Override
    public void postRegisterBlock(Block block)
    {
        // ModelLoader.setCustomStateMapper(block, GalacticraftStateMapper.INSTANCE);
    }

    @Override
    public void postRegisterItem(Item item)
    {
        if (!item.getHasSubtypes())
        {
            ClientProxyCore.itemsToRegisterJson.add(item);
        }
    }

    public static void registerEntityRenderers()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityTier1Rocket.class, new RenderTier1Rocket(new ModelRocketTier1(), GalacticraftCore.ASSET_PREFIX, "rocketT1"));
        RenderingRegistry.registerEntityRenderingHandler(EntityEvolvedSpider.class, new RenderEvolvedSpider());
        RenderingRegistry.registerEntityRenderingHandler(EntityEvolvedZombie.class, new RenderEvolvedZombie());
        RenderingRegistry.registerEntityRenderingHandler(EntityEvolvedCreeper.class, new RenderEvolvedCreeper());
        RenderingRegistry.registerEntityRenderingHandler(EntityEvolvedSkeleton.class, new RenderEvolvedSkeleton());
        RenderingRegistry.registerEntityRenderingHandler(EntitySkeletonBoss.class, new RenderEvolvedSkeletonBoss());
        RenderingRegistry.registerEntityRenderingHandler(EntityMeteor.class, new RenderMeteor());
        RenderingRegistry.registerEntityRenderingHandler(EntityFlag.class, new RenderFlag());
        RenderingRegistry.registerEntityRenderingHandler(EntityParachest.class, new RenderParaChest());
        RenderingRegistry.registerEntityRenderingHandler(EntityAlienVillager.class, new RenderAlienVillager());
        RenderingRegistry.registerEntityRenderingHandler(EntityLander.class, new RenderLander());
        RenderingRegistry.registerEntityRenderingHandler(EntityCelestialFake.class, new RenderEntityFake());
        RenderingRegistry.registerEntityRenderingHandler(EntityBuggy.class, new RenderBuggy());
//        RenderingRegistry.registerEntityRenderingHandler(EntityMeteorChunk.class, new RenderMeteorChunk());
//        RenderingRegistry.registerEntityRenderingHandler(EntityBubble.class, new RenderBubble(0.25F, 0.25F, 1.0F));

        if (Loader.isModLoaded("RenderPlayerAPI"))
        {
//            ModelPlayerAPI.register(Constants.MOD_ID_CORE, ModelPlayerBaseGC.class);
//            RenderPlayerAPI.register(Constants.MOD_ID_CORE, RenderPlayerBaseGC.class);
        }
        else
        {
//        	RenderingRegistry.registerEntityRenderingHandler(EntityPlayerSP.class, new RenderPlayerGC());
//        	RenderingRegistry.registerEntityRenderingHandler(EntityOtherPlayerMP.class, new RenderPlayerGC());

            try
            {
                Field field = RenderManager.class.getDeclaredField("playerRenderer");
                field.setAccessible(true);
                field.set(FMLClientHandler.instance().getClient().getRenderManager(), new RenderPlayerGC());

                field = RenderManager.class.getDeclaredField("skinMap");
                field.setAccessible(true);
                Map<String, RenderPlayer> skinMap = (Map<String, RenderPlayer>) field.get(FMLClientHandler.instance().getClient().getRenderManager());
                skinMap.put("default", new RenderPlayerGC());
                skinMap.put("slim", new RenderPlayerGC(true));
                field.set(FMLClientHandler.instance().getClient().getRenderManager(), skinMap);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private static void registerHandlers()
    {
        TickHandlerClient tickHandlerClient = new TickHandlerClient();
        MinecraftForge.EVENT_BUS.register(tickHandlerClient);
        MinecraftForge.EVENT_BUS.register(new KeyHandlerClient());
        ClientRegistry.registerKeyBinding(KeyHandlerClient.galaxyMap);
        ClientRegistry.registerKeyBinding(KeyHandlerClient.openFuelGui);
        ClientRegistry.registerKeyBinding(KeyHandlerClient.toggleAdvGoggles);
        MinecraftForge.EVENT_BUS.register(GalacticraftCore.proxy);
    }

    private static void registerTileEntityRenderers()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTreasureChest.class, new TileEntityTreasureChestRenderer());
//        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityParaChest.class, new TileEntityParachestRenderer());
//        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityNasaWorkbench.class, new TileEntityNasaWorkbenchRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySolar.class, new TileEntitySolarPanelRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityOxygenDistributor.class, new TileEntityBubbleProviderRenderer(0.25F, 0.25F, 1.0F));
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityScreen.class, new TileEntityScreenRenderer());
//            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAluminumWire.class, new TileEntityAluminumWireRenderer());
//            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDish.class, new TileEntityDishRenderer());
//            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityThruster.class, new TileEntityThrusterRenderer());
//            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArclamp.class, new TileEntityArclampRenderer());
//            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityOxygenPipe.class, new TileEntityOxygenPipeRenderer());
//            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityOxygenStorageModule.class, new TileEntityMachineRenderer());
//            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCircuitFabricator.class, new TileEntityMachineRenderer());
//            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityElectricIngotCompressor.class, new TileEntityMachineRenderer());
    }

    private static void registerInventoryJsons()
    {
        for (Item toReg : ClientProxyCore.itemsToRegisterJson)
        {
            ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, toReg);
        }

        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.canister, 0, "canister_tin");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.canister, 1, "canister_copper");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.rocketEngine, 0, "tier1engine");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.rocketEngine, 1, "tier1booster");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.parachute, 0, "parachute_plain");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.parachute, 1, "parachute_black");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.parachute, 2, "parachute_blue");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.parachute, 3, "parachute_lime");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.parachute, 4, "parachute_brown");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.parachute, 5, "parachute_darkblue");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.parachute, 6, "parachute_darkgray");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.parachute, 7, "parachute_darkgreen");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.parachute, 8, "parachute_gray");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.parachute, 9, "parachute_magenta");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.parachute, 10, "parachute_orange");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.parachute, 11, "parachute_pink");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.parachute, 12, "parachute_purple");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.parachute, 13, "parachute_red");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.parachute, 14, "parachute_teal");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.parachute, 15, "parachute_yellow");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.schematic, 0, "schematic_buggy");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.schematic, 1, "schematic_rocket_t2");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.key, 0, "key");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.partBuggy, 0, "wheel");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.partBuggy, 1, "seat");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.partBuggy, 2, "storage");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.basicItem, 0, "solar_module_0");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.basicItem, 1, "solar_module_1");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.basicItem, 2, "raw_silicon");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.basicItem, 3, "ingot_copper");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.basicItem, 4, "ingot_tin");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.basicItem, 5, "ingot_aluminum");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.basicItem, 6, "compressed_copper");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.basicItem, 7, "compressed_tin");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.basicItem, 8, "compressed_aluminum");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.basicItem, 9, "compressed_steel");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.basicItem, 10, "compressed_bronze");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.basicItem, 11, "compressed_iron");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.basicItem, 12, "wafer_solar");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.basicItem, 13, "wafer_basic");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.basicItem, 14, "wafer_advanced");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.basicItem, 15, "dehydrated_apple");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.basicItem, 16, "dehydrated_carrot");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.basicItem, 17, "dehydrated_melon");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.basicItem, 18, "dehydrated_potato");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.basicItem, 19, "frequency_module");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.basicItem, 20, "ambient_thermal_controller");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.meteoricIronIngot, 0, "meteoric_iron_ingot");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.meteoricIronIngot, 1, "compressed_meteoric_iron");
        for (int i = 0; i <= GCItems.oilCanister.getMaxDamage(); ++i)
        {
            int damage = 6 * i / GCItems.oilCanister.getMaxDamage();
            ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.oilCanister, i, "oil_canister_partial_" + (7 - damage - 1));
        }
        for (int i = 0; i <= GCItems.fuelCanister.getMaxDamage(); ++i)
        {
            int damage = 6 * i / GCItems.fuelCanister.getMaxDamage();
            ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.fuelCanister, i, "fuel_canister_partial_" + (7 - damage - 1));
        }
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.meteorChunk, 0, "meteor_chunk");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.meteorChunk, 1, "meteor_chunk_hot");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.buggy, 0, "buggy_0");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.buggy, 1, "buggy_1");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.buggy, 2, "buggy_2");
        ClientUtil.registerItemJson(GalacticraftCore.TEXTURE_PREFIX, GCItems.buggy, 3, "buggy_3");

        // Blocks
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.breatheableAir);
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.brightAir);
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.brightBreatheableAir);
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.brightLamp);
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.treasureChestTier1);
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.landingPad, 0, "landing_pad");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.landingPad, 1, "buggy_pad");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.unlitTorch);
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.unlitTorchLit);
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.oxygenDistributor);
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.oxygenPipe);
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.oxygenCollector);
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.oxygenCompressor, 0, "oxygen_compressor");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.oxygenCompressor, 4, "oxygen_decompressor");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.oxygenSealer);
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.oxygenDetector);
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.nasaWorkbench);
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.fallenMeteor);
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.basicBlock, 3, "deco_block_0");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.basicBlock, 4, "deco_block_1");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.basicBlock, 5, "ore_copper_gc");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.basicBlock, 6, "ore_tin_gc");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.basicBlock, 7, "ore_aluminum_gc");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.basicBlock, 8, "ore_silicon");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.basicBlock, 9, "block_copper_gc");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.basicBlock, 10, "block_tin_gc");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.basicBlock, 11, "block_aluminum_gc");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.basicBlock, 12, "block_meteoric_iron_gc");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.airLockFrame, 0, "air_lock_frame");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.airLockFrame, 1, "air_lock_controller");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.airLockSeal);
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.refinery);
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.fuelLoader);
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.landingPadFull, 0, "landing_pad_full");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.landingPadFull, 1, "buggy_pad_full");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.spaceStationBase);
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.fakeBlock);
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.sealableBlock, 1, "enclosed_oxygen_pipe");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.sealableBlock, 14, "enclosed_aluminum_wire");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.sealableBlock, 15, "enclosed_heavy_aluminum_wire");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.cargoLoader, 0, "cargo_loader");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.cargoLoader, 4, "cargo_unloader");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.parachest);
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.solarPanel, 0, "basic_solar");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.solarPanel, 4, "advanced_solar");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.machineBase, 0, "coal_generator");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.machineBase, 12, "ingot_compressor");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.machineBase2, 0, "electric_ingot_compressor");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.machineBase2, 4, "circuit_fabricator");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.machineBase2, 8, "oxygen_storage_module");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.machineTiered, 0, "energy_storage");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.machineTiered, 4, "electric_furnace");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.machineTiered, 8, "cluster_storage");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.machineTiered, 12, "arc_furnace");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.aluminumWire, 0, "aluminum_wire");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.aluminumWire, 1, "aluminum_wire_heavy");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.glowstoneTorch);
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.blockMoon, 0, "ore_copper_moon");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.blockMoon, 1, "ore_tin_moon");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.blockMoon, 2, "ore_cheese_moon");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.blockMoon, 3, "moon_dirt_moon");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.blockMoon, 4, "moon_stone");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.blockMoon, 5, "moon_turf_0");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.blockMoon, 14, "moon_dungeon_brick");
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.cheeseBlock);
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.spinThruster);
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.screen);
        ClientUtil.registerBlockJson(GalacticraftCore.TEXTURE_PREFIX, GCBlocks.telemetry);
    }

    private static void addVariants()
    {
        addCoreVariant("air_lock_frame", "air_lock_frame", "air_lock_controller");
        addCoreVariant("basic_block_core", "deco_block_0", "deco_block_1", "ore_copper_gc", "ore_tin_gc", "ore_aluminum_gc", "ore_silicon", "block_copper_gc", "block_tin_gc", "block_aluminum_gc", "block_meteoric_iron_gc");
        addCoreVariant("air_lock_frame", "air_lock_frame", "air_lock_controller");
        addCoreVariant("landing_pad", "landing_pad", "buggy_pad");
        addCoreVariant("oxygen_compressor", "oxygen_compressor", "oxygen_decompressor");
        addCoreVariant("cargo", "cargo_loader", "cargo_unloader");
        addCoreVariant("enclosed", "enclosed_oxygen_pipe", "enclosed_aluminum_wire", "enclosed_heavy_aluminum_wire");
        addCoreVariant("solar", "advanced_solar", "basic_solar");
        addCoreVariant("machine", "coal_generator", "ingot_compressor");
        addCoreVariant("machine2", "circuit_fabricator", "oxygen_storage_module", "electric_ingot_compressor");
        addCoreVariant("machine_tiered", "energy_storage", "electric_furnace", "cluster_storage", "arc_furnace");
        addCoreVariant("basic_block_moon", "ore_copper_moon", "ore_tin_moon", "ore_cheese_moon", "moon_dirt_moon", "moon_stone", "moon_turf_0", "moon_dungeon_brick");
        addCoreVariant("canister", "canister_tin", "canister_copper");
        addCoreVariant("engine", "tier1engine", "tier1booster");
        addCoreVariant("parachute", "parachute_plain", "parachute_black", "parachute_blue", "parachute_lime", "parachute_brown", "parachute_darkblue", "parachute_darkgray", "parachute_darkgreen", "parachute_gray", "parachute_magenta", "parachute_orange", "parachute_pink", "parachute_purple", "parachute_red", "parachute_teal", "parachute_yellow");
        addCoreVariant("oil_canister_partial", "oil_canister_partial_0", "oil_canister_partial_1", "oil_canister_partial_2", "oil_canister_partial_3", "oil_canister_partial_4", "oil_canister_partial_5", "oil_canister_partial_6");
        addCoreVariant("fuel_canister_partial", "fuel_canister_partial_0", "fuel_canister_partial_1", "fuel_canister_partial_2", "fuel_canister_partial_3", "fuel_canister_partial_4", "fuel_canister_partial_5", "fuel_canister_partial_6");
        addCoreVariant("schematic", "schematic_buggy", "schematic_rocket_t2");
        addCoreVariant("key", "key");
        addCoreVariant("buggymat", "wheel", "seat", "storage");
        addCoreVariant("basic_item", "solar_module_0", "solar_module_1", "raw_silicon", "ingot_copper", "ingot_tin", "ingot_aluminum", "compressed_copper", "compressed_tin", "compressed_aluminum", "compressed_steel", "compressed_bronze", "compressed_iron", "wafer_solar", "wafer_basic", "wafer_advanced", "dehydrated_apple", "dehydrated_carrot", "dehydrated_melon", "dehydrated_potato", "frequency_module", "ambient_thermal_controller");
        addCoreVariant("meteoric_iron_ingot", "meteoric_iron_ingot", "compressed_meteoric_iron");
        addCoreVariant("aluminum_wire", "aluminum_wire", "aluminum_wire_heavy");
        addCoreVariant("meteor_chunk", "meteor_chunk", "meteor_chunk_hot");
        addCoreVariant("buggy", "buggy_0", "buggy_1", "buggy_2", "buggy_3");
    }

    private static void addCoreVariant(String name, String... variants)
    {
        Item itemBlockVariants = GameRegistry.findItem(Constants.MOD_ID_CORE, name);
        String[] variants0 = new String[variants.length];
        for (int i = 0; i < variants.length; ++i)
        {
            variants0[i] = GalacticraftCore.TEXTURE_PREFIX + variants[i];
        }
        ModelBakery.addVariantName(itemBlockVariants, variants0);
    }

    public static void setupCapes()
    {
        try
        {
            ClientProxyCore.updateCapeList();
        }
        catch (Exception e)
        {
            FMLLog.severe("Error while setting up Galacticraft donor capes");
            e.printStackTrace();
        }

        /**
        if (Loader.isModLoaded("CoFHCore"))
        {
            for (Entry<String, String> e : ClientProxyCore.capeMap.entrySet())
            {
                try
                {
                    Object capeRegistry = Class.forName("cofh.api.core.RegistryAccess").getField("capeRegistry").get(null);
                    Class.forName("cofh.api.core.ISimpleRegistry").getMethod("register", String.class, String.class).invoke(capeRegistry, e.getKey(), e.getValue());
                }
                catch (Exception e1)
                {
                    e1.printStackTrace();
                    break;
                }
            }
        }
        **/
    }

    private static void updateCapeList()
    {
        int timeout = 10000;
        URL capeListUrl = null;
        
		try 
		{
			capeListUrl = new URL("https://raw.github.com/micdoodle8/Galacticraft/master/capes.txt");
		} 
		catch (MalformedURLException e) 
		{
            FMLLog.severe("Error getting capes list URL");
			e.printStackTrace();
			return;
		}
		
        URLConnection connection = null;
        
		try 
		{
			connection = capeListUrl.openConnection();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return;
		}
		
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);
        InputStream stream = null;
        
		try 
		{
			stream = connection.getInputStream();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return;
		}
		
        InputStreamReader streamReader = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(streamReader);

        String line;
        try 
        {
			while ((line = reader.readLine()) != null)
			{
			    if (line.contains(":"))
			    {
			        int splitLocation = line.indexOf(":");
			        String username = line.substring(0, splitLocation);
			        String capeUrl = "https://raw.github.com/micdoodle8/Galacticraft/master/capes/" + line.substring(splitLocation + 1) + ".png";
			        ClientProxyCore.capeMap.put(username, capeUrl);
			    }
			}
		} 
        catch (IOException e)
        {
			e.printStackTrace();
		}
        
        try 
        {
			reader.close();
		} 
        catch (IOException e)
        {
			e.printStackTrace();
		}
        try 
        {
			streamReader.close();
		} 
        catch (IOException e) 
        {
			e.printStackTrace();
		}
        try 
        {
			stream.close();
		} 
        catch (IOException e) 
        {
			e.printStackTrace();
		}
    }

    public static void registerInventoryTabs()
    {
        if (TabRegistry.getTabList().size() == 0)
        {
            TabRegistry.registerTab(new InventoryTabVanilla());
        }

        TabRegistry.registerTab(new InventoryTabGalacticraft());
    }

    public static void renderPlanets(float par3)
    {
    }

    @Override
    public int getBlockRender(Block blockID)
    {
//        if (blockID == GCBlocks.breatheableAir || blockID == GCBlocks.brightBreatheableAir)
//        {
//            return ClientProxyCore.renderIdBreathableAir;
//        }
//        else if (blockID == GCBlocks.oxygenPipe)
//        {
//            return ClientProxyCore.renderIdOxygenPipe;
//        }
//        else if (blockID == GCBlocks.fallenMeteor)
//        {
//            return ClientProxyCore.renderIdMeteor;
//        }
//        else if (blockID == GCBlocks.nasaWorkbench)
//        {
//            return ClientProxyCore.renderIdCraftingTable;
//        }
//        else if (blockID == GCBlocks.landingPadFull)
//        {
//            return ClientProxyCore.renderIdLandingPad;
//        }
//        else if (blockID instanceof BlockUnlitTorch || blockID == GCBlocks.glowstoneTorch)
//        {
//            return ClientProxyCore.renderIdTorchUnlit;
//        }
//        else if (blockID == GCBlocks.fuelLoader || blockID == GCBlocks.cargoLoader || blockID == GCBlocks.machineBase || blockID == GCBlocks.machineBase2 || blockID == GCBlocks.machineTiered || blockID == GCBlocks.oxygenCollector || blockID == GCBlocks.oxygenCompressor || blockID == GCBlocks.oxygenDetector || blockID == GCBlocks.oxygenDistributor || blockID == GCBlocks.oxygenSealer || blockID == GCBlocks.refinery || blockID == GCBlocks.telemetry)
//        {
//            return ClientProxyCore.renderIdMachine;
//        }
//        else if (blockID == GCBlocks.treasureChestTier1)
//        {
//            return ClientProxyCore.renderIdTreasureChest;
//        }
//        else if (blockID == GCBlocks.parachest)
//        {
//            return ClientProxyCore.renderIdParachest;
//        }

        return 3;
    }

    @Override
    public World getClientWorld()
    {
        return ClientProxyCore.mc.theWorld;
    }

    @Override
    public void spawnParticle(String particleID, Vector3 position, Vector3 motion, Object[] otherInfo)
    {
        EffectHandler.spawnParticle(particleID, position, motion, otherInfo);
    }

    public static void renderLiquidOverlays(float partialTicks)
    {
        if (ClientProxyCore.isInsideOfFluid(ClientProxyCore.mc.thePlayer, GalacticraftCore.fluidOil))
        {
        	ClientProxyCore.mc.getTextureManager().bindTexture(ClientProxyCore.underOilTexture);
        }
        else
        {
            return;
        }

        Tessellator tessellator = Tessellator.getInstance();
        float f1 = ClientProxyCore.mc.thePlayer.getBrightness(partialTicks) / 3.0F;
        GL11.glColor4f(f1, f1, f1, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glPushMatrix();
        float f2 = 4.0F;
        float f3 = -1.0F;
        float f4 = 1.0F;
        float f5 = -1.0F;
        float f6 = 1.0F;
        float f7 = -0.5F;
        float f8 = -ClientProxyCore.mc.thePlayer.rotationYaw / 64.0F;
        float f9 = ClientProxyCore.mc.thePlayer.rotationPitch / 64.0F;
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(f3, f5, f7).tex(f2 + f8, f2 + f9).endVertex();
        worldRenderer.pos(f4, f5, f7).tex(0.0F + f8, f2 + f9).endVertex();
        worldRenderer.pos(f4, f6, f7).tex(0.0F + f8, 0.0F + f9).endVertex();
        worldRenderer.pos(f3, f6, f7).tex(f2 + f8, 0.0F + f9).endVertex();
        tessellator.draw();
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static boolean isInsideOfFluid(Entity entity, Fluid fluid)
    {
        double d0 = entity.posY + entity.getEyeHeight();
        int i = MathHelper.floor_double(entity.posX);
        int j = MathHelper.floor_float(MathHelper.floor_double(d0));
        int k = MathHelper.floor_double(entity.posZ);
        BlockPos pos = new BlockPos(i, j, k);
        Block block = entity.worldObj.getBlockState(pos).getBlock();

        if (block != null && block instanceof IFluidBlock && ((IFluidBlock) block).getFluid() != null && ((IFluidBlock) block).getFluid().getName().equals(fluid.getName()))
        {
            double filled = ((IFluidBlock) block).getFilledPercentage(entity.worldObj, pos);
            if (filled < 0)
            {
                filled *= -1;
                return d0 > j + (1 - filled);
            }
            else
            {
                return d0 < j + filled;
            }
        }
        else
        {
            return false;
        }
    }

    public static void renderFootprints(float partialTicks)
    {
        ClientProxyCore.footprintRenderer.renderFootprints(ClientProxyCore.mc.thePlayer, partialTicks);
        MinecraftForge.EVENT_BUS.post(new EventSpecialRender(partialTicks));
    }

    public static class EventSpecialRender extends Event
    {
        public final float partialTicks;

        public EventSpecialRender(float partialTicks)
        {
            this.partialTicks = partialTicks;
        }
    }

    @Override
    public World getWorldForID(int dimensionID)
    {
        World world = ClientProxyCore.mc.theWorld;

        if (world != null && world.provider.getDimensionId() == dimensionID)
        {
            return world;
        }

        return null;
    }

    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event)
    {
        GL11.glPushMatrix();

        final EntityPlayer player = event.entityPlayer;

        if (player.ridingEntity instanceof EntityTieredRocket && player == Minecraft.getMinecraft().thePlayer
        		&& Minecraft.getMinecraft().gameSettings.thirdPersonView == 0)
        {
            EntityTieredRocket entity = (EntityTieredRocket) player.ridingEntity;
            GL11.glTranslatef(0, -entity.getRotateOffset() - PLAYER_Y_OFFSET, 0);
            float anglePitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * event.partialRenderTick;
            float angleYaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * event.partialRenderTick;
            GL11.glRotatef(-angleYaw, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(anglePitch, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(0, entity.getRotateOffset() + PLAYER_Y_OFFSET, 0);
        }

        //Gravity - freefall - jetpack changes in player model orientation can go here
    }

    @SubscribeEvent
    public void onRenderPlayerPost(RenderPlayerEvent.Post event)
    {
        GL11.glPopMatrix();
    }

    @SubscribeEvent
    public void onRenderPlayerEquipped(RenderPlayerEvent.Specials.Pre event)
    {
        final Entity ridden = event.entityPlayer.ridingEntity;
        if (ridden instanceof EntityAutoRocket || ridden instanceof EntityLanderBase)
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPostRender(RenderPlayerEvent.Specials.Post event)
    {
        AbstractClientPlayer player = (AbstractClientPlayer) event.entityPlayer;
        boolean flag = ClientProxyCore.capeMap.containsKey(event.entityPlayer.getName());
        float f4;

//        if (flag && !player.isInvisible() && !player.getHideCape())
//        {
//            String url = ClientProxyCore.capeMap.get(player.getCommandSenderName());
//            ResourceLocation capeLoc = capesMap.get(url);
//            if (!capesMap.containsKey(url))
//            {
//                try
//                {
//                    String dirName = Minecraft.getMinecraft().mcDataDir.getAbsolutePath();
//                    File directory = new File(dirName, "assets");
//                    boolean success = true;
//                    if (!directory.exists())
//                    {
//                        success = directory.mkdir();
//                    }
//                    if (success)
//                    {
//                        directory = new File(directory, "gcCapes");
//                        if (!directory.exists())
//                        {
//                            success = directory.mkdir();
//                        }
//
//                        if (success)
//                        {
//                            String hash = String.valueOf(player.getCommandSenderName().hashCode());
//                            File file1 = new File(directory, hash.substring(0, 2));
//                            File file2 = new File(file1, hash);
//                            final ResourceLocation resourcelocation = new ResourceLocation("gcCapes/" + hash);
//                            ThreadDownloadImageDataGC threaddownloadimagedata = new ThreadDownloadImageDataGC(file2, url, null, new IImageBuffer()
//                            {
//                                public BufferedImage parseUserSkin(BufferedImage p_78432_1_)
//                                {
//                                    if (p_78432_1_ == null)
//                                    {
//                                        return null;
//                                    }
//                                    else
//                                    {
//                                        BufferedImage bufferedimage1 = new BufferedImage(512, 256, 2);
//                                        Graphics graphics = bufferedimage1.getGraphics();
//                                        graphics.drawImage(p_78432_1_, 0, 0, null);
//                                        graphics.dispose();
//                                        p_78432_1_ = bufferedimage1;
//                                    }
//                                    return p_78432_1_;
//                                }
//
//                                public void func_152634_a()
//                                {
//                                }
//                            });
//
//                            if (ClientProxyCore.mc.getTextureManager().loadTexture(resourcelocation, threaddownloadimagedata))
//                            {
//                                capeLoc = resourcelocation;
//                            }
//                        }
//                    }
//                }
//                catch (Exception e)
//                {
//                    e.printStackTrace();
//                }
//
//                capesMap.put(url, capeLoc);
//            }
//
//            if (capeLoc != null)
//            {
//            	ClientProxyCore.mc.getTextureManager().bindTexture(capeLoc);
//                GL11.glPushMatrix();
//                GL11.glTranslatef(0.0F, 0.0F, 0.125F);
//                double d3 = player.field_71091_bM + (player.field_71094_bP - player.field_71091_bM) * event.partialRenderTick - (player.prevPosX + (player.posX - player.prevPosX) * event.partialRenderTick);
//                double d4 = player.field_71096_bN + (player.field_71095_bQ - player.field_71096_bN) * event.partialRenderTick - (player.prevPosY + (player.posY - player.prevPosY) * event.partialRenderTick);
//                double d0 = player.field_71097_bO + (player.field_71085_bR - player.field_71097_bO) * event.partialRenderTick - (player.prevPosZ + (player.posZ - player.prevPosZ) * event.partialRenderTick);
//                f4 = (player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * event.partialRenderTick) / 57.29578F;
//                double d1 = MathHelper.sin(f4);
//                double d2 = -MathHelper.cos(f4);
//                float f5 = (float) d4 * 10.0F;
//
//                if (f5 < -6.0F)
//                {
//                    f5 = -6.0F;
//                }
//
//                if (f5 > 32.0F)
//                {
//                    f5 = 32.0F;
//                }
//
//                float f6 = (float) (d3 * d1 + d0 * d2) * 100.0F;
//                float f7 = (float) (d3 * d2 - d0 * d1) * 100.0F;
//
//                if (f6 < 0.0F)
//                {
//                    f6 = 0.0F;
//                }
//
//                float f8 = player.prevCameraYaw + (player.cameraYaw - player.prevCameraYaw) * event.partialRenderTick;
//                f5 += MathHelper.sin((player.prevDistanceWalkedModified + (player.distanceWalkedModified - player.prevDistanceWalkedModified) * event.partialRenderTick) * 6.0F) * 32.0F * f8;
//
//                if (player.isSneaking())
//                {
//                    f5 += 25.0F;
//                }
//
//                GL11.glRotatef(6.0F + f6 / 2.0F + f5, 1.0F, 0.0F, 0.0F);
//                GL11.glRotatef(f7 / 2.0F, 0.0F, 0.0F, 1.0F);
//                GL11.glRotatef(-f7 / 2.0F, 0.0F, 1.0F, 0.0F);
//                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
//                event.renderer.modelBipedMain.renderCloak(0.0625F);
//                GL11.glPopMatrix();
//            }
//        }
    }

    public static void adjustRenderPos(Entity entity, double offsetX, double offsetY, double offsetZ)
    {
        GL11.glPushMatrix();
        //Skip mobs in mobspawners
        //Note: can also look for (entity.posY!=0.0D || entity.posX!=0.0D || entity.posZ!=0.0) which filters hand-held entities and the player in an inventory GUI
        if (ClientProxyCore.smallMoonActive && (offsetX != 0.0D || offsetY != 0.0D || offsetZ != 0.0D))
        {
            final EntityPlayerSP player = ClientProxyCore.mc.thePlayer;
            if (player.posY > ClientProxyCore.terrainHeight + 8F && player.ridingEntity != entity && player != entity)
            {
                double globalArc = ClientProxyCore.globalRadius / 57.2957795D;

                int pX = MathHelper.floor_double(player.posX / 16D) << 4;
                int pZ = MathHelper.floor_double(player.posZ / 16D) << 4;

                int eX = MathHelper.floor_double(entity.posX / 16D) << 4;
                int eY = MathHelper.floor_double(entity.posY / 16D) << 4;
                int eZ = MathHelper.floor_double(entity.posZ / 16D) << 4;

                float dX = eX - pX;
                float dZ = eZ - pZ;

                float floatPX = (float) player.posX;
                float floatPZ = (float) player.posZ;

                if (dX > 0)
                {
                    dX -= 16F;
                    if (dX > 0)
                    {
                        dX -= floatPX - pX;
                    }
                }
                else if (dX < 0)
                {
                    dX += 16F;
                    if (dX < 0)
                    {
                        dX += 16F - floatPX + pX;
                    }
                }

                if (dZ > 0)
                {
                    dZ -= 16F;
                    if (dZ > 0)
                    {
                        dZ -= floatPZ - pZ;
                    }
                }
                else if (dZ < 0)
                {
                    dZ += 16F;
                    if (dZ < 0)
                    {
                        dZ += 16F - floatPZ + pZ;
                    }
                }

                float theta = (float) MathHelper.wrapAngleTo180_double(dX / globalArc);
                float phi = (float) MathHelper.wrapAngleTo180_double(dZ / globalArc);
                if (theta < 0)
                {
                    theta += 360F;
                }
                if (phi < 0)
                {
                    phi += 360F;
                }
                float ytranslate = ClientProxyCore.globalRadius + (float) (player.posY - entity.posY) + eY - ClientProxyCore.terrainHeight;
                GL11.glTranslatef(-dX + eX - floatPX + 8F, -ytranslate, -dZ + eZ - floatPZ + 8F);
                if (theta > 0)
                {
                    GL11.glRotatef(theta, 0, 0, -1);
                }
                if (phi > 0)
                {
                    GL11.glRotatef(phi, 1, 0, 0);
                }
                GL11.glTranslatef(floatPX - eX - 8F, ytranslate, floatPZ - eZ - 8F);
            }
        }
    }

    public static void adjustTileRenderPos(TileEntity tile, double offsetX, double offsetY, double offsetZ)
    {
        GL11.glPushMatrix();
        //Skip tiles in inventory or in player's hand etc
        if (ClientProxyCore.smallMoonActive && (offsetX != 0.0D || offsetY != 0.0D || offsetZ != 0.0D))
        {
            final EntityPlayerSP player = ClientProxyCore.mc.thePlayer;
            final WorldProvider provider = ClientProxyCore.mc.theWorld.provider;
            if (provider instanceof WorldProviderMoon)
            {
                if (player.posY > ClientProxyCore.terrainHeight + 8F)
                {
                    double globalArc = ClientProxyCore.globalRadius / 57.2957795D;

                    int pX = MathHelper.floor_double(player.posX / 16D) << 4;
                    int pZ = MathHelper.floor_double(player.posZ / 16D) << 4;

                    int eX = tile.getPos().getX() / 16 << 4;
                    int eY = tile.getPos().getY() / 16 << 4;
                    int eZ = tile.getPos().getZ() / 16 << 4;

                    float dX = eX - pX;
                    float dZ = eZ - pZ;

                    float floatPX = (float) player.posX;
                    float floatPZ = (float) player.posZ;

                    if (dX > 0)
                    {
                        dX -= 16F;
                        if (dX > 0)
                        {
                            dX -= floatPX - pX;
                        }
                    }
                    else if (dX < 0)
                    {
                        dX += 16F;
                        if (dX < 0)
                        {
                            dX += 16F - floatPX + pX;
                        }
                    }

                    if (dZ > 0)
                    {
                        dZ -= 16F;
                        if (dZ > 0)
                        {
                            dZ -= floatPZ - pZ;
                        }
                    }
                    else if (dZ < 0)
                    {
                        dZ += 16F;
                        if (dZ < 0)
                        {
                            dZ += 16F - floatPZ + pZ;
                        }
                    }

                    float theta = (float) MathHelper.wrapAngleTo180_double(dX / globalArc);
                    float phi = (float) MathHelper.wrapAngleTo180_double(dZ / globalArc);
                    if (theta < 0)
                    {
                        theta += 360F;
                    }
                    if (phi < 0)
                    {
                        phi += 360F;
                    }
                    float ytranslate = ClientProxyCore.globalRadius + (float) player.posY - tile.getPos().getY() + eY - ClientProxyCore.terrainHeight;
                    GL11.glTranslatef(-dX - floatPX + eX + 8F, -ytranslate, -dZ - floatPZ + eZ + 8F);
                    if (theta > 0)
                    {
                        GL11.glRotatef(theta, 0, 0, -1);
                    }
                    if (phi > 0)
                    {
                        GL11.glRotatef(phi, 1, 0, 0);
                    }
                    GL11.glTranslatef(floatPX - eX - 8F, ytranslate, floatPZ - eZ - 8F);
                }
            }
        }
    }

    public static void orientCamera(float partialTicks)
    {
        /*EntityClientPlayerMP player = ClientProxyCore.mc.thePlayer;
        GCPlayerStatsClient stats = GCPlayerStatsClient.get(player);

        EntityLivingBase entityLivingBase = ClientProxyCore.mc.getRenderViewEntity();

        if (player.ridingEntity instanceof EntityTieredRocket && ClientProxyCore.mc.gameSettings.thirdPersonView == 0)
        {
            EntityTieredRocket entity = (EntityTieredRocket) player.ridingEntity;
            float offset = entity.getRotateOffset() + PLAYER_Y_OFFSET;
            GL11.glTranslatef(0, -offset, 0);
            float anglePitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
            float angleYaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
            GL11.glRotatef(-anglePitch, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(angleYaw, 0.0F, 1.0F, 0.0F);

            GL11.glTranslatef(0, offset, 0);
        }

        if (entityLivingBase.worldObj.provider instanceof WorldProviderOrbit && !entityLivingBase.isPlayerSleeping())
        {
            float f1 = entityLivingBase.yOffset - 1.62F;
            float pitch = entityLivingBase.prevRotationPitch + (entityLivingBase.rotationPitch - entityLivingBase.prevRotationPitch) * partialTicks;
            float yaw = entityLivingBase.prevRotationYaw + (entityLivingBase.rotationYaw - entityLivingBase.prevRotationYaw) * partialTicks + 180.0F;
            float eyeHeightChange = entityLivingBase.yOffset - entityLivingBase.width / 2.0F;

            GL11.glTranslatef(0.0F, -f1, 0.0F);
            GL11.glRotatef(-yaw, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-pitch, 1.0F, 0.0F, 0.0F);
            GL11.glTranslatef(0.0F, 0.0F, 0.1F);

            GL11.glRotatef(180.0F * stats.gdir.getThetaX(), 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(180.0F * stats.gdir.getThetaZ(), 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(pitch * stats.gdir.getPitchGravityX(), 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(pitch * stats.gdir.getPitchGravityY(), 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(yaw * stats.gdir.getYawGravityX(), 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(yaw * stats.gdir.getYawGravityY(), 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(yaw * stats.gdir.getYawGravityZ(), 0.0F, 0.0F, 1.0F);

            if (stats.landingTicks > 0)
            {
            	float sneakY;
            	if (stats.landingTicks >= 4) sneakY = (stats.landingTicks >= 5) ? 0.15F : 0.3F;
            	else
            		sneakY = stats.landingTicks * 0.075F;
            	GL11.glTranslatef(sneakY * stats.gdir.getSneakVecX(), sneakY * stats.gdir.getSneakVecY(), sneakY * stats.gdir.getSneakVecZ());
            }

            GL11.glTranslatef(eyeHeightChange * stats.gdir.getEyeVecX(), eyeHeightChange * stats.gdir.getEyeVecY(), eyeHeightChange * stats.gdir.getEyeVecZ());

            if (stats.gravityTurnRate < 1.0F)
            {
                GL11.glRotatef(90.0F * (stats.gravityTurnRatePrev + (stats.gravityTurnRate - stats.gravityTurnRatePrev) * partialTicks), stats.gravityTurnVecX, stats.gravityTurnVecY, stats.gravityTurnVecZ);
            }
        }*/

        //omit this for interesting 3P views
        //GL11.glTranslatef(0.0F, 0.0F, -0.1F);
        //GL11.glRotatef(pitch, 1.0F, 0.0F, 0.0F);
        //GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
        //GL11.glTranslatef(0.0F, f1, 0.0F);
    }

    public static void adjustRenderCamera()
    {
        GL11.glPushMatrix();
    }

    public static void setPositionList(WorldRenderer rend, int glRenderList)
    {
        /*GL11.glNewList(glRenderList + 3, GL11.GL_COMPILE);

        EntityLivingBase entitylivingbase = ClientProxyCore.mc.getRenderViewEntity();

        if (entitylivingbase != null)
        {
            if (rend.worldObj.provider instanceof WorldProviderMoon)
            {
                //See what a small moon looks like, for demo purposes
                //Note: terrainHeight must never be less than globalRadius
                ClientProxyCore.globalRadius = 300F;
                ClientProxyCore.terrainHeight = 64F;

                if (entitylivingbase.posY > ClientProxyCore.terrainHeight + 8F)
                {
                    ClientProxyCore.smallMoonActive = true;
                    double globalArc = ClientProxyCore.globalRadius / 57.2957795D;
                    float globeRadius = ClientProxyCore.globalRadius - ClientProxyCore.terrainHeight;

                    int pX = MathHelper.floor_double(entitylivingbase.posX / 16D) << 4;
                    int pZ = MathHelper.floor_double(entitylivingbase.posZ / 16D) << 4;

                    float dX = rend.posX - pX;
                    float dZ = rend.posZ - pZ;
                    float scalerX = 0;
                    float scalerZ = 0;

                    if (dX > 0)
                    {
                        dX -= 16F;
                        if (dX > 0)
                        {
                            dX -= entitylivingbase.posX - pX;
                            if (dX < 16F)
                            {
                                scalerX = 16F - ((float) entitylivingbase.posX - pX);
                            }
                            else
                            {
                                scalerX = 16F;
                            }
                        }
                    }
                    else if (dX < 0)
                    {
                        dX += 16F;
                        if (dX < 0)
                        {
                            dX += 16F - (entitylivingbase.posX - pX);
                            if (dX > -16F)
                            {
                                scalerX = (float) entitylivingbase.posX - pX;
                            }
                            else
                            {
                                scalerX = 16F;
                            }
                        }
                    }

                    if (dZ > 0)
                    {
                        dZ -= 16F;
                        if (dZ > 0)
                        {
                            dZ -= entitylivingbase.posZ - pZ;
                            if (dZ < 16F)
                            {
                                scalerZ = 16F - ((float) entitylivingbase.posZ - pZ);
                            }
                            else
                            {
                                scalerZ = 16F;
                            }
                        }
                    }
                    else if (dZ < 0)
                    {
                        dZ += 16F;
                        if (dZ < 0)
                        {
                            dZ += 16F - (entitylivingbase.posZ - pZ);
                            if (dZ > -16F)
                            {
                                scalerZ = (float) entitylivingbase.posZ - pZ;
                            }
                            else
                            {
                                scalerZ = 16F;
                            }
                        }
                    }

                    float origClipX = rend.posXClip;
                    float origClipY = rend.posYClip;
                    float origClipZ = rend.posZClip;

                    float theta = (float) MathHelper.wrapAngleTo180_double(dX / globalArc);
                    float phi = (float) MathHelper.wrapAngleTo180_double(dZ / globalArc);
                    if (theta < 0)
                    {
                        theta += 360F;
                    }
                    if (phi < 0)
                    {
                        phi += 360F;
                    }
                    GL11.glTranslatef(origClipX - dX + 8F, -globeRadius + 8F, origClipZ - dZ + 8F);
                    if (theta > 0)
                    {
                        GL11.glRotatef(theta, 0, 0, -1F);
                    }
                    if (phi > 0)
                    {
                        GL11.glRotatef(phi, 1F, 0, 0);
                    }
                    GL11.glTranslatef(-8F, origClipY + globeRadius - 8F, -8F);
                    if (dX != 0 || dZ != 0)
                    {
                        float scalex = (ClientProxyCore.globalRadius * 2F + scalerX) / ClientProxyCore.globalRadius / 2F;
                        float scalez = (ClientProxyCore.globalRadius * 2F + scalerZ) / ClientProxyCore.globalRadius / 2F;
                        ClientProxyCore.scaleup.rewind();
                        ClientProxyCore.scaleup.put(scalex);
                        ClientProxyCore.scaleup.position(10);
                        ClientProxyCore.scaleup.put(scalez);
                        ClientProxyCore.scaleup.rewind();
                        GL11.glMultMatrix(ClientProxyCore.scaleup);
                        GL11.glTranslatef(-8F * (scalex - 1F), 0, -8F * (scalez - 1F));
                    }
                    GL11.glTranslatef(-origClipX, -origClipY, -origClipZ);
                    ClientProxyCore.offsetY = rend.posY - ClientProxyCore.terrainHeight;
                }
                else
                {
                    ClientProxyCore.smallMoonActive = false;
                    ClientProxyCore.offsetY = 0;
                }
            }
            else
            {
                ClientProxyCore.terrainHeight = Float.MAX_VALUE;
                ClientProxyCore.globalRadius = Float.MAX_VALUE;
                ClientProxyCore.smallMoonActive = false;
                ClientProxyCore.offsetY = 0;
            }
        }
        GL11.glEndList();*/
    }

    @Override
    public EntityPlayer getPlayerFromNetHandler(INetHandler handler)
    {
        if (handler instanceof NetHandlerPlayServer)
        {
            return ((NetHandlerPlayServer) handler).playerEntity;
        }
        else
        {
            return FMLClientHandler.instance().getClientPlayerEntity();
        }
    }

    //For testing purposes only
    public void addVertex(double x, double y, double z)
    {
        double var7 = 1 + (y + ClientProxyCore.offsetY) / ClientProxyCore.globalRadius;
        x += (x % 16 - 8) * var7 + 8;
        z += (z % 16 - 8) * var7 + 8;
    }


    @SubscribeEvent
    public void onRenderPlanetPre(CelestialBodyRenderEvent.Pre event)
    {
        if (event.celestialBody == GalacticraftCore.planetOverworld)
        {
            if (!ClientProxyCore.overworldTextureRequestSent)
            {
                GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(PacketSimple.EnumSimplePacket.S_REQUEST_OVERWORLD_IMAGE, new Object[] {}));
                ClientProxyCore.overworldTextureRequestSent = true;
            }

            if (ClientProxyCore.overworldTexturesValid)
            {
                event.celestialBodyTexture = null;
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, ClientProxyCore.overworldTextureClient.getGlTextureId());
            }
        }
    }

    @SubscribeEvent
    public void onRenderPlanetPost(CelestialBodyRenderEvent.Post event)
    {
        if (this.mc.currentScreen instanceof GuiCelestialSelection)
        {
        	if (event.celestialBody == GalacticraftCore.planetSaturn)
        	{
                this.mc.renderEngine.bindTexture(saturnRingTexture);
                float size = GuiCelestialSelection.getWidthForCelestialBodyStatic(event.celestialBody) / 6.0F;
                ((GuiCelestialSelection)this.mc.currentScreen).drawTexturedModalRect(-7.5F * size, -1.75F * size, 15.0F * size, 3.5F * size, 0, 0, 30, 7, false, false, 30, 7);
        	}
        	else if (event.celestialBody == GalacticraftCore.planetUranus)
        	{
                this.mc.renderEngine.bindTexture(uranusRingTexture);
                float size = GuiCelestialSelection.getWidthForCelestialBodyStatic(event.celestialBody) / 6.0F;
                ((GuiCelestialSelection)this.mc.currentScreen).drawTexturedModalRect(-1.75F * size, -7.0F * size, 3.5F * size, 14.0F * size, 0, 0, 28, 7, false, false, 28, 7);
        	}
        }
    }
}
