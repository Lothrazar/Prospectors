package com.lothrazar.prospectors;
import java.util.*;
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
  public int cooldown;
  public int range;
  public boolean isBlacklist;
  private Set<String> blockSet;
  public Types type;
  public ItemProspector(Types t) {
    super();
    this.setMaxStackSize(1);
    this.type = t;
  }
  @Override
  public EnumActionResult onItemUse(EntityPlayer player, World worldObj, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
    ItemStack stack = player.getHeldItem(hand);
    Map<String, Integer> mapList = new HashMap<String, Integer>();
    String name;
    EnumFacing direction = side.getOpposite();
    BlockPos current;
    IBlockState state;
    Block blockAt;
    ItemStack s;
    for (int i = 0; i < range; i++) {
      current = pos.offset(direction, i);
      state = worldObj.getBlockState(current);
      if (state == Blocks.AIR.getDefaultState() || !isBlockShowable(state)) {
        continue;
      }
      blockAt = state.getBlock();
      s = new ItemStack(Item.getItemFromBlock(blockAt), 1, blockAt.getMetaFromState(state));
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
    this.onSuccess(player, stack, hand);
    return super.onItemUse(player, worldObj, pos, hand, side, hitX, hitY, hitZ);
  }
  private void onSuccess(EntityPlayer p, ItemStack s, EnumHand hand) {
    s.damageItem(1, p);
    p.swingArm(hand);
    if (this.cooldown > 0) {
      p.getCooldownTracker().setCooldown(s.getItem(), this.cooldown);
    }
  }
  private static String lang(String string) {
    //if we use the clientside one, it literally does not work & crashes on serverside run
    return I18n.translateToLocal(string);
  }
  private static void addChatMessage(EntityPlayer player, String text) {
    player.sendMessage(new TextComponentTranslation(lang(text)));
  }
  private boolean isBlockShowable(IBlockState state) {
    String simpleName = state.getBlock().getRegistryName().toString();
    String nameWithMeta = simpleName + "/" + state.getBlock().getMetaFromState(state);
    return isBlacklist ^ (blockSet.contains(simpleName) || blockSet.contains(nameWithMeta));
  }
  public void syncConfig(Configuration c, String[] deflist) {
    String category = "prospector_" + this.type.name().toLowerCase();
    this.range = c.getInt("range", category, 32, 1, 256, "Search range");
    this.cooldown = c.getInt("cooldown", category, 10, 0, 256, "Time delay per use (ticks); zero to disable");
    this.setMaxDamage(c.getInt("durability", category, 200, 1, 65536, "Durability: number of uses"));
    this.isBlacklist = c.getBoolean("IsBlacklist", category, false, "True means this is a blacklist: ignore whats listed. False means its a whitelist: only print whats listed.");
    String[] blockList = c.getStringList("ProspectorBlockList", category, deflist, "List of blocks that the Prospector knows about.");
    this.blockSet = new HashSet<>(Arrays.asList(blockList));
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
            'b', "cobblestone",
            's', "stickWood",
            'g', "logWood");
      break;
      case LOW:
        recipe = new ShapedOreRecipe(rl,
            new ItemStack(this),
            " sg",
            " bs",
            "b  ",
            'b', "logWood",
            's', Items.COAL,
            'g', "blockGlassColorless");
      break;
      case MED:
        recipe = new ShapedOreRecipe(rl,
            new ItemStack(this),
            " sg",
            " bs",
            "b  ",
            'b', "gemLapis",
            's', "dustRedstone",
            'g', "blockGlassColorless");
      break;
      case HIGH:
        recipe = new ShapedOreRecipe(rl,
            new ItemStack(this),
            " sg",
            " bs",
            "b  ",
            'b', "gemEmerald",
            's', "dustRedstone",
            'g',  "blockGlassColorless");
      break;
      case BEST:
        recipe = new ShapedOreRecipe(rl,
            new ItemStack(this),
            " sg",
            " bs",
            "b  ",
            'b', Items.BLAZE_ROD,
            's', "gemDiamond",
            'g',  "blockGlassColorless");
      break;
    }
    recipe.setRegistryName(rl);
    return recipe;
  }
}
