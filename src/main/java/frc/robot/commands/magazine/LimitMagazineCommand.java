package frc.robot.commands.magazine;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.HoodSubsystem;
import frc.robot.subsystems.MagazineSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class LimitMagazineCommand extends CommandBase {
  private MagazineSubsystem magazineSubsystem = RobotContainer.MAGAZINE;
  private ShooterSubsystem shooterSubsystem = RobotContainer.SHOOTER;
  private HoodSubsystem hoodSubsystem = RobotContainer.HOOD;
  private double setSpeed;

  public LimitMagazineCommand(double speed) {
    addRequirements(magazineSubsystem);
    this.setSpeed = speed;
  }

  @Override
  public void initialize() {
    if (!hoodSubsystem.isMagazineBeamBroken()) {
      magazineSubsystem.runTalon(setSpeed);
      magazineSubsystem.enableLimitSwitch(true);
    }
  }

  @Override
  public boolean isFinished() {
    return hoodSubsystem.isMagazineBeamBroken();
  }

  @Override
  public void end(boolean interrupted) {
    magazineSubsystem.stopTalon();
  }
}
