package frc.robot.commands.manifold;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ManifoldSubsystem;

public class StopManifoldCommand extends InstantCommand {
  private ManifoldSubsystem manifoldSubsystem = RobotContainer.MANIFOLD;

  public StopManifoldCommand() {
    addRequirements(manifoldSubsystem);
  }

  @Override
  public void initialize() {
    manifoldSubsystem.stopTalon();
  }
}
