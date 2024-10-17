package com.vhenriquez.txwork.screens.activities

enum class ActivityActionOption(val title: String) {
    EditActivity("Editar actividad"),
    DeleteActivity("Eliminar actividad"),
    UsersInActivity("Usuarios"),
    //ReportsInActivity("Reportes"),
    ToggleStatusActivity("Cerrar actividad"),
    Cancel("Cancelar");

    companion object {
        fun getByTitle(title: String): ActivityActionOption {
            entries.forEach { action -> if (title == action.title) return action }
            return EditActivity
        }

        fun getOptions(hasEditOption: Boolean): List<String> {
            val options = mutableListOf<String>()
            entries.forEach { activityAction ->
                if (hasEditOption || activityAction != EditActivity) {
                    options.add(activityAction.title)
                }
            }
            return options
        }
    }
}