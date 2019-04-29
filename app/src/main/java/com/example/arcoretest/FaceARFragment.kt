package com.example.arcoretest

import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment
import java.util.*

class FaceARFragment: ArFragment() {

    /**
     * Configura sessão para usar a camera frontal
     */
    override fun getSessionFeatures(): MutableSet<Session.Feature> {
        return EnumSet.of(Session.Feature.FRONT_CAMERA)
    }


    /**
     * Habilita o suporte para face mesh
     */
    override fun getSessionConfiguration(session: Session?): Config {
        //Desabilita a "mãozinha" quando abre o app e o plano pontilhado
        planeDiscoveryController.setInstructionView(null)

        val config = Config(session)
        config.augmentedFaceMode = Config.AugmentedFaceMode.MESH3D
        return config
    }
}