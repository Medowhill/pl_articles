The article defines KFAE by extending FAE with *first-class continuations*. The last article defined the small-step semantics of FAE to explain continuations formally and implemented the interpreter of FAE with continuation-passing style. Programmers writing programs in FAE cannot utilize continuation directly. Continuations just exist during evaluation.

A first-class entity of a certain programming language is an entity treated as a value. Since it is a value, it can be the value of a variable, an argument for a function call, and the return value of a function. For example, first-class functions are functions used as values.

First-class continuations are continuations used as values. If a language features first-class continuations, continuations can be the value of a variable, an argument for a function call, or the return value of a function. A continuation can be considered as a function since it takes a value and produces a value. Programmers can call a continuation as they call a function. However, a continuation differs from a function. The current continuation at some point is all of the remaining computation. Once a continuation is called and evaluated, the execution finishes. On the other hand, a function call returns a value, and the execution continues with the value. Calling a continuation changes the current continuation to the called one. It changes the control flow of the execution. First-class continuations allow programmers to express complex computation concisely.

The article defines KFAE and then implements an interpreter of KFAE. It also shows use of first-class continuations.

## Syntax

The following is the abstract syntax of KFAE. It shows an expression not existing in FAE.

\[
\begin{array}{lrcl}
\text{Expression} & e & ::= & \cdots \\
&& | & \textsf{vcc}\ x\ \textsf{in}\ e \\
\end{array}
\]

Expression \(\textsf{vcc}\ x\ \textsf{in}\ e\) evaluates \(e\) while \(x\) denotes the current continuation. The term '\(\textsf{cc}\)' of \(\textsf{vcc}\) means the **c**urrent **c**ontinuation, which is the continuation of \(\textsf{vcc}\ x\ \textsf{in}\ e\). The scope of \(x\) equals \(e\). When a continuation denoted by \(x\) is called, the continuation replaces the continuation of that point.

Consider \(1+(((\lambda v.1+v)\ 2)+3)\). \(\lambda v.1+v\) is a function that takes an argument and returns the argument increased by \(1\). Calling the function never affects the continuation. The continuation remains the same and takes the result of the function call as an argument to continue the evaluation. The continuation of \(\lambda v.1+v)\ 2\) is \(\lambda v.1+(v+3)\). The result of the function call is \(3\). Applying the continuation yields \(1+(3+3)\). The final result is \(7\).

This time, consider \(1+(\textsf{vcc}\ x\ \textsf{in}\ (x\ 2)+3)\). The continuation of \(\textsf{vcc}\ x\ \textsf{in}\ (x\ 2)+3\) is adding the result to \(1\). The function form is \(\lambda v.1+v\). The continuation is the value of \(x\). Even though a lambda abstraction can represent a continuation, a first-class continuation differs from a function. Since \(x\) is a continuation, calling \(x\) changes the continuation of the calling expression to a continuation that is the value of \(x\). \(x\ 2\) makes \(\lambda v.1+(v+3)\), the continuation, disappears. The only remaining computation is \((\lambda v.1+v)\ 2\). Evaluating \(1+2\) produces the final result: \(3\).

The following tables compare the evaluations of two expressions:

\[
\begin{array}{ccccc}
&\text{Expression being Evaluated} & \text{Redex} & \text{Continuation} & \text{Result of Redex} \\
& 1+(((\lambda v.1+v)\ 2)+3) & ((\lambda v.1+v)\ 2) & \lambda v.1+(v+3) & 1+2 \\
\rightarrow& 1+((1+2)+3) & 1+2 & \lambda v.1+(v+3) & 3 \\
\rightarrow&1+(3+3) & 3+3 & \lambda v.1+v & 6 \\
\rightarrow& 1+6 & 1+6 & \lambda v.v & 7
\end{array}
\]

\[
\begin{array}{cccccc}
&\text{Expression being Evaluated} & \text{Redex} & \text{Continuation} & \text{Result of Redex} & \text{First-Class Continuation} \\
& 1+(\textsf{vcc}\ x\ \textsf{in}\ (x\ 2)+3) & \textsf{vcc}\ x\ \textsf{in}\ (x\ 2)+3 & \lambda v.1+v & (x\ 2)+3 &        x=\lambda v.1+v \\
\rightarrow& 1+((x\ 2)+3) & (x\ 2) & \lambda v.1+(v+3) & \text{The continuation changes}  \\
\rightarrow& x\ 2 & 2 & x & 2 \\
\equiv & x\ 2 & 2 & \lambda v.1+v & 2 \\
\rightarrow& 1+2 & 1+2 & \lambda v.v & 3
\end{array}
\]

