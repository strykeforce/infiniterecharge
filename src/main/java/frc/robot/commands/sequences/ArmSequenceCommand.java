package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants.MagazineConstants;
import frc.robot.Constants.ShooterConstants;
import frc.robot.commands.WaitCommand;
import frc.robot.commands.magazine.RunMagazineCommand;
import frc.robot.commands.shooter.ArmShooterCommand;
import frc.robot.commands.shooter.RunShooterCommand;
import frc.robot.commands.shooter.SeekTargetCommand;

public class ArmSequenceCommand extends SequentialCommandGroup {
  public ArmSequenceCommand() {
    addCommands(
        new RunMagazineCommand(MagazineConstants.kOpenloopArmReverse),
        new RunShooterCommand(ShooterConstants.kOpenloopArmReverse),
        new WaitCommand(MagazineConstants.kArmTimeToShooterOn),
        new ParallelCommandGroup(new ArmShooterCommand(), new SeekTargetCommand()));
  }
}