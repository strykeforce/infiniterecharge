package frc.robot.commands.magazine;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.MagazineSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class WaitForMagazineBeamCommand extends CommandBase {
  private ShooterSubsystem shooterSubsystem = RobotContainer.SHOOTER;
  private MagazineSubsystem magazineSubsystem = RobotContainer.MAGAZINE;
  private int beamStableCount = 0;

  public WaitForMagazineBeamCommand() {
    addRequirements(magazineSubsystem);
  }

  @Override
  public boolean isFinished() {

    if (shooterSubsystem.isMagazineBeamBroken()) beamStableCount = 0;
    else beamStableCount++;

    if (beamStableCount >= Constants.MagazineConstants.kBeamStableCounts) return true;
    else return false;
  }

  @Override
  public void end(boolean interrupted) {
    magazineSubsystem.stopTalon();
  }
}
