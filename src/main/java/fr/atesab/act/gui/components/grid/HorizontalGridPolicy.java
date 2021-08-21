package fr.atesab.act.gui.components.grid;

import java.util.List;

import fr.atesab.act.gui.components.GuiComponent;
import fr.atesab.act.gui.components.GuiComponentContainer;
import fr.atesab.act.gui.components.GuiComponentLocation;

public class HorizontalGridPolicy implements GridPolicy {
    private GridAlignmentY.AlignFunction alignFunction;

    public HorizontalGridPolicy(GridAlignmentY alignment) {
        this.alignFunction = alignment.alignFunction;
    }

    @Override
    public void computeLocations(GuiComponentContainer container, List<GuiComponentLocation> components) {
        var globalHeight = components.stream().map(GuiComponentLocation::getComponent).mapToInt(GuiComponent::getHeight)
                .max().orElse(0);
        var x = 0;
        for (var l : components) {
            var c = l.getComponent();
            l.x = x;
            l.y = alignFunction.apply(globalHeight, c.getHeight());
            x += c.getWidth();
        }
    }

}
