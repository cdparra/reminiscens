package annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import be.objectify.deadbolt.java.actions.Restrict;
import security.CustomRestrictAction;

import enums.MyRoles;

import play.mvc.With;

@With(CustomRestrictAction.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
@Inherited
public @interface CustomRestrict {
    MyRoles[] value();

    Restrict config();
}


