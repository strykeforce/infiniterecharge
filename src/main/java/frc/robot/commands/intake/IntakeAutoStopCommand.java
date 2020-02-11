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
        } else if (magazine.isIntakeBeamBroken() && timerOn) {
          if (System.currentTimeMillis() - time >= Constants.IntakeConstants.kTimeFullIntake) {
            state = IntakeStates.DONE;
            intakeSubsystem.stopIntake();
          }
        } else {
          timerOn = false;
        }
        if (intakeSubsystem.isStalled()) {
          state = IntakeStates.REVERSING;
          reverseTime = System.currentTimeMillis();
          intakeSubsystem.runIntake(Constants.IntakeConstants.kEjectSpeed);
        }
      case REVERSING:
        if ((System.currentTimeMillis() - reverseTime) >= Constants.IntakeConstants.kReverseTime) {
          state = IntakeStates.INTAKING;
          intakeSubsystem.runIntake(Constants.IntakeConstants.kIntakeSpeed);
        }
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
