package frc.robot.subsystems;

import static frc.robot.Constants.kTalonConfigTimeout;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.SwerveModuleState;
import frc.robot.Constants.DriveConstants;
import frc.robot.RobotContainer;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.swerve.SwerveDrive;
import org.strykeforce.swerve.SwerveModule;
import org.strykeforce.swerve.TalonSwerveModule;
import org.strykeforce.telemetry.TelemetryService;
import org.strykeforce.telemetry.measurable.MeasurableSubsystem;
import org.strykeforce.telemetry.measurable.Measure;

public class DriveSubsystem extends MeasurableSubsystem {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final SwerveDrive swerveDrive;
  TelemetryService telemetryService;

  public DriveSubsystem() {
    var moduleBuilder =
        new TalonSwerveModule.Builder()
            .driveGearRatio(DriveConstants.kDriveGearRatio)
            .wheelDiameterInches(DriveConstants.kWheelDiameterInches)
            .driveMaximumMetersPerSecond(DriveConstants.kMaxSpeedMetersPerSecond);

    TalonSwerveModule[] swerveModules = new TalonSwerveModule[4];
    Translation2d[] wheelLocations = DriveConstants.getWheelLocationMeters();

    if (!RobotContainer.isEvent) {
      telemetryService = RobotContainer.TELEMETRY;
      telemetryService.stop();
    }

    for (int i = 0; i < 4; i++) {
      var azimuthTalon = new TalonSRX(i);
      azimuthTalon.configFactoryDefault(kTalonConfigTimeout);
      azimuthTalon.configAllSettings(DriveConstants.getAzimuthTalonConfig(), kTalonConfigTimeout);
      azimuthTalon.enableCurrentLimit(true);
      azimuthTalon.enableVoltageCompensation(true);
      azimuthTalon.setNeutralMode(NeutralMode.Coast);

      var driveTalon = new TalonFX(i + 10);
      driveTalon.configFactoryDefault(kTalonConfigTimeout);
      driveTalon.configAllSettings(DriveConstants.getDriveTalonConfig(), kTalonConfigTimeout);
      driveTalon.enableVoltageCompensation(true);
      driveTalon.setNeutralMode(NeutralMode.Brake);

      swerveModules[i] =
          moduleBuilder
              .azimuthTalon(azimuthTalon)
              .driveTalon(driveTalon)
              .wheelLocationMeters(wheelLocations[i])
              .build();

      swerveModules[i].loadAndSetAzimuthZeroReference();
      if (telemetryService != null) {
        telemetryService.register(azimuthTalon);
        telemetryService.register(driveTalon);
        telemetryService.register(this);
      }
    }

    swerveDrive = new SwerveDrive(swerveModules);
    swerveDrive.resetGyro();
  }

  /**
   * Returns the swerve drive kinematics object for use during trajectory configuration.
   *
   * @return the configured kinemetics object
   */
  public SwerveDriveKinematics getSwerveDriveKinematics() {
    return swerveDrive.getKinematics();
  }

  /** Returns the configured swerve drive modules. */
  public SwerveModule[] getSwerveModules() {
    return swerveDrive.getSwerveModules();
  }

  /**
   * Resets the robot's position on the field.
   *
   * @param pose the current pose
   */
  public void resetOdometry(Pose2d pose) {
    swerveDrive.resetOdometry(pose);
    logger.info("reset odometry with pose = {}", pose);
  }

  /**
   * Returns the position of the robot on the field.
   *
   * @return the pose of the robot (x and y ane in meters)
   */
  public Pose2d getPoseMeters() {
    return swerveDrive.getPoseMeters();
  }

  /** Perform periodic swerve drive odometry update. */
  @Override
  public void periodic() {
    swerveDrive.periodic();
  }

  /** Drive the robot with given x, y, and rotational velocities with open-loop velocity control. */
  public void drive(
      double vxMetersPerSecond, double vyMetersPerSecond, double omegaRadiansPerSecond) {
    swerveDrive.drive(vxMetersPerSecond, vyMetersPerSecond, omegaRadiansPerSecond, true);
  }

  /**
   * Move the robot with given x, y, and rotational velocities with closed-loop velocity control.
   */
  public void move(
      double vxMetersPerSecond,
      double vyMetersPerSecond,
      double omegaRadiansPerSecond,
      boolean isFieldOriented) {
    swerveDrive.move(vxMetersPerSecond, vyMetersPerSecond, omegaRadiansPerSecond, isFieldOriented);
  }

  public void resetGyro() {
    swerveDrive.resetGyro();
  }

  public void setGyroOffset(Rotation2d offsetRads) {
    swerveDrive.setGyroOffset(offsetRads);
  }

  public Rotation2d getHeading() {
    return swerveDrive.getHeading();
  }

  public void xLockSwerveDrive() {
    SwerveModuleState state1 = new SwerveModuleState(0.01, new Rotation2d(Math.toRadians(45)));
    SwerveModuleState state2 = new SwerveModuleState(0.01, new Rotation2d(Math.toRadians(-45)));
    SwerveModuleState[] states = new SwerveModuleState[4];
    for (int i = 0; i < 4; i += 2) {
      states[i] = state1;
      states[i + 1] = state2;
    }
    swerveDrive.setModuleStates(states);
  }

  // Measurable Support

  @NotNull
  @Override
  public Set<Measure> getMeasures() {
    return Set.of(
        new Measure("Gyro Rotation2d (deg)", () -> swerveDrive.getHeading().getDegrees()),
        new Measure("Odometry X", () -> swerveDrive.getPoseMeters().getX()),
        new Measure("Odometry Y", () -> swerveDrive.getPoseMeters().getY()),
        new Measure(
            "Odometry Rotation2d (deg)",
            () -> swerveDrive.getPoseMeters().getRotation().getDegrees()));
  }
}
