package net.silthus.art;

public interface RequirementProvider extends ArtProvider {

    RequirementProvider add(ArtObjectProvider<Requirement<?>> requirementInformation);

    RequirementProvider add(Class<? extends Requirement<?>> requirementClass);

    RequirementProvider add(String identifier, GenericRequirement requirement);

    <TTarget> RequirementProvider add(String identifier, Class<TTarget> targetClass, Requirement<TTarget> requirement);

    <TRequirement extends Requirement<?>> RequirementProvider add(Class<? extends TRequirement> requirementClass, ArtObjectProvider<TRequirement> requirement);
}
