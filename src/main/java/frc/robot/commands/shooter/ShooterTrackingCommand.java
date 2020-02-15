package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.VisionSubsystem;

public class ShooterTrackingCommand extends CommandBase {
  private static final ShooterSubsystem shooter = RobotContainer.SHOOTER;
  private static final VisionSubsystem vision = RobotContainer.VISION;

  public ShooterTrackingCommand() {}

  @Override
  public void initialize() {
    vision.setTrackingEnabled(true);
  }

  @Override
  public void execute() {
    //    if (vision.getOffsetAngle() == 180) shooter.seekTarget();
    //    else shooter.rotateTurret(vision.getOffsetAngle());

    int hoodPos = (int) (10000 * (90 - vision.getElevationAngle()) / 90);
    shooter.setHoodPosition(hoodPos);
  }

  @Override
  public boolean isFinished() {
    return !vision.isTrackingEnabled();
  }
}
