package com.bartbruneel.authentication;

import com.bartbruneel.entities.UserEntity;
import com.bartbruneel.repositories.UserRepository;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;

@Singleton
public class TestDataProvider {

    private final UserRepository userRepository;

    public TestDataProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @EventListener
    public void init(StartupEvent event) {
        final String email = "bart@bart.com";
        final String emailMyUser = "my-user@bart.com";
        if(userRepository.findByEmail(email).isEmpty()) {
            saveUser(email);
        }
        if(userRepository.findByEmail(emailMyUser).isEmpty()) {
            saveUser(emailMyUser);
        }
    }

    private void saveUser(String email) {
        final UserEntity entity = new UserEntity();
        entity.setEmail(email);
        entity.setPassword("secret");
        userRepository.save(entity);
    }
}
