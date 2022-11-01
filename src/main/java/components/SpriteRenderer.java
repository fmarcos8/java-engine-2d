package components;

import core.Component;

public class SpriteRenderer extends Component {

    private boolean firstTime = false;

//    public SpriteRenderer() {
//
//    }

    @Override
    public void start() {
        System.out.println("Eu estou Iniciando");
    }

    @Override
    public void update(float dt) {
        if (!firstTime) {
            System.out.println("Eu estou atualizando");
            firstTime = true;
        }
    }
}
