package frc.robot.subsystems;

import com.revrobotics.ColorMatch;
import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import java.util.List;

public class ColorSensorSubsystem extends SubsystemBase {

  private final I2C.Port i2cPort = I2C.Port.kOnboard;
  private final ColorSensorV3 colorSensor;
  private ColorMatch colorMatcher = new ColorMatch();
  public ColorValues RED;
  public ColorValues YELLOW;
  public ColorValues GREEN;
  public ColorValues BLUE;

  public static Color[] WHEEL_COLORS = new Color[4]; // Yellow, Red, Green, Blue

  public static String[][] WHEEL_POSITIONS = {
    {"c", "r", "r", "l"}, // Yellow; Order of strings goes to Target: Y, R, G, B
    {"l", "c", "r", "r"}, // Red
    {"r", "l", "c", "r"}, // Green
    {"r", "r", "l", "c"}   // Blue
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
    RED = new ColorValues(r, g, b, "Red");
    YELLOW = new ColorValues(r, g, b, "Yellow");
    GREEN = new ColorValues(r, g, b, "Green");
    BLUE = new ColorValues(r, g, b, "Blue");

  }

  public void CalColors(ColorValues ColorToCalibrate)
  {
    switch(ColorToCalibrate.ColorName)
    {
      case "Red":
        WritePreferences("ColorSensor/RED", ColorToCalibrate);

        break;

      case "Yellow":
        WritePreferences("ColorSensor/YELLOW", ColorToCalibrate);

        break;

      case "Green":
        WritePreferences("ColorSensor/GREEN", ColorToCalibrate);

        break;

      case "Blue":
        WritePreferences("ColorSensor/BLUE", ColorToCalibrate);


        break;

      default:
        System.out.println("Color does not exist. Check your spelling.");
        break;

    }
  }

  private void WritePreferences(String key, ColorValues color)
  {
    Preferences prefs = Preferences.getInstance();
    Color ResultColor = GetColor();
    prefs.putDouble(key + "r", ResultColor.red );
    prefs.putDouble(key + "g", ResultColor.green );
    prefs.putDouble(key + "b", ResultColor.blue );
    color.r = ResultColor.red;
    color.b = ResultColor.blue;
    color.g = ResultColor.green;

  }

  public Color GetColor()
  {
    return colorSensor.getColor();

  }

  public ColorSensorV3 getColorSensor() {
    return colorSensor;
  }

  public String getTargetColor() {
    return DriverStation.getInstance().getGameSpecificMessage();
  }

  public class ColorValues {

    public double r;
    public double g;
    public double b;
    public String ColorName;
    ColorValues(double r, double g, double b, String ColorName)
    {
      this.r = r;
      this.g = g;
      this.b = b;
    }
  }
}
