The last article defines BFAE, which features mutable boxes. Like BFAE, some functional languages, including OCaml, allow programmers to use mutable spaces that require explicit creation, modification, and unboxing but not mutable variables. On the other hand, variables of many languages are mutable. The article defines MFAE, which features mutable variables.

The lecture defines BMFAE, which is MFAE with mutable boxes. The last article has already dealt with boxes so that one can easily add boxes to MFAE to define BMFAE. This article focuses on only variables. I recommend students to define BMFAE by themselves as practice after reading the article.

## Syntax

MFAE is similar to FAE but allows changing the values of variables. Its expressions include every expression of FAE and assign expressions. The below shows the abstract syntax of MFAE. The common parts to FAE are omitted.

\[
\begin{array}{lrcl}
\text{Expression} & e & ::= & \cdots \\
&& | & x:=e \\
\end{array}
\]

Expression \(x:=e\) assigns a value denoted by \(e\) to a variable whose name is \(x\).

MFAE lacks expression sequencing since a lambda abstraction and a function application can encode it.


\[\mathit{encode}(e_1;e_2)=(\lambda \_ . \mathit{encode}(e_2))\ \mathit{encode}(e_1)\]

The right-hand side evaluates \(e_1\) first and then \(e_2\). The result of \(e_1\) is discarded, and the result of the whole expression equals that of \(e_2\). The result is the same as evaluating \(e_1;e_2\) of the left-hand side.

## Semantics

The semantics of MFAE uses the store passing style as that of BFAE does. \(\Rightarrow\) is a relation over environments, stores, expressions, values, and stores.

\[\Rightarrow\subseteq\text{Environment}\times\text{Store}\times\text{Expression}\times\text{Value}\times\text{Store}\]

A value of MFAE is either an integer or a closure. BFAE allows addresses to be values because programmers can directly use boxes that yield addresses after being evaluated. However, MFAE features mutable variables instead of boxes. Evaluating a variable results in its value, which differs from the address of a box containing the value. Evaluating an expression never yields an address, and thus values can exclude addresses.

\[
\begin{array}{lrcl}
\text{Value} & v & ::= & n \\
&& | & \langle \lambda x.e,\sigma \rangle
\end{array}
\]

An environment of MFAE is a partial function from an identifier to an address, but not a value. The semantics needs environments to find a value denoted by a variable. The previous languages, whose variables are immutable, are satisfied with environments that takes an identifier and returns a value. However, variables of MFAE are mutable. Since the results of evaluations exclude environments, values stored in environments are never modified. On the other hand, values in stores are modifiable as stores are the results of evaluations. Thus, stores must contain values denoted by variables, and environments must know the address of a value denoted by a variable.

\[
\begin{array}{lrcl}
\text{Environment} & \sigma & \in & \textit{Id}\hookrightarrow\text{Address} \\
\text{Store} & M & \in & \text{Address}\hookrightarrow\text{Value}
\end{array}
\]

Environments may seem unnecessary. However, in fact, removing environments from the semantics prevents the use of static scope. Assume that the semantics lacks environments, and its store is a partial function from an identifier to a value. Consider expression \((\lambda x.x:=1)0;x\). The example uses expression sequencing for brevity. The function application creates a box containing the value of \(x\) in a store. The store is passed to evaluating \(x\), and the result is \(1\). On the contrary, under static scope, the scope of the binding occurrence of \(x\) includes only \(x:=1\), and the expression results in an error because the final \(x\) is a free identifier. As the example implies, both environments and stores are essential. Environments support static scope, and stores make the values of variables editable.

Inference rules for integers, sum, products, and lambda abstractions are the following:

\[
\sigma,M\vdash n\Rightarrow n,M
\]

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
\sigma,M\vdash \lambda x.e\Rightarrow \langle\lambda x.e,\sigma\rangle,M
\]

An environment has the address of a given identifier, and a store has the value at the address. Finding the value of a variable requires lookup twice: an address from the environment and a value from the store.

\[
\frac
{ x\in\mathit{Domain}(\sigma) \quad \sigma(x)\in\mathit{Domain}(M) }
{ \sigma,M\vdash x\Rightarrow M(\sigma(x)), M }
\]

The store remains the same.

Function applications are the only expressions that create new boxes.

