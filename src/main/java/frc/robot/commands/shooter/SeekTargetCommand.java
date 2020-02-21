package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.TurretSubsystem;

public class SeekTargetCommand extends CommandBase {
  private static final TurretSubsystem turretSubsystem = RobotContainer.TURRET;

  public SeekTargetCommand() {}

  @Override
  public void initialize() {
    turretSubsystem.seekTarget();
  }

  @Override
  public void execute() {}

  @Override
  public boolean isFinished() {
    return turretSubsystem.turretAtTarget();
  }
}
