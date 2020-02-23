package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.HoodSubsystem;

public class HoodOpenLoopCommand extends InstantCommand {
  public static HoodSubsystem HOOD = RobotContainer.HOOD;
  private final double output;

  public HoodOpenLoopCommand(double output) {
    addRequirements(SHOOTER);

    this.output = output;
  }

  @Override
  public void initialize() {
    HOOD.hoodOpenLoop(output);
  }
}
