package com.vhenriquez.txwork.utils

import com.itextpdf.kernel.events.Event
import com.itextpdf.kernel.events.IEventHandler
import com.itextpdf.kernel.events.PdfDocumentEvent
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.layout.Canvas
import com.itextpdf.layout.element.Table

class HeaderFooterEventHandler (private var tableHeader : Table?, private var tableFooter : Table?) :
    IEventHandler {

    override fun handleEvent(event: Event?) {
        val docEvent: PdfDocumentEvent = event as PdfDocumentEvent
        val pdfDoc = docEvent.document
        val page = docEvent.page
        val pageSize = pdfDoc.defaultPageSize
        val cordX = pageSize.x + 50f
        val cordY = pageSize.top - 60f
        val width = pageSize.width - 100f

        val canvas = PdfCanvas(page.newContentStreamAfter(), page.resources, pdfDoc)
        if (tableHeader != null)
            Canvas(canvas, Rectangle(cordX, cordY, width, 50f)).add(tableHeader).close()
        if (tableFooter != null)
            Canvas(canvas, Rectangle(50f, 10f, width, 60f)).add(tableFooter).close()
    }
}