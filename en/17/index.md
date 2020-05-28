The article deals with continuations. It defines *redexes* and *continuations*. It shows what *continuation-passing style* is and implements an interpreter with continuation-passing style. It defines *small-step semantics* of FAE and explains how the semantics and continuations are related.

## Redexes and Continuations

Evaluating an expression that is not a value requires one or more steps of computation. For example, \(1+2\) needs computation that adds \(1\) and \(2\) to produce \(3\). \((1+2)+3\) needs two steps of computation: adding \(1\) and \(2\) to get \(3\) and adding \(3\) and \(3\) to get \(6\). An order among steps of computation exists. \(1+2\) must yield \(3\) before \(3\) is added to \(3\). It is impossible to add \(3\) to \(3\) before \(1+2\) results in \(3\).

Let \(E\) denote a step of computation. A step of computation and \(E\) are informal concepts. The article introduces them for intuitive explanations. Evaluating expression \(e\) requires multiple steps of computation, which are from \(E_1\) to \(E_n\). Consider \((1+2)+3\). \(E_1\) is adding \(1\) and \(2\); \(E_2\) is adding \(3\) to the result of \(E_1\). \(E_2\) depends on the result of \(E_1\). The order cannot change.

Multiple steps of computation can be divided into two parts: the former and latter parts. Assume steps from \(E_1\) to \(E_n\). For some \(i\) between \(1\) and \(n\), the former includes the steps from \(E_1\) to \(E_i\), and the latter includes the steps from \(E_{i+1}\) to \(E_n\). The latter relies on the result of the former. A subexpression evaluated by the former is a redex, which mean a reducible expression. The latter part, which finishes the evaluation with the value of the redex, is a continuation. Let the steps from \(E_1\) to \(E_i\) evaluate \(e'\), which is a subexpression of \(e\). \(e'\) is the redex. Computation proceeded by the steps from \(E_{i+1}\) to \(E_n\) is the continuation. As an example, consider \((1+2)+e\) again. Let \(1+2\) be a redex. The continuation is adding \(3\) to the result of the redex.

Consider another example: \((1+2)-(3+4)\). Let the former part and the latter part respectively be adding \(1\) and \(2\) and subtracting \(3+4\) from the result of the former part. \(1+2\) is a redex. Subtracting \(3+4\) from the result of the continuation. \(1+2\) produces \(3\). Continuing the evaluation equals evaluating \(3-(3+4)\). It follows from what the continuation is. The new expression can be divided into a redex and a continuation again. Computing \(3+4\) allows subtracting the result from \(3\) and finishing the evaluation. A redex is \(3+4\), and the continuation is subtracting the result of the redex from \(3\). \(3+4\) yields \(7\). Continuing the evaluation is evaluating \(3-7\). It can be divided as well. The expression itself is the only redex. The continuation is doing nothing. \(3-7\) results in \(-4\). There is no more computation to do. The evaluation finishes, and its result is \(-4\). The following table summarizes the process:

\[
\begin{array}{ccccc}
&\text{Expression being Evaluated} & \text{Redex} & \text{Continuation} & \text{Result of Redex} \\
& (1+2)-(3+4) & 1+2 & \text{Subtract }3+4\text{ from the result} & 3 \\
\rightarrow&3-(3+4) & 3+4 & \text{Subtract the result from }3 & 7 \\
\rightarrow&3-7 & 3-7 & \text{Do nothing} & -4
\end{array}
\]

Every continuation makes a result with the result of its redex. The results of both redex and continuing the evaluation are values. A continuation can be considered as a function from a value to a value. The below table rewrites the above table; it represents continuations with lambda abstractions. The article uses lambda abstractions to express continuations from this point. Note the following fact: when \(e'\) and \(k\) respectively are a redex and the continuation of \(e\), the result of \(e\) equals \(k\ v\) where \(v\) is the result of \(e'\) because \(k\) is a function.

\[
\begin{array}{ccccc}
&\text{Expression being Evaluated} & \text{Redex} & \text{Continuation} & \text{Result of Redex} \\
& (1+2)-(3+4) & 1+2 & \lambda v.v-(3+4) & 3 \\
\rightarrow&3-(3+4) & 3+4 & \lambda v.3-v & 7 \\
\rightarrow&3-7 & 3-7 & \lambda v.v & -4
\end{array}
\]

Multiple redexes can exist for an expression. The following table provides another computing process of \((1+2)-(3+4)\):


\[
\begin{array}{ccccc}
&\text{Expression being Evaluated} & \text{Redex} & \text{Continuation} & \text{Result of Redex} \\
& (1+2)-(3+4) & (1+2)-(3+4) & \lambda v.v & -4 \\
\end{array}
\]

Be aware of that \(3+4\) cannot be a redex of \((1+2)-(3+4)\). The evaluation of \(1+2\) always precedes that of \(3+4\). Since a redex is the former part, it never equals \(3+4\).

The following tables show other examples of redexes and continuations:

\[
\begin{array}{ccccc}
&\text{Expression being Evaluated} & \text{Redex} & \text{Continuation} & \text{Result of Redex} \\
& 1+2 & 1+2 & \lambda v.v & 3
\end{array}
\]

\[
\begin{array}{ccccc}
&\text{Expression being Evaluated} & \text{Redex} & \text{Continuation} & \text{Result of Redex} \\
& (1+2)+3 & 1+2 & \lambda v.v+3 & 3 \\
\rightarrow&3+3 & 3+3 & \lambda v.v & 6
\end{array}
\]

\[
\begin{array}{ccccc}
&\text{Expression being Evaluated} & \text{Redex} & \text{Continuation} & \text{Result of Redex} \\
& ((1+2)-3)+4 & 1+2 & \lambda v.(v-3)+4 & 3 \\
\rightarrow&(3-3)+4 & 3-3 & \lambda v.v+4 & 0 \\
\rightarrow&0+4 & 0+4 & \lambda v.v & 4
\end{array}
\]

\[
\begin{array}{ccccc}
&\text{Expression being Evaluated} & \text{Redex} & \text{Continuation} & \text{Result of Redex} \\
& ((1+2)-3)+4 & (1+2)-3 &\lambda v.v+4 & 0 \\
\rightarrow&0+4 & 0+4 & \lambda v.v & 4
\end{array}
\]

