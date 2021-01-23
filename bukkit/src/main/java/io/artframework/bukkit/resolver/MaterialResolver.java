package io.artframework.bukkit.resolver;

import io.artframework.Resolver;
import io.artframework.ResolverContext;
import io.artframework.annotations.ConfigOption;
import org.bukkit.Material;

public class MaterialResolver implements Resolver<Material> {

    @ConfigOption(required = true)
    private String name;

    @Override
    public Material resolve(ResolverContext context) {
        return Material.matchMaterial(name);
    }
}
