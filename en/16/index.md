The article deals with lazy evaluation. Lazy evaluation means delaying the evaluation of an expression until the result is required. Real-world languages support many features other than function applications. The evaluations of expressions at various places can be subjects of lazy evaluation. For example, Scala provides the `lazy` keyword to delay the initialization of a variable. The initialization of a variable declared with the `lazy val` keyword happens when the variable is first used, but not declared. However, discussing the evaluations of function call arguments gives enough insight to understand lazy evaluation. Therefore, the article focuses only on function applications.

Every hitherto language from the previous articles uses eager evaluation, or strict evaluation. Eager evaluation is a strategy that evaluates an argument before a function body. Call-by-value semantics of the last article belongs to eager evaluation. Call by reference denotes semantics that arguments are passed as references instead of values. Strictly speaking, it is orthogonal to the distinction between eager and lazy evaluation. However, the intention of lazy evaluation is delaying an evaluation, but call by reference does not intend it. Thus, call-by-reference semantics also commonly belongs to eager evaluation.

On the other hand, lazy evaluation evaluates a function body first. The evaluation of an argument happens when its corresponding parameter appears at the function body. Consider the following Scala code:

```scala
def f(): Int = ...
def g(): Boolean = ...
def h(x: Int, b: Boolean): Int =
  if (b) x else 0

h(f(), g())
```

Function `h` has two parameters: `x` and `b`. The value of `x` is unnecessary when `b` equals `false`. In such cases, the result always is `0` regardless of the value of `x`. In the last line of the code, the arguments of the function call are `f()` and `g()`. Suppose that `g()` equals `true`. Evaluating `f()` is essential. In contrast, when `g()` equals `false`, it is sure that the result is `0` whatever `g()` results in. Scala uses eager evaluation. Since `f()` is always evaluated even though its result might be unessential, the code is inefficient. However, lazy evaluation allows omitting the evaluation of `f()` when `g()` equals `false`. The code per se is efficient. Lazy evaluation helps programmers to write concise and efficient code.

A way to implement lazy evaluation varies according to a criterion deciding the necessity of the value of an argument. The simplest criterion is the occurrence of the corresponding parameter in the function body. One can delay more than the criterion. If the value does not participate in some calculation despite the occurrence, the value is unnecessary. Consider the following code:

```scala
def f(x: Int): Int = x

f(1 + 1) + 1
```

Follow the first criterion. Since `x` appears in the body, the evaluation of `1 + 1` happens and yields `2`. The final result is `3`, which is the result of `2 + 1`.

Now, follow the second strategy. Despite the appearance of `x`, its value is unimportant. It is possible to omit the evaluation and to return `1 + 1`. The final result is `3`, which is the result of `3 + 1`.

One can delay more than the second strategy. The final result is `(1 + 1) + 1`. The expression is evaluated only when the result becomes observable, for example being printed to a command line.

No single semantics is the best among them. The best depends on the context. Language designers must carefully decide the semantics of their language based on a domain whom it targets, performance, and ease of understanding by programmers.

The article discusses LFAE from the lecture at first. It lacks the formal semantics of LFAE but shows the implementation of an interpreter. The last part deals with call by name and call by need in detail. They are beyond the scope of the course so that one may skip the part.

## LFAE

LFAE is a language applying lazy evaluation to FAE. They have the same syntax. In LFAE, arguments are evaluated only when their values are necessary. The value is necessary if it is a function of a function application or an operand for an addition or a subtraction.

The following Scala code implements the abstract syntax of LFAE:

```scala
sealed trait LFAE
case class Num(n: Int) extends LFAE
case class Add(l: LFAE, r: LFAE) extends LFAE
case class Sub(l: LFAE, r: LFAE) extends LFAE
case class Id(x: String) extends LFAE
case class Fun(x: String, b: LFAE) extends LFAE
case class App(f: LFAE, a: LFAE) extends LFAE
```

An environment is a map from strings to values. It contains the values of arguments. However, the values are unknown when an environment is expanded. Values must be able to denote delayed evaluations.

```scala
sealed trait LFAEV
case class NumV(n: Int) extends LFAEV
case class CloV(p: String, b: LFAE, e: Env) extends LFAEV
case class ExprV(e: LFAE, env: Env) extends LFAEV

type Env = Map[String, LFAEV]
```

An `ExprV` instance denotes an expression whose value is unknown. Its value is the result of evaluating `e` under `env`.

