package com.example.myapplication.ClassActivity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.ClassDB.MyGLRenderer;

public class MyGLActivity extends AppCompatActivity {
    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView
        glSurfaceView = new GLSurfaceView(this);

        // Create an OpenGL ES 2.0 context
        glSurfaceView.setEGLContextClientVersion(2);

        // Set the renderer to our custom renderer
        glSurfaceView.setRenderer(new MyGLRenderer());

        // Set the content view to the GLSurfaceView
        setContentView(glSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause(); // Pause the GLSurfaceView
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume(); // Resume the GLSurfaceView
    }
}
