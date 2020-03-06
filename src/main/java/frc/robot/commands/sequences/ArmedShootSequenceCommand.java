package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.magazine.MagazineSmartFeedCommand;
import frc.robot.commands.turret.ShootTrackingCommand;
import frc.robot.commands.vision.WaitForTargetCommand;

public class ArmedShootSequenceCommand extends ParallelCommandGroup {

  public ArmedShootSequenceCommand() {
    addCommands(
        new ShootTrackingCommand(),
        new SequentialCommandGroup(
            new WaitForTargetCommand(),
            new VisionAimCommandGroup(),
            new MagazineSmartFeedCommand()));
  }
}
