package frc.robot.commands.auto;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.DriveSubsystem;

public class TimedDriveCommand extends CommandBase {
  private DriveSubsystem driveSubsystem = RobotContainer.DRIVE;
  private final Timer timer = new Timer();

  public TimedDriveCommand() {
    addRequirements(driveSubsystem);
    timer.start();
  }

  @Override
  public void initialize() {
    timer.reset();
    driveSubsystem.drive(1, 0, 0);
  }

  @Override
  public boolean isFinished() {
    return timer.hasElapsed(2);
  }

  @Override
  public void end(boolean interrupted) {
    driveSubsystem.move(0, 0, 0, true);
  }
}
