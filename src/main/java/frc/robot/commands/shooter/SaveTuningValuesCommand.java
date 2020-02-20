package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaveTuningValuesCommand extends InstantCommand {
  private ShooterSubsystem SHOOTER = RobotContainer.SHOOTER;
  public Logger logger = LoggerFactory.getLogger("Save Tuning Command");

  private int turretSetpoint;
  private int hoodSetpoint;
  private int shooterSpeed;

  public SaveTuningValuesCommand(int turretPos, int hoodPos, int shooterVel) {
    addRequirements(SHOOTER);

    turretSetpoint = turretPos;
    hoodSetpoint = hoodPos;
    shooterSpeed = shooterVel;
  }

  @Override
  public void initialize() {
    SHOOTER.setTuningSetpoints(turretSetpoint, hoodSetpoint, shooterSpeed);
  }
}
