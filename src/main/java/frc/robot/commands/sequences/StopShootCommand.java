package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.commands.intake.IntakeStopCommand;
import frc.robot.commands.magazine.StopMagazineCommand;
import frc.robot.commands.shooter.StopShooterCommand;

public class StopShootCommand extends ParallelCommandGroup {
  public StopShootCommand() {
    addCommands(new IntakeStopCommand(), new StopMagazineCommand(), new StopShooterCommand());
  }
}
