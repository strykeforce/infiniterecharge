/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {

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

    // Turret Specific
    public static final int kCloseEnoughTurret = 100;

    // Command Specific
    public static final long kArmTimeToAccelerate = 1500;
    public static final int kArmSpeed = 10000;
  }

  public static final class DriveConstants {
    public static final double kDeadbandXLock = 0.2;
  }
}
