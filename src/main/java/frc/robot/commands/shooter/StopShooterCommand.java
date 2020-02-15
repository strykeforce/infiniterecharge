package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;

public class StopShooterCommand extends InstantCommand {

  private ShooterSubsystem shooterSubsystem = RobotContainer.SHOOTER;

  public StopShooterCommand() {
    addRequirements(shooterSubsystem);
  }

  @Override
  public void initialize() {
    shooterSubsystem.stop();
    shooterSubsystem.isArmed = false;
  }
}
