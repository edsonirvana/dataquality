import java.sql.DriverManager
import java.sql.Connection

object DataQualityValidation {
  def main(args: Array[String]): Unit = {
    // Colunas e tabelas que serão validadas
    val columnsToValidate = List("CustomerName", "Address", "PhoneNumber")
    val tablesToValidate = List("Customers")

    // Criar conexão com o banco de dados
    val driver = "org.postgresql.Driver"
    val url = "jdbc:postgresql://localhost:5432/mydatabase"
    val username = "username"
    val password = "password"
    var connection:Connection = null
    Class.forName(driver)
    connection = DriverManager.getConnection(url, username, password)

    // Criar tabela para armazenar os resultados da validação
    val statement = connection.createStatement()
    val createTableQuery = "CREATE TABLE DataQualityValidation (ColumnName VARCHAR(100), InvalidCount INT, TotalCount INT, Score DOUBLE PRECISION)"
    statement.execute(createTableQuery)

    // Percorrer as colunas e tabelas para validação
    for (columnToValidate <- columnsToValidate) {
      for (tableToValidate <- tablesToValidate) {
        // Realizar validação de qualidade de dados
        val selectQuery = s"SELECT COUNT(*) AS TotalCount, COUNT(${columnToValidate}) AS ValidCount FROM ${tableToValidate} WHERE ${columnToValidate} IS NOT NULL AND ${columnToValidate} != ''"
        val resultSet = statement.executeQuery(selectQuery)
        resultSet.next()
        val totalCount = resultSet.getInt("TotalCount")
        val validCount = resultSet.getInt("ValidCount")
        val invalidCount = totalCount - validCount
        val score = 1 - (invalidCount.toDouble / totalCount.toDouble)

        // Inserir resultados da validação na tabela
        val insertQuery = s"INSERT INTO DataQualityValidation (ColumnName, InvalidCount, TotalCount, Score) VALUES ('${columnToValidate}', ${invalidCount}, ${totalCount}, ${score})"
        statement.execute(insertQuery)
      }
    }

    // Exibir resultados da validação
    val selectQuery = "SELECT * FROM DataQualityValidation"
    val resultSet = statement.executeQuery(selectQuery)
    while (resultSet.next()) {
      val columnName = resultSet.getString("ColumnName")
      val invalidCount = resultSet.getInt("InvalidCount")
      val totalCount = resultSet.getInt("TotalCount")
      val score = resultSet.getDouble("Score")
      println(s"${columnName}: InvalidCount=${invalidCount}, TotalCount=${totalCount}, Score=${score}")
    }

    // Fechar conexão e limpar tabela
    statement.execute("DROP TABLE DataQualityValidation")
    connection.close()
  }
}

