package frc.robot.commands.auto;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode;

public class PathDriveCommand extends CommandBase {

  private DriveSubsystem driveSubsystem = RobotContainer.DRIVE;
  private Trajectory trajectory;
  private double targetYaw;
  private double startTimeSeconds;
  private double timeElapsed;
  private boolean isFirstExecute;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public PathDriveCommand(String trajectoryName, double targetYaw) {
    addRequirements(driveSubsystem);
    trajectory = driveSubsystem.calculateTrajectory(trajectoryName);
    this.targetYaw = targetYaw;
  }

  @Override
  public void initialize() {
    isFirstExecute = true;
    driveSubsystem.offsetGyro(-targetYaw);
    driveSubsystem.startPath(trajectory, targetYaw);
    logger.info("Starting pathing...");
  }

  @Override
  public void execute() {
    if (isFirstExecute) {
      startTimeSeconds = Timer.getFPGATimestamp();
      isFirstExecute = false;
    }
    double currentTimeSeconds = Timer.getFPGATimestamp();
    timeElapsed = currentTimeSeconds - startTimeSeconds;
    //    logger.info("Current time seconds: " + timeElapsed);
    driveSubsystem.updatePathOutput(timeElapsed);
  }

  @Override
  public boolean isFinished() {
    return driveSubsystem.isPathDone(timeElapsed);
  }

  @Override
  public void end(boolean interrupted) {
    logger.info("Stopping pathing; Interruption: " + interrupted);
    driveSubsystem.offsetGyro(targetYaw);
    driveSubsystem.drive(0, 0, 0);
    driveSubsystem.offsetGyro(targetYaw);
    driveSubsystem.setDriveMode(DriveMode.OPEN_LOOP);
  }
}
