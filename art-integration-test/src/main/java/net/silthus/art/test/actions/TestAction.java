package net.silthus.art.test.actions;

import lombok.NonNull;
import net.silthus.art.Action;
import net.silthus.art.ActionContext;
import net.silthus.art.ExecutionContext;
import net.silthus.art.test.ArtIntegrationTest;

public class TestAction implements Action<String> {

    private final ArtIntegrationTest test;

    public TestAction(ArtIntegrationTest test) {
        this.test = test;
    }

    @Override
    public void execute(@NonNull ExecutionContext<String, ActionContext<String>> context) {

    }
}
