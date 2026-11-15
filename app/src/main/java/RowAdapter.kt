package com.example.tenantmanagementsystem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

data class Row(val id: Long, val title: String, val subtitle: String)

class RowAdapter(
    private val onClick: (Row) -> Unit = {},
    private val onLongClick: (Row) -> Unit = {}
) : ListAdapter<Row, RowAdapter.VH>(object : DiffUtil.ItemCallback<Row>() {
    override fun areItemsTheSame(a: Row, b: Row) = a.id == b.id
    override fun areContentsTheSame(a: Row, b: Row) = a == b
}) {
    class VH(val v: View) : RecyclerView.ViewHolder(v) {
        val t1: TextView = v.findViewById(android.R.id.text1)
        val t2: TextView = v.findViewById(android.R.id.text2)
    }
    override fun onCreateViewHolder(p: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(p.context).inflate(android.R.layout.simple_list_item_2, p, false))
    override fun onBindViewHolder(h: VH, pos: Int) {
        val r = getItem(pos)
        h.t1.text = r.title; h.t2.text = r.subtitle
        h.v.setOnClickListener { onClick(r) }
        h.v.setOnLongClickListener { onLongClick(r); true }
    }
}