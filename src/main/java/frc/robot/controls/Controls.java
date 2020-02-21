package frc.robot.controls;

public class Controls {
  private DriverControls driverControls;
  private GameControls gameControls;
  private SmartDashboardControls smartDashboardControls;

  public Controls() {
    driverControls = new DriverControls(0);
    gameControls = new GameControls(1);
    smartDashboardControls = new SmartDashboardControls();
  }

  public DriverControls getDriverControls() {
    return driverControls;
  }
}
