package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.IntakeSubsystem;

public class IntakeRunCommand extends InstantCommand {
  public static final IntakeSubsystem INTAKE = RobotContainer.INTAKE;
  private final double intakeSetpoint;
  private final double squidsSetpoint;

  public IntakeRunCommand(double intakeSetpoint, double squidsSetpoint) {
    addRequirements(INTAKE);
    this.intakeSetpoint = intakeSetpoint;
    this.squidsSetpoint = squidsSetpoint;
  }

  @Override
  public void initialize() {

    INTAKE.runBoth(intakeSetpoint, squidsSetpoint);
  }
}
