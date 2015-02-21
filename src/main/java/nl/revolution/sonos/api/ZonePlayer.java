package nl.revolution.sonos.api;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.control.MediaRendererDevice;
import org.tensin.sonos.model.Entry;
import org.tensin.sonos.model.PlayMode;
import org.tensin.sonos.model.PositionInfo;

import java.util.ArrayList;
import java.util.List;

public class ZonePlayer {

    private static final Logger LOG = LoggerFactory.getLogger(ZonePlayer.class);

    private org.tensin.sonos.control.ZonePlayer wrappedZonePlayer;

    private String zoneName;
    private MediaRendererDevice mediaRenderer;

    private RemoteDevice remoteDevice;
    private UpnpService upnpService;

    public ZonePlayer(RemoteDevice remoteDevice, UpnpService upnpService) {
        this.remoteDevice = remoteDevice;
        this.upnpService = upnpService;
        wrappedZonePlayer = new org.tensin.sonos.control.ZonePlayer(upnpService, remoteDevice);
        zoneName = wrappedZonePlayer.getDevicePropertiesService().getZoneAttributes().getName();
        mediaRenderer = wrappedZonePlayer.getMediaRendererDevice();
    }

    public void play() {
        mediaRenderer.getRenderingControlService().setMute(false);
        mediaRenderer.getAvTransportService().setPlayMode(PlayMode.NORMAL);
        mediaRenderer.getAvTransportService().play();
    }

    public void stop() {
        mediaRenderer.getAvTransportService().stop();
    }

    public void pause() {
        mediaRenderer.getAvTransportService().pause();
    }

    public boolean isMuted() {
        return getNewMediaRenderer().getRenderingControlService().getMute();
    }

    public void toggleMute() {
        mediaRenderer.getRenderingControlService().setMute(!isMuted());
    }

    public int getVolume() {
        return getNewMediaRenderer().getRenderingControlService().getVolume();
    }

    public String getCurrentTrackName() {
        PositionInfo mediaInfo = getNewMediaRenderer().getAvTransportService().getPositionInfo();
        String trackName = mediaInfo.getTrackMetaData().getCreator() + " - " + mediaInfo.getTrackMetaData().getTitle();
        return trackName;
    }

    public List<String> getQueue() {
        try {
            List<String> queue = new ArrayList<>();
            List<Entry> entries = wrappedZonePlayer.getMediaServerDevice().getContentDirectoryService().getQueue(0, Integer.MAX_VALUE);
            for (Entry entry : entries) {
                queue.add(entry.getCreator() + " - " + entry.getTitle());
            }
            return queue;
        } catch (SonosException e) {
            LOG.error("Error while fetching queue: ", e);
            return null;
        }

    }

    public void playStartOfQueue() {
        try {
            wrappedZonePlayer.playQueueEntry(0);
        } catch (SonosException e) {
            LOG.error("Error while moving to start of queue: ", e);
        }
    }

    public void setVolume(int volume) {
        mediaRenderer.getRenderingControlService().setVolume(volume);
    }

    public String getZoneName() {
        return zoneName;
    }

    public void nextTrack() {
        mediaRenderer.getAvTransportService().next();
    }

    public void previousTrack() {
        mediaRenderer.getAvTransportService().previous();
    }

    private MediaRendererDevice getNewMediaRenderer() {
        return new org.tensin.sonos.control.ZonePlayer(upnpService, remoteDevice).getMediaRendererDevice();
    }

}
