package frc.robot.commands.auto;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants.MagazineConstants;
import frc.robot.commands.magazine.RunMagazineCommand;
import frc.robot.commands.magazine.WaitForMagazineBeamCommand;
import frc.robot.commands.shooter.ArmShooterCommand;
import frc.robot.commands.turret.ArmTrackingCommand;
import frc.robot.commands.turret.SeekTargetCommand;
import frc.robot.commands.vision.SetCameraStateCommand;

public class AutoArmCommandGroup extends SequentialCommandGroup {
  public AutoArmCommandGroup() {
    addCommands(
        // put in track start
        new SetCameraStateCommand(true),
        new RunMagazineCommand(MagazineConstants.kOpenloopArmReverse),
        // new SafeShooterReverseCommand(),
        new ParallelCommandGroup(new WaitForMagazineBeamCommand(), new SeekTargetCommand()),
        new ParallelDeadlineGroup(new ArmShooterCommand(), new ArmTrackingCommand()));
  }
}
