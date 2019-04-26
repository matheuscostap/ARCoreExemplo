package com.example.arcoretest

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_download.*

class DownloadActivity : AppCompatActivity() {

    private lateinit var arFragment: ArFragment
    private lateinit var selectedPoke: Uri
    //private val BULBASAUR_MODEL = "https://raw.githubusercontent.com/06wj/pokemon/master/models/001/glTF/model.gltf"
    private val BULBASAUR_MODEL = "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/BrainStem/glTF/BrainStem.gltf"
    private val CHARMANDER_MODEL = "https://raw.githubusercontent.com/06wj/pokemon/master/models/004/glTF/model.gltf"
    private val SQUIRTLE_MODEL = "https://raw.githubusercontent.com/06wj/pokemon/master/models/100/glTF/model.gltf"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        arFragment = supportFragmentManager.findFragmentById(R.id.download_fragment) as ArFragment

        btn_bulbasaur.setOnClickListener { selectedPoke = Uri.parse(BULBASAUR_MODEL) }
        btn_charmander.setOnClickListener { selectedPoke = Uri.parse(CHARMANDER_MODEL) }
        btn_squirtle.setOnClickListener { selectedPoke = Uri.parse(SQUIRTLE_MODEL) }

        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            if(plane.type == Plane.Type.HORIZONTAL_UPWARD_FACING){
                val anchor = hitResult.createAnchor()

                if (this::selectedPoke.isInitialized){
                    placeObject(arFragment,anchor,selectedPoke)
                }
            }
        }
    }

    /**
     * @param arFragment -> O fragment do ARCore
     * @param anchor -> Anchor é o objeto que possui as coordenadas 3D
     * @param uri -> A Uri para o modelo 3D
     */
    private fun placeObject(arFragment: ArFragment, anchor: Anchor, uri: Uri){
        //progress_download.visibility = View.VISIBLE
        ModelRenderable.builder()
            .setSource(this, RenderableSource.Builder().setSource(
                this,
                uri,
                RenderableSource.SourceType.GLTF2)
                .setScale(0.3f)
                .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                .build())
            .setRegistryId(uri.toString())
            .build()
            .thenAccept { renderable ->
                //progress_download.visibility = View.GONE
                addNodeToScene(arFragment,anchor,renderable)
            }
            .exceptionally { t ->
                //progress_download.visibility = View.GONE
                showErrorMessage()
            }
    }

    /**
     * @param renderable -> É a representação do objeto 3D
     */
    private fun addNodeToScene(arFragment: ArFragment, anchor: Anchor, renderable: Renderable){
        //AnchorNode fixa o objeto em uma posição e não pode ser movido ou receber interações
        val anchorNode = AnchorNode(anchor)
        //TransformableNode permite mover e interagir com o objeto
        val transformableNode = TransformableNode(arFragment.transformationSystem)
        //Define o objeto
        transformableNode.renderable = renderable
        //Define o anchornode como parent
        transformableNode.setParent(anchorNode)
        //Adiciona o anchornode junto com o transformable node no fragment
        arFragment.arSceneView.scene.addChild(anchorNode)
        //Seleciona o transformablenode para poder interagir
        transformableNode.select()
    }

    private fun showErrorMessage(): Void{
        AlertDialog.Builder(this)
            .setTitle("Erro")
            .setMessage("Erro ao carregar modelo")
            .setNeutralButton("Ok", null)
            .create()
            .show()

        val voidConstructor = Void::class.java.getDeclaredConstructor()
        voidConstructor.isAccessible = true
        val void = voidConstructor.newInstance()
        return void
    }
}
