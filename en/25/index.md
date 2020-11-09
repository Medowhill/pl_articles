This article deals with the implementation of an interpreter of a language with first-class continuations by using only first-order functions.

## Motivation

The “First-Class Continuations” article implements an interpreter of KAFE, which provides first-class continuations, in Scala.

```scala
sealed trait Expr
case class Num(n: Int) extends Expr
case class Add(l: Expr, r: Expr) extends Expr
case class Id(x: String) extends Expr
case class Fun(x: String, b: Expr) extends Expr
case class App(f: Expr, a: Expr) extends Expr
case class Vcc(x: String, b: Expr) extends Expr

sealed trait Value
case class NumV(n: Int) extends Value
case class CloV(p: String, b: Expr, e: Env) extends Value
case class ContV(k: Cont) extends Value

type Env = Map[String, Value]
type Cont = Value => Value

def interp(e: Expr, env: Env, k: Cont): Value = e match {
  case Num(n) => k(NumV(n))
  case Id(x) => k(lookup(x, env))
  case Fun(x, b) => k(CloV(x, b, env))
  case Add(e1, e2) =>
    interp(e1, env, v1 =>
      interp(e2, env, v2 =>
        k(numVAdd(v1, v2))))
  case App(e1, e2) =>
    interp(e1, env, v1 =>
      interp(e2, env, v2 => v1 match {
        case CloV(xv1, ev1, sigmav1) =>
          interp(ev1, sigmav1 + (xv1 -> v2), k)
        case ContV(k) => k(v2)
      })
    )
  case Vcc(x, b) =>
    interp(b, env + (x -> ContV(k)), k)
}
```

(Some parts are omitted.)

To make the code fit the purpose of this article better, I change the `interp` function a bit as follows:

```scala
def continue(k: Cont, v: Value): Value = k(v)

def interp(e: Expr, env: Env, k: Cont): Value = e match {
  case Num(n) => continue(k, NumV(n))
  case Id(x) => continue(k, lookup(x, env))
  case Fun(x, b) => continue(k, CloV(x, b, env))
  case Add(e1, e2) =>
    interp(e1, env, v1 =>
      interp(e2, env, v2 =>
        continue(k, numVAdd(v1, v2))))
  case App(e1, e2) =>
    interp(e1, env, v1 =>
      interp(e2, env, v2 => v1 match {
        case CloV(xv1, ev1, sigmav1) =>
          interp(ev1, sigmav1 + (xv1 -> v2), k)
        case ContV(k) => continue(k, v2)
      })
    )
  case Vcc(x, b) =>
    interp(b, env + (x -> ContV(k)), k)
}
```

`continue(k, v)`’s have replaced `k(v)`’s. Since evaluating `k(v)` is everything `continue(k, v)` does, it is the same as the previous implementation. For now, `continue` seems useless, but it will become useful soon.

`Cont` is the type of a continuation as a value in the implementation. It denotes the type of a function from a value to a value. The interpreter represents continuations as functions. A function representing a continuation is stored inside an environment to be passed to `interp` or returned from `interp`. Therefore, the interpreter uses the notion of first-class functions.

The use of first-class functions is problematic. First, low-level languages, such as C, lack first-class functions. (C provides functions pointers, but not closures. Closures are necessary to represent continuations.) There must be another way to implement an interpreter of KFAE if one uses low-level languages. Second, functions do not give useful information. The only ability of functions is being applied to arguments. However, in particular programs like debuggers, it is necessary to figure out what a given first-class continuation does. The current implementation disallows such analysis on continuations. On the other hand, a `CloV` instance represent a closure and can give the exact information about the parameter, body, and environment of the closure. A `ContV` instance cannot of course.

This article introduces a way to represent first-class continuations without first-class functions. By not using first-class functions, an interpreter of a language with first-class continuations can be written in low-level languages. In addition, if continuations are not functions and have specific structures instead, a debugger can analyze what a given continuation denotes.

