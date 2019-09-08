이번 글은 함수형 프로그래밍에 대한 세 번째 글이다. 지난 글에서는 일급 함수, 익명 함수, 옵션 타입을 다뤘다. 또, 리스트를 다루는 함수들을 일급 함수를 사용하여 일반화하였다. 이번 글에서는 Scala 표준 라이브러리에 정의된 `List`와 `Option` 타입에 대해 살펴보려고 한다. 새로운 내용이라기보다는 지난 글에서 정의한 함수를 사용할 때와 비교하여 어떤 문법적 차이가 있는지를 주로 알아볼 것이니 가벼운 마음으로 읽어도 된다. 그 외에도, 높은 표현력을 가진 Scala의 `for`도 다룰 예정이다. 이번 글은 2018년 가을 학기에 진행한 Scala 세미나 중 세 번째 세미나인 "Working with Scala Collections" 내용을 바탕으로 작성하였다. 세미나 [자료](/files/scala/18f/3_wwsc.pdf), [코드](/files/scala/18f/3_wwsc.zip), [영상](https://youtu.be/vCU79mnD8UE) 모두 온라인으로 이용 가능하니 필요한 사람은 참고하길 바란다.

## `List`

Scala 표준 라이브러리에는 `List` 클래스가 정의되어 있다. 클래스를 정의한 패키지 이름은 `scala.collection.immutable`이지만, 언제나 *들여오기*(import) 없이 사용 가능한 [`scala` 패키지](https://www.scala-lang.org/api/current/scala/index.html)에 리스트를 다루기 위한 타입과 값의 *별명*(alias)이 존재하므로 리스트를 사용하기 위해서 들여오기를 할 필요는 없다.

이전에 정의한 빈 리스트를 나타내는 `Nil`은 Scala 리스트에서도 `Nil`이다. 반면, Scala의 리스트는 `Cons` 대신 `::`을 사용한다. 객체를 만들 때 `::(0, Nil)`처럼 작성해도 되지만, 더 직관적으로 리스트를 표현하기 위해 리스트 클래스에 정의된 `::` 메서드를 사용할 수 있다. 메서드를 사용하기 위한 기본적인 방법은 `Nil.::(0)`과 같이 `[식].[메서드 이름]([식], ...)` 형태를 사용하는 것이지만, Scala는 메서드를 *중위* *연산자*(infix operator)로 사용하는 것도 허용한다. `::`과 같이 쌍점으로 끝나는 이름을 가진 연산자는 오른쪽 결합을 하며, 오른쪽 *피연산자*(operand)가 해당 메서드의 주인이 되기 때문에, `0 :: Nil`은 `Nil.::(0)`과 같은 식이고, `::(0, Nil)`과 같은 결과를 낸다. 마찬가지로, `0 :: 1 :: Nil`은 `Nil.::(1).::(0)`과 같은 식이고, `::(0, ::(1, Nil))`과 같은 결과를 낸다. `0 :: 1 :: Nil`처럼 표현하는 것이 가장 직관적이다. `::`을 사용하는 대신 `List([식], ...)` 꼴을 사용하여 리스트를 만들 수도 있다. `List(0, 1)`은 `0 :: 1 :: Nil`과 같은 결과를 낸다. 어떤 방법으로 표현할지는 자신의 선택이지만, 원소 전체를 나열할 때는 `List(...)` 형태를 사용하고, 이미 존재하는 리스트의 앞에 원소를 덧붙일 때는 `0 :: 1 :: l`과 같이 작성하는 것이 일반적이다.

```scala
// 직접 정의한 리스트
Cons(0, Cons(1, Cons(2, Nil)))

// 표준 라이브러리 리스트
::(0, ::(1, ::(2, Nil)))
Nil.::(2).::(1).::(0)
0 :: 1 :: 2 :: Nil
List(0, 1, 2)
```
리스트를 패턴 대조하는 방법은 이전과 같지만, `Cons` 대신 `::`을 사용해야 한다. 패턴 대조 시에 클래스 이름을 중위 연산자 형태로 사용할 수 있기 때문에 `case h :: t =>`처럼 쓰는 것이 가능하다.

```scala
// 직접 정의한 리스트
Cons(0, Cons(1, Cons(2, Nil))) match {
  case Nil => "foo"
  case Cons(h, t) => "bar"
}

// 표준 라이브러리 리스트
List(0, 1, 2) match {
  case Nil => "foo"
  case h :: t => "bar"
}
```

표준 라이브러리의 리스트는 *타입* *매개변수*(type parameter)를 사용하여 *다형*(polymorphic)으로 정의되었기 때문에, 직접 만든 리스트가 정숫값만을 원소로 가질 수 있는 것과 달리, 임의의 타입에 대한 리스트를 만들고 사용할 수 있다. 리스트의 타입은 단순히 `List`가 아니며, `List[Int]`처럼 리스트의 원소 타입을 함께 표현한다. `List[Int]`는 `Int` 타입의 값만을 원소로 가지는 리스트의 타입이다.

```scala
// 직접 정의한 리스트
Cons(0, Cons(1, Cons(2, Nil))): List

// 표준 라이브러리 리스트
List(): List[Nothing]
List(true): List[Boolean]
List(0, 1, 2): List[Int]
```

`Nothing`은 모든 타입의 *서브타입*(subtype)인 *최하위*(bottom) 타입으로, 어떤 값도 `Nothing` 타입의 값이 아니지만, 빈 리스트는 원소가 없으므로 `List[Nothing]`을 타입으로 가질 수 있다. *매개변수를* *통한* *다형성*(parametric polymorphism)과 *서브타입을* *통한* *다형성*(subtype polymorphism) 모두 타입 체계에서 매우 중요한 개념이며, 나중 글에서 자세히 다룰 예정이다. 이 글의 내용이 잘 이해가 되지 않는다면, `A` 타입의 값을 원소로 하는 리스트의 타입은 `List[A]`라는 것만 알고 넘어가도 괜찮다.

표준 라이브러리의 리스트는 `map`, `filter`, `foldRight`, `foldLeft` 메서드를 가지고 있다. `foldRight`와 `foldLeft`는 *커링*(currying)을 사용하여 정의되었기 때문에 사용할 때 주의해야 한다. 커링은 매개변수를 여러 개 가진 함수를 하나의 매개변수를 가진 함수열로 바꾸는 것을 뜻한다. 또한, `:\`과 `/:` 메서드를 사용해도 `foldRight`와 `foldLeft`를 사용하는 것과 같은 결과를 얻을 수 있다.

```scala
// 직접 정의한 리스트
def inc1(l: List): List = list_map(l, _ + 1)
def odd(l: List): List = list_filter(l, _ % 2 != 0)
def sum(l: List): Int = list_foldRight(l, 0, _ + _)
def product(l: List): Int = list_foldLeft(l, 1, _ * _)

// 표준 라이브러리 리스트
def inc1(l: List[Int]): List[Int] = l.map(_ + 1)
def odd(l: List[Int]): List[Int] = l.filter(_ % 2 != 0)
def sum(l: List[Int]): Int = l.foldRight(0)(_ + _)
def sum(l: List[Int]): Int = (l :\ 0)(_ + _)
def product(l: List[Int]): Int = l.foldLeft(1)(_ * _)
def product(l: List[Int]): Int = (1 /: l)(_ * _)
```

메서드 역시 다양한 타입의 함수를 인자로 받을 수 있다.

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

`list_get`과 `list_getOption`을 정의하여 리스트의 임의의 위치의 원소를 찾은 것처럼, 표준 라이브러리의 리스트도 같은 기능을 제공한다. 표준 라이브러리의 리스트는 함수처럼 사용할 수 있으며, 매개변수로 정수 `n`을 가지고 있고, 결괏값은 리스트의 `n` 번째 원소이다. 처음 정의했던 `list_get` 함수처럼, `n`이 올바르지 않은 값인 경우, 예외가 발생한다. `list_getOption` 함수처럼 `n`이 올바르면 `Some`, 아니면 `None`을 얻고 싶다면, `lift` 메서드를 사용해서 안전하지 않은 함수를 안전한 함수로 바꿀 수 있다.

```scala
// 직접 정의한 리스트
list_get(Cons(0, Cons(1, Cons(2, Nil))), 0)
list_getOption(Cons(0, Cons(1, Cons(2, Nil))), 0)

// 표준 라이브러리 리스트
List(0, 1, 2)(0)
List(0, 1, 2).lift(0)
```

정의한 다른 함수들 역시 메서드로 존재한다.

```scala
// 직접 정의한 리스트
addBack(Cons(0, Cons(1, Nil)), 2)

length(Cons(0, Cons(1, Cons(2, Nil))))
reverse(Cons(0, Cons(1, Cons(2, Nil))))

// 표준 라이브러리 리스트
List(0, 1) :+ 2
List(0, 1) ++ List(2)
List(0, 1, 2).length
List(0, 1, 2).reverse
```

그 밖에도 많은 메서드가 정의되어 있으며, [표준 라이브러리 웹 사이트](https://www.scala-lang.org/api/current/scala/collection/immutable/List.html)에서 메서드 목록을 확인할 수 있다. 이 글에서는 매우 중요한 하나의 메서드만 추가로 소개하려고 한다. `flatMap` 메서드로, `map`과 유사한 역할을 하지만, 인자로 받는 함수가 반드시 어떤 컬렉션을 결과로 내야 한다. 여기서 말하는 컬렉션은 매우 큰 뜻의 컬렉션으로 옵션 타입의 값도 포함한다. (정확히 말하면 `IterableOnce[T]` 타입의 값이다.) `map` 메서드는 인자로 받은 함수를 원소에 적용한 결과를 그대로 리스트에 넣는 것에 비하여, `flatMap` 메서드는 결과로 나온 컬렉션의 원소를 하나씩 꺼내서 리스트에 넣는다. ‘flatMap’이라는 단어를 ‘`map`’의 결과를 ‘평평(flat)’하게 만든다는 뜻으로 이해할 수 있다.

```scala
List(0, 1, 2).flatMap(List(_))  // List(0, 1, 2)
List(0, 1, 2).flatMap(0 to _)  // List(0, 0, 1, 0, 1, 2)

def div100(n: Int): Option[Int] =
  if (n == 0) None else Some(100 / n)
List(0, 1, 2).flatMap(div100)  // List(100, 50)
```

직접 정의한 리스트를 다루는 함수 중 `list_foldLeft`를 제외한 `list_map`, `list_filter`, `list_foldRight`는 꼬리 재귀 함수가 아니다. 따라서, 긴 리스트가 인자로 들어온다면 스택 넘침이 일어날 수 있다. 다행히, 표준 라이브러리의 리스트를 사용할 때는 그런 걱정을 할 필요가 없다. 표준 라이브러리에서는 리스트를 라이브러리 내부에서만 수정할 수 있게끔 정의한다. 이를 바탕으로, `while` 문을 통해 효율적으로 \(O(n)\)만의 시간을 사용하여 스택 넘침 없이 메서드가 작동하도록 구현되어 있다. 물론, 외부에서 사용하는 사용자의 입장에서는 수정 불가능한 리스트이므로, 수정으로 인한 문제가 발생할 걱정 없이 안전하게 리스트를 사용하여 함수형 프로그래밍을 할 수 있다.

## `Option`

`Option` 클래스 역시 Scala 표준 라이브러리에 존재한다. 컬렉션 패키지가 아닌 `scala` 패키지에 바로 정의되어 있으므로 들여오기 없이 사용할 수 있다.

직접 정의한 옵션 타입과 마찬가지로 `None`과 `Some`을 사용한다.

```scala
// 직접 정의한 옵션
None
Some(0)

// 표준 라이브러리 옵션
None
Some(0)
```

패턴 대조 방법도 같다.

```scala
// 직접 정의한 옵션
Some(0) match {
  case None => "foo"
  case Some(n) => "bar"
}

// 표준 라이브러리 옵션
Some(0) match {
  case None => "foo"
  case Some(n) => "bar"
}
```

리스트와 마찬가지로 표준 라이브러리의 옵션 타입은 다형 타입이다. 따라서, `Some`이 정숫값뿐만
아니라 다른 타입의 값도 가질 수 있다. `T` 타입의 원소를 가질 수 있는 옵션 타입은 `Option[T]`이다.

```scala
// 직접 정의한 옵션
Some(0): Option

// 표준 라이브러리 옵션
None: Option[Nothing]
Some(true): Option[Boolean]
Some(0): Option[Int]
```

표준 라이브러리의 옵션 타입은 `map`과 `flatMap` 메서드를 가지고 있다. 두 메서드 모두 다양한 타입의 함수를 인자로 받을 수 있다.

```scala
// 직접 정의한 옵션
option_map(Some(0), n => n * n)
option_flatMap(Some(0), div100)

// 표준 라이브러리 옵션
Some(0).map(n => n * n)
Some(0).flatMap(div100)
```

[표준 라이브러리 웹 사이트](https://www.scala-lang.org/api/current/scala/Option.html)에서 더 많은 메서드를 찾을 수 있다.

## `for`

Scala에도 `for` 문이 존재한다. 사실, 정확히 말하면 Scala의 `for`는 값으로 계산될 수 있는 `for` 식으로, 강력한 표현력을 가지고 있다. 수정 가능한 변수나 객체와 함께 사용되는 반복문인 `while`과 달리, Scala에서 `for`는 함수형 프로그래밍의 본질을 유지하면서도 코드의 가독성을 높인다.

우선, 값으로 계산되지 않는 친숙한 형태의 `for` 문부터 보도록 하겠다. `for`의 문법은 C의 `for` 문법보다는 Java의 향상된 `for` 문('foreach' 문)이나 Python의 `for` 문과 유사하다.

```scala
for (n <- List(0, 1, 2))
  println(n * n)
```

위 코드는 `0`, `1`, `4`를 차례대로 출력한다. 첫 반복에서는 `n`이 `0`을, 두 번째에서는 `1`을, 마지막 반복에서는 `2`를 가리킨다.

`for` 식을 사용하기 위해서는 `yield` 키워드를 사용해야 한다.

```scala
for (n <- List(0, 1, 2))
  yield n * n
```

위 코드의 결과는 `List(0, 1, 4)`이다. `for (...) yield [식]`을 계산한 결과는 매 반복에서 식을 계산한 결과를 컬렉션에 차례대로 넣은 것이다. 그 전체가 하나의 식이므로, 변수의 값이나 인자로 사용하는 등, 식이 올 수 있는 모든 곳에 `for` 식이 올 수 있다.

```scala
val x = for (n <- List(0, 1, 2)) yield n * n
```
Scala에서 `for`는 문법적 설탕에 불과하다. 별도의 `for`를 계산하기 위한 규칙이 있는 것이 아니라, 컴파일 과정의 가장 앞 단계에서 컬렉션의 메서드와 익명 함수를 사용하는 코드로 변환이 이루어진다. 위에서 작성한 두 코드는 각각 `foreach`와 `map`을 사용하는 코드로 바뀐다.

```scala
List(0, 1, 2).foreach(n => println(n * n))
List(0, 1, 2).map(n => n * n)
```

이는 Scala의 `for` 문과 `for` 식을 매우 강력하게 만든다. 사용자가 본인이 새로운 타입을 정의하더라도, `foreach`와 `map` 등의 메서드만 정의해 준다면, `for`를 해당 타입의 값에 사용할 수 있다.

`for`를 통해서 단순히 컬렉션의 원소에 한 번씩 접근하는 것 말고도 더 많은 일을 할 수 있다. `;`을 사용하면 반복을 중첩해서 한 다음, 모든 결과를 한 컬렉션에 넣을 수 있다.

```scala
for (n <- List(0, 1, 2);
     m <- 0 to n)
  yield m * m
```

위 식의 결과는 `List(0, 0, 1, 0, 1, 4)`이다. 아래처럼 단순히 `for` 식을 중첩한다면 같은 결과를 내기 위해서 추가적인 처리가 필요하다.

```scala
for (n <- List(0, 1, 2)) yield
  for (m <- 0 to n)
    yield m * m
// List(Vector(0), Vector(0, 1), Vector(0, 1, 4))
```

*쌍반점*(semicolon)을 사용한 이전 코드가 더 간결하고 읽기 쉬운 것을 확인할 수 있다.

`if`를 사용하면 주어진 조건을 만족하는 원소에만 접근할 수 있다.

```scala
for (n <- List(0, 1, 2) if n % 2 == 0;
     m <- 0 to n)
  yield m * m
```

결과는 `List(0, 0, 1, 4)`이다. `n`이 `1`인 경우에는 반복이 이루어지지 않은 것을 볼 수 있다.

`;`과 `if`는 각각 `flatMap`과 `filter`를 사용하는 코드로 변환된다. 위 코드는 아래처럼 바뀐다.

```scala
List(0, 1, 2)
  .filter(n => n % 2 == 0)
  .flatMap(n => 0 to n)
  .map(m => m * m)
```

옵션 타입도 `foreach`, `map`, `filter`, `flatMap` 메서드를 가지고 있으므로, `for` 문과 식은 옵션 타입에도 사용될 수 있다. 다음 예시는 `for` 식을 옵션 타입에 사용하여 코드를 어떻게 읽기 좋게 만들 수 있는지 보여준다.

```scala
def f(x: Int): Option[Int] = ...
def g(x: Int, y: Int, z: Int, w: Int): Int = ...
```

함수 `f`는 실패할 수도 있는 계산을 하기에 결과 타입이 옵션 타입이다. 함수 `g`는 네 개의 정숫값을 인자로 받는다. `f(0)`, `f(1)`, `f(2)`, `f(3)`을 계산하여 모두 성공한 경우에만 결괏값을 `g`의 인자로 하여 계산하는 경우를 생각해보자. 간단한 방법은 패턴 대조를 사용하는 것이다.

```scala
(f(0), f(1), f(2), f(3)) match {
  case (Some(x), Some(y), Some(z), Some(w)) =>
    Some(g(x, y, z, w))
  case _ =>
    None
}
```

패턴 대조는 튜플에 대해서도 사용할 수 있기 때문에 위 코드는 잘 작동한다. 그러나, `f`가 복잡한 계산을 하는 함수라면, 앞에서 `f`가 이미 실패한 경우에도 계속해서 `f`를 호출하기 때문에, 이는 비효율적인 코드이다. 따라서, 차례대로 패턴 대조를 하여, 먼저 한 계산이 성공한 경우에만 `f`를 호출하는 것이 바람직하다.

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

단순한 작업임에도 코드가 불필요하게 길고 복잡하다. 이는 `flatMap`과 `map` 메서드를 사용함으로써 해결할 수 있다.

```scala
f(0).flatMap(x =>
f(1).flatMap(y =>
f(2).flatMap(z =>
f(3).map(w =>
  g(x, y, z, w)
))))
```

위 코드도 이해하기에 어렵지는 않지만, `for` 식을 사용하면 훨씬 더 코드가 간결하고 명료하다.

```scala
for (x <- f(0);
     y <- f(1);
     z <- f(2);
     w <- f(3))
  yield g(x, y, z, w)
```

## 감사의 말

글을 확인하고 의견을 주신 류석영 교수님, Scala 세미나를 준비할 때 의견 주신 모든 분과 Scala 세미나에 참석하신 모든 분께 감사드립니다.
