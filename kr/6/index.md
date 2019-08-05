이번 글은 함수형 프로그래밍에 대한 네 번째 글이다. 지난 글에서는 Scala 표준 라이브러리의 `List`와 `Option`을 사용하는 방법과 Scala의 `for` 식을 알아보았다. 이번 글에서는 패턴 대조를 다룬다. 이미 리스트와 옵션을 처리하기 위해서 패턴 대조를 사용하였지만, 단순한 한 가지 형태로만 사용하였다. 이 글에서는 패턴 대조의 장점과 Scala에서 사용할 수 있는 다양한 패턴에 대해서 알아본다. 프로그래밍 언어 수업에서 과제를 할 때 패턴 대조를 매우 많이 사용하게 되므로 이번 글을 잘 이해하면 과제에 도움이 될 것이다. 이번 글은 2018년 가을 학기에 진행한 Scala 세미나 중 다섯 번째 세미나인 "Pattern Matching" 내용을 바탕으로 작성하였다. 세미나 [자료](/files/scala/18f/5_pattern.pdf)와 [코드](/files/scala/18f/5_pattern.zip)를 온라인으로 이용 가능하니 필요한 사람은 참고하길 바란다.

## 대수적 데이터 타입

많은 경우에 한 타입에 속하는 값이 두 개 이상의 다양한 형태를 가질 수 있다.

자연수는

* 0이거나,
* 어떤 자연수의 다음 수이다.

리스트는

* 빈 리스트이거나,
* 원소와 리스트의 순서쌍이다.

*이진* *나무*(binary tree)는

* 빈 나무이거나,
* *뿌리*(root) 원소와 두 개의 자식 나무로 이루어진 나무이다.

*산술식*(arithmetic expression)은

* 수이거나,
* 두 산술식의 합이거나,
* 두 산술식의 차이거나,
* 두 산술식의 곱이거나,
* 두 산술식의 비이다.

조금 더 일반적으로 표현하면, 산술식은

* 수이거나,
* *단항* *연산자*(unary operator)가 붙은 산술식이거나,
* *이항* *연산자*(binary operator)를 가운데에 둔 두 산술식이다.

프로그래밍 언어 수업에서 다루는 *람다* *대수*(lambda calculus)의 식은

* 변수이거나,
* 함수, 즉 변수와 식의 순서쌍이거나,
* 함수 적용, 즉 두 식의 순서쌍이다.

이처럼, 전산학에서는 여러 가지 형태의 값이 한 타입에 속하는 일이 흔하다. 이런 타입은 일반적으로 대수적 데이터 타입으로 표현한다. 대수적 데이터 타입이라는 이름이 붙은 이유는, 타입을 *곱* *타입*(product type)의 *합* *타입*(sum type)으로 생각할 수 있기 때문이다. 곱 타입은 여러 타입의 원소를 일정한 순서로 나열한 값을 원소로 가지는 타입으로, 튜플 타입과 거의 같다고 볼 수 있다. 합 타입은 *이름* *붙은* *합집합* *타입*(tagged union type)이라고도 부르는데, 여러 타입의 값 중 하나를 원소로 가지며, 단순한 합집합 타입과 다르게 어떤 종류인지를 표시하기 위하여 이름을 붙인다.

예를 들면, 산술식은 다음과 같이 생각할 수 있다.

산술식은

* 정수이거나,
* 두 산술식의 합이다.

이때, `AE` 타입(산술식 타입)은

* `Num`이라고 이름 붙인 `Int` 타입과
* `Sum`이라고 이름 붙인 `AE * AE` 타입(`AE` 타입과 `AE` 타입의 곱 타입),

이 두 타입의 합 타입이다.

## Scala에서 대수적 데이터 타입 정의하기

대수적 데이터 타입은 함수형 언어에서 흔히 볼 수 있는 타입으로, 대부분의 함수형 언어는 언어에서 직접적으로 사용자가 대수적 데이터 타입을 정의할 방법을 제공한다. 아래는 대표적인 함수형 언어인 OCaml에서 산술식을 정의한 것이다.

```ocaml
type ae =
| Num of int
| UnOp of string * ae
| BinOp of string * ae * ae
```

