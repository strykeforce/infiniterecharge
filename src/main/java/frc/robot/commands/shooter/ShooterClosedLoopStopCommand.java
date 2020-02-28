package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;

public class ShooterClosedLoopStopCommand extends InstantCommand {
  private ShooterSubsystem SHOOTER = RobotContainer.SHOOTER;

  public ShooterClosedLoopStopCommand() {
    addRequirements(SHOOTER);
  }

  @Override
  public void initialize() {
    SHOOTER.run(0);
    SHOOTER.isArmed = false;
  }
}