\(\equiv\) means no evaluation happens during the line break. It shows that the continuation changes and what the new continuation is.

The following gives another example:

\[
\begin{array}{cccccc}
&\text{Expression being Evaluated} & \text{Redex} & \text{Continuation} & \text{Result of Redex} & \text{First-Class Continuation} \\
& \small{\textsf{vcc}\ x\ \textsf{in}\ (\textsf{vcc}\ y\ \textsf{in}\ x(1+(\textsf{vcc}\ z\ \textsf{in}\ y\ z)))3}
& \small{\textsf{vcc}\ x\ \textsf{in}\ (\textsf{vcc}\ y\ \textsf{in}\ x(1+(\textsf{vcc}\ z\ \textsf{in}\ y\ z)))3}
& \lambda v.v
& \small{(\textsf{vcc}\ y\ \textsf{in}\ x(1+(\textsf{vcc}\ z\ \textsf{in}\ y\ z)))3}
& x=\lambda v.v \\
\rightarrow & (\textsf{vcc}\ y\ \textsf{in}\ x(1+(\textsf{vcc}\ z\ \textsf{in}\ y\ z)))3
& \textsf{vcc}\ y\ \textsf{in}\ x(1+(\textsf{vcc}\ z\ \textsf{in}\ y\ z))
& \lambda v.v\ 3
& x(1+(\textsf{vcc}\ z\ \textsf{in}\ y\ z))
& y=\lambda v.v\ 3 \\
\rightarrow & x(1+(\textsf{vcc}\ z\ \textsf{in}\ y\ z))3
& \textsf{vcc}\ z\ \textsf{in}\ y\ z
& \lambda v.x(1+v)3
& y\ z
& z=\lambda v.x(1+v)3 \\
\rightarrow & x(1+(y\ z))3 & y\ z & \lambda v.x(1+v)3 & \text{The continuation changes} \\
\rightarrow & y\ z & z & y & z \\
\equiv & y\ z & z & \lambda v.v\ 3 & z \\
\rightarrow & z\ 3 & 3 & z & 3 \\
\equiv & z\ 3 & 3 & \lambda v.x(1+v)3 & 3 \\
\rightarrow & x\ (1+3)\ 3 & 1+3 & \lambda v.x\ v\ 3 & 4\\
\rightarrow & x\ 4\ 3 & x\ 4 & \lambda v.v\ 3 & \text{The continuation changes } \\
\rightarrow & x\ 4 & 4 & x & 4\\
\equiv & x\ 4 & 4 & \lambda v.v & 4 \\
\end{array}
\]

\(\textsf{vcc}\ x\ \textsf{in}\ (\textsf{vcc}\ y\ \textsf{in}\ x(1+(\textsf{vcc}\ z\ \textsf{in}\ y\ z)))3\) results in \(4\).

The following summarizes the evaluations by showing only the expression being evaluated at each step.

\[
\begin{array}{cl}
& 1+(\textsf{vcc}\ x\ \textsf{in}\ (x\ 2)+3) \\
\rightarrow& 1+((x\ 2)+3)  \\
\rightarrow& x\ 2 \\
\rightarrow& 1+2 \\
\rightarrow& 3 \\
\end{array}
\]

\[
\begin{array}{cl}
& \textsf{vcc}\ x\ \textsf{in}\ (\textsf{vcc}\ y\ \textsf{in}\ x\ (1+(\textsf{vcc}\ z\ \textsf{in}\ y\ z)))\ 3 \\
\rightarrow & (\textsf{vcc}\ y\ \textsf{in}\ x\ (1+(\textsf{vcc}\ z\ \textsf{in}\ y\ z)))\ 3\\
\rightarrow & x\ (1+(\textsf{vcc}\ z\ \textsf{in}\ y\ z))\ 3\\
\rightarrow & x\ (1+(y\ z))\ 3 \\
\rightarrow & y\ z\\
\rightarrow & z\ 3 \\
\rightarrow & x\ (1+3)\ 3 \\
\rightarrow & x\ 4\ 3 \\
\rightarrow & x\ 4\\
\rightarrow & 4 \\
\end{array}
\]

