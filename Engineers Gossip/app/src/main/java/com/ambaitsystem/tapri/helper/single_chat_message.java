package com.ambaitsystem.tapri.helper;

public class single_chat_message {
    protected String StrSingle_chat_user_name, StrSingle_chat_user_id, StrSingle_chat_message, StrSingle_chat_user_email, StrSingle_chat_timestamp;

    public void SetSingle_chat_user_name(String name) {
        StrSingle_chat_user_name = name;
    }

    public void SetSingle_chat_user_id(String user_id) {
        StrSingle_chat_user_id = user_id;
    }

    public void SetSingle_chat_message(String Message) {
        StrSingle_chat_message = Message;
    }

    public void SetStrSingle_chat_user_email(String Email) {
        StrSingle_chat_user_email = Email;
    }

    public void SetStrSingle_chat_timestamp(String TimeStamp) {
        StrSingle_chat_timestamp = TimeStamp;
    }


    public String GetSingle_chat_user_name() {

        return StrSingle_chat_user_name;
    }

    public String GetSingle_chat_user_id() {

        return StrSingle_chat_user_id;
    }

    public String GetSingle_chat_message() {

        return StrSingle_chat_message;
    }

    public String GetStrSingle_chat_user_email() {

        return StrSingle_chat_user_email;
    }

    public String GetStrSingle_chat_timestamp() {

        return StrSingle_chat_timestamp;
    }
}
