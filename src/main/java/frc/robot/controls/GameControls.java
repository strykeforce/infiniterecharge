package frc.robot.controls;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants;
import frc.robot.commands.intake.IntakeRunCommand;
import frc.robot.commands.sequences.*;
import frc.robot.commands.turret.TurretOpenLoopCommand;
import frc.robot.commands.vision.ChooseSearchLayoutCommand;

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

            boolean on = false;
            int position = controller.getPOV(0);
            if (position == 315 || position == 0 || position == 45) {
              on = true;
            }

            return on;
          }
        };

    Trigger DownDPAD =
        new Trigger() {
          @Override
          public boolean get() {

            boolean on = false;
            int position = controller.getPOV(0);
            if (position == 135 || position == 180 || position == 225) {
              on = true;
            }

            return on;
          }
        };

    // Intake Commands
    UpDPAD.whenActive(new ReverseIntakeAndMagazineCommandGroup());
    DownDPAD.whenActive(
        new IntakeRunCommand(
            Constants.IntakeConstants.kIntakeEjectSpeed,
            Constants.IntakeConstants.kSquidEjectSpeed));
    dPad.whenInactive(new StopIntakeAndMagazineCommandGroup());

    new JoystickButton(controller, Button.kB.value).whenPressed(new AutoIntakeCmdGroupController());
    new JoystickButton(controller, Button.kB.value)
        .whenReleased(new StopIntakeAndMagazineCommandGroup());

    // Vision Commands
    new JoystickButton(controller, Button.kBack.value).whenPressed(new ChooseSearchLayoutCommand());

    // Climb Commands
    //    new JoystickButton(controller, Button.kStart.value)
    //        .whenPressed(new
    // ClimbReleaseSequenceCommand(Constants.ClimberConstants.kFastUpOutput));
    //    new JoystickButton(controller, Button.kStart.value).whenReleased(new
    // ClimberOpenLoopCommand(0));
    //    new JoystickButton(controller, Button.kBack.value).whenPressed(new
    // FastClimbRatchetCommand());
    //    new JoystickButton(controller, Button.kBack.value).whenReleased(new
    // ClimberOpenLoopCommand(0));
    //    new JoystickButton(controller, Button.kBumperLeft.value)
    //        .whenPressed(new ClimberOpenLoopCommand(Constants.ClimberConstants.kSlowDownOutput));
    //    new JoystickButton(controller, Button.kBumperLeft.value)
    //        .whenReleased(new ClimberOpenLoopCommand(0));
    //    new JoystickButton(controller, Button.kBumperRight.value)
    //        .whenPressed(new
    // ClimbReleaseSequenceCommand(Constants.ClimberConstants.kSlowUpOutput));
    //    new JoystickButton(controller, Button.kBumperRight.value)
    //        .whenReleased(new ClimberOpenLoopCommand(0));

    // Shooter Commands
    new JoystickButton(controller, Button.kX.value).whenPressed(new BatterShotCommandGroup());
    new JoystickButton(controller, Button.kX.value).whenReleased(new StopShootCommand());
    new JoystickButton(controller, Button.kA.value).whenPressed(new StopShootCommand());
    new JoystickButton(controller, Button.kY.value).whenPressed(new ArmSequenceCommand());

    // Turret Commands
    LeftStickLeft.whenActive(new TurretOpenLoopCommand(0.3));
    LeftStickRight.whenActive(new TurretOpenLoopCommand(-0.3));
    LeftStickOff.whenActive(new TurretOpenLoopCommand(0));
  }
}
