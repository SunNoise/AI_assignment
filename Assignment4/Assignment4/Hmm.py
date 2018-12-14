from hmmlearn import hmm
import numpy as np

startprob = np.array([1.0, 0.0])
transmat = np.array([[0.7, 0.3],
                     [0.5, 0.5]])
emission_probs = np.array([[0.2, 0.1, 0.7], 
                           [0.3, 0.6, 0.1]])

model = hmm.MultinomialHMM(n_components=2)

model.startprob_ = startprob
model.transmat_ = transmat
model.emissionprob_ = emission_probs

# sample the model - X is the observed values (Dribble, Pass & Shoot sequence) 
# and Z is the "hidden" states (Healthy & Injured sequence) 
samples = 300
iters = 10000
print("With %d samples and %d iterations:" %(samples, iters))
X, Z = model.sample(samples)

# Make an HMM instance and execute fit
newModel = hmm.MultinomialHMM(n_components=2, n_iter=iters).fit(X)

print("Original Model:")
print("Transition matrix")
print(transmat)
print("Emission probabilities")
print(emission_probs)
print("---------------------------------")
print("Fitted Model:")
print("Transition matrix")
print(newModel.transmat_)
print("Emission probabilities")
print(newModel.emissionprob_)
print("---------------------------------")
# Predict the optimal sequence of internal hidden state
hidden_states = newModel.predict(X)
sum = np.subtract(hidden_states,Z)
sum = np.absolute(sum[sum!=0])
print("Errors:")
print("%d out of %d" % (np.sum(sum),len(Z)))