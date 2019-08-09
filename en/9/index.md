AE defined by the last article features only integers, sums, and differences. This article defines WAE by adding local variables to AE.

## Identifiers

*Identifiers* name entities such as variables, functions, types, and packages. Most languages use strings satisfying specific conditions as identifiers.

```scala
f(0)
def f(x: Int): Int = {
  val y = 2
  x + y
}
f(1)
x - y
```

`f`, `x`, and `y` are identifiers. Strictly speaking, `Int` also is an identifier, but the article ignores it. `f` is the name of a function; `x` is the name of the parameter of `f`; `y` is the name of the local variable of `f`.

Three kinds of identifiers exist.

The first kind is a *binding occurrence*. If the occurrence associates an identifier with an entity, it is a binding occurrence. Both `f` and `x` in `def f(x: Int): Int` are binding occurrences; `y` in `val y = 2` is a binding occurrence. `f` refers to a function, `(x: Int) => { val y = 2; x + y }`; `x` refers to an argument passed to `f`; `y` refers to `2`.

The second kind is a *bound occurrence*. The *scope* of a binding occurrence means a code region where an identifier introduced by the binding occurrence can appear. An occurrence of an identifier in a scope is a bound occurrence. The identifier denotes an entity referred by the identifier. Both `x` and `y` in `x + y` are bound occurrences; the binding occurrences of `x` and `y` respectively bind the bound occurrences. `x` denotes an argument used at a function call-site; `y` denotes `2`. `f` in `f(1)` is a bound occurrence; the binding occurrence of `f` binds the bound occurrence. `f` denotes a function referred by `f`, and `f(1)` thus is a correct function call.

The last kind is a *free identifier*. An identifier not belonging to any scopes of the binding occurrences of the identifier is a free identifier. The scope of the binding occurrence of `f` includes the body of `f` and every line below the definition of `f`. Therefore, `f` in `f(0)` is a free identifier. The scope of `x` equals the body of `f`, and the scope of `y` starts at a line defining `y` and ends at the end of the definition of `f`. Both `x` and `y` in `x – y` are free identifiers. Unlike bound occurrences, free identifiers do not denote any entities. Evaluating an expression containing free identifiers causes an error in most languages; compiling code containing free identifiers results in a compile error.

Consider a bound occurrence that resides in the scopes of more than one binding occurrence simultaneously. Most languages regard an entity referred by a binding occurrence owning the smallest scope as whom the bound occurrence denotes. *Shadowing* means that a binding occurrence with a smaller scope takes priority over a binding occurrence with a larger scope.

```scala
def f(x: Int): Int = {
  def g(x: Int): Int = x
  g(x)
}
```

The code is an example of shadowing. Both `x`'s in `def f(x: Int): Int` and `def g(x: Int): Int` are binding occurrences. The occurrence of `x` at the end of the second line is a bound occurrence. It belongs to the scopes of the two binding occurrences at the same time. As having a smaller scope than the first binding occurrence, the second binding occurrence binds the bound occurrence. The bound occurrence denotes an argument passed to `g`. On the other hand, `x` in `g(x)` belongs to only the scope of the first binding occurrence of `x`. The first binding occurrence binds it, and it denotes an argument passed to `f`.

## WAE

Expressions of WAE are arithmetic expressions that can define immutable local variables and refer to variables.

### Syntax

The following is the abstract syntax of WAE:

\[
\begin{array}{lrcl}
\text{Integer} & n & \in & \mathbb{Z} \\
\text{Variable} & x & \in & \textit{Id} \\
\text{Expression} & e & ::= & n \\
&& | & e + e \\
&& | & e - e \\
&& | & \textsf{val}\ x = e\ \textsf{in}\ e \\
&& | & x \\
\text{Value} & v & ::= & n
\end{array}
\]

Metavariable \(n\) ranges over integers.

Metavariable \(x\) ranges over variables, which belong to set \(\mathit{Id}\). \(\mathit{Id}\) is a set of objects differing from elements of other sets; it is the set of every possible identifier.

Metavariable \(e\) ranges over expressions. Expression \(\textsf{val}\ x=e_1\ \textsf{in}\ e_2\) declares a local variable. The occurrence of \(x\) is a binding occurrence. \(x\) refers to a value denoted by \(e_1\). The scope of the occurrence covers entire \(e_2\) but not \(e_1\). Expression \(x\) is either a bound occurrence of \(x\) or a free identifier. If it is a bound occurrence, it denotes a value associated with the identifier. For example, the first occurrence of \(x\) in \(\textsf{val}\ x=1\ \textsf{in}\ x\) binds the second occurrence of \(x\). On the other hand, the second \(x\) in \(\textsf{val}\ x=x\ \textsf{in}\ 1\) is a free identifier.

