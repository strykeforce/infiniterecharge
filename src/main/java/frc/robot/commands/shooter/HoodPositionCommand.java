package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.HoodSubsystem;

public class HoodPositionCommand extends CommandBase {
  private static final HoodSubsystem hood = RobotContainer.HOOD;
  private int position;

  public HoodPositionCommand(int position) {
    this.position = position;
  }

  @Override
  public void initialize() {
    hood.setHoodAngle(position);
  }

  @Override
  public boolean isFinished() {
    return hood.hoodAtTarget();
  }
}
