/*
 * Copyright 2020 ART-Framework Contributors (https://github.com/Silthus/art-framework)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.silthus.art;

import net.silthus.art.api.actions.Action;
import net.silthus.art.api.actions.ActionConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

@DisplayName("ActionContext")
public class ActionContextTest {

    private Action<String, String> action;
    private ActionContext<String, String> actionContext;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void beforeEach() {
        action = (Action<String, String>) mock(Action.class);
        this.actionContext = new ActionContext<>(String.class, action, new ActionConfig<>());
    }

    @Nested
    @DisplayName("execute(TTarget)")
    public class Execute {

        @Test
        @DisplayName("should call Action#execute(TTarget, TConfig)")
        public void shouldCallActionExecute() {

            assertThatCode(() -> actionContext.execute("foobar"))
                    .doesNotThrowAnyException();

            verify(action, times(1)).execute("foobar", actionContext);
        }
    }

}