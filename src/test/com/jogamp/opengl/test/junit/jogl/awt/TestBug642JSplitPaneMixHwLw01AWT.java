package com.jogamp.opengl.test.junit.jogl.awt;

import java.awt.BorderLayout;
// import java.awt.Canvas;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.IOException;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import com.jogamp.common.util.ReflectionUtil;
import com.jogamp.opengl.test.junit.jogl.demos.es2.GearsES2;
import com.jogamp.opengl.test.junit.util.MiscUtils;
import com.jogamp.opengl.test.junit.util.UITestCase;
import com.jogamp.opengl.util.Animator;

/**
 * Documenting Bug 642 (related to Bug 586)
 * 
 * <p>
 * JSplitPane cannot mix hw/lw components, only if setting property '-Dsun.awt.disableMixing=true'.
 * </p>
 * See Bug 586
 * See git commit '8df12ca151dfc577c90b485d4ebfe491b88e55aa'.
 */
public class TestBug642JSplitPaneMixHwLw01AWT extends UITestCase {
    static long durationPerTest = 500;
    
    static {
        // too late: use at cmd-line '-Dsun.awt.disableMixing=true' works
        // System.setProperty("sun.awt.disableMixing", "true");
    }

    /**
     * Doesn't work either .. 
     */
    @SuppressWarnings("serial")
    public static class TransparentJScrollPane extends JScrollPane {

        public TransparentJScrollPane(Component view) {
            super(view);
    
            setOpaque(false);
       
            try {
                ReflectionUtil.callStaticMethod(
                                            "com.sun.awt.AWTUtilities", "setComponentMixingCutoutShape", 
                                            new Class<?>[] { Component.class, Shape.class }, 
                                            new Object[] { this, new Rectangle() } , 
                                            GraphicsConfiguration.class.getClassLoader());
                System.err.println("com.sun.awt.AWTUtilities.setComponentMixingCutoutShape(..) passed");
            } catch (RuntimeException re) {
                System.err.println("com.sun.awt.AWTUtilities.setComponentMixingCutoutShape(..) failed: "+re.getMessage());
            }
        }
    
        @Override
        public void setOpaque(boolean isOpaque) {
        }
    }
    
    protected void runTestGL(GLCapabilities caps, boolean useGLJPanel) throws InterruptedException {
        final String typeS = useGLJPanel ? "LW" : "HW";
        final JFrame frame = new JFrame("Mix Hw/Lw Swing - Canvas "+typeS);
        Assert.assertNotNull(frame);
        
        final Dimension f_sz = new Dimension(824,568);
        // final Dimension f_sz = new Dimension(600,400);
        // final Dimension glc_sz = new Dimension(500,600);
        
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        final Component glComp;
        final GLAutoDrawable glad;
        if(useGLJPanel) {
            final GLJPanel glJPanel = new GLJPanel(new GLCapabilities(GLProfile.getDefault()));
            Assert.assertNotNull(glJPanel);        
            glJPanel.addGLEventListener(new GearsES2());
            glComp = glJPanel;
            glad = glJPanel;
        } else {
            final GLCanvas glCanvas = new GLCanvas(new GLCapabilities(GLProfile.getDefault()));
            Assert.assertNotNull(glCanvas);        
            glCanvas.addGLEventListener(new GearsES2());
            glComp = glCanvas;
            glad = glCanvas;
        }
        
        final Container contentPane = frame.getContentPane();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5d);
        splitPane.setLeftComponent(glComp);
        // splitPane.setLeftComponent(new JPanel());
        // splitPane.setLeftComponent(new Canvas());
        splitPane.setRightComponent(new JPanel());
        contentPane.add(splitPane, BorderLayout.CENTER);            

        Animator animator = new Animator(glad);
        animator.start();
        
        try {
            javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    frame.setPreferredSize(f_sz);
                    frame.setSize(f_sz.width+1, f_sz.height+1); // trick to force pack() to work!
                    frame.pack();
                    frame.setVisible(true);
                    // however, Hw/Lw mixing is still a problem ..
                }});
        } catch (Throwable t) {
            t.printStackTrace();
            Assume.assumeNoException(t);
        }

        animator.setUpdateFPSFrames(60, System.err);
        Thread.sleep(durationPerTest);

        animator.stop();
        
        try {
            javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    frame.setVisible(false);
                    frame.dispose();
                }});
        } catch (Throwable t) {
            t.printStackTrace();
            Assume.assumeNoException(t);
        }
    }

    @Test
    public void test01JSplitPaneWithHwGLCanvas() throws InterruptedException {
        GLProfile glp = GLProfile.getGL2ES2();
        GLCapabilities caps = new GLCapabilities(glp);
        runTestGL(caps, false);
    }

    @Test
    public void test01JSplitPaneWithLwGLJPanel() throws InterruptedException {
        GLProfile glp = GLProfile.getGL2ES2();
        GLCapabilities caps = new GLCapabilities(glp);
        runTestGL(caps, true);
    }
    
    public static void main(String args[]) throws IOException {
        for(int i=0; i<args.length; i++) {
            if(args[i].equals("-time")) {
                durationPerTest = MiscUtils.atol(args[++i], durationPerTest);
            }
        }
        /**
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        System.err.println("Press enter to continue");
        System.err.println(stdin.readLine()); 
        */
        System.out.println("durationPerTest: "+durationPerTest);
        String tstname = TestBug642JSplitPaneMixHwLw01AWT.class.getName();
        org.junit.runner.JUnitCore.main(tstname);
    }
    
}