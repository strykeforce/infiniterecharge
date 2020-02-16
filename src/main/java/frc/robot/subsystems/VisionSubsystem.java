package frc.robot.subsystems;

import com.squareup.moshi.Moshi;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import java.io.File;
import java.io.FileInputStream;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.deadeye.*;

public class VisionSubsystem extends SubsystemBase {
  public static final double HORIZ_FOV = 48.8;
  public static final double HORIZ_RES = 720;
  public static final double TARGET_WIDTH_IN = 34.65;
  public static final double CAMERA_HEIGHT = 22;
  public static final double TARGET_HEIGHT = 98.25;

  private static Deadeye deadeye;
  private static DriveSubsystem drive;
  private static ShooterSubsystem shooter;
  private static Camera<MinAreaRectTargetData> shooterCamera;
  private static MinAreaRectTargetData targetData;

  private static String path = "table.xlsx"; // FIXME
  private static FileInputStream tableFile;
  private static double[][] lookupTable;

  private static double degreeOffset;
  private static double elevationAngle;
  private static boolean trackingEnabled;

  public static String kCameraID;

  //  private static Camera<TargetCenterData> shooterCam;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final DigitalOutput lightsOutput = new DigitalOutput(6); // FIXME

  public VisionSubsystem() {
    kCameraID = Constants.VisionConstants.kCameraID;

    deadeye = RobotContainer.DEADEYE;
    shooter = RobotContainer.SHOOTER;
    drive = RobotContainer.DRIVE;

    shooterCamera = deadeye.getCamera(kCameraID);
    shooterCamera.setEnabled(false);

    configureProcess();
    lightsOutput.setPWMRate(0);
    lightsOutput.enablePWM(100);
  }

  public void configureProcess() {
    shooterCamera.setJsonAdapter(new MinAreaRectTargetDataJsonAdapter(new Moshi.Builder().build()));

    shooterCamera.setTargetDataListener(
        new TargetDataListener() {
          @Override
          public void onTargetData(TargetData arg0) {
            targetData = (MinAreaRectTargetData) arg0;
            double pixWidth = targetData.getBottomRightX() - targetData.getTopLeftX();
            double pixHeight = targetData.getTopLeftY() - targetData.getBottomRightY();
            System.out.println("W: " + pixWidth);
            System.out.println("H: " + pixHeight);
            if (pixWidth > 50 && 1 < pixWidth / pixHeight && pixWidth / pixHeight < 10) {
              degreeOffset = HORIZ_FOV * targetData.getCenterOffsetX() / HORIZ_RES;
              double enclosedAngle = HORIZ_FOV * targetData.getWidth() / HORIZ_RES;

              // angle from field square to center of target
              double fieldOrientedOffset =
                  90
                      - (Math.IEEEremainder(drive.getGyro().getAngle(), 360)
                          + (270 - shooter.getTurretAngle())
                          + degreeOffset);
              // angle from field square to edge of target
              double gamma = fieldOrientedOffset + enclosedAngle / 2;
              //              double distance =
              //                  TARGET_WIDTH_IN
              //                      / 2
              //                      * (Math.sin(Math.toRadians(gamma))
              //                              / Math.tan(Math.toRadians(enclosedAngle / 2))
              //                          + Math.cos(Math.toRadians(gamma)));
              double distance = TARGET_WIDTH_IN / 2 / Math.tan(Math.toRadians(enclosedAngle / 2));
              elevationAngle =
                  Math.toDegrees(Math.atan((TARGET_HEIGHT - CAMERA_HEIGHT) / distance));

              //              logger.info("Target pixel width = {}", pixWidth);
              logger.info("Angle offset = {}", degreeOffset);
              logger.info("Target distance = {}", distance);
            } else {
              System.out.println("Invalid target");
              degreeOffset = 180;
            }
          }
        });
    setCameraEnabled(true);
  }

  public void setTrackingEnabled(boolean isEnabled) {
    trackingEnabled = isEnabled;
  }

  public boolean isTrackingEnabled() {
    return trackingEnabled;
  }

  public double getOffsetAngle() {
    return degreeOffset;
  }

  public double getElevationAngle() {
    return elevationAngle;
  }

  public void setCameraEnabled(boolean enabled) {
    // shooterCamera.setEnabled(enabled);
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
    ;
    return didRead;
  }
}
