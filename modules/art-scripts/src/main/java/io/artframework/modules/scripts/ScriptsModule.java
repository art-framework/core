package io.artframework.modules.scripts;

import io.artframework.ArtException;
import io.artframework.ParseException;
import io.artframework.Scope;
import io.artframework.annotations.*;
import io.artframework.modules.scripts.actions.ExecuteScriptAction;
import lombok.Getter;
import lombok.extern.java.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log(topic = "art-scripts")
@ArtModule("art-scripts")
public class ScriptsModule {

    @Getter
    final List<Script> loadedScripts = new ArrayList<>();

    @Config("config.yml")
    private ScriptModuleConfig config = new ScriptModuleConfig();

    @OnLoad
    public void onLoad(Scope scope) throws ArtException {

        try {
            File scriptsDir = new File(scope.settings().basePath(), "scripts");
            scriptsDir.mkdirs();

            loadedScripts.addAll(Files.walk(scriptsDir.toPath())
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .filter(file -> file.getName().endsWith(".yml") || file.getName().endsWith(".yaml"))
                    .map(file -> scope.configuration().configs()
                            .load(ScriptConfig.class, file)
                            .map(scriptConfig -> {
                                scriptConfig.setIdentifier(file.getName().toLowerCase().replace(".yml", "").replace(".yaml", ""));
                                return scriptConfig;
                            })
                    )
                    .flatMap(Optional::stream)
                    .map(scriptConfig -> {
                        try {
                            return new Script(scriptConfig, scope.load(scriptConfig.getArt()));
                        } catch (ParseException e) {
                            log.severe("failed to load script " + scriptConfig.getIdentifier() + ": " + e.getMessage());
                            return null;
                        }
                    }).filter(Objects::nonNull)
                    .collect(Collectors.toList()));

            log.info("loaded " + loadedScripts.size() + " art-scripts.");
        } catch (IOException e) {
            throw new ArtException(e);
        }
    }

    @OnEnable
    public void onEnable(Scope scope) {

        loadedScripts.forEach(Script::enable);

        scope.configuration().actions().add(() -> new ExecuteScriptAction(this));
    }

    @OnDisable
    public void onDisable(Scope scope) {

        loadedScripts.clear();
    }
}
