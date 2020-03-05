package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.commands.LogCommand;
import frc.robot.commands.intake.IntakeStopCommand;
import frc.robot.commands.magazine.StopMagazineCommand;

public class StopIntakeAndMagazineCommandGroup extends ParallelCommandGroup {
  public StopIntakeAndMagazineCommandGroup() {
    addCommands(new LogCommand("Begin Stop Intake and Magazine Command"), new IntakeStopCommand(), new StopMagazineCommand());
  }
}
