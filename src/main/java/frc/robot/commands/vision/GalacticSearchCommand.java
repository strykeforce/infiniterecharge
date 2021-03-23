package frc.robot.commands.vision;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.DeadeyeA1;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;

public class GalacticSearchCommand extends CommandBase {
  private static final VisionSubsystem VISION = RobotContainer.VISION;
  private static final IntakeSubsystem INTAKE = RobotContainer.INTAKE;
  private static final DriveSubsystem DRIVE = RobotContainer.DRIVE;

  public Logger logger = LoggerFactory.getLogger("Layout Select Command");

  private int attempts;
  private long waitStart;
  private boolean isFirstExecute;
  private double startTimeSeconds;
  private double timeElapsed;
  private DeadeyeA1.Layout layout;
  private GalacticSearchState state;

  private Trajectory trajectoryR1;
  private Trajectory trajectoryR2;
  private Trajectory trajectoryB1;
  private Trajectory trajectoryB2;
  private Trajectory selectedTrajectory;

  public GalacticSearchCommand() {
    addRequirements(DRIVE);
    addRequirements(INTAKE);
    trajectoryR1 = DRIVE.calculateTrajectory("Red1");
    trajectoryR2 = DRIVE.calculateTrajectory("Red2");
    trajectoryB1 = DRIVE.calculateTrajectory("Blue1");
    trajectoryB2 = DRIVE.calculateTrajectory("Blue2");
  }

  @Override
  public void initialize() {
    layout = DeadeyeA1.Layout.INVALID;
    state = GalacticSearchState.SELECTING;
    VISION.setGamepieceEnabled(true);
    attempts = 0;
    isFirstExecute = true;

    INTAKE.runBoth(Constants.IntakeConstants.kIntakeSpeed, Constants.IntakeConstants.kSquidSpeed);
  }

  @Override
  public void execute() {
    switch (state) {
      case SELECTING:
        if (attempts < 20) {
          layout = VISION.getLayout();
          switch (layout) {
            case RED1:
              logger.info("Layout: Red-1");
              selectedTrajectory = trajectoryR1;
              break;
            case RED2:
              logger.info("Layout: Red-2");
              selectedTrajectory = trajectoryR2;
              break;
            case BLUE1:
              logger.info("Layout: Blue-1");
              selectedTrajectory = trajectoryB1;
              break;
            case BLUE2:
              logger.info("Layout: Blue-2");
              selectedTrajectory = trajectoryB2;
              break;
            case INVALID:
              logger.info("Layout: Invalid, retrying");
          }
          attempts++;
          if (layout != DeadeyeA1.Layout.INVALID) {
            state = GalacticSearchState.WAITING;
            waitStart = System.currentTimeMillis();
            logger.info("Waiting");
          }
        } else {
          logger.warn("Failed to identify layout, quiting");
          state = GalacticSearchState.FINISHED;
        }
        break;

        // this exists for tests so that we can disable is it is going to run the wrong path
      case WAITING:
        if (System.currentTimeMillis() - waitStart >= 250) {
          state = GalacticSearchState.RUNNING;
          DRIVE.startPath(selectedTrajectory, 0);
          logger.info("Wait ended, driving path");
        }
        break;

      case RUNNING:
        if (DRIVE.isPathDone(timeElapsed)) state = GalacticSearchState.FINISHED;

        if (isFirstExecute) {
          startTimeSeconds = Timer.getFPGATimestamp();
          isFirstExecute = false;
        }
        double currentTimeSeconds = Timer.getFPGATimestamp();
        timeElapsed = currentTimeSeconds - startTimeSeconds;
        //    logger.info("Current time seconds: " + timeElapsed);
        DRIVE.updatePathOutput(timeElapsed);
        if (DRIVE.isPathDone(timeElapsed)) state = GalacticSearchState.FINISHED;
        break;

      case FINISHED:
    }
  }

  @Override
  public boolean isFinished() {
    return state == GalacticSearchState.FINISHED;
  }

  @Override
  public void end(boolean interrupted) {
    INTAKE.stopIntake();
    INTAKE.stopSquids();
    DRIVE.drive(0, 0, 0);
    DRIVE.setDriveMode(SwerveDrive.DriveMode.OPEN_LOOP);
  }

  private enum GalacticSearchState {
    SELECTING,
    WAITING,
    RUNNING,
    FINISHED
  }
}
