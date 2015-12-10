--Script which takes in a query and outputs the ranked answer passages
--Load the query json file to hdfs
--USAGE: pig -f Searcher.pig -param jsonFile='<json file path>' -param outFolder='<output folder for results>'


--load the libraries being used
REGISTER ./lib/piggybank.jar;--for XPath loading
REGISTER ./lib/UDFs.jar;--UDFs for query processing

--define all imported functions
DEFINE XPath org.apache.pig.piggybank.evaluation.xml.XPath();
DEFINE SatisfiesQuery pig.SatisfiesQuery();
DEFINE Score pig.ScoreGen();


--load data and parse from XML to rows
loadedXMLTable =  LOAD '../data/sample.xml' using org.apache.pig.piggybank.storage.XMLLoader('page') as (x:chararray);
parsedData = FOREACH loadedXMLTable GENERATE XPath(x, 'page/title') as title, XPath(x, 'page/text') as text, XPath(x, 'page/id') as id;

--Load Query from hdfs. Query is stored as a JSON file
queryTable= LOAD '$jsonFile'
    USING JsonLoader('text:chararray,
                      words: {(word:chararray, priority:double)},
                      headWord:chararray,
                      answerType:chararray') ;

----------TF-IDF Calculation from stored IDF values
--Retrieve the keywords against which we need to search
wordsInQuery= FOREACH queryTable GENERATE FLATTEN(queryTable.words.word) as WORD;
--make words lowercase for case insensitve search
wordsInQuery= foreach wordsInQuery GENERATE LOWER(WORD) as WORD;
--Load the pre-calculated IDF values from hdfs
fromHbase= LOAD '../data/idf.txt' USING PigStorage(',') AS (WORD:chararray,VAL:int);
--Generate a databag matching keywords with idf-words. IDF Databag(word,idfvalue)
IDF = join wordsInQuery by WORD left outer, fromHbase by WORD;

----------Search: filter articles which satisfy query
query = LOAD '$jsonFile' as chararray;
filtered = FILTER parsedData by SatisfiesQuery(title, text, query.$0);

----------Back to TF-IDF calculation
--Calculate Term Frequency of query words in filtered articles
wikiPigDataFlattened = FOREACH filtered GENERATE id,flatten(TOKENIZE((chararray)$1)) as WORD;
queryWordJoin=  join wordsInQuery by WORD left outer, wikiPigDataFlattened by WORD;
queryWordJoinGrouped= GROUP queryWordJoin by id;
counted = foreach queryWordJoinGrouped GENERATE FLATTEN($1.$0) as word, COUNT($1) as cnt,$0 as id;
TF= DISTINCT counted;

--Join TF(document id, word, count) Databag with IDF Databag(word, idfvalue)
joinDocs= join TF by $0 right outer, IDF by $0;
tfidfValuesByWordAndDoc = FOREACH joinDocs GENERATE $0 as word,$1 as count,$2 as id, $5 as idfval, $1*$5 as score;
tfidfValuesByDocID = GROUP tfidfValuesByWordAndDoc by id;


----------Ranking: Rank by TFIDF and Proximity Score
--Calculate TFIDF contribution of each document
tfidfValuesByDocID = FOREACH tfidfValuesByDocID GENERATE group as id, SUM(tfidfValuesByWordAndDoc.score) as tfidf_score;

--Calculate Proximity score for filtered articles
filteredArticlesWithProximityScore = FOREACH filtered GENERATE title as title, text as text, id as id, Score(title, text, query.$0) as proximity_score;

--Join scores
finalScoresOfArticles = Join filteredArticlesWithProximityScore by id, tfidfValuesByDocID by id;
finalScoresOfArticles = FOREACH finalScoresOfArticles GENERATE filteredArticlesWithProximityScore::id as id, title as title, text as text, (proximity_score+tfidf_score) as score;

--Sort articles by score
ordered = ORDER finalScoresOfArticles by score DESC;


----------Output the result
STORE ordered INTO '$outFolder' USING JsonStorage();
