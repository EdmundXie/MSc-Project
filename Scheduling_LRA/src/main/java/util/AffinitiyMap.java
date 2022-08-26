package util;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;

// 亲和度映射map
public class AffinitiyMap extends HashMap<String, Integer> {
    public AffinitiyMap fromStr(String affStr) {
        AffinitiyMap affinitiyMap = new AffinitiyMap();
        affStr = affStr.replace("[", "");
        affStr = affStr.replace("]", "");
        if (Strings.isNullOrEmpty(affStr)) {
            return affinitiyMap;
        }
        String[] splitStr = affStr.split(",");
        for (int i = 0; i <= splitStr.length - 2; i = i + 2) {
            String key = splitStr[i].replace("(", "").trim();
            int value = Integer.parseInt(splitStr[i + 1].replace(")", "").trim());
            affinitiyMap.put(key, value);
        }
        return affinitiyMap;
    }
}
