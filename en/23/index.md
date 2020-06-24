This article defines STFAE by adding *records* and subtype polymorphism to TFAE.
It defines the abstract syntax, the dynamic semantics, and the type system of
STFAE.

## Abstract Syntax

The following shows the abstract syntax of STFAE:

\[
\begin{array}{rrcl}
\text{Label} & l & \in & \mathcal{L} \\
\text{Expression} & e & ::= & \cdots \\
&&|& \{l=e,\cdots,l=e\} \\
&&|& e.l \\
\text{Value} & v & ::= & \cdots \\
&&|& \{l=v,\cdots,l=v\} \\
\text{Type} & \tau & ::= & \cdots \\
&&|& \{l:\tau,\cdots,l:\tau\} \\
\end{array}
\]

Omitted parts are the same as TFAE.

Metavariable \(l\) ranges over labels. Labels are the names of fields in
records.

\(\{l_1=e_1,\cdots,l_n=e_n\}\) is an expression that creates a record value. A
record has zero or more fields. \(l\)'s are the names of the fields. This
article assumes that every field in a single record has a distinct name. For
evaluation of the expression, every \(e_i\) needs to be evaluated. The result of
\(e_i\) is the value of the field \(l_i\).

\(\{l_1=v_1,\cdots,l_n=v_n\}\) is a record value. \(l\)'s are the names of the
fields. \(v_i\) is the value of the field \(l_i\). For example, the result of
\(\{a=1+2,b=3+4\}\) is \(\{a=3,b=7\}\). The record has two fields: \(a\) and
\(b\). The value of \(a\) is \(3\), and the value of \(b\) is \(7\). \(\{\}\)
also is an expression that makes a record value. Its result is \(\{\}\), which
is the empty record.

\(\{l_1:\tau_1,\cdots,l_n:\tau_n\}\) is a record type. \(l\)'s are the names of
the fields. \(\tau_i\) is the type of the field \(l_i\). Since both \(3\) and
\(7\) are integers, the type of \(\{a=3,b=7\}\) is \(\{a:{\sf num},b:{\sf
num}\}\). The type of \(\{a=1+2,b=3+4\}\) also is \(\{a:{\sf num},b:{\sf
num}\}\). Similarly, the type of the empty record is \(\{\}\).

\(e.l\) is an expression that uses a record value. It is usually called a
*projection*. If the result of \(e\) is \(\{l_1=v_1,\cdots,l_n=v_n\}\), then
\(e.l\) results in \(v_i\) where \(l_i\) is equal to \(l\). The expression
computes the value of the field \(l\) of a given record value. If \(e\) does not
result in a record value, \(e.l\) causes a type error at run time. If \(e\)
results in a record value that lacks a field named \(l\), \(e.l\) also causes a
type error. In the view of a type system, projections use record types. If the
type of \(e\) is \(\{l_1:\tau_1,\cdots,l_n:\tau_n\}\), then the type of \(e.l\)
is \(\tau_i\) where \(l_i\) is equal to \(l\). For example, since
\(\{a=1+2,b=3+4\}\) results in \(\{a=3,b=7\}\), \(\{a=1+2,b=3+4\}.a\) results in
\(3\). The type of \(\{a=1+2,b=3+4\}\) is \(\{a:{\sf num},b:{\sf num}\}\), and
the type of \(\{a=1+2,b=3+4\}.a\) is, therefore, \(\sf num\). On the other hand,
\(\{a=1+2,b=3+4\}.c\) causes a type error and is an ill-typed expression because
the record lacks a field named \(c\).

## Dynamic Semantics

It is enough to consider only the semantics of records and projections. Other
expressions have the same semantics as TFAE.

\[
\frac
{ \sigma\vdash e_1\Rightarrow v_1 \quad \cdots \quad \sigma\vdash e_n\Rightarrow v_n }
{ \sigma\vdash \{l_1=e_1,\cdots,l_n=e_n\}\Rightarrow\{l_1=v_1,\cdots,l_n=v_n\} }
\]

