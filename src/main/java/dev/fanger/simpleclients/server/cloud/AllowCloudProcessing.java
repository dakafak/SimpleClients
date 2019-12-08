package dev.fanger.simpleclients.server.cloud;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AllowCloudProcessing {

    /**
     * Return type needs to be set for cloud processing to determine whether or not it should wait for a result from
     * another server. {@link ReturnType#GET} will wait for a result and {@link ReturnType#POST} will send data and
     * move on without waiting to retrieve anything back.
     *
     * @return
     */
    ReturnType returnType();

    //TODO add an annotation for syncing data across cloud processing, maybe within the task reference or something

}
