package bitwize.nullawesome;

import android.graphics.*;
public class TextRenderAgent implements RenderAgent {
    public static final int N_LINES = 6;
    public static final int CHARS_PER_LINE = 20;
    public static final PointF upperLeft = new PointF(80.f, 0.f);
    public static final PointF textPos = new PointF();
    private DrawAgent dagent;
    private Bitmap font;
    private char[] lines = new char[N_LINES * CHARS_PER_LINE];
    
    public TextRenderAgent(DrawAgent a) {
	dagent = a;
	font = ContentRepository.get().getBitmap("pac_font");
	for(int j=0; j<N_LINES * CHARS_PER_LINE; j++) {
	    lines[j] = ' ';
	}
	String s = "Hack the planet!";
	for(int i=0; i<s.length(); i++) {
	    lines[i] = s.charAt(i);
	}
    }

    public void drawOn(Canvas c) {
	for(int i=0; i<N_LINES;i++) {
	    textPos.set(upperLeft);
	    textPos.y = upperLeft.y + (i * DrawAgent.FONT_CHAR_SIZE);
	    dagent.drawChars(c, lines, font, textPos, i * CHARS_PER_LINE, CHARS_PER_LINE);
	}
    }
}
