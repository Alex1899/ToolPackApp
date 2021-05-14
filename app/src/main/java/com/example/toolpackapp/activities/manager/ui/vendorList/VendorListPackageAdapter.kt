package com.example.toolpackapp.activities.manager.ui.vendorList

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.toolpackapp.R
import com.example.toolpackapp.activities.manager.ManagerEditVendorDetailsActivity

import com.example.toolpackapp.models.Vendor

class VendorListPackageAdapter(
    private val context: Context,
    private val dataset: ArrayList<Vendor>,
) : RecyclerView.Adapter<VendorListPackageAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.vendor_name)
        val email: TextView = view.findViewById(R.id.vendor_email)
        val phone: TextView = view.findViewById(R.id.vendor_mobile)
        val address: TextView = view.findViewById(R.id.vendor_address)


        val editBtn: Button = view.findViewById(R.id.vendor_edit_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.vendor_list_item, parent, false)

        return VendorListPackageAdapter.ViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataset[position]

        holder.name.text = context.resources.getString(R.string.vendorNamee, item.vendorName)
        holder.email.text = context.resources.getString(R.string.vendorEmail, item.vendorEmail)
        holder.phone.text = context.resources.getString(R.string.vendorMobile, item.vendorPhone)
        holder.address.text = context.resources.getString(R.string.vendorAddress, item.vendorAddress)

        val vendor = Vendor(
            item.id,
            item.vendorName,
            item.vendorEmail,
            item.vendorAddress,
            item.vendorPhone,

        )
        holder.editBtn.setOnClickListener {
            val intent = Intent(context, ManagerEditVendorDetailsActivity::class.java)
            intent.putExtra("vendorListItem", vendor)
            ContextCompat.startActivity(context, intent, null)
        }


    }

    override fun getItemCount() = dataset.size
}
