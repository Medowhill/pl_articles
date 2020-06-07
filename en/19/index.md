The article deals with *type systems*. It explains the purposes and necessity of type systems. Then, it defines TFAE by revising FAE and the type system of TFAE. It compares FAE and TFAE as well. Finally, it shows possible extensions of TFAE.

## Type Errors

There are three sorts of FAE expressions. Small-step semantics is ideal to discuss the sorts. The article uses different small-step semantics from the previous articles. This semantics reduces an expression instead of computation and value stacks. However, the article does not aim defining the reduction of an expression. The following explanations are understandable without the definition of reduction.

The first sort includes expressions resulting in values.

\[
\begin{array}{rl}
& (1+2)-3 \\
\rightarrow & 3-3 \\
\rightarrow & 0 \\
\\
& (\lambda x.\lambda y.x+y)\ 1\ 2 \\
\rightarrow & (\lambda y.1+y)\ 2 \\
\rightarrow & 1+2 \\
\rightarrow & 3
\end{array}
\]

They correspond to programs terminating without any errors. The first sort is most common.

The second sort includes expressions being reduced forever.

\[
\begin{array}{rl}
& (\lambda x.x\ x)\ (\lambda x.x\ x) \\
\rightarrow & (\lambda x.x\ x)\ (\lambda x.x\ x) \\
\rightarrow & (\lambda x.x\ x)\ (\lambda x.x\ x) \\
\rightarrow & \cdots
\end{array}
\]

\((\lambda x.x\ x)\ (\lambda x.x\ x)\) is a function application, but not a value. Reducing the expression yields the same expression. Repeated reduction never reaches a value. Such expressions correspond to programs not terminating. Their executions never finish. Sometimes, programmers intend nontermination. Consider operating systems, servers, and shells as examples. They must terminate for certain input but must not for other input. In other cases, programmers' mistakes lead to nontermination. Wrong use of loops or recursive functions makes programs execute forever even though the programmers did not mean nontermination.

The third sort includes expressions being stuck at some point before reaching values.

\[
\begin{array}{rl}
& (\lambda x.x+1)\ (\lambda x.x) \\
\rightarrow & (\lambda x.x)+1 \\
\\
& (\lambda x.x\ 1)\ 1 \\
\rightarrow & 1\ 1 \\
\\
& (\lambda x.y)\ 1 \\
\rightarrow & y \\
\end{array}
\]

\((\lambda x.x)+1\) adds a function to an integer. Since such an addition is impossible, no corresponding reduction rule exists. Reduction stops before the expression reaches a value. \(1\ 1\) applies an integer to a value. A function can be applied to a value, but an intger cannot. No more reduction is possible as well. \(y\) is a free variable. Its value is unknown so that reduction cannot happen. These expressions correspond to programs causing run-time errors. Errors at run time terminate programs abnormally. Run-time errors differ from exceptions whom programmers intend. Programmers hardly will run-time errors. They exist because of mistakes of programmers.

Several kinds of run-time errors exist. A type error is one kind. The article focuses only on type errors. Defining types precedes defining type errors. Types categorize values. For example, \(1\), \(42\), \(0\), and \(-1\) are integers and belong to the integer type. \(\lambda x.x\), \(\lambda x.x+x\), and \(\lambda x.x\ 1\) are functions and belong to the function type.

A value of an unexpected type causes a type error. Consider \((\lambda x.x)+1\). The first operand belongs the function type, and the second operand belongs the integer type. Addition expects both operands to belong to the integer type. The expression results in a type error because a value of the function type appears where a value of the integer type is expected. Consider \(1\ 1\). Both operands belong to the integer type. Function application expects the first operand to belong to the function type. The expression results in a type error because a value of the integer type appears where a value of the integer type is expected.

Run-time errors are more than type errors. The problem of expression \((\lambda x.y)\ 1\) is free variable \(y\). It is not a type error; a *free identifier error* is a common name for such an error. FAE expressions can make only type errors and free identifier errors due to the simplicity of the language. Real-world languages provide various features whom FAE excludes. A program written in C can result in a *segmentation fault*. A wrong memory access causes the error. Many languages, including Java, feature arrays and checks access to arrays at run time. An invalid index for an array leads to an error, such as `ArrayIndexOutOfBoundsException` of Java. Some languages allow programmers to overload methods. Sometimes, a method invocation is *ambiguous*, and a method to resolve the invocation thus cannot be determined at run time. Many other run-time errors exist.

Although real-world languages define various run-time errors, the main target of the article if FAE. A type error or a free identifier error is the only run-time error whom an FAE expression can result in. Besides, most algorithms finding a type error in an expression find a free identifier error at the same time. Therefore, the article considers free identifier errors as a portion of type errors. Every error caused by evaluation of an FAE expression is a type error. From this point, the article ignores run-time errors other than type errors. One cannot ignore other kinds of run-time errors to deal with real-world languages. However, discussions in the article can be applied to real-world languages. The main reason is that type errors take a big portion of run-time errors. 
Extensions of type systems is another reason. Extending a type system makes more run-time errors be type errors.

## Type Systems

Programmers hardly intend type errors. Type errors make programs terminate unexpectedly during execution. Programmers want to avoid such terminations. Abnormal terminations of commercial software are unpleasant experiences for users and harm the profits and reputations of the developers of the software. Such problems in utility programs for common people can damage developers only monetarily. However, these days, people use programs for lots of purposes. Programs control cars, airplanes, and medical devices. Wrong operations of such devices may kill or hurt people. A device will operate in a weird way if a program controlling it terminates suddenly. Programmers surely need a way to check the existence of unintended type errors before they deploy programs.

The simplest way to find a type error is executing a program. It is a program testing. If the execution finishes due to a type error, the program needs revision. Otherwise, the program is usable without any problems. However, the strategy often fails. The execution can take a long time or run forever. Programmers want to deploy the program. They cannot wait the test forever. The test must stop at some point. It makes complete exclusion of type errors in the program impossible. Even though the program runs one hundred hours without any type errors, it can terminate because of a type error after one more hour. Moreover, most programs have infinitely many possibilities of input. Testing cannot cover all the possibilities. Even if the test succeeds for every input, the program can result in a type error for some other input. Testing is a simple and popular way to find type errors but cannot guarantee the nonexistence of type errors. Programs that can cause serious problems when type errors happen require a better way to check type errors than testing.

Executing programs is not enough. The only solution is preventing type errors without executing programs. Before execution, some program has to check automatically whether a program contains a type error. If it contains a type error, programmers need to revise the program. Otherwise, the program is perfectly safe.

