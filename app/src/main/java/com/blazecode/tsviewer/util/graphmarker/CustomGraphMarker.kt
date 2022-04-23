package com.blazecode.tsviewer.util.graphmarker

import com.blazecode.tsviewer.R
import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF


class CustomGraphMarker(context: Context, layoutResource: Int):  MarkerView(context, layoutResource) {
    val marker = findViewById<TextView>(R.id.textView)

    override fun refreshContent(entry: Entry?, highlight: Highlight?) {

        marker.text = entry!!.data.toString()
        super.refreshContent(entry, highlight)
    }

    override fun getOffsetForDrawingAtPoint(xpos: Float, ypos: Float): MPPointF {
        return MPPointF(-width / 2f, -height - 10f)
    }
}