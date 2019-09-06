The article defines BFAE, a language with *boxes*, which are mutable spaces storing data.

## Syntax

The below is the abstract syntax of BFAE. It shows only parts not in FAE.

\[
\begin{array}{lrcl}
\text{Expression} & e & ::= & \cdots \\
&& | & \textsf{ref}\ e \\
&& | & e:=e \\
&& | & !e \\
&& | & e;e
\end{array}
\]

\(\textsf{ref}\) creates a new box. \(\textsf{ref}\ e\) evaluates \(e\), creates a new box, and stores the result of \(e\) in the box. The whole expression denotes the box. A *low-level* interpretation of a box is a memory address where its value exists. \(\textsf{ref}\) is similar to `malloc` of C or `new` of object-oriented languages because it allocates spaces whom programmers can use in a memory. For example, rewriting \(\textsf{ref}\ 1\) in C yields `int *p = (int *) malloc(sizeof(int)); *p = 1; return p;`.

\(:=\) modifies the content of a box. \(e_1:=e_2\) evaluates \(e_1\) and then \(e_2\) and puts a value denoted by \(e_2\) in a box denoted by \(e_1\). The result of the whole expression equals the value. If \(e_1\) does not denote a box, a run-time error occurs. The expression directly modifies the memory. It is similar to an assignment statement whose left-hand side is a pointer *dereference* in C. For example, if \(x\) denotes a box or a pointer, \(x:=2\) equals \(*x=2\).

\(!\) opens a box. If \(e\) results in a box, \(!e\) results in the content of the box. It is similar to a pointer dereference not at the left-hand side of an assignment statement in C. \(!x\) equals \(*x\).

\(e_1;e_2\) is an expression obtained by *sequencing* \(e_1\) and \(e_2\). It evaluates \(e_1\) and then \(e_2\). The whole expression denotes a value denoted by \(e_2\). Languages covered by the previous articles lack expression sequencing since it is meaningless. The result of the former expression is always discarded without being used by the latter expression. On the other hand, in the case of BFAE, the former expression can create a box or modify the content of a box and is therefore meaningful although its result is ignored. Most languages allow programmers to write sequences of multiple statements or expressions separated by commas or line breaks. Expression sequencing of BFAE equals them.

## Semantics

Unlike the previous languages, BFAE is not a purely functional language but provides mutable boxes. Like imperative languages, execution of a BFAE program mutates a state. Despite mutability, the semantics of BFAE retains the functional viewpoint, which interprets an expression as a program and executes the program by finding a value denoted by the expression.

Defining a mutable memory is crucial to define the semantics of BFAE. The article calls such memories *stores*. A store saves values in boxes existing during execution of a program. Boxes are distinguishable as they have distinct names. The article calls the names *addresses*. Let \(Addr\) be the set of every possible address. A store is a partial function from an address to a value. A value mapped from the address of a box by a store is the content of the box.

\[
\begin{array}{lrcl}
\text{Address} & a & \in & \mathit{Addr} \\
\text{Store} & M & \in & \text{Address}\hookrightarrow\text{Value}
\end{array}
\]

Metavariable \(a\) ranges over addresses; \(M\) ranges over stores.

The semantics does not require a concrete definition of a box. Since the address of a box determines the meaning of the box, addresses are enough for the semantics. The result of evaluating an expression denoting a box is an address. For example, \(\textsf{ref}\ e\) yields an address. The previous languages allow only integers and closures to be values, but BFAE additionally allows addresses to be values.

\[
\begin{array}{lrcl}
\text{Value} & e & ::= & \cdots \\
&& | & a
\end{array}
\]

Note that the remaining part of the article keeps using the concept of a box. Even though the semantics abstracts boxes with addresses, in the programmers' perspective, boxes do exist. Besides, boxes are more intuitive than addresses for explanations.

Evaluating \(!e\) needs not only an environment but also a store. If \(e\) denotes a box, the store has a value stored in the box. Hence, evaluating an expression requires a store, and \(\Rightarrow\) must be a relation over environments, stores, expressions, and values.

