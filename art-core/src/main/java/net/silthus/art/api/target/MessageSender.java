package net.silthus.art.api.target;

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
