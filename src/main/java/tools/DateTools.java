package tools;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTools {

    public static String format(long time) {
        return (new SimpleDateFormat("mm:ss:SSS")).format(new Date(time));
    }

}
