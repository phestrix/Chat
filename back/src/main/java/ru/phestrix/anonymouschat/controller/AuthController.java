package ru.phestrix.anonymouschat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.phestrix.anonymouschat.model.LoginRequest;
import ru.phestrix.anonymouschat.model.RegisterRequest;
import ru.phestrix.anonymouschat.model.User;
import ru.phestrix.anonymouschat.repository.UserRepository;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepository;

    public AuthController(@Autowired  UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()) != null) {
            return ResponseEntity.badRequest().body("Пользователь уже существует");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setDateOfRegistration(LocalDate.now().toString());
        userRepository.save(user);

        return ResponseEntity.ok("Пользователь успешно зарегистрирован");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (user != null && user.getPassword().equals(request.getPassword())) {
            return ResponseEntity.ok("Успешный вход");
        }
        return ResponseEntity.badRequest().body("Неверные учетные данные");
    }
}
