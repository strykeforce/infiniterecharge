package frc.robot.controls;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.RobotContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoChooser {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private static final Controls CONTROLS = RobotContainer.CONTROLS;
  private static final int AUTON_SWITCH_DEBOUNCED = 100;
  private int autonSwitchPosition = -1;
  private int newAutonSwitchPosition;
  private int autonSwitchStableCount = 0;

  private Command command;

  public AutoChooser() {}

  public void checkSwitch() {
    if (getStartPosition()) {
      logger.info(
          "auto switch initializing position {}", String.format("%02X", autonSwitchPosition));
      command = AutoCommands.createFor(autonSwitchPosition);
    }
  }

  private boolean getStartPosition() {
    boolean changed = false;
    int switchPosition = CONTROLS.getAutoSwitch().position();

    if (switchPosition != newAutonSwitchPosition) {
      autonSwitchStableCount = 0;
      newAutonSwitchPosition = switchPosition;
    } else {
      autonSwitchStableCount++;
    }

    if (autonSwitchStableCount > AUTON_SWITCH_DEBOUNCED && autonSwitchPosition != switchPosition) {
      changed = true;
      autonSwitchPosition = switchPosition;
    }
    return changed;
  }

  public void reset() {
    if (autonSwitchPosition == -1) return;
    logger.debug("reset auton chooser");
    autonSwitchPosition = -1;
  }

  public Command getCommand() {
    return command;
  }
}
