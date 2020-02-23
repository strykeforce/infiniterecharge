package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants.MagazineConstants;
import frc.robot.commands.magazine.RunMagazineCommand;
import frc.robot.commands.magazine.WaitForMagazineBeamCommand;
import frc.robot.commands.shooter.ArmShooterCommand;
import frc.robot.commands.shooter.SafeShooterReverseCommand;
import frc.robot.commands.shooter.ShooterTrackingCommand;

public class ArmSequenceCommand extends SequentialCommandGroup {
  public ArmSequenceCommand() {
    addCommands(
        // put in track start
        new RunMagazineCommand(MagazineConstants.kOpenloopArmReverse),
        new SafeShooterReverseCommand(),
        new WaitForMagazineBeamCommand(),
        new ParallelCommandGroup(new ShooterTrackingCommand(), new ArmShooterCommand()));
  }
}