Scala는 객체지향언어와 함수형 언어의 특징을 동시에 가지고 있다. 객체를 만들기 위한 타입을 정의하는 방법과 대수적 데이터 타입을 정의하는 방법이 서로 다른 형태로 한 언어에 존재하는 것은 언어를 일관적이지 않고 복잡하게 만든다. Scala는 많은 객체지향언어가 사용하는 방법을 따라, 사용자가 클래스를 정의하여 새로운 타입을 정의할 수 있도록 한다. 그리고, 클래스를 사용하여 대수적 데이터 타입을 흉내 낼 수 있다.

클래스를 정의하는 코드는 `class [클래스 이름]`이다. `class A`는 `A`라는 이름을 가진 클래스를 정의한다. 클래스를 정의하는 것은 타입을 정의하는 것이므로 `A`는 클래스의 이름인 동시에 타입이며, 클래스 `A`의 모든 객체는 `A` 타입의 값이다. 아래 코드는 산술식 타입인 `AE` 타입을 정의한다.

```scala
class AE
```

이제, 산술식에 속하는 값의 여러 *형태*(variant)를 정의해야 한다. 각 형태 역시 클래스로 정의할 수 있다. `Num`은 수, `UnOp`는 단항 연산자가 붙은 산술식, `BinOp`는 이항 연산자를 사이에 둔 두 산술식을 나타내는 타입이다.

```scala
class AE
class Num
class UnOp
class BinOp
```

그러나, 위 코드는 각 형태가 `AE` 타입에 속한다는 정보를 나타내지 않는다. `Num` 타입의 값이 `AE` 타입의 값이 아니다. `UnOp`와 `BinOp` 타입의 값도 마찬가지이다.

두 타입 사이에 포함 관계를 표현하기 위하여, 정의하는 클래스의 부모 클래스를 정할 수 있다. `class [클래스 이름] extends [부모 클래스 이름]` 형태로 쓴다. 만약, `class A extends B`라고 썼다면, `A` 클래스는 `B` 클래스의 자식 클래스, 하위 클래스, *서브클래스*(subclass) 등으로 부르고, `B` 클래스는 `A` 클래스의 부모 클래스, 상위 클래스, *슈퍼클래스*(superclass) 등으로 부른다. 또한, ‘`A` 클래스가 `B` 클래스를 *상속*(inheritance)한다’고 말한다. `A`는 `B`의 서브타입이며, `B`는 `A`의 슈퍼타입*(supertype)*이다. `A`가 `B`의 서브타입이라는 말은 `A`의 모든 원소가 `B`의 원소라는 뜻이다.

```scala
class AE
class Num extends AE
class UnOp extends AE
class BinOp extends AE
```

이제 `Num`, `UnOp`, `BinOp`는 `AE`의 서브타입이다. `Num`, `UnOp`, `BinOp` 타입의 값은 `AE` 타입의 값이다.

위 코드만으로는 `Num` 타입의 값이 어떤 수를 나타내는지 표현할 수 없다. 마찬가지로, `UnOp`나 `BinOp` 타입의 값도 어떤 연산자와 어떤 식을 나타내는지 표현할 수 없다. 객체가 추가적인 정보를 저장하게 만들기 위하여 클래스 매개변수를 사용한다. `class [클래스 이름](val [매개변수 이름]: [매개변수 타입], ...)` 꼴로 클래스 매개변수를 지정할 수 있다. (`val`을 붙이지 않는 클래스 매개변수도 존재하지만, 이 글에서는 다루지 않는다.)

```scala
class AE
class Num(val n: Int) extends AE
class UnOp(val op: String, val e: AE) extends AE
class BinOp(val op: String, val e0: AE, val e1: AE) extends AE
```

`Num`, `UnOp`, `BinOp` 모두 `AE`의 한 형태이며, 각각의 객체가 한 개의 정수, 한 개의 문자열과 한 개의 산술식, 한 개의 문자열과 두 개의 산술식을 저장한다는 사실을 표현한다.

객체를 만들기 위해서는 *생성자*(constructor)를 호출해야 한다. `new [클래스 이름]([식], ...)`이 생성자 호출을 통하여 객체를 만드는 식이다.

```scala
new Num(1): AE
new UnOp("-", new Num(2)): AE
new BinOp("+", new Num(1), new UnOp("-", new Num(2))): AE
```

`Num`, `UnOp`, `BinOp` 타입의 값 모두 `AE` 타입의 값이다.

