package frc.robot.commands.turret;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.TurretSubsystem;

public class TurretPositionCommand extends CommandBase {
  private static final TurretSubsystem TURRET = RobotContainer.TURRET;
  private double position;

  public TurretPositionCommand(double position) {
    addRequirements(TURRET);
    this.position = position;
  }

  @Override
  public void initialize() {
    TURRET.setTurret(position);
  }

  @Override
  public boolean isFinished() {
    return TURRET.turretAtTarget();
  }
}
