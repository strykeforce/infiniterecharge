package frc.robot.commands.auto;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.DriveSubsystem;
import org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode;

public class PathDriveCommand extends CommandBase {

  private DriveSubsystem driveSubsystem = RobotContainer.DRIVE;
  private Trajectory trajectory;
  private double targetYaw;
  private double startTimeSeconds;
  private double timeElapsed;
  private boolean isFirstExecute;

  public PathDriveCommand(String trajectoryName, double targetYaw) {
    addRequirements(driveSubsystem);
    trajectory = driveSubsystem.calculateTrajctory(trajectoryName);
    this.targetYaw = targetYaw;
  }

  @Override
  public void initialize() {
    isFirstExecute = true;
    driveSubsystem.startPath(trajectory, targetYaw);
    System.out.println("Starting pathing...");
  }

  @Override
  public void execute() {
    if (isFirstExecute) {
      startTimeSeconds = Timer.getFPGATimestamp();
      isFirstExecute = false;
    }
    double currentTimeSeconds = Timer.getFPGATimestamp();
    timeElapsed = currentTimeSeconds - startTimeSeconds;
    System.out.println("Current time seconds: " + timeElapsed);
    driveSubsystem.updatePathOutput(timeElapsed);
  }

  @Override
  public boolean isFinished() {
    return driveSubsystem.isPathDone(timeElapsed);
  }

  @Override
  public void end(boolean interrupted) {
    System.out.println("Stopping pathing; Interruption: " + interrupted);
    driveSubsystem.drive(0, 0, 0);
    driveSubsystem.setDriveMode(DriveMode.OPEN_LOOP);
  }
}
