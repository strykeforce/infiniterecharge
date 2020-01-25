package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntakeSubsystem extends SubsystemBase {
  public static int TALON_ID = 20;

  public static TalonSRX intakeDrive;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  //
  public IntakeSubsystem() {
    intakeDrive = new TalonSRX(TALON_ID);
    intakeDrive.configPeakCurrentLimit(35);
    intakeDrive.configPeakCurrentDuration(40);
    intakeDrive.configContinuousCurrentLimit(30);
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