클래스 매개변수는 클래스의 *필드*(field)이다. 생성자 호출 시에 사용한 인자의 값이 필드의 값이다. 메서드 호출을 위하여 마침표를 사용한 것처럼 객체의 필드에도 마침표를 사용하여 접근할 수 있다. `[식].[필드 이름]`이라는 식을 계산하면, 앞의 식을 계산해서 얻은 객체의 해당 필드의 값이 결과이다. 

```scala
new Num(1).n  // 1
new UnOp("-", new Num(2)).op  // "-"
new BinOp("+", new Num(1), new UnOp("-", new Num(2))).op  // "+"
```

위에서 클래스 정의 시에 사용한 코드에는 한 가지 문제가 있다.

```scala
new AE: AE
```

`new AE`라는 식을 계산하여 얻은 객체는 무엇을 의미하는 것일까? 산술식은 수이거나, 단항 연산자가 붙은 산술식이거나, 이항 연산자를 사이에 둔 두 산술식일 수는 있지만, 그 이외의 가능성은 없다. 따라서, `AE` 클래스는 직접 객체를 만들 수 없어야 한다.

```scala
trait AE
class Num(val n: Int) extends AE
class UnOp(val op: String, val e: AE) extends AE
class BinOp(val op: String, val e0: AE, val e1: AE) extends AE
```

`AE` 타입의 객체를 자식 클래스를 통하지 않고 직접 만드는 것을 막기 위해서는 `AE`를 클래스가 아닌 *트레잇*(trait)으로 정의해야 한다. 트레잇은 클래스와 동일하게 새로운 타입을 정의하기 위해 사용되지만, 클래스와 구분되는 몇 가지 특징을 가진다. 그중 하나가 생성자를 호출할 수 없다는 점이다.

```scala
new AE
// trait AE is abstract; cannot be instantiated
```

트레잇으로 정의한 뒤 생성자를 호출하면 컴파일 오류가 발생한다.

## Scala에서 대수적 데이터 타입 다루기

Scala에서 대수적 데이터 타입의 값을 어떻게 처리할 수 있는 알아보자. 산술식 타입의 값이 나타내는 정수를 계산하는 함수인 `interpret`을 정의해보자. 단, 문제를 간단히 하기 위하여, 단항 연산자는 `-`, 이항 연산자는 `+`만 가능하다고 가정하며, 그 외의 연산자를 사용한 식이 인자로 들어온 경우에는 예외를 발생시킨다.

### 동적 타입 검사

가장 먼저 떠오르는 방법은 주어진 산술식이 `Num`인지 `UnOp`인지 `BinOp`인지를 동적으로, 즉 실행 시간에, 검사하여 `if-else`를 사용하여 경우를 나누어 처리하는 것이다.

```scala
def interpret(e: AE): Int = {
  if (...) // 수인 경우
    e.n
  else if (...) // 단항 연산자 -가 붙은 식인 경우
    -interpret(e.e)
  else if (...) // 이항 연산자 +가 사이에 있는 식인 경우
    interpret(e.e0) + interpret(e.e1)
  else
    throw new Exception
}
```

Scala에서는 `isInstanceOf`를 사용해서 실행 시간에 객체가 특정 타입의 원소인지 검사할 수 있다. `[식].isInstanceof[[타입]]`이라는 식을 계산하면, 앞의 식을 계산해서 얻은 객체가 주어진 타입의 원소이면 `true`, 아니면 `false`가 나온다.

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

그러나, 이 코드는 컴파일 오류를 일으킨다. 컴파일 시간에 매개변수 `e`에 관하여 컴파일러가 알고 있는 정보는 `e`가 가리키는 값이 `AE` 타입의 원소라는 것밖에 없기에, `e`의 필드에 접근할 수 없다. 이를 해결하기 위해서는 `asInstanceOf`를 사용하여 명시적으로 *타입* *변환*(type casting)을 해야 한다. `asInstanceOf`는 어떤 식이 특정 타입을 가진다는 정보를 컴파일러에 명시적으로 전달한다. `[식].asInstanceOf[[타입]]`이라는 식을 본 컴파일러는 알아낸 앞 식의 타입을 무시하고 프로그래머가 명시적으로 제공한 타입을 믿는다.

