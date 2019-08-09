It is the third article about functional programming. The last article dealt with first-class functions, anonymous functions, and option types. It generalized functions treating lists with first-class functions. This article introduces the `List` and `Option` types defined in the Scala standard library. It focuses on syntactic differences between the custom types defined in the previous articles and the types came from the library rather than emphasizing new concepts. Besides, it deals with `for`, which is extremely expressive. I write the article based on the third seminar, "Working with Scala Collections," of the fall semester in 2018. [The slides](/files/scala/18f/3_wwsc.pdf), [the code](/files/scala/18f/3_wwsc.zip), and [the video](https://youtu.be/vCU79mnD8UE) of the seminar are available on-line.

## `List`

The library defines the `List` class. The `scala.collection.immutable` package includes the class. Programmers can use lists without any import statements because *aliases* of types and values required to use lists exist in [the `scala` package](https://www.scala-lang.org/api/current/scala/index.html), whom compilers always automatically import.

Like the custom lists, the empty list is `Nil`.

On the other hand, `::` replaces `Cons`. Expressions like `::(0, Nil)` construct lists.

The `::` method of the list class provides more intuitive representations of lists than the previous code. An expression of form `[expression].[method name]([expression], …)` invokes the method referred by the name. `Nil.::(0)` creates a list with single element `0`. Scala allows using methods as infix operators. Operators whose names end with the colon are right-associative. Their owners are the operands at the right sides. Therefore, `0 :: Nil` and `Nil.::(0)` are the same expression. Both denote the same value as `::(0, Nil)` does. Similarly, `0 :: 1 :: Nil` and `Nil.::(0).::(1)` are identical. They and `::(0, ::(1, Nil))` result in the same list, which contains `0` and `1`. `0 :: 1 :: Nil` is the most intuitive representation among them.

`List` also can create lists. `List([expression], …)` evaluates every expression between the round brackets and constructs a list containing the results. `List(0, 1)` and `0 :: 1 :: Nil` produce the same value. Both representations are popular, but the `List(…)` form fits enumerating all elements well while expressions like `0 :: 1 :: l` are typical when prepending elements in front of existing lists.

```scala
// custom lists
Cons(0, Cons(1, Cons(2, Nil)))

// standard library lists
::(0, ::(1, ::(2, Nil)))
Nil.::(2).::(1).::(0)
0 :: 1 :: 2 :: Nil
List(0, 1, 2)
```

`::` replaces `Cons` during pattern matching as well. Scala allows using class names as infix operators in pattern expressions. `case h :: t =>` is valid.

```scala
// custom lists
Cons(0, Cons(1, Cons(2, Nil))) match {
  case Nil => "foo"
  case Cons(h, t) => "bar"
}

// standard library lists
List(0, 1, 2) match {
  case Nil => "foo"
  case h :: t => "bar"
}
```

The `List` type in the library is *polymorphic*. The custom lists contain only integral values. However, the lists of the library can be lists containing values of any types. The types of lists are not `List`'s. Instead, like `List[Int]`, the types represent the types of the elements of the lists. `List[Int]` is the type of a list containing only integers.

```scala
// custom lists
Cons(0, Cons(1, Cons(2, Nil))): List

// standard library lists
List(): List[Nothing]
List(true): List[Boolean]
List(0, 1, 2): List[Int]
```

`Nothing` is the *bottom* type, which is a *subtype* of every type. Values never belong to the `Nothing` type, but the empty list has type `List[Nothing]` since it does not contain any elements. Both *parametric polymorphism* and *subtype polymorphism* are essential concepts, and thus later articles deal with them. For those who are unfamiliar with the notions, understanding that a list containing values of type `A` has type `List[A]` is sufficient to use lists.

Lists have the `map`, `filter`, `foldRight`, and `foldLeft` methods. `foldRight` and `foldLeft` are curried. *Currying* transforms functions with multiple parameters into sequences of functions with a single parameter. The `:\` and `/:` methods respectively give the same results as `foldRight` and `foldLeft` give.

```scala
// custom lists
def inc1(l: List): List = list_map(l, _ + 1)
def odd(l: List): List = list_filter(l, _ % 2 != 0)
def sum(l: List): Int = list_foldRight(l, 0, _ + _)
def product(l: List): Int = list_foldLeft(l, 1, _ * _)

// standard library lists
def inc1(l: List[Int]): List[Int] = l.map(_ + 1)
def odd(l: List[Int]): List[Int] = l.filter(_ % 2 != 0)
def sum(l: List[Int]): Int = l.foldRight(0)(_ + _)
def sum(l: List[Int]): Int = (l :\ 0)(_ + _)
def product(l: List[Int]): Int = l.foldLeft(1)(_ * _)
def product(l: List[Int]): Int = (1 /: l)(_ * _)
```

The methods can take functions of various types as arguments.

```scala
List(1, 2, 3).map(_ == 1)  // List(true, false, false)
List("", "a", "ab").filter(_.length == 1)  // List("a")

def addBack(l: List[Int], n: Int): List[Int] =
  l.foldRight(List(n))(_ :: _)
def addBack(l: List[Int], n: Int): List[Int] =
  (l :\ List(n))(_ :: _)

def reverse(l: List[Int]): List[Int] =
  l.foldLeft(Nil: List[Int])((t, h) => h :: t)
def reverse(l: List[Int]): List[Int] =
  ((Nil: List[Int]) /: l)((t, h) => h :: t)
```

The `list_get` and `list_getOption` methods find an element at an arbitrary index. The library provides the same functionality. Lists are per se functions. They have single integral parameter `n` and return the `n`th elements of them. Like `list_get`, exceptions occur when `n` is wrong. The `lift` method converts unsafe functions into safe functions returning `None` instead of raising exceptions. The lifted functions do the thing `list_getOption` does.

```scala
// custom lists
list_get(Cons(0, Cons(1, Cons(2, Nil))), 0)
list_getOption(Cons(0, Cons(1, Cons(2, Nil))), 0)

// standard library lists
List(0, 1, 2)(0)
List(0, 1, 2).lift(0)
```

Other methods substitute the roles of other previously defined functions.

```scala
// custom lists
addBack(Cons(0, Cons(1, Nil)), 2)

length(Cons(0, Cons(1, Cons(2, Nil))))
reverse(Cons(0, Cons(1, Cons(2, Nil))))

// standard library lists
List(0, 1) :+ 2
List(0, 1) ++ List(2)
List(0, 1, 2).length
List(0, 1, 2).reverse
```

[The web site of the library](https://www.scala-lang.org/api/current/scala/collection/immutable/List.html) gives the full list of the methods of the `List` class. This article introduces only one additional important method. The `flatMap` method is similar to the `map` method, but its parameter is a function returning a collection. Here, the term 'collection' is broad in its meaning. Even options are collections. (Precisely, the function returns a value whose type is `IterableOnce[T]`.) After applying a given function to the elements of a given list, while `map` puts the results in a list, `flatMap` puts the elements of the results in a list. Its name implies that it **flat**tens the return value of `map`.

```scala
List(0, 1, 2).flatMap(List(_))  // List(0, 1, 2)
List(0, 1, 2).flatMap(0 to _)  // List(0, 0, 1, 0, 1, 2)

def div100(n: Int): Option[Int] =
  if (n == 0) None else Some(100 / n)
List(0, 1, 2).flatMap(div100)  // List(100, 50)
```

`list_foldLeft` is tail-recursive, but `list_map`, `list_filter`, and `list_foldRight` are not. Stacks overflow when lists given as arguments are long. Fortunately, the methods of the library are free from such a problem. The library defines lists to become mutable only when being accessed inside the library. The methods use `while` loops, require time complexity of \(O(n)\), and do not make stacks overflow. Lists are immutable for library users, and therefore using lists maintains immutability and does not harm the functional paradigm.

## `Option`

The library defines the `Option` class as well. The `scala` package includes the class so that import statements are unnecessary.

The names, `None` and `Some`, are identical to the custom options.

```scala
// custom options
None
Some(0)

// standard library options
None
Some(0)
```

Therefore, pattern matching is the same.

```scala
// custom options
Some(0) match {
  case None => "foo"
  case Some(n) => "bar"
}

// standard library options
Some(0) match {
  case None => "foo"
  case Some(n) => "bar"
}
```

Like lists, options are polymorphic. `Some` wraps not only integers but also any values. Values of type `Option[T]` contain values of type `T` or nothing.

```scala
// custom options
Some(0): Option

// standard library options
None: Option[Nothing]
Some(true): Option[Boolean]
Some(0): Option[Int]
```

The class defines the `map` and `flatMap` methods. The two methods can take functions of various types as arguments.

```scala
// custom options
option_map(Some(0), n => n * n)
option_flatMap(Some(0), div100)

// standard library options
Some(0).map(n => n * n)
Some(0).flatMap(div100)
```

[The web site](https://www.scala-lang.org/api/current/scala/Option.html) shows the full list of the methods of the `Option` class.

## `for`

Scala has `for` statements. In fact, `for` expressions, which denote values, exist in Scala. `for` expressions are highly expressive. Unlike `while` loops, which work with mutable variables or objects, `for` of Scala helps programmers to write code in a functional and readable way.

Firstly, let us see `for` statements, which do not produce values. The syntax is similar to the syntax of Java ('foreach' loops) or Python but different from the syntax of C.

```scala
for (n <- List(0, 1, 2))
  println(n * n)
```

The code prints `0`, `1`, and `4`. `n` refers to `0` at the first iteration, `1` at the second iteration, and `2` at the third iteration.

`for` expressions use the `yield` keyword.

```scala
for (n <- List(0, 1, 2))
  yield n * n
```

It results in `List(0, 1, 4)`. The result of `for (…) yield [expressions]` is a collection containing the result of evaluating the expression at each iteration. `for` expressions can appear at any places expecting expressions.

```scala
val x = for (n <- List(0, 1, 2)) yield n * n
```

In Scala, `for` is just syntactic sugar. Instead of giving specific semantics to `for`, syntactic rules transform code using `for` into the code using methods of collections and anonymous functions. The above code becomes code using `foreach` and `map`.

```scala
List(0, 1, 2).foreach(n => println(n * n))
List(0, 1, 2).map(n => n * n)
```

For this reason, `for` statements and expressions are powerful. Any user-defined types can appear in `for` statements or expressions if the types define methods like `foreach` and `map`.

Programs use `for` expressions for other purposes than accessing each element in a collection once. `;` allows nesting iterations. A nested iteration yields values and stores them in a single collection.

```scala
for (n <- List(0, 1, 2);
     m <- 0 to n)
  yield m * m
```

The result is `List(0, 0, 1, 0, 1, 4)`. Nesting `for` expressions does not produce the same result.

```scala
for (n <- List(0, 1, 2)) yield
  for (m <- 0 to n)
    yield m * m
// List(Vector(0), Vector(0, 1), Vector(0, 1, 4))
```

The code using a semicolon is more concise and more readable than the code using the nested expressions.

`if` prevents accessing elements not satisfying a given condition.

```scala
for (n <- List(0, 1, 2) if n % 2 == 0;
     m <- 0 to n)
  yield m * m
```

It results in `List(0, 0, 1, 4)`. `1` is skipped.

The code using `;` and `if` changes into the code using `flatMap` and `filter`.

```scala
List(0, 1, 2)
  .filter(n => n % 2 == 0)
  .flatMap(n => 0 to n)
  .map(m => m * m)
```

Since options have `foreach`, `map`, `filter`, and `flatMap`, options can appear in `for` expressions. The remaining of the article shows how `for` expressions make code easy to understand.

```scala
def f(x: Int): Option[Int] = ...
def g(x: Int, y: Int, z: Int, w: Int): Int = ...
```

Function `f` may fail and thus returns an option. Function `g` has four integral parameters. Consider a program that calculates `f(0)`, `f(1)`, `f(2)`, and `f(3)` and uses the results as arguments of `g` only if every function call has succeeded. Pattern matching allows simple code.

```scala
(f(0), f(1), f(2), f(3)) match {
  case (Some(x), Some(y), Some(z), Some(w)) =>
    Some(g(x, y, z, w))
  case _ =>
    None
}
```

It works well because pattern matching can be done on tuples as well. However, assume that `f` takes a long time to produce the result. Since the program calls `f` four times regardless of whether the previous call has succeeded or not, the program is inefficient. It is desirable to call next `f` only if the last call has returned a value belongs to `Some`. Let us use sequential pattern matching.

```scala
f(0) match {
  case Some(x) => f(1) match {
    case Some(y) => f(2) match {
      case Some(z) => f(3) match {
        case Some(w) =>
          Some(g(x, y, z, w))
        case None => None }
      case None => None }
    case None => None }
  case None => None }
```

It is efficient but verbose and complicated. `flatMap` and `map` improve the code.

```scala
f(0).flatMap(x =>
f(1).flatMap(y =>
f(2).flatMap(z =>
f(3).map(w =>
  g(x, y, z, w)
))))
```

A `for` expression makes code clear and concise.

```scala
for (x <- f(0);
     y <- f(1);
     z <- f(2);
     w <- f(3))
  yield g(x, y, z, w)
```

## Acknowledgments

I thank professor Ryu for giving feedback on the article. I also thank students who gave feedback on the seminar or participated in the seminar.
