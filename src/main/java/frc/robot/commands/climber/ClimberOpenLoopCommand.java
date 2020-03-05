package frc.robot.commands.climber;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ClimberSubsystem;

public class ClimberOpenLoopCommand extends InstantCommand {
  public static ClimberSubsystem CLIMBER = RobotContainer.CLIMBER;
  private final double setpoint;

  public ClimberOpenLoopCommand(double setpoint) {
    addRequirements(CLIMBER);
    this.setpoint = setpoint;
  }

  @Override
  public void initialize() {
    CLIMBER.runOpenLoop(setpoint);
  }
}
