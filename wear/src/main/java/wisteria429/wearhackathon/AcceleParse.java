package wisteria429.wearhackathon;

import android.util.Log;

/**
 * Created by fuji on 2014/09/13.
 */
public class AcceleParse {
    private static final String TAG = "AccleParse";

    public final static int ACCELE_X = 0;
    public final static int ACCELE_Y = 1;
    public final static int ACCELE_Z = 2;

    public final static int TYPE_NONE  = 0;
    public final static int TYPE_UP    = 1;
    public final static int TYPE_DOWN  = 2;
    public final static int TYPE_RIGHT = 3;
    public final static int TYPE_LEFT  = 4;
    public final static int TYPE_FRONT = 5;
    public final static int TYPE_BACK  = 6;

    private final static int THRESHOLD = 15;
    private final static int THRESHOLD2 = 10; //値が低くなる方向に利用

    public static int getAcceleType(float[] accele) {
        int type = TYPE_NONE;

        if (accele[ACCELE_X] > THRESHOLD) {
            type = TYPE_LEFT;

        } else if (accele[ACCELE_X] < THRESHOLD2 * -1) {
            type = TYPE_RIGHT;
        } else if (accele[ACCELE_Y] > THRESHOLD) {
            type = TYPE_FRONT;
        } else if (accele[ACCELE_Y] < THRESHOLD * -1) {
            type = TYPE_BACK;

        } else if (accele[ACCELE_Z] > THRESHOLD) {
            type = TYPE_DOWN;

        } else if (accele[ACCELE_Z] < THRESHOLD2 * -1) {
            type = TYPE_UP;

        }
        return type;

    }
}
