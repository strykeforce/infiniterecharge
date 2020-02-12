package frc.robot.controls;

public class Controls {
  private DriverControls driverControls;
  private GameControls gameControls;

  public Controls() {
    driverControls = new DriverControls(0);
    gameControls = new GameControls(1);
  }

  public DriverControls getDriverControls() {
    return driverControls;
  }
}
