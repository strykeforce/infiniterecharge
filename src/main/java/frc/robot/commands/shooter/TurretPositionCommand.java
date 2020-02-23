package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.TurretSubsystem;

public class TurretPositionCommand extends CommandBase {
  private static final TurretSubsystem TURRET = RobotContainer.TURRET;
  private double angle;

  public TurretPositionCommand(double angle) {
      addRequirements(TURRET);
    this.angle = angle;
  }

  @Override
  public void initialize() {
    TURRET.setTurretAngle(angle);
  }

  @Override
  public boolean isFinished() {
    return TURRET.turretAtTarget();
  }
}
