Syntax and semantics define a programming language. Syntax determines whether code is code written in the language. Semantics decides what the code denotes. Without semantics, code written by programmers is no more than a string.

The shape of semantics depends on the property of interest. If the property is about how programs modify memories of computers, defining semantics is defining what a memory is and how code modifies a memory. If the property is about input and output of programs, defining semantics is defining what input and output are and what code prints for input. The course focuses on functional languages, and the semantics of a functional language determines a value obtained by interpreting an expression.

## Defining Semantics

There are various styles of semantics: denotational semantics and operational semantics are famous; axiomatic semantics and others exist. Denotational semantics define values denoted by programs with mathematical methods. For example, denotational semantics of an imperative language views a program as a function from states to states. On the other hand, operational semantics expresses executions of programs with logical statements, such as inference rules. Operational semantics is more similar to the implementation of an interpreter than denotational semantics but does differ from an implementation.

There are multiple forms of operational semantics: natural semantics, structural operational semantics (SOS), reduction semantics, abstract machine semantics, and others. The course mainly deals with natural semantics, as known as big-step semantics. Natural semantics is composed of one or more inference rules. A rule defines a value denoted by an expression. In contrast, small-step semantics, including SOS and reduction semantics, uses inference rules that transform an expression into an expression instead of a value. Big-step semantics produces a value at one big step, while small-step semantics requires multiple small steps to attain a value.

Each kind of semantics has its characteristic. Different types of research need different types of semantics. For example, defining both concrete and abstract syntax in a denotational style allows expressing a relationship between them mathematically and showing the soundness of abstract interpretations. Axiomatic semantics fits verifying the correctness of programs, and proving the type-soundness of languages is a typical usage of reduction semantics. Natural semantics intuitively defines languages and is closest to the implementation of an interpreter.

The above explanation contains words not introduced by the course. It is enough to understand that various ways to define semantics exist and choosing a proper style for a subject is crucial.

## Natural Semantics

The last article defined the abstract syntax of AE.

\[
\begin{array}{lrcl}
\text{Integer} & n & \in & \mathbb{Z} \\
\text{Expression} & e & ::= & n \\
&& | & \{+\ e\ e\} \\
&& | & \{-\ e\ e\} \\
\end{array}
\]

Metavariable \(n\) ranges over integers; metavariable \(e\) ranges over expressions; \(\text{Expression}\) is the set of every expression.

This article defines the natural semantics of AE.

The semantics of AE decides values denoted by expressions of AE. The first thing to do is defining what values are. Every arithmetic expression denotes an integer so that every value of AE is an integer.

\[
\begin{array}{lrcl}
\text{Value} & v & ::= & n
\end{array}
\]

Metavariable \(v\) ranges over values; \(\text{Value}\) is the set of every value; it equals \(\mathbb{Z}\).

Every expression of AE denotes a value of AE. The semantics of AE seems to be a function from expressions to values. Let \(\Rightarrow\) be the function.

\[\Rightarrow:\ \text{Expression}\rightarrow\text{Value}\]

However, in general, not every expression of a language denotes a value. As an execution might terminate without yielding a result due to an error, expressions not denoting any values exist. Besides, if some expressions produce random values, a single expression may denote multiple values. Therefore, it is desirable to define semantics as a binary relation over \(\text{Expression}\) and \(\text{Value}\).

\[\Rightarrow\subseteq\text{Expression}\times\text{Value}\]

For any expression \(e\) and any value \(v\), \((e,v)\in\Rightarrow\) implies that \(e\) denotes \(v\), or \(v\) is the result of evaluating \(e\). In PL research, notation \(\vdash e\Rightarrow v\) replaces \((e,v)\in\Rightarrow\). \(\Rightarrow\) is a relation and thus does not imply input and output, but, intuitively, expressions are input and values are output.

Inference rules define the semantics of AE.

\[
\vdash n\Rightarrow n
\]

If an expression is an integer, the expression denotes the integer. The rule does not have any premises. It has the following mathematical meaning:

\[ \forall n\in\mathbb{Z}.\vdash n\Rightarrow n \]

Intuitively, \(n\) does not require any computation, and the result is \(n\).

\[
\frac
{ \vdash e_1\Rightarrow n_1\quad\vdash e_2\Rightarrow n_2 }
{ \vdash \{+\ e_1\ e_2\}\Rightarrow n_1+n_2 }
\]

If an expression is the sum of two expressions, the expression denotes the sum of two integers denoted by the two expressions. The rule has the following mathematical meaning:

\[
\begin{array}{l}
\forall e_1\in\text{Expression}.
\forall e_2\in\text{Expression}.
\forall n_1\in\mathbb{Z}.
\forall n_2\in\mathbb{Z}.\\
(\vdash e_1\Rightarrow n_1)\rightarrow
(\vdash e_2\Rightarrow n_2)\rightarrow
(\vdash \{+\ e_1\ e_2\}\Rightarrow n_1+n_2)
\end{array}
\]

