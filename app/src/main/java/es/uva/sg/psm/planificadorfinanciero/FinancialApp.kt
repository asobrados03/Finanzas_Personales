package es.uva.sg.psm.planificadorfinanciero

import android.app.Application

class FinancialApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}