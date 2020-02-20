package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.TalonSRXItem;

public class TurretSubsystem extends SubsystemBase {
    private static final DriveSubsystem DRIVE = RobotContainer.DRIVE;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static TalonSRX turret;

    private static double targetTurretPosition = 0;

    private static int turretStableCounts = 0;

    private static final int TURRET_ID = 42;

    private static final double TURRET_TICKS_PER_DEGREE =
            Constants.ShooterConstants.TURRET_TICKS_PER_DEGREE;

    private static final double kTurretZeroTicks = Constants.ShooterConstants.kTurretZero;
    private static final double kWrapRange = Constants.ShooterConstants.kWrapRange;
    private static final double kTurretMidpoint = Constants.ShooterConstants.kTurretMidpoint;

    public TurretSubsystem() {
        configTalons();
    }

    private void configTalons() {
        TalonSRXConfiguration turretConfig = new TalonSRXConfiguration();

        // turret setup
        turret = new TalonSRX(TURRET_ID);
        turret.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(true, 20, 25, 0.04));
        turretConfig.forwardSoftLimitThreshold = 26000;
        turretConfig.reverseSoftLimitThreshold = -700;
        turretConfig.forwardSoftLimitEnable = true;
        turretConfig.reverseSoftLimitEnable = true;
        turretConfig.slot0.kP = 1;
        turretConfig.slot0.kI = 0;
        turretConfig.slot0.kD = 40;
        turretConfig.slot0.kF = 0;
        turretConfig.slot0.integralZone = 0;
        turret.configAllSettings(turretConfig);

        TelemetryService telService = RobotContainer.TELEMETRY;
        telService.stop();
        telService.register(new TalonSRXItem(turret, "ShooterTurret"));
        telService.start();
    }

    public boolean zeroTurret() {
        boolean didZero = false;
        if (!turret.getSensorCollection().isFwdLimitSwitchClosed()) { // FIXME
            int absPos = turret.getSensorCollection().getPulseWidthPosition() & 0xFFF;
            // inverted because absolute and relative encoders are out of phase
            int offset = (int) -(absPos - kTurretZeroTicks);
            turret.setSelectedSensorPosition(offset);
            didZero = true;
            logger.info(
                    "Turret zeroed; offset: {} zeroTicks: {} absPosition: {}",
                    offset,
                    kTurretZeroTicks,
                    absPos);
        } else {
            turret.configPeakOutputForward(0, 0);
            turret.configPeakOutputReverse(0, 0);
            logger.error("Turret zero failed. Killing turret...");
        }

        turret.configForwardLimitSwitchSource(
                LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.Disabled);

        return didZero;
    }

    public void rotateTurret(double offset) {
        double currentAngle = turret.getSelectedSensorPosition() / TURRET_TICKS_PER_DEGREE;
        double targetAngle = currentAngle + offset;
        if (targetAngle <= kWrapRange && turret.getSelectedSensorPosition() > kTurretMidpoint
                || targetAngle < 0) {
            targetAngle += 360;
        }
        double setPoint = targetAngle * TURRET_TICKS_PER_DEGREE;
        setTurret(setPoint);
    }

    public void setTurretAngle(double targetAngle) {
        if (targetAngle <= kWrapRange && turret.getSelectedSensorPosition() > kTurretMidpoint
                || targetAngle < 0) {
            targetAngle += 360;
        }
        double setPoint = targetAngle * TURRET_TICKS_PER_DEGREE;
        setTurret(setPoint);
    }

    public void seekTarget() {
        double bearing = Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360) + 270;
        double setPoint = bearing * TURRET_TICKS_PER_DEGREE;
        logger.info("Seeking Target at angle = {}", bearing);
        logger.info("Seeking Target at position = {}", setPoint);
        setTurret(setPoint);
    }

    private void setTurret(double setPoint) {
        targetTurretPosition = setPoint;
        turret.set(ControlMode.Position, setPoint);
    }

    public void turretOpenLoop(double output) {
        turret.set(ControlMode.PercentOutput, output);
    }


    public boolean turretAtTarget() {
        double currentTurretPosition = turret.getSelectedSensorPosition();
        if (Math.abs(targetTurretPosition - currentTurretPosition)
                > Constants.ShooterConstants.kCloseEnoughTurret) {
            turretStableCounts = 0;
        } else {
            turretStableCounts++;
        }
        if (turretStableCounts >= Constants.ShooterConstants.kStableCounts) {
            logger.info("Turret at position {}", targetTurretPosition);
            return true;
        } else {
            return false;
        }
    }


}
