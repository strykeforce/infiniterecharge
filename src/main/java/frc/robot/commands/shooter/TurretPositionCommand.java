package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;

public class TurretPositionCommand extends CommandBase {
  private static final ShooterSubsystem shooter = RobotContainer.SHOOTER;
  private double angle;

  public TurretPositionCommand(double angle) {
    this.angle = angle;
  }

  @Override
  public void initialize() {
    shooter.setTurretAngle(angle);
  }

  @Override
  public boolean isFinished() {
    return shooter.turretAtTarget();
  }
}
