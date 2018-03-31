package main.htw.emulator;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import main.htw.handler.RTLSConnectionManager;

public class EmulatorGUI extends FlowPane {
	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	public EmulatorGUI() {
		this.setPadding(new Insets(5, 0, 5, 0));
		this.setVgap(4);
		this.setHgap(4);
		this.setPrefWrapLength(170);
		this.setAlignment(Pos.CENTER);
		Button sendAreasButton = createSendAreasReplyButton();
		Button sendAllDivicesButton = createSendAllDeviceReplyButton();
		Button sendFenceNotifyButton = createSendFenceNotifyButton();
		this.getChildren().addAll(sendAreasButton, sendAllDivicesButton, sendFenceNotifyButton);
	}

	private Button createSendAreasReplyButton() {

		Button button = new Button();
		button.setText("Send Areas Reply");
		button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {

					JSONArray areas = new JSONArray();

					JSONObject area1 = new JSONObject();
					area1.put("id", 1);
					area1.put("name", "Level 1");

					JSONObject area2 = new JSONObject();
					area2.put("id", 2);
					area2.put("name", "Level 2");

					JSONObject area3 = new JSONObject();
					area3.put("id", 3);
					area3.put("name", "Level 3");

					areas.put(area1);
					areas.put(area2);
					areas.put(area3);

					RTLSConnectionManager rtlsConnectionManager = RTLSConnectionManager.getInstance();
					rtlsConnectionManager.onMessage(areas.toString());
					log.info(areas.toString());

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		return button;
	}

	private Button createSendAllDeviceReplyButton() {

		Button button = new Button();
		button.setText("Send Devices Reply");
		button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					JSONArray devices = new JSONArray();

					JSONObject device1 = new JSONObject();
					device1.put("timestamp", new Long("1512044640926"));
					device1.put("connected", false);
					device1.put("networkId", 23103);
					device1.put("anchorId", -1);
					device1.put("shortAddr", 844);
					device1.put("parentAddr", 1433);
					device1.put("networkRole", "END_DEVICE");
					device1.put("networkType", "IEEE_802_15_4");
					device1.put("appRole", "MOBILE");
					device1.put("deviceState", 2);
					device1.put("activated", true);
					device1.put("customName", "Uwe Gaul");
					device1.put("customType", "Staatssekretär");
					device1.put("hardwareName", "CHINA_BADGE");
					device1.put("softwareVersion", 16777218);
					device1.put("battery", 3.7);
					device1.put("rssi", -53);
					JSONArray rangingCapabilities1 = new JSONArray();
					rangingCapabilities1.put("RSSI_802_15_4");
					rangingCapabilities1.put("PMU_RBL");
					rangingCapabilities1.put("TOF_UWB");
					rangingCapabilities1.put("TDOA_UWB");
					device1.put("rangingCapabilities", rangingCapabilities1);
					device1.put("shortAddrAsHexString", "34C");
					device1.put("address", "8121069331292357452");
					device1.put("addressAsHexString", "70B3D5879000034C");
					device1.put("parentAddrAsHexString", "599");
					device1.put("softwareVersionAsString", "1.0.0_2");

					JSONObject device2 = new JSONObject();
					device2.put("timestamp", new Long("1512044956749"));
					device2.put("connected", false);
					device2.put("networkId", 23103);
					device2.put("anchorId", -1);
					device2.put("shortAddr", 845);
					device2.put("parentAddr", 1433);
					device2.put("networkRole", "END_DEVICE");
					device2.put("networkType", "IEEE_802_15_4");
					device2.put("appRole", "MOBILE");
					device2.put("deviceState", 2);
					device2.put("activated", true);
					device2.put("customName", "Horst Schneider");
					device2.put("customType", "n/a");
					device2.put("hardwareName", "CHINA_BADGE");
					device2.put("softwareVersion", 16777218);
					device2.put("battery", 3.7);
					device2.put("rssi", -49);
					JSONArray rangingCapabilities2 = new JSONArray();
					rangingCapabilities2.put("RSSI_802_15_4");
					rangingCapabilities2.put("PMU_RBL");
					rangingCapabilities2.put("TOF_UWB");
					rangingCapabilities2.put("TDOA_UWB");
					device2.put("rangingCapabilities", rangingCapabilities2);
					device2.put("shortAddrAsHexString", "34D");
					device2.put("address", "8121069331292357453");
					device2.put("addressAsHexString", "70B3D5879000034D");
					device2.put("parentAddrAsHexString", "599");
					device2.put("softwareVersionAsString", "1.0.0_2");

					devices.put(device1);
					devices.put(device2);

					RTLSConnectionManager rtlsConnectionManager = RTLSConnectionManager.getInstance();
					rtlsConnectionManager.onMessage(devices.toString());
					log.info(devices.toString());

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		return button;
	}

	private Button createSendFenceNotifyButton() {

		Button button = new Button();
		button.setText("Send Fence Notify");
		button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					int deviceNumber = (int) (Math.random() * 2) + 1;
					int levelNumber = (int) (Math.random() * 3) + 1;
					JSONObject notifyEvent = new JSONObject();
					notifyEvent.put("topic", "GEOFENCING_EVENT");
					JSONObject geofencingEvent = new JSONObject();
					geofencingEvent.put("message",
							"Device " + ((deviceNumber == 2) ? "'Uwe Gaul'" : "'Horst Schneider'") + " enters area "
									+ "'Level " + levelNumber + "'");
					geofencingEvent.put("timestamp", new Long("1470393041329"));
					geofencingEvent.put("eventType", "IN");
					geofencingEvent.put("areaId", levelNumber);
					geofencingEvent.put("address",
							((deviceNumber == 2) ? "8121069331292357453" : "8121069331292357452"));
					geofencingEvent.put("customName", ((deviceNumber == 2) ? "Uwe Gaul" : "Horst Schneider"));
					notifyEvent.put("payload", geofencingEvent);

					RTLSConnectionManager rtlsConnectionManager = RTLSConnectionManager.getInstance();
					rtlsConnectionManager.onMessage(notifyEvent.toString());
					log.info(notifyEvent.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		return button;
	}

}
