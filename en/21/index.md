This article defines TVFAE by adding algebraic data types to TFAE. It defines
the abstract syntax, the dynamic semantics, and the type system of TVFAE and
implements a type checker and an interpreter for TVFAE.

## Algebraic Data Types

You have already seen necessity and use of algebraic data types in the 'Pattern
Matching' article. This article considers encoding algebraic data types with
TFAE rather than explaining algebraic data types themselves.

Suppose that a fruit is either an apple or banana. The shape of an apple can be
approximated as a sphere, and the radius of the sphere determines the size of
the apple. On the other hand, the shape of a banana can be approximated as a
cylinder, and the radius and the height of the cylinder determines the size of
the banana. It can be written in Scala as the following:

```scala
sealed trait Fruit
case class Apple(r: Int) extends Fruit
case class Banana(h: Int, r: Int) extends Fruit
```

It shows a typical usage of an algebraic data type in Scala.

TFAE can encode algebraic data types with pairs. Although the original TFAE
lacks pairs, I will assume that TFAE has pairs since a way to add pairs to TFAE
has been covered already. In addition, assume that local variable declarations,
Boolean values, and conditional expressions exist as well.

The type of a fruit can be \({\sf bool}\times({\sf num}\times({\sf
num}\times{\sf num}))\). A fruit value is a pair. The first value of the pair
indicates whether the fruits is an apple or a banana. If the value is \({\sf
true}\), then it is an apple. Otherwise, it is a banana. The second value of the
pair is another pair, which represents the size of the fruit. If the fruit is an
apple, only the first value of the second pair is meaningful. Otherwise, only
the second value of the pair is meaningful. The following expressions
respectively represent an apple whose radius is \(5\) and a banana whose height
is \(6\) and radius is \(2\).

\[(\textsf{true},(5,(0,0)))\]

\[(\textsf{false},(0,(6,2)))\]

It is tedious and error-prone to make fruit values like the above, while Scala
allows the following:

```scala
Apple(5)
Banana(6, 2)
```

Defining functions named \(Apple\) and \(Banana\) allows similar code. \(Apple\)
takes an integer as an argument. Its result is a pair: the first value is \({\sf
true}\); the second value is a pair whose first value is the integer passed as
an argument and second value is a pair of two arbitrary integers. The following
is such a function:

\[\lambda x:\textsf{num}.(\textsf{true},(x,(0,0)))\]

\(Banana\) can be defined in a similar manner:

\[\lambda x:\textsf{num}\times\textsf{num}.(\textsf{false},(0,x))\]

You can now create fruit values with \(Apple\) and \(Banana\):

\[Apple\ 5\]

\[Banana\ (6,2)\]

Consider a function `radius`, which compute the radius of a given fruit. In
Scala, pattern matching can be used:

```scala
def radius(f: Fruit): Int = f match {
  case Apple(r) => r
  case Banana(_, r) => r
}
```

In TFAE, the first value of a given pair indicates whether the fruit is an apple
or banana. According to the sort of the fruit, an expression computing the
radius changes. The following defines the \(radius\) function:

\[\lambda f:\textsf{bool}\times(\textsf{num}\times(\textsf{num}\times\textsf{num})).
\textsf{if}\ f.1\ f.2.1\ f.2.2.2\]

Below collects all the previous code snippets. The Scala version comes first,
and then the TFAE version comes.

```scala
sealed trait Fruit
case class Apple(r: Int) extends Fruit
case class Banana(h: Int, r: Int) extends Fruit

def radius(f: Fruit): Int = f match {
  case Apple(r) => r
  case Banana(_, r) => r
}

radius(Apple(5)) + radius(Banana(6, 2))  // 7
```

\[
\begin{array}{l}
\textsf{val}\ Apple=\lambda x:\textsf{num}.(\textsf{true},(x,(0,0)))\ \textsf{in} \\
\textsf{val}\ Banana=\lambda x:\textsf{num}\times\textsf{num}.(\textsf{false},(0,x))\ \textsf{in} \\
\textsf{val}\ radius=\lambda f:\textsf{bool}\times(\textsf{num}\times(\textsf{num}\times\textsf{num})).
\textsf{if}\ f.1\ f.2.1\ f.2.2.2\ \textsf{in} \\
(radius\ (Apple\ 5))+(radius\ (Banana\ (6,2)))
\end{array}
\]

Basic concepts in TFAE can mimic algebraic data types. Adding direct support of
algebraic data types to TFAE does seem unnecessary.

However, there are some inconvenience. \((0,0)\), which is a pair for the size
of a banana, has to be constructed even when an apple value is defined.
Similarly, a banana value requires a fake size of an apple. They adds
unessential complexity and computation to code. On the other hand, code written
in Scala is much more readable due to the direct support of algebraic data types
in Scala.

A program using multiple algebraic data types introduces another problem. In
practice, it is quite common the use multiple algebraic data types in a single
program. The type of a fruit is \({\sf bool}\times({\sf num}\times({\sf
num}\times{\sf num}))\). The same type may represent other values as well. For
example, the type of an electronic product can also be \({\sf bool}\times({\sf
num}\times({\sf num}\times{\sf num}))\). In this case, the type system allows a
function intended to take a fruit to take, nevertheless, an electronic product
as an argument. It does not cause a type error at run time but will show a
behavior that the programmers have not expected. On the other hand, `Fruit` and
`Electronics` are different types in Scala, and type checking thus desirably
fails. The direct support of algebraic data types allows it.

Furthermore, algebraic data types in most languages can be recursive. The
following implements lists of integers in Scala:

```scala
sealed trait List
case class Nil(u: Unit) extends List
case class Cons(h: Int, t: List) extends List
```

The class `Cons` defines the type `List` and uses the type `List` at the same
time. It is possible only when the type `List` is a recursive type.

