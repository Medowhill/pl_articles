## Defining Programming Languages

The course defines programming languages. Defining a language is defining the *syntax* and the *semantics* of the language. The article is about syntax. Before going into detail about syntax, it firstly explains why defining a language is essential.

Languages defined by the course are tiny and whom people do not use in practice. For example, they cannot get input from users or print results; they do not have typical types, including a string type and a floating-point number type. It seems meaningless to define languages that do not have any usages. Defining such tiny languages does not aim to make the course easy for undergraduate students. Surprisingly, many kinds of PL research deal with small unused languages.

PL research often aims to prove that a language satisfies a specific property. The language might be a language used by plenty of people at the moment or an improved, unimplemented version of an existing language with new features. The sentence uses the term 'property' in a broad sense: it refers to a property derived from the definition of the language; it refers to the characteristics of results obtained by applying a specific algorithm to code written in the language.

Researchers define small languages because real-world languages are complicated to be the subjects of research. The real-world languages have many features helping programmers, such as syntactic sugar. Verifying a property of a language containing all such features takes a long time. If all the features affected the property, they sadly would have to deal with a language with all the features. However, most features are not related to the property, whom the researchers want to show. It is efficient to work on a small language containing only important characteristics by identifying features influencing the property.

Besides, it is hard to apply research on a specific existing language to other languages. If researchers proved a property while reflecting all the features of the language, they would not be able to conclude that other languages with a portion of the features satisfy the property. In contrast, if they research a small language containing features affecting the property, they will be able to apply the result to such other languages without considerable cost.

Research on Scala is a concrete example. Scala features objects with *type members*---ignore what it is. Popular languages preceding Scala had not featured them. It had not been sure whether type systems with objects with type members are *type-sound*---ignore what type soundness is. Researchers had defined DOT (dependent object type), which is a small language with objects with type members, and proved the type-soundness of DOT. If they had tried to prove the type-soundness of Scala, they would have spent decades and exerted themselves for features orthogonal to objects with type members. However, not spending much time, they had proved the type-soundness of DOT and could apply the result to languages sharing the feature, such as Wyvern. Alas, even though DOT models the feature precisely, the type-soundness of DOT does not imply the type-soundness of Scala. Nonetheless, proving the safety of the core of Scala is crucial for those who want to trust Scala. As features other than objects with type members of Scala have been already verified with other researches, verifying only DOT is quite enough.

In summary, the following is a typical flow of PL research.

1. Want to prove that feature `B` of language `A` satisfies property `C`.
2. Define small language `a` representing `B`.
3. Define property `c` for `a` as `C` for `A`.
4. Prove that `a` satisfies `c`.
5. `A` probably satisfies `C`, and other languages which feature `B` may satisfy `C`.

Mind that numerous sorts of PL research do not follow the flow. PL researchers make, prove, and verify real-world languages, programs, and systems. They invent tools for practical usages. For instance, Infer of Facebook is a static analyzer developed by PL researchers. Companies including Facebook and Amazon have been using Infer.

