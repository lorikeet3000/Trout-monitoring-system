package ru.arvata.pomor.util

import android.view.View
import kotlinx.android.synthetic.main.layout_site_selector.view.*

class SiteSelectorView(private val container: View, data: Array<String>, listener: SiteSelectorListener) {
    init {
        container.selector_site.init(data, listener)
    }

    fun hideSiteSelector() {
        container.visibility = View.INVISIBLE
    }

    fun showSiteSelector() {
        container.visibility = View.VISIBLE
    }

    fun selectSite(position: Int) {
        /*
            по умолчанию, если новая позиция совпадает с уже выбранной, ничего не произойдет.
            это задумывалось для того, чтобы не было бесконечного обновления позиции из-за selectedSiteLiveData.
         */
        val selected = container.selector_site.selectedItemPosition
        if(position != selected) {
            container.selector_site.setSelection(position)
        }
    }
}