A system checking whether a program written in a particular language can result in a type error is a type system. *Static semantics* is another name of a type system. The term 'static' means not executing a program. A type system is static since it finds type errors without execution. The opposite of the term 'static' is the term 'dynamic,' which means executing a program. Hitherto semantics, which define how programs behave during execution, is *dynamic semantics*. The terms 'static semantics' and 'type system' are interchangeable when the subject is a language. On the other hand, if a single expression is the subject, the term 'static semantics' fits better than the term 'type system.'

A program implementing a type system is a type checker. Recall what an interpreter is. It is a program executing a given program according to the dynamic semantics of a language. An interpreter is to dynamic semantics what a type checker is to static semantics. A type checker inspect a given program without execution to find type errors of the program according to the static semantics of a language.

Type checkers aim to find programs making type errors happen. Let program \(P\) and input \(I\) for the program be input for a type checker. \(P(I)\) denote the execution of \(P\) for \(I\). If the type checker decides \(P(I)\) never result in a type error, it returns \(\textsf{OK}\). Otherwise, the result if \(\textsf{NOT OK}\). The type checker must satisfy the following three properties: it terminates in a finite time; it never terminates due to a run-time error; its result is either \(\textsf{OK}\) or \(\textsf{NOT OK}\).

*Completeness* and *soundness* are other attractive properties for type checkers. A complete type checker returns \(\textsf{OK}\) for every pair \((P,I)\) that is free from type errors. If the type checker returns \(\textsf{NOT OK}\) for some \((P,I)\), then \(P(I)\) always raises a type error. Let \(check\) be the type checker. \(check(P,I)\) denotes the inspection of \(P(I)\).

\[
\begin{array}{l}
\forall P.\forall I.(P(I)\text{ does not result in a type error})\rightarrow(\mathit{check}(P,I)=\textsf{OK}) \\
\forall P.\forall I.(\mathit{check}(P,I)=\textsf{NOT OK})\rightarrow(P(I)\text{ results in a type error})
\end{array}
\]

The former is the contrapositive of the latter.

On the other hand, a sound type checker returns \(\textsf{NOT OK}\) for every pair \((P,I)\) that causes a type error. If the type checker returns \(\textsf{OK}\) for \((P,I)\), then \(P(I)\) never results in a type error.

\[
\begin{array}{l}
\forall P.\forall I.(P(I)\text{ results in a type error})\rightarrow(\mathit{check}(P)=\textsf{NOT OK}) \\
\forall P.\forall I.(\mathit{check}(P)=\textsf{OK})\rightarrow(P(I)\text{ does not result in a type error})
\end{array}
\]

The ideal type checker is complete and sound. It satisfies the following:

\[
\mathit{check}(P, I) =
\textsf{if}\ (P(I)\text{ does not result in a type error})\ \textsf{OK}\
\textsf{else}\ \textsf{NOT OK}
\]

If the type checker returns \(\textsf{OK}\) for given \((P,I)\), then \(P(I)\) never suffers from type errors. If the type checker returns \(\textsf{NOT OK}\), then \(P(I)\) must result in a type error. Alas, such program \(check\) cannot exist if \(P\) can be an arbitrary Turing-computable program. The nonexistence has been proven already. Its proof is similar to the proof of the undecidability of the *halting problem*.

The proof is a proof by contradiction. Assume that \(check\), which is complete and sound, exists. The definition of program \(A\) follows:

\[
A(X) =
\textsf{if}\ (\mathit{check}(X, X)=\textsf{OK})\ 1\ 1\
\textsf{else}\ 0
\]

\(X\) is the input for \(A\). If \(check(X,X)\) is \(\textsf{OK}\), \(A\) evaluates \(1\ 1\). Evaluation of \(1\ 1\) causes a type error since \(1\) is not a function. It happens only if \(X\) is free from type errors when \(X\) itself is input. On the other hand, if \(check(X,X)\) is \(\textsf{NOT OK}\), \(A\) returns \(0\). It happens only if executing \(X\) with input \(X\) terminates with a type error.

Consider \(check(A,A)\). Since \(check\) works for any pair \((P,I)\), \(check(A,A)\) equals either \(\textsf{OK}\) or \(\textsf{NOT OK}\). First, assume that the result is \(\textsf{OK}\). It implies that \(A(A)\) does not result in a type error. Because of the assumption, evaluation of \(A(A)\) leads to evaluation of \(1\ 1\), which causes a type error. It contradicts the assumption. Therefore, the result cannot be \(\textsf{OK}\). Second, assume that the result is \(\textsf{NOT OK}\). It implies that \(A(A)\) results in a type error. By the assumption, \(A(A)\) returns \(0\) without any type errors. It contradicts the assumption. Thus, the result cannot be \(\textsf{NOT OK}\) either. The fact that neither \(\textsf{OK}\) nor \(\textsf{NOT OK}\) contradicts the property of \(check\). The contradiction comes from assuming the existence of \(check\). In conclusion, \(check\) does not exist.

A type checker cannot be complete and sound at the same time. Note that it is true only if a set of programs whom a type checker inspects is Turing-complete. Programs written in limited languages, such as AE and WAE, are not Turing-complete. A type checker for such a language can be complete and sound. However, most real-world languages create a Turing-complete set of programs. Type checkers for real use cannot be complete and sound. Type system designers have three options to choose: to give up soundness and to acquire completeness, to give up completeness and to acquire soundness, and to give up both completeness and soundness.

The most common choice is the second one. Most type checkers are sound but incomplete. The motivation for type checking leads to the property. The motivation is ensuring that a specific program is free from type errors. If the type checker returns \(\textsf{OK}\), the program must be safe. The property is a description for soundness. Soundness is the most important property of a type checker.

To acquire soundness, a type checker loses completeness. A type checker can return \(\textsf{NOT OK}\) even though a given program never results in a type error. A *false positive* and a *false alarm* refer to such cases. If a sound type checker generates to many false alarms, it is impractical for use. Programmers have to waste lots of time to deal with false alarms. Consider a type checker that always returns \(\textsf{NOT OK}\). The type checker is surely sound but useless.

Fortunately, some sound type checkers return \(\textsf{OK}\) for numerous useful programs. Researchers studying type systems have shown it. Hense, most type checkers aim soundness. At the same time, they try to return \(OK\) for programs as many as possible.

Now, the definition of \(check\) requires revision. No one knows input before execution of a program. A type checker does not need to consider input to judge the existence of type errors. In general, programmers want to guarantee that a particular program does not result in a type error for every input. Let \(check(P)\) denote inspection of \(P\). The revised definition follows:

\[
\begin{array}{l}
\forall P.(\exists I.P(I)\text{ results in a type error})\rightarrow(\mathit{check}(P)=\textsf{NOT OK}) \\
\forall P.(\mathit{check}(P)=\textsf{OK})\rightarrow(\forall I.P(I)\text{ does not result in a type error})
\end{array}
\]	

