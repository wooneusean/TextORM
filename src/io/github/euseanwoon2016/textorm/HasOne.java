package io.github.euseanwoon2016.textorm;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HasOne {
    String foreignKey();
}
