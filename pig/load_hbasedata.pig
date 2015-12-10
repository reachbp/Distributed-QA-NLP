
raw_data = LOAD 'hdfs:/bharathi/idf.txt' USING PigStorage(',') AS (
           word:chararray,
           val:chararray);
new_data = FOREACH raw_data GENERATE word as w,word,val;
dump new_data;
STORE new_data INTO 'hbase://idf' USING org.apache.pig.backend.hadoop.hbase.HBaseStorage('idf_data:word,idf_data:val');



fromHbase= LOAD 'hbase://idf'
      USING org.apache.pig.backend.hadoop.hbase.HBaseStorage('idf_data:word,idf_data:val', '-loadKey false')
      AS (WORD:chararray,VAL:chararray);