For a given program, if a type checker returns \(\textsf{OK}\), then the program is *well-typed*. It implies that the type checker accepts the program. It is success of type checking. If the type checker returns \(\textsf{NOT OK}\), the program is *ill-typed*. It implies that the type checker rejects the program. It is failure of type checking.

The front-end of compilers and interpreters type-check given programs. A compiler compiles programs accepted by its type checker. An interpreter type-checks a program before evaluating the program. The evaluation corresponds to calling the `interp` function. If the type checker rejects the program, the interpreter terminates without the evaluation. Otherwise, the interpreter evaluates the program. Such a form of type checking is *static type checking*, which is type checking before evaluation. Languages providing static type checking are *statically typed*. They include C, C++, Java, and Scala. Type systems are static if they define static type checking. The article deals with *static type systems*. The term 'type systems' often refers to only 'static type systems.'

Some languages feature type checking at run time. Such type checking is *dynamic type checking*, and such languages are *dynamically typed*. Dynamic type checking seems to contradict the goal of type systems. However, dynamic type checking is beneficial. Type errors at run time causes not only abnormal termination but also unexpected behaviors. For example, the C language provides static type checking, but it is unsound for pointer types. A value of the `float` type can be stored where a value of the `int` type must be. Such a circumstance does not terminate the execution directly but leads to behaviors that programmers have not considered before the execution. Dynamic type checking is a good solution to resolve the problem. It detects type errors at run time and terminates execution before bad things happen. One well-known dynamically-typed language is Python. The Python interpreter raises a type error when addition of an integer and a string happens. *Dynamic type systems* are much simpler than static type systems. For this reason, the terms 'type systems' and 'type checkers' exclude dynamic type systems and dynamic type checkers and include static type systems and static type checkers only. On the other hand, the terms 'dynamic type checking' and 'dynamically-typed language' are widely used.

Statically-typed languages and dynamically-typed languages have their own pros and cons. Statically-typed languages allow early error detection. Programmers can find errors before execution. Static type checking is attractive when a program is complicated or requires maintenance for a long time. Static type checking fits programs that must be highly trustworthy as well. Besides, static type checking gives information about types to compilers, and compilers can optimize programs with the information. Static type checking removes the necessity of dynamic type checking. For these reasons, programs in statically-typed languages overperform programs in dynamically-typed languages. Some statically-typed languages require programmers to annotate types in code. Type checkers verifies the correctness of the *type annotations*. They are automatically-verified comments, which never become outdated, and help the maintenance of programs.

Statically-typed languages attain type soundness by giving up completeness of the type systems. Type checkers reject programs that are free from type errors. To overcome the limitation, most statically-typed languages supports unsound features, for example *explicit type casting*. Programmers avoid being rejected by the type checkers with the unsound features. However, use of unsound features breaks the type soundness of the type checkers. The use weakens the abilities of the type checkers so needs the programmers' care. Type annotations are another weakness. They are beneficial as being machine-checked comments but make code verbose unnecessarily. Statically-typed languages often feature *type inference* to allow programmers to omit type annotations. Omission resolves verbosity.

Dynamically-typed languages lacks the advantages of static type checking. Errors are discovered during execution. Programs in dynamically-typed languages underperform programs in statically-typed languages. However, inconvenience due to the incompleteness of type checkers disappears. Programmers using statically-typed languages waste their time to make type checkers believe that given programs do not result in type errors. They spend time to write correct type annotations. Dynamically-typed languages liberate programmers from the burdens. Therefore, dynamically-typed languages are ideal for the early stage of development. Programmers can easily make a prototype of their program and try various changes in the prototype. They do not waste their time to argue with type checkers. However, the advantages become invaluable after the early stage. If once the design of a program finishes, maintenance of the program and improvement of the performance are important.

*Gradual type* systems are popular in the programming language research these days. Gradual type systems aim to combine the advantages of static and dynamic type systems. Programmers can utilize gradually-typed languages as dynamically-typed languages in the early stage. After the early stage, they can gradually revise the code to make it type-safe without changing the language. Gradual addition of type annotations allows type checkers to check the nonexistence of type errors partially. The TypeScript language is one example of gradually-typed languages. It is an application of a gradual type system to the JavaScript language.

## TFAE

TFAE is a variant of FAE and features static type checking. The article defines the abstract syntax, the dynamic semantics, and the type system of TFAE and implements a type checker and an interpreter of TFAE.

### Syntax

The abstract syntax of TFAE follows:

\[
\begin{array}{lrcl}
\text{Integer} & n & \in & \mathbb{Z} \\
\text{Variable} & x & \in & \textit{Id} \\
\text{Expression} & e & ::= & n \\
&& | & e + e \\
&& | & e - e \\
&& | & x \\
&& | & \lambda x:\tau.e \\
&& | & e\ e \\
\text{Value} & v & ::= & n \\
&& | & \langle \lambda x:\tau.e,\sigma \rangle \\
\text{Environment} & \sigma & \in & \textit{Id}\hookrightarrow\text{Value}
\end{array}
\]

Unlike FAE, A lambda abstraction has a type annotation for the parameter. Lambda abstraction \(\lambda x.e\) of FAE denotes a function whose parameter is \(x\) and body is \(e\). Lambda abstraction \(\lambda x:\tau.e\) of TFAE has \(\tau\) in addition. Type \(\tau\) denotes the type of an argument for the function. Metavariable \(\tau\) ranges over types. The following defines types:

\[
\begin{array}{lrcl}
\text{Type} & \tau & ::= & \textsf{num} \\
&& | & \tau\rightarrow\tau
\end{array}
\]

Types classify values. Type \(\textsf{num}\) is the type of all the integers. Type \(\tau_1\rightarrow\tau_2\) is the type of a function that takes a value of type \(\tau_1\) and returns a value of type \(\tau_2\). For example, \(\lambda x:\textsf{num}.x\) takes an argument of type \(\textsf{num}\) and returns the argument. Its type is \(\textsf{num}\rightarrow\textsf{num}\). \(\lambda x:\textsf{num}.\lambda y:\textsf{num}.x+y\) takes an argument of type \(\textsf{num}\) and returns \(\lambda y:\textsf{num}.x+y\). \(\lambda y:\textsf{num}.x+y\) also takes an argument of type \(\textsf{num}\). Since both \(x\) and \(y\) are integers, \(x+y\) also is an integer, whose type is \(\textsf{num}\). Therefore, the type of \(\lambda y:\textsf{num}.x+y\) is \(\textsf{num}\rightarrow\textsf{num}\), and the type of \(\lambda x:\textsf{num}.\lambda y:\textsf{num}.x+y\) is \(\textsf{num}\rightarrow(\textsf{num}\rightarrow\textsf{num})\). Because arrows in function types are right-associative, the type equals \(\textsf{num}\rightarrow\textsf{num}\rightarrow\textsf{num}\).

