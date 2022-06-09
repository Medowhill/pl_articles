이번 글에서는 TFAE에 *매개변수에 의한 다형성*(parametric polymorphism)을 추가하여 TpolyFAE를 정의한다. TpolyFAE의 문법, 동적 의미, 타입 체계를 정의하며 타입 검사기와 인터프리터를 구현할 것이다.

## 매개변수에 의한 다형성

TFAE의 함수는 FAE의 함수에 비해 할 수 있는 일이 제한적이다. FAE의 함수 \(\lambda x.x\)를 생각해 보자. 이 함수는 값 하나를 인자로 받아 그 값을 결과로 내는 항등 함수이다. 어느 값이나 인자로 받을 수 있다. 함수 몸통에서 인자를 가지고 아무 계산도 하지 않으므로 어떤 인자를 받아도 몸통을 계산할 때 타입 오류가 일어나지 않는다. 한편 TFAE의 함수 \(\lambda x:\textsf{num}.x\)는 정수 하나를 인자로 받아 그 정수를 결과로 내는 항등 함수이다. 매개변수 타입이 정수로 제한되어 있기 때문에 정수가 아닌 타입의 값을 인자로 넘기는 프로그램은 타입 체계가 거절한다. 그러나 함수 몸통을 보면 인자를 가지고 아무 계산도 하지 않는다. 인자가 정수가 아니어도 함수 몸통을 계산할 때 타입 오류가 일어나지 않는다. 몸통을 그대로 두고 매개변수 타입 표시만 바꾸어도 아무런 문제없이 다른 타입의 값을 인자로 받게 할 수 있다. 예를 들면 \(\lambda x:\textsf{bool}.x\)는 불 값을 인자로 받아 그 값을 결과로 내며 올바른 타입의 식이다. 완전히 같은 일을 하는 함수임에도 매개변수 타입이 다르다는 이유만으로 두 개의 다른 함수를 정의해야 한다.

다음 FAE 식은 타입 오류 없이 계산된다.

\[
\begin{array}{l}
\textsf{let}\ f=\lambda x.x\ \textsf{in} \\
\textsf{let}\ x=f\ 1\ \textsf{in} \\
f\ \textsf{true}
\end{array}
\]

반면 다음 TFAE 식은 타입 오류 없이 계산됨에도 타입 체계가 거절한다.

\[
\begin{array}{l}
\textsf{let}\ f=\lambda x:\textsf{num}.x\ \textsf{in} \\
\textsf{let}\ x=f\ 1\ \textsf{in} \\
f\ \textsf{true}
\end{array}
\]

불 값을 위한 항등 함수를 추가로 정의해야만 위 식을 올바른 타입의 식으로 만들 수 있다.

\[
\begin{array}{l}
\textsf{let}\ f=\lambda x:\textsf{num}.x\ \textsf{in} \\
\textsf{let}\ g=\lambda x:\textsf{bool}.x\ \textsf{in} \\
\textsf{let}\ x=f\ 1\ \textsf{in} \\
g\ \textsf{true}
\end{array}
\]

이런 문제를 해결하기 위해서는 언어에 *다형성*(polymorphism)을 추가해야 한다. 다형성은 하나의 대상을 여러 타입으로 사용할 수 있게 하는 언어의 기능을 말한다. 즉, \(\lambda x.x\)라는 하나의 함수를 여러 타입으로 사용할 수 있게 만드는 것이다. 다형성에는 여러 종류가 있다. 가장 널리 사용되는 다형성은 세 가지로, 매개변수에 의한 다형성, *서브타입에 의한 다형성*(subtype polymorphism), *즉석 다형성*(ad hoc polymorphism)이다. 이번 글에서는 매개변수에 의한 다형성에만 집중한다. 서브타입에 의한 다형성은 다음 글에서 다룰 것이다. 즉석 다형성은 강의 보조 자료에서 다루지 않을 것이며, 함수 오버로딩이나 메서드 오버로딩이 즉석 다형성의 대표적 예시이다.

매개변수에 의한 다형성은 말 그대로 매개변수를 사용하여 하나의 대상을 여러 타입으로 사용할 수 있게 만드는 것이다. 지금까지 매개변수라는 말은 함수를 설명할 때 사용했다. \(\lambda x.x+x\)라는 함수의 매개변수는 \(x\)이다. 함수 몸통 \(x+x\)는 매개변수 \(x\)를 사용하여 표현되었다. 함수의 역할은 식을 식으로 요약하는 것이다. \(1+1\)이라는 식은 \((\lambda x.x+x)\ 1\)이라고 바꾸어 쓸 수 있다. \(2+2\)라는 식은 \((\lambda x.x+x)\ 2\)로 바꿀 수 있다. 또, \((1+2)+(1+2)\)라는 식은 \((\lambda x.x+x)\ (1+2)\)로 바꿀 수 있다. 이처럼, 함수는 식에 등장하는 어떤 부분을 매개변수로 바꾸어 요약한 것이다. 함수를 식에 적용함으로써 비슷한 형태를 가진 여러 식들을 일관적으로 표현할 수 있다. 각 식에서 달라지는 부분은 인자로 표현된다.

매개변수에 의한 다형성은 함수의 개념을 타입에 적용한 것이다. 함수는 식을 식으로 요약한 것이다. 함수를 어떤 식에 적용하면 그 결과는 다른 어떤 식이다. 언어에 매개변수에 의한 다형성을 추가하면 타입 함수와 타입 적용이 생긴다. 타입 함수는 식을 타입으로 요약한 것이다. 타입 함수를 어떤 타입에 적용하면 그 결과는 어떤 식이다. \(\lambda x:\textsf{num}.x\)와 \(\lambda x:\textsf{bool}.x\)를 생각해 보자. 두 식에서 다른 부분은 매개변수 타입 표시뿐이다. 비슷한 형태의 두 식에서 다른 부분만을 매개변수로 바꾸어 보자. 그러면 \(\Lambda\alpha.\lambda x:\alpha.x\)가 나온다. 이 식은 타입 함수로, 식을 타입으로 요약한다. 함수와 타입 함수가 쉽게 구분되도록 \(\lambda\) 대신 \(\Lambda\)를 사용했다. 이 타입 함수를 원하는 타입에 적용할 수 있다. 타입 적용을 표현하기 위해 \(\lbrack\ \rbrack\)를 사용하겠다. \(e\ \lbrack\tau\rbrack\)는 타입 함수 \(e\)를 타입 \(\tau\)에 적용한 것이다. \(\lambda x:\textsf{num}.x\)는 \((\Lambda\alpha.\lambda x:\alpha.x)\ \lbrack\textsf{num}\rbrack\)으로 바꿀 수 있다. 또, \(\lambda x:\textsf{bool}.x\)는 \((\Lambda\alpha.\lambda x:\alpha.x)\ \lbrack\textsf{bool}\rbrack\)으로 바꿀 수 있다. 이것이 타입 함수를 타입에 적용한 것이다. 함수를 식에 적용할 때 달라지는 부분이 인자로 표현되었듯, 타입 함수를 타입에 적용할 때도 달라지는 부분이 인자로 표현된다.

