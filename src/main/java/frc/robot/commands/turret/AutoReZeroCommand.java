package frc.robot.commands.turret;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.TurretSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoReZeroCommand extends CommandBase {
  private TurretSubsystem TURRET = RobotContainer.TURRET;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public AutoReZeroCommand() {
    addRequirements(TURRET);
  }

  @Override
  public void initialize() {
    logger.info("Turret Talon Reset - Auto Re-Zeroing");
    if (TURRET.getStringPot() < Constants.TurretConstants.kMinStringPotZero) {
      TURRET.turretOpenLoop(0.2);
    } else if (TURRET.getStringPot() > Constants.TurretConstants.kMaxStringPotZero) {
      TURRET.turretOpenLoop(-0.2);
    }
  }

  @Override
  public boolean isFinished() {
    return (TURRET.getStringPot() <= Constants.TurretConstants.kMaxStringPotZero
        || TURRET.getStringPot() >= Constants.TurretConstants.kMinStringPotZero);
  }

  @Override
  public void end(boolean interrupted) {
    TURRET.turretOpenLoop(0.0);
    TURRET.zeroTurret();
    TURRET.talonReset = false;
  }
}
