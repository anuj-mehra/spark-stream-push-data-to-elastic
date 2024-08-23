package com.spike.sparkstreaming.yodlee;

import java.util.Stack;

public class ObjectsPoolImpl {

    private static final String _LOCKREAD = "lockRead";
    private static final String _LOCKWRITE = "lockWrite";

    private int poolSize; // pool size

    private int retryDuration = 1;

    private Stack<ObjectsPoolImpl> poolStack;

    public ObjectsPoolImpl(int poolSize){
        poolStack = new Stack<ObjectsPoolImpl>();

        // Populate this pool
        // for loop on 'poolSize'
    }

    // Step1 --> start the process
    public ObjectsPoolImpl getObjectFromPool(int maxWwaitDuration){ // 10
        ObjectsPoolImpl conn = null;
        synchronized (_LOCKREAD) {
            if(poolStack.size() > 0){
                conn = poolStack.pop();
            }else{
                // re-try mechanism for sometime --> maxWwaitDuration
                int startTime = 0;
                while(true){
                    //int currTime = machine time;
                    if(currTime - startTime == maxWwaitDuration){
                        break;
                    }
                }
            }
        }

        return conn;
    }

    // Step last --> end the process
    public void putObjectBackIntoPool(ObjectsPoolImpl returnBackObvject){
        synchronized (_LOCKWRITE) {
            poolStack.add(returnBackObvject);
        }
    }


}
