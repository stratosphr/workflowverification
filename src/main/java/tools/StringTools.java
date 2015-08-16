package tools;

import exceptions.JoinOnNullException;

import java.util.ArrayList;
import java.util.Arrays;

public class StringTools {

    public static String join(ArrayList<?> objects, String separator) {
        if (objects != null) {
            if (objects.isEmpty()) {
                return "";
            } else {
                String str = "";
                for (int i = 0; i < objects.size() - 1; i++) {
                    str += objects.get(i) + separator;
                }
                str += objects.get(objects.size() - 1);
                return str;
            }
        } else {
            throw new JoinOnNullException();
        }
    }

    public static String join(Object[] objects, String separator) {
        return StringTools.join(new ArrayList<>(Arrays.asList(objects)), separator);
    }

    public static String separator(int size) {
        String str = "";
        for (int i = 0; i < size; i++) {
            str += "-";
        }
        str += "\n";
        return str;
    }
}
