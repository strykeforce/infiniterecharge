package frc.robot.controls;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.commands.sequences.*;
import frc.robot.commands.shooter.ActuateTuningCommand;
import frc.robot.commands.vision.UpdateWidthsCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartDashboardControls {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public SmartDashboardControls() {
    addTuningCommands();
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
    SmartDashboard.putData("Tuning/StartVolley", new ArmedShootSequenceCommand());
    SmartDashboard.putData("Tuning/StopFiring", new StopShootCommand());
  }

  public void addMatchCommands() {
    SmartDashboard.putBoolean("Match/Magazine Full", false);
    SmartDashboard.putBoolean("Match/Ball Chambered", false);
    SmartDashboard.putBoolean("Match/Intake Stalled", false);
    SmartDashboard.putBoolean("Match/Locked On", false);
  }
}
