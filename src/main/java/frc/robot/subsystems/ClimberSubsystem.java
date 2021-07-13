package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.telemetry.TelemetryService;
import org.strykeforce.telemetry.measurable.TalonSRXMeasurable;

public class ClimberSubsystem extends SubsystemBase {

  private static int TALON_ID = 50;
  private static int RATCHET_ID = 0;
  private static int PIN_ID; //FIXME

  private static TalonSRX climb;
  private static Servo ratchet;
  private static Servo pin;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  public double ratchetReleasedTime;
  public double releaseStartTime;
  public double servoMoveTime;
  private SupplyCurrentLimitConfiguration runningCurrent =
      new SupplyCurrentLimitConfiguration(true, 60, 80, 1);

  public ClimberSubsystem() {
    climb = new TalonSRX(TALON_ID);
    ratchet = new Servo(RATCHET_ID);
    pin = new Servo(PIN_ID);

    TalonSRXConfiguration talonConfig = new TalonSRXConfiguration();
    talonConfig.forwardSoftLimitThreshold = Constants.ClimberConstants.kForwardSoftLimit;
    talonConfig.forwardSoftLimitEnable = true;
    talonConfig.reverseSoftLimitThreshold = Constants.ClimberConstants.kReverseSoftLimit;
    talonConfig.reverseSoftLimitEnable = true;
    climb.configAllSettings(talonConfig);
    climb.setNeutralMode(NeutralMode.Brake);
    climb.configSupplyCurrentLimit(runningCurrent);
    if (!RobotContainer.isEvent) {
      TelemetryService telemetryService = RobotContainer.TELEMETRY;
      telemetryService.stop();
      telemetryService.register(new TalonSRXMeasurable(climb, "Climb"));
      telemetryService.start();
    }
  }

  public void stopClimb() {
    logger.info("stop Climb");
    climb.set(ControlMode.PercentOutput, 0.0);
  }

  public void runOpenLoop(double setpoint) {
    climb.set(ControlMode.PercentOutput, setpoint);
    logger.info("Running open-loop at: {}", setpoint);
  }

  public void setClimbCurrentLimit() {
    climb.configSupplyCurrentLimit(runningCurrent);
  }

  public void disableSoftLimits() {
    climb.configForwardSoftLimitEnable(false);
    climb.configReverseSoftLimitEnable(false);
  }

  public void engageRatchet(boolean enable) {
    if (enable) {
      ratchet.set(Constants.ClimberConstants.kRatchetEngage);
      logger.info("Engaging Ratchet");
    } else {
      ratchet.set(Constants.ClimberConstants.kRatchetDisable);
      logger.info("Disabling Ratchet");
    }
  }

  public double getClimbPosition() {
    return climb.getSelectedSensorPosition();
  }

  public void zeroClimb() {
    logger.info("Zeroing Climber");
    climb.setSelectedSensorPosition(0);
  }

  public void disableClimb() {
    climb.set(TalonSRXControlMode.PercentOutput, 0.0);
    climb.configPeakOutputForward(0.0);
    climb.configPeakOutputReverse(0.0);
    logger.error("Ratchet did not disengage, disabling climb");
  }

  public enum State {
    STOWED,
    SERVO_ENGAGE,
    RELEASING_RATCHET,
    CHECK_RATCHET,
    CLIMBING,
    LOCKED_OUT
  }
}
