package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotContainer;
import org.strykeforce.thirdcoast.talon.TalonFXItem;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

public class ShooterSubsystem extends SubsystemBase {
  private static TalonFX leftMaster;
  private static final int LMASTERID = 40;

  public ShooterSubsystem() {
    configTalons();
  }

  private void configTalons() {
    TalonFXConfiguration lMasterConfig = new TalonFXConfiguration();
    lMasterConfig.supplyCurrLimit.currentLimit = 40.0;
    lMasterConfig.supplyCurrLimit.triggerThresholdCurrent = 45;
    lMasterConfig.supplyCurrLimit.triggerThresholdTime = 0.04;
    lMasterConfig.supplyCurrLimit.enable = true;
    leftMaster = new TalonFX(LMASTERID);
    leftMaster.configAllSettings(lMasterConfig);
    leftMaster.setNeutralMode(NeutralMode.Coast);

    TelemetryService telService = RobotContainer.TELEMETRY;
    telService.stop();
    telService.register(new TalonFXItem(leftMaster, "ShooterLeftMaster"));
    telService.start();
  }

  public void run(double setPoint) {
    leftMaster.set(ControlMode.PercentOutput, setPoint);
  }

  public void stop() {
    leftMaster.set(ControlMode.PercentOutput, 0);
  }
}
