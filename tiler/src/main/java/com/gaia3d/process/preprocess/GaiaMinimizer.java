package com.gaia3d.process.preprocess;

import com.gaia3d.basic.exchangable.GaiaSet;
import com.gaia3d.basic.model.GaiaScene;
import com.gaia3d.process.tileprocess.tile.TileInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

@Slf4j
@AllArgsConstructor
public class GaiaMinimizer implements PreProcess {

    @Override
    public TileInfo run(TileInfo tileInfo) {
        GaiaScene scene = tileInfo.getScene();
        if (scene != null) {
            GaiaSet tempSet = GaiaSet.fromGaiaScene(scene);
            Path tempPath = tempSet.writeFile(tileInfo.getTempPath(), tileInfo.getSerial(), tempSet.getAttribute());
            tileInfo.setTempPath(tempPath);
            tempSet.clear();
            tempSet = null;
            scene.clear();
            scene = null;
        }
        return tileInfo;
    }
}
