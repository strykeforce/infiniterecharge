package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.IntakeSubsystem;

public class IntakeRunCommand extends InstantCommand {
  public static IntakeSubsystem INTAKE = RobotContainer.INTAKE;
  private final double setpoint;

  public IntakeRunCommand(double setpoint) {
    this.setpoint = setpoint;
  }

  @Override
  public void initialize() {
    INTAKE.runSquids(setpoint);
  }
}
