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
