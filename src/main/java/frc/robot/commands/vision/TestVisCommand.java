package frc.robot.commands.vision;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.shooter.ShooterTrackingCommand;
import frc.robot.commands.shooter.TurretRangeCommand;

public class TestVisCommand extends SequentialCommandGroup {
  public TestVisCommand() {
    addCommands(new TurretRangeCommand(260), new ShooterTrackingCommand());
  }
}
