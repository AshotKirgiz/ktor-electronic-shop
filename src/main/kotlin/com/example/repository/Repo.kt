package com.example.repository

import com.example.data.model.Product
import com.example.data.model.Rating
import com.example.data.model.User
import com.example.data.table.ProductTable
import com.example.data.table.RatingTable
import com.example.data.table.UserTable
import com.example.plugins.db
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

    suspend fun getUser(email: String): List<User?> = dbQuery {
        UserTable.select { UserTable.email.eq(email) }
            .map { rowToUser(it)}
    }

    suspend fun deleteUser(email: String): Boolean = dbQuery {
        UserTable.deleteWhere { UserTable.email.eq(email) } > 0
    }
    private fun rowToUser(row: ResultRow?):User?{
        if(row == null){
            return null
        }

        return User(
            id = row[UserTable.id],
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
    suspend fun addRating(rating: Rating){
        dbQuery {
            RatingTable.insert { rt->
                rt[RatingTable.productid] = rating.productid
                rt[RatingTable.name] = rating.userName
                rt[RatingTable.grade] = rating.grade
                rt[RatingTable.title] = rating.title
                rt[RatingTable.description] = rating.description
            }
        }
    }

    suspend fun checkRating(name: String, productid: String): Boolean {
        try {
            return dbQuery {
                RatingTable.select { (RatingTable.name.eq(name) and RatingTable.productid.eq(productid)) }
                    .count() > 0
            }
        } catch (e: Exception) {
            return false
        }
    }
    suspend fun getRating(productid: String): List<Rating?> = dbQuery {
        RatingTable.select{
            RatingTable.productid.eq(productid)
        }.map { rowToRating(it) }
    }

    suspend fun getUserRating(name: String): List<Rating?> = dbQuery {
        RatingTable.select { RatingTable.name.eq(name) }
            .map { rowToRating(it)}
    }

    suspend fun updateRating(rating: Rating){
        dbQuery {
            RatingTable.update(
                where = {
                    RatingTable.gradeid.eq(rating.gradeid)
                }
            ){ rt->
                rt[RatingTable.grade] = rating.grade
                rt[RatingTable.title] = rating.title
                rt[RatingTable.description] = rating.description
            }
        }
    }

    suspend fun deleteRating(gradeid:Int){
        dbQuery {
            RatingTable.deleteWhere { RatingTable.gradeid.eq(gradeid) }
        }
    }
    private fun rowToRating(row: ResultRow) = Rating(
        productid = row[RatingTable.productid],
        gradeid = row[RatingTable.gradeid],
        userName = row[RatingTable.name],
        grade = row[RatingTable.grade],
        title = row[RatingTable.title],
        description = row[RatingTable.description]
    )
}

