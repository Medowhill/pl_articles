This article defines TpolyFAE by adding *parametric polymorphism* to TFAE. It
defines the abstract syntax, the dynamic semantics, and the type system of
TpolyFAE and implements a type checker and an interpreter of TFAE.

## Parametric Polymorphism

A function in TFAE is more restrictive than a function in FAE. Consider
\(\lambda x.x\) in FAE. It is an identity function, which takes a value as an
argument and returns the value without changing it. Any value can be an argument
for this function. Since the body of the function do nothing with the argument,
the evaluation of the body never causes a type error. On the other hand,
\(\lambda x:{\sf num}.x\) in TFAE is an identity function that takes only an
integer. The parameter type annotation restricts the type of an argument to be
only \(\sf num\). The type system rejects a program that passes a nonintegral
value to the function. However, the body does not use the argument. Even if
the argument is nonintegral, a type error never happens. If the parameter type
annotation changes, the function will be able to take a value of another type
as an argument without changing its body. For example, \(\lambda x:{\sf
bool}.x\) is a well-typed identity function for Boolean values. \(\lambda
x:{\sf num}.x\) and \(\lambda x:{\sf bool}.x\) do the exactly same thing but
cannot be represented by a single function just because their parameter types
are different.

The following FAE expression does not cause a type error:

\[
\begin{array}{l}
\textsf{let}\ f=\lambda x.x\ \textsf{in} \\
\textsf{let}\ x=f\ 1\ \textsf{in} \\
f\ \textsf{true}
\end{array}
\]

The TFAE type system rejects the following TFAE expression even though does
not cause a type error:

\[
\begin{array}{l}
\textsf{let}\ f=\lambda x:\textsf{num}.x\ \textsf{in} \\
\textsf{let}\ x=f\ 1\ \textsf{in} \\
f\ \textsf{true}
\end{array}
\]

The following expression is a revised version, which is well-typed because of
an additional function \(g\).

\[
\begin{array}{l}
\textsf{let}\ f=\lambda x:\textsf{num}.x\ \textsf{in} \\
\textsf{let}\ g=\lambda x:\textsf{bool}.x\ \textsf{in} \\
\textsf{let}\ x=f\ 1\ \textsf{in} \\
g\ \textsf{true}
\end{array}
\]

*Polymorphism* resolves the problem. Polymorphism is a notion of using a single
entity as multiple types. For example, it may allow \(\lambda x.x\) to be used
as multiple types. Multiple sorts of polymorphism exist. Parametric
polymorphism, *subtype polymorphism*, and *ad-hoc polymorphism* are most widely
used. This article focuses on only parametric polymorphism. The next article
will deal with subtype polymorphism. Overloading is one form of ad-hoc
polymorphism, and ad-hoc polymorphism is outside the scope of the course.

Parametric polymorphism allows an entity to be instantiated as multiple types
with parameters. Until this point, the term "parameter" has been used to
explain functions. \(x\) is the parameter of \(\lambda x.x+x\). The body,
\(x+x\), is parametrized by the parameter, \(x\). A function abstracts an
expression with an expression. Consider \(\lambda x.x+x\). The expression
\(1+1\) is the same as \((\lambda x.x+x)\ 1\); \(2+2\) is the same as
\((\lambda x.x+x)\ 2\); \((1+2)+(1+2)\) is the same as \((\lambda x.x+x)\
(1+2)\). A function abstracts an expression by replacing some portion of the
expression with the parameter of a function. By applying a function to an
expression, multiple expressions sharing the common form can be expressed in a
consistent way. Parts that are different in each expression can be expressed
by an argument.

