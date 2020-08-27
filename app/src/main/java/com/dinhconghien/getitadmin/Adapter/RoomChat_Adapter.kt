package com.dinhconghien.getitadmin.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dinhconghien.getitadmin.Model.RoomChat
import com.dinhconghien.getitadmin.R
import com.dinhconghien.getitadmin.Screen.ChatMessageActivity

class RoomChat_Adapter(
    var context: Context,
    var listRoomChat: ArrayList<RoomChat>,
    var idAdmin: String
) : RecyclerView.Adapter<RoomChat_Adapter.ViewHolder>() {
    var listener : RoomChat_Adapter.OnItemClickedListener? = null

    fun setListRoomChatNew(listUser: ArrayList<RoomChat>) {
        this.listRoomChat = listUser
        notifyDataSetChanged()
    }

    fun setOnItemClickedListener(listener: RoomChat_Adapter.OnItemClickedListener) {
        this.listener = listener
    }

    interface OnItemClickedListener {
        fun onClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomChat_Adapter.ViewHolder {
        val inflate: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_roomchat, null)
        context = parent.context
        return ViewHolder(inflate)
    }

    override fun getItemCount(): Int {
        return listRoomChat.size
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onBindViewHolder(holder: RoomChat_Adapter.ViewHolder, position: Int) {
        val roomChatModel = listRoomChat.get(position)
        val lastMessage = roomChatModel.lastMessage
        val lastDate = roomChatModel.lastDate
        val wasReadMes = roomChatModel.wasReadLastMes
        val idSender = roomChatModel.idSenderLastMes
//        val listUnreadMesUser = listRoomChat.get(position).listUnreadMessageUser
        val countUnreadAdmin = roomChatModel.countUnreadMesAdmin
        holder.tv_userName.text = roomChatModel.userName
        holder.tv_date.text = lastDate
        if (roomChatModel.wasSeenUser == false) {
            holder.imv_isOnline.setBackgroundResource(R.drawable.bg_offline)
        }
        if (wasReadMes == false && idSender != idAdmin){ // khi user chưa đọc và người gửi khác admin
            holder.tv_lastMes.setTextColor(R.color.black)
            holder.tv_lastMes.text = lastMessage
        }
        else if (wasReadMes == false && idSender == idAdmin){
            holder.tv_lastMes.setTextColor(Color.parseColor("#000000"))
            holder.tv_lastMes.text = "Bạn : $lastMessage"
        }
        else if (wasReadMes == false && idSender != idAdmin){
            holder.tv_lastMes.setTextColor(Color.parseColor("#000000"))
            holder.tv_lastMes.text = lastMessage
        }
        else if (wasReadMes == true && idSender == idAdmin){
            holder.tv_lastMes.text = "Bạn : $lastMessage"
        }
        else if (wasReadMes == true && idSender != idAdmin){
            holder.tv_lastMes.text = lastMessage
        }
        if (countUnreadAdmin == 0){
            holder.tv_unReadMes.visibility = View.GONE
        }else{
            holder.tv_unReadMes.text = countUnreadAdmin.toString()
        }
        if (roomChatModel.avaUser != ""){
            Glide.with(context).load(roomChatModel.avaUser).fitCenter().into(holder.imv_avaUser)
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context,ChatMessageActivity::class.java)
            intent.putExtra("idUser",roomChatModel.idUser)
            intent.putExtra("idRoomChat",roomChatModel.idRoomChat)
            intent.putExtra("adminName",roomChatModel.adminName)
            intent.putExtra("avaAdmin",roomChatModel.avaAdmin)
            listener?.onClicked(position)
            context.startActivity(intent)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imv_avaUser: ImageView = itemView.findViewById(R.id.imv_avatar_itemListMessage)
        val tv_unReadMes: TextView = itemView.findViewById(R.id.tv_unReadMes_listMesItem)
        val imv_isOnline: ImageView = itemView.findViewById(R.id.imv_online_listMesItem)
        val tv_userName: TextView = itemView.findViewById(R.id.tv_userName_listMessageItem)
        val tv_lastMes: TextView = itemView.findViewById(R.id.tv_lastMessage_listMessageItem)
        val tv_date: TextView = itemView.findViewById(R.id.tv_date_listMessageItem)
    }
}