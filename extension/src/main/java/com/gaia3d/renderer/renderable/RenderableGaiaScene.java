package com.gaia3d.renderer.renderable;

import com.gaia3d.basic.model.GaiaMaterial;
import com.gaia3d.basic.model.GaiaScene;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RenderableGaiaScene {
    private GaiaScene originalGaiaScene;
    private Path originalPath;
    List<RenderableNode> renderableNodes;
    private List<GaiaMaterial> materials = new ArrayList<>();
    public RenderableGaiaScene() {
        renderableNodes = new ArrayList<>();
        originalGaiaScene = null;
    }

    public void addRenderableNode(RenderableNode renderableNode) {
        renderableNodes.add(renderableNode);
    }

    public void extractRenderablePrimitives(List<RenderablePrimitive> resultRenderablePrimitives) {
        for (RenderableNode renderableNode : renderableNodes) {
            renderableNode.extractRenderablePrimitives(resultRenderablePrimitives);
        }
    }
}