Parametric polymorphism is a notion that applies the concept of a function to
types. A function abstracts an expression with an expression. When a function
is applied to an expression, the result is an expression. If a language
supports parametric polymorphism, there are type functions and type
applications. A type function abstracts an expression with a type. When a type
function is applied to a type, the result is an expression. Consider \(\lambda
x:{\sf num}.x\) and \(\lambda x:{\sf bool}.x\). The only difference is their
parameter type annotations. Let the type annotations be replaced with a
parameter. \(\Lambda\alpha.\lambda x:\alpha.x\) can be obtained. The
expression is a type function, which abstracts an expression with a type. (The
article uses \(\Lambda\) instead of \(\lambda\) to distinguish type functions
from "normal" functions.) A type function can be applied to a type. The
article uses \([]\) for type applications. \(e\ [\tau]\) is an expression that
applies a type function \(e\) to a type \(\tau\). Therefore, \(\lambda x:{\sf
num}.x\) is the same as \((\Lambda\alpha.\lambda x:\alpha.x)\ [{\sf num}]\).
Similarly, \(\lambda x:{\sf bool}.x\) is the same as \((\Lambda\alpha.\lambda
x:\alpha.x)\ [{\sf bool}]\). These show type applications, which are applying
type functions to types.

Via parametric polymorphism, the previous example can be a well-typed
expression without defining function more than once.

\[
\begin{array}{l}
\textsf{let}\ f=\Lambda\alpha.\lambda x:\alpha.x\ \textsf{in} \\
\textsf{let}\ x=f\ \lbrack\textsf{num}\rbrack\ 1\ \textsf{in} \\
f\ \lbrack\textsf{bool}\rbrack\ \textsf{true}
\end{array}
\]

It is more complex than the FAE expression, but defines a function only once,
not like the TFAE expression. With a type function and type applications, it
uses a single function, \(\lambda x:\alpha.x\), as a function of the \({\sf
num}\rightarrow{\sf num}\) type and a function the \({\sf bool}\rightarrow{\sf
bool}\) type at the same time. The example shows why the term "parametric
polymorphism" is used: a single entity with a type parameter can be used as
multiple types.

Traditionally, functional languages have featured parametric polymorphism. For
example, OCaml and Haskell is well-known for their type systems that support
parametric polymorphism. On the other hand, object-oriented languages have
featured only subtype polymorphism. For instance, in case of Java, versions
from JDK 1.0 to J2SE 1.4 lack parametric polymorphism. However, programmers in
these days require more features for languages as their programs become more
complicated. For this reason, Java has been supporting parametric polymorphism
since J2SE 5.0. Many recent languages, such as Scala, are designed to be
multi-paradigm languages and provide both parametric and subtype polymorphism.

## Abstract Syntax

The following is a portion of the abstract syntax of TpolyFAE:

\[
\begin{array}{rrcl}
\text{Type Identifier} & \alpha & \in & \mathit{TId} \\
\text{Expression} & e & ::= & \cdots \\
&&|& \Lambda\alpha.e \\
&&|& e\ \lbrack\tau\rbrack \\
\text{Value} & v & ::= & \cdots \\
&&|& \langle \Lambda\alpha.e,\sigma\rangle \\
\text{Type} & \tau & ::= & \cdots \\
&&|& \alpha \\
&&|& \forall\alpha.\tau \\
\end{array}
\]

Omitted parts are the same as TFAE.

Metavariable \(\alpha\) ranges over type identifiers. Sometimes, type
identifiers being used as type parameters are called type variables.

\(\Lambda\alpha.e\) is a type function. Another name of a type function is a
type abstraction. \(\alpha\) is the type parameter of the function. The scope of
\(\alpha\) is \(e\). Here, \(\alpha\) is a binding occurrence. If \(\alpha\)
appears inside \(e\), it is a bound occurrence. Previously shown
\(\Lambda\alpha.\lambda x:\alpha.x\) is one example of a type function.

\(\alpha\) can be a type. In \(\Lambda\alpha.\lambda x:\alpha.x\), the second
\(\alpha\) is the type of \(x\). It is similar to that \(t\), which is the name
of a type, can be a type in TVFAE. The syntax does not care where type
identifiers occur. However, like in TVFAE, every type identifier must be used
only inside its scope. Well-formedness rules and typing rules check whether a
type identifier exist at a valid place.

\(\langle\Lambda\alpha.e,\sigma\rangle\) is a type function value. A function
results in a closure, which is a function value; a type function results in a
type function value. The body of a function is evaluated when the function is
applied to an argument, and for the evaluation, the closure stores an
environment that is provided when the closure is constructed. Similarly, the
body of a type function is evaluated when the type function is applied to a type
argument, and for the evaluation, the type function values captures the
environment. Evaluation of \(\Lambda\alpha.e\) under \(\sigma\) results in
\(\langle\Lambda\alpha.e,\sigma\rangle\). For example, \(\Lambda\alpha.\lambda
x:\alpha.x\) results in \(\langle\Lambda\alpha.\lambda
x:\alpha.x,\emptyset\rangle\) under the empty environment.

