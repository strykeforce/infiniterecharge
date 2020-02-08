package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.commands.intake.IntakeRunCommand;
import frc.robot.commands.magazine.RunMagazineCommand;

public class ReverseIntakeAndMagazineCommandGroup extends ParallelCommandGroup {
  public ReverseIntakeAndMagazineCommandGroup() {
    addCommands(new IntakeRunCommand(0.25), new RunMagazineCommand(-0.25));
  }
}
