package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.controls.DriverControls;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShooterTrackingCommand extends CommandBase {
  private static final ShooterSubsystem SHOOTER = RobotContainer.SHOOTER;
  private static final VisionSubsystem VISION = RobotContainer.VISION;
  private static DriverControls DRIVER_CONTROLS;

  double strafeFactor = 5.45; // theoretical 7.45

  public Logger logger = LoggerFactory.getLogger("Track Command");

  public ShooterTrackingCommand() {
    addRequirements(SHOOTER);
  }

  @Override
  public void initialize() {
    VISION.setTrackingEnabled(true);
    DRIVER_CONTROLS = RobotContainer.CONTROLS.getDriverControls();
  }

  @Override
  public void execute() {
    if (VISION.isTargetValid()) {
      SHOOTER.rotateTurret(-0.95 * VISION.getOffsetAngle() + getStrafeAdjustment());
    } else SHOOTER.seekTarget();
  }

  @Override
  public boolean isFinished() {
    return !VISION.isTrackingEnabled();
  }

  private double getStrafeAdjustment() {
    double strafe = DRIVER_CONTROLS.getStrafe();
    if (Math.abs(strafe) > .2) {
      return strafe * strafeFactor;
    }
    return 0;
  }
}