Two important facts exist. First, \(\textsf{vcc}\) itself computes nothing. It creates a first-class continuation from the continuation and evaluates the body. Its computation equals its body's computation. Since evaluating \(\textsf{vcc}\ x\ \textsf{in}\ (x\ 2)+3\) is evaluating \((x\ 2)+3\), \(1+(\textsf{vcc}\ x\ \textsf{in}\ (x\ 2)+3)\) is equivalent to \(1+((x\ 2)+3)\). Second, calling a continuation removes anything other than the continuation and the argument. In \(1+((x\ 2)+3)\), because \(x\) is a continuation, it becomes \(x\ 2\). In \(x\ (1+(y\ z))\ 3\), \(y\) is a continuation so that it becomes \(y\ z\). Changing the current continuation to a called continuation corresponds to deleting everything except the call from the current expression.

## Semantics

The article defines the semantics of KFAE in small-step style as the last article does.

\[
\begin{array}{lrcl}
\text{Value} & v & ::= & \cdots \\
&& | & \langle k,s\rangle \\
\end{array}
\]

Since the language supports first-class continuations, a value is either a number, a closure, or a continuation. A continuation is a pair of a computation stack and a value stack. \(\langle k,s\rangle\) denotes a continuation as a value. Its function form is \(\lambda v.\mathit{eval}(k\ ||\ v::s)\) where \(\mathit{eval}(k\ ||\ v::s)\) denotes \(v'\) that makes \(k\ ||\ v::s\rightarrow^\ast\square\ ||\ v'::\blacksquare\) true. Applying \(\langle k,s\rangle\) to value \(v\) reduces the state to \(k\ ||\ v::s\).

KFAE can use rules defining the semantics of FAE but needs two more rules. One is for expression \(\textsf{vcc}\ x\ \textsf{in}\ e\), and the other is for \((@)\) when a value stack contains a continuation.

\[
\sigma\vdash\textsf{vcc}\ x\ \textsf{in}\ e::k\ ||\ s\rightarrow
\sigma[x\mapsto\langle k,s\rangle]\vdash e::k\ ||\ s
\]

Expression \(\textsf{vcc}\ x\ \textsf{in}\ e\) evaluates \(e\) where \(x\) denotes the current continuation. If the current state is \(\sigma\vdash\textsf{vcc}\ x\ \textsf{in}\ e::k\ ||\ s\), a redex is \(\textsf{vcc}\ x\ \textsf{in}\ e\), and the continuation is \(\langle k,s\rangle\). Reducing the state changes the top of the computation stack to \(\sigma[x\mapsto\langle k,s\rangle]\vdash e\).

To define a rule applying a continuation to a value, check the existing rule for a function application.

\[
(@)::k\ ||\ v::\langle\lambda x.e,\sigma\rangle::s\rightarrow
\sigma[x\mapsto v]\vdash e::k\ ||\ s
\]

Let a continuation reside in the value stack. The current state is \((@)::k\ ||\ v::\langle k',s'\rangle::s\). The continuation in the value stack is \(\langle k,s\rangle\). \(v\) is the argument. The function form of the continuation is \(\lambda v.\mathit{eval}(k'\ ||\ v::s')\). Reduction results in state \(k'\ ||\ v::s'\).

\[
(@)::k\ ||\ v::\langle k',s'\rangle::s\rightarrow
k'\ ||\ v::s'
\]

The current continuation, \(k\ ||\ s\), completely disappears.

The following reduces a previously shown example according to the semantics. Let \(\sigma\) be \(\lbrack x\mapsto\langle(+)::\square\ ||\ 1::\blacksquare\rangle\rbrack\).

\[
\begin{array}{lrcr}
& \emptyset\vdash 1+(\textsf{vcc}\ x\ \textsf{in}\ ((x\ 2)+3))::\square &||& \blacksquare \\
\rightarrow & \emptyset\vdash 1::\emptyset\vdash \textsf{vcc}\ x\ \textsf{in}\ ((x\ 2)+3)::(+)::\square &||& \blacksquare \\
\rightarrow & \emptyset\vdash \textsf{vcc}\ x\ \textsf{in}\ ((x\ 2)+3)::(+)::\square &||& 1::\blacksquare \\
\rightarrow & \sigma\vdash (x\ 2)+3::(+)::\square &||& 1::\blacksquare \\
\rightarrow & \sigma\vdash x\ 2::\sigma\vdash 3::(+)::(+)::\square &||& 1::\blacksquare \\
\rightarrow & \sigma\vdash x::\sigma\vdash 2::(@)::\sigma\vdash 3::(+)::(+)::\square &||& 1::\blacksquare \\
\rightarrow & \sigma\vdash 2::(@)::\sigma\vdash 3::(+)::(+)::\square &||& \langle(+)::\square \ ||\  1::\blacksquare\rangle:: 1::           \blacksquare \\
\rightarrow & (@)::\sigma\vdash 3::(+)::(+)::\square &||& 2::\langle(+)::\square \ ||\  1::\blacksquare\rangle::1::           \blacksquare \\
\rightarrow & (+)::\square &||& 2::1::\blacksquare \\
\rightarrow & \square &||& 3::\blacksquare \\
\end{array}
\]

