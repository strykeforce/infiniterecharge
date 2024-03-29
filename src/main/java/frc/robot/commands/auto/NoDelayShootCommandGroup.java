package frc.robot.commands.auto;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.commands.sequences.ArmedShootSequenceCommand;
import frc.robot.commands.sequences.StopShootCommand;

public class NoDelayShootCommandGroup extends SequentialCommandGroup {

  public NoDelayShootCommandGroup(double gyroOffset, double waitSeconds) {
    addCommands(
        new AutoArmCommandGroup(),
        new ParallelDeadlineGroup(new WaitCommand(waitSeconds), new ArmedShootSequenceCommand()),
        new ParallelCommandGroup(new StopShootCommand(), new PathDriveCommand("BackupPath")));
  }
}
