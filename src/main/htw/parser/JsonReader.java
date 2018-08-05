package main.htw.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The JsonReader creates usable Java objects from a given JSON formatted
 * string. The JsonReader coordinates the following actions:
 * <li>*
 */
public class JsonReader {

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	private static JSONParser parser = new JSONParser();

	/**
	 * @param args
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public static InputStream getContent(final String args)
			throws IOException, NoSuchAlgorithmException, KeyManagementException {

		// Create a trust manager that does not validate certificate chains
		final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

			@Override
			public void checkClientTrusted(final X509Certificate[] chain, final String authType) {

			}

			@Override
			public void checkServerTrusted(final X509Certificate[] chain, final String authType) {

			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		} };

		// Install the all-trusting trust manager
		final SSLContext sslContext = SSLContext.getInstance("SSL");
		sslContext.init(null, trustAllCerts, null);

		// Create an ssl socket factory with our all-trusting manager
		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				return true;
			}
		});

		// All set up, we can get a resource through https now:
		final URL url = new URL(args);
		URLConnection connection = url.openConnection();
		return (InputStream) connection.getContent();
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	/**
	 * Parses JSON formatted content of a webpage to an array containing JSON
	 * objects which can be iterated on. If the method cannot read the content or
	 * the resulting JSON object contains errors an exception will be thrown. Other
	 * exceptions will be handled via logging them. The method will attempt to read
	 * the contents 10 times before timing out.
	 * 
	 * @param url
	 *            The URL from the webpage which houses the JSON formatted content
	 *            which will be parsed. The content of the URL has to be formatted
	 *            in <strong>UTF-8</strong>.
	 * @return Returns a JSONArray containing JSON object parsed from the webpage
	 *         content. <br>
	 *         If the method times out 10 times then <code>NULL</code> will be
	 *         returned.
	 * @throws IOException
	 *             Thrown when the URL cannot be accessed.
	 * @throws JSONException
	 *             Thrown when the JSON contains errors and cannot be parsed.
	 */
	public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
		int timeoutTrysCounter = 1;
		JSONArray jsonArray = null;
		while (jsonArray == null && timeoutTrysCounter < 10) {
			try {
				InputStream is = getContent(url);
				BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
				String jsonText = readAll(rd);
				Object obj = parser.parse(jsonText);
				jsonArray = (JSONArray) obj;
				log.info("Areas read from Zigpos: " + jsonArray.toJSONString());
				return jsonArray;
			} catch (Exception e) {
				log.error(
						"Can not read Areas from Zigpos! the following Exception Occured:  " + e.getLocalizedMessage());
				timeoutTrysCounter++;
				log.info("Try to read Areas again! " + timeoutTrysCounter + " try...");
			}
		}
		return null;
	}

	/**
	 * Parses JSON formatted content of a webpage to a JSON object. If the method
	 * cannot read the content or the resulting JSON object contains errors an
	 * exception will be thrown. Other exceptions will be handled via logging them.
	 * 
	 * @param url
	 *            The URL from the webpage which houses the JSON formatted content
	 *            which will be parsed. The content of the URL has to be formatted
	 *            in <strong>UTF-8</strong>.
	 * @return Returns a JSONObject parsed from the webpage content. <br>
	 *         If the method encounters an exception different from the ones it
	 *         throws it will return <code>NULL</code>.
	 * @throws IOException
	 *             Thrown when the URL cannot be accessed.
	 * @throws JSONException
	 *             Thrown when the JSON contains errors and cannot be parsed.
	 */
	public static JSONObject readJsonObjectFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			Object obj = parser.parse(jsonText);
			JSONObject json = (JSONObject) obj;
			return json;
		} catch (Exception e) {
			log.error("Can not read JSONObject from URL" + url + "! Got the following Exception: "
					+ e.getLocalizedMessage());
			return null;
		}
	}

	/**
	 * Debug method which parses JSON formatted content of a file to a JSON Array
	 * containing JSON objects which can be iterated on. Useful if you want to test
	 * something locally.
	 * 
	 * @param filepath
	 *            The filepath to the file containing the JSON formatted content.
	 * @return Returns a JSONObject parsed from the file content.
	 * @throws FileNotFoundException
	 *             Thrown if the file is not physically present under the given
	 *             filepath.
	 * @throws IOException
	 *             Thrown if the file cannot be accessed by the application.
	 * @throws ParseException
	 *             Thrown if the file content is not JSON formatted and the
	 *             JSONArray cannot be parsed.
	 */
	public static JSONArray readJsonFromFile(String filepath)
			throws FileNotFoundException, IOException, ParseException {
		JSONArray a = (JSONArray) parser.parse(new FileReader(filepath));

		return a;
	}
}