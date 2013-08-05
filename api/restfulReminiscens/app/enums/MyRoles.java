package enums;

import be.objectify.deadbolt.core.models.Role;

public enum MyRoles implements Role {
	MEMBER, ADMIN, SYSTEM, CONTEXTCURATORS, CURATOR, TEST;
	
	@Override
	public String getName() {
		 return name();
	}
}
