package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.commands.LogCommand;
import frc.robot.commands.hood.HoodAimCommand;
import frc.robot.commands.shooter.ShooterAimCommand;
import frc.robot.commands.turret.TurretAimCommand;
import frc.robot.commands.vision.SetCameraStateCommand;

public class VisionAimCommandGroup extends ParallelCommandGroup {
  VisionAimCommandGroup() {
    addCommands(
        new LogCommand("Begin Vision Aim Command Group"),
        new SetCameraStateCommand(true),
        new TurretAimCommand(),
        new ShooterAimCommand(),
        new HoodAimCommand());
  }
}
