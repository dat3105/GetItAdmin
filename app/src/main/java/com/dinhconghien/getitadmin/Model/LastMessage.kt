package com.dinhconghien.getitadmin.Model

data class LastMessage(
    var idRoomChat: String = "",
    var idSender: String = "",
    var idReceiver: String = "",
    var message: String = "",
    var date: String = "",
    var wasRead : Boolean = false
) {
}