Metavariable \(v\) ranges over values. Values of WAE are integers as those of AE are.

### Semantics

The natural semantics of AE defines the semantics of integers, sums, and differences. The natural semantics of WAE additionally requires the semantics of binding and bound occurrences. Consider \(\textsf{val}\ x=1\ \textsf{in}\ x\). The following Scala expression expresses the same thing:

```scala
{
  val x = 1
  x
}
```

It denotes `1`. Since the binding occurrence associates `x` with `1`, the bound occurrence denotes `1`. The value denoted by the entire expression equals `1`, the value of the bound occurrence.

\(\textsf{val}\ x=1\ \textsf{in}\ x\) is evaluated in the same manner. The value denoted by the whole expression equals the value denoted by \(x\), the body of the expression. However, it is not possible to evaluate \(x\) without any information. A value associated with \(x\) by the binding occurrence is necessary. WAE newly defines *environments* to store such information. An environment is a dictionary of finite size; its keys are identifiers, and its values are values of WAE. Mathematically, it is a *partial function* from \(\mathit{Id}\) to \(\text{Value}\).

\[
\begin{array}{lrcl}
\text{Environment} & \sigma & \in & \mathit{Id}\hookrightarrow \text{Value}
\end{array}
\]

Metavariable \(\sigma\) ranges over environments. Binding occurrences add information to environments, and bound occurrences use information in environments.

The natural semantics of AE is a binary relation over \(\text{Expression}\) and \(\text{Value}\). The natural semantics of WAE is not a binary relation of the two sets since evaluating an expression requires an environment providing the values of identifiers in the expression. The natural semantics is a ternary relation over \(\text{Environment}\), \(\text{Expression}\), and \(\text{Value}\).

\[\Rightarrow\subseteq\text{Environment}\times\text{Expression}\times\text{Value}\]

\((\sigma,e,v)\in\Rightarrow\) implies that evaluating expression \(e\) under environment \(\sigma\) results in value \(v\). \(\sigma\vdash e\Rightarrow v\) replaces the notation. Intuitively, an environment and an expression are given input, and a value is output obtained by computation.

Inference rules define the natural semantics of WAE.

\[
\sigma\vdash n\Rightarrow n
\]

The rule equals that of AE.

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

The rules equal those of AE except evaluating *subexpressions* \(e_1\) and \(e_2\) also requires environments. The premises and the conclusion use the same environment.

The rule for a binding occurrence needs a way to add information to an environment. The following defines an extension of an environment.

\[
\sigma \lbrack x\mapsto v\rbrack=\sigma\setminus\{(x',v'):x'=x\land\sigma(x')=v'\}\cup\{(x,v)\}
\]

An environment obtained by adding that identifier \(x\) refers to value \(v\) to environment \(\sigma\) is \(\sigma\) with \((x,v)\) but without any other pairs about (\x\). It has the following property:

