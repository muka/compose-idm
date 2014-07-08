package de.passau.uni.sec.compose.id.rest.controller;

import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestDataFixture.authorizationHttpHeaderToken;
import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestEventFixtures.userResponseMessage;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.event.GetUserEvent;
import de.passau.uni.sec.compose.id.core.service.ApplicationService;
import de.passau.uni.sec.compose.id.core.service.UserService;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;

@RunWith(PowerMockRunner.class)
@PrepareForTest(UserService.class)
@PowerMockIgnore(value = {"org.apache.log4j.*"})
public class UserDetailsControllerIntegrationTest {

    MockMvc mockMvc;

    @InjectMocks
    UserDetailsController userDetailsController;

    @Mock
    RestAuthentication authenticator;

    @Spy
    UserService userService = new UserService();

    @SuppressWarnings("unchecked")
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = standaloneSetup(userDetailsController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();

        when(
                authenticator.authenticatePrincipals(any(Logger.class),
                        any(Collection.class))).thenReturn(
                new LinkedList<IPrincipal>());
    }

    @Test
    public void requestEntityHttpOkTest() throws Exception {
        PowerMockito.doReturn(userResponseMessage()).when(userService)
                .getEntity(any(GetUserEvent.class));

        this.mockMvc.perform(
                get("/idm/user/testId").headers(authorizationHttpHeaderToken())
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isOk());
    }

    @Test
    public void requestUserIdmExceptionTest() throws Exception {
        PowerMockito
                .doThrow(
                        new IdManagementException(null, null, LoggerFactory
                                .getLogger(ApplicationService.class), null,
                                Level.ERROR, 500)).when(userService)
                .getEntity(any(GetUserEvent.class));

        this.mockMvc.perform(
                get("/idm/user/testId").headers(authorizationHttpHeaderToken())
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().is5xxServerError());
    }
}
