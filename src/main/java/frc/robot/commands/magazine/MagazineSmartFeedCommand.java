package frc.robot.commands.magazine;

import static frc.robot.Constants.IntakeConstants.*;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.MagazineSubsystem;
import frc.robot.subsystems.TurretSubsystem;
import frc.robot.subsystems.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MagazineSmartFeedCommand extends CommandBase {
  private MagazineSubsystem MAGAZINE = RobotContainer.MAGAZINE;
  private IntakeSubsystem INTAKE = RobotContainer.INTAKE;
  private TurretSubsystem TURRET = RobotContainer.TURRET;
  private VisionSubsystem VISION = RobotContainer.VISION;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private FeedStates state;
  private double timerStart;

  public MagazineSmartFeedCommand() {
    addRequirements(MAGAZINE);
    addRequirements(INTAKE);
  }

  @Override
  public void initialize() {
    state = FeedStates.STOPPED;
    logger.info("Begin Magazine Smart Feed Sequence");
  }

  @Override
  public void execute() {
    switch (state) {
      case STOPPED:
        if (!isMoving()) {
          state = FeedStates.START_MAG;
          logger.info("Turret Done Moving - Starting Smart Feed again");
        } else System.out.println("MOVING!!!!");
        break;
      case START_MAG:
        MAGAZINE.runSpeed(Constants.MagazineConstants.kClosedLoopShoot);
        timerStart = System.currentTimeMillis();
        state = FeedStates.WAIT;
        logger.info("Starting Magazine");
        break;
      case WAIT:
        if (System.currentTimeMillis() - timerStart
            > Constants.MagazineConstants.kArmTimeToShooterOn) {
          state = FeedStates.START_INTAKE;
        }
        System.out.println("Waiting");
        break;
      case START_INTAKE:
        INTAKE.runBoth(kIntakeShootSpeed, kSquidShootSpeed);
        state = FeedStates.RUNNING;
        logger.info("Starting Intake");
        break;
      case RUNNING:
        if (isMoving()) {
          INTAKE.runBoth(0, 0);
          MAGAZINE.runOpenLoop(0);
          state = FeedStates.STOPPED;
          logger.info("Turret is Moving - Stopping Intake and Magazine");
        }
        break;
    }
  }

  private boolean isMoving() {
    return TURRET.getTurretError() > Constants.TurretConstants.kMaxShootError;
  }

  private enum FeedStates {
    RUNNING,
    STOPPED,
    START_MAG,
    WAIT,
    START_INTAKE,
  }
}
