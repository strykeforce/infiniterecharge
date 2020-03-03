package frc.robot.commands.climber;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ClimberSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClimbReleaseFastSequenceCommand extends CommandBase {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private ClimberSubsystem CLIMB = RobotContainer.CLIMBER;
  private ClimberSubsystem.State state;

  public ClimbReleaseFastSequenceCommand() {
    addRequirements(CLIMB);
  }

  @Override
  public void initialize() {
    state = CLIMB.currentState;
    logger.info("Fast Climb Up - Current State: {}", state);
  }

  @Override
  public void execute() {
    switch (state) {
      case STOWED:
        CLIMB.engageRatchet(false);
        state = ClimberSubsystem.State.SERVO_ENGAGE;
        CLIMB.servoMoveTime = Timer.getFPGATimestamp();
        logger.info("Fast Climb - Moving Servo, time: {}", CLIMB.servoMoveTime);
        break;
      case SERVO_ENGAGE:
        if ((Timer.getFPGATimestamp() - CLIMB.servoMoveTime)
            >= Constants.ClimberConstants.kServoMoveTime) {
          CLIMB.runOpenLoop(Constants.ClimberConstants.kSlowDownOutput);
          logger.info("Servo Moved - Moving Down");
        }
      case RELEASING_RATCHET:
        if (Math.abs(CLIMB.getClimbPosition() - Constants.ClimberConstants.kReverseSoftLimit)
            <= Constants.ClimberConstants.kCloseEnoughTicks) {
          CLIMB.ratchetReleaseTime = Timer.getFPGATimestamp();
          logger.info(
              "Ratchet Released - Initialize Ratchet Check time: {}", CLIMB.ratchetReleaseTime);
          CLIMB.runOpenLoop(Constants.ClimberConstants.kSlowUpOutput);
          state = ClimberSubsystem.State.CHECK_RATCHET;
        } else CLIMB.runOpenLoop(Constants.ClimberConstants.kSlowDownOutput);
        break;
      case CHECK_RATCHET:
        if ((Timer.getFPGATimestamp() - CLIMB.ratchetReleaseTime)
            >= Constants.ClimberConstants.kTimeoutRatchetCheck) {
          CLIMB.disableClimb();
          state = ClimberSubsystem.State.LOCKED_OUT;
          logger.info("Climb Locked Out - Ratchet didn't release");
        } else if (Math.abs(
                CLIMB.getClimbPosition() - Constants.ClimberConstants.kCheckRatchetTicks)
            <= Constants.ClimberConstants.kCloseEnoughTicks) {
          state = ClimberSubsystem.State.CLIMBING;
          CLIMB.setClimbCurrentLimit();
          CLIMB.runOpenLoop(Constants.ClimberConstants.kFastUpOutput);
          logger.info("Ratchet released successfully - Climbing Fast");
        } else {
          CLIMB.runOpenLoop(Constants.ClimberConstants.kSlowUpOutput);
        }
        break;
      case CLIMBING:
        CLIMB.runOpenLoop(Constants.ClimberConstants.kFastUpOutput);
      case LOCKED_OUT:
        CLIMB.stopClimb();
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
