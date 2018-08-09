package main.htw.manager;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.htw.xml.AreaList;
import main.htw.xml.BadgeList;

public class XMLMarshler {

	private final static String AREAS_XML_FILE = "cfg" + File.separator + "areas.xml";
	private final static String BADGES_XML_FILE = "cfg" + File.separator + "badges.xml";

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

	public void marshalBadgeList(BadgeList badgeList) throws JAXBException {

		log.debug("Initialize JAXB context...");
		JAXBContext jaxbContext = JAXBContext.newInstance(BadgeList.class);

		log.debug("Create marshaller...");
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		log.info("Marshaling badges...");
		jaxbMarshaller.marshal(badgeList, new File(BADGES_XML_FILE));
	}

	public BadgeList unMarshalBadgeList() throws JAXBException {

		log.debug("Initialize JAXB context...");
		JAXBContext jaxbContext = JAXBContext.newInstance(BadgeList.class);

		log.debug("Create unmarshaller...");
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		log.info("Unmarshaling badges...");
		return (BadgeList) jaxbUnmarshaller.unmarshal(new File(BADGES_XML_FILE));
	}
}
