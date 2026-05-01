package com.epam.gymcrm.service;

import com.epam.gymcrm.domain.Trainee;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.repository.TraineeRepository;
import com.epam.gymcrm.util.PasswordGenerator;
import com.epam.gymcrm.util.UsernameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TraineeServiceIntegrationTest {

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private UsernameGenerator usernameGenerator;

    @Autowired
    private PasswordGenerator passwordGenerator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public OAuth2AuthorizedClientRepository oauth2AuthorizedClientRepository() {
            return Mockito.mock(OAuth2AuthorizedClientRepository.class);
        }
    }

    @Test
    void getTrainee() {
        //given
        Trainee trainee = new Trainee();
        User user = new User();
        trainee.setUser(user);
        trainee.getUser().setFirstName("firstName");
        trainee.getUser().setLastName("lastName");
        trainee.getUser().setPassword(passwordEncoder.encode("password"));
        String username = usernameGenerator.generateUsername(trainee.getUser().getFirstName(), trainee.getUser().getLastName());
        trainee.getUser().setActive(true);
        trainee.getUser().setUsername(username);
        traineeRepository.save(trainee);

        //when
        var s = traineeRepository.getTraineeByUserUsername(username).orElse(null);

        //then
        then(s.getUser().getUsername()).isEqualTo(username);
        then(s.getUser().getUsername()).isNotNull();
    }
}
