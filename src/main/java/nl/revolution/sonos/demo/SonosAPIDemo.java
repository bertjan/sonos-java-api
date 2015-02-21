package nl.revolution.sonos.demo;

import nl.revolution.sonos.api.ZonePlayer;
import nl.revolution.sonos.api.ZonePlayers;
import nl.revolution.sonos.api.internal.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.SonosException;

import java.io.IOException;

public class SonosAPIDemo {

    private static final Logger LOG = LoggerFactory.getLogger(SonosAPIDemo.class);

    public static void main(String... args) throws SonosException, IOException {
        new SonosAPIDemo().doTest();
    }

    private void doTest() throws SonosException, IOException {
        Utils.configureLogging();

        // Find ZonePlayers in our network.
        ZonePlayers zonePlayers = ZonePlayers.discover();

        // Wait until zone has been found, or timeout (5s) expires.
        ZonePlayer zone = zonePlayers.get("woonkamer", 5000);
        if (zone == null) {
            LOG.error("Zone not found.");
            return;
        }

        // Log current ZonePlayer status.
        LOG.info("Current track: {}", zone.getCurrentTrackName());
        LOG.info("Current volume: {}, muted: {}", zone.getVolume(), zone.isMuted());
        LOG.info("Current queue contents: {}", zone.getQueue());

        // Play first item in queue at acceptable volume ;-)
        zone.setVolume(15);
        zone.playStartOfQueue();

        // Let it play for 5 secs, then skip to next queue item.
        Utils.sleep(5000);
        zone.nextTrack();

        // Let it play for 5 secs, then pause playback.
        Utils.sleep(5000);
        zone.pause();

        LOG.info("Done.");

        // Trigger JVM exit to kill all background threads.
        // Sleep a bit to give running commands time to finish.
        Utils.sleep(1000);
        System.exit(0);
    }

}