`asInstanceOf`는 실행 시간에 동적 타입 검사만 하고 값을 바꾸지는 않는다. `[식].asInstanceOf[[타입]]`이라는 식을 생각해보자. 앞의 식을 계산한 값이 주어진 타입의 원소라면, 전체 식을 계산한 결과는 앞의 식을 계산한 값과 같다. 만약 계산한 값이 주어진 타입의 원소가 아니라면, 예외가 발생한다.

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

코드가 실제로 하는 일에 비해서 길고 복잡하다. 계산을 나타내는 코드는 별로 없고, 동적 타입 검사와 명시적인 타입 변환이 코드의 많은 부분을 차지한다. 단순히 코드가 길고 복잡하다는 것 이외에도 이런 방식으로 코드를 작성하면 실행 시간에 오류가 발생할 위험이 커진다. `asInstanceOf`를 사용한 코드는 컴파일러가 부정확하여 알아내지 못한 정보를 프로그래머가 알려줄 수 있다는 장점이 있다. (여기서 부정확하다는 것은 틀린 정보를 찾는다는 것이 아니라, 모든 정보를 찾아내지 못한다는 뜻이다.) 그러나, 동시에 프로그래머가 실수로 잘못된 정보를 제공할 큰 위험성이 존재한다. 잘못된 정보를 제공한다면, 실행 시간에 타입 오류가 발생하여 프로그램이 비정상적으로 종료될 수 있다. 물론 위 코드는 열 줄 정도에 불과하고 큰 어려움 없이 코드가 올바르다는 것을 확인할 수 있다. 그렇지만, 타입이나 수행해야 하는 계산이 복잡한 경우에는 코드에 오류가 있을 가능성이 높다.

### 메서드 오버로딩

메서드 *오버로딩*(overloading)은 같은 이름의 메서드를 매개변수의 타입을 다르게 하여 여러 개 정의하는 것을 의미한다. Scala를 포함한 많은 객체지향언어에서 메서드 오버로딩이 가능하다.

```scala
object Show {
  def show(i: Int): String = i + ": Int"
  def show(s: String): String = s + ": String"
}
Show.show(1)  // "1: Int"
Show.show("1")  // "1: String"
```

단독 객체 `Show`는 두 개의 `show` 메서드를 가지고 있다. 각각 `Int`와 `String`을 매개변수 타입으로 가지고 있기 때문에 문제없이 컴파일 된다. `Show` 객체의 `show` 메서드를 호출할 때, 주어진 인자의 타입에 적합한 메서드가 호출되는 것을 볼 수 있다.

메서드 오버로딩을 사용해서 `interpret` 메서드를 올바르게 구현할 수 있을까?

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

잘 작동하는 것으로 보이지만, 사실은 그렇지 않다.

```scala
// -1 + 2
Interpreter.interpret(new BinOp(
  "+",
  new UnOp("-", new Num(1)),
  new Num(2)
))
// java.lang.Exception: What is it?
```

놀랍게도 첫 번째 메서드가 호출된 것을 볼 수 있다. 그 이유는 Scala에서 메서드를 선택할 때, 괄호 안에 들어 있는 인자의 경우, 컴파일 시간의 타입만 고려되기 때문이다. 따라서, `interpret(e.e0)`라는 식을 계산할 때 `e.e0`의 컴파일 시간 타입은 `AE`이므로 실행 시간 타입은 `UnOp`이지만 첫 번째 메서드가 호출되는 것이다. 따라서, 메서드 오버로딩을 통해 올바른 `interpret` 메서드를 구현하는 것은 불가능하다.

### 방문자 패턴

