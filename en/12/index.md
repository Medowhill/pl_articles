*Lambda calculus* is a language featuring only variables, lambda abstractions, and function applications. The article discusses how much lambda calculus can express.

## Syntax

The following is the abstract syntax of lambda calculus:

\[
\begin{array}{lrcl}
\text{Variable} & x & \in & \textit{Id} \\
\text{Expression} & e & ::= & x \\
&& | & \lambda x.e \\
&& | & e\ e \\
\text{Value} & v & ::= & \langle \lambda x.e,\sigma \rangle \\
\text{Environment} & \sigma & \in & \textit{Id}\hookrightarrow\text{Value}
\end{array}
\]

Unlike FAE, lambda calculus lacks integers, sums, and differences. An expression is a variable, a lambda abstraction, or a function application. A value is a closure but not an integer.

## Semantics

The semantics of lambda calculus follows that of FAE.

\[\Rightarrow\subseteq\text{Environment}\times\text{Expression}\times\text{Value}\]

\[
\frac
{ x\in\mathit{Domain}(\sigma) }
{ \sigma\vdash x\Rightarrow \sigma(x)}
\]

\[
\sigma\vdash \lambda x.e\Rightarrow \langle\lambda x.e,\sigma\rangle
\]

\[
\frac
{ \sigma\vdash e_1\Rightarrow\langle\lambda x.e,\sigma'\rangle \quad
  \sigma\vdash e_2\Rightarrow v' \quad
  \sigma'\lbrack x\mapsto v'\rbrack\vdash e\Rightarrow v }
{ \sigma\vdash e_1\ e_2\Rightarrow v }
\]

## Church Numerals

*Church numerals* encode numbers and operations treating number, such as addition and multiplication, with lambda calculus. The following encode natural numbers with lambda calculus:

\[
\begin{array}{rcl}
\mathit{encode}(0)&=&\lambda f.\lambda x.x \\
\mathit{encode}(1)&=&\lambda f.\lambda x.f\ x \\
\mathit{encode}(2)&=&\lambda f.\lambda x.f\ (f\ x) \\
\mathit{encode}(3)&=&\lambda f.\lambda x.f\ (f\ (f\ x)) \\
&\cdots&
\end{array}
\]

Intuitively, natural number \(n\) is a function that takes a function as an argument and returns a function applying the argument \(n\) times: \(\mathit{encode}(n)=f\mapsto f^n\). For any natural number \(n\) and any function \(f\), \(n\ f\) equals \(f^n\).

The encoding of sums shows why the above encoding makes sense.

\[
\begin{array}{rcl}
+&\equiv&\lambda n.\lambda m.\lambda f.\lambda x.n\ f\ (m\ f\ x) \\
\mathit{encode}(e_1+e_2)&=&\lambda f.\lambda x.\mathit{encode}(e_1)\ f\ (\mathit{encode}(e_2)\ f\ x)
\end{array}
\]

For any natural number \(n\) and \(m\) and any function \(f\), \( (n+m)\ f\) equals \(\lambda x.n\ f\ (m\ f\ x)\). Since \(n\ f\) equals \(f^n\), and \(m\ f\) equals \(f^m\), it is \(\lambda x.f^n\ (f^m\ x)\) and equals \(\lambda x.f^{n+m}\ x\), or \(f^{n+m}\). Therefore, \(n+m\) equals \(f\mapsto f^{n+m}\), and the \(\mathit{encode}\) function correctly encodes sums.

The following encodes products:

\[
\begin{array}{rcl}
\times&\equiv&\lambda n.\lambda m.\lambda f.\lambda x.n\ (m\ f)\ x \\
\mathit{encode}(e_1\times e_2)&=&\lambda f.\lambda x.\mathit{encode}(e_1)\ (\mathit{encode}(e_2)\ f)\ x
\end{array}
\]

For any natural number \(n\) and \(m\) and any function \(f\), \( (n\times m)\ f\) equals \(\lambda x.n\ (m\ f)\ x)\). Since \(m\ f\) equals \(f^m\), it is \(\lambda x.n\ f^m\ x\). As \(n\ f\) is \(f^n\), \(n\ f^m\) is \( (f^m )^n\). Hence, \(n\times m\) equals \(f\mapsto f^{n\times m}\), and the \(\mathit{encode}\) function correctly encodes products.

