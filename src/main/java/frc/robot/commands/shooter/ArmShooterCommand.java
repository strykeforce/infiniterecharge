package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;

public class ArmShooterCommand extends CommandBase {
  private static final ShooterSubsystem shooterSubsystem = RobotContainer.SHOOTER;

  private boolean isArmed;

  public ArmShooterCommand() {
    addRequirements(shooterSubsystem);
  }

  @Override
  public void initialize() {
    isArmed = shooterSubsystem.isArmed;
    if (!shooterSubsystem.isArmed) {
      shooterSubsystem.run(Constants.ShooterConstants.kArmSpeed);
    }
    System.out.println("Arming!!!");
  }

  @Override
  public boolean isFinished() {
    return isArmed || shooterSubsystem.atTargetSpeed();
  }

  @Override
  public void end(boolean interrupted) {
    shooterSubsystem.isArmed = true;
    System.out.println("Armed!!!");
  }
}
