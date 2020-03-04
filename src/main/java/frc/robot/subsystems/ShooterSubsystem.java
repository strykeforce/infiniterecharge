package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.TalonFXItem;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

public class ShooterSubsystem extends SubsystemBase {
  private static final DriveSubsystem DRIVE = RobotContainer.DRIVE;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static TalonFX leftMaster;
  private static TalonFX rightSlave;

  public static boolean isArmed = false;
  private static int targetShooterSpeed = 0;
  private static int shooterStableCounts = 0;

  private static final int L_MASTER_ID = 40;
  private static final int R_SLAVE_ID = 41;

  TelemetryService telService;

  public ShooterSubsystem() {
    configTalons();
  }

  private void configTalons() {
    TalonFXConfiguration lMasterConfig = new TalonFXConfiguration();

    lMasterConfig.supplyCurrLimit.currentLimit = 40.0;
    lMasterConfig.supplyCurrLimit.triggerThresholdCurrent = 50.0;
    lMasterConfig.supplyCurrLimit.triggerThresholdTime = 2.0;
    lMasterConfig.supplyCurrLimit.enable = true;
    lMasterConfig.slot0.kP = 1.5;
    lMasterConfig.slot0.kI = 0;
    lMasterConfig.slot0.kD = 5;
    lMasterConfig.slot0.kF = 0.048;
    lMasterConfig.peakOutputForward = 1.0;
    lMasterConfig.peakOutputReverse = 0.0;
    lMasterConfig.velocityMeasurementPeriod = VelocityMeasPeriod.Period_20Ms;
    lMasterConfig.velocityMeasurementWindow = 16;
    leftMaster = new TalonFX(L_MASTER_ID);
    leftMaster.configAllSettings(lMasterConfig);
    leftMaster.setNeutralMode(NeutralMode.Coast);
    leftMaster.enableVoltageCompensation(false);

    rightSlave = new TalonFX(R_SLAVE_ID);
    rightSlave.configAllSettings(lMasterConfig);
    rightSlave.setNeutralMode(NeutralMode.Coast);
    rightSlave.setInverted(true);
    rightSlave.enableVoltageCompensation(false);
    rightSlave.follow(leftMaster);
    if (!RobotContainer.isEvent) {
      TelemetryService telService = RobotContainer.TELEMETRY;
      telService.stop();
      telService.register(new TalonFXItem(leftMaster, "ShooterLeftMaster"));
      telService.register(new TalonFXItem(rightSlave, "ShooterRightSlave"));
      telService.start();
    }
  }

  public List<BaseTalon> getTalons() {
    return List.of(leftMaster, rightSlave);
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
}
