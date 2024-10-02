package com.gaia3d.converter.geometry.shape;

import com.gaia3d.basic.geometry.GaiaBoundingBox;
import com.gaia3d.basic.geometry.tessellator.GaiaExtruder;
import com.gaia3d.basic.geometry.tessellator.GaiaExtrusionSurface;
import com.gaia3d.basic.geometry.tessellator.Vector3dOnlyHashEquals;
import com.gaia3d.basic.structure.*;
import com.gaia3d.command.mago.GlobalOptions;
import com.gaia3d.converter.Converter;
import com.gaia3d.converter.EasySceneCreator;
import com.gaia3d.converter.geometry.*;

import com.gaia3d.converter.geometry.pipe.GaiaPipeLineString;
import com.gaia3d.converter.geometry.pipe.PipeType;
import com.gaia3d.util.GlobeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.geotools.data.DataStore;
import org.geotools.data.Query;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.feature.FeatureIterator;

import org.joml.Matrix4d;
import org.joml.Vector3d;

import org.locationtech.jts.geom.*;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.ProjCoordinate;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.datum.GeodeticDatum;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
public class ShapeConverter extends AbstractGeometryConverter implements Converter {

    private final GlobalOptions globalOptions = GlobalOptions.getInstance();

    @Override
    public List<GaiaScene> load(String path) {
        return convert(new File(path));
    }

    @Override
    public List<GaiaScene> load(File file) {
        return convert(file);
    }

    @Override
    public List<GaiaScene> load(Path path) {
        return convert(path.toFile());
    }

