The article defines FAE by adding *first-class functions* to AE. First-class functions are values: arguments for function calls and the return values of functions. Functions taking functions as arguments or returning functions are not first-order; they are *higher-order*. In most contexts, the terms 'first-class functions' and 'higher-order functions' are interchangeable.

## Syntax

The following is the abstract syntax of FAE:

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
&& | & \langle \lambda x.e,\sigma \rangle \\
\text{Environment} & \sigma & \in & \textit{Id}\hookrightarrow\text{Value}
\end{array}
\]

An expression of FAE is an expression of AE, variable \(x\), *lambda abstraction* \(\lambda x.e\), or *function application* \(e\ e\). A lambda abstraction creates an anonymous function: \(\lambda x.e\) denotes a function whose parameter and body are \(x\) and \(e\) respectively. \(x\) is a binding occurrence. In function application \(e_1\ e_2\), \(e_1\) denotes a function, and \(e_2\) denotes an argument. Evaluating a function application equals applying its function to its argument.

A value of FAE is either an integer or a *closure*. A closure, which is a function as a value, is the pair of a lambda abstraction and the environment of when the lambda abstraction defines the function. Lambda abstractions may have free identifiers, but the environments of closures store values denoted by the free identifiers if a program is correct. Consider the following expression:

\[\lambda x.\lambda y.(x + y)\ 1\ 2\]

\(\lambda y.(x+y)\) contains free identifier \(x\). At run time, when the lambda abstraction is evaluated, the environment of the moment knows that \(x\) refers to \(1\). Hence, the environment of a closure defined by \(\lambda y.(x+y)\) also knows that \(x\) refers to \(1\). The evaluation of the body of a closure happens under the environment of the closure. \(x+y\) does not result in an error. The next section shows the formal semantics of FAE and clarifies how lambda abstractions and function applications operate.

An environment of FAE is a partial function from identifiers to values. Note that values are not only integers but also closures.

## Semantics

The semantics of FAE is a relation over environments, expressions, and values, as that of WAE is.

\[\Rightarrow\subseteq\text{Environment}\times\text{Expression}\times\text{Value}\]

\(\sigma\vdash e\Rightarrow v\) implies that evaluating \(e\) under \(\sigma\) yields \(v\).

The rules for integers, sums, differences, and variables equal those of WAE.

\[
\sigma\vdash n\Rightarrow n
\]

\[
\frac
{ \sigma\vdash e_1\Rightarrow n_1 \quad \sigma\vdash e_1\Rightarrow n_2 }
{ \sigma\vdash e_1+e_2\Rightarrow n_1+n_2 }
\]

\[
\frac
{ \sigma\vdash e_1\Rightarrow n_1 \quad \sigma\vdash e_1\Rightarrow n_2 }
{ \sigma\vdash e_1-e_2\Rightarrow n_1-n_2 }
\]

\[
\frac
{ x\in\mathit{Domain}(\sigma) }
{ \sigma\vdash x\Rightarrow \sigma(x)}
\]

A lambda abstraction creates a closure containing the current environment.

\[
\sigma\vdash \lambda x.e\Rightarrow \langle\lambda x.e,\sigma\rangle
\]

A function application evaluates its both subexpressions. Then, it evaluates the body of the closure under an environment obtained by adding the value of the argument to the environment of the closure.

\[
\frac
{ \sigma\vdash e_1\Rightarrow\langle\lambda x.e,\sigma'\rangle \quad
  \sigma\vdash e_2\Rightarrow v' \quad
  \sigma'\lbrack x\mapsto v'\rbrack\vdash e\Rightarrow v }
{ \sigma\vdash e_1\ e_2\Rightarrow v }
\]

The following proof tree proves that \(\lambda x.\lambda y.(x+y)\ 1\ 2\) yields \(3\).

\[
\frac
{
  {\Large
  \frac
  {
    \begin{array}{c}
    \emptyset\vdash\lambda x.\lambda y.(x+y)\Rightarrow\langle\lambda x.\lambda y.(x+y),\emptyset\rangle \quad
    \emptyset\vdash 1\Rightarrow 1 \\
    \lbrack x\mapsto 1\rbrack\vdash \lambda y.(x+y)\Rightarrow\langle\lambda y.(x+y),\lbrack x\mapsto 1\rbrack\rangle
    \end{array}
  }
  { \emptyset\vdash\lambda x.\lambda y.(x+y)\ 1\Rightarrow\langle\lambda y.(x+y),\lbrack x\mapsto 1\rbrack\rangle }} \quad
  \emptyset\vdash2\Rightarrow 2 \quad
  {\Large
  \frac
  {
    {\huge
    \frac
    { x\in\mathit{Domain}(\lbrack x\mapsto 1,y\mapsto 2\rbrack) }
    { \lbrack x\mapsto 1,y\mapsto 2\rbrack\vdash x\Rightarrow 1 } \quad
    \frac
    { y\in\mathit{Domain}(\lbrack x\mapsto 1,y\mapsto 2\rbrack) }
    { \lbrack x\mapsto 1,y\mapsto 2\rbrack\vdash y\Rightarrow 2 }
    }
  }
  { \lbrack x\mapsto 1,y\mapsto 2\rbrack\vdash x+y\Rightarrow 3 }
  }
}
{ \emptyset\vdash\lambda x.\lambda y.(x+y)\ 1\ 2\Rightarrow 3 }
\]

## Implementing an Interpreter

The following Scala code implements the abstract syntax and environments of FAE:

