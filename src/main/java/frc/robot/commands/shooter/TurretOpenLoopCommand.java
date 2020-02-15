package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;

public class TurretOpenLoopCommand extends InstantCommand {
  public static ShooterSubsystem SHOOTER = RobotContainer.SHOOTER;
  private final double setpoint;

  public TurretOpenLoopCommand(double setpoint) {
    this.setpoint = setpoint;
  }

  @Override
  public void initialize() {
    SHOOTER.turretOpenLoop(setpoint);
  }
}
