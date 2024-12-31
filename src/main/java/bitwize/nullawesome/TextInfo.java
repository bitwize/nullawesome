package bitwize.nullawesome;

public class TextInfo {
    public static final int N_LINES = 6;
    public static final int CHARS_PER_LINE = 20;
    public static final int JIFFIES_PER_CHAR = 3;
    public static final int JIFFIES_PER_BLINK = 15;
    public static String readyString = ContentRepository.get().getString("ready");
    public static String scanString = ContentRepository.get().getString("scan");
    public static String selectString = ContentRepository.get().getString("select");
    public static String accessString = ContentRepository.get().getString("access");
    public static String grantedString = ContentRepository.get().getString("granted");
    public static String deniedString = ContentRepository.get().getString("denied");
    public static String copyString = ContentRepository.get().getString("copy");
    public static String copiedString = ContentRepository.get().getString("copied");
    public StringBuilder textBuffer = new StringBuilder();
    public int typeTimer;
    public int cursorTimer;
    public int cursorPos;
    public boolean drawCursor;
    public boolean showDisplay = false;
    public int displayTime = 0;
    public int maxDisplayTime = 300;
    public char[] lines = new char[N_LINES * CHARS_PER_LINE];
}
