package ru.phestrix.anonymouschat.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.phestrix.anonymouschat.service.ChatService;

@RestController
@RequestMapping("api/chat")
public class ChatController {
    private final ChatService chatService;

    public ChatController(@Autowired ChatService chatService) {
        this.chatService = chatService;
    }

    @RequestMapping("/create")
    public ResponseEntity<String> createChat(@RequestParam String firstUserName, @RequestParam String secondUserName) {
        try {
            chatService.createChat(firstUserName, secondUserName);
            return ResponseEntity.ok("Chat created");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
