package io.artframework.impl.test;

import io.artframework.*;
import io.artframework.annotations.ART;
import io.artframework.integration.data.Player;
import lombok.NonNull;

@ART("test-action")
public class TestAction implements Action<Player> {
    @Override
    public Result execute(@NonNull Target<Player> target, @NonNull ExecutionContext<ActionContext<Player>> context) {
        return null;
    }
}
