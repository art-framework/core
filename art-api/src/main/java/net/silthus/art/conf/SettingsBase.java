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

package net.silthus.art.conf;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * This base class is extended by all XJC-generated {@link Settings} classes
 * <p>
 * Using such a base class seems to be a lot simpler than depending on any one
 * of those many JAXB / XJC plugins. Besides, cloning objects through the
 * standard Java {@link Cloneable} mechanism is around factor 1000x faster than
 * using {@link Serializable}, and even 10000x faster than using
 * {@link javax.xml.bind.JAXB#marshal(Object, java.io.OutputStream)},
 * marshalling a JAXB object into a {@link ByteArrayOutputStream}.
 */
abstract class SettingsBase implements Serializable, Cloneable {

    /**
     * Generated UID
     */
    private static final long serialVersionUID = 9238589725583228L;

    @Override
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}