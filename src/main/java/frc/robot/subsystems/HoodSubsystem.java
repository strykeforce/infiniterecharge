package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.telemetry.TelemetryService;
import org.strykeforce.telemetry.measurable.TalonSRXMeasurable;

public class HoodSubsystem extends SubsystemBase {

  private static final DriveSubsystem DRIVE = RobotContainer.DRIVE;
  private static final int HOOD_ID = 43;
  private static final double HOOD_TICKS_PER_DEGREE = Constants.HoodConstants.HOOD_TICKS_PER_DEGREE;
  private static TalonSRX hood;
  private static double targetHoodPosition = 0;
  private static int hoodStableCounts = 0;
  private static int kHoodZeroTicks;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  TelemetryService telService;

  public HoodSubsystem() {
    kHoodZeroTicks = Constants.HoodConstants.kHoodZeroTicks;
    configTalons();
  }

  private void configTalons() {
    TalonSRXConfiguration hoodConfig = new TalonSRXConfiguration();

    // hood setup
    hood = new TalonSRX(HOOD_ID);
    hoodConfig.forwardSoftLimitThreshold = Constants.HoodConstants.kForwardSoftLimit;
    hoodConfig.reverseSoftLimitThreshold = Constants.HoodConstants.kReverseSoftLimit;
    hoodConfig.forwardSoftLimitEnable = true;
    hoodConfig.reverseSoftLimitEnable = true;
    hoodConfig.slot0.kP = 4.0;
    hoodConfig.slot0.kI = 0.0;
    hoodConfig.slot0.kD = 60.0;
    hoodConfig.slot0.kF = 0.19;
    hoodConfig.slot0.integralZone = 0;
    hoodConfig.slot0.maxIntegralAccumulator = 0;
    hoodConfig.velocityMeasurementPeriod = VelocityMeasPeriod.Period_100Ms;
    hoodConfig.velocityMeasurementWindow = 64;
    hoodConfig.motionAcceleration = 120_000;
    hoodConfig.motionCruiseVelocity = 4_000;
    hoodConfig.voltageCompSaturation = 12;
    hoodConfig.voltageMeasurementFilter = 32;
    hoodConfig.reverseLimitSwitchNormal = LimitSwitchNormal.Disabled;
    hoodConfig.forwardLimitSwitchNormal = LimitSwitchNormal.Disabled;
    hood.setNeutralMode(NeutralMode.Brake);
    hood.configAllSettings(hoodConfig);
    hood.enableCurrentLimit(false);
    hood.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 5, 30, 0.5));
    hood.enableVoltageCompensation(true);
    if (!RobotContainer.isEvent) {
      TelemetryService telService = RobotContainer.TELEMETRY;
      telService.stop();
      telService.register(new TalonSRXMeasurable(hood, "ShooterHood"));
      telService.start();
    }
  }

  public List<BaseTalon> getTalons() {
    return List.of(hood);
  }

  public boolean zeroHood() {
    boolean didZero = false;
    // proximity sensor = normally closed
    if (!hood.getSensorCollection().isRevLimitSwitchClosed()) {
      int absPos = hood.getSensorCollection().getPulseWidthPosition() & 0xFFF;
      int offset = (int) (absPos - kHoodZeroTicks);
      hood.setSelectedSensorPosition(offset + Constants.HoodConstants.kOffsetZeroTicks);
      didZero = true;
      logger.info(
          "Hood zeroed; offset: {} zeroTicks: {} absPosition: {}", offset, kHoodZeroTicks, absPos);
    } else {
      hood.configPeakOutputForward(0, 0);
      hood.configPeakOutputReverse(0, 0);
      logger.error("Hood zero failed. Killing hood...");
    }

    hood.configReverseLimitSwitchSource(
        LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.Disabled);

    return didZero;
  }

  public int getHoodPosition() {
    return (int) hood.getSelectedSensorPosition();
  }

  public void setHoodPosition(int position) {
    hood.set(ControlMode.MotionMagic, position);
    targetHoodPosition = position;
    logger.info("Setting Hood to {} ticks", position);
  }

  public void hoodOpenLoop(double output) {
    hood.set(ControlMode.PercentOutput, output);
    logger.info("Running Hood open-loop at: {}", output);
  }

  public boolean hoodAtTarget() {
    double currentHoodPosition = hood.getSelectedSensorPosition();
    if (Math.abs(targetHoodPosition - currentHoodPosition)
        > Constants.HoodConstants.kCloseEnoughHood) {
      hoodStableCounts = 0;
    } else {
      hoodStableCounts++;
    }
    if (hoodStableCounts >= Constants.ShooterConstants.kStableCounts) {
      logger.info("Hood at position {}", targetHoodPosition);
      return true;
    } else {
      return false;
    }
  }

  public boolean isMagazineBeamBroken() {
    return hood.getSensorCollection().isFwdLimitSwitchClosed();
  }

  @Override
  public void periodic() {
    if (isMagazineBeamBroken()) {
      SmartDashboard.putBoolean("Match/Ball Chambered", true);
    } else {
      SmartDashboard.putBoolean("Match/Ball Chambered", false);
    }
  }
}
