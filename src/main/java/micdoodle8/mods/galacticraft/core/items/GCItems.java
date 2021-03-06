package micdoodle8.mods.galacticraft.core.items;

import com.google.common.collect.Lists;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.util.StackSorted;
import net.minecraftforge.fml.common.registry.GameRegistry;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class GCItems
{
    public static Item oxTankLight;
    public static Item oxTankMedium;
    public static Item oxTankHeavy;
    public static Item oxMask;
    public static Item rocketTier1;
    public static Item sensorGlasses;
    public static Item sensorLens;
    public static Item steelPickaxe;
    public static Item steelAxe;
    public static Item steelHoe;
    public static Item steelSpade;
    public static Item steelSword;
    public static Item steelHelmet;
    public static Item steelChestplate;
    public static Item steelLeggings;
    public static Item steelBoots;
    public static Item canister;
    public static Item oxygenVent;
    public static Item oxygenFan;
    public static Item oxygenConcentrator;
    public static Item rocketEngine;
    public static Item heavyPlatingTier1;
    public static Item partNoseCone;
    public static Item partFins;
    public static Item buggy;
    public static Item flag;
    public static Item oxygenGear;
    public static Item parachute;
    public static Item canvas;
    public static Item flagPole;
    public static Item oilCanister;
    public static Item fuelCanister;
    public static Item oxygenCanisterInfinite;
    public static Item schematic;
    public static Item key;
    public static Item partBuggy;
    public static Item basicItem;
    public static Item battery;
    public static Item infiniteBatery;
    public static Item meteorChunk;
    public static Item wrench;
    public static Item cheeseCurd;
    public static Item meteoricIronRaw;
    public static Item itemBasicMoon;
    public static Item bucketOil;
    public static Item bucketFuel;
//	public static Item cheeseBlock;

//    public static ArmorMaterial addArmorMaterial(String name, String textureName, int durability, int[] reductionAmounts, int enchantability)
    public static ArmorMaterial ARMOR_SENSOR_GLASSES = EnumHelper.addArmorMaterial("SENSORGLASSES", "", 200, new int[] { 0, 0, 0, 0 }, 0);
    public static ArmorMaterial ARMOR_STEEL = EnumHelper.addArmorMaterial("steel", "", 30, new int[] { 3, 8, 6, 3 }, 12);
    public static ToolMaterial TOOL_STEEL = EnumHelper.addToolMaterial("steel", 3, 768, 5.0F, 2, 8);

    public static ArrayList<Item> hiddenItems = new ArrayList<Item>();

    public static void initItems()
    {
        GCItems.oxTankLight = new ItemOxygenTank(1, "oxygen_tank_light_full");
        GCItems.oxTankMedium = new ItemOxygenTank(2, "oxygen_tank_med_full");
        GCItems.oxTankHeavy = new ItemOxygenTank(3, "oxygen_tank_heavy_full");
        GCItems.oxMask = new ItemOxygenMask("oxygen_mask");
        GCItems.rocketTier1 = new ItemTier1Rocket("rocket_t1");
        GCItems.sensorGlasses = new ItemSensorGlasses("sensor_glasses");
        GCItems.steelPickaxe = new ItemPickaxeGC("steel_pickaxe");
        GCItems.steelAxe = new ItemAxeGC("steel_axe");
        GCItems.steelHoe = new ItemHoeGC("steel_hoe");
        GCItems.steelSpade = new ItemSpadeGC("steel_shovel");
        GCItems.steelSword = new ItemSwordGC("steel_sword");
        GCItems.steelHelmet = new ItemArmorGC(0, "helmet");
        GCItems.steelChestplate = new ItemArmorGC(1, "chestplate");
        GCItems.steelLeggings = new ItemArmorGC(2, "leggings");
        GCItems.steelBoots = new ItemArmorGC(3, "boots");
        GCItems.canister = new ItemCanister("canister");
        GCItems.oxygenVent = new ItemBase("air_vent");
        GCItems.oxygenFan = new ItemBase("air_fan");
        GCItems.oxygenConcentrator = new ItemBase("oxygen_concentrator");
        GCItems.heavyPlatingTier1 = new ItemBase("heavy_plating");
        GCItems.rocketEngine = new ItemRocketEngineGC("engine");
        GCItems.partFins = new ItemBase("rocket_fins");
        GCItems.partNoseCone = new ItemBase("nose_cone");
        GCItems.sensorLens = new ItemBase("sensor_lens");
        GCItems.buggy = new ItemBuggy("buggy");
        GCItems.flag = new ItemFlag("flag");
        GCItems.oxygenGear = new ItemOxygenGear("oxygen_gear");
        GCItems.parachute = new ItemParaChute("parachute");
        GCItems.canvas = new ItemBase("canvas");
        GCItems.oilCanister = new ItemOilCanister("oil_canister_partial");
        GCItems.fuelCanister = new ItemFuelCanister("fuel_canister_partial");
        GCItems.oxygenCanisterInfinite = new ItemCanisterOxygenInfinite("infinite_oxygen");
        GCItems.flagPole = new ItemBase("steel_pole");
        GCItems.schematic = new ItemSchematic("schematic");
        GCItems.key = new ItemKey("key");
        GCItems.partBuggy = new ItemBuggyMaterial("buggymat");
        GCItems.basicItem = new ItemBasic("basic_item");
        GCItems.battery = new ItemBattery("battery");
        GCItems.infiniteBatery = new ItemBatteryInfinite("infinite_battery");
        GCItems.meteorChunk = new ItemMeteorChunk("meteor_chunk");
        GCItems.wrench = new ItemUniversalWrench("standard_wrench");
        GCItems.cheeseCurd = new ItemCheese(1, 0.1F, false);
//		GCItems.cheeseBlock = new ItemBlockCheese(GCBlocks.cheeseBlock, "cheeseBlock");
        GCItems.meteoricIronRaw = new ItemMeteoricIron("meteoric_iron_raw");
        GCItems.itemBasicMoon = new ItemMoon("item_basic_moon");

        GCItems.registerHarvestLevels();

        GCItems.registerItems();

        for (int i = 0; i < ItemBasic.names.length; i++)
        {
            if (ItemBasic.names[i].contains("ingot") || ItemBasic.names[i].contains("compressed") || ItemBasic.names[i].contains("wafer"))
            {
                String name = ItemBasic.names[i];
                while (name.contains("_"))
                {
                    int loc = name.indexOf("_");
                    name = name.substring(0, loc) + name.substring(loc + 1, loc + 2).toUpperCase() + name.substring(loc + 2, name.length());
                }
                OreDictionary.registerOre(name, new ItemStack(GCItems.basicItem, 1, i));
            }
        }

        OreDictionary.registerOre("oreMeteoricIron", new ItemStack(GCItems.itemBasicMoon, 1, 1));
        OreDictionary.registerOre("ingotMeteoricIron", new ItemStack(GCItems.itemBasicMoon, 1, 0));
    }

    public static void registerHarvestLevels()
    {
        GCItems.steelPickaxe.setHarvestLevel("pickaxe", 4);
        GCItems.steelAxe.setHarvestLevel("axe", 4);
        GCItems.steelSpade.setHarvestLevel("shovel", 4);
    }

    public static void registerSorted(Item item)
    {
        if (item instanceof ISortableItem)
        {
            ISortableItem sortableItem = (ISortableItem) item;
            List<ItemStack> items = Lists.newArrayList();
            item.getSubItems(item, null, items);
            for (ItemStack stack : items)
            {
                GalacticraftCore.sortMapItems.get(sortableItem.getCategory(stack.getItemDamage())).add(new StackSorted(stack.getItem(), stack.getItemDamage()));
            }
        }
        else if (item.getCreativeTab() == GalacticraftCore.galacticraftItemsTab)
        {
            throw new RuntimeException(item.getClass() + " must inherit " + ISortableItem.class.getSimpleName() + "!");
        }
    }

    public static void registerItems()
    {
        GCItems.registerItem(GCItems.rocketTier1);
        GCItems.registerItem(GCItems.oxMask);
        GCItems.registerItem(GCItems.oxygenGear);
        GCItems.registerItem(GCItems.oxTankLight);
        GCItems.registerItem(GCItems.oxTankMedium);
        GCItems.registerItem(GCItems.oxTankHeavy);
        GCItems.registerItem(GCItems.oxygenCanisterInfinite);
        GCItems.registerItem(GCItems.sensorLens);
        GCItems.registerItem(GCItems.sensorGlasses);
        GCItems.registerItem(GCItems.wrench);
        GCItems.registerItem(GCItems.steelPickaxe);
        GCItems.registerItem(GCItems.steelAxe);
        GCItems.registerItem(GCItems.steelHoe);
        GCItems.registerItem(GCItems.steelSpade);
        GCItems.registerItem(GCItems.steelSword);
        GCItems.registerItem(GCItems.steelHelmet);
        GCItems.registerItem(GCItems.steelChestplate);
        GCItems.registerItem(GCItems.steelLeggings);
        GCItems.registerItem(GCItems.steelBoots);
        GCItems.registerItem(GCItems.canister);
        GCItems.registerItem(GCItems.oxygenVent);
        GCItems.registerItem(GCItems.oxygenFan);
        GCItems.registerItem(GCItems.oxygenConcentrator);
        GCItems.registerItem(GCItems.rocketEngine);
        GCItems.registerItem(GCItems.heavyPlatingTier1);
        GCItems.registerItem(GCItems.partNoseCone);
        GCItems.registerItem(GCItems.partFins);
        GCItems.registerItem(GCItems.flagPole);
        GCItems.registerItem(GCItems.canvas);
        GCItems.registerItem(GCItems.oilCanister);
        GCItems.registerItem(GCItems.fuelCanister);
        GCItems.registerItem(GCItems.schematic);
        GCItems.registerItem(GCItems.key);
        GCItems.registerItem(GCItems.partBuggy);
        GCItems.registerItem(GCItems.buggy);
        GCItems.registerItem(GCItems.basicItem);
        GCItems.registerItem(GCItems.battery);
        GCItems.registerItem(GCItems.infiniteBatery);
        GCItems.registerItem(GCItems.meteorChunk);
        GCItems.registerItem(GCItems.cheeseCurd);
        GCItems.registerItem(GCItems.meteoricIronRaw);
        GCItems.registerItem(GCItems.itemBasicMoon);
//		GCItems.registerItem(GCItems.cheeseBlock);
        GCItems.registerItem(GCItems.flag);
        GCItems.registerItem(GCItems.parachute);
    }

    public static void registerItem(Item item)
    {
        String name = item.getUnlocalizedName().substring(5);
        GCCoreUtil.registerGalacticraftItem(name, item);
        GameRegistry.registerItem(item, item.getUnlocalizedName().substring(5));
        GalacticraftCore.proxy.postRegisterItem(item);
        GCItems.registerSorted(item);
    }
}
