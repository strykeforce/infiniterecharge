package frc.robot.commands.magazine;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.MagazineSubsystem;

public class RunMagazineCommand extends InstantCommand {
  private MagazineSubsystem magazineSubsystem = RobotContainer.MAGAZINE;
  private double setSpeed;

  public RunMagazineCommand(double speed) {
    addRequirements(magazineSubsystem);
    this.setSpeed = speed;
  }

  @Override
  public void initialize() {
    magazineSubsystem.enableLimitSwitch(false);
    magazineSubsystem.disableReverseLimitSwitch();
    magazineSubsystem.runTalon(setSpeed);
  }
}
