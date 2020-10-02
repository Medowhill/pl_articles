The article defines RFAE featuring recursive functions.

## CFAE

CFAE adds conditional expressions to FAE.

### Syntax

The below is the abstract syntax of CFAE. It shows a conditional expression, which is the only new feature.

\[
\begin{array}{lrcl}
\text{Expression} & e & ::= & \cdots \\
&& | & \textsf{if0}\ e\ e\ e
\end{array}
\]

\(\textsf{if0}\) is similar to \(\textsf{if}\) of BAE, defined by the last article, but its condition can be any value since CFAE lacks Boolean values. If a condition is zero, the true branch is evaluated; otherwise, the false branch is evaluated.

### Semantics

The following define the semantics of conditional expressions:

\[
\frac
{ \sigma\vdash e_1\Rightarrow 0 \quad \sigma\vdash e_2\Rightarrow v }
{ \sigma\vdash \textsf{if0}\ e_1\ e_2\ e_3\Rightarrow v}
\]

\[
\frac
{ \sigma\vdash e_1\Rightarrow v' \quad v'\not=0 \quad \sigma\vdash e_3\Rightarrow v }
{ \sigma\vdash \textsf{if0}\ e_1\ e_2\ e_3\Rightarrow v}
\]

The semantics is similar to that of \(\textsf{if}\). Always one of the true and false branches are evaluated, but not both.

## Recursion

Is it possible to implement a factorial function with CFAE? Assume that CFAE features multiplications. Firstly, consider a factorial function written functionally. The following Scala function calculates factorials:

```scala
def factorial(n: Int): Int =
  if (n == 0) 1
  else n * factorial(n – 1)
```

It seems the following CFAE expression is equivalent to the above code:

\[\textsf{val}\ factorial=\lambda n.\textsf{if0}\ n\ 1\ (n\times(factorial\ (n-1)))\ \textsf{in}\ \cdots\]

However, it is wrong since the scope of the binding occurrence of \(factorial\) includes \(\cdots\) but excludes the lambda abstraction. Identifier \(factorial\) in the lambda expression is free. CFAE disallows defining recursive functions.

## RFAE

RFAE adds recursive functions to CFAE.

### Syntax

The below is the abstract syntax of RFAE. It omits features common to CFAE.

\[
\begin{array}{lrcl}
\text{Expression} & e & ::= & \cdots \\
&& | & {\sf def}\ x(x)=e\ {\sf in}\ e
\end{array}
\]

\({\sf def}\ x_1(x_2)=e_1\ {\sf in}\ e_2\) defines a recursive function. \(x_1\) is the name of a function, and both \(e_1\) and \(e_2\) can refer to \(x_1\). For example, the following is a factorial function:

\[{\sf def}\ factorial(n)=\textsf{if0}\ n\ 1\ (n\times(factorial\ (n-1)))\ {\sf in}\ factorial\ 10\]

### Semantics

The following defines the semantics of recursive functions:

\[
\frac
{ \sigma'=\sigma\lbrack x_1\mapsto\langle\lambda x_2.e_1,\sigma'\rangle\rbrack \quad
  \sigma'\vdash e_2\Rightarrow v
}
{ \sigma\vdash {\sf def}\ x_1(x_2)=e_1\ {\sf in}\ e_2\Rightarrow v}
\]

The closure of a recursive function is similar to that of a lambda abstraction but does not store the environment of the moment. Instead, it stores an environment obtained by adding that the name of the function denotes the closure to the environment. Calling a closure evaluates the body of the closure under the environment of the closure so that recursive calls are valid.

The below proof trees prove that the factorial of one is one. The proof splits
into three trees for readability.

\[
\begin{array}{rcl}
\sigma_1&=&\lbrack f\mapsto\langle\lambda n.\textsf{if0}\ n\ 1\ (n\times(f\ (n-1))),\sigma_1\rangle\rbrack \\
\sigma_2&=&\sigma_1\lbrack n\mapsto 1\rbrack \\
&=&\lbrack f\mapsto\langle\lambda n.\textsf{if0}\ n\ 1\ (n\times(f\ (n-1))),\sigma_1\rangle,n\mapsto 1\rbrack \\
\sigma_3&=&\sigma_1\lbrack n\mapsto 0\rbrack \\
&=&\lbrack f\mapsto\langle\lambda n.\textsf{if0}\ n\ 1\ (n\times(f\ (n-1))),\sigma_1\rangle,n\mapsto 0\rbrack \\
\end{array}
\]

Assume the above.

