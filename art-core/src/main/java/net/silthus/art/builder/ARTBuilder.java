package net.silthus.art.builder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.silthus.art.api.ARTFactory;
import net.silthus.art.api.actions.Action;
import net.silthus.art.api.actions.ActionFactory;
import net.silthus.art.builder.ARTBuilder.TargetBuilder.ActionBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ARTBuilder {

    private final Map<Class<?>, TargetBuilder<?>> builders = new HashMap<>();

    @SuppressWarnings("rawtypes")
    public List<ARTFactory> build() {
        return builders.values().stream()
                .flatMap(targetBuilder -> targetBuilder.actionFactories.stream())
                .map(actionFactory -> (ARTFactory) actionFactory)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public <TTarget> TargetBuilder<TTarget> target(Class<TTarget> targetClass) {

        if (!builders.containsKey(targetClass)) {
            builders.put(targetClass, new TargetBuilder<>(targetClass));
        }

        return (TargetBuilder<TTarget>) builders.get(targetClass);
    }

    public <TTarget, TConfig> TargetBuilder<TTarget>.ActionBuilder<TConfig> action(Class<TTarget> targetClass, Class<TConfig> configClass, Action<TTarget, TConfig> action) {
        return target(targetClass).action(configClass, action);
    }

    @RequiredArgsConstructor
    public class TargetBuilder<TTarget> {

        private final Class<TTarget> targetClass;
        private final List<ActionFactory<TTarget, ?>> actionFactories = new ArrayList<>();

        public <TConfig> ActionBuilder<TConfig> action(Class<TConfig> configClass, Action<TTarget, TConfig> action) {
            ActionBuilder<TConfig> actionBuilder = new ActionBuilder<>(configClass, action);
            actionFactories.add(actionBuilder.getActionFactory());
            return actionBuilder;
        }

        @RequiredArgsConstructor
        public class ActionBuilder<TConfig> {
            @Getter
            private final ActionFactory<TTarget, TConfig> actionFactory;

            public ActionBuilder(Class<TConfig> configClass, Action<TTarget, TConfig> action) {
                this.actionFactory = new ActionFactory<>(targetClass, configClass, action);
            }

            public <TNextTarget> TargetBuilder<TNextTarget> target(Class<TNextTarget> targetClass) {
                return ARTBuilder.this.target(targetClass);
            }

            public <TNextConfig> ActionBuilder<TNextConfig> action(Class<TNextConfig> configClass, Action<TTarget, TNextConfig> action) {
                return TargetBuilder.this.action(configClass, action);
            }

            public ActionBuilder<TConfig> withName(String name) {
                actionFactory.setName(name);
                return this;
            }
        }
    }
}

