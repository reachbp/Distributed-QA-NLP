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
    
if __name__=="__main__":
	inputString = open(sys.argv[1], "r").read()
	print textToJson(inputString)
