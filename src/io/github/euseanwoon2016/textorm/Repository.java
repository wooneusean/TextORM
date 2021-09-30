package io.github.euseanwoon2016.textorm;

import java.lang.annotation.*;

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Repository {
    String repositoryName() default "";
}
