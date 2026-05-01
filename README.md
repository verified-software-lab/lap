# LAP: Logic, Algorithms, and Proof Tools

The LAP toolset is a set of command line tools for teaching logic in
computer science.  It provides implementations of standard
algorithms for propositional and first order logic, including
conversions to various normal forms, propositional satisfiability
algorithms such as DPLL, Tseytin's transformation, and equivalence
checking.  Significantly, LAP also supports a language for
expressing a natural deduction derivation for propositional or first
order logic.  The tools can check the derivation, provide meaningful
feedback if it is wrong, or display the derivation in a variety of
formats.  The toolset is written in Java and has no dependencies
other than a Java Virtual Machine.  The code has been designed to be
easy to read and to illuminate the data definitions and algorithms.

**Note:** the command line interface has changed since v1.0.0.  See
[the version of this README for the earlier interface](https://github.com/verified-software-lab/lap/blob/7322b135eb45a046fbad0ae8ca3b595998a0e85f/README.md).

## Examples

Find a satisfying model for a propositional formula by brute force:
```
> lap sat -v -model -alg brute -f '(p|q)&!p'
Formula: (p∨q)∧¬p
Evaluating formula at model {}.  Result: false
Evaluating formula at model {p}.  Result: false
Evaluating formula at model {q}.  Result: true
{q}
```

Convert a propositional formula to an equivalent formula in Conjunctive
Normal Form (CNF):
```
siegel@giacomo lap % lap cnf -f '!(!p->!q)|r'
(¬p∨r)∧(q∨r)
```
Apply the DPLL Boolean Satisfiability algorithm to a CNF formula:
```
siegel@giacomo lap % lap dpll -v -f 'p&!q'
CNF formula: p∧¬q
CNF Structure: {[¬q], [p]}
Push {[¬q], [p]}.  Model = {}
[UNIT] Setting q to false.
Push {[p]}.  Model = {}
[UNIT] Setting p to true.
Push {}.  Model = {p}
Satisfying model found!
Pop.
Pop.
Pop.
true
```

Apply the Tseytin Transformation to a propositional formula to convert it to
an equisatisfiable formula in CNF:
```
siegel@giacomo lap % lap tseytin -f '(p<->q) & !(q<->p)'
(p3∨q)∧(p5∨¬p3∨¬p4)∧(p0∨¬q)∧(p1∨q)∧(¬p4∨¬p∨q)∧(p4∨p)∧(¬p5∨p4)∧(p3∨¬p)∧(¬p5∨p3)∧(¬p6∨p2)∧(¬p1∨¬q∨p)∧(p1∨¬p)∧(p4∨¬q)∧(¬p2∨p1)∧(¬p3∨¬q∨p)∧(¬p6∨¬p5)∧(p2∨¬p0∨¬p1)∧(p6∨¬p2∨p5)∧(p0∨p)∧(¬p0∨¬p∨q)∧(¬p2∨p0)∧p6
```

Again, but pipe the output into the DPLL algorithm to determine satisfiability:
```
siegel@giacomo lap % lap tseytin -f '(p<->q) & !(q<->p)' | lap dpll -in 
false
```

Check a (natural deduction for propositional logic) derivation:
```
siegel@giacomo lap % cat examples/pl/ex46.lap
1.   p|q |- p|q (Ax).     
2. p|q,p |- p   (Ax).     
3. p|q,p |- q|p (IOR2)2.
4. p|q,q |- q   (Ax).     
5. p|q,q |- q|p (IOR1)4.   
6.   p|q |- q|p (EOR)1,3,5.
siegel@giacomo lap % lap check examples/pl/ex46.lap 
true
```

Again, but show the derivation in all formats: tuple, linear, tree, hierarchy, and fitch, with steps numbered consistently in all cases:
```
siegel@giacomo lap % lap check -v examples/pl/ex46.lap
true

(⑥ p∨q ⊢ q∨p, E∨, (
  (① p∨q ⊢ p∨q, Ax, ()), 
  (③ p∨q,p ⊢ q∨p, I∨2, (
    (② p∨q,p ⊢ p, Ax, ()))), 
  (⑤ p∨q,q ⊢ q∨p, I∨1, (
    (④ p∨q,q ⊢ q, Ax, ())))))

1.   p∨q ⊢ p∨q (Ax).     
2. p∨q,p ⊢ p   (Ax).     
3. p∨q,p ⊢ q∨p (I∨2)2.   
4. p∨q,q ⊢ q   (Ax).     
5. p∨q,q ⊢ q∨p (I∨1)4.   
6.   p∨q ⊢ q∨p (E∨)1,3,5.

                                                                     
                             (Ax) ───────────        (Ax) ───────────
                                  ② p∨q,p ⊢ p             ④ p∨q,q ⊢ q
     (Ax) ───────────  (I∨2) ────────────────  (I∨1) ────────────────
          ① p∨q ⊢ p∨q         ③ p∨q,p ⊢ q∨p         ⑤ p∨q,q ⊢ q∨p
(E∨) ────────────────────────────────────────────────────────────────
                               ⑥ p∨q ⊢ q∨p

6. p∨q ⊢ q∨p  (E∨)
1. │ p∨q ⊢ p∨q  (Ax)
3. │ p∨q,p ⊢ q∨p  (I∨2)
2. │ │ p∨q,p ⊢ p  (Ax)
5. │ p∨q,q ⊢ q∨p  (I∨1)
4. │ │ p∨q,q ⊢ q  (Ax)

   │ p∨q            
   ├───             
1. │ p∨q   (Ax)     
   │ │ p            
   │ ├───           
2. │ │ p   (Ax)     
3. │ │ q∨p (I∨2)2   
   │                
   │ │ q            
   │ ├───           
4. │ │ q   (Ax)     
5. │ │ q∨p (I∨1)4   
6. │ q∨p   (E∨)1,3,5
siegel@giacomo lap % 
```
Check an FOL derivation:
```
siegel@giacomo fol % cat ch6_1_3_quantifierdual_1.lap 
1. ∀x. ¬P(x), ∃x. P(x) ⊢ ∃x. P(x) (Ax).
2. ∀x. ¬P(x), ∃x. P(x), P(y) ⊢ P(y) (Ax).
3. ∀x. ¬P(x), ∃x. P(x), P(y) ⊢ ∀x. ¬P(x) (Ax).
4. ∀x. ¬P(x), ∃x. P(x), P(y) ⊢ ¬P(y) (E∀)3.
5. ∀x. ¬P(x), ∃x. P(x), P(y) ⊢ ⊥ (E¬)2,4.
6. ∀x. ¬P(x), ∃x. P(x) ⊢ ⊥ (E∃)1,5.
7. ∀x. ¬P(x) ⊢ ¬∃x. P(x) (I¬)6.
siegel@giacomo fol % lap check -lang fol -v ch6_1_3_quantifierdual_1.lap 
true

(⑦ ∀x.¬P(x) ⊢ ¬∃x.P(x), I¬, (
  (⑥ ∀x.¬P(x),∃x.P(x) ⊢ ⊥, E∃, (
    (① ∀x.¬P(x),∃x.P(x) ⊢ ∃x.P(x), Ax, ()), 
    (⑤ P(y),∀x.¬P(x),∃x.P(x) ⊢ ⊥, E¬, (
      (② P(y),∀x.¬P(x),∃x.P(x) ⊢ P(y), Ax, ()), 
      (④ P(y),∀x.¬P(x),∃x.P(x) ⊢ ¬P(y), E∀, (
        (③ P(y),∀x.¬P(x),∃x.P(x) ⊢ ∀x.¬P(x), Ax, ())))))))))

1.      ∀x.¬P(x),∃x.P(x) ⊢ ∃x.P(x)  (Ax).   
2. P(y),∀x.¬P(x),∃x.P(x) ⊢ P(y)     (Ax).   
3. P(y),∀x.¬P(x),∃x.P(x) ⊢ ∀x.¬P(x) (Ax).   
4. P(y),∀x.¬P(x),∃x.P(x) ⊢ ¬P(y)    (E∀)3.  
5. P(y),∀x.¬P(x),∃x.P(x) ⊢ ⊥        (E¬)2,4.
6.      ∀x.¬P(x),∃x.P(x) ⊢ ⊥        (E∃)1,5.
7.              ∀x.¬P(x) ⊢ ¬∃x.P(x) (I¬)6.  

7. ∀x.¬P(x) ⊢ ¬∃x.P(x)  (I¬)
6. │ ∀x.¬P(x),∃x.P(x) ⊢ ⊥  (E∃)
1. │ │ ∀x.¬P(x),∃x.P(x) ⊢ ∃x.P(x)  (Ax)
5. │ │ P(y),∀x.¬P(x),∃x.P(x) ⊢ ⊥  (E¬)
2. │ │ │ P(y),∀x.¬P(x),∃x.P(x) ⊢ P(y)  (Ax)
4. │ │ │ P(y),∀x.¬P(x),∃x.P(x) ⊢ ¬P(y)  (E∀)
3. │ │ │ │ P(y),∀x.¬P(x),∃x.P(x) ⊢ ∀x.¬P(x)  (Ax)

   │ ∀x.¬P(x)            
   ├───                  
   │ │ ∃x.P(x)           
   │ ├───                
1. │ │ ∃x.P(x)    (Ax)   
   │ │ │ P(y)            
   │ │ ├───              
2. │ │ │ P(y)     (Ax)   
3. │ │ │ ∀x.¬P(x) (Ax)   
4. │ │ │ ¬P(y)    (E∀)3  
5. │ │ │ ⊥        (E¬)2,4
6. │ │ ⊥          (E∃)1,5
7. │ ¬∃x.P(x)     (I¬)6  
                                                                                                                                   
                                                                                            (Ax) ──────────────────────────────────
                                                                                                 ③ P(y),∀x.¬P(x),∃x.P(x) ⊢ ∀x.¬P(x)
                                                  (Ax) ──────────────────────────────  (E∀) ───────────────────────────────────────
                                                       ② P(y),∀x.¬P(x),∃x.P(x) ⊢ P(y)           ④ P(y),∀x.¬P(x),∃x.P(x) ⊢ ¬P(y)
          (Ax) ────────────────────────────  (E¬) ─────────────────────────────────────────────────────────────────────────────────
               ① ∀x.¬P(x),∃x.P(x) ⊢ ∃x.P(x)                                  ⑤ P(y),∀x.¬P(x),∃x.P(x) ⊢ ⊥
     (E∃) ─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
                                                           ⑥ ∀x.¬P(x),∃x.P(x) ⊢ ⊥
(I¬) ──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
                                                         ⑦ ∀x.¬P(x) ⊢ ¬∃x.P(x)

```

What happens when you try to check an incorrect derivation?
```
siegel@giacomo fol % cat badEexists1.lap   
1. ∃x. P(x) ⊢ ∃x. P(x) (Ax).
2. ∃x. P(x), P(y) ⊢ P(y) (Ax).
3. ∃x. P(x) ⊢ P(y) (E∃)1,2.

siegel@giacomo fol % lap check -v -lang fol badEexists1.lap 
false
Violation of rule E∃ at step 3:
  Premise 1  : ∃x.P(x) ⊢ ∃x.P(x)
  Premise 2  : P(y),∃x.P(x) ⊢ P(y)
  Conclusion : ∃x.P(x) ⊢ P(y)
This step violates Side condition 1 as y does occur free in the
conclusion's succedent θ (P(y))

Rule E∃ ("eliminate exists") :
     Γ ⊢ ∃x φ    Γ, φ[y/x] ⊢ θ
     ─────────────────────────
               Γ ⊢ θ
Side condition 1: y must not occur free in Γ, φ, or θ;
Side condition 2: y must be free for x in φ.
siegel@giacomo fol % 
```

## Install

You will need a Java runtime, v.21 or higher.

Download `lap.jar` from the latest release.  Put it somewhere convenient.
Create an alias in your startup file (e.g., `.zprofile`, `.profile`, `.bash_profile`,  etc.)
like this:
```
alias lap='java -jar /path/to/lap.jar'
```
Then source the startup file, e.g.:
```
. /path/to/.zprofile
```
Now you should be able to use the command `lap` from any directory.  Try
```
lap help
```
and see if you get the help message.

## Authors
- Stephen Siegel
- Yuxin Zhou
