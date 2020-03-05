package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.drive.QuickLockDriveCommand;
import frc.robot.commands.turret.TurretAimCommand;
import frc.robot.commands.vision.StopVisionTrackingCommand;

public class OneFrameShootCommandGroup extends SequentialCommandGroup {
  public OneFrameShootCommandGroup() {
    addCommands(
        new QuickLockDriveCommand(),
        new StopVisionTrackingCommand(),
        new TurretAimCommand(),
        new ArmedShootSequenceCommand());
  }
}