A type is either \(\textsf{num}\) or \(\tau_1\rightarrow\tau_2\) for some \(\tau_1\) and \(\tau_2\). Every value belongs to a unique type. No value is an integer and a function at the same time. No function takes an integer as an argument and a function as an argument at the same time. In the article, every value has at most one type. Every expression has at most one type as well. Later part of the article defines the type of an expression. A value or an expression of another language may have multiple types.

### Dynamic Semantics

The article defines the dynamic semantics of TFAE in big-step style. However, note that small-step semantics are more appropriate than big-step semantics to deal with type systems. Small-step semantics makes type soundness be defined and proven easily.

The dynamic semantics of TFAE is similar to that of FAE. The only difference is a type annotation in a lambda abstraction.

\[
\sigma\vdash n\Rightarrow n
\]

\[
\frac
{ \sigma\vdash e_1\Rightarrow n_1 \quad \sigma\vdash e_2\Rightarrow n_2 }
{ \sigma\vdash e_1+e_2\Rightarrow n_1+n_2 }
\]

\[
\frac
{ \sigma\vdash e_1\Rightarrow n_1 \quad \sigma\vdash e_2\Rightarrow n_2 }
{ \sigma\vdash e_1-e_2\Rightarrow n_1-n_2 }
\]

\[
\frac
{ x\in\mathit{Domain}(\sigma) }
{ \sigma\vdash x\Rightarrow \sigma(x)}
\]

\[
\sigma\vdash \lambda x:\tau.e\Rightarrow \langle\lambda x:\tau.e,\sigma\rangle
\]

\[
\frac
{ \sigma\vdash e_1\Rightarrow\langle\lambda x:\tau.e,\sigma'\rangle \quad
  \sigma\vdash e_2\Rightarrow v' \quad
  \sigma'\lbrack x\mapsto v'\rbrack\vdash e\Rightarrow v }
{ \sigma\vdash e_1\ e_2\Rightarrow v }
\]

One may expect dynamic type checking, which is checking whether the type of argument \(v'\) equals type annotation \(\tau\), in the rule for function application. Such a design is possible, but the article focuses on only static type systems. Moreover, static type systems disallow evaluation of ill-typed programs. Hence, the type of \(v'\) must be \(\tau\), and dynamic type checking is inessential.

### Type System

The type system does not define how to evaluate an expression. It predicts whether type errors occur during the evaluation without the evaluation. To find a type error, the type system must know the type of the result of an expression. Consider \(e_1+e_2\). Even though evaluation of both \(e_1\) and \(e_2\) are free from type errors, the addition can lead to a type error because the result of \(e_1\) or \(e_2\) can be a value other than an integer. To guarantee the type safety of \(e_1+e_2\), the type system needs the types of the results are both \(\textsf{num}\) in addition to the type safety of \(e_1\) and \(e_2\).

"The type of expression \(e\) is \(\tau\)" implies that "evaluation of \(e\) does not result in a type error, and the type of the result is \(\tau\)." Therefore, one can say that if the types of \(e_1\) and \(e_2\) are both \(\textsf{num}\), then the type of \(e_1+e_2\) is \(\textsf{num}\).

The previous explanation lacks the consideration of nontermination. Nontermination implies type safety since a type error terminates execution. The revised definition follows: "The type of expression \(e\) is \(\tau\)" implies that "evaluation of \(e\) does not result in a type error, and the type of the result is \(\tau\)" or "evaluation of \(e\) never ends." The fact that if the types of \(e_1\) and \(e_2\) are both \(\textsf{num}\), then the type of \(e_1+e_2\) is \(\textsf{num}\) is still true. If evaluation of \(e_1\) or \(e_2\) does not terminate, evaluation of \(e_1+e_2\) also does not.

The type system defines a relation over expressions and types to determine the type of a given expression. The relation has premises and conclusions. For example, the conclusion "\(e_1+e_2\) has type \(\textsf{num}\)" needs the premises "\(e_1\) has type \(\textsf{num}\)" and "\(e_2\) has type \(\textsf{num}\)." Therefore, the type system uses inference rules to define the relation. The premises of a rule describe conditions whom the subexpressions of the expression in the conclusion must satisfy. A way to define the type system is similar to a way to define the big-step semantics. The big-step semantics defines a relation over expressions and values with inference rules; the type system defines a relation over expressions and types with inference rules.

The above explanation is insufficient. Recall that the dynamic semantics is a relation over environments, expressions, and values. An environment stores the values of variables. Variables exist both before and at run time. The type system needs information about variables as well. As the dynamic semantics requires the values of variables, the type system requires the types of variables. An environment is to the dynamic semantics what a *type environment* is to the type system. A type environment is a partial function from identifiers to types.

\[
\begin{array}{lrcl}
\text{Type Environment} & \Gamma & \in & \textit{Id}\hookrightarrow\text{Type}
\end{array}
\]

The type system is a relation over type environments, expressions, and types.

\[:\subseteq\text{Type Environment}\times\text{Expression}\times\text{Type}\]

\(\Gamma\vdash e:\tau\) denotes that the type of expression \(e\) under type environment \(\Gamma\) is \(\tau\). For some \(\tau\), if \(\emptyset\vdash e:\tau\) is true, then \(e\) is well-typed, and the type system accepts the expression. If \(\emptyset\vdash e:\tau\) is false for every \(\tau\), then \(e\) is ill-typed, and the type system rejects the expression.

Inference rules for each expression follow.

\[
\Gamma\vdash n:\textsf{num}
\]

The type of an integer is \(\textsf{num}\).

\[
\frac
{ \Gamma\vdash e_1:\textsf{num} \quad \Gamma\vdash e_2:\textsf{num} }
{ \Gamma\vdash e_1+e_2:\textsf{num} }
\]

If the types of \(e_1\) and \(e_2\) are both \(\textsf{num}\), then the type of \(e_1+e_2\) is \(\textsf{num}\).

\[
\frac
{ \Gamma\vdash e_1:\textsf{num} \quad \Gamma\vdash e_2:\textsf{num} }
{ \Gamma\vdash e_1-e_2:\textsf{num} }
\]

The rule for subtraction is the same as that for addition.

\[
\frac
{ x\in\mathit{Domain}(\Gamma) }
{ \Gamma\vdash x:\Gamma(x)}
\]

The dynamic semantics finds the value of a variable from an environment. The type system finds the type of a variable from a type environment. The rule allows the type system to detect free identifier errors.

