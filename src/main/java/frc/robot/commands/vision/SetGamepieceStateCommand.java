package frc.robot.commands.vision;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.VisionSubsystem;

public class SetGamepieceStateCommand extends InstantCommand {
  private static final VisionSubsystem VISION = RobotContainer.VISION;
  private boolean enabled;

  public SetGamepieceStateCommand(boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  public void initialize() {
    VISION.setGamepieceEnabled(enabled);
  }
}
