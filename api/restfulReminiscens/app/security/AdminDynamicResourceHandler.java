package security;

import models.User;
import play.mvc.Http;
import pojos.LifeStoryBean;
import be.objectify.deadbolt.java.AbstractDynamicResourceHandler;
import be.objectify.deadbolt.java.DeadboltHandler;
import delegates.LifeStoryDelegate;

public class AdminDynamicResourceHandler extends
		AbstractDynamicResourceHandler {

//	@Override
//	public boolean isAllowed(String name, String meta,
//			DeadboltHandler deadboltHandler, Http.Context context) {
//		boolean result = false;
//		Long idPerson = new Long(0);
//		if (SecurityModelConstants.ID_FROM_PERSON.equals(meta)) {
//			idPerson = MyDynamicResourceHandler.getIdFromPath(context.request().path(), SecurityModelConstants.ID_FROM_PERSON);
//		} else if (SecurityModelConstants.ID_FROM_STORY.equals(meta)) {
//			Long idStory = MyDynamicResourceHandler.getIdFromPath(context.request().path(), SecurityModelConstants.ID_FROM_STORY);
//			LifeStoryBean bean = LifeStoryDelegate.getInstance().getLifeStory(idStory);
//		//	idPerson = bean.getParticipationList().i;
//		}
//		result = isAClubOwner(idPerson, deadboltHandler, context);
//		return result;
//
//	}
//
//	public boolean isAClubOwner(Long idClub, DeadboltHandler deadboltHandler,
//			Http.Context context) {
//		models.User user = (User) deadboltHandler.getSubject(context);
//		if (user == null) {
//			return false;
//		}
//		ClubBean bean = ClubDelegate.getInstance().getClub(idClub);
//		if (bean == null) {
//			return false;
//		}
//
//		return MyDynamicResourceHandler.isUserInTheList(bean.getOwners(),
//				user.getId());
//	}

}
