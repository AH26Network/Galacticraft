package micdoodle8.mods.galacticraft.core.util;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import micdoodle8.mods.galacticraft.core.wrappers.ModelTransformWrapper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.dimension.SpaceRace;
import micdoodle8.mods.galacticraft.core.dimension.SpaceRaceManager;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.wrappers.FlagData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.util.List;

@SideOnly(Side.CLIENT)
public class ClientUtil
{
    public static void registerBlockJson(String texturePrefix, Block block)
    {
        registerBlockJson(texturePrefix, block, 0, block.getUnlocalizedName().substring(5));
    }

    public static void registerBlockJson(String texturePrefix, Block block, int meta, String name)
    {
        FMLClientHandler.instance().getClient().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), meta, new ModelResourceLocation(texturePrefix + name, "inventory"));
    }

    public static void registerItemJson(String texturePrefix, Item item)
    {
        registerItemJson(texturePrefix, item, 0, item.getUnlocalizedName().substring(5));
    }

    public static void registerItemJson(String texturePrefix, Item item, int meta, String name)
    {
        FMLClientHandler.instance().getClient().getRenderItem().getItemModelMesher().register(item, meta, new ModelResourceLocation(texturePrefix + name, "inventory"));
    }

    public static ScaledResolution getScaledRes(Minecraft minecraft, int width, int height)
    {
        return new ScaledResolution(minecraft);
//        return VersionUtil.getScaledRes(minecraft, width, height);
    }

    public static FlagData updateFlagData(String playerName, boolean sendPacket)
    {
        SpaceRace race = SpaceRaceManager.getSpaceRaceFromPlayer(playerName);

        if (race != null)
        {
            return race.getFlagData();
        }
        else if (!ClientProxyCore.flagRequestsSent.contains(playerName) && sendPacket)
        {
            GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(EnumSimplePacket.S_REQUEST_FLAG_DATA, new Object[] { playerName }));
            ClientProxyCore.flagRequestsSent.add(playerName);
        }

        return null;
    }

    public static Vector3 updateTeamColor(String playerName, boolean sendPacket)
    {
        SpaceRace race = SpaceRaceManager.getSpaceRaceFromPlayer(playerName);

        if (race != null)
        {
            return race.getTeamColor();
        }
        else if (!ClientProxyCore.flagRequestsSent.contains(playerName) && sendPacket)
        {
            GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(EnumSimplePacket.S_REQUEST_FLAG_DATA, new Object[] { playerName }));
            ClientProxyCore.flagRequestsSent.add(playerName);
        }

        return new Vector3(1, 1, 1);
    }

    public static void replaceModel(String modid, ModelBakeEvent event, String resLoc, String objLoc, List<String> visibleGroups, Class<? extends ModelTransformWrapper> clazz, IModelState parentState)
    {
        ModelResourceLocation modelResourceLocation = new ModelResourceLocation(modid + ":" + resLoc, "inventory");
        IBakedModel object = event.modelRegistry.getObject(modelResourceLocation);
        if (object != null)
        {
            IBakedModel newModel;

            try
            {
                OBJModel model = (OBJModel) ModelLoaderRegistry.getModel(new ResourceLocation(modid, objLoc));
                model = (OBJModel) model.process(ImmutableMap.of("flip-v", "true"));

                Function<ResourceLocation, TextureAtlasSprite> spriteFunction = new Function<ResourceLocation, TextureAtlasSprite>() {
                    @Override
                    public TextureAtlasSprite apply(ResourceLocation location) {
                        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
                    }
                };
                newModel = model.bake(new OBJModel.OBJState(visibleGroups, false, parentState), DefaultVertexFormats.ITEM, spriteFunction);
                if (clazz != null)
                {
                    newModel = clazz.getConstructor(IBakedModel.class).newInstance(newModel);
                }
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }

            event.modelRegistry.putObject(modelResourceLocation, newModel);
        }
    }
}
