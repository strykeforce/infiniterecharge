package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants;
import frc.robot.commands.hood.HoodPositionCommand;
import frc.robot.commands.magazine.MagazineSmartFeedCommand;
import frc.robot.commands.magazine.RunMagazineSpeedCommand;
import frc.robot.commands.magazine.WaitForMagazineBeamCommand;
import frc.robot.commands.shooter.ShooterVelocityCommand;
import frc.robot.commands.turret.TurretPositionCommand;

public class BatterShotCommandGroup extends SequentialCommandGroup {
  public BatterShotCommandGroup() {
    addCommands(
        new ParallelCommandGroup(
            new ShooterVelocityCommand(Constants.ShooterConstants.kBatterShotVelocity),
            new TurretPositionCommand(Constants.TurretConstants.kBatterShotTicks),
            new HoodPositionCommand(Constants.HoodConstants.kBatterShotTicks),
            new SequentialCommandGroup(
                new RunMagazineSpeedCommand(Constants.MagazineConstants.kClosedLoopShoot),
                new WaitForMagazineBeamCommand())),
        new MagazineSmartFeedCommand());
  }
}
