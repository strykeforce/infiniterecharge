package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;

public class ArmShooterCommand extends CommandBase {
  private static final ShooterSubsystem SHOOTER = RobotContainer.SHOOTER;

  private boolean isArmed;

  public ArmShooterCommand() {
    //    addRequirements(shooterSubsystem); weird
  }

  @Override
  public void initialize() {
    isArmed = SHOOTER.isArmed;
    if (!SHOOTER.isArmed) {
      SHOOTER.run(Constants.ShooterConstants.kArmSpeed);
    }
    System.out.println("Arming!!!");
  }

  @Override
  public boolean isFinished() {
    return isArmed || SHOOTER.atTargetSpeed();
  }

  @Override
  public void end(boolean interrupted) {
    SHOOTER.isArmed = true;
    System.out.println("Armed!!!");
  }
}