\[
\frac
{
  \Large
  \frac
  { f\in\mathit{Domain}(\sigma_2) }
  { \sigma_2\vdash f\Rightarrow \langle\lambda n.\textsf{if0}\ n\ 1\ (n\times(f\ (n-1))),\sigma_1\rangle }
  \quad
  \frac
  {
    \frac
    { n\in\mathit{Domain}(\sigma_2) }
   { \sigma_2\vdash n\Rightarrow 1 } \quad
    \sigma_2\vdash 1\Rightarrow 1
  }
  { \sigma_2\vdash n-1\Rightarrow 0 } \quad
  \frac
  {
    \frac
    { n\in\mathit{Domain}(\sigma_3) }
    { \sigma_3\vdash n\Rightarrow 0 } \quad
    \sigma_3\vdash 1\Rightarrow 1
  }
  { \sigma_3\vdash \textsf{if0}\ n\ 1\ (n\times(f\ (n-1))) \Rightarrow 1 }
}
{ \sigma_2\vdash f\ (n-1)\Rightarrow 1 }
\]

\[
\frac
{
  \Large
  \frac
  { f\in\mathit{Domain}(\sigma_1)}
  { \sigma_1\vdash f\Rightarrow\langle\lambda n.\textsf{if0}\ n\ 1\ (n\times(f\ (n-1))),\sigma_1\rangle }
  \quad
  {\normalsize \sigma_1\vdash 1\Rightarrow 1}
  \quad
  \frac
  { 
    \frac
    { n\in\mathit{Domain}(\sigma_2) }
    { \sigma_2\vdash n\Rightarrow 1 } \quad
    \frac
    {
      \frac
      { n\in\mathit{Domain}(\sigma_2) }
      { \sigma_2\vdash n\Rightarrow 1 } \quad
      \sigma_2\vdash f\ (n-1)\Rightarrow 1
    }
    { \sigma_2\vdash (n\times(f\ (n-1)))\Rightarrow 1 }
  }
  {\sigma_2\vdash\textsf{if0}\ n\ 1\ (n\times(f\ (n-1)))\Rightarrow 1 }
}
{ \sigma_1\vdash f\ 1\Rightarrow 1 }
\]

\[
\frac
{
  \sigma_1=\lbrack f\mapsto\langle\lambda n.\textsf{if0}\ n\ 1\ (n\times(f\ (n-1))),\sigma_1\rangle\rbrack
  \quad
  \sigma_1\vdash f\ 1\Rightarrow 1
}
{\emptyset\vdash
{\sf def}\ f(n)=\textsf{if0}\ n\ 1\ (n\times(f\ (n-1)))\ {\sf in}\ f\ 1
\Rightarrow 1
}
\]

### Implementing an Interpreter

The following Scala code implements the abstract syntax and environments of RFAE:

```scala
sealed trait Expr
case class Num(n: Int) extends Expr
case class Add(l: Expr, r: Expr) extends Expr
case class Sub(l: Expr, r: Expr) extends Expr
case class Mul(l: Expr, r: Expr) extends Expr
case class Id(x: String) extends Expr
case class Fun(x: String, b: Expr) extends Expr
case class App(f: Expr, a: Expr) extends Expr
case class If0(c: Expr, t: Expr, f: Expr) extends Expr
case class Rec(f: String, x: String, b: Expr, e: Expr) extends Expr

sealed trait Value
case class NumV(n: Int) extends Value
case class CloV(p: String, b: Expr, var e: Env) extends Value

type Env = Map[String, Value]
def lookup(x: String, env: Env): Value =
  env.getOrElse(x, throw new Exception)
```

`If0` instnaces corresponds to conditional expressions; `Rec` instances corresponds to recursive functions. `CloV` instances, which are closures, have mutable environments because adding themselves to the environments requires the environments mutable.

```scala
def interp(e: Expr, env: Env): Value = e match {
  case Num(n) => NumV(n)
  case Add(l, r) =>
    val NumV(n) = interp(l, env)
    val NumV(m) = interp(r, env)
    NumV(n + m)
  case Sub(l, r) =>
    val NumV(n) = interp(l, env)
    val NumV(m) = interp(r, env)
    NumV(n - m)
  case Mul(l, r) =>
    val NumV(n) = interp(l, env)
    val NumV(m) = interp(r, env)
    NumV(n * m)
  case Id(x) => lookup(x, env)
  case Fun(x, b) => CloV(x, b, env)
  case App(f, a) =>
    val CloV(x, b, fEnv) = interp(f, env)
    interp(b, fEnv + (x -> interp(a, env)))
  case If0(c, t, f) =>
    interp(
      if (interp(c, env) == NumV(0)) t else f,
      env
    )
  case Rec(f, x, b, e) =>
    val cloV = CloV(x, b, env)
    val nenv = env + (f -> cloV)
    cloV.e = nenv
    interp(e, nenv)
}
```

