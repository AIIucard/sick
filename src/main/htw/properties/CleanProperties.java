package main.htw.properties;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

/**
 * Save the default properties and reverts all changes.
 *
 */
public class CleanProperties extends Properties {

	public CleanProperties() {
		this(null);
	}

	public CleanProperties(Properties defaults) {
		this.defaults = defaults;
	}

	private static class StripFirstLineStream extends FilterOutputStream {

		private boolean firstlineseen = false;

		public StripFirstLineStream(final OutputStream out) {
			super(out);
		}

		@Override
		public void write(final int b) throws IOException {
			if (firstlineseen) {
				super.write(b);
			} else if (b == '\n') {
				firstlineseen = true;
			}
		}
	}

	private static final long serialVersionUID = 7567765340218227372L;

	@Override
	public synchronized Enumeration<Object> keys() {
		return Collections.enumeration(new TreeSet<>(super.keySet()));
	}

	@Override
	public void store(final OutputStream out, final String comments) throws IOException {
		super.store(new StripFirstLineStream(out), null);
	}
}