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

package net.silthus.art.parser.flow;

import com.google.common.base.Strings;
import lombok.Data;
import net.silthus.art.ArtParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public abstract class Parser<TOutput> {

    private final Pattern pattern;
    private Matcher matcher;
    private String input;

    public boolean accept(String line) {

        if (Strings.isNullOrEmpty(line)) return false;

        matcher = getPattern().matcher(line);
        input = line;
        return getMatcher().matches();
    }

    public abstract TOutput parse() throws ArtParseException;
}
