package io.artframework.bukkit.targets;

import io.artframework.AbstractTarget;
import org.bukkit.event.Event;

import java.util.UUID;

public class BukkitEventTarget extends AbstractTarget<Event> {

    private final UUID uuid = UUID.randomUUID();

    public BukkitEventTarget(Event source) {
        super(source);
    }

    @Override
    public String uniqueId() {

        return uuid.toString();
    }
}
