package com.example.data.table

import org.jetbrains.exposed.sql.Table

object ProductTable: Table() {
    val id = varchar("id", 256)
    val title = varchar("title", 256)
    val description = varchar("description", 1024)
    val category = varchar("category", 256)
    val price = integer("price")
    val availability = bool("availability")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}