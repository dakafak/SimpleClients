package dev.fanger.simpleclients.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AllowCloudProcessing {

    /**
     * Needs to be set for cloud processing to determine whether or not it should wait for a result from
     * another server.
     *
     * @return
     */
    boolean requiresReturnData();

    /**
     * Server load limit to decide when to send the request to another server in the cloud cluster
     *
     * @return
     */
    int serverLoadLimit();

    /**
     * The number of threads to use for cloud task processors {@link dev.fanger.simpleclients.server.handlerthreads.datahelper.DataReceiveHelperServer}
     *
     * @return
     */
    int numberCloudTaskProcessingThreads();

    //TODO add an annotation for syncing data across cloud processing, maybe within the task reference or something

}
