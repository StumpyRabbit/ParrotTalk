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

import java.util.ArrayList;
import java.util.List;

public class EventEngine implements EventEngineInterface {
	protected List<Listener<?>> listeners = new ArrayList<Listener<?>>();

    @SuppressWarnings("unchecked")
    @Override
	public <GEvent> void fire(GEvent event) {
        for (final Listener<?> listener : listeners) {
            try {
                	((Listener<GEvent>)listener).handleGenericEvent(event);
            } catch (EventNotHandledException notHandledEx) {
            } catch (Exception handleException) {
            	System.out.println("event engine fire error: " + getClass().getSimpleName() + ": " + handleException.getLocalizedMessage());
            }
        }
    }
    public synchronized void addListener(final Listener<?> handler) { listeners.add(handler); } 
    public synchronized void removeListener(final Listener<?> handler) { listeners.remove(handler); }
    public synchronized void removeAllListeners() { listeners.clear(); }
	public void forwardEventsTo(EventEngine engine) {
		if(getForwarderListenerFor(engine) == null) {
			addListener(new ForwardingListener<Object>(Object.class, engine));
		}
	}
	public void unforwardEventsTo(EventEngine engine) {
		ForwardingListener<?> forwardingListener = getForwarderListenerFor(engine);
		if(forwardingListener != null) {
			removeListener(forwardingListener);
		}
	}

	public String toString() { return getClass().getSimpleName(); }

	private ForwardingListener<?> getForwarderListenerFor(EventEngine engine) {
		ForwardingListener<?> listener = null;
		for(Listener<?> aListener : listeners) {
			if(aListener.getClass().isAssignableFrom(ForwardingListener.class)) {
				listener = (ForwardingListener<?>)aListener; 
				if(listener.getForwardedEngine().equals(engine)) {
					return listener;
				}
			}
		}
		return listener;
	}

	protected static class ForwardingListener<GEvent> extends Listener<GEvent> {
		private EventEngine eventEngine;

		public ForwardingListener(Class<GEvent> eventClazz, EventEngine eventProducer) {
			super(eventClazz);
			this.eventEngine = eventProducer;
		}

		public EventEngine getForwardedEngine() { return eventEngine; }

		@Override
		protected void handle(GEvent event) {
			eventEngine.fire(event);
		}
	}
}