Is it possible to encode lists, just like fruits, in TFAE? You need to determine
the type of a list at first. Consider fruits again. Since an apple value
contains a single integer and a banana value contains a pair of two integer,
\({\sf bool}\times({\sf num}\times({\sf num}\times{\sf num}))\) is the type of
every fruit. Now, let the type of a list be \(\tau\). `Nil` has a value of the
type `Unit`; `Cons` has a pair of an integer and a list. Therefore, the type of
a list is \({\sf bool}\times({\sf unit}\times({\sf num}\times\tau))\). It
implies that \(\tau\) has to be equal to \({\sf bool}\times({\sf
unit}\times({\sf num}\times\tau))\). However, \({\sf bool}\times({\sf
unit}\times({\sf num}\times\tau))\) already contains \(\tau\). It never can be
the same as \(\tau\). Alas, there is no \(\tau\) satisfying the condition. In
conclusion, TFAE cannot represent the type of a list. The same conclusion
applies to any other recursive algebraic data types. Without direct support of
algebraic types, use of recursive types, such as lists, is impossible.

## Syntax

The following is the abstract syntax of TVFAE that differs from that of TFAE.

\[
\begin{array}{rrcl}
\text{Type Identifier} & t & \in & \mathit{TId} \\
\text{Expression} & e & ::= & \cdots \\
&&|& \textsf{type}\ t=x@\tau+x@\tau\ \textsf{in}\ e \\
&&|& e\ \textsf{match}\ x(x)\rightarrow e\ |\ x(x)\rightarrow e \\
\text{Value} & v & ::= & \cdots \\
&&|& x(v) \\
&&|& \langle x\rangle \\
\text{Type} & \tau & ::= & \cdots \\
&&|& t \\
\end{array}
\]

\(t\) is a metavariable ranges over type identifiers, which is the names of
types defined by programmers.

An expression \({\sf type}\ t=x_1@\tau_1+x_2@\tau_2\ {\sf in}\ e\) defines a new
algebraic data type. \(t\) is the name of the type. A single algebraic data type
can have two variants in TVFAE. The restriction exists to make the language
simple. Most real-world languages allow programmers to define any number of
variants for a single type. It is straightforward to change the language to
allow more than two variants. \(x_1\) and \(x_2\) are the names of the variants.
They must differ from each other. \(\tau_1\) is the type of value contained in a
value of the first variant. Similarly, \(\tau_2\) is the type for the second
variant. In some languages like Scala, a value of a variant can contain multiple
values. Previously shown \(Banana\) is one such example. In fact, multiple
values can form a single value by making a tuple (or a nested pair). For this
reason, there are no fundamental difference between supporting multiple values
and supporting only a single value. For simplicity, TVFAE restricts each value
of a variant to carry only one value. The scopes of the variants \(x_1\) and
\(x_2\) are both the same as \(e\). On the other hand, the type \(t\) can be
used not only in \(e\) but also in the types used to define the variants. The
following expressions define fruits and list:

\[\textsf{type}\ Fruit=Apple@\textsf{num}+Banana@(\textsf{num}\times\textsf{num})\ \textsf{in}\ \cdots\]

\[\textsf{type}\ List=Nil@\textsf{unit}+Cons@(\textsf{num}\times List)\ \textsf{in}\ \cdots\]

A value \(x(v)\) is a value of variant named \(x\). \(v\) is a valued contained
in \(x(v)\). For example, \(Apple(5)\) denotes an apple whose radius is \(5\).
\(Banana((6,2))\) is a banana whose height is \(6\) and radius is \(2\).

\(t\) as a type is the type of a value of an algebraic data type whose name is
\(t\). In the previous example, since both \(Apple\) and \(Banana\) are variants
of \(Fruit\), the types of \(Apple(5)\) and \(Banana((6,2))\) are \(Fruit\).

A value \(\langle x\rangle\) is the constructor of a variant named \(x\). Just
like a function, a constructor can be applied to a value. If the value of
\(e_1\) is \(\langle x\rangle\) and the value of \(e_2\) is \(v\), then the
value of \(e_1\ e_2\) is \(x(v)\). When the variable \(Apple\) denotes \(\langle
Apple\rangle\), the result of \(Apple\ 5\) is \(Apple(5)\). Programmers want to
write expressions like \(Apple\ 5\) to construct values of variants. Therefore,
after a type is defined, the environment has to contain the fact that the value
of a variable whose name is equal to the name of a variant of the type is the
constructor of the variant. For example, the environment must know that the
value of the variable \(Apple\) is \(\langle Apple\rangle\). The type of a
constructor is a function type from the type of a value contained by a value of
the variant to the type of an algebraic data type whom the variant belongs to.
For example, the type of \(\langle Apple\rangle\) is \({\sf num}\rightarrow
Fruit\). Since \(\langle Apple\rangle\) can be used as a function and takes an
integer \(n\) to produce \(Apple(n)\), whose type is \(Fruit\), the type is
correct.

An expression \(e\ {\sf match}\ x_1(x_3)\rightarrow e_1\ |\ x_2(x_4)\rightarrow
e_2\) uses pattern matching on \(e\). The result of \(e\) is the target of the
pattern matching. Let \(v\) be the result of \(e\). If \(v\) is a value of a
variant named \(x_1\), \(e_1\) is evaluated. Suppose that \(v\) equals
\(x_1(v_1)\). When \(e_1\) is evaluated, a value denoted by \(x_3\) is \(v_1\).
The result of the whole expression is the same as the result of \(x_1\).
Similarly, if \(v\) is a value of a variant named \(x_2\), \(e_2\) is evaluated.
A value denoted by \(x_4\) is \(v_2\) during the evaluation of \(e_2\), provided
that \(v\) equals \(x_2(v_2)\). In this case, the result of the whole expression
is that of \(e_2\). Now, the previously shown \(radius\) function can be defined
as the following:

