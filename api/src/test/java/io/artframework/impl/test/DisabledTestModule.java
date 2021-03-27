package io.artframework.impl.test;

import io.artframework.Module;
import io.artframework.annotations.ArtModule;

@ArtModule(value = "disabled-module", autoRegisterArt = false)
public class DisabledTestModule implements Module {
}
