package nl.revolution.sonos.api;

import nl.revolution.sonos.api.internal.Utils;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.model.message.header.UDAServiceTypeHeader;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.SonosConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ZonePlayers {

    private static final Logger LOG = LoggerFactory.getLogger(ZonePlayers.class);

    private List<ZonePlayer> zones = Collections.synchronizedList(new ArrayList<>());

    public static ZonePlayers discover() {
        ZonePlayers zonePlayers = new ZonePlayers();

        final UpnpService lookupService = new UpnpServiceImpl();
        final UpnpService upnpService = new UpnpServiceImpl(new DefaultRegistryListener() {
            @Override
            public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
                if (zonePlayers.isSonos(device)) {
                    zonePlayers.add(new ZonePlayer(device, lookupService));
                }
            }
        });

        upnpService.getControlPoint().search(
                new UDAServiceTypeHeader(new UDAServiceType(SonosConstants.AV_TRANSPORT)), 120);

        return zonePlayers;
    }

    public ZonePlayer get(String zoneName, int timeout) {
        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < timeout) {
            for (ZonePlayer zone : zones) {
                if (zone.getZoneName().equalsIgnoreCase(zoneName)) {
                    return zone;
                }
            }

            Utils.sleep(100);
        }

        // Not found, timeout expired.
        LOG.info("returning null");
        return null;
    }

    private void add(ZonePlayer zone) {
        zones.add(zone);
    }

    private boolean isSonos(RemoteDevice device) {
        return "urn:schemas-upnp-org:device:ZonePlayer:1".equals(device.getType().toString());
    }

}
