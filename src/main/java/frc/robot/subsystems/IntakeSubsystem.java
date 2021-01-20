package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
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
  public static TalonSRX intakeDrive;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  TelemetryService telemetryService;

  public IntakeSubsystem() {
    squidsDrive = new TalonFX(SQUIDS_ID);
    intakeDrive = new TalonSRX(INTAKE_ID);

    TalonFXConfiguration squidsConfig = new TalonFXConfiguration();
    squidsConfig.supplyCurrLimit.currentLimit = 40.0;
    squidsConfig.supplyCurrLimit.triggerThresholdCurrent = 45.0;
    squidsConfig.supplyCurrLimit.triggerThresholdTime = 0.04;
    squidsConfig.supplyCurrLimit.enable = true;

    TalonSRXConfiguration intakeConfig = new TalonSRXConfiguration();
    intakeDrive.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 40, 45, 0.04));

    squidsDrive.configAllSettings(squidsConfig);
    if (!RobotContainer.isEvent) {
      TelemetryService telemetryService = RobotContainer.TELEMETRY;
      telemetryService.stop();
      telemetryService.register(new TalonFXItem(squidsDrive, "Squids"));
      telemetryService.start();
    }
  }

  public List<BaseTalon> getTalons() {
    return List.of(squidsDrive);
  }

  public void stopSquids() {
    logger.info("stop Squids");
    squidsDrive.set(ControlMode.PercentOutput, 0.0);
  }

  public void runSquids(double setpoint) {
    logger.info("run Squids at: {}", setpoint);
    squidsDrive.set(ControlMode.PercentOutput, setpoint);
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