Such practical research cannot exist without a theoretical background for core properties and algorithms produced by research dealing with small languages. [The foundations of Infer](https://fbinfer.com/docs/separation-logic-and-bi-abduction.html) are theories suggested by a few papers that are not on real-world languages. Since the objects of the papers are small but general, Infer can analyze Java, C, C++, and Objective-C rather than a single language.

The most crucial thing of PL research is to define and solve a small precise problem expressing a problem of interest. So does the course. The course focuses on essential features provided by most languages and defines tiny languages representing the features. The course is a starting point of PL research. At the same time, the course gives students who are not interested in PL basic knowledge to understand and to use new languages.

## Syntax

The syntax of a language determines whether code is correct code written in the language.

```scala
class A
```

```scala
class A {
```

The former is code written in Scala, but the latter is not. The syntax of Scala determines it.

In a mathematical sense, assume that the set of all possible code exists; the set of all correct code written in language A is a subset of the former set. The syntax of A defines the subset.

Syntax is either concrete or abstract. Despite the lack of strict definitions of concrete and abstract syntax, they have distinct properties and are thus easily distinguished. The course explains them briefly: concrete syntax is for people; abstract syntax is for computers. The explanation intuitively shows what they are.

### Concrete Syntax

Existing for humans, *concrete syntax* deals with code written by people. It defines a rule for strings and cares about all the characters including whitespaces and newlines; it specifies rules like "two quotation marks are at the start and the end of a string," "two consecutive backslashes indicate the start of a comment," and "every operator is an infix operator." The specifications of most languages describe the concrete syntax of the languages since programmers write code according to the specifications.

*Backus-Naur form* (BNF) is the most popular way to describe syntax. A form includes one or more rules. Each rule is in the form of `<symbol> ::= expression | expression …`. A symbol between angle brackets is a *metavariable*, which denotes a set of strings. An expression is an enumeration of metavariables and strings. A set denoted by the metavariable includes strings obtained by substituting metavariables with elements of the metavariables in one of the expressions. Every string starts and ends with quotation marks.

The article defines the syntax of AE, a language for arithmetic expressions.

An expression of AE is

* an integer,
* the sum of two expressions, or
* the difference of two expressions.

The following is the concrete syntax of AE in the BNF:

\[
\begin{array}{l}
\texttt{digit ::= "0" | "1" | "2" | "3" | "4"} \\
{\tt\ \ \ \ \ \ \ \ }\texttt{| "5" | "6" | "7" | "8" | "9"} \\
\texttt{nat}{\tt\ \ \ }\texttt{::= digit | digit nat} \\
\texttt{num}{\tt\ \ \ }\texttt{::= nat | "-" nat} \\
\texttt{expr}{\tt\ \ }\texttt{::= num} \\
{\tt\ \ \ \ \ \ \ \ }\texttt{| "(" expr "+" expr ")"} \\
{\tt\ \ \ \ \ \ \ \ }\texttt{| "(" expr "-" expr ")"} \\
\end{array}
\]

The remaining part of the section shows how to interpret syntax in the BNF. \(Digit\) is a set denoted by \(\tt digit\); \(Nat\) is a set denoted by \(\tt nat\); \(Num\) is a set denoted by \(\tt num \); \(Expr\) is a set denoted by \(\tt expr\).

\(Digit\) equals , a set of the digits of decimals.

\(Nat\) is the smallest set satisfying the following two conditions; it denotes the set of every natural number. The \(\cdot\) operator denotes string concatenation.

1. \(\forall d\in Digit.d\in Nat\)
2. \(\forall d\in Digit.\forall n\in Nat.d \cdot n\in Nat\)

\(Num\) is the smallest set satisfying the following two conditions; it denotes the set of every integer.

1. \(\forall n\in Nat.n\in Num\)
2. \(\forall n\in Nat.\texttt{"-"}\cdot n\in Num\)

\(Expr\) is the smallest set satisfying the following three conditions; it denotes the set of every arithmetic expression.

1. \(\forall n\in Num.n\in Expr\)
2. \(\forall e_1\in Expr.\forall e_2\in Expr.{\tt"("}\cdot e_1\cdot{\tt"+"}\cdot e_2\cdot{\tt")"}\in Expr\)
3. \(\forall e_1\in Expr.\forall e_2\in Expr.{\tt"("}\cdot e_1\cdot\texttt{"-"}\cdot e_2\cdot{\tt")"}\in Expr\)

\(\tt"(1+2)"\) is an element of \(Expr\), but \(\tt"1+2"\) is not an element
of \(Expr\) due to the lack of parentheses.

### Abstract Syntax

Most kinds of PL research define languages with *abstract syntax* instead of concrete syntax, which is unnecessarily precise. As the previous section shows, concrete syntax cares unimportant details.

Abstract syntax is an abstract data structure describing code. Unlike concrete syntax, which deals with strings, it deals with abstract objects. Since people mostly use strings to represent information, strings often describe abstract syntax. However, the essence of abstract syntax is not about strings. For example, strings "1" and "one" represent the number one, but the essence of the number one is that it is the successor of zero but not how people write it on papers. In the same manner, regardless of a way of describing abstract syntax, abstract deals with abstract objects but not strings.

The following is the abstract syntax of AE in the BNF:

\[
\begin{array}{rcl}
n & \in & \mathbb{Z} \\
e & ::= & n \\
& | & e+e \\
& | & e-e \\
\end{array}
\]

Metavariable \(n\) ranges over integers; metavariable \(e\) ranges over expressions.

Like concrete syntax, the abstract syntax in the BNF defines a set. Let \(\mathcal{A}\) is a set denoted by \(e\). \(\mathcal{A}\) is the smallest set satisfying the following three conditions.

1. \(\forall n\in\mathbb{Z}.n\in \mathcal{A}\)
2. \(\forall e_1\in\mathcal{A}.\forall e_2\in\mathcal{A}.e_1+e_2\in\mathcal{A}\)
3. \(\forall e_1\in\mathcal{A}.\forall e_2\in\mathcal{A}.e_1-e_2\in\mathcal{A}\)

*Inference rules* can define abstract syntax as well. Inference rules typically define the semantics of languages, but the article defines abstract syntax with inference rules to make readers familiar with inference rules. It is possible to define concrete syntax with inference rules, but I think that it is redundant and unnecessary.

Inference rules derive a *proposition* from propositions. An inference rule is composed of a horizontal line, zero or more propositions above the line, and a proposition below the line. If no proposition exists above the line, then the line can be omitted. The propositions above the line are premises; the proposition below the line is a conclusion. Every proposition in the rule may have metavariables.

For instance, an inference rule can encode *modus ponens*, which implies that for any propositions \(P\) and \(Q\), if \(P\rightarrow Q\) and \(P\), then \(Q\). Let metavariables \(p\) and \(q\) range over propositions.

\[
\frac
{ p\rightarrow q \quad p }
{ q }
\]

If substituting every metavariable with an element of the metavariable in a rule makes every premise of the rule true, then the conclusion of the rule also is true. Assume that \(P\) and \(Q\) are propositions, and both \(Q\rightarrow P\) and \(Q\) are true. Substituting \(p\) and \(q\) with \(Q\) and \(P\) results in two true premises and conclusion \(P\). The following *proof tree* is a proof of \(P\):

\[
\frac
{ Q\rightarrow P \quad Q }
{ P }
\]

One can use inference rules multiple times to prove a proposition. Assume that \(P\), \(Q\), and \(R\) are propositions, and \(P\rightarrow(Q\rightarrow R)\), \(P\), and \(Q\) are true. Substituting \(p\) and \(q\) with \(P\) and \(Q\rightarrow R\) yields that \(Q\rightarrow R\) is true. Substituting \(p\) and \(q\) with \(Q\) and \(R\) finally proves \(R\). The following proof tree gives a proof:

\[
\frac
{ 
{\Large\frac
  {\large P\rightarrow(Q\rightarrow R) \quad P }
  { Q\rightarrow R } }\quad
  Q }
{ R }
\]

The following inference rules define the abstract syntax of AE:

\[
\frac
{ n\in\mathbb{Z} }
{ n\in\mathcal{A} }
\quad
\frac
{ e_1\in\mathcal{A} \quad e_2\in\mathcal{A} }
{ e_1+e_2\in\mathcal{A} }
\quad
\frac
{ e_1\in\mathcal{A} \quad e_2\in\mathcal{A} }
{ e_1-e_2\in\mathcal{A} }
\]

The following proof tree proves that \(4+(2-1)\) is an element of \(\mathcal{A}\).
Note that we can use parentheses to resolve ambiguity in abstract syntax since
it defines mathematical notation.

\[
\frac
{ 
{\Large
\frac
  { 4\in\mathbb{Z} }
  { 4\in\mathcal{A} } \quad
  \frac
  { \frac
    { 2\in\mathbb{Z} }
    { 2\in\mathcal{A} }
    \frac
    { 1\in\mathbb{Z} }
    { 1\in\mathcal{A} }
  }
  { (2-1)\in\mathcal{A} }
}
}
{ 4+(2-1)\in\mathcal{A} }
\]

Scala code also can represent the abstract syntax of AE. It is a typical ADT; a sealed trait and case classes define it:

```scala
sealed trait Expr
case class Num(n: Int) extends Expr
case class Add(l: Expr, r: Expr) extends Expr
case class Sub(l: Expr, r: Expr) extends Expr
```

The following Scala code represents \(4+(2-1)\):

```scala
Add(Num(4), Sub(Num(2), Num(1)))
```

Most sorts of abstract syntax define tree shapes. Trees following abstract syntax are *abstract syntax trees* (ASTs). The below tree visualizes \(4+(2-1)\). The structure of an object defined by the above Scala code equals the tree.

<div class="chart" id="tree-ae-0"></div>
<script src="https://cdnjs.cloudflare.com/ajax/libs/treant-js/1.0/Treant.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/raphael/2.2.8/raphael.js"></script>
<script src="./7_0_treant.js"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/treant-js/1.0/Treant.css">
<link rel="stylesheet" href="./7_0_treant.css">
<script>new Treant( simple_chart_config );</script>

### Parsing

*Parsing* is a process that transforms strings following concrete syntax into ASTs and rejects strings not following the concrete syntax. A *parser* is a parsing program. Parsing is out of the scope of the course and thus is out of the scope of the article.

The Scala standard library provides *parser combinators*. Programmers can implement parsers without detailed knowledge about parsing. The below code implements a parser of AE. The parser takes a string as input and produces an AST of AE; it throws an exception if the string does not follow the concrete syntax of AE. Note that strings may contain whitespaces freely, while concrete syntax defined by the article is tight with whitespaces.

```scala
import scala.util.parsing.combinator._

object Expr extends RegexParsers {
  def wrap[T](e: Parser[T]): Parser[T] = "(" ~> e <~ ")"
  lazy val n: Parser[Int] = "-?\\d+".r ^^ (_.toInt)
  lazy val e: Parser[Expr] =
    n                    ^^ Num                         |
    wrap((e <~ "+") ~ e) ^^ { case l ~ r => Add(l, r) } |
    wrap((e <~ "-") ~ e) ^^ { case l ~ r => Sub(l, r) }

  def parse(s: String): Expr =
    parseAll(e, s).getOrElse(throw new Exception)
}

Expr.parse("1") 
// Num(1)

Expr.parse("(4 + (2 - 1))")
// Add(Num(4),Sub(Num(2),Num(1)))

Expr.parse("1 + 2")
// java.lang.Exception
```

## Acknowledgments

I thank professor Ryu for giving feedback on the article.
