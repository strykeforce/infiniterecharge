package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.IntakeConstants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.MagazineSubsystem;

public class IntakeAutoStopCommand extends CommandBase {

  private IntakeSubsystem intakeSubsystem = RobotContainer.INTAKE;
  private MagazineSubsystem magazine = RobotContainer.MAGAZINE;
  private long time;
  private boolean timerOn;

  public IntakeAutoStopCommand() {
    addRequirements(intakeSubsystem);
  }

  @Override
  public void initialize() {
    intakeSubsystem.runIntake(IntakeConstants.kIntakeSpeed);
  }

  @Override
  public boolean isFinished() {
    if (magazine.isIntakeBeamBroken()) {
      if (timerOn) {
        if ((System.currentTimeMillis() - time) > 500) {
          intakeSubsystem.stopIntake();
          timerOn = false;
          return true;
        }
      } else {
        timerOn = true;
        time = System.currentTimeMillis();
      }
    } else {
      timerOn = false;
    }
    return false;
  }
}
