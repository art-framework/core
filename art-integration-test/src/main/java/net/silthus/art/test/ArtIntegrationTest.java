package net.silthus.art.test;

import net.silthus.art.ART;
import net.silthus.art.test.actions.TestAction;

public class ArtIntegrationTest {

    public static void main(String[] args) {

        new ArtIntegrationTest().enable();
    }

    public void enable() {

        ART.register()
                .actions()
                    .add(TestAction.class, () -> new TestAction(this))
                    .add("foo", context -> {})
                    .add("bar", String.class, context -> {})
                    .add(TestAction.class);
    }
}
