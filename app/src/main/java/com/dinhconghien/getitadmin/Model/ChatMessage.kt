package com.dinhconghien.getitadmin.Model

data class ChatMessage(
    var idChatMessage: String = "", //keyFirebase tự sinh = idChatMessage
    var idRoomChat: String = "",
    var messageContent: String = "",
    var date: String = "",
    var idSender: String = "",
    var idReceiver: String =""
) {
}