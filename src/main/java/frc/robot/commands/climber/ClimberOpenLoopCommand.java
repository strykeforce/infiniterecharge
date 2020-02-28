package frc.robot.commands.climber;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.RobotContainer;
import frc.robot.subsystems.IntakeSubsystem;

public class ClimberOpenLoopCommand extends InstantCommand {
    public static IntakeSubsystem INTAKE = RobotContainer.INTAKE;
    private final double setpoint;

    public ClimberOpenLoopCommand(double setpoint) {
        this.setpoint = setpoint;
    }

    @Override
    public void initialize() {
        INTAKE.runIntake(setpoint);
    }
}
