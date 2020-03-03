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
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.DoubleSupplier;
import net.consensys.cava.toml.Toml;
import net.consensys.cava.toml.TomlArray;
import net.consensys.cava.toml.TomlParseResult;
import net.consensys.cava.toml.TomlTable;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode;
import org.strykeforce.thirdcoast.swerve.SwerveDriveConfig;
import org.strykeforce.thirdcoast.swerve.Wheel;
import org.strykeforce.thirdcoast.talon.TalonFXItem;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.Measurable;
import org.strykeforce.thirdcoast.telemetry.item.Measure;
import org.strykeforce.thirdcoast.telemetry.item.TalonSRXItem;

public class DriveSubsystem extends SubsystemBase implements Measurable {

  private static final double ROBOT_LENGTH = 25.5;
  private static final double ROBOT_WIDTH = 21.5;
  private static final double DRIVE_SETPOINT_MAX = 0.0;
  private static final int XLOCK_FL_TICKS_TARGET = 567;
  private static final int XLOCK_FR_TICKS_TARGET = 1481;
  private static final int AZIMUTH_TICKS = 4096;

  private static final double MAX_VELOCITY = 40000; // FIXME
  private static final double MAX_ACCELERATION = 2.0; // FIXME
  private static final int TICKS_PER_REV = 9011; // FIXME
  private static final double WHEEL_DIAMETER = 0.0635; // In meters
  private static final double TICKS_PER_METER = TICKS_PER_REV / (WHEEL_DIAMETER * Math.PI);
  private static final double kP_PATH = 10; // FIXME?
  private static final double MAX_VELOCITY_MPS = (MAX_VELOCITY * 10) / TICKS_PER_METER;
  private static final double kV_PATH = 1 / MAX_VELOCITY_MPS;
  private static final double kP_YAW = 0.01;

  private final SwerveDrive swerve = configSwerve();
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

  public void offsetGyro(double offset) {
    AHRS gyro = swerve.getGyro();
    gyro.setAngleAdjustment(0);
    double adj = gyro.getAngle() % 360;
    gyro.setAngleAdjustment(-adj - offset);
    logger.info("offsetting gyro: ({})", offset);
  }

  public void zeroAzimuths() {
    swerve.zeroAzimuthEncoders();
  }

  public void setDriveMode(DriveMode mode) {
    swerve.setDriveMode(mode);
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
    telemetryService.register(this);
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
    int angle = 0;

    System.out.println(
        "FrontLeft: " + XLOCK_FL_TICKS_TARGET + ", FrontRight: " + XLOCK_FR_TICKS_TARGET);
    Wheel[] swerveWheels = swerve.getWheels();

    for (int i = 0; i < 4; i++) {

      int position = swerveWheels[i].getAzimuthTalon().getSelectedSensorPosition();
      int TARGET = XLOCK_FL_TICKS_TARGET;
      angle = position % AZIMUTH_TICKS;
      if (i == 1 || i == 2) {
        TARGET = XLOCK_FR_TICKS_TARGET;
      }

      if (angle >= 0) {
        int delta = TARGET - angle;
        if (Math.abs(delta) > AZIMUTH_TICKS / 4) {
          delta = (TARGET + AZIMUTH_TICKS / 2) - angle;
        }
        swerveWheels[i].setAzimuthPosition(position + delta);
      } else {
        int delta = (TARGET - AZIMUTH_TICKS / 2) - angle;
        if (Math.abs(delta) > AZIMUTH_TICKS / 4) {
          delta = (TARGET - AZIMUTH_TICKS) - angle;
        }
        swerveWheels[i].setAzimuthPosition(position + delta);
      }
    }
  }

  public void ZeroSwerve() {
    Wheel[] swerveWheels = swerve.getWheels();
    for (int i = 0; i < 4; i++) {
      swerveWheels[i].setAzimuthPosition(0);
    }
  }

  public AHRS getGyro() {
    return swerve.getGyro();
  }

  public Wheel[] getAllWheels() {
    return swerve.getWheels();
  }

  // ----------------------------------Path
  // Methods--------------------------------------------------
  public Trajectory calculateTrajctory(String name) {
    // Take name and parse
    try {
      // Parse Toml File
      TomlParseResult parseResult =
          Toml.parse(Paths.get("/home/lvuser/deploy/paths/" + name + ".toml"));

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
      for (int i = 0; i < states.size(); i++) {
        //        logger.info(states.get(i).toString());
      }
    } catch (IOException error) {
      logger.error(error.toString());
      logger.error("Path {} not found", name);
    }
    return trajectoryGenerated;
  }

