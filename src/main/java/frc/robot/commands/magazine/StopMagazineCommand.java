package frc.robot.commands.magazine;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.MagazineSubsystem;

public class StopMagazineCommand extends InstantCommand {
  private MagazineSubsystem magazineSubsystem = RobotContainer.MAGAZINE;

  public StopMagazineCommand() {
    addRequirements(magazineSubsystem);
  }

  @Override
  public void initialize() {
    magazineSubsystem.stopTalon();
  }
}
