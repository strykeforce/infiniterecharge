package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class WaitCommand extends CommandBase {
  public long waitTime; // Milliseconds
  public long startTime;

  public WaitCommand(long time) {
    waitTime = time;
  }

  @Override
  public void initialize() {
    startTime = System.currentTimeMillis();
  }

  @Override
  public void execute() {}

  @Override
  public boolean isFinished() {
    return System.currentTimeMillis() - startTime >= waitTime;
  }
}
