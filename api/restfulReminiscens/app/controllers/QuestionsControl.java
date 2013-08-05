package controllers;

import static play.libs.Json.toJson;

import java.util.List;

import delegates.QuestionDelegate;
import play.mvc.*;
import pojos.QuestionBean;

public class QuestionsControl extends Controller {

    public static Result getGeneralQuestionsForYear(Integer byear, Integer fyear) {
		QuestionDelegate.getInstance();
		List<QuestionBean> lp = QuestionDelegate.getQuestionsForLifeDecade(byear,fyear);
		return lp != null ? ok(toJson(lp)) : notFound();
	}

	public static Result getGeneralQuestionsForLifeChapter(String chapter) {
		QuestionDelegate.getInstance();
		List<QuestionBean> lp = QuestionDelegate.getQuestionsForLifeChapter(chapter);
		return lp != null ? ok(toJson(lp)) : notFound();
	}
}