  public void startPath(Trajectory trajectoryGenerated, double targetYaw) {
    this.trajectoryGenerated = trajectoryGenerated;
    this.targetYaw = targetYaw;
    for (int i = 0; i < 4; i++) {
      startEncoderPosition[i] = swerve.getWheels()[i].getDriveTalon().getSelectedSensorPosition();
    }

    // Reset State Variables
    lastPosition = Constants.AutoConstants.START_PATH.getTranslation();
    estimatedDistanceTraveled = 0;
    desiredDistance = 0;
    distError = 0.0;
    // swerve.setDriveMode(DriveMode.CLOSED_LOOP); //FIXME
  }

  public void updatePathOutput(double timeSeconds) {
    Trajectory.State currentState = trajectoryGenerated.sample(timeSeconds);

    // Calculate Desired Distance
    estimatedDistanceTraveled += currentState.poseMeters.getTranslation().getDistance(lastPosition);
    desiredDistance = estimatedDistanceTraveled;

    // Get Current Distance Travelled
    int currentEncoderPosition = 0;
    for (int i = 0; i < 4; i++) {
      currentEncoderPosition +=
          Math.abs(
              swerve.getWheels()[i].getDriveTalon().getSelectedSensorPosition()
                  - startEncoderPosition[i]);
    }
    currentEncoderPosition = currentEncoderPosition / 4;
    currentDistance = currentEncoderPosition / TICKS_PER_METER;

    // Calculate Output Velocity
    distError = desiredDistance - currentDistance;
    desiredPathVelocity = currentState.velocityMetersPerSecond / MAX_VELOCITY_MPS;
    double rawOutput =
        Math.copySign(1, currentState.velocityMetersPerSecond) * kP_PATH * distError
            + currentState.velocityMetersPerSecond / MAX_VELOCITY_MPS;
    pathVelocity =
        rawOutput / MAX_VELOCITY_MPS; // * TICKS_PER_METER)/(MAX_VELOCITY * 10); // Ticks per 100ms
    // //FIXME
    double heading = currentState.poseMeters.getRotation().getRadians();

    // Convert to FWD/STR Components
    fwdPath = Math.cos(heading) * pathVelocity;
    strPath = Math.sin(heading) * pathVelocity;
    if (targetYaw == 180 || targetYaw == -180) {
      double currentGyro = Math.IEEEremainder(swerve.getGyro().getAngle(), 360);
      double posError = currentGyro - 180;
      double negError = currentGyro + 180;
      if (Math.abs(posError) < Math.abs(negError)) yawError = posError;
      else yawError = negError;
    } else {
      yawError = Math.IEEEremainder(swerve.getGyro().getAngle(), 360) - targetYaw;
    }
    if (Math.abs(yawError) < 1) {
      yawPath = 0.0;
    } else {
      yawPath = -yawError * kP_YAW;
    }
    drive(-fwdPath, -strPath, -yawPath);
    lastPosition = currentState.poseMeters.getTranslation();
  }

  public boolean isPathDone(double timePassedSeconds) {
    return timePassedSeconds >= trajectoryGenerated.getTotalTimeSeconds();
  }

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

  @NotNull
  @Override
  public String getDescription() {
    return "drive subsystem";
  }

  @Override
  public int getDeviceId() {
    return 0;
  }

  @NotNull
  @Override
  public Set<Measure> getMeasures() {
    return Set.of(
        new Measure(DESIRED_DISTANCE, "desired distance"),
        new Measure(ACTUAL_DISTANCE, "actual distance"),
        new Measure(DISTANCE_ERROR, "distance error"),
        new Measure(PATH_VELOCITY, "path velocity"),
        new Measure(DESIRED_VELOCITY, "desired path velocity"),
        new Measure(FWD_PATH, "forward path"),
        new Measure(STR_PATH, "strafe path"),
        new Measure(YAW_PATH, "yaw path"),
        new Measure(YAW_ERROR, "yaw_error"),
        new Measure(YAW, "yaw"));
  }

  @NotNull
  @Override
  public String getType() {
    return "drive subsystem";
  }

  @Override
  public int compareTo(@NotNull Measurable measurable) {
    return 0;
  }

  @NotNull
  @Override
  public DoubleSupplier measurementFor(@NotNull Measure measure) {
    switch (measure.getName()) {
      case DESIRED_DISTANCE:
        return () -> desiredDistance;
      case ACTUAL_DISTANCE:
        return () -> currentDistance;
      case DISTANCE_ERROR:
        return () -> distError;
      case PATH_VELOCITY:
        return () -> pathVelocity;
      case DESIRED_VELOCITY:
        return () -> desiredPathVelocity;
      case FWD_PATH:
        return () -> fwdPath;
      case STR_PATH:
        return () -> strPath;
      case YAW_PATH:
        return () -> yawPath;
      case YAW_ERROR:
        return () -> yawError;
      case YAW:
        return () -> Math.IEEEremainder(swerve.getGyro().getAngle(), 360);
      default:
        return () -> 2767;
    }
  }
}
