The article defines TRCFAE, which extends TFAE with conditional expressions and recursive functions. It defines the abstract syntax, the dynamic semantics, and the type system of TRCFAE, and implement a type checker and an interpreter of TRCFAE.

TFAE has the normalization property. Evaluation of every well-typed TFAE expression terminates in a finite time. It implies that the fixed point combinator is ill-typed in TFAE. Use of the fixed point combinator can create an expression whose evaluation does not terminate. Therefore, programmers cannot define recursive functions in TFAE. The article defines TRCFAE, which features recursive functions. The type system accepts some expressions using recursive functions after the extension. The extension from FAE to RCFAE does not increase the expressivity. It is only for convenience of programmers. On the other hand, the extension from TFAE to TRCFAE does increase the expressivity by allowing recursive functions.

## Synatx

The following is the abstract syntax of TRCFAE:

\[
\begin{array}{lrcl}
\text{Expression} & e & ::= & \cdots \\
&& | & \textsf{if0}\ e\ e\ e \\
&& | & \mu x:\tau\rightarrow\tau.\lambda x.e \\
\end{array}
\]

The dynamic semantics of \(\textsf{if0}\ e_1\ e_2\ e_3\) is same as that of RCFAE.

\(\mu x_1:\tau_1\rightarrow\tau_2.\lambda x_2.e\) is a recursive function. It is similar to a recursive function of RCFAE. The only difference is type annotation \(\tau_1\rightarrow\tau_2\). The type denotes the type of the recursive function. The function must take an argument of type \(\tau_1\) and returns a value of type \(\tau_2\).

## Dynamic Semantics

The dynamic semantics of TRCFAE is similar to that of RCFAE. The rules common to TFAE are omitted.

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
{ \Gamma\lbrack x_1:\tau_1\rightarrow\tau_2,x_2:\tau_1\rbrack\vdash e:\tau_2 }
{ \Gamma\vdash\mu x_1:\tau_1\rightarrow\tau_2.\lambda x_2.e:\tau_1\rightarrow\tau_2}
\]

The static semantics of a recursive function is similar to that of a lambda abstraction. The rule needs to check the type of the function body. The body can use not only the parameter but also the function itself. The type of the function is \(\tau_1\rightarrow\tau_2\). The type of the parameter is \(\tau_1\). Type checking of the body uses the extended type environment, which has the type of the function and the parameter. The type of \(e\) equals the return type of the function, which is \(\tau_2\).

The following proof tree proves that the type of \((\mu f:\textsf{num}\rightarrow\textsf{num}.\lambda n.\textsf{if0}\ n\ 0\ (n+(f\ (n-1))))\ 3\) is \(\textsf{num}\):

\[\Gamma=\lbrack f:\textsf{num}\rightarrow\textsf{num},n:\textsf{num}\rbrack\]

\[
\frac
{
  \frac
  {\huge
    \frac
    {
      \frac
      {n\in\mathit{Domain}(\Gamma)}
      {\vdash n:\textsf{num}} \quad
      \Gamma\vdash 0:\textsf{num} \quad
      \frac
      {
        \frac
        {n\in\mathit{Domain}(\Gamma)}
        { \Gamma\vdash n:\textsf{num} } \quad
        \frac
        {
          \frac
          {f\in\mathit{Domain}(\Gamma)}
          { \Gamma\vdash f:\textsf{num}\rightarrow\textsf{num} } \quad
          \frac
          {
            \frac
            { n\in\mathit{Domain}(\Gamma) }
            { \Gamma\vdash n:\textsf{num} } \quad
            \Gamma\vdash 1:\textsf{num}
          }
          { \Gamma\vdash n-1:\textsf{num} } \quad
        }
        { \Gamma\vdash f\ (n-1):\textsf{num} }
      }
      { \Gamma\vdash n+(f\ (n-1)):\textsf{num} }
    }
    { \Gamma\vdash \textsf{if0}\ n\ 0\ (n+(f\ (n-1))):\textsf{num} }
  }
  { \Large\emptyset\vdash
    \mu f:\textsf{num}\rightarrow\textsf{num}.\lambda n.\textsf{if0}\ n\ 0\ (n+(f\ (n-1)))
    :\textsf{num}\rightarrow\textsf{num} } \quad
  \emptyset\vdash 3:\textsf{num}
}
{ \emptyset\vdash
(\mu f:\textsf{num}\rightarrow\textsf{num}.\lambda n.\textsf{if0}\ n\ 0\ (n+(f\ (n-1))))\ 3
:\textsf{num} }
\]

