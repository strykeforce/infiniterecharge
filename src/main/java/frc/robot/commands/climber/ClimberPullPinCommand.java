package frc.robot.commands.climber;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ClimberSubsystem;

public class ClimberPullPinCommand extends InstantCommand {
  private ClimberSubsystem climberSubsystem = RobotContainer.CLIMBER;
  private boolean doPull;

  public ClimberPullPinCommand(boolean doPull) {
    addRequirements(climberSubsystem);
    this.doPull = doPull;
  }

  @Override
  public void initialize() {
    climberSubsystem.pullPin(doPull);
  }
}
