package net.silthus.art.builder;

import lombok.RequiredArgsConstructor;
import net.silthus.art.api.actions.Action;
import net.silthus.art.api.actions.ActionFactory;

public class ARTBuilder {

    public <TTarget> TargetBuilder<TTarget> target(Class<TTarget> targetClass) {
        return new TargetBuilder<>(targetClass);
    }

    public <TTarget, TConfig> ARTBuilder action(Class<TTarget> targetClass, Action<TTarget, TConfig> action) {
        new TargetBuilder<TTarget>(targetClass).action(action);
        return this;
    }

    @RequiredArgsConstructor
    public class TargetBuilder<TTarget> {

        private final Class<TTarget> targetClass;

        public <TConfig> ActionBuilder<TConfig> action(Action<TTarget, TConfig> action) {
            return new ActionBuilder<>(action);
        }

        @RequiredArgsConstructor
        public class ActionBuilder<TConfig> {
            private final ActionFactory<TTarget, TConfig> actionFactory;

            public ActionBuilder(Action<TTarget, TConfig> action) {
                this.actionFactory = new ActionFactory<>(targetClass, action);
            }

            public <TNextTarget> TargetBuilder<TNextTarget> target(Class<TNextTarget> targetClass) {
                return ARTBuilder.this.target(targetClass);
            }

            public <TNextConfig> ActionBuilder<TNextConfig> action(Action<TTarget, TNextConfig> action) {
                return TargetBuilder.this.action(action);
            }

            public ActionBuilder<TConfig> withName(String name) {
                actionFactory.setName(name);
                return this;
            }
        }
    }
}