Every \(e_i\) has to be evaluated for evaluation of
\(\{l_1=e_1,\cdots,l_n=e_n\}\). If the value of \(e_1\) is \(v_1\), the value of
the field \(l_i\) is \(v_i\). The result is \(\{l_1=v_1,\cdots,l_n=v_n\}\).

\[
\frac
{ \sigma\vdash e\Rightarrow\{l_1=v_1,\cdots,l=v,\cdots,l_n=v_n\} }
{ \sigma\vdash e.l\Rightarrow v }
\]

\(e\) has to be evaluated first for evaluation of \(e.l\). The result \(e\) must
be a record that contains a field named \(l\). The value of \(e.l\) is the same
as the value of the field \(l\).

## Type System

### Typing Rules

It is enough to consider only the semantics of records and projections. Other
typing rules are the same as those of TFAE.

\[
\frac
{ \Gamma\vdash e_1:\tau_1 \quad \cdots \quad \Gamma\vdash e_n:\tau_n }
{ \Gamma\vdash \{l_1=e_1,\cdots,l_n=e_n\}\Rightarrow\{l_1:\tau_1,\cdots,l_n:\tau_n\} }
\]

The value of \(\{l_1=e_1,\cdots,l_n=e_n\}\) is \(\{l_1=v_1,\cdots,l_n=v_n\}\).
Let the type of \(v_i\) be \(\tau_i\). The type of
\(\{l_1=v_1,\cdots,l_n=v_n\}\) is \(\{l_1:\tau_1,\cdots,l_n:\tau_n\}\), and,
therefore, the type of \(\{l_1=e_1,\cdots,l_n=e_n\}\) also is
\(\{l_1:\tau_1,\cdots,l_n:\tau_n\}\). Since the type of \(v_i\) is \(\tau_i\),
the type of \(e_i\) also is \(\tau_i\). Thus, it is enough to compute the type
of every \(e_i\) to type-check \(\{l_1=e_1,\cdots,l_n=e_n\}\).

\[
\frac
{ \Gamma\vdash e:\{l_1:\tau_1,\cdots,l:\tau,\cdots,l_n:\tau_n\} }
{ \Gamma\vdash e.l: \tau }
\]

\(e.l\) can be evaluated without an error only if \(e\) is well-typed and the
result of \(e\) is a record containing a field named \(l\). Therefore, the type
of \(e\) has to be a record type that contains a field \(l\). Then, the type of
\(e.l\) is the type of \(l\).

### Subtype Polymorphism

The current type system is sound but not expressive enough. It rejects many
expressions that do not cause an error at run time. Consider the following
expression:

\[
(\lambda x:\{a:\textsf{num}\}.x.a)\ \{a=1,b=2\}
\]

The expression evaluates \(\{a=1,b=2\}.a\), which yields \(1\) without any
error. However, the type system rejects the expression. The type of
\(\{a=1,b=2\}\) is \(\{a:{\sf num},b:{\sf num}\}\), while the parameter type of
the function \(\lambda x:\{a:{\sf num}\}.x.a\) is \(\{a:{\sf num}\}\). Since the
types of the argument and the parameter differ from each other, it is ill-typed.

The type \(\{a:{\sf num}\}\) is the type of a record that has an integer-value
field \(a\). Even though the record has fields other than \(a\), it still has
the field \(a\). Therefore, it would be correct to say that \(\{a=1,b=2\}\) is a
value of the type \(\{a:{\sf num}\}\). However, under the current type system,
the only type of \(\{a=1,b=2\}\) is \(\{a:{\sf num},b:{\sf num}\}\). \(\{a:{\sf
num}\}\) is not a type of \(\{a=1,b=2\}\).

The type system should be able to consider \(\{a=1,b=2\}\) to be a value of not
only \(\{a:{\sf num},b:{\sf num}\}\) but also \(\{a:{\sf num}\}\). A language
feature that allows a single entity to be used as multiple types is called
polymorphism. The last article dealt with parametric polymorphism. However,
parametric polymorphism cannot resolve the current issue. A new sort of
polymorphism is required. It is time to consider subtype polymorphism.

