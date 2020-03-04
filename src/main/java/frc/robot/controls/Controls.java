package frc.robot.controls;

import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.RobotContainer;
import frc.robot.commands.turret.AutoReZeroCommand;

public class Controls {
  private DriverControls driverControls;
  private GameControls gameControls;
  private SmartDashboardControls smartDashboardControls;
  private AutoSwitch autoSwitch;

  public Controls() {
    driverControls = new DriverControls(0);
    gameControls = new GameControls(1);
    smartDashboardControls = new SmartDashboardControls();
    autoSwitch = new AutoSwitch();

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

  public AutoSwitch getAutoSwitch() {
    return autoSwitch;
  }
}
