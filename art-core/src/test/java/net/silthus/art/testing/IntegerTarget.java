package net.silthus.art.testing;

import lombok.Data;
import net.silthus.art.api.target.Target;

@Data
public class IntegerTarget implements Target<Integer> {

    private final int source;

    @Override
    public String getUniqueId() {
        return hashCode() + "";
    }

    public Integer getSource() {
        return source;
    }
}
