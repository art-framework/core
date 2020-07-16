package net.silthus.art;

public interface ActionProvider extends ArtProvider, ArtFactoryProvider<ActionFactory<?>> {

    ActionProvider add(ArtObjectInformation<Action<?>> actionInformation);

    ActionProvider add(Class<? extends Action<?>> actionClass);

    ActionProvider add(String identifier, GenericAction action);

    <TTarget> ActionProvider add(String identifier, Class<TTarget> targetClass, Action<TTarget> action);

    <TAction extends Action<TTarget>, TTarget> ActionProvider add(Class<TAction> actionClass, ArtObjectProvider<TAction> action);
}
