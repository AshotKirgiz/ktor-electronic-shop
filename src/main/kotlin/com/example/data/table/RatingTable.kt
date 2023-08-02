package com.example.data.table

import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNotNull
import org.jetbrains.exposed.sql.Table

object RatingTable: Table() {
    val productid = varchar("productid", 256).references(ProductTable.id)
    val gradeid = integer("gradeid").autoIncrement()
    val name = varchar("name", 512)
    val grade = integer("grade")
    val title = varchar("title", 256)
    val description = varchar("description", 1024)

    override val primaryKey: PrimaryKey = PrimaryKey(gradeid)
}