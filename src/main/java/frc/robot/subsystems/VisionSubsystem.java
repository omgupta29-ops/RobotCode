// Copyright (c) FIRST and other WPILib contributors. 
// Open Source Software; you can modify and/or share it under the terms of 
// the WPILib BSD license file in the root directory of this project. 
 
package frc.robot.subsystems; 
 
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase; 
import frc.robot.LimelightHelpers; 
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
 
public class VisionSubsystem extends SubsystemBase { 
 
    // ------------------------------------------------------- 
    // MEASURE YOUR ROBOT AND UPDATE THESE VALUES (See Step 5) 
    // ------------------------------------------------------- 
    private static final double CAMERA_HEIGHT_METERS = 
        Units.inchesToMeters(20.0);  // Floor to center of Limelight lens 
    private static final double TARGET_HEIGHT_METERS = 
        Units.inchesToMeters(51.25); // Floor to center of AprilTag on goal 
                                     // SET THIS FROM THE 2026 GAME MANUAL 
    private static final double CAMERA_PITCH_RADIANS = 
        Units.degreesToRadians(11.0); // Upward tilt angle of Limelight 
    private static final double DESIRED_RANGE_METERS = 1.9; 
                                     // Shooting distance bumper to goal 
 
    // ------------------------------------------------------- 
    // PID TUNING CONSTANTS (See Step 5) 
    // ------------------------------------------------------- 
    private static final double kP_ROTATION = 0.45; 
    private static final double kP_DISTANCE = 1.75; 
 
    // Must match the name shown in the Limelight dashboard Settings tab 
    private static final String LIMELIGHT_NAME = "limelight"; 
 
    /** 
     * Returns [forwardSpeed, rotationSpeed] for auto-alignment. 
     * Returns [0.0, 0.0] if no target is visible. 
     * Forward speed drives to DESIRED_RANGE_METERS. 
     * Rotation speed centers the tag horizontally in frame. 
     */ 
    public double[] getAlignmentSpeeds() { 
        // TV: 1.0 if a target is visible, 0.0 if not 
        boolean hasTarget = LimelightHelpers.getTV(LIMELIGHT_NAME); 
 
        if (hasTarget) { 
            // TX: horizontal angle to target in degrees 
            // Negative = target is left, positive = target is right 
            double yaw = LimelightHelpers.getTX(LIMELIGHT_NAME); 
 
            // TY: vertical angle to target in degrees 
            // Used in trig-based distance calculation below 
            double pitch = LimelightHelpers.getTY(LIMELIGHT_NAME); 
 
            // Rotation: proportional to how far off-center the tag is 
            double rotationSpeed = yaw * kP_ROTATION; 
 
            // Distance via trigonometry using physical robot measurements 
            // More accurate than Limelight's built-in pixel-size estimate 
            double angleToTarget = CAMERA_PITCH_RADIANS 
                + Units.degreesToRadians(pitch); 
            double currentDist = 
                (TARGET_HEIGHT_METERS - CAMERA_HEIGHT_METERS) 
                / Math.tan(angleToTarget); 
 
            // Forward speed: positive = drive forward, negative = back up 
            double forwardSpeed = 
                (currentDist - DESIRED_RANGE_METERS) * kP_DISTANCE; 
            SmartDashboard.putNumber("CurrentDist", currentDist);
            SmartDashboard.putNumber("ForwardSpeed", forwardSpeed);

            // Safety caps: limit max speed in either direction 
            return new double[]{ 
                Math.max(-0.5, Math.min(0.5, forwardSpeed)),   // index 0 
                Math.max(-0.4, Math.min(0.4, rotationSpeed))  // index 1 
            }; 
        } 
 
        // No target visible — stop all movement 
        return new double[]{0.0, 0.0}; 
    } 
} 