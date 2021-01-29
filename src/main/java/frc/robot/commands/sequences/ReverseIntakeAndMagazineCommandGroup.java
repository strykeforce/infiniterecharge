package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.Constants.IntakeConstants;
import frc.robot.Constants.MagazineConstants;
import frc.robot.commands.LogCommand;
import frc.robot.commands.intake.IntakeRunCommand;
import frc.robot.commands.magazine.RunMagazineCommand;

public class ReverseIntakeAndMagazineCommandGroup extends ParallelCommandGroup {
  public ReverseIntakeAndMagazineCommandGroup() {
    addCommands(
        new LogCommand("Begin Reverse Intake and Magazine Command Group"),
        new IntakeRunCommand(IntakeConstants.kIntakeEjectSpeed, IntakeConstants.kSquidEjectSpeed),
        new RunMagazineCommand(MagazineConstants.kOpenloopReverse));
  }
}
