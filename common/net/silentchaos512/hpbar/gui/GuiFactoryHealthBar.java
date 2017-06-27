package net.silentchaos512.hpbar.gui;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

public class GuiFactoryHealthBar implements IModGuiFactory {

  @Override
  public void initialize(Minecraft minecraftInstance) {

  }

  @Override
  public Class<? extends GuiScreen> mainConfigGuiClass() {

    return GuiConfigHealthBar.class;
  }

  @Override
  public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {

    return null;
  }

  @Override
  public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {

    return null;
  }

  @Override
  public boolean hasConfigGui() {

    return true;
  }

  @Override
  public GuiScreen createConfigGui(GuiScreen parentScreen) {

    try {
      return this.mainConfigGuiClass().getConstructor(GuiScreen.class).newInstance(this);
    } catch (Exception e) {
      throw new AbstractMethodError();
    }
  }
}