    protected List<GaiaScene> convert(File file) {
        List<GaiaScene> scenes = new ArrayList<>();
        InnerRingRemover innerRingRemover = new InnerRingRemover();

        boolean flipCoordinate = globalOptions.isFlipCoordinate();
        String nameColumnName = globalOptions.getNameColumn();
        String heightColumnName = globalOptions.getHeightColumn();
        String altitudeColumnName = globalOptions.getAltitudeColumn();
        String diameterColumnName = globalOptions.getDiameterColumn();

        double absoluteAltitudeValue = globalOptions.getAbsoluteAltitude();
        double minimumHeightValue = globalOptions.getMinimumHeight();
        double skirtHeight = globalOptions.getSkirtHeight();

        ShpFiles shpFiles = null;
        ShapefileReader reader = null;
        try {
            shpFiles = new ShpFiles(file);
            reader = new ShapefileReader(shpFiles, true, true, new GeometryFactory());
            DataStore dataStore = new ShapefileDataStore(file.toURI().toURL());
            String typeName = dataStore.getTypeNames()[0];
            ContentFeatureSource source = (ContentFeatureSource) dataStore.getFeatureSource(typeName);
            var query = new Query(typeName, Filter.INCLUDE);
            //query.getHints().add(new Hints(Hints.FEATURE_2D, true));

            int totalCount = source.getCount(query);
            log.info(" - Total Shape Feature Count : {}", totalCount);

            SimpleFeatureCollection features = source.getFeatures(query);
            FeatureIterator<SimpleFeature> iterator = features.features();
            List<GaiaExtrusionBuilding> buildings = new ArrayList<>();
            List<GaiaPipeLineString> pipeLineStrings = new ArrayList<>();
            while (iterator.hasNext()) {

                SimpleFeature feature = iterator.next();
                Geometry geom = (Geometry) feature.getDefaultGeometry();

                if (geom == null) {
                    log.debug("Is Null Geometry : {}", feature.getID());
                    continue;
                }

                List<Polygon> polygons = new ArrayList<>();
                List<LineString> LineStrings = new ArrayList<>();
                if (geom instanceof MultiPolygon) {
                    int count = geom.getNumGeometries();
                    for (int i = 0; i < count; i++) {
                        Polygon polygon = (Polygon) geom.getGeometryN(i);
                        polygons.add(polygon);
                    }
                } else if (geom instanceof Polygon) {
                    polygons.add((Polygon) geom);
                } else if (geom instanceof LineString) {
                    LineStrings.add((LineString) geom);
                } else if (geom instanceof MultiLineString) {
                    int count = geom.getNumGeometries();
                    for (int i = 0; i < count; i++) {
                        LineString lineString = (LineString) geom.getGeometryN(i);
                        LineStrings.add(lineString);
                    }
                } else {
                    log.debug("Is Not Supported Geometry Type : {}", geom.getGeometryType());
                    continue;
                }

                Map<String, String> attributes = new HashMap<>();
                FeatureType featureType = feature.getFeatureType();
                Collection<PropertyDescriptor> featureDescriptors = featureType.getDescriptors();
                AtomicInteger index = new AtomicInteger(0);
                featureDescriptors.forEach(attributeDescriptor -> {
                    Object attribute = feature.getAttribute(index.getAndIncrement());
                    if (attribute instanceof Geometry) {
                        return;
                    }
                    String attributeString = castStringFromObject(attribute, "null");
                    attributes.put(attributeDescriptor.getName().getLocalPart(), attributeString);
                });

                for (LineString lineString : LineStrings) {
                    Coordinate[] coordinates = lineString.getCoordinates();
                    List<Vector3d> positions = new ArrayList<>();
                    if(coordinates.length < 2) {
                        log.warn("Invalid LineString : {}", feature.getID());
                        continue;
                    }
                    for (Coordinate coordinate : coordinates) {
                        Point point = new GeometryFactory().createPoint(coordinate);
                        double x, y, z;
                        if (flipCoordinate) {
                            x = point.getY();
                            y = point.getX();
                        } else {
                            x = point.getX();
                            y = point.getY();
                        }

                        z = point.getCoordinate().getZ();

                        Vector3d position = new Vector3d(x, y, z); // usually crs 3857.***
                        positions.add(position);
                    }

                    double diameter = getDiameter(feature, diameterColumnName);

                    GaiaPipeLineString pipeLineString = GaiaPipeLineString.builder()
                            .id(feature.getID())
                            .profileType(PipeType.CIRCULAR)
                            .diameter(diameter)
                            .properties(attributes)
                            .positions(positions).build();
                    pipeLineString.setOriginalFilePath(file.getPath());
                    pipeLineStrings.add(pipeLineString);
                }

                for (Polygon polygon : polygons) {
                    if (!polygon.isValid()) {
                        log.warn("{} Is Invalid Polygon.", feature.getID());
                        continue;
                    }

                    LineString lineString = polygon.getExteriorRing();
                    Coordinate[] outerCoordinates = lineString.getCoordinates();

                    int innerRingCount = polygon.getNumInteriorRing();
                    List<Coordinate[]> innerCoordinates = new ArrayList<>();
                    for (int i = 0; i < innerRingCount; i++) {
                        LineString innerRing = polygon.getInteriorRingN(i);
                        Coordinate[] innerCoordinatesArray = innerRing.getCoordinates();
                        innerCoordinates.add(innerCoordinatesArray);
                    }
                    if (innerRingCount > 0) {
                        outerCoordinates = innerRingRemover.removeAll(outerCoordinates, innerCoordinates);
                    }

                    GaiaBoundingBox boundingBox = new GaiaBoundingBox();
                    List<Vector3d> positions = new ArrayList<>();

                    for (Coordinate coordinate : outerCoordinates) {
                        double x, y, z;
                        if (flipCoordinate) {
                            x = coordinate.getY();
                            y = coordinate.getX();
                        } else {
                            x = coordinate.getX();
                            y = coordinate.getY();
                        }
                        z = coordinate.getZ();

                        Vector3d position;
                        CoordinateReferenceSystem crs = globalOptions.getCrs();
                        if (crs != null && !crs.getName().equals("EPSG:4326")) {
                            ProjCoordinate projCoordinate = new ProjCoordinate(x, y, boundingBox.getMinZ());
                            ProjCoordinate centerWgs84 = GlobeUtils.transform(crs, projCoordinate);
                            position = new Vector3d(centerWgs84.x, centerWgs84.y, 0.0d);
                        } else {
                            position = new Vector3d(x, y, 0.0d);
                        }

                        positions.add(position);
                        boundingBox.addPoint(position);
                    }

                    String name = getAttributeValueOfDefault(feature, nameColumnName, "Extrusion-Building");
                    if (positions.size() >= 3) {
                        double height = getHeight(feature, heightColumnName, minimumHeightValue);
                        double altitude = absoluteAltitudeValue;
                        if (altitudeColumnName != null) {
                            altitude = getAltitude(feature, altitudeColumnName);
                        }
                        GaiaExtrusionBuilding building = GaiaExtrusionBuilding.builder()
                                .id(feature.getID())
                                .name(name)
                                .boundingBox(boundingBox)
                                .floorHeight(altitude)
                                .roofHeight(height + skirtHeight)
                                .positions(positions)
                                .originalFilePath(file.getPath())
                                .properties(attributes)
                                .build();
                        buildings.add(building);
                    } else {
                        log.warn("Invalid Geometry : {}, {}", feature.getID(), name);
                    }
                }
            }
            iterator.close();
            reader.close();
            shpFiles.dispose();
            dataStore.dispose();

            convertPipeLineStrings(pipeLineStrings, scenes, file);
            convertExtrusionBuildings(buildings, scenes, file);
        } catch (IOException e) {
            log.error("Error while reading shapefile", e);
            throw new RuntimeException(e);
        }
        shpFiles.dispose();
        return scenes;
    }
    //convertPipeLineStrings(pipeLineStrings, scenes);
    private void convertExtrusionBuildings(List<GaiaExtrusionBuilding> buildings, List<GaiaScene> resultScenes, File file) {
        double skirtHeight = globalOptions.getSkirtHeight();
        GaiaExtruder gaiaExtruder = new GaiaExtruder();

        EasySceneCreator easySceneCreator = new EasySceneCreator();
        for (GaiaExtrusionBuilding building : buildings) {
            GaiaScene scene = easySceneCreator.createScene(file);

            GaiaNode rootNode = scene.getNodes().get(0);
            rootNode.setName(building.getName());

            GaiaAttribute gaiaAttribute = scene.getAttribute();
            gaiaAttribute.setAttributes(building.getProperties());
            Map<String, String> attributes = gaiaAttribute.getAttributes();
            gaiaAttribute.setNodeName(rootNode.getName());
            attributes.put("name", building.getName());

            Vector3d center = building.getBoundingBox().getCenter();
            center.z = center.z - skirtHeight;

            Vector3d centerWorldCoordinate = GlobeUtils.geographicToCartesianWgs84(center);
            Matrix4d transformMatrix = GlobeUtils.transformMatrixAtCartesianPointWgs84(centerWorldCoordinate);
            Matrix4d transformMatrixInv = new Matrix4d(transformMatrix).invert();

            List<Vector3d> localPositions = new ArrayList<>();
            for (Vector3d position : building.getPositions()) {
                Vector3d positionWorldCoordinate = GlobeUtils.geographicToCartesianWgs84(position);
                Vector3d localPosition = positionWorldCoordinate.mulPosition(transformMatrixInv);
                localPosition.z = 0.0d;
                localPositions.add(new Vector3dOnlyHashEquals(localPosition));
            }
            Collections.reverse(localPositions);
            localPositions.remove(localPositions.size() - 1);

            List<GaiaExtrusionSurface> extrusionSurfaces = gaiaExtruder.extrude(localPositions, building.getRoofHeight(), building.getFloorHeight());

            GaiaNode node = new GaiaNode();
            node.setTransformMatrix(new Matrix4d().identity());
            GaiaMesh mesh = new GaiaMesh();
            node.getMeshes().add(mesh);

            GaiaPrimitive primitive = createPrimitiveFromGaiaExtrusionSurfaces(extrusionSurfaces);

            primitive.setMaterialIndex(0);
            mesh.getPrimitives().add(primitive);

            rootNode.getChildren().add(node);

            Matrix4d rootTransformMatrix = new Matrix4d().identity();
            rootTransformMatrix.translate(center, rootTransformMatrix);
            rootNode.setTransformMatrix(rootTransformMatrix);
            resultScenes.add(scene);
        }
    }

