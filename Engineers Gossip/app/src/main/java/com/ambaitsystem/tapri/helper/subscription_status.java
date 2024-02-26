package com.ambaitsystem.tapri.helper;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class subscription_status
{
    public SQLiteDatabase db=null;
    public boolean check_subscription_status(Context context,String ChatRoomId)
    {
        //Create Table
        create_DB(context);
        boolean RoomId_subscription_Status = Check_ChatRoomId_Exsist_OR_Not(context,ChatRoomId);

        return RoomId_subscription_Status;
    }

    public void create_DB(Context context)
    {
        db =(new DbBasic(context)).getWritableDatabase();

        try
        {
            db.execSQL("CREATE TABLE IF NOT EXISTS subscriber_status_engggossip (_id INTEGER PRIMARY KEY,chatroom_id TEXT,subscriptionstatus TEXT);");
            db.close();
        }
        catch(Exception e)
        {
            db.close();

        }
    }

    public boolean Update_ChatRoomId_for_Subscribe_OR_Unsubscribe(Context context,String RoomId,String subOrUnsub)
    {
        //Insert ChatRoomID to DB With subscriber_status = true
        db = (new DbBasic(context)).getWritableDatabase();

        try {
            String Insert_Query = "Update subscriber_status_engggossip set subscriptionstatus = '"+subOrUnsub +"' Where chatroom_id = "+ RoomId;
            //Log.v("DB in Update","# " + subOrUnsub + "Room : " + RoomId );
            db.execSQL(Insert_Query);
            db.close();
        } catch (Exception e)
        {
           // Log.v("Exception Update 1","#"+e.toString());
            db.close();
            return  false;
        }
        return  true;
    }

    public boolean Check_ChatRoomId_Exsist_OR_Not(Context context,String RoomId)
    {
        try
        {
            db =(new DbBasic(context)).getReadableDatabase();
            String Sql = "SELECT subscriptionstatus FROM subscriber_status_engggossip where chatroom_id = "+RoomId;
            //Log.v("RoomID","#"+RoomId);
            Cursor constantsCursor = db.rawQuery(Sql,null);
            constantsCursor.moveToFirst();
            try
            {
                if(constantsCursor.getCount()<=0)
                {
                    db.close();
                    constantsCursor.close();

                    //Insert ChatRoomID to DB With subscriber_status = true
                    db = (new DbBasic(context)).getWritableDatabase();

                    try {
                        String Insert_Query = "INSERT INTO subscriber_status_engggossip (chatroom_id,subscriptionstatus) VALUES('" +  RoomId +"','true');";
                        db.execSQL(Insert_Query);
                        db.close();
                    } catch (Exception e) {
                       // Log.v("Exception 1","#"+e.toString());
                        db.close();
                    }

                    return  true;
                }
                else
                {
                  //  Log.v("Next Time","#"+constantsCursor.getString(0).toString());
                    Boolean status = Boolean.valueOf(constantsCursor.getString(0).toString());

                    db.close();
                    constantsCursor.close();

                    return status;

                }

            }
            catch(Exception  e)
            {
                db.close();
                //Log.v("Exception 2", "#" + e.toString());
                constantsCursor.close();

            }

        }
        catch(Exception e)
        {
          //  Log.v("Exception 3","#"+e.toString());
            db.close();

        }

        return false;
    }
}
