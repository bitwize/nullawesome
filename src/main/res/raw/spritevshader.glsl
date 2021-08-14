attribute vec3 position;
attribute vec2 in_texcoord;
uniform vec4 rect;
uniform vec2 pixelSize;
varying vec2 texcoord;
void main(void) {
     texcoord = in_texcoord;
     gl_Position = vec4((rect.x * pixelSize.x) + (rect.z * pixelSize.x * position.x) - 1.0,
			(rect.y * pixelSize.y) + (rect.w * pixelSize.y * position.y) - 1.0,
			0.0,
			1.0);
}
