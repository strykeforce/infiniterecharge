
import edu.wpi.first.wpilibj.TalonSRX;


public class IntakeSubsystem extends SubsystemBase {
    public static double TALON_ID = 20;

    public static TalonSRX intakeDrive;


    public IntakeSubsystem() {
        intakeDrive = new TalonSRX(TALON_ID);
        intakeDrive.configPeakCurrentLimit = 10;
        intakeDrive.configPeakCurrentDuration = 15;
        intakeDrive.configContinuousCurrentLimit = 40;
    }
    
    public void stopIntake() {
        logger.info("stop Intake");
        intakeDrive.set(ControlMode.PercentOutput, 0.0);
    }

    public void runIntake(double setpoint) {
        logger.info("run Intake");
        intakeDrive.set(ControlMode.PercentOutput, setpoint);
    }

}