매개변수에 의한 다형성을 통해서 위에서 본 식을 함수를 한 번만 정의하고도 올바른 타입의 식이 되게 할 수 있다.

\[
\begin{array}{l}
\textsf{let}\ f=\Lambda\alpha.\lambda x:\alpha.x\ \textsf{in} \\
\textsf{let}\ x=f\ \lbrack\textsf{num}\rbrack\ 1\ \textsf{in} \\
f\ \lbrack\textsf{bool}\rbrack\ \textsf{true}
\end{array}
\]

FAE 식보다는 복잡하지만 TFAE와 달리 함수를 한 번만 정의하면 되는 것을 볼 수 있다. 타입 요약과 타입 적용을 하는 부분이 추가되긴 했지만, 사실상 \(\lambda x:\alpha.x\)라는 하나의 함수를 \(\textsf{num}\rightarrow\textsf{num}\) 타입으로도 사용하고 \(\textsf{bool}\rightarrow\textsf{bool}\) 타입으로도 사용한 것이다. 이렇게 매개변수를 사용하여 하나의 대상을 여러 타입으로 사용할 수 있게 해주기 때문에 이 기능을 매개변수에 의한 다형성이라 부른다.

전통적으로 매개변수에 의한 다형성은 함수형 언어의 기능이었다. 대표적인 함수형 언어인 OCaml과 Haskell이 매개변수에 의한 다형성을 지원한다. 그에 반해 객체 지향 언어는 서브타입에 의한 다형성을 제공하였다. 대표적인 객체 지향 언어인 Java의 경우, JDK 1.0부터 J2SE 1.4까지 매개변수에 의한 다형성 없이 서브타입에 의한 다형성만을 제공하였다. 그러나 복잡한 프로그램을 만들 일이 많아짐에 따라 사람들은 언어에 더 많은 기능을 요구하게 되었다. 그 결과 J2SE 5.0에 매개변수에 의한 다형성이 추가되어 현재의 Java는 매개변수에 의한 다형성을 지원한다. Scala를 비롯한 최근 언어는 함수형 언어이면서 객체 지향 언어인 경우가 많다. 한 언어에 서브타입에 의한 다형성과 객체 지향 언어에 의한 다형성이 모두 존재하는 일 역시 흔하다.

객체 지향 언어에서 매개변수에 의한 다형성을 지원하는 경우 *제네릭스*[^gen](generics)라는 표현을 많이 사용한다. 매개변수에 의한 다형성과 제네릭스는 대부분 상황에서 같은 뜻으로 사용할 수 있다. 다만, 매개변수에 의한 다형성은 이론적인 논의를 하거나 함수형 언어에 대해 설명할 때 일반적으로 쓰이는 단어이고, 제네릭스는 객체 지향 언어에 존재하는 매개변수에 의한 다형성을 일컫기 위해 주로 사용한다는 사실을 알아두는 것이 좋다.

[^gen]: 단어 ‘generic’의 발음이 [dʒənerɪk]이므로 외래어 표기법에 따르면 ‘generics’는 ‘저네릭스’로 적는 것이 원칙이지만 이미 관용적으로 ‘제네릭스’라 부르는 것이 굳어졌기에 ‘제네릭스’라 쓴다.

매개변수에 의한 다형성과 서브타입에 의한 다형성이 한 언어에 공존하면 여러 재미있는 일이 일어난다. 타입 매개변수에 서브타입 관계에 대한 제약을 걸어 타입 함수가 받을 수 있는 타입 인자에 제한을 걸 수 있다. 또, *타입 생성자*(type constructor)가 인자로 받은 타입의 서브 타입 관계를 어떻게 다루는지를 *가변성*(variance)이라는 개념으로 표현한다. 타입 매개변수에 대한 제약이나 가변성은 많은 실제 언어에서 찾아볼 수 있는 개념이며 학문적으로도 흥미롭다. 그러나 이 글의 수준을 벗어나므로 더 이상 다루지 않는다.

### Scala의 매개변수에 의한 다형성

Scala는 매개변수에 의한 다형성을 제공한다. Scala가 제공하는 매개변수에 의한 다형성은 이 글에서 볼 매개변수에 의한 다형성과 다르다. 그럼에도 기본적인 개념은 똑같기에 Scala를 보는 것이 직관적인 이해를 도울 것이라 생각한다. 그래서 Scala의 매개변수에 의한 다형성을 가볍게 다루려 한다.

다음 코드는 *다형*(polymorphic) 항등 함수를 정의한다. 어떤 대상이 다형이라는 말은 그 대상이 타입 함수처럼 사용될 수 있다는 뜻이다.

```scala
def id[T](x: T): T = x
```

`id`는 다형 함수이다. 함수 이름 `id` 다음에 있는 `[T]`가 타입 매개변수를 정의하는 부분이다. 즉, `T`는 `id` 함수의 유일한 타입 매개변수이다. 함수의 매개변수 타입, 결과 타입을 표시할 때 `T`가 사용된다. (타입 매개변수와 매개변수 타입은 전혀 다른 대상을 가리키는 용어임에 주의하자.) 앞에서 본 \(\lambda \alpha.x:\alpha.x\)에서의 \(\alpha\)의 역할을 `T`가 하는 것이다.

다음 코드는 위에서 정의한 `id` 함수를 사용하는 예시이다.

```scala
id[Int](1)  // 1
id[Bool](true)  // true
```