If an `ExprV` instance is an operand or a function being applied to an argument, then the real value of the instance is necessary. The following `strict` function calculates the value:

```scala
def strict(v: LFAEV): LFAEV = v match {
  case ExprV(e, env) => strict(interp(e, env))
  case _ => v
}
```

An `ExprV` instance denotes a value that is the result of evaluating its expression under its environment. The value can be an `ExprV` instance as well. Recursive calls resolve such cases. When argument `v` is a `NumV` or `CloV` instance, it is the final result of the function.


```scala
def lookup(x: String, env: Env): LFAEV =
  env.getOrElse(x, throw new Exception)

def interp(e: LFAE, env: Env): LFAEV = e match {
  case Num(n) => NumV(n)
  case Add(l, r) =>
    val NumV(n) = strict(interp(l, env))
    val NumV(m) = strict(interp(r, env))
    NumV(n + m)
  case Sub(l, r) =>
    val NumV(n) = strict(interp(l, env))
    val NumV(m) = strict(interp(r, env))
    NumV(n - m)
  case Id(x) => lookup(x, env)
  case Fun(x, b) => CloV(x, b, env)
  case App(f, a) =>
    val CloV(x, b, fEnv) = strict(interp(f, env))
    interp(b, fEnv + (x -> ExprV(a, env)))
}
```

The `Num`, `Id`, and `Fun` cases equal those of the FAE interpreter. The `Add` and `Sub` cases need to apply the `strict` function to the results of `interp` function calls. The `Fun` case applies the `strict` function to the result of `f` to get a closure. There is no `interp` call for an argument expression. A new `ExprV` instance consists of the argument expression and the current environment. The instance is added to the environment.

The following code evaluates \((\lambda x.1)(1\ 1)\) with the `interp` function. The FAE interpreter will raise an error for the expression as the first \(1\) of \(1\ 1\) is not a function. On the other hand, the LFAE interpreter yields \(1\) because the function body lacks \(x\), the parameter. It never evaluates \(1\ 1\), which is problematic.

The current implementation is efficient when a parameter appears once or less in the function body. However, using a parameter twice or more leads to redundant calculations. Consider applying the current implementation to the following Scala code:

```scala
def f(x: Int): Int = x + x
def g(): Int = ...

f(g())
```

`x` appears twice in the body of function `f`. `g()` is evaluated twice. Suppose that `g` is a pure function, which lacks a side effect and always produces the same result. Then, the second evaluation yields the same result to the first evaluation. The redundant evaluation never affects other evaluations. Therefore, using the first result of `g()` again does not make any differences. On the other hand, eager evaluation naturally makes `g()` to be evaluated only once.

By storing the value of an argument and using the value again, the interpreter becomes efficient. It is as optimla as eager evaluation when a parameter appears multiple times; it is as optimal as lazy evaluation when the value of a parameter can be unnecessary. If a language features mutable boxes or variables, which cause side effects, storing the value of an argument can change the result. The modification in an interpreter changes the semantics. It is not an optimization. However, LFAE lacks side effects, and it thus is just an optimization.

```scala
case class ExprV(
  e: LFAE, env: Env, var v: Option[LFAEV]
) extends LFAEV
```

The definition of the `ExprV` class has changed. Field `v` is mutable. When the value of the expression is unknown, `v` equals `None`. After the value is calculated once, `v` equals a `Some` instance containing the value. To use the value again, reading the value of `v` is enough. No redundant evaluations happen. The `strict` function requires the following changes:

```scala
def strict(v: LFAEV): LFAEV = v match {
  case ev @ ExprV(e, env, None) =>
    val cache = strict(interp(e, env))
    ev.v = Some(cache)
    cache
  case ExprV(_, _, Some(cache)) => cache
  case _ => v
}
```

The `App` case of the `interp` function must pass `None`, which is the initial value of `v`, as an argument for the `ExprV` constructor.

```scala
case App(f, a) =>
  val CloV(x, b, fEnv) = strict(interp(f, env))
  interp(b, fEnv + (x -> ExprV(a, env, None)))
```

One may use a default parameter value for the `ExprV` class instead of revising the `interp` function.

```scala
case class ExprV(
  e: LFAE, env: Env, var v: Option[LFAEV] = None
) extends LFAEV
```

In this case, passing only two arguments for a new `ExprV` instance initializes field `v` to `None` automatically. Hence, changing only `strict` function suffices. The `interp` function remains the same.

## Substitution

