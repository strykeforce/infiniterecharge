package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;

public class HoodPositionCommand extends CommandBase {
  private static final ShooterSubsystem shooter = RobotContainer.SHOOTER;
  private int position;

  public HoodPositionCommand(int position) {
    this.position = position;
  }

  @Override
  public void initialize() {
    shooter.setHoodPosition(position);
  }

  @Override
  public boolean isFinished() {
    return shooter.hoodAtTarget();
  }
}