Lambda calculus can encode differences and ratios as well. Moreover, it can encode integers, rational numbers, and operations for them. They are beyond the scope of the article.

## Church Booleans

*Church Booleans* encode true, false, conditional expressions, logical sums, logical products, and other logical language features. The hitherto defined languages lack Boolean values. The section defines BAE by adding true, false, and conditional expressions to AE.

\[
\begin{array}{lrcl}
\text{Boolean} & b & ::= & true \\
&& | & false \\
\text{Expression} & e & ::= & \cdots \\
&& | & b \\
&& | & \textsf{if}\ e\ e\ e \\
\text{Value} & v & ::= & \cdots \\
&& | & b
\end{array}
\]

It shows only features missed by AE. Metavariable \(b\) ranges over Boolean values.

\[
\vdash b\Rightarrow b
\]

\[
\frac
{ \vdash e_1\Rightarrow true \quad \vdash e_2\Rightarrow v }
{ \vdash \textsf{if}\ e_1\ e_2\ e_3\Rightarrow v}
\]

\[
\frac
{ \vdash e_1\Rightarrow false \quad \vdash e_3\Rightarrow v }
{ \vdash \textsf{if}\ e_1\ e_2\ e_3\Rightarrow v}
\]

If the condition of an expression is true, the true branch is evaluated, but the false branch is not. Otherwise, the false branch is evaluated, but the true branch is not.

The following encode true and false:

\[
\begin{array}{rcl}
\mathit{encode}(true)&=&\lambda a.\lambda b.a\ \_ \\
\mathit{encode}(false)&=&\lambda a.\lambda b.b\ \_ \\
\end{array}
\]

The underscore denotes an arbitrary expression. It implies that the value of an argument is needless. It is possible to consider the underscore as any expression, such as \(\lambda x.x\).

As the encoding of sums and products helps to understand the encoding of natural number, the encoding of conditional expressions helps to understand the encoding of true and false.

\[
\begin{array}{rcl}
\mathit{encode}(\textsf{if}\ e_1\ e_2\ e_3)&=&
\mathit{encode}(e_1)\ (\lambda\_ .\mathit{encode}(e_2))\ (\lambda\_ .\mathit{encode}(e_3))
\end{array}
\]

The underscore implies that a body does not refer to a parameter. Any parameter name, such as \(x\), can replace the underscore. Assume that \(e_1\) denotes true. The whole expression equals \( (\lambda a.\lambda b.a \_ )\ (\lambda\_ .\mathit{encode}(e_2))\ (\lambda\_ .\mathit{encode}(e_3))\), which is \( (\lambda\_ .\mathit{encode}(e_2))\ \_ \). It denotes the same value as \(\mathit{encode}(e_2)\) does. The evaluation of \(\mathit{encode}(e_3)\) is unnecessary. If \(e_1\) denotes false, the expression denotes the same value as \(\mathit{encode}(e_3)\) does, and the evaluation of \(\mathit{encode}(e_2)\) is unnecessary.

Consider the following encoding:

\[
\begin{array}{rcl}
\mathit{encode}(true)&=&\lambda a.\lambda b.a \\
\mathit{encode}(false)&=&\lambda a.\lambda b.b \\
\textsf{if}&\equiv&\lambda c.\lambda a.\lambda b.c\ a\ b \\
\mathit{encode}(\textsf{if}\ e_1\ e_2\ e_3)&=&\mathit{encode}(e_1)\ \mathit{encode}(e_2)\ \mathit{encode}(e_3)
\end{array}
\]

It is simpler than the previous encoding but always evaluates both true and false branches. Lambda calculus defined by the article uses eager evaluation, but lambda calculus with lazy evaluation allows using the latter encoding without computing needlessly. Later articles discuss lazy evaluation.

## Expressivity

How expressive is lambda calculus? *Lambda computable* functions are functions encodable with lambda calculus. Similarly, *Turing computable* functions are functions implementable with *Turing machines*. Lambda computable functions are Turing computable; Turing computable functions are lambda computable. The set of every lambda computable function equals the set of every Turing computable function; lambda calculus is *Turing complete*. Computation doable with Turing machines almost equals that with real computers. The only difference is that the tapes of Turing machines are infinite, while the memories of computers are finite. Therefore, lambda calculus expresses everything whom computers compute. Lambda calculus is 'the only' programming language.

## Acknowledgments

I thank professor Ryu for giving feedback on the article.
