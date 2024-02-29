/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.shaderlab;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author codex
 */
public class BloomTest extends SimpleApplication {
    
    
    public static void main(String[] args) {
        var app = new BloomTest();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        
        flyCam.setMoveSpeed(10);        
        rootNode.addLight(new DirectionalLight(new Vector3f(1, -1, -1)));        
        
        var mat = new Material(assetManager, "MatDefs/basic.j3md");
        mat.setColor("Color", ColorRGBA.White);
        
        var box = new Geometry("box", new Box(1, 1, 1));
        box.setMaterial(mat);
        rootNode.attachChild(box);
        
        var sphere = new Geometry("box", new Sphere(100, 100, 1));
        sphere.setLocalTranslation(3, 0, 0);
        sphere.setMaterial(mat);
        rootNode.attachChild(sphere);
        
        var fpp = new FilterPostProcessor(assetManager);
        viewPort.addProcessor(fpp);
        
//        var bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
//        bloom.setBloomIntensity(2f);
//        bloom.setBlurScale(5);
//        bloom.setDownSamplingFactor(1f);
//        bloom.setExposureCutOff(1f);
//        bloom.setExposurePower(2f);
//        fpp.addFilter(bloom);

        var bloom = new BetterBloomFilter();
        fpp.addFilter(bloom);
        
    }
    
}
