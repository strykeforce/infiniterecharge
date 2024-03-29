package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.LogCommand;
import frc.robot.commands.vision.ChooseSearchLayoutCommand;
import frc.robot.commands.vision.SetGamepieceStateCommand;

public class GalacticSearchCmdGroup extends SequentialCommandGroup {
  public GalacticSearchCmdGroup() {
    addCommands(
        new LogCommand("Begin Galactic Search Sequence"),
        new SetGamepieceStateCommand(true),
        new ChooseSearchLayoutCommand(),
        new SetGamepieceStateCommand(false));
  }
}
