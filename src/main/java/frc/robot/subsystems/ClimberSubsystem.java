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
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.TalonSRXItem;

public class ClimberSubsystem extends SubsystemBase {
  private static int TALON_ID = 50;
  private static int RATCHET_ID = 0;

  private static TalonSRX climb;
  private static Servo ratchet;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private SupplyCurrentLimitConfiguration holdingCurrent =
      new SupplyCurrentLimitConfiguration(true, 10, 15, 0.04);
  private SupplyCurrentLimitConfiguration runningCurrent =
      new SupplyCurrentLimitConfiguration(true, 40, 45, 0.04);

  public ClimberSubsystem() {
    climb = new TalonSRX(TALON_ID);
    ratchet = new Servo(RATCHET_ID);

    TalonSRXConfiguration talonConfig = new TalonSRXConfiguration(); // FIXME
    climb.configAllSettings(talonConfig);
    climb.setNeutralMode(NeutralMode.Brake);
    climb.configSupplyCurrentLimit(holdingCurrent);

    TelemetryService telemetryService = RobotContainer.TELEMETRY;
    telemetryService.stop();
    telemetryService.register(new TalonSRXItem(climb, "Climb"));
    telemetryService.start();
    engageRatchet(false);
    holdClimb();
  }

  public void stopClimb() {
    logger.info("stop Climb");
    climb.set(ControlMode.PercentOutput, 0.0);
  }

  public void runOpenLoop(double setpoint) {
    logger.info("run Climb");
    climb.configSupplyCurrentLimit(runningCurrent);
    climb.set(ControlMode.PercentOutput, setpoint);
  }

  public void holdClimb() {
    climb.configSupplyCurrentLimit(holdingCurrent);
    climb.set(TalonSRXControlMode.PercentOutput, Constants.ClimberConstants.kHoldOutput);
    logger.info("holding climb");
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
}