## First-Order Representation of Continuations

We need to know which continuations are used in the original interpreter to define values representing continuations. There are four sorts of continuations in the interpreter:

(I have removed subtraction from the language. Subtraction is similar to addition.)

* `v1 => interp(e2, env, v2 => continue(k, numVAdd(v1, v2)))`
* `v2 => continue(k, numVAdd(v1, v2))`
* `v1 => interp(e2, env, v2 => v1 match { ... })`
* `v2 => v1 match { ... }`

(The omitted parts can be found in the previous section.)

The first continuation, `v1 => interp(e2, env, v2 => continue(k, numVAdd(v1, v2)))`, is used after the evaluation of the left operand of addition. It evaluates the right operand, calculates the sum, and passes the sum to the continuation of the entire addition. The parameter `v1` denotes the value of the left operand. The function body contains three free variables: `e2`, `env`, and `k`. `e2` is the right operand; `env` is the current environment; `k` is the continuation of the addition. (`numVAdd` is defined outside `interp`. Its definition can be found in the previous article) If the values of the free variables are determined, the behavior of the continuation is also determined. Therefore, `(e2, env, k)`, which is a triple of an expression, an environment, and a continuation, represents the continuation from now on.

Function applications continued the evaluation with continuations before. It was possible because continuations were functions. However, continuations are not functions now. They are triples and cannot be applied to values. We need a new way to continue evaluation when a continuation and a value are given. The clue already exists---look at the body of the function representing a continuation. When the function was applied to `v1`, the result was `interp(e2, env, v2 => continue(k, numVAdd(v1, v2)))`. Now, `(e2, env, k)` and `v1` are provided instead of the function and `v1`. It is enough to evaluate `interp(e2, env, v2 => continue(k, numVAdd(v1, v2)))` with `v1`, `e2`, `env`, and `k`. It evaluates the thing exactly same as the original function application.

Below compare the previous and current strategies:

* Previous: `v1 => interp(e2, env, v2 => continue(k ,numVAdd(v1, v2)))` and `v1` are given. Then, evaluate `interp(e2, env, v2 => continue(k, numVAdd(v1, v2)))` by applying `v1 => interp(e2, env, v2 => continue(k ,numVAdd(v1, v2)))` to `v1`.
* Current: `(e2, env, k)` and `v1` are given. Then, evaluate `interp(e2, env, v2 => continue(k, numVAdd(v1, v2)))` with `e2`, `env`, `k`, and `v1`.

Both strategies evaluate `interp(e2, env, v2 => continue(k, numVAdd(v1, v2)))` in the end. While the previous strategy represents continuations with functions, the current strategy represents continuations with triples.

Once you understand the first sort of continuations, the remaining ones are straightforward. The second continuation, `v2 => continue(k, numVAdd(v1, v2))`, is used after the evaluation of the right operand of addition. It calculates the sum of the operands and passes the sum to the continuation of the addition. The parameter `v2` denotes the value of the right operand. The function body contains two free variables: `k` and `v1`. `k` is the continuation of the addition; `v1` is the value of the left operand. In a similar fashion, only `v1` and `k` are enough to determine what the continuation does. Therefore, `(v1, k)`, a pair of a value and a continuation, can represent the continuation. To continue the evaluation when `(v1, k)` and `v2` are given, `continue(k, numVAdd(v1, v2))` needs to be evaluated.

* Previous: `v2 => continue(k, numVAdd(v1, v2))` and `v2` are given. Then, evaluate `continue(k, numVAdd(v1, v2))` by applying `v2 => continue(k, numVAdd(v1, v2))` to `v2`.
* Current: `(v1, k)` and `v2` are given. Then, evaluate `continue(k, numVAdd(v1, v2))` with `v1`, `k`, and `v2`.

