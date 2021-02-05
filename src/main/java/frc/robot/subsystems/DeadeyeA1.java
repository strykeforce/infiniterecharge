package frc.robot.subsystems;

import java.util.List;
import java.util.Set;
import java.util.function.DoubleSupplier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.deadeye.*;
import org.strykeforce.thirdcoast.telemetry.item.Measurable;
import org.strykeforce.thirdcoast.telemetry.item.Measure;

public class DeadeyeA1 implements TargetDataListener<TargetListTargetData>, Measurable {
  private static final String NUM_TARGETS = "NUM_TARGETS";

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

  public double getLargestSize() {
    double largest = 0;
    for(TargetListTargetData.Target target : targets) {
      if (target.contourArea > largest) largest = target.contourArea;
    }
    return largest;
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
    return Set.of(
        new Measure(NUM_TARGETS, "Number of Targets"));
  }

  @NotNull
  @Override
  public String getType() {
    return "deadeye";
  }

  @Override
  public int compareTo(@NotNull Measurable measurable) {
    return Integer.compare(getDeviceId(), measurable.getDeviceId());
  }

  @Override
  public int getDeviceId() {
    return 0;
  }

  @NotNull
  @Override
  public DoubleSupplier measurementFor(@NotNull Measure measure) {
    switch (measure.getName()) {
      case NUM_TARGETS:
        return this::getNumTargets;
      default:
        throw new IllegalStateException("Unexpected value: " + measure.getName());
    }
  }
}
