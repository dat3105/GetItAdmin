package com.dinhconghien.getitadmin.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dinhconghien.getitadmin.Model.User
import com.dinhconghien.getitadmin.R

class ListUser_Adapter(var context: Context, var listAdmin : ArrayList<User>)
    : RecyclerView.Adapter<ListUser_Adapter.ViewHolder>() {
    var listener : OnItemClickedListener? = null

    fun setListAdminNew(listAdmin: ArrayList<User>){
        this.listAdmin = listAdmin
        notifyDataSetChanged()
    }

    fun setOnItemClickedListener(listener: OnItemClickedListener) {
        this.listener = listener
    }

    interface OnItemClickedListener {
        fun onClicked(position: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflate: View = LayoutInflater.from(parent.context).inflate(R.layout.item_list_user, null)
        context = parent.context
        return ViewHolder(inflate)
    }
    override fun getItemCount(): Int {
        return listAdmin.size
    }

    override fun onBindViewHolder(holder: ListUser_Adapter.ViewHolder, position: Int) {
        val adminModel = listAdmin.get(position)
        holder.tv_userName.text = adminModel.userName
        holder.tv_phoneNumber.text = adminModel.phone
        if (adminModel.wasOnline == false){
            holder.imv_isOnline.setBackgroundResource(R.drawable.bg_offline)
        }
        if (adminModel.avaUser != ""){
            Glide.with(context).load(adminModel.avaUser).fitCenter().into(holder.imv_avaAdmin)
        }

        holder.itemView.setOnClickListener {
            listener?.onClicked(position)
        }
    }
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var imv_avaAdmin : ImageView = itemView.findViewById(R.id.imv_avaAdmin_itemListAdmin)
        val imv_isOnline : ImageView = itemView.findViewById(R.id.imv_online_listAdminItem)
        val tv_userName : TextView = itemView.findViewById(R.id.tv_userName_listMessageItem)
        val tv_phoneNumber : TextView = itemView.findViewById(R.id.tv_phoneNumber_listAdminItem)
    }
}