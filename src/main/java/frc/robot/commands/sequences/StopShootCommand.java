package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.commands.LogCommand;
import frc.robot.commands.hood.HoodPositionCommand;
import frc.robot.commands.intake.IntakeStopCommand;
import frc.robot.commands.magazine.StopMagazineCommand;
import frc.robot.commands.shooter.ShooterClosedLoopStopCommand;
import frc.robot.commands.turret.TurretAngleCommand;
import frc.robot.commands.vision.StopVisionTrackingCommand;

public class StopShootCommand extends ParallelCommandGroup {
  public StopShootCommand() {
    addCommands(
        new LogCommand("Begin Stop Shoot Sequence"),
        new IntakeStopCommand(),
        new StopMagazineCommand(),
        new ShooterClosedLoopStopCommand(),
        new StopVisionTrackingCommand(),
        new TurretAngleCommand(270),
        new HoodPositionCommand(600));
  }
}
