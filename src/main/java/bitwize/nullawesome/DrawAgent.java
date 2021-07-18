package bitwize.nullawesome;

import android.graphics.*;
import android.view.*;
import java.util.*;
import android.util.Log;
import android.util.DisplayMetrics;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.io.IOException;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;


public class DrawAgent implements GLSurfaceView.Renderer {

    public static final int HRES = 480;
    public static final int VRES = 320;

    public static final float[] rectVertices = {
	-1.0f, -1.0f, 0.0f, 0.0f, 0.0f,
	1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
	-1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
	1.0f, 1.0f, 0.0f, 1.0f, 1.0f
    };

    public static final short[] rectIndices = {
	0, 3, 2,
	0, 1, 3
    };

    public static final FloatBuffer rectVbuf;
    public static final ShortBuffer rectIbuf;

    public static final int FLOAT_SIZE = 4;
    public static final int SHORT_SIZE = 2;
    
    static {
	ByteBuffer b1 = ByteBuffer.allocateDirect(FLOAT_SIZE * rectVertices.length);
	ByteBuffer b2 = ByteBuffer.allocateDirect(SHORT_SIZE * rectIndices.length);
	b1.order(ByteOrder.nativeOrder());
	b2.order(ByteOrder.nativeOrder());
	rectVbuf = b1.asFloatBuffer();
	rectIbuf = b2.asShortBuffer();
	rectVbuf.put(rectVertices);
	rectIbuf.put(rectIndices);
	rectVbuf.position(0);
	rectIbuf.position(0);
    }
    
    private ArrayList<RenderAgent> rlist;
    private boolean running;
    private Rect src, dst;
    private Matrix xform;
    private int wwidth, wheight;
    private int framebuffer;
    private int fbtex;
    private int vbuffer, ibuffer;
    private int backbufProgram;
    private int polyProgram;
    public DrawAgent(ArrayList<RenderAgent> r) {
	rlist = r;
	src = new Rect();
	dst = new Rect();
	xform = new Matrix();
    }
    
    public void draw() {
	for(int i=0; i<rlist.size();i++) {
	    rlist.get(i).draw();
	}
    }

    public void drawMap(TileMap map, Point offset) {
	int w = HRES;
	int h = VRES;
	int tw = (w / TileMap.TILE_SIZE) + 1;
	int th = (h / TileMap.TILE_SIZE) + 1;
	int tl = offset.x / TileMap.TILE_SIZE;
	int tt = offset.y / TileMap.TILE_SIZE;
	int tr = tl + tw;
	int tb = tt + th;
	if(tl < 0) tl = 0;
	if(tt < 0) tt = 0;
	if(tr > map.getWidth()) tr = map.getWidth();
	if(tb > map.getHeight()) tb = map.getHeight();
	for(int j=tt; j<tb; j++) {
	    for(int i=tl; i<tr; i++) {
		int oy = j * TileMap.TILE_SIZE;
		int ox = i * TileMap.TILE_SIZE;
		short tile = map.getTile(i, j);
		int flags;
		short frame = map.getFrame();
		if(tile <= 0) continue;
		flags = map.getTileFlags(tile);
		src.left = ((flags & TileMap.FLAG_ANIMATE) != 0)
		    ? TileMap.TILE_SIZE * frame
		    : 0;
		src.top = TileMap.TILE_SIZE * tile;
		src.right = src.left + TileMap.TILE_SIZE;
		src.bottom = src.top + TileMap.TILE_SIZE;
		dst.left = ox - offset.x;
		dst.top = oy - offset.y;
		dst.right = dst.left + TileMap.TILE_SIZE;
		dst.bottom = dst.top + TileMap.TILE_SIZE;
	    }
	}
    }

    public void drawSprite(Bitmap b, Rect subsection, PointF location) {
	dst.left = (int)location.x;
	dst.top = (int)location.y;
	dst.right = dst.left + (subsection.right - subsection.left);
	dst.bottom = dst.top + (subsection.bottom - subsection.top);
    }
    public void drawTileBG(Bitmap b, Rect subsection, PointF location) {
	int w = subsection.width();
	int h = subsection.height();
	int startx = -(((int)location.x) % w);
	int starty = -(((int)location.y) % h);
	if(startx > 0) startx -= w;
	if(starty > 0) starty -= h;
	for(; starty < VRES; starty += h) {
	    for(; startx < HRES; startx += w) {
		dst.left = startx;
		dst.top = starty;
		dst.right = dst.left + w;
		dst.bottom = dst.top + h;
	    }
	}
    }
    public void drawButton(Bitmap b, Rect subsection, PointF location) {
	drawSprite(b, subsection, location);
    }

