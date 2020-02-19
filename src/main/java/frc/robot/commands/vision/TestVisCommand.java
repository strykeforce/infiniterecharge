package frc.robot.commands.vision;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class TestVisCommand extends SequentialCommandGroup {
  public TestVisCommand() {
    addCommands( // new TurretRangeCommand(260),
        // new ShooterTrackingCommand());
        new TempVisInstant());
  }
}
