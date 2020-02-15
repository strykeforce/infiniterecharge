package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants;
import frc.robot.commands.WaitCommand;
import frc.robot.commands.intake.IntakeRunCommand;
import frc.robot.commands.magazine.RunMagazineCommand;
import frc.robot.commands.shooter.ArmShooterCommand;

public class ArmedShootSequenceCommand extends SequentialCommandGroup {

  public ArmedShootSequenceCommand() {
    addCommands(
        new ArmShooterCommand(),
        new RunMagazineCommand(Constants.MagazineConstants.kOpenloopShoot),
        new WaitCommand(Constants.IntakeConstants.kShootDelayIntake),
        new IntakeRunCommand(Constants.IntakeConstants.kIntakeSpeed));
  }
}
