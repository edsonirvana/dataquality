import pandas as pd

# Colunas e tabelas que serão validadas
columns_to_validate = ['CustomerName', 'Address', 'PhoneNumber']
tables_to_validate = ['Customers']

# Criar dataframe para armazenar os resultados da validação
data_quality_validation = pd.DataFrame(columns=['ColumnName', 'InvalidCount', 'TotalCount', 'Score'])

# Percorrer as colunas e tabelas para validação
for column_to_validate in columns_to_validate:
    for table_to_validate in tables_to_validate:
        # Realizar validação de qualidade de dados
        df = pd.read_sql(f"SELECT COUNT(*) AS TotalCount, COUNT({column_to_validate}) AS ValidCount FROM {table_to_validate} WHERE {column_to_validate} IS NOT NULL AND {column_to_validate} != ''", connection)
        invalid_count = df['TotalCount'] - df['ValidCount']
        score = 1 - (invalid_count / df['TotalCount'])
        
        # Armazenar resultados da validação no dataframe
        data_quality_validation = data_quality_validation.append({
            'ColumnName': column_to_validate,
            'InvalidCount': invalid_count,
            'TotalCount': df['TotalCount'],
            'Score': score
        }, ignore_index=True)

# Exibir resultados da validação
print(data_quality_validation)

# Limpar dataframe
data_quality_validation = data_quality_validation.iloc[0:0]

