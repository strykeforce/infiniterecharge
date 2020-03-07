package frc.robot.commands.turret;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.TurretSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TurretPositionCommand extends CommandBase {
  private static final TurretSubsystem TURRET = RobotContainer.TURRET;
  private double position;
  public Logger logger = LoggerFactory.getLogger(this.getClass());

  public TurretPositionCommand(double position) {
    addRequirements(TURRET);
    this.position = position;
  }

  @Override
  public void initialize() {
    TURRET.setTurret(position);
    logger.info("Rotating Turret to {} ticks", position);
  }

  @Override
  public boolean isFinished() {
    return TURRET.turretAtTarget();
  }
}