```scala
sealed trait FAE
case class Num(n: Int) extends FAE
case class Add(l: FAE, r: FAE) extends FAE
case class Sub(l: FAE, r: FAE) extends FAE
case class Id(x: String) extends FAE
case class Fun(x: String, b: FAE) extends FAE
case class App(f: FAE, a: FAE) extends FAE

sealed trait FAEV
case class NumV(n: Int) extends FAEV
case class CloV(p: String, b: FAE, e: Env) extends FAEV

type Env = Map[String, FAEV]
```

Since a value is either an integer or a closure, `FAEV`, instead of `Int`, denotes the type of values. The `NumV` type corresponds to integers; the `CloV` type corresponds to closures. The type of environments is a map from `String` to `FAEV`, but not `Int`.

```scala
def lookup(x: String, env: Env): FAEV =
  env.getOrElse(x, throw new Exception)
```

The `lookup` function finds a value denoted by an identifier from an environment.

```scala
def interp(e: FAE, env: Env): FAEV = e match {
  case Num(n) => NumV(n)
  case Add(l, r) =>
    val NumV(n) = interp(l, env)
    val NumV(m) = interp(r, env)
    NumV(n + m)
  case Sub(l, r) =>
    val NumV(n) = interp(l, env)
    val NumV(m) = interp(r, env)
    NumV(n - m)
  case Id(x) => lookup(x, env)
  case Fun(x, b) => CloV(x, b, env)
  case App(f, a) =>
    val CloV(x, b, fEnv) = interp(f, env)
    interp(b, fEnv + (x -> interp(a, env)))
}
```

The `Num` case creates a `NumV` instance. Both `Add` and `Sub` cases check whether values are integral, respectively calculate the sum or the difference, and create `NumV` instances. The `Id` case equals that of WAE. The `Fun` case constructs a `CloV` instance. The `App` case obtains a closure by evaluating the function, calculates the argument, adds the argument to the environment of the closure, and evaluates the body of the closure.

Passing \(\lambda x.\lambda y.(x+y)\ 1\ 2\) and the empty environment to `interp` results in `NumV(3)`.

```scala
// lambda x.lambda y.(x + y) 1 2
interp(
  App(
    App(
      Fun("x", Fun("y",
        Add(Id("x"), Id("y")))),
      Num(1)
    ),
    Num(2)
  ),
  Map.empty
)
// NumV(3)
```

## Type Errors

Free identifiers are the only reasons for run-time errors of WAE and F1WAE. On the other hand, FAE expressions can result in run-time errors even though they do not contain any free identifiers.

\[1 + \lambda x.x\]

The two premises of the inference rule for sums require both operands to denote integers. The right operand of the above expression denotes a closure and thus does not satisfy the premise. The expression does not yield any values; interpreting the expressions causes a run-time error.

\[1\ 1\]

One of the premises of the inference rule for function applications enforces the former subexpression of a function application to denote a closure. However, \(1\) yields an integer, but not a closure. Like the previous one, the expression does not yield any values and results in a run-time error.

Both expressions cause *type errors*. In the former, an expression denoting a function occurs where an integer must come. In the latter, an expression denoting an integer occurs where a function must come. Errors such as those from the examples are type errors since their reasons are expressions of unexpected types.

Syntactic methods can hardly prevent type errors. Such solutions restrict languages too much. Consider restricting operands of sums and differences to only integers rather than arbitrary expressions. Then, the syntax rejects many useful or trivially correct expressions including \(1+1+1\). In the same manner, restricting the first expressions of function applications to lambda abstractions refuses \(\lambda x.\lambda y.(x+y)\ 1\ 2\) and many others.

A *type system* is the most popular method to avoid type errors before executions. Type systems prove that particular programs never cause type errors at run time without executing them. Since they are the semantics of programs before run time, *static semantics* is another name of them. To distinguish 'semantics,' whom the previous articles and the current article focus on, from static semantics, *dynamic semantics* means 'semantics,' which defines the run-time behaviors of programs. Type systems are out of the scope of the article, and later articles discuss type systems in detail.

## Encoding WAE with FAE

If one can transform every code of a language into code of another language without changing its meaning, the latter can express everything the former expresses. *Encoding* is rewriting a code written in a language with another language.

WAE is encodable with FAE; FAE expresses everything WAE expresses; FAE is at least as *expressive* as WAE. Precisely, FAE is more expressive than WAE. The below \(\mathit{encode}\) function takes an expression of WAE as an argument and returns an expression of FAE; it encodes WAE with FAE.

\[
\begin{array}{l}
\mathit{encode}(n)=n \\
\mathit{encode}(e_1+e_2)=\mathit{encode}(e_1)+\mathit{encode}(e_2) \\
\mathit{encode}(e_1-e_2)=\mathit{encode}(e_1)-\mathit{encode}(e_2) \\
\mathit{encode}(\textsf{val}\ x = e_1\ \textsf{in}\ e_2)=
\lambda x.\mathit{encode}(e_2)\ \mathit{encode}(e_1) \\
\mathit{encode}(x)=x
\end{array}
\]

Most cases are straightforward. The most important case is an expression declaring a local variable. Hereafter, examples may declare local variables for brevity even though they are written in languages without the feature. It is safe to transform them to use lambda expressions and function applications.

Encoding complex languages with simple, but expressive languages can be understood as desugaring code written in a language with various syntactic sugar. Syntactic sugar provides convenience for programmers; desugaring provides convenience for language designers: it simplifies implementations of interpreters and compilers. Researchers often encode languages with others as well: encoding makes proofs easier or allows borrowing already proven facts.

*Structural induction* proves the correctness of encodings. Such proofs are beyond the scope of the article and the course.

## Acknowledgments

I thank professor Ryu for giving feedback on the article.