Two steps of reduction use the new rules.

\[\begin{array}{lrcr}
& \emptyset\vdash \textsf{vcc}\ x\ \textsf{in}\ ((x\ 2)+3)::(+)::\square &||& 1::\blacksquare \\
\rightarrow & \lbrack x\mapsto\langle(+)::\square\ ||\ 1::\blacksquare\rangle\rbrack\vdash (x\ 2)+3::(+)::\square &||& 1::    \blacksquare \\
\end{array}\]

The top of the computation stack changes to compute the body of the \(\textsf{vcc}\) expression. Note that the environment contains \(\langle(+)::\square\ ||\ 1::\blacksquare\rangle\), which is a continuation.

\[\begin{array}{lrcr}
&(@)::\sigma\vdash 3::(+)::(+)::\square &||& 2::\langle(+)::\square \ ||\  1::\blacksquare\rangle::1::\blacksquare \\
\rightarrow & (+)::\square &||& 2::1::\blacksquare \\
\end{array}\]

The original continuation, \(\sigma\vdash 3::(+)::(+)::\square\ ||\ 1::\blacksquare\), vanishes. Since continuation \(\langle(+)::\square\ ||\ 1::\blacksquare\rangle\) is called, the result of the reduction is \((+)::\square\ ||\ 2::1::\blacksquare\).

Recall that \(x\) denotes \(\langle(+)::\square\ ||\ 1::\blacksquare\rangle\). Applying \(x\) to value \(v\) makes the following true:

\[\begin{array}{lrcr}
& (+)::\square &||&  v::1::\blacksquare \\
\rightarrow & \square &||&  1+v::\blacksquare \\
\end{array}\]

It coincides with the first section, which mentions that \(x\) equals \(\lambda v.1+v\).

The following shows another example:

\[
\begin{array}{rcl}
v_1&=&\langle\square,\blacksquare\rangle\\
v_2&=&\langle\sigma_1\vdash3::(@)::\square,\blacksquare\rangle\\
v_3&=&\langle(+)::(@)::\sigma_1\vdash3::(@)::\square,1::v_1::\blacksquare\rangle\\
\sigma_1&=&[x\mapsto v_1] \\
\sigma_2&=&\sigma_1[y\mapsto v_2] \\
\sigma_3&=&\sigma_2[z\mapsto v_3]
\end{array}
\]