\[
\frac
{ \Gamma\lbrack x:\tau_1\rbrack\vdash e:\tau_2 }
{ \Gamma\vdash \lambda x:\tau_1.e:\tau_1\rightarrow\tau_2 }
\]

The rule for a lambda abstraction needs to compute the type of a closure created by the lambda abstraction. The type of an argument is given as \(\tau_1\) by the type annotation. The rule requires the type of the return value of the function as well. The type equals the type of \(e\), the function body. The value of an argument is unknown, but the type is known as \(\tau_1\). Since a closure captures an environment when it is created, evaluation of its body can use variables in the environment. Thus, computation of the type of \(e\) needs every information in \(\Gamma\) and that the type of \(x\) is \(\tau_1\). The computation uses \(\Gamma\lbrack x:\tau_1\rbrack\). If the type is \(\tau_2\), then the return type of the function is \(\tau_2\). Finally, the type of the lambda abstraction is \(\tau_1\rightarrow\tau_2\).

\[
\frac
{ \Gamma\vdash e_1:\tau_1\rightarrow\tau_2 \quad
  \Gamma\vdash e_2:\tau_1 }
{ \Gamma\vdash e_1\ e_2\Rightarrow\tau_2 }
\]

Function application \(e_1\ e_2\) is well-typed only if \(e_1\) is a function. Let the type of \(e_1\) be \(\tau_1\rightarrow\tau_2\). The type of the argument, \(e_2\) must be \(\tau_1\). The type of the return value is \(\tau_2\) so that the type of \(e_1\ e_2\) is \(\tau_2\).

The following proof tree proves that the type of \((\lambda x:\textsf{num}.\lambda y:\textsf{num}.x+y)\ 1\ 2\) is \(\textsf{num}\).

\[
\frac
{
  \frac
  {{\huge
    \frac
    {
      \frac
      {
        \frac
        {
          \frac
          { x\in\mathit{Domain}(\lbrack x:\textsf{num},y:\textsf{num}\rbrack) }
          { \lbrack x:\textsf{num},y:\textsf{num}\rbrack\vdash x:\textsf{num} } \quad
          \frac
          { y\in\mathit{Domain}(\lbrack x:\textsf{num},y:\textsf{num}\rbrack) }
          { \lbrack x:\textsf{num},y:\textsf{num}\rbrack\vdash y:\textsf{num} }
        }
        { \lbrack x:\textsf{num},y:\textsf{num}\rbrack\vdash x+y:\textsf{num} }
      }
      { \lbrack x:\textsf{num}\rbrack\vdash\lambda y:\textsf{num}.x+y
        :\textsf{num}\rightarrow\textsf{num} }
    }
    { \emptyset\vdash\lambda x:\textsf{num}.\lambda y:\textsf{num}.x+y
      :\textsf{num}\rightarrow\textsf{num}\rightarrow\textsf{num} } \quad
    {\Large \emptyset\vdash1:\textsf{num}}
  }}
  { {\Large \emptyset\vdash(\lambda x:\textsf{num}.\lambda y:\textsf{num}.x+y)\ 1:\textsf{num}\rightarrow\textsf{num} }}
  \quad \emptyset\vdash2:\textsf{num}
}
{ \emptyset\vdash(\lambda x:\textsf{num}.\lambda y:\textsf{num}.x+y)\ 1\ 2:\textsf{num} }
\]

The type system is sound. The proof of the soundness is beyond the scope of the article. The type system rejects every expression producing a type error due to the soundness.

Consider \((\lambda x:\textsf{num}\rightarrow\textsf{num}.x\ 1)\ 1\). Evaluation of the expression results in evaluation of \(1\ 1\), which causes a type error. Since the type of \(x\ 1\) is \(\textsf{num}\), the type of the function is \((\textsf{num}\rightarrow\textsf{num})\rightarrow\textsf{num}\). The function takes an argument of type \(\textsf{num}\rightarrow\textsf{num}\). However, \(1\), the argument, has type \(\textsf{num}\), which differs from \(\textsf{num}\rightarrow\textsf{num}\). The type checking fails. The expression is ill-typed and causes a type error.

If the type system of a language is sound, then the language is *type sound*. In other words, the language satisfies *type soundness*. Since the type system of TFAE is sound, TFAE is type sound.

A sound type system is incomplete. Therefore, the type system of TFAE is incomplete. The type system can reject an expression that is free from a type error. The following formalizes the fact:

\[
\exists e.(\exists v.\emptyset\vdash e\Rightarrow v)\land(\not\exists\tau.\emptyset\vdash e:\tau)
\]

Various such expressions exist. Consider \((\lambda x:\textsf{num}.x)\ (\lambda x:\textsf{num}.x)\). The expression yields \(\lambda x:\textsf{num}.x\) without a type error. However, the type system rejects the expression. \(\lambda x:\textsf{num}.x\) takes an argument of type \(\textsf{num}\). However, \(\lambda x:\textsf{num}.x\), the argument, has type \(\textsf{num}\rightarrow\textsf{num}\), which differs from \(\textsf{num}\). As a result, the expression never causes a type error but is ill-typed.

### Implementing a Type Checker

The following Scala code implements the abstract syntax of TFAE:

```scala
sealed trait Expr
case class Num(n: Int) extends Expr
case class Add(l: Expr, r: Expr) extends Expr
case class Sub(l: Expr, r: Expr) extends Expr
case class Id(x: String) extends Expr
case class Fun(x: String, t: Type, b: Expr) extends Expr
case class App(f: Expr, a: Expr) extends Expr

sealed trait Type
case object NumT extends Type
case class ArrowT(p: Type, r: Type) extends Type

type TEnv = Map[String, Type]
```

A `TFAE` instance represents a TFAE expression. The only difference between this implementation and the implementation of FAE is field `t` of the `Fun` class. The field represents the type of a parameter. A `Type` instance represents a TFAE type. `NumT` corresponds to type \(\textsf{num}\). An `ArrowT` instance represents the type of a function. `TEnv` is the type of a type environment; it is a map from strings onto TFAE types.

The `mustSame` function compares given two types. If the types are the same, the result is the type. Otherwise, it raises an exception.

The below `typeCheck` function checks the type of an expression. It takes a TFAE expression and a type environment as arguments. If type checking succeeds, the result of the function is the type of the expression. Otherwise, it raises an exception.

