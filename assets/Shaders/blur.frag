
#import "Common/ShaderLib/GLSLCompat.glsllib"
#import "Common/ShaderLib/MultiSample.glsllib"

uniform COLORTEXTURE m_Texture;
uniform int m_Samples;
uniform int m_SampleLength;
uniform bool m_Horizontal;

varying vec2 texCoord;

float mapRange(float value, float inMin, float inMax, float outMin, float outMax) {
    float factor = (value - inMin)/(inMax - inMin);
    factor = smoothstep(0.0, 1.0, factor);
    return (outMax - outMin) * factor + outMin;
}

void main() {
    
    vec2 offset = (1.0 / textureSize(m_Texture, 0)) * m_SampleLength;
    vec4 result = getColor(m_Texture, texCoord);
    
    if (m_Horizontal) {
        for (int i = 1; i <= m_Samples; i++) {
            float factor = mapRange(i, 1, m_Samples, 1.0, 0.0);
            vec2 s = vec2(offset.x * i, 0.0);
            vec4 c1 = getColor(m_Texture, texCoord + s);
            vec4 c2 = getColor(m_Texture, texCoord - s);
            result += c1 * factor;
            result += c2 * factor;
        }
    } else {
        for (int i = 1; i <= m_Samples; i++) {
            float factor = mapRange(i, 1, m_Samples, 1.0, 0.0);
            vec2 s = vec2(0.0, offset.y * i);
            vec4 c1 = getColor(m_Texture, texCoord + s);
            vec4 c2 = getColor(m_Texture, texCoord - s);
            result += c1 * factor;
            result += c2 * factor;
        }
    }
    
    gl_FragColor = result;
    
}