    public int setupShaderProgram(int vshadRes, int fshadRes) {
	int shaderStatus[] = new int[1];
	int vshad, fshad, pgm;
	String vshadSrc;
	String fshadSrc;
	try {
	    vshadSrc = ContentRepository.get().loadString(vshadRes);
	    fshadSrc = ContentRepository.get().loadString(fshadRes);
	}
	catch(IOException e) {
	    Log.e("DrawAgent", "could not load shader source");
	    return 0;
	}
	vshad = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
	GLES20.glShaderSource(vshad, vshadSrc);
	GLES20.glCompileShader(vshad);
	GLES20.glGetShaderiv(vshad, GLES20.GL_COMPILE_STATUS, shaderStatus, 0);
	if(shaderStatus[0] == 0) {
	    Log.e("DrawAgent", "could not compile shader: " + GLES20.glGetShaderInfoLog(vshad));
	    return 0;
	}
	fshad = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
	GLES20.glShaderSource(fshad, fshadSrc);
	GLES20.glCompileShader(fshad);
	GLES20.glGetShaderiv(fshad, GLES20.GL_COMPILE_STATUS, shaderStatus, 0);
	if(shaderStatus[0] == 0) {
	    Log.e("DrawAgent", "could not compile shader: " + GLES20.glGetShaderInfoLog(fshad));
	    return 0;
	}
	pgm = GLES20.glCreateProgram();
	GLES20.glAttachShader(pgm, vshad);
	GLES20.glAttachShader(pgm, fshad);
	GLES20.glLinkProgram(pgm);
	GLES20.glGetProgramiv(pgm, GLES20.GL_LINK_STATUS, shaderStatus, 0);
	if(shaderStatus[0] == 0) {
	    Log.e("DrawAgent", "could not link program: " + GLES20.glGetProgramInfoLog(pgm));
	    return 0;
	}
	return pgm;
    }
    
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
	int fbNames[] = new int[1];
	int fbtexNames[] = new int[1];
	int vboNames[] = new int[2];

	// set some gl context params
	GLES20.glDisable(GLES20.GL_DEPTH_TEST);
	GLES20.glDisable(GLES20.GL_DITHER);

	// set up the FBO and back-buffer texture
	GLES20.glGenFramebuffers(1, fbNames, 0);
	GLES20.glGenTextures(1, fbtexNames, 0);
	GLES20.glGenBuffers(2, vboNames, 0);
	framebuffer = fbNames[0];
	fbtex = fbtexNames[0];
	vbuffer = vboNames[0];
	ibuffer = vboNames[1];
	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fbtex);
	GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
			    HRES, VRES,
			    0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
	GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
	GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
	GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
	GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
	GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer);
	GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, fbtex, 0);
	GLES20.glClearColor(1.0f,0.0f,0.0f,1.0f);
	GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
	GLES20.glFinish();
	if(GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) == GLES20.GL_FRAMEBUFFER_COMPLETE) {
	    Log.i("DrawAgent", "framebuffer successfully generated");
	}
	GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

	// setup vertex and index buffers
	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbuffer);
	GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, rectVbuf.limit() * FLOAT_SIZE, rectVbuf, GLES20.GL_STATIC_DRAW);
	GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibuffer);
	GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, rectIbuf.limit() * SHORT_SIZE, rectIbuf, GLES20.GL_STATIC_DRAW);
	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

	//setup shader program
	backbufProgram = setupShaderProgram(R.raw.spritevshader, R.raw.spritefshader);
	polyProgram = setupShaderProgram(R.raw.polyvshader, R.raw.polyfshader);
    }
    
    public void onSurfaceChanged(GL10 gl, int width, int height) {
	wwidth = width;
	wheight = height;
    }

    public void onDrawFrame(GL10 gl) {
	synchronized(this) {
	    int posAttrib, tcAttrib, texU;
	    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer);
	    GLES20.glViewport(0, 0, HRES, VRES);
	    GLES20.glUseProgram(polyProgram);
	    posAttrib = GLES20.glGetAttribLocation(polyProgram, "position");
	    GLES20.glClearColor(1.0f,0.0f,0.0f,1.0f);
	    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
	    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbuffer);
	    GLES20.glVertexAttribPointer(posAttrib, 3, GLES20.GL_FLOAT, false, 20, 0);
	    GLES20.glEnableVertexAttribArray(posAttrib);
	    GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibuffer);
	    GLES20.glDrawElements(GLES20.GL_TRIANGLES, 3, GLES20.GL_UNSIGNED_SHORT, 0);
	    GLES20.glFlush();
	    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	    GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	    GLES20.glUseProgram(0);    
	    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
	    GLES20.glViewport(0, 0, wwidth, wheight);
	    GLES20.glDepthRangef(0.0f, 1.0f);
	    GLES20.glUseProgram(backbufProgram);
	    posAttrib = GLES20.glGetAttribLocation(backbufProgram, "position");
	    tcAttrib = GLES20.glGetAttribLocation(backbufProgram, "in_texcoord");
	    texU = GLES20.glGetUniformLocation(backbufProgram, "texture");
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fbtex);
	    GLES20.glUniform1i(texU, 0);
	    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbuffer);
	    GLES20.glVertexAttribPointer(posAttrib, 3, GLES20.GL_FLOAT, false, 20, 0);
	    GLES20.glVertexAttribPointer(tcAttrib, 2, GLES20.GL_FLOAT, false, 20, 12);
	    GLES20.glEnableVertexAttribArray(posAttrib);
	    GLES20.glEnableVertexAttribArray(tcAttrib);
	    GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibuffer);
	    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
	    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
	    GLES20.glDrawElements(GLES20.GL_TRIANGLES, rectIbuf.limit(), GLES20.GL_UNSIGNED_SHORT, 0);
	    GLES20.glFlush();
	    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	    GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	    GLES20.glUseProgram(0);
	}
    }
}
