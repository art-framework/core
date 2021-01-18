package io.artframework.parser;

import com.google.common.base.Strings;
import io.artframework.ParseException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Iterator;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Use the line parser as a super class for any parser class that processes a single line.
 * <p>A new line parser instance should be created for each iterator and use the same
 * iterator reference. The line parser itself will not use the iterator to move the match forward.
 * It is just used for downstream parsers that want to do forward parsing.
 * <p>{@link #accept(String)} should be called with the {@link Iterator#next()} method to avoid
 * a desynchronization of the iterator.
 * <p>Every line that is passed to the {@code accept(String)} method is pushed into the {@link #inputs()} stack
 * and provided a {@link #matcher()} that is also pushed onto the {@link #matchers()} stack. The matchers and inputs
 * stacks are always in sync containing the same line.
 * <p>The stacks will not update if the provided input string is null or empty.
 * <p>See the {@link io.artframework.parser.flow.FlowLineParser} for an example implementation.
 * <br><br><h3>Implementation Notice</h3>
 * Make sure to use the {@link #next()} method to get the next line in the iterator
 * when doing forward lookups and multiline parsing.
 *
 * @param <TResult> the result type of the parser
 */
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class LineParser<TResult> implements Iterator<String> {

    private final Iterator<String> iterator;
    private final Pattern pattern;
    private final Stack<Matcher> matchers = new Stack<>();
    private final Stack<String> inputs = new Stack<>();

    @Override
    public boolean hasNext() {

        return iterator.hasNext();
    }

    @Override
    public String next() {

        return iterator.next();
    }

    /**
     * Takes the given line and matches it against the given pattern.
     * <p>The line and the corresponding matcher will be pushed onto the stack.
     * The last line and its matcher is always avaiable with the {@link #matcher()} and {@link #input()} method.
     *
     * @param line the line that should be parsed
     * @return true if the line matches the pattern of this line parser
     */
    public final boolean accept(String line) {

        if (Strings.isNullOrEmpty(line)) return false;

        this.matchers.push(pattern.matcher(line));
        this.inputs.push(line);

        return matchers().peek().matches();
    }

    /**
     * Peeks the matcher stack returning the current matcher.
     *
     * @return the current matcher on top of the stack
     * @throws java.util.EmptyStackException if the stack is empty
     */
    public final Matcher matcher() {

        return matchers.peek();
    }

    /**
     * Peeks the stack returning the current input line.
     *
     * @return the current input on top of the stack
     * @throws java.util.EmptyStackException if the stack is empty
     */
    public final String input() {

        return inputs.peek();
    }

    /**
     * Parses the current input into the given result.
     * <p>{@link #accept(String)}</p> must be called before calling parse.
     * <p>The implementing parser may move the iterator forward if doing a
     * forward lookup or parsing multiple lines.
     *
     * @return the parsed result
     * @throws ParseException if an error occured when parsing the current input
     */
    public abstract TResult parse() throws ParseException;
}
