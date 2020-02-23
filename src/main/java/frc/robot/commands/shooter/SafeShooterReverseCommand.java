package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.MagazineSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class SafeShooterReverseCommand extends InstantCommand {
  private ShooterSubsystem SHOOTER = RobotContainer.SHOOTER;
  private MagazineSubsystem MAGAZINE = RobotContainer.MAGAZINE;

  public SafeShooterReverseCommand() {}

  @Override
  public void initialize() {
    if (MAGAZINE.isIntakeBeamBroken() && SHOOTER.getShooterSpeed() <= 100) {
      SHOOTER.runOpenLoop(Constants.ShooterConstants.kOpenloopArmReverse);
    }
  }
}
