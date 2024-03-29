package frc.robot.commands.turret;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.controls.DriverControls;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.TurretSubsystem;
import frc.robot.subsystems.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TurretTrackingCommand extends CommandBase {
  private static final TurretSubsystem TURRET = RobotContainer.TURRET;
  private static final VisionSubsystem VISION = RobotContainer.VISION;
  private static final ShooterSubsystem SHOOTER = RobotContainer.SHOOTER;
  private static DriverControls DRIVER_CONTROLS;

  private double strafeFactor = 5.45; // theoretical 7.45
  private TrackingState state;
  private int attemptNum;
  private final int ATTEMPT_LIMIT = 2;
  private boolean isArmed;
  private boolean lost;
  private boolean doReseek;

  public Logger logger = LoggerFactory.getLogger("Turret Track Command");

  public TurretTrackingCommand(boolean doReseek) {
    addRequirements(TURRET);
    this.doReseek = doReseek;
  }

  @Override
  public void initialize() {
    isArmed = SHOOTER.isArmed();
    if (isArmed) {
      state = TrackingState.HAS_TARGET;
    } else state = TrackingState.SEEK_LEFT;
    attemptNum = 1;
    VISION.setTrackingEnabled(true);
    DRIVER_CONTROLS = RobotContainer.CONTROLS.getDriverControls();
    SmartDashboard.putBoolean("Match/Locked On", false);
  }

  @Override
  public void execute() {
    // stop looking after two sweeps
    if (attemptNum > ATTEMPT_LIMIT) {
      VISION.setTrackingEnabled(false);
      return;
    }

    switch (state) {
      case SEEK_LEFT:

        // switch to tracking if target is detected
        if (VISION.isTargetValid()) {
          state = TrackingState.HAS_TARGET;
          logger.info("Target acquired");
          break;
        }

        // wait to reach the left sweep point, then switch
        TURRET.seekTarget(Constants.TurretConstants.kSweepRange);
        if (TURRET.turretInRange(2)) {
          logger.info("Seeking right");
          state = TrackingState.SEEK_RIGHT;
        }
        break;
      case SEEK_RIGHT:
        // switch to tracking if target is detected
        if (VISION.isTargetValid()) {
          state = TrackingState.HAS_TARGET;
          logger.info("Target acquired");
          break;
        }

        // wait to reach the right sweep point, then switch, mark new attempt
        TURRET.seekTarget(-Constants.TurretConstants.kSweepRange);
        if (TURRET.turretInRange(2)) {
          attemptNum++;
          logger.info("Seeking left");
          state = TrackingState.SEEK_LEFT;
        }
        break;
      case HAS_TARGET:
        // continue tracking until target is lost
        lost = VISION.lostCheck();
        attemptNum = 1;
        if (VISION.isTargetValid()) {
          TURRET.rotateTurret(-0.95 * VISION.getOffsetAngle()); // + getStrafeAdjustment()
          SmartDashboard.putBoolean("Match/Locked On", true);
        } else {
          if (lost && doReseek) {
            state = TrackingState.SEEK_RIGHT;
          }
        }
        break;
    }
  }

  @Override
  public boolean isFinished() {
    return !VISION.isTrackingEnabled();
  }

  private double getStrafeAdjustment() {
    double strafe = DRIVER_CONTROLS.getStrafe();
    if (Math.abs(strafe) > .2) {
      return strafe * strafeFactor;
    }
    return 0;
  }

  private enum TrackingState {
    SEEK_RIGHT,
    SEEK_LEFT,
    HAS_TARGET
  }
}
