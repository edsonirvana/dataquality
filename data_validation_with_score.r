library(RPostgreSQL)

# Colunas e tabelas que serão validadas
columns_to_validate <- c("CustomerName", "Address", "PhoneNumber")
tables_to_validate <- c("Customers")

# Criar conexão com o banco de dados
drv <- dbDriver("PostgreSQL")
con <- dbConnect(drv, dbname="mydatabase", host="localhost", port="5432", user="username", password="password")

# Criar tabela para armazenar os resultados da validação
dbSendQuery(con, "CREATE TABLE DataQualityValidation (ColumnName VARCHAR(100), InvalidCount INT, TotalCount INT, Score DOUBLE PRECISION)")

# Percorrer as colunas e tabelas para validação
for (column_to_validate in columns_to_validate) {
  for (table_to_validate in tables_to_validate) {
    # Realizar validação de qualidade de dados
    select_query <- paste0("SELECT COUNT(*) AS TotalCount, COUNT(", column_to_validate, ") AS ValidCount FROM ", table_to_validate, " WHERE ", column_to_validate, " IS NOT NULL AND ", column_to_validate, " != ''")
    result_set <- dbSendQuery(con, select_query)
    result <- dbFetch(result_set)
    total_count <- result$TotalCount[1]
    valid_count <- result$ValidCount[1]
    invalid_count <- total_count - valid_count
    score <- 1 - (invalid_count / total_count)

    # Inserir resultados da validação na tabela
    insert_query <- paste0("INSERT INTO DataQualityValidation (ColumnName, InvalidCount, TotalCount, Score) VALUES ('", column_to_validate, "', ", invalid_count, ", ", total_count, ", ", score, ")")
    dbSendQuery(con, insert_query)
  }
}

# Exibir resultados da validação
result_set <- dbSendQuery(con, "SELECT * FROM DataQualityValidation")
result <- dbFetch(result_set)
print(result)

# Fechar conexão e limpar tabela
dbSendQuery(con, "DROP TABLE DataQualityValidation")
dbDisconnect(con)

