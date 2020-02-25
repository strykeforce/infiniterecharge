package frc.robot.subsystems;

import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ColorSensorSubsystem extends SubsystemBase {

  private final I2C.Port i2cPort = I2C.Port.kOnboard;
  private final ColorSensorV3 colorSensor;
  private ColorMatch colorMatcher = new ColorMatch();

  public ColorValues RED_CV;
  public ColorValues YELLOW_CV;
  public ColorValues GREEN_CV;
  public ColorValues BLUE_CV;

  public Color REDCAL = ColorMatch.makeColor(RED_CV.r, RED_CV.g, RED_CV.b);
  public Color YELLOWCAL = ColorMatch.makeColor(YELLOW_CV.r, YELLOW_CV.g, YELLOW_CV.b);
  public Color GREENCAL = ColorMatch.makeColor(GREEN_CV.r, GREEN_CV.g, GREEN_CV.b);
  public Color BLUECAL = ColorMatch.makeColor(BLUE_CV.r, BLUE_CV.g, BLUE_CV.b);

  public static final int CW_RIGHT = 1;
  public static final int CW_LEFT = -1;
  public static final int CW_STAY = 0;

  public enum ColorNames {
    YELLOW,
    RED,
    GREEN,
    BLUE,
    DEFAULT
  }

  public static Color[] WHEEL_COLORS = new Color[4]; // Yellow, Red, Green, Blue

  public static int[][] WHEEL_POSITIONS = {
    // Y          R         G          B
    {CW_STAY, CW_RIGHT, CW_RIGHT, CW_LEFT}, // Yellow; Order of strings goes to Target: Y, R, G, B
    {CW_LEFT, CW_STAY, CW_RIGHT, CW_RIGHT}, // Red
    {CW_RIGHT, CW_LEFT, CW_STAY, CW_RIGHT}, // Green
    {CW_RIGHT, CW_RIGHT, CW_LEFT, CW_STAY} // Blue
  };

  public ColorSensorSubsystem() {
    colorSensor = new ColorSensorV3(i2cPort);
  }

  private void getPreferences() {
    String KEY = "ColorSensor/";
    Preferences prefs = Preferences.getInstance();
    double r = prefs.getDouble(KEY + "RED/r", 2767);
    double b = prefs.getDouble(KEY + "BLUE/b", 2767);
    double g = prefs.getDouble(KEY + "GREEN/g", 2767);
    YELLOW_CV = new ColorValues(r, g, b, ColorNames.YELLOW, ColorNames.YELLOW.ordinal());
    RED_CV = new ColorValues(r, g, b, ColorNames.RED, ColorNames.RED.ordinal());
    GREEN_CV = new ColorValues(r, g, b, ColorNames.GREEN, ColorNames.GREEN.ordinal());
    BLUE_CV = new ColorValues(r, g, b, ColorNames.BLUE, ColorNames.BLUE.ordinal());
  }

  public void CalColors(ColorValues ColorToCalibrate) {
    switch (ColorToCalibrate.color) {
      case YELLOW:
        WritePreferences("ColorSensor/YELLOW", ColorToCalibrate);

        break;

      case RED:
        WritePreferences("ColorSensor/RED", ColorToCalibrate);

        break;

      case GREEN:
        WritePreferences("ColorSensor/GREEN", ColorToCalibrate);

        break;

      case BLUE:
        WritePreferences("ColorSensor/BLUE", ColorToCalibrate);

        break;

      default:
        System.out.println("Color does not exist. Check your spelling.");
        break;
    }
  }

  private void WritePreferences(String key, ColorValues color) {
    Preferences prefs = Preferences.getInstance();
    Color ResultColor = GetColor();
    prefs.putDouble(key + "r", ResultColor.red);
    prefs.putDouble(key + "g", ResultColor.green);
    prefs.putDouble(key + "b", ResultColor.blue);
    color.r = ResultColor.red;
    color.b = ResultColor.blue;
    color.g = ResultColor.green;
  }

  public Color GetColor() {
    return colorSensor.getColor();
  }

  public void initializeColors() {
    colorMatcher.addColorMatch(YELLOWCAL);
    colorMatcher.addColorMatch(REDCAL);
    colorMatcher.addColorMatch(BLUECAL);
    colorMatcher.addColorMatch(GREENCAL);
  }

  public ColorSensorV3 getColorSensor() {
    return colorSensor;
  }

  public ColorNames returnColor() {
    ColorNames color;
    Color currentColor = colorSensor.getColor();
    ColorMatchResult result = colorMatcher.matchClosestColor(currentColor);

    if (result.equals(YELLOWCAL)) {
      color = ColorNames.YELLOW;
    } else if (result.equals(REDCAL)) {
      color = ColorNames.RED;
    } else if (result.equals(GREENCAL)) {
      color = ColorNames.GREEN;
    } else if (result.equals(BLUECAL)) {
      color = ColorNames.BLUE;
    } else {
      color = ColorNames.DEFAULT;
    }

    return color;
  }

  public int wheelLogic(ColorNames desiredColor) {
    ColorNames currentColor = returnColor();
    int direction = WHEEL_POSITIONS[currentColor.ordinal()][desiredColor.ordinal()];
    return direction;
  }

  public String getTargetColor() {
    return DriverStation.getInstance().getGameSpecificMessage();
  }

  public void testMethod(int direction) {
    if (direction > 0) {
      System.out.println("Moving Right");
    }
    if (direction < 0) {
      System.out.println("Moving Left");
    }
    if (direction == 0) {
      System.out.println("Is There");
    }
  }

  public class ColorValues {

    public double r;
    public double g;
    public double b;
    public String colorName;
    public ColorNames color;
    public int colorID;

    ColorValues(double r, double g, double b, ColorNames color, int colorID) {
      this.r = r;
      this.g = g;
      this.b = b;
      this.colorID = colorID;

      colorName = color.toString();
      this.color = color;
    }
  }
}
