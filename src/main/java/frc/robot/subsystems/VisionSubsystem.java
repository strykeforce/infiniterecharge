package frc.robot.subsystems;

import com.opencsv.CSVReader;
import com.squareup.moshi.Moshi;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Set;
import java.util.function.DoubleSupplier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.deadeye.*;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.Measurable;
import org.strykeforce.thirdcoast.telemetry.item.Measure;

public class VisionSubsystem extends SubsystemBase implements Measurable {
  private static final String RANGE = "RANGE";
  private static final String GROUND_RANGE = "GROUND_RANGE";
  private static final String BEARING = "BEARING";
  private static final String X_OFFSET = "X_OFFSET";
  private static final String RAW_WIDTH = "RAW_WIDTH";
  private static final String CORRECTED_WIDTH = "CORRECTED_WIDTH";

  public static double VERTICAL_FOV;
  public static double HORIZ_FOV;
  public static double HORIZ_RES;
  public static double TARGET_WIDTH_IN;
  public static double CAMERA_HEIGHT;
  public static double TARGET_HEIGHT;

  private static Deadeye deadeye;
  private static DriveSubsystem drive;
  private static TurretSubsystem turret;
  private static Camera<MinAreaRectTargetData> shooterCamera;
  private static MinAreaRectTargetData targetData;

  private static String[][] lookupTable;

  private static boolean trackingEnabled;
  private static double initOffset = 0;
  private static int visionStableCounts;

  public static String kCameraID;
  public static String kTablePath;
  public static int kStableRange;
  public static int kStableCounts;
  public static int kTableMin;
  public static int kTableMax;
  public static int kTableRes;
  public static int kHoodIndex;
  public static int kShooterIndex;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public VisionSubsystem() {
    VERTICAL_FOV = Constants.VisionConstants.VERTICAL_FOV;
    HORIZ_FOV = Constants.VisionConstants.HORIZ_FOV;
    HORIZ_RES = Constants.VisionConstants.HORIZ_RES;
    CAMERA_HEIGHT = Constants.VisionConstants.CAMERA_HEIGHT;
    TARGET_WIDTH_IN = Constants.VisionConstants.TARGET_WIDTH_IN;
    TARGET_HEIGHT = Constants.VisionConstants.TARGET_HEIGHT;
    kCameraID = Constants.VisionConstants.kCameraID;
    kTablePath = Constants.VisionConstants.kTablePath;
    kStableRange = Constants.VisionConstants.kStableRange;
    kStableCounts = Constants.VisionConstants.kStableCounts;
    kTableMin = Constants.VisionConstants.kTableMin;
    kTableMax = Constants.VisionConstants.kTableMax;
    kTableRes = Constants.VisionConstants.kTableRes;
    kHoodIndex = Constants.VisionConstants.kHoodIndex;
    kShooterIndex = Constants.VisionConstants.kShooterIndex;

    deadeye = RobotContainer.DEADEYE;
    turret = RobotContainer.TURRET;
    drive = RobotContainer.DRIVE;

    shooterCamera = deadeye.getCamera(kCameraID);

    shooterCamera.setLightEnabled(false);

    configureProcess();

    TelemetryService telService = RobotContainer.TELEMETRY;
    telService.stop();
    telService.register(this);
    telService.start();
    shooterCamera.setEnabled(true);

    try {
      readTable();
    } catch (Exception exception) {
      logger.error("Could not read table at {}", kTablePath);
    }
  }

  public void configureProcess() {
    shooterCamera.setJsonAdapter(new MinAreaRectTargetDataJsonAdapter(new Moshi.Builder().build()));
    shooterCamera.getId();
    shooterCamera.setTargetDataListener(
        new TargetDataListener() {
          @Override
          public void onTargetData(TargetData arg0) {
            targetData = (MinAreaRectTargetData) arg0;
          }
        });
  }

  public synchronized MinAreaRectTargetData getTargetData() {
    return targetData;
  }

  public void setTrackingEnabled(boolean isEnabled) {
    trackingEnabled = isEnabled;
  }

  public boolean isTrackingEnabled() {
    return trackingEnabled;
  }

  public double getOffsetAngle() {
    return HORIZ_FOV * getPixOffset() / HORIZ_RES;
  }

  public double getElevationAngle() {
    return Math.toDegrees(Math.atan((TARGET_HEIGHT - CAMERA_HEIGHT) / getDistance()));
  }

  public double getPixOffset() {
    return getTargetData().getCenterOffsetX();
  }

