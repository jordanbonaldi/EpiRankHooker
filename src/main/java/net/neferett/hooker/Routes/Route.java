package net.neferett.hooker.Routes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Route {

    String name() default "";

    boolean activated() default true;
}
