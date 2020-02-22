package dev.fanger.simpleclients.annotations;

import dev.fanger.simpleclients.server.data.task.TaskExecutor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AdvancedTaskProperties {

    /**
     * The number of threads the task will use. The number of threads will correlate to
     * the number of {@link TaskExecutor} available
     *
     * @return
     */
    int numberThreads() default 1;

    /**
     * The max number of items that can be in the {@link TaskExecutor} queue at one time.
     * This is also used to determine when to send items to another server when using {@link #enableCloudProcessing()}
     *
     * @return
     */
    int queueCapacity() default 128;

    /**
     * Needs to be set to true for automated cloud processing. Using {@link dev.fanger.simpleclients.server.cloud.CloudManager}
     *
     * @return
     */
    boolean enableCloudProcessing() default false;

    /**
     * Needs to be set for cloud processing to determine whether or not it should wait for a result from
     * another server.
     *
     * @return
     */
    boolean requiresReturnData() default false;

}
