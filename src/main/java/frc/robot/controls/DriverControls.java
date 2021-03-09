package frc.robot.controls;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.commands.auto.PathDriveCommand;
import frc.robot.commands.drive.XLockCommand;
import frc.robot.commands.drive.ZeroGyroCommand;
import frc.robot.commands.hood.HoodOpenLoopCommand;
import frc.robot.commands.sequences.*;

public class DriverControls {
  private Joystick joystick;

  DriverControls(int portNumber) {
    joystick = new Joystick(portNumber);

    // Drive Commands
    new JoystickButton(joystick, Button.RESET.id).whenPressed(new ZeroGyroCommand());
    new JoystickButton(joystick, Button.X.id).whenPressed(new XLockCommand());

    // Shoot Commands
    new JoystickButton(joystick, Shoulder.LEFT_UP.id).whenPressed(new ArmedShootSequenceCommand());
    new JoystickButton(joystick, Shoulder.LEFT_UP.id).whenReleased(new StopShootCommand());

    // Intake Commands
    new JoystickButton(joystick, Shoulder.RIGHT_DOWN.id).whenPressed(new AutoIntakeCmdGroup());
    new JoystickButton(joystick, Shoulder.RIGHT_DOWN.id)
        .whenReleased(new StopIntakeAndMagazineCommandGroup());

    // Hood Commands
    new JoystickButton(joystick, Trim.LEFT_Y_POS.id).whenPressed(new HoodOpenLoopCommand(.2));
    new JoystickButton(joystick, Trim.LEFT_Y_POS.id).whenReleased(new HoodOpenLoopCommand(0));
    new JoystickButton(joystick, Trim.LEFT_Y_NEG.id).whenPressed(new HoodOpenLoopCommand(-.2));
    new JoystickButton(joystick, Trim.LEFT_Y_NEG.id).whenReleased(new HoodOpenLoopCommand(0));

    // Software Testing

    new JoystickButton(joystick, Button.HAMBURGER.id)
        .whenPressed(new PathDriveCommand("SlalomPath", 90));
  }
  /** Left stick X (up-down) axis. */
  public double getForward() {
    return -joystick.getRawAxis(Axis.LEFT_X.id);
  }

  /** Left stick Y (left-right) axis. */
  public double getStrafe() {
    return joystick.getRawAxis(Axis.LEFT_Y.id);
  }

  /** Right stick Y (left-right) axis. */
  public double getYaw() {
    return joystick.getRawAxis(Axis.RIGHT_Y.id);
  }

  public enum Axis {
    RIGHT_X(1),
    RIGHT_Y(0),
    LEFT_X(2),
    LEFT_Y(5),
    TUNER(6),
    LEFT_BACK(4),
    RIGHT_BACK(3);

    private final int id;

    Axis(int id) {
      this.id = id;
    }
  }

  public enum Shoulder {
    RIGHT_DOWN(2),
    LEFT_DOWN(4),
    LEFT_UP(5);

    private final int id;

    Shoulder(int id) {
      this.id = id;
    }
  }

  public enum Toggle {
    LEFT_TOGGLE(1);

    private final int id;

    Toggle(int id) {
      this.id = id;
    }
  }

  public enum Button {
    RESET(3),
    HAMBURGER(14),
    X(15),
    UP(16),
    DOWN(17);

    private final int id;

    Button(int id) {
      this.id = id;
    }
  }

  public enum Trim {
    LEFT_Y_POS(7),
    LEFT_Y_NEG(6),
    LEFT_X_POS(8),
    LEFT_X_NEG(9),
    RIGHT_X_POS(10),
    RIGHT_X_NEG(11),
    RIGHT_Y_POS(12),
    RIGHT_Y_NEG(13);

    private final int id;

    Trim(int id) {
      this.id = id;
    }
  }
}
