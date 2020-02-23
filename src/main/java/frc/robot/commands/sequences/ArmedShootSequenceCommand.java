package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants;
import frc.robot.commands.WaitCommand;
import frc.robot.commands.intake.IntakeRunCommand;
import frc.robot.commands.magazine.RunMagazineCommand;
import frc.robot.commands.turret.ShooterTrackingCommand;

public class ArmedShootSequenceCommand extends ParallelCommandGroup {

  public ArmedShootSequenceCommand() {
    addCommands(
        new ShooterTrackingCommand(),
        new SequentialCommandGroup(
            new RunMagazineCommand(Constants.MagazineConstants.kOpenloopShoot),
            new WaitCommand(Constants.IntakeConstants.kShootDelayIntake),
            new IntakeRunCommand(Constants.IntakeConstants.kIntakeSpeed)));
  }
}
