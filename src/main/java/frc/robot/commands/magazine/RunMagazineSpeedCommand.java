package frc.robot.commands.magazine;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.MagazineSubsystem;

public class RunMagazineSpeedCommand extends InstantCommand {
  private static MagazineSubsystem MAGAZINE = RobotContainer.MAGAZINE;
  private double velocity;

  public RunMagazineSpeedCommand(double velocity) {
    addRequirements(MAGAZINE);
    this.velocity = velocity;
  }

  @Override
  public void initialize() {
    MAGAZINE.enableLimitSwitch(false);
    MAGAZINE.disableReverseLimitSwitch();
    MAGAZINE.runSpeed(velocity);
  }
}
