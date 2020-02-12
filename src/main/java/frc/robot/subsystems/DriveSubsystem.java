package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.swerve.SwerveDriveConfig;
import org.strykeforce.thirdcoast.swerve.Wheel;
import org.strykeforce.thirdcoast.talon.TalonFXItem;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.TalonSRXItem;

public class DriveSubsystem extends SubsystemBase {

  private static final double ROBOT_LENGTH = 25.5;
  private static final double ROBOT_WIDTH = 21.5;
  private static final double DRIVE_SETPOINT_MAX = 0.0;
  private static final Double XLOCK_FL_RATIO =
      Math.atan2(ROBOT_WIDTH, ROBOT_LENGTH) * 0.5 / Math.PI;
  private static final Double XLOCK_FL_TICKS = XLOCK_FL_RATIO * (4096 / 2);
  private static final Double XLOCK_FR_RATIO = -XLOCK_FL_RATIO;
  private static final Double XLOCK_FR_TICKS = XLOCK_FR_RATIO * (4096 / 2);
  private static boolean isXLocked = false;

  private final SwerveDrive swerve = configSwerve();
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public DriveSubsystem() {
    swerve.setFieldOriented(true);
    zeroAzimuths();
  }

  public void drive(double forward, double strafe, double yaw) {
    swerve.drive(forward, strafe, yaw);
  }

  public void zeroGyro() {
    AHRS gyro = swerve.getGyro();
    gyro.setAngleAdjustment(0);
    double adj = gyro.getAngle() % 360;
    gyro.setAngleAdjustment(-adj);
    logger.info("resetting gyro: ({})", adj);
  }

  public void zeroAzimuths() {
    swerve.zeroAzimuthEncoders();
  }

  private Wheel[] getWheels() {
    TalonSRXConfiguration azimuthConfig = new TalonSRXConfiguration();
    azimuthConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative;
    azimuthConfig.continuousCurrentLimit = 10;
    azimuthConfig.peakCurrentDuration = 0;
    azimuthConfig.peakCurrentLimit = 0;
    azimuthConfig.slot0.kP = 10.0;
    azimuthConfig.slot0.kI = 0.0;
    azimuthConfig.slot0.kD = 100.0;
    azimuthConfig.slot0.kF = 0.0;
    azimuthConfig.slot0.integralZone = 0;
    azimuthConfig.slot0.allowableClosedloopError = 0;
    azimuthConfig.motionAcceleration = 10_000;
    azimuthConfig.motionCruiseVelocity = 800;
    azimuthConfig.velocityMeasurementWindow = 64;
    azimuthConfig.voltageCompSaturation = 12;

    TalonFXConfiguration driveConfig = new TalonFXConfiguration();
    driveConfig.supplyCurrLimit.currentLimit = 0.04;
    driveConfig.supplyCurrLimit.triggerThresholdCurrent = 45;
    driveConfig.supplyCurrLimit.triggerThresholdTime = 40;
    driveConfig.supplyCurrLimit.enable = true;
    driveConfig.slot0.integralZone = 1000;
    driveConfig.slot0.maxIntegralAccumulator = 150_000;
    driveConfig.slot0.allowableClosedloopError = 0;
    driveConfig.velocityMeasurementPeriod = VelocityMeasPeriod.Period_100Ms;
    driveConfig.velocityMeasurementWindow = 64;
    driveConfig.voltageCompSaturation = 12;

    TelemetryService telemetryService = RobotContainer.TELEMETRY;
    telemetryService.stop();

    Wheel[] wheels = new Wheel[4];

    for (int i = 0; i < 4; i++) {
      TalonSRX azimuthTalon = new TalonSRX(i);
      azimuthTalon.configAllSettings(azimuthConfig);
      azimuthTalon.enableCurrentLimit(true);
      azimuthTalon.enableVoltageCompensation(true);
      azimuthTalon.setNeutralMode(NeutralMode.Coast);

      TalonFX driveTalon = new TalonFX(i + 10);
      driveTalon.configAllSettings(driveConfig);
      driveTalon.setNeutralMode(NeutralMode.Brake);
      driveTalon.enableVoltageCompensation(true);

      telemetryService.register(new TalonSRXItem(azimuthTalon, "Azimuth " + i));
      telemetryService.register(new TalonFXItem(driveTalon, "Drive " + (i + 10)));

      wheels[i] = new Wheel(azimuthTalon, driveTalon, DRIVE_SETPOINT_MAX);
    }
    telemetryService.start();

    return wheels;
  }

  private SwerveDrive configSwerve() {
    SwerveDriveConfig config = new SwerveDriveConfig();
    config.length = ROBOT_LENGTH;
    config.width = ROBOT_WIDTH;
    config.wheels = getWheels();
    config.gyro = new AHRS(SPI.Port.kMXP);
    config.gyroLoggingEnabled = true;
    config.summarizeTalonErrors = false;

    return new SwerveDrive(config);
  }

  public void xLockSwerveDrive() {
    System.out.println("FrontLeft: " + XLOCK_FL_TICKS + ", FrontRight: " + XLOCK_FR_TICKS);
    Wheel[] swerveWheels = swerve.getWheels();
    swerveWheels[0].setAzimuthPosition(2000);
    swerveWheels[1].setAzimuthPosition(2000);
    swerveWheels[2].setAzimuthPosition(2000);
    swerveWheels[3].setAzimuthPosition(2000);
    isXLocked = true;
  }
}
