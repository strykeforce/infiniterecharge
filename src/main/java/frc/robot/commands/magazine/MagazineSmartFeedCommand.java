package frc.robot.commands.magazine;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.MagazineSubsystem;
import frc.robot.subsystems.TurretSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MagazineSmartFeedCommand extends CommandBase {
  private MagazineSubsystem MAGAZINE = RobotContainer.MAGAZINE;
  private IntakeSubsystem INTAKE = RobotContainer.INTAKE;
  private TurretSubsystem TURRET = RobotContainer.TURRET;
  private FeedStates state;
  private double timerStart;

  public MagazineSmartFeedCommand() {
    addRequirements(MAGAZINE);
    addRequirements(INTAKE);
  }

  @Override
  public void initialize() {
    state = FeedStates.STOPPED;
  }

  @Override
  public void execute() {
    switch (state) {
      case STOPPED:
        if (!isMoving()) {
          state = FeedStates.START_MAG;
        }
        break;
      case START_MAG:
        MAGAZINE.runTalon(Constants.MagazineConstants.kOpenloopShoot);
        timerStart = System.currentTimeMillis();
        state = FeedStates.WAIT;
        break;
      case WAIT:
        if (System.currentTimeMillis() - timerStart
            > Constants.MagazineConstants.kArmTimeToShooterOn) {
          state = FeedStates.START_INTAKE;
        }
        break;
      case START_INTAKE:
        INTAKE.runIntake(Constants.IntakeConstants.kIntakeSpeed);
        state = FeedStates.RUNNING;
        break;
      case RUNNING:
        if (isMoving()) {
          INTAKE.runIntake(0);
          MAGAZINE.runTalon(0);
          state = FeedStates.STOPPED;
        }
        break;
    }
  }

  private boolean isMoving() {
    return TURRET.getTurretSpeed() > Constants.TurretConstants.kMaxShootSpeed;
  }

  private enum FeedStates {
    RUNNING,
    STOPPED,
    START_MAG,
    WAIT,
    START_INTAKE,
  }
}
