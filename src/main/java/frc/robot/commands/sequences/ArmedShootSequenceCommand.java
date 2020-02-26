package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.commands.magazine.MagazineSmartFeedCommand;
import frc.robot.commands.turret.ShooterTrackingCommand;

public class ArmedShootSequenceCommand extends ParallelCommandGroup {

  public ArmedShootSequenceCommand() {
    addCommands(new ShooterTrackingCommand(), new MagazineSmartFeedCommand());
  }
}
