package frc.robot.commands.auto;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.LogCommand;
import frc.robot.commands.drive.BruteRotateCommand;

public class BounceCommandGroup extends SequentialCommandGroup {
  public BounceCommandGroup() {
    addCommands(
        new LogCommand("Begin Bounce Path Group"),
        new PathDriveCommand("Bounce1", 90),
        new BruteRotateCommand(-1, 300));
  }
}
