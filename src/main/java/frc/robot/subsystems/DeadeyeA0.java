package frc.robot.subsystems;

import java.util.Set;
import java.util.function.DoubleSupplier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.deadeye.*;
import org.strykeforce.thirdcoast.telemetry.item.Measurable;
import org.strykeforce.thirdcoast.telemetry.item.Measure;

public class DeadeyeA0 implements TargetDataListener<MinAreaRectTargetData>, Measurable {
  private static final String X = "X";
  private static final String Y = "Y";

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final Deadeye<MinAreaRectTargetData> deadeye =
      new Deadeye<>("A0", MinAreaRectTargetData.class);

  private double centerX;
  private double centerY;
  private double height;
  private double width;
  private Point2D topLeft;
  private Point2D topRight;
  private Point2D bottomRight;
  private boolean valid;

  public DeadeyeA0() {
    deadeye.setTargetDataListener(this);
  }

  public double getCenterX() {
    return centerX;
  }

  public double getCenterY() {
    return centerY;
  }

  public double getHeight() {
    return height;
  }

  public double getWidth() {
    return width;
  }

  public double getTopLeftX() {
    return topLeft.x;
  }

  public double getTopRightX() {
    return topRight.x;
  }

  public double getBottomRightX() {
    return bottomRight.x;
  }

  public boolean getValid() {
    return valid;
  }

  public void setLightsEnabled(boolean enabled) {
    deadeye.setLightEnabled(enabled);
  }

  public void setEnabled(boolean enabled) {
    deadeye.setEnabled(enabled);
  }

  ///////////////////////////////////////////////////////////////////////////
  // TargetDataListener
  ///////////////////////////////////////////////////////////////////////////
  @Override
  public void onTargetData(MinAreaRectTargetData data) {
    Point2D center = data.center;
    centerX = center.x;
    centerY = center.y;
    height = data.height;
    width = data.width;
    valid = data.valid;
    topLeft = data.points[1];
    topRight = data.points[2];
    bottomRight = data.points[3];
  }

  ///////////////////////////////////////////////////////////////////////////
  // Measurable
  ///////////////////////////////////////////////////////////////////////////
  @NotNull
  @Override
  public String getDescription() {
    return "Deadeye A0";
  }

  @NotNull
  @Override
  public Set<Measure> getMeasures() {
    return Set.of(new Measure(X, "Target X"), new Measure(Y, "Target Y"));
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
      case X:
        return this::getCenterX;
      case Y:
        return this::getCenterY;
      default:
        throw new IllegalStateException("Unexpected value: " + measure.getName());
    }
  }
}
