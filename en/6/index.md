It is the fourth article about functional programming. The last article dealt with the `List` and `Option` types of the Scala standard library and `for` expressions of Scala. This article is about pattern matching. The previous articles have already used pattern matching for lists and options, but the form of the pattern matching was simple. The article discusses the benefits of pattern matching and various patterns available in Scala. The homework of the course requires pattern matching, and understanding the article thus is crucial for the homework. I write the article based on the fifth Scala seminar, "Pattern Matching," of the fall semester in 2018. [The slides](/files/scala/18f/5_pattern.pdf) and [the code](/files/scala/18f/5_pattern.zip) of the Seminar are accessible on-line.

## Algebraic Data Types

It is common to include values of various shapes in a single type.

A natural number is

* zero or
* the successor of a natural number.

A list is

* the empty list or
* the pair of an element and a list.

A binary tree is

* the empty tree or
* a tree containing a root element and two child trees.

An arithmetic expression is

* a number,
* the sum of two arithmetic expressions,
* the difference of two arithmetic expressions,
* the product of two arithmetic expressions, or
* the ratio of two arithmetic expressions.

In general, an arithmetic expression is

* a number,
* an arithmetic expression with a unary operator, or
* two arithmetic expressions with a binary operator.

An expression of the *lambda* *calculus*, which is one of the topics of the course, is

* a variable,
* a function, which is the pair of a variable and an expression, or
* a function application, which is the pair of two expressions.

As the examples show, in computer science, a type often includes values of various shapes. Algebraic data types typically express such types. An algebraic data type is a *sum* *type* of *product* *types*. A product type is a type whose every element is an enumeration of values of types in the same specific order. Tuple types are typical product types. A sum type, whose another name is a *tagged* *union* *type*, has values of multiple types as its values. Unlike a union type, each *variant* of a sum type has a 'tag' to be distinguished from other variants.

For example, an arithmetic expression is

* an integer or
* the sum of two arithmetic expressions.

Therefore, the `AE` type is the sum type of

* the `Int` type tagged with `Num` and
* the `AE * AE` type (the product type of `AE` and `AE`) tagged with `Sum`.

## Defining Algebraic Data Types in Scala

Algebraic data types are common in functional languages. Most functional languages allow users to define new algebraic data types. The following OCaml code defines arithmetic expressions:

```ocaml
type ae =
| Num of int
| UnOp of string * ae
| BinOp of string * ae * ae
```

Scala is functional and object-oriented at the same time. A language defining types of objects and algebraic data types with two different ways is inconsistent and complex. Like many other object-oriented languages, Scala lets programmers define a new type by defining a new class. Types defined with classes can simulate algebraic data types.

`class [class name]` defines a class of a given name. `class A` defines a class whose name is `A`. `A` is a name of the class and a type simultaneously, for defining a class is defining a type. Every instance of class `A` belongs to type `A`. The following code defines the `AE` type, which is a type of algebraic expressions:

```scala
class AE
```

Classes define variants belonging to `AE`. `Num`, `UnOp`, and `BinOp` are types denoting a number, an arithmetic expression with a unary operator, and two arithmetic expressions with a binary operator respectively.

```scala
class AE
class Num
class UnOp
class BinOp
```

However, the code does not inform that `AE` subsumes the variants. Every value of the `Num`, `UnOp`, and `BinOp` types is not a value of the `AE` type.

Defining a class by extending a class makes the latter subsume the former. Code like `class [class name] extends [parent class name]` make the latter class be a parent of the former class. `class A extends B` implies that class `A` is a child class, or a *subclass*, of class `B`, and class `B` is a parent class, or a *superclass*, of class `A`. The `A` class *inherits* from the `B` class. `A` is a subtype of `B`; `B` is a *supertype* of `A`; every element of type `A` is an element of type `B`.

```scala
class AE
class Num extends AE
class UnOp extends AE
class BinOp extends AE
```

`Num`, `UnOp`, and `BinOp` are subtypes of `AE`. Values of the types belong to `AE`.

Instances of the `Num` class cannot express integers whom they denote. In the same manner, instances of `UnOp` and `BinOp` cannot express operators and arithmetic expressions. Class parameters allow objects to store additional information. Form `class [class name](val [parameter name]: [parameter type], …)` specifies class parameters. `val` is unnecessary for some usages but not in the article.

