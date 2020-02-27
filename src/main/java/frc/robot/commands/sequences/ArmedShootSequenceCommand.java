package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.magazine.MagazineSmartFeedCommand;

public class ArmedShootSequenceCommand extends SequentialCommandGroup {

  public ArmedShootSequenceCommand() {
    addCommands(new VisionAimCommandGroup(), new MagazineSmartFeedCommand());
  }
}
