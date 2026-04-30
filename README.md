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

## Example

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
