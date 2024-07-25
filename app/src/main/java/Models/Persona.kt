package Models

import java.io.Serializable

class Persona : Serializable {
    // Getters
    var id: Int
        private set
    @JvmField
    var nombre: String
    @JvmField
    var telefono: String
    @JvmField
    var latitud: String
    @JvmField
    var longitud: String
    @JvmField
    var firma: String

    // Constructor
    constructor(
        nombre: String,
        telefono: String,
        latitud: String,
        longitud: String,
        firma: String,
        id: Int
    ) {
        this.id = id
        this.nombre = nombre
        this.telefono = telefono
        this.latitud = latitud
        this.longitud = longitud
        this.firma = firma
    }

    constructor() {
        this.id = 0
        this.nombre = ""
        this.telefono = ""
        this.latitud = ""
        this.longitud = ""
        this.firma = ""
    }


    fun getId(id: Int) {
        this.id = id
    }
}
