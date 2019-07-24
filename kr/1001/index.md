이 글에서는 Odersky가 이야기한 초심자에게 가장 도움이 되는 Scala 3의 새로운 기능 3가지에 대하여 설명한다.

## 3위: `new` 없이 객체 생성

Scala 2에서는 일반적인 클래스의 객체를 생성하기 위해 생성자를 호출할 때 `new` 키워드를 사용해야 했다.

```scala
class A(x: Int)
val a = new A(1)
```

`new` 키워드를 사용하지 않으려면 그냥 클래스 대신 *경우* *클래스*(case class)를 사용하면 된다.

```scala
case class A(x: Int)
val a = A(1)
```

물론 경우 클래스는 단순한 *문법* *설탕*(syntactic sugar)에 불과하기 때문에, 실제로는 아래와 같은 코드가 만들어진다. (실제로는 더 많은 코드가 만들어지나 여기서 하고자 하는 이야기와는 관계가 없기에 생략하였다.)

```scala
class A(x: Int)
object A {
  def apply(x: Int): A = new A(x)
}
val a = A(1)
```

컴파일 과정에서 `A`는 메서드의 이름이 아니므로 `apply`가 자동으로 삽입되어 `A(1)`은 `A.apply(1)`로 변환된다.

경우 클래스를 사용하지 않고, 위 코드를 직접 작성해도 `new` 키워드 없이 객체를 만들 수 있다.

`new` 키워드를 사용하는 것은 대부분의 경우 불필요하게 코드를 길게 만들 뿐이기에, 많은 프로그래머는 단순히 객체 생성 코드를 보기 좋게 만들기 위해서 클래스를 경우 클래스로 정의하고는 했다.

Scala 3에서는 경우 클래스가 아닌 클래스도 `new` 없이 객체를 생성하는 것을 허용한다. 모든 클래스에 대해 `apply` 메서드를 자동으로 만들어내는 것은 기존 코드에서 *애매한*(ambiguous) 메서드 오버로딩과 메서드 *덮어쓰기*(overriding) 충돌을 일으켰기 때문에, 경우 클래스와 달리 메서드 호출의 타입 검사 방식을 수정하여 `new` 없이 객체를 생성할 수 있게 하였다. 기존에 `f(args)`를 타입 검사할 때는 아래와 같은 과정을 거쳤다.

1. `f`가 `args`의 타입에 적용 가능한 메서드 이름인지 확인
2. 1이 실패하면, `f`가 가리키는 값이 `args`의 타입에 적용 가능한 `apply` 메서드를 가지고 있는지 확인
3. 1과 2가 실패하면, `f`가 `p.m` 형태이며 `p`에 대한 *암묵적* *변환*(implicit conversion) `c`가 존재하여 `c(p).m(args)`가 가능한지 확인

Scala 3에서는 한 가지 과정을 더 추가하였다.

4. 1, 2, 3이 실패하면, `f`가 타입 이름이며 `new f`가 `args`의 타입에 적용 가능한지 확인‘

따라서, Scala 3에서는, 애매함이 발생하지 않는 경우, 아래 코드와 같이 일반적인 클래스의 객체를 `new` 키워드 없이 생성할 수 있다.

```scala
class A(x: Int)
val a = A(1)
```

*타입* *매개변수*(type parameter)를 가진 *다형*(polymorphic) 클래스에 대해서도 마찬가지로 적용된다.

```scala
class B[X](x: X)
val b0 = B(1)
val b1 = B[Int](1)
```

이 변경 사항은 더 읽기 좋은 코드를 가능하게 하고, 클래스의 구체적인 구현을 가린다는 점에서 좋다고 할 수 있다.

