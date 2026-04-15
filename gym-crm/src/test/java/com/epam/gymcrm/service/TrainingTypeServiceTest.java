package com.epam.gymcrm.service;

import com.epam.gymcrm.domain.TrainingType;
import com.epam.gymcrm.repository.TrainingTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingTypeService trainingTypeService;

    @Test
    void findAll_returnsAllTrainingTypes() {
        TrainingType mma = new TrainingType("MMA");
        TrainingType yoga = new TrainingType("Yoga");
        List<TrainingType> expected = List.of(mma, yoga);

        when(trainingTypeRepository.findAll()).thenReturn(expected);

        List<TrainingType> result = trainingTypeService.findAll();

        assertEquals(expected, result);
        verify(trainingTypeRepository).findAll();
    }
}

