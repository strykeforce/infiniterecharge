package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.TalonSRXItem;

public class HoodSubsystem extends SubsystemBase {
  private static final DriveSubsystem DRIVE = RobotContainer.DRIVE;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static TalonSRX hood;

  private static double targetHoodPosition = 0;
  private static int hoodStableCounts = 0;

  private static final int HOOD_ID = 43;

  private static final double HOOD_TICKS_PER_DEGREE = Constants.HoodConstants.HOOD_TICKS_PER_DEGREE;

  private static final double kHoodZeroTicks = Constants.HoodConstants.kHoodZeroTicks;

  public HoodSubsystem() {
    configTalons();
  }

  private void configTalons() {
    TalonSRXConfiguration hoodConfig = new TalonSRXConfiguration();

    // hood setup
    hood = new TalonSRX(HOOD_ID);
    hood.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 5, 10, 1));
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

    TelemetryService telService = RobotContainer.TELEMETRY;
    telService.stop();
    telService.register(new TalonSRXItem(hood, "ShooterHood"));
    telService.start();
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

  public void setHoodAngle(int position) {
    hood.set(ControlMode.Position, position);
  }

  public void hoodOpenLoop(double output) {
    hood.set(ControlMode.PercentOutput, output);
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
}
