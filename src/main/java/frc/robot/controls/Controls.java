package frc.robot.controls;

public class Controls {
  private DriverControls driverControls;

  public Controls() {
    driverControls = new DriverControls(0);
  }

  public DriverControls getDriverControls() {
    return driverControls;
  }
}
