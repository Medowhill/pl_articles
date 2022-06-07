The article defines TRFAE, which extends TFAE with conditional expressions and recursive functions. It defines the abstract syntax, the dynamic semantics, and the type system of TRFAE, and implement a type checker and an interpreter of TRFAE.

TFAE has the normalization property. Evaluation of every well-typed TFAE expression terminates in a finite time. It implies that the fixed point combinator is ill-typed in TFAE. Use of the fixed point combinator can create an expression whose evaluation does not terminate. Therefore, programmers cannot define recursive functions in TFAE. The article defines TRFAE, which features recursive functions. The type system accepts some expressions using recursive functions after the extension. The extension from FAE to RFAE does not increase the expressivity. It is only for convenience of programmers. On the other hand, the extension from TFAE to TRFAE does increase the expressivity by allowing recursive functions.

## Syntax

The following is the abstract syntax of TRFAE:

\[
\begin{array}{lrcl}
\text{Expression} & e & ::= & \cdots \\
&& | & \textsf{if0}\ e\ e\ e \\
&& | & {\sf def}\ x(x:\tau):\tau=e\ {\sf in}\ e \\
\text{Value} & v & ::= & n \\
&& | & \langle \lambda x.e,\sigma \rangle \\
\end{array}
\]

\(\textsf{if0}\ e_1\ e_2\ e_3\) is the same as that of RFAE. \(e_1\) is the condition; \(e_2\) is the true branch; \(e_3\) is the false branch.

\({\sf def}\ x_1(x_2:\tau_1):\tau_2=e_1\ {\sf in}\ e_2\) defines a recursive function. It is similar to a recursive function of RFAE. The only difference is type annotation \(\tau_1\) and \(\tau_2\). \(\tau_1\) denotes the parameter type of the function; \(\tau_2\) denotes the return type of the function. The function must take an argument of type \(\tau_1\) and return a value of type \(\tau_2\). Type annotations are used for type checking, just like type annotations in TFAE.

## Dynamic Semantics

The dynamic semantics of TRFAE is similar to that of RFAE. The rules common to TFAE are omitted.

\[
\frac
{ \sigma\vdash e_1\Rightarrow 0 \quad
  \sigma\vdash e_2\Rightarrow v }
{ \sigma\vdash\textsf{if0}\ e_1\ e_2\ e_3\Rightarrow v}
\]

\[
\frac
{ \sigma\vdash e_1\Rightarrow v' \quad
  v'\not=0 \quad
  \sigma\vdash e_3\Rightarrow v }
{ \sigma\vdash\textsf{if0}\ e_1\ e_2\ e_3\Rightarrow v}
\]

