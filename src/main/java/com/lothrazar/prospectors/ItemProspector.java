package com.lothrazar.prospectors;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import com.lothrazar.prospectors.Prospectors.Types;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ItemProspector extends Item {
  public int COOLDOWN = 12;
  public int range;
  public boolean isBlacklist;
  public String[] blocklist;
  public Types type;
  public ItemProspector(Types t) {
    super();
    this.setMaxStackSize(1);
    this.type = t;
  }
  @Override
  public EnumActionResult onItemUse(EntityPlayer player, World worldObj, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
    ItemStack stack = player.getHeldItem(hand);
    if (side == null || pos == null) { return super.onItemUse(player, worldObj, pos, hand, side, hitX, hitY, hitZ); }
    Map<String, Integer> mapList = new HashMap<String, Integer>();
    String name;
    EnumFacing direction = side.getOpposite();
    BlockPos current;
    IBlockState at;
    Block blockAt;
    ItemStack s;
    for (int i = 0; i < range; i++) {
      current = pos.offset(direction, i);
      at = worldObj.getBlockState(current);
      if (at == null) {
        continue;
      }
      blockAt = at.getBlock();
      if (blockAt == null) {
        continue;
      }
      if (at == Blocks.AIR) {
        continue;
      }
      s = new ItemStack(Item.getItemFromBlock(blockAt), 1, blockAt.getMetaFromState(at));
      if (isBlockShowable(s) == false) {
        continue;
      }
      name = s.getDisplayName();
      int previous = (mapList.containsKey(name)) ? mapList.get(name) : 0;
      mapList.put(name, previous + 1);
    }
    //now send messages
    if (worldObj.isRemote) {
      if (mapList.size() == 0) {
        addChatMessage(player, lang("prospector.none") + range);
      }
      for (Map.Entry<String, Integer> entry : mapList.entrySet()) {
        addChatMessage(player, lang("prospector.found") + entry.getKey() + " " + entry.getValue());
      }
    }
    player.swingArm(hand);
    damageItem(player, stack);
    return super.onItemUse(player, worldObj, pos, hand, side, hitX, hitY, hitZ);
  }
  private void damageItem(EntityPlayer p, ItemStack s) {
    s.damageItem(1, p);
  }
  private static String lang(String string) {
    //if we use the clientside one, it literally does not work & crashes on serverside run
    return I18n.translateToLocal(string);
  }
  private static void addChatMessage(EntityPlayer player, String text) {
    player.sendMessage(new TextComponentTranslation(lang(text)));
  }
  private boolean isBlockShowable(ItemStack stack) {
    if (stack == null || stack.getItem() == null) { return false; } //nulls
    String itemName = getStringForItemStack(stack);//this one includes metadata
    String itemSimpleName = getStringForItem(stack.getItem());//this one doesnt
    boolean isInList = false;
    for (String s : blocklist) {//dont use .contains on the list. must use .equals on string
      if (s == null) {
        continue;
      } //lol
      //so if list has only "minecraft:stone" then all metadata is covered
      //otherwise, list might have "minecraft:stone/3" so it only matches that
      if (s.equals(itemName) || s.equals(itemSimpleName)) {
        isInList = true;
        break;
      }
    }
    //if its a blacklist, and its IN the list, DONT show it (false)
    //otherwise, its a whitelist, so if it IS in the list then show it (true)v
    boolean yesShowIt = (this.isBlacklist) ? (!isInList) : isInList;
    return yesShowIt;
  }
  private static @Nonnull String getStringForItemStack(ItemStack itemStack) {
    Item item = itemStack.getItem();
    return item.getRegistryName().getResourceDomain() + ":" + item.getRegistryName().getResourcePath() + "/" + itemStack.getMetadata();
  }
  private static String getStringForItem(Item item) {
    if (item == null || item.getRegistryName() == null) { return ""; }
    return item.getRegistryName().getResourceDomain() + ":" + item.getRegistryName().getResourcePath();
  }
  public void syncConfig(Configuration c, String[] deflist) {
    String category = this.type.name().toLowerCase();
    this.range = c.getInt("range", category, 1, 32, 256, "Search range");
    this.COOLDOWN = c.getInt("cooldown", category, 1, 32, 256, "Time delay per use");
    this.setMaxDamage(c.getInt("durability", category, 1, 32, 256, "Durability; number of uses"));
    this.isBlacklist = c.getBoolean("IsBlacklist", category, false, "True means this is a blacklist, ignore whats listed. False means its a whitelist: only print whats listed.");
    this.blocklist = c.getStringList("ProspectorBlockList", category, deflist, "List of blocks that the Prospector knows about.");
  }
  public IRecipe addRecipe(ResourceLocation rl) {
    IRecipe recipe = null;
    switch (this.type) {
      case LOWEST:
        recipe = new ShapedOreRecipe(rl,
            new ItemStack(this),
            " sg",
            " bs",
            "b  ",
            'b', Items.STICK,
            's', "gemDiamond",
            'g', "blockGlassLightBlue");
      break;
      case LOW:
        recipe = new ShapedOreRecipe(rl,
            new ItemStack(this),
            " sg",
            " bs",
            "b  ",
            'b', Prospectors.lowest,
            's', "gemDiamond",
            'g', "blockGlassLightBlue");
      break;
      case MED:
        recipe = new ShapedOreRecipe(rl,
            new ItemStack(this),
            " sg",
            " bs",
            "b  ",
            'b', Prospectors.low,
            's', "gemDiamond",
            'g', "blockGlassLightBlue");
      break;
      case HIGH:
        recipe = new ShapedOreRecipe(rl,
            new ItemStack(this),
            " sg",
            " bs",
            "b  ",
            'b', Prospectors.med,
            's', "gemDiamond",
            'g', "blockGlassLightBlue");
      break;
      case BEST:
        recipe = new ShapedOreRecipe(rl,
            new ItemStack(this),
            " sg",
            " bs",
            "b  ",
            'b', Prospectors.best,
            's', "gemDiamond",
            'g', "blockGlassLightBlue");
      break;
    }
    recipe.setRegistryName(rl);
    return recipe;
  }
}
