package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;

public class ShooterVelocityCommand extends CommandBase {
  private ShooterSubsystem SHOOTER = RobotContainer.SHOOTER;

  private int velocity;

  public ShooterVelocityCommand(int velocity) {
    addRequirements(SHOOTER);
    this.velocity = velocity;
  }

  @Override
  public void initialize() {
    SHOOTER.run(velocity);
  }

  @Override
  public boolean isFinished() {
    return SHOOTER.atTargetSpeed();
  }
}
