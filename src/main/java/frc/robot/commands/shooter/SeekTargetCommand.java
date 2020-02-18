package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;

public class SeekTargetCommand extends CommandBase {
  private static final ShooterSubsystem SHOOTER = RobotContainer.SHOOTER;

  public SeekTargetCommand() {
    addRequirements(SHOOTER);
  }

  @Override
  public void initialize() {
    SHOOTER.seekTarget();
  }

  @Override
  public void execute() {}

  @Override
  public boolean isFinished() {
    return SHOOTER.turretAtTarget();
  }
}
