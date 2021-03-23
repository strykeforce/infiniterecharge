package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants.MagazineConstants;
import frc.robot.commands.LogCommand;
import frc.robot.commands.magazine.RunMagazineCommand;
import frc.robot.commands.magazine.WaitForMagazineBeamCommand;
import frc.robot.commands.shooter.ArmShooterCommand;
import frc.robot.commands.turret.SeekTargetCommand;
import frc.robot.commands.turret.TurretTrackingCommand;
import frc.robot.commands.vision.SetCameraStateCommand;

public class ArmSequenceCommand extends SequentialCommandGroup {
  public ArmSequenceCommand() {
    addCommands(
        // put in track start
        new LogCommand("Begin Arm Sequence"),
        new SetCameraStateCommand(true),
        //        new IntakeRunCommand(0, Constants.IntakeConstants.kIntakeSpeed),
        new RunMagazineCommand(MagazineConstants.kOpenloopArmReverse),
        // new SafeShooterReverseCommand(),
        new ParallelCommandGroup(new WaitForMagazineBeamCommand(), new SeekTargetCommand()),
        new ParallelCommandGroup(new TurretTrackingCommand(true), new ArmShooterCommand()),
        new LogCommand("End Arm Sequence"));
  }
}
