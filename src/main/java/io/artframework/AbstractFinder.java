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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;

public abstract class AbstractFinder extends AbstractScope implements Finder {

    protected AbstractFinder(Configuration configuration) {
        super(configuration);
    }

    protected abstract FinderResult<?> findAllIn(File... files);

    @Override
    public final FinderResult<?> findAllIn(Path path, Predicate<File> predicate) {
        if (Files.isRegularFile(path)) {
            return findAllIn(path.toFile());
        } else {
            try {
                return findAllIn(Files.walk(Paths.get(path.toUri()))
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .filter(predicate)
                        .toArray(File[]::new));
            } catch (IOException e) {
                e.printStackTrace();
                return FinderResult.empty();
            }
        }
    }
}
