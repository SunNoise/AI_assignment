from sklearn.naive_bayes import MultinomialNB
from sklearn.naive_bayes import BernoulliNB
from os import listdir
from string import punctuation
from collections import Counter
import numpy as np

def load_doc(filename):
    file = open(filename, 'r')
    text = file.read()
    file.close()
    return text

dict = ["awful", "bad", "boring", "dull", "effective", "enjoyable", "great", "hilarious"]
def clean_doc(doc):
    tokens = doc.split()
    table = str.maketrans('', '', punctuation)
    tokens = [w.translate(table) for w in tokens]
    tokens = [word for word in tokens if word in dict]
    return tokens

def process_docs(directory, start, count):
    docs = []
    y = []
    for filename in listdir(directory):
        if(not filename.startswith(start)):
            continue

        path = directory + '/' + filename
        doc = load_doc(path)
        tokens = clean_doc(doc)
        vocab = Counter(dict)
        vocab.subtract(dict)
        vocab.update(tokens)
        vals = np.array(list(vocab.values()))
        if(not count):
            vals[vals > 1] = 1
        docs.append(vals)
        if(directory.endswith('neg')):
            y.append(0)
        else:
            y.append(1)
    return docs, y

def run(X_train,y_train,X_test,y_test, clf):
    clf.fit(X_train,y_train)
    y_pred = clf.predict(X_test)
    acc = np.mean((y_test-y_pred)==0)
    #Confusion matrix
    fpos = np.sum((y_test-y_pred)==-1)
    fneg = np.sum((y_test-y_pred)==1)
    tpos = np.sum((y_test+y_pred)==2)
    tneg = np.sum((y_test+y_pred)==0)
    return fpos, fneg, tpos, tneg, acc

#Bernoulli
print("Bernoulli NB:")
multi = False
data = []
y = []
for i in range(10):
    res, res2 = process_docs('data/neg', "cv%d" %(i), multi)
    data += [res]
    y += [res2]
    res, res2 = process_docs('data/pos', "cv%d" %(i), multi)
    data[i].extend(res)
    y[i].extend(res2)
data = np.array(data)
y = np.array(y)

#Full data for training and testing
x = data.reshape(-1,data.shape[2])
lbls = y.reshape(-1)
fp,fn,tp,tn,acc = run(x,lbls,x,lbls, BernoulliNB())
print("Accuracy = %f" %(acc))
print("Confusion Matrix")
print("True positives = %d" %(tp))
print("False positives = %d" %(fp))
print("True negatives = %d" %(tn))
print("False negatives = %d" %(fn))
#cross val
accs = []
foldsToTest = np.arange(len(data))
for fold in foldsToTest:
    x_tr = data[np.arange(len(data))!=fold].reshape(-1,data.shape[2])
    x_va = data[fold]
    y_tr = y[np.arange(len(y))!=fold].reshape(-1)
    y_va = y[fold]
    fp, fn, tp, tn, acc = run(x_tr,y_tr,x_va,y_va, BernoulliNB())
    accs.append(acc)
print("Average Accuracy = %f" %(np.mean(np.array(accs))))

#Multinomial
print("Multinomial NB:")
multi = True
data = []
y = []
for i in range(10):
    res, res2 = process_docs('data/neg', "cv%d" %(i), multi)
    data += [res]
    y += [res2]
    res, res2 = process_docs('data/pos', "cv%d" %(i), multi)
    data[i].extend(res)
    y[i].extend(res2)
data = np.array(data)
y = np.array(y)

#Full data for training and testing
x = data.reshape(-1,data.shape[2])
lbls = y.reshape(-1)
fp,fn,tp,tn,acc = run(x,lbls,x,lbls, MultinomialNB())
print("Accuracy = %f" %(acc))
print("Confusion Matrix")
print("True positives = %d" %(tp))
print("False positives = %d" %(fp))
print("True negatives = %d" %(tn))
print("False negatives = %d" %(fn))
#cross val
accs = []
foldsToTest = np.arange(len(data))
for fold in foldsToTest:
    x_tr = data[np.arange(len(data))!=fold].reshape(-1,data.shape[2])
    x_va = data[fold]
    y_tr = y[np.arange(len(y))!=fold].reshape(-1)
    y_va = y[fold]
    fp, fn, tp, tn, acc = run(x_tr,y_tr,x_va,y_va, MultinomialNB())
    accs.append(acc)
print("Average Accuracy = %f" %(np.mean(np.array(accs))))