package net.silthus.art.impl;

import lombok.NonNull;
import net.silthus.art.ArtContext;
import net.silthus.art.ArtObjectContext;
import net.silthus.art.Configuration;
import net.silthus.art.ExecutionContext;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Optional;
import java.util.Stack;

public class DefaultExecutionContext<TContext extends ArtObjectContext> extends AbstractScope implements ExecutionContext<TContext> {

    private final ArtContext root;
    private final Stack<ArtObjectContext> parents = new Stack<>();
    private TContext current;

    public DefaultExecutionContext(
            @NonNull Configuration configuration,
            @Nullable ArtContext root
    ) {
        super(configuration);
        this.root = root;
    }

    @Override
    public Optional<ArtContext> root() {
        return Optional.ofNullable(root);
    }

    @Override
    public Optional<ArtObjectContext> parent() {
        if (parents.isEmpty()) return Optional.empty();
        return Optional.of(parents.peek());
    }

    @Override
    public Iterator<ArtObjectContext> iterator() {
        return parents.iterator();
    }

    @Override
    public TContext current() {
        return current;
    }

    @Override
    public <TNextContext extends ArtObjectContext> ExecutionContext<TNextContext> next(@Nullable TNextContext nextContext) {
        this.current = nextContext
    }
}
