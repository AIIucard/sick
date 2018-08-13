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

/**
 * XMLMarshler is a singleton, so the instantiation is restricted to one object.
 * The XMLMarshler coordinates the following actions:
 * <ul>
 * <li>Save the badge data in a XML file
 * <li>Load the badge data from a XML file
 * <li>Save the area data in a XML file
 * <li>Load the area data from a XML file
 * </ul>
 *
 */
public class XMLMarshler {

	private final static String AREAS_XML_FILE = "cfg" + File.separator + "areas.xml";
	private final static String BADGES_XML_FILE = "cfg" + File.separator + "badges.xml";

	private static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	private static Object lock = new Object();
	private static XMLMarshler instance = null;

	/**
	 * Use <code>getInstance</code> method!
	 * 
	 * @deprecated
	 */
	private XMLMarshler() {
		// Use getInstance
	}

	/**
	 * Realizes the singleton pattern with synchronized(lock), ensures that just one
	 * class can instantiate this class in one specific moment. If there is already
	 * an instance of this class, the method returns a reference.
	 *
	 * @return the new or referenced instance of this class.
	 */
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

	/**
	 * Save the area data of the application to a XML file. The XML file destination
	 * can be changed inside this class.
	 * 
	 * @param areaList
	 *            the area list to save.
	 * @throws JAXBException
	 *             if the data can't be parsed to XML.
	 */
	public void marshalAreaList(AreaList areaList) throws JAXBException {

		log.debug("Initialize JAXB context...");
		JAXBContext jaxbContext = JAXBContext.newInstance(AreaList.class);

		log.debug("Create marshaller...");
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		log.info("Marshaling areas...");
		jaxbMarshaller.marshal(areaList, new File(AREAS_XML_FILE));
	}

	/**
	 * Load the area data for the application from a XML file. The XML file
	 * destination can be changed inside this class.
	 * 
	 * @return the list of areas from the XML file.
	 * @throws JAXBException
	 *             if the data can't be parsed from XML.
	 */
	public AreaList unMarshalAreaList() throws JAXBException {

		log.debug("Initialize JAXB context...");
		JAXBContext jaxbContext = JAXBContext.newInstance(AreaList.class);

		log.debug("Create unmarshaller...");
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		log.info("Unmarshaling areas...");
		return (AreaList) jaxbUnmarshaller.unmarshal(new File(AREAS_XML_FILE));
	}

	/**
	 * Save the badge data of the application to a XML file. The XML file
	 * destination can be changed inside this class.
	 * 
	 * @param badgeList
	 *            the badge list to save.
	 * @throws JAXBException
	 *             if the data can't be parsed to XML.
	 */
	public void marshalBadgeList(BadgeList badgeList) throws JAXBException {

		log.debug("Initialize JAXB context...");
		JAXBContext jaxbContext = JAXBContext.newInstance(BadgeList.class);

		log.debug("Create marshaller...");
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		log.info("Marshaling badges...");
		jaxbMarshaller.marshal(badgeList, new File(BADGES_XML_FILE));
	}

	/**
	 * Load the badge data for the application from a XML file. The XML file
	 * destination can be changed inside this class.
	 * 
	 * @return the list of badges from the XML file.
	 * @throws JAXBException
	 *             if the data can't be parsed from XML.
	 */
	public BadgeList unMarshalBadgeList() throws JAXBException {

		log.debug("Initialize JAXB context...");
		JAXBContext jaxbContext = JAXBContext.newInstance(BadgeList.class);

		log.debug("Create unmarshaller...");
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		log.info("Unmarshaling badges...");
		return (BadgeList) jaxbUnmarshaller.unmarshal(new File(BADGES_XML_FILE));
	}
}
