이 글에서는 Odersky가 이야기한 일반 개발자에게 가장 도움이 되는 Scala 3의 새로운 기능 3가지에 대하여 설명한다.

## 3위: 합집합 타입

*합집합* *타입*(union type)은 수학에서의 합집합 개념을 그대로 타입 체계에 반영한 것이다. 타입 `A`와 `B`에 대해서 `A`의 모든 원소와 `B`의 모든 원소는 `A`와 `B`의 합집합 타입의 원소이며, 그 이외의 어떤 값도 해당 합집합 타입의 원소가 아니다. 합집합 타입은 Scala 2에 없던, Scala 3에 *교집합* *타입*(intersection type)과 함께 추가된 기능으로, `A`와 `B`의 합집합은 `A | B`라고 표현한다. 직관적으로, `A`와 `B` 모두 `A | B`의 서브타입이며, `A | B`와 `B | A`는 같은 타입이다.

합집합 타입은 두 개 이상의 타입을 *포장*(boxing)하는 비용 없이 조합하여 사용하는 것을 허용한다. 합집합 타입이 없다면, 두 개의 다른 타입의 원소를 함수의 인자로 받고 싶을 때, 다음과 같이 경우 클래스를 사용해서(Scala 3에서는 열거형을 사용할 수도 있다) 값을 포장하고 패턴 대조를 통하여 경우를 나누어 처리해야 한다.

```scala
sealed trait StringOrInt
case class StringBox(s: String) extends StringOrInt
case class IntBox(i: Int) extends StringOrInt

def f(x: StringOrInt) = x match {
  case StringBox(s) => s"String $s"
  case IntBox(i) => s"Int $i"
}
```

실제로 생성자를 호출하여 객체를 생성하고, 패턴 대조를 위해서 *추출자*(extractor)를 호출하는 작업이 실행 중에 이루어진다.

합집합 타입을 이용하면, 실행 시간에 객체를 생성하거나 추출자를 호출하는 작업도 필요 없으며, 코드도 더 간결하고 읽기 쉬워진다.

```scala
def f(x: String | Int) = x match {
  case s: String => s"String $s"
  case i: Int => s"Int $i"
}
```

경우 클래스를 사용할 때와 마찬가지로, 합집합 타입도 패턴 대조 시에 *완전성*(exhaustivity) 검사가 이루어진다.

```scala
def f(x: String | Int) = x match {
  case s: String => s"String $s"
}

// Pattern Match Exhaustivity Warning
// match may not be exhaustive.
// It would fail on pattern case: _: Int
```

합집합 타입은 역시 Scala 3에 추가된 (Scala 2.13에도 추가되었다) 리터럴 타입과 함께 사용할 수 있기 때문에 더욱 유용하다. 리터럴 타입은 `Int`, `Boolean`, `Double` 등의 *기본*(primitive) 타입과 `String` 등에 대해 정의되며, 원소를 한 개만 가지는 단독 타입이다. 예를 들면 `1`은 `1`을 유일한 원소로 가지는 타입이다.

```scala
def f(x: 1 | 2 | 3) = x match {
  case _: 1 => "one"
  case _: 2 => "two"
  case _: 3 => "three"
}
```

타입 유추 시에는 합집합 타입이 나타날 수 있는 경우 자동으로 합집합 타입이 아닌 *상위* *타입*(supertype)을 사용하도록 유추된다. 따라서, 합집합 타입을 메서드의 결과 타입이나 리스트의 타입 인자 등으로 사용하고 싶은 경우에는 반드시 명시해 주어야 한다.

```scala
def f(b: Boolean) = if (b) 1 else "1"  // def f(b: Boolean): Any
def f(b: Boolean): String | Int = if (b) 1 else "1"  // def f(b: Boolean): String | Int

List(1, "1")  // List[Any]
List[String | Int](1, "1")  // List[String | Int]
```

