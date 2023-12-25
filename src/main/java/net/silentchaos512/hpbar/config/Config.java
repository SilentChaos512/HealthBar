package net.silentchaos512.hpbar.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class Config {
    public static final class Client {
        static final ForgeConfigSpec spec;

        static {
            ForgeConfigSpec.Builder builder =new ForgeConfigSpec.Builder();

            spec = builder.build();
        }
    }

    public static String healthStringFormat = "%.1f / %.1f";
    public static String damageStringFormat = "(%.3f)";
    public static boolean showLastDamageTaken = true;
    public static float textScale = 0.8f;
    public static float xOffset = 0.5f;
    public static float yOffset = 0.75f;
    public static int barWidth = 96;
    public static int barHeight = 16;
    public static float barScale = 1.0f;
    public static boolean barShowAlways = true;
    public static boolean replaceVanillaHealth = false;
    public static float barOpacity = 0.6f;
    public static float barQuiverFraction = 0.25f;
    public static float barQuiverIntensity = 1.0f;
    public static String barJustification = "CENTER";
    public static int checkinFrequency = 300;
    public static Color colorHealthBar = new Color(1f, 0f, 0f);

    private static final String sep = ".";
    public static final String CAT_BAR = "health_bar";
    public static final String CAT_BAR_POSITION = CAT_BAR + sep + "position";
    public static final String CAT_BAR_RENDER = CAT_BAR + sep + "render";
    public static final String CAT_BAR_SIZE = CAT_BAR + sep + "size";
    public static final String CAT_BAR_TEXT = CAT_BAR + sep + "text";
    public static final String CAT_NETWORK = "network";

    public static void load() {
        /*healthStringFormat = loadFormatString();
        damageStringFormat = c.getString(
                "DamageStringFormat", CAT_BAR_TEXT,
                damageStringFormat,
                "Format string for last damage taken.");
        showLastDamageTaken = c.getBoolean(
                "ShowLastDamageTaken", CAT_BAR_TEXT,
                showLastDamageTaken,
                "Shows the last amount of damage the player took.");
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
                new String[]{"CENTER", "LEFT", "RIGHT"});

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
                        + " bandwidth conscious).");*/
    }

    private static String loadFormatString() {
        /*String str = c.getString("HealthStringFormat", CAT_BAR_TEXT, healthStringFormat,
                "The format string the player's current and maximum health are passed through.\n"
                        + "To show only the integer part of your health, try '%.0f / %.0f'.\n"
                        + "You need not use two format codes. Using one will show your current health only.\n"
                        + "https://docs.oracle.com/javase/tutorial/java/data/numberformat.html");
        try {
            // Try the loaded string to make sure it works.
            String.format(str, 1f, 1f);
            return str;
        } catch (IllegalFormatException ex) {
            // TODO: Warning message!
            System.out.println("Health Bar mod failed to load the format string!");
            return healthStringFormat;
        }*/
        return healthStringFormat;
    }

    public static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Client.spec);
    }
}
