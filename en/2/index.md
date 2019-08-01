The article introduces Scala. Readers who have already experienced Scala before can skip the article.

## Scala

Professor Odersky and his students in LAMP at EPFL have developed Scala. Scala stands for "**Sca**lable **La**nguage." The ["Growing a Language"](https://www.youtube.com/watch?v=_ahvzDzKdB0) talk given by Guy Steele has influenced Scala. Scala supports both *functional* and *object-oriented* styles and aims a growable language. Later articles will discuss functional programming more.

---

#### Compilers and Interpreters

*Compilers* and *interpreters* are out of the topic but I believe that it is the best point to discuss them.

Compilers are programs that translate one programming language into another language. Usually, the term "compilers " is used for a narrower range of programs: compilers get code written in high-level languages, whom people can easily understand, as input and generate code written in low-level languages like *machine languages* and *bytecode*. Physical machines can directly interpret machine languages and *virtual machines* can directly interpret bytecode. For example, GCC translates C and C++ into machine languages and `javac` translates Java into Java bytecode. Since machines can directly interpret the low-level languages, compiled forms of programs guarantee fast execution. However, when programmers modify their code, they must re-compile the code to execute programs so that checking results immediately after changing the code takes a long time.

On the other hand, interpreters directly interpret code given as input and show results instead of generating new code. Python and JavaScript are typical languages using interpreters. Since given code is a *string*, interpreters do complex procedures including *parsing* at run time and therefore execution takes a long time. However, because changing code does not require compiling, checking results after changing the code takes a short time.

---

Scala compilers compile Scala code into Java bytecode executable by Java virtual machines (JVM). Every Scala project can use the Java standard library and any external Java libraries. Not only using compiled Java code but also mixing Java code and Scala code in a single project is possible. In addition, the Scala standard library supports [conversion](https://www.scala-lang.org/api/current/scala/collection/JavaConverters$.html) between Java *collections* and Scala collections. In practice, interoperability with Java is useful a lot. Lack of Scala libraries is not problematic since libraries from the Java ecosystem are available.

The most recent Scala version in July 2019 is 2.13.0. At the same time, Scala 3, as known as Dotty, is coming. The most recent Dotty version in July 2019 is 0.16.0. The official GitHub repositories are [https://github.com/scala/scala](https://github.com/scala/scala) and [https://github.com/lampepfl/dotty](https://github.com/lampepfl/dotty), respectively.

## Scala in Industry

Unfortunately, Scala is not one of the most popular languages. According to [GitHub Octoverse 2017](https://octoverse.github.com/2017/), Scala was ranked the thirteenth place. The criterion was the number of PRs in one year. [GitHub Octoverse 2018](https://octoverse.github.com/) showed until the tenth place and Scala did not appear on the list. According to [Stackoverflow Developer Survey 2019](https://insights.stackoverflow.com/survey/2019), Scala was ranked the twentieth place.

However, we can find several places using Scala. PLRG at KAIST has developed JavaScript static analyzer [SAFE 2.0](https://github.com/sukyoung/safe) in Scala. Trivially, the official Scala and Dotty compilers have been developed in Scala. In industry, Scala is popular for concurrent programming and distributed computing. [Akka](https://akka.io/) is a concurrent, distributed computing library written in Scala. Many companies have been using Akka. [Apache Spark](https://spark.apache.org/), a well-known library for data processing, also is written in Scala. [Play](https://www.playframework.com/) is a widely-used web framework based on Akka.

In another point of view, Scala is valuable since it is a good introduction to functional programming. Nowadays, functional programming is popular in industry as well as academia. Using one of functional languages like OCaml and Haskell in future is highly possible for undergraduate students studying computer science of these days. Therefore, if you started programming with *imperative* languages like C, Java, and Python, experiencing Scala will increase the choices of your future job.

## Programming in Scala

[The official Scala web site](https://www.scala-lang.org/) provides instructions for installation. The site discusses also possible IDEs for Scala.

### Scala REPL

After installing Scala, type `scala` in a command line to execute the Scala REPL. The term "REPL" stands for "**r**ead, **e**val, **p**rint, and **l**oop." It is a program that reads a line from a user, evaluates the code, prints the result, and goes back to the first. Languages with interpreters used to support REPLs but, nowadays, languages with compilers also supports REPLs. In the Scala REPL, for every line of input, the Scala compiler compiles the code and the REPL shows the result of the execution.

```scala
scala> 0
res0: Int = 0
```

For input *expression* `0`, the resulting *value* is `0` and the type of `0` is `Int`.

```scala
scala> 1 * 1
res1: Int = 1
```

It works well for more complex expressions.

### Variable Declarations

```scala
scala> val x = 2
x: Int = 2
```

To declare a variable, `val` can be used. `val [name] = [expression]` implies that the result of evaluating the expression becomes the thing referred by the name of the variable. The type of `x` is inferred as `Int`.

```scala
scala> x = 3
<console>:12: error: reassignment to val
       x = 3
```

Since `val` declares *immutable* variables, assigning an expression to an already defined variable is impossible. Immutability is the most important property of functional programming.

```scala
scala> val x = 4
x: Int = 4
```

On the other hand, we can declare `x` again because, in the REPL, two `x`'s belong to different scopes. It is impossible to define two local variables of the same name in a single scope.

```scala
scala> val y: Int = x + 1
y: Int = 5
```

Mentioning explicitly the type of a variable is possible. Usually, due to *type inference*, type annotations are unnecessary but they are required in rare cases.

```scala
scala> var z = 6
z: Int = 6

scala> z = 7
z: Int = 7
```

`var` allows to declare *mutable* variables.

```scala
scala> z = "8"
<console>:12: error: type mismatch;
 found   : String("8")
 required: Int
       z = "8"
           ^
```

To assign an expression to a mutable variable, the expression must follow the type of the variable. Functional programming avoids using mutable variables. Actually, and surprisingly, prohibiting mutable variables does not decrease expressivity of programs at all. Therefore, `val` should be used in most cases but, for efficiency, `var` might be used. In the course, if there is no special instruction, students cannot use `var` in their homework.

### Function Declarations

```scala
scala> def add(x: Int, y: Int) = x + y
add: (x: Int, y: Int)Int
```

`def` declares functions. `def [name]([name]: [type], …) = [expression]` is the form. Like functions in mathematics, `return` is unnecessary. The return value of a function call is the result of evaluating the expression at the right-hand side. Since `x + y` has type `Int`, the return type of `add` is also `Int` due to type inference.

```scala
scala> add(4, 5)
res2: Int = 9
```

The return value is correct.

```scala
scala> def add(x: Int, y: Int): Int = x + y
add: (x: Int, y: Int)Int
```

Like variables, it is possible to annotate return types of functions.

```scala
scala> def add(x, y): Int = x + y
<console>:1: error: ':' expected but ',' found.
       def add(x, y): Int = x + y
                ^
```

Unlike some languages including OCaml, types of function *parameters* are essential.

```scala
scala> def f(x: Int) = {
     |   val y = x + 1
     |   val z = x + 2
     |   y * y + z * z
     | }
f: (x: Int)Int
```

If a function needs to use local variables, the body of the function is a sequence of multiple expressions bundled in a pair of curly brackets. Every expression inside the brackets is sequentially evaluated from top to bottom. The result is equal to the result of the last expression.

```scala
scala> def g(x: Int) = {
     |   println(x)
     |   x * x
     |   x
     | }
g: (x: Int)Int

scala> g(10)
10
res3: Int = 10
```

`g` shows the evaluation of bundled multiple expressions more clearly. `g` prints `10`, calculates `x * x`, whose result is discarded, and returns `10`, the result of evaluating `x`.

```scala
scala> val w = {
     |   val a = 5
     |   a + a + 1
     | }
w: Int = 11

scala> add({
     |   val x = 6
     |   x + x
     | }, 0)
res4: Int = 12
```

Since a sequence of bundled expressions is an expression, it can be used anywhere expecting expressions.

### Conditionals

```scala
scala> if (true) 13 else 14
res5: Int = 13
```

*Conditional* expressions use `if` and `else` like many other languages. Evaluating a conditional expression results in a value.

```scala
scala> val a = if (false) 13 else 14
a: Int = 14

scala> var b = 0
b: Int = 0

scala> if (false) b = 14 else b = 15

scala> b
res6: Int = 15
```

In Scala, immutable variables are enough to assign conditional values. On the other hand, imperative languages require mutable variables and reassignment. `if` and `else` in Scala look similar to the ternary operator `? :` in imperative languages but are more general.

```scala
scala> if (true) {
     |   val x = 8
     |   x + x
     | } else {
     |   val x = 9
     |   x * x
     | }
res7: Int = 16
```

Since a sequence of bundled expressions is an expression, conditional expressions can use curly brackets to express complex computation while it is impossible in imperative languages.

### Compiling Scala Code

The Scala compiler `scalac` compiles Scala code into Java bytecode.

```scala
object App {
  def main(args: Array[String]) = println("Hello world!")
}
```

Save the above code into `App.scala` file and type `scala App.scala` and `scala App` in a command line. The console will print "Hello world!". `scalac` compiles `App.scala` and generates `App.class`. `scala` uses JVM to execute `App.class`.

Like Java, the `main` *method* is the entry point of every Scala program. The `main` method must contain code has to be executed.

In practice, like other languages, programmers use build tools for their Scala projects. Like CMake for C and C++ and Ant, Maven, and Gradle for Java, Scala projects usually use SBT.

## Acknowledgments

I thank professor Ryu for giving feedback on the article.
