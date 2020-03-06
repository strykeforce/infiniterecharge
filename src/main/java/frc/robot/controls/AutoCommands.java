package frc.robot.controls;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.commands.auto.DelayShootCommandGroup;
import frc.robot.commands.auto.ExitLineCommand;
import frc.robot.commands.auto.NoDelayShootCommandGroup;
import frc.robot.commands.auto.TrenchAutoCommandGroup;

public class AutoCommands {
  // .2 toml distance = 7"
  static Command createFor(int switchPosition) {
    switch (switchPosition) {
      case 0x10:
        return new NoDelayShootCommandGroup(180, 2.5, 180);
      case 0x11:
        return new DelayShootCommandGroup(180, 4, 2.5, 180);
      case 0x12:
        return new DelayShootCommandGroup(180, 8, 2.5, 180);
      case 0x13:
        return new DelayShootCommandGroup(180, 10, 2.5, 180);
      case 0x20:
        return new TrenchAutoCommandGroup(180, 1, 2, "TrenchPickupPath", "TrenchReturnPath");
      case 0x30:
        return new NoDelayShootCommandGroup(90, 12, -90);

      default:
        String msg =
            String.format("no auto command assigned for switch position %02X", switchPosition);
        DriverStation.reportWarning(msg, false);
        return new ExitLineCommand();
    }
  }
}
