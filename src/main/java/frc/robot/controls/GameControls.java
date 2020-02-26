package frc.robot.controls;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants;
import frc.robot.commands.hood.HoodPositionCommand;
import frc.robot.commands.intake.IntakeRunCommand;
import frc.robot.commands.sequences.*;
import frc.robot.commands.turret.SeekTargetCommand;
import frc.robot.commands.turret.TurretOpenLoopCommand;

public class GameControls {
  private XboxController controller;

  private static final double LEFT_DEADBAND = 0.8;

  public GameControls(int portNumber) {
    controller = new XboxController(portNumber);

    Trigger dPad =
        new Trigger() {
          @Override
          public boolean get() {
            return controller.getPOV(0) != -1;
          }
        };

    Trigger LeftStickRight =
        new Trigger() {
          @Override
          public boolean get() {
            return controller.getRawAxis(XboxController.Axis.kLeftX.value) > LEFT_DEADBAND;
          }
        };

    Trigger LeftStickLeft =
        new Trigger() {
          @Override
          public boolean get() {
            return controller.getRawAxis(XboxController.Axis.kLeftX.value) < -LEFT_DEADBAND;
          }
        };

    Trigger LeftStickOff =
        new Trigger() {
          @Override
          public boolean get() {
            return Math.abs(controller.getRawAxis(XboxController.Axis.kLeftX.value))
                < LEFT_DEADBAND;
          }
        };

    Trigger UpDPAD =
        new Trigger() {
          @Override
          public boolean get() {

            int returnValue = 0;
            if (controller.getPOV(0) == 315) {
              returnValue = 315;
            }
            if (controller.getPOV(0) == 0) {
              returnValue = 0;
            }

            if (controller.getPOV(0) == 45) {
              returnValue = 45;
            }

            return controller.getPOV(0) == returnValue;
          }
        };

    Trigger DownDPAD =
        new Trigger() {
          @Override
          public boolean get() {

            int returnValue = 0;
            if (controller.getPOV(0) == 135) {
              returnValue = 135;
            }
            if (controller.getPOV(0) == 180) {
              returnValue = 180;
            }

            if (controller.getPOV(0) == 225) {
              returnValue = 225;
            }

            return controller.getPOV(0) == returnValue;
          }
        };

    new JoystickButton(controller, Button.kB.value).whenPressed(new AutoIntakeCmdGroup());
    new JoystickButton(controller, Button.kB.value)
        .whenReleased(new StopIntakeAndMagazineCommandGroup());
    new JoystickButton(controller, Button.kY.value).whenPressed(new ArmSequenceCommand());
    new JoystickButton(controller, Button.kStart.value).whenPressed(new SeekTargetCommand());
    new JoystickButton(controller, Button.kBack.value).whenPressed(new HoodPositionCommand(5000));
    UpDPAD.whenActive(new ReverseIntakeAndMagazineCommandGroup());
    DownDPAD.whenActive(new IntakeRunCommand(Constants.IntakeConstants.kEjectSpeed));

    dPad.whenInactive(new StopIntakeAndMagazineCommandGroup());

    new JoystickButton(controller, Button.kX.value).whenPressed(new StopShootCommand());

    LeftStickLeft.whenActive(new TurretOpenLoopCommand(0.3));
    LeftStickRight.whenActive(new TurretOpenLoopCommand(-0.3));
    LeftStickOff.whenActive(new TurretOpenLoopCommand(0));
  }
}
