package frc.robot.commands.climber;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ClimberSubsystem;

public class ClimberExtendCommand extends InstantCommand {
  private double setpoint;
  private ClimberSubsystem climberSubsystem = RobotContainer.CLIMBER;

  public ClimberExtendCommand(double setpoint) {
    addRequirements(climberSubsystem);

    this.setpoint = setpoint;
  }

  @Override
  public void initialize() {
    climberSubsystem.pullPin(true);
    climberSubsystem.engageRatchet(false);
    climberSubsystem.runOpenLoop(setpoint);
  }
}