The semantics of FAE needs revision. The formalization will change, but the meaning remains the same. Substitution replaces environments. Substitution allows defining lazy evaluation semantics easily and comparing eager and lazy evaluations clearly.

Substitution is changing a particular subexpression in an expression into another subexpression. Usually, it changes a variable into a specific expression. For example, substituting \(x\) with \((z\ 3)\) in \(x+y\) results in \((z\ 3)+y\).

The following is the abstract syntax of FAE:

\[
\begin{array}{lrcl}
\text{Integer} & n & \in & \mathbb{Z} \\
\text{Variable} & x & \in & \textit{Id} \\
\text{Expression} & e & ::= & n \\
&& | & e + e \\
&& | & e - e \\
&& | & x \\
&& | & \lambda x.e \\
&& | & e\ e
\end{array}
\]

\(\lbrack e'/x\rbrack e\) denotes substituting \(x\) with \(e'\) in \(e\). \(\lbrack /\rbrack\) is a function from an expression, a variable, and an expression to an expression.

\[
\lbrack/\rbrack \in \text{Expression}\times\text{Variable}\times\text{Expression}\rightarrow\text{Expression}
\]

Substitution retains an integer since it lacks variables.

\[\lbrack e/x \rbrack n  =  n\]

A variable changes into an expression if the subject of the substitution equals the variable. Otherwise, it remains the same.

\[
\lbrack e/x' \rbrack x = \begin{cases} e& \text{if } x=x'\\ x & \text{if } x\not=x'\end{cases}
\]

Substitution for sums, differences, and function applications has recursive definitions. The result of substitution is applying the substitution to all the subexpressions.

\[
\begin{array}{rcl}
\lbrack e/x \rbrack (e_1+e_2) = \lbrack e/x \rbrack e_1+\lbrack e/x \rbrack e_2 \\
\lbrack e/x \rbrack (e_1-e_2) = \lbrack e/x \rbrack e_1-\lbrack e/x \rbrack e_2 \\
\lbrack e/x \rbrack (e_1\ e_2)\ = \lbrack e/x \rbrack e_1\ \lbrack e/x \rbrack e_2
\end{array}
\]

If the subject of substitution differs from the parameter of a lambda abstraction, the result of the substitution is applying it to the body. Otherwise, it remains the same.

The above rule is crucial for defining the semantics of function applications with substitution. Substituting a parameter in the function body corresponds to passing arguments to functions. If the name of the parameter is unchecked before substitution, shadowing fails.

Now, the semantics of FAE uses substitution. Environments are needless. The semantics is a relation over expressions and values. \(\Downarrow\) denotes the semantics.

\[
\Downarrow \subseteq \text{Expression}\times\text{Value}
\]

\(e\Downarrow v\) implies that evaluating \(e\) yields \(v\).

A value is either an integer or a function value. Due to the absence of environments, a function value lacks an environment. A lambda abstraction instead of a closure is a function value.

\[
\begin{array}{lrcl}
\text{Value} & v & ::= & n \\
&& | & \lambda x.e
\end{array}
\]

The inference rules for integers, sums, and differences are similar to the previous rules.

[
n\Downarrow n
\]

\[
\frac
{
  e_1\Downarrow n_1 \quad
  e_2\Downarrow n_2
}
{ e_1+e_2\Downarrow n_1+n_2 }
\]

\[
\frac
{
  e_1\Downarrow n_1 \quad
  e_2\Downarrow n_2
}
{ e_1-e_2\Downarrow n_1-n_2 }
\]

Like an integer, a lambda abstraction produces itself.

\[
\lambda x.e\Downarrow\lambda x.e
\]

A function application evaluates the function and the argument. Then, it evaluates an expression obtained by substituting the parameter with the value of the argument in the function body.

\[
\frac{
  e_1\Downarrow \lambda x.e \quad
  e_2\Downarrow v' \quad
  \lbrack v'/x\rbrack e\Downarrow v
}
{ e_1\ e_2\Downarrow v }
\]

Since substitution changes parameters into values, evaluating expressions without free variables never results in situations directly requiring the values of variables. No rules for variables exist. If an evaluation fails because of a variable, then the original expression contains a free variable. Consider \(\lambda x.y)\ 1\). Variable \(y\) is free in the expression. Substituting \(x\) with \(1\) in \(\lambda x.y\) results in \(y\). It is impossible to evaluate \(y\).

