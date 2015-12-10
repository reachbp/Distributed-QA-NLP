'''
Created on Nov 28, 2015

@author: Saurabh
'''
import os
import nltk
import gensim
from nltk.tree import Tree
# from nltk.collocations import *
# from nltk.book import *
# nltk.download('all')

import json
import sys

noun_priority = 7.0
adj_priority = 2.0
ne_priority = 10.0

def textToJson(inputString):
    result = {}
    parts = inputString.split("\n")
    #text
    result["text"] = parts[0][3:]
    #words
    result["words"] = []
    ##### nouns
    nouns = eval(parts[4])
    for noun in nouns:
        word = {}
        word["word"] = noun
        word["priority"] = noun_priority
        
        result["words"].append(word)

    ##### Adjectives
    adjs = eval(parts[6])
    for adj in adjs:
        word = {}
        word["word"] = adj
        word["priority"] = adj_priority
        
        result["words"].append(word)
        
    ##### Named Entities
    #nes = eval(parts[8])
    #for ne in nes:
    #    word = {}
    #    word["word"] = ne
    #    word["priority"] = ne_priority
    #    
    #    result["words"].append(word)
        
    # answerType
    result["answerType"] = parts[12]
    
    return json.dumps(result)

def get_continuous_chunks(chunked):
    prev = None
    continuous_chunk = []
    current_chunk = []
    entity_type = []
    for i in chunked:
        if type(i) == Tree:
            entity_type.append(i.label())
            current_chunk.append(" ".join([token for token, pos in i.leaves()]))
        elif current_chunk:
            named_entity = " ".join(current_chunk)
            if named_entity not in continuous_chunk:
                continuous_chunk.append(named_entity)
                current_chunk = []
            else:
                continue
    return continuous_chunk, entity_type

def main():
    # model = gensim.models.word2vec.Word2Vec.load_word2vec_format(os.path.join(os.path.dirname(__file__), 'GoogleNews-vectors-negative300.bin'), binary=True)
    # print(model.most_similar('rose'))
    
    
    while True:
        print("What would you like to do?")
        print("1. Ask a question?")
        print("2. Exit")
        option = int(input())
        if option == 1:
            #print('Please enter your question:')
            question = raw_input("Q: ")
            # print question
            q_token = nltk.word_tokenize(question)
            q_pos = nltk.pos_tag(q_token)
            print(q_pos)
            q_ne = nltk.ne_chunk(q_pos)    
            #print(q_ne)       
            entities, entity_type = get_continuous_chunks(q_ne)
            #print(entities)
            nouns = []
            adjectives = []
            first = 0
            ans_entity = ''
            head_noun = ''
            check = 0
            for word, pos in q_pos:
                if first == 0:
                    first = 1
                    if word.lower() == 'who':
                        if 'PERSON' in entity_type:
                            ans_entity = ''
                            head_noun = entities[0]
                            check = 2
                        else:
                            ans_entity = 'PERSON'
                    elif word.lower() == 'where':
                        ans_entity = 'GPE'
                    elif word.lower() == 'what':
                        ans_entity = ''
                        check = 1;
                elif pos == 'NNP' or pos == 'NNPS' or pos == 'NN' or pos == 'NNS':
                    nouns.append(word)
                    if check == 1:
                        head_noun = word
                        check = 2
                elif pos == 'JJ' or pos == 'JJR' or pos == 'JJS':
                    adjectives.append(word)
            output = 'Q: '+question+'\n' 
            output = output + str(q_pos) + '\n' +"The keywords are:" +'\n'+ 'Noun:' + '\n'
#             print("The keywords are:")
#             print("Noun:")
#             print(nouns)
            output = output + str(nouns) + '\n' + "Adjectives:" + '\n'
#             print("Adjectives:")
#             print(adjectives)
            output = output + str(adjectives) + '\n' + "entities:" + '\n'
#             print("entities:")
#             print(entities)
            output = output + str(entities) + '\n' + "entity types:" + '\n'
#             print("entity types:")
#             print(entity_type)
            if check == 2:
                output = output + str(entity_type) + '\n' + "Head noun:" + '\n'
#                 print("Head noun:")
#                 print(head_noun)
                output = output + str(head_noun)
            else:
                output = output + str(entity_type) + '\n' + "answer entity:" + '\n'
#                 print("answer entity:")
#                 print(ans_entity)
                output = output + str(ans_entity)
            print
            #print output
            queryJson = textToJson(output)
            print queryJson
        else:
            exit()
        
    
        


if __name__ == '__main__':
    main()
