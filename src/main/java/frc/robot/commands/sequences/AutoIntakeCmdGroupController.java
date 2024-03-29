package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.Constants;
import frc.robot.Constants.MagazineConstants;
import frc.robot.RobotContainer;
import frc.robot.commands.LogCommand;
import frc.robot.commands.intake.IntakeAutoStopCommandController;
import frc.robot.commands.magazine.LimitMagazineCommand;
import frc.robot.commands.turret.TurretAngleCommand;
import frc.robot.subsystems.IntakeSubsystem;

public class AutoIntakeCmdGroupController extends ParallelCommandGroup {
  public IntakeSubsystem INTAKE = RobotContainer.INTAKE;

  public AutoIntakeCmdGroupController() {
    addCommands(
        new LogCommand("Game Controller: Auto Intake Command Begin"),
        new LimitMagazineCommand(MagazineConstants.kOpenLoopLoad),
        new IntakeAutoStopCommandController(),
        new TurretAngleCommand(Constants.TurretConstants.loadAngle));
  }
}
