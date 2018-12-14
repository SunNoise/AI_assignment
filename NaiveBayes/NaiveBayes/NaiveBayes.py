class MyBayesClassifier():
    def __init__(self, smooth):
        self._smooth = smooth
        self._feat_prob = []
        self._class_prob = []
        self._Ncls = []
        self._Nfeat = []

    def train(self, X, y):
        self._Ncls.append(np.unique(y).size)
        self._Nfeat.append(X[0].size)
        Ccls = [0 for z in range(self._Ncls[0])]
        Cfeat = [[0 for col in range(X[0].size)] for row in range(self._Ncls[0])]

        for i, item in enumerate(y):
            #print("%i of %i" % (i, y.size))
            Ccls[item] += 1
            for j in range(X[i].size):
                Cfeat[item][j] += X[i][j]

        for f, item in enumerate(Ccls):
            num = (item + self._smooth)
            den = (y.size + (self._Ncls[0] * self._smooth))
            self._class_prob.append((num / float(den)))

            a = []
            den = (item + (2 * self._smooth))  # 2 because binomial
            for j in range(X[0].size):
                num = (Cfeat[f][j] + self._smooth)
                a.append((num / float(den)))
            self._feat_prob.append(a)
        print("Training Done")
        return

    def predict(self, X):
        import sys
        y = []
        for z, x in enumerate(X):
            #print("%i of %i" % (z, X.shape[0]))
            currentMin = sys.float_info.max
            for i in range(self._Ncls[0]):
                current = -np.log(self._class_prob[i])
                for j in range(self._Nfeat[0]):
                    if (x[j]) == 0:
                        current -= np.log(1 - self._feat_prob[i][j])
                    else:
                        current -= np.log(self._feat_prob[i][j])
                if currentMin > current:
                    category = i
                    currentMin = current

            y.append(category)
        return y

def loadData():
    x = []
    y = []
    for k in range(0,10):
        for j in range(2):
            label = ''
            if(j == 1):
                label = 'pos'
            else:
                label = 'neg'
            for i in range(0,100):
                currentList = [0] * 8
                filepath = label+'/cv'+str(k)+"{0:0=2d}".format(i)+'_*.txt'
                txt = glob.glob(filepath)
                for textfile in txt:
                    with open(textfile, 'r') as currentFile:
                        words = currentFile.read().lower()
                        if 'awful' in words:
                            currentList[0] = 1
                        if 'bad' in words:
                            currentList[1] = 1
                        if 'boring' in words:
                            currentList[2] = 1
                        if 'dull' in words:
                            currentList[3] = 1
                        if 'effective' in words:
                            currentList[4] = 1
                        if 'enjoyable' in words:
                            currentList[5] = 1
                        if 'great' in words:
                            currentList[6] = 1
                        if 'hilarious' in words:
                            currentList[7] = 1
                x.append(currentList)
                y.append(j)
    return x, y

def run(X_train,y_train,X_test,y_test):
    alpha = 1
    clf = MyBayesClassifier(alpha)
    clf.train(X_train,y_train)
    y_pred = clf.predict(X_test)

    acc = np.mean((y_test-y_pred)==0)
    #Confusion matrix
    fpos = np.sum((y_test-y_pred)==-1)
    fneg = np.sum((y_test-y_pred)==1)
    tpos = np.sum((y_test+y_pred)==2)
    tneg = np.sum((y_test+y_pred)==0)
    return fpos, fneg, tpos, tneg, acc


import glob
import numpy as np
import pickle
import random

try:
    x = pickle.load(open('x.pkl','rb'))
    y = pickle.load(open('y.pkl','rb'))
except:
    x,y = loadData()
    pickle.dump(x, open('x.pkl', 'wb'))
    pickle.dump(y, open('y.pkl', 'wb'))
x = np.array(x)
y = np.array(y)
#fp,fn,tp,tn,acc = run(x,y,x,y)
#print("Accuracy = %f" %(acc))
#print("Confusion Matrix")
#print("True positives = %d" %(tp))
#print("False positives = %d" %(fp))
#print("True negatives = %d" %(tn))
#print("False negatives = %d" %(fn))

##cross val
accs = []
numFolds = 10
foldSize = int(len(x)/numFolds)
xFolds = []
yFolds = []
for i in range(numFolds):
    xFolds.append(x[foldSize*i:foldSize*(i+1)])
    yFolds.append(y[foldSize*i:foldSize*(i+1)])
xFolds = np.array(xFolds)
yFolds = np.array(yFolds)
foldsToTest = np.arange(numFolds)
for fold in foldsToTest:
    x_tr = xFolds[np.arange(len(xFolds))!=fold].reshape(-1,xFolds.shape[2])
    x_va = xFolds[fold]
    y_tr = yFolds[np.arange(len(yFolds))!=fold].reshape(-1)
    y_va = yFolds[fold]
    fp, fn, tp, tn, acc = run(x_tr,y_tr,x_va,y_va)
    accs.append(acc)
print("Average Accuracy = %f" %(np.mean(np.array(accs))))

#Generative model
def generateOneWord(probs):
    r = random.uniform(0, np.sum(probs))
    cumulativeProb = [probs[0]]
    output = []
    if(r < cumulativeProb[0]):
        output.append("awful")
    elif(r < accumulateProb(cumulativeProb,probs[1])):
        output.append("bad")
    elif(r < accumulateProb(cumulativeProb,probs[2])):
        output.append("boring")
    elif(r < accumulateProb(cumulativeProb,probs[3])):
        output.append("dull")
    elif(r < accumulateProb(cumulativeProb,probs[4])):
        output.append("effective")
    elif(r < accumulateProb(cumulativeProb,probs[5])):
        output.append("enjoyable")
    elif(r < accumulateProb(cumulativeProb,probs[6])):
        output.append("great")
    elif(r < accumulateProb(cumulativeProb,probs[7])):
        output.append("hilarious")
    return output

def accumulateProb(cumulative, prob):
    cumulative[0] += prob
    return cumulative[0]

def normalize(x):
    return [float(i)/sum(x) for i in x]

def generate(probs):
    words = ["awful", "bad", "boring", "dull", "effective", "enjoyable", "great", "hilarious"]
    output = []
    p = np.sum(probs)
    for i, word in enumerate(words):
        r = random.uniform(0, p)
        if(r < probs[i]):
            output.append(word)
    return output

clf = MyBayesClassifier(1)
clf.train(x,y)
posProbs = clf._feat_prob[1]
negProbs = clf._feat_prob[0]
#normalizedPos = normalize(posProbs)
#normalizedNeg = normalize(negProbs)

print("Generated Positive Reviews: ")
for i in range(5):
    print(generate(posProbs))
print("Generated Negative Reviews: ")
for i in range(5):
    print(generate(negProbs))