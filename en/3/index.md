The article and the next three articles cover basic functional programming. For those who are unfamiliar with functional programming, I highly recommend reading the four articles carefully for the homework of the course. People who are familiar with functional programming can skip or lightly skim through the four articles. The article is based on the first Scala seminar, "Introduction to Functional Programming", of the fall semester in 2018. [The slides](/files/scala/18f/1_itfp.pdf) and [the code](/files/scala/18f/1_itfp.zip) used in the seminar are available on-line and [the video](https://youtu.be/vGaWV_qs0yg) of the seminar can be found on Youtube.

## The Definition of Functional Programming

What is functional programming? English Wikipedia says the following:

> Functional programming is a programming paradigm that treats computation as the evaluation of mathematical functions and avoids changing-state and mutable data.

According to the book "Functional Programming in Scala":

> Functional programming (FP) is based on a simple premise with far-reaching implications: we construct our programs using only pure functions—in other words, functions that have no side effects.

It is the first sentence of the first chapter.

The above two sentences are enough to describe functional programming. Firstly, consider the phrase 'the evaluation of mathematical functions.' In the previous article, I mentioned that everything is an expression in Scala and an expression is evaluated into a value. In the perspective of functional programming, a program is a single (mathematical) expression and the execution of the program is finding a value denoted by the expression. The following code shows how functional programming is different from imperative programming.

```c
int x = 1;
int y = 2;
if (y < 3)
    x = x + 4;
else
    x = x - 5;
```

```ocaml
let x = 1 in
  let y = 2 in
    if y < 3 then x + 4 else x - 5
```

The first code is written in C, which represents imperative languages. Imperative programming mimics a way that computers operate in. During the execution of a program, a *state*, which can be interpreted as a memory of a computer, exists and the execution modifies the state. The execution of the C program has the following steps:

1. A state that both `x` and `y` are uninitialized
2. A state that `x` is `1` and `y` is uninitialized
3. A state that `x` is `1` and `y` is 2
4. Since `y < 3` is `true` under the state of the third step, go to the next line.
5. A state that `x` is `5` and `y` is 2

The second code is written in OCaml, which represents functional languages. A program is an expression and the result of the execution is the result of evaluating the expression. The execution does not require a notion of a state. The execution of the OCaml program has the following steps:

1. Given a fact that `x` equals `1`, evaluate `let y = 2 in if y < 3 then x + 4 else x – 5`.
2. Given a fact that `x` equals `1` and `y` equals `2`, evaluate `if y < 3 then x + 4 else x – 5`.
3. Given a fact that `x` equals `1` and `y` equals `2`, evaluating `y < 3` yields `true` and thus evaluate `x + 4`.
4. Given a fact that `x` equals `1` and `y` equals `2`, evaluate `x + 4`.
5. The result is `5`.

Since the programs are simple, two programs look similar but it is important to understand two different perspectives of what a program is.

Secondly, look at the phrases 'avoids changing-state and mutable data' and 'using only pure functions.' The previous article said that functional programming avoids mutable variables. Functional programming does not modify data including variables and objects. States, which change throughout the execution of programs, do not exist. Due to the lack of states, a function always does the same stuff and for given the same argument, always returns the same value. Such functions are called *pure functions*.

In practice, especially for large-scale projects, using only immutable things in the whole code is usually inefficient. Most real-world functional languages provide mutable variables or structures like `var` of Scala, `ref` of OCaml, and `set!` and `box` of Racket. However, during the course, you will see that functional programming uses immutable things in most cases but can still express a lot of programs without difficulties. I discuss the advantages of immutability in a later section of the article.

## Functional Programming in Industry

Before going deeply into functional programming, let us find how people use functional programming in industry.

### OCaml

Facebook has developed [Infer](https://fbinfer.com/), a static analyzer for Java, C, C++, and Objective-C, in OCaml. Facebook and other companies including Amazon and Mozila use Infer to find bugs statically in their programs. Facebook has developed also [Flow](https://flow.org/), a static type checker for JavaScript.

[Jane Street](https://www.janestreet.com/) is a financial company well-known in the PL community and has developed its own software in OCaml.

According to [the official OCaml web site](http://ocaml.org/learn/companies.html), various companies including Docker use OCaml.

### Haskell

[Haskell Wiki](http://wiki.haskell.org/Haskell_in_industry) mentions that Google, Facebook, Microsoft, Nvidia, and many others use Haskell.

### Erlang and Elixir

Erlang is a functional language for concurrent and parallel computing. Elixir operates on Erlang virtual machines and is used for the same purpose as Erlang. [An article from Code Sync](https://codesync.global/media/successful-companies-using-elixir-and-erlang/) introduced companies including WhatsApp, Pinterest, and Goldman Sachs using Erlang and Elixir.

Programs whose input has complicated and abstract structures like source code and programs that have to be trustworthy or targets concurrent and parallel computing are typically written in functional languages.

## The Advantages of Immutability

The advantages of immutability per se become the advantages of functional programming. The book "Programming in Scala" discusses four strength of immutability:

> First, immutable objects are often easier to reason about than mutable ones, because they do not have complex state spaces that change over time. Second, you can pass immutable objects around quite freely, whereas you may need to make defensive copies of mutable objects before passing them to other code. Third, there is no way for two threads concurrently accessing an immutable to corrupt its state once it has been properly constructed, because no thread can change the state of an immutable. Fourth, immutable objects make safe hash table keys. If a mutable object is mutated after it is placed into a `HashSet`, for example, that object may not be found the next time you look into the `HashSet`.

### Is Easy to Reason about

```scala
val x = 1
...
f(x)
```

At the first line of the code, `x` is `1`. Since `x` is immutable, there is no doubt that `x` is still `1` when `x` is passed as an argument for `f` at the last line of the code.

```scala
var x = 1
...
f(x)
```

On the other hand, if `x` is a mutable variable, one should read every line of code in the middle to find the value of `x` at the time when the function call happens.

For small programs written by one person, whether `x` is immutable or mutable is unimportant. If the program does not expect any future usages, mutable `x` is fine. However, suppose the situation reading the code without any prior knowledge about the code. A big difference between immutable and mutable `x` exists even though there is no person who reads only the first and the last line of course. When `x` is mutable, without tracking every modification of `x` throughout the code, the value of `x` at the last line is unknown. It makes programmers feel difficulties to understand the code so possibly leads to more bugs. The program with immutable `x` does not suffer from such a problem. Remembering only one line of the code is enough to track the value of `x`.

Mutable data structures cause similar problems.

```scala
val x = List(1, 2)
...
f(x)
...
x
```

`List` is an immutable data structure came from the Scala standard library. `x` is always a list containing `1` and `2`.

```scala
import scala.collection.mutable.ListBuffer
val x = ListBuffer(1, 2)
...
f(x)
...
x
```

On the other hand, `ListBuffer` is a mutable data structure in the Scala standard library. It is possible to add an item to or to remove an item from a list referred by `x`. Programmers are not sure about the content of `x` unless they read all the lines in between. Besides, function `f` also is able to change the content of `x`. If one writes a program with a wrong assumption that `f` does not modify `x`, then the program might be buggy.

Mutable global variables make code much harder to understand than mutable local variables.

```scala
def f(x: Int) = g(x, y)
```

The return value of function `f` depends on the value of global variable `y`. If `y` is mutable, `f` is not a pure function and expecting the behavior of `f` is nontrivial. `y` can be declared in any arbitrary file and all files are able to access `y` and to change the value of `y`. In the worst case, an external library defines `y` and source code modifying `y` is not available for reading.

The examples are small and seem artificial but immutability greatly improves maintainability and readability of code in practice, especially for large projects.

### Does not Need Defensive Copies

```scala
val x = ListBuffer(1, 2)
...
f(x)
...
x
```

Since `ListBuffer` creates mutable lists, there is no guarantee that the content of `x` does not change by `f`. If it is necessary to prevent modification, copying `x` is essential.

```scala
val x = ListBuffer(1, 2)
val y = x.clone
...
f(y)
...
x
```

In cases that `x` has many elements and the code is executed multiple times, copying `x` increases the execution time significantly.

In the code, using the `clone` method is enough to copy the list because the list contains only integral values. However, to pass lists containing mutable objects safely to functions, defining additional methods for deep copy is inevitable.

### Guarantees Safe Accesses from Multiple Threads

```scala
val x = ListBuffer(1, 2)
def a() = f(x)
def b() = g(x)
```

Consider two different threads calling functions `a` and `b`, respectively. Functions `f` and `g` are arbitrarily overlapped so that each execution can yield a different result from the result of each other. Some results might be unwanted. Even worse, the `ListBuffer` collection is unsafe for concurrent accesses from multiple threads and therefore the program perhaps crashes during execution.

By using `List` instead of `ListBuffer`, the program becomes safe. Functions `f` and `g` can only read the content of `x` and cannot change the content. For any possible execution orders, the program behaves equally and is safe to call functions `a` and `b` from two different threads.

### Creates Safe Hash Values

The hash value of an object is a key to find the object in hash tables. For the purpose, the same object must have the same hash value all the time. However, the hash value of a mutable object changes when the object is modified. It is problematic when using data structures using hash tables like sets and maps.

```scala
val x = ListBuffer(0)
val y = Set(x, ...)
val z = Map(x -> 0, ...)

y.contains(x)
z(x)

x += 1

y.contains(x)
z(x)
```

The former `y.contains(x)` and `z(x)` yield `true` and `0`, respectively. After appending `1` to `x`, the hash value of `x` changes and therefore `y.contains` results in `false` and `z(x)` throws an exception named `NoSuchElementException`. (Note that `Set` and `Map` of the Scala standard library use hash tables only when they contain more than four elements so that the explanation holds only for `y` and `z` with more than four elements.) Using mutable objects as hash keys is dangerous while immutable objects maintain the same hash values throughout execution.

Immutability has several clear advantages. Immutability is important in functional programming. Functional programs use immutable variables and objects in most cases. However, mind that immutability is not the silver bullet for every program. For example, implementing algorithms like sorts in a functional style is highly inefficient. Use mutable data structures like arrays, mutable variables, and loops like `for` and `while` to implement algorithms. It makes programs much more readable and faster. Choosing a proper programming paradigm to the purpose of a program is the key to write good code.

## Recursion

Repeating the same computation multiple times is a common pattern in programming. Loops allow concise code expressing such cases. However, if everything is immutable, going back to the beginnings of loops does not change any states and therefore it is impossible to apply the same operation on different values for each iteration or to terminate the loops. As a consequence, loops are useless in functional programming. Functional programs use recursive functions instead of loops to rerun computation. A recursive function is a function that calls itself. To do more computation, the function calls itself with proper arguments. Otherwise, it terminates with some return value.

The below `factorial` functions calculate the factorial of a given integral argument. For simplicity, assume that the functions return one for negative integers. The former uses a loop and the latter uses *recursion*.

```scala
def factorial(n: Int) = {
  var i = 1, res = 1
  while (i <= n) {
    res *= i
    i += 1
  }
  res
}

def factorial(n: Int): Int = if (n <= 0) 1 else n * factorial(n - 1)
```

In Scala, recursive functions require explicit return types.

Recursive functions usually reveal their mathematical definitions more clearly then functions using loops.

\[n!=\begin{cases}1 & \text{if } n=0\\n \times (n-1)! & \text{otherwise}\end{cases}\]

The implementation of the `factorial` function using recursion is identical to the mathematical definition of factorial. Recursion allows not only repetitive computation but also concise and intuitive descriptions of mathematical functions. Recursive functions are easier to be verified that they are correct than imperative versions of the functions. Even if mathematical verification is unnecessary, recursion is better for intuitive reasoning of functions than loops. However, some functions become efficient when their implementation uses loops rather than recursion. Selecting an appropriate implementation strategy is crucial.

Recursion has disadvantages: overheads of function calls and *stack overflow*. Most modern CPUs have enough computing power to ignore function call overheads but loops are ideal for functions with short computation time in programs whose performance is extremely important. Stack overflow happens when a stack lacks space due to repetitive function calls. It is a critical problem since it causes immediate termination of execution without yielding meaningful output. Moreover, programs like web servers do not finish their execution so that stack overflow must happen. To resolve the problem, many functional languages try *tail call optimization* to prevent stack overflow. The last part of the article deals with tail call optimization in detail.

## Lists

Almost all programs use lists. Understanding a way to define and to treat lists in functional programming is good practice. This section defines lists and functions dealing with lists. The Scala standard library contains `List` but the section defines its own version of `List`.

### The Definition of a List

A list contains finite elements. An order exists among the elements and duplicates might exist. Simply, a list is an enumeration of a finite number of values. However, defining lists in functional programming requires a recursive definition of a list. The following is the definition of a list:

A list is

* `Nil`: the empty list or
* `Cons`: a pair of a value and a list.

(Lisp, its variants, and some other languages use name `Nil` and `Cons`. To describe functional lists, the names are typically chosen.)

Any nonempty list has at least one element. The list is divided into the first element and the remaining elements. The first element is *head* and the list of the remaining elements is *tail*. The definition describes every list and a thing satisfying the definition is a list. The following expresses a list containing `1`, `2`, and `3` using `Nil` and `Cons`:

```
Cons(1, Cons(2, Cons(3, Nil)))
```

For simple implementation, assume lists contain only integral values as elements. The following Scala code implements lists:

```scala
trait List
case object Nil extends List
case class Cons(head: Int, tail: List) extends List
```

The `List` type is a typical *algebraic data type*.

`trait List` defines type `List`.

`case object Nil extends List` defines value `Nil` and declares that `Nil` is a value of type `List`. Keyword `object` implies that `Nil` is a *singleton object*. The code is *syntactic sugar* of the following:

```scala
class Nil$ extends List
val Nil = new Nil$
```

A singleton object is a unique instance of a class. The class cannot have any instance other than the singleton object. Since all empty lists are identical to the single empty list, rather than defining `Nil` as a class and creating instances whenever the empty list appears, it is more efficient to define the single `Nil` instance and to refer to the instance for any appearance of the empty list. Using single `Nil` is safe because it is immutable.

`case class Cons(head: Int, tail: List) extends List` defines a `Cons` class. Every instance of `Cons` belongs to `List`. An instance has two fields `head`, whose type is `Int`, and `tail`, whose type is `List`.

The following code constructs list values:

```Scala
Nil
Cons(1, Nil)
Cons(1, Cons(2, Nil))
```

Functional lists are *singly linked lists* in the perspective of data structures. Accessing the next element is possible while accessing the previous element is impossible.

### Functions for Lists

An arbitrary list is either the empty list or a nonempty list. Programmers use *pattern matching* to split into the two cases, `Nil` and `Cons`.

```scala
def f(l: List) = l match {
  case Nil => "The empty list"
  case Cons(h, t) => "A pair of an integer and a list"
}
```

Scala requires form `[expression] match { case [pattern] => [expression] … }` for pattern matching. Firstly, the first expression is evaluated. The result is compared to the patterns. The result of the whole expression is the result of an expression corresponding to the first matching pattern. If `l` is `Nil`, the return value is `"The empty list"`. Otherwise, the return value is `"A pair of an integer and a list"`. `h` and `t` respectively refer to the head and tail of `l`. The expression corresponding to the `Cons` pattern may use `h` and `t`.

The `inc1` function takes a list as an argument and returns a list whose elements are one larger than the elements of the given list.

```scala
def inc1(l: List): List = l match {
  case Nil => Nil
  case Cons(h, t) => Cons(h + 1, inc1(t))
}
```

For the given empty list, the function returns the empty list. Otherwise, the return value is a list whose head is one larger than the head of the given list and tail has elements that are one larger than the elements of the tail of the given list.

Define function `square`, which takes a list as an argument and returns a list whose elements are the squares of the elements of the given list. Check the below code after trying to find an answer.

<details><summary>`square` code</summary>
```scala
def square(l: List): List = l match {
  case Nil => Nil
  case Cons(h, t) => Cons(h * h, square(t))
}
```
</details>

The `odd` function takes a list as an argument and returns a list whose every element is an odd integer.

```scala
def odd(l: List): List = l match {
  case Nil => Nil
  case Cons(h, t) =>
    if (h % 2 != 0) Cons(h, odd(t))
    else odd(t)
}
```

For a nonempty list, the function checks whether the head is odd or not. If the head is odd, the resulting list contains the head and its tail is the tail with only odd integers. Otherwise, the head is removed.

Define function `positive`, which takes a list as an argument and returns a list whose every element is a positive integer.

<details><summary>`positive` code</summary>
```scala
def positive(l: List): List = l match {
  case Nil => Nil
  case Cons(h, t) =>
    if (h > 0) Cons(h, positive(t))
    else positive(t)
}
```
</details>

The `length` function calculates the length of a given list.

```scala
def length(l: List): Int = l match {
  case Nil => 0
  case Cons(h, t) => 1 + length(t)
}
```

The length of the empty list is zero. The length of a nonempty list is one larger than the length of its tail.

Define functions `sum` and `product`, which respectively calculate the sum and the product of the elements of a given list.

<details><summary>`sum` code</summary>
```scala
def sum(l: List): Int = l match {
  case Nil => 0
  case Cons(h, t) => h + sum(t)
}
```
</details>

<details><summary>`product` code</summary>
```scala
def product(l: List): Int = l match {
  case Nil => 1
  case Cons(h, t) => h * product(t)
}
```
</details>

Define function `addBack`, which takes a list and an integer as arguments and return a list obtained by appending the integer at the end of the list.

<details><summary>`addBack` code</summary>
```scala
def addBack(l: List, n: Int): List = l match {
  case Nil => Cons(n, Nil)
  case Cons(h, t) => Cons(h, addBack(t, n))
}
```
</details>

Adding an element at the end of a list requires *time complexity* of \(O(n)\). *Space complexity* also is \(O(n)\) since the function creates a new entire list. In contrast, prepending an element at the beginning of a list requires both time and space complexity of \(O(1)\). Therefore, adding an element at the beginning instead of the end of a list is desirable in functional programming.

## Tail Call Optimization

If the last action of a function is calling a function, then the call is a tail call. When a tail call happens, after the call, the *callee* does every computation and thus the local variables of the *caller* have no need to remain. The stack frame of the caller can be destroyed. Most functional languages optimize tail calls. At compile time, compilers check whether calls are tail calls. If a call is a tail call, the compilers generate code that eliminates the stack frame of the caller before the call. They do not optimize non-tail function calls because the local variables of the callers can be used after returning from the callees. If every function call in a program is a tail call, a stack never grows so that the program is safe from stack overflow.

```scala
def factorial(n: Int): Int = if (n <= 0) 1 else n * factorial(n - 1)
```

The previous `factorial` function multiplies `n` and the return value of the recursive `factorial(n -1)` call. The multiplication is the last action. The recursive call is not a tail call. The stack frame of the caller must remain. The following process computes `factorial(3)`:

* `factorial(3)`
* `3 * factorial(2)`
* `3 * (2 * factorial(1))`
* `3 * (2 * (1 * factorial(0)))`
* `3 * (2 * (1 * 1))`
* `3 * (2 * 1)`
* `3 * 2`
* `6`

At most four stack frames coexist. For a large enough argument, a stack grows again and again and finally overflows.

```scala
scala> def factorial(n: Int): Int = if (n <= 0) 1 else n * factorial(n - 1)
factorial: (n: Int)Int

scala> factorial(10000)
java.lang.StackOverflowError
  at .factorial(<console>:12)
```

To implement the function using a tail call, instead of multiplying `n` and `factorial(n - 1)`, the function has to pass both `n` and `n - 1` as arguments and to make the callee multiply them. One can interpret the strategy as passing an intermediate result.

* `factorial(3)`
* `factorial(2, intermediate result = 3)`
* `factorial(1, intermediate result = 3 * 2)`
* `factorial(1, intermediate result = 6)`
* `factorial(0, intermediate result = 6 * 1)`
* `factorial(0, intermediate result = 6)`
* `6`

There is no need to return to the caller. The below code shows the `factorial` function with a tail call. The function needs one more parameter that takes an intermediate result. `factorial(n, i)` computes \(n!\times i\).

```scala
def factorial(n: Int, inter: Int): Int =
  if (n <= 0) inter else factorial(n - 1, inter * n)
```

The function uses the tail call. More precisely, the function is *tail-recursive*. Its last action is calling itself. Unlike most functional languages, Scala cannot optimize general tail calls. Scala optimizes only tail-recursive calls.

Scala compilers generate Java bytecode, whom JVMs execute. The JVMs does not allow to jump to the beginning of another function. In the JVMs, functions can only either return or call functions. The Scala compilers cannot generate optimized code by removing the stack frame of the caller. Instead, they transform tail-recursive calls into loops. `javap` disassembles Java bytecode files. The compiled and disassembled `factorial` function using tail recursion does not call any function but jumps to instructions inside the function.

```java
public int factorial(int, int);
  Code:
     0: iload_1
     1: iconst_0
     2: if_icmpgt     9
     5: iload_2
     6: goto          20
     9: iload_1
    10: iconst_1
    11: isub
    12: iload_2
    13: iload_1
    14: imul
    15: istore_2
    16: istore_1
    17: goto          0
    20: ireturn
```

The function does not result in stack overflow.

(The result is still abnormal due to integer overflow. The `BigInt` type resolves it.)

The transformation not only prevents stack overflow but also removes the overheads of function calls. The downside is that *mutually recursive functions* using tail calls lie beyond the scope of the transformation. The below functions may cause stack overflow.

```scala
def even(n: Int): Boolean = if (n <= 0) true else odd(n - 1)
def odd(n: Int): Boolean = if (n == 1) true else even(n - 1)
```

In Scala, by using *annotations*, programmers ask the compilers to check whether functions are tail-recursive. The annotations prevent non-tail-recursive function made by mistakes.

```scala
import scala.annotation.tailrec
@tailrec def factorial(n: Int, inter: Int): Int =
  if (n <= 0) inter else factorial(n - 1, inter * n)
```

A non-tail-recursive function with the `tailrec` annotation results in a compile error. The annotation does not affect the behaviors of the compilers. Regardless of the existence of the annotation, the compilers always optimize tail-recursive functions. Still, using the annotations is desirable to prevent mistakes.

Calling the tail-recursive version of `factorial` needs the unnecessary second argument. The below code defines a new `factorial` function with one parameter and uses the tail-recursive one as a local function inside the function.

```scala
def factorial(n: Int): Int = {
  @tailrec def aux(n: Int, inter: Int): Int =
    if (n <= 0) inter else aux(n - 1, inter * n)
  aux(n, 1)
}
```

By revising `length`, it becomes tail-recursive:

```scala
def length(l: List): Int = {
  @tailrec def aux(l: List, inter: Int): Int = l match {
    case Nil => inter
    case Cons(h, t) => aux(t, inter + 1)
  }
  aux(l, 0)
}
```

The `sum` and `product` functions can be modified in the same manner.

<details><summary>`sum` code</summary>
```scala
def sum(l: List): Int = {
  @tailrec def aux(l: List, inter: Int): Int = l match {
    case Nil => inter
    case Cons(h, t) => aux(t, inter + h)
  }
  aux(l, 0)
}
```
</details>

<details><summary>`product` code</summary>
```scala
def product(l: List): Int = l match {
  @tailrec def aux(l: List, inter: Int): Int = l match {
    case Nil => inter
    case Cons(h, t) => aux(t, inter * h)
  }
  aux(l, 1)
}
```
</details>

The following is tail-recursive `inc1`.

```scala
def inc1(l: List): List = {
  @tailrec def aux(l: List, inter: List): List = l match {
    case Nil => inter
    case Cons(h, t) => aux(t, addBack(inter, h + 1))
  }
  aux(l, Nil)
}
```

Its result is correct but takes \(O(n^2)\) time while the original version requires only \(O(n)\).

It is possible to implement tail-recursive `inc1` using \(O(n)\) time.

<details><summary>`inc1` code</summary>
```scala
def reverse(l: List): List = {
  @tailrec def aux(l: List, inter: List): List = l match {
    case Nil => inter
    case Cons(h, t) => aux(t, Cons(h, inter))
  }
  aux(l, Nil)
}

def inc1(l: List): List = {
  @tailrec def aux(l: List, inter: List): List = l match {
    case Nil => inter
    case Cons(h, t) => aux(t, Cons(h + 1, inter))
  }
  reverse(aux(l, Nil))
}
```
</details>

## Acknowledgement

I thank professor Ryu for giving feedback on the article. I also thank students who gave feedback on the seminar or participated the seminar.