  public double getDistance() {
    double enclosedAngle =
        HORIZ_FOV * Math.max(getTargetData().getWidth(), getTargetData().getHeight()) / HORIZ_RES;
    if (getTargetData().getValid())
      return TARGET_WIDTH_IN / 2 / Math.tan(Math.toRadians(enclosedAngle / 2));
    return -1;
  }

  public double getGroundDistance() {
    if (getTargetData().getValid())
      return Math.sqrt(Math.pow(getDistance(), 2) - Math.pow(TARGET_HEIGHT - CAMERA_HEIGHT, 2));
    else return -1;
  }

  public double getRawWidth() {
    if (getTargetData().getValid()) {
      return Math.max(getTargetData().getHeight(), getTargetData().getWidth());
    } else return -1;
  }

  public double getCorrectedWidth() {
    double fieldOrientedOffset =
        90
            - (Math.IEEEremainder(drive.getGyro().getAngle(), 360)
                + (270 - turret.getTurretAngle())
                + getOffsetAngle());
    if (getTargetData().getValid()) {
      return getRawWidth() / Math.sin(Math.toRadians(fieldOrientedOffset));
    } else return -1;
  }

  public boolean isTargetValid() {
    return getTargetData().getValid();
  }

  public void setLightsEnabled(boolean enabled) {
    shooterCamera.setLightEnabled(enabled);
  }

  public void setCameraEnabled(boolean enabled) {
    shooterCamera.setEnabled(enabled);
  }

  public boolean isStable() {
    double currentOffset = getPixOffset();
    if (Math.abs(initOffset - currentOffset) > kStableRange) {
      visionStableCounts = 0;
      initOffset = currentOffset;
    } else {
      visionStableCounts++;
    }
    if (visionStableCounts >= kStableCounts) {
      visionStableCounts = 0;
      return true;
    } else {
      return false;
    }
  }

  public int getBestTableIndex() {
    double distance = getGroundDistance();
    if (distance > kTableMax) {
      logger.warn(
          "Error retrieving table entry for {}, using MAX distance in table {}",
          distance,
          kTableMax);
      return lookupTable.length;
    }
    if (distance < kTableMin) {
      logger.warn(
          "Error retrieving table entry for {}, using MIN distance in table {}",
          distance,
          kTableMin);
      return 1;
    } else {
      int index = (int) (Math.round(distance / kTableRes)) + 1 - kTableMin;
      logger.info("Selected table row = {}", index);
      return index;
    }
  }

  public double getHoodSetpoint(int lookupIndex) {
    logger.info("Hood setpoint = {}", lookupTable[lookupIndex][kHoodIndex]);
    return Double.parseDouble(lookupTable[lookupIndex][kHoodIndex]);
  }

  public double getShooterSetpoint(int lookupIndex) {
    logger.info("Shooter setpoint = {}", lookupTable[lookupIndex][kShooterIndex]);
    return Double.parseDouble(lookupTable[lookupIndex][kShooterIndex]);
  }

  private void readTable() throws Exception {
    CSVReader csvReader = new CSVReader(new FileReader(new File(kTablePath)));

    List<String[]> list = csvReader.readAll();

    // Convert to 2D array
    String[][] strArr = new String[list.size()][];
    lookupTable = list.toArray(strArr);

    logger.info("First cell = {}" + lookupTable[1][0]);
    logger.info("Second cell = {}" + lookupTable[2][0]);
  }

  @NotNull
  @Override
  public String getDescription() {
    return "Vision Subsystem";
  }

  @Override
  public int getDeviceId() {
    return 0;
  }

  @NotNull
  @Override
  public Set<Measure> getMeasures() {
    return Set.of(
        new Measure(RANGE, "Raw Range"),
        new Measure(GROUND_RANGE, "Ground Range"),
        new Measure(BEARING, "Bearing"),
        new Measure(X_OFFSET, "Pixel offset"),
        new Measure(RAW_WIDTH, "Raw Pixel Width"),
        new Measure(CORRECTED_WIDTH, "Corrected Pixel Width"));
  }

  @NotNull
  @Override
  public String getType() {
    return "vision";
  }

  @Override
  public int compareTo(@NotNull Measurable item) {
    return 0;
  }

  @NotNull
  @Override
  public DoubleSupplier measurementFor(@NotNull Measure measure) {
    switch (measure.getName()) {
      case RANGE:
        return this::getDistance;
      case GROUND_RANGE:
        return this::getGroundDistance;
      case BEARING:
        return this::getOffsetAngle;
      case X_OFFSET:
        return this::getPixOffset;
      case RAW_WIDTH:
        return this::getRawWidth;
      case CORRECTED_WIDTH:
        return this::getCorrectedWidth;
      default:
        return () -> 2767;
    }
  }
}
