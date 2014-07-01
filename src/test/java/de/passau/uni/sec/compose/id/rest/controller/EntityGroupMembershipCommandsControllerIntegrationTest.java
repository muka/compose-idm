package de.passau.uni.sec.compose.id.rest.controller;

import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestDataFixture.authorizationHttpHeaderToken;
import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestDataFixture.tokenUnmodifiedHttpHeader;
import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestDataFixture.entityGroupMembershipCreateMessageJSON;
import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestEventFixtures.entityGroupMembershipResponseMessage;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import de.passau.uni.sec.compose.id.core.event.ApproveEntityGroupMembershipEvent;
import de.passau.uni.sec.compose.id.core.event.CreateEntityGroupMembershipEvent;
import de.passau.uni.sec.compose.id.core.event.DeleteEntityGroupMembershipEvent;
import de.passau.uni.sec.compose.id.core.service.ApplicationService;
import de.passau.uni.sec.compose.id.core.service.EntityGroupMembershipService;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;

@RunWith(PowerMockRunner.class)
@PrepareForTest(EntityGroupMembershipService.class)
@PowerMockIgnore(value = {"org.apache.log4j.*"})
public class EntityGroupMembershipCommandsControllerIntegrationTest {

    MockMvc mockMvc;

    @InjectMocks
    EntityGroupMembershipCommandsController entityGroupMembershipCommandsController;

    @Mock
    RestAuthentication authenticator;

    @Spy
    EntityGroupMembershipService membershipService = new EntityGroupMembershipService();

    @SuppressWarnings("unchecked")
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = standaloneSetup(entityGroupMembershipCommandsController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();

        when(
                authenticator.authenticatePrincipals(any(Logger.class),
                        any(Collection.class))).thenReturn(
                new LinkedList<IPrincipal>());
    }

    @Test
    public void createEntityHttpCreatedTest() throws Exception {
        PowerMockito
                .doReturn(entityGroupMembershipResponseMessage("testGroupId"))
                .when(membershipService)
                .createEntity(any(CreateEntityGroupMembershipEvent.class));

        this.mockMvc.perform(
                post("/idm/group_memberships/testType/testId/")
                        .headers(authorizationHttpHeaderToken())
                        .content(entityGroupMembershipCreateMessageJSON())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isCreated());
    }

    @Test
    public void createCreateEntityGroupMembershipRenderAsJsonTest()
            throws Exception {

        PowerMockito
                .doReturn(entityGroupMembershipResponseMessage("testGroupId"))
                .when(membershipService)
                .createEntity(any(CreateEntityGroupMembershipEvent.class));

        this.mockMvc.perform(
                post("/idm/group_memberships/testType/testId/")
                        .headers(authorizationHttpHeaderToken())
                        .content(entityGroupMembershipCreateMessageJSON())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                jsonPath("$.id").value("testGroupId"));
    }

    @Test
    public void createCreateEntityGroupMembershipIdmExceptionTest()
            throws Exception {

        PowerMockito
                .doThrow(
                        new IdManagementException(null, null, LoggerFactory
                                .getLogger(ApplicationService.class), null,
                                Level.ERROR, 500)).when(membershipService)
                .createEntity(any(CreateEntityGroupMembershipEvent.class));

        this.mockMvc.perform(
                post("/idm/group_memberships/testType/testId/")
                        .headers(authorizationHttpHeaderToken())
                        .content(entityGroupMembershipCreateMessageJSON())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().is5xxServerError());
    }

    @Test
    public void createEntityHttpisOk() throws Exception {

        PowerMockito
                .doReturn(entityGroupMembershipResponseMessage("testGroupId"))
                .when(membershipService)
                .updateEntity(any(ApproveEntityGroupMembershipEvent.class));

        this.mockMvc.perform(
                put("/idm/group_memberships/approve/testId/")
                        .headers(tokenUnmodifiedHttpHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isOk());
    }

    @Test
    public void deleteCreateEntityGroupMembershipHttpOkTest() throws Exception {

        PowerMockito.doNothing().when(membershipService)
                .deleteEntity(any(DeleteEntityGroupMembershipEvent.class));

        this.mockMvc.perform(
                delete("/idm/group_memberships/delete/testId")
                        .headers(tokenUnmodifiedHttpHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isOk());
    }

    @Test
    public void deleteCreateEntityGroupMembershipIdmExceptionTest()
            throws Exception {

        PowerMockito
                .doThrow(
                        new IdManagementException(null, null, LoggerFactory
                                .getLogger(ApplicationService.class), null,
                                Level.ERROR, 500)).when(membershipService)
                .deleteEntity(any(DeleteEntityGroupMembershipEvent.class));

        this.mockMvc.perform(
                delete("/idm/group_memberships/delete/testId")
                        .headers(tokenUnmodifiedHttpHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().is5xxServerError());
    }
}