\[
\sigma \lbrack x\mapsto v\rbrack(x') =
\begin{cases}
v & \text{if}\ x=x' \\
v' & \text{if}\ x\neq x'\land\sigma(x')=v'
\end{cases}
\]

The \(x=x'\) case shows shadowing.

\[
\frac
{
  \sigma\vdash e_1\Rightarrow v_1 \quad
  \sigma\lbrack x\mapsto v_1\rbrack\vdash e_2\Rightarrow v_2
}
{ \sigma\vdash \textsf{val}\ x=e_1\ \textsf{in}\ e_2\Rightarrow v_2 }
\]

Evaluating \(\textsf{val}\ x=e_1\ \textsf{in}\ e_2\) requires a value denoted by identifier \(x\). Firstly, evaluating \(e_1\) attains \(v_1\). The scope of the binding occurrence excludes \(e_1\) so that the evaluation uses \(\sigma\). A value denoted by the whole expression equals \(v_2\), denoted by \(e_2\). The evaluation of \(e_2\) uses \(\sigma\lbrack x\mapsto v_1\rbrack\), which knows that \(x\) refers to \(v_1\) in addition to information from \(\sigma\).

If an expression is an identifier, it is essential to check whether the identifier is a bound occurrence or a free identifier. The following function calculates the domain of an environment:

\[
\mathit{Domain}(\sigma)=\{x:\exists v.(x,v)\in\sigma\}
\]

\(\mathit{Domain}(\sigma)\) is the domain of \(\sigma\). An identifier that belongs to the domain is a bound occurrence. Otherwise, it is a free identifier.

\[
\frac
{ x\in\mathit{Domain}(\sigma) }
{ \sigma\vdash x\Rightarrow \sigma(x)}
\]

The bound occurrence of \(x\) denotes a value whom \(x\) refers to in the environment. If \(x\) is a free identifier, the premise is false, and, therefore, an expression containing a free identifier does not denote any values. It explains that executing a program with free identifiers results in an error.

The following inference rules are all of the natural semantics of WAE:

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
{
  \sigma\vdash e_1\Rightarrow v_1 \quad
  \sigma\lbrack x\mapsto v_1\rbrack\vdash e_2\Rightarrow v_2
}
{ \sigma\vdash \textsf{val}\ x=e_1\ \textsf{in}\ e_2\Rightarrow v_2 }
\]

\[
\frac
{ x\in\mathit{Domain}(\sigma) }
{ \sigma\vdash x\Rightarrow \sigma(x)}
\]

### Implementing an Interpreter

The following Scala code expresses the abstract syntax of WAE:

```scala
sealed trait WAE
case class Num(n: Int) extends WAE
case class Add(l: WAE, r: WAE) extends WAE
case class Sub(l: WAE, r: WAE) extends WAE
case class With(x: String, i: WAE, b: WAE) extends WAE
case class Id(x: String) extends WAE
```

An identifier is an arbitrary string. `With` constructs an expression declaring a local variable; `Id` constructs an expression using a local variable.

`Map` in the Scala standard library can represent environments. `Map` creates dictionaries.

```scala
val m0: Map[String, Int] = Map.empty
val m1: Map[String, Int] = m0 + ("one" -> 1)
m1.getOrElse("one", -1)  // 1
m1.getOrElse("two", -1)  // -1
```

The `Map` type has two type parameters: the first one is the type of keys, and the second one is the type of values. `Map[String, Int]` is the type of a dictionary whose keys and values are strings and integers respectively. `Map.empty` is a dictionary without any keys and values. The library defines various methods: the `+` method adds the pair of a key and a value to a dictionary; the `getOrElse` method takes two arguments and returns a value corresponding to a key, the first argument, in a dictionary. `getOrElse` evaluates the second argument and returns the result only if the first argument is not a key of the dictionary.

```scala
type Env = Map[String, Int]
```

An environment is a partial function from identifiers to values. Identifiers are strings; values are integers. Therefore, the type of an environment is `Map[String, Int]`. The above expression defines `Env`, a type alias of `Map[String, Int]`. The two types are identical, and an environment thus has type `Env`.

```scala
def lookup(x: String, env: Env): Int =
  env.getOrElse(x, throw new Exception)
```

The `lookup` function returns a value referred by an identifier, the first argument, in an environment, the second argument. It throws an exception if the environment lacks information about the identifier.

```scala
def interp(e: WAE, env: Env): Int = e match {
  case Num(n) => n
  case Add(l, r) => interp(l, env) + interp(r, env)
  case Sub(l, r) => interp(l, env) - interp(r, env)
  case With(x, i, b) => interp(b, env + (x -> interp(i, env)))
  case Id(x) => lookup(x, env)
}
```

The `Num` case equals that of the interpreter, implemented by the last article, of AE. The `Add` and `Sub` cases pass `env` as arguments. The `With` case calculates the value of `b` under an environment extended with the result of evaluating `i` under `env`. The `Id` case calls the `lookup` function to find a value denoted by an identifier.

Calling the `interp` function with arguments \(\textsf{val}\ x=1\ \textsf{in}\ x+x\) and the empty environment yields `2`.

```scala
// val x = 1 in x + x
interp(
  With("x", Num(1),
    Add(Id("x"), Id("x"))
  ),
  Map.empty
)
// 2
```

The following proof tree proves that \(\textsf{val}\ x=1\ \textsf{in}\ x+x\) denotes \(2\) under \(\emptyset\), the empty environment:

\[
\frac
{
  \emptyset\vdash 1\Rightarrow 1 \quad
  \frac
  {
    {\huge
    \frac
    { x\in\mathit{Domain}(\lbrack x\mapsto 1\rbrack) }
    { \lbrack x\mapsto 1\rbrack\vdash x\Rightarrow 1 } \quad
    \frac
    { x\in\mathit{Domain}(\lbrack x\mapsto 1\rbrack) }
    { \lbrack x\mapsto 1\rbrack\vdash x\Rightarrow 1 }
    }
  }
  {{\Large \lbrack x\mapsto 1\rbrack\vdash x+x\Rightarrow 2 }}
}
{ \emptyset\vdash \textsf{val}\ x=1\ \textsf{in}\ x+x\Rightarrow 2 }
\]

## Acknowledgments

I thank professor Ryu for giving feedback on the article.
