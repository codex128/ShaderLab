/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shaderlab;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.post.Filter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.texture.Image;
import java.util.ArrayList;

/**
 *
 * @author codex
 */
public class EdgeFilter extends Filter {
    
    private AssetManager assetManager;
    private RenderManager renderManager;
    private ViewPort viewport;
    private Material xEdgeMat, yEdgeMat;
    private Pass xEdgePass, yEdgePass;
    private int width, height;
    
    @Override
    protected void initFilter(AssetManager am, RenderManager rm, ViewPort vp, int w, int h) {
        
        this.assetManager = am;
        this.renderManager = rm;
        this.viewport = vp;
        this.width = w;
        this.height = h;
        
        postRenderPasses = new ArrayList<>();
        
        xEdgeMat = new Material(assetManager, "MatDefs/edgeDetect.j3md");
        xEdgeMat.setBoolean("XPass", true);
        xEdgePass = new Pass() {
            @Override
            public boolean requiresDepthAsTexture() {
                return true;
            }
            @Override
            public void beforeRender() {
                xEdgeMat.setTexture("DepthTexture", getDepthTexture());
                xEdgeMat.setFloat("TexelStep", 1f/width);
            }
        };
        xEdgePass.init(renderManager.getRenderer(), width, height, Image.Format.R32F, Image.Format.Depth32F, 1, xEdgeMat);
        postRenderPasses.add(xEdgePass);
        
        yEdgeMat = new Material(assetManager, "MatDefs/edgeDetect.j3md");
        yEdgePass = new Pass() {
            @Override
            public boolean requiresDepthAsTexture() {
                return true;
            }
            @Override
            public void beforeRender() {
                yEdgeMat.setTexture("Texture", xEdgePass.getRenderedTexture());
                yEdgeMat.setTexture("DepthTexture", getDepthTexture());
                yEdgeMat.setFloat("TexelStep", 1f/height);
            }
        };
        yEdgePass.init(renderManager.getRenderer(), width, height, Image.Format.R32F, Image.Format.Depth32F, 1, yEdgeMat);
        postRenderPasses.add(yEdgePass);
        
        material = new Material(assetManager, "MatDefs/edgeFinal.j3md");
        material.setTexture("EdgeMap", yEdgePass.getRenderedTexture());
        
    }
    @Override
    protected Material getMaterial() {
        return material;
    }
    @Override
    protected void postQueue(RenderQueue queue) {
        //renderManager.getRenderer().setBackgroundColor(ColorRGBA.BlackNoAlpha);            
        //renderManager.getRenderer().setFrameBuffer(xEdgePass.getRenderFrameBuffer());
        //renderManager.getRenderer().clearBuffers(true, true, true);
        //renderManager.setForcedTechnique("Glow");
        //renderManager.renderViewPortQueues(viewport, false);         
        //renderManager.setForcedTechnique(null);
        //renderManager.getRenderer().setFrameBuffer(viewport.getOutputFrameBuffer());
    }
    @Override
    protected boolean isRequiresDepthTexture() {
        return true;
    }
    
}
