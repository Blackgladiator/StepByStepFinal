package dienichtskoenner.stepbystep;

import java.util.Calendar;

/**
 * Created by thomasslawik on 21.09.17.
 */

public abstract class UtilC {

    public static long getToday(){

        Calendar c = Calendar.getInstance();
        try {

            c.setTimeInMillis(System.currentTimeMillis());
            c.set(Calendar.HOUR_OF_DAY,0);
            c.set(Calendar.MINUTE,0);
            c.set(Calendar.SECOND,0);
            c.set(Calendar.MILLISECOND,0);

        }catch (NullPointerException e){

        }
        return c.getTimeInMillis();
    }


}
