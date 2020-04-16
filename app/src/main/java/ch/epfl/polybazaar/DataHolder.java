package ch.epfl.polybazaar;

import java.util.ArrayList;

/**
 * Singleton class used to pass listing references between activities without risking an overflowing bundle
 */
public class DataHolder {
    private ArrayList<String> data = new ArrayList<>();
    public ArrayList<String> getData() {return data;}
    public void setData(ArrayList data) {this.data = data;}

    private DataHolder(){}
    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {return holder;}
}
