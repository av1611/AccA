package mattecarra.accapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import mattecarra.accapp.R
import mattecarra.accapp.models.AccaProfile
import mattecarra.accapp.models.ProfileExportItem

class ProfileImportAdapter: RecyclerView.Adapter<ProfileImportAdapter.ProfileImportItemHolder>() {

    private var mProfiles: ArrayList<ProfileExportItem> = ArrayList()

    inner class ProfileImportItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        ProfileExportItem.Listener, View.OnClickListener {

        lateinit var mExportProfile: ProfileExportItem

        private val itemRl: RelativeLayout = itemView.findViewById(R.id.pex_item_rl)
        private val titleTv: TextView = itemView.findViewById(R.id.pex_item_name_tv)
        private val selectedCb: CheckBox = itemView.findViewById(R.id.pex_item_checkbox)

        init {
            itemRl.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            mProfiles[adapterPosition].check()
        }

        fun setData(exportItem: ProfileExportItem) {
            mExportProfile = exportItem
            titleTv.text = exportItem.getName()
        }

        override fun onCheckedChanged(value: Boolean) {
            selectedCb.isChecked = value
        }

        fun getExportProfile(): ProfileExportItem {
            return mExportProfile
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileImportItemHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.profile_export_item, parent, false)
        return ProfileImportItemHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileImportItemHolder, position: Int) {
        val exportProfile: ProfileExportItem = mProfiles[position]
        exportProfile.setOnCheckedChangedListener(holder)
        holder.setData(exportProfile)
    }

    override fun onViewRecycled(holder: ProfileImportItemHolder) {
        holder.getExportProfile().setOnCheckedChangedListener(null)
    }

    override fun getItemCount(): Int {
        return mProfiles.size
    }

    fun setProfiles(profiles: List<AccaProfile>) {
        // Create ExportProfile list based off AccaProfile list provided
        // Clear existing profiles
        mProfiles.clear()

        for (profile in profiles) {
            var exPro = ProfileExportItem(profile, profile.profileName)
            mProfiles.add(exPro)
        }

        notifyDataSetChanged()
    }

    fun getExports(): ArrayList<ProfileExportItem> {
        return mProfiles
    }
}