
#import "Common/ShaderLib/GLSLCompat.glsllib"
#import "Common/ShaderLib/Instancing.glsllib"
#import "Common/ShaderLib/Skinning.glsllib"
#import "Common/ShaderLib/MorphAnim.glsllib"

attribute vec3 inPosition;
attribute vec3 inNormal;
attribute vec2 inTexCoord;

varying vec3 wPosition;
varying vec3 wNormal;
varying vec2 texCoord;

void main() {
    
    vec4 modelSpacePos = vec4(inPosition, 1.0);
    vec3 modelSpaceNorm = inNormal;
    
    #ifdef NUM_MORPH_TARGETS
        Morph_Compute(modelSpacePos, modelSpaceNorm);
    #endif
    #ifdef NUM_BONES
        Skinning_Compute(modelSpacePos, modelSpaceNorm);
    #endif
    
    gl_Position = TransformWorldViewProjection(modelSpacePos);
    wPosition = TransformWorld(modelSpacePos).xyz;
    wNormal = TransformWorldNormal(modelSpaceNorm);
    texCoord = inTexCoord;
    
}
