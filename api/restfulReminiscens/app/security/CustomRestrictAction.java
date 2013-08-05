package security;

import java.util.ArrayList;
import java.util.List;

import play.mvc.Http;
import play.mvc.Result;
import annotations.CustomRestrict;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.RestrictAction;
import enums.MyRoles;

public class CustomRestrictAction extends RestrictAction {
	@Override
	public Result call(Http.Context context) throws Throwable {

		final CustomRestrict outerConfig = (CustomRestrict) configuration;

		RestrictAction restrictAction = new RestrictAction(
				((CustomRestrict) configuration).config(), this.delegate) {

			public String[] getRoleNames() {
				List<String> roleNames = new ArrayList<String>();
				for (MyRoles role : outerConfig.value()) {
					roleNames.add(role.getName());
				}
				return roleNames.toArray(new String[roleNames.size()]);
			}

			public List<String[]> getRoleGroups() {
				List<String[]> roleGroups = new ArrayList<String[]>();
				for (MyRoles role: outerConfig.value()) {; 
				
					String [] arrayRoles = new String[1];
					arrayRoles[0] = role.toString();
					roleGroups.add(arrayRoles);
				}
				return roleGroups;
			}

		};
		return restrictAction.call(context);
	}

}