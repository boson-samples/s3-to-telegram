package functions;

import java.util.Date;
import java.util.List;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.funqy.Funq;

public class Function {

    @ConfigProperty(name = "TELEGRAM_API_KEY")
    String telegramBotApiKey;

    @Funq
    public void function(byte[] data) {
        String input = new String(data);
        System.out.println("Received: " + input);

        TelegramBot bot = new TelegramBot(telegramBotApiKey);
        
        List<Update> updates = bot.execute(new GetUpdates()).updates();
        if (updates.size() > 0) {
            long chatId = updates.get(0).message().chat().id();
            String message = new Date() + ": New data in S3 Bucket: " + input;

            SendResponse response = bot.execute(new SendMessage(chatId, message));
            if (response.isOk()) {
                System.out.println("Data from S3 bucket has been consumed and sent to Telegram chat");
            } else {
                System.out.println("Sending data to Telegram chat failed, response: " + response);
            }
        } else {
            System.out.println("Telegram chat hasn't been initialized, to do so please send any message to the chat.");
        }
    }
}