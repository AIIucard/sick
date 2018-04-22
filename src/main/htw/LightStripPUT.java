package main.htw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LightStripPUT {
	// http://localhost:8080/RESTfulExample/json/product/post
	public static void main(String[] args) {

		try {

			URL url = new URL("http://192.168.8.1/api/C02773CB34/lights/1/state");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", "application/json");

			// on = true | off = false
			// xy = colore
			// \"xy\": [0.734662, 0.265047] = rot
			// \"xy\": [0.499226, 0.478163] = gelb
			// \"xy\": [0.126289, 0.815775] = grün
			String input = "{ \"on\": false, \"xy\": [0.734662, 0.265047]}";

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {

				System.out.println(output);
			}

			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();

		}

	}
}
