/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2003, 2016 Robert Withers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * ******************************************************************************
 * murmur/whisper would not be possible without the ideas, implementation, 
 * brilliance and passion of the Squeak/Pharo communities and the cryptography 
 * team, which are this software's foundation.
 *******************************************************************************/

package club.callistohouse.utils.events;

import java.io.IOException;

public abstract class Listener<GEvent> {
    private final Class<GEvent> eventClazz;

	public Listener(final Class<GEvent> eventClazz) { this.eventClazz = eventClazz; }

	@SuppressWarnings("unchecked")
	public void handleGenericEvent(final Object event) throws ClassNotFoundException, IOException {
    	if(canHandle(event)) {
    		handle((GEvent)event);
    	} else {
    		throw new EventNotHandledException("Event not handled: " + event);
    	}
    }

	@SuppressWarnings("unchecked")
	public boolean canHandle(final Object event) {
    	return eventClazz.isAssignableFrom(event.getClass()) && match((GEvent)event);
    }

	protected abstract void handle(GEvent event) throws ClassNotFoundException, IOException;
	protected boolean match(final GEvent event) { return true; };

	public Runnable getRunnable(final Object event) {
		return new Runnable() { 
			public void run() { 
				try {
					handleGenericEvent(event);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} }};
	}

	// private and protected static methods
    protected static boolean dosShellMatch(final String string1, final String string2) {
		String regex1 = convertDOSShellToRegex(string1); String regex2 = convertDOSShellToRegex(string2);
		return regex1.matches(regex2) || regex2.matches(regex1);
	}
	private static String convertDOSShellToRegex(final String string) {
		return  string == null ? "" : string.replace(".", "\\.").replace("*", ".*").replace("?", ".?");
	}
}
