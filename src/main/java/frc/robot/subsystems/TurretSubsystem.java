package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.TalonSRXItem;

public class TurretSubsystem extends SubsystemBase {
  private static final DriveSubsystem DRIVE = RobotContainer.DRIVE;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static TalonSRX turret;

  private static double targetTurretPosition = 0;

  private static int turretStableCounts = 0;

  private static final int TURRET_ID = 42;

  private static final double TURRET_TICKS_PER_DEGREE =
      Constants.TurretConstants.TURRET_TICKS_PER_DEGREE;

  private static final double kTurretZeroTicks = Constants.TurretConstants.kTurretZeroTicks;
  private static final double kWrapRange = Constants.TurretConstants.kWrapRange;
  private static final double kTurretMidpoint = Constants.TurretConstants.kTurretMidpoint;

  public TurretSubsystem() {
    configTalons();
  }

  private void configTalons() {
    TalonSRXConfiguration turretConfig = new TalonSRXConfiguration();

    // turret setup
    turret = new TalonSRX(TURRET_ID);
    turretConfig.forwardSoftLimitThreshold = Constants.TurretConstants.kForwardLimit;
    turretConfig.reverseSoftLimitThreshold = Constants.TurretConstants.kReverseLimit;
    turretConfig.forwardSoftLimitEnable = true;
    turretConfig.reverseSoftLimitEnable = true;
    turretConfig.slot0.kP = 2;
    turretConfig.slot0.kI = 0.01;
    turretConfig.slot0.kD = 80;
    turretConfig.slot0.kF = 0.21;
    turretConfig.slot0.integralZone = 40;
    turretConfig.slot0.maxIntegralAccumulator = 4500;
    turretConfig.voltageMeasurementFilter = 32;
    turretConfig.voltageCompSaturation = 12;
    turret.configAllSettings(turretConfig);
    turret.configMotionCruiseVelocity(4000);
    turret.configMotionAcceleration(30000);
    turret.enableCurrentLimit(false);
    turret.enableVoltageCompensation(true);
    turret.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 5, 30, 500));

    TelemetryService telService = RobotContainer.TELEMETRY;
    telService.stop();
    telService.register(new TalonSRXItem(turret, "ShooterTurret"));
    telService.start();
  }

  public boolean zeroTurret() {
    boolean didZero = false;
    if (!turret.getSensorCollection().isFwdLimitSwitchClosed()) { // FIXME
      int absPos = turret.getSensorCollection().getPulseWidthPosition() & 0xFFF;
      // inverted because absolute and relative encoders are out of phase
      int offset = (int) -(absPos - kTurretZeroTicks);
      turret.setSelectedSensorPosition(offset);
      didZero = true;
      logger.info(
          "Turret zeroed; offset: {} zeroTicks: {} absPosition: {}",
          offset,
          kTurretZeroTicks,
          absPos);
    } else {
      turret.configPeakOutputForward(0, 0);
      turret.configPeakOutputReverse(0, 0);
      logger.error("Turret zero failed. Killing turret...");
    }

    turret.configForwardLimitSwitchSource(
        LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.Disabled);

    return didZero;
  }

  public void rotateTurret(double offset) {
    double currentAngle = turret.getSelectedSensorPosition() / TURRET_TICKS_PER_DEGREE;
    double targetAngle = currentAngle + offset;
    if (targetAngle <= kWrapRange && turret.getSelectedSensorPosition() > kTurretMidpoint
        || targetAngle < 0) {
      targetAngle += 360;
    }
    double setPoint = targetAngle * TURRET_TICKS_PER_DEGREE;
    setTurret(setPoint);
  }

  public void setTurretAngle(double targetAngle) {
    if (targetAngle <= kWrapRange && turret.getSelectedSensorPosition() > kTurretMidpoint
        || targetAngle < 0) {
      targetAngle += 360;
    }
    double setPoint = targetAngle * TURRET_TICKS_PER_DEGREE;
    setTurret(setPoint);
  }

  public void seekTarget() {
    double bearing = (DRIVE.getGyro().getAngle() + 270) % 360;
    if (bearing < 0) bearing += 360;
    double setPoint = bearing * TURRET_TICKS_PER_DEGREE;
    logger.info("Seeking Target at angle = {}", bearing);
    logger.info("Seeking Target at position = {}", setPoint);
    setTurret(setPoint);
  }

  public void setTurret(double setPoint) {
    targetTurretPosition = setPoint;
    turret.set(ControlMode.MotionMagic, setPoint);
  }

  public void turretOpenLoop(double output) {
    turret.set(ControlMode.PercentOutput, output);
  }

  public boolean turretAtTarget() {
    double currentTurretPosition = turret.getSelectedSensorPosition();
    if (Math.abs(targetTurretPosition - currentTurretPosition)
        > Constants.TurretConstants.kCloseEnoughTurret) {
      turretStableCounts = 0;
    } else {
      turretStableCounts++;
    }
    if (turretStableCounts >= Constants.ShooterConstants.kStableCounts) {
      logger.info("Turret at position {}", targetTurretPosition);
      return true;
    } else {
      return false;
    }
  }

  public double getTurretAngle() {
    return turret.getSelectedSensorPosition() / TURRET_TICKS_PER_DEGREE;
  }

  public boolean turretInRange(int targetCounts) {
    double currentTurretPosition = turret.getSelectedSensorPosition();
    if (Math.abs(targetTurretPosition - currentTurretPosition)
        > Constants.TurretConstants.kCloseEnoughTurret) {
      turretStableCounts = 0;
    } else {
      turretStableCounts++;
    }
    if (turretStableCounts >= targetCounts) {
      logger.info("Turret at position {}", targetTurretPosition);
      return true;
    } else {
      return false;
    }
  }
}