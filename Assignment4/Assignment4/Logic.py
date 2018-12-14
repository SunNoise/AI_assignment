from kanren import Relation, facts, run, conde, var

print("Using logic programming:")
parent = Relation()

facts(parent, ('Darth Vader', 'Luke Skywalker'),
              ('Darth Vader', 'Leia Organa'),
              ('Leia Organa', 'Kylo Ren'),
              ('Han Solo', 'Kylo Ren'))

q = var()
# Who is the parent of Luke Skywalker?
print((run(0, q, parent(q, 'Luke Skywalker'))))

# Who  are the children of Darth Vader?
print((run(0, q, parent('Darth Vader', q))))

def grandparent(gp, child):
    p = var()
    return conde((parent(gp, p), parent(p, child)))

# Who is the grandparent of Kylo Ren?
print((run(0, q, grandparent(q, 'Kylo Ren'))))

#Question 4
print("Using standard python:")
class Member(object):
    def __init__(self, name):
        self.parents = []
        self.children = []
        self.name = name

    def hasChild(self, child):
        self.children.append(child)
        child.parents.append(self)

    def grandparents(self):
        return list(x.parents for x in self.parents if x.parents)

    def __str__(self):
     return self.name
    def __unicode__(self):
        return self.name
    def __repr__(self):
        return self.name


hs = Member('Han Solo')
lo = Member('Leia Organa')
dv = Member('Darth Vader')
ls = Member('Luke Skywalker')
kr = Member('Kylo Ren')

dv.hasChild(ls)
dv.hasChild(lo)
lo.hasChild(kr)
hs.hasChild(kr)


# Who is the parent of Luke Skywalker?
print(ls.parents)

# Who  are the children of Darth Vader?
print(dv.children)

# Who is the grandparent of Kylo Ren?
print(kr.grandparents())