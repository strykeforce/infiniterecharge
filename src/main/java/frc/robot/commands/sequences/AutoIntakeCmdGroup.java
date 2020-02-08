package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.commands.intake.IntakeAutoStopCommand;
import frc.robot.commands.magazine.LimitMagazineCommand;

public class AutoIntakeCmdGroup extends ParallelCommandGroup {
  public AutoIntakeCmdGroup() {
    addCommands(new LimitMagazineCommand(1), new IntakeAutoStopCommand());
  }
}
