package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.ColorSensorSubsystem;

public class ColorDirectionCommand extends InstantCommand {
  private ColorSensorSubsystem COLORSENSOR = RobotContainer.COLORSENSOR;

  public ColorDirectionCommand() {
    addRequirements(COLORSENSOR);
  }

  @Override
  public void initialize() {
    COLORSENSOR.testMethod(COLORSENSOR.wheelLogic(ColorSensorSubsystem.ColorNames.GREEN));
  }
}
