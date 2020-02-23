/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.DigitalInput;

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

  private static DigitalInput digitalInput = new DigitalInput(9);

  public Constants() {
    if (digitalInput.get()) isCompBot = false;
    else isCompBot = true;

    if (isCompBot) {
      TurretConstants.kTurretZeroTicks = CompConstants.kTurretZeroTicks;
      HoodConstants.kHoodZeroTicks = CompConstants.kHoodZeroTicks;
    } else {
      TurretConstants.kTurretZeroTicks = ProtoConstants.kTurretZeroTicks;
      HoodConstants.kHoodZeroTicks = ProtoConstants.kHoodZeroTicks;
    }
  }

  public static final class IntakeConstants {
    // Subsystem Specific
    public static final double kIntakeSpeed = -0.5;
    public static final double kEjectSpeed = 0.25;
    public static final int kStallVelocity = 100;

    // Command Specific
    public static final long kTimeFullIntake = 1000;
    public static final long kShootDelayIntake = 500;
    public static final long kReverseTime = 500;
    public static final double kStallCount = 5;
  }

  public static final class MagazineConstants {
    // Subsystem Specific
    public static final double kOpenLoopLoad = 0.7;
    public static final double kOpenloopShoot = 1.0;
    public static final double kOpenloopReverse = -0.25;
    public static final double kOpenloopArmReverse = -0.2;

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
  }

  public static final class TurretConstants {
    // Subsystem Specific
    public static final int kCloseEnoughTurret = 40;
    public static int kTurretZeroTicks = 2488;
    public static final int kForwardLimit = 26000;
    public static final int kReverseLimit = -700;
    public static final double TURRET_TICKS_PER_DEGREE = 72.404;
    public static final double kWrapRange = 1; // FIXME
    public static final double kTurretMidpoint = 1; // FIXME
  }

  public static final class HoodConstants {
    // Subsystem Specific
    public static final double HOOD_TICKS_PER_DEGREE = 572;
    public static final int kCloseEnoughHood = 100;
    public static double kHoodZeroTicks = 1347;
  }

  public static final class DriveConstants {
    public static final double kDeadbandXLock = 0.2;
  }

  public static final class VisionConstants {
    public static final double VERTICAL_FOV = 48.8;
    public static final double HORIZ_FOV = 62.2;
    public static final double HORIZ_RES = 1280;
    public static final double TARGET_WIDTH_IN = 34.65;
    public static final double CAMERA_HEIGHT = 22;
    public static final double TARGET_HEIGHT = 98.25;

    public static final String kCameraID = "D0";
    public static final double kTurretDeadband = 3;

    public static final int kStableRange = 20;
    public static final int kStableCounts = 5;
  }

  public static class CompConstants {
    public static final int kTurretZeroTicks = 2488;
    public static final double kHoodZeroTicks = 2650;
  }

  public static class ProtoConstants {
    public static final int kTurretZeroTicks = 2488;
    public static final double kHoodZeroTicks = 2650;
  }
}
