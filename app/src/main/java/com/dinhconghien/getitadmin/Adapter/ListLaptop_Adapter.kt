package com.dinhconghien.getitadmin.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.dinhconghien.getitadmin.Model.Laptop
import com.dinhconghien.getitadmin.Model.User
import com.dinhconghien.getitadmin.R
import com.dinhconghien.getitadmin.Screen.UpdateLapActivity

class ListLaptop_Adapter(var context: Context,var listLap : ArrayList<Laptop>) : RecyclerView.Adapter<ListLaptop_Adapter.ViewHolder>() {
    var listener : OnItemClickedListener? = null

     fun setIDBrandLap(listLap: ArrayList<Laptop>){
        this.listLap = listLap
        notifyDataSetChanged()
    }

    fun setOnItemClickedListener(listener: OnItemClickedListener) {
        this.listener = listener
    }

    interface OnItemClickedListener {
        fun onClicked(position: Int,typeFunction : Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate: View = LayoutInflater.from(context).inflate(R.layout.item_listlap, null)
        context = parent.context
        return ViewHolder(inflate)
    }

    override fun getItemCount(): Int {
        return listLap.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val lapModel = listLap.get(position)
        holder.tv_nameLap.text = lapModel.nameLap
        var price = lapModel.priceLap.toString()
        var isClick = true
            if (price.length == 7){
                val firstChar = price.substring(0,1)
                val middleChar = price.substring(1,4)
                val lastChar = price.substring(4,7)
                price = "$firstChar.$middleChar.$lastChar"
            }else if (price.length == 8){
                val firstChar = price.substring(0,2)
                val middleChar = price.substring(2,5)
                val lastChar = price.substring(5,8)
                price = "$firstChar.$middleChar.$lastChar"
            }
        Log.v("Check price","price's length : ${price.length}")
        holder.tv_priceLap.text = price
        holder.tv_quantity.text = "${lapModel.quantity}"

        holder.tv_update.setOnClickListener {
            val intent = Intent(context,UpdateLapActivity::class.java)
            intent.putExtra("idBrandLap",lapModel.idBrandLap)
            intent.putExtra("nameLap",lapModel.nameLap)
            intent.putExtra("idLap",lapModel.idLap)
            intent.putExtra("priceLap",lapModel.priceLap)
            intent.putExtra("quantity",lapModel.quantity)
            intent.putExtra("avaLap",lapModel.avaLap)
            intent.putExtra("nameBrand",lapModel.nameBrand)
            context.startActivity(intent)
            listener?.onClicked(position,false)
        }
        holder.tv_delete.setOnClickListener {
            //because listLap is the first List which inited when start the ListLapActivity in the first time
            //so to delete item at 'position' and set list after deleting it,listLap needs a copy of itseft
            //to apply this action otherwise it will be get java.lang.indexoutofboundsexception
            val listAfterterDelete : MutableList<Laptop> = listLap.toMutableList().apply {
                removeAt(position)
            }
            setIDBrandLap(listAfterterDelete as ArrayList<Laptop>)
            notifyItemRangeChanged(position,itemCount)
            listener?.onClicked(position,true)
            holder.linear_option.visibility = View.GONE
        }
//test git
        holder.itemView.setOnClickListener {
            if (isClick){
                holder.linear_option.visibility = View.VISIBLE
                isClick = false
            }else{
                holder.linear_option.visibility = View.GONE
                isClick = true
            }
        }
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val tv_nameLap : TextView = itemView.findViewById(R.id.tv_nameLap_itemListLap)
        val tv_priceLap : TextView = itemView.findViewById(R.id.tv_priceLap_itemListLap)
        val tv_quantity : TextView = itemView.findViewById(R.id.tv_quantity_itemListLap)
        val linear_option : LinearLayout = itemView.findViewById(R.id.linear_option_itemListLap)
        val tv_update : AppCompatTextView = itemView.findViewById(R.id.tv_update_itemListLap)
        val tv_delete : AppCompatTextView = itemView.findViewById(R.id.tv_delete_itemListLap)
    }


}