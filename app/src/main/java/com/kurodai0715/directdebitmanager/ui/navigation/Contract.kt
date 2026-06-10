package com.kurodai0715.directdebitmanager.ui.navigation

object Contract {

    sealed interface SelectTarget {

        val route: String

        data object Payer : SelectTarget {
            override val route: String = "payer"
        }

        data object Payee : SelectTarget {
            override val route: String = "payee"
        }

        companion object {
            fun valueOf(route: String): SelectTarget {
                return when (route) {
                    Payer.route -> Payer
                    Payee.route -> Payee
                    else -> throw IllegalArgumentException("Invalid route: $route")
                }
            }
        }
    }
}