`id`를 사용하기 위해서는 적절한 타입 인자를 넘겨야 한다. `id` 자체는 다형 함수이나 `id[Int]`는 `Int => Int` 타입의 함수이다. 따라서 타입이 `Int`인 `1`에 적용할 수 있다. 또, `id[Bool]`은 `Bool => Bool` 타입의 함수이다. 그러므로 `Bool` 타입의 값인 `true`에 적용할 수 있다. 앞에서 본 \((\Lambda\alpha.\lambda x:\alpha.x)\ \lbrack\textsf{num}\rbrack\ 1\), \((\Lambda\alpha.\lambda x:\alpha.x)\ \lbrack\textsf{bool}\rbrack\ \textsf{true}\)와 매우 유사하다.

함수뿐 아니라 클래스 역시 다형일 수 있다. 다음 코드는 정수의 리스트를 정의한다.

```scala
sealed trait List
case object Nil extends List
case class Cons(h: Int, t: List) extends List
```

위처럼 정의한 리스트는 매우 잘 작동하지만 정수만을 원소로 가질 수 있다는 문제가 있다. 불 값의 리스트도, 문자열의 리스트도 정수의 리스트와 같은 방법으로 정의할 수 있다. 원소의 타입을 제외하고 완전히 같은 코드를 여러 번 작성하는 것은 비효율적이다. 다형 클래스를 사용함으로써 임의의 타입의 원소의 리스트를 한 번에 정의할 수 있다.

```scala
sealed trait List[T]
case class Nil[T]() extends List[T]
case class Cons[T](h: T, t: List[T]) extends List[T]
```

`List[T]`의 `[T]`는 `List` 클래스가 한 개의 타입 매개변수 `T`를 가지고 있음을 표현한다. 이제 다음과 같이 리스트를 만들 수 있다.

```scala
Cons[Int](0, Cons[Int](1, Nil[Int]()))
Cons[Bool](true, Nil[Bool]())
Cons[String]("0", Cons[String]("1", Cons[String]("2", Nil[String]())))
```

정수의 리스트, 불 값의 리스트, 문자열의 리스트를 모두 만들 수 있다. 리스트를 정의한 코드에 대한 설명이 충분하지는 않으나 이 글의 목표가 Scala에 대해 자세히 알아보는 것이 아니기에 이 정도로 넘어가겠다.

다형 클래스는 TpolyFAE가 제공하는 매개변수에 의한 다형성으로 표현되지 않는다. 그럼에도 설명한 이유는 매개변수에 의한 다형성에 대한 직관적 이해를 돕고 사용함으로써 좋은 점을 보이기 위함이다.

## 문법

다음은 TpolyFAE의 요약 문법이다. TFAE와 비교하여 추가된 부분만 적었다.

\[
\begin{array}{rrcl}
\text{Type Identifier} & \alpha & \in & \mathit{TId} \\
\text{Expression} & e & ::= & \cdots \\
&&|& \Lambda\alpha.e \\
&&|& e\ \lbrack\tau\rbrack \\
\text{Value} & v & ::= & \cdots \\
&&|& \langle \Lambda\alpha.e,\sigma\rangle \\
\text{Type} & \tau & ::= & \cdots \\
&&|& \alpha \\
&&|& \forall\alpha.\tau \\
\end{array}
\]

메타변수 \(\alpha\)는 타입 식별자를 나타낸다.

식 \(\Lambda\alpha.e\)는 타입 함수, 또는 타입 요약이다. \(\alpha\)는 이 타입 함수의 타입 매개변수이다. \(e\) 안에서 \(\alpha\)를 사용할 수 있다. 앞에서 본 \(\Lambda\alpha.\lambda x:\alpha.x\)가 타입 함수의 대표적인 예이다. 타입 함수는 타입 식별자를 묶는다. 식 \(\Lambda\alpha.e\)에서 타입 매개변수 \(\alpha\)는 타입 식별자 \(\alpha\)의 묶는 등장이며 그 영역은 \(e\) 전체이다.

타입 식별자 \(\alpha\)는 타입으로 사용될 수 있다. 식 \(\Lambda\alpha.\lambda x:\alpha.x\)에서 \(\alpha\)가 \(x\)의 타입으로 사용되었다. TVFAE에서 정의한 타입의 이름인 \(t\)를 타입으로 사용할 수 있던 것과 비슷하다. 문법은 타입 식별자가 어디에서 사용되든 상관하지 않는다. 그러나 TVFAE에서 정의하지 않은 타입을 사용할 수 없도록 타입이 올바른 형태인지 검사한 것처럼, TpolyFAE에서도 타입 식별자가 타입 함수에 묶인 후 올바른 영역에서만 사용되게 해야 한다. 이는 올바른 형태 규칙과 타입 규칙이 할 일로, 뒤에서 볼 것이다.

값 \(\langle\Lambda\alpha.e,\sigma\rangle\)는 타입 함수 값이다. 함수를 계산하면 클로저가 나오는 것처럼 타입 함수를 계산하면 타입 함수 값이 나온다. 함수의 몸통은 함수가 클로저가 될 때 계산되지 않고 함수가 호출되었을 때 계산된다. 몸통은 클로저가 만들어질 때의 환경 아래서 계산되어야 하므로 클로저는 만들어질 때의 환경을 저장하고 있다. 마찬가지로 타입 함수의 몸통도 타입 함수가 타입 함수 값이 될 때 계산되지 않는다. 타입 함수가 어떤 타입에 적용되면 그때 몸통이 계산된다. 타입 함수의 몸통 역시 타입 함수 값이 만들어질 때의 환경 아래서 계산되어야 한다. 그러므로 클로저와 마찬가지로 타입 함수 값도 환경을 저장한다. 환경 \(\sigma\) 아래서 타입 함수 \(\Lambda\alpha.e\)를 계산한 결과는 타입 함수 값 \(\langle\Lambda\alpha.e,\sigma\rangle\)이다. 빈 환경 아래서 \(\Lambda\alpha.\lambda x:\alpha.x\)를 계산하면 그 결과는 \(\langle\Lambda\alpha.\lambda x:\alpha.x,\emptyset\rangle\)이다.

