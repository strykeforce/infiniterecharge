package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.LogCommand;
import frc.robot.commands.magazine.MagazineSmartFeedCommand;
import frc.robot.commands.turret.TurretTrackingCommand;
import frc.robot.commands.vision.WaitForTargetCommand;

public class ArmedShootSequenceCommand extends ParallelCommandGroup {

  public ArmedShootSequenceCommand() {
    addCommands(
        new LogCommand("Beginning shoot sequence"),
        new TurretTrackingCommand(false),
        new SequentialCommandGroup(
            new WaitForTargetCommand(),
            new VisionAimCommandGroup(),
            new MagazineSmartFeedCommand()));
  }
}
