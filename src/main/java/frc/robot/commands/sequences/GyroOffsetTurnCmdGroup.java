package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.drive.GyroOffsetCommand;

public class GyroOffsetTurnCmdGroup extends SequentialCommandGroup {
  public GyroOffsetTurnCmdGroup() {
    addCommands(new GyroOffsetCommand(true));
    // TODO: add turn towards generator command when available
  }
}
