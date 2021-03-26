package io.artframework.integration;

import io.artframework.ART;
import io.artframework.BootstrapScope;
import io.artframework.Module;
import io.artframework.Scope;
import io.artframework.annotations.ArtModule;

@ArtModule("external-test")
public class ExternalTestModule implements Module {

    public ExternalTestModule() {

        ART.register(this);
    }

    @Override
    public void onBootstrap(BootstrapScope scope) throws Exception {

    }

    @Override
    public void onLoad(Scope scope) throws Exception {

    }

    @Override
    public void onReload(Scope scope) throws Exception {

    }

    @Override
    public void onEnable(Scope scope) throws Exception {

    }

    @Override
    public void onDisable(Scope scope) throws Exception {

    }
}