```scala
class AE
class Num(val n: Int) extends AE
class UnOp(val op: String, val e: AE) extends AE
class BinOp(val op: String, val e0: AE, val e1: AE) extends AE
```

The declarations of `Num`, `UnOp`, and `BinOp` denote that their instances respectively store an integer, a string and an arithmetic expression, and a string and two arithmetic expressions.

Calling a *constructor* creates an object. Constructor calls are in the form of `new [class name]([expression], …)`.

```scala
new Num(1): AE
new UnOp("-", new Num(2)): AE
new BinOp("+", new Num(1), new UnOp("-", new Num(2))): AE
```

Type `AE` includes instances of the three child classes of `AE`.

The parameters of a class are the *fields* of the class. The values of arguments used for a constructor call are the values of fields. As method calls need periods, field accesses require periods. Expression `[expressions].[field name]` denotes the value of the field of an object obtained by evaluating the expression.

```scala
new Num(1).n  // 1
new UnOp("-", new Num(2)).op  // "-"
new BinOp("+", new Num(1), new UnOp("-", new Num(2))).op  // "+"
```

The code is still problematic.

```scala
new AE: AE
```

An arithmetic expression cannot be anything other than a number, an arithmetic expression with a unary operator, or two arithmetic expressions with a binary operator. Writing `new AE` is meaningless. Therefore, it must be impossible to construct an instance of the `AE` class directly.

```scala
trait AE
class Num(val n: Int) extends AE
class UnOp(val op: String, val e: AE) extends AE
class BinOp(val op: String, val e0: AE, val e1: AE) extends AE
```

Defining `AE` as a *trait* instead of a class prevents creating an instance of `AE` without the constructors of the child classes. Traits define new types as classes but differ from classes in several aspects. One difference is that traits forbid calling their constructors.

```scala
new AE
// trait AE is abstract; cannot be instantiated
```

Calling the constructor of `AE` results in a compile error because it is a trait.

## Treating Algebraic Data Types in Scala

The section defines function `interpret`, which calculates an integer denoted by a given arithmetic expression, with different ways. For simplicity, assume that `-` is the only possible unary operator, and `+` is the only possible binary operator. Any other operators result in exceptions.

### Dynamic Type Checking

The first approach is checking the type of a given arithmetic expression dynamically, or at run time.

```scala
def interpret(e: AE): Int = {
  if (...) // e is a number
    e.n
  else if (...) // e has unary operator -
    -interpret(e.e)
  else if (...) // e has binary operator +
    interpret(e.e0) + interpret(e.e1)
  else
    throw new Exception
}
```

In Scala, `isInstanceOf` checks whether an object is a value of a type. Expression `[expression].isInstanceOf[[type]]` denotes `true` if the value of the expression belongs to the type and `false` otherwise.

```scala
def interpret(e: AE): Int = {
  if (e.isInstanceOf[Num])
    e.n
  else if (e.isInstanceOf[UnOp] && e.op == "-")
    -interpret(e.e)
  else if (e.isInstanceOf[BinOp] && e.op == "+")
    interpret(e.e0) + interpret(e.e1)
  else
    throw new Exception
}
```

The code results in a compile error. Compilers know only that a value referred by `e` is an element of type `AE` about `e`; fields of `e` are inaccessible. `asInstanceOf`, which inform the compilers that an expression has a particular type, resolves the problem by *casting* *types* explicitly. To type `[expression].asInstanceOf[[type]]`, the compilers ignore a type calculated by themselves and rely on a type given by programmers.

At run time, `asInstanceOf` does not change the value of an expression but checks the type of the value. Evaluating `[expression].asInstanceOf[[type]]` results in a value denoted by the expression if the value belongs to the type but throws an exception otherwise.

```scala
def interpret(e: AE): Int = {
  if (e.isInstanceOf[Num])
    e.asInstanceOf[Num].n
  else if (e.isInstanceOf[UnOp] &&
    e.asInstanceOf[UnOp].op == "-")
    -interpret(e.asInstanceOf[UnOp].e)
  else if (e.isInstanceOf[BinOp] &&
    e.asInstanceOf[BinOp].op == "+")
    interpret(e.asInstanceOf[BinOp].e0) +
      interpret(e.asInstanceOf[BinOp].e1)
  else
    throw new Exception
}

// -1 + 2
interpret(new BinOp(
  "+",
  new UnOp("-", new Num(1)),
  new Num(2)
))
// 1
```

