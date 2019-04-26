package com.example.arcoretest

import android.util.Log
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment

class CustomARFragment: ArFragment(){

    /**
     * @param session -> Controla o lifecycle da realidade aumentada
     */
    override fun getSessionConfiguration(session: Session): Config {
        //Desabilita a "mãozinha" quando abre o app e o plano pontilhado
        planeDiscoveryController.setInstructionView(null)

        //Configura a session
        val config = Config(session)
        config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
        //Aplica as configurações
        session.configure(config)

        //Coloca a session em um cena
        arSceneView.setupSession(session)

        //Chama o método da activity para criar o banco de imagens
        val actv = activity as AugmentedImageActivity
        if (actv.setupAugmentedImageDB(config,session)){
            Log.i("CustomARFragment", "Banco de imagens criado!")
        }else{
            Log.e("CustomARFragment", "Falha ao criar banco de imagens!")
        }

        return config
    }

}