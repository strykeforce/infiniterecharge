package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;
import com.squareup.moshi.Moshi;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.deadeye.*;

public class VisionSubsystem extends SubsystemBase {
  public static final double HORIZ_FOV = 48.8;
  public static final double HORIZ_RES = 960;
  public static final double TARGET_WIDTH_IN = 34.65;

  private static Deadeye deadeye;
  private static DriveSubsystem drive;
  private static ShooterSubsystem shooter;
  private static Camera<CenterTargetData> shooterCamera;
  private static CenterTargetData targetData;

  public static String kCameraID;

  //  private static Camera<TargetCenterData> shooterCam;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public VisionSubsystem() {
    kCameraID = Constants.VisionConstants.kCameraID;

    deadeye = RobotContainer.DEADEYE;
    shooter = RobotContainer.SHOOTER;
    drive = RobotContainer.DRIVE;

    shooterCamera = deadeye.getCamera(kCameraID);

    shooterCamera.setJsonAdapter(new CenterTargetDataJsonAdapter(new Moshi.Builder().build()));

    configureProcess(shooterCamera);
  }

  public void configureProcess(Camera<CenterTargetData> camera) {
    camera.setTargetDataListener(
        new TargetDataListener() {
          @Override
          public void onTargetData(TargetData arg0) {
            targetData = (CenterTargetData) arg0;

            double pixWidth = targetData.getBottomRightX() - targetData.getTopLeftX();
            double degOffset = HORIZ_FOV * targetData.getCenterOffsetX() / HORIZ_RES;
            double enclosedAngle = HORIZ_FOV * pixWidth / HORIZ_RES;

            double fieldOrientedOffset = shooter.getTurretAngle() + drive.getGyro().getYaw() + degOffset;
            double gamma = fieldOrientedOffset + enclosedAngle / 2;
            double distance = TARGET_WIDTH_IN / 2 * (Math.sin(Math.toRadians(gamma)) / Math.cos(Math.cos(enclosedAngle / 2)) + Math.cos(Math.toRadians(gamma)));
          }
        });
  }
}
