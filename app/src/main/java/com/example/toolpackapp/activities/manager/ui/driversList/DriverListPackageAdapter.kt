package com.example.toolpackapp.activities.manager.ui.driversList

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.toolpackapp.R
import com.example.toolpackapp.activities.manager.ManagerEditDriverDetailsActivity
import com.example.toolpackapp.models.User
import com.example.toolpackapp.utils.GlideLoader

class DriverListPackageAdapter(
    private val context: Context,
    private val dataset: ArrayList<User>,
) : RecyclerView.Adapter<DriverListPackageAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.driver_avatar)
        val name: TextView = view.findViewById(R.id.driver_fullname)
        val email: TextView = view.findViewById(R.id.driver_email)
        val mobile: TextView = view.findViewById(R.id.driver_mobile)
        val profileCompleted: TextView = view.findViewById(R.id.driver_profille_completed)

        val editBtn: Button = view.findViewById(R.id.driver_edit_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.driver_list_item, parent, false)

        return DriverListPackageAdapter.ViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataset[position]
        GlideLoader(context).loadUserPicture(item.photo.toUri(), holder.avatar)

        holder.name.text = context.resources.getString(R.string.driverName, item.fullname)
        holder.email.text = context.resources.getString(R.string.driverEmail, item.email)
        holder.mobile.text = context.resources.getString(R.string.driverMobile, item.mobile)

        val color = if (item.profileCompleted == 0) ContextCompat.getColorStateList(
            context,
            R.color.red_700
        ) else ContextCompat.getColorStateList(context, R.color.green)

        holder.profileCompleted.text = if(item.profileCompleted == 1) "Yes" else "No"
        holder.profileCompleted.setTextColor(color)

        val driver = User(
            item.id,
            item.fullname,
            item.email,
            item.photo,
            item.mobile,
            item.accountType,
            item.profileCompleted
        )
        holder.editBtn.setOnClickListener{
            val intent = Intent(context, ManagerEditDriverDetailsActivity::class.java)
            intent.putExtra("driverListItem", driver)
            startActivity(context, intent, null)
        }


    }

    override fun getItemCount() = dataset.size
}