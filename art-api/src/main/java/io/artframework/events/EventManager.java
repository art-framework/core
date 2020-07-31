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

package io.artframework.events;

import io.artframework.IllegalArtAccessException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class EventManager {

    public static <T extends Event> T callEvent(T event) {

        HandlerList handlerlist = event.getHandlers();
        handlerlist.bake();

        RegisteredEvent[] handlers = handlerlist.getRegisteredListeners();

        for (RegisteredEvent listener : handlers) {
            try {
                listener.callTrigger(event);
            } catch (EventException e) {
                e.printStackTrace();
            }
        }
        return event;
    }

    public static void registerListeners(EventListener listener) {

        for (Map.Entry<Class<? extends Event>, Set<RegisteredEvent>> entry : createRegisteredEvent(listener).entrySet()) {
            getTriggerListeners(getRegistrationClass(entry.getKey())).registerAll(entry.getValue());
        }
    }

    public static void unregisterListeners(EventListener listener) {

        for (HandlerList handlerList : HandlerList.getHandlerLists()) {
            handlerList.unregister(listener);
        }
    }

    public static void unregisterAll() {

        HandlerList.unregisterAll();
    }

    private static HandlerList getTriggerListeners(Class<? extends Event> type) {

        try {
            Method method = getRegistrationClass(type).getDeclaredMethod("getHandlerList");
            method.setAccessible(true);
            return (HandlerList) method.invoke(null);
        } catch (Exception e) {
            throw new IllegalArtAccessException(e.toString(), e);
        }
    }

    private static Class<? extends Event> getRegistrationClass(Class<? extends Event> clazz) {

        try {
            clazz.getDeclaredMethod("getHandlerList");
            return clazz;
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null
                    && !clazz.getSuperclass().equals(Event.class)
                    && Event.class.isAssignableFrom(clazz.getSuperclass())) {
                return getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
            } else {
                throw new IllegalArtAccessException("Unable to find handler list for event " + clazz.getName());
            }
        }
    }

    public static Map<Class<? extends Event>, Set<RegisteredEvent>> createRegisteredEvent(EventListener listener) {

        Map<Class<? extends Event>, Set<RegisteredEvent>> ret = new HashMap<>();
        Set<Method> methods;

        Method[] publicMethods = listener.getClass().getMethods();
        methods = new HashSet<>(publicMethods.length, Float.MAX_VALUE);
        Collections.addAll(methods, publicMethods);
        Collections.addAll(methods, listener.getClass().getDeclaredMethods());

        for (final Method method : methods) {

            if (!method.isAnnotationPresent(EventHandler.class)) continue;

            EventHandler annotation = method.getAnnotation(EventHandler.class);
            final Class<?> checkClass = method.getParameterTypes()[0];

            if (!Event.class.isAssignableFrom(checkClass) || method.getParameterTypes().length != 1) {
                continue;
            }

            final Class<? extends Event> eventClass = checkClass.asSubclass(Event.class);
            method.setAccessible(true);
            Set<RegisteredEvent> eventSet = ret.computeIfAbsent(eventClass, k -> new HashSet<>());

            EventExecutor executor = new EventExecutor() {
                public void execute(EventListener listener, Event event) throws EventException {

                    try {
                        if (!eventClass.isAssignableFrom(event.getClass())) {
                            return;
                        }
                        method.invoke(listener, event);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new EventException(e.getMessage(), e.getCause());
                    }
                }
            };

            eventSet.add(new RegisteredEvent(listener, executor, annotation) {
                @Override
                protected void call(Event trigger) throws EventException {

                    executor.execute(listener, trigger);
                }
            });
        }
        return ret;
    }
}