Adding lambda abstractions and function applications does not make any changes for finding redexes and continuations. The following tables use substitution instead of closures and environments:

\[
\begin{array}{ccccc}
&\text{Expression being Evaluated} & \text{Redex} & \text{Continuation} & \text{Result of Redex} \\
& (\lambda x.x)(1+1) & 1+1 & \lambda v.(\lambda x.x)v & 2 \\
\rightarrow& (\lambda x.x)2 & (\lambda x.x)2 & \lambda v.v & 2
\end{array}
\]

\[
\begin{array}{ccccc}
&\text{Expression being Evaluated} & \text{Redex} & \text{Continuation} & \text{Result of Redex} \\
& (\lambda x.\lambda y.x+y)\ 1\ 2 & (\lambda x.\lambda y.x+y)\ 1& \lambda v.v\ 2 & \lambda y.1+y \\
\rightarrow & (\lambda y.1+y)\ 2 & (\lambda y.1+y)\ 2 & \lambda v.v & 1+2 \\
\rightarrow & 1+2 & 1+2 & \lambda v.v & 3
\end{array}
\]

Substitution helps intuitive understanding while interpreters use closures and environments. The below table describes redexes and continuations of the same expressions but uses closures and environments unlike the above. Let \(\sigma\vdash e\) denote evaluation of \(e\) under \(\sigma\). The notation is informal but effective for understanding. Besides, let closures be considered as expressions. For instance, \(\langle\lambda x.x,\emptyset\rangle\ 1\) is an expression applying closure \(\langle\lambda x.x,\emptyset\rangle\) to \(1\).

\[
\begin{array}{ccccc}
&\text{Expression being Evaluated} & \text{Redex} & \text{Continuation} & \text{Result of Redex} \\
& (\lambda x.x)(1+1) & \lambda x.x & \lambda v.v(1+1) & \langle\lambda x.x,\emptyset\rangle \\
\rightarrow&\langle\lambda x.x,\emptyset\rangle(1+1) & 1+1 & \lambda v.(\langle\lambda x.x,\emptyset\rangle)v & 2 \\
\equiv&\langle\lambda x.x,\emptyset\rangle(1+1) & 1+1 & \lambda v.[x\mapsto v]\vdash x & 2 \\
\rightarrow& [x\mapsto 2]\vdash x & [x\mapsto 2]\vdash x & \lambda v.v & 2 \\
\end{array}
\]

Symbol \(\equiv\) implies that the line rewrites the line before it without any computation.

\[
\begin{array}{ccccc}
&\text{Expression being Evaluated} & \text{Redex} & \text{Continuation} & \text{Result of Redex} \\
& (\lambda x.\lambda y.x+y)\ 1\ 2 & \lambda x.\lambda y.x+y & \lambda v.v\ 1\ 2 & \langle\lambda x.\lambda y.x+y,             \emptyset\rangle \\
\rightarrow&\langle\lambda x.\lambda y.x+y,\emptyset\rangle\ 1\ 2 &
\langle\lambda x.\lambda y.x+y,\emptyset\rangle\ 1 & \lambda v.v\ 2 &  \\
\equiv&\langle\lambda x.\lambda y.x+y,\emptyset\rangle\ 1\ 2 &
[x\mapsto 1]\vdash\lambda y.x+y & \lambda v.v\ 2 &
\langle\lambda y.x+y,[x\mapsto 1]\rangle \\
\rightarrow& \langle\lambda y.x+y,[x\mapsto 1]\rangle\ 2 &
\langle\lambda y.x+y,[x\mapsto 1]\rangle\ 2 & \lambda v.v & \\
\equiv& \langle\lambda y.x+y,[x\mapsto 1]\rangle\ 2 &
[x\mapsto 1,y\mapsto 2]\vdash x+y & \lambda v.v & 3 \\
\end{array}
\]

## Continuation-Passing Style

Continuation-passing style is a style of programming. Programs following the style passes the result to the current continuation when no more computation exists and calls a function with a new proper continuation otherwise. Every function in the programs takes the current continuation as an argument. The body of every function must call a function once and only once. Then function being called can be the continuation or some other function.

Consider a function calculating the factorial of a given integer. The following function does not use continuation-passing style:

```scala
def factorial(n: Int): Int =
  if (n <= 1) 1
  else n * factorial(n - 1)

factorial(4)  // 24
factorial(7)  // 5040
```

On the other hand, the `factorialC` function uses the style. It takes two arguments: the first is an integer; the second is the continuation. For some integer `n` and some function `k` from an integer to integer, `factorialC(n,k)` yields the value same as the result of `k(factorial(n))`.

```scala
def factorialC(n: Int, k: Int => Int): Int =
  if (n <= 1) k(1)
  else factorialC(n - 1, x => k(n * x))

factorialC(4, x => x)  // 24
factorialC(7, x => x)  // 5040
```

When `n` is less than `1`, `factorial(n)` equals `1`. Thus, `k(factorial(n))` is `k(1)`. The function returns `k(1)` in such cases.

Otherwise, `factorial(n)` equals `n * factorial(n - 1)`. The function must return a value that equals to `k(n * factorial(n - 1))`. The body cannot directly use the expression as it calls functions twice. It avoids continuation-passing style.

Despite the incorrectness, `k(n * factorial(n - 1))` is a good starting point. The expression multiplies `factorial(n - 1)` by `n` and passes the result to `k`. Define function `f` as the following:

```scala
def f(x: Int): Int = k(n * x)
```

The expression is equivalent to `f(factorial(n - 1))`. Since the result of `k(factorial(n))` equals that of `factorialC(n, k)`, `factorialC(n - 1, f)` can replace `f(factorial(n - 1))`. Anonymous function `x => k(n * x)` exactly does what `f` does. Finally, `factorialC(n - 1, x => k(n * x))` appears. The following summarizes the transformation:

\[
\begin{array}{cl}
& \texttt{k(n * factorial(n – 1))} \\
= & \texttt{(x => k(n * x))(factorial(n – 1))} \\
= & \texttt{factorialC(n – 1, x => k(n * x))} \\
\end{array}
\]

