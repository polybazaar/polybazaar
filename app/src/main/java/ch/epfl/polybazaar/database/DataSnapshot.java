package ch.epfl.polybazaar.database;

import java.util.HashMap;
import java.util.Map;

public class DataSnapshot {

    private boolean exists;
    private Object data;
    public DataSnapshot(boolean exists, Object data){
        this.exists = exists;
        this.data = data;
    }


    public boolean exists(){
        return exists;
    }
    public Object get(String field){
        return ((Map<String,Object>)(data)).get(field);
        //return data;
    }
    public Object data(){
        return data;
    }

}
