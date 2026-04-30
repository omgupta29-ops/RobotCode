// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.CANDriveSubsystem;
import frc.robot.subsystems.VisionSubsystem;

public class VisionAlign extends Command {

private final CANDriveSubsystem driveSubsystem;
private final VisionSubsystem visionSubsystem;

public VisionAlign(CANDriveSubsystem driveSubsystem, VisionSubsystem visionSubsystem) {
addRequirements(driveSubsystem);
this.driveSubsystem = driveSubsystem;
this.visionSubsystem = visionSubsystem;
}

@Override
public void initialize() {
}

@Override
public void execute() {
double[] speeds = visionSubsystem.getAlignmentSpeeds();
driveSubsystem.driveArcade(speeds[0], speeds[1]);
}

@Override
public void end(boolean interrupted) {
driveSubsystem.driveArcade(0, 0);
}

@Override
public boolean isFinished() {
return false;
}
}
