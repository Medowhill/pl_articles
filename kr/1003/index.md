이 글에서는 Odersky가 이야기한 전문가에게 가장 도움이 되는 Scala 3의 새로운 기능 3가지에 대하여 설명한다.

## 3위: 대조 타입

기존의 패턴 대조는 항을 받아, 항을 실행 시간에 값으로 계산한 뒤, 그 값에 맞는 패턴을 찾아, 패턴이 가리키는 항을 계산한 값을 결과로 낸다. 즉, 패턴 대조는 항에서 항으로 가는 개념이다. Scala 3에 새롭게 추가된 *대조* *타입*(match type)은 타입에서 타입으로 가는 패턴 대조이다. 대조 타입은 타입을 받아, 컴파일 시간에 해당 타입에 맞는 패턴을 찾아, 해당 패턴이 가리키는 타입을 결과로 낸다. 문법은 기존의 항에 대한 패턴 대조와 동일하다.

```scala
type Elem[X] = X match {
  case String => Char
  case Array[t] => t
  case Iterable[t] => t
}

'1': Elem[String]
1: Elem[Array[Int]]
"one": Elem[Iterable[String]]
```

대조 타입이 굳이 필요할까 싶지만, 대조 타입은 매우 강력한 기능이다. 그 예시로, Scala 3의 튜플 타입이 얼마나 정확한지 보려고 한다. Scala 3의 튜플 타입은 *불균질* *리스트*(heterogeneous list; HList) 형태로 구현되어 있다. 일반적으로 우리가 생각하는 리스트는 모든 원소가 같은 타입을 가지지만, 튜플 타입은 각 원소가 각각의 타입을 유지하고 있는 리스트로 생각할 수 있다. 단, 불균질 리스트는 어디까지나 타입을 표현하기 위한 것으로, 실행 시간에는 Scala 2와 마찬가지로 `TupleN` 타입을 사용하도록 컴파일 되므로 성능이 저하될 걱정은 하지 않아도 된다.

```scala
scala> (1, '2') ++ ("3", 4.0)
val res0: (Int, Char, String, Double) = (1,2,3,4.0)

scala> (1, '2', "3").head
val res1: Int = 1

scala> (1, '2', "3").tail
val res2: (Char, String) = (2,3)
```

임의의 길이의 두 튜플을 접합할 수 있으며, 접합한 후에도 원래 가지고 있던 타입의 정확도가 그대로 유지된다. 튜플의 *머리*(head)와 *꼬리*(tail)도 타입을 그대로 보존하면서 얻을 수 있다.

```scala
scala> (1, '2', "3").size
val res3: Int = 3

scala> (1, '2', "3").size: 3
val res4: Int = 3
```

임의의 튜플의 크기를 구할 수 있으며, 크기의 타입은 단순히 `Int`가 아닌 정확한 리터럴 타입으로 나오기 때문에, 크기가 3인 튜플의 크기는 `3`을 타입으로 가진다.

```scala
scala> (1, '2')(0)
val res5: Int = 1

scala> (1, '2')(1)
val res6: Char = 2

scala> (1, '2')(2)
1 |(1, '2')(2)
  |         ^
  |         index out of bounds: 2
```

더 나아가 튜플의 임의의 위치에 있는 원소를 얻을 수 있다. 이때도 각 원소의 타입이 정확히 보존되며, 튜플의 크기를 넘어가는 위치가 인자로 주어질 경우에는 실행 시간 오류가 아닌 컴파일 오류가 발생한다.

