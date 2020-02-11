package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.Constants.IntakeConstants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.MagazineSubsystem;

public class IntakeAutoStopCommand extends CommandBase {

  private IntakeSubsystem intakeSubsystem = RobotContainer.INTAKE;
  private MagazineSubsystem magazine = RobotContainer.MAGAZINE;
  private long time;
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
    // TODO Auto-generated method stub
    super.execute();

    switch (state) {
      case INTAKING:
        if (magazine.isIntakeBeamBroken() && !timerOn) {
          timerOn = true;
          time = System.currentTimeMillis();
          System.out.println("intake beam broken");
        } else if (magazine.isIntakeBeamBroken() && timerOn) {
          if (System.currentTimeMillis() - time >= Constants.IntakeConstants.kTimeFullIntake) {
            state = IntakeStates.DONE;
            System.out.println("intake stopping");
            intakeSubsystem.stopIntake();
          }
        } else {
          timerOn = false;
        }

        if (intakeSubsystem.isStalled()) stallCount++;
        else stallCount = 0;

        if (stallCount >= Constants.IntakeConstants.kStallCount) {
          state = IntakeStates.REVERSING;
          System.out.println("intake stalled");
          reverseTime = System.currentTimeMillis();
          intakeSubsystem.runIntake(Constants.IntakeConstants.kEjectSpeed);
        }
        break;
      case REVERSING:
        if ((System.currentTimeMillis() - reverseTime) >= Constants.IntakeConstants.kReverseTime) {
          state = IntakeStates.INTAKING;
          System.out.println("unjammed running intake");
          intakeSubsystem.runIntake(Constants.IntakeConstants.kIntakeSpeed);
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
