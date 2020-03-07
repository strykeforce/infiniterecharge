package frc.robot.commands.climber;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ClimberSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClimbReleaseSequenceCommand extends CommandBase {
  private ClimberSubsystem CLIMB = RobotContainer.CLIMBER;
  private ClimberSubsystem.State state;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private double climbOutput;
  private double releaseStartTime;

  public ClimbReleaseSequenceCommand(double climbOutput) {
    addRequirements(CLIMB);
    this.climbOutput = climbOutput;
  }

  @Override
  public void initialize() {
    if (CLIMB.getClimbPosition() <= Constants.ClimberConstants.kPastDeployedTicks) {
      state = ClimberSubsystem.State.STOWED;
    } else {
      state = ClimberSubsystem.State.CLIMBING;
    }
    logger.info("Slow Climb Up - Current State: {}", state);
  }

  @Override
  public void execute() {
    switch (state) {
      case STOWED:
        CLIMB.engageRatchet(false);
        state = ClimberSubsystem.State.SERVO_ENGAGE;
        CLIMB.servoMoveTime = Timer.getFPGATimestamp();
        logger.info("Moving Servo, time: {}", CLIMB.servoMoveTime);
        break;
      case SERVO_ENGAGE:
        if ((Timer.getFPGATimestamp() - CLIMB.servoMoveTime)
            >= Constants.ClimberConstants.kServoMoveTime) {
          CLIMB.runOpenLoop(Constants.ClimberConstants.kReleaseRatchetOutput);
          logger.info("Servo Moved - Moving Down");
          CLIMB.releaseStartTime = Timer.getFPGATimestamp();
          state = ClimberSubsystem.State.RELEASING_RATCHET;
        }
        break;
      case RELEASING_RATCHET:
        if (Timer.getFPGATimestamp() - CLIMB.releaseStartTime
            >= Constants.ClimberConstants.kTimeoutRelease) {
          CLIMB.ratchetReleasedTime = Timer.getFPGATimestamp();
          logger.info(
              "Ratchet Released - Initialize Ratchet Check time: {}", CLIMB.ratchetReleasedTime);
          CLIMB.runOpenLoop(Constants.ClimberConstants.kSlowUpOutput);
          state = ClimberSubsystem.State.CHECK_RATCHET;
        } else CLIMB.runOpenLoop(Constants.ClimberConstants.kReleaseRatchetOutput);
        break;
      case CHECK_RATCHET:
        if ((Timer.getFPGATimestamp() - CLIMB.ratchetReleasedTime)
            >= Constants.ClimberConstants.kTimeoutRatchetCheck) {
          CLIMB.disableClimb();
          state = ClimberSubsystem.State.LOCKED_OUT;
          logger.info("Climb Locked Out - Ratchet didn't release");
        } else if (CLIMB.getClimbPosition() >= Constants.ClimberConstants.kCheckRatchetTicks) {
          state = ClimberSubsystem.State.CLIMBING;
          CLIMB.setClimbCurrentLimit();
          CLIMB.runOpenLoop(climbOutput);
          logger.info("Ratchet released successfully - Climbing at: {}", climbOutput);
        } else {
          CLIMB.runOpenLoop(Constants.ClimberConstants.kSlowUpOutput);
        }
        if (CLIMB.getClimbPosition() <= Constants.ClimberConstants.kPastDeployedTicks) {
          CLIMB.disableSoftLimits();
        }
        break;
      case CLIMBING:
        CLIMB.runOpenLoop(climbOutput);
        break;
      case LOCKED_OUT:
        CLIMB.stopClimb();
        break;
    }
  }

  @Override
  public boolean isFinished() {
    return state == ClimberSubsystem.State.LOCKED_OUT;
  }

  @Override
  public void end(boolean interrupted) {
    CLIMB.stopClimb();
  }
}
