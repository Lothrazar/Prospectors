package com.lothrazar.prospectors;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;

public class IngameConfigGui extends GuiConfig {
  public IngameConfigGui(GuiScreen parent) {
    super(parent, new ConfigElement(Prospectors.config.getCategory(Prospectors.MODID)).getChildElements(), Prospectors.MODID,
        false, false, "Ore Prospectors");
  }
  @Override
  public void initGui() {
    super.initGui();
  }
  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    super.drawScreen(mouseX, mouseY, partialTicks);
  }
  @Override
  protected void actionPerformed(GuiButton button) {
    super.actionPerformed(button);
  }
}