\[\lambda f:Fruit.f\ \textsf{match}\ Apple(x)\rightarrow x\ |\ Banana(x)\rightarrow x.2\]

The order between variants can change. The following also is correct:

\[\lambda f:Fruit.f\ \textsf{match}\ Banana(x)\rightarrow x.2\ |\ Apple(x)\rightarrow x\]

## Dynamic Semantics

Rules common to TFAE are omitted.

\[
\frac
{ \sigma[x_1\mapsto \langle x_1\rangle,x_2\mapsto \langle x_2\rangle]\vdash e\Rightarrow v }
{ \sigma\vdash \textsf{type}\ t=x_1@\tau_1+x_2@\tau_2\ \textsf{in}\ e\Rightarrow v }
\]

The result of \({\sf type}\ t=x_1@\tau_1+x_2@\tau_2\ {\sf in}\ e\) equals the
result of \(e\). The constructors of the variants have to be available during
the evaluation of \(e\). For this reason, the environment used for the
evaluation of \(e\) contains that \(x_1\) denotes \(\langle x_1\rangle\) and
\(x_2\) denotes \(\langle x_2\rangle\).

\[
\frac
{ \sigma\vdash e_1\Rightarrow \langle x\rangle \quad
  \sigma\vdash e_2\Rightarrow v }
{ \sigma\vdash e_1\ e_2\Rightarrow x(v) }
\]

TFAE allows only closures to occur at the function position of function
applications. However, TVFAE additionally allows constructors, and a rule for
such cases are thus necessary. If \(e_1\) results in \(\langle x_1\rangle\) and
\(e_2\) results in \(v\), then \(e_1\ e_2\) results in \(x(v)\). The constructor
of the variant \(x\) makes values of the variant.

