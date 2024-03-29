package frc.robot.commands.turret;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.TurretSubsystem;
import frc.robot.subsystems.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TurretAimCommand extends CommandBase {
  private VisionSubsystem VISION = RobotContainer.VISION;
  private TurretSubsystem TURRET = RobotContainer.TURRET;
  public Logger logger = LoggerFactory.getLogger("Aim Shooter Command");

  public TurretAimCommand() {
    addRequirements(TURRET);
    addRequirements(VISION);
  }

  @Override
  public void initialize() {
    if (VISION.isTargetValid()) {
      double offset = VISION.getOffsetAngle();
      TURRET.rotateTurret(offset + VISION.getHorizAngleAdjustment());
      logger.info("Single correction: offset angle {}", offset);
    }
  }
  //
  //  @Override
  //  public void execute() {
  //    if (!VISION.getTargetData().getValid()) {
  //    }
  //  }

  @Override
  public boolean isFinished() {
    SmartDashboard.putBoolean("Match/Locked On", true);
    return TURRET.turretAtTarget() && VISION.isStable() && VISION.isTargetValid();
  }
}