\[
\begin{array}{lrcr}
& \emptyset\vdash\textsf{vcc}\ x\ \textsf{in}\ (\textsf{vcc}\ y\ \textsf{in}\ x(1+(\textsf{vcc}\ z\ \textsf{in}\ y\     z)))3
::\square &||& \blacksquare \\
\rightarrow& \sigma_1\vdash(\textsf{vcc}\ y\ \textsf{in}\ x(1+(\textsf{vcc}\ z\ \textsf{in}\ y\ z)))3
::\square &||& \blacksquare \\
\rightarrow& \sigma_1\vdash\textsf{vcc}\ y\ \textsf{in}\ x(1+(\textsf{vcc}\ z\ \textsf{in}\ y\ z))::\sigma_1\vdash3::(@)
::\square &||& \blacksquare \\
\rightarrow& \sigma_2\vdash x(1+(\textsf{vcc}\ z\ \textsf{in}\ y\ z))::\sigma_1\vdash3::(@)
::\square &||& \blacksquare \\
\rightarrow& \sigma_2\vdash x::\sigma_2\vdash 1+(\textsf{vcc}\ z\ \textsf{in}\ y\ z)::(@)::\sigma_1\vdash3::(@)
::\square &||& \blacksquare \\
\rightarrow& \sigma_2\vdash 1+(\textsf{vcc}\ z\ \textsf{in}\ y\ z)::(@)::\sigma_1\vdash3::(@)
::\square &||& v_1::\blacksquare \\
\rightarrow& \sigma_2\vdash 1::\sigma_2\vdash\textsf{vcc}\ z\ \textsf{in}\ y\ z::(+)::(@)::\sigma_1\vdash3::(@)
::\square &||& v_1::\blacksquare \\
\rightarrow& \sigma_2\vdash\textsf{vcc}\ z\ \textsf{in}\ y\ z::(+)::(@)::\sigma_1\vdash3::(@)
::\square &||& 1::v_1::\blacksquare \\
\rightarrow& \sigma_3\vdash y\ z::(+)::(@)::\sigma_1\vdash3::(@)
::\square &||& 1::v_1::\blacksquare \\
\rightarrow& \sigma_3\vdash y::\sigma_3\vdash z::(@)::(+)::(@)::\sigma_1\vdash3::(@)
::\square &||& 1::v_1::\blacksquare \\
\rightarrow& \sigma_3\vdash z::(@)::(+)::(@)::\sigma_1\vdash3::(@)
::\square &||& v_2::1::v_1::\blacksquare \\
\rightarrow& (@)::(+)::(@)::\sigma_1\vdash3::(@)
::\square &||& v_3::v_2::1::v_1::\blacksquare \\
\rightarrow& \sigma_1\vdash3::(@)
::\square &||& v_3::\blacksquare \\
\rightarrow& (@)
::\square &||& 3::v_3::\blacksquare \\
\rightarrow& (+)::(@)::\sigma_1\vdash3::(@)
::\square &||& 3::1::v_1::\blacksquare \\
\rightarrow& (@)::\sigma_1\vdash3::(@)
::\square &||& 4::v_1::\blacksquare \\
\rightarrow& \square &||& 4::\blacksquare \\
\end{array}
\]

\(x\) denotes \(\langle\square,\blacksquare\rangle\). It coincides \(x=\lambda v.v\).

\(y\) denotes \(\langle\sigma_1\vdash3::(@)::\square,\blacksquare\rangle\). Applying \(y\) to value \(v\) makes the following true:

\[\begin{array}{lrcr}
& \sigma_1\vdash3::(@)::\square &||&  v::\blacksquare \\
\rightarrow & (@)::\square &||&  3::v::\blacksquare
\end{array}\]

It coincides \(y=\lambda v.v\ 3\).

\(z\) denotes \(\langle(+)::(@)::\sigma_1\vdash3::(@)::\square,1::v_1::\blacksquare\rangle\). Applying \(z\) to value \(v\) makes the following true:

\[\begin{array}{lrcr}
& (+)::(@)::\sigma_1\vdash3::(@)::\square &||& v::1::v_1::\blacksquare\\
\rightarrow & (@)::\sigma_1\vdash3::(@)::\square &||& 1+v::v_1::\blacksquare\\
\end{array}\]

It coincides \(z=\lambda v.x\ (1+v)\ 3\) because \(x\) equals \(v_1\).

## Implementing an Interpreter

The following Scala code implements the abstract syntax of KFAE:

```scala
sealed trait Expr
case class Num(n: Int) extends Expr
case class Add(l: Expr, r: Expr) extends Expr
case class Sub(l: Expr, r: Expr) extends Expr
case class Id(x: String) extends Expr
case class Fun(x: String, b: Expr) extends Expr
case class App(f: Expr, a: Expr) extends Expr
case class Vcc(x: String, b: Expr) extends Expr

sealed trait Value
case class NumV(n: Int) extends Value
case class CloV(p: String, b: Expr, e: Env) extends Value
case class ContV(k: Cont) extends Value

type Env = Map[String, Value]
def lookup(x: String, env: Env): Value =
  env.getOrElse(x, throw new Exception)

type Cont = Value => Value

def numVAdd(v1: Value, v2: Value): Value = {
  val NumV(n1) = v1
  val NumV(n2) = v2
  NumV(n1 + n2)
}
def numVSub(v1: Value, v2: Value): Value = {
  val NumV(n1) = v1
  val NumV(n2) = v2
  NumV(n1 - n2)
}
```

An `Vcc` instance expresses a \(\textsf{vcc}\) expression. A `ContV` instance is a first-class continuation. Since the interpreter treats a continuation as a Scala function, a `ContV` instance has a single field whose type is `Cont`, the type of a function from a KFAE value to a KFAE value.

The `interp` function needs the `Vcc` case.

```scala
case Vcc(x, b) =>
  interp(b, env + (x -> ContV(k)), k)
```

It follows the rule:

