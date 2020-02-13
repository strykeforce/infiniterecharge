package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;

public class RunShooterCommand extends InstantCommand {

  private ShooterSubsystem shooterSubsystem = RobotContainer.SHOOTER;
  private double wheelSpeed;

  public RunShooterCommand(double speed) {
    addRequirements(shooterSubsystem);
    wheelSpeed = speed;
  }

  @Override
  public void initialize() {
    shooterSubsystem.runOpenLoop(wheelSpeed);
  }
}
