The article defines RCFAE featuring recursive functions.

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

## RCFAE

RCFAE adds recursive functions to CFAE.

### Syntax

The below is the abstract syntax of RCFAE. It omits features common to CFAE.

\[
\begin{array}{lrcl}
\text{Expression} & e & ::= & \cdots \\
&& | & \mu x.\lambda x.e
\end{array}
\]

\(\mu x_1.\lambda x_2.e\) defines a recursive function. \(x_1\) is the name of a function, and \(e\) can refer to \(x_1\). For example, the following is a factorial function:

\[\mu factorial.\lambda n.\textsf{if0}\ n\ 1\ (n\times(factorial\ (n-1)))\]

Any expressions other than \(e\) cannot refer to \(x_1\). Recursive functions are anonymous as well. \(x_1\) takes a similar role to `this` or `self` of object-oriented languages. Objects use the keywords to denote themselves. They are available inside objects but not outside objects.

### Semantics

The following defines the semantics of recursive functions:

\[
\frac
{ \sigma'=\sigma\lbrack x_1\mapsto\langle\lambda x_2.e,\sigma'\rangle\rbrack }
{ \sigma\vdash \mu x_1.\lambda x_2.e\Rightarrow \langle\lambda x_2.e,\sigma'\rangle}
\]

The closure of a recursive function is similar to that of a lambda abstraction but does not store the environment of the moment. Instead, it stores an environment obtained by adding that the name of the function denotes the closure to the environment. Calling a closure evaluates the body of the closure under the environment of the closure so that recursive calls are valid.

The below proof tree proves that the factorial of one is one.

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
  \begin{array}{c}
  \large{
  \frac
  {\sigma_1=\lbrack f\mapsto\langle\lambda n.\textsf{if0}\ n\ 1\ (n\times(f\ (n-1))),\sigma_1\rangle\rbrack}
  {\emptyset\vdash\mu f.\lambda n.\textsf{if0}\ n\ 1\ (n\times(f\ (n-1)))\Rightarrow\langle\lambda n.\textsf{if0}\ n\ 1\ (n\times(f\ (n-1))),\sigma_1\rangle}}\quad
  \emptyset\vdash 1\Rightarrow 1 \\
  \frac
  { \LARGE{
    \frac
    { n\in\mathit{Domain}(\sigma_2) }
    { \sigma_2\vdash n\Rightarrow 1 } \quad
    \frac
    {
      \frac
      { n\in\mathit{Domain}(\sigma_2) }
      { \sigma_2\vdash n\Rightarrow 1 } \quad
      \frac
      {
        \LARGE{\begin{array}{c}
        \frac
        { f\in\mathit{Domain}(\sigma_2) }
        { \sigma_2\vdash f\Rightarrow \langle\lambda n.\textsf{if0}\ n\ 1\ (n\times(f\ (n-1))),\sigma_1\rangle } \\
        \frac
        {
          \frac
          { n\in\mathit{Domain}(\sigma_2) }
         { \sigma_2\vdash n\Rightarrow 1 } \quad
          \sigma_2\vdash 1\Rightarrow 1
        }
        { \sigma_2\vdash n-1\Rightarrow 0 } \\
        \frac
        {
          \frac
          { n\in\mathit{Domain}(\sigma_3) }
          { \sigma_3\vdash n\Rightarrow 0 } \quad
          \sigma_3\vdash 1\Rightarrow 1
        }
        { \sigma_3\vdash \textsf{if0}\ n\ 1\ (n\times(f\ (n-1))) \Rightarrow 1 }
        \end{array}}
      }
      { \sigma_2\vdash f\ (n-1)\Rightarrow 1 }
    }
    { \sigma_2\vdash (n\times(f\ (n-1)))\Rightarrow 1 }
  }}
  {\Large{\sigma_2\vdash\textsf{if0}\ n\ 1\ (n\times(f\ (n-1)))\Rightarrow 1} }
  \end{array}
}
{\emptyset\vdash
(\mu f.\lambda n.\textsf{if0}\ n\ 1\ (n\times(f\ (n-1))))\ 1
\Rightarrow 1
}
\]

