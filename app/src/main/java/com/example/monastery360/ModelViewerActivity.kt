package com.example.monastery360

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.net.Uri
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.ArSceneView

class ModelViewerActivity : AppCompatActivity() {

    private lateinit var arSceneView: ArSceneView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model_viewer)

        arSceneView = findViewById(R.id.arSceneView)

        loadModel()
    }

    private fun loadModel() {
        ModelRenderable.builder()
            .setSource(this, R.raw.rumtek_monastery_3d_model)
            .build()
            .thenAccept { renderable ->
                val node = Node()
                node.renderable = renderable
                arSceneView.scene.addChild(node)
                println("Rumtek Monastery model loaded!")
            }
            .exceptionally { throwable ->
                throwable.printStackTrace()
                println("Error loading model: ${throwable.message}")
                null
            }
    }

    override fun onResume() {
        super.onResume()
        arSceneView.resume()
    }

    override fun onPause() {
        arSceneView.pause()
        super.onPause()
    }

    override fun onDestroy() {
        arSceneView.destroy()
        super.onDestroy()
    }
}