The third continuation, `v1 => interp(e2, env, v2 => v1 match { ... })`, is used after the evaluation of the expression at the function position of a function application. It evaluates the argument, applies the function to the argument, and passes the return value to the continuation of the application. `v1` denotes the value of the expression at the function position. The body of the function representing the continuation contains three free variables: `e2`, `env`, and `k`. `e2` is the expression at the argument position; `env` is the current environment; `k` is the continuation of the application. (`k` is in `...`.) Therefore, `e2`, `env`, and `k` determines what the continuation does, and `(e2, env, k)`, a triple of an expression, an environment, and a continuation, can represent the continuation. Continuing the evaluation is evaluating `interp(e2, env, v2 => v1 match { ... })`, which can be done with `(e2, env, k)` and `v1`.

* Previous: `v1 => interp(e2, env, v2 => v1 match { ... })` and `v1` are given. Then, evaluate `v1 => interp(e2, env, v2 => v1 match { ... })` by applying `v1 => interp(e2, env, v2 => v1 match { ... })` to `v1`.
* Current: `(e2, env, k)` and `v1` are given. Then, evaluate `interp(e2, env, v2 => v1 match { ... })` with `e2`, `env`, `k`, and `v1`.

The fourth continuation, `v2 => v1 match { ... }`, is used after the evaluation of the argument of a function application. It applies the function to the argument and passes the return value to the continuation of the application. `v2` denotes the value of the argument. The body of the function representing the continuation contains two free variables: `v1` and `k`. `v1` is the value of the expression at the function position; `k` is the continuation of the application. (`k` is in `...`.) Therefore, `(v1, k)`, a pair of a value and a continuation, can represent the continuation. Continuing the evaluation is evaluating `v1 match { ... }` with `(v1, k)` and `v2`.

* Previous: `v2 => v1 match { ... }` and `v2` are given. Then, evaluate `v1 match { ... }` by applying `v2 => v1 match { ... }` to `v2`.
* Current: `(v1, k)` and `v2` are given. Then, evaluate `v1 match { ... }` with `v1`, `k`, and `v2`.

There is one more continuation, which does not appear in the implementation of `interp`. It is one that was represented as an identity function and is passed to `interp` in the beginning. The identity function returns a given argument without changes. No addition information is necessary to determine the behavior of the continuation. Therefore, `()`, the zero-length tuple (the `Unit` value in Scala) can represent the continuation. To continue the evaluation with the continuation and a value `v`, it is enough to give `v` as the result.

* Previous: An identity function and `v` are given. Then, evaluate `v` by applying the identity function to `v`.
* Current: `()` and `v` are given. Then, evaluate `v` with `v`.

In summary, the KFAE interpreter consists of continuations of the following five sorts:

* `(e2: Expr, env: Env, k: Cont)`
* `(v1: Value, k: Cont)`
* `(e2: Expr, env: Env, k: Cont)`
* `(v1: Value, k: Cont)`
* `()`

(The tuples show type information as well.)

Note that the first and the third are different even though they look the same. The first continuation computes `interp(e2, env, v2 => continue(k, numVAdd(v1, v2)))` with its data, while the third continuation computes `interp(e2, env, v2 => v1 match { ... })` with its data. Similarly, the second and the fourth are diffent as well. The second computes `continue(k, numVAdd(v1, v2))`, while the fourth computes `v1 match { ... }`.

An ADT is a way to implement a type that consists of values of various shapes. Thus, `Cont` can be newly defined with a `sealed trait` and `case class`es as follows:

```scala
sealed trait Cont
case class AddSecondK(e2: Expr, env: Env, k: Cont) extends Cont
case class DoAddK(v1: Value, k: Cont) extends Cont
case class AppArgK(e2: Expr, env: Env, k: Cont) extends Cont
case class DoAppK(v1: Value, k: Cont) extends Cont
case object MtK extends Cont
```

The names of the classes do not matter, though they are named carefully so that the names can reflect what they are for. The important things are data carried by each continuation. The last sort was represented by the empty tuple but is represented by a singleton object now by following the Scala convention. One may use `case class MtK() extends Cont` instead without changing the semantics, but the singleton object is more efficient than the case class in the implementation perspective. Now, the implementation of continuations does not require first-class functions.

