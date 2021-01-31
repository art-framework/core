package io.artframework.util;

import com.google.common.base.Strings;
import lombok.Value;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Takes the given input and checks it against the required value.
 * <p>The input can have any of the following comparators: >, <, >=, <=, =.
 * <p>Here is an example: you want to check the if the player level is above or equal to level 5.
 * <pre>{@code
 * ModifierMatcher matcher = new ModifierMatcher(">=5");
 * if (matcher.matches(6)) {
 *     ...
 * }
 * }</pre>
 */
@Value
public class ModifierMatcher {

    private static final Pattern MODIFIER_PATTERN = Pattern.compile("^(?<modifier>[><=]{1,2})?(?<amount>\\d+)$");

    String input;
    Matcher matcher;

    public ModifierMatcher(String input) {
        this.input = input;
        this.matcher = MODIFIER_PATTERN.matcher(input);
    }

    /**
     * @return true if the input matches the modifier pattern
     */
    public boolean matchesPattern() {

        return matcher.matches();
    }

    /**
     * Checks the given value against the required value of the provided input.
     * <p>False will always be returned if the pattern does not match.
     * Use the {@link #matchesPattern()} method to check that first.
     * <p>If the input pattern only provided a number greater equals to the given
     * input will be checked. {@code 5} becomes {@code >=5}.
     *
     * @param actual the actual value to compare against the input pattern
     * @return true if the provided value matches the required value pattern
     */
    public boolean matches(double actual) {

        if (!matchesPattern()) return false;

        try {
            String modifier = matcher.group("modifier");
            double requirement = Double.parseDouble(matcher.group("amount"));

            if (Strings.isNullOrEmpty(modifier)) {
                return actual >= requirement;
            } else if (modifier.equals("=") || modifier.equals("==")) {
                return actual == requirement;
            } else if (modifier.equals(">")) {
                return actual > requirement;
            } else if (modifier.equals("<")) {
                return actual < requirement;
            } else if (modifier.equals(">=") || modifier.equals("=>")) {
                return actual >= requirement;
            } else if (modifier.equals("<=") || modifier.equals("=<")) {
                return actual <= requirement;
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            e.printStackTrace();
        }

        return false;
    }
}