Another explanation is possible. Let a redex be the factorial of `n` and `k` refer to the continuation. The factorial of `n` equals multiplying the factorial of `n - 1` by `n`. Therefore, the evaluation can be divided with another way: a redex is the factorial of `n - 1`; the continuation is multiplying the result by `n` and passing the result of the multiplication to `k`. The explanation also leads to `factorialC(n - 1, x => k(n * x))`.

Programs using continuation-passing style never use the result of a function call. They pass any additional computation as continuations. `n * factorial(n - 1)` differs from the style because it uses the result of the function call. The continuation of ` factorial(n - 1)` is multiplying `n` by the result. On the other hand, continuation-passing style always explicitly express the current continuation and passes the continuation as an argument. `factorialC(n - 1, x => k(n * x))` implies that the continuation of computing the factorial of `n - 1` is `x => k(n * x)`. Since the expression explicitly passes the continuation, it follows the style.

Changing the return type of every function to `Unit` allows checking whether a program is using continuation-passing style.

```scala
def factorialC(n: Int, k: Int => Unit): Unit =
  if (n <= 1) k(1)
  else factorialC(n - 1, x => k(n * x))

factorialC(4, println)  // 24
factorialC(7, println)  // 5040
```

Since the return type is `Unit`, every return value is useless. It always is `()`, which contains zero information. Expressions such as `k(n * factorialC(n - 1, x => x))` produces a compile error because it is impossible to multiply `()`, which is not a number, by `n`.

The `Unit` return type follows the definition of a continuation. A continuation is all of the remaining computation at some point. After calling the continuation, there must be nothing to do. A program cannot use the result of the continuation for any purposes. Calling the continuation equals finishing the evaluation. Continuation-passing style prohibits "coming back."

Continuation-passing style is a style that programs pass continuations from functions to functions. All the other descriptions are analogical to it. "Call a function once and only once." "Express the current continuation explicitly and pass it as an argument." "Every return value is useless." "Once a function is called, it never returns."

The article finally implements an interpreter of FAE with continuation-passing style. The abstract syntax of FAE follows:

```scala
sealed trait Expr
case class Num(n: Int) extends Expr
case class Add(l: Expr, r: Expr) extends Expr
case class Sub(l: Expr, r: Expr) extends Expr
case class Id(x: String) extends Expr
case class Fun(x: String, b: Expr) extends Expr
case class App(f: Expr, a: Expr) extends Expr

sealed trait Value
case class NumV(n: Int) extends Value
case class CloV(p: String, b: Expr, e: Env) extends Value

type Env = Map[String, Value]
def lookup(x: String, env: Env): Value =
  env.getOrElse(x, throw new Exception)
```

`Cont` is the type of a continuation:

```scala
type Cont = Value => Value
```

The original signature of the `interp` function equals the following:

```scala
def interp(e: Expr, env: Env): Value = ...
```

To follow continuation-passing style, the function needs an additional parameter to take the current continuation as an argument.

```scala
def interp(e: Expr, env: Env, k: Cont): Value = ...
```

For any `e`, `env`, and `k`, `interp(e, env, k)` must produce the same result as that of `k(interp(e, env))`.

`interp(Num(n), env)` equals `NumV(n)`. Hence, `k(interp(Num(n), env))` equals `k(NumV(n))`.

```scala
case Num(n) => k(NumV(n))
```

The `Id` and `Fun` cases are similar.

```scala
case Id(x) => k(lookup(x, env))
case Fun(x, b) => k(CloV(x, b, env))
```

Consider the explanation of the first section of the article. Let \(e\) and \(k\) respectively denote a redex and the continuation. \(k\) is a function. The evaluation equals passing the result of \(e\) to \(k\), which is evaluating \(k\ e\).

Let \(k\) be the continuation of \(e_1+e_2\). The whole evaluation equals \(k(e_1+e_2)\). \(e_1+e_2\) is equivalent to \(\lambda v_1.v_1+e_2)e_1\). \(k((\lambda v_1.v_1+e_2)e_1)\) replaces \(k(e_1+e_2)\). \), and \(k((\lambda v_1.v_1+e_2)e_1)\) equals \(\lambda v_1.k(v_1+e_2))e_1\). Focus on \(v_1+e_2\). It is equivalent to \(\lambda v_2.v_1+v_2)e_2\). \(k(v_1+e_2)\) becomes \(k((\lambda v_2.v_1+v_2)e_2)\), which is \((\lambda v_2.k(v_1+v_2))e_2\). Replacing \(k(v_1+e_2)\) with \((\lambda v_2.k(v_1+v_2))e_2\) in \(\lambda v_1.k(v_1+e_2))e_1\) produces \((\lambda v_1.(\lambda v_2.k(v_1+v_2))e_2)e_1\). Therefore, the evaluation is newly divided into redex \(e_1\) and continuation \(\lambda v_1.(\lambda v_2.k(v_1+v_2))e_2\). The body of the continuation is \((\lambda v_2.k(v_1+v_2))e_2\), which can be divided into redex \(e_2\) and continuation \(\lambda v_2.k(v_1+v_2)\). The following summarizes the transformation:

\[
\begin{array}{rl}
& k(e_1+e_2) \\
= & k((\lambda v_1.v_1+e_2)e_1) \\
= & (\lambda v_1.k(v_1+e_2))e_1 \\
= & (\lambda v_1.k((\lambda v_2.v_1+v_2)e_2))e_1 \\
= & (\lambda v_1.(\lambda v_2.k(v_1+v_2))e_2)e_1 \\
\end{array}
\]

Let an Scala expression express the same thing. \(\lambda v_2.k(v_1+v_2)\) equals `v2 => k(v1 + v2)`. It is the continuation of \(e_2\). `interp(e2, env, v2 => k(v1 + v2))` expresses the evaluation. The continuation of \(e_1\) is `v1 => interp(e2, env, v2 => k(v1 + v2))`. The complete code follows:

```scala
case Add(e1, e2) =>
  interp(e1, env, v1 =>
    interp(e2, env, v2 =>
      k(v1 + v2)))
```

The only problem is that `v1 + v2` is impossible. The function must remove `NumV` and wrap the result with `NumV` again.

```scala
def numVAdd(v1: Value, v2: Value): Value = {
  val NumV(n1) = v1
  val NumV(n2) = v2
  NumV(n1 + n2)
}
```

