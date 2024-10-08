package com.gaia3d.converter.kml.kml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.gaia3d.command.mago.Mago3DTilerMain;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class KmlRootTest {
    @Test
    void case01() {
        XmlMapper xmlMapper = new XmlMapper();
        String path = "D:\\Mago3DTiler-UnitTest\\input\\auto-created-i3dm\\sample-instances.kml";
        try {
             KmlRoot root = xmlMapper.readValue(new File(path), KmlRoot.class);
             log.info("{}", xmlMapper.writeValueAsString(root));
        } catch (IOException e) {
            log.error("Error : {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}