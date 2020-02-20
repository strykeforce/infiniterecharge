package frc.robot.subsystems;

import com.squareup.moshi.Moshi;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import java.io.File;
import java.io.FileInputStream;
import java.util.Set;
import java.util.function.DoubleSupplier;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.deadeye.*;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.Measurable;
import org.strykeforce.thirdcoast.telemetry.item.Measure;

public class VisionSubsystem extends SubsystemBase implements Measurable {
  private static final String RANGE = "RANGE";
  private static final String BEARING = "BEARING";
  private static final String X_OFFSET = "X_OFFSET";

  public static double VERTICAL_FOV;
  public static double HORIZ_FOV;
  public static double HORIZ_RES;
  public static double TARGET_WIDTH_IN;
  public static double CAMERA_HEIGHT;
  public static double TARGET_HEIGHT;

  private static Deadeye deadeye;
  private static DriveSubsystem drive;
  private static ShooterSubsystem shooter;
  private static Camera<MinAreaRectTargetData> shooterCamera;
  private static MinAreaRectTargetData targetData;

  private static String path = "table.xlsx"; // FIXME
  private static FileInputStream tableFile;
  private static double[][] lookupTable;

  private static boolean trackingEnabled;
  private static double initOffset = 0;
  private static int visionStableCounts;

  public static String kCameraID;
  public static double kTurretDeadband;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public VisionSubsystem() {
    VERTICAL_FOV = Constants.VisionConstants.VERTICAL_FOV;
    HORIZ_FOV = Constants.VisionConstants.HORIZ_FOV;
    HORIZ_RES = Constants.VisionConstants.HORIZ_RES;
    CAMERA_HEIGHT = Constants.VisionConstants.CAMERA_HEIGHT;
    TARGET_WIDTH_IN = Constants.VisionConstants.TARGET_WIDTH_IN;
    TARGET_HEIGHT = Constants.VisionConstants.TARGET_HEIGHT;
    kCameraID = Constants.VisionConstants.kCameraID;
    kTurretDeadband = Constants.VisionConstants.kTurretDeadband;

    deadeye = RobotContainer.DEADEYE;
    shooter = RobotContainer.SHOOTER;
    drive = RobotContainer.DRIVE;

    shooterCamera = deadeye.getCamera(kCameraID);

    if (shooterCamera.getEnabled()) shooterCamera.setEnabled(false);

    configureProcess();

    TelemetryService telService = RobotContainer.TELEMETRY;
    telService.stop();
    telService.register(this);
    telService.start();
    shooterCamera.setEnabled(true);
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
    MinAreaRectTargetData targetData = getTargetData();
    return targetData.getCenterOffsetX();
  }

  public double getDistance() {
    MinAreaRectTargetData targetData = getTargetData();
    double enclosedAngle = HORIZ_FOV * targetData.getWidth() / HORIZ_RES;
    return TARGET_WIDTH_IN / 2 / Math.tan(Math.toRadians(enclosedAngle / 2));

    // angle from field square to center of target
    // double fieldOrientedOffset = 90 - (Math.IEEEremainder(drive.getGyro().getAngle(), 360) +
    // (270 - shooter.getTurretAngle()) + getOffsetAngle());

    // angle from field square to edge of target
    // double gamma = fieldOrientedOffset + enclosedAngle / 2;
    // return TARGET_WIDTH_IN / 2 * (Math.sin(Math.toRadians(gamma)) /
    // Math.tan(Math.toRadians(enclosedAngle / 2)) + Math.cos(Math.toRadians(gamma)));
  }

  public void setCameraEnabled(boolean enabled) {
    shooterCamera.setEnabled(enabled);
  }

  public boolean isStable() {
    double currentOffset = getPixOffset();
    if (Math.abs(initOffset - currentOffset) > Constants.VisionConstants.kStableRange) {
      visionStableCounts = 0;
      initOffset = currentOffset;
    } else {
      visionStableCounts++;
    }
    if (visionStableCounts >= Constants.VisionConstants.kStableCounts) {
      visionStableCounts = 0;
      return true;
    } else {
      return false;
    }
  }

  private boolean readTable() {
    int rowNum = -1, columnNum = -1;
    boolean didRead;

    try {
      tableFile = new FileInputStream(new File(path));
      XSSFWorkbook workbook = new XSSFWorkbook(tableFile);
      XSSFSheet sheet = workbook.getSheetAt(0);

      lookupTable = new double[sheet.getLastRowNum()][sheet.getRow(0).getLastCellNum()];

      for (int i = 0; i < sheet.getLastRowNum(); i++) {
        Row row = sheet.getRow(i);
        rowNum = i;
        for (int j = 0; j < row.getLastCellNum(); j++) {
          Cell cell = row.getCell(j);
          columnNum = j;
          lookupTable[i][j] = cell.getNumericCellValue();
        }
      }

      tableFile.close();
      didRead = true;
    } catch (Exception exception) {
      if (rowNum == -1) logger.error("Error reading vision file");
      else logger.error("Error reading cell ({}, {})", columnNum, rowNum);
      exception.printStackTrace(System.out);
      didRead = false;
    }

    return didRead;
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
        new Measure(BEARING, "Bearing"),
        new Measure(X_OFFSET, "Pixel offset"));
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
      case BEARING:
        return this::getOffsetAngle;
      case X_OFFSET:
        return this::getPixOffset;
      default:
        return () -> 2767;
    }
  }
}
