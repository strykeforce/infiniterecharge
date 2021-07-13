package frc.robot.commands.auto;

import edu.wpi.first.wpilibj2.command.*;
import frc.robot.commands.drive.OffsetGyroCommand;
import frc.robot.commands.hood.HoodPositionCommand;
import frc.robot.commands.sequences.ArmedShootSequenceCommand;
import frc.robot.commands.sequences.AutoIntakeCmdGroup;
import frc.robot.commands.shooter.ShooterClosedLoopStopCommand;
import frc.robot.commands.turret.TurretPositionCommand;
import frc.robot.commands.vision.StopVisionTrackingCommand;

public class TrenchAutoCommandGroup extends SequentialCommandGroup {
  public TrenchAutoCommandGroup(
      double gyroOffset,
      double intakeWait,
      double shootWait,
      String ballFetchPath,
      String driveShootPath) {
    addCommands(
        new OffsetGyroCommand(gyroOffset),//FIXME: Move to end
        new AutoArmCommandGroup(),
        new ParallelDeadlineGroup(new WaitCommand(shootWait), new ArmedShootSequenceCommand()),
        new ParallelDeadlineGroup(
            new SequentialCommandGroup(
                new PathDriveCommand(ballFetchPath), new WaitCommand(intakeWait)),
            new AutoIntakeCmdGroup(),
            new ShooterClosedLoopStopCommand(),
            new StopVisionTrackingCommand(),
            new TurretPositionCommand(0),
            new HoodPositionCommand(500)),
        new ParallelCommandGroup(new PathDriveCommand(driveShootPath), new AutoArmCommandGroup()),
        new ArmedShootSequenceCommand());
  }
}