Subtype polymorphism uses a notion of subtyping. Subtyping is a relation between
two types. \(\tau_1<:\tau_2\) denotes that \(\tau_1\) is a subtype of
\(\tau_2\). If \(\tau_1\) is a subtype of \(\tau_2\), then \(\tau_2\) is a
supertype of \(\tau_1\), and a value of \(\tau_1\) can be used where a value of
\(\tau_2\) is expected. Subtype polymorphism is a concept based on
*substitutability*. The previous examples shows that a value of \(\{a:{\sf
num},b:{\sf num}\}\) can appear where a value of \(\{a:{\sf num}\}\) is
expected. Therefore, \(\{a:{\sf num},b:{\sf num}\}\) is a subtype of \(\{a:{\sf
num}\}\), and \(\{a:{\sf num}\}\) is a supertype of \(\{a:{\sf num},b:{\sf
num}\}\).

The type system needs a new typing rule to support subtype polymorphism. The
subtype relation of the type system will be shown soon. Just assume that the
relation has been defined already for a while. The following typing rule allows
a single expression to have multiple types by using the subtype relation:

\[
\frac
{ \Gamma\vdash e:\tau' \quad \tau'<:\tau }
{ \Gamma\vdash e:\tau }
\]

The rule is usually called a *subsumption* rule. The rule implies that if
\(\tau'\) is a type of \(e\) and \(\tau'\) is a subtype of \(\tau\), then
\(\tau\) also is a type of \(e\). Due to the subtype polymorphism, a single
expression can have multiple types. It is a big difference between STFAE and the
hitherto languages.

### Subtyping Rules

It is time to define *subtyping rules* of the language. As typing rules define the
types of expressions, subtyping rules define the subtype relation.

#### Reflexivity

The subtype relation is *reflexive*. A value of \(\tau\) can be used where a value
of \(\tau\) is expected. It is trivial. Thus, every type is a subtype of itself.
The following rule formalizes this fact:

\[\tau<:\tau\]

By the rule, \(\{a:{\sf num}\}<:\{a:{\sf num}\}\) is true.

#### Transitivity

The subtype relation is *transitive* as well. Let \(\tau_1\) be a subtype of
\(\tau_2\) and \(\tau_2\) be a subtype of \(\tau_3\). A value of \(\tau_2\) can
appear where a value of \(\tau_3\) is expected. Also, a value of \(\tau_1\) can
appear where a value of \(\tau_2\) is expected. Therefore, a value of \(\tau_1\)
can replace a value of \(\tau_3\) without causing an error. In conclusion,
\(\tau_1\) is a subtype of \(\tau_3\). The following rule formalizes the
transitivity:

\[
\frac
{ \tau_1<:\tau_2 \quad \tau_2<:\tau_3 }
{ \tau_1<:\tau_3 }
\]

The above two rules describe important properties of the subtype relation.
However, they are not enough to prove interesting facts, such as \(\{a:{\sf
num},b:{\sf num}\}<:\{a:{\sf num}\}\).

#### Width Rule

Consider the previous example again. The type system should be able to prove
\(\{a:{\sf num},b:{\sf num}\}<:\{a:{\sf num}\}\). The following rule makes the
type system achieve the goal:

\[
\{l_1:\tau_1,\cdots,l_n:\tau_n,l:\tau\}<:\{l_1:\tau_1,\cdots,l_n:\tau_n\}
\]

If a field \(l\) is appended to a record type
\(\{l_1:\tau_1,\cdots,l_n:\tau_n\}\), the resulting type is a subtype of the
original type. It is a valid rule since even after a record value acquires one
more field, the record value will be able to do everything the original record
can do. Intuitively, the rule allows considering a record type of "a longer
width" to be a subtype of a record type of "a shorter width." For this reason,
the article calls the rule "the width rule." Other authors may not use the same
name. Now, \(\{a:{\sf num},b:{\sf num}\}<:\{a:{\sf num}\}\) is true.

The fact that \(\{a:num\}\) is a type of \(\{a=1,b=2\}\) can be provable. The
following proof tree proves the fact:

