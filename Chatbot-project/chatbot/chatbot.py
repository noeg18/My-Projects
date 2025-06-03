# imports for NLP
import nltk
from nltk.stem.lancaster import LancasterStemmer
stemmer = LancasterStemmer()

# Tensorflow imports
import numpy as np
import tflearn
import tensorflow as tf
import random

# load in our intents file
import json
with open('intents.json') as json_file:
    intents = json.load(json_file)


words = []
classes = []
documents = []

words_to_ignore = ['?']

# loop through all sentences in patterns in intents

for intent in intents['intents']:
    for pattern in intent['patterns']:
        # tokenise every word
        tokenised_word = nltk.word_tokenize(pattern)
        # add to tokenised word to list
        words.extend(w)
        # add to documents
        documents.append((tokenised_word, intent['tag']))
        # add to clasess
        if intent['tag'] not in classes:
            classes.append(intent['tag'])

# stem and lower every word
words = [stemmer.stem(tokenised_words.lower()) for tokenised_words in words if tokenised_word not in words_to_ignore]
# remove duplicates
words = sorted(list(set(words)))
classes = sorted(list(set(classes)))

#print (len(documents), "documents")
#print(len(classes), "classes", classes)

# create training data

training = []
output = []
empty_output = [0] * len(classes)

# bag-of-words for each sentence 
for doc in documents:
    #initialise bag-of-words
    bag = []
    #list of tokenised words for every pattern
    pattern_words = doc[0]
    # stem every word
    pattern_words = [stemmer.stem(word.lower()) for word in pattern_word]
    # create array for bag-of-words
    for w in words:
        bag.append(1) if tokenised_word in pattern_words else bag.append(0)
    
    output_row = list(empty_output)
    output_row[classes.index(doc[1])] = 1
    
    training.append([bag, output_row])

# shuffle data to mitigate bias
random.shuffle(training)
# turn into numPy array
training = np.array(training)

# create training lists, one for inputs and one for outputs
train_x = list(training[:,0])
train_y = list(training[:,1])