Evaluating \(\textsf{ref}\ e\) creates a new box; evaluating \(e_1:=e_2\) changes the content of a box. Both modify stores. Modifying a store differs from extending an environment with a new variable. Evaluating \(\textsf{val}\ x=e_1\ \textsf{in}\ e_2\) adds \(x\) to the environment, but only \(e_2\) uses the extended environment because the scope of the binding occurrence of \(x\) is \(e_2\) but nowhere else. For instance, let \(e\) denote the expression, then both \(e'\) and addition of \(e+e'\) do not require the extended one. On the other hand, the modified store is unnecessary for the subexpressions of an expression that creates or modifies a box while other parts of the program need the modified one. Consider \(x:=2;!x\) as an example. \(!x\) must know that \(x:=2\) has changed the content of a box denoted by \(x\) into \(2\). Therefore, how stores change due to expressions is important. If an expression contains two subexpressions, a store obtained by evaluating the first subexpression has to be passed to the evaluation of the second expressions. Since the semantics needs to yield both of the resulting value and a new store, the final correct definition of \(\Rightarrow\) is a relation over environments, stores, expressions, values, and stores. Evaluation reads the former store and creates the second store.

\[\Rightarrow\subseteq\text{Environment}\times\text{Store}\times\text{Expression}\times\text{Value}\times\text{Store}\]

\(\sigma,M\vdash e\Rightarrow v,M'\) implies that evaluating \(e\) under \(\sigma\) and \(M\) results in \(v\) and creates \(M'\). The semantics uses the *store passing style*. The style allows defining BFAE, featuring mutable boxes, without any mutable concepts.

The order among subexpressions matters as the subexpressions can modify stores. Suppose that \(x\) denotes a box, and the box contains \(1\). \( (x:=2)+(!x) \) yields \(4\) if \(x:=2\) comes first so that \(!x\) equals \(2\). In contrast, \( (x:=2)+(!x) \) yields \(3\) if \(!x\) comes first so that \(!x\) equals \(1\). Inference rules naturally set orders by passing stores.

The inference rules for integers, variables, lambda abstractions equal those of FAE but additionally require stores. They maintain the contents of given stores.

\[
\sigma,M\vdash n\Rightarrow n,M
\]

\[
\frac
{ x\in\mathit{Domain}(\sigma) }
{ \sigma,M\vdash x\Rightarrow \sigma(x),M }
\]

\[
\sigma,M\vdash \lambda x.e\Rightarrow \langle\lambda x.e,\sigma\rangle,M
\]

A sequenced expression per se cannot modify a given store, but its subexpressions can. The left subexpression comes before the right one. The evaluation of the right considers any modifications of the store made by the left.

\[
\frac
{ \sigma,M\vdash e_1\Rightarrow v_1,M_1 \quad
  \sigma,M_1\vdash e_2\Rightarrow v_2,M_2 }
{ \sigma,M\vdash e_1;e_2\Rightarrow v_2,M_2 }
\]

The rule passes \(M_1\), obtained by evaluating the left, to the evaluation of the right and discards the result of the left. The final result equals the result of the right.

Sums, differences, and function applications are similar to sequenced expressions. They cannot modify stores, but their subexpressions can. The evaluation orders are the same as those of sequenced expressions. BFAE chooses the left-to-right order for every expression, but orders vary with languages.

\[
\frac
{ \sigma,M\vdash e_1\Rightarrow n_1,M_1 \quad
  \sigma,M_1\vdash e_2\Rightarrow n_2,M_2 }
{ \sigma,M\vdash e_1+e_2\Rightarrow n_1+n_2,M_2 }
\]

\[
\frac
{ \sigma,M\vdash e_1\Rightarrow n_1,M_1 \quad
  \sigma,M_1\vdash e_2\Rightarrow n_2,M_2 }
{ \sigma,M\vdash e_1-e_2\Rightarrow n_1-n_2,M_2 }
\]