### Implementing an Interpreter

The following Scala code implements the abstract syntax and environments of RCFAE:

```scala
sealed trait RCFAE
case class Num(n: Int) extends RCFAE
case class Add(l: RCFAE, r: RCFAE) extends RCFAE
case class Sub(l: RCFAE, r: RCFAE) extends RCFAE
case class Mul(l: RCFAE, r: RCFAE) extends RCFAE
case class Id(x: String) extends RCFAE
case class Fun(x: String, b: RCFAE) extends RCFAE
case class App(f: RCFAE, a: RCFAE) extends RCFAE
case class If0(c: RCFAE, t: RCFAE, f: RCFAE) extends RCFAE
case class Rec(f: String, x: String, b: RCFAE) extends RCFAE

sealed trait RCFAEV
case class NumV(n: Int) extends RCFAEV
case class CloV(p: String, b: RCFAE, var e: Env) extends RCFAEV

type Env = Map[String, RCFAEV]
def lookup(x: String, env: Env): RCFAEV =
  env.getOrElse(x, throw new Exception)
```

`If0` instnaces corresponds to conditional expressions; `Rec` instances corresponds to recursive functions. `CloV` instances, which are closures, have mutable environments because adding themselves to the environments requires the environments mutable.

```scala
def interp(e: RCFAE, env: Env): RCFAEV = e match {
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
  case Rec(f, x, b) =>
    val c = CloV(x, b, env)
    c.e += f -> c
    c
}
```

The `If0` case evaluates the true branch if the condition equals `NumV(0)` and the false branch otherwise. The `Rec` case constructs a closure and adds the closure to the environment of the closure.

The following calculates the factorial of three by calling the `interp` function:

```scala
// (mu f.lambda n.if0 n 1 (n * (f (n-1)))) 3
interp(
  App(
    Rec("f", "n",
      If0(Id("n"),
          Num(1),
          Mul(
            Id("n"),
            App(Id("f"), Sub(Id("n"), Num(1)))
          )
      )
    ),
    Num(3)
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
\mathit{encode}(\mu f.\lambda x.e)&=&Z\ \lambda f.\lambda x.e
\end{array}
\]

\(Z\) is a *fixed point combinator*; it calculates a fixed point of a given function. A fixed point of a function is a value that makes the function yield itself: a fixed point of function \(f\) is any \(x\) satisfying \(f(x)=x\). If an argument given to \(Z\) is a function whose fixed point is a particular recursive function, the result of applying \(Z\) to the function is the recursive function. Consider \(\lambda f.\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1)))\). If \(f\) is a factorial function, then \(\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1)))\) also is. Thus, the factorial function is a fixed point of \(\lambda f.\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1)))\), and \(Z\ \lambda f.\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1)))\) also is a factorial function.

How does the fixed point combinator work? \(Z\ \lambda f.\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1)))\) equals \( (\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\) if \(f\) denotes \(\lambda f.\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1)))\). It equals \(f\ \lambda v.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\ v\). Applying \(f\) to the argument results in \(\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (\lambda v.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\ v)\ (x-1)))\). Applying the function to \(0\) yields \(1\) since \(x\) is \(0\). On the other hand, applying the function to a nonzero value leads to \(x\times(f\ (\lambda v.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\ v)\ (x-1))\). Then, \(f\ \lambda v.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\ v\) has reappeared, but its argument has decreased by one. It successfully simulates a recursive call and calculates factorials.

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
(\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (\lambda v.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\ v)\ (x-1))))\ 1 \\
&& (x\leftarrow 1) \\
\rightarrow &
1\times(f\ (\lambda v.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\ v)\ 0)
\end{array}
\]

Via the fixed point combinator, a factorial function is implementable without using a recursive function of RCFAE.

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
