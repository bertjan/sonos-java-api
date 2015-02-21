package nl.revolution.sonos.api.internal;

import ch.qos.logback.classic.Level;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class Utils {

    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {

        }
    }

    private static void setLogLevel(String name, ch.qos.logback.classic.Level level) {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(name)).setLevel(level);
    }

    public static void configureLogging() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        setLogLevel("ROOT", Level.INFO);
        setLogLevel("org.fourthline.cling", Level.ERROR);
        setLogLevel("org.tensin.sonos.control", Level.ERROR);
    }

}
