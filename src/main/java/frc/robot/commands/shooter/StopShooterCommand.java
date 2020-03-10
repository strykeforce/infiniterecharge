package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;

public class StopShooterCommand extends InstantCommand {

  private ShooterSubsystem SHOOTER = RobotContainer.SHOOTER;

  public StopShooterCommand() {
    addRequirements(SHOOTER);
  }

  @Override
  public void initialize() {
    SHOOTER.stop();
    SHOOTER.setArmedState(false);
  }
}
