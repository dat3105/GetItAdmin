package com.dinhconghien.getitadmin.Model

data class Bill(
    var idBill : String = "",
    var idUser : String = "",
    var date : String = "",
    var sumPrice : String = "",
    var status : String = "",
    var addressOrder : String = "",
    var listLapOrder : ArrayList<Laptop> = ArrayList(),
    var wasRated : Boolean = false,
    var idAdmin : String = "",
    var idPersonCancel : String = ""
) {
}