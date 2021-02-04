package frc.robot.commands.intake;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.Constants.IntakeConstants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.MagazineSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntakeAutoStopCommand extends CommandBase {

  private IntakeSubsystem intakeSubsystem = RobotContainer.INTAKE;
  private MagazineSubsystem magazine = RobotContainer.MAGAZINE;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private long breakTime;
  private boolean timerOn;
  private IntakeStates state;
  private long reverseTime;
  private int stallCount = 0;

  public IntakeAutoStopCommand() {
    addRequirements(intakeSubsystem);
  }

  @Override
  public void initialize() {
    logger.info("Begin Driver Controller - Auto Intake");
    if (!magazine.isIntakeBeamBroken()) {
      intakeSubsystem.runBoth(IntakeConstants.kIntakeSpeed, IntakeConstants.kSquidSpeed);
      state = IntakeStates.INTAKING;
      timerOn = false;
      logger.info("Intake beam not broken - starting intake");

    } else {
      state = IntakeStates.DONE;
      logger.info("Intake beam already broken - ending AutoIntake");
    }
  }

  @Override
  public void execute() {
    long currentTime = System.currentTimeMillis();
    switch (state) {
      case INTAKING:
        if (magazine.isIntakeBeamBroken() && !timerOn) {
          timerOn = true;
          breakTime = currentTime;
          logger.info("intake beam broken - start timer");
        } else if (magazine.isIntakeBeamBroken() && timerOn) {
          if (currentTime - breakTime >= Constants.IntakeConstants.kTimeFullIntake) {
            state = IntakeStates.DONE;
            logger.info("intake timer {} reached - stop intake", IntakeConstants.kTimeFullIntake);
            SmartDashboard.putBoolean("Match/Magazine Full", true);
            intakeSubsystem.stopSquids();
            intakeSubsystem.stopIntake();
          }
        } else {
          timerOn = false;
        }

        if (intakeSubsystem.squidsStalled()) stallCount++;
        else stallCount = 0;

        if (stallCount >= IntakeConstants.kStallCount) {
          state = IntakeStates.REVERSING;
          logger.debug("intake stalled");
          reverseTime = currentTime;
          intakeSubsystem.runBoth(
              IntakeConstants.kIntakeEjectSpeed, IntakeConstants.kSquidEjectSpeed);

          SmartDashboard.putBoolean("Match/Intake Stalled", true);
        }
        break;
      case REVERSING:
        if ((currentTime - reverseTime) >= IntakeConstants.kReverseTime) {
          state = IntakeStates.INTAKING;
          logger.debug("unjammed, running intake");
          intakeSubsystem.runBoth(IntakeConstants.kIntakeSpeed, IntakeConstants.kSquidSpeed);
          SmartDashboard.putBoolean("Match/Intake Stalled", false);
        }
        break;
    }
  }

  @Override
  public boolean isFinished() {
    return state == IntakeStates.DONE;
  }

  @Override
  public void end(boolean interrupted) {
    intakeSubsystem.stopSquids();
    intakeSubsystem.stopIntake();
    logger.info("Driver Controller - Stopping Auto Intake");
  }

  private enum IntakeStates {
    INTAKING,
    REVERSING,
    DONE
  }
}
