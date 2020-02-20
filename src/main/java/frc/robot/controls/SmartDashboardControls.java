package frc.robot.controls;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.commands.shooter.ExecuteTuningCommand;
import frc.robot.commands.shooter.SaveTuningValuesCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartDashboardControls {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public SmartDashboardControls() {
    addTuningCommands();
  }

  public void addTuningCommands() {
    SmartDashboard.putNumber("Tuning/turretPos", 0);
    SmartDashboard.putNumber("Tuning/hoodPos", 0);
    SmartDashboard.putNumber("Tuning/shooterVel", 0);
    SmartDashboard.putData(
        "Tuning/saveNums",
        new SaveTuningValuesCommand(
            (int) SmartDashboard.getNumber("Tuning/turretPos", 0),
            (int) SmartDashboard.getNumber("Tuning/hoodPos", 0),
            (int) SmartDashboard.getNumber("Tuning/shooterVel", 0)));
    SmartDashboard.putData("Tuning/actuateTuning", new ExecuteTuningCommand());
  }
}
