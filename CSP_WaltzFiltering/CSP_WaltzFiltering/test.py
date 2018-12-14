from pgmpy.models import BayesianModel
from pgmpy.factors.discrete import TabularCPD

# Defining the network structure
model = BayesianModel([('A', 'C'), ('B', 'C')])

# Defining the CPDs:
cpd_p = TabularCPD('A', 2, [[0.99, 0.01]])
cpd_a = TabularCPD('B', 2, [[0.9, 0.1]])
cpd_t = TabularCPD('C', 2, [[0.9, 0.5, 0.4, 0.1], 
                                  [0.1, 0.5, 0.6, 0.9]],
                  evidence=['A', 'B'], evidence_card=[2, 2])

# Associating the CPDs with the network structure.
model.add_cpds(cpd_p, cpd_a, cpd_t)

# Some other methods
model.get_cpds()

from pgmpy.inference import VariableElimination

print('P(B|A=1,C=1)')
infer = VariableElimination(model)
posterior_p = infer.query(['B'], evidence={'A': 1, 'C': 1})
print(posterior_p['B'])

print('P(B|C=1)')
posterior_p = infer.query(['B'], evidence={'C': 1})
print(posterior_p['B'])

print('probs')
posterior_p = infer.query(['B','C','A'])
for entry in posterior_p:
    print(posterior_p[entry])