package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShooterAimCommand extends CommandBase {
  private static final ShooterSubsystem SHOOTER = RobotContainer.SHOOTER;
  private static final VisionSubsystem VISION = RobotContainer.VISION;

  public Logger logger = LoggerFactory.getLogger("Shooter Track Command");

  public ShooterAimCommand() {
    addRequirements(SHOOTER);
  }

  @Override
  public void initialize() {
    VISION.setTrackingEnabled(true);
    if (VISION.isTargetValid()) {
      SHOOTER.run((int) VISION.getShooterSetpoint(VISION.getBestTableIndex()));
    }
  }

  @Override
  public boolean isFinished() {
    return SHOOTER.atTargetSpeed();
  }
}
