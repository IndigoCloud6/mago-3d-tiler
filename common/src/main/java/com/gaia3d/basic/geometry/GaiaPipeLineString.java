package com.gaia3d.basic.geometry;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3d;

import java.util.List;

@Builder
@Getter
@Setter
public class GaiaPipeLineString {
    private String id;
    private String name;
    private double diameterCm;
    private float[] pipeRectangularSize; // for rectangular pipe.
    private GaiaBoundingBox boundingBox;
    private int pipeProfileType; // 0 = unknown, 1 = circular, 2 = rectangular, 3 = oval, 4 = irregular, etc.
    private String originalFilePath;
    private List<Vector3d> positions;

    public boolean isSameProfile(GaiaPipeLineString pipeLineString) {
        if (pipeProfileType != pipeLineString.pipeProfileType) {
            return false;
        }
        if (pipeProfileType == 1) {
            return diameterCm == pipeLineString.diameterCm;
        } else if (pipeProfileType == 2) {
            return pipeRectangularSize[0] == pipeLineString.pipeRectangularSize[0] && pipeRectangularSize[1] == pipeLineString.pipeRectangularSize[1];
        }
        return true;
    }

    public void pushFrontPoints(List<Vector3d> points) {
        points.addAll(positions);
        positions.clear();
        positions.addAll(points);
    }

    public void pushBackPoints(List<Vector3d> points) {
        positions.addAll(points);
    }

    public boolean deleteDuplicatedPoints() {
        // search for adjacent points with same positions.
        boolean deleted = false;
        for (int i = 0; i < positions.size() - 1; i++) {
            Vector3d point = positions.get(i);
            Vector3d point2 = positions.get(i + 1);
            double error = point.distance(point2);
            if (error < 0.01) {
                if (i > 0) {
                    positions.remove(i);
                } else {
                    positions.remove(i + 1);
                }
                i--;
                deleted = true;
            }
        }
        return deleted;
    }

    public boolean intersects(GaiaPipeLineString pipeLine, double tolerance) {
        if (this.boundingBox == null) {
            this.calculateBoundingBox();
        }
        if (pipeLine.boundingBox == null) {
            pipeLine.calculateBoundingBox();
        }

        return this.boundingBox.intersects(pipeLine.boundingBox, tolerance);
    }

    public void calculateBoundingBox() {
        if (boundingBox == null) {
            boundingBox = new GaiaBoundingBox();
        }
        for (int i = 0; i < positions.size(); i++) {
            Vector3d point = positions.get(i);
            if (i == 0) {
                boundingBox.setMinX(point.x);
                boundingBox.setMinY(point.y);
                boundingBox.setMinZ(point.z);
                boundingBox.setMaxX(point.x);
                boundingBox.setMaxY(point.y);
                boundingBox.setMaxZ(point.z);
            } else {
                boundingBox.addPoint(point);
            }
        }
    }
}
