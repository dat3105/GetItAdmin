package com.dinhconghien.getitadmin.Screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dinhconghien.getitadmin.Adapter.Bill_Adapter
import com.dinhconghien.getitadmin.Model.Bill
import com.dinhconghien.getitadmin.R
import com.dinhconghien.getitadmin.Util.SharePreference_Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class InvoiceCancelledFragment : Fragment() {
    lateinit var adapterBill : Bill_Adapter
    lateinit var rcView_invoiceCancelled : RecyclerView
    lateinit var swipeRL_bill : SwipeRefreshLayout
    var listInvoiceCancelled = ArrayList<Bill>()
    var listInvoice = ArrayList<Bill>()
    lateinit var idUser : String
    val DB_BILL = FirebaseDatabase.getInstance().getReference("bill")
    val TAG_GETBILL = "DbError_getBill_billCancelled"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val view =  inflater.inflate(R.layout.fragment_invoice_cancelled, container, false)
        init(view)
        getInvoiceItem(view)
        swipeRL_bill.setOnRefreshListener {
            swipeRL_bill.isRefreshing = false
            getInvoiceItem(view)
        }
        return view
    }

    private fun init(view: View){
        rcView_invoiceCancelled = view.findViewById(R.id.rcView_canceledBillScreen)
        swipeRL_bill = view.findViewById(R.id.swipeRL_canceledBillScreen)

    }

    private fun getInvoiceItem(view: View){
        adapterBill = Bill_Adapter(view.context,listInvoiceCancelled)
        rcView_invoiceCancelled.layoutManager = LinearLayoutManager(view.context,
            LinearLayoutManager.VERTICAL,false)
        rcView_invoiceCancelled.setHasFixedSize(true)
        rcView_invoiceCancelled.adapter = adapterBill
        getBill()
    }

    private fun getBill(){
        DB_BILL.addListenerForSingleValueEvent(object :
            ValueEventListener {
            @SuppressLint("LongLogTag")
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG_GETBILL,error.toString())
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                getBillCancelled()
            }
        })
    }

    private fun getBillCancelled(){
        DB_BILL.orderByChild("status").equalTo("Đã hủy")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("LongLogTag")
                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG_GETBILL,error.toString())
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    listInvoice.clear()
                    getBillModel(snapshot)
                }

            })
    }

    private fun getBillModel(snapshot: DataSnapshot){
        for(param in snapshot.children){
            val billModel = param.getValue(Bill::class.java)
            if (billModel != null){
                listInvoice.add(billModel)
                adapterBill.setListBill(listInvoice)
            }
        }
    }


}