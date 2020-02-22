package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.TalonFXItem;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.TalonSRXItem;

public class ShooterSubsystem extends SubsystemBase {
  private static final DriveSubsystem DRIVE = RobotContainer.DRIVE;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static TalonFX leftMaster;
  private static TalonFX rightSlave;
  private static TalonSRX turret;
  private static TalonSRX hood;

  public static boolean isArmed = false;
  private static int targetShooterSpeed = 0;
  private static double targetTurretPosition = 0;
  private static int shooterStableCounts = 0;
  private static int turretStableCounts = 0;

  private static double targetHoodPosition = 0;
  private static int hoodStableCounts = 0;

  private static final int L_MASTER_ID = 40;
  private static final int R_SLAVE_ID = 41;
  private static final int TURRET_ID = 42;
  private static final int HOOD_ID = 43;

  private static final double TURRET_TICKS_PER_DEGREE =
      Constants.ShooterConstants.TURRET_TICKS_PER_DEGREE;
  private static final double HOOD_TICKS_PER_DEGREE =
      Constants.ShooterConstants.HOOD_TICKS_PER_DEGREE;
  private static final double kTurretZeroTicks = Constants.ShooterConstants.kTurretZero;
  private static final double kHoodZeroTicks = Constants.ShooterConstants.kHoodZeroTicks;
  private static final double kWrapRange = Constants.ShooterConstants.kWrapRange;
  private static final double kTurretMidpoint = Constants.ShooterConstants.kTurretMidpoint;

  public ShooterSubsystem() {
    configTalons();
  }

