package frc.robot.subsystems;

import com.opencsv.CSVReader;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.deadeye.Deadeye;
import org.strykeforce.deadeye.MinAreaRectTargetData;
import org.strykeforce.telemetry.TelemetryService;
import org.strykeforce.telemetry.measurable.MeasurableSubsystem;
import org.strykeforce.telemetry.measurable.Measure;

public class VisionSubsystem extends MeasurableSubsystem {
  public static double VERTICAL_FOV;
  public static double HORIZ_FOV;
  public static double HORIZ_RES;
  public static double TARGET_WIDTH_IN;
  public static double CAMERA_HEIGHT;
  public static double TARGET_HEIGHT;
  public static String kCameraID;
  public static String kTablePath;
  public static int kStableRange;
  public static int kStableCounts;
  public static int kTableMin;
  public static int kTableMax;
  public static int kTableRes;
  public static int kHoodIndex;
  public static int kShooterIndex;
  private static Deadeye deadeye;
  private static DriveSubsystem drive;
  private static TurretSubsystem turret;
  private static DeadeyeA0 shooterCamera;
  private static DeadeyeA1 gamepieceCamera;
  private static MinAreaRectTargetData targetData;
  private static String[][] lookupTable;
  private static boolean trackingEnabled;
  private static double initOffset = 0;
  private static int visionStableCounts;
  private static int visionLostCounts;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public VisionSubsystem() {

    // FIXME: Check framerate
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

    targetData = new MinAreaRectTargetData();
    shooterCamera = new DeadeyeA0();
    gamepieceCamera = new DeadeyeA1();

    shooterCamera.setLightsEnabled(false);

    if (!RobotContainer.isEvent) {
      TelemetryService telService = RobotContainer.TELEMETRY;
      telService.stop();
      telService.register(this);
      telService.register(shooterCamera);
      telService.start();
      shooterCamera.setEnabled(true);
    }

    try {
      readTable();
    } catch (Exception exception) {
      logger.error("Could not read table at {}", kTablePath);
    }
  }

  public boolean isTrackingEnabled() {
    return trackingEnabled;
  }

  public void setTrackingEnabled(boolean isEnabled) {
    trackingEnabled = isEnabled;
  }

  public double getOffsetAngle() {
    if (shooterCamera.getValid()) {
      return HORIZ_FOV * getPixOffset() / HORIZ_RES;
    }
    return 2767;
  }

  public double getHorizAngleAdjustment() {
    return Constants.VisionConstants.kHorizAngleCorrection;
  }

  public int getHoodTicksAdjustment() {
    double distance = getGroundDistance();
    if (distance > 96 && distance <= 180) {
      return Constants.VisionConstants.kHoodInchesCorrectionR1
          * Constants.VisionConstants.kHoodTicksPerInchR1;
    }
    if (distance > 180 && distance <= 228) {
      return Constants.VisionConstants.kHoodInchesCorrectionR2
          * Constants.VisionConstants.kHoodTicksPerInchR2;
    }
    if (distance > 228 && distance <= 300) {
      return Constants.VisionConstants.kHoodInchesCorrectionR3
          * Constants.VisionConstants.kHoodTicksPerInchR3;
    }
    if (distance > 300) {
      return Constants.VisionConstants.kHoodInchesCorrectionR4
          * Constants.VisionConstants.kHoodTicksPerInchR4;
    }
    return 0;
  }

  public double getElevationAngle() {
    return Math.toDegrees(Math.atan((TARGET_HEIGHT - CAMERA_HEIGHT) / getDistance()));
  }

  public double getPixOffset() {
    if (shooterCamera.getValid()) {
      double center =
          (shooterCamera.getWidth() > shooterCamera.getHeight())
              ? (shooterCamera.getTopLeftX() + shooterCamera.getTopRightX()) / 2
              : (shooterCamera.getTopRightX() + shooterCamera.getBottomRightX()) / 2;
      return center - HORIZ_RES / 2;
    }
    return 2767;
  }

  public double getDistance() {
    double enclosedAngle =
        HORIZ_FOV * Math.max(shooterCamera.getWidth(), shooterCamera.getHeight()) / HORIZ_RES;
    if (shooterCamera.getValid()) {
      return TARGET_WIDTH_IN / 2 / Math.tan(Math.toRadians(enclosedAngle / 2));
    }
    return -1;
  }

  public double getGroundDistance() {
    if (shooterCamera.getValid()) {
      return Math.sqrt(Math.pow(getDistance(), 2) - Math.pow(TARGET_HEIGHT - CAMERA_HEIGHT, 2));
    } else {
      return -1;
    }
  }

  public double getRawWidth() {
    if (shooterCamera.getValid()) {
      return Math.max(shooterCamera.getHeight(), shooterCamera.getWidth());
    } else {
      return -1;
    }
  }

  public double getCorrectedWidth() {
    double fieldOrientedOffset =
        (Math.IEEEremainder(-drive.getHeading().getDegrees(), 360)
            + (270 - turret.getTurretAngle())
            + getOffsetAngle());
    if (shooterCamera.getValid()) {
      return getRawWidth() / Math.cos(Math.toRadians(fieldOrientedOffset));
    } else {
      return -1;
    }
  }

  public boolean isTargetValid() {
    //    double fieldOrientedOffset =
    //        (Math.IEEEremainder(drive.getGyro().getAngle(), 360)
    //            + (270 - turret.getTurretAngle())
    //            + getOffsetAngle());
    return shooterCamera.getValid(); // && Math.abs(fieldOrientedOffset) < 90
  }

  public void setLightsEnabled(boolean enabled) {
    shooterCamera.setLightsEnabled(enabled);
  }

  public void setCameraEnabled(boolean enabled) {
    logger.info("Setting camera enabled to {}", enabled);
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
      logger.info("Camera image stable, resetting stable counts");
      return true;
    } else {
      return false;
    }
  }

  public boolean lostCheck() {
    if (!isTargetValid()) {
      visionLostCounts++;
    } else {
      visionLostCounts = 0;
    }

    if (visionLostCounts > Constants.VisionConstants.kLostLimit) {
      logger.info("Target lost");
      return true;
    }
    return false;
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

  // GAMEPIECE CAMERA
  public void setGamepieceEnabled(boolean enabled) {
    gamepieceCamera.setEnabled(enabled);
  }

  public DeadeyeA1.Layout getLayout() {
    return gamepieceCamera.getLayout();
  }

  @NotNull
  @Override
  public Set<Measure> getMeasures() {
    return Set.of(
        new Measure("Raw Range", this::getDistance),
        new Measure("Ground Range", this::getGroundDistance),
        new Measure("Bearing", this::getOffsetAngle),
        new Measure("Pixel offset", this::getPixOffset),
        new Measure("Raw Pixel Width", this::getRawWidth),
        new Measure("Corrected Pixel Width", this::getCorrectedWidth));
  }
}
