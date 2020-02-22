package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaitCommand extends CommandBase {
  public long waitTime; // Milliseconds
  public long startTime;
  public Logger logger = LoggerFactory.getLogger("Wait Command");

  public WaitCommand(long time) {
    waitTime = time;
  }

  @Override
  public void initialize() {
    logger.info("Begin waiting");
    startTime = System.currentTimeMillis();
  }

  @Override
  public void execute() {}

  @Override
  public boolean isFinished() {
    return System.currentTimeMillis() - startTime >= waitTime;
  }

  @Override
  public void end(boolean interrupted) {
    logger.info("Finished waiting");
  }
}
