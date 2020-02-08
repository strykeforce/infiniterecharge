package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.WaitCommand;
import frc.robot.commands.intake.IntakeRunCommand;
import frc.robot.commands.magazine.RunMagazineCommand;
import frc.robot.commands.shooter.RunShooterCommand;

public class OpenLoopShootCommand extends SequentialCommandGroup {
  public OpenLoopShootCommand() {
    addCommands(
        new RunMagazineCommand(-0.1),
        new WaitCommand(500),
        new RunShooterCommand(0.5),
        new WaitCommand(1500),
        new RunMagazineCommand(1.0),
        new WaitCommand(500),
        new IntakeRunCommand(-0.5));
  }
}
