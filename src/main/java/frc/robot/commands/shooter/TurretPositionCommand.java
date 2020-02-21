package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.TurretSubsystem;

public class TurretPositionCommand extends CommandBase {
  private static final ShooterSubsystem shooter = RobotContainer.SHOOTER;
  private static final TurretSubsystem turret = RobotContainer.TURRET;
  private double angle;

  public TurretPositionCommand(double angle) {
    this.angle = angle;
  }

  @Override
  public void initialize() {
    turret.setTurretAngle(angle);
  }

  @Override
  public boolean isFinished() {
    return turret.turretAtTarget();
  }
}
