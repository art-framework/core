package net.silthus.art;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public final class TestUtil {

    public static ActionContext<?, ?> action() {
        ActionContext<?, ?> action = mock(ActionContext.class);
        when(action.isTargetType(any())).thenReturn(true);
        return action;
    }

    public static <TTarget> ActionContext<TTarget, ?> action(Class<TTarget> targetClass) {
        ActionContext<TTarget, ?> action = mock(ActionContext.class);
        when(action.isTargetType(any(targetClass))).thenReturn(true);
        return action;
    }

    public static RequirementContext<?, ?> requirement(boolean result) {
        RequirementContext requirement = mock(RequirementContext.class);
        doReturn(true).when(requirement).isTargetType(any());
        doReturn(result).when(requirement).test(any());
        doReturn(result).when(requirement).test(any(), any());
        return requirement;
    }

    public static <TTarget> RequirementContext<TTarget, ?> requirement(Class<TTarget> targetClass, boolean result) {
        RequirementContext requirement = mock(RequirementContext.class);
        doReturn(true).when(requirement).isTargetType(any(targetClass));
        doReturn(result).when(requirement).test(any(targetClass));
        doReturn(result).when(requirement).test(any(targetClass), any());
        return requirement;
    }
}