The `If0` case evaluates the true branch if the condition equals `NumV(0)` and the false branch otherwise. The `Rec` case constructs a closure and adds the closure to the environment of the closure.

The following calculates the factorial of three by calling the `interp` function:

```scala
// def f(n) = if0 n 1 (n * (f (n-1))) in f(3)
interp(
  Rec(
    "f", "n",
    If0(Id("n"),
        Num(1),
        Mul(
          Id("n"),
          App(Id("f"), Sub(Id("n"), Num(1)))
        )
    ),
    App(Id("f"), Num(3))
  ),
  Map.empty
)
// NumV(6)
```

## Encoding Recursive Functions

As lambda calculus is Turing complete, recursive functions are encodable with lambda calculus. Since both FAE and CFAE subsume the features of lambda calculus, recursive functions are encodable with them as well.

\[
\begin{array}{rcl}
Z&\equiv&\lambda f.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\\
\mathit{encode}({\sf def}\ x_1(x_2)=e_1\ {\sf in}\ e_2)&=&
(\lambda x_1.e_2)\ (Z\ \lambda x_1.\lambda x_2.e_1)
\end{array}
\]

\(Z\) is a *fixed point combinator*; it calculates a fixed point of a given function. A fixed point of a function is a value that makes the function yield itself: a fixed point of function \(f\) is any \(x\) satisfying \(f(x)=x\). If an argument given to \(Z\) is a function whose fixed point is a particular recursive function, the result of applying \(Z\) to the function is the recursive function. Consider \(\lambda f.\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1)))\). If \(f\) is a factorial function, then \(\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1)))\) also is. Thus, the factorial function is a fixed point of \(\lambda f.\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1)))\), and \(Z\ \lambda f.\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1)))\) also is a factorial function.

How does the fixed point combinator work? \(Z\ \lambda f.\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1)))\) equals \( (\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\) if \(f\) denotes \(\lambda f.\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1)))\). It equals \(f\ \lambda v.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\ v\). Applying \(f\) to the argument results in \(\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (\lambda v.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\ v)\ (x-1)))\). Applying the function to \(0\) yields \(1\) since \(x\) is \(0\). On the other hand, applying the function to a nonzero value leads to \(x\times((\lambda v.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\ v)\ (x-1))\). Then, \(\lambda v.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\ v\) has reappeared, but its argument has decreased by one. It successfully simulates a recursive call and calculates factorials.

The following shows how to get the factorial of one:

\[
\begin{array}{rll}
& Z\ (\lambda f.\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1))))\ 1 \\
=&
(\lambda f.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v))\ (\lambda f.\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1))))\ 1 \\
&& (f\leftarrow\lambda f.\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1)))) \\
\rightarrow &
(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\ 1 & (f=\lambda f.\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1)))) \\
&& (x\leftarrow\lambda x.f\ \lambda v.x\ x\ v) \\
\rightarrow &
f\ (\lambda v.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\ v)\ 1 \\
= &
(\lambda f.\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1))))\ (\lambda v.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\ v)\ 1\\
&& (f\leftarrow\lambda v.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\ v) \\
\rightarrow &
(\lambda x.\textsf{if0}\ x\ 1\ (x\times((\lambda v.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\ v)\ (x-1))))\ 1 \\
&& (x\leftarrow 1) \\
\rightarrow &
1\times((\lambda v.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\ v)\ 0)
\end{array}
\]

Via the fixed point combinator, a factorial function is implementable without using a recursive function of RFAE.

```scala
// lambda f.(lambda x.f lambda v.x x v) (lambda x.f lambda v.x x v)
val Z =
  Fun("f",
    App(
      Fun("x",
        App(
          Id("f"),
          Fun("v", App(App(Id("x"), Id("x")), Id("v")))
        )
      ),
      Fun("x",
        App(
          Id("f"),
          Fun("v", App(App(Id("x"), Id("x")), Id("v")))
        )
      )
    )
  )

// (Z lambda f.lambda n.if0 n 1 (n * (f (n-1)))) 3
interp(
  App(
    App(
      Z,
      Fun("f", Fun("n",
        If0(Id("n"),
            Num(1),
            Mul(
              Id("n"),
              App(Id("f"), Sub(Id("n"), Num(1)))
            )
        )
      ))
    ),
    Num(3)
  ),
  Map.empty
)
// NumV(6)
```

## Acknowledgments

I thank professor Ryu for giving feedback on the article.
I also thank "pi" for finding a wrong environment in a proof tree.
