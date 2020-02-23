package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;

public class TurretRangeCommand extends CommandBase {
  private static final ShooterSubsystem SHOOTER = RobotContainer.SHOOTER;
  private double angle;

  public TurretRangeCommand(double angle) {
    addRequirements(SHOOTER);

    this.angle = angle;
  }

  @Override
  public void initialize() {
    SHOOTER.setTurretAngle(angle);
  }

  @Override
  public boolean isFinished() {
    return SHOOTER.turretInRange(1);
  }
}
