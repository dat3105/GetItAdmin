package com.dinhconghien.getitadmin.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dinhconghien.getitadmin.Model.Laptop
import com.dinhconghien.getitadmin.R

class ListLapPayment_Adapter(var listLapPayment: ArrayList<Laptop>) :
    RecyclerView.Adapter<ListLapPayment_Adapter.ViewHolder>() {

    fun setListLapPaymentNew(listLapPayment: ArrayList<Laptop>){
        this.listLapPayment = listLapPayment
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflate: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_listlap_order, null)
        return ViewHolder(inflate)
    }

    override fun getItemCount(): Int {
        return listLapPayment.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lapPayment = listLapPayment.get(position)
        holder.tv_nameLap.text = lapPayment.nameLap
        holder.tv_brandLap.text = lapPayment.nameBrand
        var price = lapPayment.priceLap.toString()
        if (price.length == 7) {
            val firstChar = price.substring(0, 1)
            val middleChar = price.substring(1, 4)
            val lastChar = price.substring(4, 7)
            price = "$firstChar.$middleChar.$lastChar"
        } else if (price.length == 8) {
            val firstChar = price.substring(0, 2)
            val middleChar = price.substring(2, 5)
            val lastChar = price.substring(5, 8)
            price = "$firstChar.$middleChar.$lastChar"
        }
        holder.tv_priceLap.text = "$price VNĐ"
        holder.tv_amountLap.text = "x ${lapPayment.amountInCart}"
        Glide.with(holder.itemView)
            .load(lapPayment.avaLap)
            .fitCenter()
            .into(holder.imv_avaLap)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imv_avaLap: ImageView = itemView.findViewById(R.id.imv_avaLap_paymentItem)
        val tv_nameLap: TextView = itemView.findViewById(R.id.tv_nameLapTop_paymentItem)
        val tv_brandLap: TextView = itemView.findViewById(R.id.tv_nameBrand_paymentItem)
        val tv_priceLap: TextView = itemView.findViewById(R.id.tv_priceLap_paymentItem)
        val tv_amountLap: TextView = itemView.findViewById(R.id.tv_amount_paymentItem)
    }
}