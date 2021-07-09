package frc.robot.commands.drive;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.controls.DriverControls;
import frc.robot.subsystems.DriveSubsystem;
import org.strykeforce.thirdcoast.util.ExpoScale;

public class TeleopDriveCommand extends CommandBase {
  private DriveSubsystem DRIVE = RobotContainer.DRIVE;
  private DriverControls driverControls = RobotContainer.CONTROLS.getDriverControls();

  private static final double FORWARD_DEADBAND = 0.05;
  private static final double STRAFE_DEADBAND = 0.05;
  private static final double YAW_DEADBAND = 0.01;

  private static final double FORWARD_XPOSCALE = 0.6;
  private static final double STRAFE_XPOSCALE = 0.6;
  private static final double YAW_XPOSCALE = 0.75;

  private final ExpoScale forwardScale;
  private final ExpoScale strafeScale;
  private final ExpoScale yawScale;

  public TeleopDriveCommand() {
    addRequirements(DRIVE);

    forwardScale = new ExpoScale(FORWARD_DEADBAND, FORWARD_XPOSCALE);
    strafeScale = new ExpoScale(STRAFE_DEADBAND, STRAFE_XPOSCALE);
    yawScale = new ExpoScale(YAW_DEADBAND, YAW_XPOSCALE);
  }

  @Override
  public void execute() {
    double forward = forwardScale.apply(driverControls.getForward());
    double strafe = strafeScale.apply(driverControls.getStrafe());
    double yaw = yawScale.apply(driverControls.getYaw());
    double vx = forward * Constants.DriveConstants.kMaxSpeedMetersPerSecond;
    double vy = strafe * Constants.DriveConstants.kMaxSpeedMetersPerSecond;
    double omega = yaw * Constants.DriveConstants.kMaxOmega;

    DRIVE.drive(vx, vy, omega);
  }

  @Override
  public void end(boolean interrupted) {
    DRIVE.drive(0, 0, 0);
  }
}
