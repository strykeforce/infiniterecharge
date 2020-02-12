package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.controls.DriverControls;
import frc.robot.subsystems.DriveSubsystem;

public class XLockCommand extends CommandBase {
  private DriveSubsystem driveSubsystem = RobotContainer.DRIVE;
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
}
