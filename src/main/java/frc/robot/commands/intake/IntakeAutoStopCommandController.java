package frc.robot.commands.intake;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
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
      intakeSubsystem.runBoth(IntakeConstants.kIntakeSpeed, IntakeConstants.kSquidSpeed);
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
        if (intakeSubsystem.squidsStalled()) stallCount++;
        else stallCount = 0;

        if (stallCount >= IntakeConstants.kStallCount) {
          state = IntakeStates.REVERSING;
          logger.debug("intake stalled");
          reverseTime = currentTime;
          intakeSubsystem.runBoth(
              IntakeConstants.kIntakeEjectSpeed, IntakeConstants.kSquidEjectSpeed);
        }
        break;
      case REVERSING:
        if ((currentTime - reverseTime) >= IntakeConstants.kReverseTime) {
          state = IntakeStates.INTAKING;
          logger.debug("unjammed, running intake");
          intakeSubsystem.runBoth(IntakeConstants.kIntakeSpeed, IntakeConstants.kSquidSpeed);
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
  }

  private enum IntakeStates {
    INTAKING,
    REVERSING,
    DONE
  }
}
