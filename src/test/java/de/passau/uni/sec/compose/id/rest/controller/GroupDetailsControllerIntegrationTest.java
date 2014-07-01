package de.passau.uni.sec.compose.id.rest.controller;

import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestDataFixture.authorizationHttpHeaderToken;
import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestEventFixtures.groupResponseMessage;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.After;
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
import de.passau.uni.sec.compose.id.core.event.GetGroupEvent;
import de.passau.uni.sec.compose.id.core.service.ApplicationService;
import de.passau.uni.sec.compose.id.core.service.GroupService;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GroupService.class)
@PowerMockIgnore(value = {"org.apache.log4j.*"})
public class GroupDetailsControllerIntegrationTest {

    MockMvc mockMvc;

    @InjectMocks
    GroupDetailsController groupDetailsController;

    @Mock
    RestAuthentication authenticator;

    @Spy
    GroupService groupService = new GroupService();

    @SuppressWarnings("unchecked")
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = standaloneSetup(groupDetailsController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();

        when(
                authenticator.authenticatePrincipals(any(Logger.class),
                        any(Collection.class))).thenReturn(
                new LinkedList<IPrincipal>());
    }
    
    @After
    public void tearDown(){
        groupService = null;
        mockMvc = null;
        System.out.println("tearDown");
    }

    @Test
    public void requestEntityHttpOkTest() throws Exception {
        PowerMockito.doReturn(groupResponseMessage("groupTestId"))
                .when(groupService).getEntity(any(GetGroupEvent.class));

        this.mockMvc.perform(
                get("/idm/group/groupTestId").headers(
                        authorizationHttpHeaderToken()).accept(
                        MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void requestGroupIdmExceptionTest() throws Exception {
        PowerMockito
                .doThrow(
                        new IdManagementException(null, null, LoggerFactory
                                .getLogger(ApplicationService.class), null,
                                Level.ERROR, 500)).when(groupService)
                .getEntity(any(GetGroupEvent.class));

        this.mockMvc.perform(
                get("/idm/group/groupTestId").headers(
                        authorizationHttpHeaderToken()).accept(
                        MediaType.APPLICATION_JSON)).andExpect(
                status().is5xxServerError());
    }
}
