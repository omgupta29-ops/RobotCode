package frc.robot.subsystems;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.LimelightHelpers;

public class VisionSubsystem extends SubsystemBase {

// --- MEASURE AND UPDATE THESE ---
private static final double CAMERA_HEIGHT_METERS =
Units.inchesToMeters(18.0); // Replace with your measurement
private static final double TARGET_HEIGHT_METERS =
Units.inchesToMeters(45); // Replace with 2026 Manual value
private static final double CAMERA_PITCH_RADIANS =
Units.degreesToRadians(10.0); // Replace with your tilt angle
private static final double DESIRED_RANGE_METERS = 2.0; // Replace

// --- TUNING ---
private static final double kP_ROTATION = 0.04;
private static final double kP_DISTANCE = 0.6;

private static final String LIMELIGHT_NAME = "limelight";

public double[] getAlignmentSpeeds() {
boolean hasTarget = LimelightHelpers.getTV(LIMELIGHT_NAME);

if (hasTarget) {
double yaw = LimelightHelpers.getTX(LIMELIGHT_NAME);
double pitch = LimelightHelpers.getTY(LIMELIGHT_NAME);

double rotationSpeed = yaw * kP_ROTATION;

double angleToTarget = CAMERA_PITCH_RADIANS
+ Units.degreesToRadians(pitch);
double currentDist = (TARGET_HEIGHT_METERS - CAMERA_HEIGHT_METERS)
/ Math.tan(angleToTarget);

double forwardSpeed = (currentDist - DESIRED_RANGE_METERS)
* kP_DISTANCE;

return new double[]{
Math.max(-0.5, Math.min(0.5, forwardSpeed)),
Math.max(-0.4, Math.min(0.4, rotationSpeed))
};
}
return new double[]{0.0, 0.0};
}
}