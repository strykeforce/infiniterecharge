package frc.robot.controls;

import edu.wpi.first.wpilibj.DigitalInput;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoSwitch {
  private static final int BITS = 6;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final List<DigitalInput> digitalInputs = new ArrayList<>();

  public AutoSwitch() {
    logger.debug("initializing auton switch");
    for (int i = 0; i < BITS; i++) {
      digitalInputs.add(i, new DigitalInput(i));
    }
  }

  // Read Auton Switch from binary-coded HEX switched
  public int position() {
    int val = 0;
    for (int i = BITS; i-- > 0; ) {
      val = val << 1;
      val = (val & 0xFE) | (digitalInputs.get(i).get() ? 0 : 1);
    }
    return val;
  }
}
