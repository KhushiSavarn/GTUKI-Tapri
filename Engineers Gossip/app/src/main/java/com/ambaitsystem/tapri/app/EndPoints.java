package com.ambaitsystem.tapri.app;


public class EndPoints {

    // localhost url
    // public static final String BASE_URL = "http://192.168.0.101/gcm_chat/v1";
    public static final String BASE_URL ="https://realyatri.com/ait_test_dir/v1/";
    public static final String LOGIN = BASE_URL + "index_v2.php/user/login";
    public static final String SEARCH_FRIENDS = BASE_URL + "index_v2.php/friends_ontapri_search/search";
    public static final String MY_SEARCH_FRIENDS = BASE_URL + "index_v2.php/my_friends_ontapri_search/search";


    public static final String USER = BASE_URL + "index_v2.php/user/_ID_";
    public static final String CHAT_ROOMS = BASE_URL + "index_v2.php/chat_rooms";
    public static final String FRIENDS_ON_TAPRI = BASE_URL + "index_v2.php/friends_ontapri";

    public static final String PRODUCTS_ON_TAPRI = BASE_URL + "index_v2.php/products_ontapri";

    public static final String CREATE_MY_PRODUCTS_ON_TAPRI = BASE_URL + "index_v2.php/product/create";
    public static final String LIST_MY_PRODUCTS_ON_TAPRI = BASE_URL + "index_v2.php/my_products_ontapri";
    public static final String DELETE_MY_PRODUCTS_ON_TAPRI = BASE_URL + "index_v2.php/delete_my_products_ontapri";

    public static final String FRIENDS_ON_TAPRI_LASTONLINE = BASE_URL + "index_v2.php/friends_ontapri_lastonline";

    public static final String REQUEST_STATUS = BASE_URL + "index_v2.php/request_status";
    public static final String REQUEST_STATUS_NEWS = BASE_URL + "index_v2.php/request_news";
    public static final String REQUEST_ALL_NEWS_TO_APPROVE = BASE_URL + "index_v2.php/request_news_approval";

    public static final String CREATE_MY_NEWS_DETAIL = BASE_URL + "index_v2.php/create_news_detail";
    public static final String LIST_MY_NACTIVITY = BASE_URL + "index_v2.php/my_news_activity";
    public static final String LIKE_ACTIVITY = BASE_URL + "index_v2.php/like_news_activity";
    public static final String APPROVE_REJECT_NEWS = BASE_URL + "index_v2.php/approve_reject_news";



    public static final String ADDRESS_FOR_GIFT = BASE_URL + "index_v2.php/store_address_for_gift";
    public static final String REPORTING_NEWS = BASE_URL + "index_v2.php/reporting_news";

    public static final String UPDATE_TO_PRIME = BASE_URL + "index_v2.php/update_product_to_premium";

    public static final String RESPOND_STATUS = BASE_URL + "index_v2.php/respond_update_status";
    public static final String REMOVE_FRIEND = BASE_URL + "index_v2.php/remove_friend";

    public static final String INCREASE_VIEW_COUNT = BASE_URL + "index_v2.php/increase_view_count";

    public static final String FILE_UPLOAD_URL = BASE_URL + "image_upload.php";
    public static final String PRODUCT_FILE_UPLOAD_URL = BASE_URL + "product_upload.php";
    public static final String NEWS_FILE_UPLOAD_URL = BASE_URL + "news_upload.php";

    public static final String OFFER_TEA = BASE_URL + "index_v2.php/offeringtea";

    public static final String CHAT_THREAD = BASE_URL + "index_v2.php/chat_rooms/_ID_";
    public static final String SINGLE_ROOM_MESSAGE_PUSH = BASE_URL + "index_v2.php/users/message";

    public static final String CHAT_ROOM_MESSAGE = BASE_URL + "index_v2.php/chat_rooms/_ID_/message";
    public static final String APP_UPDATE = BASE_URL + "User_Add_Reply_userId_offlinecount.php";

    public static final String APP_PACKAGE_NAME = "com.ambaitsystem.vgecchat";
    public static final int Grid_spacing = 2;
    public static final int Thrashold_gift_on_news = 121;
    public static final String APP_BASE_FOR_RSS_SPLASH = "http://niruma.tv/ait/";


}
