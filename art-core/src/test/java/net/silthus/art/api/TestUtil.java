package net.silthus.art.api;

import net.silthus.art.api.actions.ActionContext;
import net.silthus.art.api.requirements.RequirementContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class TestUtil {

    public static ActionContext<?, ?> action() {
        ActionContext<?, ?> action = mock(ActionContext.class);
        when(action.isTargetType(any())).thenReturn(true);
        return action;
    }

    public static <TTarget> ActionContext<TTarget, ?> action(Class<TTarget> targetClass) {
        ActionContext<TTarget, ?> action = mock(ActionContext.class);
        when(action.isTargetType(any())).thenCallRealMethod();
        when(action.getTargetClass()).thenReturn(targetClass);
        return action;
    }

    public static RequirementContext<?, ?> requirement(boolean result) {
        RequirementContext requirement = mock(RequirementContext.class);
        when(requirement.isTargetType(any())).thenReturn(true);
        when(requirement.test(any(), any())).thenReturn(result);
        when(requirement.test(any())).thenReturn(result);
        return requirement;
    }

    public static <TTarget> RequirementContext<TTarget, ?> requirement(Class<TTarget> targetClass, boolean result) {
        RequirementContext requirement = mock(RequirementContext.class);
        when(requirement.getTargetClass()).thenReturn(targetClass);
        when(requirement.isTargetType(any())).thenCallRealMethod();
        when(requirement.test(any(), any())).thenReturn(result);
        when(requirement.test(any())).thenReturn(result);
        return requirement;
    }
}
