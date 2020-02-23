package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.HoodSubsystem;

public class HoodPositionCommand extends CommandBase {
  private static final HoodSubsystem HOOD = RobotContainer.HOOD;
  private int position;

  public HoodPositionCommand(int position) {
    addRequirements(HOOD);
    this.position = position;
  }

  @Override
  public void initialize() {
    HOOD.setHoodAngle(position);
  }

  @Override
  public boolean isFinished() {
    return HOOD.hoodAtTarget();
  }
}
