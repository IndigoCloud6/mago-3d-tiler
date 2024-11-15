package com.gaia3d.converter.geometry;

import com.gaia3d.basic.types.FormatType;
import com.gaia3d.command.mago.GlobalOptions;
import com.gaia3d.converter.Converter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class ExtrusionTempGenerator {
    private final Converter converter;
    public List<File> generate(File tempPath, List<File> fileList) {
        GlobalOptions options = GlobalOptions.getInstance();
        List<GaiaSceneTempHolder> sceneList = new ArrayList<>();
        for (File file : fileList) {
            List<GaiaSceneTempHolder> tempList = converter.convertTemp(file, tempPath);
            sceneList.addAll(tempList);
        }

        FormatType formatType = options.getInputFormat();
        if (formatType.equals(FormatType.GEOJSON) || formatType.equals(FormatType.SHP)) {
            return sceneList.stream().map(GaiaSceneTempHolder::getTempFile).collect(Collectors.toList());
        } else {
            return fileList;
        }
    }
}