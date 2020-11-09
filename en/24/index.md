This article introduces *de Bruijn indices*, which allow representing expressions without giving names to variables. By using de Bruijn indices, expressions become nameless, and problems arising from naming conflicts can be avoided.

## Motivation

All the previous articles distinguish different variables by naming them. For example, \(\lambda {\tt x}.\lambda {\tt y}.{\tt x}\) is a function that takes an argument twice and returns the first argument. Since two arguments have different names, one can easily conclude that the first argument is the result. The first argument is named \(\tt x\), and the second argument is named \(\tt y\). Therefore, \(\tt x\) in the function body denotes the first argument.

Naming variables is an intuitive and practically useful way to represent variables. However, it becomes problematic in some cases like formalizing the semantics of languages and implementing interpreters and compilers, which take source code as input.

First, two variables may not be distinguished when their names are the same. Environments can easily deal with variables of the same name well, but substitution is often used instead of environments to define the semantics of languages. For instance, defining the semantics of function applications with substitutions is as follows: evaluating \((\lambda {\tt x}.{\tt x} + {\tt x})\ 1\) is the same as evaluating \(1+1\), which is obtained by substituting \(\tt x\) with \(1\) in the function body \({\tt x}+{\tt x}\). Since the main purpose of mentioning substitutions is explaining the problem of naming variables, I will not formally define substitutions. In fact, it is difficult to define the semantics correctly with substitutions. Consider the expression \((\lambda {\tt f}.\lambda {\tt x}.{\tt f})\ \lambda {\tt y}.{\tt x}\). By applying the same principle, evaluating the expression is the same as evaluating \(\lambda {\tt x}.\lambda {\tt y}.{\tt x}\), which is obtained by substituting \(\tt f\) with \(\lambda {\tt y}.{\tt x}\) in \(\lambda {\tt x}.{\tt f}\). Alas, it is wrong. \(\tt x\) in the original argument \(\lambda {\tt y}.{\tt x}\) is a free identifier, while \(\tt x\) in \(\lambda {\tt x}.\lambda {\tt y}.{\tt x}\) is a binding occurrence. The meaning of \(\tt x\) before and after the substitution is completely different. This example shows that the current semantics is incorrect, and the root cause of the problem is that two different variables are named \(\tt x\).

Such naming conflicts can be found even in type systems. The “Algebraic Data Types” and “Parametric Polymorphism” articles explained various sources of unsoundness in TVFAE and TPFAE. One of the sources was that the type systems allowed defining types of the same names, and it was resolved by revising the typing rules to disallow such types. Since names are used to distinguish different types, different types can be incorrectly considered as the same one when their names are the same.

Second, names hinder us from checking the semantic equivalence of expressions. For example, both \(\lambda {\tt x}.{\tt x}\) and \(\lambda {\tt y}.{\tt y}\) are identity functions. However, a naïve syntactic check cannot prove the semantic equivalence of them, i.e. that their behaviors are the same, because the first expression names the parameter \(\tt x\), while the second expression names the parameter \(\tt y\). The ability to check semantic equivalence is valuable in many places. Consider optimization of expressions.

\[
\begin{array}{l}
{\sf val}\ {\tt f}=\lambda {\tt x}.{\tt x}; \\
{\sf val}\ {\tt g}=\lambda {\tt y}.{\tt y}; \\
({\tt f}\ 1)+({\tt g}\ 2) \\
\end{array}
\]

The above expression defined the functions \(\tt f\) and \(\tt g\) and, then, evaluate \(({\tt f}\ 1)+({\tt g}\ 2)\). \(\tt f\) and \(\tt g\) are semantically equivalent, but the names of their parameter are different. If a compiler is aware of their equivalence, it can reduce the size of the binary code by modifying the expression like below:

\[
\begin{array}{l}
{\sf val}\ {\tt f}=\lambda {\tt x}.{\tt x}; \\
({\tt f}\ 1)+({\tt f}\ 2) \\
\end{array}
\]

