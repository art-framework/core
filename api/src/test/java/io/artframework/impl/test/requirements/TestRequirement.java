package io.artframework.impl.test.requirements;

import io.artframework.*;
import io.artframework.annotations.ART;
import io.artframework.integration.data.Player;
import lombok.NonNull;

@ART("test-req")
public class TestRequirement implements Requirement<Player> {
    @Override
    public Result test(@NonNull Target<Player> target, @NonNull ExecutionContext<RequirementContext<Player>> context) {
        return null;
    }
}
