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

package io.artframework.integration.data;

import io.artframework.MessageSender;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Player extends Entity implements MessageSender {

    private int health = 100;
    @Singular
    private List<Consumer<String[]>> messageConsumer = new ArrayList<>();

    public Player(String name) {
        super(name);
    }

    public Player() {
        super(RandomStringUtils.randomAlphanumeric(10));
    }

    public void sendMessage(String... messages) {
        getMessageConsumer().forEach(stringConsumer -> stringConsumer.accept(messages));
    }
}
