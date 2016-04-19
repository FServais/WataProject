from nltk.stem.wordnet import WordNetLemmatizer

import sys

if len(sys.argv) < 2:
    print(" ")
    sys.exit()

word = sys.argv[1]

lmtzr = WordNetLemmatizer()
print(lmtzr.lemmatize(word))
