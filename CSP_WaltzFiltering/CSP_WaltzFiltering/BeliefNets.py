from pgmpy.models import BayesianModel
from pgmpy.factors.discrete import TabularCPD
from pgmpy.inference import VariableElimination
from pgmpy.sampling import BayesianModelSampling
from pgmpy.factors.discrete import State
import numpy as np


model = BayesianModel([('D', 'R'), ('M', 'R'), ('R', 'L'), ('M', 'E')])

cpd_d = TabularCPD(variable='D', variable_card=2, values=[[0.6, 0.4]])
cpd_m = TabularCPD(variable='M', variable_card=2, values=[[0.7, 0.3]])

cpd_r = TabularCPD(variable='R', variable_card=3, 
                   values=[[0.3, 0.05, 0.9,  0.5],
                           [0.4, 0.25, 0.08, 0.3],
                           [0.3, 0.7,  0.02, 0.2]],
                  evidence=['M', 'D'],
                  evidence_card=[2, 2])

cpd_l = TabularCPD(variable='L', variable_card=2, 
                   values=[[0.1, 0.4, 0.99],
                           [0.9, 0.6, 0.01]],
                   evidence=['R'],
                   evidence_card=[3])

cpd_e = TabularCPD(variable='E', variable_card=2,
                   values=[[0.95, 0.2],
                           [0.05, 0.8]],
                   evidence=['M'],
                   evidence_card=[2])

model.add_cpds(cpd_d, cpd_m, cpd_r, cpd_l, cpd_e)


#Joint prob
infer = VariableElimination(model)
probs = []
dict = {'M':1, 'D':0, 'R':1, 'E':1, 'L':0}
for var in model.node:
    temp = dict.copy()
    val = temp.pop(var)
    res = infer.query([var], evidence= temp)[var]
    probs.append(res.values[val])
probs = np.array(probs)
print("Joint probability:")
print(np.prod(probs))

#Exact
print("Exact Inference:")
print(infer.query(['L'])['L'])
print(infer.query(['L'], evidence= {'M':0})['L'])

#Approx
print("Approximate Inference:")
infer = BayesianModelSampling(model)
samples = infer.forward_sample(size=50000, return_type='recarray')
print("L_1 approximation with 50000 samples:")
print(np.mean(samples['L']))

evidence = [State(var='M', state=0)]
samples = infer.rejection_sample(evidence=evidence, size=50000, return_type='recarray')
print("L_1 given weak M approximation with 50000 samples:")
print(np.mean(samples['L']))