The code is long and complicated despite its simple functionality. Dynamic type checking and explicit type casting occupy most of the code, while real computation requires short code. Besides, such code is error-prone. Programmers give information whom compilers cannot find due to their imprecision---does not mean that the compilers are wrong but signifies that they fail to find all the information---with `asInstanceOf`. However, they may be incorrect. Incorrect information might terminate programs abnormally because of type errors at run time. It is easy to check whether the code is correct as it is short. In contrast, complex types or computation increase the possibilities of errors.

### Method Overloading

Method *overloading* allows defining methods of the same name with different types of parameters. Many object-oriented languages including Scala feature method overloading.

```scala
object Show {
  def show(i: Int): String = i + ": Int"
  def show(s: String): String = s + ": String"
}
Show.show(1)  // "1: Int"
Show.show("1")  // "1: String"
```

Singleton object `Show` has the two `show` methods. Their parameter types respectively are `Int` and `String`, which differ, and the compilation thus succeeds. Each invocation of the `show` method chooses a method proper to the type of the argument.

Is method overloading a correct solution for the `interpret` method?

```scala
object Interpreter {
  def interpret(e: AE): Int =
    throw new Exception("What is it?")
  def interpret(e: Num): Int = e.n
  def interpret(e: UnOp): Int =
    if (e.op == "-") -interpret(e.e)
    else throw new Exception("Only -")
  def interpret(e: BinOp): Int =
    if (e.op == "+")
      interpret(e.e0) + interpret(e.e1)
    else throw new Exception("Only +")
}

Interpreter.interpret(new Num(1))  // 1
```

It seems to work but, alas, does not.

```scala
// -1 + 2
Interpreter.interpret(new BinOp(
  "+",
  new UnOp("-", new Num(1)),
  new Num(2)
))
// java.lang.Exception: What is it?
```

Surprisingly, the first method is invoked because the compile-time types rather than the run-time types of arguments between angle brackets affect dynamic method dispatch. At compile time, `e.e0` in expression `interpret(e.e0)` has type `AE`, and the first method is therefore called, while its type is `UnOp` at run time. As a consequence, method overloading is not a correct strategy to implement the `interpret` method.

### The Visitor Pattern

