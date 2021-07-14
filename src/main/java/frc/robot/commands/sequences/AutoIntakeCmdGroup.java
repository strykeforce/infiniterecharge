package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.Constants;
import frc.robot.Constants.MagazineConstants;
import frc.robot.commands.LogCommand;
import frc.robot.commands.intake.IntakeRunCommand;
import frc.robot.commands.magazine.LimitMagazineCommand;

public class AutoIntakeCmdGroup extends ParallelCommandGroup {
  public AutoIntakeCmdGroup() {
    addCommands(
        new LogCommand("Begin Driver Controller - Auto Intake"),
        new LimitMagazineCommand(MagazineConstants.kOpenLoopLoad),
        new IntakeRunCommand(
            Constants.IntakeConstants.kIntakeSpeed, Constants.IntakeConstants.kSquidSpeed));
  }
}
