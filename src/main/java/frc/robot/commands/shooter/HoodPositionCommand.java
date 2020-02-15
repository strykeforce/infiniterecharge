package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;

public class HoodPositionCommand extends CommandBase {
  private static final ShooterSubsystem shooter = RobotContainer.SHOOTER;
  private double angle;

  public HoodPositionCommand(double angle) {
    this.angle = angle;
  }

  @Override
  public void initialize() {
    shooter.setHoodAngle(angle);
  }

  @Override
  public boolean isFinished() {
    return shooter.hoodAtTarget();
  }
}
