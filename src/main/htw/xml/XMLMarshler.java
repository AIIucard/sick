package main.htw.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLMarshler {

	private final static String AREAS_XML_FILE = "cfg" + File.separator + "areas.xml";

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	private static Object lock = new Object();
	private static XMLMarshler instance = null;

	private XMLMarshler() {
		// Use getInstance
	}

	public static XMLMarshler getInstance() throws IOException {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new XMLMarshler();
				}
			}
		}
		return (instance);
	}

	public void marshalAreaList(AreaList areaList) throws JAXBException {

		log.debug("Initialize JAXB context...");
		JAXBContext jaxbContext = JAXBContext.newInstance(AreaList.class);

		log.debug("Create marshaller...");
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		log.info("Marshaling areas...");
		jaxbMarshaller.marshal(areaList, new File(AREAS_XML_FILE));
	}

	public AreaList unMarshalAreaList() throws JAXBException {

		log.debug("Initialize JAXB context...");
		JAXBContext jaxbContext = JAXBContext.newInstance(AreaList.class);

		log.debug("Create unmarshaller...");
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		log.info("Unmarshaling areas...");
		return (AreaList) jaxbUnmarshaller.unmarshal(new File(AREAS_XML_FILE));
	}

	// public void loadProperties() throws IOException {
	//
	// // create and load fence properties
	// FileInputStream in = new FileInputStream(VIRTUAL_FENCE_PROPERTIES_FILE);
	//
	// try {
	// log.info("Load virtual fence properties file...");
	// prop.loadFromXML(in);
	// in.close();
	// log.info("Virtual fence properties loaded.");
	// } catch (Throwable th) {
	// log.error("Cannot load virtual fence properties!\n" +
	// th.getLocalizedMessage());
	// }
	// }

	// public void storeProperties() throws IOException {
	// FileOutputStream out = new FileOutputStream(VIRTUAL_FENCE_PROPERTIES_FILE);
	// prop.storeToXML(out, "");
	// out.close();
	// log.info("Stored virtual fence properties.");
	// }
	//
	// public String getProperty(String key) {
	// return prop.getProperty(key);
	// }
}
