package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.TalonFXItem;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

public class IntakeSubsystem extends SubsystemBase {
  public static double lastIntakePressedTime = 0;

  public static int SQUIDS_ID = 20;
  public static int INTAKE_ID = 21;

  public static TalonFX squidsDrive;
  public static TalonFX intakeDrive;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  TelemetryService telemetryService;

  public IntakeSubsystem() {
    squidsDrive = new TalonFX(SQUIDS_ID);
    intakeDrive = new TalonFX(INTAKE_ID);

    TalonFXConfiguration squidsConfig = new TalonFXConfiguration();
    squidsConfig.supplyCurrLimit.currentLimit = 40.0;
    squidsConfig.supplyCurrLimit.triggerThresholdCurrent = 45.0;
    squidsConfig.supplyCurrLimit.triggerThresholdTime = 0.04;
    squidsConfig.supplyCurrLimit.enable = true;

    TalonFXConfiguration intakeConfig = new TalonFXConfiguration();
    intakeConfig.supplyCurrLimit.currentLimit = 40.0;
    intakeConfig.supplyCurrLimit.triggerThresholdCurrent = 45.0;
    intakeConfig.supplyCurrLimit.triggerThresholdTime = 0.04;
    intakeConfig.supplyCurrLimit.enable = true;

    squidsDrive.configAllSettings(squidsConfig);
    intakeDrive.configAllSettings(intakeConfig);
    if (!RobotContainer.isEvent) {
      TelemetryService telemetryService = RobotContainer.TELEMETRY;
      telemetryService.stop();
      telemetryService.register(new TalonFXItem(squidsDrive, "Squids"));
      telemetryService.register(new TalonFXItem(intakeDrive, "Intake"));
      telemetryService.start();
    }
  }

  public List<BaseTalon> getTalons() {
    return List.of(squidsDrive, intakeDrive);
  }

  public void stopIntake() {
    logger.info("stop Intake");
    intakeDrive.set(ControlMode.PercentOutput, 0.0);
  }

  public void stopSquids() {
    logger.info("stop Squids");
    squidsDrive.set(ControlMode.PercentOutput, 0.0);
  }

  public void runSquids(double setpoint) {
    logger.info("run Squids at: {}", setpoint);
    squidsDrive.set(ControlMode.PercentOutput, setpoint);
  }

  public void runIntake(double setpoint) {
    logger.info("run Intake at: {}", setpoint);
    intakeDrive.set(ControlMode.PercentOutput, setpoint);
  }

  public void runBoth(double intakeSpeed, double squidSpeed) {
    logger.info("Run Intake at: {} Run Squids at: {}", intakeSpeed, squidSpeed);
    intakeDrive.set(ControlMode.PercentOutput, intakeSpeed);
    squidsDrive.set(ControlMode.PercentOutput, squidSpeed);
  }

  public boolean squidsStalled() {
    if (squidsDrive.getMotorOutputPercent() != 0) {
      if (Math.abs(squidsDrive.getSelectedSensorVelocity())
          < Constants.IntakeConstants.kStallVelocity) {
        logger.info("Squids Stalled");
        return true;
      } else return false;
    } else return false;
  }
}