Now, we need to revise the `continue` function. The previous implementation was `def continue(k: Cont, v: Value): Value = k(v)`. It worked because `Cont` is a function type. However, `Cont` is not a function now and `continue` needs a fix. In fact, we already know everything to make a correct fix. Previously, the function applied `k` to `v` when `k` and `v` are given. Now, it should check `k` and do the correct computation according to the data in `k`. Below is the repetition of the previous explanations:

* `(e2, env, k)` and `v1` are given. Then, evaluate `interp(e2, env, v2 => continue(k, numVAdd(v1, v2)))` with `e2`, `env`, `k`, and `v1`.
* `(v1, k)` and `v2` are given. Then, evaluate `continue(k, numVAdd(v1, v2))` with `v1`, `k`, and `v2`.
* `(e2, env, k)` and `v1` are given. Then, evaluate `interp(e2, env, v2 => v1 match { ... })` with `e2`, `env`, `k`, and `v1`.
* `(v1, k)` and `v2` are given. Then, evaluate `v1 match { ... }` with `v1`, `k`, and `v2`.
* `()` and `v` are given. Then, evaluate `v` with `v`.

The first and third explanations still pass functions to `interp` even though continuations are not functions anymore. They need small changes. Now, `(v1, k)` represents `v2 => continue(k, numVAdd(v1, v2))`, and `(v1, k)` represents `v2 => v1 match { ... }`.

* `(e2, env, k)` and `v1` are given. Then, evaluate `interp(e2, env, DoAddK(v1, k))` with `e2`, `env`, `k`, and `v1`.
* `(e2, env, k)` and `v1` are given. Then, evaluate `interp(e2, env, DoAppK(v1, k))` with `e2`, `env`, `k`, and `v1`.

The new implementation of `continue` is as follows:

```scala
def continue(k: Cont, v: Value): Value = k match {
  case AddSecondK(e2, env, k) => interp(e2, env, DoAddK(v, k))
  case DoAddK(v1, k) => continue(k, numVAdd(v1, v))
  case AppArgK(e2, env, k) => interp(e2, env, DoAppK(v, k))
  case DoAppK(v1, k) => v1 match {
    case CloV(xv1, ev1, sigmav1) =>
      interp(ev1, sigmav1 + (xv1 -> v), k)
    case ContV(k) => continue(k, v)
  }
  case MtK => v
}
```

The code is straightforward since it is exactly the same as the explanation.

The `interp` function also needs a fix to follow the new definition of `Cont`:

```scala
def interp(e: Expr, env: Env, k: Cont): Value = e match {
  case Num(n) => continue(k, NumV(n))
  case Id(x) => continue(k, lookup(x, env))
  case Fun(x, b) => continue(k, CloV(x, b, env))
  case Add(e1, e2) => interp(e1, env, AddSecondK(e2, env, k))
  case App(e1, e2) => interp(e1, env, AppArgK(e2, env, k))
  case Vcc(x, b) => interp(b, env + (x -> ContV(k)), k)
}
```

Only the `Add` and `App` cases have been changed. The `Add` case uses `AddSecondK(e2, env, k)` to represent `v1 => interp(e2, env, v2 => continue(k, numVAdd(v1, v2)))`; the `App` case uses `AppArgK(e2, env, k)` to represent `v1 => interp(e2, env, v2 => v1 match { ... })`.

The following examples check whether `interp` works properly:

```scala
// 1 + (vcc x in ((x 2) + 3))
interp(
  Add(
    Num(1),
    Vcc("x",
      Add(
       App(Id("x"), Num(2)),
       Num(3)
      )
    )
  ),
  Map.empty,
  MtK
)
// 3

// vcc x in
//   (vcc y in
//     x (1 + (vcc z in y z))
//   ) 3
interp(
  Vcc("x",
    App(
      Vcc("y",
        App(
          Id("x"),
          Add(
            Num(1),
            Vcc("z",
              App(Id("y"), Id("z"))
            )
          )
        )
      ),
      Num(3)
    )
  ),
  Map.empty,
  MtK
)
// 4
```

