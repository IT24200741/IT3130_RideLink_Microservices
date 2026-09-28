package com.ridelink.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridelink.account.model.User;
import com.ridelink.account.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration"
})
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected UserRepository userRepository;

    protected final Map<String, User> userDatabase = new ConcurrentHashMap<>();
    protected final AtomicLong idGenerator = new AtomicLong(1);

    @BeforeEach
    protected void setupDatabaseMock() {
        userDatabase.clear();
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            if (user.getId() == null) {
                user.setId(String.valueOf(idGenerator.getAndIncrement()));
            }
            userDatabase.put(user.getId(), user);
            return user;
        });
        when(userRepository.findById(anyString())).thenAnswer(invocation -> {
            String id = invocation.getArgument(0);
            return Optional.ofNullable(userDatabase.get(id));
        });
        when(userRepository.findByEmail(anyString())).thenAnswer(invocation -> {
            String email = invocation.getArgument(0);
            return userDatabase.values().stream().filter(u -> email.equalsIgnoreCase(u.getEmail())).findFirst();
        });
        when(userRepository.existsByEmail(anyString())).thenAnswer(invocation -> {
            String email = invocation.getArgument(0);
            return userDatabase.values().stream().anyMatch(u -> email.equalsIgnoreCase(u.getEmail()));
        });
        when(userRepository.findAll()).thenAnswer(invocation -> new ArrayList<>(userDatabase.values()));
        doAnswer(invocation -> {
            userDatabase.clear();
            return null;
        }).when(userRepository).deleteAll();
    }
}
