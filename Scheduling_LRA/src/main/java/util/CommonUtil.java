package util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CommonUtil {
    public static <T> List<T> partSort(List<T> list, int fromIndex, Comparator<? super T> comparator) {
        T[] a = (T[]) list.toArray(new Object[0]);
        Arrays.sort(a, fromIndex, list.size() - 1, comparator);
        return Arrays.stream(a).collect(Collectors.toList());
    }

}
