package com.example.tenantmanagementsystem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

data class Row(val id: Long, val title: String, val subtitle: String)

class RowAdapter(
    private val onClick: (Row) -> Unit = {},
    private val onEditClick: (Row) -> Unit = {},
    private val onDeleteClick: (Row) -> Unit = {},
    private val onLongClick: (Row) -> Unit = {}
) : ListAdapter<Row, RowAdapter.VH>(object : DiffUtil.ItemCallback<Row>() {
    override fun areItemsTheSame(a: Row, b: Row) = a.id == b.id
    override fun areContentsTheSame(a: Row, b: Row) = a == b
}) {
    class VH(val v: View) : RecyclerView.ViewHolder(v) {
        val avatar: TextView = v.findViewById(R.id.avatarTextView)
        val t1: TextView = v.findViewById(R.id.tenantNameTextView)
        val t2: TextView = v.findViewById(R.id.tenantSubtitleTextView)
        val menuBtn: ImageButton = v.findViewById(R.id.menuButton)
    }

    override fun onCreateViewHolder(p: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(p.context).inflate(R.layout.item_tenant_card, p, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val r = getItem(pos)
        h.avatar.text = r.title.take(1).uppercase()
        h.t1.text = r.title
        h.t2.text = r.subtitle

        h.v.setOnClickListener { onClick(r) }
        h.v.setOnLongClickListener {
            onLongClick(r)
            true
        }

        h.menuBtn.setOnClickListener { view ->
            val popup = PopupMenu(view.context, view)
            popup.menu.add(0, 1, 0, "View Details")
            popup.menu.add(0, 2, 1, "Edit Tenant")
            popup.menu.add(0, 3, 2, "Delete Tenant")
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    1 -> onClick(r)
                    2 -> onEditClick(r)
                    3 -> onDeleteClick(r)
                }
                true
            }
            popup.show()
        }
    }
}
