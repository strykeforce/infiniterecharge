package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogCommand extends InstantCommand {
    String msg;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public LogCommand(String msg) {
        this.msg = msg;
    }

    @Override
    public void initialize() {
        logger.info(msg);
    }
}