\[
\frac
{ 
  {\Large\frac
  { \emptyset\vdash1:\textsf{num} \quad \emptyset\vdash2:\textsf{num} }
  { \emptyset\vdash\{a=1,b=2\}:\{a:\textsf{num},b:\textsf{num}\} }} \quad
  \{a:\textsf{num},b:\textsf{num}\}<:\{a:\textsf{num}\}}
{ \emptyset\vdash\{a=1,b=2\}:\{a:\textsf{num}\} }
\]

The following expression is now well-typed:

\[
(\lambda x:\{a:\textsf{num}\}.x.a)\ \{a=1,b=2\}
\]

Other interesting subtypes can be also found by the transitivity and the width
rule. For example, the following proof tree proves that \(\{a:{\sf num},b:{\sf
num},c:{\sf num}\}<:\{a:{\sf num}\}\).

\[
\frac
{ 
  \{a:\textsf{num},b:\textsf{num},c:\textsf{num}\}<:\{a:\textsf{num},b:\textsf{num}\} \quad
  \{a:\textsf{num},b:\textsf{num}\}<:\{a:\textsf{num}\}}
{ \{a:\textsf{num},b:\textsf{num},c:\textsf{num}\}<:\{a:\textsf{num}\} }
\]

By the same principle, \(\{\}\), which is the empty record type, is a supertype
of every record type. In other words, every record type is a subtype of
\(\{\}\).

#### Permutation Rule

The type system is still restrictive. The following expression is ill-typed but
does not cause a run-time error:

\[
(\lambda x:\{a:\textsf{num},b:\textsf{num}\}.x.a)\ \{b=2,a=1\}
\]

The above expression evaluates \(\{b=2,a=1\}.a\), which results in \(1\).
However, it is ill-typed. The type of \(\{b=2,a=1\}\) is \(\{b:{\sf num},a:{\sf
num}\}\), while the parameter type of the function \(\lambda x:\{a:{\sf
num},b:{\sf num}\}.x.a\) is \(\{a:{\sf num},b:{\sf num}\}\).

In fact, the order among the fields of a record does not matter at all. If two
records have the same fields and each field has the same value in both records,
their behaviors as records are the same even if the orders are different. On the
contrary, the orders matter during type checking under the current type system.
It is sure that a value of \(\{a:{\sf num},b:{\sf num}\}\) can replace a value
of \(\{b:{\sf num},a:{\sf num}\}\) without causing a type error and vice versa.
Therefore, \(\{a:{\sf num},b:{\sf num}\}\) should be a subtype of \(\{b:{\sf
num},a:{\sf num}\}\) and vice versa. The following rule solves the problem:

\[
\frac
{ \{(l_1,\tau_1),\cdots,(l_n,\tau_n)\}=\{(l'_1,\tau'_1),\cdots,(l'_n,\tau'_n)\} }
{ \{l_1:\tau_1,\cdots,l_n:\tau_n\}<:\{l'_1:\tau'_1,\cdots,l'_n:\tau'_n\} }
\]

Equality of sets is determined solely by elements in sets. Orders do not matter.
The rule implies that altering the order among the fields of a record type makes
a subtype of the record type. The article calls it "the permutation rule." Other
authors may not use the same term. The following proof trees prove that
\(\{a:{\sf num},b:{\sf num}\}<:\{b:{\sf num},a:{\sf num}\}\) and \(\{b:{\sf
num},a:{\sf num}\}<:\{a:{\sf num},b:{\sf num}\}\).

\[
\frac
{ \{(a,\textsf{num}),(b,\textsf{num})\}=\{(b,\textsf{num}),(a,\textsf{num})\} }
{ \{a:\textsf{num},b:\textsf{num}\}<:\{b:\textsf{num},a:\textsf{num}\} }
\]

\[
\frac
{ \{(b,\textsf{num}),(a,\textsf{num})\}=\{(a,\textsf{num}),(b,\textsf{num})\} }
{ \{b:\textsf{num},a:\textsf{num}\}<:\{a:\textsf{num},b:\textsf{num}\} }
\]

