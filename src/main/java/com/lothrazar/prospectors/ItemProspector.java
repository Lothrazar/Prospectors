package com.lothrazar.prospectors;
import java.util.*;
import com.lothrazar.prospectors.Prospectors.Types;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
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
    if (worldObj.isRemote) {
      Map<IBlockState, Integer> mapList = new HashMap<>();
      Map<IBlockState, BlockPos> lastPositions = new HashMap<>();
      EnumFacing direction = side.getOpposite();
      for (int i = 0; i < range; i++) {
        BlockPos offsetPos = pos.offset(direction, i);
        IBlockState state = worldObj.getBlockState(offsetPos);
        if (state == Blocks.AIR.getDefaultState() || !isBlockShowable(state)) {
          continue;
        }
        int previous = mapList.getOrDefault(state, 0);
        mapList.put(state, previous + 1);
        lastPositions.put(state, offsetPos);
      }
      //now send messages
      ITextComponent message = null;
      if (mapList.size() == 0) {
        message = new TextComponentTranslation("prospector.none", range);
      } else {
        for (Map.Entry<IBlockState, Integer> entry : mapList.entrySet()) {
          IBlockState state = entry.getKey();
          BlockPos lastPosition = lastPositions.get(state);
          ItemStack pickBlock = state.getBlock().getPickBlock(state, null, worldObj, lastPosition, player);
          ITextComponent blockName = pickBlock.getTextComponent();
          if (message == null) {
            message = new TextComponentTranslation("prospector.found", entry.getValue(), blockName);
          } else {
            message.appendSibling(new TextComponentTranslation("prospector.found.and", entry.getValue(), blockName));
          }
        }
      }
      player.sendStatusMessage(message, true);
    }
    ItemStack stack = player.getHeldItem(hand);
    this.onSuccess(player, stack, hand);
    return EnumActionResult.SUCCESS;
  }
  private void onSuccess(EntityPlayer p, ItemStack s, EnumHand hand) {
    s.damageItem(1, p);
    p.swingArm(hand);
    if (this.cooldown > 0) {
      p.getCooldownTracker().setCooldown(s.getItem(), this.cooldown);
    }
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
