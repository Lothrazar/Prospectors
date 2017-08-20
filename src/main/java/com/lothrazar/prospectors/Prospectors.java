package com.lothrazar.prospectors;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(modid = Prospectors.MODID)
public class Prospectors {
  @Instance(value = Prospectors.MODID)
  public static Prospectors instance;
  public static final String MODID = "prospectors";
  private List<Item> items = new ArrayList<Item>();
  private List<IRecipe> recipes = new ArrayList<IRecipe>();
  public final static CreativeTabs TAB = new CreativeTabs(MODID) {
    @Override
    public ItemStack getTabIconItem() {
      return new ItemStack(Items.DIAMOND);
    }
  };
  public enum Types {
    LOWEST, LOW, MED, HIGH, BEST;
  }
  @EventHandler
  public void onPreInit(FMLPreInitializationEvent event) {
    Configuration c = new Configuration(event.getSuggestedConfigurationFile());
    c.load();
    ItemProspector lowest = new ItemProspector(Types.LOWEST);
    ItemProspector low = new ItemProspector(Types.LOW);
    ItemProspector med = new ItemProspector(Types.MED);
    ItemProspector high = new ItemProspector(Types.HIGH);
    ItemProspector best = new ItemProspector(Types.BEST);
    lowest.syncConfig(c,
        new String[] { "minecraft:coal_ore", "minecraft:gravel", "minecraft:sand" });
    low.syncConfig(c,
        new String[] { "minecraft:iron_ore", "minecraft:coal_ore" });
    med.syncConfig(c,
        new String[] { "minecraft:gold_ore", "minecraft:iron_ore", "minecraft:coal_ore" });
    high.syncConfig(c,
        new String[] { "minecraft:iron_ore", "minecraft:gold_ore", "minecraft:iron_ore", "minecraft:coal_ore" });
    best.syncConfig(c,
        new String[] { "minecraft:emerald_ore", "minecraft:iron_ore", "minecraft:gold_ore", "minecraft:iron_ore", "minecraft:coal_ore" });
    c.save();
    //TODO: CONFIG:
    // DURABILITY: COOLDOWN< RANGE
    MinecraftForge.EVENT_BUS.register(this);
    register(low, "prospector_" + lowest.type.name().toLowerCase());
    register(low, "prospector_" + low.type.name().toLowerCase());
    register(low, "prospector_" + med.type.name().toLowerCase());
    register(low, "prospector_" + high.type.name().toLowerCase());
    register(low, "prospector_" + best.type.name().toLowerCase());
  }
  @EventHandler
  public void init(FMLInitializationEvent event) {}
  private void register(ItemProspector item, String key) {
    item.setUnlocalizedName(key);
    item.setCreativeTab(TAB);
    ResourceLocation rl = new ResourceLocation(MODID, key);
    item.setRegistryName(rl);
    items.add(item);
   
    //    IRecipe recipe = new ShapedOreRecipe(rl,
//        new ItemStack(item),
//        " sg",
//        " bs",
//        "b  ",
//        'b', new ItemStack(Items.BLAZE_ROD),
//        's', "gemDiamond",
//        'g', "blockGlassLightBlue");
//    recipe.setRegistryName(rl);
    addRecipe( item.addRecipe(rl));
  }
  private void addRecipe(IRecipe r) {
    recipes.add(r);
  }
  @SubscribeEvent
  public void onRegisterItems(RegistryEvent.Register<Item> event) {
    event.getRegistry().registerAll(items.toArray(new Item[0]));
  }
  @SubscribeEvent
  public void onRegisterRecipe(RegistryEvent.Register<IRecipe> event) {
    event.getRegistry().registerAll(recipes.toArray(new IRecipe[0]));
  }
}