The following expression is now well-typed:

\[
(\lambda x:\{a:\textsf{num},b:\textsf{num}\}.x.a)\ \{b=2,a=1\}
\]

Other interesting subtypes can be also found by the width and permutation rules.
The following proof tree proves that \(\{b:{\sf num},a:{\sf num}\}<:\{a:{\sf
num}\}\).

\[
\frac
{{\Large
  \frac
  { \{(a,\textsf{num}),(b,\textsf{num})\}=\{(b,\textsf{num}),(a,\textsf{num})\} }
  { \{a:\textsf{num},b:\textsf{num}\}<:\{b:\textsf{num},a:\textsf{num}\} }} \quad
  \{b:\textsf{num},a:\textsf{num}\}<:\{b:\textsf{num}\}
}
{ \{a:\textsf{num},b:\textsf{num}\}<:\{b:\textsf{num}\} }
\]

#### Depth Rule

The type system still can be improved more. Consider the following expression:

\[
(\lambda x:\{a:\{a:\textsf{num},b:\textsf{num}\}\}.(\lambda x:\{a:\{a:\textsf{num}\}\}.x.a.a)\ x)\ \{a=\{a=1,b=2\}\}
\]

The above expression evaluates \(\{a=\{a=1,b=2\}\}.a.a\). Since
\(\{a=\{a=1,b=2\}\}.a\) results in \(\{a=1,b=2\}\) and \(\{a=1,b=2\}.a\) results
in \(1\), the expression results in \(1\) without any error. However, the
expression is well-typed. The type of \(x\) is \(\{a:\{a:{\sf num},b:{\sf
num}\}\}\), while the parameter type of the function \(\lambda x:\{a:\{a:{\sf
num}\}\}.x.a.a\) is \(\{a:\{a:{\sf num}\}\}\).

