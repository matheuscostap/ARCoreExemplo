package com.example.arcoretest

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.ar.core.Anchor
import com.google.ar.core.AugmentedFace
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.AugmentedFaceNode
import com.google.ar.sceneform.ux.TransformableNode

class FaceActivity : AppCompatActivity(), Scene.OnUpdateListener {

    private lateinit var arFragment: ArFragment
    private val faceNodeMap = HashMap<AugmentedFace, AugmentedFaceNode>()
    private var meshAttached = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face)

        arFragment = supportFragmentManager.findFragmentById(R.id.face_fragment) as FaceARFragment
        //Desabilita a "mãozinha" quando abre o app e o plano pontilhado
        arFragment.planeDiscoveryController.hide()
        //Define um listener para disparar toda vez que mudar o frame da camera
        arFragment.arSceneView.scene.addOnUpdateListener(this)
    }

    override fun onUpdate(frameTime: FrameTime) {
        if (!meshAttached){
            loadMesh(arFragment)
        }
    }

    private fun loadMesh(arFragment: ArFragment){
        ModelRenderable.builder()
            .setSource(this, Uri.parse("file:///android_asset/oculos.sfb"))
            .build()
            .thenAccept { renderable ->
                renderable.isShadowCaster = false
                renderable.isShadowReceiver = false
                addMesh(arFragment,renderable)
            }
    }

    private fun addMesh(arFragment: ArFragment,renderable: Renderable){
        val faceList = arFragment.arSceneView.session?.getAllTrackables(AugmentedFace::class.java)

        if (faceList != null) {
            for (face in faceList){
                if (!faceNodeMap.containsKey(face)){
                    val faceNode = AugmentedFaceNode(face)
                    faceNode.setParent(arFragment.arSceneView.scene)
                    faceNode.faceRegionsRenderable = renderable as ModelRenderable
                    faceNodeMap.put(face,faceNode)
                    meshAttached = true
                }
            }
        }
    }
}