Note that the initial continuation for `interp` is `MtK`, which represents an identity function.

## Big-Step Semantics of KFAE

Let us define the big-step semantics of KFAE from the new implementation. There are two sorts of propositions: \(\sigma,\kappa\vdash e\Rightarrow v\), which denotes that the result of \({\tt interp}(e,\sigma,\kappa)\) is \(v\), and \(v_1\mapsto\kappa\Downarrow v_2\), which denotes that the result of \({\tt continue}(\kappa, v_1)\) is \(v_2\).

The abstract syntax of KFAE is as follows:

\[
\begin{array}{lrcl}
\text{Expression} & e & ::= & n \\
&&|& e+e \\
&&|& x \\
&&|& \lambda x.e \\
&&|& e\ e \\
&&|& \textsf{vcc}\ x;\ e \\
\text{Value} & v & ::= & n \\
&&|& \langle\lambda x.e,\sigma\rangle \\
&&|& \kappa \\
\end{array}
\]

The metavariable \(\kappa\) ranges over continuations. Continuations have not defined yet. The first thing to do is defining continuations. It is straightforward from the implementation.

```scala
sealed trait Cont
case class AddSecondK(e2: Expr, env: Env, k: Cont) extends Cont
case class DoAddK(v1: Value, k: Cont) extends Cont
case class AppArgK(e2: Expr, env: Env, k: Cont) extends Cont
case class DoAppK(v1: Value, k: Cont) extends Cont
case object MtK extends Cont
```

\[
\begin{array}{lrcl}
\text{Continuation} & \kappa & ::= & [\square+(e,\sigma)]::\kappa \\
&&|& [v+\square]::\kappa \\
&&|& [\square\ (e,\sigma)]::\kappa \\
&&|& [v\ \square]::\kappa \\
&&|& [\square] \\
\end{array}
\]

The notations are arbitrarily chosen but represent continuations intuitively. Any other notations are possible. For example, the following notations are more similar to the implementation than the previous ones:

\[
\begin{array}{lrcl}
\text{Continuation} & \kappa & ::= & +(e,\sigma,\kappa) \\
&&|& +(v,\kappa) \\
&&|& @(e,\sigma,\kappa) \\
&&|& @(v,\kappa) \\
&&|& () \\
\end{array}
\]

This article uses the former notations.

Let us define inference rules that give proofs of \(\sigma,\kappa\vdash e\Rightarrow v\). It is related to the implementation of `interp`.

```scala
def interp(e: Expr, env: Env, k: Cont): Value = e match {
  case Num(n) => continue(k, NumV(n))
  case Id(x) => continue(k, lookup(x, env))
  case Fun(x, b) => continue(k, CloV(x, b, env))
  case Add(e1, e2) => interp(e1, env, AddSecondK(e2, env, k))
  case App(e1, e2) => interp(e1, env, AppArgK(e2, env, k))
  case Vcc(x, b) => interp(b, env + (x -> ContV(k)), k)
}
```

\[
\frac
{ n\mapsto\kappa\Downarrow v }
{ \sigma,\kappa\vdash n\Rightarrow v }
\]

The conclusion, \(\sigma,\kappa\vdash n\Rightarrow v\), denotes that the result of \({\tt interp}(n, \sigma, \kappa)\) is \(v\). The result of \({\tt interp}(n, \sigma, \kappa)\) is the same as that of \({\tt continue}(\kappa, {\tt NumV}(n))\). \(n\mapsto\kappa\Downarrow v\) denotes that the result of \({\tt continue}(\kappa, {\tt NumV}(n))\) is \(v\).