In languages with parametric polymorphism, the names of type parameters make the comparison of types difficult. Consider TPFAE. Both \(\forall\alpha.\alpha\rightarrow\alpha\) and \(\forall\beta.\beta\rightarrow\beta\) are the types of a polymorphic identity function. Therefore, an expression of the type \(\forall\beta.\beta\rightarrow\beta\) should be able to appear where an expression of the type \(\forall\alpha.\alpha\rightarrow\alpha\) is required. Sadly, a naïve syntactic comparison of types cannot achieve the goal.

The above two examples show the importance of comparing two expressions (or two types). Naming variables (or type variables) is not a good way for this purpose.

Names are often problematic in programming languages. If different entities are named equally, they may not be distinguished correctly. The semantic equivalence of functions may not be proved because of different parameter names. Multiple solutions have been proposed to resolve the issue. This article introduces a de Bruijn index, which is one of those solutions. De Bruijn indices represent variables with indices, not names. For simplicity, the article only deals with de Bruijn indices for variables. In fact, de Bruijn indices can be used anywhere names lead to a problem.

## De Bruijn Indices

De Bruijn indices represent variables with indices, which are natural numbers. The number of \(\lambda\) between a bound occurrence and the corresponding binding occurrence represents the binding occurrence. For instance, \(\lambda.\underline{0}\) is the nameless version of \(\lambda {\tt x}.{\tt x}\). \(\lambda.\underline{0}\) is a function with one parameter. Its body is \(\underline{0}\), which differs from a natural number \(0\). \(\underline{0}\) denotes a variable whose distance from its definition is zero. The distance means the number of \(\lambda\). Therefore, the parameter of \(\lambda.\underline{0}\) is the one that \(\underline{0}\) is bound to. In a similar fashion, \(\lambda.\lambda.\underline{1}\) is the nameless version of \(\lambda {\tt x}.\lambda{\tt y}.{\tt x}\). \(\lambda.\lambda.\underline{1}\) is a function with one parameter and the body expression \(\lambda.\underline{1}\). \(\lambda.\underline{1}\) also is a function with one parameter. Its body is \(\underline{1}\), which is a variable whose distance from the definition is one. Thus, the parameter of \(\lambda.\underline{1}\) cannot be denoted by \(\underline{1}\). There is no \(\lambda\) between the parameter and \(\underline{1}\). \(\underline{1}\) denotes the parameter of \(\lambda.\lambda.\underline{1}\) because there are one \(\lambda\) in between. The following shows various examples of de Bruijn indices.

* \(\lambda {\tt x}.{\tt x}\rightarrow\lambda.\underline{0}\)
* \(\lambda {\tt x}.\lambda {\tt y}.{\tt x}\rightarrow\lambda.\lambda.\underline{1}\)
* \(\lambda {\tt x}.\lambda {\tt y}.{\tt y}\rightarrow\lambda.\lambda.\underline{0}\)
* \(\lambda {\tt x}.\lambda {\tt y}.{\tt x}+{\tt y}\rightarrow\lambda.\lambda.\underline{1}+\underline{0}\)
* \(\lambda {\tt x}.\lambda {\tt y}.{\tt x}+{\tt y}+42\rightarrow\lambda.\lambda.\underline{1}+\underline{0}+42\)
* \(\lambda {\tt x}.({\tt x}\ \lambda {\tt y}.({\tt x}\ {\tt y}))\rightarrow\lambda.(\underline{0}\ \lambda.(\underline{1}\ \underline{0}))\)
* \(\lambda {\tt x}.((\lambda {\tt y}.{\tt x})\ (\lambda {\tt z}.{\tt x}))\rightarrow\lambda.((\lambda.\underline{1})\ (\lambda.\underline{1}))\)

It is important to notice that different indices can denote the same variable, and the same indices can denote different variables. Consider the second example from the bottom. The first \(\underline{0}\) in \(\lambda.(\underline{0}\ \lambda.(\underline{1}\ \underline{0}))\) denotes \(\tt x\) of the original expression. At the same time, \(\underline{1}\) also denotes \(\tt x\) of the original expression. On the other hand, the second \(\underline{0}\) denotes \(\tt y\) of the original expression. The distance from the definition depends on the location of a variable. Since de Bruijn indices represent variables with the distances, the indices of a single variable can vary among places.