\[
\frac
{ \sigma'=\sigma\lbrack x_1\mapsto\langle\lambda x_2.e,\sigma'\rangle\rbrack }
{ \sigma\vdash\mu x_1:\tau_1\rightarrow\tau_2.\lambda x_2.e\Rightarrow\langle\lambda x_2.e,\sigma'\rangle}
\]

## Type System

The rules common to TFAE are omitted.

\[
\frac
{ \Gamma\vdash e_1:\tau' \quad
  \Gamma\vdash e_2:\tau \quad
  \Gamma\vdash e_3:\tau }
{ \Gamma\vdash\textsf{if0}\ e_1\ e_2\ e_3:\tau}
\]

The condition of a conditional expression must be well-typed. If the result of the condition is \(0\), then the first branch is evaluated. Otherwise, the second branch is evaluated. Therefore, the result can be any value. \(\Gamma\vdash e_1:\tau'\) denotes that. The rule cannot determine whether the first or the second branch will be evaluated. Since every expression has at most one type, \(e_2\) and \(e_3\) have the same type, \(\tau\). The type of the whole expression is \(\tau\).

One may change the above rule to the following rule:

\[
\frac
{ \Gamma\vdash e_1:\textsf{num} \quad
  \Gamma\vdash e_2:\tau \quad
  \Gamma\vdash e_3:\tau }
{ \Gamma\vdash\textsf{if0}\ e_1\ e_2\ e_3:\tau}
\]

Both rules make the type system sound. The latter rejects more expressions than the former. However, programmers usually want to use an integer as a condition. Therefore, rejecting nonintegral values being used as a condition can prevent mistakes of programmers.

\[
\frac
{
  \Gamma\lbrack x_1:\tau_1\rightarrow\tau_2,x_2:\tau_1\rbrack\vdash e_1:\tau_2
  \quad
  \Gamma\lbrack x_1:\tau_1\rightarrow\tau_2\rbrack\vdash e_2:\tau
}
{ \Gamma\vdash{\sf def}\ x_1(x_2:\tau_1):\tau_2=e_1\ {\sf in}\ e_2:\tau}
\]

The static semantics of a recursive function is similar to that of a lambda abstraction. The rule needs to check the type of the function body. The body can use not only the parameter but also the function itself. The type of the function is \(\tau_1\rightarrow\tau_2\). The type of the parameter is \(\tau_1\). Type checking of the body uses the extended type environment, which has the type of the function and the parameter. The type of \(e_1\) has to be equal to the return type of the function, which is \(\tau_2\).

The following proof tree proves that the type of \({\sf def}\ f(n:{\sf num}):{\sf num}={\sf if0}\ n\ 0\ (n+(f\ (n-1)))\ {\sf in}\ f\ 3\) is \(\textsf{num}\):

\[\Gamma_1=\lbrack f:\textsf{num}\rightarrow\textsf{num},n:\textsf{num}\rbrack\]
\[\Gamma_2=\lbrack f:\textsf{num}\rightarrow\textsf{num}\rbrack\]

\[
\frac
{
  \Large
  \frac
  {
    \frac
    {n\in\mathit{Domain}(\Gamma_1)}
    {\Gamma_1\vdash n:\textsf{num}} \quad
    \Gamma_1\vdash 0:\textsf{num} \quad
    \frac
    {
      \frac
      {n\in\mathit{Domain}(\Gamma_1)}
      { \Gamma_1\vdash n:\textsf{num} } \quad
      \frac
      {
        \frac
        {f\in\mathit{Domain}(\Gamma_1)}
        { \Gamma_1\vdash f:\textsf{num}\rightarrow\textsf{num} } \quad
        \frac
        {
          \frac
          { n\in\mathit{Domain}(\Gamma_1) }
          { \Gamma_1\vdash n:\textsf{num} } \quad
          \Gamma_1\vdash 1:\textsf{num}
        }
        { \Gamma_1\vdash n-1:\textsf{num} } \quad
      }
      { \Gamma_1\vdash f\ (n-1):\textsf{num} }
    }
    { \Gamma_1\vdash n+(f\ (n-1)):\textsf{num} }
  }
  { \Gamma_1\vdash \textsf{if0}\ n\ 0\ (n+(f\ (n-1))):\textsf{num} }
  \quad
  \frac
  {
    \frac
    { f\in{\it Domain}(\Gamma_2) }
    { \Gamma_2\vdash f:\textsf{num}\rightarrow\textsf{num} }
    \quad
    \Gamma_2\vdash 3:\textsf{num}
  }
  { \Gamma_2\vdash f\ 3:\textsf{num} }
}
{
  \emptyset\vdash
  {\sf def}\ f(n:{\sf num}):{\sf num}={\sf if0}\ n\ 0\ (n+(f\ (n-1)))\ {\sf in}\ f\ 3
  :\textsf{num}
}
\]

## Implementing a Type Checker

The following Scala code implements the abstract syntax of TRFAE:

```scala
sealed trait Expr
case class Num(n: Int) extends Expr
case class Add(l: Expr, r: Expr) extends Expr
case class Sub(l: Expr, r: Expr) extends Expr
case class Id(x: String) extends Expr
case class Fun(x: String, t: Type, b: Expr) extends Expr
case class App(f: Expr, a: Expr) extends Expr
case class If0(c: Expr, t: Expr, f: Expr) extends Expr
case class Rec(f: String, x: String, p: Type, r: Type, b: Expr, e: Expr) extends Expr

sealed trait Type
case object NumT extends Type
case class ArrowT(p: Type, r: Type) extends Type

type TEnv = Map[String, Type]

def mustSame(t1: Type, t2: Type): Type =
  if (t1 == t2) t1 else throw new Exception
```

A `Expr` instance represent a TRFAE expression. The implementation is similar to that of RFAE.

```scala
case If0(c, t, f) =>
  typeCheck(c, env)
  mustSame(typeCheck(t, env), typeCheck(f, env))
```

\[
\frac
{ \Gamma\vdash e_1:\tau' \quad
  \Gamma\vdash e_2:\tau \quad
  \Gamma\vdash e_3:\tau }
{ \Gamma\vdash\textsf{if0}\ e_1\ e_2\ e_3:\tau}
\]

The condition of an expression must be well-typed. The `typeCheck` function checks the type of `c`, and the result of type checking is unnecessary. The types of the two branches must be the same. The `typeCheck` function checks the types of `t` and `f`. The `mustSame` function compares the results. If they are the same, then the type is the type of the whole expression.

```scala
case Rec(f, x, p, r, b, e) =>
  val t = ArrowT(p, r)
  val nenv = env + (f -> t)
  mustSame(r, typeCheck(b, nenv + (x -> p)))
  typeCheck(e, nenv)
```

\[
\frac
{
  \Gamma\lbrack x_1:\tau_1\rightarrow\tau_2,x_2:\tau_1\rbrack\vdash e_1:\tau_2
  \quad
  \Gamma\lbrack x_1:\tau_1\rightarrow\tau_2\rbrack\vdash e_2:\tau
}
{ \Gamma\vdash{\sf def}\ x_1(x_2:\tau_1):\tau_2=e_1\ {\sf in}\ e_2:\tau}
\]

The parameter type is `p`, and the return type is `r`. Thus, the type of `f` is the function type from `p` to `r`. The type of `x` is `p`. To type-check `b`, the type environment must have the types of `f` and `x`. The type of `b` must equal `r`. The `mustSame` function compares the types. The function can be used not only in `b`, which is the body of the function, but also in `e`. On the other hand, the parameter `x` cannot be used in `e`. Therefore, it is enough to add only the type of `f` to the type environment used to type-check `e`. The type of the whole expression is equal to the type of `e`.

The following shows the complete code of the function:

```scala
def typeCheck(e: Expr, env: TEnv): Type = e match {
  case Num(n) => NumT
  case Add(l, r) =>
    mustSame(mustSame(typeCheck(l, env), NumT), typeCheck(r, env))
  case Sub(l, r) =>
    mustSame(mustSame(typeCheck(l, env), NumT), typeCheck(r, env))
  case Id(x) => env(x)
  case Fun(x, t, b) =>
    ArrowT(t, typeCheck(b, env + (x -> t)))
  case App(f, a) =>
    val ArrowT(t1, t2) = typeCheck(f, env)
    val t3 = typeCheck(a, env)
    mustSame(t1, t3)
    t2
  case If0(c, t, f) =>
    typeCheck(c, env)
    mustSame(typeCheck(t, env), typeCheck(f, env))
  case Rec(f, x, p, r, b, e) =>
    val t = ArrowT(p, r)
    val nenv = env + (f -> t)
    mustSame(r, typeCheck(b, nenv + (x -> p)))
    typeCheck(e, nenv)
}
```

The following code checks the type of \({\sf def}\ f(n:{\sf num}):{\sf num}={\sf if0}\ n\ 0\ (n+(f\ (n-1)))\ {\sf in}\ f\ 3\):

```scala
// def f(x: num): num = if0 n 0 (n + (f (n-1))); f(3)
typeCheck(
  Rec(
    "f", "n", NumT, NumT,
    If0(Id("n"),
        Num(0),
        Add(
          Id("n"),
          App(Id("f"), Sub(Id("n"), Num(1)))
        )
    ),
    App(Id("f"), Num(3))
  ),
  Map.empty
)
// num
```

## Implementing an Interpreter

The interpreter is similar to that of RFAE.

```scala
sealed trait Value
case class NumV(n: Int) extends Value
case class CloV(p: String, b: Expr, var e: Env) extends Value

type Env = Map[String, Value]

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
  case Id(x) => env(x)
  case Fun(x, _, b) => CloV(x, b, env)
  case App(f, a) =>
    val CloV(x, b, fEnv) = interp(f, env)
    interp(b, fEnv + (x -> interp(a, env)))
  case If0(c, t, f) =>
    interp(if (interp(c, env) == NumV(0)) t else f, env)
  case Rec(f, x, _, _, b, e) =>
    val cloV = CloV(x, b, env)
    val nenv = env + (f -> cloV)
    cloV.e = nenv
    interp(e, nenv)
}

def run(e: Expr): Value = {
  typeCheck(e, Map.empty)
  interp(e, Map.empty)
}
```

The following code executes \({\sf def}\ f(n:{\sf num}):{\sf num}={\sf if0}\ n\ 0\ (n+(f\ (n-1)))\ {\sf in}\ f\ 3\):

```scala
// def f(x: num): num = if0 n 0 (n + (f (n-1))); 3
run(
  Rec(
    "f", "n", NumT, NumT,
    If0(Id("n"),
        Num(0),
        Add(
          Id("n"),
          App(Id("f"), Sub(Id("n"), Num(1)))
        )
    ),
    App(Id("f"), Num(3))
  )
)
// 6
```

## Acknowledgments

I thank professor Ryu for giving feedback on the article. I also thank ‘요셉’
for finding a typo.
