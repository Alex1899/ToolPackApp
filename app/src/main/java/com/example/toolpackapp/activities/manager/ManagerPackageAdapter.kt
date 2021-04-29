package com.example.toolpackapp.activities.manager

import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColorStateList
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.toolpackapp.R
import com.example.toolpackapp.models.PackageListItem

class ManagerPackageAdapter(
    private val context: Context,
    private val dataset: ArrayList<PackageListItem>
) : RecyclerView.Adapter<ManagerPackageAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.package_name)
        val driver: TextView = view.findViewById(R.id.package_driver)
        val vendor: TextView = view.findViewById(R.id.package_vendor)
        val status: TextView = view.findViewById(R.id.package_status)
        val deliveryDate: TextView = view.findViewById(R.id.package_delivery_date)
        val deliveryTime: TextView = view.findViewById(R.id.package_delivery_time)
        val buildingSite: TextView = view.findViewById(R.id.package_building_site)
        val description: TextView = view.findViewById(R.id.package_description)
        val editBtn: Button = view.findViewById(R.id.edit_btn)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.package_item_manager, parent, false)
        return ViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataset[position]

        Log.d("EditPackage", "$item")

        holder.name.text = context.resources.getString(R.string.packageName, item.name)
        holder.status.text = item.status

        val color = if (item.status == "Pending") getColorStateList(
            context,
            R.color.red_700
        ) else getColorStateList(context,R.color.green)

        holder.status.setTextColor(color)

        holder.driver.text = context.resources.getString(R.string.packageDriver, item.driver)
        holder.vendor.text = item.vendor

        holder.deliveryDate.text = item.deliveryDate
        holder.deliveryTime.text = item.deliveryTime
        holder.buildingSite.text = item.buildingSite

        if(item.description === ""){
            holder.description.visibility = View.GONE
        }else{
            holder.description.text = context.resources.getString(R.string.packageDescription, item.description)
        }

        val packageListItem = PackageListItem(
            item.name,
            item.deliveryDate,
            item.deliveryTime,
            item.buildingSite,
            item.driver,
            item.status,
            item.vendor,
            item.id,
            item.description
        )
        holder.editBtn.setOnClickListener {
            val intent = Intent(context, ManagerEditPackageDetailsActivity::class.java)
            intent.putExtra("packageListItem", packageListItem)
            startActivity(context, intent, null)
        }
    }

    override fun getItemCount() = dataset.size
}