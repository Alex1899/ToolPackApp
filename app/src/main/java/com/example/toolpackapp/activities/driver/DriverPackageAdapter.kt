package com.example.toolpackapp.activities.driver

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.toolpackapp.R
import com.example.toolpackapp.firestore.FirestoreClass
import com.example.toolpackapp.models.PackageListItem

class DriverPackageAdapter(
    private val context: Context,
    private val dataset: ArrayList<PackageListItem>,
    val onDeliverClick: (String) -> Unit,
    val onLocationClick: (PackageListItem) -> Unit
) : RecyclerView.Adapter<DriverPackageAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.package_name)
        val deliveryDate: TextView = view.findViewById(R.id.package_delivery_date)
        val deliveryTime: TextView = view.findViewById(R.id.package_delivery_time)
        val buildingSite: TextView = view.findViewById(R.id.package_building_site)
        val vendorAddress: TextView = view.findViewById(R.id.package_vendor_adress)

        val showLocationBtn: Button = view.findViewById(R.id.show_location_btn)
        val markAsDeliveredBtn: Button = view.findViewById(R.id.mark_as_delivered)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.package_item_driver, parent, false)

        return ViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataset[position]
        holder.name.text = context.resources.getString(R.string.packageName, item.name)

        holder.deliveryDate.text =
            context.resources.getString(R.string.deliveryDate, item.deliveryDate)
        holder.deliveryTime.text =
            context.resources.getString(R.string.deliveryTime, item.deliveryTime)
        holder.buildingSite.text =
            context.resources.getString(R.string.buildingSite, item.buildingSite)

        holder.vendorAddress.text = context.resources.getString(R.string.vendorAddress, item.vendor)


        holder.markAsDeliveredBtn.setOnClickListener {
            onDeliverClick(item.id)
        }

        holder.showLocationBtn.setOnClickListener {
            onLocationClick(item)
            }
    }

    override fun getItemCount() = dataset.size


}