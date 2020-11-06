package frc.robot.commands.intake;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.Constants.IntakeConstants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.MagazineSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntakeAutoStopCommandController extends CommandBase {

  private IntakeSubsystem intakeSubsystem = RobotContainer.INTAKE;
  private MagazineSubsystem magazine = RobotContainer.MAGAZINE;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private long breakTime;
  private boolean timerOn;
  private boolean doublePressed;
  private IntakeStates state;
  private long reverseTime;
  private int stallCount = 0;

  public IntakeAutoStopCommandController() {
    addRequirements(intakeSubsystem);
  }

  @Override
  public void initialize() {

    doublePressed =
        (Timer.getFPGATimestamp() - intakeSubsystem.lastIntakePressedTime)
            <= IntakeConstants.kDoublePressMaxTime;
    if (doublePressed || !magazine.isIntakeBeamBroken()) {
      state = IntakeStates.INTAKING;
      intakeSubsystem.runSquids(IntakeConstants.kIntakeSpeed);
      intakeSubsystem.lastIntakePressedTime = Timer.getFPGATimestamp();
      timerOn = false;

    } else {
      state = IntakeStates.DONE;
      intakeSubsystem.lastIntakePressedTime = Timer.getFPGATimestamp();
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
          logger.debug("intake beam broken");
        } else if (magazine.isIntakeBeamBroken() && timerOn) {
          if (currentTime - breakTime >= Constants.IntakeConstants.kTimeFullIntake) {
            state = IntakeStates.DONE;
            logger.debug("intake stopping");
            intakeSubsystem.stopSquids();
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
          intakeSubsystem.runSquids(IntakeConstants.kEjectSpeed);
        }
        break;
      case REVERSING:
        if ((currentTime - reverseTime) >= IntakeConstants.kReverseTime) {
          state = IntakeStates.INTAKING;
          logger.debug("unjammed, running intake");
          intakeSubsystem.runSquids(IntakeConstants.kIntakeSpeed);
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
  }

  private enum IntakeStates {
    INTAKING,
    REVERSING,
    DONE
  }
}