튜플 타입이 이렇게 높은 타입 정확도를 가질 수 있는 것은 튜플 타입이 대조 타입을 사용하기 때문이다. 튜플의 구현은 [scala.Tuple](https://github.com/lampepfl/dotty/blob/master/library/src/scala/Tuple.scala)에서 볼 수 있다. 이 글에서는 중요한 부분만 살펴보겠다.

```scala
sealed abstract class *:[+H, +T <: Tuple] extends NonEmptyTuple
```

우선 비어있지 않은 튜플은 머리의 타입 `H`와 튜플의 서브타입인 꼬리의 타입 `T`를 타입 매개변수로 받는다.

```scala
type Head[X <: NonEmptyTuple] = X match {
  case x *: _ => x
}

type Tail[X <: NonEmptyTuple] <: Tuple = X match {
  case _ *: xs => xs
}

sealed trait NonEmptyTuple extends Tuple {
  ...
  inline def head[This >: this.type <: NonEmptyTuple]: Head[This] = ...
  inline def tail[This >: this.type <: NonEmptyTuple]: Tail[This] = ...
  ...
}
```

`head`와 `tail` 메서드의 결과 타입은 `Head`와 `Tail`이라는 대조 타입을 사용하여 지정되어 있기 때문에, 각 길이의 튜플 타입마다 메서드를 따로 정의할 필요 없이 모든 비어있지 않은 튜플에 대해서 한 번만 정의하는 것으로 처리할 수 있다.

```scala
type Concat[X <: Tuple, +Y <: Tuple] <: Tuple = X match {
  case Unit => Y
  case x1 *: xs1 => x1 *: Concat[xs1, Y]
}

type Size[X <: Tuple] <: Int = X match {
  case Unit => 0
  case x *: xs => S[Size[xs]]
}

sealed trait Tuple extends Any {
  ...
  inline def ++[This >: this.type <: Tuple](that: Tuple): Concat[This, that.type] = ...
  inline def size[This >: this.type <: Tuple]: Size[This] = ...
  ...
}
```

튜플의 접합과 길이도 비슷한 방식을 통해서 정의하였기에, 각 길이의 튜플 타입마다 정의할 필요 없이, 모든 튜플에 대해서 한 번만 정의해도 충분하다. `Size`를 정의하기 위해 사용한 `S` 타입은 [scala.compiletime](https://github.com/lampepfl/dotty/blob/master/library/src/scala/compiletime/package.scala)에 정의된 타입으로, `type S[X <: Int] <: Int`가 그 정의이며, 컴파일러가 자동으로 1 더 큰 정수에 해당하는 리터럴 타입으로 바꾸어준다. 즉, S[1]과 2는 같은 타입이다.

```scala
import scala.compiletime._
def f(x: 2): S[1] = x
```

```scala
type Elem[X <: Tuple, N <: Int] = X match {
  case x *: xs =>
    N match {
    case 0 => x
    case S[n1] => Elem[xs, n1]
  }
}

sealed trait NonEmptyTuple extends Tuple {
  ...
  inline def apply[This >: this.type <: NonEmptyTuple](n: Int): Elem[This, n.type] = ...
  ...
}
```

튜플의 *사영*(projection) 역시 대조 타입을 사용하여 비어있지 않은 튜플에 대해 한 번만 정의되었다.

이 절은 Dotty 웹 사이트의 ['Match Types' 글](https://dotty.epfl.ch/docs/reference/new-types/match-types.html)을 참고하여 작성하였으며, 대조 타입의 사용 예시에 대해서만 다루었으니, 더 자세한 대조 타입의 의미를 알고 싶은 사람은 링크의 글을 읽는 것을 추천한다.

## 2위: 타입 클래스 도출

*타입* *클래스* *도출*(type class derivation)은 타입 클래스를 제공하고자 하는 라이브러리 개발자에게 매우 유용한 기능이다. 타입 클래스를 사용할 때, 사용자는 해당 타입 클래스의 인스턴스를 직접 만들어야 한다. 그러나, 대부분의 경우, 간단한 구조를 가진 경우 클래스의 타입 클래스 인스턴스를 만드는 일은 단순하지만 귀찮은 작업이다. Scala 3에서 도입된 타입 클래스 도출 기능을 사용하면, 라이브러리 개발자가 타입 클래스 인스턴스를 만들기 위한 코드를 제공함으로써, 라이브러리를 사용하는 고객들은 매우 쉽게, 코드 몇 글자를 추가하는 것만으로도 구현한 경우 클래스의 타입 클래스 인스턴스를 만들 수 있다.

```scala
trait Show[T] {
  def show(t: T): String
}

object Show {
  def derived[T: scala.deriving.Mirror.Of]: Show[T] = ...
}
```

위와 같이 정의한 타입 클래스 `Show`를 생각해보자. `Show` 단독 객체는 `derived`라는 이름의 메서드를 가지고 있으며, 해당 메서드는 `scala.deriving.Mirror.Of[T]` 타입의 알려진 매개변수를 가지고 있고 `Show[T]` 타입의 타입 클래스 인스턴스를 결과로 낸다.

```scala
case class ISB(i: Int, s: String, b: Boolean) derives Show
```

이제 라이브러리 사용자는 자신이 정의한 경우 클래스의 가장 뒤에 `derives Show`를 추가하는 것만으로 `ISB` 타입을 `Show` 타입 클래스에 속하게 만들 수 있다. 컴파일 시, 컴파일러는 `ISB` 단독 객체 안에 `Show[ISB]` 타입의 알려진 객체를 추가한다.

```scala
object ISB {
  ...
  given as Show[ISB] =
    Show.derived[ISB] given ISB.asInstanceOf[
      scala.deriving.Mirror.Product {
        type MirroredMonoType = ISB
        type MirroredType = ISB
        type MirroredLabel = "ISB"
        type MirroredElemTypes = (Int, String, Boolean)
        type MirroredElemLabels = ("i", "s", "b")
      }
    ]
  ...
}
```

볼 수 있듯이, 컴파일러는 `scala.deriving.Mirror.Of[ISB]` 타입의 항을 만들어내서 `Show.derived[ISB]` 메서드의 알려진 인자로 사용한다. 주어진 인자를 가지고 올바른 기능을 하는 `Show[ISB]` 객체를 만들어내는 것은 `derived` 메서드의 일로, 라이브러리 개발자가 구현할 부분이다. `derived` 메서드를 올바르게 구현하기 위해서는 Scala 3의 메타 프로그래밍에 대한 지식이 필요하며, 이 글에서는 이 이상 다루지 않겠다.

이 절은 Dotty 웹 사이트의 ['Typeclass Derivation' 글](https://dotty.epfl.ch/docs/reference/contextual/derivation.html)을 참고하여 작성하였으며, `derived` 메서드를 구현하는 방법을 알고 싶은 사람은 링크의 글을 읽는 것을 추천한다.

## 1위: 모든 곳에 함수를

Scala는 함수형 언어임에도, Scala 2에서는 함수와 메서드의 표현력이 같지 않았다. 메서드는 *종속적*(dependent)이거나, 다형이거나, 암묵적 매개변수를 가질 수 있었으나, 함수는 세 가지 모두 불가능했다.

```scala
def f(x: AnyRef): x.type = x
def g[T](x: T): T = x
def h(implicit x: Int) = x
```

Scala 3에서는 종속적 함수, 다형 함수, 알려진 매개변수를 가진 함수를 모두 정의하고 사용할 수 있으며, 세 가지 기능 중 둘 이상을 가진, 예를 들면 알려진 매개변수를 가진 다형 함수 같은, 함수를 만들고 사용할 수 있다.

```scala
val f: (x: AnyRef) => x.type = x => x
val y = "1"
f(y): y.type  // "1": y.type

val g: [T] => T => T = [T] => (x: T) => x
g(1)  // 1: Int
g[String]("1")  // "1": String

val h: given Int => Int = given Int => the[Int]
given as Int = 1
h  // 1: Int

trait C {
  type T
  def f[S](x: S): T
}
val fgh: [S] => (x: C) => given S => x.T =
  [S] => (x: C) => given (y: S) => x.f[S](y)
val x = new C { type T = Int; def f[S](x: S) = x.toString.toInt }
given as String = "1"
fgh(x)  // 1: x.T
fgh[String](x)  // 1: x.T
fgh[Double](x)  // Error: no implicit argument of type Double
                // was found for parameter of given Double => x.T
```

일반적인 메서드의 타입이 주어진 함수 타입으로 *에타* *팽창*(eta expansion)될 수 있다면 메서드가 에타 팽창된다. 이제 종속적 메서드는 종속적 함수로, 알려진 매개변수를 가진 메서드는 알려진 매개변수를 가진 함수로 에타 팽창된다. 다만, 확인해본 결과 다형 메서드는 다형 함수로 에타 팽창되지 않고 에러가 발생하는데, 아직 다형 함수 관련해서 구현하지 않은 기능이 꽤 있으며, 다형 메서드의 에타 팽창 역시 [추후 구현 예정](https://github.com/lampepfl/dotty/issues/6927)이라고 한다.

```scala
def f(x: AnyRef): x.type = x
val _f: (x: AnyRef) => x.type = f

def g[T](x: T): T = x
val _g: [T] => T => T = g  // Error
                     // ^
                     // Found:    Any => Any
                     // Required: PolyFunction{apply: [T](x$1: T): T}

def h given Int = the[Int]
val _h: given Int => Int = h
```

이 절은 Dotty 웹 사이트의 ['Dependent Function Types' 글](https://dotty.epfl.ch/docs/reference/new-types/dependent-function-types.html), ['Polymorphic Function Types' 글](https://dotty.epfl.ch/blog/2019/06/11/16th-dotty-milestone-release.html#polymorphic-function-types), ['Implicit Function Types' 글](https://dotty.epfl.ch/docs/reference/contextual/implicit-function-types.html)을 참고하여 작성하였다.
