package com.example.photocoder.config;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MyAmazingBot extends TelegramLongPollingBot {
	
	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage() && update.getMessage().hasText()) {
			SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
			message.setChatId(update.getMessage().getChatId().toString());
			message.setText(update.getMessage().getText());

			try {
				execute(message); // Call method to send the message
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}
		if (update.getMessage().hasPhoto()) {
			String fileId = update.getMessage().getPhoto().get(0).getFileId();
			try {
				downloadPhoto(getBotToken(), fetchPath(getBotToken(), fileId));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String fetchPath(String botToken, String fileId) {
		RestTemplate restTemplate = new RestTemplate();
		String fooResourceUrl
		  = "https://api.telegram.org/bot"+botToken+"/getfile?file_id="+fileId;
		ResponseEntity<String> response
		  = restTemplate.getForEntity(fooResourceUrl, String.class);
		JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
		JsonObject result = jsonObject.get("result").getAsJsonObject();
		return result.get("file_path").getAsString();		
	}

	@Override
	public String getBotUsername() {
		return "Hideandseek";
	}

	@Override
	public String getBotToken() {
		return "5844733041:AAHKyrdlHE__J2vp-KVPLyyFcQhOt4AFpYg";
	}
	
	
	public void downloadPhoto(String botToken,String fileId) throws IOException {
        // Replace BOT_TOKEN with your actual bot token
        // Construct the download URL
        String downloadUrl = "https://api.telegram.org/file/bot" + botToken + "/" + fileId;

        // Open a connection to the download URL
        URL url = new URL(downloadUrl);
        URLConnection connection = url.openConnection();
        connection.connect();

        // Get the input stream for the file
        InputStream inputStream = connection.getInputStream();

        // Create a file output stream to save the file
        FileOutputStream outputStream = new FileOutputStream(fileId);

        // Create a buffer to hold the data while it's being downloaded
        byte[] buffer = new byte[4096];

        // Download the file in 4KB chunks
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        // Close the streams
        inputStream.close();
        outputStream.close();
    }
	
	
}
