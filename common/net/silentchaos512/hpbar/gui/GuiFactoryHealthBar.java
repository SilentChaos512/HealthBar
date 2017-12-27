package net.silentchaos512.hpbar.gui;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

public abstract class GuiFactoryHealthBar implements IModGuiFactory {

  @Override
  public void initialize(Minecraft minecraftInstance) {

  }

  @Override
  public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {

    return new HashSet<RuntimeOptionCategoryElement>();
  }

  @Override
  public boolean hasConfigGui() {

    return true;
  }

  @Override
  public GuiScreen createConfigGui(GuiScreen parentScreen) {

    return new GuiConfigHealthBar(parentScreen);
  }
}
