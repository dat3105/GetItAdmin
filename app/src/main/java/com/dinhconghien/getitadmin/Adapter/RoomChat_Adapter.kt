package com.dinhconghien.getitadmin.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dinhconghien.getitadmin.Model.RoomChat
import com.dinhconghien.getitadmin.R

class RoomChat_Adapter(var context: Context, var listRoomChat : ArrayList<RoomChat>, var idUser : String)
    : RecyclerView.Adapter<RoomChat_Adapter.ViewHolder>() {

    fun setListRoomChatNew(listRoomChat: ArrayList<RoomChat>){
        this.listRoomChat = listRoomChat
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomChat_Adapter.ViewHolder {
        val inflate: View = LayoutInflater.from(parent.context).inflate(R.layout.item_roomchat, null)
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
        val listUnreadMesUser = listRoomChat.get(position).listUnreadMessageUser
        holder.tv_userName.text = roomChatModel.adminName
        holder.tv_date.text = lastDate
        if (roomChatModel.wasOnlineAdmin == false) {
            holder.imv_isOnline.setBackgroundResource(R.drawable.bg_offline)
        }
        if (wasReadMes == false && idSender != idUser){
            holder.tv_lastMes.setTextColor(R.color.black)
            holder.tv_lastMes.text = lastMessage
        }
        else if (wasReadMes == false && idSender == idUser){
            holder.tv_lastMes.setTextColor(R.color.black)
            holder.tv_lastMes.text = "Bạn : $lastMessage"
        }
        holder.tv_lastMes.text = lastMessage
        if (listUnreadMesUser.size == 0){
            holder.tv_unReadMes.visibility = View.GONE
        }
        holder.tv_unReadMes.text = listUnreadMesUser.size.toString()
        if (roomChatModel.avaAdmin != ""){
            Glide.with(context).load(roomChatModel.avaAdmin).fitCenter().into(holder.imv_avaUser)
        }

    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var imv_avaUser : ImageView = itemView.findViewById(R.id.imv_avatar_itemListMessage)
        val tv_unReadMes : TextView = itemView.findViewById(R.id.tv_unReadMes_listMesItem)
        val imv_isOnline : ImageView = itemView.findViewById(R.id.imv_online_listMesItem)
        val tv_userName : TextView = itemView.findViewById(R.id.tv_userName_listMessageItem)
        val tv_lastMes : TextView = itemView.findViewById(R.id.tv_lastMessage_listMessageItem)
        val tv_date : TextView = itemView.findViewById(R.id.tv_date_listMessageItem)
    }
}