package com.example.arcoretest

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_basic_model.*

class BasicModelActivity : AppCompatActivity() {

    private lateinit var arFragment: ArFragment
    private lateinit var selectedObject: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic_model)

        arFragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment

        btn_coke.setOnClickListener { selectedObject = Uri.parse("file:///android_asset/coke.sfb") }
        btn_pizza.setOnClickListener { selectedObject = Uri.parse("file:///android_asset/pizza.sfb") }
        btn_matheus.setOnClickListener { selectedObject = Uri.parse("file:///android_asset/matheus.sfb") }

        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            //Verifica a orientação do plano para garantir que o objeto não será criado na vertical
            if(plane.type == Plane.Type.HORIZONTAL_UPWARD_FACING){
                //Cria um objeto Anchor com as coordenadas do clique
                val anchor = hitResult.createAnchor()

                if (this::selectedObject.isInitialized){
                    placeObject(arFragment,anchor,selectedObject)
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
        ModelRenderable.builder()
            .setSource(arFragment.context, uri) //Passa o context do fragment e a uri do modelo
            .build()
            .thenAccept {renderable -> //Callback executado ao finalizar o carregamento do modelo. Retorna um ModelRenderable
                addNodeToScene(arFragment,anchor,renderable)
            }
            .exceptionally {throwable -> //Callback executado quando ocorrer um erro
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
