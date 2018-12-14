from constraint import *
problem = Problem()
problem.addVariables(range(15),range(3+1))
E1 = 0
E2 = 1
E3 = 2
E4 = 3
E5 = 4
E6 = 5
E7 = 6
E8 = 7
E9 = 8
E10 = 9
E11 = 10
E12 = 11
E13 = 12
E14 = 13
E15 = 14

class Junction:
    A = 0
    R = 1
    M = 2
    P = 3

class L(Junction):
    def __init__(self,x,y):
        problem.addConstraint(lambda a, b: (a == self.R and b == self.P) or
                                           (a == self.R and b == self.R) or
                                           (a == self.P and b == self.R) or
                                           (a == self.A and b == self.M) or
                                           (a == self.A and b == self.A) or
                                           (a == self.M and b == self.A), (x, y))
class Arrow(Junction):
    def __init__(self,x,y,z):
        problem.addConstraint(lambda a, b, c: (a == self.A and b == self.P and c == self.A) or
                                              (a == self.M and b == self.P and c == self.M) or
                                              (a == self.P and b == self.M and c == self.P), (x, y, z))
class Fork(Junction):
    def __init__(self,x,y,z):
        problem.addConstraint(lambda a, b, c: (a == self.A and b == self.A and c == self.M) or
                                              (a == self.M and b == self.A and c == self.A) or
                                              (a == self.A and b == self.M and c == self.A) or
                                              (a == self.P and b == self.P and c == self.P) or
                                              (a == self.M and b == self.M and c == self.M), (x, y, z))

J2 = L(E1, E2)
J6 = L(E5, E6)
J8 = L(E7, E8)
J4 = Fork(E4, E3, E14)
J9 = Fork(E9, E11, E10)
J11 = Fork(E12, E13, E15)
J1 = Arrow(E8, E9, E1)
J3 = Arrow(E2, E10, E3)
J5 = Arrow(E4, E15, E5)
J7 = Arrow(E6, E13, E7)
J10 = Arrow(E12, E14, E11)

sols = problem.getSolutions()
for i in range(len(sols)):
    for j in range(len(sols[i])):
        print("[%d" %(sols[i][j]), end=']')
    print(",")