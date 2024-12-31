package bitwize.nullawesome;

public class TextUpdateAgent implements UpdateAgent {
    private EntityRepository repo;
    public TextUpdateAgent() {
        repo = EntityRepository.get();
        int textEid = repo.findEntityWithComponent(TextInfo.class);
        TextInfo textinfo = (TextInfo)repo.getComponent(textEid, TextInfo.class);
        addString(textinfo, TextInfo.readyString);
    }

    public void addString(TextInfo ti, String str) {
        ti.textBuffer.append(str);
    }

    public void addChar(TextInfo ti, char c) {
        ti.textBuffer.append(c);
    }

    public void lineFeed(TextInfo ti) {
        ti.cursorPos = ((ti.cursorPos + TextInfo.CHARS_PER_LINE) / TextInfo.CHARS_PER_LINE) * TextInfo.CHARS_PER_LINE;
        scrollDisplayIfNeeded(ti);
    }

    public void advanceChar(TextInfo ti) {
        ti.cursorPos++;
        scrollDisplayIfNeeded(ti);
    }

    public void typeChar(TextInfo ti) {
        if(ti.textBuffer.length() < 1) {
            return;
        }
        char c = ti.textBuffer.charAt(0);
        if(c == '\n') {
            lineFeed(ti);
        } else {
            ti.lines[ti.cursorPos] = c;
            advanceChar(ti);
        }
        ti.textBuffer.deleteCharAt(0);
    }

    public void scrollDisplayIfNeeded(TextInfo ti) {
        if(ti.cursorPos >= ti.lines.length) {
            int sizeOfAllButLastLine = TextInfo.CHARS_PER_LINE * (TextInfo.N_LINES - 1);
            for(int i=0; i<ti.lines.length; i++) {
                if(i >= sizeOfAllButLastLine) {
                    ti.lines[i] = ' ';
                } else {
                    ti.lines[i] = ti.lines[i + TextInfo.CHARS_PER_LINE];
                }
            }
            ti.cursorPos = sizeOfAllButLastLine;
        }
    }
    
    public void update(long time) {
        int textEid = repo.findEntityWithComponent(TextInfo.class);
        TextInfo textinfo = (TextInfo)repo.getComponent(textEid, TextInfo.class);
        textinfo.typeTimer++;
        if(textinfo.typeTimer >= TextInfo.JIFFIES_PER_CHAR) {
            typeChar(textinfo);
            textinfo.typeTimer = 0;
        }
        textinfo.cursorTimer++;
        if(textinfo.cursorTimer >= TextInfo.JIFFIES_PER_BLINK) {
            textinfo.drawCursor = !(textinfo.drawCursor);
            textinfo.cursorTimer = 0;
        }
        if(textinfo.displayTime > 0) {
            textinfo.displayTime--;
            if(textinfo.displayTime <= 0) {
                textinfo.showDisplay = false;
            }
        }
    }
}
