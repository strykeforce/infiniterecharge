package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AimShooterCommand extends CommandBase {
  private VisionSubsystem VISION = RobotContainer.VISION;
  private ShooterSubsystem SHOOTER = RobotContainer.SHOOTER;
  public Logger logger = LoggerFactory.getLogger("Aim Shooter Command");

  public AimShooterCommand() {
    addRequirements(SHOOTER);
    addRequirements(VISION);
  }

  @Override
  public void initialize() {
    if (VISION.getTargetData().getValid()) {
      double offset = VISION.getOffsetAngle();
      SHOOTER.rotateTurret(-1.015 * offset);
      logger.info("Single correction: offset angle {}, correction: {}", offset, 1.015 * offset);
    }
  }

  @Override
  public boolean isFinished() {
    return SHOOTER.turretAtTarget() && VISION.isStable();
  }
}
