package frc.robot.subsystems;

import frc.robot.Constants;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.deadeye.Deadeye;
import org.strykeforce.deadeye.TargetDataListener;
import org.strykeforce.deadeye.TargetListTargetData;
import org.strykeforce.telemetry.measurable.Measurable;
import org.strykeforce.telemetry.measurable.Measure;

public class DeadeyeA1 implements TargetDataListener<TargetListTargetData>, Measurable {

  private static final String NUM_TARGETS = "NUM_TARGETS";
  private static final double SIZE_THRESHOLD = Constants.VisionConstants.SIZE_THRESHOLD;
  private static final double DISTANCE_THRESHOLD = Constants.VisionConstants.DISTANCE_THRESHOLD;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final Deadeye<TargetListTargetData> deadeye =
      new Deadeye<>("A1", TargetListTargetData.class);

  private int numTargets;
  private boolean valid;
  private List<TargetListTargetData.Target> targets;

  public DeadeyeA1() {
    deadeye.setTargetDataListener(this);
  }

  public int getNumTargets() {
    return numTargets;
  }

  public Layout getLayout() {
    if (valid == false || targets.size() == 0) {
      return Layout.INVALID;
    }
    if (getLargestSize() > SIZE_THRESHOLD) {
      return isRed1() ? Layout.RED1 : Layout.RED2;
    } else {
      return isBlue1() ? Layout.BLUE1 : Layout.BLUE2;
    }
  }

  public double getLargestSize() {
    double largest = 0;
    for (TargetListTargetData.Target target : targets) {
      if (target.contourArea > largest) {
        largest = target.contourArea;
      }
    }

    return largest;
  }

  private boolean isRed1() {
    int leftmost = 640;
    boolean isBottom = true;
    for (TargetListTargetData.Target target : targets) {
      if (target.topLeft.x < leftmost) {
        leftmost = target.topLeft.x;
        isBottom = true;
        for (TargetListTargetData.Target compare : targets) {
          if (target.topLeft.y < compare.topLeft.y) {
            isBottom = false;
            break;
          }
        }
      }
    }
    return !isBottom;
  }

  private boolean isBlue1() {
    int rightmost = 0;
    for (TargetListTargetData.Target target : targets) {
      if (target.topLeft.x > rightmost) {
        rightmost = target.topLeft.x;
      }
    }

    int leftmost = 640;
    for (TargetListTargetData.Target target : targets) {
      if (target.topLeft.x < leftmost) {
        leftmost = target.topLeft.x;
      }
    }

    return rightmost - leftmost > DISTANCE_THRESHOLD;
  }

  public void setEnabled(boolean enabled) {
    deadeye.setEnabled(enabled);
  }

  ///////////////////////////////////////////////////////////////////////////
  // TargetDataListener
  ///////////////////////////////////////////////////////////////////////////
  @Override
  public void onTargetData(TargetListTargetData data) {
    numTargets = data.targets.size();
    valid = data.valid;
    targets = data.targets;
  }

  ///////////////////////////////////////////////////////////////////////////
  // Measurable
  ///////////////////////////////////////////////////////////////////////////
  @NotNull
  @Override
  public String getDescription() {
    return "Deadeye A1";
  }

  @NotNull
  @Override
  public Set<Measure> getMeasures() {
    return Set.of(new Measure(NUM_TARGETS, "Number of Targets", this::getNumTargets));
  }

  @NotNull
  @Override
  public String getType() {
    return "deadeye";
  }

  @Override
  public int getDeviceId() {
    return 0;
  }

  public enum Layout {
    RED1,
    RED2,
    BLUE1,
    BLUE2,
    INVALID
  }
}
