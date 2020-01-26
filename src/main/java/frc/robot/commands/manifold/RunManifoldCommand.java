package frc.robot.commands.manifold;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ManifoldSubsystem;

public class RunManifoldCommand extends InstantCommand {
  private ManifoldSubsystem manifoldSubsystem = RobotContainer.MANIFOLD;
  private double setSpeed;

  public RunManifoldCommand(double speed) {
    addRequirements(manifoldSubsystem);
    this.setSpeed = speed;
  }

  @Override
  public void initialize() {
    manifoldSubsystem.runTalon(setSpeed);
  }
}
