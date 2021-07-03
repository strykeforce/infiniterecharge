package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import frc.robot.RobotContainer;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.swerve.SwerveDrive;
import org.strykeforce.telemetry.TelemetryService;
import org.strykeforce.telemetry.measurable.MeasurableSubsystem;
import org.strykeforce.telemetry.measurable.Measure;

public class DriveSubsystem extends MeasurableSubsystem {

  private static final double ROBOT_LENGTH = 25.5;
  private static final double ROBOT_WIDTH = 21.5;
  private static final double DRIVE_SETPOINT_MAX = 18000.0;
  private static final int XLOCK_FL_TICKS_TARGET = 567;
  private static final int XLOCK_FR_TICKS_TARGET = 1481;
  private static final int AZIMUTH_TICKS = 4096;

  private static final double MAX_VELOCITY = 40000; // FIXME
  private static final double MAX_ACCELERATION = 2.0; // FIXME
  private static final int TICKS_PER_REV = 9011; // FIXME
  private static final double WHEEL_DIAMETER = 0.0635; // In meters
  private static final double TICKS_PER_METER = 55451; // TICKS_PER_REV / (WHEEL_DIAMETER * Math.PI)
  private static final double kP_PATH = 10; // FIXME?
  private static final double MAX_VELOCITY_MPS = (MAX_VELOCITY * 10) / TICKS_PER_METER;
  private static final double kV_PATH = 1 / MAX_VELOCITY_MPS;
  private static final double kP_YAW = 0.01;
  // -----------------------------------GRAPHER
  // METHODS---------------------------------------------------
  private static final String DESIRED_DISTANCE = "desired distance";
  private static final String ACTUAL_DISTANCE = "actual distance";
  private static final String DISTANCE_ERROR = "distance error";
  private static final String PATH_VELOCITY = "path velocity";
  private static final String DESIRED_VELOCITY = "desired path velocity";
  private static final String FWD_PATH = "forward path";
  private static final String STR_PATH = "strafe path";
  private static final String YAW_PATH = "yaw path";
  private static final String YAW_ERROR = "yaw error";
  private static final String YAW = "yaw";
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final SwerveDrive swerve = configSwerve();
  TelemetryService telemetryService;
  private Trajectory trajectoryGenerated;
  private int[] startEncoderPosition = new int[4];
  private double estimatedDistanceTraveled;
  private Translation2d lastPosition;
  private double targetYaw;
  private double currentDistance = 0;
  private double desiredDistance = 0;
  private double distError = 0;
  private double yawError = 0;
  private double pathVelocity = 0;
  private double desiredPathVelocity = 0;
  private double fwdPath = 0;
  private double strPath = 0;
  private double yawPath = 0;

  public DriveSubsystem() {
    //    swerve.setFieldOriented(true);
    zeroAzimuths();
  }

  public void drive(double forward, double strafe, double yaw) {
    //    swerve.drive(forward, strafe, yaw);
  }

  public void zeroGyro() {
    //    AHRS gyro = swerve.getGyro();
    //    gyro.setAngleAdjustment(0);
    //    double adj = gyro.getAngle() % 360;
    //    gyro.setAngleAdjustment(-adj);
    //    logger.info("resetting gyro: ({})", adj);
  }

  public void offsetGyro(double offset) {
    //    AHRS gyro = swerve.getGyro();
    //    gyro.setAngleAdjustment(0);
    //    double adj = gyro.getAngle() % 360;
    //    gyro.setAngleAdjustment(-adj - offset);
    //    logger.info("offsetting gyro: ({})", offset);
  }

  public void zeroAzimuths() {
    //    swerve.zeroAzimuthEncoders();
  }

