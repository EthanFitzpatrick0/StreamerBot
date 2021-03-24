//Code slightly adapted from a stackoverflow answer by Carter Page- https://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values
package StreamerBot;
import java.util.Map.*;
import java.util.*;

public class MapUtil {
    public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue(Map<K, V> map) {
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Entry.comparingByValue(Comparator.reverseOrder()));

        LinkedHashMap<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}