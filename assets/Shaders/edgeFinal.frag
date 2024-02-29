
#import "Common/ShaderLib/GLSLCompat.glsllib"
#import "Common/ShaderLib/MultiSample.glsllib"

uniform COLORTEXTURE m_Texture;
uniform DEPTHTEXTURE m_DepthTexture;
uniform sampler2D m_EdgeMap;
uniform vec4 m_EdgeColor;

varying vec2 texCoord;

void main() {
    
    float factor = texture2D(m_EdgeMap, texCoord).r;   
    //factor *= factor;
    //gl_FragColor = vec4(factor); 
    gl_FragColor = mix(m_EdgeColor, getColor(m_Texture, texCoord), factor);
    //gl_FragColor = texture2D(m_EdgeMap, texCoord);
    //gl_FragColor = vec4(getDepth(m_DepthTexture, texCoord).r) * factor;
    
}
