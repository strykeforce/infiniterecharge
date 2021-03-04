package frc.robot.commands.drive;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;
import frc.robot.subsystems.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistanceCalibrateCommand extends CommandBase {

  private DriveSubsystem driveSubsystem = RobotContainer.DRIVE;
  private double[] currentEncoder = new double[4];
  private double[] beginningEncoder = new double[4];
  private int ticks = 0;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public DistanceCalibrateCommand(int ticks) {

    addRequirements(driveSubsystem);
    this.ticks = ticks;
  }

  @Override
  public void initialize() {
    for (int i = 0; i < 4; i++) {
      beginningEncoder[i] =
          driveSubsystem.getAllWheels()[i].getDriveTalon().getSelectedSensorPosition();
    }
    driveSubsystem.drive(0.25, 0, 0);
  }

  @Override
  public void execute() {}

  @Override
  public void end(boolean interrupted) {
    double[] deltaEncoder = new double[4];
    for (int i = 0; i < 4; i++) {
      currentEncoder[i] =
          driveSubsystem.getAllWheels()[i].getDriveTalon().getSelectedSensorPosition();
      deltaEncoder[i] = Math.abs(currentEncoder[i] - beginningEncoder[i]);
    }
    driveSubsystem.drive(0, 0, 0);
    logger.info(
        "Delta Ticks, {}, {}, {}, {}, ",
        deltaEncoder[0],
        deltaEncoder[1],
        deltaEncoder[2],
        deltaEncoder[3]);
  }

  @Override
  public boolean isFinished() {

    return Math.abs(
            driveSubsystem.getAllWheels()[1].getDriveTalon().getSelectedSensorPosition()
                - beginningEncoder[1])
        >= ticks;
  }
}
