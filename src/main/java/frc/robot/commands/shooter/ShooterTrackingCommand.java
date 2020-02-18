package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.VisionSubsystem;

public class ShooterTrackingCommand extends CommandBase {
  private static final ShooterSubsystem SHOOTER = RobotContainer.SHOOTER;
  private static final VisionSubsystem VISION = RobotContainer.VISION;

  public ShooterTrackingCommand() {
    addRequirements(SHOOTER);
  }

  @Override
  public void initialize() {
    VISION.setTrackingEnabled(true);
  }

  @Override
  public void execute() {
    System.out.println(VISION.getOffsetAngle());
    if (VISION.getOffsetAngle() == 180) SHOOTER.seekTarget();
    else SHOOTER.rotateTurret(-VISION.getOffsetAngle());

    //    int hoodPos = (int) (5000 * (90 - vision.getElevationAngle()) / 90);
    //    if (hoodPos > 5000) hoodPos = 5000;
    //    shooter.setHoodPosition(hoodPos);
  }

  @Override
  public boolean isFinished() {
    return !VISION.isTrackingEnabled();
  }
}
