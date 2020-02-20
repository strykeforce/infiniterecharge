package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants.MagazineConstants;
import frc.robot.Constants.ShooterConstants;
import frc.robot.commands.magazine.RunMagazineCommand;
import frc.robot.commands.magazine.WaitForMagazineBeamCommand;
import frc.robot.commands.shooter.ArmShooterCommand;
import frc.robot.commands.shooter.RunShooterCommand;
import frc.robot.commands.shooter.SeekTargetCommand;

public class ArmSequenceCommand extends SequentialCommandGroup {
  public ArmSequenceCommand() {
    addCommands(
        // put in track start
        new RunMagazineCommand(MagazineConstants.kOpenloopArmReverse),
        new RunShooterCommand(ShooterConstants.kOpenloopArmReverse),
        new WaitForMagazineBeamCommand(),
        new ArmShooterCommand(),
        new SeekTargetCommand());
  }
}
