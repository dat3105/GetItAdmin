package com.dinhconghien.getitadmin.Screen


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.dinhconghien.getitadmin.Adapter.TablayoutViewPager_Adapter
import com.dinhconghien.getitadmin.R
import com.google.android.material.tabs.TabLayout


class InvoiceFragment : androidx.fragment.app.Fragment() {
    lateinit var viewPager: ViewPager
    lateinit var tabLayout: TabLayout
    lateinit var tablayoutviewpagerAdapter  : TablayoutViewPager_Adapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      val view =  inflater.inflate(R.layout.fragment_invoice, container, false)
        anhXa(view)
        val fragmentManager =
            activity!!.supportFragmentManager
        tablayoutviewpagerAdapter = TablayoutViewPager_Adapter(fragmentManager)
        tablayoutviewpagerAdapter.addFragment(InvoiceWatingFragment(), "Chờ xác nhận")
        tablayoutviewpagerAdapter.addFragment(InvoiceAcceptedFragment(), "Đã giao")
        tablayoutviewpagerAdapter.addFragment(InvoiceCancelledFragment(), "Đã hủy")
        viewPager.adapter = tablayoutviewpagerAdapter
        tabLayout.setupWithViewPager(viewPager)
        return view
    }

    private fun anhXa(view: View) {
        viewPager = view.findViewById(R.id.viewPager_invoiceScreen)
        tabLayout = view.findViewById(R.id.tablayout_invoiceScreen)
    }
}