from nltk.stem.porter import PorterStemmer
import sys

if len(sys.argv) < 2:
    print(" ")
    sys.exit()

word = sys.argv[1]

stemmer = PorterStemmer()
print(stemmer.stem(word))
