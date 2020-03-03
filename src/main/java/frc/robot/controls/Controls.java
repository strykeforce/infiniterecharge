package frc.robot.controls;

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
  }

  public DriverControls getDriverControls() {
    return driverControls;
  }

  public AutoSwitch getAutoSwitch() {
    return autoSwitch;
  }
}