Note that expressions should be treated as trees, not strings, to calculate the distances. Consider the last example. There are two \(\lambda\)’s between the last \(\tt x\) and its definition when the expression is written as a string. However, when the abstract syntax tree representing the expression is considered, there is only one \(\lambda\) in between. Therefore, the index of the last \(\tt x\) is \(\underline{1}\), not \(\underline{2}\). We usually write expressions as strings for convenience, but they always have tree structures in fact.

De Bruijn indices successfully resolve the issues arising from names. Consider the comparison of expressions. \(\lambda {\tt x}.{\tt x}\) and \(\lambda {\tt y}.{\tt y}\) are semantically equivalent but syntactically different expressions. Both become \(\lambda.\underline{0}\) when de Bruijn indices are used. By the help of de Bruijn indices, a simple syntactic check will find out that two expressions are equal.

Let us define the procedure that transform expressions with names into nameless expressions. It will help understanding de Bruijn indices. At the same time, the procedure is practically valuable. Use of names is the best way to denote variables for programmers. Therefore, expressions written by programmers have names. On the other hand, programs like interpreters and compilers sometimes need to use de Bruijn indices to represent variables. In such cases, the procedure is a part of the interpreter/compiler implementation. This article focuses on the transform procedure for FAE. It is easy to modify the procedure so that it can work for another language.

Below is the definition of an expression with and without names. Strictly speaking, two different metavariables should denote each kind of expressions. For brevity, I abuse the notation, so \(e\) is used for both sorts of expressions.

\[
\begin{array}{lrcl}
\text{Expression} & e & ::= & x \\
&&|& \lambda x.e \\
&&|& e\ e \\
&&|& n \\
&&|& e+e \\
\end{array}
\]

\[
\begin{array}{lrcl}
\text{Index} & i & \in & \mathbb{N} \\
\text{Expression} & e & ::= & \underline{i} \\
&&|& \lambda.e \\
&&|& e\ e \\
&&|& n \\
&&|& e+e \\
\end{array}
\]

The former defines expressions with names; the latter defines nameless expressions.

In nameless expressions, natural numbers represent variables. Those numbers have underlines and, therefore, cannot be confused with integers in FAE. Lambda abstractions \(\lambda.e\) lack the names of their parameters. Note that \(\lambda.e\) does have a single parameter. It is not a function with zero parameters.

In addition, a context, which is a partial function from a name to a natural number, takes an important role during the transformation. A context gives the distance between a variable and its definition.

\[\chi\in{\it Id}\hookrightarrow\mathbb{N}\]

Let \([e]\chi\) be a nameless expression representing \(e\) under a context \(\chi\). The definition of \([e]\chi\) is as follows:

