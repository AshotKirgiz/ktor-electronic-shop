package com.example.repository

import com.example.data.model.Product
import com.example.data.model.User
import com.example.data.table.ProductTable
import com.example.data.table.UserTable
import com.example.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class Repo {
    suspend fun addUser(user: User) {
        dbQuery {
            UserTable.insert { ut ->
                ut[UserTable.email] = user.email
                ut[UserTable.hashPassword] = user.hashPassword
                ut[UserTable.name] = user.userName
            }
        }
    }

    suspend fun findUserByEmail(email:String) = dbQuery {
        UserTable.select { UserTable.email.eq(email) }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    private fun rowToUser(row: ResultRow?):User?{
        if(row == null){
            return null
        }

        return User(
            email = row[UserTable.email],
            hashPassword = row[UserTable.hashPassword],
            userName = row[UserTable.name]
        )
    }

    suspend fun addProduct(product: Product){
        dbQuery {
            ProductTable.insert { pt->
                pt[ProductTable.id] = product.id
                pt[ProductTable.title] = product.title
                pt[ProductTable.description] = product.description
                pt[ProductTable.category] = product.category
                pt[ProductTable.price] = product.price
                pt[ProductTable.availability] = product.availability
            }
        }
    }

    suspend fun getAllProducts(): List<Product?> = dbQuery {
        ProductTable.selectAll().map(::rowToProduct)
    }

    suspend fun findProductById(id: String): List<Product?> = dbQuery {
        ProductTable.select{
            ProductTable.id.eq(id)
        }.map { rowToProduct(it) }
    }

    suspend fun findProductByCategory(category: String) = dbQuery {
        ProductTable.select {
            ProductTable.category.eq(category)
        }.mapNotNull { rowToProduct(it) }
    }

    suspend fun updateProduct(product: Product) {
        dbQuery {
            ProductTable.update(
                where = {
                    ProductTable.id.eq(product.id)
                }
            ){ pt->
                pt[ProductTable.price] = product.price
                pt[ProductTable.availability] = product.availability
            }
        }
    }

    suspend fun deleteProduct(id: String){
        dbQuery {
            ProductTable.deleteWhere {ProductTable.id eq id}
        }
    }
    private fun rowToProduct(row: ResultRow) = Product(
        id = row[ProductTable.id],
        title = row[ProductTable.title],
        description = row[ProductTable.description],
        category = row[ProductTable.category],
        price = row[ProductTable.price],
        availability = row[ProductTable.availability]
    )
}

