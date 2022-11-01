package Scenes;

import components.FontRenderer;
import components.SpriteRenderer;
import core.Camera;
import core.GameObject;
import core.Scene;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;
import renderer.Texture;
import utils.Time;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {
    private float[] vertexArray = {
        //Position                //Color                       //UV Coordinates
        100.0f,   0.0f, 0.0f,       1.0f, 0.0f, 0.0f, 1.0f,     1, 1, //Bottom Right 0
          0.0f, 100.0f, 0.0f,       0.0f, 1.0f, 0.0f, 1.0f,     0, 0, //Top Left     1
        100.0f, 100.0f, 0.0f,       1.0f, 0.0f, 1.0f, 1.0f,     1, 0, //Top Right    2
          0.0f,   0.0f, 0.0f,       1.0f, 1.0f, 0.0f, 1.0f,     0, 1  //Bottom Left  3
    };

    //IMPORTANT: Must be in counter-clockwise order
    private int[] elementArray = {
        2, 1, 0, //Top right triangle
        0, 1, 3  //Bottom left triangle
    };

    private int vaoID, vboID, eboID;

    private Shader shader;
    private Texture texture;
    private GameObject obj;

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        this.obj = new GameObject("Objeto de Teste");
        this.obj.addComponent(new SpriteRenderer());
        this.obj.addComponent(new FontRenderer());
        this.addGameObjectToScene(this.obj);

        this.camera = new Camera(new Vector2f(-200, -300));
        shader = new Shader("assets/shaders/default.glsl");
        shader.compile();
        texture = new Texture("assets/images/testImage.png");

        //Generate VAO, VBO, and EBO buffer objects, and send to GPU
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //Create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        //Create VBO upload the vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        //Create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        //Add the vertex attribute pointers
        int positionSize = 3;
        int colorSize = 4;
        int uvSize = 2;
        int vertexSizeBytes = (positionSize + colorSize + uvSize) * Float.BYTES;
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionSize + colorSize) * Float.BYTES);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update(float dt) {
        shader.use();

        //Upload texture to shader
        shader.uploadTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        texture.bind();

        shader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        shader.uploadMat4f("uView", camera.getViewMatrix());
        shader.uploadFloat("uTime", Time.getTime());

        //Bind he VAO that we're using
        glBindVertexArray(vaoID);

        //Enable the vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);
        shader.detach();

        for (GameObject go: this.gameObjects) {
            go.update(dt);
        }
    }
}
