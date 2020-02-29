package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.TalonFXItem;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

public class ClimberSubsystem extends SubsystemBase {
  private static int TALON_ID = 50;

  private static TalonFX climb;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public ClimberSubsystem() {
    climb = new TalonFX(TALON_ID);

    TalonFXConfiguration talonConfig = new TalonFXConfiguration(); // FIXME

    climb.configAllSettings(talonConfig);

    TelemetryService telemetryService = RobotContainer.TELEMETRY;
    telemetryService.stop();
    telemetryService.register(new TalonFXItem(climb, "Climb"));
    telemetryService.start();
  }

  public void stopClimb() {
    logger.info("stop Climb");
    climb.set(ControlMode.PercentOutput, 0.0);
  }

  public void runOpenLoop(double setpoint) {
    logger.info("run Climb");
    climb.set(ControlMode.PercentOutput, setpoint);
  }
}
