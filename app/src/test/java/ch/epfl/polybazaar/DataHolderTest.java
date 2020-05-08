package ch.epfl.polybazaar;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class DataHolderTest {

    @Test
    public void DataHolderArrayTest() {
        ArrayList<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        DataHolder.getInstance().setData(list);
        ArrayList<String> savedList = DataHolder.getInstance().getData();
        assertEquals(list, savedList);
    }

    @Test
    public void DataHolderMapTest() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("1", "item1");
        map.put("2", "item2");
        map.put("3", "item3");
        DataHolder.getInstance().setDataMap(map);
        Map<String, String> savedMap = DataHolder.getInstance().getDataMap();
        assertEquals(map, savedMap);
    }


}
