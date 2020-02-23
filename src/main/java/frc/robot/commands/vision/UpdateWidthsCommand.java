package frc.robot.commands.vision;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.VisionSubsystem;

public class UpdateWidthsCommand extends InstantCommand {
  private static final VisionSubsystem VISION = RobotContainer.VISION;

  public UpdateWidthsCommand() {
    addRequirements(VISION);
  }

  @Override
  public void initialize() {
    SmartDashboard.putNumber("Tuning/rawPixWidth", VISION.getRawWidth());
    SmartDashboard.putNumber("Tuning/correctedPixWidth", VISION.getCorrectedWidth());
  }
}