  private void getWheels() {
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
    driveConfig.slot0.kP = 0.045;
    driveConfig.slot0.kI = 0.0005;
    driveConfig.slot0.kD = 0.000;
    driveConfig.slot0.kF = 0.047;
    driveConfig.slot0.integralZone = 500;
    driveConfig.slot0.maxIntegralAccumulator = 75_000;
    driveConfig.slot0.allowableClosedloopError = 0;
    driveConfig.velocityMeasurementPeriod = VelocityMeasPeriod.Period_100Ms;
    driveConfig.velocityMeasurementWindow = 64;
    driveConfig.voltageCompSaturation = 12;
    if (!RobotContainer.isEvent) {
      telemetryService = RobotContainer.TELEMETRY;
      telemetryService.stop();
    }

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
      if (!RobotContainer.isEvent) {
        //        telemetryService.register(new TalonSRXItem(azimuthTalon, "Azimuth " + i));
        //        telemetryService.register(new TalonFXItem(driveTalon, "Drive " + (i + 10)));
      }
      //      wheels[i] = new Wheel(azimuthTalon, driveTalon, DRIVE_SETPOINT_MAX);
    }
    if (!RobotContainer.isEvent) {
      telemetryService.register(this);
      telemetryService.start();
    }
  }

  private SwerveDrive configSwerve() {
    return null;
  }

  public void xLockSwerveDrive() {
    //    int angle = 0;
    //    logger.info("X-locking wheels");
    //    Wheel[] swerveWheels = swerve.getWheels();
    //
    //    for (int i = 0; i < 4; i++) {
    //
    //      int position = (int) swerveWheels[i].getAzimuthTalon().getSelectedSensorPosition();
    //      int TARGET = XLOCK_FL_TICKS_TARGET;
    //      angle = position % AZIMUTH_TICKS;
    //      if (i == 1 || i == 2) {
    //        TARGET = XLOCK_FR_TICKS_TARGET;
    //      }
    //
    //      if (angle >= 0) {
    //        int delta = TARGET - angle;
    //        if (Math.abs(delta) > AZIMUTH_TICKS / 4) {
    //          delta = (TARGET + AZIMUTH_TICKS / 2) - angle;
    //        }
    //        swerveWheels[i].setAzimuthPosition(position + delta);
    //      } else {
    //        int delta = (TARGET - AZIMUTH_TICKS / 2) - angle;
    //        if (Math.abs(delta) > AZIMUTH_TICKS / 4) {
    //          delta = (TARGET - AZIMUTH_TICKS) - angle;
    //        }
    //        swerveWheels[i].setAzimuthPosition(position + delta);
    //      }
    //    }
  }

  public void zeroSwerve() {
    //    Wheel[] swerveWheels = swerve.getWheels();
    //    for (int i = 0; i < 4; i++) {
    //      swerveWheels[i].setAzimuthPosition(0);
    //    }
  }

  public AHRS getGyro() {
    throw new UnsupportedOperationException("gyro not implemented");
  }

  // ----------------------------------Path
  // Methods--------------------------------------------------
  public Trajectory calculateTrajectory(String name) {
    throw new UnsupportedOperationException("old path code");
  }

  public void startPath(Trajectory trajectoryGenerated, double targetYaw) {
    throw new UnsupportedOperationException("old path code");
  }

  public void updatePathOutput(double timeSeconds) {
    throw new UnsupportedOperationException("old path code");
  }

  public boolean isPathDone(double timePassedSeconds) {
    throw new UnsupportedOperationException("old path code");
  }

  @NotNull
  @Override
  public Set<Measure> getMeasures() {
    return Set.of(
        new Measure(DESIRED_DISTANCE, "desired distance", () -> desiredDistance),
        new Measure(ACTUAL_DISTANCE, "actual distance", () -> currentDistance),
        new Measure(DISTANCE_ERROR, "distance error", () -> distError),
        new Measure(PATH_VELOCITY, "path velocity", () -> pathVelocity),
        new Measure(DESIRED_VELOCITY, "desired path velocity", () -> desiredPathVelocity),
        new Measure(FWD_PATH, "forward path", () -> fwdPath),
        new Measure(STR_PATH, "strafe path", () -> strPath),
        new Measure(YAW_PATH, "yaw path", () -> yawPath),
        new Measure(YAW_ERROR, "yaw_error", () -> yawError),
        new Measure(
            YAW,
            "yaw",
            () -> {
              throw new UnsupportedOperationException();
            }));
    //        new Measure(YAW, "yaw", () -> Math.IEEEremainder(swerve.getGyro().getAngle(), 360)));
  }
}
