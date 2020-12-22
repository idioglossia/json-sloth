package lab.idioglossia.jsonsloth;

import lab.idioglossia.sloth.collection.Collection;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsonSlothEntity {
    String collectionName();
    Collection.Type type() default Collection.Type.LIST;
}
