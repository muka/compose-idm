package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;

import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;

public class ListUsersInGroupEvent extends AbstractGetEvent implements Event
{

	public ListUsersInGroupEvent(Collection<IPrincipal> princi, String id) {
		super.principals = princi;
		super.id = id;
	}
	@Override
	public String getLoggingDetails() {
		return "Listing all users in group with id: "+super.id+" by principals: "+RestAuthentication.getBasicInfoPrincipals(super.principals);
	}

}
