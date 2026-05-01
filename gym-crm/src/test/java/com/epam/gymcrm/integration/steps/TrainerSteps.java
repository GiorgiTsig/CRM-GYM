package com.epam.gymcrm.integration.steps;

import com.epam.gymcrm.domain.Trainer;
import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.domain.User;
import com.epam.gymcrm.repository.TrainerRepository;
import com.epam.gymcrm.repository.TrainingTypeRepository;
import com.epam.gymcrm.util.PasswordGenerator;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TrainerSteps {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordGenerator passwordGenerator;

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private TestContext context;

    @Before
    public void seedTrainingTypes() {
        if (trainingTypeRepository.findTrainingTypeByTrainingTypeName("MMA") == null) {
            trainingTypeRepository.save(new TrainingType("MMA"));
        }
    }

    @Given("a trainer with username {string} exists")
    public void trainerExists(String username) {
        var trainerExists = trainerRepository.getTrainerByUserUsername(username).isPresent();

        if (trainerExists) {
            return;
        }

        User user = new User();
        Trainer trainer = new Trainer();
        user.setFirstName("John");
        user.setLastName("Trainer");
        String type = "MMA";

        var password = passwordGenerator.generatePassword();
        user.setPassword(passwordEncoder.encode(password));


        user.setUsername(username);
        user.setActive(true);

        trainer.setUser(user);
        var trainingType = trainingTypeRepository.findTrainingTypeByTrainingTypeName(type);
        trainer.setTrainingType(trainingType);
        user.setTrainer(trainer);

        trainerRepository.save(trainer);
        context.setUsername(username);
        context.setPassword(password);
    }
}
