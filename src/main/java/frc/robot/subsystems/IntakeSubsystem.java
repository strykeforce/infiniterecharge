package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.TalonFXItem;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

public class IntakeSubsystem extends SubsystemBase {
  public static double lastIntakePressedTime = 0;

  public static int TALON_ID = 20;

  public static TalonFX intakeDrive;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  TelemetryService telemetryService;

  public IntakeSubsystem() {
    intakeDrive = new TalonFX(TALON_ID);

    TalonFXConfiguration talonConfig = new TalonFXConfiguration();
    talonConfig.supplyCurrLimit.currentLimit = 40.0;
    talonConfig.supplyCurrLimit.triggerThresholdCurrent = 45.0;
    talonConfig.supplyCurrLimit.triggerThresholdTime = 0.04;
    talonConfig.supplyCurrLimit.enable = true;

    intakeDrive.configAllSettings(talonConfig);
    if (!RobotContainer.isEvent) {
      TelemetryService telemetryService = RobotContainer.TELEMETRY;
      telemetryService.stop();
      telemetryService.register(new TalonFXItem(intakeDrive, "Intake"));
      telemetryService.start();
    }
  }

  public void stopIntake() {
    logger.info("stop Intake");
    intakeDrive.set(ControlMode.PercentOutput, 0.0);
  }

  public void runIntake(double setpoint) {
    logger.info("run Intake");
    intakeDrive.set(ControlMode.PercentOutput, setpoint);
  }

  public boolean isStalled() {

    if (intakeDrive.getMotorOutputPercent() != 0) {
      if (Math.abs(intakeDrive.getSelectedSensorVelocity())
          < Constants.IntakeConstants.kStallVelocity) return true;
      else return false;
    } else return false;
  }
}
