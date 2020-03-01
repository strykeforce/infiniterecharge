package frc.robot.commands.turret;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.TurretSubsystem;

public class TurretAngleCommand extends CommandBase {
  private static final TurretSubsystem TURRET = RobotContainer.TURRET;
  private double angle;

  public TurretAngleCommand(double angle) {
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