\[
\frac
{ x\in{\it Domain}(\sigma) \quad \sigma(x)\mapsto\kappa\Downarrow v }
{ \sigma,\kappa\vdash x\Rightarrow v }
\]

\[
\frac
{ \langle\lambda x.e,\sigma\rangle\mapsto\kappa\Downarrow v }
{ \sigma,\kappa\vdash \lambda x.e\Rightarrow v }
\]

The rules for variables and functions are similar the rule for integers.

\[
\frac
{ \sigma,[\square+(e_2,\sigma)]::\kappa\vdash e_1\Rightarrow v }
{ \sigma,\kappa\vdash e_1+e_2\Rightarrow v }
\]

The conclusion, \(\sigma,\kappa\vdash e_1+e_2\Rightarrow v\), denotes that the result of \({\tt interp}({\tt Add}(e_1,e_2), \sigma, \kappa)\) is \(v\). The result of \({\tt interp}({\tt Add}(e_1,e_2), \sigma, \kappa)\) is the same as that of \({\tt interp}(e_1, \sigma, {\tt AddSecondK}(e_2, \sigma, \kappa))\). Note that \([\square+(e_2,\sigma)]::\kappa\) denotes \({\tt AddSecondK}(e_2, \sigma, \kappa)\). \(\sigma,\kappa'\vdash e_1\Rightarrow v\) denotes that the result of \({\tt interp}(e_1, \sigma, \kappa')\) is \(v\), where \(\kappa'\) is \([\square+(e_2,\sigma)]::\kappa\).

\[
\frac
{ \sigma,[\square\ (e_2,\sigma)]::\kappa\vdash e_1\Rightarrow v }
{ \sigma,\kappa\vdash e_1\ e_2\Rightarrow v }
\]

The rule for function applications is similar to the rule for sums.

\[
\frac
{ \sigma[x\mapsto\kappa],\kappa\vdash e\Rightarrow v }
{ \sigma,\kappa\vdash {\sf vcc}\ x;\ e\Rightarrow v }
\]

The conclusion, \(\sigma,\kappa\vdash {\sf vcc}\ x;\ e\Rightarrow v\), denotes that the result of \({\tt interp}({\tt Vcc}(x,e), \sigma, \kappa)\) is \(v\). The result of \({\tt interp}({\tt Vcc}(x,e), \sigma, \kappa)\) is the same as that of \({\tt interp}(e, \sigma[x\mapsto\kappa], \kappa)\). \(\sigma[x\mapsto\kappa],\kappa\vdash e\Rightarrow v\) denotes that the result of \({\tt interp}(e, \sigma[x\mapsto\kappa], \kappa)\) is \(v\).

Lastly, let us define inference rules that give proofs of \(v_1\mapsto\kappa\Downarrow v_2\). It is related to the implementation of `continue`.

```scala
def continue(k: Cont, v: Value): Value = k match {
  case AddSecondK(e2, env, k) => interp(e2, env, DoAddK(v, k))
  case DoAddK(v1, k) => continue(k, numVAdd(v1, v))
  case AppArgK(e2, env, k) => interp(e2, env, DoAppK(v, k))
  case DoAppK(v1, k) => v1 match {
    case CloV(xv1, ev1, sigmav1) =>
      interp(ev1, sigmav1 + (xv1 -> v), k)
    case ContV(k) => continue(k, v)
  }
  case MtK => v
}
```

\[
\frac
{ \sigma,[v_1+\square]::\kappa\vdash e_2\Rightarrow v_2 }
{ v_1\mapsto[\square+(e_2,\sigma)]::\kappa\Downarrow v_2 }
\]

The conclusion, \(v_1\mapsto[\square+(e_2,\sigma)]::\kappa\Downarrow v_2\), denotes that the result of \({\tt continue}({\tt AddSecondK}(e_2, \sigma, \kappa), v_1)\) is \(v_2\). The result of \({\tt continue}({\tt AddSecondK}(e_2, \sigma, \kappa), v_1)\) is the same as that of \({\tt interp}(e_2, \sigma, {\tt DoAddK}(v_1, \kappa))\). Note that \([v_1+\square]\) denotes \({\tt DoAddK}(v_1, \kappa)\). \(\sigma,\kappa'\vdash e_2\Rightarrow v_2\) denotes that the result of \({\tt interp}(e_2, \sigma, \kappa')\) is \(v_2\) where \(\kappa'\) is \([v_1+\square]\).

\[
\frac
{ n_1+n_2\mapsto\kappa\Downarrow v }
{ n_2\mapsto[n_1+\square]::\kappa\Downarrow v }
\]

The conclusion, \(n_2\mapsto[n_1+\square]::\kappa\Downarrow v\), denotes that the result of \({\tt continue}({\tt DoAddK}({\tt NumV}(n_1), \kappa), {\tt NumV}(n_2))\) is \(v\). The result of \({\tt continue}({\tt DoAddK}({\tt NumV}(n_1), \kappa), {\tt NumV}(n_2))\) is the same as that of \({\tt continue}(\kappa, {\tt numVAdd}({\tt NumV}(n_1), {\tt NumV}(n_2)))\). Note that \({\tt numVAdd}({\tt NumV}(n_1), {\tt NumV}(n_2))\) equals \({\tt NumV}(n_1+n_2)\). \(n_1+n_2\mapsto\kappa\Downarrow v\) denotes that the result of \({\tt continue}(\kappa, {\tt NumV}(n_1+n_2))\) is \(v\).

\[
\frac
{ \sigma,[v_1\ \square]::\kappa\vdash e\Rightarrow v_2 }
{ v_1\mapsto[\square\ (e,\sigma)]::\kappa\Downarrow v_2 }
\]

This rule is similar to the rule when the continuation is \([\square+(e_2,\sigma)]\).

\[
\frac
{ \sigma[x\mapsto v_2],\kappa\vdash e\Rightarrow v }
{ v_2\mapsto[\langle\lambda x.e,\sigma\rangle\ \square]::\kappa\Downarrow v }
\]

The conclusion, \(v_2\mapsto[\langle\lambda x.e,\sigma\rangle\ \square]::\kappa\Downarrow v\), denotes that the result of \({\tt continue}({\tt DoAppK}({\tt CloV}(x,e,\sigma), \kappa), v_2)\) is \(v\). The result of \({\tt continue}({\tt DoAppK}({\tt CloV}(x,e,\sigma), \kappa), v_2)\) is the same as that of \({\tt interp}(e, \sigma[x\mapsto v_2], \kappa)\). \(\sigma[x\mapsto v_2],\kappa\vdash e\Rightarrow v\) denotes that the result of \({\tt interp}(e, \sigma[x\mapsto v_2], \kappa)\) is \(v\).

\[
\frac
{ v_2\mapsto\kappa_1\Downarrow v }
{ v_2\mapsto[\kappa_1\ \square]::\kappa\Downarrow v }
\]

The conclusion, \(v_2\mapsto[\kappa_1\ \square]::\kappa\Downarrow v\), denotes that the result of \({\tt continue}({\tt DoAppK}({\tt ContV}(\kappa_1), \kappa), v_2)\) is \(v\). The result of \({\tt continue}({\tt DoAppK}({\tt ContV}(\kappa_1), \kappa), v_2)\) is the same as that of \({\tt continue}(\kappa_1, v_2)\). \(v_2\mapsto\kappa_1\Downarrow v\) denotes that the result of \({\tt continue}(\kappa_1, v_2)\) is \(v\).

\[
v\mapsto[\square]\Downarrow v
\]

The conclusion, \(v\mapsto[\square]\Downarrow v\), denotes that the result of \({\tt continue}({\tt MtK}, v)\) is \(v\). The result of \({\tt continue}({\tt MtK}, v)\) is actually \(v\).

## Acknowledgments

I thank professor Ryu for giving feedback on the article.
