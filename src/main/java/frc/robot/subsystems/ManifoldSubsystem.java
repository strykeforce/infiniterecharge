package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ManifoldSubsystem extends SubsystemBase {
  private static final int MANIFOLD_ID = 30;

  private TalonSRX manifoldTalon;

  public ManifoldSubsystem() {
    manifoldTalon = new TalonSRX(MANIFOLD_ID);
    configureTalon();
  }

  private void configureTalon() {
    TalonSRXConfiguration config = new TalonSRXConfiguration();
    config.continuousCurrentLimit = 30;
    config.peakCurrentDuration = 40;
    config.peakCurrentLimit = 35;
    manifoldTalon.configAllSettings(config);
    manifoldTalon.enableCurrentLimit(true);
  }

  public void runTalon(double speed) {
    manifoldTalon.set(ControlMode.PercentOutput, speed);
  }

  public void stopTalon() {
    manifoldTalon.set(ControlMode.PercentOutput, 0);
  }
}