타입 \(\forall\alpha.\tau\)는 타입 함수의 타입이다. 타입 함수 \(\Lambda\alpha.e\)가 있을 때, \(e\)의 타입이 \(\tau\)라면 타입 함수의 타입은 \(\forall\alpha.\tau\)이다. 예를 들어, \(\lambda x:\alpha.x\)의 타입은 \(\alpha\rightarrow\alpha\)이므로 \(\Lambda\alpha.\lambda x:\alpha.x\)의 타입은 \(\forall\alpha.\alpha\rightarrow\alpha\)이다. 기호 \(\forall\)를 *전칭 기호*(universal quantifier)라 부르는 것에 따라 \(\forall\alpha.\tau\) 형태의 타입을 *전칭 타입*(universal type; universally quantified type)이라 부른다. 전칭 타입은 타입 함수와 비슷하게 타입 식별자를 묶는 효과가 있다. 타입 \(\forall\alpha.\tau\)에서 \(\alpha\)는 타입 식별자 \(\alpha\)의 묶는 등장이며 그 영역은 \(\tau\) 전체이다.

식 \(e\ \lbrack\tau\rbrack\)는 타입 적용을 하는 식이다. \(e\)를 계산한 결과가 타입 함수 값이면 타입 함수의 몸통을 값이 가지고 있는 환경 아래서 계산하면 된다. 함수를 값에 적용할 때는 인자를 환경에 추가한다. 값을 환경에 저장하는 것처럼 타입은 타입 환경에 저장된다. 그러나 타입 환경은 실행 중에 존재하지 않으므로 타입 적용을 계산할 때 타입을 타입 환경에 추가할 수는 없다. 그 대신 치환을 사용한다. \(e\)의 계산 결과가 \(\langle\Lambda\alpha.e',\sigma\rangle\)이면 \(e\ \lbrack\tau\rbrack\)를 계산하는 것은 \(e'\)에서 \(\alpha\)를 \(\tau\)로 치환하여 얻은 식을 \(\sigma\) 아래서 계산하는 것이다. 치환에 대해서는 뒤에서 다시 볼 것이다. 앞에서 \(\Lambda\alpha.\lambda x:\alpha.x\)를 빈 환경 아래서 계산하면 \(\langle\Lambda\alpha.\lambda x:\alpha.x,\emptyset\rangle\)이 나온다고 했다. 따라서 \((\Lambda\alpha.\lambda x:\alpha.x)\ \lbrack\textsf{num}\rbrack\)을 계산하는 것은 \(\lambda x:\alpha.x\)에서 \(\alpha\)를 \(\textsf{num}\)으로 치환하여 나온 \(\lambda x:\textsf{num}.x\)을 타입 환경 값이 가지고 있던 빈 환경 아래에서 계산하는 것이다. 그 결과는 \(\langle\lambda x.x,\emptyset\rangle\)이다.

타입 함수가 전칭 타입을 만들어내는 식이라면 타입 적용은 전칭 타입을 사용하는 식이다. 식 \(e\ \lbrack\tau\rbrack\)에서 \(e\)의 타입이 전칭 타입인 \(\forall\alpha.\tau'\)이라면 전체 식의 타입은 \(\tau'\)에서 \(\alpha\)를 \(\tau\)로 치환하여 얻은 타입이다. 예를 들면, \(\Lambda\alpha.\lambda x:\alpha.x\)의 타입은 \(\forall\alpha.\alpha\rightarrow\alpha\)라 하였다. 그러면 \((\Lambda\alpha.\lambda x:\alpha.x)\ \lbrack\textsf{num}\rbrack\)의 타입은 \(\alpha\rightarrow\alpha\)에서 \(\alpha\)를 \(\textsf{num}\)으로 치환한 것이므로 \(\textsf{num}\rightarrow\textsf{num}\)이다.

## 동적 의미

치환을 먼저 정의하겠다. 치환은 타입에 들어 있는 타입 식별자를 치환하는 것과 식에 들어 있는 타입 식별자를 치환하는 것의 두 종류가 있다. 식 안에 타입이 등장하므로 식에 대한 치환을 정의하려면 타입에 대한 치환이 먼저 정의되어야 한다.

