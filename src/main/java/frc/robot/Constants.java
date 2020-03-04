/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.DigitalInput;
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

  public static boolean isCompBot;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static DigitalInput digitalInput = new DigitalInput(9);

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

  public static final class IntakeConstants {
    // Subsystem Specific
    public static final double kIntakeSpeed = -0.5;
    public static final double kEjectSpeed = 0.25;
    public static final int kStallVelocity = 100;
    public static final double kDoublePressMaxTime = 0.5; // Timer class returns time in seconds

    // Command Specific
    public static final long kTimeFullIntake = 1000;
    public static final long kShootDelayIntake = 0;
    public static final long kReverseTime = 500;
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
    public static final long kArmTimeToShooterOn = 500;
    public static final int kBeamStableCounts = 5;
  }

  public static final class ShooterConstants {
    // Shooter Specific
    public static final double kOpenloopShoot = 0.5;
    public static final double kOpenloopArmReverse = -0.2;
    public static final int kCloseEnough = 1000;
    public static final int kStableCounts = 5;

    // Command Specific
    public static final long kArmTimeToAccelerate = 1500;
    public static final int kArmSpeed = 10000;
    public static final int kBatterShotVelocity = 11000; // 12-27 inches (2 feet)
  }

  public static final class TurretConstants {
    // Subsystem Specific
    public static int kTurretZeroTicks = 2488;
    public static final int kForwardLimit = 26095; // 26000
    public static final int kReverseLimit = -100; // -700
    public static final double TURRET_TICKS_PER_DEGREE = 72.404;
    public static double kMaxStringPotZero = 500;
    public static double kMinStringPotZero = 1000;

    public static final int kCloseEnoughTurret = 40;
    public static final int kMaxShootError = 5000;
    public static final double kSweepRange = 20;
    public static final double kWrapRange = 1; // FIXME
    public static final double kTurretMidpoint = 13_000; // FIXME
    public static final double loadAngle = 25;
    public static final int kBatterShotTicks = 19550; // FIXME
  }

  public static final class HoodConstants {
    // Subsystem Specific
    public static final double HOOD_TICKS_PER_DEGREE = 572;
    public static final int kCloseEnoughHood = 100;
    public static int kHoodZeroTicks; // gut check: 149
    public static final int kForwardSoftLimit = 9000; // 10000
    public static final int kReverseSoftLimit = 0; // 0
    public static final int kOffsetZeroTicks = 1820;
    public static final int kBatterShotTicks = 0; // FIXME
  }

  public static final class ClimberConstants {
    public static final double kSlowUpOutput = 0.25;
    public static final double kSlowDownOutput = -0.25;
    public static final double kFastUpOutput = 1.0;
    public static final double kFastDownOutput = -1.0;
    public static final double kHoldOutput = -0.05;

    public static final double kRatchetEngage = 0.5;
    public static final double kRatchetDisable = 0.15;
  }

  public static final class DriveConstants {
    public static final double kDeadbandXLock = 0.2;
  }

  public static final class VisionConstants {
    public static final double VERTICAL_FOV = 48.8;
    public static final double HORIZ_FOV = 57.999; // 50.8 //146
    public static final double HORIZ_RES = 640; // 1280
    public static final double TARGET_WIDTH_IN = 39.5; // 34.6
    public static final double CAMERA_HEIGHT = 20.75;
    public static final double TARGET_HEIGHT = 98.5;
    public static String kCameraID = "A0";

    public static final int kStableRange = 20;
    public static final int kStableCounts = 5;

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
  }

  public static class CompConstants {
    // Turret
    public static final int kTurretZeroTicks = 1486;
    public static final double kMaxStringPotZero = 138;
    public static final double kMinStringPotZero = 52;

    // Hood
    public static final int kHoodZeroTicks = 2008; // gut check: 162

    // Camera
    public static final String kCameraId = "A0";
  }

  public static class ProtoConstants {
    // Turret
    public static final int kTurretZeroTicks = 2488;
    public static final double kMaxStringPotZero = 95;
    public static final double kMinStringPotZero = 13;

    // Hood
    public static final int kHoodZeroTicks = 2650;

    // Camera
    public static final String kCameraId = "A0";
  }
}
