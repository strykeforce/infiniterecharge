package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants;
import frc.robot.commands.LogCommand;
import frc.robot.commands.hood.HoodPositionCommand;
import frc.robot.commands.magazine.MagazineSmartFeedCommand;
import frc.robot.commands.magazine.RunMagazineCommand;
import frc.robot.commands.magazine.WaitForMagazineBeamCommand;
import frc.robot.commands.shooter.ShooterVelocityCommand;
import frc.robot.commands.turret.TurretPositionCommand;

public class ZoneFourShotCmdGrp extends SequentialCommandGroup {
  public ZoneFourShotCmdGrp() {
    addCommands(
        new ParallelCommandGroup(
            new LogCommand("Begin Batter Shot Sequence"),
            new ShooterVelocityCommand(Constants.ShooterConstants.kLongShotVelocity),
            new TurretPositionCommand(Constants.TurretConstants.kLongShotTicks),
            new HoodPositionCommand(Constants.HoodConstants.kLongShotTicks),
            new SequentialCommandGroup(
                new RunMagazineCommand(Constants.MagazineConstants.kOpenloopArmReverse),
                new WaitForMagazineBeamCommand())),
        new MagazineSmartFeedCommand(),
        new LogCommand("End Batter Shot Sequence"));
  }
}
