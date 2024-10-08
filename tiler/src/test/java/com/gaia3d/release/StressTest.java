package com.gaia3d.release;

import com.gaia3d.command.mago.Mago3DTilerMain;
import org.junit.jupiter.api.Test;

import java.io.File;

public class StressTest {
    private static final String INPUT_PATH = "D:\\data\\mago-tiler-data\\stress-test-input";
    private static final String OUTPUT_PATH = "C:\\Workspaces\\GitSources\\mago-viewer\\data\\stress-test-output";


    //
    @Test
    void testIfcMep() {
        String path = "LARGE-MEP-IFC";
        String[] args = new String[] {
                "-i", getInputPath(path).getAbsolutePath(),
                "-o", getOutputPath(path).getAbsolutePath(),
                "-it", "ifc",
                //"-crs", "5186",
        };
        Mago3DTilerMain.main(args);
    }

    @Test
    void testYeouidoShape() {
        String path = "YEOUIDO-SHP";
        String[] args = new String[] {
                "-i", getInputPath(path).getAbsolutePath(),
                "-o", getOutputPath(path).getAbsolutePath(),
                "-it", "shp",
                "-crs", "5186",
                //"-terrain", getInputPath(path).getAbsolutePath() + File.separator + "korea-compressed.tif",
                "-debug"
        };
        Mago3DTilerMain.main(args);
    }

    @Test
    void testSeoulShape() {
        String path = "SEOUL-SHP";
        String[] args = new String[] {
                "-i", getInputPath(path).getAbsolutePath(),
                "-o", getOutputPath(path).getAbsolutePath(),
                "-it", "shp",
                "-crs", "5186",
                //"-terrain", getInputPath(path).getAbsolutePath() + File.separator + "korea-compressed.tif",
                "-debug"
        };
        Mago3DTilerMain.main(args);
    }

    @Test
    void testSouthKoreaShape() {
        String path = "SOUTH-KOREA-SHP";
        String[] args = new String[] {
                "-i", getInputPath(path).getAbsolutePath(),
                "-o", getOutputPath(path).getAbsolutePath(),
                "-it", "shp",
                "-crs", "5186",
                "-terrain", getInputPath(path).getAbsolutePath() + File.separator + "korea-compressed.tif",
                "-debug"
        };
        Mago3DTilerMain.main(args);
    }

    @Test
    void testSejongUndergroundShape() {
        String path = "SEJONG-UNDERGROUND-SHP";
        String[] args = new String[] {
                "-i", getInputPath(path).getAbsolutePath(),
                "-o", getOutputPath(path).getAbsolutePath(),
                "-it", "shp",
                "-crs", "5186",
                "-dc", "STD_DIP",
        };
        Mago3DTilerMain.main(args);
    }

    @Test
    void testLargeBuildingFbxFromHayashiSang() {
        String path = "LARGE-BUILDING-FBX";
        String[] args = new String[] {
                "-i", getInputPath(path).getAbsolutePath(),
                "-o", getOutputPath(path).getAbsolutePath(),
                //"-it", "fbx",
                "-crs", "6674",
                "-minLod", "0",
                "-maxLod", "0",
                //"-refineAdd",
                "-swapUpAxis",
                //"-reverseUpAxis",
                //"-rotateUpAxis",
                //"-glb",
                //"-largeMesh",
                "-debug"
        };
        Mago3DTilerMain.main(args);
    }

    @Test
    void testSangJiUni() {
        String path = "SANGJI-UNI-DAE";
        String[] args = new String[] {
                "-i", getInputPath(path).getAbsolutePath(),
                "-o", getOutputPath(path).getAbsolutePath(),
                //"-it", "dae",
                //"-crs", "5186",
                "-minLod", "0",
                "-maxLod", "0",
                //"-autoUpAxis",
                // "-rotateUpAxis",
                "-refineAdd",
                //"-glb",
                //"-recursive",
                "-debug"
        };
        Mago3DTilerMain.main(args);
    }

    @Test
    void testSaehanCollada() {
        String path = "SEAHAN-DAE";
        String[] args = new String[] {
                "-i", getInputPath(path).getAbsolutePath(),
                "-o", getOutputPath(path).getAbsolutePath(),
                "-it", "kml",
                "-crs", "5186",
                "-debug"
        };
        Mago3DTilerMain.main(args);
    }

    private File getInputPath(String path) {
        return new File(INPUT_PATH, path);
    }

    private File getOutputPath(String path) {
        return new File(OUTPUT_PATH, path);
    }
}
