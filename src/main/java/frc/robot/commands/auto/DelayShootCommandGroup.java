package frc.robot.commands.auto;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.commands.drive.OffsetGyroCommand;
import frc.robot.commands.sequences.ArmedShootSequenceCommand;
import frc.robot.commands.sequences.StopShootCommand;

public class DelayShootCommandGroup extends SequentialCommandGroup {

  public DelayShootCommandGroup(
      double gyroOffset, double initDelay, double shootWait, double targetYaw) {
    addCommands(
        new OffsetGyroCommand(gyroOffset),
        new WaitCommand(initDelay),
        new AutoArmCommandGroup(),
        new ParallelDeadlineGroup(new WaitCommand(shootWait), new ArmedShootSequenceCommand()),
        new ParallelCommandGroup(
            new StopShootCommand(), new PathDriveCommand("BackupPath", targetYaw)));
  }
}
