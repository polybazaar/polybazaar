package ch.epfl.polybazaar;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Singleton class used to pass listing references between activities without risking an overflowing bundle
 */
public class DataHolder {
    private ArrayList<String> data = new ArrayList<>();
    private Map<String, String> dataMap = new LinkedHashMap<>();
    public ArrayList<String> getData() {return data;}
    public Map<String, String> getDataMap() {return dataMap;}
    public void setData(ArrayList data) {this.data = data;}
    public void setDataMap(Map dataMap) {this.dataMap = dataMap;}

    private DataHolder(){}
    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {return holder;}
}
