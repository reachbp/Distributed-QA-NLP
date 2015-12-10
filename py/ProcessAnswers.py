'''
Created on Dec 5, 2015

@author: Saurabh
'''

import os
import nltk
import gensim
from nltk.tree import Tree
import sys
from traits.trait_types import self
# from nltk.collocations import *
# from nltk.book import *
# nltk.download('all')

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
    print('Please enter your question:')
    question = raw_input("Q: ")
            # print question
    question = unicode(question, "utf-8")
    q_token = nltk.word_tokenize(question)
    q_pos = nltk.pos_tag(q_token)
    q_ne = nltk.ne_chunk(q_pos)           
    entities, entity_type = get_continuous_chunks(q_ne)
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
                    
    f = open('004.txt', 'r')
    for line in f:
        #print line
        sentences = line.split(".")
        
        for sent in sentences:
            
        
            sent = unicode(sent, "utf-8")
            
            a_token = nltk.word_tokenize(sent)
            a_pos = nltk.pos_tag(a_token)
            a_ne = nltk.ne_chunk(a_pos)           
            a_entities, a_entity_type = get_continuous_chunks(a_ne)
            if check == 2:
                if head_noun in a_entities:
                    print sent
                    exit()
                else:
                    var = 0
                    for i in nouns:
                        if i not in a_token:
                            var = 1
                            break
                    if var == 0:
                        print sent
                        exit()
            else:
                var = 0
                for i in nouns:
                    if i not in a_token:
                        var = 1
                        break
                if var == 0 and a_entities:
                    if ans_entity in a_entity_type:
                        print sent
                        exit()
            #print "not in this line"
           # print a_token
            #print a_pos
            #print a_ne
            #print entities
            #print entity_type
        

if __name__ == '__main__':
    main()
