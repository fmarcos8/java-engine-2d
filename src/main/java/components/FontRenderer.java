package components;

import core.Component;

public class FontRenderer extends Component {

    @Override
    public void start() {
        if (parent.getComponent(SpriteRenderer.class) != null) {
            System.out.println("Found Font Renderer");
        }
    }

    @Override
    public void update(float dt) {

    }
}