Object-oriented programmers usually use the *visitor* *pattern* to resolve such problems. The pattern is appropriate for the `interpret` method as well but makes code long and complex and needs some boilerplate code. Despite the disadvantages, it is the most effective solution for languages without pattern matching like Java; it is popular. The article does not discuss the visitor pattern in detail and refers to [an article of English Wikipedia](https://en.wikipedia.org/wiki/Visitor_pattern) instead.

### Pattern Matching

Pattern matching is the best solution for such problems and a typical feature of functional languages. The following code defines the `interpret` function in OCaml with pattern matching:

```ocaml
let rec interpret e =
  match e with
  | Num n -> n
  | UnOp ("-", e0) -> -(interpret e0)
  | BinOp ("+", e0, e1) -> (interpret e0) + (interpret e1)
  | _ -> raise Exception
```

The code is intuitive and concise even for those who are unknowledgeable about OCaml.

Scala features pattern matching as well. Pattern matching requires types defined by *case* *classes* instead of regular classes. Actually, every type is available for pattern matching, but the article does not introduce how to do it. The following code defines arithmetic expressions with case classes:

```scala
trait AE
case class Num(n: Int) extends AE
case class UnOp(op: String, e: AE) extends AE
case class BinOp(op: String, e0: AE, e1: AE) extends AE
```

Compilers do not treat case classes as special cases but desugar them to simple classes. Case classes have a few features whom ordinary classes do not have: pattern matching is possible without additional code; constructing instances does not require the `new` keyword; class parameters do not need the `val` keyword. More exist but are out of the scope of the article.

```scala
def interpret(e: AE): Int = e match {
  case Num(n) => n
  case UnOp(op, e0) =>
    if (op == "-") -interpret(e0)
    else throw new Exception
  case BinOp(op, e0, e1) =>
    if (op == "+") interpret(e0) + interpret(e1)
    else throw new Exception
}

// -1 + 2
interpret(BinOp(
  "+",
  UnOp("-", Num(1)),
  Num(2)
))
// 1
```

The function uses pattern matching. It is more concise than the code using dynamic type checking and safe due to the lack of explicit type casting.

## Advantages of Pattern Matching and Patterns in Scala

### Exhaustivity Checking

Pattern matching checks the exhaustivity of patterns. At run time, a match error occurs when a given value matches none of patterns.

```scala
def interpret(e: AE): Int = e match {
  case UnOp(op, e0) =>
    if (op == "-") -interpret(e0)
    else throw new Exception
  case BinOp(op, e0, e1) =>
    if (op == "+") interpret(e0) + interpret(e1)
    else throw new Exception
}
```

The function lacks the `Num` pattern.

```scala
interpret(Num(3))
// scala.MatchError: Num(3) (of class Num)
```

An argument of type `Num` results in a match error.

Scala compilers check whether patterns are exhaustive and warn if they are not. However, the compilers do not warn about the function, for they cannot know all the child classes of the `AE` trait. Every file can define classes extending `AE`. The unit of compilation is a single file, but it is impossible to find all the child classes by scanning a single file. The `sealed` keyword resolves the problem. A *sealed* class or trait forbids defining its children outside a file defining it. As a result, the compilers identify all the child classes by reading one file and check the exhaustivity of patterns successfully.

```scala
sealed trait AE
case class Num(n: Int) extends AE
case class UnOp(op: String, e: AE) extends AE
case class BinOp(op: String, e0: AE, e1: AE) extends AE

def interpret(e: AE): Int = e match {
  case UnOp(op, e0) =>
    if (op == "-") -interpret(e0)
    else throw new Exception
  case BinOp(op, e0, e1) =>
    if (op == "+") interpret(e0) + interpret(e1)
    else throw new Exception
}
// warning: match may not be exhaustive.
// It would fail on the following input: Num(_)
//       def interpret(e: AE): Int = e match {
//                                   ^
```

The compilers warn about inexhaustive patterns as `AE` is a sealed trait. Exhaustivity checking is beneficial for complex programs. It helps to make safe programs and thus is a crucial strength of pattern matching.

### Constant and Wildcard Patterns

`switch-case` statements divide a given value into multiple cases in imperative languages. Pattern matching is a general form of `switch-case`. The following code is an example using a `switch-case` statement in Java:

```java
String grade(int score) {
  switch (score / 10) {
    case 10: return "A";
    case 9: return "A";
    case 8: return "B";
    case 7: return "C";
    case 6: return "D";
    default: return "F";
  }
}
```

Constant and wildcard patterns exist in Scala. Constant patterns are *literals* like integers and strings. A constant pattern matches a given value if a value denoted by the pattern equals the given value. The underscore denotes the wildcard pattern, which matches every value, and is equivalent to `default` of `switch-case`. The following function rewrites the previous function with pattern matching:

```scala
def grade(score: Int): String =
  (score / 10) match {
    case 10 => "A"
    case 9 => "A"
    case 8 => "B"
    case 7 => "C"
    case 6 => "D"
    case _ => "F"
  }

grade(85)  // "B"
```

### Or Patterns

`switch-case` statements use the fall-through semantics; if `break` does not exist, after executing code corresponding to a case, the flow of the execution moves to code corresponding to the next case. Since the results of cases `10` and `9` are identical, the function can use fall-through.

```java
String grade(int score) {
  switch (score / 10) {
    case 10:
    case 9: return "A";
    case 8: return "B";
    case 7: return "C";
    case 6: return "D";
    default: return "F";
  }
}
```

In contrast, pattern matching disallows fall-through. Instead, *or* patterns give a way to write the same expression only once for multiple patterns. Or patterns are in the form of `[pattern] | [pattern] …`, which is a sequence of multiple patterns with vertical bars in between. `A | B` matches values that match `A` or `B`.

```scala
def grade(score: Int): String =
  (score / 10) match {
    case 10 | 9 => "A"
    case 8 => "B"
    case 7 => "C"
    case 6 => "D"
    case _ => "F"
  }

grade(100)  // "A"
```

### Nested Patterns

Nested patterns are patterns containing patterns. Nested patterns simplify the `interpret` function.

```scala
def interpret(e: AE): Int = e match {
  case Num(n) => n
  case UnOp("-", e0) => -interpret(e0)
  case BinOp("+", e0, e1) =>
    interpret(e0) + interpret(e1)
  case _ => throw new Exception
}
```

As the `UnOp` pattern contains constant pattern `"-"`, the second pattern matches an arithmetic expression only with unary operator `"-"`. Similarly, the third pattern matches an arithmetic expression only with binary operator `"+"`.

It is possible to nest multiple patterns. The `optimizeAdd` function optimizes a given arithmetic expression by eliminating additions of zeros.

```scala
def optimizeAdd(e: AE): AE = e match {
  case Num(_) => e
  case UnOp(op, e0) => UnOp(op, optimizeAdd(e0))
  case BinOp("+", Num(0), e1) => optimizeAdd(e1)
  case BinOp("+", e0, Num(0)) => optimizeAdd(e0)
  case BinOp(op, e0, e1) =>
    BinOp(op, optimizeAdd(e0), optimizeAdd(e1))
}

// 0 + 1 + 2
optimizeAdd(BinOp(
  "+",
  Num(0),
  BinOp("+", Num(1), Num(2))
))
// 1 + 2
```

Assume that `"abs"`, a unary operator, is available. It denotes the absolute value of an operand. Optimizing an arithmetic expression decorated by two consecutive `"abs"` operators results in the arithmetic expression with only one `"abs"` operator.

```scala
def optimizeAbs(e: AE): AE = e match {
  case Num(_) => e
  case UnOp("abs", UnOp("abs", e0)) =>
    optimizeAbs(UnOp("abs", e0))
  case UnOp(op, e0) => UnOp(op, optimizeAbs(e0))
  case BinOp(op, e0, e1) =>
    BinOp(op, optimizeAbs(e0), optimizeAbs(e1))
}

// | | -1 | |
optimizeAbs(UnOp("abs",
  UnOp("abs",
    UnOp("-", Num(1))
  )
))
// | - 1 |
```

A flaw of the implementation is that a value matching `UnOp("abs", e0)` cannot be an argument of `optimizeAbs` directly, and constructing a new `UnOp` instance containing a value matching `e0` is essential. The at sign makes code efficient by binding a value matching to a pattern to a variable. Pattern `[variable] @ [pattern]` makes the variable refer to a value matching the pattern.

```scala
def optimizeAbs(e: AE): AE = e match {
  case Num(_) => e
  case UnOp("abs", e0 @ UnOp("abs", _)) =>
    optimizeAbs(e0)
  case UnOp(op, e0) => UnOp(op, optimizeAbs(e0))
  case BinOp(op, e0, e1) =>
    BinOp(op, optimizeAbs(e0), optimizeAbs(e1))
}
```

Nested patterns and the at sign help to treat values of complex structures easily.

### Unreachable Patterns

A value is likely to match more than one pattern when a pattern matching expression contains multiple nested patterns. Like `switch-case`, pattern matching compares a value to patterns sequentially from top to bottom and selects the first matching pattern. If both a pattern handling a specific case and a pattern handling a general case exist, the former must occur earlier than the latter. However, if the number of patterns is large, the patterns might be in the wrong order so that a value matches an unintended pattern. Scala compilers warn when they find *unreachable* patterns to prevent such code.

```scala
def optimizeAbs(e: AE): AE = e match {
  case Num(_) => e
  case UnOp(op, e0) => UnOp(op, optimizeAbs(e0))
  case UnOp("abs", e0 @ UnOp("abs", _)) =>
    optimizeAbs(e0)
  case BinOp(op, e0, e1) =>
    BinOp(op, optimizeAbs(e0), optimizeAbs(e1))
}
// warning: unreachable code
//         case UnOp("abs", e0 @ UnOp("abs", _)) => optimizeAbs(e0)
//                                                             ^
```

Checking the unreachability of patterns also is a significant advantage of pattern matching. It avoids programs behaving incorrectly because of patterns in the wrong order. However, not every wrong pattern results in an unreachable pattern. In some code, values match unintended patterns, but every pattern is reachable. The compilers do not warn about such code, and programmers thus need to be careful.

### Type Patterns

The `optimizeNeg` function optimizes arithmetic expression by removing two consecutive `"-"` unary operators.

```scala
def optimizeNeg(e: AE): AE = e match {
  case Num(_) => e
  case UnOp("-", UnOp("-", e0)) => e0
  case UnOp(op, e0) => UnOp(op, optimizeNeg(e0))
  case BinOp(op, e0, e1) =>
    BinOp(op, optimizeNeg(e0), optimizeNeg(e1))
}

// -(-(1 + 1))
optimizeNeg(UnOp("-",
  UnOp("-",
    BinOp("+", Num(1), Num(1))
  )
))
// 1 + 1
```

The first `Num(_)` pattern does no more than checking whether a value belongs to type `Num`. A type pattern helps to rewrite the function. Type patterns are in the form of `[variable]: [type]`. If a value belongs to the type, it matches the pattern, and the variable refers to the value. The wildcard pattern can substitute the variable if the variable is unnecessary.

```scala
def optimizeNeg(e: AE): AE = e match {
  case _: Num => e
  case UnOp("-", UnOp("-", e0)) => e0
  case UnOp(op, e0) => UnOp(op, optimizeNeg(e0))
  case BinOp(op, e0, e1) =>
    BinOp(op, optimizeNeg(e0), optimizeNeg(e1))
}
```

Type patterns are useful for dynamic type checking.

```scala
def show(x: Any): String = x match {
  case i: Int => i + ": Int"
  case s: String => s + ": String"
  case _ => x + ": Any"
}

show(1)  // "1: Int"
show("1")  // "1: String"
show(1.0)  // "1.0: Any"
```

`Any` is the *top* type of the Scala type system; every value belongs to `Any`.

Note that type patterns cannot check type arguments of polymorphic types. Using pattern matching against polymorphic types is dangerous.

```scala
def show(x: Any): String = x match {
  case l: List[Int] => l + ": List[Int]"
  case l: List[String] => l + ": List[String]"
  case _ => x + ": Any"
}
// warning: non-variable type argument Int
// in type pattern List[Int] is unchecked
// since it is eliminated by erasure
//         case l: List[Int] => l + ": List[Int]"
//                 ^
// warning: non-variable type argument String
// in type pattern List[String] is unchecked
// since it is eliminated by erasure
//         case l: List[String] => l + ": List[String]"
//                 ^
// warning: unreachable code
//         case l: List[String] => l + ": List[String]"
//                                   ^

show("one" :: Nil)  // "List(one): List[Int]"
```

Although the type of the argument is `List[String]`, it matches the first pattern. As the warnings imply, the JVM uses the *type* *erasure* semantics, and type arguments are therefore not available at run time. A later article will deal with polymorphic types and type erasures in detail.

### Tuple Patterns

The last article has already used tuple patterns. They are in the form of `([pattern], … )`. The `equals` function uses tuple patterns and checks whether two lists are identical.

```scala
def equal(l0: List[Int], l1: List[Int]): Boolean =
  (l0, l1) match {
    case (h0 :: t0, h1 :: t1) =>
      h0 == h1 && equal(t0, t1)
    case (Nil, Nil) => true
    case _ => false
  }

equal(List(0, 1), List(0, 1))  // true
equal(List(0, 1), List(0))  // false
```

### Pattern Guards

A binary (search) tree is

* the empty tree or
* a tree containing an integral root element and two child trees.

```scala
sealed trait BST
case object Empty extends BST
case class Node(root: Int, left: BST, right: BST) extends BST
```

Function `add` takes a tree and an integer as arguments and returns a tree obtained by adding the integer to the tree. If the integer is an element of the given tree, the tree itself is the return value.

```scala
def add(t: BST, n: Int): BST =
  t match {
    case Empty => Node(n, Empty, Empty)
    case Node(m, t0, t1) =>
      if (n < m) Node(m, add(t0, n), t1)
      else if (n > m) Node(m, t0, add(t1, n))
      else t
  }
```

An expression corresponding to the second pattern uses `if-else`. Pattern guards allow adding constraints to patterns. A pattern in the form of `[pattern] if [expression]` matches a value if the value matches the pattern, and the expression results in `true`. The following `add` function uses *pattern* *guards*:

```scala
def add(t: BST, n: Int): BST =
  t match {
    case Empty => Node(n, Empty, Empty)
    case Node(m, t0, t1) if n < m =>
      Node(m, add(t0, n), t1)
    case Node(m, t0, t1) if n > m =>
      Node(m, t0, add(t1, n))
    case _ => t
  }
```

Guarded patterns may be inexhaustive.

```scala
def add(t: BST, n: Int): BST =
  t match {
    case Empty => Node(n, Empty, Empty)
    case Node(m, t0, t1) if n < m =>
      Node(m, add(t0, n), t1)
    case Node(m, t0, t1) if n > m =>
      Node(m, t0, add(t1, n))
  }
```

Compilers do not warn about the inexhaustivity of the code. However, since a pattern handling cases that a given integer is an element of a given tree is missing, a match error occurs in the cases.

### Patterns with Backticks

Function `remove` takes a tree and an integer as arguments and returns a tree obtained by removing the integer from the tree. If the integer is not an element of the tree, the given tree itself is the return value. `removeMin` is a helper function used by `remove`. It returns the pair of the smallest element of a given tree and a tree obtained by removing the element from the tree.

```scala
def removeMin(t: Node): (Int, BST) = {
  t match {
    case Node(n, Empty, t1) =>
      (n, t1)
    case Node(n, t0: Node, t1) =>
      val (min, t2) = removeMin(t0)
      (min, Node(n, t2, t1))
  }
}

def remove(t: BST, n: Int): BST = {
  t match {
    case Empty =>
      Empty
    case Node(m, t0, Empty) if n == m =>
      t0
    case Node(m, t0, t1: Node) if n == m =>
      val (min, t2) = removeMin(t1)
      Node(min, t0, t2)
    case Node(m, t0, t1) if n < m =>
      Node(m, remove(t0, n), t1)
    case Node(m, t0, t1) if n > m =>
      Node(m, t0, remove(t1, n))
  }
}
```

```case Node(`n`, t0, Empty)``` can replace `case None(m, t0, Empty) if n == m`. `case Node(n, t0, Empty)` defines new `n` other than parameter `n` and makes new `n` to refer to the root element; it does not check whether the root element equals `n`. However, backticks prohibit to define new `n` and allow to compare the root element to `n` in the scope.

```scala
def remove(t: BinTree, n: Int): BinTree = {
  t match {
    case Empty =>
      Empty
    case Node(`n`, t0, Empty) =>
      t0
    case Node(`n`, t0, t1: Node) =>
      val (min, t2) = removeMin(t1)
      Node(min, t0, t2)
    case Node(m, t0, t1) if n < m =>
      Node(m, remove(t0, n), t1)
    case Node(m, t0, t1) if n > m =>
      Node(m, t0, remove(t1, n))
  }
}
```

## Applications of Pattern Matching

### Variable Declarations

It is possible to declare variables with pattern matching.

```scala
val (n, m) = (1, 2)
// n = 1, m = 2
val (a, b, c) = ("a", "b", "c")
// a = "a", b = "b", c = "c"
val h :: t = List(1, 2, 3, 4)
// h = 1, t = List(2, 3, 4)
val BinOp(op, e0, e1) = BinOp("+", Num(1), Num(2))
// op = "+", e0 = Num(1), e1 = Num(2)
```

Pattern matching helps to declare variables concisely, but a match error occurs when a pattern does not match a right-hand-side value. It is desirable to use pattern matching only when a guarantee that a pattern must match exists. Since tuple patterns always match tuple values, tuple patterns are typical for variable declarations.

### Anonymous Functions

Function `toSum` takes a list of pairs of two integers as arguments and returns a list whose elements are the sums of the integers in the pairs.

```scala
def toSum(l: List[(Int, Int)]): List[Int] =
  l.map(p => p match {
    case (n, m) => n + m
  })

toSum(List((0, 1), (2, 3), (3, 4)))
// List(1, 5, 7)
```

An anonymous function using parameter `p` as a value matched is verbose. Scala compilers consider an expression in the form of `{ case [pattern] => [expression] … }` as an anonymous function if the expression occurs where they expect a function. The following function is concise:

```scala
def toSum(l: List[(Int, Int)]): List[Int] =
  l.map({ case (n, m) => n + m })
```

An argument that is an anonymous function defined by pattern matching does not require surrounding round brackets. It is a special rule.

```scala
def toSum(l: List[(Int, Int)]): List[Int] =
  l.map { case (n, m) => n + m }
```

The period also is unnecessary. It is not special; Scala allows using methods as infix operators; for example, `Nil.::(0)` and `0 :: Nil` are identical.

```scala
def toSum(l: List[(Int, Int)]): List[Int] =
  l map { case (n, m) => n + m }
```

### `for`

`toSum` can use a `for` expression instead of `map`.

```scala
def toSum(l: List[(Int, Int)]): List[Int] =
  for (p <- l) yield p match {
    case (n, m) => n + m
  }
```

`for` expressions can use pattern matching.

```scala
def toSum(l: List[(Int, Int)]): List[Int] =
  for ((n, m) <- l) yield n + m
```

The code is readable and concise.

## Acknowledgments

I thank professor Ryu for giving feedback on the article. I also thank students who gave feedback on the seminar or participated in the seminar.