\(\tau'[\alpha\leftarrow\tau]\)은 타입 \(\tau'\)에서 \(\alpha\)를 \(\tau\)로 치환하여 얻은 타입을 나타낸다. 즉, 치환은 타입, 타입 식별자, 타입에서 타입으로 가는 함수이다. \(\alpha\)를 \(\tau\)로 치환한다는 것은 모든 자유 식별자 \(\alpha\)를 \(\tau\)로 대체한다는 뜻이다. \(\tau'\) 안에 \(\alpha\)의 묶는 등장이 있을 수 있다. 그 영역 안에 나오는 \(\alpha\)는 자유 식별자가 아닌 묶인 등장이므로 대체해서는 안 된다.

다음은 타입에 대한 치환을 정의한다.

\[
\begin{array}{rcl}
\textsf{num} \lbrack\alpha\leftarrow\tau\rbrack &=& \textsf{num} \\
(\tau_1\rightarrow\tau_2) \lbrack\alpha\leftarrow\tau\rbrack &=&
(\tau_1 \lbrack\alpha\leftarrow\tau\rbrack)\rightarrow(\tau_2\lbrack\alpha\leftarrow\tau\rbrack) \\
\alpha \lbrack\alpha\leftarrow\tau\rbrack &=& \tau \\
\alpha' \lbrack\alpha\leftarrow\tau\rbrack &=& \alpha'\quad
\textsf{(if } \alpha\not=\alpha'\textsf{)} \\
(\forall\alpha.\tau') \lbrack\alpha\leftarrow\tau\rbrack &=& \forall\alpha.\tau' \\
(\forall\alpha'.\tau') \lbrack\alpha\leftarrow\tau\rbrack &=&
\forall\alpha'.(\tau'\lbrack\alpha\leftarrow\tau\rbrack) \quad
\textsf{(if } \alpha\not=\alpha'\textsf{)} \\
\end{array}
\]

대부분의 경우는 이해하기 쉽다. 주목해야 할 부분은 마지막에 있는 전칭 타입에 대한 치환이다. 어떤 타입 식별자의 묶는 등장이 있는지 확인하는 것을 볼 수 있다. 치환 대상과 묶는 등장을 한 타입 식별자가 같으면 전칭 타입의 몸통에서는 치환을 하면 안 된다.

이제 식에 대한 치환을 정의할 수 있다. \(e[\alpha\leftarrow\tau]\)은 식 \(e\)에서 \(\alpha\)를 \(\tau\)로 치환하여 얻은 식을 나타낸다. 이 경우, 치환은 타입, 타입 식별자, 식에서 식으로 가는 함수이다. 타입에 대한 치환과 마찬가지로 \(e\)에서 자유 식별자 \(\alpha\)만 \(\tau\)로 대체해야 한다.

다음은 식에 대한 치환을 정의한다.

\[
\begin{array}{rcl}
n \lbrack\alpha\leftarrow\tau\rbrack &=& n \\
(e_1+e_2) \lbrack\alpha\leftarrow\tau\rbrack &=&
(e_1\lbrack\alpha\leftarrow\tau\rbrack) + (e_2\lbrack\alpha\leftarrow\tau\rbrack) \\
(e_1-e_2) \lbrack\alpha\leftarrow\tau\rbrack &=&
(e_1\lbrack\alpha\leftarrow\tau\rbrack) - (e_2\lbrack\alpha\leftarrow\tau\rbrack) \\
x \lbrack\alpha\leftarrow\tau\rbrack &=& x \\
(\lambda x:\tau'.e) \lbrack\alpha\leftarrow\tau\rbrack &=&
\lambda x:(\tau'\lbrack\alpha\leftarrow\tau\rbrack).(e\lbrack\alpha\leftarrow\tau\rbrack) \\
(e_1\ e_2) \lbrack\alpha\leftarrow\tau\rbrack &=&
(e_1\lbrack\alpha\leftarrow\tau\rbrack)\ (e_2\lbrack\alpha\leftarrow\tau\rbrack) \\
(\Lambda\alpha.e)\lbrack\alpha\leftarrow\tau\rbrack &=& \Lambda\alpha.e \\
(\Lambda\alpha'.e)\lbrack\alpha\leftarrow\tau\rbrack &=&
\Lambda\alpha'.(e\lbrack\alpha\leftarrow\tau\rbrack)\quad
\textsf{(if } \alpha\not=\alpha'\textsf{)} \\
(e\ \lbrack\tau'\rbrack)\lbrack\alpha\leftarrow\tau\rbrack &=&
(e\lbrack\alpha\leftarrow\tau\rbrack)\ \lbrack \tau'\lbrack\alpha\leftarrow\tau\rbrack\rbrack \\
\end{array}
\]

역시 대부분의 경우는 이해하기 쉽다. 타입 함수에 대한 치환만 조심하면 된다. 전칭 타입처럼 타입 함수도 타입 식별자를 묶으므로 어떤 타입 식별자의 묶는 등장이 있는지 확인해야 한다.

이제 TpolyFAE의 동적 의미를 정의하겠다. 추가된 식인 타입 함수와 타입 적용에 대한 추론 규칙만 보겠다. 나머지 규칙은 TFAE의 추론 규칙과 같다.

\[
\sigma\vdash \Lambda\alpha.e\Rightarrow \langle \Lambda\alpha.e,\sigma\rangle
\]

타입 함수를 계산하면 타입 함수 값이 나온다. 어떤 계산도 필요하지 않다. 타입 함수와 주어진 환경을 사용하여 그대로 타입 함수 값을 만들면 된다.

\[
\frac
{ \sigma\vdash e\Rightarrow \langle\Lambda\alpha.e',\sigma'\rangle \quad
  \sigma'\vdash e'\lbrack\alpha\leftarrow\tau\rbrack \Rightarrow v }
{ \sigma\vdash e\ \lbrack\tau\rbrack\Rightarrow v }
\]

타입 적용을 계산하려면 먼저 타입 함수 위치의 식을 계산해야 한다. 그 계산 결과는 반드시 타입 함수 값이어야 한다. 타입 함수 값의 몸통에 있는 타입 식별자를 주어진 타입으로 치환한 뒤 타입 함수 값의 환경 아래서 계산한 결과가 최종 결과이다.

다음은 \((\Lambda\alpha.\lambda x:\alpha.x)\ \lbrack\textsf{num}\rbrack\ 1\)의 계산 결과가 \(1\)임을 증명하는 증명 나무이다.

\[
\frac
{
  {\Large\frac
  {
    \emptyset\vdash\Lambda\alpha.\lambda x:\alpha.x
    \Rightarrow\langle\Lambda\alpha.\lambda x:\alpha.x,\emptyset\rangle \quad
    \emptyset\vdash\lambda x:\textsf{num}.x
    \Rightarrow\langle\lambda x.x,\emptyset\rangle
  }
  { \emptyset\vdash
    (\Lambda\alpha.\lambda x:\alpha.x)\ \lbrack\textsf{num}\rbrack
    \Rightarrow\langle\lambda x.x,\emptyset\rangle
  }} \quad
  \emptyset\vdash 1\Rightarrow 1 \quad
  {\Large\frac
  { x\in\mathit{Domain}(\lbrack x\mapsto1\rbrack) }
  { \lbrack x\mapsto1\rbrack\vdash x\Rightarrow 1 }}
}
{ \emptyset\vdash
(\Lambda\alpha.\lambda x:\alpha.x)\ \lbrack\textsf{num}\rbrack\ 1
\Rightarrow 1 }
\]

## 타입 체계

TVFAE에서와 마찬가지로 타입 환경의 정의에 수정이 필요하다. TpolyFAE의 타입 환경은 변수의 타입을 저장하는 것에 더해 사용할 수 있는 타입 식별자를 저장해야 한다. TVFAE와 달리 타입 식별자에 관련된 정보가 없으므로 묶는 등장을 한 타입 식별자를 저장하기만 하면 된다. 다음은 타입 환경의 정의이다.

\[
\begin{array}{rrcl}
\text{Type Environment} & \Gamma & \in &
\mathit{Id}\cup\mathit{TId}\xrightarrow{\text{fin}}
\text{Type}\cup\{\cdot\} \\
\end{array}
\]

타입 환경의 공역에 아무런 의미 없는 원소인 \(\cdot\)을 추가하였다. 편의상 \(\Gamma\lbrack\alpha\rbrack\)라 쓰면 \(\Gamma\lbrack\alpha:\cdot\rbrack\)을 나타낸 것이라고 정의하겠다.

### 올바른 형태의 타입

TVFAE에서처럼 올바른 형태의 타입을 정의해야 한다. 타입 환경에 타입 식별자가 들어 있으므로 어떤 타입이 올바른 형태인지는 주어진 타입 환경 아래서 결정된다.

\[\Gamma\vdash\textsf{num}\]

\[
\frac
{ \Gamma\vdash\tau_1 \quad
  \Gamma\vdash\tau_2 }
{ \Gamma\vdash\tau_1\rightarrow\tau_2 }
\]

이 두 규칙은 TVFAE의 규칙과 같다.

\[
\frac
{ \alpha\in\mathit{Domain}(\Gamma) }
{ \Gamma\vdash\alpha }
\]

타입 식별자 \(\alpha\)가 타입 환경에 저장되어 있으면 \(\alpha\)는 올바른 형태이다.

\[
\frac
{ \Gamma\lbrack\alpha\rbrack\vdash\tau }
{ \Gamma\vdash\forall\alpha.\tau }
\]

타입 \(\forall\alpha.\tau\)의 경우, \(\tau\) 안에서 \(\alpha\)를 사용할 수 있다. 따라서 \(\tau\)가 올바른 형태인지 확인할 때 타입 환경에 \(\alpha\)가 들어 있어야 한다. 그러므로 \(\Gamma\) 아래서 \(\forall\alpha.\tau\)가 올바른 형태인 것은 \(\Gamma\lbrack\alpha\rbrack\) 아래서 \(\tau\)가 올바른 형태인 것이다. 예를 들어, 빈 타입 환경 아래서 \(\forall\alpha.\alpha\)는 올바른 형태이지만 \(\forall\alpha.\beta\)는 잘못된 형태이다.

### 타입 규칙

TFAE의 타입 규칙과 비교하여 달라진 타입 규칙을 먼저 보겠다.

\[
\frac
{ \Gamma\vdash\tau \quad \Gamma\lbrack x:\tau\rbrack\vdash e:\tau' }
{ \Gamma\vdash \lambda x:\tau.e:\tau\rightarrow\tau' }
\]

TVFAE의 타입 규칙과 마찬가지로 람다 요약의 타입 규칙은 매개변수 타입이 올바른 형태인지 확인해야 한다.

다음은 TFAE의 함수 적용에 대한 타입 규칙이다.

\[
\frac
{ \Gamma\vdash e_1:\tau'\rightarrow\tau \quad
  \Gamma\vdash e_2:\tau' }
{ \Gamma\vdash e_1\ e_2:\tau }
\]

이 규칙을 TpolyFAE에서 그대로 사용해도 타입 안전성을 해치지 않지만 언어의 표현력을 제한한다. 다음의 식을 생각해 보자.

\[(\lambda x:(\forall\alpha.\alpha\rightarrow\alpha).x)\ (\Lambda\beta.\lambda x:\beta.x)\]

인자로 사용된 \((\Lambda\beta.\lambda x:\beta.x)\)의 타입은 \(\forall\beta.\beta\rightarrow\beta\)이다. 함수 \(\lambda x:(\forall\alpha.\alpha\rightarrow\alpha).x\)는 인자로 \(\forall\alpha.\alpha\rightarrow\alpha\) 타입의 값을 받는다. 위 규칙을 그대로 사용하면 이 식은 타입 체계가 거절한다. 그러나 \(\forall\alpha.\alpha\rightarrow\alpha\)와 \(\forall\beta.\beta\rightarrow\beta\)는 타입 식별자가 일관적으로 바뀐, 실제로는 같은 타입이다. 두 타입을 동치인 타입으로 정의하고 함수 적용의 타입 규칙이 매개변수와 인자의 타입이 동치인 경우를 허용하게 하여 언어의 표현력을 높일 수 있다.

다음은 타입의 동치를 정의한다.

\[
\tau\equiv\tau
\]

\[
\frac
{ \tau\equiv\tau'\lbrack\alpha'\leftarrow\alpha\rbrack }
{ \forall\alpha.\tau\equiv\forall\alpha'.\tau' }
\]

같은 타입은 동치인 타입이다. 두 전칭 타입은 묶는 타입 식별자를 같게 만들었을 때 동치인 타입이면 동치이다.

\[
\frac
{ \Gamma\vdash e_1:\tau'\rightarrow\tau \quad
  \Gamma\vdash e_2:\tau'' \quad
  \tau'\equiv\tau'' }
{ \Gamma\vdash e_1\ e_2:\tau }
\]

수정된 함수 적용의 타입 규칙은 매개변수와 인자의 타입이 동치인 경우를 허용한다.

이제 TpolyFAE에 추가된 식에 대한 타입 규칙을 보겠다.

\[
\frac
{ \alpha\not\in\mathit{Domain}(\Gamma) \quad 
  \Gamma\lbrack\alpha\rbrack\vdash e:\tau }
{ \Gamma\vdash \Lambda \alpha.e:\forall\alpha.\tau }
\]

\(e\)의 타입이 \(\tau\)일 때 타입 함수 \(\lambda \alpha.e\)의 타입은 \(\forall\alpha.\tau\)이다. \(e\)의 타입을 계산할 때 \(\alpha\)가 올바른 형태여야 하므로 \(e\)의 타입은 타입 환경에 \(\alpha\)를 추가한 상태에서 계산된다. 주목할 점은 타입 매개변수 \(\alpha\)가 타입 환경에 존재하지 않아야 한다는 전제가 있다는 것이다. 이 전제가 없으면 타입 안전성이 무너진다. 식 \(\Lambda\alpha.\lambda x:\alpha.\Lambda\alpha.x\)를 고려하면 타입 안전성에 문제가 생기는 이유를 찾을 수 있을 것이다.

\[
\frac
{ \Gamma\vdash\tau \quad \Gamma\vdash e:\forall\alpha.\tau' }
{ \Gamma\vdash e\ \lbrack\tau\rbrack:\tau'\lbrack\alpha\leftarrow\tau\rbrack }
\]

\(e\)의 타입이 \(\forall\alpha.\tau'\)일 때 타입 적용 \(e\ \lbrack\tau\rbrack\)의 타입은 \(\tau'\)에서 \(\alpha\)를 \(\tau\)로 치환한 것이다. \(\tau\)는 프로그래머가 적은 타입이므로 올바른 형태인지 검사해야 한다.

다음은 \((\Lambda\alpha.\lambda x:\alpha.x)\ \lbrack\textsf{num}\rbrack\ 1\)의 타입이 \(\textsf{num}\)임을 증명하는 증명 나무이다.

\[
\frac
{
  \frac
  {\huge
    \frac
    { 
      \frac
      {
        \frac
        { \alpha\in\mathit{Domain}(\lbrack\alpha\rbrack) }
        { \lbrack\alpha\rbrack\vdash\alpha } \quad
        \frac
        { x\in\mathit{Domain}(\lbrack\alpha,x:\alpha\rbrack) }
        { \lbrack\alpha,x:\alpha\rbrack\vdash x:\alpha }
      }
      { \lbrack\alpha\rbrack\vdash\lambda x:\alpha.x:\alpha\rightarrow\alpha }
    }
    { \emptyset\vdash\Lambda\alpha.\lambda x:\alpha.x:
      \forall\alpha.\alpha\rightarrow\alpha } \quad
    {\Large\emptyset\vdash\textsf{num}}
  }
  { \Large\emptyset\vdash(\Lambda\alpha.\lambda x:\alpha.x)\ \lbrack\textsf{num}\rbrack:
    \textsf{num}\rightarrow\textsf{num}
  } \quad
  \emptyset\vdash 1:\textsf{num} \quad
  \textsf{num}\equiv\textsf{num}
}
{ \emptyset\vdash
(\Lambda\alpha.\lambda x:\alpha.x)\ \lbrack\textsf{num}\rbrack\ 1:
\textsf{num} }
\]

## 타입 검사기 구현

다음은 TpolyFAE의 요약 문법을 Scala로 구현한 것이다.

```scala
sealed trait Expr
case class Num(n: Int) extends Expr
case class Add(l: Expr, r: Expr) extends Expr
case class Sub(l: Expr, r: Expr) extends Expr
case class Id(x: String) extends Expr
case class Fun(x: String, t: Type, b: Expr) extends Expr
case class App(f: Expr, a: Expr) extends Expr
case class TFun(a: String, b: Expr) extends Expr
case class TApp(f: Expr, t: Type) extends Expr

sealed trait Type
case object NumT extends Type
case class ArrowT(p: Type, r: Type) extends Type
case class ForallT(a: String, t: Type) extends Type
case class IdT(a: String) extends Type
```

`Expr` 인스턴스는 TpolyFAE 식을 표현한다. `TFun` 인스턴스는 타입 함수, `TApp`인스턴스는 타입 적용을 나타낸다. `ForallT` 인스턴스는 전칭 타입, `IdT` 인스턴스는 타입으로서의 타입 식별자를 나타낸다. 타입 식별자는 임의의 문자열이다.

```scala
case class TEnv(
  vars: Map[String, Type] = Map(),
  tbinds: Set[String] = Set()
) {
  def +(p: (String, Type)): TEnv =
    copy(vars = vars + p)
  def +(x: String): TEnv =
    copy(tbinds = tbinds + x)
  def contains(x: String): Boolean = tbinds(x)
}
```

`TEnv`는 타입 환경의 타입이다. 필드 `vars`는 변수의 타입을 저장한다. 필드 `tbinds`는 현재 묶여 있는 타입 식별자의 집합으로, 그 타입은 문자열의 집합인 `Set[String]`이다. TVFAE의 `TEnv`와 비슷하게 타입 환경을 사용하기 쉽도록 `+` 메서드와 `contains` 메서드를 정의했다. 다음처럼 변수의 타입이나 타입 식별자를 타입 환경에 추가할 수 있다.

```scala
env + ("x" -> NumT)
env + "alpha"
```

다음처럼 타입 식별자가 타입 환경에 묶여 있는지 확인할 수 있다.

```scala
env.contains("alpha")
```

다음의 `subst` 함수는 타입에 대한 치환을 정의한다. 치환의 정의에서 구현이 바로 나온다.

```scala
def subst(t1: Type, a: String, t2: Type): Type = t1 match {
  case NumT => t1
  case ArrowT(p, r) => ArrowT(subst(p, a, t2), subst(r, a, t2))
  case IdT(a1) => if (a == a1) t2 else t1
  case ForallT(a1, t) => if (a == a1) t1 else ForallT(a1, subst(t, a, t2))
}
```

`mustSame` 함수가 타입의 동치를 고려하도록 수정해야 한다. 전칭 타입의 경우에 동치를 고려하는 것을 제외하면 나머지는 자명하게 두 타입이 같은지 비교한다.

```scala
def mustSame(t1: Type, t2: Type): Type = (t1, t2) match {
  case (NumT, NumT) => t1
  case (ArrowT(p1, r1), ArrowT(p2, r2)) =>
    ArrowT(mustSame(p1, p2), mustSame(r1, r2))
  case (IdT(a1), IdT(a2)) if a1 == a2 => t1
  case (ForallT(a1, t1), ForallT(a2, t2)) =>
    ForallT(a1, mustSame(t1, subst(t2, a2, IdT(a1))))
  case _ => throw new Exception
}
```

`validType` 함수는 주어진 타입이 주어진 환경 아래서 올바른 형태인지 확인한다.

```scala
def validType(t: Type, env: TEnv): Type = t match {
  case NumT => t
  case ArrowT(p, r) =>
    ArrowT(validType(p, env), validType(r, env))
  case IdT(t) =>
    if (env.contains(t)) IdT(t)
    else throw new Exception
  case ForallT(a, t) => ForallT(a, validType(t, env + a))
}
```

이제 `typeCheck` 함수에 추가되어야 하는 코드를 보겠다.

```scala
case TFun(a, b) =>
  if (env.contains(a)) throw new Exception
  ForallT(a, typeCheck(b, env + a))
```

\[
\frac
{ \alpha\not\in\mathit{Domain}(\Gamma) \quad 
  \Gamma\lbrack\alpha\rbrack\vdash e:\tau }
{ \Gamma\vdash \Lambda \alpha.e:\forall\alpha.\tau }
\]

타입 함수의 타입 매개변수는 타입 환경에 들어 있지 않은 타입 식별자여야 한다. 타입 환경에 타입 식별자를 추가한 뒤 몸통의 타입을 계산한다. 타입 매개변수와 몸통의 타입으로 전칭 타입을 만든다.

```scala
case TApp(f, t) =>
  validType(t, env)
  val ForallT(a, t1) = typeCheck(f, env)
  subst(t1, a, t)
```

\[
\frac
{ \Gamma\vdash\tau \quad \Gamma\vdash e:\forall\alpha.\tau' }
{ \Gamma\vdash e\ \lbrack\tau\rbrack:\tau'\lbrack\alpha\leftarrow\tau\rbrack }
\]

타입 인자는 올바른 형태여야 한다. 타입 함수 위치의 식의 타입은 전칭 타입이어야 한다. 전칭 타입의 몸통에서 타입 식별자를 타입 인자로 치환하여 얻은 타입이 타입 적용의 타입이다.

```scala
case Id(x) => env.vars(x)
case Fun(x, t, b) =>
  validType(t, env)
  ArrowT(t, typeCheck(b, env + (x -> t)))
```

`Id` 경우와 `Fun` 경우의 수정은 TVFAE 타입 검사기에서 수정한 것과 동일하다.

다음은 `typeCheck` 함수의 전체 코드이다.

```scala
def typeCheck(e: Expr, env: TEnv): Type = e match {
  case Num(n) => NumT
  case Add(l, r) =>
    mustSame(mustSame(typeCheck(l, env), NumT), typeCheck(r, env))
  case Sub(l, r) =>
    mustSame(mustSame(typeCheck(l, env), NumT), typeCheck(r, env))
  case Id(x) => env.vars(x)
  case Fun(x, t, b) =>
    validType(t, env)
    ArrowT(t, typeCheck(b, env + (x -> t)))
  case App(f, a) =>
    val ArrowT(t1, t2) = typeCheck(f, env)
    val t3 = typeCheck(a, env)
    mustSame(t1, t3)
    t2
  case TFun(a, b) =>
    if (env.contains(a)) throw new Exception
    ForallT(a, typeCheck(b, env + a))
  case TApp(f, t) =>
    validType(t, env)
    val ForallT(a, t1) = typeCheck(f, env)
    subst(t1, a, t)
}
```

다음은 타입 검사기를 통해 \((\Lambda\alpha.\lambda x:\alpha.x)\ \lbrack\textsf{num}\rbrack\ 1\)의 타입을 계산한 것이다.

```scala
// (Lambda alpha.lambda x:alpha.x) [num] 1
typeCheck(
  App(
    TApp(
      TFun("alpha",
        Fun("x", IdT("alpha"),
          Id("x")
        )
      ),
      NumT
    ),
    Num(1)
  ),
  TEnv()
)
// num
```

## 인터프리터 구현

이제 TpolyFAE의 인터프리터를 보겠다.

```scala
sealed trait Value
case class NumV(n: Int) extends Value
case class CloV(p: String, b: Expr, e: Env) extends Value
case class TFunV(a: String, b: Expr, e: Env) extends Value
```

`TFunV` 인스턴스는 타입 함수 값을 나타낸다.

```scala
case TFun(a, b) => TFunV(a, b, env)
```

\[
\sigma\vdash \Lambda\alpha.e\Rightarrow \langle \Lambda\alpha.e,\sigma\rangle
\]

타입 함수를 계산하면 타입 함수 값이 나온다. 타입 함수 값은 계산 시점의 환경을 저장한다.

```scala
case TApp(f, t) =>
  val TFunV(a, b, fEnv) = interp(f, env)
  interp(subst(b, a, t), fEnv)
```

\[
\frac
{ \sigma\vdash e\Rightarrow \langle\Lambda\alpha.e',\sigma'\rangle \quad
  \sigma'\vdash e'\lbrack\alpha\leftarrow\tau\rbrack \Rightarrow v }
{ \sigma\vdash e\ \lbrack\tau\rbrack\Rightarrow v }
\]

먼저 타입 함수 위치의 식을 계산한다. 계산 결과는 타입 함수 값이어야 한다. 타입 함수의 몸통에서 타입 매개변수를 타입 인자로 치환한다. 치환하여 얻은 식을 타입 함수 값의 환경 아래서 계산한 결과가 최종 결과이다.

다음은 인터프리터 전체 코드이다.

```scala
def subst(e: Expr, a: String, t: Type): Expr = e match {
  case Num(n) => Num(n)
  case Add(l, r) => Add(subst(l, a, t), subst(r, a, t))
  case Sub(l, r) => Sub(subst(l, a, t), subst(r, a, t))
  case Id(x) => Id(x)
  case Fun(x, t0, b) => Fun(x, subst(t0, a, t), subst(b, a, t))
  case App(f, arg) => App(subst(f, a, t), subst(arg, a, t))
  case TFun(a0, b) => if (a0 == a) TFun(a0, b) else TFun(a0, subst(b, a, t))
  case TApp(f, t0) => TApp(subst(f, a, t), subst(t0, a, t))
}

def interp(e: Expr, env: Env): Value = e match {
  case Num(n) => NumV(n)
  case Add(l, r) =>
    val NumV(n) = interp(l, env)
    val NumV(m) = interp(r, env)
    NumV(n + m)
  case Sub(l, r) =>
    val NumV(n) = interp(l, env)
    val NumV(m) = interp(r, env)
    NumV(n - m)
  case Id(x) => env(x)
  case Fun(x, t, b) => CloV(x, b, env)
  case App(f, a) =>
    val CloV(x, b, fEnv) = interp(f, env)
    interp(b, fEnv + (x -> interp(a, env)))
  case TFun(a, b) => TFunV(a, b, env)
  case TApp(f, t) =>
    val TFunV(a, b, fEnv) = interp(f, env)
    interp(subst(b, a, t), fEnv)
}

def run(e: Expr): Value = {
  typeCheck(e, TEnv())
  interp(e, Map.empty)
}
```

다음은 인터프리터를 통해 \((\Lambda\alpha.\lambda x:\alpha.x)\ \lbrack\textsf{num}\rbrack\ 1\)의 값을 계산한 것이다.

```scala
// (Lambda alpha.lambda x:alpha.x) [num] 1
run(
  App(
    TApp(
      TFun("alpha",
        Fun("x", IdT("alpha"),
          Id("x")
        )
      ),
      NumT
    ),
    Num(1)
  )
)
// 1
```

## 감사의 말

글을 확인하고 의견을 주신 류석영 교수님께 감사드립니다. 글에서 잘못된 점을 찾아
주신 pi님께 감사드립니다.
