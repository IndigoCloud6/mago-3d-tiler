package com.gaia3d.release;

import com.gaia3d.command.mago.Mago3DTilerMain;
import org.junit.jupiter.api.Test;

import java.io.File;

public class StressTest {
    private static final String INPUT_PATH = "D:\\data\\mago-tiler-data\\stress-test-input";
    private static final String OUTPUT_PATH = "C:\\Workspaces\\GitSources\\mago-viewer\\data\\stress-test-output";

    @Test
    void testSouthKoreaShape() {
        String path = "SOUTH-KOREA-SHP";
        String[] args = new String[] {
                "-i", getInputPath(path).getAbsolutePath(),
                "-o", getOutputPath(path).getAbsolutePath(),
                "-it", "shp",
                "-crs", "5186",
                "-terrain", getInputPath(path).getAbsolutePath() + File.separator + "korea-compressed.tif",
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
                //"-radiusColumn", "STD_DIP",
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
