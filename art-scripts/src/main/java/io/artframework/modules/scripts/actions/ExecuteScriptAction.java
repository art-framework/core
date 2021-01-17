package io.artframework.modules.scripts.actions;

import io.artframework.*;
import io.artframework.annotations.ART;
import io.artframework.modules.scripts.ScriptsModule;
import lombok.NonNull;

@ART(
        value = "art-scripts:execute",
        alias = {"script", "exec", "execute"},
        description = "Executes the given art-script loaded from the scripts directory.",
        autoRegister = false
)
public class ExecuteScriptAction implements GenericAction {

    private final ScriptsModule scriptsModule;

    public ExecuteScriptAction(ScriptsModule scriptsModule) {
        this.scriptsModule = scriptsModule;
    }

    @Override
    public Result execute(@NonNull Target<Object> target, @NonNull ExecutionContext<ActionContext<Object>> context) {

        return null;
    }
}