\[
\frac
{ \sigma,M\vdash e_1\Rightarrow \langle\lambda x.e,\sigma'\rangle,M_1 \quad
  \sigma,M_1\vdash e_2\Rightarrow v_1,M_2 \quad
  a\not\in \mathit{Domain}(M_2) \quad
  \sigma'\lbrack x\mapsto a\rbrack,M_2\lbrack a\mapsto v_1\rbrack\vdash e\Rightarrow v_2,M_3 }
{ \sigma,M\vdash e_1\ e_2\Rightarrow v_2,M_3 }
\]

Evaluating the argument yields a value. A box is created, and the value goes into the box. The relationship between the name of the parameter and the address of the box is added to the environment.

Assigning a value to a variable is similar to modifying the value of a box of BFAE. The left-hand side of an assign expression is a variable. The store has the address of the variable.

\[
\frac
{ x\in\mathit{Domain}(\sigma) \quad
  \sigma,M\vdash e\Rightarrow v,M' }
{ \sigma,M\vdash x:=e\Rightarrow v,M'\lbrack \sigma(x)\mapsto v\rbrack }
\]

Evaluation of the right-hand side yields a value and a store. A value at the address of the variable in the store becomes the value of the right-hand side.

## Implementing an Interpreter

The following Scala code implements the abstract syntax, environments, and stores of MFAE:

```scala
sealed trait MFAE
case class Num(n: Int) extends MFAE
case class Add(l: MFAE, r: MFAE) extends MFAE
case class Sub(l: MFAE, r: MFAE) extends MFAE
case class Id(x: String) extends MFAE
case class Fun(x: String, b: MFAE) extends MFAE
case class App(f: MFAE, a: MFAE) extends MFAE
case class Set(x: String, e: MFAE) extends MFAE

sealed trait MFAEV
case class NumV(n: Int) extends MFAEV
case class CloV(p: String, b: MFAE, e: Env) extends MFAEV

type Env = Map[String, Addr]
def lookup(x: String, env: Env): Addr =
  env.getOrElse(x, throw new Exception)

type Addr = Int
type Sto = Map[Addr, MFAEV]
def storeLookup(a: Addr, sto: Sto): MFAEV =
  sto.getOrElse(a, throw new Exception)
def malloc(sto: Sto): Addr =
  sto.keys.maxOption.getOrElse(0) + 1
```

The `Set` class corresponds to an assign expression.

The `Num`, `Add`, `Sub`, and `Fun` cases of the `interp` function equal to the interpreter of BFAE.

```scala
def interp(e: MFAE, env: Env, sto: Sto): (MFAEV, Sto) = e match {
  case Num(n) => (NumV(n), sto)
  case Add(l, r) =>
    val (NumV(n), ls) = interp(l, env, sto)
    val (NumV(m), rs) = interp(r, env, ls)
    (NumV(n + m), rs)
  case Sub(l, r) =>
    val (NumV(n), ls) = interp(l, env, sto)
    val (NumV(m), rs) = interp(r, env, ls)
    (NumV(n - m), rs)
  case Fun(x, b) => (CloV(x, b, env), sto)
```

The `Id` case calls both `lookup` and `storeLookup`.

```scala
  case Id(x) => (storeLookup(lookup(x, env), sto), sto)
```

`App` calls `malloc` to find a new address.

```scala
  case App(f, a) =>
    val (CloV(x, b, fEnv), ls) = interp(f, env, sto)
    val (v, rs) = interp(a, env, ls)
    val addr = malloc(rs)
    interp(b, fEnv + (x -> addr), rs + (addr -> v))
```

`Set` calls `lookup` to find the address of the variable and changes the value at the address.

```scala
  case Set(x, e) =>
    val (v, s) = interp(e, env, sto)
    (v, s + (lookup(x, env) -> v))
}
```

The below shows the whole code at once.

<details><summary>See the code</summary>

```scala
sealed trait MFAE
case class Num(n: Int) extends MFAE
case class Add(l: MFAE, r: MFAE) extends MFAE
case class Sub(l: MFAE, r: MFAE) extends MFAE
case class Id(x: String) extends MFAE
case class Fun(x: String, b: MFAE) extends MFAE
case class App(f: MFAE, a: MFAE) extends MFAE
case class Set(x: String, e: MFAE) extends MFAE

sealed trait MFAEV
case class NumV(n: Int) extends MFAEV
case class CloV(p: String, b: MFAE, e: Env) extends MFAEV

type Env = Map[String, Addr]
def lookup(x: String, env: Env): Addr =
  env.getOrElse(x, throw new Exception)

type Addr = Int
type Sto = Map[Addr, MFAEV]
def storeLookup(a: Addr, sto: Sto): MFAEV =
  sto.getOrElse(a, throw new Exception)
def malloc(sto: Sto): Addr =
  sto.keys.maxOption.getOrElse(0) + 1

def interp(e: MFAE, env: Env, sto: Sto): (MFAEV, Sto) = e match {
  case Num(n) => (NumV(n), sto)
  case Add(l, r) =>
    val (NumV(n), ls) = interp(l, env, sto)
    val (NumV(m), rs) = interp(r, env, ls)
    (NumV(n + m), rs)
  case Sub(l, r) =>
    val (NumV(n), ls) = interp(l, env, sto)
    val (NumV(m), rs) = interp(r, env, ls)
    (NumV(n - m), rs)
  case Fun(x, b) => (CloV(x, b, env), sto)
  case Id(x) => (storeLookup(lookup(x, env), sto), sto)
  case App(f, a) =>
    val (CloV(x, b, fEnv), ls) = interp(f, env, sto)
    val (v, rs) = interp(a, env, ls)
    val addr = malloc(rs)
    interp(b, fEnv + (x -> addr), rs + (addr -> v))
  case Set(x, e) =>
    val (v, s) = interp(e, env, sto)
    (v, s + (lookup(x, env) -> v))
}
```

</details>

The below example evaluates \((\lambda x.x+(x:=1)+x)0\) with the `interp` function. The first \(x\) and the second \(x\) respectively denote \(0\) and \(1\). \(x:=1\) between two \(x\)'s results in \(1\). The result must be \(2\), and the final store must map the address of \(x\) onto \(1\).

```scala
// (lambda x.x+(x:=1)+x) 0
interp(
  App(
    Fun("x",
      Add(Add(
        Id("x"),
        Set("x", Num(1))),
        Id("x")
      )
    ),
    Num(0)
  ),
  Map.empty,
  Map.empty
)
// (NumV(2), Map(1 -> NumV(1)))
```

## Call by Reference

The semantics of a function application varies in how arguments are passed to a function. Different languages use different function application semantics. Some languages provide multiple ways to pass arguments and allow programmers to choose one of them.

Every hitherto language in the articles uses *call-by-value* semantics. Under the semantics, the evaluation of the argument precedes the evaluation of the function body. Only a value denoted by the argumens is passed to the function.

*Call by reference* is another semantics to deal with function applications. It allows passing an address instead of a value if the argument is a variable. The following inference rules define the semantics of MFAE featuring call by reference:

\[
\frac
{ \sigma,M\vdash e\Rightarrow \langle\lambda x'.e',\sigma'\rangle,M_1 \quad
  x\in\mathit{Domain}(\sigma) \quad
  \sigma'\lbrack x'\mapsto \sigma(x)\rbrack,M_1\vdash e'\Rightarrow v,M_2 }
{ \sigma,M\vdash e\ x\Rightarrow v,M_2 }
\]

\[
\frac
{ \sigma,M\vdash e_1\Rightarrow \langle\lambda x.e,\sigma'\rangle,M_1 \quad
  e_2\not\in\text{Variable} \quad
  \sigma,M_1\vdash e_2\Rightarrow v_1,M_2 \quad
  a\not\in \mathit{Domain}(M_2) \quad
  \sigma'\lbrack x\mapsto a\rbrack,M_2\lbrack a\mapsto v_1\rbrack\vdash e\Rightarrow v_2,M_3 }
{ \sigma,M\vdash e_1\ e_2\Rightarrow v_2,M_3 }
\]

The first rule corresponds to the cases that the argument is a variable; the second corresponds to any other cases. When the argument is a variable, box creation is unnecessary, and it is enough to add a map from the parameter to the address of the variable to the environment. The address of the parameter in the function body equals the address of the variable. It is call by reference. On the other hand, an expression that is not a variable may result in a value that is not stored in any boxes. There is no way to pass an address, and thus call by value is used.

The interpreter needs the following change:

```scala
  case App(f, a) =>
    val (CloV(x, b, fEnv), ls) = interp(f, env, sto)
    a match {
      case Id(y) =>
        interp(b, fEnv + (x -> lookup(y, env)), ls)
      case _ =>
        val (v, rs) = interp(a, env, ls)
        val addr = malloc(rs)
        interp(b, fEnv + (x -> addr), rs + (addr -> v))
    }
```

An example of call by reference is a parameter with the ampersand in C++. If the ampersand follows the name of a parameter, the parameter uses the call-by-reference semantics.

```c++
#include <iostream>

void f(int &);

int main() {
    int x = 1;
    f(x);
    std::cout << x << std::endl;
}

void f(int &x) {
    x = 2;
}
```

Execution of the code prints `2`.

In C or C++, programmers can mimic call by reference by explicitly passing pointers as arguments. In Java or Scala, if arguments are objects, their references are passed. One can say that it is call by reference. Many other object-oriented languages use call by reference. However, since they pass the addresses of objects instead of the addresses of variables, their call-by-reference semantics differs from call by reference of C++ or MFAE. Some people call such semantics of object-oriented languages *call by sharing*, but the terminology is unpopular.

*Call by name* and *call by need* are other semantics for function applications. The next article deals with them.

## Acknowledgments

I thank professor Ryu for giving feedback on the article.
