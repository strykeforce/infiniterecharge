package frc.robot.commands.vision;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TempVisInstant extends InstantCommand {
  private VisionSubsystem VISION = RobotContainer.VISION;
  private ShooterSubsystem SHOOTER = RobotContainer.SHOOTER;
  public Logger logger = LoggerFactory.getLogger("Temp Command");

  public TempVisInstant() {
    addRequirements(SHOOTER);
  }

  @Override
  public void initialize() {
    logger.info("Temp command running with offset = {}", VISION.getOffsetAngle());
    if (VISION.getTargetData().getValid()) SHOOTER.rotateTurret(-VISION.getOffsetAngle());
  }
}
