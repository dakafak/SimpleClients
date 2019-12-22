package dev.fanger.simpleclients.server.data.task;

import dev.fanger.simpleclients.connection.Connection;
import dev.fanger.simpleclients.logging.Level;
import dev.fanger.simpleclients.logging.Logger;
import dev.fanger.simpleclients.server.cloud.CloudManager;
import dev.fanger.simpleclients.server.cloud.TaskLoad;
import dev.fanger.simpleclients.server.data.payload.Payload;
import dev.fanger.simpleclients.server.data.payload.ServerLoadUpdateRequest;
import dev.fanger.simpleclients.server.data.payload.ServerLoadUpdateResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ServerLoadTaskRequestUpdate extends Task {

    private CloudManager cloudManager;

    public ServerLoadTaskRequestUpdate(CloudManager cloudManager) {
        this.cloudManager = cloudManager;
    }

    @Override
    public void executeTask(Connection connection, Payload payload) {
        if(payload.getData() instanceof ServerLoadUpdateRequest) {
            ServerLoadUpdateRequest serverLoadUpdateRequest = (ServerLoadUpdateRequest) payload.getData();
            int returnLoad = cloudManager.getTaskLoadManager().getCurrentServerLoad(serverLoadUpdateRequest.getTaskClass());

            ServerLoadUpdateResponse serverLoadUpdateResponse = new ServerLoadUpdateResponse(
                    returnLoad,
                    serverLoadUpdateRequest.getTaskClass(),
                    serverLoadUpdateRequest.getServerConnectionInfo());

//            Payload returnPayload = new Payload(serverLoadUpdateResponse, "/system/server/load/update");
            Payload returnPayload = new Payload(serverLoadUpdateResponse, null);
            connection.sendData(returnPayload);
        } else {
            Logger.log(Level.ERROR, "Wrong payload data type for server load request");
        }
//        ArrayList<ClassTaskLoadPair> allTaskLoadPairs = getListOfTaskLoadPairs(cloudManager.getTaskLoadManager().getCurrentTaskLoads());
//        if(payload != null && payload.getData() instanceof Class) {
//            Class taskClassFromPayload = (Class) payload.getData();
//            Payload returnPayload = new Payload(cloudManager.getTaskLoadManager().getCurrentServerLoad(taskClassFromPayload), null);
//            connection.sendData(returnPayload);
//        } else {
//            System.out.println("Server load payload request was either null or wrong type");
//        }
        //TODO the problem seems to be a result of sending a concurrenthashmap or arraylist and for some reason getting
        //  the error java.io.ObjectStreamClass cannot be cast to dev.fanger.simpleclients.server.data.payload.Payload
        //  maybe this will work when relaunching intellij later
    }

//    private ArrayList<ClassTaskLoadPair> getListOfTaskLoadPairs(ConcurrentHashMap<Class, TaskLoad> taskClassTaskLoadMap) {
//        ArrayList<ClassTaskLoadPair> taskLoadPairs = new ArrayList<>();
//
//        for(Class taskClass : taskClassTaskLoadMap.keySet()) {
//            taskLoadPairs.add(new ClassTaskLoadPair(taskClass, taskClassTaskLoadMap.get(taskClass)));
//        }
//
//        return taskLoadPairs;
//    }

//    public class ClassTaskLoadPair implements Serializable {
//
//        private Class taskClass;
//        private TaskLoad taskLoad;
//
//        public ClassTaskLoadPair(Class taskClass, TaskLoad taskLoad) {
//            this.taskClass = taskClass;
//            this.taskLoad = taskLoad;
//        }
//
//        public Class getTaskClass() {
//            return taskClass;
//        }
//
//        public void setTaskClass(Class taskClass) {
//            this.taskClass = taskClass;
//        }
//
//        public TaskLoad getTaskLoad() {
//            return taskLoad;
//        }
//
//        public void setTaskLoad(TaskLoad taskLoad) {
//            this.taskLoad = taskLoad;
//        }
//
//    }

}
