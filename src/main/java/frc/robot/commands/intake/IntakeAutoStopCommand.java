package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.IntakeSubsystem;

public class IntakeAutoStopCommand extends CommandBase {

    private IntakeSubsystem intakeSubsystem = RobotContainer.INTAKE;

    public IntakeAutoStopCommand() {
        addRequirements(intakeSubsystem);
    }
    
    @Override
    public void initialize() {
        
    }
}