객체지향 프로그래밍에서 이런 문제를 해결하기 위해서 널리 사용하는 코딩 방식이 *방문자* *패턴*(visitor pattern)이다. 방문자 패턴을 사용하여 `interpret` 메서드를 구현할 수 있으나, 방문자 패턴은 코드를 길고 복잡하게 만들며, 상당한 양의 상용구 코드가 필요하다. 그럼에도, 방문자 패턴은 Java처럼 패턴 대조가 없는 언어에서 이런 문제를 해결하는 가장 효과적인 방법이기에 많이 사용된다. 이 글에서 자세히 다루지는 않을 것이기에 방문자 패턴이 궁금한 사람은 직접 알아보기를 추천한다. [영어 Wikipedia](https://en.wikipedia.org/wiki/Visitor_pattern)에 자세한 설명이 나와 있다.

### 패턴 대조

패턴 대조는 대수적 데이터 타입을 다루기 위한 가장 좋은 방법이다. 패턴 대조는 함수형 언어에서 흔히 사용된다. 다음은 OCaml에서 패턴 대조를 사용해서 `interpret` 함수를 정의한 것이다.

```ocaml
let rec interpret e =
  match e with
  | Num n -> n
  | UnOp ("-", e0) -> -(interpret e0)
  | BinOp ("+", e0, e1) -> (interpret e0) + (interpret e1)
  | _ -> raise Exception
```

OCaml을 전혀 모르고 보더라도 직관적이며 간결하다.

Scala에서도 패턴 대조를 사용할 수 있다. 패턴 대조를 사용하기 위해서는 일반적인 클래스 대신 *경우* *클래스*(case class)를 사용해야 한다. (정확히 말하면, 일반적인 클래스도 패턴 대조를 사용할 수 있으나 이 글에서는 다루지 않는다.) 산술식을 경우 클래스를 사용하여 다시 정의해보자.

```scala
trait AE
case class Num(n: Int) extends AE
case class UnOp(op: String, e: AE) extends AE
case class BinOp(op: String, e0: AE, e1: AE) extends AE
```

경우 클래스는 특별한 존재가 아니라 단순한 문법적 설탕으로 컴파일 과정에서 일반 클래스를 사용하는 코드로 변환된다. 경우 클래스는 일반 클래스에 없는 몇 가지 기능을 가진다. 패턴 대조가 가능하며, 객체를 만들 때 `new` 키워드가 필요 없다. 이미 이전 글에서 `new` 없이 리스트와 옵션을 만들었다. 또한, 클래스 매개변수에 `val` 키워드가 필요 없다. 그 밖에도 여러 추가적인 기능이 존재하나 이 글의 주제와는 무관하므로 다루지 않겠다.

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

`interpret` 함수를 패턴 대조를 사용하여 정의하였다. 동적 타입 검사를 사용하는 것보다 코드가 더 간결하며, 명시적 타입 변환이 필요 없기에 안전하다.

## 패턴 대조의 장점과 Scala가 제공하는 패턴

### 완전성 검사

패턴 대조는 패턴의 완전성(exhaustivity)을 검사한다. 패턴 대조 시에 주어진 값이 어떤 패턴과도 일치하지 않으면 실행 시간에 대조 오류가 발생한다.

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

위 함수는 `Num` 패턴을 빠트렸다.

```scala
interpret(Num(3))
// scala.MatchError: Num(3) (of class Num)
```

따라서, `Num` 타입의 값이 인자로 들어오면 대조 오류가 발생한다.

Scala 컴파일러는 패턴이 완전한지, 즉 프로그래머가 빠트린 패턴이 없는지 검사하여, 빠트린 패턴이 있다면 경고를 한다. 그러나, 위 함수는 컴파일 시에 어떤 경고도 받지 못하였다. 그 이유는 `AE` 트레잇의 자식 클래스를 모두 알 수 없기 때문이다. `AE` 트레잇을 상속하여 클래스를 정의하는 코드는 어느 파일에나 있을 수 있다. Scala 컴파일러는 파일 단위로 컴파일 하므로, 컴파일 시점에 한 파일만 보고 모든 자식 클래스를 알아낼 수 없다. 이 문제는 `sealed` 키워드를 사용하여 해결할 수 있다. `sealed` 키워드가 붙은 클래스나 트레잇은 해당 클래스나 트레잇이 정의된 파일이 아닌 다른 파일에 자식 클래스나 트레잇이 정의될 수 없다. 따라서, 컴파일러가 한 파일만 보고도 모든 자식 클래스를 알아낼 수 있고, 패턴의 완전성을 검사할 수 있다.

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

`AE`를 *봉인된*(sealed) 트레잇으로 정의하자 컴파일러가 패턴이 완전하지 않다고 경고한다. 프로그램이 복잡할수록 완전성 검사는 더 도움이 된다. 완전성 검사는 안전한 프로그램을 만들 수 있도록 도와주는, 패턴 대조의 큰 장점이다.

### 상수 패턴과 와일드카드 패턴

명령형 언어에서는 `switch-case` 문을 사용해서 주어진 값을 여러 경우로 나누어 처리한다. 패턴 대조는 `switch-case`의 일반화된 형태라고 볼 수 있다. 아래 코드는 Java에서 `switch-case` 문을 사용한 예시이다.

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

Scala에는 *상수*(constant) 패턴과 *와일드카드*(wildcard) 패턴이 있다. 상수 패턴은 정수나 문자열 등의 *리터럴*(literal)l로, 대조하는 값이 패턴이 나타내는 값과 같으면 패턴이 일치한다. 와일드카드 패턴은 밑줄 문자를 사용하며, 모든 값에 일치하는 패턴이다. `switch-case`의 `default`와 같은 의미이다. 따라서, 위 함수는 Scala에서 패턴 대조를 사용해서 정의할 수 있다.

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

### 논리합 패턴

`switch-case` 문에서 `break`을 사용하지 않으면, 어떤 경우에 해당하는 코드를 실행한 뒤 문의 끝으로 이동하는 것이 아니라, 다음 경우에 해당하는 코드로 이동한다. 따라서, 이전 코드에서 `10`과 `9`에 해당하는 결괏값이 동일하므로 코드를 더 짧게 만들 수 있다.

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

반면, 이미 보았듯이 패턴 대조에서는 코드 흐름이 다음 패턴으로 이동하지 않는다. 두 개 이상의 패턴에 대해 같은 식을 계산하기 위해서는 *논리합*(or) 패턴을 사용할 수 있다. 논리합 패턴은 `[패턴] | [패턴] ...` 꼴로 패턴 사이에 수직선을 그은 것으로, `A | B`라는 패턴에 일치하는 값에는 `A`에 일치하는 값과 `B`에 일치하는 값이 모두 포함된다.

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

### 중첩된 패턴

패턴 안에 다른 패턴을 넣어 중첩된 패턴을 만들 수 있다. 중첩된 패턴을 사용하면 앞에서 구현한 `interpret` 함수를 더 간단하게 만들 수 있다.

```scala
def interpret(e: AE): Int = e match {
  case Num(n) => n
  case UnOp("-", e0) => -interpret(e0)
  case BinOp("+", e0, e1) =>
    interpret(e0) + interpret(e1)
  case _ => throw new Exception
}
```

패턴 `UnOp` 안에 상수 패턴 `"-"`가 사용되었기 때문에, 두 번째 패턴은 연산자 `"-"`가 붙은 산술식에만 일치한다. 마찬가지로 세 번째 패턴은 연산자 `"+"`를 사용한 산술식에만 일치한다.

패턴을 여러 번 중첩하는 것도 가능하다. 아래의 `optimizeAdd`는 인자로 주어진 산술식에서 `0`을 더하는 부분을 없애서 최적화한 산술식을 결과로 낸다.

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

단항 연산자로 `"abs"`도 사용할 수 있다고 가정하자. `"abs"`는 절댓값을 구하는 연산자이다. 만약, 어떤 산술식에 `"abs"`가 두 번 붙어있다면, `"abs"`가 한 번 붙어있는 산술식으로 바꿈으로써 최적화할 수 있다. 아래의 `optimizeAbs` 함수는 이를 구현한 것이다.

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

위 구현에서 한 가지 아쉬운 점은 `UnOp("abs", e0)`에 해당하는 값을 바로 `optimizeAbs`의 인자로 사용하지 못하고 `e0`에 해당하는 값을 얻어 다시 `UpOp` 객체를 만들어 인자로 넘긴다는 것이다. 패턴에 일치하는 값을 변수가 가리키게 만들기 위해 *골뱅이*(at sign)를 사용할 수 있다. `[변수] @ [패턴]` 형태로 패턴을 작성한 경우, 앞의 변수가 뒤 패턴에 일치한 값을 가리킨다.

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

중첩된 패턴과 골뱅이를 사용하여 복잡한 구조를 가진 값을 쉽게 처리할 수 있다.

### 도달할 수 없는 패턴

중첩된 패턴을 여러 개 사용하여 패턴 대조를 하면 한 값이 두 개 이상의 패턴에 일치하는 경우가 흔하다. `switch-case`와 마찬가지로 패턴 대조는 위에 있는 패턴부터 값과 대조하므로, 일치하는 패턴 중 가장 위에 있는 패턴이 선택된다. 만약 특수한 경우를 처리하는 패턴과 일반적인 경우를 처리하는 패턴을 모두 사용한다면, 특수한 경우를 위한 패턴이 더 위에 와야 한다. 그러나, 패턴의 수가 많다면, 패턴을 잘못된 순서로 배열하여 값이 원하지 않는 패턴에 일치할 수 있다. Scala는 잘못된 코드를 줄이기 위해서 *도달할* *수* *없는*(unreachable) 패턴을 발견하면 컴파일 시에 경고한다.

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

도달할 수 없는 패턴을 찾아주는 것 역시 패턴 대조의 큰 장점이다. 패턴의 순서를 잘못 정하거나, 중첩된 패턴을 잘못 사용하여 프로그램이 잘못된 동작을 하는 것을 막아준다. 그러나, 모든 잘못된 패턴이 도달할 수 없는 패턴을 만들지는 않는다. 모든 패턴에 도달할 수 있지만 일부 값은 의도하지 않은 패턴에 일치하는 코드도 많으며, 이런 코드는 컴파일러가 경고해주지 않으니 조심해야 한다.

### 타입 패턴

아래의 `optimizeNeg` 함수는 어떤 산술식 앞에 `"-"`가 두 개 붙은 경우, 두 단항 연산자를 모두 없애서 산술식을 최적화하는 함수이다.

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

첫 번째 패턴 `Num(_)`은 실질적으로 주어진 값이 `Num` 타입의 값인지 확인하는 역할만 하고 있다. 따라서, 타입 패턴을 사용하여 코드를 다시 쓸 수 있다. 타입 패턴은 `[변수]: [타입]` 형태로, 주어진 값이 해당 타입을 가지면, 패턴에 일치하고 변수가 그 값을 가리킨다. 변수가 필요하지 않다면 와일드카드 패턴을 사용할 수 있다.

```scala
def optimizeNeg(e: AE): AE = e match {
  case _: Num => e
  case UnOp("-", UnOp("-", e0)) => e0
  case UnOp(op, e0) => UnOp(op, optimizeNeg(e0))
  case BinOp(op, e0, e1) =>
    BinOp(op, optimizeNeg(e0), optimizeNeg(e1))
}
```

타입 패턴은 동적 타입 검사를 할 때 유용하다.

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

`Any`는 Scala에서 *최상위*(top) 타입으로 모든 값이 `Any` 타입의 원소이다.

단, 타입 패턴은 다형 타입의 타입 인자를 검사하지 않는다. 따라서, 리스트 같은 다형 타입을 패턴 대조할 때 주의해야 한다.

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

`List[String]` 타입의 인자를 넘겼지만, 첫 번째 패턴에 일치한다. 이는 컴파일러의 경고에서 볼 수 있듯, JVM의 *타입* *지우개*(type erasure) 때문에 타입 인자가 실행 시간에 남아있지 않으므로 타입 인자를 확인할 수 없기 때문이다. 다형성과 타입 지우개에 대해서는 나중 글에서 자세히 다룰 것이므로 이해하지 않고 넘어가도 괜찮다.

### 튜플 패턴

지난 글에서 튜플 패턴을 사용하는 것을 이미 보았다. 튜플 패턴은 `([패턴], ...)` 형태이다. 아래의 `equal` 함수는 두 리스트가 같은지 비교하며, 튜플 패턴을 사용한다.

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

### 패턴 보호

이진 (탐색) 나무는

* 빈 나무이거나,
* 뿌리 원소인 정숫값과 두 개의 자식 나무로 이루어진 나무이다.

```scala
sealed trait BST
case object Empty extends BST
case class Node(root: Int, left: BST, right: BST) extends BST
```

함수 `add`는 나무 하나와 정수 하나를 인자로 받아, 해당 정수를 추가한 나무를 만들어 결과로 낸다. 만약, 그 정수가 이미 주어진 나무의 원소라면 결괏값은 주어진 나무 그대로이다.

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

두 번째 패턴에 대응되는 식이 `if-else`를 사용하는데, *패턴* *보호*(pattern guard)를 사용하여 패턴 자체에 조건을 걸 수 있다. `[패턴] if [식]` 형태의 패턴은 값이 앞의 패턴에 일치하고 조건을 만족할 때(주어진 식을 계산하면 `true`일 때) 일치한다. `add`는 아래처럼 다시 쓸 수 있다.

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

단, 보호된 패턴은 완전성 검사의 대상이 아니다.

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

컴파일러는 위 코드에 아무런 경고도 주지 않는다. 그러나, 나무에 이미 있는 원소를 추가하는 경우에 대한 패턴이 없으므로 그 상황에는 대조 오류가 발생한다.

### 억음 부호 패턴

함수 `remove`는 나무 하나와 정수 하나를 인자로 받아 주어진 나무에서 주어진 정수를 제거한 나무를 결과로 낸다. 만약 주어진 정수가 나무의 원소가 아니라면, 주어진 나무가 그대로 결과이다. `remove`에서 사용하기 위하여 `removeMin` 함수도 정의하였다. `removeMin`은 비어 있지 않은 나무의 최솟값과 나무에서 최솟값을 제거하여 얻어진 나무의 순서쌍을 결과로 낸다.

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

`case Node(m, t0, Empty) if n == m`이라는 패턴은 *억음* *부호*(backtick)를 사용해서 ```case Node(`n`, t0, Empty)```라고 쓸 수 있다. 만약, `case Node(n, t0, Empty)`라고 쓴다면, 이 패턴은 뿌리 원소가 `n`과 같은지 비교하는 것이 아니라, 매개변수 `n`과는 다른 새로운 `n`을 정의하여 그 `n`이 뿌리 원소를 가리키게 한다. 그러나, 억음 부호를 사용함으로써, 새로운 `n`을 정의하지 않고, 이미 범위에 있는 `n`과 비교하라는 의미를 나타낼 수 있다.

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

## 패턴 대조의 활용

### 변수 선언

변수를 선언할 때 패턴 대조를 사용할 수 있다.

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

패턴 대조를 사용한 변수 선언은 코드를 간결하게 만들지만, 패턴이 일치하지 않으면 대조 오류가 발생하므로 패턴이 일치한다는 보장이 있을 때만 사용하는 것이 좋다. 튜플 패턴은 반드시 일치하므로, 튜플을 분해하여 변수에 대입하는 것이 가장 일반적인 사용 방법이다.

### 익명 함수

함수 `toSum`은 정수의 순서쌍이 들어 있는 리스트를 인자로 받아, 순서쌍의 두 정수의 합을 원소로 하는 리스트를 결과로 낸다.

```scala
def toSum(l: List[(Int, Int)]): List[Int] =
  l.map(p => p match {
    case (n, m) => n + m
  })

toSum(List((0, 1), (2, 3), (3, 4)))
// List(1, 5, 7)
```

매개변수 `p`를 바로 패턴 대조의 대상으로 사용하는 익명 함수는 장황하다. Scala에서는 함수 타입이 기대되는 자리에 `{ case [패턴] => [식] ... }` 형태의 식이 오면 이를 익명 함수로 간주한다. 따라서, `toSum`은 아래처럼 다시 쓸 수 있다.

```scala
def toSum(l: List[(Int, Int)]): List[Int] =
  l.map({ case (n, m) => n + m })
```

패턴 대조를 사용하여 정의된 익명 함수를 인자로 사용할 때는 특별히 괄호를 생략할 수 있다.

```scala
def toSum(l: List[(Int, Int)]): List[Int] =
  l.map { case (n, m) => n + m }
```

마침표도 생략할 수 있다. 위와 달리, 이는 특별한 경우는 아니고, Scala에서 메서드를 중위 연산자로 사용하는 것을 허용하기 때문이다. `Nil.::(0)`을 `0 :: Nil`이라 쓸 수 있는 것과 같다.

```scala
def toSum(l: List[(Int, Int)]): List[Int] =
  l map { case (n, m) => n + m }
```

### `for`

`toSum`은 `map` 메서드 대신 `for` 식을 사용하여 구현할 수 있다.

```scala
def toSum(l: List[(Int, Int)]): List[Int] =
  for (p <- l) yield p match {
    case (n, m) => n + m
  }
```

`for` 식에서도 패턴 대조를 사용할 수 있다.

```scala
def toSum(l: List[(Int, Int)]): List[Int] =
  for ((n, m) <- l) yield n + m
```

코드가 더 간결하고 읽기 쉽다.

## 감사의 말

글을 확인하고 의견을 주신 류석영 교수님, Scala 세미나를 준비할 때 의견 주신 모든 분과 Scala 세미나에 참석하신 모든 분께 감사드립니다.
