package es.uva.sg.psm.planificadorfinanciero.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TransactionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addTransaction(transactionEntity: Transaction)

    @Query("SELECT * FROM `Transaction`")
    abstract fun getAllTransactions(): Flow<List<Transaction>>

    @Update
    abstract suspend fun updateTransaction(transactionEntity: Transaction)

    @Delete
    abstract suspend fun deleteTransaction(transactionEntity: Transaction)

    @Query("SELECT * FROM `Transaction` WHERE id=:id")
    abstract fun getTransactionById(id: Long): Flow<Transaction>

    @Query("SELECT COUNT(*) FROM `Transaction` WHERE `transaction-category` = :categoryId")
    abstract suspend fun getTransactionCountForCategory(categoryId: Long): Int

    @Query("""
        SELECT * FROM `Transaction` 
        WHERE `transaction-category` = :categoryId
        AND strftime('%Y', datetime(`transaction-date`/1000, 'unixepoch')) = :year
        AND strftime('%m', datetime(`transaction-date`/1000, 'unixepoch')) = :month
        ORDER BY `transaction-date` DESC
    """)
    abstract fun getTransactionsByCategoryAndDate(
        categoryId: Long, year: String, month: String
    ): Flow<List<Transaction>>

}