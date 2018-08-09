package main.htw.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.htw.manager.CFGPropertyManager;

/**
 * Handler interface for all handler in this project. Provides a logger for all
 * handler classes.
 */
public class SickHandler {

	protected static Logger log = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

	protected static CFGPropertyManager propManager;
}
