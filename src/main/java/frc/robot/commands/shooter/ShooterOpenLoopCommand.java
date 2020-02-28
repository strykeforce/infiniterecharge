package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;

public class ShooterOpenLoopCommand extends InstantCommand {
  private ShooterSubsystem SHOOTER = RobotContainer.SHOOTER;

  private double output;

  public ShooterOpenLoopCommand(double output) {
    addRequirements(SHOOTER);
    this.output = output;
  }

  @Override
  public void initialize() {
    SHOOTER.runOpenLoop(output);
  }
}