\[
\frac
{ \sigma\vdash e\Rightarrow x_1(v') \quad
  \sigma[x_3\mapsto v']\vdash e_1\Rightarrow v }
{ \sigma\vdash e\ \textsf{match}\ x_1(x_3)\rightarrow e_1\ |\ x_2(x_4)\rightarrow e_2\Rightarrow v }
\]

During pattern matching, the target has to be computed first. Therefore, \(e\)
is the first expression to be evaluated in \(e\ {\sf match}\ x_1(x_3)\rightarrow
e_1\ |\ x_2(x_4)\rightarrow e_2\). If the result of \(e\) is \(x_1(v')\), then
\(e_1\) is the next expression to be evaluated. During the evaluation of
\(e_1\), the value of \(x_3\) is \(v'\), and the environment therefore has to
know that. The result of \(e_1\) is the result of the whole expression.

\[
\frac
{ \sigma\vdash e\Rightarrow x_2(v') \quad
  \sigma[x_4\mapsto v']\vdash e_2\Rightarrow v }
{ \sigma\vdash e\ \textsf{match}\ x_1(x_3)\rightarrow e_1\ |\ x_2(x_4)\rightarrow e_2\Rightarrow v }
\]

On the other hand, if \(e\) results in \(x_2(v')\), \(e_2\) is evaluated. In
this case, the environment contains a mapping from \(x_4\) to \(v'\).

The following proof tree proves that \(\textsf{type}\ Fruit=Apple@\textsf{num}+Banana@(\textsf{num}\times\textsf{num})\ \textsf{in}\ (Apple\ 5)\ \textsf{match}\ Apple(x)\rightarrow x\ |\ Banana(x)\rightarrow x.2\) results in \(5\).

\[
\begin{array}{rcl}
\sigma_1&=&\lbrack Apple\mapsto\langle Apple\rangle,Banana\mapsto\langle Banana\rangle\rbrack \\
\sigma_2&=&\sigma_1\lbrack x\mapsto 5\rbrack
\end{array}
\]

\[
\frac
{ \Large
  \frac
  { \huge
    \frac
    {
      \frac
      { Apple\in\mathit{Domain}(\sigma_1) }
      { \sigma_1\vdash Apple\Rightarrow\langle Apple\rangle } \quad
      \sigma_1\vdash 5\Rightarrow 5
    }
    { \sigma_1\vdash(Apple\ 5)\Rightarrow Apple(5) } \quad
    \frac
    { x\in\mathit{Domain}(\sigma_2) }
    { \sigma_2\vdash x\Rightarrow 5 }
  }
  { \sigma_1\vdash(Apple\ 5)\ \textsf{match}\ Apple(x)\rightarrow x\ |\ Banana(x)\rightarrow x.2 
    \Rightarrow 5 }
}
{ \emptyset\vdash
  \textsf{type}\ Fruit=Apple@\textsf{num}+Banana@(\textsf{num}\times\textsf{num})
  \ \textsf{in}\ (Apple\ 5)\ \textsf{match}\ Apple(x)\rightarrow x\ |\ Banana(x)\rightarrow x.2
  \Rightarrow 5 }
\]

## Type System

The definition of a type environment needs to be revised to define the type
system of TVFAE. In TFAE, type environments store the types of variables. They
are partial functions from identifiers to types. In TVFAE, type environments
have more things to do. They have to store information about algebraic data
types. The type checking process uses the information. Let \(\Gamma\) be the type
environment when \({\sf type}\ t=x_1@\tau_1+x_2@\tau_2\ {\sf in}\ e\) is
type-checked. Adding the information of \(t\) to \(\Gamma\) yields
\(\Gamma[t=\{(x_1,\tau_1),(x_2,\tau_2)\}]\). The variants are elements of a set
since the order between them is unimportant. From now on,
\(x_1@\tau_1+x_2@\tau_2\) is a notation for \(\{(x_1,\tau_1),(x_2,\tau_2)\}\).
\(@\) and \(+\) do not have special meaning. They are just parts of the
notation. The domain of a type environment now needs to include \(t\), which is
a type identifier. Also, the codomain has to contain \(x_1@\tau_1+x_2@\tau_2\).
Below is the revised definition. Note that \(\mathcal{P}(A)\) denotes the power set
of \(A\).

\[
\begin{array}{rrcl}
\text{Type Environment} & \Gamma & \in &
\mathit{Id}\cup\mathit{TId}\xrightarrow{\text{fin}}
\text{Type}\cup\mathcal{P}(\mathit{Id}\times\text{Type}) \\
\end{array}
\]

### Well-Formed Types

An arbitrary type identifier can be a type in TVFAE. For example, \(Fruit\) is a
type. It is true regardless of whether the type \(Fruit\) has been defined
already. Even though \(Fruit\) never has been defined before, one may make a
function \(\lambda x:Fruit.x\). It does not makes sense to allow a function
whose type is \(Fruit\rightarrow Fruit\) where \(Fruit\) is an unknown type.
Such expressions are weird and can, furthermore, break the type soundness of the
type system.

When a certain type appears in an expression, the *well-formedness* of the type
must be passed. The type system has to reject an expression containing an
*ill-formed* type. Types that do not need to be defined in an expression are
always well-formed. \({\sf num}\) is such a type. Types that need to be defined
can also be well-formed type. For example, if \(Fruit\) has been defined, it is
well-formed. In addition, types consisting of well-formed types are well-formed
as well. For instance, \({\sf num}\rightarrow{\sf num}\) is well-formed. Any
other types are ill-formed: \(Fruit\) is ill-formed if it has not been defined;
\(Fruit\rightarrow Fruit\) is ill-formed if \(Fruit\) is so.

It is time to define well-formed types formally. The well-formed relation is a
relation over type environments and types.

\[\vdash\in\text{Type Environment}\times\text{Type}\]

The type environment is required since it contains the list of defined types.
\(\Gamma\vdash \tau\) denotes that \(\tau\) is well-formed under \(\Gamma\).

\[\Gamma\vdash\textsf{num}\]

\(\sf num\) is always well-formed.

\[
\frac
{ \Gamma\vdash\tau_1 \quad
  \Gamma\vdash\tau_2 }
{ \Gamma\vdash\tau_1\rightarrow\tau_2 }
\]

If both \(\tau_1\) and \(\tau_2\) is well-formed, then
\(\tau_1\rightarrow\tau_2\) also is well-formed.

\[
\frac
{ t\in\mathit{Domain}(\Gamma) }
{ \Gamma\vdash t }
\]

If \(t\) has been defined, i.e. the domain of \(\Gamma\) includes \(t\), then
\(t\) is well-formed.

If \(\Gamma\vdash\tau\) is not provable, then \(\tau\) is ill-formed under
\(\Gamma\).

## Typing Rules

Now, typing rules of TVFAE can be defined. Typing rules are rules defining the
types of expressions among inference rules consisting a type system.

\[
\frac
{ \begin{array}{c}
  t\not\in\mathit{Domain}(\Gamma) \\
  \Gamma'=\Gamma[t=x_1@\tau_1+x_2@\tau_2,x_1:\tau_1\rightarrow t,x_2:\tau_2\rightarrow t] \\
  \Gamma'\vdash e:\tau \quad
  \Gamma'\vdash \tau_1 \quad
  \Gamma'\vdash \tau_2 \quad
  \Gamma\vdash \tau\end{array} }
{ \Gamma\vdash \textsf{type}\ t=x_1@\tau_1+x_2@\tau_2\ \textsf{in}\ e:\tau }
\]

The above rule defines the type of an expression \({\sf type}\
t=x_1@\tau_1+x_2@\tau_2\ {\sf in}\ e\). The type \(t\) must have a different
name from existing types. Dropping this premise from the rule makes the type
system unsound. Before the type checking of \(e\), the definition of \(t\) must
be added to the type environment. In addition, since \(e\) can use the
constructors, the type environment also contains that the types of \(x_1\) and
\(x_2\) respectively are \(\tau_1\rightarrow t\) and \(\tau_2\rightarrow t\).
\(\Gamma'\) denotes the type environment after the addition. Well-formedness of
\(\tau_1\) and \(\tau_2\) has to be check. It is essential to retain the type
soundness. Note that the well-formedness conditions use \(\Gamma'\), not
\(\Gamma\). Use of \(\Gamma\) prevents recursively defined types, while
\(\Gamma'\) does not. Let the type of \(e\) under \(\Gamma'\) be \(\tau\). It is
guaranteed that \(\tau\) is well-formed under \(\Gamma'\) since it is the result
of type checking. However, \(\tau\) is required to be well-formed also under
\(\Gamma\). It prevents \(t\) escaping its scope. Without the condition, the
type system is unsound. If all the premises are satisfied, the type of the whole
expression is \(\tau\).

Most premises check well-formedness to maintain the type soundness. Omitting
them gives an insight to the rule:

\[
\frac
{
  \Gamma'=\Gamma[t=x_1@\tau_1+x_2@\tau_2,x_1:\tau_1\rightarrow t,x_2:\tau_2\rightarrow t] \quad
  \Gamma'\vdash e:\tau }
{ \Gamma\vdash \textsf{type}\ t=x_1@\tau_1+x_2@\tau_2\ \textsf{in}\ e:\tau }
\]

Important premises are adding the definition and the constructors and computing
the type of the body.

\[
\frac
{ \begin{array}{c}\Gamma\vdash e:t \quad
  t\in\mathit{Domain}(\Gamma) \quad
  \Gamma(t)=x_1@\tau_1+x_2@\tau_2 \\
  \Gamma[x_3:\tau_1]\vdash e_1:\tau \quad
  \Gamma[x_4:\tau_2]\vdash e_2:\tau\end{array} }
{ \Gamma\vdash e\ \textsf{match}\ x_1(x_3)\rightarrow e_1\ |\ x_2(x_4)\rightarrow e_2:\tau }
\]

The above rule defined the type of \(e\ {\sf match}\ x_1(x_3)\rightarrow e_1\ |\
x_2(x_4)\rightarrow e_2\). First, the type of \(e\), which is the target, is
computed. The type must be \(t\), which is a type identifier, and it must have
been defined. In this case, the type environment has the definition of \(t\).
The names of the variants have to equal \(x_1\) and \(x_2\) in the expression.
The order need not to be the same: when the type was defined, \(x_2\) may
precede \(x_1\). Since a set represents the definition, the order is not
recorded. Finally, the types of \(e_1\) and \(e_2\) are computed. The type of
\(x_3\) is \(\tau_1\) during the type checking of \(e_1\); the type of \(x_4\)
is \(\tau_2\) during the type checking of \(e_2\). The types must coincide. The
common type is the type of the whole expression.

One rule from TFAE needs a revision. The rule for lambda abstractions is it.

\[
\frac
{ \Gamma\vdash\tau \quad \Gamma\lbrack x:\tau\rbrack\vdash e:\tau' }
{ \Gamma\vdash \lambda x:\tau.e:\tau\rightarrow\tau' }
\]

The parameter type annotation has to be well-typed. It is necessary for the type
soundness.

One may wonder which types require well-formedness checks. In every language
treated in the articles, only type annotations provided by programmers need
checks. However, in some other languages, situations can be more complex.
Fortunately, such languages are beyond the scope of the articles.

The following proof tree proves that the type of \(\textsf{type}\ Fruit=Apple@\textsf{num}+Banana@(\textsf{num}\times\textsf{num})\ \textsf{in}\ (Apple\ 5)\ \textsf{match}\ Apple(x)\rightarrow x\ |\ Banana(x)\rightarrow x.2\) is \(\sf num\).

\[
\begin{array}{rcl}
\Gamma_1&=&\lbrack 
Fruit=Apple@\textsf{num}+Banana@(\textsf{num}\times\textsf{num}),
Apple:\textsf{num}\rightarrow Fruit,
Banana:(\textsf{num}\times\textsf{num})\rightarrow Fruit\rbrack \\
\Gamma_2&=&\Gamma_1\lbrack x:\textsf{num}\rbrack \\
\Gamma_3&=&\Gamma_1\lbrack x:\textsf{num}\times\textsf{num}\rbrack
\end{array}
\]

\[
\frac
{ \begin{array}{c}
  Fruit\not\in\mathit{Domain}(\emptyset) \quad
  \Gamma_1=\Gamma_1 \\
  \Gamma_1\vdash \textsf{num} \quad
  \frac
  { \Gamma_1\vdash \textsf{num} \quad \Gamma_1\vdash \textsf{num}}
  { \Gamma_1\vdash \textsf{num}\times\textsf{num} } \quad
  \emptyset\vdash \textsf{num}
  \end{array} \quad
  {\large\frac
  {
    \begin{array}{c}
    {\Large\frac
    { 
      \frac
      { Apple\in\mathit{Domain}(\Gamma_1) }
      { \Gamma_1\vdash Apple:\textsf{num}\rightarrow Fruit } \quad
      \Gamma_1\vdash 5:\textsf{num}
    }
    { \Gamma_1\vdash (Apple\ 5):Fruit } \quad
    \frac
    { x\in\mathit{Domain}(\Gamma_2) }
    { \Gamma_2\vdash x:\textsf{num} } \quad
    \frac
    { 
      \frac
      { x\in\mathit{Domain}(\Gamma_3) }
      { \Gamma_3\vdash x:\textsf{num}\times\textsf{num} }
    }
    { \Gamma_3\vdash x.2:\textsf{num} } }\\
    Fruit\in\mathit{Domain}(\Gamma_1) \quad
    \Gamma_1(Fruit)=Apple@\textsf{num}+Banana@(\textsf{num}\times\textsf{num})
    \end{array}
  }
  { \Gamma_1\vdash (Apple\ 5)\ \textsf{match}\ Apple(x)\rightarrow x\ |\ Banana(x)\rightarrow x.2
    :\textsf{num} }}
}
{ \emptyset\vdash
  \textsf{type}\ Fruit=Apple@\textsf{num}+Banana@(\textsf{num}\times\textsf{num})
  \ \textsf{in}\ (Apple\ 5)\ \textsf{match}\ Apple(x)\rightarrow x\ |\ Banana(x)\rightarrow x.2
  :\textsf{num} }
\]

## Implementing Type Checker

The following Scala code implements the abstract syntax of TVFAE:

```scala
sealed trait Expr
case class Num(n: Int) extends Expr
case class Add(l: Expr, r: Expr) extends Expr
case class Sub(l: Expr, r: Expr) extends Expr
case class Id(x: String) extends Expr
case class Fun(x: String, t: Type, b: Expr) extends Expr
case class App(f: Expr, a: Expr) extends Expr
case class TypeDef(
  t: String, v1: String, vt1: Type,
  v2: String, vt2: Type, b: Expr
) extends Expr
case class Match(
  e: Expr, v1: String, x1: String, e1: Expr,
  v2: String, x2: String, e2: Expr
) extends Expr

sealed trait Type
case object NumT extends Type
case class ArrowT(p: Type, r: Type) extends Type
case class IdT(t: String) extends Type

def mustSame(t1: Type, t2: Type): Type =
  if (t1 == t2) t1 else throw new Exception
```

An `Expr` instance represent a TVFAE expression; a `TypeDef` instance represents
an expression defining an algebraic data type; a `Match` instance represents a
pattern matching expression; an `IdT` instance represents a type identifier as a
type. A type identifier is a string.

```scala
case class TEnv(
  vars: Map[String, Type] = Map(),
  tbinds: Map[String, Map[String, Type]] = Map()
) {
  def +(x: String, t: Type): TEnv =
    TEnv(vars + (x -> t), tbinds)
  def +(x: String, m: Map[String, Type]): TEnv =
    TEnv(vars, tbinds + (x -> m))
  def contains(x: String): Boolean =
    tbinds.contains(x)
}
```

`TEnv` is the type of a type environment. Type environments in TFAE can be
represented by maps whose keys are strings and values are types in TFAE.
However, type environments in TVFAE have to store the definitions of types in
addition. `TEnv` is now defined as a case class. It has two fields: `vars` and
`tbinds`. The field `vars`, which is a map from strings to TVFAE types, contains
the types of variables; the field `tbinds`, which is a map from strings to maps,
contains the definitions of types. Maps used as values of the map use strings as
keys and types as values. The strings are the names of variants; the types are
the types of values contained in values of the variants. For example, `tbinds`
containing the \(Fruit\) type is the following:

```scala
Map("Fruit" -> Map("Apple" -> NumT, "Banana" -> PairT(NumT, NumT)))
```

For the ease of adding variables and types to type environments, the `TEnv`
class has two methods named `+`. Adding that the type of a variable \(x\) is
\(\sf num\) to `env` can be written as the following:

```scala
env + ("x", NumT)
```

Adding the \(Fruit\) type to `env` can be written as the following:

```scala
env + ("Fruit", Map("Apple" -> NumT, "Banana" -> PairT(NumT, NumT)))
```

The `contains` method of the `TEnv` class checks whether a particular type
identifier is the name of a type that has been already defined. For instance,
the following code checks whether \(Fruit\) has been defined:

```scala
env.contains("Fruit")
```

A function checking the well-formedness of a type has to be defined first. Let
`validType` be the name of the function. It takes a type environment and a type
as arguments. If the type is well-formed under the type environment, then the
function returns the type. Otherwise, it throws an exception.

```scala
def validType(t: Type, env: TEnv): Type = t match {
  case NumT => t
  case ArrowT(p, r) =>
    ArrowT(validType(p, env), validType(r, env))
  case IdT(t) =>
    if (env.contains(t)) IdT(t)
    else throw new Exception
}
```

Now, it is time to revise the `typeCheck` function.

```scala
 case TypeDef(t, v1, vt1, v2, vt2, b) =>
  if (env.contains(t)) throw new Exception
  val nenv = env +
    (t, Map(v1 -> vt1, v2 -> vt2)) +
    (v1, ArrowT(vt1, IdT(t))) +
    (v2, ArrowT(vt2, IdT(t)))
  validType(vt1, nenv)
  validType(vt2, nenv)
  validType(typeCheck(b, nenv), env)
```

\[
\frac
{ \begin{array}{c}
  t\not\in\mathit{Domain}(\Gamma) \\
  \Gamma'=\Gamma[t=x_1@\tau_1+x_2@\tau_2,x_1:\tau_1\rightarrow t,x_2:\tau_2\rightarrow t] \\
  \Gamma'\vdash e:\tau \quad
  \Gamma'\vdash \tau_1 \quad
  \Gamma'\vdash \tau_2 \quad
  \Gamma\vdash \tau\end{array} }
{ \Gamma\vdash \textsf{type}\ t=x_1@\tau_1+x_2@\tau_2\ \textsf{in}\ e:\tau }
\]

First, the function has to check whether the name of the type already exists in
the type environment. If it exists, then the function rejects the expression.
Otherwise, it adds the type and the constructors to the type environment and
checks the well-formedness of the types of the variants under the new type
environment. Finally, it computes the type of the body expression and check the
well-formedness of the result type under the original type environment. If the
type is well-formed, then it is the type of the whole expression.

```scala
case Match(e, v1, x1, e1, v2, x2, e2) =>
  val IdT(t) = typeCheck(e, env)
  val tdef = env.tbinds(t)
  mustSame(
    typeCheck(e1, env + (x1, tdef(v1))),
    typeCheck(e2, env + (x2, tdef(v2)))
  )
```

\[
\frac
{ \begin{array}{c}\Gamma\vdash e:t \quad
  t\in\mathit{Domain}(\Gamma) \quad
  \Gamma(t)=x_1@\tau_1+x_2@\tau_2 \\
  \Gamma[x_3:\tau_1]\vdash e_1:\tau \quad
  \Gamma[x_4:\tau_2]\vdash e_2:\tau\end{array} }
{ \Gamma\vdash e\ \textsf{match}\ x_1(x_3)\rightarrow e_1\ |\ x_2(x_4)\rightarrow e_2:\tau }
\]

First, the function computes the type of the target expression. The type must be
a type identifier. The definition of the type can be found in the type
environment. The definition gives the type of each variant of the type. The
function computes the types of `e1` and `e2` under the respectively extended
type environments. The types must be the same, and if it is the case, the common
type is the type of the whole expression.

```scala
case Fun(x, t, b) =>
  validType(t, env)
  ArrowT(t, typeCheck(b, env + (x, t)))
```

\[
\frac
{ \Gamma\vdash\tau \quad \Gamma\lbrack x:\tau\rbrack\vdash \tau' }
{ \Gamma\vdash \lambda x:\tau.e:\tau\rightarrow\tau' }
\]

As previously mentioned, the `Fun` case needs a revision, though it is not a new
case. The well-formedness of the parameter type annotation needs to be checked.

```scala
case Id(x) => env.vars(x)
```

The `Id` case also has a small change: a way to find the type of a variable has
been changed due to the new definition of a type environment.

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
    ArrowT(t, typeCheck(b, env + (x, t)))
  case App(f, a) =>
    val ArrowT(t1, t2) = typeCheck(f, env)
    val t3 = typeCheck(a, env)
    mustSame(t1, t3)
    t2
  case TypeDef(t, v1, vt1, v2, vt2, b) =>
    if (env.contains(t)) throw new Exception
    val nenv = env +
      (t, Map(v1 -> vt1, v2 -> vt2)) +
      (v1, ArrowT(vt1, IdT(t))) +
      (v2, ArrowT(vt2, IdT(t)))
    validType(vt1, nenv)
    validType(vt2, nenv)
    validType(typeCheck(b, nenv), env)
  case Match(e, v1, x1, e1, v2, x2, e2) =>
    val IdT(t) = typeCheck(e, env)
    val tdef = env.tbinds(t)
    mustSame(
      typeCheck(e1, env + (x1, tdef(v1))),
      typeCheck(e2, env + (x2, tdef(v2)))
    )
}
```

The following computes the type of \(\textsf{type}\ Fruit=Apple@\textsf{num}+Banana@\textsf{num}\ \textsf{in}\ (Apple\ 5)\ \textsf{match}\ Apple(x)\rightarrow x\ |\ Banana(x)\rightarrow x\) with the type checker.

```scala
// type Fruit = Apple@num+Banana@num in
//   (Apple 5) match
//     Apple(x) -> x |
//     Banana(x) -> x
typeCheck(
  TypeDef(
    "Fruit", "Apple", NumT, "Banana", NumT,
    Match(
      App(Id("Apple"), Num(5)),
      "Apple", "x", Id("x"),
      "Banana", "x", Id("x")
    )
  ), TEnv()
)
// num
```

## Implementing Interpreter

The following implements values in TVFAE:

```scala
sealed trait Value
case class NumV(n: Int) extends Value
case class CloV(p: String, b: Expr, e: Env) extends Value
case class VariantV(x: String, v: Value) extends Value
case class ConstructorV(x: String) extends Value

type Env = Map[String, Value]
```

A `VariantV` instance represents a value of a variant; a `ConstructorV` instance
represents the constructor of a variant.

```scala
case TypeDef(_, v1, _, v2, _, b) =>
  interp(b, env + (v1 -> ConstructorV(v1)) + (v2 -> ConstructorV(v2)))
```

\[
\frac
{ \sigma[x_1\mapsto \langle x_1\rangle,x_2\mapsto \langle x_2\rangle]\vdash e\Rightarrow v }
{ \sigma\vdash \textsf{type}\ t=x_1@\tau_1+x_2@\tau_2\ \textsf{in}\ e\Rightarrow v }
\]

Evaluation of an expression defining a type is evaluating the body under the
environment with the constructors.

```scala
case Match(e, v1, x1, e1, v2, x2, e2) =>
  interp(e, env) match {
    case VariantV(`v1`, v) => interp(e1, env + (x1 -> v))
    case VariantV(`v2`, v) => interp(e2, env + (x2 -> v))
  }
```

\[
\frac
{ \sigma\vdash e\Rightarrow x_1(v') \quad
  \sigma[x_3\mapsto v']\vdash e_1\Rightarrow v }
{ \sigma\vdash e\ \textsf{match}\ x_1(x_3)\rightarrow e_1\ |\ x_2(x_4)\rightarrow e_2\Rightarrow v }
\]

\[
\frac
{ \sigma\vdash e\Rightarrow x_2(v') \quad
  \sigma[x_4\mapsto v']\vdash e_2\Rightarrow v }
{ \sigma\vdash e\ \textsf{match}\ x_1(x_3)\rightarrow e_1\ |\ x_2(x_4)\rightarrow e_2\Rightarrow v }
\]

To evaluate a pattern matching expression, the function evaluates the target
expression first. If the result is a variant value and its name is the same as
`v1`, then `e1` is evaluated under the environment with the value of `x1`. If
the names is the same as `v1`, then `e2` is evaluated under the environment with
the value of `x1`.

```scala
case App(f, a) => interp(f, env) match {
  case CloV(x, b, fEnv) =>
    interp(b, fEnv + (x -> interp(a, env)))
  case ConstructorV(x) => VariantV(x, interp(a, env))
}
```

\[
\frac
{ \sigma\vdash e_1\Rightarrow \langle x\rangle \quad
  \sigma\vdash e_2\Rightarrow v }
{ \sigma\vdash e_1\ e_2\Rightarrow x(v) }
\]

A function application allows a constructor to occur at the function position.
If a constructor appears, the argument is evaluated, and the final variant value
contains a value denoted by the argument.

The following shows the entire code of the `interp` function:

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
  case Id(x) => env(x)
  case Fun(x, t, b) => CloV(x, b, env)
  case App(f, a) => interp(f, env) match {
    case CloV(x, b, fEnv) =>
      interp(b, fEnv + (x -> interp(a, env)))
    case ConstructorV(x) => VariantV(x, interp(a, env))
  }
  case TypeDef(_, v1, _, v2, _, b) =>
    interp(b, env + (v1 -> ConstructorV(v1)) + (v2 -> ConstructorV(v2)))
  case Match(e, v1, x1, e1, v2, x2, e2) =>
    interp(e, env) match {
      case VariantV(`v1`, v) => interp(e1, env + (x1 -> v))
      case VariantV(`v2`, v) => interp(e2, env + (x2 -> v))
    }
}

def run(e: Expr): Value = {
  typeCheck(e, TEnv())
  interp(e, Map.empty)
}
```

The following evaluates \(\textsf{type}\ Fruit=Apple@\textsf{num}+Banana@\textsf{num}\ \textsf{in}\ (Apple\ 5)\ \textsf{match}\ Apple(x)\rightarrow x\ |\ Banana(x)\rightarrow x\) with the interpreter:

```scala
// type Fruit = Apple@num+Banana@num in
//   (Apple 5) match
//     Apple(x) -> x |
//     Banana(x) -> x
run(
  TypeDef(
    "Fruit", "Apple", NumT, "Banana", NumT,
    Match(
      App(Id("Apple"), Num(5)),
      "Apple", "x", Id("x"),
      "Banana", "x", Id("x")
    )
  )
)
// 5
```

## Broken Type-Soudness

The following rules are parts of the current type system:

\[
\frac
{ \begin{array}{c}
  t\not\in\mathit{Domain}(\Gamma) \\
  \Gamma'=\Gamma[t=x_1@\tau_1+x_2@\tau_2,x_1:\tau_1\rightarrow t,x_2:\tau_2\rightarrow t] \\
  \Gamma'\vdash e:\tau \quad
  \Gamma'\vdash \tau_1 \quad
  \Gamma'\vdash \tau_2 \quad
  \Gamma\vdash \tau\end{array} }
{ \Gamma\vdash \textsf{type}\ t=x_1@\tau_1+x_2@\tau_2\ \textsf{in}\ e:\tau }
\]

\[
\frac
{ \Gamma\vdash\tau \quad \Gamma\lbrack x:\tau\rbrack\vdash \tau' }
{ \Gamma\vdash \lambda x:\tau.e:\tau\rightarrow\tau' }
\]

The premises \(t\not\in{\it Domain}(\Gamma)\), \(\Gamma'\vdash\tau_1\),
\(\Gamma'\vdash\tau_2\), and \(\Gamma\vdash\tau\) are essential to make TVFAE
type-sound. Removing only one of them makes the language not type-sound.

To show that a type system is unsound, a counterexample must be found. An
expression that is not rejected by the type system but causes a run-time type
error is such a counterexample.

### Allowing Types of the Same Name

Consider the following rule:

\[
\frac
{ \begin{array}{c}
  \Gamma'=\Gamma[t=x_1@\tau_1+x_2@\tau_2,x_1:\tau_1\rightarrow t,x_2:\tau_2\rightarrow t] \\
  \Gamma'\vdash e:\tau \quad
  \Gamma'\vdash \tau_1 \quad
  \Gamma'\vdash \tau_2 \quad
  \Gamma\vdash \tau\end{array} }
{ \Gamma\vdash \textsf{type}\ t=x_1@\tau_1+x_2@\tau_2\ \textsf{in}\ e:\tau }
\]

It lacks the premise \(t\not\in{\it Domain}(\Gamma)\).

\[
\begin{array}{l}
\textsf{type}\ Fruit=Apple@\textsf{num}+Banana@\textsf{num}\ \textsf{in} \\
\quad \textsf{type}\ Fruit=Apple@\textsf{num}+Cherry@\textsf{num}\ \textsf{in} \\
\quad\quad e
\end{array}
\]

By finding a proper expression \(e\), one can make the above expression be a
counterexample of the type soundness.

### Allowing Ill-Formed Types for Variants

Consider the following rule:

\[
\frac
{ \begin{array}{c}
  t\not\in\mathit{Domain}(\Gamma) \\
  \Gamma'=\Gamma[t=x_1@\tau_1+x_2@\tau_2,x_1:\tau_1\rightarrow t,x_2:\tau_2\rightarrow t] \\
  \Gamma'\vdash e:\tau \quad
  \Gamma'\vdash \tau_2 \quad
  \Gamma\vdash \tau\end{array} }
{ \Gamma\vdash \textsf{type}\ t=x_1@\tau_1+x_2@\tau_2\ \textsf{in}\ e:\tau }
\]

It lacks the premise \(\Gamma'\vdash\tau_1\).

\[
\begin{array}{l}
\textsf{type}\ Fruit=Apple@Color+Banana@\textsf{num}\ \textsf{in} \\
\quad (\lambda f:Fruit.\textsf{type}\ Color=\cdots \\
\quad\quad \cdots \\
\quad )\ (\textsf{type}\ Color=\cdots \\
\quad\quad \cdots \\
\quad )
\end{array}
\]

By finding proper expressions for the omitted parts, one can make the above
expression be a counterexample of the type soundness.

### Allowing Local Types to Escape Scopes

Consider the following rule:

\[
\frac
{ \begin{array}{c}
  t\not\in\mathit{Domain}(\Gamma) \\
  \Gamma'=\Gamma[t=x_1@\tau_1+x_2@\tau_2,x_1:\tau_1\rightarrow t,x_2:\tau_2\rightarrow t] \\
  \Gamma'\vdash e:\tau \quad
  \Gamma'\vdash \tau_1 \quad
  \Gamma'\vdash \tau_2 \quad
  \end{array} }
{ \Gamma\vdash \textsf{type}\ t=x_1@\tau_1+x_2@\tau_2\ \textsf{in}\ e:\tau }
\]

It lacks the premise \(\Gamma\vdash\tau\).

\[
\begin{array}{l}
( \\
\quad \textsf{type}\ Fruit=Apple@(\textsf{num}\times\textsf{num})+Banana@\textsf{num}\ \textsf{in} \\
\quad \quad \lambda f:Fruit.\cdots \\
) ( \\
\quad\textsf{type}\ Fruit=Apple@\textsf{num}+Banana@\textsf{num}\ \textsf{in} \\
\quad \quad Apple\ 5 \\
)
\end{array}
\]

By finding a proper expression for the omitted part, one can make the above
expression be a counterexample of the type soundness.

### Allowing Ill-Formed Parameter Type Annotations

Consider the following rule:

\[
\frac
{ \Gamma\lbrack x:\tau\rbrack\vdash \tau' }
{ \Gamma\vdash \lambda x:\tau.e:\tau\rightarrow\tau' }
\]

It lacks the premise \(\Gamma\vdash\tau\).

\[
(\lambda f:Fruit\rightarrow\textsf{num}.\cdots)(\lambda f:Fruit.\cdots)
\]

By finding proper expressions for the omitted parts, one can make the above
expression be a counterexample of the type soundness.

## Acknowledgments

I thank professor Ryu for giving feedback on the article.
