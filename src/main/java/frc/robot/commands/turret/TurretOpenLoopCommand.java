package frc.robot.commands.turret;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.TurretSubsystem;

public class TurretOpenLoopCommand extends InstantCommand {
  public static TurretSubsystem TURRET = RobotContainer.TURRET;
  private final double setpoint;

  public TurretOpenLoopCommand(double setpoint) {
    addRequirements(TURRET);
    this.setpoint = setpoint;
  }

  @Override
  public void initialize() {
    TURRET.turretOpenLoop(setpoint);
  }
}
