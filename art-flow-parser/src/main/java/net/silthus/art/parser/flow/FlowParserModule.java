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

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import net.silthus.art.api.parser.ArtParser;
import net.silthus.art.parser.flow.parser.ActionParser;
import net.silthus.art.parser.flow.parser.ArtTypeParser;
import net.silthus.art.parser.flow.parser.RequirementParser;

public class FlowParserModule extends AbstractModule {

    @Override
    protected void configure() {

        var multibinder = Multibinder.newSetBinder(binder(), new TypeLiteral<ArtTypeParser<?, ?>>() {});
        multibinder.addBinding().to(ActionParser.class);
        multibinder.addBinding().to(RequirementParser.class);

        MapBinder<String, ArtParser> mapBinder = MapBinder.newMapBinder(binder(), String.class, ArtParser.class);
        mapBinder.addBinding("flow").to(FlowParser.class);
    }
}
