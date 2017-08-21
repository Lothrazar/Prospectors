package com.lothrazar.prospectors;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = Prospectors.MODID, updateJSON = "https://raw.githubusercontent.com/PrinceOfAmber/Prospectors/master/update.json", guiFactory = "com.lothrazar." + Prospectors.MODID + ".IngameConfigFactory")
public class Prospectors {
  @Instance(value = Prospectors.MODID)
  public static Prospectors instance;
  @SidedProxy(clientSide = "com.lothrazar." + Prospectors.MODID + ".ClientProxy", serverSide = "com.lothrazar." + Prospectors.MODID + ".CommonProxy")
  public static CommonProxy proxy;
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
  public final static ItemProspector lowest = new ItemProspector(Types.LOWEST);
  public final static ItemProspector low = new ItemProspector(Types.LOW);
  public final static ItemProspector med = new ItemProspector(Types.MED);
  public final static ItemProspector high = new ItemProspector(Types.HIGH);
  public final static ItemProspector best = new ItemProspector(Types.BEST);
  static Configuration config;
  @EventHandler
  public void onPreInit(FMLPreInitializationEvent event) {
    config = new Configuration(event.getSuggestedConfigurationFile());
    config.load();
    syncAllConfig();
    register(lowest, "prospector_" + lowest.type.name().toLowerCase());
    register(low, "prospector_" + low.type.name().toLowerCase());
    register(med, "prospector_" + med.type.name().toLowerCase());
    register(high, "prospector_" + high.type.name().toLowerCase());
    register(best, "prospector_" + best.type.name().toLowerCase());
    MinecraftForge.EVENT_BUS.register(this);
  }
  private void register(ItemProspector item, String key) {
    item.setUnlocalizedName(key);
    item.setCreativeTab(TAB);
    ResourceLocation rl = new ResourceLocation(MODID, key);
    item.setRegistryName(rl);
    items.add(item);
    addRecipe(item.addRecipe(rl));
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
  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void registerModels(ModelRegistryEvent event) {
    // with help from
    // http://www.minecraftforge.net/forum/index.php?topic=32492.0
    // https://github.com/TheOnlySilverClaw/Birdmod/blob/master/src/main/java/silverclaw/birds/client/ClientProxyBirds.java
    // More info on proxy rendering
    // http://www.minecraftforge.net/forum/index.php?topic=27684.0
    // http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/modification-development/2272349-lessons-from-my-first-mc-1-8-mod
    //    ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
    String name;
    for (Item item : items) {
      name = MODID + ":" + item.getUnlocalizedName().replaceAll("item.", "");
      ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(name, "inventory"));
    }
  }
  @SubscribeEvent
  public void onConfigChanged(OnConfigChangedEvent event) {
    if (event.getModID().equals(MODID)) {
      syncAllConfig();
    }
  }
  private void syncAllConfig() {
    lowest.syncConfig(config,
        new String[] { "minecraft:coal_ore", "minecraft:gravel", "minecraft:sand" });
    low.syncConfig(config,
        new String[] { "minecraft:iron_ore", "minecraft:coal_ore" });
    med.syncConfig(config,
        new String[] { "minecraft:gold_ore", "minecraft:iron_ore", "minecraft:coal_ore", "minecraft:lapis_ore" });
    high.syncConfig(config,
        new String[] { "minecraft:quartz_ore", "minecraft:iron_ore", "minecraft:gold_ore", "minecraft:iron_ore", "minecraft:coal_ore", "minecraft:lapis_ore", "minecraft:redstone_ore/0", "minecraft:redstone_ore/1" });
    best.syncConfig(config,
        new String[] { "minecraft:quartz_ore", "minecraft:diamond_ore", "minecraft:emerald_ore", "minecraft:iron_ore", "minecraft:gold_ore", "minecraft:iron_ore", "minecraft:coal_ore", "minecraft:lapis_ore", "minecraft:redstone_ore/0", "minecraft:redstone_ore/1" });
    if (config.hasChanged())
      config.save();
  }
}
