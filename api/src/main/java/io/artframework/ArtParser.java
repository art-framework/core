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

package io.artframework;

/**
 * The art parser parses the given input into a valid art context.
 * <p>You can create your own parser and register it with the art loader
 * so others can use it: {@link ArtLoader}
 * @param <TParser>
 * @param <TInput>
 */
public interface ArtParser<TParser extends Parser<TInput>, TInput> extends ArtLoader {

    ArtParser<TParser, TInput> add(TInput input);

    ArtContext parse();
}