\[
\begin{array}{rcl}
[x]\chi &=& \underline{i}\ \ \text{if}\ \chi(x)=i \\
[\lambda x.e]\chi &=& \lambda.[e]{\chi'}\ \ \text{where}\ \chi'=(\uparrow\chi)[x\mapsto 0] \\
[e_1\ e_2]\chi &=& [e_1]\chi\ [e_2]\chi \\
[n]\chi &=& n \\
[e_1+e_2]\chi &=& [e_1]\chi+[e_2]\chi \\
\end{array}
\]

\([x]\chi\) is the result of transforming \(x\). A natural number represents a variable, and the natural number can be found in \(\chi\). Therefore, when \(\chi(x)\) is \(i\), \(x\) is transformed in to \(\underline{i}\).

\([\lambda x.e]\chi\) is the result of transforming \(\lambda x.e\) and should look like \(\lambda.e\). However, \(e\) uses names and, thus, needs to be transformed. \(\chi\) is not the correct context for the transformation of \(e\). First, it lacks the information of \(x\). If \(x\) appears in \(e\) without any function definitions, there is no \(\lambda\) between the use and the definition. The context must know that the index of \(x\) is 0. In addition, indices in \(\chi\) need changes. Suppose that \(x'\) is in \(\chi\) and its index is 0. If \(x'\) occurs in \(e\), its index is not 0 anymore. Since \(e\) is the body of \(\lambda x.e\), there is one \(\lambda\) between \(x'\) and is definition. During the transformation of \(e\), the index of \(x'\) is 1, not 0. Similarly, if there is a variable whose index of 1 in \(\chi\), its index must be 2 during the transformation of \(e\). In conclusion, every index in \(\chi\) has to increase by one. \(\uparrow\chi\) denotes the context same as \(\chi\) but whose indices are one larger. The context used during the transformation of \(e\) is \((\uparrow\chi)[x\mapsto0]\). \([\lambda x.e]\chi\) is \(\lambda.[e]{\chi'}\) where \(\chi'\) is \((\uparrow\chi)[x\mapsto0]\).

The remaining cases are straightforward. The transformations of \(e_1\ e_2\) and \(e_1+e_2\) are recursively defined. Since \(n\) does not contain variables, \(n\) itself is the result.

Below shows how \(\lambda {\tt x}.\lambda {\tt y}.{\tt x}+{\tt y}\) is transformed by the procedure. In the beginning, the context is empty because there is no variable yet.

\[
\begin{array}{cl}
& [\lambda {\tt x}.\lambda {\tt y}.{\tt x}+{\tt y}]\emptyset \\
= & \lambda.[\lambda {\tt y}.{\tt x}+{\tt y}][{\tt x}\mapsto 0] \\
= & \lambda.\lambda.[{\tt x}+{\tt y}][{\tt x}\mapsto 1,{\tt y}\mapsto 0] \\
= & \lambda.\lambda.[{\tt x}][{\tt x}\mapsto 1,{\tt y}\mapsto 0]+[{\tt y}][{\tt x}\mapsto 1,{\tt y}\mapsto 0] \\
= & \lambda.\lambda.\underline{1}+[{\tt y}][{\tt x}\mapsto 1,{\tt y}\mapsto 0] \\
= & \lambda.\lambda.\underline{1}+\underline{0} \\
\end{array}
\]

Now, let us implement the procedure in Scala.

```scala
sealed trait Expr
case class Id(x: String) extends Expr
case class Fun(x: String, e: Expr) extends Expr
case class App(f: Expr, a: Expr) extends Expr
case class Num(n: Int) extends Expr
case class Add(l: Expr, r: Expr) extends Expr
```

The above defines expressions with names.

```scala
object Nameless {
  sealed trait Expr
  case class Id(i: Int) extends Expr
  case class Fun(e: Expr) extends Expr
  case class App(f: Expr, a: Expr) extends Expr
  case class Num(n: Int) extends Expr
  case class Add(l: Expr, r: Expr) extends Expr
}
```

Nameless expressions are defined in the `Nameless` singleton object. `Id(i)` is a variable whose index is `i`; `Fun(e)` is a function with one parameter and the body expression `e`.

```scala
type Ctx = Map[String, Int]
```

`Ctx`, the type of a context, is a map from a string to an integer.

Let the `transform` function recursively transform an expression with names into a nameless expression.

```scala
def transform(e: Expr, ctx: Ctx): Nameless.Expr = e match {
  case Id(x) => Nameless.Id(ctx(x))
  case Fun(x, e) =>
    Nameless.Fun(transform(e, ctx.map{ case (x, i) => x -> (i + 1) } + (x -> 0)))
  case App(f, a) =>
    Nameless.App(transform(f, ctx), transform(a, ctx))
  case Num(n) => Nameless.Num(n)
  case Add(l, r) =>
    Nameless.Add(transform(l, ctx), transform(r, ctx))
}
```

The function exactly looks like its mathematical definition, so it is easy to understand the code.

The following program transforms \(\lambda {\tt x}.\lambda {\tt y}.{\tt x}+{\tt y}\) with `transform`:

```scala
// lambda x.lambda y.x+y
transform(Fun("x", Fun("y", Add(Id("x"), Id("y")))), Map())
// Fun(Fun(Add(Id(1),Id(0))))
// lambda.lambda._1+_0
```

Lists can replace maps in the implementation. A context is a list of names, and the index of a name is the location of the name in the list. Lists simplify the implementation. When a name is added to a context, its index is always zero. It means that the name is the head of the list. Adding a name is the same as making the head of the list be the name. Increasing every index by one is the same as moving each name backward by one slot. Therefore, if a context is a list, prepending a new name in front of the list will do everything we need. For example, consider a context containing \(\tt x\) and \(\tt y\). Let the indices of \(\tt x\) and \(\tt y\) respectively be 0 and 1. The context is represented by the list \([{\tt x},{\tt y}]\). It is enough to prepend \(\tt z\) to the list to add \(\tt z\) to the context. The resulting list is \([{\tt z},{\tt x},{\tt y}]\): \(\tt z\) at index 0, \(\tt x\) at index 1, and \(\tt y\) at index 2. Since \(\tt z\) is the new one, its index should be 0. At the same time, the indices of \(\tt x\) and \(\tt y\) should be greater by one than before. The new list does represent the new context well.

```scala
type Ctx = List[String]
```

Now, `Ctx` is a list of strings.

```scala
def transform(e: Expr, ctx: Ctx): Nameless.Expr = e match {
  case Id(x) => Nameless.Id(ctx.indexOf(x))
  case Fun(x, e) => Nameless.Fun(transform(e, x :: ctx))
  case App(f, a) =>
    Nameless.App(transform(f, ctx), transform(a, ctx))
  case Num(n) => Nameless.Num(n)
  case Add(l, r) =>
    Nameless.Add(transform(l, ctx), transform(r, ctx))
}
```

The `Id` case needs to calculate the location of a given variable in a given context. It is enough to use `indexOf`. In the `Fun` case, `x :: ctx` is everything we need to add `x` to `ctx`.

The following program transforms \(\lambda {\tt x}.\lambda {\tt y}.{\tt x}+{\tt y}\) with `transform`:

```scala
// lambda x.lambda y.x+y
transform(Fun("x", Fun("y", Add(Id("x"), Id("y")))), Nil)
// Fun(Fun(Add(Id(1),Id(0))))
// lambda.lambda._1+_0
```

## Evaluation of Nameless Expressions

Evaluation of nameless expressions is similar to evaluation of expressions with names. The definitions of values and environments are as follows:

\[
\begin{array}{rrcl}
\text{Value} & v & ::= & n \\
&&|& \langle\lambda.e,\sigma\rangle \\
\text{Environment} & \sigma & \in & \mathbb{N}\hookrightarrow\text{Value}
\end{array}
\]

As lambda abstractions lack parameter names, closures also lack parameter names. Environments are partial functions from indices, which are natural numbers, to values.

\[
\frac
{ i\in{\it Domain}(\sigma) }
{ \sigma\vdash\underline{i}\Rightarrow\sigma(i) }
\]

The value of a variable can be found in a given environment.

\[
\sigma\vdash\lambda.e\rightarrow\langle\lambda.e,\sigma\rangle
\]

A lambda abstraction evaluates to a closure without evaluating anything.

\[
\frac
{
  \sigma\vdash e_1\Rightarrow\langle\lambda.e,\sigma'\rangle \quad
  \sigma\vdash e_2\Rightarrow v_2 \quad
  (\uparrow\sigma')[0\mapsto v_2]\vdash e\Rightarrow v
}
{ \sigma\vdash e_1\ e_2\Rightarrow v }
\]

Evaluation of \(e_1\ e_2\) evaluates both \(e_1\) and \(e_2\). Then, the body of the closure is evaluated under the environment captured by the closure with the value of the argument. If the parameter is used in the body, there is no \(\lambda\) between the use and the definition. Its index is 0. Therefore, the value of the argument has the index 0 in the new environment. In addition, every index in the environment of the closure needs a change. Let a value \(v\) correspond to the index 0. The value is not the value of the argument, so it cannot correspond to the index 0 anymore. As \(\lambda\) from the closure exists between the use and the definition, the index should increase by one. By the same principle, every index in the environment increases by one. Let \(\uparrow\sigma'\) denote the context same as \(\sigma'\) but whose indices are one larger. Then, the body of the closure is evaluated under \((\uparrow\sigma')[0\mapsto v_2]\).

The rules for integers and sums are omitted because they are the same as those of FAE.

The following proof tree proves that the reulst of \((\lambda.\lambda.\underline{1}+\underline{0})\ 2\ 3\) is \(5\).

\[
\frac
{
  {\Large
  \frac
  {
    \emptyset\vdash\lambda.\lambda.\underline{1}+\underline{0}\Rightarrow\langle\lambda.\lambda.\underline{1}+\underline{0},\emptyset\rangle\quad
    \emptyset\vdash2\Rightarrow2\quad
    [0\mapsto2]\vdash\lambda.\underline{1}+\underline{0}\Rightarrow\langle\lambda.\underline{1}+\underline{0},[0\mapsto2]\rangle
  }
  { \emptyset\vdash(\lambda.\lambda.\underline{1}+\underline{0})\ 2\Rightarrow\langle\lambda.\underline{1}+\underline{0},[0\mapsto2]\rangle }
  }
  \quad
  \emptyset\vdash3\Rightarrow3 \quad
  {\Large
  \frac
  {
    {\huge
    \frac
    { 1\in{\it Domain}(\sigma) }
    { \sigma\vdash\underline{1}\Rightarrow2 }
    \quad
    \frac
    { 0\in{\it Domain}(\sigma) }
    { \sigma\vdash\underline{0}\Rightarrow3 }
    }
  }
  { \sigma\vdash\underline{1}+\underline{0}\Rightarrow5 }
  }
}
{ \emptyset\vdash(\lambda.\lambda.\underline{1}+\underline{0})\ 2\ 3\Rightarrow5 }
\]

\[\sigma=[0\mapsto3,1\mapsto2]\]

Let us implement an interpreter of nameless expressions in Scala. Expressions has been defined already. Below is the definitions of values and environments.

```scala
type Env = List[Value]

sealed trait Value
case class NumV(n: Int) extends Value
case class CloV(e: Expr, env: Env) extends Value
```

An environment is a list of values. As shown by the implementation of `transform`, lists are simpler than maps from integers to values.

```scala
def interp(e: Expr, env: Env): Value = e match {
  case Id(i) => env(i)
  case Fun(e) => CloV(e, env)
  case App(f, a) =>
    val CloV(b, fenv) = interp(f, env)
    interp(b, interp(a, env) :: fenv)
  case Num(n) => NumV(n)
  case Add(l, r) =>
    val NumV(n) = interp(l, env)
    val NumV(m) = interp(r, env)
    NumV(n + m)
}
```

The `App` case is the only interesting case. The others are the same as before. Since a closure lacks its parameter name and an environment does not need the name, it is enough to prepend the value of the argument in front of the list.

The following program evaluates \((\lambda.\lambda.\underline{1}+\underline{0})\ 2\ 3\) with `interp`. The result is \(5\).

```scala
// (lambda.lambda._1+_0) 2 3
interp(
  App(
    App(
      Fun(Fun(Add(Id(1), Id(0)))),
      Num(2)
    ),
    Num(3)
  ),
  Nil
)
// 5
```

Let \(e\) be an expression with names. The result of evaluating \(e\) is the same as evaluating \(e'\) where \(e'\) is a nameless expression obtained by transforming \(e\). Mathematically, \(\forall e,v.(\emptyset\vdash e\Rightarrow v)\leftrightarrow(\emptyset\vdash[e]\emptyset\Rightarrow v)\). (Assume that the equality of closures is defined properly.) In Scala implementation, given `e`, which represents variables with names, `interp(e, Map())` and `interp(transform(e, Nil), Nil)` result in the same value.

The interpreter of this article is an interpreter of FAE and does not use CPS. Those who understand the implementation will be able to implement an interpreter of another language or an interpreter with CPS by using de Bruijn indices.

## Acknowledgments

I thank professor Ryu for giving feedback on the article.
