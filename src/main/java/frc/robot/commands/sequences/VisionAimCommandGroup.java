package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.commands.hood.HoodAimCommand;
import frc.robot.commands.shooter.ShooterAimCommand;
import frc.robot.commands.turret.TurretAimCommand;
import frc.robot.commands.vision.SetLightStateCommand;

public class VisionAimCommandGroup extends ParallelCommandGroup {
  VisionAimCommandGroup() {
    addCommands(
        new SetLightStateCommand(true),
        new TurretAimCommand(),
        new ShooterAimCommand(),
        new HoodAimCommand());
  }
}
