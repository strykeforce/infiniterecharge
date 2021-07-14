package frc.robot.commands.turret;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.TurretSubsystem;

public class TurretClimbClearCommand extends CommandBase {
  private static final TurretSubsystem turretSubsystem = RobotContainer.TURRET;
  private double angle;

  public TurretClimbClearCommand(double angle) {
    addRequirements(turretSubsystem);
    this.angle = angle;
  }

  @Override
  public void initialize() {
    if (Math.abs(turretSubsystem.getTurretAngle() - 270) > 5) {
      turretSubsystem.setTurretAngle(angle);
    }
  }

  @Override
  public boolean isFinished() {
    return turretSubsystem.turretAtTarget();
  }
}