```scala
def typeCheck(e: Expr, env: TEnv): Type = e match {
  case Num(n) => NumT
  case Add(l, r) =>
    mustSame(mustSame(NumT,
      typeCheck(l, env)), typeCheck(r, env))
  case Sub(l, r) =>
    mustSame(mustSame(NumT,
      typeCheck(l, env)), typeCheck(r, env))
  case Id(x) => env(x)
  case Fun(x, t, b) =>
    ArrowT(t, typeCheck(b, env + (x -> t)))
  case App(f, a) =>
    val ArrowT(t1, t2) = typeCheck(f, env)
    val t3 = typeCheck(a, env)
    mustSame(t1, t3)
    t2
}
```

The implementation is similar to the inference rules of the type system. In the `Num` case, the type is `NumT`. In the `Add` and `Sub` cases, the two subexpressions of the expression must have type `NumT`. The type of the expression also is `NumT`. The `Fun` case checks the type of the function body under the extended type environment. The type of the expression is a function type whose return type is the type of the body. The `App` case checks the types of the function and the argument. The parameter type of the function must equal the type of the argument. The type of the expression is the return type of the function.

The following code checks the type of \((\lambda x:\textsf{num}.\lambda y:\textsf{num}.x+y)\ 1\ 2\):

```scala
// (lambda x:num.lambda y:num.x + y) 1 2
typeCheck(
  App(
    App(
      Fun("x", NumT, Fun("y", NumT,
        Add(Id("x"), Id("y")))),
      Num(1)
    ),
    Num(2)
  ),
  Map.empty
)
// num
```

The type checker rejects \((\lambda x:\textsf{num}\rightarrow\textsf{num}.x\ 1)\ 1\) and \((\lambda x:\textsf{num}.x)\ (\lambda x:\textsf{num}.x)\).

``` scala
// (lambda x:num->num.x 1) 1
typeCheck(
  App(
    Fun("x", ArrowT(NumT, NumT),
      App(Id("x"), Num(1))),
    Num(1)
  ),
  Map.empty
)
// java.lang.Exception
//   at TFAE$.mustSame
//   at TFAE$.typeCheck
```

```scala
// (lambda x:num.x) (lambda x:num.x)
typeCheck(
  App(
    Fun("x", NumT, Id("x")),
    Fun("x", NumT, Id("x"))
  ),
  Map.empty
)
// java.lang.Exception
//   at TFAE$.mustSame
//   at TFAE$.typeCheck
```

### Implementing an Interpreter

The interpreter of TFAE is similar to that of FAE.

```scala
sealed trait Value
case class NumV(n: Int) extends Value
case class CloV(p: String, b: Expr, e: Env) extends Value

type Env = Map[String, Value]

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
  case Fun(x, _, b) => CloV(x, b, env)
  case App(f, a) =>
    val CloV(x, b, fEnv) = interp(f, env)
    interp(b, fEnv + (x -> interp(a, env)))
}
```

Since type annotations do not take any roles at run time, closures lack type annotations.

```scala
def run(e: Expr): Value = {
  typeCheck(e, Map.empty)
  interp(e, Map.empty)
}
```

The `run` function takes an expression as an argument. It applies the `typeCheck` and `interp` functions to the expression. If type checking fails, due to a raised exception, the application `interp` does not happen. It happens only if type checking succeeds.

The following code executes the three previous expressions with the `run` function:

```scala
// (lambda x:num.lambda y:num.x + y) 1 2
run(
  App(
    App(
      Fun("x", NumT, Fun("y", NumT,
        Add(Id("x"), Id("y")))),
      Num(1)
    ),
    Num(2)
  )
)
// 3
```

``` scala
// (lambda x:num->num.x 1) 1
run(
  App(
    Fun("x", ArrowT(NumT, NumT),
      App(Id("x"), Num(1))),
    Num(1)
  )
)
// java.lang.Exception
//   at TFAE$.mustSame
//   at TFAE$.typeCheck
//   at TFAE$.run
```

```scala
// (lambda x:num.x) (lambda x:num.x)
run(
  App(
    Fun("x", NumT, Id("x")),
    Fun("x", NumT, Id("x"))
  )
)
// java.lang.Exception
//   at TFAE$.mustSame
//   at TFAE$.typeCheck
//   at TFAE$.run
```

The function returns the value of the first expression correctly. The type checker correctly rejects the second expression. It prevents the evaluation, which leads to a type error. The third expression does not cause a type error. However, the type checker rejects the expression and prevents the evaluation.

## FAE and TFAE

### Type Erasure

According to the dynamic semantics of TFAE, parameter type annotations in lambda
abstractions take no role at run time. They are necessary only for static type
checking. Therefore, it is possible to erase type annotations in order to make
code used at run time, while type annotations exist at compile time. Type
erasure denotes such semantics, which removes type annotations from code used at
run time.

The following shows how to make an FAE counterpart of a TFAE expression via type
erasure. \(\it erase\) is a function from a TFAE expression to an FAE
expression.

\[
\begin{array}{rcl}
\mathit{erase}(n) &=& n \\
\mathit{erase}(e_1+e_2) &=& \mathit{erase}(e_1)+\mathit{erase}(e_2) \\
\mathit{erase}(e_1-e_2) &=& \mathit{erase}(e_1)-\mathit{erase}(e_2) \\
\mathit{erase}(x) &=& x \\
\mathit{erase}(\lambda x:\tau.e) &=& \lambda x.\mathit{erase}(e) \\
\mathit{erase}(e_1\ e_2) &=& \mathit{erase}(e_1)\ \mathit{erase}(e_2)
\end{array}
\]

The only changes happen in lambda abstractions: removal of parameter type
annotations. Integers and variables remain the same. The function is defined
recursively for addition, subtraction, and function application.

The following Scala code implements type erasure:

```scala
object FAE {
  sealed trait Expr
  case class Num(n: Int) extends Expr
  case class Add(l: Expr, r: Expr) extends Expr
  case class Sub(l: Expr, r: Expr) extends Expr
  case class Id(x: String) extends Expr
  case class Fun(x: String, b: Expr) extends Expr
  case class App(f: Expr, a: Expr) extends Expr
}

def erase(e: Expr): FAE.Expr = e match {
  case Num(n) => FAE.Num(n)
  case Add(l, r) =>
    FAE.Add(erase(l), erase(r))
  case Sub(l, r) =>
    FAE.Sub(erase(l), erase(r))
  case Id(x) => FAE.Id(x)
  case Fun(x, _, b) => FAE.Fun(x, erase(b))
  case App(f, a) =>
    FAE.App(erase(f), erase(a))
}
```

Since the classes represeting TFAE expressions have the same names as the
classes represeting FAE expressions, the `FAE` singleton object contains the classes for
FAE expressions.

