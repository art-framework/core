package io.artframework;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Accessors(fluent = true)
public final class TriggerExecution<TTrigger extends Trigger> implements Scoped, TargetCreator {

    public static <TTrigger extends Trigger> TriggerExecution<TTrigger> of(Scope scope, Class<TTrigger> triggerClass) {

        return new TriggerExecution<>(scope, triggerClass);
    }

    @Getter
    private final Scope scope;
    @Getter
    private final Class<TTrigger> triggerClass;
    private final List<Target<?>> targets = new ArrayList<>();
    @Getter
    private boolean executed = false;

    TriggerExecution(Scope scope, Class<TTrigger> triggerClass) {
        this.scope = scope;
        this.triggerClass = triggerClass;
    }

    /**
     * @return all targets of this trigger execution
     */
    public Target<?>[] targets() {

        return targets.toArray(new Target[0]);
    }

    /**
     * Adds the given target to the execution context of this trigger.
     * <p>All listeners of this trigger will be called with the given target
     * and can perform actions and requirement checks against it.
     * <p>Every target that is added to this trigger must have a matching
     * {@link Target} wrapper. Adding the target will fail silently if no
     * matching wrapper is found and a log message is printed in the console.
     *
     * @param target the target to add
     * @param <TTarget> the type of the target
     * @return this trigger execution context
     */
    public <TTarget> TriggerExecution<TTrigger> with(TTarget target) {

        target(target).ifPresent(targets::add);
        return this;
    }

    /**
     * Finalizes this trigger execution and calls all listeners subscribed to the trigger type.
     * <p>Every trigger execution can only be called once.
     *
     * @throws UnsupportedOperationException if the trigger execution was already called once
     */
    public void execute() {

        if (executed()) {
            throw new UnsupportedOperationException("This trigger execution was already called. You can only call a trigger once and need to create a new instance for every call.");
        }

        scope().configuration().trigger().execute(this);

        // fetch registered listeners (trigger context) from the provider
        // call them with all targets provided by this builder
        // the trigger context holds the configured trigger instance
        // and calls test for any matching target
        // the trigger will only call all targets if the provided test is successful

        // TODO: remove event api
        // TODO: add trigger class -> context listener mapping in provider
        // TODO: construct trigger instance with config mapping in parser like others
        // TODO: refactor trigger interface to match requirement
        // TODO: add new marker interface with shortcut to this builder
    }
}
