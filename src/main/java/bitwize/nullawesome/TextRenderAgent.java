package bitwize.nullawesome;

import android.graphics.*;
public class TextRenderAgent implements RenderAgent {
    public static final PointF upperLeft = new PointF(80.f, 0.f);
    public static final PointF textPos = new PointF();
    public static final RectF screenRect = new RectF(upperLeft.x - 8.f,
                                                     upperLeft.y,
                                                     upperLeft.x + (DrawAgent.FONT_CHAR_SIZE * TextInfo.CHARS_PER_LINE) + 8.f,
                                                     upperLeft.y + (DrawAgent.FONT_CHAR_SIZE * TextInfo.N_LINES) + 8.f);
    private DrawAgent dagent;
    private Bitmap font;
    private EntityRepository repo;
    
    public TextRenderAgent(DrawAgent a) {
        repo = EntityRepository.get();
        dagent = a;
        font = ContentRepository.get().getBitmap("pac_font");
    }

    public void drawOn(Canvas c) {
        int textEid = repo.findEntityWithComponent(TextInfo.class);
        TextInfo textinfo = (TextInfo)repo.getComponent(textEid, TextInfo.class);
        if(!textinfo.showDisplay) return;
        char curschar = textinfo.lines[textinfo.cursorPos];
        if(textinfo.drawCursor) {
            textinfo.lines[textinfo.cursorPos] = '\0';
        }
        dagent.drawScreenBG(c, screenRect);
        for(int i=0; i<TextInfo.N_LINES;i++) {
            textPos.set(upperLeft);
            textPos.y = upperLeft.y + (i * DrawAgent.FONT_CHAR_SIZE);
            dagent.drawChars(c,
                             textinfo.lines,
                             font,
                             textPos,
                             i * TextInfo.CHARS_PER_LINE, TextInfo.CHARS_PER_LINE);
        }
        textinfo.lines[textinfo.cursorPos] = curschar;
    }
}
