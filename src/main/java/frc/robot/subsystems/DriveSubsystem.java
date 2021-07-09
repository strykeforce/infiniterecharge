package frc.robot.subsystems;

import static frc.robot.Constants.kTalonConfigTimeout;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.controller.HolonomicDriveController;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import frc.robot.Constants.DriveConstants;
import frc.robot.RobotContainer;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.consensys.cava.toml.Toml;
import net.consensys.cava.toml.TomlArray;
import net.consensys.cava.toml.TomlParseResult;
import net.consensys.cava.toml.TomlTable;
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
  private Trajectory trajectoryGenerated;
  private HolonomicDriveController holonomicDriveController;
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

  public Rotation2d getHeading() {
    return swerveDrive.getHeading();
  }

  // ----------------------------------Path
  // Methods--------------------------------------------------
  public Trajectory calculateTrajctory(String name) {
    // Take name and parse
    try {
      // Parse Toml File
      TomlParseResult parseResult =
          Toml.parse(Paths.get("/home/lvuser/deploy/paths/" + name + ".toml"));
      logger.info("calculating trajectory: {}", name);
      Pose2d startPos =
          new Pose2d(
              parseResult.getTable("start_pose").getDouble("x"),
              parseResult.getTable("start_pose").getDouble("y"),
              new Rotation2d(parseResult.getTable("start_pose").getDouble("angle")));
      Pose2d endPos =
          new Pose2d(
              parseResult.getTable("end_pose").getDouble("x"),
              parseResult.getTable("end_pose").getDouble("y"),
              new Rotation2d(parseResult.getTable("end_pose").getDouble("angle")));
      TomlArray internalPointsToml = parseResult.getArray("internal_points");
      ArrayList<Translation2d> path = new ArrayList<>();
      logger.info("Toml Array Size: {}", internalPointsToml.size());

      for (int i = 0; i < internalPointsToml.size(); i++) {
        TomlTable pointToml = internalPointsToml.getTable(i);
        Translation2d point = new Translation2d(pointToml.getDouble("x"), pointToml.getDouble("y"));
        //        path.set(i, point);
        path.add(point);
      }

      // Create Trajectory
      TrajectoryConfig trajectoryConfig =
          new TrajectoryConfig(
              parseResult.getDouble("max_velocity"), parseResult.getDouble("max_acceleration"));
      trajectoryConfig.setReversed(parseResult.getBoolean("is_reversed"));
      trajectoryConfig.setStartVelocity(parseResult.getDouble("start_velocity"));
      trajectoryConfig.setEndVelocity(parseResult.getDouble("end_velocity"));
      trajectoryGenerated =
          TrajectoryGenerator.generateTrajectory(startPos, path, endPos, trajectoryConfig);

      List<Trajectory.State> states = trajectoryGenerated.getStates();
      for (int i = 0; i < states.size(); i++) {}
    } catch (IOException error) {
      logger.error(error.toString());
      logger.error("Path {} not found", name);
    }
    return trajectoryGenerated;
  }

  public Trajectory.State sampleTrajectory(double timeSeconds) {
    return trajectoryGenerated.sample(timeSeconds);
  }

  public Trajectory getTrajectory() {
    return trajectoryGenerated;
  }
  // Measurable Support

  @NotNull
  @Override
  public Set<Measure> getMeasures() {
    return Set.of(
        new Measure("Gyro Rotation2d (deg)", () -> swerveDrive.getHeading().getDegrees()),
        new Measure("Gyro Angle (deg)", swerveDrive::getGyroAngle),
        new Measure("Odometry X", () -> swerveDrive.getPoseMeters().getX()),
        new Measure("Odometry Y", () -> swerveDrive.getPoseMeters().getY()),
        new Measure(
            "Odometry Rotation2d (deg)",
            () -> swerveDrive.getPoseMeters().getRotation().getDegrees()));
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
}