Intuitively, computing \(\{+\ e_1\ e_2\}\) requires computing \(e_1\) and \(e_2\), and since \(e_1\) and \(e_2\) respectively result in \(n_1\) and \(n_2\), the result is \(n_1+n_2\). \(e_1\) and \(e_2\) are given; intermediate computation yields \(n_1\) and \(n_2\); the final result is \(n_1+n_2\).

\[
\frac
{ \vdash e_1\Rightarrow n_1\quad\vdash e_2\Rightarrow n_2 }
{ \vdash \{-\ e_1\ e_2\}\Rightarrow n_1-n_2 }
\]

If an expression is the difference of two expressions, the expression denotes the difference of two integers denoted by the two expressions. The rule has the following mathematical meaning:

\[
\begin{array}{l}
\forall e_1\in\text{Expression}.
\forall e_2\in\text{Expression}.
\forall n_1\in\mathbb{Z}.
\forall n_2\in\mathbb{Z}.\\
(\vdash e_1\Rightarrow n_1)\rightarrow
(\vdash e_2\Rightarrow n_2)\rightarrow
(\vdash \{-\ e_1\ e_2\}\Rightarrow n_1-n_2)
\end{array}
\]

Intuitively, computing \(\{-\ e_1\ e_2\}\) requires computing \(e_1\) and \(e_2\), and since \(e_1\) and \(e_2\) respectively result in \(n_1\) and \(n_2\), the result is \(n_1-n_2\). \(e_1\) and \(e_2\) are given; intermediate computation yields \(n_1\) and \(n_2\); the final result is \(n_1-n_2\).

The article keeps emphasizing that both understanding mathematical definitions and interpreting the semantics intuitively are essential. In a mathematical sense, the natural semantics of AE is a relation over \(\text{Expression}\) and \(\text{Value}\), and the inference rules do not care what given things are and what obtained things are. In contrast, intuitively, the natural semantics find a value denoted by a given expression. An expression inside the conclusion of a rule is input; the premises of a rule represent required computation; a value inside the conclusion is output. Not considering mathematical definitions, one may make a mistake while strictly thinking and hardly understand complicated semantics. Complex semantics needs rules not interpreted with the concepts of input, computation, and output; for instance, computation uses output. It intuitively seems odd but is natural in a mathematical sense, which does not have such concepts. On the other hand, not interpreting semantics intuitively, one hardly understands a language. In conclusion, both viewpoints are crucial.

The following rules are all of the natural semantics of AE:

\[
\vdash n\Rightarrow n
\]

\[
\frac
{ \vdash e_1\Rightarrow n_1\quad\vdash e_2\Rightarrow n_2 }
{ \vdash \{+\ e_1\ e_2\}\Rightarrow n_1+n_2 }
\]

\[
\frac
{ \vdash e_1\Rightarrow n_1\quad\vdash e_2\Rightarrow n_2 }
{ \vdash \{-\ e_1\ e_2\}\Rightarrow n_1-n_2 }
\]

### Drawing Proof Trees

The following proof tree proves that \(\{+\ 4\ \{-\ 2 \ 1\}\}\) denotes \(5\):

\[
\frac
{
  {\large
  \vdash 4\Rightarrow 4 \quad
  \frac
  {\vdash 2\Rightarrow 2 \quad \vdash 1\Rightarrow 1}
  {\vdash \{-\ 2\ 1\}\Rightarrow 1}
  }
}
{\vdash\{+\ 4\ \{-2 \ 1\}\}\Rightarrow 5}
\]

Drawing proof trees is not an interesting research topic. However, it is a good practice to understand semantics, and some students feel difficult about it. The article thus briefly introduces a strategy to draw proof trees.

Languages defined by the course have simple semantics. Usually, only a single inference rule fits a given expression. The meanings of propositions are unimportant, and substituting metavariables with appropriate expressions is enough to draw proof trees. Drawing proof trees is often mechanical.

The remaining part of the section draws a proof tree proving that \(\{+\ 4\ \{-\ 2 \ 1\}\}\) denotes \(5\) step by step. Firstly, an expression inside a conclusion is \(\{+\ 4\ \{-\ 2 \ 1\}\}\).

\[
\color{red}{
\frac
{
  \color{white}{{\large
  \vdash 4\Rightarrow 4 \quad
  \frac
  {\vdash 2\Rightarrow 2 \quad \vdash 1\Rightarrow 1}
  {\vdash \{-\ 2\ 1\}\Rightarrow 1}
  }}
}
{\vdash\{+\ 4\ \{-2 \ 1\}\}\Rightarrow \color{white}{5}}
}
\]

