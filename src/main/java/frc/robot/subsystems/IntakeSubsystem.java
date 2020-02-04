package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.TalonFXItem;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

public class IntakeSubsystem extends SubsystemBase {
  public static int TALON_ID = 20;

  public static TalonFX intakeDrive;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public IntakeSubsystem() {
    intakeDrive = new TalonFX(TALON_ID);

    TalonFXConfiguration talonConfig = new TalonFXConfiguration();
    talonConfig.supplyCurrLimit.currentLimit = 40.0;
    talonConfig.supplyCurrLimit.triggerThresholdCurrent = 45.0;
    talonConfig.supplyCurrLimit.triggerThresholdTime = 40.0;
    talonConfig.supplyCurrLimit.enable = true;

    intakeDrive.configAllSettings(talonConfig);

    TelemetryService telemetryService = RobotContainer.TELEMETRY;
    telemetryService.stop();
    telemetryService.register(new TalonFXItem(intakeDrive, "Intake"));
    telemetryService.start();
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
