package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.XLockCommand;
import frc.robot.commands.shooter.AimShooterCommand;
import frc.robot.commands.vision.StopVisionTrackingCommand;

public class VisionShootCommandGroup extends SequentialCommandGroup {
  public VisionShootCommandGroup() {
    addCommands(
        new XLockCommand(),
        new StopVisionTrackingCommand(),
        new AimShooterCommand(),
        new ArmedShootSequenceCommand());
  }
}
