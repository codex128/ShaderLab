/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shaderlab;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.post.Filter;
import com.jme3.post.filters.BloomFilter.GlowMode;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.texture.Image.Format;
import java.util.ArrayList;

/**
 *
 * @author codex
 */
public class BetterBloomFilter extends Filter {
    
    private RenderManager renderManager;
    private ViewPort viewPort;
    
    private Material extractMat, hBlurMat, vBlurMat;
    private Pass prePass, extractPass, hBlurPass, vBlurPass;
    
    private GlowMode mode = GlowMode.Objects;
    private float exposurePower = 5.0f;
    private float exposureCutOff = 0.0f;
    
    @Override
    protected void initFilter(AssetManager assetManager, RenderManager renderManager, ViewPort vp, int w, int h) {
        
        this.renderManager = renderManager;
        this.viewPort = vp;
        postRenderPasses = new ArrayList<Pass>();
        
        if (mode != GlowMode.Scene) {
            prePass = new Pass();
            prePass.init(renderManager.getRenderer(), w, h, Format.RGBA8, Format.Depth);
        }
        
        extractMat = new Material(assetManager, "Common/MatDefs/Post/BloomExtract.j3md");
        extractPass = new Pass() {
            @Override
            public boolean requiresSceneAsTexture() {
                return true;
            }
            @Override
            public void beforeRender() {
                extractMat.setFloat("ExposurePow", exposurePower);
                extractMat.setFloat("ExposureCutoff", exposureCutOff);
                if (mode != GlowMode.Scene) {
                    extractMat.setTexture("GlowMap", prePass.getRenderedTexture());
                }
                extractMat.setBoolean("Extract", mode != GlowMode.Objects);
            }
        };        
        extractPass.init(renderManager.getRenderer(), w, h, Format.RGBA8, Format.Depth, 1, extractMat);
        postRenderPasses.add(extractPass);

        //configuring horizontal blur pass
        hBlurMat = new Material(assetManager, "MatDefs/blur.j3md");
        hBlurMat.setBoolean("Horizontal", true);
        hBlurPass = new Pass() {
            @Override
            public void beforeRender() {
                hBlurMat.setTexture("Texture", extractPass.getRenderedTexture());
            }
        };
        hBlurPass.init(renderManager.getRenderer(), w, h, Format.RGBA8, Format.Depth, 1, hBlurMat);
        postRenderPasses.add(hBlurPass);

        //configuring vertical blur pass
        vBlurMat = new Material(assetManager, "MatDefs/blur.j3md");
        vBlurMat.setBoolean("Horizontal", false);
        vBlurPass = new Pass() {
            @Override
            public void beforeRender() {
                vBlurMat.setTexture("Texture", hBlurPass.getRenderedTexture());
            }
        };
        vBlurPass.init(renderManager.getRenderer(), w, h, Format.RGBA8, Format.Depth, 1, vBlurMat);
        postRenderPasses.add(vBlurPass);

        //final material
        material = new Material(assetManager, "Common/MatDefs/Post/BloomFinal.j3md");
        material.setTexture("BloomTex", vBlurPass.getRenderedTexture());
        material.setFloat("BloomIntensity", 1);
        
    }
    @Override
    protected Material getMaterial() {
        return material;
    }    
    @Override
    protected void postQueue(RenderQueue queue) {
        if (mode != GlowMode.Scene) {           
            renderManager.getRenderer().setBackgroundColor(ColorRGBA.BlackNoAlpha);            
            renderManager.getRenderer().setFrameBuffer(prePass.getRenderFrameBuffer());
            renderManager.getRenderer().clearBuffers(true, true, true);
            renderManager.setForcedTechnique("Glow");
            renderManager.renderViewPortQueues(viewPort, false);
            renderManager.setForcedTechnique(null);
            renderManager.getRenderer().setFrameBuffer(viewPort.getOutputFrameBuffer());
        }
    }
    @Override
    protected void cleanUpFilter(Renderer r) {
        if (mode != GlowMode.Scene) {   
            prePass.cleanup(r);
        }
    }
    
}
