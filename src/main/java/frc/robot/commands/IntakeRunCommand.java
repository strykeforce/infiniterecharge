import frc.robot.RobotContainer;

public class IntakeRunCommand extends InstantCommand {
    public static IntakeSubsystem INTAKE = RobotContainer.INTAKE;
    private final double setpoint;

    public IntakeRunCommand(double setpoint) {
        this.setpoint = setpoint;
    }
    
    protected void initialize() {
        INTAKE.runIntake(setpoint);
    }

    protected boolean isFinished() {
        return true;
    }
}