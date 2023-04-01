-- Criar tabela temporária para armazenar os resultados da validação
CREATE TABLE #DataQualityValidation 
(
    ColumnName VARCHAR(100),
    InvalidCount INT,
    TotalCount INT,
    Score FLOAT
)

-- Colunas e tabelas que serão validadas
DECLARE @ColumnsToValidate VARCHAR(200) = 'CustomerName, Address, PhoneNumber'
DECLARE @TablesToValidate VARCHAR(200) = 'Customers'

-- Percorrer as colunas e tabelas para validação
DECLARE @ColumnToValidate VARCHAR(100)
DECLARE @TableToValidate VARCHAR(100)

WHILE LEN(@ColumnsToValidate) > 0
BEGIN
    SET @ColumnToValidate = SUBSTRING(@ColumnsToValidate, 1, 
        CASE CHARINDEX(',', @ColumnsToValidate) 
             WHEN 0 THEN LEN(@ColumnsToValidate) 
             ELSE CHARINDEX(',', @ColumnsToValidate) - 1 
        END)
    SET @ColumnsToValidate = SUBSTRING(@ColumnsToValidate, 
        CASE CHARINDEX(',', @ColumnsToValidate) 
             WHEN 0 THEN LEN(@ColumnsToValidate) 
             ELSE CHARINDEX(',', @ColumnsToValidate) + 1 
        END, LEN(@ColumnsToValidate))

    WHILE LEN(@TablesToValidate) > 0
    BEGIN
        SET @TableToValidate = SUBSTRING(@TablesToValidate, 1, 
            CASE CHARINDEX(',', @TablesToValidate) 
                 WHEN 0 THEN LEN(@TablesToValidate) 
                 ELSE CHARINDEX(',', @TablesToValidate) - 1 
            END)
        SET @TablesToValidate = SUBSTRING(@TablesToValidate, 
            CASE CHARINDEX(',', @TablesToValidate) 
                 WHEN 0 THEN LEN(@TablesToValidate) 
                 ELSE CHARINDEX(',', @TablesToValidate) + 1 
            END, LEN(@TablesToValidate))

        -- Realizar validação de qualidade de dados
        DECLARE @SQL VARCHAR(MAX)
        SET @SQL = 'INSERT INTO #DataQualityValidation (ColumnName, InvalidCount, TotalCount, Score)
                    SELECT ''' + @ColumnToValidate + ''', COUNT(*), COUNT(' + @ColumnToValidate + '), 
                        1 - (CAST(COUNT(*) AS FLOAT) / CAST(COUNT(' + @ColumnToValidate + ') AS FLOAT))
                    FROM ' + @TableToValidate + ' WHERE ' + @ColumnToValidate + ' IS NULL OR ' + @ColumnToValidate + ' = '''''
        EXEC (@SQL)
    END
END

-- Exibir resultados da validação
SELECT * FROM #DataQualityValidation

-- Limpar tabela temporária
DROP TABLE #DataQualityValidation

