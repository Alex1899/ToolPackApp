package com.example.toolpackapp.activities.manager.ui.buildingSiteList

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
import com.example.toolpackapp.activities.manager.ManagerEditBuildingSiteDetailsActivity
import com.example.toolpackapp.activities.manager.ManagerEditVendorDetailsActivity
import com.example.toolpackapp.activities.manager.ui.vendorList.VendorListPackageAdapter
import com.example.toolpackapp.models.BuildingSite
import com.example.toolpackapp.models.Vendor

class BuildingSitePackageAdapter(private val context: Context,
                                 private val dataset: ArrayList<BuildingSite>,
) : RecyclerView.Adapter<BuildingSitePackageAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val siteName: TextView = view.findViewById(R.id.building_name)
        val siteAdminEmail: TextView = view.findViewById(R.id.siteAdmin_email)
        val siteAdminFullname: TextView = view.findViewById(R.id.siteAdmin_fullname)
        val phone: TextView = view.findViewById(R.id.site_phone)
        val address: TextView = view.findViewById(R.id.site_address)


        val editBtn: Button = view.findViewById(R.id.site_edit_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.building_list_item, parent, false)

        return BuildingSitePackageAdapter.ViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataset[position]

        holder.siteName.text = context.resources.getString(R.string.buildingName, item.siteName)
        holder.siteAdminEmail.text =
            context.resources.getString(R.string.siteAdminEmail, item.siteAdminEmail)
        holder.siteAdminFullname.text =
            context.resources.getString(R.string.siteAdminName, item.siteAdminFullname)
        holder.phone.text = context.resources.getString(R.string.buildingPhone, item.sitePhone)
        holder.address.text =
            context.resources.getString(R.string.buildingAddress, item.siteAddress)

        val building = BuildingSite(
            item.id,
            item.siteName,
            item.siteAdminFullname,
            item.siteAdminEmail,
            item.siteAddress,
            item.sitePhone
        )
        holder.editBtn.setOnClickListener {
            val intent = Intent(context, ManagerEditBuildingSiteDetailsActivity::class.java)
            intent.putExtra("buildingListItem", building)
            ContextCompat.startActivity(context, intent, null)
        }

    }

    override fun getItemCount() = dataset.size
}