    private void convertPipeLineStrings(List<GaiaPipeLineString> pipeLineStrings, List<GaiaScene> resultScenes, File file) {
        if (pipeLineStrings.isEmpty()) {
            return;
        }

        GlobalOptions globalOptions = GlobalOptions.getInstance();
        for (GaiaPipeLineString pipeLineString : pipeLineStrings) {
            int pointsCount = pipeLineString.getPositions().size();
            pipeLineString.setBoundingBox(new GaiaBoundingBox());
            GaiaBoundingBox bbox = pipeLineString.getBoundingBox();
            bbox.setInit(false);
            for (int j = 0; j < pointsCount; j++) {
                Vector3d point = pipeLineString.getPositions().get(j);
                //Vector3d position = new Vector3d(x, y, z);
                CoordinateReferenceSystem crs = globalOptions.getCrs();
                if (crs != null && !crs.getName().equals("EPSG:4326")) {
                    ProjCoordinate projCoordinate = new ProjCoordinate(point.x, point.y, point.z);
                    ProjCoordinate centerWgs84 = GlobeUtils.transform(crs, projCoordinate);

                    double defaultHeight = 2.0;
                    double heightOffset = 0.0;

                    if (pipeLineString.getProfileType() == PipeType.CIRCULAR) {
                        heightOffset = pipeLineString.getDiameter() / 1000 / 2;
                    } else if (pipeLineString.getProfileType() == PipeType.RECTANGULAR) {
                        heightOffset = pipeLineString.getRectangularSize()[1] / 1000 / 2;
                    }

                    point.set(centerWgs84.x, centerWgs84.y, point.z - heightOffset - defaultHeight);
                    bbox.addPoint(point);
                }
            }
        }

        EasySceneCreator easySceneCreator = new EasySceneCreator();
        for (GaiaPipeLineString pipeLineString : pipeLineStrings) {
            int pointsCount = pipeLineString.getPositions().size();
            if(pointsCount < 2) {
                log.warn("Invalid PipeLineString : {}", pipeLineString.getId());
                continue;
            }

            GaiaScene scene = easySceneCreator.createScene(file);
            GaiaNode rootNode = scene.getNodes().get(0);
            rootNode.setName("PipeLineStrings");

            GaiaAttribute gaiaAttribute = scene.getAttribute();
            gaiaAttribute.setAttributes(pipeLineString.getProperties());
            Map<String, String> attributes = gaiaAttribute.getAttributes();
            gaiaAttribute.setNodeName(rootNode.getName());
            attributes.put("name", pipeLineString.getName());

            GaiaBoundingBox boundingBox = pipeLineString.getBoundingBox();
            Vector3d bboxCenter = boundingBox.getCenter();

            Vector3d centerWorldCoordinate = GlobeUtils.geographicToCartesianWgs84(bboxCenter);
            Matrix4d transformMatrix = GlobeUtils.transformMatrixAtCartesianPointWgs84(centerWorldCoordinate);
            Matrix4d transformMatrixInv = new Matrix4d(transformMatrix).invert();

            List<Vector3d> localPositions = new ArrayList<>();
            for (Vector3d position : pipeLineString.getPositions()) {
                Vector3d positionWorldCoordinate = GlobeUtils.geographicToCartesianWgs84(position);
                Vector3d localPosition = positionWorldCoordinate.mulPosition(transformMatrixInv);
                localPositions.add(new Vector3dOnlyHashEquals(localPosition));
            }

            // set the positions in the pipeLineString.***
            pipeLineString.setPositions(localPositions);

            //pipeLineString.TEST_Check();
            if(localPositions.size() > 2) {
                pipeLineString.deleteDuplicatedPoints();
            }

            // once deleted duplicatedPoints, check pointsCount again.***
            pointsCount = pipeLineString.getPositions().size();
            if(pointsCount < 2) {
                log.warn("Invalid PipeLineString POINTS COUNT LESS THAN 2: {}", pipeLineString.getId());
                continue;
            }

            GaiaNode node = createPrimitiveFromPipeLineString(pipeLineString);
            if(node == null) {
                log.warn("Invalid PipeLineString NULL NODE: {}", pipeLineString.getId());
                continue;
            }
            node.setName(pipeLineString.getName());
            node.setTransformMatrix(new Matrix4d().identity());

            // for all primitives set the material index.***
            for (GaiaMesh mesh : node.getMeshes()) {
                for (GaiaPrimitive primitive : mesh.getPrimitives()) {
                    primitive.setMaterialIndex(0);
                }
            }

            rootNode.getChildren().add(node);
            Matrix4d rootTransformMatrix = new Matrix4d().identity();
            rootTransformMatrix.translate(bboxCenter, rootTransformMatrix);
            rootNode.setTransformMatrix(rootTransformMatrix);
            resultScenes.add(scene);
        }

    }

}
