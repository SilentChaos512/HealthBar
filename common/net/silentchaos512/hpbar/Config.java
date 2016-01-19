package net.silentchaos512.hpbar;

import java.io.File;
import java.util.IllegalFormatException;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public class Config {

  public static String healthStringFormat = "%.1f / %.1f";
  public static float textScale = 0.8f;
  public static float xOffset = 0.5f;
  public static float yOffset = 0.75f;
  public static int barWidth = 64;
  public static int barHeight = 8;
  public static float barScale = 1.0f;
  public static boolean barShowAlways = false;
  public static boolean replaceVanillaHealth = false;
  public static float barOpacity = 0.6f;
  public static float barQuiverFraction = 0.25f;
  public static float barQuiverIntensity = 1.0f;
  public static String barJustification = "CENTER";
  public static int checkinFrequency = 300;

  private static Configuration c;
  public static File configFile;

  private static final String sep = Configuration.CATEGORY_SPLITTER;
  public static final String CAT_BAR = "health_bar";
  public static final String CAT_BAR_POSITION = CAT_BAR + sep + "position";
  public static final String CAT_BAR_RENDER = CAT_BAR + sep + "render";
  public static final String CAT_BAR_SIZE = CAT_BAR + sep + "size";
  public static final String CAT_NETWORK = "network";

  public static void init(File file) {

    configFile = file;
    c = new Configuration(file);
    load();
  }

  public static void load() {

    //@formatter:off

    healthStringFormat = loadFormatString();
    textScale = c.getFloat(
        "TextScale", CAT_BAR_RENDER,
        textScale, 0f, Float.MAX_VALUE,
        "The scale of the text displaying the player's health above the bar.\n"
            + "Set to 0 to disable text.");

    xOffset = c.getFloat(
        "XOffset", CAT_BAR_POSITION,
        xOffset, 0f, 1f,
        "How far across the screen the health bar renders.");
    yOffset = c.getFloat(
        "YOffset", CAT_BAR_POSITION,
        yOffset, 0f, 1f,
        "How far down the screen the health bar renders.");

    barWidth = c.getInt(
        "Width", CAT_BAR_SIZE,
        barWidth, 0, Integer.MAX_VALUE,
        "The width of the health bar.");
    barHeight = c.getInt(
        "Height", CAT_BAR_SIZE,
        barHeight, 0, Integer.MAX_VALUE,
        "The height of the health bar.");
    barScale = c.getFloat(
        "BarScale", CAT_BAR_RENDER,
        barScale, 0f, Float.MAX_VALUE,
        "The scale of the health bar.\nSet to 0 to disable the health bar.");

    barShowAlways = c.getBoolean(
        "ShowAlways", CAT_BAR_RENDER,
        barShowAlways,
        "Always show the health bar, even at full health.");
    barOpacity = c.getFloat(
        "Opacity", CAT_BAR_RENDER,
        barOpacity, 0f, 1f,
        "The opacity of the health bar.");
    replaceVanillaHealth = c.getBoolean(
        "ReplaceVanillaHealth", CAT_BAR_RENDER,
        replaceVanillaHealth,
        "Hides vanilla hearts and places the bar in their place. Ignores some configs if true.");
    barJustification = c.getString(
        "Justification", CAT_BAR_RENDER,
        barJustification,
        "Where the health bar is rendered in the frame. CENTER means there will be equal amounts of "
            + "empty space to the left and right. LEFT and RIGHT mean all empty space is to the right "
            + "or left, respectively.",
        new String[] { "CENTER", "LEFT", "RIGHT" });

    barQuiverFraction = c.getFloat(
        "QuiverFraction", CAT_BAR_RENDER,
        barQuiverFraction, 0f, 1f,
        "The fraction of health remaining when the bar begins to quiver/shake. Set to 0 to disable.");
    barQuiverIntensity = c.getFloat(
        "QuiverIntensity", CAT_BAR_RENDER,
        barQuiverIntensity, 0f, Float.MAX_VALUE,
        "How much the bar shakes when low on health. Intensity also increases with lower health.");

    checkinFrequency = c.getInt(
        "CheckInFrequency", CAT_NETWORK,
        checkinFrequency, 0, Integer.MAX_VALUE,
        "Even if the player's health has not changed, an update packet will be sent"
            + " after this many ticks. Set to 0 to disable (not recommended, unless you're very"
            + " bandwidth conscious).");

  //@formatter:off
  }

  private static String loadFormatString() {

    String str = c.getString("HealthStringFormat", "health_bar.text", healthStringFormat,
        "The format string the player's current and maximum health are passed through.\n"
            + "To show only the integer part of your health, try '%.0f / %.0f'.\n"
            + "Note there needs to be exactly two format codes, or the mod will use the default!"
            + "https://docs.oracle.com/javase/tutorial/java/data/numberformat.html");
    try {
      // Try the loaded string to make sure it works.
      String.format(str, 1f, 1f);
      return str;
    } catch (IllegalFormatException ex) {
      // TODO: Warning message!
      System.out.println("Health Bar mod failed to load the format string!");
      return healthStringFormat;
    }
  }

  public static void save() {

    if (c.hasChanged()) {
      c.save();
    }
  }

  public static ConfigCategory getCategory(String str) {

    return c.getCategory(str);
  }

  public static Configuration getConfiguration() {

    return c;
  }
}
