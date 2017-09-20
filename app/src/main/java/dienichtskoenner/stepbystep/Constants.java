package dienichtskoenner.stepbystep;

import dienichtskoenner.stepbystep.geofence.GeoFenceMainActivity;

/**
 * Created by c.lisik on 20.09.2017.
 */

public class Constants {

    public static class ConstantsFragmentBottom{

        public static final float ZOOM_LOCATION=16;

        public static final int REQUEST_LOCATION=1;
        public static final int TILT_LOCATION=0;
    }

    public static class ConstantsGeoFenceMainActivity{

        public static final String NOTIFICATION_MSG = "NOTIFICATION MSG";
        public static final String GEOFENCE_REQ_ID = "My Geofence";
        public static final String TAG = GeoFenceMainActivity.class.getSimpleName();
        public static final String KEY_GEOFENCE_LAT = "GEOFENCE LATITUDE";
        public static final String KEY_GEOFENCE_LON = "GEOFENCE LONGITUDE";

        public static final int REQ_PERMISSION = 999;
        public static final int UPDATE_INTERVAL =  1000;
        public static final int FASTEST_INTERVAL = 900;
        public static final int GEOFENCE_ZOOM = 14;
        public static final int GEOFENCE_REQ_CODE = 0;

        public static final long GEO_DURATION = 60 * 60 * 1000;

        public static final float GEOFENCE_RADIUS = 400.0f; // meters
    }


}