```scala
object FAE {
  ...

  sealed trait Value
  case class NumV(n: Int) extends Value
  case class CloV(p: String, b: FAE, e: Env) extends Value

  type Env = Map[String, Value]

  def interp(e: FAE, env: Env): Value = e match {
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
    case Fun(x, b) => CloV(x, b, env)
    case App(f, a) =>
      val CloV(x, b, fEnv) = interp(f, env)
      interp(b, fEnv + (x -> interp(a, env)))
  }
}

def run(e: Expr): FAE.Value = {
  typeCheck(e, Map.empty)
  FAE.interp(erase(e), Map.empty)
}
```

`erase` allows a TFAE expression to be transformed to an FAE expression so
that it is possible to reuse the existing `interp` function for FAE instead of
defining a new `interp` function for TFAE. Parameter type annotations do not
affect the result of evaluation. The above `run` function is the same as the
previous `run` function except that its return type is `FAE.Value`, not
`Value`. Even though the class is different, the result represents exactly the
same value. The following describes this property mathematically:

\[
\forall\sigma.\forall e.\forall v.
(\sigma\vdash e\Rightarrow v)\leftrightarrow(\mathit{erase}(\sigma)\vdash\mathit{erase}(e)\Rightarrow\mathit{erase}(v))
\]

Below defines type erasure for values and environments.

\[
\begin{array}{rcl}
\mathit{erase}(n) &=& n \\
\mathit{erase}(\langle\lambda x:\tau.e,\sigma\rangle) &=& \langle\lambda x.\mathit{erase}(e),\mathit{erase}(\sigma)\rangle \\
\mathit{erase}(\lbrack x_1\mapsto v_1,\cdots,x_n\mapsto v_n\rbrack) &=&\lbrack x_1\mapsto\mathit{erase}(v_1),\cdots,x_n\mapsto\mathit{erase}(v_n)\rbrack
\end{array}
\]


### Type System of FAE

Can one define the type system of FAE? The only difference between FAE and TFAE
is existence of parameter type annotations in lambda abstractions. For the other
sorts of expressions, the inference rules of the TFAE type system work well.
An inference rule for lambda abstractions is the last piece of the type system
of FAE. First, consider the inference rule of TFAE again.

\[
\frac
{ \Gamma\lbrack x:\tau_1\rbrack\vdash e:\tau_2 }
{ \Gamma\vdash \lambda x:\tau_1.e:\tau_1\rightarrow\tau_2 }
\]

In TFAE, a lambda abstraction gives the fact that the type of its parameter is
\(\tau_1\). The type of the body \(e\) can be computed under \(\Gamma\lbrack x:\tau_1\rbrack\)
without any problems.

On the other hand, FAE lacks parameter type annotations. Thus, the following is
a possible choice:

\[
\frac
{ \Gamma\lbrack x:\tau_1\rbrack\vdash e:\tau_2 }
{ \Gamma\vdash \lambda x.e:\tau_1\rightarrow\tau_2 }
\]

Interestingly, it has exactly the same premise. Even though \(\tau_1\) is
unknown, the premise uses \(\tau_1\). It is a completely correct rule. The
semantics of a language does not have to give an insight into implementation.
It is just a mathematically defined system. The above rule retains the type
soundness of FAE and make many expressions well-typed. For example, consider
\((\lambda x.\lambda y.x+y)\ 1\ 2\). The following proof tree proves that the
type of the expression is \(\sf num\).

\[
\frac
{
  \frac
  {{\huge
    \frac
    {
      \frac
      {
        \frac
        {
          \frac
          { x\in\mathit{Domain}(\lbrack x:\textsf{num},y:\textsf{num}\rbrack) }
          { \lbrack x:\textsf{num},y:\textsf{num}\rbrack\vdash x:\textsf{num} } \quad
          \frac
          { y\in\mathit{Domain}(\lbrack x:\textsf{num},y:\textsf{num}\rbrack) }
          { \lbrack x:\textsf{num},y:\textsf{num}\rbrack\vdash y:\textsf{num} }
        }
        { \lbrack x:\textsf{num},y:\textsf{num}\rbrack\vdash x+y:\textsf{num} }
      }
      { \lbrack x:\textsf{num}\rbrack\vdash\lambda y.x+y
        :\textsf{num}\rightarrow\textsf{num} }
    }
    { \emptyset\vdash\lambda x.\lambda y.x+y
      :\textsf{num}\rightarrow\textsf{num}\rightarrow\textsf{num} } \quad
    {\Large \emptyset\vdash1:\textsf{num}}
  }}
  { {\Large \emptyset\vdash(\lambda x.\lambda y.x+y)\ 1:\textsf{num}\rightarrow\textsf{num} }}
  \quad \emptyset\vdash2:\textsf{num}
}
{ \emptyset\vdash(\lambda x.\lambda y.x+y)\ 1\ 2:\textsf{num} }
\]

Humans can easily find that both types of \(x\) and \(y\) are \(\sf num\).
However, it is nontrivial to make computers do the same thing. The type system
of TFAE hints at implementation of the `typeCheck` function. However, the type
system of FAE does not say anything about how to find the types of parameters.
To make a type checker for TFAE, it is important to know how to recover omitted
parameter type annotations. Such recovery is called type inference. Type
inference is outside the scope of this article.

## Extending TFAE

The type system of TFAE is sound but incomplete. How many type-safe expressions are rejected by the type system? The *normalization property* of TFAE has been proved already. It implies that evaluation of any well-typed TFAE expression terminates in a finite time. Many FAE expressions including \((\lambda x.x\ x)\ (\lambda x.x\ x)\) does not terminate. However, such expressions are ill-typed in TFAE. TFAE is not Turing-complete while lambda calculus and FAE are Turing-complete.

Hitherto articles have extended the language for convenience of programmers. Lambda calculus is Turing-complete. Adding features to lambda calculus cannot increase the expressivity of the language. However, the articles have added integers, arithmetic operations, recursive functions, mutation, and first-class continuations. Even though expressivity remains the same, such extensions are valuable because they allow programmers to implement complex programs easily.

Under the presence of the type system of a language, convenience of programmers is one of the most important reasons to extends the language. Helping programmers to implement complex programs is always desirable things to do for type system designers.

On the other hand, by extending the language, one can make the type system accept more expressions than the type system before the extension. No extension makes the language complete. However, the type system can be more precise. If types classify values precisely, the type system can determine a type-safe expression as a well-typed expression. It increases the expressivity of the language and allows programmers to implement more programs.

### Local Variables

The following adds local variables to TFAE:

\[
\begin{array}{lrcl}
\text{Expression} & e & ::= & \cdots \\
&& | & \textsf{val}\ x=e\ \textsf{in}\ e
\end{array}
\]

The dynamic semantics follows that of WAE.

