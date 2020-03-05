package frc.robot.commands.drive;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.controls.DriverControls;
import frc.robot.subsystems.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XLockCommand extends CommandBase {
  private DriveSubsystem driveSubsystem = RobotContainer.DRIVE;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private DriverControls controls;
  private boolean isDoneLocked;

  public XLockCommand() {
    addRequirements(driveSubsystem);
  }

  @Override
  public void initialize() {
    controls = RobotContainer.CONTROLS.getDriverControls();
    isDoneLocked = false;
    driveSubsystem.xLockSwerveDrive();
  }

  @Override
  public void execute() {
    if ((Math.abs(controls.getYaw()) >= Constants.DriveConstants.kDeadbandXLock)
        || (Math.abs(controls.getForward()) >= Constants.DriveConstants.kDeadbandXLock)
        || (Math.abs(controls.getStrafe()) >= Constants.DriveConstants.kDeadbandXLock)) {
      isDoneLocked = true;
    }
  }

  @Override
  public boolean isFinished() {
    return isDoneLocked;
  }

  @Override
  public void end(boolean interrupted) {
    logger.info("Interrupting X-Lock Command");
  }
}