A single rule fits \(\{+\ 4\ \{-\ 2 \ 1\}\}\). Substitute \(e_1\) and \(e_2\) with \(4\) and \(\{-\ 2\ 1\}\) respectively to make premises. Do not write values of the premises.

\[
\frac
{
  {\large
  \color{red}{\vdash 4\Rightarrow {\color{white}4} \quad
  \frac
  {\color{white}{\vdash 2\Rightarrow 2 \quad \vdash 1\Rightarrow 1}}
  {\vdash \{-\ 2\ 1\}\Rightarrow \color{white}{1}}
  }}
}
{\vdash\{+\ 4\ \{-2 \ 1\}\}\Rightarrow \color{white}{5}}
\]

A single rule fits \(4\). The rule has no premises. Substitute \(n\) with \(4\) and write the value.

\[
\frac
{
  {\large
  \vdash 4\Rightarrow \color{red}{4} \quad
  \frac
  {\color{white}{\vdash 2\Rightarrow 2 \quad \vdash 1\Rightarrow 1}}
  {\vdash \{-\ 2\ 1\}\Rightarrow \color{white}{1}}
  }
}
{\vdash\{+\ 4\ \{-2 \ 1\}\}\Rightarrow \color{white}{5}}
\]

A single rule fits \(\{-\ 2\ 1\}\). Substitute \(e_1\) and \(e_2\) with \(2\) and \(1\) respectively to make premises. Do not write values of the premises.

\[
\frac
{
  {\large
  \vdash 4\Rightarrow 4 \quad
  \frac
  {\color{red}{\vdash 2\Rightarrow \color{white}{2} \quad \vdash 1\Rightarrow \color{white}{1}}}
  {\vdash \{-\ 2\ 1\}\Rightarrow \color{white}{1}}
  }
}
{\vdash\{+\ 4\ \{-2 \ 1\}\}\Rightarrow \color{white}{5}}
\]

A single rule fits \(2\). The rule has no premises. Substitute \(n\) with \(2\) and write the value.

\[
\frac
{
  {\large
  \vdash 4\Rightarrow 4 \quad
  \frac
  {\vdash 2\Rightarrow \color{red}{2} \quad \vdash 1\Rightarrow \color{white}{1}}
  {\vdash \{-\ 2\ 1\}\Rightarrow \color{white}{1}}
  }
}
{\vdash\{+\ 4\ \{-2 \ 1\}\}\Rightarrow \color{white}{5}}
\]

A single rule fits \(1\). The rule has no premises. Substitute \(n\) with \(1\) and write the value.

\[
\frac
{
  {\large
  \vdash 4\Rightarrow 4 \quad
  \frac
  {\vdash 2\Rightarrow 2 \quad \vdash 1\Rightarrow \color{red}{1}}
  {\vdash \{-\ 2\ 1\}\Rightarrow \color{white}{1}}
  }
}
{\vdash\{+\ 4\ \{-2 \ 1\}\}\Rightarrow \color{white}{5}}
\]

Compute \(2-1\) and write \(1\), the result of \(\{-\ 2\ 1\}\).

\[
\frac
{
  {\large
  \vdash 4\Rightarrow 4 \quad
  \frac
  {\vdash 2\Rightarrow 2 \quad \vdash 1\Rightarrow 1}
  {\vdash \{-\ 2\ 1\}\Rightarrow \color{red}{1}}
  }
}
{\vdash\{+\ 4\ \{-2 \ 1\}\}\Rightarrow \color{white}{5}}
\]

Compute \(4+1\) and write \(5\), the result of \(\{+\ 4\ \{-\ 2 \ 1\}\}\).

\[
\frac
{
  {\large
  \vdash 4\Rightarrow 4 \quad
  \frac
  {\vdash 2\Rightarrow 2 \quad \vdash 1\Rightarrow 1}
  {\vdash \{-\ 2\ 1\}\Rightarrow 1}
  }
}
{\vdash\{+\ 4\ \{-2 \ 1\}\}\Rightarrow \color{red}{5}}
\]

The tree is complete.

### Implementing an Interpreter

The following Scala code is the implementation of an interpreter following the natural semantics of AE:

```scala
sealed trait AE
case class Num(n: Int) extends AE
case class Add(l: AE, r: AE) extends AE
case class Sub(l: AE, r: AE) extends AE

def interp(e: AE): Int = e match {
  case Num(n) => n
  case Add(l, r) => interp(l) + interp(r)
  case Sub(l, r) => interp(l) - interp(r)
}

interp(Add(Num(4), Sub(Num(2), Num(1))))  // 5
```

## Acknowledgments

I thank professor Ryu for giving feedback on the article.
