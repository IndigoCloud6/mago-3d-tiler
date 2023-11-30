package com.gaia3d.process;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProcessOptions {
    HELP("help", "h", "help", false, "print this message"),
    VERSION("version", "v", "version", false, "print version"),
    QUIET("quiet", "q", "quiet", false, "quiet mode"),
    LOG("log", "l", "log", true, "output log file path"),
    INPUT("input", "i", "input", true, "input file path"),
    OUTPUT("output", "o", "output", true, "output file path"),
    INPUT_TYPE("inputType", "it", "inputType", true, "input file type (kml, 3ds, obj, gltf, etc...)"),
    OUTPUT_TYPE("outputType", "ot", "outputType", true, "output file type"),
    CRS("crs", "c", "crs", true,"Coordinate Reference Systems, only epsg code (4326, 3857, etc...)"),
    PROJ4("proj", "p", "proj", true, "proj4 parameters (ex: +proj=tmerc +la...)"),

    // 3D Options
    RECURSIVE("recursive", "r", "recursive", false, "deep directory exploration"),
    SWAP_YZ("swapYZ", "yz", "swapYZ", false, "swap vertices axis YZ"),
    REVERSE_TEXCOORD("reverseTexCoord", "rt", "reverseTexCoord", false, "texture y-axis coordinate reverse"),
    MULTI_THREAD("multiThread", "mt", "multiThread", false, "multi thread mode"),

    // 3D Tiles Options
    REFINE_ADD("refineAdd", "ra", "refineAdd", false, "refine addd mode"),
    MAX_COUNT("maxCount", "mx", "maxCount", true, "max count of nodes (Default: 256)"),
    MAX_LOD("maxLod", "xl", "maxLod", true, "max level of detail (Default: 3)"),
    MIN_LOD("minLod", "nl", "minLod", true, "min level of detail (Default: 0)"),
    MAX_POINTS("maxPoints", "mp", "maxPoints", true, "max points of node (Default: 20000)"),

    // 2D Options
    FLIP_COORDINATE("flipCoordinate", "fc", "flipCoordinate", false, "flip x,y Coordinate."),
    NAME_COLUMN("nameColumn", "nc", "nameColumn", true, "name column setting."),
    HEIGHT_COLUMN("heightColumn", "hc", "heightColumn", true, "height column setting."),
    ALTITUDE_COLUMN("altitudeColumn", "ac", "altitudeColumn", true, "altitude Column setting."),
    MINIMUM_HEIGHT("minimumHeight", "mh", "minimumHeight", true, "minimum height setting."),
    ABSOLUTE_ALTITUDE("absoluteAltitude", "aa", "absoluteAltitude", true, "absolute altitude mode."),

    //Experimental
    //AUTO_AXIS("autoAxis", "aa", "autoAxis", false, "[Experimental] auto axis"),
    //GENERATE_NORMALS("genNormals", "gn", "genNormals", false, "generate normals"),
    //SCALE("scale", "sc", "scale", false, "scale factor"),
    //STRICT("strict", "st", "strict", false, "strict mode"),

    DEBUG("debug", "d", "debug", false,"debug mode"),
    DEBUG_ALL_DRAWING("debugAllDrawing", "dad", "debugAllDrawing", false,"debug all drawing"),
    DEBUG_IGNORE_TEXTURES("debugIgnoreTextures", "dit", "debugIgnoreTextures", false,"debug ignore textures"),
    DEBUG_GLTF("gltf", "gltf", "gltf", false, "create gltf file."),
    DEBUG_GLB("glb", "glb", "glb", false, "create glb file.");

    private final String longName;
    private final String shortName;
    private final String argName;
    private final boolean argRequired;
    private final String description;
}
