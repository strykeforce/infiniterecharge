package frc.robot.commands.turret;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.TurretSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeekTargetCommand extends CommandBase {
  private static final TurretSubsystem TURRET = RobotContainer.TURRET;
  public Logger logger = LoggerFactory.getLogger(this.getClass());

  public SeekTargetCommand() {
    addRequirements(TURRET);
  }

  @Override
  public void initialize() {
    TURRET.seekTarget(0);
    logger.info("Seeking Target");
  }

  @Override
  public void execute() {}

  @Override
  public boolean isFinished() {
    return TURRET.turretAtTarget();
  }
}
