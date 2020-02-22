package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;

public class RunShooterCommand extends InstantCommand {
  private ShooterSubsystem SHOOTER = RobotContainer.SHOOTER;

  private double wheelSpeed;

  public RunShooterCommand(double speed) {
    addRequirements(SHOOTER);
    wheelSpeed = speed;
  }

  @Override
  public void initialize() {
    SHOOTER.runOpenLoop(wheelSpeed);
  }
}
