package com.example.arcoretest

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

class AugmentedImageActivity : AppCompatActivity() {

    private lateinit var arFragment: CustomARFragment
    private var modelAdded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_augmented_image)

        arFragment = supportFragmentManager.findFragmentById(R.id.custom_arfragment) as CustomARFragment
        //Desabilita a "mãozinha" quando abre o app e o plano pontilhado
        arFragment.planeDiscoveryController.hide()
        //Define um listener para disparar toda vez que mudar o frame da camera
        arFragment.arSceneView.scene.addOnUpdateListener(this::onUpdateFrame)

    }

    fun setupAugmentedImageDB(config: Config, session: Session): Boolean{
        val bitmap = loadAugmentedImage()

        if (bitmap != null){
            //Instancia o banco de imagends do ARCore
            val db = AugmentedImageDatabase(session)
            //Salva o bitmap da imagem que está nos assets
            db.addImage("tenis_ball",bitmap)
            //Seta o banco nas configurações da session
            config.augmentedImageDatabase = db
            return true
        }else{
            return false
        }

    }

    private fun loadAugmentedImage(): Bitmap?{
        //Retorna o bitmap da imagem nos assets
        return try {
            val iStream = assets.open("tenis_ball.jpg")
            BitmapFactory.decodeStream(iStream)
        }catch (t: Throwable){
            t.printStackTrace()
            null
        }
    }

    private fun onUpdateFrame(frameTime: FrameTime){
        //"Screenshot" do frame
        val frame = arFragment.arSceneView.arFrame
        //Retorna todas as imagens no banco
        val arImages = frame?.getUpdatedTrackables(AugmentedImage::class.java)

        //Compara todas as imagens com o frame
        arImages?.forEach {
            //Verifica se a imagem corresponde com o frame
            if (it.trackingState == TrackingState.TRACKING){
                //Verifica QUAL imagem corresponde com o frame
                if (it.name == "tenis_ball" && !modelAdded){
                    //Posiciona o modelo por cima
                    placeObject(arFragment, it.createAnchor(it.centerPose),Uri.parse("file:///android_asset/ball.sfb"))
                    //Flag para adicionar somente uma vez
                    modelAdded = true
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
