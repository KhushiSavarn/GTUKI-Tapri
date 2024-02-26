package com.ambaitsystem.tapri.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import com.ambaitsystem.tapri.model.User;

/**
 * Created by Lincoln on 07/01/16.
 */
public class MyPreferenceManager {

    private String TAG = MyPreferenceManager.class.getSimpleName();
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "engggossip";
    private static final String KEY_VERSION_CODE = "vcode";
    private static final String KEY_ISADMIN = "isadmin";


    // All Shared Preferences Keys
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_COLLEGE = "user_college";
    private static final String KEY_USER_DEPARTMENT = "user_department";
    private static final String KEY_USER_ADMISSION = "user_admission";
    private static final String KEY_USER_EMAIL = "user_email";

    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_TOOLTIP = "tooltip";
    private static final String KEY_UNREAD_COUNT = "unreadcount";
    private static final String KEY_LAST_MESSAGE = "lastmessage";
    private static final String KEY_BASE_URL = "baseurl";
    private static final String KEY_CELLNUMBER = "cellnumber";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_GIFT = "gift_claimed";

    // Constructor
    public MyPreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void storeUser(User user) {
        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());

        editor.putString(KEY_USER_COLLEGE, user.getcollegename());
        editor.putString(KEY_USER_DEPARTMENT, user.getdepartment_index());
        editor.putString(KEY_USER_ADMISSION, user.getadmission_index());
        editor.commit();

        Log.e(TAG, "User is stored in shared preferences. " + user.getName() + ", " + user.getEmail());
    }

    public User getUser() {
        if (pref.getString(KEY_USER_ID, null) != null)
        {
            //public User(String id, String name, String email,String collegename,String department_index,String admission_index) {

            String id, name, email,collegename,admission_index,department_index;
            id = pref.getString(KEY_USER_ID, null);
            name = pref.getString(KEY_USER_NAME, null);
            email = pref.getString(KEY_USER_EMAIL, null);
            collegename = pref.getString(KEY_USER_COLLEGE, null);
            department_index = pref.getString(KEY_USER_DEPARTMENT, null);
            admission_index = pref.getString(KEY_USER_ADMISSION, null);
            User user = new User(id, name, email,collegename,department_index,admission_index);
            return user;
        }
        return null;
    }

    /*sTORE ENDPOINT:BASEURL*/

    public void storebaseurl(String BASEURL) {
        editor.putString(KEY_BASE_URL, BASEURL);
        editor.commit();

    }

    public String getBASEURL() {
        if (pref.getString(KEY_BASE_URL, null) != null) {
            String baseurl;
            baseurl = pref.getString(KEY_BASE_URL, null);
            return baseurl;
        }
        return null;
    }

    /*enD */

    /*GET SET For LAST MESSAGE*/
    public void store_last_message(String  Message,String roomid)
    {
        Log.v("KEY_roomid[s]", "#" + KEY_LAST_MESSAGE + "_" + roomid);
                editor.putString(KEY_LAST_MESSAGE + "_" + roomid, Message);
        editor.commit();
    }

    public String get_last_message(String roomid)
    {
        if (pref.getString(KEY_LAST_MESSAGE + "_" + roomid, null) != null)
        {
            String Message= pref.getString(KEY_LAST_MESSAGE + "_" + roomid, null);
            Log.v("KEY_roomid[g]", "#" + KEY_LAST_MESSAGE + "_" + roomid + " : " +Message);
            return Message;
        }
        return null;
    }
    /*END OF LAST MESSAGE*/

    /*GET SET For CELL NUMBER*/
    public void store_cellnumber(String cellnumber)
    {
        editor.putString(KEY_CELLNUMBER , cellnumber);
        editor.commit();
    }

    public String get_cellnumber()
    {
        if (pref.getString(KEY_CELLNUMBER, null) != null)
        {
            String cellnumber= pref.getString(KEY_CELLNUMBER, null);
            return cellnumber;
        }
        return null;
    }

    /*GET SET For address*/
    public void store_address(String address)
    {
        editor.putString(KEY_ADDRESS , address);
        editor.commit();
    }

    public String get_address()
    {
        if (pref.getString(KEY_ADDRESS, null) != null)
        {
            String address= pref.getString(KEY_ADDRESS, null);
            return address;
        }
        return null;
    }

    /*GET SET For CELL NUMBER*/
    public void store_flag_gift_claimed(Boolean flag)
    {
        editor.putBoolean(KEY_GIFT , flag);
        editor.commit();
    }

    public Boolean get_flag_gift_claimed()
    {
        if (pref.getBoolean(KEY_GIFT, false) != false)
        {
            Boolean flag = pref.getBoolean(KEY_GIFT, false);
            return flag;
        }
        return false;
    }
    /*END OF CELL NUMBER*/

    /*
        <Count Operations>
     */

    public void reset_unread_count(String roomid) {
        editor.putInt(KEY_UNREAD_COUNT + "_" + roomid, 0);
        editor.commit();
    }


    public int Increament_unread_count(String roomid)
    {
            int count;
            count = pref.getInt(KEY_UNREAD_COUNT + "_" + roomid, 0);
            count = count +1;
            store_unread_count(count,roomid);

            return count;

    }

    public void store_unread_count(int count,String roomid) {
        editor.putInt(KEY_UNREAD_COUNT + "_" + roomid, count);
        editor.commit();
    }

    public int get_unread_count(String roomid)
    {
        if (pref.getInt(KEY_UNREAD_COUNT + "_" + roomid, 0) != 0) {
            int count;
            count = pref.getInt(KEY_UNREAD_COUNT + "_" + roomid, 0);
            return count;
        }
        return 0;
    }

    /*End of Code Count Block*/

    public void StoreToolTipFlag(String tooltipflag)
    {
       editor.putString(KEY_TOOLTIP, tooltipflag);
        editor.commit();
    }
    public String GetToolTipFlag()
    {
        if (pref.getString(KEY_TOOLTIP, null) != null)
        {
               String ToolTip = pref.getString(KEY_TOOLTIP,null);
            return ToolTip;
        }
        return  null;

    }
    public void addNotification(String notification) {

        // get old notifications
        String oldNotifications = getNotifications();

        if (oldNotifications != null) {
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }

        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    public String getNotifications() {
        return pref.getString(KEY_NOTIFICATIONS, null);
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }

    //Store version code
    public void storeversion_code(String VERSION_CODE) {
        editor.putString(KEY_VERSION_CODE, VERSION_CODE);
        editor.commit();

    }

    public String getversion_code(Context context)
    {
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        String vcode= null;
        try {
            vcode = packageManager.getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (pref.getString(KEY_VERSION_CODE, null) != null) {

            try {
                vcode = pref.getString(KEY_VERSION_CODE, packageManager.getPackageInfo(packageName, 0).versionName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return vcode;
        }
        return vcode;
    }

    //Store isAdmin or not?
    public void storeisadmin_code(String ISADMIN) {
        editor.putString(KEY_ISADMIN, ISADMIN);
        editor.commit();

    }

    public String getisadmin_code(Context context)
    {
        String IsAdmin = "0";
        if (pref.getString(KEY_ISADMIN, null) != null) {

                IsAdmin = pref.getString(KEY_ISADMIN, "0");
            return IsAdmin;
        }
        return IsAdmin;
    }


    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

}
