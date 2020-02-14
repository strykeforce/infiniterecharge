package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;

public class SeekTargetCommand extends CommandBase {
  private static final ShooterSubsystem shooterSubsystem = RobotContainer.SHOOTER;

  public SeekTargetCommand() {}

  @Override
  public void initialize() {
    shooterSubsystem.seekTarget();
  }

  @Override
  public void execute() {}

  @Override
  public boolean isFinished() {
    return shooterSubsystem.turretAtTarget();
  }
}
