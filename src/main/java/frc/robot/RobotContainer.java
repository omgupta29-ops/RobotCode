cat > /tmp/currentrobot/RobotCode-main/src/main/java/frc/robot/RobotContainer.java << 'EOF'
// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.
// ...existing code...
package frc.robot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import static frc.robot.Constants.OperatorConstants.*;
import frc.robot.commands.Drive;
import frc.robot.commands.Eject;
import frc.robot.commands.ExampleAuto;
import frc.robot.commands.Intake;
import frc.robot.commands.LaunchSequence;
import frc.robot.subsystems.CANDriveSubsystem;
import frc.robot.subsystems.CANFuelSubsystem;
import frc.robot.subsystems.VisionSubsystem;
// ...existing code...

public class RobotContainer {
  // The robot's subsystems
  private final CANDriveSubsystem driveSubsystem = new CANDriveSubsystem();
  private final CANFuelSubsystem fuelSubsystem = new CANFuelSubsystem();
  private final VisionSubsystem visionSubsystem = new VisionSubsystem();

  // The driver's controller
  private final CommandXboxController driverController = new CommandXboxController(
      DRIVER_CONTROLLER_PORT);
  // The operator's controller
  private final CommandXboxController operatorController = new CommandXboxController(
      OPERATOR_CONTROLLER_PORT);

  // Launcher throttle (0.0 .. 1.0). Start at 100%.
  private double launcherThrottle = 1.0;

  // The autonomous chooser
  private final SendableChooser<Command> autoChooser = new SendableChooser<>();

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    // publish initial throttle so other code/commands can read it from SmartDashboard
    SmartDashboard.putNumber("Launcher Throttle", launcherThrottle);

    configureBindings();
    // Set the options to show up in the Dashboard for selecting auto modes. If you
    // add additional auto modes you can add additional lines here with
    // autoChooser.addOption
    autoChooser.setDefaultOption("Autonomous", new ExampleAuto(driveSubsystem,
        fuelSubsystem, visionSubsystem));
  }

  private void adjustLauncherThrottle(double delta) {
    launcherThrottle += delta;
    // clamp between 0.0 and 1.0
    if (launcherThrottle < 0.0) {
      launcherThrottle = 0.0;
    } else if (launcherThrottle > 1.0) {
      launcherThrottle = 1.0;
    }
    SmartDashboard.putNumber("Launcher Throttle", launcherThrottle);
  }

  public double getLauncherThrottle() {
    return launcherThrottle;
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be
   * created via the {@link Trigger#Trigger(java.util.function.BooleanSupplier)}
   * constructor with an arbitrary predicate, or via the named factories in
   * {@link edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses
   * for {@link CommandXboxController Xbox}/
   * {@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller PS4}
   * controllers or
   * {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  private void configureBindings() {
    // While the left bumper on operator controller is held, intake Fuel
    operatorController.leftBumper().whileTrue(new Intake(fuelSubsystem));
    // While the right bumper on the operator controller is held, spin up for 1
    // second, then launch fuel. When the button is released, stop.
    operatorController.rightBumper().whileTrue(new LaunchSequence(fuelSubsystem));
    // While the A button is held on the operator controller, eject fuel back out
    // the intake
    operatorController.a().whileTrue(new Eject(fuelSubsystem));

    // Throttle adjustments:
    // Press the operator left-stick (below left bumper) to decrease throttle by 20%
    operatorController.leftStick()
        .onTrue(new InstantCommand(() -> adjustLauncherThrottle(-0.20)));
    // Press the operator right-stick (below right bumper) to increase throttle by 20%
    operatorController.rightStick()
        .onTrue(new InstantCommand(() -> adjustLauncherThrottle(0.20)));

    // Default: left stick Y controls forward/back, right stick X controls rotation.
    // Invert Y so pushing forward (negative on the controller) drives forwards.
    driveSubsystem.setDefaultCommand(new RunCommand(() -> {
      double forward = driverController.getLeftY() * DRIVE_SCALING;
      double rotation = driverController.getRightX() * ROTATION_SCALING;
      driveSubsystem.driveArcade(forward, rotation);
    }, driveSubsystem));

    // B button on driver controller activates vision alignment.
    // Hold B = robot rotates toward AprilTag and drives to target distance.
    // Release B = returns to manual Drive command automatically.
    driverController.b().whileTrue(new RunCommand(() -> {
      double[] speeds = visionSubsystem.getAlignmentSpeeds();
      // speeds[0] = forward/back speed, speeds[1] = rotation speed
      driveSubsystem.driveArcade(speeds[0], speeds[1]);
    }, driveSubsystem));

    // X button enables brake mode while held, returns to coast when released
    driverController.x().onTrue(new InstantCommand(() -> driveSubsystem.setBrakeMode(true)));
    driverController.x().onFalse(new InstantCommand(() -> driveSubsystem.setBrakeMode(false)));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    return autoChooser.getSelected();
  }
}