이 절은 Dotty 웹 사이트의 ['Creator Applications' 글](http://dotty.epfl.ch/docs/reference/other-new-features/creator-applications.html)을 참고하여 작성하였다.

## 2위: 최상단 정의

Java 가상 기계에서 작동하는 Scala 특성상, *최상단*(top-level)에는 클래스만 정의할 수 있다. 이는 C나 Python 등의 언어에서 함수나 변수도 최상단에 정의할 수 있는 것과 비교하여 불편한 점이다. 이런 한계를 극복하기 위해서 Scala 2에서는 *패키지* *객체*(package object)를 정의하는 것을 허용하였다.

```scala
package object p {
  val x: Int = 0
  def m(x: Int): Int = x
  type T = Int
}
```

`package object` 키워드를 사용해서 패키지 객체를 만들 수 있다. 위 코드를 `A.scala`에 저장한 뒤 아래 코드를 `B.scala`에 저장하고 두 파일을 함께 컴파일 하면 문제없이 되는 것을 볼 수 있다.

```scala
class C {
  p.m(p.x): p.T
}
```

`p` 패키지에 속하는 클래스에서는 바로 접근할 수 있다. 따라서 아래 코드를 `B.scala`에 저장해도 똑같이 잘 컴파일 된다.

```scala
package p

class C {
  m(x): T
}
```

컴파일러는 `A.scala`를 컴파일 할 때 패키지 `p` 안에 `` `package` ``라는 이름을 가진 *단독* *객체*(singleton object)를 만들고 그 안에 패키지 객체가 가지고 있는 정의를 넣는다. (엄밀히 말하면, 객체 역시 최상단에 존재할 수 없으므로 `O`라는 이름의 단독 객체가 컴파일 될 때, 컴파일러는 `O$`라는 이름의 클래스를 정의하고 `O$` 클래스 파일을 불러올 때 `O$` 클래스의 객체를 만들어 `O$` 클래스의 `MODULE$`이라는 이름의 *정적*(static) 필드가 그 객체를 가리키게 만든다. 코드에서 `O`에 접근하는 것은 `O$.MODULE$`에 접근하는 것을 통해 이루어진다.)

Scala 3에서는 패키지 객체 없이 바로 최상단에 변수, 메서드, 타입을 정의할 수 있다. 따라서,  더는 패키지 객체가 필요하지 않으며, Scala 3에서 패키지 객체는 *사용* *자제*(deprecated) 상태이고 추후에 삭제될 예정이다.

```scala
val x: Int = 1
def m(x: Int): Int = x
type T = Int
```

```scala
class A {
  m(x): T
}
```

두 코드를 각각 `A.scala`와 `B.scala`에 저장하고 함께 컴파일 하면 잘 컴파일 된다. Scala 2와 유사하게 컴파일러는 `B$package`라는 이름의 최상단 단독 객체를 만들어 그 안에 다른 최상단 정의를 넣는다.

이 절은 Dotty 웹 사이트의 ['Dropped: Package Objects' 글](https://dotty.epfl.ch/docs/reference/dropped-features/package-objects.html)을 참고하여 작성하였다.

## 1위: 열거형

*열거형*(enumeration, enum)은 이름 붙은 값들을 원소로 가지는 타입이다. C나 Java에서 열거형을 직접 정의하는 것을 허용하는 것과 달리 Scala 2에서는 열거형을 직접 정의할 수는 없었고, 단독 객체와 상속을 통해서 흉내 낼 수 있었다. 아래는 C에서 열거형을 사용한 예시이다.

```c
enum color { color_red, color_green, color_blue };

enum color red = color_red;
```

Scala 2에서는 다음과 같이 쓸 수 있다.

```scala
sealed trait Color
object Color {
  object Red extends Color
  object Green extends Color
  object Blue extends Color
}

val red: Color = Color.Red
```

Scala 3에서는 `enum` 키워드를 사용하여 직접 열거형을 정의할 수 있다.

```scala
enum Color { case Red, Green, Blue }

val red: Color = Color.Red
```

`enum` 역시 문법 설탕이므로 컴파일 시에 클래스와 단독 객체 등을 사용하는 것으로 바뀌며, `values`, `valueOf`, `ordinal`과 같은 메서드를 자동으로 정의하므로 문자열에서 열거형 값을 만들거나 열거형 값을 정숫값으로 변환하는 데 사용할 수 있다.

```scala
Color.values  // Array(Red, Green, Blue): Array[Color]
val red = Color.valueOf("Red")  // Red: Color
red.ordinal  // 0: Int
```

명시적으로 `java.lang.Enum`을 상속함으로써 Java의 열거형이 제공하는 기능도 그대로 사용할 수 있다.

```scala
enum Color extends java.lang.Enum[Color] { case Red, Green, Blue }
Color.Red compareTo Color.Green  // -1: Int
```

위 코드에서 `Color.Red`가 가지고 있는 `compareTo` 메서드는 Java 열거형을 상속하였기에 자동으로 정의된 것이다.

Scala 3의 열거형은 클래스를 통해서 표현된다는 것에서 짐작할 수 있듯, 매우 강력한 표현력을 가지고 있어, 매개변수, 필드, 메서드를 가질 수 있고, *대수적* *데이터* *타입*(algebraic data type; ADT), 더 나아가 *일반화된* *대수적* *데이터* *타입*(generalized ADT; GADT)을 정의하는 데 사용될 수 있다.

다음은 매개변수, 필드, 메서드를 가지는 열거형 `Planet`이다.

```scala
enum Planet(val mass: Double, radius: Double) {
  private val G = 6.67E-11
  def gravity = G * mass / (radius * radius)

  case Mercury extends Planet(3.3E23, 2.4E6)
  case Venus   extends Planet(4.9E24, 6.1E6)
  case Earth   extends Planet(6.0E24, 6.4E6)
}

val earth: Planet = Planet.Earth
earth.mass  // 6.0E24: Double
earth.radius  // Error
earth.gravity  // 9.7705078125: Double
```

주의해야 할 점은, 경우 클래스와 달리 열거형의 매개변수는 자동으로 필드가 되지 않기 때문에, 필드로 사용할 매개변수는 클래스를 정의할 때처럼 `val` 키워드를 붙어야 한다는 것이다.

이제 열거형을 통해서 ADT를 정의하는 방법을 알아보자. Scala 2에서는 ADT를 경우 클래스를 사용하여 아래와 같이 정의하였다.

```scala
sealed trait Option[+T]
case object None extends Option[Nothing]
case class Some[T](t: T) extends Option[T]
```

열거형으로는 다음과 같이 정의할 수 있다.

```scala
enum Option[+T] {
  case None extends Option[Nothing]
  case Some[T](t: T) extends Option[T]
}
```

열거형 선언 시에도 타입 매개변수를 정의하고 *가변성*(variance)을 설정할 수 있는 것을 볼 수 있다. 이렇게 정의한 옵션 타입은 `Option.None`과 `Option.Some`으로 사용해야 한다는 점만 제외하면 기존의 옵션 타입과 동일하다.

```scala
enum Option[+T] {
  case None
  case Some(t: T)
}
```

위 코드도 정상적으로 컴파일 되며, 동일하게 동작한다. `extends`를 명시적으로 사용하지 않아도 자동으로 상속이 이루어지고 타입 인자가 유추된 것을 볼 수 있다.

위에서 본 것과 마찬가지로 `Option` 열거형 안에도 필드와 메서드를 정의할 수 있으며, 경우 클래스를 사용한 것과 마찬가지로 패턴 대조를 사용할 수 있다.

```scala
enum Option[+T] {
  case None
  case Some(t: T)

  def map[A](f: T => A): Option[A] = this match {
    case None => None
    case Some(t) => Some(f(t))
  }
}

Option.None.map(x => x)  // None: Option[Nothing]
Option.Some(1).map(x => x * x)  // Some(1): Option[Int]
```

더 나아가 GADT도 열거형을 통해서 표현할 수 있다. ADT로 정의한 타입의 경우, 타입이 타입 매개변수를 가지고 있다면, 그 타입에 속하는 모든 *형태*(variant)는 동일하게 매개변수화 된다. (OCaml에서 옵션 타입을 정의하려면 `type 'a option = None | Some of 'a`와 같이 할 수 있다. 이 때, `None`이 가지는 타입은 `'a option`으로 `None`이 사용되는 곳에 따라 알맞게 타입 인자가 유추된다. `Some`은 그 자체로 값은 아니지만, `'a -> 'a option` 타입으로 생각할 수 있다. 이처럼 `None`과 `Some` 모두 `'a option` 타입의 값을 만들어내는 것이 ADT이다. 그러나, Scala에서는 옵션 타입을 정의할 때도 이미 GADT의 개념을 사용했다고 볼 수 있는데, Scala에서는 값이 다형일 수 없기 때문에, `None`을 \(\forall\alpha.\textsf{Option}[\alpha]\) 같은 타입을 가지게 할 수 없다. 따라서, 그 대신 옵션 타입을 타입 매개변수에 대해 *공변*(covariant)하도록 설정하고 `None`의 타입을 `Option[Nohting]`으로 정의함으로써 마치 다형 타입을 가지고 있는 것처럼 동작하게 한다.) 반면, GADT에서는 각 형태가 정의한 타입의 특정 *타입* *인스턴스*(type instance)에 속하는 값만을 만들어내는 것을 허용한다. 아래는 열거형을 사용하여 GADT `Tree`를 정의한 것이다. `Tree`는 타입이 붙은 *요약* *문법* *나무*(abstract syntax tree; AST)를 표현하며 타입 인자가 해당 나무의 타입을 나타낸다.

```scala
enum Tree[T] {
  case True extends Tree[Boolean]
  case Zero extends Tree[Int]
  case Succ(pred: Tree[Int]) extends Tree[Int]
  case If(cond: Tree[Boolean], thenT: Tree[T], elseT: Tree[T])
}
```

Scala 3의 열거형은 단순히 전통적인 열거형만 정의할 수 있도록 하는 것이 아니라, ADT나 GADT 같은 복잡한 구조도 간단하게 표현할 수 있게 해준다는 큰 장점을 가진다. Scala 2에서도 동일한 동작을 하는 코드를 얼마든지 작성할 수 있었지만, 열거형의 추가로 *상용구*(boilerplate)를 줄여 코드를 짧게 만들고 프로그래머의 의도를 코드에 더 직접적으로 드러낼 수 있게 되었다.

이 절은 Dotty 웹 사이트의 ['Enumerations' 글](https://dotty.epfl.ch/docs/reference/enums/enums.html)과 ['Algebraic Data Types' 글](https://dotty.epfl.ch/docs/reference/enums/adts.html)을 참고하여 작성하였다.
