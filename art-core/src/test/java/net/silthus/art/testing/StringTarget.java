package net.silthus.art.testing;

import lombok.Data;
import net.silthus.art.api.trigger.Target;

@Data
public class StringTarget implements Target<String> {

    private final String target;

    @Override
    public String getUniqueId() {
        return target.hashCode() + "";
    }
}
