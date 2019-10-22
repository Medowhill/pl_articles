The article defines F1WAE by adding *first-order functions* to WAE. First-order functions cannot take functions as arguments or return functions. The articles have followed the same pattern, extending a language by defining syntax and semantics, since the previous one. This article and later articles omit tedious details unless complex concepts appear.

F1WAE covered by the article differs from that of the lecture. The lecture defines function definitions and expressions of F1WAE while the article additionally defines programs of F1WAE. Defining programs makes the language complete but is not the main topic of the article. Please focus on the syntax and semantics of calling first-order functions.

## Syntax

The following is the abstract syntax of F1WAE:

\[
\begin{array}{lrcl}
\text{Integer} & n & \in & \mathbb{Z} \\
\text{Variable} & x & \in & \textit{Id} \\
\text{Function Name} & f & \in & \textit{Id} \\
\text{Expression} & e & ::= & n \\
&& | & e + e \\
&& | & e - e \\
&& | & \textsf{val}\ x = e\ \textsf{in}\ e \\
&& | & x \\
&& | & f(e) \\
\text{Value} & v & ::= & n \\
\text{Function Definition} & F & ::= & f(x)=e \\
\text{Program} & P & ::= & e \\
&& | & F;P
\end{array}
\]

Expressions of F1WAE are function applications in addition to those of WAE. \(f(e)\) is a function application, which applies a function named \(f\) to a value denoted by \(e\).

The name, the name of a parameter, and a body expression defines a function. Metavariable \(F\) and \(f\) respectively range over function definitions and function names.

A program is either an expression or the pair of a function definition and a program. In other words, it is an expression following an arbitrary number of function definitions. Metavariable \(P\) ranges over programs.

The following is an example of an F1WAE program:

\[
\begin{array}{l}
id(x)=x; \\
twice(x)=x+x; \\
\textsf{val}\ x=1\ \textsf{in}\ twice(id(x))
\end{array}
\]

## Semantics

\[
\begin{array}{lrcl}
\text{Environment} & \sigma & \in & \mathit{Id}\hookrightarrow \text{Value}
\end{array}
\]

An environment is a partial function from identifiers to values. It stores values denoted by variables.

Evaluating an expression requires not only values denoted by variables but also function definitions denoted by function names.

\[
\begin{array}{lrcl}
\text{Function Environment} & \phi & \in & \mathit{Id}\hookrightarrow (\mathit{Id}\times\text{Expression})
\end{array}
\]

A function environment is a partial function from identifiers to pairs of identifiers and expressions. It stores names of parameters and bodies denoted by function names.

\[\Rightarrow\subseteq\text{Environment}\times\text{Function Environment}\times\text{Expression}\times\text{Value}\]

An environment and a function environment are essential to evaluate an expression. \(\Rightarrow\) is a relation over four sets. \(\sigma;\phi\vdash e\Rightarrow v\) implies that evaluating \(e\) under \(\sigma\) and \(\phi\) yields \(v\).