The `numVAdd` function corrects the code:

```scala
case Add(e1, e2) =>
  interp(e1, env, v1 =>
    interp(e2, env, v2 =>
      k(numVAdd(v1, v2))))
```

Another intuitive explanation for the code exists. The continuation of `Add(e1, e2)` is `k`. The evaluation requires three steps: letting `v1` denote the result of evaluating `e1`; letting `v2` denote the result of evaluating `e2`; passing `numVAdd(v1, v2)` to `k`. Thus, the continuation of `e1` is evaluating `e2`, which yields `v2`, and passing `numVAdd(v1, v2)` to `k`. The continuation is evaluating `e2` with a continuation that is calling the result `v2` and passing `numVAdd(v1, v2)` to `k`. The continuation of `e2` is `v2 => k(numVAdd(v1, v2))`. Evaluating `e2` is `interp(e2, env, v2 => k(numVAdd(v1, v2)))`. The continuation of `e1` is `v1 => interp(e2, env, v2 => k(numVadd(v1, v2)))`.

The following summarization is another interpretation of the code.

Things to do are three steps of computation:

1. Evaluate `e1` and call the result `v1`.
2. Evaluate `e2` and call the result `v2`.
3. Evaluate `numVAdd(v1, v2)` and pass the result of `k`.

The continuation at step 1 includes steps 2 and 3. It equals the following:

1. Take `v1` as an argument.
2. Evaluate `e2` and call the result `v2`.
3. Evaluate `numVAdd(v1, v2)` and pass the result of `k`.

The continuation at step 2 is step 3. It equals the following:

1. Take `v2` as an argument.
2. Evaluate `numVAdd(v1, v2)` and pass the result of `k`.

Step 2 equals `k(numVAdd(v1, v2))`. The continuation is `v2 => k(numVAdd(v1, v2))`. Steps 2 and 3 of the second computation is `interp(e2, env, v2 => k(numVAdd(v1, v2)))`. The continuation of `e1` is `v1 => interp(e2, env, v2 => k(numVAdd(v1, v2)))`. The whole evaluation equals `interp(e1, env, v1 => interp(e2, env, v2 => k(numVAdd(v1, v2))))`.

The `Sub` case is similar to the `Add` case.

```scala
def numVSub(v1: Value, v2: Value): Value = {
  val NumV(n1) = v1
  val NumV(n2) = v2
  NumV(n1 - n2)
}
```

```scala
case Sub(e1, e2) =>
  interp(e1, env, v1 =>
    interp(e2, env, v2 =>
      k(numVSub(v1, v2))))
```

The `App` case has some differences. Let a redex and the continuation respectively be \(e_1\ e_2\) and \(k\). It is equivalent to that the continuation of \(e_1\) is \(\lambda v_1.(\lambda v_2.k(v_1\ v_2))e_2\). Assume that \(v_1\) is \(\langle\lambda x_{v1}.e_{v1},\sigma_{v1}\rangle\). \(v_1\ v_2\) implies evaluating the body of \(v_1\) under the environment of \(v_1\) with \(v_2\). \(\sigma_{v1}[x_{v1}\mapsto v_2]\vdash e_{v1}\) denotes it. As a result, \(\lambda v_1.(\lambda v_2.k(v_1\ v_2))e_2\) equals \(\lambda v_1.(\lambda v_2.k(\sigma_{v1}[x_{v1}\mapsto v_2]\vdash e_{v1}))e_2\). Note that \(k(\sigma_{v1}[x_{v1}\mapsto v_2]\vdash e_{v1})\) means evaluating \(e_{v1}\) under \(\sigma_{v1}[x_{v1}\mapsto v_2]\) with continuation \(k\), which is `interp(ev1, sigmav1 + (xv1 -> v2), k)`.

```scala
case App(e1, e2) =>
  interp(e1, env, v1 =>
    interp(e2, env, v2 =>
      interp(ev1, sigmav1 + (xv1 -> v2), k)))
```

It contains free variables: `ev1`, `sigmav1`, and `xv1`.

```scala
case App(e1, e2) =>
  interp(e1, env, v1 =>
    interp(e2, env, v2 => {
      val CloV(xv1, ev1, sigmav1) = v1
      interp(ev1, sigmav1 + (xv1 -> v2), k)
    })
  )
```

The code does not directly call `k`. Instead of calling `k`, it passes `k` as an argument. Look at `interp(ev1, sigmav1 + (xv1 -> v2), k)`. `k` is passed to the `interp` function and therefore eventually called.

The following is a complete interpreter:

```scala
def interp(e: Expr, env: Env, k: Cont): Value = e match {
  case Num(n) => k(NumV(n))
  case Id(x) => k(lookup(x, env))
  case Fun(x, b) => k(CloV(x, b, env))
  case Add(e1, e2) =>
    interp(e1, env, v1 =>
      interp(e2, env, v2 =>
        k(numVAdd(v1, v2))))
  case Sub(e1, e2) =>
    interp(e1, env, v1 =>
      interp(e2, env, v2 =>
        k(numVSub(v1, v2))))
  case App(e1, e2) =>
    interp(e1, env, v1 =>
      interp(e2, env, v2 => {
        val CloV(xv1, ev1, sigmav1) = v1
        interp(ev1, sigmav1 + (xv1 -> v2), k)
      })
    )
}
```

One can check its correctness as the following:

```scala
// (1 + 2) - (3 + 4)
interp(
  Sub(
    Add(Num(1), Num(2)),
    Add(Num(3), Num(4))
  ),
  Map.empty,
  x => x
)
// -4

// (lambda x.lambda y.x + y) 1 2
interp(
  App(
    App(
      Fun("x", Fun("y",
        Add(Id("x"), Id("y")))),
      Num(1)
    ),
    Num(2)
  ),
  Map.empty,
  x => x
)
// 3
```

To check whether the interpreter uses continuation-passing style, change the return types of continuations and the `interp` function to `Unit`.

```scala
type Cont = Value => Unit
def interp(e: Expr, env: Env, k: Cont): Unit = ...
```

The interpreter works well even after the change.

