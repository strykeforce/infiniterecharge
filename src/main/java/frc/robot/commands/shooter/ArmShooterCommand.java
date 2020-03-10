package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ShooterSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArmShooterCommand extends CommandBase {
  private static final ShooterSubsystem SHOOTER = RobotContainer.SHOOTER;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public ArmShooterCommand() {
    //    addRequirements(shooterSubsystem); weird
  }

  @Override
  public void initialize() {
    if (!SHOOTER.isArmed()) {
      SHOOTER.run(Constants.ShooterConstants.kArmSpeed);
    }
    logger.info("Arming!!!");
  }

  @Override
  public boolean isFinished() {
    return SHOOTER.isArmed() || SHOOTER.atTargetSpeed();
  }

  @Override
  public void end(boolean interrupted) {
    SHOOTER.setArmedState(true);
    System.out.println("Armed!!!");
  }
}
