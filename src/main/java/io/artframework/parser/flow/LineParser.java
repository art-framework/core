/*
 * Copyright 2020 ART-Framework Contributors (https://github.com/Silthus/art-framework)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.artframework.parser.flow;

import com.google.common.base.Strings;
import io.artframework.ParseException;
import io.artframework.Configuration;
import io.artframework.Scope;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public abstract class LineParser<TResult> implements Scope {

    private final Configuration configuration;
    private final Pattern pattern;
    private Matcher matcher;
    private String input;

    @Override
    public @NonNull Configuration configuration() {
        return configuration;
    }

    public boolean accept(String line) {

        if (Strings.isNullOrEmpty(line)) return false;

        matcher = pattern.matcher(line);
        input = line;
        return matcher.matches();
    }

    public abstract TResult parse() throws ParseException;
}

