It is the second article about functional programming. The previous article discussed immutability, recursion, functional lists, and tail call optimization. This article focuses on functions. It defines a *first-class function* and a way to use *anonymous functions* in Scala. Besides, it generalizes the functions defined in the previous article by using first-class functions. It also introduces *option types*, which handle erroneous situations in a functional way. I write the article based on the second Scala seminar, "First-Class Functions," of the fall semester in 2018. [The slides](/files/scala/18f/2_fcf.pdf), [the code](/files/scala/18f/2_fcf.zip), and [the video](https://youtu.be/5JhkobMgAj0) of the seminar are available on-line.

## First-Class Functions

An entity in a programming language is a first-class citizen if it satisfies the following three conditions:

1. It can be an argument of a function call.
2. It can be a return value of a function.
3. A variable can refer to it.

A first-class citizen can be used as a value. Functions are highly important and treated as values in functional languages. Functions in Scala also are first-class.

```scala
def f(x: Int): Int = x
def g(h: Int => Int): Int = h(0)
g(f)
```

Function `g` has one parameter `h`. The type of `h` is `Int => Int`. An argument passed to `g` is a function that takes one integral parameter and returns an integral value. In Scala, `=>` expresses the types of functions. Functions without parameters have types of form `() => [return type]`. `[parameter type] => [return type]` is the type of a function with a single parameter. Round brackets express the types of functions with more than one parameter: `([parameter type], … ) => [return type]`. Function `f` has one integral parameter and returns an integer so can be an argument for `g`. Evaluating `g(f)` equals to evaluating `f(0)`, which results in `0`.

```scala
def f(y: Int): Int => Int = {
  def g(x: Int): Int = x
  g
}
f(0)(0)
```

Function `f` returns function `g`. Since `f` has return type `Int => Int`, a return value must be a function that takes an integer as a parameter and returns an integer. `g` satisfies the condition. `f(0)` is same as `g` and therefore is a function. `f(0)(0)` is `g(0)`, which returns `0`.

```scala
val h0 = f
val h1 = f(0)
h0(0)(0)
h1(0)
```

Variables can refer to `f` and `f(0)`. `h0` has type `Int => (Int => Int)`. `Int => Int => Int` denotes the same type because function types are right-associative. `h1` refers to the return value of `f(0)` and has type `Int => Int`. Calling variables referring to function values is possible. Both `h(0)(0)` and `h1(0)` are valid expressions and result in `0`.

### Anonymous Functions

In functional programming, functions often appear only once as an argument or a return value. Naming the functions used only once is unnecessary. The meanings of function values are how they act. The parameters and bodies of functions decide the meanings, but the names do not have a role. Naturally, most functional languages provide syntax to define functions without giving them names. Such functions are anonymous functions.

Anonymous functions in Scala have form `([parameter name]: [parameter type], …) => [expression]`. Since curly brackets bundle multiple expressions and create a single expression, the bodies of anonymous functions can have multiple expressions: `(…) => { … }`. Like functions declared by `def`, anonymous functions can be arguments, return values, or values referred by variables. Directly calling them is possible as well.

```scala
((x: Int) => x)(0)
def g(h: Int => Int): Int = h(0)
g((x: Int) => x)
def f(): Int => Int = (x: Int) => x
val h = (x: Int) => x
```

The code does similar things to the previous code but uses anonymous functions.

Anonymous functions need explicit parameter types as named functions do. However, annotating every parameter type is verbose and inconvenient. Scala compilers infer the types of parameters when anonymous functions occur where the compilers expect function types.

```scala
def g(h: Int => Int): Int = h(0)
g(x => x)
```

Since `g` has a parameter of type `Int => Int`, the compilers expect `x => x` to have type `Int => Int`. They infer the type of `x` as `Int`.

```scala
def f0(): Int => Int = x => x
def f1() = (x: Int) => x
def f2() = x => x  // a compile error
```

`f0` has the explicit return type. `Int => Int` is the expected type of `x => x`. The compilers infer the type of `x` as `Int`. While `f1` does not result in a compile error, `f2` is problematic. Since `(x: Int) => Int` specifies the parameter type, the compilers know that its type is `Int => Int`. In contrast, there is no information to infer the type of `x => x`, the body of `f2`.

```scala
val h0: Int => Int = x => x
val h1 = (x: Int) => x
val h2 = x => x  // a compile error
```

As shown in the previous example, `h0` and `h1` are valid, but `h2` is not.

Most cases using anonymous functions are arguments for function calls so that the functions do not require explicit parameter types. However, beginners might not be sure about whether omitting parameter types is allowed or not. Specifying parameter types is safe when it is not assured.

Scala provides one more syntax for anonymous functions. The syntax uses underscores. Underscores help programmers to define concise and intuitive anonymous functions. They make code readable but can appear only in particular situations. Every parameter occurs exactly once in the body of a function in the order. Moreover, the function is not an identity function like `(x: Int) => x`. In such a function, underscores can replace parameters in the body. Otherwise, it is impossible to use underscores to define anonymous functions.

```scala
def g0(h: Int => Int): Int = h(0)
g0(_ + 1)

def g1(h: (Int, Int) => Int): Int = h(0, 0)
g1(_ + _)
```

The compilers *desugar* `_ + 1` and obtain `x => x + 1`. `_ + _` becomes `(x, y) => x + y`. The compilers automatically create parameters as many as underscores and substitute the underscores with the parameters. The mechanism clearly shows why the restriction exists.

```scala
val h0 = (_: Int) + 1
val h1 = (_: Int) + (_: Int)
```

Underscores can have explicit types.

In the code, the compilers cannot infer parameter types and therefore require explicit parameter types to succeed compiling.

The transformation happens for the shortest expression containing underscores. Expressing anonymous functions with complex bodies using underscores is tricky.

```scala
def f(x: Int): Int = x
def g1(h: Int => Int): Int = h(0)
def g2(h: (Int, Int) => Int): Int = h(0, 0)
g(f(_))
g1(f(_ + 1))  // a compile error
g2(f(_) + _)
g2(f(_ + 1) + _)  // a compile error
```

As intended, `f(_)` becomes `x => f(x)`, and `f(_) + _` becomes `(x, y) => f(x) + y`. (Actually, there is no need to write `g(f(_))` because it is equal to `g(f)`.) On the other hand, `f(_ + 1)` becomes `f(x => x + 1)` but not `x => f(x + 1)`. `f(_ + 1) + _` becomes `y => f(x => x + 1) + y` but not `(x, y) => f(x + 1) +y`. 

Like type inference of parameter types, novices may not be sure about how anonymous functions with underscores change. I recommend using normal anonymous functions without underscores for those who are not confident about the mechanism of underscores.

### Closures

Functions are values in some non-functional languages also. For example, C provides *function pointers*. Like other pointers, function pointers can be arguments, return values, and referred by variables.

```c
int f(int x) {
    return x;
}

int g(int (*h)(int)) {
    return h(0);
}

int (*i())(int) {
    return f;
}

int main() {
    g(f);
    int (*h)(int) = f;
    i()(0);
}
```

Functions in functional languages are much more expressive than function pointers in C. Functions are *closures* in functional languages, but function pointers are not closure. Closures are function values that capture *environments*---states---when they are defined. The bodies of closures may have *free variables*, and their environments stores values referred by the free variables.

```scala
def makeAdder(x: Int): Int => Int = {
  def adder(y: Int): Int = x + y
  adder
}
```

The definition of `adder`, `def adder(y: Int): Int = x + y`, does not bind `x`. `x` is a free variable. However, the code is correct.

```scala
val add1 = makeAdder(1)
add1(2)
val add2 = makeAdder(2)
add2(2)
```

`add1` and `add2` refer to the same `adder` function, but the former returns an integer one larger than an argument, and the latter returns an integer two larger than an argument. The results of `add(1)` and `add(2)` are `3` and `4`, respectively. It is possible because the closures capture the environments when they are created. `add1` refers to a thing like `(adder, x = 1)` instead of simple `adder`. Similarly, `add2` is actually `(adder, x = 2)`. Since the environment of `add1` stores the fact that `x` is `1`, `add1(2)` results in `3`. Under the environment of `add2`, `x` denotes `2`, and thus `x + y`, or add2(2)`, is `4`.

Function pointers in C do not allow such application. However, one can simulate closures using function pointers. It requires some efforts.

<details><summary>C closure implementation</summary>
```c
#include <stdio.h>
#include <stdlib.h>

struct closure {
    int (*f)(struct closure *, int);
    void *env;
};

int call_closure(struct closure *c, int arg0) {
    return c->f(c, arg0);
}

int adder(struct closure *c, int y) {
    return ((int *) (c->env))[0] + y;
}

struct closure *makeAdder(int x) {
    struct closure *c = malloc(sizeof(struct closure));
    c->f = adder;
    c->env = malloc(sizeof(int));
    ((int *) (c->env))[0] = x;
    return c;
}

int main() {
    struct closure *add1 = makeAdder(1);
    struct closure *add2 = makeAdder(2);

    int n1 = call_closure(add1, 2);
    int n2 = call_closure(add2, 2);

    printf("%d %d\n", n1, n2);  // 3 4
}
```
</details>

In the course, students learn how to implement interpreters for languages with closures. Implementing interpreter helps to understand the concept of a closure, which captures an environment.

## First-Class Functions and Lists

The section shows how first-class functions allow generalization of the functions defined in the previous article.

### map

```scala
def inc1(l: List): List = l match {
  case Nil => Nil
  case Cons(h, t) => Cons(h + 1, inc1(t))
}

def square(l: List): List = l match {
  case Nil => Nil
  case Cons(h, t) => Cons(h * h, square(t))
}
```

`inc1` increases every element of a given list by one, and `square` squares every element. The two functions are remarkably similar. To make the similarity clearer, let us rename the functions to `g`.

```scala
def g(l: List): List = l match {
  case Nil => Nil
  case Cons(h, t) => Cons(h + 1, g(t))
}

def g(l: List): List = l match {
  case Nil => Nil
  case Cons(h, t) => Cons(h * h, g(t))
}
```

The only difference is the first argument of `Cons` in the third line: `h + 1` versus `h * h`. By adding one parameter, the functions become entirely identical.

```scala
def g(l: List, f: Int => Int): List = l match {
  case Nil => Nil
  case Cons(h, t) => Cons(f(h), g(t, f))
}
g(l, h => h + 1)

def g(l: List, f: Int => Int): List = l match {
  case Nil => Nil
  case Cons(h, t) => Cons(f(h), g(t, f))
}
g(l, h => h * h)
```

In the article, I call the function `list_map`. An argument and the return value have elements **map**ped by a given function.

```scala
def list_map(l: List, f: Int => Int): List = l match {
  case Nil => Nil
  case Cons(h, t) => Cons(f(h), list_map(t, f))
}
```

`inc1` and `square` can be redefined using `list_map`.

```scala
def inc1(l: List): List = list_map(l, h => h + 1)
def square(l: List): List = list_map(l, h => h * h)
```

An underscore makes `inc1` concise.

```scala
def inc1(l: List): List = list_map(l, _ + 1)
```

Implement the `incBy` function, which takes a list and an integer as arguments and increases every element of the list by the given integer. Use `list_map`.

<details><summary>`incBy` code</summary>
```scala
def incBy(l: List, n: Int): List = list_map(l, h => h + n)
def incBy(l: List, n: Int): List = list_map(l, _ + n)
```
</details>

### filter

Let us compare `odd` and `positive`.

```scala
def odd(l: List): List = l match {
  case Nil => Nil
  case Cons(h, t) =>
    if (h % 2 != 0) Cons(h, odd(t))
    else odd(t)
}

def positive(l: List): List = l match {
  case Nil => Nil
  case Cons(h, t) =>
    if (h > 0) Cons(h, positive(t))
    else positive(t)
}
```

They look similar. Rename the functions and add a parameter. The functions become identical. I call the function `list_filter`. The function **filter**s unwanted elements in an argument.

```scala
def list_filter(l: List, f: Int => Boolean): List = l match {
  case Nil => Nil
  case Cons(h, t) =>
    if (f(h)) Cons(h, list_filter(t, f))
    else list_filter(t, f)
}
```

`odd` and `positive` can be redefined using `list_filter`.

```scala
def odd(l: List): List = list_filter(l, h => h % 2 != 0)
def positive(l: List): List = list_filter(l, h => h > 0)
```

Underscores make the functions concise.

```scala
def odd(l: List): List = list_filter(l, _ % 2 != 0)
def positive(l: List): List = list_filter(l, _ > 0)
```

Implement the `gt` function, which takes a list and an integer as arguments and filters elements less than or equal to the given integer out from the list. Use `list_filter`.

<details><summary>`gt` code</summary>
```scala
def gt(l: List, n: Int): List = list_filter(l, h => h > n)
def gt(l: List, n: Int): List = list_filter(l, _ > n)
```
</details>

### foldRight

Let us compare `sum` and `product` without tail recursion.

```scala
def sum(l: List): Int = l match {
  case Nil => 0
  case Cons(h, t) => h + sum(t)
}

def product(l: List): Int = l match {
  case Nil => 1
  case Cons(h, t) => h * product(t)
}
```

After renaming the names to `g`, two differences exist: `0` versus `1` and `h + g(t)` versus `h * g(t)`. By adding two parameters, an initial value and a function taking `h` and `g(t)` as arguments, the functions become identical. I call the function `list_foldRight`. The function appends an initial value at the **right** side of a list and **fold**s the list from the **right** side using a given function.

```scala
def list_foldRight(l: List, n: Int, f: (Int, Int) => Int): Int = l match {
  case Nil => n
  case Cons(h, t) => f(h, list_foldRight(t, n, f))
}
```

`sum` and `product` can be redefined using `list_foldRight`.

```scala
def sum(l: List): Int = list_foldRight(l, 0, (h, gt) => h + gt)
def product(l: List): Int = list_foldRight(l, 1, (h, gt) => h * gt)
```

They may use underscores for conciseness.

```scala
def sum(l: List): Int = list_foldRight(l, 0, _ + _)
def product(l: List): Int = list_foldRight(l, 1, _ * _)
```

The following gives an intuitive interpretation of the function:

```scala
  list_foldRight(List(a, b, .., y, z), n, f)
= f(a, f(b, .. f(y, f(n, z)) .. ))

  list_foldRight(List(1, 2, 3), 0, +)
= +(1, +(2, +(3, 0)))

  list_foldRight(List(1, 2, 3), 1, *)
= *(1, *(2, *(3, 1)))
```

Implement `length` with `list_foldRight`.

<details><summary>`length` code</summary>
```scala
def length(l: List): List = list_foldRight(l, 0, (h, gt) => 1 + gt)
```
</details>

### foldLeft

Let us compare tail-recursive `sum` and `product`.

```scala
def sum(l: List): Int = {
  @tailrec def aux(l: List, inter: Int): Int = l match {
    case Nil => inter
    case Cons(h, t) => aux(t, inter + h)
  }
  aux(l, 0)
}

def product(l: List): Int = l match {
  @tailrec def aux(l: List, inter: Int): Int = l match {
    case Nil => inter
    case Cons(h, t) => aux(t, inter * h)
  }
  aux(l, 1)
}
```

After renaming, there are two differences: `inter + h` versus `inter * h` and `0` versus `1`. Similarly, adding two parameters makes the functions identical.

```scala
def list_foldLeft(l: List, n: Int, f: (Int, Int) => Int): Int = {
  @tailrec def aux(l: List, inter: Int): Int = l match {
    case Nil => inter
    case Cons(h, t) => aux(t, f(inter, h))
  }
  aux(l, n)
}
```

I call the function `list_foldLeft`. Its semantics is different from `list_foldRight`. While `list_foldRight` appends an initial value at the right side and folds a list from the right side, `list_foldLeft` prepends an initial value at the **left** side and **fold**s a list from the **left** side. The following gives an intuitive interpretation:

```scala
  list_foldLeft(List(a, b, .., y, z), n, f)
= f(f( .. f(f(n, a), b), .. , y), z)

  list_foldLeft(List(1, 2, 3), 0, +)
= +(+(+(0, 1), 2), 3)

  list_foldRight(List(1, 2, 3), 1, *)
= *(*(*(1, 1), 2), 3)
```

The order traversing a list does not affect the results of `sum`, `product`, and `length`. Both `list_foldRight` and `list_foldLeft` can express the functions.

```scala
def sum(l: List): Int = list_foldLeft(l, 0, _ + _)
def product(l: List): Int = list_foldLeft(l, 1, _ * _)
def length(l: List): Int = list_foldLeft(l, 0, (inter, h) => inter + 1)
```

On the other hand, the order is important for function such as `addBack` and `reverse`. Using one of `list_foldRight` and `list_foldLeft` is more efficient than using the other. `list_foldRight` fits `addBack` and `list_foldLeft` fits `reverse`. (The following code is incorrect because of types. Consider it as a conceptual example.)

```scala
def addBack(l: List, n: Int): List =
  list_foldRight(l, Cons(n, Nil), (h, gt) => Cons(h, gt))
def addBack(l: List, n: Int): List =
  list_foldRight(l, Cons(n, Nil), Cons)
def reverse(l: List): List =
  list_foldLeft(l, Nil, (inter, h) => Cons(h, inter))
```

`list_map`, `list_filter`, `list_foldRight`, and `list_foldLeft` are powerful functions. The four functions offer concise implementation for most procedures dealing with lists. In most functional languages, libraries provide functions similar to the four functions. The `List` class of the Scala standard library defines `map`, `filter`, `foldRight`, and `foldLeft` methods. The next article introduces the `List` class and its methods.

## Option Types

Consider the `list_get` function, which takes a list and integer `n` as arguments and returns the `n`th element of the list. The case when `n` is negative or exceeds the length of a list is troublesome. Throwing exceptions is a widely used solution in imperative languages.

```scala
def list_get(l: List, n: Int): Int =
  if (n < 0)
    throw new Exception("Negative index.")
  else l match {
    case Nil =>
      throw new Exception("Index out of bound.")
    case Cons(h, t) =>
      if (n == 0) h else list_get(t, n - 1)
  }
```

It is simple and effective, but the program terminates abnormally if *exception handlers* do not exist at call sites of the function. Most type systems do not check exceptions. They do not enforce programmers to handle exceptions. For this reason, Java has introduced *checked exceptions*, whom compilers check. However, programmers usually do not adequately handle exceptions but use only conventional `try-catch` statements to make programs pass type checking. Many people have criticized the concept of a checked exception. Another problem of throwing exceptions is that exception handling is not local. Exceptions spread through function call stacks so that the *control flow* of programs suddenly jumps to the positions of exception handlers. It disturbs programmers to understand code. Implementing `list_get` without exceptions is desirable.

```scala
def list_get(l: List, n: Int): Int =
  if (n < 0) null
  else l match {
    case Nil => null
    case Cons(h, t) =>
      if (n == 0) h else list_get(t, n - 1)
  }
```

The first attempt is using `null`. `null` is a value that denotes that it does not refer to any existing object. In Java and therefore Scala, `Int` is a primitive type, and `null` is not an element of `Int`. The code is invalid. Even though we assume that `null` belongs to `Int`, without checking whether a return value is `null`, using the return value may lead to `NullPointerException`. Like exceptions, `null` is beyond the scopes of type systems of Java and Scala. Some modern languages including Kotlin have introduced *nullable types* and *non-null types* to make programs safe from `NullPointerException`. The concept of a nullable type is similar to an option type, the subject of the section.

```scala
def list_get(l: List, n: Int): Int =
  if (n < 0) -1
  else l match {
    case Nil => -1
    case Cons(h, t) =>
      if (n == 0) h else list_get(t, n - 1)
  }
```

The second attempt is using a particular error-indicating value, `-1` for example. The strategy has an obvious problem: at call-sites, programs cannot judge whether lists contain `-1` as elements or indices are wrong. It can be successful for certain purposes but does not fit the `list_get` function in general.

```scala
def list_getOrElse(l: List, n: Int, default: Int): Int =
  if (n < 0) default
  else l match {
    case Nil => default
    case Cons(h, t) =>
      if (n == 0) h else list_getOrElse(t, n - 1, default)
}
```

Instead of using a fixed particular value, specifying default values for failures at call sites is possible. It works well when an appropriate default value exists. However, when checking failures is per se important, the new strategy is as bad as the previous strategy. There is no way to distinguish an element and a default value.

Functional languages provide option types to handle erroneous situations safely without any mutation. Many languages including Scala use `Option` as the name, but some call them `Maybe`. As the name implies, an option type represents an optional existence of a value. The article defines an option type for `Int` and functions treating values of the type.

```scala
trait Option
case object None extends Option
case class Some(n: Int) extends Option
```

The code is similar to the code defining `List`, `Nil`, and `Cons`. A value of the `Option` type either is `None` or belongs to `Some`. `None` is a value that does not denote any value and similar to `null`. It indicates a problematic situation. Like `Nil`, it is a singleton object defined by `object`. `Some` constructs a value that denotes that a value exists. It is similar to a reference to a real object and indicates that computation succeeds without exceptions.

The following defines `list_getOption` using the `Option` type:

```scala
def list_getOption(l: List, n: Int): Option =
  if (n < 0) None
  else l match {
    case Nil => None
    case Cons(h, t) =>
      if (n == 0) Some(h) else list_getOption(t, n - 1)
  }
```

For wrong indices, the return value is `None`. Otherwise, the function packs an element inside `Some` to make the return value.

Define the `div100` function, which takes an integer as an argument and returns an optional value to handle a division by zero safely.

<details><summary>`div100` code</summary>
```scala
def div100(n: Int): Option =
  if (n == 0) None else Some(100 / n)
```
</details>

Pattern matching allows programmers to deal with optional values by distinguishing the `None` and the `Some` cases. Like the functions for lists, common patterns handling optional values exist. It is desirable to define functions generalizing the patterns.

```scala
def option_map(opt: Option, f: Int => Int): Option = opt match {
  case None => None
  case Some(n) => Some(f(n))
}
```

Consider computation that might fail. After the computation, one wants to do additional computation only if the computation has succeeded and to do nothing otherwise. The situation is the typical usage of the `option_map` function. It takes an optional value and a function as arguments and applies the function to a value wrapped in the optional value only if the value belongs to `Some`.

Define the `getSquare` function, which takes a list and integer `n` as arguments and returns the square of the `n`th element of the list. The return type of the function must be `Option`. Use `list_getOption` and `option_map`.

<details><summary>`getSquare` code</summary>
```scala
def getSquare(l: List, n: Int): Option =
  option_map(list_getOption(l, n), n => n * n)
```
</details>

In this time, consider a situation that additional computation also can fail. It is the place for the `option_flatMap` function. It takes a function whose return type is `Option` as the second argument. If every computation succeeds, the result belongs to `Some`. Otherwise, it is `None`.

Define the `getAndDiv100` function, which takes a list and integer `n` as arguments and returns an integer obtained by dividing `100` by the `n`th element of the list. The function must return an optional value. Use `list_getOption`, `div100`, and `option_flatMap`.

<details><summary>`getAndDiv100` code</summary>
```scala
def getAndDiv100(l: List, n: Int): Option =
  option_flatMap(list_getOption(l, n), div100)
```
</details>

Option types are powerful tools to handle erroneous cases in a functional way. Functional programming uses lists, first-class functions, anonymous functions, and optional values a lot.

## Acknowledgement

I thank professor Ryu for giving feedback on the article. I also thank students who gave feedback on the seminar or participated in the seminar.