The current type system is too strict to the type of a field in a record. For
example, consider \(\{a:\{a:{\sf num}\}\}\). Any other type of the form
\(\{a:\tau\}\) cannot be a subtype of \(\{a:\{a:{\sf num}\}\}\). However, it
would be beneficial to allow some other types to be subtypes. Let the type of
\(e\) be \(\{a:\{a:{\sf num}\}\}\). Then, the result of \(e.a\) can be used
where a value of \(\{a:{\sf num}\}\) is expected. Now, let the type of \(e'\) be
\(\{a:\{a:{\sf num},b:{\sf num}\}\}\). The result of \(e.a\) is a value of
\(\{a:{\sf num},b:{\sf num}\}\). It is known that \(\{a:{\sf num},b:{\sf
num}\}\) is a subtype of \(\{a:{\sf num}\}\). Therefore, \(e'.a\) can replace
\(e.a\) without causing a type error. It implies that \(e'\) can be used instead
of \(e\). In other words, \(\{a:\{a:{\sf num},b:{\sf num}\}\}\) is a subtype of
\(\{a:\{a:{\sf num}\}\}\). More generally, if \(\tau<:\{a:{\sf num}\}\), then
\(\{a:\tau\}<:\{a:\{a:{\sf num}\}\}\).

The same logic can be applied to any records regardless of the names and the
number of fields. It leads to the following rule:

\[
\frac
{ \tau_1<:\tau'_1 \quad \cdots \quad \tau_n<:\tau'_n }
{ \{l_1:\tau_1,\cdots,l_n:\tau_n\}<:\{l_1:\tau'_1,\cdots,l_n:\tau'_n\} }
\]

It makes the subtype relation to consider the types of fields in record types.
The article calls it "the depth rule" since the rule inspects record types more
"deeply." Other authors may not use the same term. The following proof tree
proves that \(\{a:\{a:{\sf num},b:{\sf num}\}\}<:\{a:\{a:{\sf num}\}\}\):

\[
\frac
{ \{a:\textsf{num},b:\textsf{num}\}<:\{a:\textsf{num}\} }
{ \{a:\{a:\textsf{num},b:\textsf{num}\}\}<:\{a:\{a:\textsf{num}\}\} }
\]

The following expression is now well-typed:

\[
(\lambda x:\{a:\{a:\textsf{num},b:\textsf{num}\}\}.(\lambda x:\{a:\{a:\textsf{num}\}\}.x.a.a)\ x)\ \{a=\{a=1,b=2\}\}
\]

Finally, the subtype relation is precise enough for record types.

#### Subtyping Rule for Function Types

It is time to consider a subtyping rule for function types. A function type
consists of the parameter type and the return type. The article explains about
the return type first and then the parameter type.

Consider two function types: \(\tau_1\rightarrow\tau_2\) and
\(\tau_1\rightarrow\tau_2'\). Assume that \(\tau_2\) is a subtype of
\(\tau_2'\).

Can \(\tau_1\rightarrow\tau_2\) be a subtype of \(\tau_1\rightarrow\tau_2'\)? If
so, then a value of \(\tau_1\rightarrow\tau_2\) can be used where a value of
\(\tau_1\rightarrow\tau_2'\) is expected. Let the type of \(e_1'\) be
\(\tau_1\rightarrow\tau_2'\). Then, \(e_1'\) can be appear at the function
position of a function application. Let \(e_2\) be an expression of \(\tau_1\).
\(e_1'\ e_2\) is well-typed, and its type is \(\tau_2'\). The result of the
function application can be used anywhere a value of \(\tau_2'\) is expected.
Now, we are going to check whether \(e_1\), whose type is
\(\tau_1\rightarrow\tau_2\), can replace \(e_1'\). Consider \(e_1\ e_2\). Since
the parameter type of \(e_1\) is \(\tau_1\), it is well-formed. The result is a
value of \(\tau_2\), which is a subtype of \(\tau_2'\). Therefore, the result of
the function application can be used where a value of \(\tau_2'\) is expected.
It implies that \(e_1\ e_2\) can replace \(e_1'\ e_2\) without causing a type
error. Function applications are only use of functions. Thus, \(e_1\) can
replace \(e_1'\), and \(\tau_1\rightarrow\tau_2\) is a subtype of
\(\tau_1\rightarrow\tau_2'\). The following rule formalizes this fact:

\[
\frac
{ \tau_2<:\tau_2' }
{ \tau_1\rightarrow\tau_2<:\tau_1\rightarrow\tau_2' }
\]

Function types preserves the subtype relation between their return types.
Suppose that there are two function types. If their parameter types are the same
and the return type of the former is a subtype of that of the latter, then the
former is a subtype of the latter. For example, \({\sf num}\rightarrow\{a:{\sf
num},b:{\sf num}\}\) is a subtype of , \({\sf num}\rightarrow\{a:{\sf num}\}\).

Consider two function types: \(\tau_1\rightarrow\tau_2\) and
\(\tau_1'\rightarrow\tau_2\). Assume that \(\tau_1'\) is a subtype of
\(\tau_1\).

Can \(\tau_1\rightarrow\tau_2\) be a subtype of \(\tau_1'\rightarrow\tau_2\)?
Let the type of \(e_1\) be \(\tau_1\rightarrow\tau_2\) and the type of \(e_1'\)
be \(\tau_1'\rightarrow\tau_2\). Since their return type are the same, their
return values can be used at the same place. It is enough to focus on their
arguments. Let the type of \(e_2\) be \(\tau_1'\). Then, \(e_1'\ e_2\) is
well-typed since the parameter type of \(e_1'\) is equal to the type of \(e_2\).
On the other hand, \(e_1\) can take a value of \(\tau_1\) as an argument. Every
expression whose type is \(\tau_1\) can be an argument. The type of \(e_2\) is
\(\tau_1'\). By the assumption that \(\tau_1'\) is a subtype of \(\tau_1\),
\(e_2\) can be used where a value of \(\tau_1\) is expected. Thus, \(e_2\) can
be an argument for \(e_1\). \(e_1\ e_2\) is a well-typed expression. In
conclusion, \(e_1\) can replace \(e_2\) without causing a type error, and
\(\tau_1\rightarrow\tau_2\) is a subtype of \(\tau_1'\rightarrow\tau_2\). The
following rule formalizes this fact:

\[
\frac
{ \tau_1'<:\tau_1 }
{ \tau_1\rightarrow\tau_2<:\tau_1'\rightarrow\tau_2 }
\]

Function types reverses the subtype relation between their parameter types.
Suppose that there are two function types. If their return types are the same
and the parameter type of the former is a supertype of that of the latter, then
the former is a subtype of the latter. For example, , \(\{a:{\sf
num}\}\rightarrow{\sf num}\) is a subtype of \(\{a:{\sf num},b:{\sf
num}\}\rightarrow{\sf num}\).

The above rules can combine to form one rule:

\[
\frac
{ \tau_1'<:\tau_1 \quad \tau_2<:\tau_2' }
{ \tau_1\rightarrow\tau_2<:\tau_1'\rightarrow\tau_2' }
\]

The above rule is the final version of a subtyping rule for function types.

Below shows all the subtyping rules of STFAE:

\[\tau<:\tau\]

\[
\frac
{ \tau_1<:\tau_2 \quad \tau_2<:\tau_3 }
{ \tau_1<:\tau_3 }
\]

\[
\{l_1:\tau_1,\cdots,l_n:\tau_n,l:\tau\}<:\{l_1:\tau_1,\cdots,l_n:\tau_n\}
\]

\[
\frac
{ \{(l_1,\tau_1),\cdots,(l_n,\tau_n)\}=\{(l'_1,\tau'_1),\cdots,(l'_n,\tau'_n)\} }
{ \{l_1:\tau_1,\cdots,l_n:\tau_n\}<:\{l'_1:\tau'_1,\cdots,l'_n:\tau'_n\} }
\]

\[
\frac
{ \tau_1<:\tau'_1 \quad \cdots \quad \tau_n<:\tau'_n }
{ \{l_1:\tau_1,\cdots,l_n:\tau_n\}<:\{l_1:\tau'_1,\cdots,l_n:\tau'_n\} }
\]

\[
\frac
{ \tau_1'<:\tau_1 \quad \tau_2<:\tau_2' }
{ \tau_1\rightarrow\tau_2<:\tau_1'\rightarrow\tau_2' }
\]

## Extending STFAE

STFAE can be easily extended with a few types.

### Top Type

\[
\begin{array}{rrcl}
\text{Type} & \tau & ::= & \cdots \\
&&|& \top \\
\end{array}
\]

\(\top\) is the *top* type. The top type is a supertype of every type, and every
type is a subtype of the top type. Every value is a value of the top type. The
following is a subtyping rule for the top type:

\[\tau<:\top\]

The top type can be used to give a single type to two or more completely
irrelevant expressions. Suppose that the language has conditional expressions.
Then, the type of the following expression is \(\{a:{\sf num}\}\):

\[\textsf{if}\ \textsf{true}\ \{a=1\}\ \{a=1,b=2\}\]

However, the following expression is ill-formed in STFAE even though it does not
cause a type error:

\[\textsf{if}\ \textsf{true}\ \{a=1\}\ 1\]

By extending STFAE with the top type, the type of the above expression can be
\(\top\).

### Bottom Type

\[
\begin{array}{rrcl}
\text{Type} & \tau & ::= & \cdots \\
&&|& \bot \\
\end{array}
\]

\(\bot\) is the *bottom* type. The bottom type is a subtype of every type, and
every type is a supertype of the bottom type. The following is a subtyping rule
for the bottom type:

\[\bot<:\tau\]

Types like \(\sf num\) and \({\sf num}\rightarrow{\sf num}\) do not have any
common elements. Therefore, if a type is a subtype of every type, no value can
be a value of the type. The bottom type does not have any elements. Despite
the fact, the bottom type is useful. The bottom type can be the types of
expressions throwing exceptions or calling first-class continuations. Those
expressions do not result in any values. They only change the flow of
programs. Thus, it is quite natural to say that the type of such an expression
is the bottom type.

## Acknowledgments

I thank professor Ryu for giving feedback on the article.
