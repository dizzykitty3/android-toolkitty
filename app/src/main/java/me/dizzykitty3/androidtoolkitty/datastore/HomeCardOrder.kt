package me.dizzykitty3.androidtoolkitty.datastore

internal fun normalizedHomeCardOrder(saved: List<String>, defaults: List<String>): List<String> =
    (saved.filter { it in defaults } + defaults).distinct()