## Implementing a Type Checker

The following Scala code implements the abstract syntax of TRCFAE:

```scala
sealed trait TRCFAE
case class Num(n: Int) extends TRCFAE
case class Add(l: TRCFAE, r: TRCFAE) extends TRCFAE
case class Sub(l: TRCFAE, r: TRCFAE) extends TRCFAE
case class Id(x: String) extends TRCFAE
case class Fun(x: String, t: TRCFAET, b: TRCFAE) extends TRCFAE
case class App(f: TRCFAE, a: TRCFAE) extends TRCFAE
case class If0(c: TRCFAE, t: TRCFAE, f: TRCFAE) extends TRCFAE
case class Rec(f: String, p: TRCFAET, r: TRCFAET, x: String, b: TRCFAE) extends TRCFAE

sealed trait TRCFAET
case object NumT extends TRCFAET
case class ArrowT(p: TRCFAET, r: TRCFAET) extends TRCFAET

type TEnv = Map[String, TRCFAET]

def mustSame(t1: TRCFAET, t2: TRCFAET): TRCFAET =
  if (t1 == t2) t1 else throw new Exception
```

A `TRCFAE` instance represent a TRCFAE expression. The implementation is similar to that of RCFAE.

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
case Rec(f, p, r, x, b) =>
  val t = ArrowT(p, r)
  mustSame(typeCheck(b, env + (f -> t) + (x -> p)), r)
  t
```

\[
\frac
{ \Gamma\lbrack x_1:\tau_1\rightarrow\tau_2,x_2:\tau_1\rbrack\vdash e:\tau_2 }
{ \Gamma\vdash\mu x_1:\tau_1\rightarrow\tau_2.\lambda x_2.e:\tau_1\rightarrow\tau_2}
\]

The parameter type is `p`, and the return type is `r`. Thus, the type of `f` is the function type from `p` to `r`. The type of `x` is `p`. To type-check `b`, a type environment must have the types of `f` and `x`. The type of `b` must equal `r`. The `mustSame` function compares the types. The type of the whole expression is the type of `f`.

The following shows the complete code of the function:

```scala
def typeCheck(e: TRCFAE, env: TEnv): TRCFAET = e match {
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
  case Rec(f, p, r, x, b) =>
    val t = ArrowT(p, r)
    mustSame(typeCheck(b, env + (f -> t) + (x -> p)), r)
    t
}
```

The following code checks the type of \((\mu f:\textsf{num}\rightarrow\textsf{num}.\lambda n.\textsf{if0}\ n\ 0\ (n+(f\ (n-1))))\ 3\):

```scala
// (mu f:num->num.lambda n.if0 n 0 (n + (f (n-1)))) 3
typeCheck(
  App(
    Rec("f", NumT, NumT, "n",
      If0(Id("n"),
          Num(0),
          Add(
            Id("n"),
            App(Id("f"), Sub(Id("n"), Num(1)))
          )
      )
    ),
    Num(6)
  ),
  Map.empty
)
// num
```

## Implementing an Interpreter

The interpreter is similar to that of RCFAE.

```scala
sealed trait TRCFAEV
case class NumV(n: Int) extends TRCFAEV
case class CloV(p: String, b: TRCFAE, var e: Env) extends TRCFAEV

type Env = Map[String, TRCFAEV]

def interp(e: TRCFAE, env: Env): TRCFAEV = e match {
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
  case Rec(f, _, _, x, b) =>
    val c = CloV(x, b, env)
    c.e += f -> c
    c
}

def run(e: TRCFAE): TRCFAEV = {
  typeCheck(e, Map.empty)
  interp(e, Map.empty)
}
```

The following code executes \((\mu f:\textsf{num}\rightarrow\textsf{num}.\lambda n.\textsf{if0}\ n\ 0\ (n+(f\ (n-1))))\ 3\):

```scala
// (mu f:num->num.lambda n.if0 n 0 (n + (f (n-1)))) 3
run(
  App(
    Rec("f", NumT, NumT, "n",
      If0(Id("n"),
          Num(0),
          Add(
            Id("n"),
            App(Id("f"), Sub(Id("n"), Num(1)))
          )
      )
    ),
    Num(6)
  )
)
// 6
```

## Acknowledgments

I thank professor Ryu for giving feedback on the article.
