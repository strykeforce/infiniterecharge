package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.IntakeSubsystem;

public class IntakeStopCommand extends InstantCommand {
  public static IntakeSubsystem INTAKE = RobotContainer.INTAKE;

  public IntakeStopCommand() {}

  @Override
  public void initialize() {
    INTAKE.stopIntake();
  }
}
