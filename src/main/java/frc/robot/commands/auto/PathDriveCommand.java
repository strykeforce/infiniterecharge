package frc.robot.commands.auto;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.controller.HolonomicDriveController;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathDriveCommand extends CommandBase {

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

  public PathDriveCommand(String trajectoryName, double targetYaw) {
    // FIXME remove target yaw
    addRequirements(driveSubsystem);
    trajectory = driveSubsystem.calculateTrajctory(trajectoryName);
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
    // TODO: convert to new trajectory
    //    return driveSubsystem.isPathDone(timeElapsed);
  }

  @Override
  public void end(boolean interrupted) {
    driveSubsystem.drive(0.0, 0.0, 0.0);
    logger.info("Finished Path in: {}", timer.get());
    // TODO: convert to new trajectory
    //    logger.info("Stopping pathing; Interruption: " + interrupted);
    //    driveSubsystem.offsetGyro(targetYaw);
    //    driveSubsystem.drive(0, 0, 0);
    //    driveSubsystem.offsetGyro(targetYaw);
    //    driveSubsystem.setDriveMode(DriveMode.OPEN_LOOP);
  }
}