To understand the definition of substitution, consider \((\lambda x.(\lambda x.x)\ 1)\ 2\). \(\lambda x.x\) is an identity function. Since \((\lambda x.x\)\ 1\) always results in \(1\), \(\lambda x.(\lambda x.x)\ 1\) is a function returning \(1\) regardless of the value of an argument. According to the current definition of substitution, substituting \(x\) with \(2\) in \((\lambda x.x)\ 1\) results in \((\lambda x.x)\ 1\) because subject \(x\) equals parameter \(x\). \((\lambda x.x)\ 1\) yields \(1\), which is correct. Assume that substitution always changes the body of a lambda abstraction. Then the substitution results in \((\lambda x.2)\ 1\). The final result is \(2\), which is wrong. It implies that the innermost \(x\) is bound to parameter \(x\) of \(\lambda x.(\lambda x.x)\ 1\) instead of parameter \(x\) of \(\lambda x.x\). Shadowing fails, and the original definition thus is correct.

### Alpha Conversion

Alas, the semantics of FAE is incomplete. An expression containing a free variable can result in an error because directly evaluating a variable is impossible. However, under the current semantics, such an expression can incorrectly yield a value. Consider \((\lambda x.\lambda y.x)\ (\lambda z.y)\ 1\ 2\). \(\lambda z.y\) has free variable \(y\). \(\lambda x.\lambda y.x\) is a function taking two arguments and returning the former. Thus, \((\lambda x.\lambda y.x)\ (\lambda z.y)\ 1\) equals \(\lambda z.y\). Applying the function to \(2\) produces an error as the value of \(y\) is unknown. However, the current semantics evaluates the expression in a different way. Substituting \(x\) with \(\lambda z.y\) in \(\lambda x.\lambda y.x\) results in \(\lambda y.\lambda z.y\). It has already become problematic. Free variable \(y\) in \(\lambda z.y\) has been unexpectedly bound to parameter \(y\) of \(\lambda y.x\). Continuing the evaluation yields \(1\) since \(\lambda y.\lambda z.y\) takes two arguments and returns the former. Why did it happen? \(y\) bound to parameter \(y\) of \(\lambda y.x\) must syntactically belong to the body of the function. However, the substitution syntactically replaces \(x\) with \(\lambda z.y\). As a consequence, free variable \(y\), which is unrelated to parameter \(y\), has been bound to parameter \(y\). Variable capture refers to that a free variable becomes unexpectedly bound to an irrelevant binding occurrence.

Alpha conversion, or alpha renaming, resolves variable capture. It changes the name of the parameter of a lambda abstraction while preserving the behavior of the function. For example, alpha-converting \(\lambda x.x\) can yield \(\lambda y.y\). Both functions are identity functions and thus have the same semantics but differ in the names of the parameters. A lambda abstraction and a lambda abstraction obtained by alpha-converting the former are alpha equivalent.

A new name of a parameter for alpha conversion must be an identifier that is not in the lambda abstraction. Otherwise, the semantics of the function can change. For example, replacing \(x\) with \(z\) in \(\lambda x.y\) yields \(\lambda z.y\) and is correct alpha conversion. On the other hand, replacing \(x\) with \(y\) yields \(\lambda y.y\), whose semantics differs from that of the original function. Replacing \(x\) with \(z\) in \(\lambda x.\lambda y/x\) is correct since the result is \(\lambda z.\lambda y.z\). However, replacing \(x\) with \(y\) results in \(\lambda y.\lambda y.y\), which changes the semantics.

Function \(\mathit{id}\) takes an expression as an argument and returns the set of all the identifiers in the expression.

\[
\mathit{id}\in \text{Expression}\rightarrow \mathcal{P}(\text{Variable})
\]

\[
\begin{array}{rcl}
\mathit{id}(n) & = & \emptyset \\
\mathit{id}(e_1+e_2) & = & \mathit{id}(e_1)\cup\mathit{id}(e_2) \\
\mathit{id}(e_1-e_2) & = & \mathit{id}(e_1)\cup\mathit{id}(e_2) \\
\mathit{id}(x) & = & \{x\} \\
\mathit{id}(\lambda x.e) & = & \mathit{id}(e)\cup\{x\} \\
\mathit{id}(e_1\ e_2) & = & \mathit{id}(e_1)\cup\mathit{id}(e_2)
\end{array}
\]

An integer lacks identifiers. A variable contains a single identifier that is its name. When an expression is a sum, a difference, or a function application, the union of the identifiers of its subexpressions is the result. A lambda abstraction has the name of its parameter and every identifier in its body.