\[
\frac
{
  \sigma\vdash e_1\Rightarrow v_1 \quad
  \sigma\lbrack x\mapsto v_1\rbrack\vdash e_2\Rightarrow v_2
}
{ \sigma\vdash \textsf{val}\ x=e_1\ \textsf{in}\ e_2\Rightarrow v_2 }
\]

The static semantics is similar to the dynamic semantics.

\[
\frac
{
  \Gamma\vdash e_1:\tau_1 \quad
  \Gamma\lbrack x:\tau_1\rbrack\vdash e_2: \tau_2
}
{ \Gamma\vdash \textsf{val}\ x=e_1\ \textsf{in}\ e_2:\tau_2 }
\]

A lambda abstraction and a function application can express a local variable declaration. The only difference is that a local variable declaration does not require a type annotation, but a lambda abstraction does. Local variable declarations allow programmers to implement programs concisely.

The following revision makes the interpreter to support local variable declarations:

```scala
case class With(x: String, e: Expr, b: Expr) extends Expr

def typeCheck(e: Expr, env: TEnv): Type = e match {
  ...
  case With(x, e, b) =>
    typeCheck(b, env + (x -> typeCheck(e, env)))
}

def interp(e: Expr, env: Env): Value = e match {
  ...
  case With(x, e, b) =>
    interp(b, env + (x -> interp(e, env)))
}
```

## Pairs

The following adds pairs to TFAE:

\[
\begin{array}{lrcl}
\text{Expression} & e & ::= & \cdots \\
&& | & (e,e) \\
&& | & e.1 \\
&& | & e.2 \\
\text{Value} & v & ::= & \cdots \\
&& | & (v,v) \\
\end{array}
\]

\((v_1,v_2)\) is a pair whose first value is \(v_1\) and second value is \(v_2\).

The following rules define the dynamic semantics:

\[
\frac
{
  \sigma\vdash e_1\Rightarrow v_1 \quad
  \sigma\vdash e_2\Rightarrow v_2
}
{ \sigma\vdash (e_1,e_2)\Rightarrow (v_1,v_2) }
\]

\[
\frac
{
  \sigma\vdash e\Rightarrow (v_1,v_2)
}
{ \sigma\vdash e.1\Rightarrow v_1 }
\]

\[
\frac
{
  \sigma\vdash e\Rightarrow (v_1,v_2)
}
{ \sigma\vdash e.2\Rightarrow v_2 }
\]

Type \(\tau_1\times\tau_2\) is the type of \((v_1,v_2)\) if the type of \(v_1\) is \(\tau_1\), and the type of \(v_2\) is \(\tau_2\).

\[
\begin{array}{lrcl}
\text{Type} & \tau & ::= & \cdots \\
&& | & \tau\times\tau \\
\end{array}
\]

The following rules define the static semantics:

\[
\frac
{
  \Gamma\vdash e_1:\tau_1 \quad
  \Gamma\vdash e_2:\tau_2
}
{ \Gamma\vdash (e_1,e_2):\tau_1\times\tau_2 }
\]

\[
\frac
{
  \Gamma\vdash e:\tau_1\times\tau_2
}
{ \Gamma\vdash e.1:\tau_1 }
\]

\[
\frac
{
  \Gamma\vdash e:\tau_1\times\tau_2
}
{ \Gamma\vdash e.2:\tau_2 }
\]

The following revision makes the interpreter to support pairs:

```scala
case class Pair(f: Expr, s: Expr) extends Expr
case class Fst(e: Expr) extends Expr
case class Snd(e: Expr) extends Expr

case class PairT(f: Type, s: Type) extends Type

def typeCheck(e: Expr, env: TEnv): Type = e match {
  ...
  case Pair(f, s) =>
    PairT(typeCheck(f, env), typeCheck(s, env))
  case Fst(e) =>
    val PairT(f, s) = typeCheck(e, env)
    f
  case Snd(e) =>
    val PairT(f, s) = typeCheck(e, env)
    s
}

case class PairV(f: Value, s: Value) extends Value

def interp(e: Expr, env: Env): Value = e match {
  ...
  case Pair(f, s) =>
    PairV(interp(f, env), interp(s, env))
  case Fst(e) =>
    val PairV(f, s) = interp(e, env)
    f
  case Snd(e) =>
    val PairV(f, s) = interp(e, env)
    s
}
```

### Conditional Expressions

The following adds Boolean values and conditional expressions to TFAE:

\[
\begin{array}{lrcl}
\text{Boolean} & b & ::= & \textsf{true} \\
&&|& \textsf{false} \\
\text{Expression} & e & ::= & \cdots \\
&& | & b \\
&& | & \textsf{if}\ e\ e\ e \\
\text{Value} & v & ::= & \cdots \\
&& | & b \\
\end{array}
\]

The following rules define the dynamic semantics:

\[
\sigma\vdash b\Rightarrow b
\]

\[
\frac
{
  \sigma\vdash e_1\Rightarrow\textsf{true} \quad
  \sigma\vdash e_2\Rightarrow v
}
{ \sigma\vdash \textsf{if}\ e_1\ e_2\ e_3\Rightarrow v }
\]

\[
\frac
{
  \sigma\vdash e_1\Rightarrow\textsf{false} \quad
  \sigma\vdash e_3\Rightarrow v
}
{ \sigma\vdash \textsf{if}\ e_1\ e_2\ e_3\Rightarrow v }
\]

Type \(\textsf{bool}\) is the type of a Boolean value.

\[
\begin{array}{lrcl}
\text{Type} & \tau & ::= & \cdots \\
&& | & \textsf{bool} \\
\end{array}
\]

The following rules define the static semantics:

\[
{ \Gamma\vdash b:\textsf{bool} }
\]

\[
\frac
{
  \Gamma\vdash e_1:\textsf{bool} \quad
  \Gamma\vdash e_2:\tau \quad
  \Gamma\vdash e_3:\tau
}
{ \Gamma\vdash \textsf{if}\ e_1\ e_2\ e_3:\tau }
\]

The following revision makes the interpreter to support Boolean values and conditional expressions:

```scala
case class Bool(b: Boolean) extends Expr
case class If(c: Expr, t: Expr, f: Expr) extends Expr

case object BoolT extends Type

def typeCheck(e: Expr, env: TEnv): Type = e match {
  ...
  case Bool(b) => BoolT
  case If(c, t, f) =>
    mustSame(typeCheck(c, env), BoolT)
    mustSame(typeCheck(t, env), typeCheck(f, env))
}

case class BoolV(b: Boolean) extends Value

def interp(e: Expr, env: Env): Value = e match {
  ...
  case Bool(b) => BoolV(b)
  case If(c, t, f) =>
    val BoolV(b) = interp(c, env)
    interp(if (b) t else f, env)
}
```

## Acknowledgments

I thank professor Ryu for giving feedback on the article.
