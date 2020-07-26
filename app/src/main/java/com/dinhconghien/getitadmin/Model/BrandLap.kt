package com.dinhconghien.getitadmin.Model

import java.util.*

class BrandLap() {
    var idBrandLap : String? = null
    var nameBrand  : String?=null
    var avaBrandLap : String? =null
    constructor(idBrandLap: String,nameBrandLap: String,avaBrandLap: String) : this(){
        this.idBrandLap = idBrandLap
        this.nameBrand = nameBrandLap
        this.avaBrandLap=avaBrandLap
    }

     override fun toString(): String {
         return nameBrand?.toUpperCase(Locale.ENGLISH)!!
     }
}