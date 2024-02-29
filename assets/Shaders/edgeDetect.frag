
#import "Common/ShaderLib/GLSLCompat.glsllib"
#import "Common/ShaderLib/MultiSample.glsllib"

uniform COLORTEXTURE m_Texture;
uniform DEPTHTEXTURE m_DepthTexture;
uniform int m_Radius;
uniform float m_TexelStep;

varying vec2 texCoord;

bool isEdge(vec4 c1, vec4 c2) {
    return abs(c1.r - c2.r) > 0.01;
}
#ifdef X_PASS
    bool computeSample(inout float render, inout vec4 sampleColor, inout vec4 prevColor, float i) {
        if (isEdge(prevColor, sampleColor)) {
            render = i;
            return true;
        }
        return false;
    }
#else
    void computeSample(inout float render, inout vec4 sampleColor, inout vec4 prevColor, float dist, float i) {
        if (dist < 1.0) {
            float x = dist * m_Radius;
            float dsq = x*x + i*i;
            if (dsq < render) {
                render = dsq;
            }
        }
        if (isEdge(sampleColor, prevColor)) {
            float dsq = i*i;
            if (dsq < render) {
                render = dsq;
            }
        }
    }
#endif

void main() {
    
    vec4 prevSamplePos = getDepth(m_DepthTexture, texCoord);
    vec4 prevSampleNeg = prevSamplePos;
    #ifdef X_PASS
        float render = m_Radius;
    #else
        float render = m_Radius * m_Radius;
    #endif
    
    #ifdef X_PASS
        int i = 1;
    #else
        int i = 0;
    #endif
    
    for (; i < m_Radius; i++) {
        #ifdef X_PASS
            vec2 tPos = vec2(texCoord.x + m_TexelStep * i, texCoord.y);
            vec2 tNeg = vec2(texCoord.x - m_TexelStep * i, texCoord.y);
        #else
            vec2 tPos = vec2(texCoord.x, texCoord.y + m_TexelStep * i);
            vec2 tNeg = vec2(texCoord.x, texCoord.y - m_TexelStep * i);
        #endif
        vec4 sPos = getDepth(m_DepthTexture, tPos);
        #ifdef X_PASS
            if (computeSample(render, sPos, prevSamplePos, i)) {
                break;
            }
        #else
            computeSample(render, sPos, prevSamplePos, getColor(m_Texture, tPos).r, i);
        #endif
        prevSamplePos = sPos.rgba;
        vec4 sNeg = getDepth(m_DepthTexture, tNeg);
        #ifdef X_PASS
            if (computeSample(render, sNeg, prevSampleNeg, i)) {
                break;
            }
        #else
            computeSample(render, sNeg, prevSampleNeg, getColor(m_Texture, tNeg).r, i);
        #endif
        prevSampleNeg = sNeg.rgba;
    }
    
    #ifndef X_PASS
        render = sqrt(render);
    #endif
    
    render /= m_Radius;
    
    gl_FragColor = vec4(render);
    
}





