\[
\sigma\vdash\textsf{vcc}\ x\ \textsf{in}\ e::k\ ||\ s\rightarrow
\sigma[x\mapsto\langle k,s\rangle]\vdash e::k\ ||\ s
\]

The continuation remains the same; the environment for the body contains the continuation.

The `interp` function must handle continuation applications in the `App` case.

```scala
case App(e1, e2) =>
  interp(e1, env, v1 =>
    interp(e2, env, v2 => v1 match {
      case CloV(xv1, ev1, sigmav1) =>
        interp(ev1, sigmav1 + (xv1 -> v2), k)
       case ContV(k) => k(v2)
    })
  )
```

Recall the rule for such cases.

\[
(@)::k\ ||\ v::\langle k',s'\rangle::s\rightarrow
k'\ ||\ v::s'
\]

When `v1` is a `CloV` instance, nothing changes. If `v1` is a `ContV` instance, the expression calls a continuation. The called continuation is the field of \(v1\). \(v2\) is the argument for the continuation. Since a Scala function expresses a continuation, calling the Scala function, the field of \(v1\), is enough. There is no `interp` call. It follows that the rule never adds \(\sigma\vdash e\) to the computation stack.

The following is the entire code of the `interp` function.

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

The following code uses the function to evaluate KFAE expressions:

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
  x => x
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
  x => x
)
// 4
```

To clarify what the interpreter does, I provide a program showing intermediates redexes, continuations, and environments. One can download [the "KFAE.scala" file](https://raw.githubusercontent.com/Medowhill/pl_articles/master/kr/18/KFAE.scala). Go to a directory containing the file. From the Scala REPL, load the file and import everything inside `KFAE`. Pass an KFAE expression to the `run` function.

```
$ scala
Welcome to Scala.
Type in expressions for evaluation. Or try :help.

scala> :load KFAE.scala
args: Array[String] = Array()
Loading KFAE.scala...
defined object KFAE

scala> import KFAE._
import KFAE._

