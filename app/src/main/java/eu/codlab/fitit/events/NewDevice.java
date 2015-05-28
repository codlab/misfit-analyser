package eu.codlab.fitit.events;

import com.misfitwearables.ble.shine.ShineDevice;

/**
 * Created by kevinleperf on 01/05/15.
 */
public class NewDevice {
    public ShineDevice device;
    public int rssi;

    public NewDevice(ShineDevice device, int rssi) {
        this.device = device;
        this.rssi = rssi;
    }
}
