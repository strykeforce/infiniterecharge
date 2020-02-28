package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.commands.hood.HoodPositionCommand;
import frc.robot.commands.intake.IntakeStopCommand;
import frc.robot.commands.magazine.StopMagazineCommand;
import frc.robot.commands.shooter.ShooterClosedLoopStopCommand;
import frc.robot.commands.vision.StopVisionTrackingCommand;

public class StopShootCommand extends ParallelCommandGroup {
  public StopShootCommand() {
    addCommands(
        new IntakeStopCommand(),
        new StopMagazineCommand(),
        new ShooterClosedLoopStopCommand(),
        new StopVisionTrackingCommand(),
        new HoodPositionCommand(500));
  }
}
