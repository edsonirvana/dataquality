/* Criar tabela para armazenar os resultados da validação */
data DataQualityValidation;
  length ColumnName $100.;
  InvalidCount = .;
  TotalCount = .;
  Score = .;
  keep ColumnName InvalidCount TotalCount Score;
run;

/* Colunas e tabelas que serão validadas */
%let ColumnsToValidate = CustomerName Address PhoneNumber;
%let TablesToValidate = Customers;

/* Percorrer as colunas e tabelas para validação */
%let i = 1;
%let j = 1;

%do i = 1 %to %sysfunc(countw(&ColumnsToValidate));
  %let ColumnToValidate = %scan(&ColumnsToValidate,&i);

  %do j = 1 %to %sysfunc(countw(&TablesToValidate));
    %let TableToValidate = %scan(&TablesToValidate,&j);

    /* Realizar validação de qualidade de dados */
    proc sql;
      insert into DataQualityValidation (ColumnName, InvalidCount, TotalCount, Score)
      select "&ColumnToValidate" as ColumnName, count(*) as InvalidCount, count(&ColumnToValidate) as TotalCount, 
        1 - (count(*) / count(&ColumnToValidate)) as Score
      from &TableToValidate
      where &ColumnToValidate is null or &ColumnToValidate = "";
    quit;
  %end;
%end;

/* Exibir resultados da validação */
proc print data=DataQualityValidation;
run;

/* Limpar tabela */
proc sql;
  delete from DataQualityValidation;
quit;

