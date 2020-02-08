package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotContainer;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.TalonSRXItem;

public class MagazineSubsystem extends SubsystemBase {
  private static final int MAGAZINE_ID = 30;

  private TalonSRX magazineTalon;

  public MagazineSubsystem() {
    magazineTalon = new TalonSRX(MAGAZINE_ID);
    configureTalon();
  }

  private void configureTalon() {
    TalonSRXConfiguration config = new TalonSRXConfiguration();
    config.continuousCurrentLimit = 30;
    config.peakCurrentDuration = 40;
    config.peakCurrentLimit = 35;
    magazineTalon.configAllSettings(config);
    magazineTalon.enableCurrentLimit(true);
    magazineTalon.configForwardLimitSwitchSource(
        LimitSwitchSource.RemoteTalonSRX, LimitSwitchNormal.NormallyOpen, 43);
    TelemetryService telemetry = RobotContainer.TELEMETRY;
    telemetry.stop();
    telemetry.register(new TalonSRXItem(magazineTalon, "Magazine"));
    telemetry.start();
  }

  public void runTalon(double speed) {
    magazineTalon.set(ControlMode.PercentOutput, speed);
  }

  public void stopTalon() {
    magazineTalon.set(ControlMode.PercentOutput, 0);
  }

  public void enableLimitSwitch(boolean isEnabled) {
    if (isEnabled) {
      magazineTalon.configForwardLimitSwitchSource(
          LimitSwitchSource.RemoteTalonSRX, LimitSwitchNormal.NormallyOpen, 43);
    } else {
      magazineTalon.configForwardLimitSwitchSource(
          LimitSwitchSource.RemoteTalonSRX, LimitSwitchNormal.Disabled, 43);
    }
  }

  public boolean isIntakeBeamBroken() {
    return magazineTalon.getSensorCollection().isRevLimitSwitchClosed();
  }
}
