
// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.CANDriveSubsystem;
import frc.robot.subsystems.CANFuelSubsystem;
import frc.robot.subsystems.VisionSubsystem;

public class ExampleAuto extends SequentialCommandGroup {

public ExampleAuto(CANDriveSubsystem driveSubsystem, CANFuelSubsystem fuelSubsystem, VisionSubsystem visionSubsystem) {

addCommands(
new AutoDrive(driveSubsystem, -0.75, 0.0).withTimeout(1.25),

// STEP 1: Vision align (up to 5 seconds)
new VisionAlign(driveSubsystem, visionSubsystem)
.withTimeout(9.0),

// STEP 2: SpinUp (2.5 seconds)
new SpinUp(fuelSubsystem)
.withTimeout(frc.robot.Constants.FuelConstants.SPIN_UP_SECONDS1),

// STEP 3: Launch stationary (8 seconds)
new Launch(fuelSubsystem)
.withTimeout(8.0),

// STEP 4: Shake + Launch in parallel (12 seconds)
new ParallelCommandGroup(

new Launch(fuelSubsystem),

new SequentialCommandGroup(
new AutoDrive(driveSubsystem, 0.5, 0.0).withTimeout(0.4),
new AutoDrive(driveSubsystem, -0.5, 0.0).withTimeout(0.4),
new AutoDrive(driveSubsystem, 0.5, 0.0).withTimeout(0.4),
new AutoDrive(driveSubsystem, -0.5, 0.0).withTimeout(0.4),
new AutoDrive(driveSubsystem, 0.5, 0.0).withTimeout(0.4),
new AutoDrive(driveSubsystem, -0.5, 0.0).withTimeout(0.4),
new AutoDrive(driveSubsystem, 0.5, 0.0).withTimeout(0.4),
new AutoDrive(driveSubsystem, -0.5, 0.0).withTimeout(0.4),
new AutoDrive(driveSubsystem, 0.5, 0.0).withTimeout(0.4),
new AutoDrive(driveSubsystem, -0.5, 0.0).withTimeout(0.4),
new AutoDrive(driveSubsystem, 0.5, 0.0).withTimeout(0.4),
new AutoDrive(driveSubsystem, -0.5, 0.0).withTimeout(0.4),
new AutoDrive(driveSubsystem, 0.5, 0.0).withTimeout(0.4),
new AutoDrive(driveSubsystem, -0.5, 0.0).withTimeout(0.4),
new AutoDrive(driveSubsystem, 0.5, 0.0).withTimeout(0.4),
new AutoDrive(driveSubsystem, -0.5, 0.0).withTimeout(0.4)
)

).withTimeout(12.0)

);
}
}
