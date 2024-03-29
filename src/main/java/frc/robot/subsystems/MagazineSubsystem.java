package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotContainer;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.telemetry.TelemetryService;
import org.strykeforce.telemetry.measurable.TalonSRXMeasurable;

public class MagazineSubsystem extends SubsystemBase {
  private static final int MAGAZINE_ID = 30;

  private TalonSRX magazineTalon;
  TelemetryService telemetry;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public MagazineSubsystem() {
    magazineTalon = new TalonSRX(MAGAZINE_ID);
    configureTalon();
  }

  private void configureTalon() {
    TalonSRXConfiguration config = new TalonSRXConfiguration();
    config.continuousCurrentLimit = 30;
    config.peakCurrentDuration = 40;
    config.peakCurrentLimit = 35;
    config.slot0.kP = 0.15;
    config.slot0.kI = 0.0;
    config.slot0.kD = 8.0;
    config.slot0.kF = 2.0;
    config.slot0.integralZone = 0;
    config.slot0.maxIntegralAccumulator = 0;
    config.velocityMeasurementWindow = 64;
    config.velocityMeasurementPeriod = VelocityMeasPeriod.Period_100Ms;
    config.voltageCompSaturation = 12;
    config.voltageMeasurementFilter = 32;
    magazineTalon.configAllSettings(config);
    magazineTalon.enableCurrentLimit(true);
    magazineTalon.configForwardLimitSwitchSource(
        RemoteLimitSwitchSource.RemoteTalonSRX, LimitSwitchNormal.NormallyOpen, 43);
    magazineTalon.setNeutralMode(NeutralMode.Brake);
    magazineTalon.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 10, 30, 1));
    magazineTalon.enableVoltageCompensation(true);
    if (!RobotContainer.isEvent) {
      TelemetryService telemetry = RobotContainer.TELEMETRY;
      telemetry.stop();
      telemetry.register(new TalonSRXMeasurable(magazineTalon, "Magazine"));
      telemetry.start();
    }
  }

  public List<BaseTalon> getTalons() {
    return List.of(magazineTalon);
  }

  public void runOpenLoop(double percent) {
    magazineTalon.set(ControlMode.PercentOutput, percent);
    logger.info("Running open loop at: {}", percent);
  }

  public void runSpeed(double velocity) {
    magazineTalon.set(ControlMode.Velocity, velocity);
    logger.info("Running closed loop at: {}", velocity);
  }

  public void stopTalon() {
    magazineTalon.set(ControlMode.PercentOutput, 0);
    logger.info("Stopping Magazine");
  }

  public void enableLimitSwitch(boolean isEnabled) {
    if (isEnabled) {
      magazineTalon.configForwardLimitSwitchSource(
          RemoteLimitSwitchSource.RemoteTalonSRX, LimitSwitchNormal.NormallyOpen, 43);
    } else {
      magazineTalon.configForwardLimitSwitchSource(
          RemoteLimitSwitchSource.RemoteTalonSRX, LimitSwitchNormal.Disabled, 43);
    }
  }

  public boolean isIntakeBeamBroken() {
    return magazineTalon.getSensorCollection().isRevLimitSwitchClosed();
  }

  public void disableReverseLimitSwitch() {
    magazineTalon.configReverseLimitSwitchSource(
        LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.Disabled);
  }
}
