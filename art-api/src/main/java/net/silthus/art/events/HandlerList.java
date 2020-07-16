/*
 * Copyright 2020 ART-Framework Contributors (https://github.com/Silthus/art-framework)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.silthus.art.events;

import java.util.*;

/**
 * A list of event handlers, stored per-event. Based on lahwran's fevents.
 */
public class HandlerList {

    /**
     * List of all HandlerLists which have been created, for use in bakeAll()
     */
    private static final ArrayList<HandlerList> allLists = new ArrayList<>();
    /**
     * Dynamic handler lists. These are changed using register() and
     * unregister() and are automatically baked to the handlers array any
     * time they have changed.
     */
    private final EnumMap<EventPriority, ArrayList<RegisteredEvent>> handlerslots;
    /**
     * Handler array. This field being an array is the key to this system's speed.
     */
    private volatile RegisteredEvent[] handlers = null;

    /**
     * Create a new handler list and initialize using EventPriority
     * The HandlerList is then added to meta-list for use in bakeAll()
     */
    public HandlerList() {

        handlerslots = new EnumMap<>(EventPriority.class);
        for (EventPriority o : EventPriority.values()) {
            handlerslots.put(o, new ArrayList<>());
        }
        synchronized (allLists) {
            allLists.add(this);
        }
    }

    /**
     * Bake all handler lists. Best used just after all normal event
     * registration is complete, ie just after all plugins are loaded if
     * you're using fevents in a plugin system.
     */
    public static void bakeAll() {

        synchronized (allLists) {
            for (HandlerList h : allLists) {
                h.bake();
            }
        }
    }

    /**
     * Bake HashMap and ArrayLists to 2d array - does nothing if not necessary
     */
    public synchronized void bake() {

        if (handlers != null) return; // don't re-bake when still valid
        List<RegisteredEvent> entries = new ArrayList<>();
        for (Map.Entry<EventPriority, ArrayList<RegisteredEvent>> entry : handlerslots.entrySet()) {
            entries.addAll(entry.getValue());
        }
        handlers = entries.toArray(new RegisteredEvent[0]);
    }

    /**
     * Unregister all listeners from all handler lists.
     */
    public static void unregisterAll() {

        synchronized (allLists) {
            for (HandlerList h : allLists) {
                for (List<RegisteredEvent> list : h.handlerslots.values()) {
                    list.clear();
                }
                h.handlers = null;
            }
        }
    }

    /**
     * Unregister a specific listener from all handler lists.
     *
     * @param listener listener to unregister
     */
    public static void unregisterAll(ArtEventListener listener) {

        synchronized (allLists) {
            for (HandlerList h : allLists) {
                h.unregister(listener);
            }
        }
    }

    /**
     * Remove a specific listener from this handler
     *
     * @param listener listener to remove
     */
    public synchronized void unregister(ArtEventListener listener) {

        boolean changed = false;
        for (List<RegisteredEvent> list : handlerslots.values()) {
            for (ListIterator<RegisteredEvent> i = list.listIterator(); i.hasNext(); ) {
                if (i.next().getListener().equals(listener)) {
                    i.remove();
                    changed = true;
                }
            }
        }
        if (changed) handlers = null;
    }

    /**
     * Get a list of all handler lists for every event type
     *
     * @return the list of all handler lists
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<HandlerList> getHandlerLists() {

        synchronized (allLists) {
            return (ArrayList<HandlerList>) allLists.clone();
        }
    }

    /**
     * Register a collection of new listeners in this handler list
     *
     * @param listeners listeners to register
     */
    public void registerAll(Collection<RegisteredEvent> listeners) {

        for (RegisteredEvent listener : listeners) {
            register(listener);
        }
    }

    /**
     * Register a new listener in this handler list
     *
     * @param listener listener to register
     */
    public synchronized void register(RegisteredEvent listener) {

        if (handlerslots.get(listener.getPriority()).contains(listener)) {
            throw new IllegalStateException("This listener is already registered to priority " + listener.getPriority().toString());
        }
        handlers = null;
        handlerslots.get(listener.getPriority()).add(listener);
    }

    /**
     * Remove a listener from a specific order slot
     *
     * @param listener listener to remove
     */
    public synchronized void unregister(RegisteredEvent listener) {

        if (handlerslots.get(listener.getPriority()).remove(listener)) {
            handlers = null;
        }
    }

    /**
     * Get the baked registered listeners associated with this handler list
     *
     * @return the array of registered listeners
     */
    public RegisteredEvent[] getRegisteredListeners() {

        RegisteredEvent[] handlers;
        while ((handlers = this.handlers) == null) bake(); // This prevents fringe cases of returning null
        return handlers;
    }
}
