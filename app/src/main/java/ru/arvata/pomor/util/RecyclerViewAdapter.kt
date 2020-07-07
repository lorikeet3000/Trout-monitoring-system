package ru.arvata.pomor.util

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

// based on github.com/armcha/Kadapter

// usage:
/*
 recyclerViewAdapter = recycler_view.init(null, R.layout.layout_item_events) {
            this.text_time.text = it.time
        }

recyclerViewAdapter?.setList(list)
*/

typealias Binder<ITEM> = View.(ITEM, position: Int) -> Unit

class RecyclerViewAdapter<ITEM>(private var list: List<ITEM>?,
                                     private val layoutRedId: Int,
                                     private val binder: Binder<ITEM>) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(val root: View) : RecyclerView.ViewHolder(root)

    fun setList(list: List<ITEM>?) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(layoutRedId, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list?.get(position)
        item?.let {
            holder.root.binder(it, holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int = list?.size ?: 0
}

fun <ITEM> RecyclerView.init(list: List<ITEM>?, layoutResId: Int, reverse: Boolean? = false, binder: Binder<ITEM>)
        : RecyclerViewAdapter<ITEM> = RecyclerViewAdapter(list, layoutResId, binder)
    .apply {
        adapter = this
        val lm = LinearLayoutManager(context)
        if(reverse == true) {
            lm.stackFromEnd = true
            lm.reverseLayout = true
        }
        layoutManager = lm
    }

