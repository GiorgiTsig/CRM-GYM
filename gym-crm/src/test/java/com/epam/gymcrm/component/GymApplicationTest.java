package com.epam.gymcrm.component;

import com.epam.gymcrm.facade.TraineeFacade;
import com.epam.gymcrm.facade.TrainerFacade;
import com.epam.gymcrm.facade.TrainingFacade;
import com.epam.gymcrm.facade.TrainingTypesFacade;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("test")
@CucumberContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class GymApplicationTest {

    @MockitoBean
    private TraineeFacade traineeFacade;

    @MockitoBean
    private TrainerFacade trainerFacade;

    @MockitoBean
    private TrainingFacade trainingFacade;

    @MockitoBean
    private TrainingTypesFacade trainingTypesFacade;

}
