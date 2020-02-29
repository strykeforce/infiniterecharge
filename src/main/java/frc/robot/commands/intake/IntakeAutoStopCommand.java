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
    intakeSubsystem.runIntake(IntakeConstants.kIntakeSpeed);
    state = IntakeStates.INTAKING;
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
            SmartDashboard.putBoolean("Match/Magazine Full", true);
            intakeSubsystem.stopIntake();
          }
        } else {
          timerOn = false;
        }

        if (intakeSubsystem.isStalled()) stallCount++;
        else stallCount = 0;

        if (stallCount >= IntakeConstants.kStallCount) {
          state = IntakeStates.REVERSING;
          logger.debug("intake stalled");
          reverseTime = currentTime;
          intakeSubsystem.runIntake(IntakeConstants.kEjectSpeed);
          SmartDashboard.putBoolean("Match/Intake Stalled", true);
        }
        break;
      case REVERSING:
        if ((currentTime - reverseTime) >= IntakeConstants.kReverseTime) {
          state = IntakeStates.INTAKING;
          logger.debug("unjammed, running intake");
          intakeSubsystem.runIntake(IntakeConstants.kIntakeSpeed);
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
    intakeSubsystem.stopIntake();
  }

  private enum IntakeStates {
    INTAKING,
    REVERSING,
    DONE
  }
}
