package frc.robot.commands.manifold;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.MagazineSubsystem;

public class StopManifoldCommand extends InstantCommand {
  private MagazineSubsystem magazineSubsystem = RobotContainer.MANIFOLD;

  public StopManifoldCommand() {
    addRequirements(magazineSubsystem);
  }

  @Override
  public void initialize() {
    magazineSubsystem.stopTalon();
  }
}
