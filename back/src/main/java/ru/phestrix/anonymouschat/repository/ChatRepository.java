package ru.phestrix.anonymouschat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.phestrix.anonymouschat.model.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    Chat findChatByFirstUserNameAndSecondUserName(String firstUserName, String secondUserName);
}
