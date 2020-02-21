package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants;
import frc.robot.commands.intake.IntakeRunCommand;
import frc.robot.commands.magazine.RunMagazineCommand;
import frc.robot.commands.shooter.ActuateTuningCommand;

public class TuneFireCommandGroup extends SequentialCommandGroup {
  public TuneFireCommandGroup() {
    addCommands(
        new IntakeRunCommand(Constants.IntakeConstants.kIntakeSpeed),
        new ActuateTuningCommand(),
        new RunMagazineCommand(Constants.MagazineConstants.kOpenloopShoot));
  }
}
