package frc.robot.commands.climber;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants;
import frc.robot.commands.turret.TurretClimbClearCommand;

public class ClimberUpCmdGroup extends SequentialCommandGroup {
  public ClimberUpCmdGroup(double output) {
    addCommands(
        new TurretClimbClearCommand(Constants.TurretConstants.kClimbClearAngle),
        new TurretClimbClearCommand(270),
        new ClimberPullPinCommand(true),
        new ClimberOpenLoopCommand(output));
  }
}
