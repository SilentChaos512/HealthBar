package net.silentchaos512.hpbar.config;

public class Color {

  public final float red;
  public final float green;
  public final float blue;

  public Color(String hexString) {

    if (hexString.length() != 6) {
      red = green = blue = 0;
    } else {
      red = (16 * hexDigitToInt(hexString.charAt(0)) + hexDigitToInt(hexString.charAt(1))) / 255f;
      green = (16 * hexDigitToInt(hexString.charAt(2)) + hexDigitToInt(hexString.charAt(3))) / 255f;
      blue = (16 * hexDigitToInt(hexString.charAt(4)) + hexDigitToInt(hexString.charAt(5))) / 255f;
    }
  }

  public Color(float red, float green, float blue) {

    this.red = red;
    this.green = green;
    this.blue = blue;
  }

  private int hexDigitToInt(char c) {

    c = Character.toLowerCase(c);
    if (c >= '0' && c <= '9') {
      return c - '0';
    } else if (c >= 'a' && c <= 'f') {
      return c - 'a';
    }
    return 0;
  }
}
