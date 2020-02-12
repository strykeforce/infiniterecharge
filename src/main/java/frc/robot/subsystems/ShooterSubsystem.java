package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotContainer;
import org.strykeforce.thirdcoast.talon.TalonFXItem;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.TalonSRXItem;

public class ShooterSubsystem extends SubsystemBase {
  private static final DriveSubsystem DRIVE = RobotContainer.DRIVE;

  private static TalonFX leftMaster;
  private static TalonFX rightSlave;
  private static TalonSRX turret;
  private static TalonSRX hood;

  private static final int L_MASTER_ID = 40;
  private static final int R_SLAVE_ID = 41;
  private static final int TURRET_ID = 42;
  private static final int HOOD_ID = 43;

  private static final double TURRET_TICKS_PER_DEGREE = 1; // FIXME
  private static final double HOOD_TICKS_PER_DEGREE = 1; // FIXME
  private static final double kTurretZeroTicks = 1; // FIXME
  private static final double kHoodZeroTicks = 1; // FIXME
  private static final double kWrapRange = 1; // FIXME
  private static final double kTurretMidpoint = 1; // FIXME

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
    leftMaster.configAllSettings(lMasterConfig);
    leftMaster.setNeutralMode(NeutralMode.Coast);

    rightSlave = new TalonFX(R_SLAVE_ID);
    rightSlave.configAllSettings(lMasterConfig);
    rightSlave.setNeutralMode(NeutralMode.Coast);
    rightSlave.setInverted(true);
    rightSlave.follow(leftMaster);

    // turret setup
    turret = new TalonSRX(TURRET_ID);
    turret.configAllSettings(turretConfig);

    // hood setup
    hood = new TalonSRX(HOOD_ID);
    hood.configAllSettings(hoodConfig);

    TelemetryService telService = RobotContainer.TELEMETRY;
    telService.stop();
    telService.register(new TalonSRXItem(hood, "ShooterHood"));
    telService.register(new TalonFXItem(leftMaster, "ShooterLeftMaster"));
    telService.start();
  }

  public void run(double setPoint) {
    leftMaster.set(ControlMode.Velocity, setPoint);
  }

  public boolean zeroTurret() {
    boolean didZero = false;
    if (turret.getSensorCollection().isFwdLimitSwitchClosed()) {
      int absPos = turret.getSensorCollection().getPulseWidthPosition() & 0xFFF;

      // appears backwards because absolute and relative encoders are out-of-phase in hardware
      int offset = (int) (kTurretZeroTicks - absPos);
      turret.setSelectedSensorPosition(offset);
      didZero = true;
    } else {
      turret.configPeakOutputForward(0, 0);
      turret.configPeakOutputReverse(0, 0);
    }

    turret.configForwardLimitSwitchSource(
        LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.Disabled);

    return didZero;
  }

  public boolean zeroHood() {
    boolean didZero = false;
    if (hood.getSensorCollection().isRevLimitSwitchClosed()) {
      int absPos = hood.getSensorCollection().getPulseWidthPosition() & 0xFFF;

      // appears backwards because absolute and relative encoders are out-of-phase in hardware
      int offset = (int) (kHoodZeroTicks - absPos);
      hood.setSelectedSensorPosition(offset);
      didZero = true;
    } else {
      hood.configPeakOutputForward(0, 0);
      hood.configPeakOutputReverse(0, 0);
    }

    hood.configForwardLimitSwitchSource(
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
    turret.set(ControlMode.MotionMagic, setPoint);
  }

  public void setTurretAngle(double targetAngle) {
    if (targetAngle <= kWrapRange && turret.getSelectedSensorPosition() > kTurretMidpoint
            || targetAngle < 0) {
      targetAngle += 360;
    }
    double setPoint = targetAngle * TURRET_TICKS_PER_DEGREE;
    turret.set(ControlMode.MotionMagic, setPoint);
  }

  public void seekTarget() {
    double bearing = Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360);
    double setPoint = -bearing * TURRET_TICKS_PER_DEGREE;
    turret.set(ControlMode.MotionMagic, setPoint);
  }

  public void setHoodAngle(double angle) {
    double setPoint = angle * HOOD_TICKS_PER_DEGREE;
    hood.set(ControlMode.MotionMagic, setPoint);
  }

  public void stop() {
    leftMaster.set(ControlMode.PercentOutput, 0);
  }

  public boolean isMagazineBeamBroken() {
    return hood.getSensorCollection().isFwdLimitSwitchClosed();
  }
}