  private void configTalons() {
    TalonFXConfiguration lMasterConfig = new TalonFXConfiguration();
    TalonSRXConfiguration turretConfig = new TalonSRXConfiguration();
    TalonSRXConfiguration hoodConfig = new TalonSRXConfiguration();

    lMasterConfig.supplyCurrLimit.currentLimit = 40.0;
    lMasterConfig.supplyCurrLimit.triggerThresholdCurrent = 45;
    lMasterConfig.supplyCurrLimit.triggerThresholdTime = 0.04;
    lMasterConfig.supplyCurrLimit.enable = true;
    leftMaster = new TalonFX(L_MASTER_ID);
    lMasterConfig.slot0.kP = 0.45;
    lMasterConfig.slot0.kI = 0;
    lMasterConfig.slot0.kD = 10;
    lMasterConfig.slot0.kF = 0.05;
    leftMaster = new TalonFX(L_MASTER_ID);
    leftMaster.configAllSettings(lMasterConfig);
    leftMaster.setNeutralMode(NeutralMode.Coast);

    rightSlave = new TalonFX(R_SLAVE_ID);
    rightSlave.configAllSettings(lMasterConfig);
    rightSlave.setNeutralMode(NeutralMode.Coast);
    rightSlave.setInverted(true);
    rightSlave.follow(leftMaster);

    // turret setup
    turret = new TalonSRX(TURRET_ID);
    turretConfig.forwardSoftLimitThreshold = Constants.ShooterConstants.kForwardLimit;
    turretConfig.reverseSoftLimitThreshold = Constants.ShooterConstants.kReverseLimit;
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

    // hood setup
    hood = new TalonSRX(HOOD_ID);
    hoodConfig.forwardSoftLimitThreshold = 10000;
    hoodConfig.reverseSoftLimitThreshold = 0;
    hoodConfig.forwardSoftLimitEnable = true;
    hoodConfig.reverseSoftLimitEnable = true;
    hoodConfig.slot0.kP = 4;
    hoodConfig.slot0.kI = 0;
    hoodConfig.slot0.kD = 60;
    hoodConfig.slot0.kF = 0;
    hood.setNeutralMode(NeutralMode.Brake);
    hood.configAllSettings(hoodConfig);
    hood.enableCurrentLimit(false);
    hood.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 5, 10, 1));

    TelemetryService telService = RobotContainer.TELEMETRY;
    telService.stop();
    telService.register(new TalonSRXItem(hood, "ShooterHood"));
    telService.register(new TalonFXItem(leftMaster, "ShooterLeftMaster"));
    telService.register(new TalonFXItem(rightSlave, "ShooterRightSlave"));
    telService.register(new TalonSRXItem(turret, "ShooterTurret"));
    telService.start();
  }

  public void run(int velocity) {
    rightSlave.follow(leftMaster);
    leftMaster.set(ControlMode.Velocity, velocity);
    targetShooterSpeed = velocity;
  }

  public void stop() {
    rightSlave.follow(leftMaster);
    leftMaster.set(ControlMode.PercentOutput, 0);
  }

  public void runOpenLoop(double percent) {
    rightSlave.follow(leftMaster);
    leftMaster.set(ControlMode.PercentOutput, percent);
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

  public boolean zeroHood() {
    boolean didZero = false;
    if (!hood.getSensorCollection().isRevLimitSwitchClosed()) { // FIXME
      int absPos = hood.getSensorCollection().getPulseWidthPosition() & 0xFFF;
      int offset = (int) (absPos - kHoodZeroTicks);
      hood.setSelectedSensorPosition(offset);
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

  public void rotateTurret(double offset) {
    double currentAngle = turret.getSelectedSensorPosition() / TURRET_TICKS_PER_DEGREE;
    double targetAngle = Math.IEEEremainder(currentAngle + offset, 360);
    if (targetAngle <= kWrapRange && turret.getSelectedSensorPosition() > kTurretMidpoint
        || targetAngle < 0) {
      targetAngle += 360;
    }

    setTurret(targetAngle * TURRET_TICKS_PER_DEGREE);
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

  private void setTurret(double setPoint) {
    targetTurretPosition = setPoint;
    turret.set(ControlMode.MotionMagic, setPoint);
  }

  public void turretOpenLoop(double output) {
    turret.set(ControlMode.PercentOutput, output);
  }

  public void setHoodPosition(int position) {
    if (position > 5000) position = 5000;
    hood.set(ControlMode.Position, position);
  }

  public double getTurretAngle() {
    return turret.getSelectedSensorPosition() / TURRET_TICKS_PER_DEGREE;
  }

  public void hoodOpenLoop(double output) {
    hood.set(ControlMode.PercentOutput, output);
  }

  public void applyTuningSetpoints(
      double tuningTurretPosition, int tuningHoodPosition, int tuningShooterVelocity) {
    setTurret(tuningTurretPosition);
    setHoodPosition(tuningHoodPosition);
    run(tuningShooterVelocity);
  }

  public double getShooterSpeed() {
    return leftMaster.getSelectedSensorVelocity();
  }

  public boolean atTargetSpeed() {
    double currentSpeed = leftMaster.getSelectedSensorVelocity();
    if (Math.abs(targetShooterSpeed - currentSpeed) > Constants.ShooterConstants.kCloseEnough) {
      shooterStableCounts = 0;
    } else {
      shooterStableCounts++;
    }
    if (shooterStableCounts >= Constants.ShooterConstants.kStableCounts) {
      logger.info("Shooter at speed {}", targetShooterSpeed);
      return true;
    } else {
      return false;
    }
  }

  public boolean turretAtTarget() {
    double currentTurretPosition = turret.getSelectedSensorPosition();
    if (Math.abs(targetTurretPosition - currentTurretPosition)
        > Constants.ShooterConstants.kCloseEnoughTurret) {
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

  public boolean turretInRange(int targetCounts) {
    double currentTurretPosition = turret.getSelectedSensorPosition();
    if (Math.abs(targetTurretPosition - currentTurretPosition)
        > Constants.ShooterConstants.kCloseEnoughTurret) {
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

  public boolean hoodAtTarget() {
    double currentHoodPosition = hood.getSelectedSensorPosition();
    if (Math.abs(targetHoodPosition - currentHoodPosition)
        > Constants.ShooterConstants.kCloseEnoughHood) {
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
}
