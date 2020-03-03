package frc.robot.commands.auto;

import edu.wpi.first.wpilibj2.command.*;
import frc.robot.commands.OffsetGyroCommand;
import frc.robot.commands.sequences.ArmedShootSequenceCommand;
import frc.robot.commands.sequences.AutoIntakeCmdGroup;
import frc.robot.commands.sequences.StopShootCommand;

public class TrenchAutoCommandGroup extends SequentialCommandGroup {
  public TrenchAutoCommandGroup(
      double gyroOffset,
      double intakeWait,
      double shootWait,
      String ballFetchPath,
      String driveShootPath) {
    addCommands(
        new OffsetGyroCommand(gyroOffset),
        new AutoArmCommandGroup(),
        new ParallelDeadlineGroup(new WaitCommand(shootWait), new ArmedShootSequenceCommand()),
        new StopShootCommand(),
        new ParallelRaceGroup(
            new SequentialCommandGroup(
                new PathDriveCommand(ballFetchPath), new WaitCommand(intakeWait)),
            new AutoIntakeCmdGroup()),
        new PathDriveCommand(driveShootPath),
        new AutoArmCommandGroup(),
        new ArmedShootSequenceCommand());
  }
}
