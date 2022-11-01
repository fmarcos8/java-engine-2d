package core;

public abstract class Component {
    public GameObject parent = null;

    public void start() {

    }

    public abstract void update(float dt);

}