\(\forall\alpha.\tau\) is the type of a type function. If the type of \(e\) is
\(\tau\), then the type of \(\Lambda\alpha.e\) is \(\forall\alpha.\tau\). For
instance, since the type of \(\lambda x:\alpha.x\) is
\(\alpha\rightarrow\alpha\), the type of \(\Lambda\alpha.\lambda x:\alpha.x\) is
\(\forall\alpha.\alpha\rightarrow\alpha\). Types of the form
\(\forall\alpha.\tau\) are called *universal types* or *universally quantified
types*. Universal types bind type identifiers. In \(\forall\alpha.\tau\),
\(\alpha\) is a binding occurrence, and its scope is \(\tau\).

\(e\ [\tau]\) is a type application. If \(e\) denotes a type function, \(e\
[\tau]\) evaluates the body of the type function under the environment captured
by the type function. A function application adds the value of an argument to
the environment. Environments store values, and type environments stores types.
However, type environments do not exist at run time. Thus, a type application
cannot add a type argument to the type environment. Instead, it uses
a substitution. If \(e\) results in \(\langle\Lambda\alpha.e',\sigma\rangle\),
evaluating \(e\ [\tau]\) is the same as evaluating an expression obtained by
substituting \(\alpha\) with \(\tau\) in \(e'\) under \(\sigma\). Consider the
previous example again. Since \(\Lambda\alpha.\lambda x:\alpha.x\) results in
\(\langle\Lambda\alpha.\lambda x:\alpha.x,\emptyset\rangle\), evaluating
\((\Lambda\alpha.\lambda x:\alpha.x)\ [{\sf num}]\) is the same as evaluating
\(\lambda x:{\sf num}.x\), which is obtained by substituting \(\alpha\) with
\(\sf num\) in \(\lambda x:\alpha.x\), under \(\emptyset\). The result is
\(\langle\lambda x:{\sf num}.x,\emptyset\rangle\).

A type function is an expression whose type is a universal type, while a type
application is an expression that uses a value of a universal type. If the type
of \(e\) is \(\forall\alpha.\tau'\), then the type of \(e\ [\tau]\) is a type
obtained by substituting \(\alpha\) with \(\tau\) in \(\tau'\). In the previous
example, the type of \(\Lambda\alpha.\lambda x:\alpha.x\) is
\(\forall\alpha.\alpha\rightarrow\alpha\). Therefore, the type of
\((\Lambda\alpha.\lambda x:\alpha.x)\ [{\sf num}]\) is \({\sf
num}\rightarrow{\sf num}\), which is obtained by substituting \(\alpha\) with
\(\sf num\) in \(\alpha\rightarrow\alpha\).

## Dynamic Semantics

Substitutions must be defined first. Two sorts of substitutions exist:
substitutions on types and substitutions on expressions. Consider substitutions on
types first.

\(\tau'[\alpha\leftarrow\tau]\) is a type obtained by substituting \(\alpha\)
with \(\tau\) in \(\tau'\). A substitution is a function from a type, a type
identifier, and a type to a type. Substituting \(\alpha\) with \(\tau\) means
replacing every free occurrence of \(\alpha\) with \(\tau\). Bound occurrences
of \(\alpha\) must not be replaced.

The following defines substitutions on types:

\[
\begin{array}{rcl}
\textsf{num} \lbrack\alpha\leftarrow\tau\rbrack &=& \textsf{num} \\
(\tau_1\rightarrow\tau_2) \lbrack\alpha\leftarrow\tau\rbrack &=&
(\tau_1 \lbrack\alpha\leftarrow\tau\rbrack)\rightarrow(\tau_2\lbrack\alpha\leftarrow\tau\rbrack) \\
\alpha \lbrack\alpha\leftarrow\tau\rbrack &=& \tau \\
\alpha' \lbrack\alpha\leftarrow\tau\rbrack &=& \alpha'\quad
\textsf{(if } \alpha\not=\alpha'\textsf{)} \\
(\forall\alpha.\tau') \lbrack\alpha\leftarrow\tau\rbrack &=& \forall\alpha.\tau' \\
(\forall\alpha'.\tau') \lbrack\alpha\leftarrow\tau\rbrack &=&
\forall\alpha'.(\tau'\lbrack\alpha\leftarrow\tau\rbrack) \quad
\textsf{(if } \alpha\not=\alpha'\textsf{)} \\
\end{array}
\]

Now, consider substitutions on expressions. \(e[\alpha\leftarrow\tau]\) is an
expression obtained by substituting \(\alpha\) with \(\tau\) in \(e\). In this
case, a substitution is a function from an expression, a type identifier and a
type to an expression. Only free occurrences of \(\alpha\) in \(e\) are replaced
with \(\tau\).

The following defines substitutions on expressions:

\[
\begin{array}{rcl}
n \lbrack\alpha\leftarrow\tau\rbrack &=& n \\
(e_1+e_2) \lbrack\alpha\leftarrow\tau\rbrack &=&
(e_1\lbrack\alpha\leftarrow\tau\rbrack) + (e_2\lbrack\alpha\leftarrow\tau\rbrack) \\
(e_1-e_2) \lbrack\alpha\leftarrow\tau\rbrack &=&
(e_1\lbrack\alpha\leftarrow\tau\rbrack) - (e_2\lbrack\alpha\leftarrow\tau\rbrack) \\
x \lbrack\alpha\leftarrow\tau\rbrack &=& x \\
(\lambda x:\tau'.e) \lbrack\alpha\leftarrow\tau\rbrack &=&
\lambda x:(\tau'\lbrack\alpha\leftarrow\tau\rbrack).(e\lbrack\alpha\leftarrow\tau\rbrack) \\
(e_1\ e_2) \lbrack\alpha\leftarrow\tau\rbrack &=&
(e_1\lbrack\alpha\leftarrow\tau\rbrack)\ (e_2\lbrack\alpha\leftarrow\tau\rbrack) \\
(\Lambda\alpha.e)\lbrack\alpha\leftarrow\tau\rbrack &=& \Lambda\alpha.e \\
(\Lambda\alpha'.e)\lbrack\alpha\leftarrow\tau\rbrack &=&
\Lambda\alpha'.(e\lbrack\alpha\leftarrow\tau\rbrack)\quad
\textsf{(if } \alpha\not=\alpha'\textsf{)} \\
(e\ \lbrack\tau'\rbrack)\lbrack\alpha\leftarrow\tau\rbrack &=&
(e\lbrack\alpha\leftarrow\tau\rbrack)\ \lbrack \tau'\lbrack\alpha\leftarrow\tau\rbrack\rbrack \\
\end{array}
\]

Finally, we define the dynamic semantics of TpolyFAE. Consider only rules that
are not in the type system of TFAE.

\[
\sigma\vdash \Lambda\alpha.e\Rightarrow \langle \Lambda\alpha.e,\sigma\rangle
\]

A type function results in a type function value. No computation is required.
The type function value captures the current environment.

\[
\frac
{ \sigma\vdash e\Rightarrow \langle\Lambda\alpha.e',\sigma'\rangle \quad
  \sigma'\vdash e'\lbrack\alpha\leftarrow\tau\rbrack \Rightarrow v }
{ \sigma\vdash e\ \lbrack\tau\rbrack\Rightarrow v }
\]

An expression at the type function position has to be evaluated to evaluate a
type application. The result must be a type function value. The value of the
whole expression is the same as the value of an expression obtained by
substituting the type parameter with the type argument in the body.

The following proof tree proves that the result of \((\Lambda\alpha.\lambda
x:\alpha.x)\ [{\sf num}]\ 1\) is \(1\).

\[
\frac
{
  {\Large\frac
  {
    \emptyset\vdash\Lambda\alpha.\lambda x:\alpha.x
    \Rightarrow\langle\Lambda\alpha.\lambda x:\alpha.x,\emptyset\rangle \quad
    \emptyset\vdash\lambda x:\textsf{num}.x
    \Rightarrow\langle\lambda x.x,\emptyset\rangle
  }
  { \emptyset\vdash
    (\Lambda\alpha.\lambda x:\alpha.x)\ \lbrack\textsf{num}\rbrack
    \Rightarrow\langle\lambda x.x,\emptyset\rangle
  }} \quad
  \emptyset\vdash 1\Rightarrow 1 \quad
  {\Large\frac
  { x\in\mathit{Domain}(\lbrack x\mapsto1\rbrack) }
  { \lbrack x\mapsto1\rbrack\vdash x\Rightarrow 1 }}
}
{ \emptyset\vdash
(\Lambda\alpha.\lambda x:\alpha.x)\ \lbrack\textsf{num}\rbrack\ 1
\Rightarrow 1 }
\]

## Type System

The definition of a type environment needs to be revised. A type environment in
TpolyFAE has to be able to store type identifiers in addition to the type of
variables. Type identifiers do not have further information. Only their
existence is important. The following is a new definition:

\[
\begin{array}{rrcl}
\text{Type Environment} & \Gamma & \in &
\mathit{Id}\cup\mathit{TId}\xrightarrow{\text{fin}}
\text{Type}\cup\{\cdot\} \\
\end{array}
\]

Now, the codomain of a type environment contains \(\cdot\), which is a
meaningless mathematical object. For brevity, let \(\Gamma[\alpha]\) denote
\(\Gamma[\alpha:\cdot]\).

### Well-Formed Types

Type environments stores type identifiers. Whether a type is well-formed or not
has to be determined under a type environment.

\[\Gamma\vdash\textsf{num}\]

\[
\frac
{ \Gamma\vdash\tau_1 \quad
  \Gamma\vdash\tau_2 }
{ \Gamma\vdash\tau_1\rightarrow\tau_2 }
\]

The above rules are the same as those of TVFAE.

\[
\frac
{ \alpha\in\mathit{Domain}(\Gamma) }
{ \Gamma\vdash\alpha }
\]

If \(\alpha\) is in the type environment, it is well-formed.

\[
\frac
{ \Gamma\lbrack\alpha\rbrack\vdash\tau }
{ \Gamma\vdash\forall\alpha.\tau }
\]

In \(\forall\alpha.\tau\), \(\alpha\) can appear inside \(\tau\). Therefore,
\(\alpha\) must be in the type environment when the well-formedness of \(\tau\)
is checked. \(\forall\alpha.\tau\) is well-formed under \(\Gamma\) if \(\tau\)
is well-formed under \(\Gamma[\alpha]\). For example, \(\forall\alpha.\alpha\)
is well-formed under the empty type environment, while \(\forall\alpha.\beta\)
is ill-formed under the same type environment.

### Typing Rules

First, consider TFAE typing rules that need fixes.

\[
\frac
{ \Gamma\vdash\tau \quad \Gamma\lbrack x:\tau\rbrack\vdash e:\tau' }
{ \Gamma\vdash \lambda x:\tau.e:\tau\rightarrow\tau' }
\]

Like in TVFAE, the rule for lambda abstractions has to check the well-formedness
of parameter types.

The following is a TFAE typing rule for function applications.

\[
\frac
{ \Gamma\vdash e_1:\tau'\rightarrow\tau \quad
  \Gamma\vdash e_2:\tau' }
{ \Gamma\vdash e_1\ e_2:\tau }
\]

Using the exactly same rule does not break type soundness. However, it restricts
the expressiveness of the language. Consider the following expression:

\[(\lambda x:(\forall\alpha.\alpha\rightarrow\alpha).x)\ (\Lambda\beta.\lambda x:\beta.x)\]

The type of \(\Lambda\beta.\lambda x:\beta.x\) is
\(\forall\beta.\beta\rightarrow\beta\). The function \(\lambda
x:(\forall\alpha.\alpha\rightarrow\alpha).x\) takes an argument of the
\(\forall\alpha.\alpha\rightarrow\alpha\) type. The above rule rejects the
expression. However, in fact, \(\forall\beta.\beta\rightarrow\beta\) is the same
as \(\forall\alpha.\alpha\rightarrow\alpha\) since the type parameter is
consistently renamed. Defining equivalence of types resolves the issue.

The following rules define equivalence of types:

\[
\tau\equiv\tau
\]

\[
\frac
{ \tau\equiv\tau'\lbrack\alpha'\leftarrow\alpha\rbrack }
{ \forall\alpha.\tau\equiv\forall\alpha'.\tau' }
\]

Two equal types are equivalent. Two universal types are equivalent if types
after consistent renaming of the type parameter is equivalent.

\[
\frac
{ \Gamma\vdash e_1:\tau'\rightarrow\tau \quad
  \Gamma\vdash e_2:\tau'' \quad
  \tau'\equiv\tau'' }
{ \Gamma\vdash e_1\ e_2:\tau }
\]

The above is a revised rule for function applications. It allows cases when the
parameter type and the argument type are equivalent.

Now, consider new typing rules for TpolyFAE.

\[
\frac
{ \alpha\not\in\mathit{Domain}(\Gamma) \quad 
  \Gamma\lbrack\alpha\rbrack\vdash e:\tau }
{ \Gamma\vdash \Lambda \alpha.e:\forall\alpha.\tau }
\]

The type of \(\Lambda\alpha.e\) is \(\forall\alpha.\tau\) if the type of \(e\)
is \(\tau\). \(\alpha\) is well-formed during the type checking of \(e\).
Therefore, \(e\) needs to be type-checked under the type environment with
\(\alpha\). Note that there is a premise that \(\alpha\) must not exist in the
initial type environment. If the premise disappears, the language becomes no
more type sound. Consider \(\Lambda\alpha.\lambda x:\alpha.\Lambda\alpha.x\) to
find how the type soundness can be broken.

\[
\frac
{ \Gamma\vdash\tau \quad \Gamma\vdash e:\forall\alpha.\tau' }
{ \Gamma\vdash e\ \lbrack\tau\rbrack:\tau'\lbrack\alpha\leftarrow\tau\rbrack }
\]

If the type of \(e\) is \(\forall\alpha.\tau'\), the type of \(e\ [\tau]\) is a
type obtained by substituting \(\alpha\) with \(\tau\) in \(\tau'\). Since
\(\tau\) is a type given by a programmer, well-formeness of \(\tau\) must be
checked.

The following proof tree proves the type of \((\Lambda\alpha.\lambda
x:\alpha.x)\ [{\sf num}]\ 1\) is \(\sf num\).

\[
\frac
{
  \frac
  {\huge
    \frac
    { 
      \frac
      {
        \frac
        { \alpha\in\mathit{Domain}(\lbrack\alpha\rbrack) }
        { \lbrack\alpha\rbrack\vdash\alpha } \quad
        \frac
        { x\in\mathit{Domain}(\lbrack\alpha,x:\alpha\rbrack) }
        { \lbrack\alpha,x:\alpha\rbrack\vdash x:\alpha }
      }
      { \lbrack\alpha\rbrack\vdash\lambda x:\alpha.x:\alpha\rightarrow\alpha }
    }
    { \emptyset\vdash\Lambda\alpha.\lambda x:\alpha.x:
      \forall\alpha.\alpha\rightarrow\alpha } \quad
    {\Large\emptyset\vdash\textsf{num}}
  }
  { \Large\emptyset\vdash(\Lambda\alpha.\lambda x:\alpha.x)\ \lbrack\textsf{num}\rbrack:
    \textsf{num}\rightarrow\textsf{num}
  } \quad
  \emptyset\vdash 1:\textsf{num} \quad
  \textsf{num}\equiv\textsf{num}
}
{ \emptyset\vdash
(\Lambda\alpha.\lambda x:\alpha.x)\ \lbrack\textsf{num}\rbrack\ 1:
\textsf{num} }
\]

## Implementing Type Checker

The following Scala code implements the abstract syntax of TpolyFAE:

```scala
sealed trait Expr
case class Num(n: Int) extends Expr
case class Add(l: Expr, r: Expr) extends Expr
case class Sub(l: Expr, r: Expr) extends Expr
case class Id(x: String) extends Expr
case class Fun(x: String, t: Type, b: Expr) extends Expr
case class App(f: Expr, a: Expr) extends Expr
case class TFun(a: String, b: Expr) extends Expr
case class TApp(f: Expr, t: Type) extends Expr

sealed trait Type
case object NumT extends Type
case class ArrowT(p: Type, r: Type) extends Type
case class ForallT(a: String, t: Type) extends Type
case class IdT(a: String) extends Type
```

An `Expr` instance represents a TpolyFAE expression; a `TFun` instance
represents a type function; a `TApp` instance represents a type application; a
`ForallT` instance represents a universal type; a `IdT` instance represents a
type identifier as a type. A type identifier is any string.

```scala
case class TEnv(
  vars: Map[String, Type] = Map(),
  tbinds: Set[String] = Set()
) {
  def +(p: (String, Type)): TEnv =
    copy(vars = vars + p)
  def +(x: String): TEnv =
    copy(tbinds = tbinds + x)
  def contains(x: String): Boolean = tbinds(x)
}
```

`TEnv` is the type of a type environment. The field `vars` stores the types of
variables; the filed `tbinds` stores bound type identifiers. The methods `+` and
`contains` help using type environments. For example, the following shows adding
a variable and a type to a type environment:

```scala
env + ("x" -> NumT)
env + "alpha"
```

Also, the following shows how one can check whether a type identifier is bound
to a type environment:

```scala
env.contains("alpha")
```

The following function `subst` defines substitutions on types:

```scala
def subst(t1: Type, a: String, t2: Type): Type = t1 match {
  case NumT => t1
  case ArrowT(p, r) => ArrowT(subst(p, a, t2), subst(r, a, t2))
  case IdT(a1) => if (a == a1) t2 else t1
  case ForallT(a1, t) => if (a == a1) t1 else ForallT(a1, subst(t, a, t2))
}
```

The function `mustSame` now considers equivalence of types:

```scala
def mustSame(t1: Type, t2: Type): Type = (t1, t2) match {
  case (NumT, NumT) => t1
  case (ArrowT(p1, r1), ArrowT(p2, r2)) =>
    ArrowT(mustSame(p1, p2), mustSame(r1, r2))
  case (IdT(a1), IdT(a2)) if a1 == a2 => t1
  case (ForallT(a1, t1), ForallT(a2, t2)) =>
    ForallT(a1, mustSame(t1, subst(t2, a2, IdT(a1))))
  case _ => throw new Exception
}
```

The function `validType` checks whether a given type is well-formed under a
given type environment:

```scala
def validType(t: Type, env: TEnv): Type = t match {
  case NumT => t
  case ArrowT(p, r) =>
    ArrowT(validType(p, env), validType(r, env))
  case IdT(t) =>
    if (env.contains(t)) IdT(t)
    else throw new Exception
  case ForallT(a, t) => ForallT(a, validType(t, env + a))
}
```

Now, consider the function `typeCheck`.

```scala
case TFun(a, b) =>
  if (env.contains(a)) throw new Exception
  ForallT(a, typeCheck(b, env + a))
```

\[
\frac
{ \alpha\not\in\mathit{Domain}(\Gamma) \quad 
  \Gamma\lbrack\alpha\rbrack\vdash e:\tau }
{ \Gamma\vdash \lambda \alpha.e:\forall\alpha.\tau }
\]

The type parameter of a type function must not be in the type environment. The
type of the body is computed under the type environment with the type parameter.
The resulting type is a universal type that consists of the type parameter and
the type of the body.

```scala
case TApp(f, t) =>
  validType(t, env)
  val ForallT(a, t1) = typeCheck(f, env)
  subst(t1, a, t)
```

\[
\frac
{ \Gamma\vdash\tau \quad \Gamma\vdash e:\forall\alpha.\tau' }
{ \Gamma\vdash e\ \lbrack\tau\rbrack:\tau'\lbrack\alpha\leftarrow\tau\rbrack }
\]

A type argument must be well-formed. The type of an expression at the type
function position must be a universal type. The resulting type is obtained by
substituting the type parameter of the universal type with the type argument in
the body of the universal type.

The following is the entire code of the `typeCheck` function:

```scala
def typeCheck(e: Expr, env: TEnv): Type = e match {
  case Num(n) => NumT
  case Add(l, r) =>
    mustSame(mustSame(typeCheck(l, env), NumT), typeCheck(r, env))
  case Sub(l, r) =>
    mustSame(mustSame(typeCheck(l, env), NumT), typeCheck(r, env))
  case Id(x) => env.vars(x)
  case Fun(x, t, b) =>
    validType(t, env)
    ArrowT(t, typeCheck(b, env + (x -> t)))
  case App(f, a) =>
    val ArrowT(t1, t2) = typeCheck(f, env)
    val t3 = typeCheck(a, env)
    mustSame(t1, t3)
    t2
  case TFun(a, b) =>
    if (env.contains(a)) throw new Exception
    ForallT(a, typeCheck(b, env + a))
  case TApp(f, t) =>
    validType(t, env)
    val ForallT(a, t1) = typeCheck(f, env)
    subst(t1, a, t)
}
```

The following computes the type of \((\Lambda\alpha.\lambda x:\alpha.x)\ [{\sf
num}]\ 1\) with the type checker.

```scala
// (Lambda alpha.lambda x:alpha.x) [num] 1
typeCheck(
  App(
    TApp(
      TFun("alpha",
        Fun("x", IdT("alpha"),
          Id("x")
        )
      ),
      NumT
    ),
    Num(1)
  ),
  TEnv()
)
// num
```

## Implementing Interpreter

Now, consider an interpreter for TpolyFAE.

```scala
sealed trait Value
case class NumV(n: Int) extends Value
case class CloV(p: String, b: Expr, e: Env) extends Value
case class TFunV(a: String, b: Expr, e: Env) extends Value
```

A `TFunV` instance represents a type function value.

```scala
case TFun(a, b) => TFunV(a, b, env)
```

\[
\sigma\vdash \Lambda\alpha.e\Rightarrow \langle \Lambda\alpha.e,\sigma\rangle
\]

A type function results in a type function value that captures the current
environment.

```scala
case TApp(f, t) =>
  val TFunV(a, b, fEnv) = interp(f, env)
  interp(subst(b, a, t), fEnv)
```

\[
\frac
{ \sigma\vdash e\Rightarrow \langle\Lambda\alpha.e',\sigma'\rangle \quad
  \sigma'\vdash e'\lbrack\alpha\leftarrow\tau\rbrack \Rightarrow v }
{ \sigma\vdash e\ \lbrack\tau\rbrack\Rightarrow v }
\]

For evaluation of a type application, the type function expression is evaluated
first. The result must be a type function value. Obtain an expression by
substituting the type parameter of the type function value with the type
argument in the body of the type function value. Evaluate the expression under
the environment captured by the type function value to acquire the final result.

The following is the entire of the `interp` and `run` functions:

```scala
def subst(e: Expr, a: String, t: Type): Expr = e match {
  case Num(n) => Num(n)
  case Add(l, r) => Add(subst(l, a, t), subst(r, a, t))
  case Sub(l, r) => Sub(subst(l, a, t), subst(r, a, t))
  case Id(x) => Id(x)
  case Fun(x, t0, b) => Fun(x, subst(t0, a, t), subst(b, a, t))
  case App(f, arg) => App(subst(f, a, t), subst(arg, a, t))
  case TFun(a0, b) => if (a0 == a) TFun(a0, b) else TFun(a0, subst(b, a, t))
  case TApp(f, t0) => TApp(subst(f, a, t), subst(t0, a, t))
}

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
  case Fun(x, t, b) => CloV(x, b, env)
  case App(f, a) =>
    val CloV(x, b, fEnv) = interp(f, env)
    interp(b, fEnv + (x -> interp(a, env)))
  case TFun(a, b) => TFunV(a, b, env)
  case TApp(f, t) =>
    val TFunV(a, b, fEnv) = interp(f, env)
    interp(subst(b, a, t), fEnv)
}

def run(e: Expr): Value = {
  typeCheck(e, TEnv())
  interp(e, Map.empty)
}
```

The following computes the result of \((\Lambda\alpha.\lambda x:\alpha.x)\
[{\sf num}]\ 1\) with the interpreter.

```scala
// (Lambda alpha.lambda x:alpha.x) [num] 1
run(
  App(
    TApp(
      TFun("alpha",
        Fun("x", IdT("alpha"),
          Id("x")
        )
      ),
      NumT
    ),
    Num(1)
  )
)
// 1
```

## Acknowledgments

I thank professor Ryu for giving feedback on the article.
I thank 'pi' for pointing out incorrect things in the article.
