package frc.robot.controls;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.RobotContainer;
import frc.robot.commands.turret.AutoReZeroCommand;
import java.util.ArrayList;
import org.strykeforce.thirdcoast.util.AutonSwitch;

public class Controls {
  private DriverControls driverControls;
  private GameControls gameControls;
  private SmartDashboardControls smartDashboardControls;
  private AutonSwitch autoSwitch;

  public Controls() {
    driverControls = new DriverControls(0);
    gameControls = new GameControls(1);
    smartDashboardControls = new SmartDashboardControls();

    // Normally we would use AutonSwitch(6) constructor for 6 bits, but specify DIO 0, 1, 2, 3, 4, 6
    // since DIO pin 5 is mission on Tunnel Rat roboRIO
    ArrayList<DigitalInput> digitalInputs = new ArrayList<>();
    for (int i = 0; i < 5; i++) digitalInputs.add(new DigitalInput(i));
    digitalInputs.add(new DigitalInput(6));
    autoSwitch = new AutonSwitch(digitalInputs);

    Trigger turretReset =
        new Trigger() {
          @Override
          public boolean get() {
            return RobotContainer.TURRET.talonReset;
          }
        };

    turretReset.whenActive(new AutoReZeroCommand());
  }

  public DriverControls getDriverControls() {
    return driverControls;
  }

  public AutonSwitch getAutoSwitch() {
    return autoSwitch;
  }
}
