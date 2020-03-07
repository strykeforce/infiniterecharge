package frc.robot.commands.turret;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.TurretSubsystem;

public class SeekTargetCommand extends CommandBase {
  private static final TurretSubsystem TURRET = RobotContainer.TURRET;

  public SeekTargetCommand() {
    addRequirements(TURRET);
  }

  @Override
  public void initialize() {
    TURRET.seekTarget(0);
  }

  @Override
  public void execute() {}

  @Override
  public boolean isFinished() {
    return TURRET.turretAtTarget();
  }
}
