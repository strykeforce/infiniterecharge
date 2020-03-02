package frc.robot.commands.auto;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class TrenchAutoCommandGroup extends SequentialCommandGroup {
  public TrenchAutoCommandGroup(
      double gyroOffset, double turretAngle, String ballFetchPath, String driveShootPath) {}
}
