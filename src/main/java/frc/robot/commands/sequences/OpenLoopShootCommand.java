package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants.IntakeConstants;
import frc.robot.Constants.MagazineConstants;
import frc.robot.Constants.ShooterConstants;
import frc.robot.commands.intake.IntakeRunCommand;
import frc.robot.commands.magazine.RunMagazineCommand;
import frc.robot.commands.magazine.WaitForMagazineBeamCommand;
import frc.robot.commands.shooter.ShooterOpenLoopCommand;

public class OpenLoopShootCommand extends SequentialCommandGroup {
  public OpenLoopShootCommand() {
    addCommands(
        new RunMagazineCommand(MagazineConstants.kOpenloopArmReverse),
        new ShooterOpenLoopCommand(ShooterConstants.kOpenloopArmReverse),
        new WaitForMagazineBeamCommand(),
        new ShooterOpenLoopCommand(ShooterConstants.kOpenloopShoot),
        new edu.wpi.first.wpilibj2.command.WaitCommand(ShooterConstants.kArmTimeToAccelerate),
        new RunMagazineCommand(MagazineConstants.kOpenloopShoot),
        new WaitCommand(IntakeConstants.kShootDelayIntake),
        new IntakeRunCommand(IntakeConstants.kIntakeSpeed, IntakeConstants.kSquidSpeed));
  }
}