Alpha conversion is a relation over expressions and expressions. The definition of alpha conversion follows:

\[
\equiv^\alpha \subseteq \text{Expression}\times\text{Expression}
\]

\[
\frac
{ x'\not\in\textit{ids}(\lambda x.e) }
{ \lambda x.e\equiv^\alpha\lambda x'.\lbrack x'/x\rbrack e }
\]

\[
\lambda x.e\equiv^\alpha\lambda x.e
\]

The domain of alpha conversion includes only lambda abstractions. For a given function, it replaces every occurrence of the parameter with a fresh identifier, which never occurs in the function, in the function. The result can be the function itself.

Substitution avoids variable capture through alpha conversion. Before that, a free variable of an expression must be formally defined. Function \(\mathit{fv}\) takes an expression as an argument and returns the set of all the free variables in the expression.

\[
\mathit{fv}\in \text{Expression}\rightarrow \mathcal{P}(\text{Variable})
\]

\[
\begin{array}{rcl}
\mathit{fv}(n) & = & \emptyset \\
\mathit{fv}(e_1+e_2) & = & \mathit{fv}(e_1)\cup\mathit{fv}(e_2) \\
\mathit{fv}(e_1-e_2) & = & \mathit{fv}(e_1)\cup\mathit{fv}(e_2) \\
\mathit{fv}(x) & = & \{x\} \\
\mathit{fv}(\lambda x.e) & = & \mathit{fv}(e)\setminus\{x\} \\
\mathit{fv}(e_1\ e_2) & = & \mathit{fv}(e_1)\cup\mathit{fv}(e_2)
\end{array}
\]

An integer lacks free variables. A variable has a single free variable that is itself. When an expression is a sum, a difference, or a function application, the union of the free variables of its subexpressions is the result. The free variables of a lambda expression equal the free variables of the body except the parameter.

Now, the definition of substitution changes. Since alpha conversion can produce more than one expression, substitution is a relation, but not a function.

Substitution for lambda abstractions only needs changes. All the other cases remain the same.

\[
\lbrack e/x \rbrack\lambda x.e' = \lambda x.e'
\]

\[
\frac
{ x\not=x' \quad
  \lambda x'.e'\equiv^\alpha \lambda x'' .e'' \quad
  x'' \not\in\mathit{fv}(e) }
{ \lbrack e/x \rbrack\lambda x'.e' =
  \lambda x'' .\lbrack e/x \rbrack e'' }
\]

Substitution correctly avoids variable capture. The semantics of FAE has been perfect.

Many sorts of programming language research focus on topics irrelevant to variable capture and alpha conversion. They usually assume alpha conversion to prevent variable capture in their semantics. They typically say that they treat expressions up to alpha conversion. It may seem naive but makes researchers be able to concentrate on important points. Substitution often fits defining semantics better than environments. As the assumption simply removes the concern about variable capture, most researchers use substitution instead of environments to define semantics.

Proof assistants, such as Coq, strictly deals with every insignificant detail. Users cannot simply assume alpha conversion to resolve variable capture. They commonly use De Bruijn indices to overcome the difficulty. Higher-order abstract syntax, which represents variable binding with functions at the level of abstract syntax trees, is another solution. Both topics are beyond the scope of the article.

Interpreters can use substitution, but such interpreters are rare. Assuming alpha conversion does not work for interpreters. Implementing correct substitution requires some amount of effort. Besides, interpreters using environments outperform interpreters using substitution. Maps efficiently implement environments. Both extension and lookup require constant time. On the other hand, applying substitution to an expression whose size is \(n\) requires time complexity of \(O(n)\). The definition of the size of an expression is omitted as it is unimportant.

## Call by Name

The inference rule for function applications under call-by-value semantics follows:

\[
\frac{
  e_1\Downarrow \lambda x.e \quad
  e_2\Downarrow v' \quad
  \lbrack v'/x\rbrack e\Downarrow v
}
{ e_1\ e_2\Downarrow v }
\]

Call-by-name semantics changes the rule not to evaluate the argument and to substitute the parameter with the argument expression.

All the other rules remain the same.

If evaluating an expression under call-by-value semantics results in a value, then call-by-name semantics yields the same value. The following formally rephrases it:

\[
\forall e.\forall v.e\Downarrow v\rightarrow e\Downarrow_l v
\]

It is a corollary of the standardization theorem[^1]. The theorem states that for a given expression, if there is an order of evaluation that results in a value, then normal-order evaluation produces the same value. Normal order is similar to call by name. The only difference is that normal-order evaluation evaluates the body of a function that is never called. Normal order is beyond the scope of the article.

[^1]: John C. Reynolds, *Theories of Programming Languages*, Cambridge, p. 200

The above proposition is true because FAE and LFAE lack side effects. Expressions of languages with side effects vary in the results according to the order of evaluation. If side effects exist, redundant calculations can change states. Under call-by-value semantics, every argument is evaluated once and only once. However, under call-by-name semantics, an argument can be evaluated zero or more times.

The converse of the proposition is false. Some expressions yield results under only call-by-name semantics. Even though the evaluation of an argument raises an error or does not terminate, the function application succeeds if the body does not require the value of the argument. However, the evaluation always happens under the call-by-value semantics.

### Implementing an Interpreter

Making some revision to the LFAE interpreter allows call by name for function applications. The existing interpreter evaluates an argument when it appears as a function of a function application or an operand of an addition or a subtraction. Under the call-by-name semantics, an interpreter must evaluate an argument when the corresponding parameter occurs.

```scala
sealed trait LFAE
case class Num(n: Int) extends LFAE
case class Add(l: LFAE, r: LFAE) extends LFAE
case class Sub(l: LFAE, r: LFAE) extends LFAE
case class Id(x: String) extends LFAE
case class Fun(x: String, b: LFAE) extends LFAE
case class App(f: LFAE, a: LFAE) extends LFAE
```

The abstract syntax remains the same.

```scala
sealed trait LFAEV
case class NumV(n: Int) extends LFAEV
case class CloV(p: String, b: LFAE, e: Env) extends LFAEV

case class Expr(e: LFAE, env: Env)
type Env = Map[String, Expr]
```

The previous interpreter needs the `ExprV` class to represent a delayed evaluation as a value. Now, the result of the `interp` function always differs from a delayed evaluation because any occurrence of a parameter leads to evaluation of the argument. The `Expr` class replaces the `ExprV` class. The `Expr` class takes the same role to the `ExprV` class, but its instance is not considered as a value of LFAE. An environment is a map from a string to an `Expr` instance.

```scala
def lookup(x: String, env: Env): Expr =
  env.getOrElse(x, throw new Exception)

def interp(e: LFAE, env: Env): LFAEV = e match {
  case Num(n) => NumV(n)
  case Add(l, r) =>
    val NumV(n) = interp(l, env)
    val NumV(m) = interp(r, env)
    NumV(n + m)
  case Sub(l, r) =>
    val NumV(n) = interp(l, env)
    val NumV(m) = interp(r, env)
    NumV(n - m)
  case Id(x) =>
    val Expr(e, eEnv) = lookup(x, env)
    interp(e, eEnv)
  case Fun(x, b) => CloV(x, b, env)
  case App(f, a) =>
    val CloV(x, b, fEnv) = interp(f, env)
    interp(b, fEnv + (x -> Expr(a, env)))
}
```

Since the result of `interp` function cannot be `ExprV`, the `strict` function is useless. The `Id` case finds an `Expr` instance from the environment and evaluates the expression in the instance under the environment in the instance. The `App` case creates an `Expr` instance to extend the environment.

### Call by Need

Call by need is semantics that evaluates an argument when the corresponding parameter occurs in the function body, like call by name. The only difference between two semantics is that call-by-need semantics stores a value of the argument after the first evaluation and reuses the value for other occurrences of the parameter. It applies the optimization for LFAE, which uses the option type, to call by name. Two semantics always produces the same result for languages without side effects. Under the presence of side effects, two semantics vary.

Call by need is formalizable as well as call by name. However, to represent storing values of arguments, formalization needs a concept similar to a store of BFAE or MFAE. It complexifies the semantics needlessly. Moreover, an LFAE expression results in the same value regardless of choice between call by name and call by need. Call by need for LFAE is an optimization of call by name for LFAE. Thus, the article omits formalization of call by need. One can find additionally from other articles, such as "An operational semantics of sharing in lazy evaluation."[^2] Since modifying the interpreter to use call by need is trivial, the article omits it as well.

[^2]: Jill Seaman, S.Purushothaman Iyer, *An operational semantics of sharing in lazy evaluation*, Science of Computer Programming, Volume  27, Issue 3, 1996, Pages 289-322, <https://doi.org/10.1016/0167-6423(96)00012-3>.

## Acknowledgments

I thank professor Ryu for giving feedback on the article.
