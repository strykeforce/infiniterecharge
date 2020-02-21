/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.commands.TeleopDriveCommand;
import frc.robot.controls.Controls;
import frc.robot.subsystems.*;
import org.strykeforce.thirdcoast.telemetry.TelemetryController;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {

  public static TelemetryService TELEMETRY;

  // The robot's subsystems and commands are defined here...
  public static DriveSubsystem DRIVE;
  public static MagazineSubsystem MAGAZINE;
  public static Controls CONTROLS;
  public static IntakeSubsystem INTAKE;
  public static ShooterSubsystem SHOOTER;
  public static HoodSubsystem HOOD;
  public static TurretSubsystem TURRET;

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    TELEMETRY = new TelemetryService(TelemetryController::new);
    DRIVE = new DriveSubsystem();
    MAGAZINE = new MagazineSubsystem();
    INTAKE = new IntakeSubsystem();
    SHOOTER = new ShooterSubsystem();
    CONTROLS = new Controls();
    HOOD = new HoodSubsystem();
    TURRET = new TurretSubsystem();
    DRIVE.setDefaultCommand(new TeleopDriveCommand());

    // Configure the button bindings
    configureButtonBindings();
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
   * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {}

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An ExampleCommand will run in autonomous
    return null; // FIXME
  }
}