\[
\frac
{
  f\in\mathit{Domain}(\phi) \quad
  \phi(f)=(x,e') \quad
  \sigma;\phi\vdash e\Rightarrow v' \quad
  \lbrack x\mapsto v'\rbrack;\phi\vdash e'\Rightarrow v
}
{ \sigma;\phi\vdash f(e)\Rightarrow v }
\]

The inference rule defines the semantics of a function application. An environment used by a function body is an environment existing when the function is defined but not called. Function definitions do not belong to the scopes of the binding occurrences of any variables. Therefore, programs define every function under the empty environment. The rule uses \(\lbrack x\mapsto v'\rbrack\) instead of \(\sigma\lbrack x\mapsto v'\rbrack\) to evaluate \(e'\), the body of a function. On the other hand, the scope of the binding occurrence of every function name equals an entire program. A whole program is under the same function environment. The rule uses \(\phi\) to evaluate both \(e\) and \(e'\).

The other rules equal those of WAE except they need function environments.

\[
\sigma;\phi\vdash n\Rightarrow n
\]

\[
\frac
{ \sigma;\phi\vdash e_1\Rightarrow n_1 \quad \sigma;\phi\vdash e_2\Rightarrow n_2 }
{ \sigma;\phi\vdash e_1+e_2\Rightarrow n_1+n_2 }
\]

\[
\frac
{ \sigma;\phi\vdash e_1\Rightarrow n_1 \quad \sigma;\phi\vdash e_2\Rightarrow n_2 }
{ \sigma;\phi\vdash e_1-e_2\Rightarrow n_1-n_2 }
\]

\[
\frac
{
  \sigma;\phi\vdash e_1\Rightarrow v_1 \quad
  \sigma\lbrack x\mapsto v_1\rbrack;\phi\vdash e_2\Rightarrow v_2
}
{ \sigma;\phi\vdash \textsf{val}\ x=e_1\ \textsf{in}\ e_2\Rightarrow v_2 }
\]

\[
\frac
{ x\in\mathit{Domain}(\sigma) }
{ \sigma;\phi\vdash x\Rightarrow \sigma(x)}
\]

The semantics of a program is a relation over function environments, programs, and values. The semantics of an expression has already used \(\Rightarrow\), but using \(\Rightarrow\) for also the semantics of a program retains clarity and thus can be abused for convenience.

\[\Rightarrow\subseteq\text{Function Environment}\times\text{Program}\times\text{Value}\]

\(\phi\vdash P\Rightarrow v\) implies that evaluating \(P\) under \(\phi\) yields \(v\).

\[
\frac
{ \emptyset;\phi\vdash e\Rightarrow v }
{ \phi\vdash e\Rightarrow v }
\]

Evaluating a program without function definitions equals evaluating its expression.

\[
\frac
{ \phi\lbrack f\mapsto(x,e)\rbrack\vdash P\Rightarrow v }
{ \phi\vdash f(x)=e;P\Rightarrow v }
\]

Evaluating a program that is the pair of a function definition and a program equals evaluating the latter program under a function environment containing the function definition.

## Implementing an Interpreter

The following Scala code expresses the abstract syntax of F1WAE:

```scala
sealed trait F1WAE
case class Num(n: Int) extends F1WAE
case class Add(l: F1WAE, r: F1WAE) extends F1WAE
case class Sub(l: F1WAE, r: F1WAE) extends F1WAE
case class With(x: String, i: F1WAE, b: F1WAE) extends F1WAE
case class Id(x: String) extends F1WAE
case class App(f: String, a: F1WAE) extends F1WAE
```

Dictionaries encode both an environment and a function environment. The keys and the values of an environment are strings and integers respectively. The keys and the values of a function environment are strings and pairs of strings and expressions of F1WAE respectively.

```scala
type Env = Map[String, Int]
type FEnv = Map[String, (String, F1WAE)]
```

Function `lookup` finds a value denoted by an identifier from an environment. Function `lookupFD` finds a function denoted by an identifier from a function environment.

```scala
def lookup(x: String, env: Env): Int =
  env.getOrElse(x, throw new Exception)

def lookupFD(f: String, fEnv: FEnv): (String, F1WAE) =
  fEnv.getOrElse(f, throw new Exception)
```

Function `interp` takes an expression, an environment, and a function environment as arguments and calculates a value denoted by the expression.

```scala
def interp(e: F1WAE, env: Env, fEnv: FEnv): Int = e match {
  case Num(n) => n
  case Add(l, r) => interp(l, env, fEnv) + interp(r, env, fEnv)
  case Sub(l, r) => interp(l, env, fEnv) - interp(r, env, fEnv)
  case With(x, i, b) =>
    interp(b, env + (x -> interp(i, env, fEnv)), fEnv)
  case Id(x) => lookup(x, env)
  case App(f, a) =>
    val (x, e) = lookupFD(f, fEnv)
    interp(e, Map(x -> interp(a, env, fEnv)), fEnv)
}
```

The `App` case creates a new environment, which contains a single identifier, to evaluate the body of a function.

The following is an example of calling `interp`:

```scala
// id(x) = x;
// twice(x) = x + x;
// val x = 1 in twice(id(x))
interp(
  With("x", Num(1),
    App("twice",
      App("id", Id("x"))
    )
  ),
  Map.empty,
  Map(
    "id" -> ("x", Id("x")),
    "twice" -> ("x", Add(Id("x"), Id("x")))
  )
)
// 2
```

## Scope

### Static Scope

The semantics and the interpreter use *static scope*. Static scope enforces function bodies to use environments existing when the functions are defined. Calling below function `f` always results in a run-time error.

\[f(x)=x+y\]

Static scope allows finding free identifiers and binding occurrences binding bound occurrences without executing programs. Besides, every bound occurrence is bound to a fixed single binding occurrence. Since code, not an execution, determines entities referred by identifiers under static scope, *lexical scope* is another name of static scope.

Most modern languages feature static scope.

### Dynamic Scope

Unlike static scope, *dynamic scope* makes function bodies use environments from function call-sites. A value denoted by identifier \(y\) of below function \(f\) depends on a call-site. The identifier can be either free or bound. Different binding occurrences may bind it during different calls.

\[f(x)=x+y\]

For example, the below expression denotes \(3\). At each call of \(f\), \(y\) refers to a different value.

\[
\begin{array}{l}
f(x)=x+y; \\
(\textsf{val}\ y=1\ \textsf{in}\ f(0))\ +\ (\textsf{val}\ y=2\ \textsf{in}\ f(0))
\end{array}
\]

The following inference rule defines the semantics of a function application using dynamic scope.

\[
\frac
{
  \phi(f)=(x,e') \quad
  \sigma;\phi\vdash e\Rightarrow v' \quad
  \sigma\lbrack x\mapsto v'\rbrack;\phi\vdash e'\Rightarrow v
}
{ \sigma;\phi\vdash f(e)\Rightarrow v }
\]

Revising the `App` case of the `interp` function makes the interpreter use dynamic scope.

```scala
def interp(e: F1WAE, env: Env, fEnv: FEnv): Int = e match {
  ...
  case App(f, a) =>
    val (x, e) = lookupFD(f, fEnv)
    interp(e, env + (x -> interp(a, env, fEnv)), fEnv)
}
```

Dynamic scope prevents programs from being modular. An environment from a call-site affects the behavior of a function under dynamic scope. Different parts of a program unexpectedly interfere with each other. Programs show unexpected behaviors and become error-prone.

Languages hardly feature dynamic scope. Common LISP is one example. *Macros* in C behave similarly to functions under dynamic scope.

```c
#define f(x) x + y

int main() {
    int y = 0;
    return f(0);
}
```

## Acknowledgments

I thank professor Ryu for giving feedback on the article.
