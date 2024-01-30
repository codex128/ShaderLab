package codex.shaderlab;

import codex.boost.scene.SceneGraphIterator;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.VideoRecorderAppState;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.io.File;
import java.util.ArrayList;
import jme3utilities.math.noise.Generator;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class ViewerSuite extends SimpleApplication {
    
    public static final String TEST_SUBJECT_ID = "TestSubject";
    public static final String COLOR_ID = "Color";
    
    public static final boolean CAPTURE_VIDEO = !true;
    
    private static final Generator gen = new Generator();
    
    public static void main(String[] args) {
        ViewerSuite app = new ViewerSuite();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        var material = new Material(assetManager, "MatDefs/hologram.j3md");
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        material.setTransparent(true);
        material.setBoolean("UseVertexColor", true);
        //material.setFloat("", 1.0f);
        
        var model = assetManager.loadModel("Scenes/shader-draft-suite.j3o");
        rootNode.attachChild(model);
        var iterator = new SceneGraphIterator(model);
        for (var s : iterator) {
            var color = getColorData(s, COLOR_ID);
            if (color != null) {
                PointLight p = new PointLight(s.getWorldTranslation(), color, 5f);
                rootNode.addLight(p);
            }
            if (s.getUserData(TEST_SUBJECT_ID) != null) {
                s.addControl(new RotateControl(2));
                var mat = material.clone();
                if (color != null) {
                    mat.setColor("Color", color);
                }
                mat.setFloat("ScanOffset", gen.nextFloat(0, 100));
                s.setMaterial(mat);
                s.setQueueBucket(RenderQueue.Bucket.Transparent);
            }
        }
        
        flyCam.setMoveSpeed(10);
        cam.setLocation(new Vector3f(15, 0, 0));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        
        var fpp = new FilterPostProcessor(assetManager);
        var bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        bloom.setBloomIntensity(5.0f);
        fpp.addFilter(bloom);
        viewPort.addProcessor(fpp);
        
        if (CAPTURE_VIDEO) {
            var output = new File(System.getProperty("user.home")+"/Pictures/hologram"+System.currentTimeMillis()+".avi");
            stateManager.attach(new VideoRecorderAppState(output));
        }
        
    }
    @Override
    public void simpleUpdate(float tpf) {}
    @Override
    public void simpleRender(RenderManager rm) {}
    
    @SuppressWarnings("null")
    private ColorRGBA getColorData(Spatial spatial, String name) {
        ArrayList<String> values = spatial.getUserData(name);
        if (values == null || values.size() < 3) {
            return null;
        }
        return new ColorRGBA(
            Float.parseFloat(values.get(0)),
            Float.parseFloat(values.get(1)),
            Float.parseFloat(values.get(2)),
            (values.size() >= 4 ? Float.parseFloat(values.get(3)) : 1)
        );
    }
    
    private class RotateControl extends AbstractControl {

        private final float speed;

        public RotateControl(float speed) {
            this.speed = speed;
        }
        
        @Override
        protected void controlUpdate(float tpf) {
            spatial.rotate(0, speed*tpf, 0);
        }
        @Override
        protected void controlRender(RenderManager rm, ViewPort vp) {}
        
    }
    
}
