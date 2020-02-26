package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants.MagazineConstants;
import frc.robot.commands.magazine.RunMagazineCommand;
import frc.robot.commands.magazine.WaitForMagazineBeamCommand;
import frc.robot.commands.shooter.ArmShooterCommand;
import frc.robot.commands.turret.ShooterTrackingCommand;
import frc.robot.commands.vision.SetLightStateCommand;

public class ArmSequenceCommand extends SequentialCommandGroup {
  public ArmSequenceCommand() {
    addCommands(
        // put in track start
        new SetLightStateCommand(true),
        new RunMagazineCommand(MagazineConstants.kOpenloopArmReverse),
        // new SafeShooterReverseCommand(),
        new WaitForMagazineBeamCommand(),
        new ParallelCommandGroup(new ShooterTrackingCommand(), new ArmShooterCommand()));
  }
}
