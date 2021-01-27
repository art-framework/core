package io.artframework.replacements;

import com.google.common.base.Strings;
import io.artframework.Context;
import io.artframework.Replacement;
import io.artframework.Variable;
import io.artframework.impl.ReplacementContext;
import org.apache.commons.lang3.ClassUtils;

import java.util.HashMap;
import java.util.Map;

public class VariableReplacement implements Replacement {

    @Override
    public String replace(String value, ReplacementContext context) {

        Map<String, Variable<?>> variableMap = context.executionContext().map(Context::variables).orElse(new HashMap<>());

        if (variableMap.isEmpty() || Strings.isNullOrEmpty(value)) return value;

        for (Map.Entry<String, Variable<?>> entry : variableMap.entrySet()) {
            if (String.class.isAssignableFrom(entry.getValue().type()) || ClassUtils.isPrimitiveOrWrapper(entry.getValue().type())) {
                value = value.replace("${" + entry.getKey() + "}", entry.getValue().value().toString());
            }
        }

        return value;
    }
}
