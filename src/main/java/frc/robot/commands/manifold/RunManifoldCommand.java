package frc.robot.commands.manifold;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.MagazineSubsystem;

public class RunManifoldCommand extends InstantCommand {
  private MagazineSubsystem magazineSubsystem = RobotContainer.MANIFOLD;
  private double setSpeed;

  public RunManifoldCommand(double speed) {
    addRequirements(magazineSubsystem);
    this.setSpeed = speed;
  }

  @Override
  public void initialize() {
    magazineSubsystem.runTalon(setSpeed);
  }
}