scala> run(...)
```

Some examples follow:

```scala
// 1 + (vcc x in ((x 2) + 3))
run(
  Add(
    Num(1),
    Vcc("x",
      Add(
       App(Id("x"), Num(2)),
       Num(3)
      )
    )
  )
)
```

```
v1 = <(1 + □)>
(1 + vcc x in ((x 2) + 3)) | □                          | ∅
1                          | (□ + vcc x in ((x 2) + 3)) | ∅
vcc x in ((x 2) + 3)       | (1 + □)                    | ∅
((x 2) + 3)                | (1 + □)                    | [x -> v1]
(x 2)                      | (1 + (□ + 3))              | [x -> v1]
x                          | (1 + ((□ 2) + 3))          | [x -> v1]
2                          | (1 + ((v1 □) + 3))         | [x -> v1]
2                          | (1 + □)                    |
1 + 2                      | □                          | ∅
3
```

```scala
// vcc x in
//   (vcc y in
//     x (1 + (vcc z in y z))
//   ) 3
run(
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
  )
)
```

```
v1 = <□>
v2 = <(□ 3)>
v3 = <((v1 (1 + □)) 3)>
vcc x in (vcc y in (x (1 + vcc z in (y z))) 3) | □                             | ∅
(vcc y in (x (1 + vcc z in (y z))) 3)          | □                             | [x -> v1]
vcc y in (x (1 + vcc z in (y z)))              | (□ 3)                         | [x -> v1]
(x (1 + vcc z in (y z)))                       | (□ 3)                         | [x -> v1, y -> v2]
x                                              | ((□ (1 + vcc z in (y z))) 3)  | [x -> v1, y -> v2]
(1 + vcc z in (y z))                           | ((v1 □) 3)                    | [x -> v1, y -> v2]
1                                              | ((v1 (□ + vcc z in (y z))) 3) | [x -> v1, y -> v2]
vcc z in (y z)                                 | ((v1 (1 + □)) 3)              | [x -> v1, y -> v2]
(y z)                                          | ((v1 (1 + □)) 3)              | [x -> v1, y -> v2, z -> v3]
y                                              | ((v1 (1 + (□ z))) 3)          | [x -> v1, y -> v2, z -> v3]
z                                              | ((v1 (1 + (v2 □))) 3)         | [x -> v1, y -> v2, z -> v3]
v3                                             | (□ 3)                         |
3                                              | (v3 □)                        | [x -> v1]
3                                              | ((v1 (1 + □)) 3)              |
1 + 3                                          | ((v1 □) 3)                    | [x -> v1, y -> v2]
4                                              | □                             |
4
```

## Use of First-Class Continuations

A continuation represents the flow of a program. The current continuation at some point is the remaining computation. Changing the current continuation makes the program compute different things. It changes the flow of the program. Imperative languages feature control statements, such as `return`, `break`, and `continue`, to allow programmers to change the flow. However, languages, such as FAE, from the articles do not.

Programmers can change the flow with first-class continuations of KFAE. Programmers change the continuation freely by calling a first-class continuation. It is an arbitrary change of the flow. Statements like `return` change the flow in a fixed way according to their semantics. On the other hand, programmers using KFAE can make continuations with \(\textsf{vcc}\) and call them at any points. Expressivity of first-class continuations surpasses that of control statements. They can encode all the control statements.

Consider the `return` statement. Let \(e\) use \(return\) where \(e\) is the body of \(\lambda x.e\). \(return\) takes one argument. Applying \(return\) to a value makes the function return the value immediately.

\[
\textit{encode}(\lambda x.e)=
\lambda x.\textsf{vcc}\ return\ \textsf{in}\ \textit{encode}(e)
\]

Computation after the end of the function call is computation after the evaluation of the function body. It is the continuation of \(e\), the function body. Calling \(return\) makes the program to jump to the point right after the evaluation of the body. The jump coincides with calling the continuation. Since \(e\) is the body of \(\textsf{vcc}\ return\ \textsf{in}\ \textit{encode}(e)\), the value of \(return\) is the continuation. The encoding is correct.

The following examples uses \(return\):

\[
\textit{encode}(((\lambda x.(return\ 1)+x)\ 2) + 3)=
((\lambda x.\textsf{vcc}\ return\ \textsf{in}\ (return\ 1)+x) 2)+3
\]

Since \(return\ 1\) precedes the addition of \(x\), the result of the function call is \(1\). The final result equals \(4\). The interpreter provides the same result:

```scala
interp(
  Add(
    App(
      Fun("x", Vcc("return",
        Add(
          App(Id("return"), Num(1)),
          Id("x")
        )
      )),
      Num(2)
    ),
    Num(3)
  ),
  Map.empty,
  x => x
)
// 4
```

```scala
run(
  Add(
    App(
      Fun("x", Vcc("return",
        Add(
          App(Id("return"), Num(1)),
          Id("x")
        )
      )),
      Num(2)
    ),
    Num(3)
  )
)
```

```
v1 = <(□ + 3)>
((λx.vcc return in ((return 1) + x) 2) + 3) | □                                                | ∅
(λx.vcc return in ((return 1) + x) 2)       | (□ + 3)                                          | ∅
λx.vcc return in ((return 1) + x)           | ((□ 2) + 3)                                      | ∅
2                                           | ((<λx.vcc return in ((return 1) + x), ∅> □) + 3) | ∅
vcc return in ((return 1) + x)              | (□ + 3)                                          | [x -> 2]
((return 1) + x)                            | (□ + 3)                                          | [x -> 2, return -> v1]
(return 1)                                  | ((□ + x) + 3)                                    | [x -> 2, return -> v1]
return                                      | (((□ 1) + x) + 3)                                | [x -> 2, return -> v1]
1                                           | (((v1 □) + x) + 3)                               | [x -> 2, return -> v1]
1                                           | (□ + 3)                                          |
3                                           | (1 + □)                                          | ∅
1 + 3                                       | □                                                | ∅
4
```

The value of \(return\) is \(\square+3\). It represents the remaining computation, which uses the returned value, after the function call.

The semantics gives the same result:

\[
\begin{array}{rcl}
v1&=&\langle\lambda x.\textsf{vcc}\ return\ \textsf{in}\ (return\ 1)+x,\emptyset\rangle\\
v2&=&
\langle
\emptyset\vdash 3::(+)
::\square, \blacksquare\rangle
\end{array}
\]

\[
\begin{array}{lrcr}
& \emptyset\vdash((\lambda x.\textsf{vcc}\ return\ \textsf{in}\ (return\ 1)+x) 2)+3
::\square &||& \blacksquare \\
\rightarrow& \emptyset\vdash(\lambda x.\textsf{vcc}\ return\ \textsf{in}\ (return\ 1)+x) 2
::\emptyset\vdash 3::(+)
::\square &||& \blacksquare \\
\rightarrow& \emptyset\vdash\lambda x.\textsf{vcc}\ return\ \textsf{in}\ (return\ 1)+x
::\emptyset\vdash 2::(@)
::\emptyset\vdash 3::(+)
::\square &||& \blacksquare \\
\rightarrow& \emptyset\vdash 2::(@)
::\emptyset\vdash 3::(+)
::\square &||&
v1
::\blacksquare \\
\rightarrow& (@)::\emptyset\vdash 3::(+)
::\square &||&
2::v1
::\blacksquare \\
\rightarrow&
\lbrack x\mapsto 2\rbrack\vdash \textsf{vcc}\ return\ \textsf{in}\ (return\ 1)+x::
\emptyset\vdash 3::(+)
::\square &||& \blacksquare \\
\rightarrow&
\lbrack x\mapsto 2,return\mapsto v2\rbrack\vdash (return\ 1)+x::
\emptyset\vdash 3::(+)
::\square &||& \blacksquare \\
\rightarrow&
\lbrack x\mapsto 2,return\mapsto v2\rbrack\vdash return\ 1::
\lbrack x\mapsto 2,return\mapsto v2\rbrack\vdash x::(+)::
\emptyset\vdash 3::(+)
::\square &||& \blacksquare \\
\rightarrow&
\lbrack x\mapsto 2,return\mapsto v2\rbrack\vdash return::
\lbrack x\mapsto 2,return\mapsto v2\rbrack\vdash 1::(@)
\lbrack x\mapsto 2,return\mapsto v2\rbrack\vdash x::(+)::
\emptyset\vdash 3::(+)
::\square &||& \blacksquare \\
\rightarrow&
\lbrack x\mapsto 2,return\mapsto v2\rbrack\vdash 1::(@)
\lbrack x\mapsto 2,return\mapsto v2\rbrack\vdash x::(+)::
\emptyset\vdash 3::(+)
::\square &||& v2::\blacksquare \\
\rightarrow&
(@)::\lbrack x\mapsto 2,return\mapsto v2\rbrack\vdash x::(+)::
\emptyset\vdash 3::(+)
::\square &||& 1::v2::\blacksquare \\
\rightarrow&
\emptyset\vdash 3::(+)
::\square &||& 1::\blacksquare \\
\rightarrow&
(+)
::\square &||& 3::1::\blacksquare \\
\rightarrow&
\square &||& 4::\blacksquare
\end{array}
\]

Now, consider the `break` and `continue` statements. Since KFAE lacks loops, such as `while`, the article introduces them briefly.

Suppose that \(\textsf{while}\ e_1\ e_2\) evaluates \(e_2\) repeatedly while \(e_1\) equals \(true\). When the evaluation terminates, the result is \(()\), which contains zero information. The only `Unit` type value, `()`, in Scala is analogous. Let \(e_2\) use \(break\) and \(continue\). They do not take any arguments.

\[
\textit{encode}(\textsf{while}\ e_1\ e_2)=
\textsf{vcc}\ break\ \textsf{in}\
(\textsf{while}\ e_1\
(\textsf{vcc}\ continue\ \textsf{in}\
\lbrack (break\ ())/break\rbrack\lbrack (continue\ ())/continue\rbrack\mathit{encode}(e_2)))
\]

\(break\) terminates the surrounding loop. Computation after the termination equals the continuation of the whole loop. The body of \(\textsf{vcc}\) is the whole loop, and the value of \(break\) is the continuation.

\(continue\) skips the current iteration. It makes the program jump to the condition expression. It is the continuation of the body of the loop. The body of \(\textsf{vcc}\) is the body of the loop, and the value of \(continue\) is the continuation.

\(break\) and \(continue\) in code written by programmers lack arguments. However, calling a continuation requires an argument. Since the result of a loop always is \(()\), they need to take \(()\) as arguments. For this purpose, the \(\mathit{encode}\) function respectively substitutes \(break\) and \(continue\) with \(break\ ()\) and \(continue\ ()\) in the body of the loop.

Consider the following example:

\[
\textit{encode}(\textsf{while}\ \textsf{true}\ break)=
\textsf{vcc}\ break\ \textsf{in}\
(\textsf{while}\ \textsf{true}\
break\ ())
\]

The encoding omits \(continue\). The value of \(break\) is a continuation doing nothing. Even though the condition always equals true, the evaluation of \(break\ ()\) makes the final result of the program \(()\) and the program terminate.

## Acknowledgments

I thank professor Ryu for giving feedback on the article.
