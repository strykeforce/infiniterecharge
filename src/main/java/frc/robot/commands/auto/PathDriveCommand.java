package frc.robot.commands.auto;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.controller.HolonomicDriveController;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.DriveSubsystem;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;
import net.consensys.cava.toml.Toml;
import net.consensys.cava.toml.TomlArray;
import net.consensys.cava.toml.TomlParseResult;
import net.consensys.cava.toml.TomlTable;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.telemetry.TelemetryService;
import org.strykeforce.telemetry.measurable.Measurable;
import org.strykeforce.telemetry.measurable.Measure;

public class PathDriveCommand extends CommandBase implements Measurable {

  private DriveSubsystem driveSubsystem = RobotContainer.DRIVE;
  private Trajectory trajectory;
  private double targetYaw;
  private double startTimeSeconds;
  private double timeElapsed;

  private boolean isFirstExecute;
  private final Timer timer = new Timer();
  private HolonomicDriveController holonomicDriveController;
  private Trajectory.State state = new Trajectory.State();
  private Pose2d odometryPose = new Pose2d();
  private ChassisSpeeds speeds = new ChassisSpeeds();
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private TelemetryService telemetryService;

  public PathDriveCommand(String trajectoryName) {
    addRequirements(driveSubsystem);

    if (!RobotContainer.isEvent) {
      telemetryService = RobotContainer.TELEMETRY;
      telemetryService.stop();
    }
    if (telemetryService != null) {
      telemetryService.register(this);
    }
    trajectory = calculateTrajectory(trajectoryName);
    timer.start();
    logger.info("Starting timer!");
  }

  @Override
  public void initialize() {
    var p = 6.0;
    var d = p / 100.0;
    holonomicDriveController =
        new HolonomicDriveController(
            new PIDController(p, 0, d),
            new PIDController(p, 0, d),
            new ProfiledPIDController(
                -2.5,
                0,
                0,
                new TrapezoidProfile.Constraints(Constants.DriveConstants.kMaxOmega / 2.0, 3.14)));

    holonomicDriveController.setEnabled(true);
    driveSubsystem.resetOdometry(trajectory.getInitialPose());
    logger.info("Resetting timer!!!");
    timer.reset();
  }

  @Override
  public void execute() {
    state = trajectory.sample(timer.get());
    odometryPose = driveSubsystem.getPoseMeters();
    speeds = holonomicDriveController.calculate(odometryPose, state, new Rotation2d());
    driveSubsystem.move(
        speeds.vxMetersPerSecond, speeds.vyMetersPerSecond, speeds.omegaRadiansPerSecond, true);
  }

  @Override
  public boolean isFinished() {
    return timer.hasElapsed(trajectory.getTotalTimeSeconds());
  }

  @Override
  public void end(boolean interrupted) {
    driveSubsystem.drive(0.0, 0.0, 0.0);
    logger.info("Finished Path in: {}", timer.get());
  }

  public Trajectory calculateTrajectory(String name) {
    // Take name and parse
    Trajectory trajectoryGenerated = new Trajectory();
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

    } catch (IOException error) {
      logger.error(error.toString());
      logger.error("Path {} not found", name);
    }
    return trajectoryGenerated;
  }

  @NotNull
  @Override
  public String getDescription() {
    return "Trajectory Command";
  }

  @Override
  public int getDeviceId() {
    return 0;
  }

  @NotNull
  @Override
  public Set<Measure> getMeasures() {
    return Set.of(
        new Measure("Traj. Accel", () -> state.accelerationMetersPerSecondSq),
        new Measure("Traj. Curvature", () -> state.curvatureRadPerMeter),
        new Measure("Traj. X", () -> state.poseMeters.getX()),
        new Measure("Traj. Y", () -> state.poseMeters.getY()),
        new Measure("Traj. Degrees", () -> state.poseMeters.getRotation().getDegrees()),
        new Measure("Traj. Time", () -> state.timeSeconds),
        new Measure("Traj. Vel", () -> state.velocityMetersPerSecond),
        //        new Measure("Gyro Degrees", () -> driveSubsystem.getHeading().getDegrees()),
        new Measure("HC Vx", () -> speeds.vxMetersPerSecond),
        new Measure("HC Vy", () -> speeds.vyMetersPerSecond),
        new Measure("HC Omega", () -> speeds.omegaRadiansPerSecond)
        //        new Measure("Odom. X", () -> odometryPose.getX()),
        //        new Measure("Odom. Y", () -> odometryPose.getY()),
        //        new Measure("Odom. Degrees", () -> odometryPose.getRotation().getDegrees())
        );
  }
}
