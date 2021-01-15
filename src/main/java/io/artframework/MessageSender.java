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
 * Implement this on your {@link Target} to allow it to receive messages.
 */
public interface MessageSender {

    /**
     * Sends a message to the receiving {@link Target}.
     * Each line in the arrays will be a new line in the chat.
     *
     * @param message to send
     */
    void sendMessage(String... message);
}
