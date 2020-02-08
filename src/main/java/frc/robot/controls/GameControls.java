package frc.robot.controls;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.commands.sequences.AutoIntakeCmdGroup;
import frc.robot.commands.sequences.ReverseIntakeAndMagazineCommandGroup;
import frc.robot.commands.sequences.StopIntakeAndMagazineCommandGroup;

public class GameControls {
  private XboxController controller;

  public GameControls(int portNumber) {
    controller = new XboxController(portNumber);

    Trigger dPad =
        new Trigger() {
          @Override
          public boolean get() {
            return controller.getPOV(0) != -1;
          }
        };

    new JoystickButton(controller, Button.kB.value).whenPressed(new AutoIntakeCmdGroup());
    new JoystickButton(controller, Button.kB.value)
        .whenReleased(new StopIntakeAndMagazineCommandGroup());
    dPad.whenActive(new ReverseIntakeAndMagazineCommandGroup());
    dPad.whenInactive(new StopIntakeAndMagazineCommandGroup());
  }
}
