/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {

  private static final Logger logger = LoggerFactory.getLogger(Constants.class);
  private static final DigitalInput digitalInput = new DigitalInput(9);
  public static boolean isCompBot;
  public static final int kTalonConfigTimeout = 10; // ms

  public Constants() {
    isCompBot = digitalInput.get();

    if (isCompBot) {
      TurretConstants.kTurretZeroTicks = CompConstants.kTurretZeroTicks;
      TurretConstants.kMinStringPotZero = CompConstants.kMinStringPotZero;
      TurretConstants.kMaxStringPotZero = CompConstants.kMaxStringPotZero;
      HoodConstants.kHoodZeroTicks = CompConstants.kHoodZeroTicks;
      VisionConstants.kCameraID = CompConstants.kCameraId;
      logger.info(
          "Comp Bot Constants: turret zero {}, string pot min {}, string pot max {}, hood zero {}, camera {}",
          TurretConstants.kTurretZeroTicks,
          TurretConstants.kMinStringPotZero,
          TurretConstants.kMaxStringPotZero,
          HoodConstants.kHoodZeroTicks,
          VisionConstants.kCameraID);
    } else {
      TurretConstants.kTurretZeroTicks = ProtoConstants.kTurretZeroTicks;
      TurretConstants.kMinStringPotZero = ProtoConstants.kMinStringPotZero;
      TurretConstants.kMaxStringPotZero = ProtoConstants.kMaxStringPotZero;
      HoodConstants.kHoodZeroTicks = ProtoConstants.kHoodZeroTicks;
      VisionConstants.kCameraID = ProtoConstants.kCameraId;
      logger.info(
          "Proto Bot Constants: turret zero {}, string pot min {}, string pot max {}, hood zero {}, camera {}",
          TurretConstants.kTurretZeroTicks,
          TurretConstants.kMinStringPotZero,
          TurretConstants.kMaxStringPotZero,
          HoodConstants.kHoodZeroTicks,
          VisionConstants.kCameraID);
    }
  }

  public static final class DriveConstants {

    public static final double kDeadbandXLock = 0.2;

    // TODO: verify diameter and run calibration
    // 500 cm calibration = actual / odometry
    public static final double kWheelDiameterInches = 2.5 * (500.0 / 500.0);

    // From: https://github.com/strykeforce/axis-config/
    public static final double kMaxSpeedMetersPerSecond = 3.889;

    public static final double kMaxOmega =
        (kMaxSpeedMetersPerSecond / Math.hypot(0.5461 / 2.0, 0.6477 / 2.0))
            / 2.0; // wheel locations below

    // From: https://github.com/strykeforce/axis-config/
    static final double kDriveMotorOutputGear = 22;
    static final double kDriveInputGear = 48;
    static final double kBevelInputGear = 15;
    static final double kBevelOutputGear = 45;
    public static final double kDriveGearRatio =
        (kDriveMotorOutputGear / kDriveInputGear) * (kBevelInputGear / kBevelOutputGear);

    static {
      logger.debug("kMaxOmega = {}", kMaxOmega);
    }

    public static Translation2d[] getWheelLocationMeters() {
      final double x = 0.5461 / 2.0; // front-back, was ROBOT_LENGTH
      final double y = 0.6477 / 2.0; // left-right, was ROBOT_WIDTH
      Translation2d[] locs = new Translation2d[4];
      locs[0] = new Translation2d(x, y); // left front
      locs[1] = new Translation2d(x, -y); // right front
      locs[2] = new Translation2d(-x, y); // left rear
      locs[3] = new Translation2d(-x, -y); // right rear
      return locs;
    }

    public static TalonSRXConfiguration getAzimuthTalonConfig() {
      // constructor sets encoder to Quad/CTRE_MagEncoder_Relative
      TalonSRXConfiguration azimuthConfig = new TalonSRXConfiguration();

      azimuthConfig.primaryPID.selectedFeedbackCoefficient = 1.0;
      azimuthConfig.auxiliaryPID.selectedFeedbackSensor = FeedbackDevice.None;

      azimuthConfig.forwardLimitSwitchSource = LimitSwitchSource.Deactivated;
      azimuthConfig.reverseLimitSwitchSource = LimitSwitchSource.Deactivated;

      azimuthConfig.continuousCurrentLimit = 10;
      azimuthConfig.peakCurrentDuration = 0;
      azimuthConfig.peakCurrentLimit = 0;
      azimuthConfig.slot0.kP = 10.0;
      azimuthConfig.slot0.kI = 0.0;
      azimuthConfig.slot0.kD = 100.0;
      azimuthConfig.slot0.kF = 0.0;
      azimuthConfig.slot0.integralZone = 0;
      azimuthConfig.slot0.allowableClosedloopError = 0;
      azimuthConfig.slot0.maxIntegralAccumulator = 0;
      azimuthConfig.motionCruiseVelocity = 800;
      azimuthConfig.motionAcceleration = 10_000;
      azimuthConfig.velocityMeasurementWindow = 64;
      azimuthConfig.voltageCompSaturation = 12;
      return azimuthConfig;
    }

    public static TalonFXConfiguration getDriveTalonConfig() {
      TalonFXConfiguration driveConfig = new TalonFXConfiguration();
      driveConfig.supplyCurrLimit.currentLimit = 0.04;
      driveConfig.supplyCurrLimit.triggerThresholdCurrent = 45;
      driveConfig.supplyCurrLimit.triggerThresholdTime = 40;
      driveConfig.supplyCurrLimit.enable = true;
      driveConfig.slot0.kP = 0.045;
      driveConfig.slot0.kI = 0.0005;
      driveConfig.slot0.kD = 0.000;
      driveConfig.slot0.kF = 0.047;
      driveConfig.slot0.integralZone = 500;
      driveConfig.slot0.maxIntegralAccumulator = 75_000;
      driveConfig.slot0.allowableClosedloopError = 0;
      driveConfig.velocityMeasurementPeriod = VelocityMeasPeriod.Period_100Ms;
      driveConfig.velocityMeasurementWindow = 64;
      driveConfig.voltageCompSaturation = 12;
      return driveConfig;
    }
  }

  public static final class IntakeConstants {

    // Subsystem Specific
    public static final double kSquidSpeed = -0.4;
    public static final double kIntakeSpeed = -0.4;
    public static final double kSquidShootSpeed = -0.75;
    public static final double kIntakeShootSpeed = -0.75;
    public static final double kSquidEjectSpeed = 0.25;
    public static final double kIntakeEjectSpeed = 0.25;
    public static final int kStallVelocity = 100;
    public static final double kDoublePressMaxTime = 0.5; // Timer class returns time in seconds

    // Command Specific
    public static final long kTimeFullIntake = 1000;
    public static final double kShootDelayIntake = 0;
    public static final double kReverseTime = .5;
    public static final double kStallCount = 5;
  }

  public static final class MagazineConstants {

    // Subsystem Specific
    public static final double kOpenLoopLoad = 0.7;
    public static final double kOpenloopShoot = 1.0;
    public static final double kOpenloopReverse = -0.25;
    public static final double kOpenloopArmReverse = -0.2;
    public static final double kClosedLoopShoot = 4800;

    // Command Specific
    public static final long kArmTimeToShooterOn = 250;
    public static final int kBeamStableCounts = 5;
  }

  public static final class ShooterConstants {

    // Shooter Specific
    public static final double kOpenloopShoot = 0.5;
    public static final double kOpenloopArmReverse = -0.2;
    public static final int kCloseEnough = 1000;
    public static final int kStableCounts = 5;

    // Command Specific
    public static final double kArmTimeToAccelerate = 1.5;
    public static final int kArmSpeed = 10000;
    public static final int kBatterShotVelocity = 8000; // 12-27 inches (2 feet)
  }

  public static final class TurretConstants {

    public static final int kForwardLimit = 26095; // 26000
    public static final int kReverseLimit = -100; // -700
    public static final double TURRET_TICKS_PER_DEGREE = 72.404;
    public static final int kCloseEnoughTurret = 40;
    public static final int kMaxShootError = 5000;
    public static final double kSweepRange = 25;
    public static final double kWrapRange = 1; // FIXME
    public static final double kTurretMidpoint = 13_000; // FIXME
    public static final double loadAngle = 25;
    public static final int kBatterShotTicks = 19600;
    // Subsystem Specific
    public static int kTurretZeroTicks = 2488;
    public static double kMaxStringPotZero = 500;
    public static double kMinStringPotZero = 1000;
  }

  public static final class HoodConstants {

    // Subsystem Specific
    public static final double HOOD_TICKS_PER_DEGREE = 572;
    public static final int kCloseEnoughHood = 100;
    public static final int kForwardSoftLimit = 9000; // 10000
    public static final int kReverseSoftLimit = 0; // 0
    public static final int kOffsetZeroTicks = 1820;
    public static final int kBatterShotTicks = 1600;
    public static int kHoodZeroTicks; // gut check: 149
  }

  public static final class ClimberConstants {

    public static final double kSlowUpOutput = 0.25;
    public static final double kSlowDownOutput = -0.25;
    public static final double kFastUpOutput = 1.0;
    public static final double kFastDownOutput = -1.0;
    public static final double kReleaseRatchetOutput = -1.0;

    public static final double kRatchetEngage = 0.6;
    public static final double kRatchetDisable = 0.3;
    public static final double kServoMoveTime = 0.05;

    public static final int kReverseSoftLimit = -300;
    public static final int kForwardSoftLimit = 55_000;

    public static final int kPastDeployedTicks = 500;
    public static final int kCheckRatchetTicks = 4_000;

    public static final double kTimeoutRelease = 0.2;
    public static final double kTimeoutRatchetCheck = 2;
  }

  public static final class VisionConstants {

    public static final double VERTICAL_FOV = 48.8;
    public static final double HORIZ_FOV = 57.999; // 50.8 //146
    public static final double HORIZ_RES = 640; // 1280
    public static final double TARGET_WIDTH_IN = 39.5; // 34.6
    public static final double CAMERA_HEIGHT = 20.75;
    public static final double TARGET_HEIGHT = 98.5;
    public static final double SIZE_THRESHOLD = 400;
    public static final double DISTANCE_THRESHOLD = 200;
    public static final int kStableRange = 20;
    public static final int kStableCounts = 5;
    public static final double kCenteredRange = 2;
    public static final double kLostLimit = 30;
    public static final String kTablePath = "/home/lvuser/deploy/Lookup_Table.csv";
    public static final int kTableMin = 96;
    public static final int kTableMax = 360;
    public static final int kTableRes = 1;
    public static final int kShooterIndex = 2;
    public static final int kHoodIndex = 3;
    public static final double kHorizAngleCorrection = 0;
    // + is further and lower
    public static final int kHoodInchesCorrectionR1 = 0; // 8-15 feet
    public static final int kHoodInchesCorrectionR2 = 0; // 15-19 feet
    public static final int kHoodInchesCorrectionR3 = 0; // 19-25 feet
    public static final int kHoodInchesCorrectionR4 = 0; // 25+ feet
    public static final int kHoodTicksPerInchR1 = 40; // 8-15 feet
    public static final int kHoodTicksPerInchR2 = 75; // 15-19 feet
    public static final int kHoodTicksPerInchR3 = 75; // 19-25 feet
    public static final int kHoodTicksPerInchR4 = 40; // 25+ feet
    public static String kCameraID = "A0";
  }

  public static final class AutoConstants {

    public static final Pose2d START_PATH = new Pose2d(0.0, 0.0, new Rotation2d(0.0)); // FIXME
    public static final Pose2d END_PATH = new Pose2d(4.0, 0.0, new Rotation2d(0.0)); // FIXME
    public static final List<Translation2d> INTERNAL_POINTS = List.of(); // FIXME
  }

  public static class CompConstants {

    // Turret
    public static final int kTurretZeroTicks = 1931;
    public static final double kMaxStringPotZero = 100;
    public static final double kMinStringPotZero = 35;

    // Hood
    public static final int kHoodZeroTicks = 2008; // gut check: 162

    // Camera
    public static final String kCameraId = "A0";
  }

  public static class ProtoConstants {

    // Turret
    public static final int kTurretZeroTicks = 2488;
    public static final double kMaxStringPotZero = 100;
    public static final double kMinStringPotZero = -1;

    // Hood
    public static final int kHoodZeroTicks = 2650;

    // Camera
    public static final String kCameraId = "A0";
  }
}