```scala
// (1 + 2) - (3 + 4)
interp(
  Sub(
    Add(Num(1), Num(2)),
    Add(Num(3), Num(4))
  ),
  Map.empty,
  println
)
// -4

// (lambda x.lambda y.x + y) 1 2
interp(
  App(
    App(
      Fun("x", Fun("y",
        Add(Id("x"), Id("y")))),
      Num(1)
    ),
    Num(2)
  ),
  Map.empty,
  println
)
// 3
```

To clarify what the interpreter does, I provide a program showing intermediates redexes, continuations, and environments. One can download [the "FAE.scala" file](https://raw.githubusercontent.com/Medowhill/pl_articles/master/kr/17/FAE.scala). Go to a directory containing the file. From the Scala REPL, load the file and import everything inside `FAE`. Pass an FAE expression to the `run` function.

```
$ scala
Welcome to Scala.
Type in expressions for evaluation. Or try :help.

scala> :load FAE.scala
args: Array[String] = Array()
Loading FAE.scala...
defined object FAE

scala> import FAE._
import FAE._

scala> run(...)
```

Some examples follow:

```scala
// (1 + 2) - (3 + 4)
run(
  Sub(
    Add(Num(1), Num(2)),
    Add(Num(3), Num(4))
  )
)
```

```
((1 + 2) - (3 + 4)) | □                 | ∅
(1 + 2)             | □ - (3 + 4)       | ∅
1                   | (□ + 2) - (3 + 4) | ∅
2                   | (1 + □) - (3 + 4) | ∅
1 + 2               | □ - (3 + 4)       | ∅
(3 + 4)             | (3 - □)           | ∅
3                   | (3 - (□ + 4))     | ∅
4                   | (3 - (3 + □))     | ∅
3 + 4               | (3 - □)           | ∅
3 - 7               | □                 | ∅
-4
```

```scala
// (lambda x.lambda y.x + y) 1 2
run(
  App(
    App(
      Fun("x", Fun("y",
        Add(Id("x"), Id("y")))),
      Num(1)
    ),
    Num(2)
  )
)
```

```
((λx.λy.(x + y) 1) 2) | □                          | ∅
(λx.λy.(x + y) 1)     | (□ 2)                      | ∅
λx.λy.(x + y)         | ((□ 1) 2)                  | ∅
1                     | ((<λx.λy.(x + y), ∅> □) 2) | ∅
λy.(x + y)            | (□ 2)                      | [x -> 1]
2                     | (<λy.(x + y), [x -> 1]> □) | ∅
(x + y)               | □                          | [x -> 1, y -> 2]
x                     | (□ + y)                    | [x -> 1, y -> 2]
y                     | (1 + □)                    | [x -> 1, y -> 2]
1 + 2                 | □                          | [x -> 1, y -> 2]
3
```

A single line shows the redex, the continuation, and the environment at each step. A square of a continuation is a place whom the result of the redex replaces. For example, `3 - □` equals \(\lambda v.3-v\).

Continuation-passing style seems needless. One may think it complexify the implementation of the interpreter. It is true that continuation-passing style creates unnecessary difficulties in the implementation. The style makes every function call be a tail call. It is one benefit. If an interpreter is written in a language with tail-call optimization, continuation-passing style prevents stack overflow. However, the Scala complier optimizes only tail-recursive calls. The possibility of stack overflow remains despite the use of continuation-passing style as the article uses Scala. The next article will give a reason that this article uses continuation-passing style to implement the interpreter. The next article adds a new feature whom an interpreter using continuation-passing style can easily support to FAE.

## Small-Step Semantics

This section defines semantics that explicitly shows continuations for FAE. Every hitherto article has defined semantics in big-step style. Big-step semantics is intuitive, and its inference rules give a nice clue to interpreter implementers. However, it is a bad way to formalize continuations. Its single inference rule defines the value of an expression. For instance, the big-step rule of a sum follows:

\[
\frac
{ \sigma\vdash e_1\Rightarrow v_1\quad
  \sigma\vdash e_2\Rightarrow v_2 }
{ \sigma\vdash e_1+e_2\Rightarrow v_1+v_2 }
\]

The rule implies that the value of \(e_1+e_2\) is \(v_1+v_2\), but lacks a way to evaluate \(e_1\) and \(e_2\). The only given step of computation is adding \(v_1\) and \(v_2\). Moreover, the rule does not specify the order of the computation. The evaluation of \(e_1\) can precede that of \(e_2\), and vice versa. Continuations rely on the two concepts. Defining them requires every step of computation and the exact order of computation. For this reason, the article avoids using big-step semantics.

Small-step semantics is another way to define semantics of a language. It defines a relation over expressions and expressions. The relation shows an expression obtained by applying one step of computation to a given expression. Small-step semantics clarifies every step of computation and the order among them. It fits defining continuations.

Small-step semantics defines a relation describing one step of computation. *Reduction* refers to proceeding computation by a single step. A state is reduced to a new state. Various forms of small-step semantics vary in defining a state. Usually, a state is an expression. In this article, it is not the case. The later part will show what a state is. Defining small-step semantics is defining reduction so that another name of small-step semantics is reduction semantics.

The abstract syntax of FAE is the following:

\[
\begin{array}{lrcl}
\text{Integer} & n & \in & \mathbb{Z} \\
\text{Variable} & x & \in & \textit{Id} \\
\text{Expression} & e & ::= & n \\
&& | & e + e \\
&& | & e - e \\
&& | & x \\
&& | & \lambda x.e \\
&& | & e\ e \\
\text{Value} & v & ::= & n \\
&& | & \langle\lambda x.e,\sigma\rangle \\
\text{Environment} & \sigma & \in &
\textit{Id} \hookrightarrow \text{Value}
\end{array}
\]

A state of FAE is a pair of a computation stack and a value stack. The following defines computation and value stacks:

\[
\begin{array}{lrcl}
\text{Computation Stack} & k & ::= & \square \\
&& | & \sigma\vdash e::k \\
&& | & (+)::k \\
&& | & (-)::k \\
&& | & (@)::k \\
\text{Value Stack} & s & ::= & \blacksquare \\
&& | & v::s
\end{array}
\]

A value stack is a stack containing values. Since it is a stack, push and pop are only available operations. The black square, \(\blacksquare\), denotes the empty value stack.

A computation stack is a stack containing remaining steps of computation. The top of the stack represents the first computation to be done. The white square, \(\square\), denotes the empty computation stack. The empty computation stack implies that there are no more things to do. It is the end of the evaluation. Four kinds of computation exist. \(\sigma\vdash e\) evaluates \(e\) under \(\sigma\) and then pushes the result into the value stack. \((+)\) pops two values from the value stack and then pushes the sum of the values into the value stack. \((-)\) pops two values from the value stack and then pushes the difference of the values into the value stack. The difference equals an integer obtained by subtracting the firstly-popped value from the secondly-popped value. \((@)\) pops two values from the value stack, applies the secondly-popped value to the firstly-popped value, and pushes the result of the application to the value stack. Every completed computation disappears from the computation stack.

\(k\ ||\ s\) denotes that the computation stack and the value stack of the current state respectively are \(k\) and \(s\). \(k\) includes remaining steps of computation; \(s\) includes values used by the steps.

\(\rightarrow\) denotes reduction. Reduction produces a new state from a given state. Therefore, reduction is a relation over computations stacks, value stacks, computation stack, and value stacks.

\[\rightarrow \subseteq
\text{Computation Stack}\times\text{Value Stack}
\times\text{Computation Stack}\times\text{Value Stack} \]

\(k_1\ ||\ s_1\rightarrow k_2\ ||\ s_2\) means reducing \(k_1\ ||\ s_1\) results in \(k_2\ ||\ s_2\).

Repeating reduction until no more reduction is possible evaluates a state. If the computation stack is empty, reduction is impossible. If a value popped from the value stack is not an integer, both addition or subtraction are impossible so that reduction cannot happen. If the secondly-popped value is not a closure, a function application is impossible. It also makes reduction impossible. The first case and the remaining cases differ from each other. The empty stack implies the end of the evaluation. It corresponds to a normal termination of a program. On the other hand, an improper value in the value stack prevents further reduction although computation remains. It corresponds to an abnormal termination of a program due to a run-time error.

\(\rightarrow^\ast\) denotes repeated reduction. \(k_1\ ||\ s_1\rightarrow^\ast k_n\ ||\ s_n\) implies \(k_1\ ||\ s_1\rightarrow k_2\ ||\ s_2\), \(k_2\ ||\ s_2\rightarrow k_3\ ||\ s_3\), …, and \(\(k_{n-1}\ ||\ s_{n-1}\rightarrow k_n\ ||\ s_n\). The following formalizes the relation:

\[k\ ||\ s\rightarrow^{\ast}k\ ||\ s\]

\[
\frac
{ k\ ||\ s\rightarrow^{\ast}k'' \ ||\ s'' \quad
  k'' \ ||\ s'' \rightarrow k'\ ||\ s' }
{ k\ ||\ s\rightarrow^{\ast}k'\ ||\ s' }
\]

Note that \(\rightarrow^\ast\) does not require reduction to be impossible. It means zero or more times of reduction. All of \(k_1\ ||\ s_1\rightarrow^\ast k_1\ ||\ s_1\), \(k_1\ ||\ s_1\rightarrow^\ast k_2\ ||\ s_2\), and \(k_1\ ||\ s_1\rightarrow^\ast k_3\ ||\ s_3\) are true.

\(\rightarrow^\ast\) is a *reflexive transitive closure* of \(\rightarrow\). First, \(\rightarrow^\ast\) subsumes \(\rightarrow\) because \(k_1\ ||\ s_1\rightarrow k_2\ ||\ s_2\) implies \(k_1\ ||\ s_1\rightarrow^\ast k_2\ ||\ s_2\). Second, \(\rightarrow^\ast\) is reflexive because for any \(k\) and \(s\), \(k\ ||\ s\rightarrow^\ast k\ ||\ s\). Third, \(\rightarrow^\ast\) is transitive because for any \(k_1\), \(s_1\), \(k_2\), \(s_2\), \(k_3\), and \(s_3\), \(k_1\ ||\ s_1\rightarrow^\ast k_3\ ||\ s_3\) if \(k_1\ ||\ s_1\rightarrow^\ast k_2\ ||\ s_2\) and \(k_2\ ||\ s_2\rightarrow^\ast k_3\ ||\ s_3\).

How are the small-step semantics of the article and the existing big-step semantics related? \(\sigma\vdash e\Rightarrow v\) implies that evaluating \(e\) under \(\sigma\) results in \(v\). The small-step semantics must make the following preposition true:

\[\forall \sigma.\forall e.\forall v.(\sigma\vdash e\Rightarrow v)\leftrightarrow(\sigma\vdash e:: \square\ ||\ \blacksquare\rightarrow^\ast\square\ ||\ v::\blacksquare)\]

More generally, the following must be true:

\[\forall \sigma.\forall e.\forall v.\forall k.\forall s.(\sigma\vdash e\Rightarrow                v)\leftrightarrow(\sigma\vdash e::k\ ||\ s\rightarrow^\ast k\ ||\ v::s)\]

Let the current state be \(\sigma\vdash e::k\ ||\ s\). A redex is \(e\), and the continuation is \(k\ ||\ s\). Think about a representation of \(k\ ||\ s\) in the function form. First, define \(\mathit{eval}(k\ ||\ v::s)=v'\) when \(k\ ||\ v::s\rightarrow^\ast\square\ ||\ v'::\blacksquare\). Then, function \(\lambda v.\mathit{eval}(k\ ||\ v::s)\) expresses the continuation. Continuation \(k\ ||\ s\) implies that the remaining computation is repeatedly reducing \(k\ ||\ v::s\) where \(v\) is the result of evaluating the redex.

As an example, consider arbitrary expression \(e\). Let \(v\) be the result of evaluating \(e\) under \(\sigma\) and the current state be \(\sigma\vdash e::\square\ ||\ \blacksquare\). \(e\) is a redex; \(\square\ ||\ \blacksquare\) is the continuation. Since the computation stack is empty, the computation does nothing. As \(e\) results in \(v\), the final result also is \(v\). The function form of the continuation is \(\lambda v.\mathit{eval}(\square\ ||\ v::\blacksquare)\). Applying the function to \(v\) produces \(v\) because \(\square\ ||\ v::\blacksquare\rightarrow^\ast \square\ ||\ v::\blacksquare\) is true.

Consider \(\sigma\vdash e::(+)::\square\ ||\ n_1::\blacksquare\). Let integer \(n_2\) be the result of evaluating \(e\) under \(\sigma\). A redex is \(e\); the continuation is \((+)::\square\ ||\ n_1::\blacksquare\). Its function form is \(\lambda v.\mathit{eval}((+)::\square\ ||\ v::n_1::\blacksquare)\). It implies adding an argument to \(n_1\). Suppose the continuation is applied to \(n_2\). The result equals \(\mathit{eval}((+)::\square\ ||\ n_2::n_1::\blacksquare)\), which is \(n_1+n_2\) because \((+)\) adds two values popped from the value stack and pushes the result into the value stack. The whole computation equals applying the continuation to the result of the redex. Therefore, the final result is \(n_1+n_2\).

Now, the article finally defines reduction. Consider \((+)\) and \((-)\) first.

\[(+)::k\ ||\ n_2::n_1::s\rightarrow k\ ||\ n_1+n_2::s\]

\[(-)::k\ ||\ n_2::n_1::s\rightarrow k\ ||\ n_1-n_2::s\]

It follows the meanings of \((+)\) and \((-)\) described before.

The rule for \((@)\) is more complex.

\[(@)::k\ ||\ v::\langle\lambda x.e,\sigma\rangle::s\rightarrow \sigma\lbrack x\mapsto             v\rbrack\vdash e::k\ ||\ s\]

To get the result of a function application, the function body must be evaluated. Big-step semantics requires a single rule to include the evaluation of the function body. On the other hand, small-step semantics differs from that. Adding the evaluation of the function body to the computation stack is enough.

The only remaining case is the evaluation of an expression. If an expression is either an integer, an identifier, or a function, its value is direct. A rule must push the value into the value stack.

\[\sigma\vdash n::k\ ||\ s\rightarrow k\ ||\ n::s\]

\[
\frac
{ x\in\mathit{Domain}(\sigma) }
{ \sigma\vdash x::k\ ||\ s\rightarrow k\ ||\ \sigma(x)::s }
\]

\[\sigma\vdash \lambda x.e::k\ ||\ s\rightarrow k\ ||\ \langle\lambda x.e,\sigma\rangle ::s\]

A completed step of computation disappears from the computation stack.

Suppose that an expression is a sum. Let \(n_1\) and \(n_2\) respectively be the results of evaluating \(e_1\) and \(e_2\) under \(\sigma\). The current state is \(\sigma\vdash e_1+e_2::k\ ||\ s\). A correct definition of reduction makes \(\sigma\vdash e_1+e_2::k\ ||\ s\rightarrow^\ast k\ ||\ n_1+n_2::s\) true. It is sure that both \(e_1\) and \(e_2\) must be evaluated to evaluate \(e_1+e_2\). Also, the evaluation \(e_1\) precedes that of \(e_2\). The following is the first attempt to write the rule:

\[\sigma\vdash e_1+e_2::k\ ||\ s\rightarrow \sigma\vdash e_1::\sigma\vdash e_2::k\ ||\ s\]

Then, the following is true:

\[
\begin{array}{lrcr}
& \sigma\vdash e_1+e_2::k & || & s \\
\rightarrow & \sigma\vdash e_1::\sigma\vdash e_2::k & || & s \\
\rightarrow^\ast & \sigma\vdash e_2::k & || & n_1::s \\
\rightarrow^\ast & k & || & n_2::n_1::s \\
\end{array}
\]

Achieving \(k\ ||\ n_1+n_2::s\) requires one more step. Replacing \(k\) with \((+)::k\) makes the following true:

\[
\begin{array}{lrcr}
& (+)::k & || & n_2::n_1::s \\
\rightarrow & k & || & n_1+n_2::s
\end{array}
\]

In conclusion, the next state must be \(\sigma\vdash e_1::\sigma\vdash e_2:: (+)::k\) instead of \(\sigma\vdash e_1::\sigma\vdash e_2::k\). The following rule is correct:

\[\sigma\vdash e_1+e_2::k\ ||\ s\rightarrow \sigma\vdash e_1::\sigma\vdash e_2::(+)::k\ ||\ s\]

The rule makes the following true:

\[
\begin{array}{lrcr}
& \sigma\vdash e_1+e_2::k & || & s \\
\rightarrow & \sigma\vdash e_1::\sigma\vdash e_2::(+)::k & || & s \\
\rightarrow^\ast & \sigma\vdash e_2::(+)::k & || & n_1::s \\
\rightarrow^\ast & (+)::k & || & n_2::n_1::s \\
\rightarrow & k & || & n_1+n_2::s
\end{array}
\]

The rules for differences and function applications are similar.

\[\sigma\vdash e_1-e_2::k\ ||\ s\rightarrow \sigma\vdash e_1::\sigma\vdash e_2::(-)::k\ ||\ s\]

\[\sigma\vdash e_1\ e_2::k\ ||\ s\rightarrow \sigma\vdash e_1::\sigma\vdash e_2::(@)::k\ ||\ s\]

The following shows all the rules at once:

\[
\begin{array}{lrcr}
&(+)::k& ||& n_2::n_1::s \\
\rightarrow& k& ||& n_1+n_2::s\\
&(-)::k& ||& n_2::n_1::s\\
\rightarrow& k& ||& n_1-n_2::s\\
&(@)::k& ||& v::\langle\lambda x.e,\sigma\rangle::s\\
\rightarrow& \sigma\lbrack x\mapsto v\rbrack\vdash e::k& ||& s\\
&\sigma\vdash n::k& ||&s\\
\rightarrow& k& ||&n::s\\
&\sigma\vdash x::k& ||&s\\
\rightarrow& k& ||& \sigma(x)::s\\
&\sigma\vdash \lambda x.e::k& ||& s\\
\rightarrow& k& ||&\langle\lambda x.e,\sigma\rangle ::s\\
&\sigma\vdash e_1+e_2::k& ||& s\\
\rightarrow& \sigma\vdash e_1::\sigma\vdash e_2::(+)::k& ||& s\\
&\sigma\vdash e_1-e_2::k& ||& s\\
\rightarrow& \sigma\vdash e_1::\sigma\vdash e_2::(-)::k& ||& s\\
&\sigma\vdash e_1\ e_2::k& ||& s\\
\rightarrow& \sigma\vdash e_1::\sigma\vdash e_2::(@)::k& ||& s\\
\end{array}
\]

Note that \((+)\), \((-)\), and \((@)\) decrease the size of the value stack by one. On the other hand, \(\sigma\vdash e\) increases the size of the value stack by one. When the computation does nothing, the evaluation of an expression eventually makes the value stack contain a single value. The value is the final result of the evaluation.

The following are examples of reduction according to the small-step semantics.

\[
\begin{array}{lrcr}
& \emptyset\vdash(1+2)-(3+4)::\square &||& \blacksquare \\
\rightarrow & \emptyset\vdash1+2::\emptyset\vdash3+4::(-)::\square &||& \blacksquare \\
\rightarrow & \emptyset\vdash1::\emptyset\vdash2::(+)::\emptyset\vdash3+4::(-)::\square &||&       \blacksquare \\
\rightarrow & \emptyset\vdash2::(+)::\emptyset\vdash3+4::(-)::\square &||& 1::\blacksquare \\
\rightarrow & (+)::\emptyset\vdash3+4::(-)::\square &||& 2::1::\blacksquare \\
\rightarrow & \emptyset\vdash3+4::(-)::\square &||& 3::\blacksquare \\
\rightarrow & \emptyset\vdash3::\emptyset\vdash4::(+)::(-)::\square &||& 3::\blacksquare \\
\rightarrow & \emptyset\vdash4::(+)::(-)::\square &||& 3::3::\blacksquare \\
\rightarrow & (+)::(-)::\square &||& 4::3::3::\blacksquare \\
\rightarrow & (-)::\square &||& 7::3::\blacksquare \\
\rightarrow & \square &||& -4::\blacksquare \\
\end{array}
\]

\[
\begin{array}{lrcr}
& \emptyset\vdash(\lambda x.\lambda y.x+y)\ 1\ 2)::\square &||& \blacksquare \\
\rightarrow & \emptyset\vdash(\lambda x.\lambda y.x+y)\ 1::\emptyset\vdash2::(@)::\square &||&     \blacksquare \\
\rightarrow & \emptyset\vdash\lambda x.\lambda y.x+y::\emptyset\vdash 1::(@)::\emptyset\vdash2::(@ )::\square &||& \blacksquare \\
\rightarrow & \emptyset\vdash 1::(@)::\emptyset\vdash2::(@)::\square &||& \langle\lambda x.\lambda y.x+y,\emptyset\rangle::\blacksquare \\
\rightarrow & (@)::\emptyset\vdash2::(@)::\square &||& 1::\langle\lambda x.\lambda y.x+y,          \emptyset\rangle::\blacksquare \\
\rightarrow & \lbrack x\mapsto 1\rbrack\vdash\lambda y.x+y::\emptyset\vdash2::(@)::\square &||&    \blacksquare \\
\rightarrow & \emptyset\vdash2::(@)::\square &||& \langle\lambda y.x+y,\lbrack x\mapsto            1\rbrack\rangle::\blacksquare \\
\rightarrow & (@)::\square &||& 2::\langle\lambda y.x+y,\lbrack x\mapsto 1\rbrack\rangle::         \blacksquare \\
\rightarrow & \lbrack x\mapsto 1,y\mapsto 2\rbrack\vdash x+y::\square &||& \blacksquare \\
\rightarrow & \lbrack x\mapsto 1,y\mapsto 2\rbrack\vdash x::\lbrack x\mapsto 1,y\mapsto            2\rbrack\vdash y::(+)::\square &||& \blacksquare \\
\rightarrow & \lbrack x\mapsto 1,y\mapsto 2\rbrack\vdash y::(+)::\square &||& 1::\blacksquare \\
\rightarrow & (+)::\square &||& 2::1::\blacksquare \\
\rightarrow & \square &||& 3::\blacksquare \\
\end{array}
\]

How are the small-step semantics and the implementation of the interpreter related?

\[
\begin{array}{lrcr}
&\sigma\vdash e_1+e_2::k& ||& s\\
\rightarrow& \sigma\vdash e_1::\sigma\vdash e_2::(+)::k& ||& s\\
\end{array}
\]

A redex is \(e_1+e_2\); the continuation is \(k\ ||\ s\), which is \(\lambda  v.\mathit{eval}(k\ ||\ v::s)\). Call the function \(K\). Reducing once yields \(\sigma\vdash   e_1::\sigma\vdash e_2::(+)::k\ ||\ s\). A redex is \(e_1\); the continuation is \(\sigma\vdash e_2::(+)::k\ ||\ s\), which is \(\lambda v_1.\mathit{eval}(\sigma\vdash e_2::(+)::k\ ||\ v_1::s)\). Focus on \(\sigma\vdash e_2:: (+)::k\ ||\ v_1::s\), the state in the body of the continuation. A redex is \(e_2\); the continuation is \((+)::k\ ||\ v_1::s\), which is \(\lambda v_2.\mathit{eval}((+)::k\ ||\ v_2::v_1::s)\). \((+)::k\ ||\ v_2::v_1:: s\rightarrow k\ ||\ v_1+v_2::s\) is already known. By the definition of \(K\), \(\lambda v_2.\mathit{eval}((+)::k\ ||\ v_2::v_1::s)\) equals \(\lambda v_2.\mathit{eval}(k\ ||\ v_1+v_2::s)\), which is \(\lambda v_2.K(v_1+v_2)\). As a result, the continuation of \(e_2\) is \(\lambda v_2.K(v_1+v_2)\), whose Scala representation is `v2 => k(numVAdd(v1, v2))`. Therefore, `interp(e2, env, v2 => k(numVAdd(v1, v2)))` expresses \(\mathit{eval}(\sigma\vdash e_2::(+)::k\ ||\ v_1::s)\). The continuation of \(e_1\) is \(\lambda v_1.\mathit{eval}(\sigma\vdash e_2::(+)::k\ ||\ v_1::s)\), whose Scala representation is `v1 => interp(e2, env, v2 =>  k(numVAdd(v1, v2)))`. Finally, `interp(e1, env, v1 => interp(e2, env, v2 => k(numVAdd(v1, v2))))` appears.

## Acknowledgments

I thank professor Ryu for giving feedback on the article.