이 절은 Dotty 웹 사이트의 ['Union Types' 글](https://dotty.epfl.ch/docs/reference/new-types/union-types.html)을 참고하여 작성하였다.

## 2위: 확장 메서드

프로그래머는 새로운 클래스를 정의할 때 해당 클래스의 객체가 가지고 있는 메서드를 정의할 수 있지만, 이미 존재하는 타입에 메서드를 추가하는 것은 불가능하다. 유리수를 표현하는 다음의 `Rational` 클래스를 생각해보자.

```scala
class Rational(val numer: Int, val denom: Int) {
  def +(n: Int): Rational = new Rational(numer + denom * n, denom)
}
```

Rational 클래스는 한 개의 메서드 `+`를 정의하여, 정수와의 덧셈을 가능하게 한다. Scala에서는 메서드를 *중위* *연산자*(infix operator)처럼 사용하는 것도 허용하므로 다음의 두 코드는 동일하다.

```scala
new Rational(1, 2).+(1)
new Rational(1, 2) + 1
```

두 번째 방식이 보기 더 좋다는 데에 대부분 사람이 동의할 것이다. 이렇게 유리수에 정수를 더하는 것까지는 잘 작동하지만, 순서를 바꾸어 정수에 유리수를 더하는 것은 조금 어렵다. 앞에서도 말했듯, `Int`라는 타입 자체에 새로운 메서드를 추가할 수는 없다.

```scala
def add(n: Int, r: Rational): Rational = r + n
add(1, new Rational(1, 2))
```

이처럼 지역 메서드를 정의하여 사용하는 방법을 고려할 수는 있으나, 코드의 일관성이나 가독성 측면에서 바람직하지 않다. 이 문제를 해결하기 위해서 Scala 2에서는 암묵적 클래스를 정의하여, 암묵적 변환이 일어나도록 했다.

```scala
implicit class IntExtension(n: Int) {
  def +(r: Rational): Rational = r + n
}
1 + new Rational(1, 2)
```

`1`이 가지고 있는 메서드 `+` 중 `Rational` 타입을 인자로 받을 수 있는 메서드가 없으므로, 암묵적 변환을 시도하게 되고, 범위에 있는 `IntExtension` 암묵적 클래스가 `Rational` 타입을 인자로 받는 `+` 메서드를 가지고 있기 때문에 암묵적 변환이 이루어져, 컴파일 시 `1 + new Rational(1, 2)`는 `new IntExtension(1) + new Rational(1, 2)`와 같은 형태로 바뀐다.

암묵은 Scala 2가 제공하는 매우 강력한 기능인 동시에 많은 논란을 일으키는 기능이었고, Scala 3에서 암묵에 대한 변경이 이루어지면서, 암묵 없이 직접 *확장* *메서드*(extension method)를 정의할 방법을 제공하게 되었다. Scala 3은 아래 코드를 허용한다. 암묵을 사용하는 것에 비해 더 간결하고 읽기 쉬운 것을 볼 수 있다.

```scala
def (n: Int) + (r: Rational): Rational = r + n
1 + new Rational(1, 2)
```

매개변수가 없는 확장 메서드도 유사하게 정의할 수 있다.

```scala
def (r: Rational) toInt: Int = r.numer / r.denom
new Rational(4, 2).toInt
```

확장 메서드를 단독 객체 안에 정의하고 명시적으로 *들여오기*(import)를 해서 사용하면 예상하지 못한 확장 메서드의 사용을 막을 수 있다.

```scala
object IntExtension {
  def (n: Int) + (r: Rational): Rational = r + n
}

1 + new Rational(1, 2)  // Error

import IntExtension._
1 + new Rational(1, 2)
```

일반적인 메서드와 마찬가지로 확장 메서드 역시 타입 매개변수를 가질 수 있다.

```scala
def (l: List[T]) push[T] (t: T) = t :: l
def (l: List[T]) pop[T] = l.tail

Nil.push(1).pop
```

이 절은 Dotty 웹 사이트의 ['Extension Methods' 글](https://dotty.epfl.ch/docs/reference/contextual/extension-methods.html)을 참고하여 작성하였다.

## 1위: 문맥 요약 기능

Scala 2는 암묵을 통한 *문맥* *요약*(contextual abstraction) 기능을 제공하였다. Scala 3의 *알려진* *객체*(given instance)와 *알려진* *매개변수*(given parameter)는 Scala 2의 암묵을 대신하는 개념이다. `implicit`이라는 키워드 대신 `given`이라는 키워드를 사용하며, 의미는 Scala 2의 암묵과 거의 동일하지만 문법적인 차이가 조금 있다. Odersky의 표현을 빌리자면, "express intent instead of mechanism", 즉 기존의 암묵이 암묵적 객체가 암묵적 매개변수의 자리에 ‘암묵’적으로 전달되는 그 과정 자체를 드러내는 이름이었다면, 알려진 객체와 알려진 매개변수라는 이름은 문맥을 요약한다는 의도를 더 드러내는 이름이라고 생각할 수 있다. 사실`given`이라는 키워드가 선택되기까지 매우 많은 논의가 있었으며, `assume`, `representative`, `repr`, `evidence`, `witness` `implied`, `impl` 등 많은 단어가 제시되었고, `implicit`을 그대로 사용하는 것도 한 가지 안이었다, Scala Days 발표에서만 해도 `delegate`이 선택된 상태였지만, Scala Days 이후 `given`을 사용하는 것으로 바뀌었고, 이 글도 바뀐 키워드를 따라 작성 시점 기준으로 `given`을 사용하여 작성한다. Scala 3의 기능 고정이 아직 이루어지지 않았기 때문에, 다른 키워드를 사용하지 않을 것이라는 보장은 없겠지만, `given`이 계속 사용되기를 바라면서 글을 쓰려고 한다.

`given`이라는 키워드를 어떻게 한국어로 번역할 것인지에 대해서도 고민을 해보았는데, 키워드 자체를 부를 때는 `given`이라고 작성하더라도 큰 문제가 없겠지만, ‘given instance’를 기븐 객체라고 번역하는 것은 원하지 않았기 때문에 음차 대신 적절한 번역을 찾으려고 노력하였다. "At the same time there were suggestions to use given as a noun (from Miles, Adriaan, maybe others) that felt to me like it could work."라고 Odersky가 [GitHub](https://github.com/lampepfl/dotty/pull/6773)에 언급한 바에 따라 [Oxford 사전](https://www.lexico.com/en/definition/given)에서 ‘given’이 명사로 쓰일 때 뜻을 찾아보면 ‘a known or established fact or situation’이라 나와 있고, [Naver 사전](https://endic.naver.com/enkrEntry.nhn?sLn=kr&entryId=8a757fdbcb004a4baeabbf0b9a3632fc&query=given)에서는 ‘기정사실’이라고 하고 있다. 이를 바탕으로 내 나름대로 ‘given instance’와 ‘given parameter’를 ‘알려진 객체’와 ‘알려진 매개변수’라고 번역하기로 결정하였다. 이는 형용사로 ‘given’이 사용될 때의 뜻인 ‘specified or stated’에도 어느 정도 부합하고 알려진 객체와 알려진 매개변수를 사용하는 의도에도 합치한다고 생각한다. 물론, 만족스럽지 않게 생각하는 사람들도 있을 것이며, ‘implicit’ 자체를 ‘암묵’이라 번역한 것처럼 ‘given’ 자체를 번역할 마땅한 단어는 찾지 못했다는 문제가 남아 있다. (‘알려진 사실’, ‘알려진 상황’, ‘기정사실’ 등 어느 것도 알려진 객체와 알려진 매개변수를 아우르는 종합적인 Scala의 기능을 표현하는 적절한 단어는 아닌 것 같다.) 다만, Dotty 웹 사이트에서는 알려진 객체와 매개변수 내용을 다루는 부분을 ‘contextual abstraction’이라는 단어로 묶고 있으니, 알려진 객체와 매개변수를 Scala의 문맥 요약 기능이라고 통칭하려 한다. 만약 누군가 더 나은 단어를 생각해낸다면 알려주기를 바란다.

### 타입 클래스

Scala 2에서 암묵이 사용되는 전형적인 예시인 *타입* *클래스*(type class)에 대해 간단히 설명한 다음, 타입 클래스를 구현하는 방법이 Scala 2와 Scala 3에서 어떤 차이를 가지는지 확인함으로써, 문맥 요약 방법이 어떻게 다른지 보려고 한다.

타입 클래스는 *즉석* *다형성*(ad hoc polymorphism)을 위한 기능으로 Haskell에서 처음 제공하였고, 지금도 Haskell이 타입 클래스를 직접 제공하는 가장 잘 알려진 언어이기 때문에, 타입 클래스라고 하면 보통 Haskell의 타입 클래스를 이야기한다. 따라서, 이 글에서도 Haskell의 타입 클래스를 설명한다.

타입 클래스를 통해 표현할 수 있는 대표적인 예시는 값의 동일성을 판단하는 것이다. Haskell과 달리 객체지향언어에서는 서브타입 다형성을 사용하므로, 값의 동일성 판단 역시 서브타입 다형성을 통하여 이루어진다. 예를 들면, Java에서는 최상위 타입인 `Object` 클래스가 `equals` 메서드를 정의하고 있고, 모든 객체는 `Object` 클래스 또는 `Object` 클래스의 서브클래스의 객체이므로 `equals` 메서드를 자동으로 가지고 있기에, 임의의 두 객체의 동일성은 언제나 판단할 수 있다. (물론, 판단할 수 있다는 것은 어디까지나 메서드 호출이 가능하다는 이야기일 뿐, 프로그래머가 올바르게 `equals` 메서드를 덮어써야만 제대로 된 동일성 판단이 가능하다. 서브타입 다형성을 사용한 `equals` 메서드 구현은 `equals` 메서드가 `Object` 타입의 매개변수를 가진다는 점 때문에, *단일* *선택*(single dispatch) 사용 시에서는 `equals` 메서드를 올바르게 구현하는 것이 어렵다. 그러나, 이 글에서 다루고자 하는 주제는 아니므로 더 자세히 이야기하지는 않겠다.) 반면 Haskell은 객체지향언어가 아니며, 상속의 개념도 없기 때문에, 동일성 판단을 서브타입 다형성을 통해서 하는 것은 불가능하다. 대신 타입 클래스는 직관적으로 이해했을 때, 이미 존재하는 타입에 새로운 기능을 추가하는 것을 가능하게 하며, 동일성 판단도 이미 존재하는 타입에 동일성 판단이라는 기능을 추가함으로써 가능하게 할 수 있다.

Haskell에서 동일성 판단을 위한 `Comparable` 타입 클래스는 아래와 같이 구현할 수 있다. 나는 Haskell을 지금까지 한 번도 사용해보지 않은 사람이기 때문에, Haskell 코드는 전적으로 영어 Wikipedia의 ['Type class' 페이지](https://en.wikipedia.org/wiki/Type_class)를 참고하여 작성하였으며, 잘못된 점이 있다면 알려주길 바란다.

```haskell
class Comparable a where
  eq :: a -> a -> Bool
```

위 코드는 `Comparable` 타입 클래스를 정의한다. 코드는 어떤 타입 `a`에 대해 `Comparable a`이기 위해서는 (타입 `a`가 타입 클래스 `Comparable`에 속하기 위해서는) `a -> a -> Bool` 타입의 함수 `eq`, 즉 `a` 타입의 값 두 개를 비교해서 `True` 또는 `False`를 결과로 내는 함수 `eq` 가 존재해야 한다는 것을 의미한다.

```haskell
instance Comparable Integer where 
  eq x y = x == y
```

특정 타입 `a`에 대한 `eq` 함수를 가진 인스턴스는 위와 같이 정의할 수 있다. 여기서는 `Integer`가 `Comparable` 타입 클래스에 속하도록 `Integer -> Integer -> Bool` 타입의 `eq` 함수를 가진 `Comparable Integer`의 인스턴스를 정의하였다.

```haskell
contains :: Comparable a => [a] -> a -> Bool
contains [] a = False
contains (h:t) a = (eq h a) || contains t a
```

`eq` 함수를 사용할 수 있는 대상은 `Comparable` 타입 클래스에 속하는 타입의 값으로 한정되므로, `eq` 함수를 사용하기 위해서는 먼저 `Comparable a`라는 제약조건을 걸어야 한다. `Comparable a =>`가 그 역할을 한다. `contains` 함수는 `Comparable a`라는 문맥 아래에서 `[a]`(`a`의 리스트) 타입의 값과 `a` 타입의 값을 하나 받아서 받은 리스트가 받은 값을 원소로 가지고 있는지 확인한다. 세 번째 줄에서 `a` 타입의 값 두 개에 대해서 `eq` 함수를 사용한 것을 볼 수 있다.

```haskell
containsInteger :: [Integer] -> Integer -> Bool
containsInteger l i = contains l i

containsBool :: [Bool] -> Bool -> Bool
containsBool l b = contains l b  -- No instance for (Comparable Bool)
```

`Integer`는 `Comparable`에 속하므로 `Integer` 리스트와 `Integer`에 대해서 `contains`를 사용하는 `containsInteger` 함수는 문제없이 정의되지만, `Bool`은 `Comparable`에 속하지 않으므로 `containsBool` 함수는 `No instance for (Comparable Bool)`이라는 오류 문구와 함께 컴파일이 되지 않는다.

타입 클래스 개념은 서브타입 다형성이 존재하는 객체지향언어에서도 유용하게 사용될 수 있다. 일반적으로 사용되는 객체지향언어에서는 이미 존재하는 타입의 값을 새롭게 정의한 타입의 값으로 사용하는 것이 불가능하다. 이 한계를 해결하기 위해서 Scala에서는 암묵적 변환이나 확장 메서드 같은 기능을 사용할 수 있으며, 타입 클래스는 더 강력하고 일반화된 해결책을 제시해 준다. 물론 Scala에서 직접적으로 타입 클래스를 제공하는 것은 아니지만, Scala 2에서는 암묵을 사용하여 Haskell의 타입 클래스를 거의 그대로 흉내 낼 수 있었고, Scala 3에서는 알려진 객체와 알려진 매개변수가 이전보다 더 '대놓고' 타입 클래스를 사용할 수 있도록 도와준다.

지금부터는 Scala에서 모노이드 타입 클래스를 정의하고 사용하는 방법을 살펴볼 것이다. 모노이드는 결합 법칙을 만족하는 한 개의 이항 연산과 그 연산에 대한 항등원이 존재하는 대수 구조이다. 예를 들면, 정수는 덧셈과 0에 대해서 모노이드가 되고, 문자열은 문자열 *접합*(concatenation)과 빈 문자열에 대해서 모노이드가 된다. 모노이드를 타입 클래스로 정의함으로써, 기존에 Scala에 존재하는 `Int`, `String` 같은 타입을 모노이드로 취급하여 다룰 수 있다.

### Scala 2에서의 타입 클래스 구현

```scala
trait Monoid[T] {
  val id: T
  def op(t0: T, t1: T): T
}
```

`Monoid` 트레잇은 모노이드 타입 클래스를 정의한다. 임의의 타입 `T`가 모노이드이기 위해서는 항등원을 나타내는 `T` 타입의 값 `id`와 결합 법칙을 만족하는 `T`에 대한 이항 연산을 나타내는 메서드 `op`가 정의되어야 한다.

```scala
def sum[T](list: List[T])(implicit ev: Monoid[T]) =
  list.foldLeft(ev.id)(ev.op)
```

어떤 모노이드의 원소들로 이루어진 리스트의 합을 구하는 메서드 `sum`이다. 첫 매개변수의 타입이 `List[T]`인 것에 더해, 두 번째 매개변수 `ev`의 타입을 `Monoid[T]`로 지정함으로써 `T`가 모노이드라는 증거도 함께 받도록 하고 있다. 이때, `ev`는 암묵적 매개변수로, 메서드 호출 시 범위 안에서 `Monoid[T]` 타입의 암묵적 객체를 만들 수 있다면, 프로그래머가 두 번째 인자를 명시하지 않아도 해당 객체를 만들어내는 *항*(term)이 자동으로 두 번째 인자 자리를 채운다. 이러한 특징으로 인해서 Dotty 웹 사이트에서는 문맥 요약 기능을 설명할 때, 기존의 타입 유추가 생략된 타입을 컴파일러가 유추하는 것에 비해, 이 기능은 생략된 항을 컴파일러가 유추하는 항 유추라고 표현하고 있다.

```scala
def sum[T: Monoid](list: List[T]) =
  list.foldLeft(implicitly[Monoid[T]].id)(implicitly[Monoid[T]].op)
```

`sum` 메서드는 위 코드로 다시 쓸 수 있다. `def sum[T: Monoid](list: List[T])` 부분이 컴파일 시 자동으로 `def sum[T](list: List[T])(implicit evidence$1: Monoid[T])`로 바뀐다. 즉, `: Monoid` 부분이 `T`가 모노이드라는 문맥을 강제하는 것이다. 이렇게 타입 매개변수에 대한 문맥을 강제하는 것을 *문맥* *묶기*(context bound)라 부른다. 단, 이렇게 코드를 작성하면 직접 암묵적 인자의 이름을 지정할 수 없기 때문에, 암묵적 인자를 사용할 때는 `implicitly`를 사용해야 한다. `implicitly`는 컴파일러가 별도로 처리하는 키워드가 아닌 Scala 표준 라이브러리에 정의된 암묵적 매개변수를 사용하는 메서드로, 지정한 타입의 암묵적 객체를 찾아준다.

```scala
object Monoids {
  implicit val intMonoid = new Monoid[Int] {
    val id = 0
    def op(t0: Int, t1: Int) = t0 + t1
  }
  ...
```

`intMonoid`는 `Int`가 모노이드라는 증거로서, `implicit` 키워드를 사용하여 암묵적 객체로 정의되었기 때문에, 암묵적 인자로 사용될 수 있다.

```scala
object Monoids {
  ...
  implicit val stringMonoid = new Monoid[String] {
    val id = ""
    def op(t0: String, t1: String) = t0 + t1
  }
  ...
```

`String`이 모노이드라는 증거도 비슷하게 만들 수 있다.

```scala
object Monoids {
  ...
  implicit def listMonoid[T] = new Monoid[List[T]] {
    val id = Nil
    def op(t0: List[T], t1: List[T]) = t0 ++ t1
  }
  ...
}
```

`List[T]` 타입 역시 모노이드이다. 다만, 앞의 `Int`와 `String`과는 달리, `List[T]`는 다형 타입이기 때문에 `val` 키워드를 사용하여 값으로 정의될 수는 없고 `def`를 통해 암묵적 객체를 제공하는 메서드를 만들 수 있다.

```scala
object Monoids {
  ...
  implicit def pairMonoid[T: Monoid, S: Monoid] =
    new Monoid[(T, S)] {
      val id = (implicitly[Monoid[T]].id, implicitly[Monoid[S]].id)
      def op(t0: (T, S), t1: (T, S)) =
      (implicitly[Monoid[T]].op(t0._1, t1._1),
       implicitly[Monoid[S]].op(t0._2, t1._2))
    }
}
```

임의의 순서쌍 타입 `(T, S)`는 `T`와 `S`가 모노이드이면 모노이드이다. 따라서, `Monoid[(T, S)]` 타입의 값을 만들기 위해서는 `Monoid[T]`와 `Monoid[S]` 타입의 값이 필요하며, 이를 메서드의 인자로 받고 있다. (코드에는 드러나지 않지만, 위에서 본 대로 컴파일러가 암묵적 매개변수를 만들어낸다.)

```scala
import Monoids._
sum(List(0, 1, 2))  // 3
sum(List("A", "B", "C"))  // ABC
sum(List(List('a', 'b'), List('c', 'd'), List('e')))  // List(a, b, c, d, e)
sum(List((0, "0"), (1, "1"), (2, "2")))  // (3, 012)
```

`Monoids`에 정의된 필드와 메서드를 이용하여 암묵적 인자를 만들어내기 위해서는 들여오기를 해야 한다.

실행 가능한 전체 코드를 보고 싶다면 아래 접어둔 부분을 펼쳐보면 된다.

<details><summary>Scala 2 모노이드 타입 클래스 구현</summary>
```scala
trait Monoid[T] {
  val id: T
  def op(t0: T, t1: T): T
}

object Monoids {
  implicit val intMonoid = new Monoid[Int] {
    val id = 0
    def op(t0: Int, t1: Int) = t0 + t1
  }

  implicit val stringMonoid = new Monoid[String] {
    val id = ""
    def op(t0: String, t1: String) = t0 + t1
  }
  
  implicit def listMonoid[T] = new Monoid[List[T]] {
    val id = Nil
    def op(t0: List[T], t1: List[T]) = t0 ++ t1
  }
  
  implicit def pairMonoid[T: Monoid, S: Monoid] =
    new Monoid[(T, S)] {
      val id = (implicitly[Monoid[T]].id, implicitly[Monoid[S]].id)
      def op(t0: (T, S), t1: (T, S)) =
      (implicitly[Monoid[T]].op(t0._1, t1._1),
       implicitly[Monoid[S]].op(t0._2, t1._2))
    }
}

object Main {
  def sum[T: Monoid](list: List[T]) =
    list.foldLeft(implicitly[Monoid[T]].id)(implicitly[Monoid[T]].op)

  def main(args: Array[String]): Unit = {
    import Monoids._
    println(sum(List(0, 1, 2)))
    println(sum(List("A", "B", "C")))
    println(sum(List(List('a', 'b'), List('c', 'd'), List('e'))))
    println(sum(List((0, "0"), (1, "1"), (2, "2"))))
  }
}

```
</details>

### Scala 3에서의 타입 클래스 구현

```scala
trait Monoid[T] {
  val id: T
  def op(t0: T, t1: T): T
}
```

`Monoid` 트레잇의 구현은 같다.

```scala
def sum[T](list: List[T]) given (ev: Monoid[T]) =
  list.foldLeft(ev.id)(ev.op)
```

`sum` 메서드의 구현은 유사하지만 `implicit` 키워드 대신 `given` 키워드를 사용하여 알려진 매개변수를 정의한다.

```scala
def sum[T](list: List[T]) given Monoid[T] =
  list.foldLeft(the[Monoid[T]].id)(the[Monoid[T]].op)
```

알려진 매개변수의 이름은 생략할 수 있다. Scala 3에서는 `implicitly` 대신 `the`를 사용할 수 있다. `implicitly`가 다소 길어서 불편했는데, 더 짧고 의미도 직관적인 `the`를 사용할 수 있게 된 것은 좋은 일이라고 생각한다.

```scala
def sum[T: Monoid](list: List[T]) =
  list.foldLeft(the[Monoid[T]].id)(the[Monoid[T]].op)
```

Scala 2에서처럼 문맥 묶기를 사용해도 된다.

```scala
object Monoids {
  given as Monoid[Int] {
    val id = 0
    def op(t0: Int, t1: Int) = t0 + t1
  }
  ...
}
```

Scala 2에서 `implicit` 키워드를 통해서 암묵적 객체를 만든 것과 달리 Scala 3에서는 `given as`를 사용하여 직접적으로 타입 클래스의 인스턴스로 사용할 알려진 객체를 만들 수 있다. 기본적으로 이름이 생략 가능한 것을 볼 수 있다.

```scala
object Monoids {
  ...
  given StringMonoid as Monoid[String] {
    val id = ""
    def op(t0: String, t1: String) = t0 + t1
  }
  ...
}
```

원한다면 이름을 명시할 수도 있다.

```scala
object Monoids {
  ...
  given [T] as Monoid[List[T]] {
    val id = Nil
    def op(t0: List[T], t1: List[T]) = t0 ++ t1
  }
  ...
```

다형 타입에 대해서도 알려진 객체를 만들 수 있다. Scala 2에서처럼 `val`과 `def`를 구분하지 않아도 되는 것을 볼 수 있다. 한 가지 아쉬운 점은 `given [T] as`로 쓰다 보니 마치 `T`가 주어진 무언가인 것처럼 읽힌다는 것인데, 파서를 힘들게 하지 않으면서 배치할 적절한 위치를 찾다 보니 저기에 들어간 것 같다. 마땅히 다른 방법을 찾기 쉽지 않고, 이 문법도 수많은 논의 끝에 나온 것이긴 하지만, 여전히 다소 비직관적이라고 생각한다.

```scala
object Monoids {
  ...
  given [T: Monoid, S: Monoid] as Monoid[(T, S)] {
    val id = (the[Monoid[T]].id, the[Monoid[S]].id)
    def op(t0: (T, S), t1: (T, S)) =
      (the[Monoid[T]].op(t0(0), t1(0)),
       the[Monoid[S]].op(t0(1), t1(1)))
  }
}
```

순서쌍도 마찬가지로 `given as`를 사용하여 알려진 객체를 만들 수 있다.

```scala
import given Monoids._
sum(List(0, 1, 2))  // 3
sum(List("A", "B", "C"))  // ABC
sum(List(List('a', 'b'), List('c', 'd'), List('e')))  // List(a, b, c, d, e)
sum(List((0, "0"), (1, "1"), (2, "2")))  // List(3, 012)
```

Scala 3에서는 알려진 객체 들여오기가 일반적인 들여오기와 구분되어 존재하기 때문에 알려진 객체들을 사용하기 위해서는 `import given`을 사용해야 한다.

실행 가능한 전체 코드를 보고 싶다면 아래 접어둔 부분을 펼쳐보면 된다.

<details><summary>Scala 3 모노이드 타입 클래스 구현</summary>
```scala
trait Monoid[T] {
  val id: T
  def op(t0: T, t1: T): T
}

object Monoids {
  given as Monoid[Int] {
    val id = 0
    def op(t0: Int, t1: Int) = t0 + t1
  }

  given StringMonoid as Monoid[String] {
    val id = ""
    def op(t0: String, t1: String) = t0 + t1
  }
  
  given [T] as Monoid[List[T]] {
    val id = Nil
    def op(t0: List[T], t1: List[T]) = t0 ++ t1
  }
  
  given [T: Monoid, S: Monoid] as Monoid[(T, S)] {
    val id = (the[Monoid[T]].id, the[Monoid[S]].id)
    def op(t0: (T, S), t1: (T, S)) =
      (the[Monoid[T]].op(t0(0), t1(0)),
       the[Monoid[S]].op(t0(1), t1(1)))
  }
}

object Main {
  def sum[T: Monoid](list: List[T]) =
    list.foldLeft(the[Monoid[T]].id)(the[Monoid[T]].op)

  def main(args: Array[String]): Unit = {
    import given Monoids._
    println(sum(List(0, 1, 2)))
    println(sum(List("A", "B", "C")))
    println(sum(List(List('a', 'b'), List('c', 'd'), List('e'))))
    println(sum(List((0, "0"), (1, "1"), (2, "2"))))
  }
}
```
</details>

### 문맥 요약 기능의 다른 활용 예시

인터프리터를 구현할 때도 문맥 요약 기능을 사용하면 반복적으로 당연한 인자를 적는 것을 생략하여 코드를 간략하게 만들 수 있다. 궁금한 사람은 아래 코드를 펼쳐서 보기를 바란다. 지금까지 나온 내용을 이해했다면 아주 어려운 부분은 없기 때문에 별도의 설명을 달지는 않겠다.

<details><summary>Scala 3 인터프리터 구현</summary>
```scala
type Env = Map[String, Val]

enum Expr {
  case Var(x: String)
  case Num(n: Int)
  case Add(e0: Expr, e1: Expr)
  case Fun(param: String, body: Expr)
  case App(e0: Expr, e1: Expr)
}

enum Val {
  case NumV(n: Int)
  case ClosureV(param: String, body: Expr, env: Env)
}

object Main {
  import Expr._
  import Val._

  def interp(e: Expr) given Env: Val = e match {
    case Var(x) => the[Env](x)
    case Num(n) => NumV(n)
    case Add(e0, e1) => 
      val NumV(n0) = interp(e0)
      val NumV(n1) = interp(e1)
      NumV(n0 + n1)
    case Fun(param, body) => ClosureV(param, body, the[Env])
    case App(e0, e1) =>
      val ClosureV(param, body, cenv) = interp(e0)
      val arg = interp(e1)
      interp(body) given (cenv + (param -> arg))
  }

  def main(args: Array[String]): Unit = {
    given as Env = Map[String, Val]()

    println(
      interp(
        App(
          Fun("x", Add(Var("x"), Num(1))),
          Num(2)
        )
      )
    )
  }
}
```
</details>

위 코드에서는 *식*(expression)의 종류가 다양하지 않아 *환경*(environment) 인자를 생략하는 경우가 많지는 않으나, 식의 종류가 다양한 경우에는 대부분의 경우에 인자를 생략할 수 있기 때문에, 알려진 객체와 알려진 매개변수가 코드를 간결하게 만드는 데 도움을 준다.

이와 유사하게 컴파일러를 개발할 때는, 나무를 수정하고 새로 만드는 과정에서 어떤 나무가 처음 코드의 몇째 줄 몇째 글자를 가리키는지 계속 알고 있어야 하므로, 재귀 함수의 인자로 계속해서 위치값을 넘겨야 한다. 이때, 대부분의 재귀 호출에서 위치는 변하지 않기 때문에, 위치 인자를 알려진 인자로 선언함으로써 코드를 간결하게 만들 수 있다.

이 절은 Dotty 웹 사이트의 글들을 참고하여 작성하였다.

* [Contextual Abstractions Overview](https://dotty.epfl.ch/docs/reference/contextual/motivation.html)
* [Given Instances](https://dotty.epfl.ch/docs/reference/contextual/delegates.html)
* [Given Clauses](https://dotty.epfl.ch/docs/reference/contextual/given-clauses.html)
* [Context Bounds](https://dotty.epfl.ch/docs/reference/contextual/context-bounds.html)
* [Given Imports](https://dotty.epfl.ch/docs/reference/contextual/import-delegate.html)
* [Implementing Typeclasses](https://dotty.epfl.ch/docs/reference/contextual/typeclasses.html)
* [Relationship with Scala 2 Implicits](https://dotty.epfl.ch/docs/reference/contextual/relationship-implicits.html)

이 밖에도 웹 사이트의 해당 부분에 더 많은 글이 있으니 관심 있는 사람은 읽어보길 바란다.
