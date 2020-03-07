package frc.robot.commands.climber;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ClimberSubsystem;

public class FastClimbRatchetCommand extends InstantCommand {
  private ClimberSubsystem CLIMB = RobotContainer.CLIMBER;

  public FastClimbRatchetCommand() {
    addRequirements(CLIMB);
  }

  @Override
  public void initialize() {
    CLIMB.runOpenLoop(Constants.ClimberConstants.kFastDownOutput);
    CLIMB.engageRatchet(true);
  }
}