\[
\frac
{ \sigma,M\vdash e_1\Rightarrow \langle\lambda x.e,\sigma'\rangle,M_1 \quad
  \sigma,M_1\vdash e_2\Rightarrow v_1,M_2 \quad
  \sigma'\lbrack x\mapsto v_1\rbrack,M_2\vdash e\Rightarrow v_2,M_3 }
{ \sigma,M\vdash e_1\ e_2\Rightarrow v_2,M_3 }
\]

Note that the evaluation of the body of a closure can modify the store as well.

The remaining, creating a box, modifying a box, and opening a box, change or read stores directly.

\[
\frac
{ \sigma,M\vdash e\Rightarrow v,M' \quad
  a\not\in \mathit{Domain}(M') }
{ \sigma,M\vdash \textsf{ref}\ e\Rightarrow a,M'\lbrack a\mapsto v\rbrack }
\]

When a box is created, the address of the box must not belong to the domain of a store attained by evaluating the subexpression. The result contains the address and the store plus a mapping from the address to the value of the subexpression.

\[
\frac
{ \sigma,M\vdash e_1\Rightarrow a,M_1 \quad
  \sigma,M_1\vdash e_2\Rightarrow v,M_2 }
{ \sigma,M\vdash e_1:=e_2\Rightarrow v,M_2\lbrack a\mapsto v\rbrack }
\]

Like other expressions, an expression modifying a box uses the left-to-right order. If the left subexpression results in an address, a value associated with the address in the store changes into the value of the right subexpression. The whole expression denotes the value.

\[
\frac
{ \sigma,M\vdash e\Rightarrow a,M' \quad
  a\in \mathit{Domain}(M') }
{ \sigma,M\vdash !e\Rightarrow M'(a),M' }
\]

The subexpression of an expression opening a box has to yield an address. A value denoted by the expression is a value at the address in the store. The store differs from a given store but is the outcome of evaluating the subexpression. For example, \(!(\textsf{ref}\ 1)\) correctly results in \(1\) only if the opening uses the store given by \(\textsf{ref}\ 1\).

## Implementing an Interpreter

The following Scala code implements the abstract syntax, environments, and stores of BFAE:

```scala
sealed trait BFAE
case class Num(n: Int) extends BFAE
case class Add(l: BFAE, r: BFAE) extends BFAE
case class Sub(l: BFAE, r: BFAE) extends BFAE
case class Id(x: String) extends BFAE
case class Fun(x: String, b: BFAE) extends BFAE
case class App(f: BFAE, a: BFAE) extends BFAE
case class NewBox(e: BFAE) extends BFAE
case class SetBox(b: BFAE, e: BFAE) extends BFAE
case class OpenBox(b: BFAE) extends BFAE
case class Seqn(l: BFAE, r: BFAE) extends BFAE

sealed trait BFAEV
case class NumV(n: Int) extends BFAEV
case class CloV(p: String, b: BFAE, var e: Env) extends BFAEV
case class BoxV(a: Addr) extends BFAEV

type Env = Map[String, BFAEV]
def lookup(x: String, env: Env): BFAEV =
  env.getOrElse(x, throw new Exception)

type Addr = Int
type Sto = Map[Addr, BFAEV]
def storeLookup(a: Addr, sto: Sto): BFAEV =
  sto.getOrElse(a, throw new Exception)
def malloc(sto: Sto): Addr =
  sto.keys.maxOption.getOrElse(0) + 1
```

The `NewBox` class corresponds to creating a box; the `SetBox` class corresponds to modifying a box; the `OpenBox` class corresponds to opening a box; the `Seqn` class corresponds to sequencing expressions. `BoxV` instances model values that are addresses. `Addr` denotes the type of an address and is `Int`. `Sto` is the type of a store and is a map from `Addr` to `BFAEV`. The `lookup` function finds a value from a given environment; the `storeLookup` function finds from a given store. The `malloc` function computes an address unused by a given store.

`interp` takes an expression, an environment, and a store as arguments and returns the pair of a value and a store.

```scala
def interp(e: BFAE, env: Env, sto: Sto): (BFAEV, Sto) = e match { ... }
```

I discuss the cases of the pattern matching in the same order as the inference rules.

```scala
case Num(n) => (NumV(n), sto)
case Id(x) => (lookup(x, env), sto)
case Fun(x, b) => (CloV(x, b, env), sto)
```

The `Num`, `Id`, and `Fun` cases use given stores as the results.

```scala
case Seqn(l, r) =>
  val (_, ls) = interp(l, env, sto)
  interp(r, env, ls)
case Add(l, r) =>
  val (NumV(n), ls) = interp(l, env, sto)
  val (NumV(m), rs) = interp(r, env, ls)
  (NumV(n + m), rs)
case Sub(l, r) =>
  val (NumV(n), ls) = interp(l, env, sto)
  val (NumV(m), rs) = interp(r, env, ls)
  (NumV(n - m), rs)
case App(f, a) =>
  val (CloV(x, b, fEnv), ls) = interp(f, env, sto)
  val (v, rs) = interp(a, env, ls)
  interp(b, env + (x -> v), rs)
```

The `Seqn`, `Add`, `Sub`, and `App` cases do not directly modify or read stores, but pass stores returned from the recursive calls to the other recursive calls or use them as the results.

```scala
case NewBox(e) =>
  val (v, s) = interp(e, env, sto)
  val a = malloc(s)
  (BoxV(a), s + (a -> v))
```

The `NewBox` case calls `malloc` to compute an unused address after evaluating the subexpression. The result contains the address and the extended store.

```scala
case SetBox(b, e) =>
  val (BoxV(a), bs) = interp(b, env, sto)
  val (v, es) = interp(e, env, bs)
  (v, es + (a -> v))
```

The `SetBox` case evaluates the two subexpressions and modifies a given store. The result contains the result of the second subexpression and the modified store.

```scala
case OpenBox(e) =>
  val (BoxV(a), s) = interp(e, env, sto)
  (storeLookup(a, s), s)
```

The `OpenBox` case finds a value corresponding to an address denoted by the subexpression in a given store. The store remains unchanged.

I have covered all the cases. The following shows the whole code at once:

<details><summary>See the code</summary>
```scala
sealed trait BFAE
case class Num(n: Int) extends BFAE
case class Add(l: BFAE, r: BFAE) extends BFAE
case class Sub(l: BFAE, r: BFAE) extends BFAE
case class Id(x: String) extends BFAE
case class Fun(x: String, b: BFAE) extends BFAE
case class App(f: BFAE, a: BFAE) extends BFAE
case class NewBox(e: BFAE) extends BFAE
case class SetBox(b: BFAE, e: BFAE) extends BFAE
case class OpenBox(b: BFAE) extends BFAE
case class Seqn(l: BFAE, r: BFAE) extends BFAE

sealed trait BFAEV
case class NumV(n: Int) extends BFAEV
case class CloV(p: String, b: BFAE, var e: Env) extends BFAEV
case class BoxV(a: Addr) extends BFAEV

type Env = Map[String, BFAEV]
def lookup(x: String, env: Env): BFAEV =
  env.getOrElse(x, throw new Exception)

type Addr = Int
type Sto = Map[Addr, BFAEV]
def storeLookup(a: Addr, sto: Sto): BFAEV =
  sto.getOrElse(a, throw new Exception)
def malloc(sto: Sto): Addr =
  sto.keys.maxOption.getOrElse(0) + 1

def interp(e: BFAE, env: Env, sto: Sto): (BFAEV, Sto) = e match {
  case Num(n) => (NumV(n), sto)
  case Id(x) => (lookup(x, env), sto)
  case Fun(x, b) => (CloV(x, b, env), sto)
  case Seqn(l, r) =>
    val (_, ls) = interp(l, env, sto)
    interp(r, env, ls)
  case Add(l, r) =>
    val (NumV(n), ls) = interp(l, env, sto)
    val (NumV(m), rs) = interp(r, env, ls)
    (NumV(n + m), rs)
  case Sub(l, r) =>
    val (NumV(n), ls) = interp(l, env, sto)
    val (NumV(m), rs) = interp(r, env, ls)
    (NumV(n - m), rs)
  case App(f, a) =>
    val (CloV(x, b, fEnv), ls) = interp(f, env, sto)
    val (v, rs) = interp(a, env, ls)
    interp(b, env + (x -> v), rs)
  case NewBox(e) =>
    val (v, s) = interp(e, env, sto)
    val a = malloc(s)
    (BoxV(a), s + (a -> v))
  case SetBox(b, e) =>
    val (BoxV(a), bs) = interp(b, env, sto)
    val (v, es) = interp(e, env, bs)
    (v, es + (a -> v))
  case OpenBox(e) =>
    val (BoxV(a), s) = interp(e, env, sto)
    (storeLookup(a, s), s)
}
```
</details>

The below code evaluates \( (\lambda x.(x:=1);!x)\ (\textsf{ref}\ 2) \). The result is \(1\), and the final store has a single box whose content is \(1\).

```scala
// (lambda x.(x:=1); !x) (ref 2)
interp(
  App(
    Fun("x",
      Seqn(
        SetBox(Id("x"), Num(1)),
        OpenBox(Id("x"))
      )
    ),
    NewBox(Num(2))
  ),
  Map.empty,
  Map.empty
)
// (NumV(1), Map(1 -> NumV(1)))
```

## Acknowledgments

I thank professor Ryu for giving feedback on the article.
