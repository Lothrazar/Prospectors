package com.lothrazar.prospectors;
import java.util.HashSet;
import java.util.Set;
import com.lothrazar.prospectors.Prospectors.Types;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

public class IngameConfigFactory implements IModGuiFactory {
  @Override
  public void initialize(Minecraft minecraftInstance) {}
  @Override
  public boolean hasConfigGui() {
    return false;//ffrick not working still idk
  }
  @Override
  public GuiScreen createConfigGui(GuiScreen parentScreen) {
    return new IngameConfigGui(parentScreen);
  }
  @Override
  public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
   return new HashSet<RuntimeOptionCategoryElement>();
    //    return new HashSet<RuntimeOptionCategoryElement>() {
//      {
//        add(new RuntimeOptionCategoryElement(null, "prospector_" + Types.LOWEST.name().toLowerCase()));
//        add(new RuntimeOptionCategoryElement(null, "prospector_" + Types.LOW.name().toLowerCase()));
//        add(new RuntimeOptionCategoryElement(null, "prospector_" + Types.MED.name().toLowerCase()));
//        add(new RuntimeOptionCategoryElement(null, "prospector_" + Types.HIGH.name().toLowerCase()));
//        add(new RuntimeOptionCategoryElement(null, "prospector_" + Types.BEST.name().toLowerCase()));
//      }
//    };
  }
}
