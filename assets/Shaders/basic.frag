
#import "Common/ShaderLib/GLSLCompat.glsllib"

uniform vec4 m_Color;

varying vec3 wPosition;
varying vec3 wNormal;
varying vec2 texCoord;

void main() {
    gl_FragColor = m_Color;
}
