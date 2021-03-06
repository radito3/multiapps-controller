package org.cloudfoundry.multiapps.controller.web.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.cloudfoundry.multiapps.controller.core.util.ApplicationConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class FlowableConfigurationTest {

    private static final String APP_ID = "foo";
    private static final int APP_INSTANCE_INDEX = 1;
    private static final String RANDOM_ID = "bar";

    @Mock
    private ApplicationConfiguration applicationConfiguration;
    private final FlowableConfiguration flowableConfiguration = new FlowableConfiguration();

    @BeforeEach
    void initMocks() throws Exception {
        MockitoAnnotations.openMocks(this)
                          .close();
        flowableConfiguration.randomIdGenerator = () -> RANDOM_ID;
    }

    @ParameterizedTest
    @MethodSource
    void testJobExecutorId(String applicationId, Integer applicationInstanceIndex, String expectedJobExecutorId) {
        Mockito.when(applicationConfiguration.getApplicationInstanceIndex())
               .thenReturn(applicationInstanceIndex);
        Mockito.when(applicationConfiguration.getApplicationGuid())
               .thenReturn(applicationId);

        String jobExecutorId = flowableConfiguration.jobExecutorId(applicationConfiguration);

        assertEquals(expectedJobExecutorId, jobExecutorId);
    }

    static Stream<Arguments> testJobExecutorId() {
        return Stream.of(Arguments.of(null, null, RANDOM_ID), Arguments.of(APP_ID, null, RANDOM_ID),
                         Arguments.of(null, APP_INSTANCE_INDEX, RANDOM_ID), Arguments.of(APP_ID, APP_INSTANCE_INDEX, "ds-foo/1/bar"));
    }

}
