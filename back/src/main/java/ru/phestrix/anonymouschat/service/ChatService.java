package ru.phestrix.anonymouschat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.phestrix.anonymouschat.model.Chat;
import ru.phestrix.anonymouschat.model.User;
import ru.phestrix.anonymouschat.repository.ChatRepository;
import ru.phestrix.anonymouschat.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public ChatService(@Autowired ChatRepository chatRepository, @Autowired UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    public void createChat(String username1, String username2) {
        User user1 = userRepository.findByUsername(username1);
        User user2 = userRepository.findByUsername(username2);

        if (user1 == null || user2 == null) {
            throw new RuntimeException("Один из пользователей не найден");
        }

        String encryptionKey = generateEncryptionKey(user1, user2);

        Chat chat = new Chat();
        chat.setFirstUserName(username1);
        chat.setSecondUserName(username2);
        chat.setEncryptionKey(encryptionKey);
        chatRepository.save(chat);

    }

    private String generateEncryptionKey(User user1, User user2) {
        try {
            String combinedData = user1.getUsername() + user1.getDateOfRegistration()
                    + user2.getUsername() + user2.getDateOfRegistration();

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(combinedData.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Ошибка генерации ключа шифрования", e);
        }
    }
}