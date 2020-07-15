package net.silthus.art;

public interface ActionProvider extends ArtProvider, ArtFactoryProvider<ActionFactory<?>> {

    ActionProvider add(Class<? extends Action<?>> actionClass);

    ActionProvider add(String identifier, GenericAction action);

    <TTarget> ActionProvider add(String identifier, Class<TTarget> targetClass, Action<TTarget> action);

    <TAction extends Action<?>> ActionProvider add(Class<? extends TAction> actionClass, ArtObjectProvider<TAction> action);
}
