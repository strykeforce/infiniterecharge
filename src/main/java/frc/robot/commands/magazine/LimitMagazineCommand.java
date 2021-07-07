package frc.robot.commands.magazine;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.HoodSubsystem;
import frc.robot.subsystems.MagazineSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LimitMagazineCommand extends CommandBase {
  private MagazineSubsystem magazineSubsystem = RobotContainer.MAGAZINE;
  private HoodSubsystem hoodSubsystem = RobotContainer.HOOD;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private double setSpeed;

  public LimitMagazineCommand(double speed) {
    addRequirements(magazineSubsystem);
    this.setSpeed = speed;
  }

  @Override
  public void initialize() {
    if (!hoodSubsystem.isMagazineBeamBroken()) {
      magazineSubsystem.runOpenLoop(setSpeed);
      magazineSubsystem.enableLimitSwitch(true);
      logger.info("No Balls in Magazine yet - starting magazine");
    }
  }

  @Override
  public boolean isFinished() {
    return hoodSubsystem.isMagazineBeamBroken();
  }

  @Override
  public void end(boolean interrupted) {
    magazineSubsystem.stopTalon();
    logger.info("Ball in magazine, stopping");
  }
}
