package frc.robot.controls;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.commands.HealthCheckCommand;
import frc.robot.commands.drive.ZeroDriveWheelsCommand;
import frc.robot.commands.hood.HoodPositionCommand;
import frc.robot.commands.intake.IntakeRunCommand;
import frc.robot.commands.magazine.MagazineSmartFeedCommand;
import frc.robot.commands.magazine.RunMagazineCommand;
import frc.robot.commands.sequences.*;
import frc.robot.commands.shooter.ActuateTuningCommand;
import frc.robot.commands.vision.SetCameraStateCommand;
import frc.robot.commands.vision.UpdateWidthsCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartDashboardControls {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public SmartDashboardControls() {

    if (!RobotContainer.isEvent) {
      addTuningCommands();
      addPitCommands();
    }
    addMatchCommands();
  }

  public void addTuningCommands() {
    SmartDashboard.putNumber("Tuning/turretPos", 0);
    SmartDashboard.putNumber("Tuning/hoodPos", 0);
    SmartDashboard.putNumber("Tuning/shooterVel", 0);

    SmartDashboard.putNumber("Tuning/rawPixWidth", 0);
    SmartDashboard.putNumber("Tuning/correctedPixWidth", 0);
    SmartDashboard.putData("Tuning/updateWidths", new UpdateWidthsCommand());

    SmartDashboard.putData("Tuning/Fire", new TuneFireCommandGroup());

    SmartDashboard.putData("Tuning/setPositions", new ActuateTuningCommand());

    SmartDashboard.putData("Tuning/Intake", new AutoIntakeCmdGroup());
    SmartDashboard.putData("Tuning/StartVolley", new MagazineSmartFeedCommand());
    SmartDashboard.putData("Tuning/StopFiring", new StopShootCommand());
  }

  public void addPitCommands() {
    SmartDashboard.putData("Pit/HealthCheck", new HealthCheckCommand());
    SmartDashboard.putData("Pit/Zero Hood", new HoodPositionCommand(0));
    SmartDashboard.putData(
        "Pit/Offset Zero Hood", new HoodPositionCommand(Constants.HoodConstants.kOffsetZeroTicks));
    SmartDashboard.putData("Pit/Zero Wheels", new ZeroDriveWheelsCommand());
    SmartDashboard.putData(
        "Pit/Eject Balls",
        new ParallelCommandGroup(
            new IntakeRunCommand(
                -Constants.IntakeConstants.kIntakeSpeed, -Constants.IntakeConstants.kSquidSpeed),
            new RunMagazineCommand(Constants.MagazineConstants.kOpenloopReverse)));
  }

  public void addMatchCommands() {
    SmartDashboard.putBoolean("Match/Magazine Full", false);
    SmartDashboard.putBoolean("Match/Ball Chambered", false);
    SmartDashboard.putBoolean("Match/Intake Stalled", false);
    SmartDashboard.putBoolean("Match/Locked On", false);
    SmartDashboard.putData("Match/CameraOn", new SetCameraStateCommand(true));
    SmartDashboard.putData("Match/CameraOff", new SetCameraStateCommand(false));
  }
}
