package shine.app.tubeviewmultitask;

/**
 * Created by Shine Tran on 16/2/16.
 */
public class Constants {

    //Type of player
    //WebView player = 0
    //Youtube player = 1
    public  static  int playerType = 0;

    //Type of link
    //Single song link = 0
    //Playlist link = 1
    public  static  int linkType = 0;

    //Repeat
    //if repeatType = 0  --> no repeatType
    //if repeatType = 1  --> repeatType complete
    //if repeatType = 2  --> repeatType single
    public  static  int repeatType = 0;
    public  static  int noOfRepeats = 0;
    //Playback Quality
    //0 = auto
    //1 = hd1080
    //2 = hd720
    //3 = large(480p)
    //4 = medium(360p)
    //5 = small(240p)
    //6 = tiny(144p)
    public  static  int playbackQuality = 0;
    //Finish service on end video
    public static boolean finishOnEnd = false;
    public static boolean autoFloating = false;


    private static String strPlaybackQuality = "Auto";
    public static String getPlaybackQuality() {
        if(playbackQuality == 0){
            strPlaybackQuality = "Auto";
        }
        else if (playbackQuality == 1){
            strPlaybackQuality = "HD1080";
        }
        else if (playbackQuality == 2){
            strPlaybackQuality = "HD720";
        }
        else if (playbackQuality == 3){
            strPlaybackQuality = "Large";
        }
        else if (playbackQuality == 4){
            strPlaybackQuality = "Medium";
        }
        else if (playbackQuality == 5){
            strPlaybackQuality = "Small";
        }
        else{
            strPlaybackQuality = "Tiny";
        }
        return strPlaybackQuality;
    }


    //Actions
    public interface ACTION {
        public static String PREV_ACTION = "shine.app.tubeviewmultitask.action.prev";
        public static String PAUSE_PLAY_ACTION = "shine.app.tubeviewmultitask.action.play";
        public static String NEXT_ACTION = "shine.app.tubeviewmultitask.action.next";
        public static String STARTFOREGROUND_WEB_ACTION = "shine.app.tubeviewmultitask.action.playingweb";
        public static String STOPFOREGROUND_WEB_ACTION = "shine.app.tubeviewmultitask.action.stopplayingweb";
        public static String STARTFOREGROUND_YTUBE_ACTION = "shine.app.tubeviewmultitask.action.playingytube";
        public static String STOPFOREGROUND_YTUBE_ACTION = "shine.app.tubeviewmultitask.action.stopplayingytube";
    }

    //Notification Id
    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
    static final public String RESULT = "shine.app.tubeviewmultitask.Service.REQUEST_PROCESSED";
    static final public String MESSAGE = "shine.app.tubeviewmultitask.Service.MSG";
    static final public String SEND_BACK_PLAYER = "shine.app.tubeviewmultitask.Service.SEND";
    static final public String TAG_FR = "shine.app.tubeviewmultitask.Frantment_TAG";
}
