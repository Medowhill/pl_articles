이번 글에서는 TFAE에 대수적 데이터 타입을 추가하여 TVFAE를 정의한다. TVFAE의 문법, 동적 의미, 타입 체계를 정의하며 타입 검사기와 인터프리터를 구현할 것이다.

## 대수적 데이터 타입

대수적 데이터 타입의 필요성과 유용함에 대해서는 이미 ‘패턴 대조’ 글에서 자세히 보았다. 그러므로 대수적 데이터 타입을 설명하기보다는 TFAE에서 대수적 데이터 타입을 사용할 수 있을지 알아보겠다.

과일은 사과 또는 바나나라고 하자. 사과는 구로 근사하여 반지름으로 크기를 나타낸다. 반지름은 정수이다. 바나나는 원기둥으로 근사하여 높이와 반지름으로 크기를 나타낸다. 높이와 반지름 모두 정수이다. 이를 Scala 코드로 쓰면 다음과 같다.

```scala
sealed trait Fruit
case class Apple(r: Int) extends Fruit
case class Banana(h: Int, r: Int) extends Fruit
```

이는 Scala에서 대수적 데이터 타입을 사용하는 전형적인 예시이다.

TFAE에서는 순서쌍을 사용하여 대수적 데이터 타입을 표현할 수 있다. TFAE에는 원래 순서쌍이 없지만 순서쌍을 추가하는 방법을 이미 다루었으므로 TFAE에 순서쌍이 있다고 가정하겠다. 지역 변수 선언, 불 값, 조건식 역시 사용할 수 있다고 가정하겠다. 이 역시 추가하는 방법을 이미 다루었다.

과일 타입을 \(\textsf{bool}\times(\textsf{num}\times(\textsf{num}\times\textsf{num}))\)으로 나타내자. 어떤 과일 값은 하나의 순서쌍이다. 순서쌍의 첫 번째 값은 이 값이 사과인지 바나나인지 나타낸다. 첫 번째 값이 \(\textsf{true}\)이면 이 과일은 사과이고 \(\textsf{false}\)이면 바나나이다. 순서쌍의 두 번째 값은 다시 순서쌍이며 과일의 크기를 나타낸다. 과일이 사과이면 크기를 나타내는 순서쌍의 첫 번째 값만 의미가 있고 바나나이면 두 번째 값만 의미가 있다. 다음 두 식 중 첫 번째 식은 반지름이 \(5\)인 사과, 두 번째 식은 높이가 \(6\), 반지름이 \(2\)인 바나나를 나타낸다.

\[(\textsf{true},(5,(0,0)))\]

\[(\textsf{false},(0,(6,2)))\]

과일 값을 만들기 위해 위 순서쌍을 직접 만드는 것은 번거롭고 실수할 가능성이 크다. Scala에서는 아래와 같이 과일 값을 만들 수 있다.

```scala
Apple(5)
Banana(6, 2)
```

비슷하게 코드를 작성할 수 있도록 \(Apple\)과 \(Banana\) 함수를 만들어 보자. \(Apple\)은 정수를 인자로 받는다. 결과는 순서쌍으로 그 첫 값은 \(true\)이다. 순서쌍의 두 번째 값은 다시 순서쌍이며 첫 값은 인자로 받은 정수, 둘째 값은 임의의 두 정수의 순서쌍이다. 따라서 다음 식처럼 쓸 수 있다.

\[\lambda x:\textsf{num}.(\textsf{true},(x,(0,0)))\]

\(Banana\) 함수도 비슷하게 만들 수 있다.

\[\lambda x:\textsf{num}\times\textsf{num}.(\textsf{false},(0,x))\]

\(Apple\)과 \(Banana\) 함수가 올바르게 정의되어 있으면 아래처럼 과일을 만들 수 있다.

\[Apple\ 5\]

\[Banana\ (6,2)\]

이제 과일의 반지름을 구하는 함수 \(radius\)를 만들어 보자. Scala에서는 패턴 대조를 사용하여 다음처럼 구현할 수 있다.

```scala
def radius(f: Fruit): Int = f match {
  case Apple(r) => r
  case Banana(_, r) => r
}
```

TFAE에서는 과일을 나타내는 순서쌍의 첫 값을 확인하여 주어진 과일이 사과인지 바나나인지 알 수 있다. 어느 과일인지 안다면 그에 맞춰 반지름을 구할 수 있다. 다음 식은 \(radius\) 함수이다.

\[\lambda f:\textsf{bool}\times(\textsf{num}\times(\textsf{num}\times\textsf{num})).
\textsf{if}\ f.1\ f.2.1\ f.2.2.2\]

지금까지 작성한 코드를 모두 모아서 아래처럼 쓸 수 있다. Scala 코드를 먼저 보고 같은 의미의 TFAE 식을 보겠다.

```scala
sealed trait Fruit
case class Apple(r: Int) extends Fruit
case class Banana(h: Int, r: Int) extends Fruit

def radius(f: Fruit): Int = f match {
  case Apple(r) => r
  case Banana(_, r) => r
}

radius(Apple(5)) + radius(Banana(6, 2))  // 7
```

\[
\begin{array}{l}
\textsf{val}\ Apple=\lambda x:\textsf{num}.(\textsf{true},(x,(0,0)))\ \textsf{in} \\
\textsf{val}\ Banana=\lambda x:\textsf{num}\times\textsf{num}.(\textsf{false},(0,x))\ \textsf{in} \\
\textsf{val}\ radius=\lambda f:\textsf{bool}\times(\textsf{num}\times(\textsf{num}\times\textsf{num})).
\textsf{if}\ f.1\ f.2.1\ f.2.2.2\ \textsf{in} \\
(radius\ (Apple\ 5))+(radius\ (Banana\ (6,2)))
\end{array}
\]

따라서 TFAE에 대수적 데이터 타입을 정의하는 기능을 추가하지 않아도 순서쌍, 불 값, 조건식, 지역 변수 선언 같은 기본적인 기능만으로 대수적 데이터 타입을 흉내 낼 수 있다.

그러나 사과 값을 만들 때도 바나나의 크기에 해당하는 순서쌍을 만들어야 하는 불편함이 있다. 바나나 값을 만들 때도 마찬가지로 사과의 크기에 해당하는 정숫값 자리를 채워야 한다. 이는 코드에 불필요한 복잡함과 계산을 추가한다. 대수적 데이터 타입을 정의하는 기능을 제공하는 Scala로 작성한 코드는 더 이해하기 쉽다.

대수적 데이터 타입을 여러 개 정의해서 사용하면 또 다른 문제가 생긴다. 위 코드에서 과일의 타입은 \(\textsf{bool}\times(\textsf{num}\times(\textsf{num}\times\textsf{num}))\)이다. 복잡한 프로그램이라면 대수적 데이터 타입이 여럿 사용되는 것이 일반적이다. 그러다 보면 동일한 타입을 다른 종류의 값을 표현하기 위해 사용하게 될 가능성이 있다. 예를 들면 전자 제품의 타입도 \(\textsf{bool}\times(\textsf{num}\times(\textsf{num}\times\textsf{num}))\)이 되어 과일의 타입과 같아질 수 있다. 이 경우 과일을 인자로 받아야 하는 \(radius\) 함수가 전자 제품을 인자로 받아도 타입 체계가 프로그램을 수락한다. 물론 프로그램을 실행해도 타입 오류가 일어나지는 않을 것이다. 그러나 프로그램이 프로그래머가 의도하지 않은 동작을 수행할 가능성이 매우 크다. Scala로 같은 프로그램을 작성한다면 `Fruit`과 `Electronics` 타입이 다른 타입이므로 타입 검사가 실패할 것이다. 이 역시 Scala가 대수적 데이터 타입을 정의하는 기능을 제공하기 때문에 가능한 것이다.

Scala를 포함한 대수적 데이터 타입을 제공하는 여러 언어는 대수적 데이터 타입을 재귀적으로 정의하는 것을 허용한다. 다음은 Scala로 정수의 리스트를 구현한 것이다.

```scala
sealed trait List
case class Nil(u: Unit) extends List
case class Cons(h: Int, t: List) extends List
```

`Cons` 클래스가 `List` 타입을 정의함과 동시에 필드의 타입으로 `List`를 사용한다. 이는 `List` 타입이 재귀적인 대수적 데이터 타입이라는 것을 보여준다.

TFAE에서 과일을 표현한 것과 같은 방법으로 리스트를 표현할 수 있을까? 표현을 위해서는 리스트의 타입을 알아야 한다. 과일의 경우, 사과는 정숫값을 가지고 바나나는 두 정숫값의 순서쌍을 가지기 때문에 \(\textsf{bool}\times(\textsf{num}\times(\textsf{num}\times\textsf{num}))\)이 타입이었다. 리스트의 타입을 찾아보자. 리스트의 타입을 \(\tau\)라 하겠다. `Nil`은 `Unit` 값을 가지고 `Cons`는 정수와 리스트의 순서쌍을 가진다. 따라서 리스트의 타입은 \(\textsf{bool}\times(\textsf{unit}\times(\textsf{num}\times\tau))\)이다. 그러므로 \(\tau=\textsf{bool}\times(\textsf{unit}\times(\textsf{num}\times\tau))\)를 만족하는 \(\tau\)를 찾아야 한다. 그러나 \(\textsf{bool}\times(\textsf{unit}\times(\textsf{num}\times\tau))\)가 이미 \(\tau\)를 포함하므로 \(\tau\)와 같아질 수 없다. 이 조건을 만족하는 \(\tau\)는 존재하지 않는 것이다. 리스트의 타입을 TFAE에서 표현할 수 없다는 결론을 얻을 수 있다. 이 결론은 리스트뿐 아니라 모든 재귀적 대수적 데이터 타입에 적용된다. 리스트 같은 재귀적 대수적 데이터 타입을 사용하기 위해서는 언어에 재귀적 타입을 정의하는 기능을 추가해야만 한다.

## 문법

다음은 TVFAE의 요약 문법이다. TFAE와 비교하여 추가된 부분만 작성하였다.

\[
\begin{array}{rrcl}
\text{Type Identifier} & t & \in & \mathit{TId} \\
\text{Expression} & e & ::= & \cdots \\
&&|& \textsf{type}\ t=x(\tau)\ |\ x(\tau)\ \textsf{in}\ e \\
&&|& e\ \textsf{match}\ x(x)\rightarrow e\ |\ x(x)\rightarrow e \\
\text{Value} & v & ::= & \cdots \\
&&|& x(v) \\
&&|& \langle x\rangle \\
\text{Type} & \tau & ::= & \cdots \\
&&|& t \\
\end{array}
\]

\(t\)는 타입 식별자를 나타내는 메타변수이다. 프로그래머가 정의한 타입의 이름으로 사용된다.

식 \(\textsf{type}\ t=x_1(\tau_1)\ |\ x_2(\tau_2)\ \textsf{in}\ e\)는 새로운 대수적 데이터 타입을 정의한다. \(t\)는 정의한 타입의 이름이다. 하나의 대수적 데이터 타입이 가지는 *형태*(variant)는 반드시 두 개이다. 이는 언어를 단순하게 만들기 위함이다. 대부분의 실제 사용되는 언어는 형태를 몇 개든 자유롭게 만들 수 있다. 형태의 개수를 늘려 언어를 정의하는 것은 쉬운 일이므로 TVFAE는 형태를 두 개만 허용하도록 하겠다. \(x_1\)과 \(x_2\)는 각 형태의 이름이다. 두 이름은 반드시 달라야 한다. \(\tau_1\)은 첫 형태가 갖는 값의 타입이고 \(\tau_2\)는 둘째 형태가 갖는 값의 타입이다. Scala 등의 언어에서는 각 형태의 값이 여러 개의 값을 가질 수 있게 한다. 위에서 본 `Banana` 값이 그 예시이다. 그러나 여러 개의 값은 순서쌍이나 튜플로 값을 묶어 하나의 값으로 표현할 수 있다. 따라서 언어를 단순하게 만들기 위해 형태가 갖는 값 역시 언제나 하나인 것으로 제한한다. 정의한 타입 \(t\)와 형태 \(x_1\), \(x_2\)는 \(e\) 안에서만 사용할 수 있다. 과일과 리스트는 다음처럼 표현된다.

\[\textsf{type}\ Fruit=Apple(\textsf{num})\ |\ Banana(\textsf{num}\times\textsf{num})\ \textsf{in}\ \cdots\]

\[\textsf{type}\ List=Nil(\textsf{unit})\ |\ Cons(\textsf{num}\times List)\ \textsf{in}\ \cdots\]

값 \(x(v)\)는 \(x\)가 이름인 형태의 값이다. \(v\)는 \(x(v)\)가 가지고 있는 값이다. 따라서 반지름이 \(5\)인 사과 값은 \(Apple(5)\)이다. 또, 높이가 \(6\), 반지름이 \(2\)인 바나나 값은 \(Banana((6,2))\)이다.

타입으로서의 \(t\)는 이름이 \(t\)인 대수적 데이터 타입의 한 형태의 값의 타입이다. 예를 들면, 앞에서처럼 \(Fruit\) 타입과 \(Apple\), \(Banana\) 형태를 정의했을 때 \(Apple(5)\)와 \(Banana((6,2))\)의 타입은 모두 \(Fruit\)이다.

값 \(\langle x\rangle\)는 \(x\)가 이름인 형태의 값의 생성자이다. 생성자는 함수처럼 사용할 수 있다. \(e_1\)을 계산한 값이 \(\langle x\rangle\)이고 \(e_2\)를 계산한 값이 \(v\)이면 \(e_1\ e_2\)의 계산 결과는 \(x(v)\)이다. 만약 변수 \(Apple\)의 값이 \(\langle Apple\rangle\)이라면 \(Apple\ 5\)의 계산 결과는 \(Apple(5)\)이다. 프로그래머는 \(Apple\ 5\) 같은 식으로 각 형태의 값을 만들기를 원한다. 따라서 타입 정의의 몸통을 계산할 때, 형태의 이름을 이름으로 하는 변수의 값이 그 형태의 생성자라는 정보가 환경에 들어 있어야 한다. 예를 들면, 환경에 \(Apple\)이라는 변수의 값이 \(\langle Apple\rangle\)이라는 정보가 있어야 한다는 뜻이다. 생성자의 타입은 형태의 값이 가지는 값의 타입에서 형태가 속한 대수적 데이터 타입으로 가는 함수 타입이다. 예를 들면, 앞의 타입 정의를 그대로 사용한다고 가정할 때 \(\langle Apple\rangle\)의 타입은 \(\textsf{num}\rightarrow Fruit\)이다. 이는 \(\langle Apple\rangle\)이 함수처럼 사용될 수 있으며 정숫값 \(n\)을 인자로 받아 \(Fruit\) 타입의 값인 \(Apple(n)\)을 만들어낸다는 점에서 나온다.

식 \(e\ \textsf{match}\ x_1(x_3)\rightarrow e_1\ |\ x_2(x_4)\rightarrow e_2\)는 패턴 대조를 진행한다. \(e\)를 계산하여 얻은 결과 \(v\)가 패턴 대조 대상이다. \(v\)가 \(x_1\)을 이름으로 하는 형태의 값, 즉 \(x_1(v_1)\)이면 \(e_1\)을 계산한다. \(e_1\)을 계산할 때 변수 \(x_3\)의 값은 \(v_1\)이다. 반대로, \(v\)가 \(x_2\)를 이름으로 하는 형태의 값인 \(x_2(v_2)\)이면 \(e_2\)를 계산한다. \(e_2\)를 계산할 때 변수 \(x_4\)의 값은 \(v_2\)이다. \(e_1\) 또는 \(e_2\)를 계산하여 얻은 결과가 전체 식의 결과이다. 위에서 정의한 함수 \(radius\)는 다음처럼 쓸 수 있다.

\[\lambda f:Fruit.f\ \textsf{match}\ Apple(x)\rightarrow x\ |\ Banana(x)\rightarrow x.2\]

형태의 이름을 쓰는 순서는 바뀔 수 있다. 다음 역시 올바른 \(radius\) 함수이다.

\[\lambda f:Fruit.f\ \textsf{match}\ Banana(x)\rightarrow x.2\ |\ Apple(x)\rightarrow x\]

## 동적 의미

TFAE의 동적 의미와 비교하여 추가되어야 하는 규칙들만 보겠다.

\[
\frac
{ \sigma[x_1\mapsto \langle x_1\rangle,x_2\mapsto \langle x_2\rangle]\vdash e\Rightarrow v }
{ \sigma\vdash \textsf{type}\ t=x_1(\tau_1)\ |\ x_2(\tau_2)\ \textsf{in}\ e\Rightarrow v }
\]

타입을 정의하는 식인 \(\textsf{type}\ t=x_1(\tau_1)\ |\ x_2(\tau_2)\ \textsf{in}\ e\)를 계산한 결과는 그 몸통인 \(e\)를 계산한 결과와 같다. 단, \(e\)를 계산할 때 각 형태의 생성자를 사용할 수 있어야 한다. 따라서 환경에 \(x_1\)의 값이 \(\langle x_1\rangle\)이고 \(x_2\)의 값이 \(\langle x_2\rangle\)라는 정보를 추가한 채로 \(e\)를 계산해야 한다.

\[
\frac
{ \sigma\vdash e_1\Rightarrow \langle x\rangle \quad
  \sigma\vdash e_2\Rightarrow v }
{ \sigma\vdash e_1\ e_2\Rightarrow x(v) }
\]

TFAE에서는 함수 적용에서 함수 위치에 올 수 있는 값이 클로저뿐이었다. TVFAE에서는 생성자 역시 함수 적용의 함수 위치에 올 수 있다. 이 경우를 위한 규칙을 추가해야 한다. 앞서 설명한 것처럼 \(e_1\)의 계산 결과가 \(\langle x\rangle\)이고 \(e_2\)의 계산 결과가 \(v\)이면 \(e_1\ e_2\)의 계산 결과는 \(x(v)\)이다. \(x\)라는 이름의 형태의 생성자가 그 형태의 값을 만든 것이다.

\[
\frac
{ \sigma\vdash e\Rightarrow x_1(v') \quad
  \sigma[x_3\mapsto v']\vdash e_1\Rightarrow v }
{ \sigma\vdash e\ \textsf{match}\ x_1(x_3)\rightarrow e_1\ |\ x_2(x_4)\rightarrow e_2\Rightarrow v }
\]

패턴 대조 시에는 대조 대상을 먼저 찾아야 한다. \(e\ \textsf{match}\ x_1(x_3)\rightarrow e_1\ |\ x_2(x_4)\rightarrow e_2\)를 계산하려면 \(e\)를 가장 먼저 계산해야 하는 것이다. \(e\)의 계산 결과가 \(x_1(v')\)이면 \(e_1\)을 계산하면 된다. 이때, \(x_3\)의 값이 \(v'\)이어야 하므로 \(e_1\)을 계산할 때의 환경에 그 정보를 추가한다. \(e_1\)의 계산 결과가 전체 결과이다.

\[
\frac
{ \sigma\vdash e\Rightarrow x_2(v') \quad
  \sigma[x_4\mapsto v']\vdash e_2\Rightarrow v }
{ \sigma\vdash e\ \textsf{match}\ x_1(x_3)\rightarrow e_1\ |\ x_2(x_4)\rightarrow e_2\Rightarrow v }
\]

한편 \(e\)의 계산 결과가 \(x_2(v')\)이면 \(e_2\)를 계산해야 한다. \(e_2\)를 계산할 때 환경에 \(x_4\)의 값이 \(v'\)이라는 정보가 들어 있다.

다음 증명 나무는 \(\textsf{type}\ Fruit=Apple(\textsf{num})\ |\ Banana(\textsf{num}\times\textsf{num})\ \textsf{in}\ (Apple\ 5)\ \textsf{match}\ Apple(x)\rightarrow x\ |\ Banana(x)\rightarrow x.2\)를 계산하면 \(5\)가 나옴을 보인다.

\[
\begin{array}{rcl}
\sigma_1&=&\lbrack Apple\mapsto\langle Apple\rangle,Banana\mapsto\langle Banana\rangle\rbrack \\
\sigma_2&=&\sigma_1\lbrack x\mapsto 5\rbrack
\end{array}
\]

\[
\frac
{ \Large
  \frac
  { \huge
    \frac
    {
      \frac
      { Apple\in\mathit{Domain}(\sigma_1) }
      { \sigma_1\vdash Apple\Rightarrow\langle Apple\rangle } \quad
      \sigma_1\vdash 5\Rightarrow 5
    }
    { \sigma_1\vdash(Apple\ 5)\Rightarrow Apple(5) } \quad
    \frac
    { x\in\mathit{Domain}(\sigma_2) }
    { \sigma_2\vdash x\Rightarrow 5 }
  }
  { \sigma_1\vdash(Apple\ 5)\ \textsf{match}\ Apple(x)\rightarrow x\ |\ Banana(x)\rightarrow x.2 
    \Rightarrow 5 }
}
{ \emptyset\vdash
  \textsf{type}\ Fruit=Apple(\textsf{num})\ |\ Banana(\textsf{num}\times\textsf{num})
  \ \textsf{in}\ (Apple\ 5)\ \textsf{match}\ Apple(x)\rightarrow x\ |\ Banana(x)\rightarrow x.2
  \Rightarrow 5 }
\]

## 타입 체계

TVFAE의 타입 체계를 정의하기 위해서는 타입 환경의 정의를 수정해야 한다. TFAE의 타입 환경은 변수의 타입을 저장한다. 따라서 타입 환경이 식별자에서 타입으로 가는 부분 함수였다. TVFAE의 타입 환경은 변수의 타입을 저장하는 것 말고도 할 일이 더 있다. 타입을 정의하는 식을 통해 정의된 대수적 데이터 타입의 정보를 저장해야 한다. 저장한 정보는 타입 검사 과정에서 사용된다. 어떻게 사용되는지는 뒤에서 볼 것이다. 식 \(\textsf{type}\ t=x_1(\tau_1)\ |\ x_2(\tau_2)\ \textsf{in}\ e\)를 타입 검사할 때 타입 환경이 \(\Gamma\)라고 하자. \(\Gamma\)에 \(t\)의 정보를 추가한 것은 \(\Gamma\lbrack t=\{(x_1,\tau_1),(x_2,\tau_2)\}\rbrack\)이다. \(x_1\)과 \(x_2\) 사이의 순서는 중요하지 않기 때문에 집합으로 표현하였다. 이제 타입 환경의 정의역에 \(t\)가 속해야 하며 공역에는 \(\{(x_1,\tau_1),(x_2,\tau_2)\}\)가 속해야 한다. 이에 맞추어 다음처럼 타입 환경을 다시 정의하겠다. \(\mathcal{P}(A)\)는 집합 \(A\)의 *멱집합*(power set)을 의미한다.

\[
\begin{array}{rrcl}
\text{Type Environment} & \Gamma & \in &
\mathit{Id}\cup\mathit{TId}\xrightarrow{\text{fin}}
\text{Type}\cup\mathcal{P}(\mathit{Id}\times\text{Type}) \\
\end{array}
\]

### 올바른 형태의 타입

TVFAE의 타입은 임의의 타입 식별자일 수 있다. 예를 들면 타입 식별자 \(Fruit\)은 타입이다. 이는 \(Fruit\)이라는 타입을 정의했는지 여부와 상관이 없다. 그러므로 \(\lambda x:Fruit.x\) 같은 식을 \(Fruit\) 타입을 정의한 적 없이 작성하고 사용할 수 있다. \(Fruit\) 타입을 정의한 적이 없는데 \(Fruit\rightarrow Fruit\) 타입의 함수가 존재하는 것은 이상한 일이다. 이렇게 정의되지 않은 타입을 사용한 식은 단순히 이상한 것을 넘어 타입 체계의 안전성을 해칠 수도 있다.

식에 어떤 타입이 등장했을 때 그 타입이 *올바른 형태의*(well-formed) 타입인지 확인할 필요가 있다. *잘못된 형태의*(ill-formed) 타입을 사용한 식은 타입 체계가 거절하는 것이 안전하다. 올바른 형태의 타입은 \(\textsf{num}\) 같이 정의하지 않고도 사용할 수 있는 타입과 정의하고 사용하는 타입을 포함한다. \(Fruit\) 타입을 정의하였다면 \(Fruit\)은 올바른 형태이다. 또한, 올바른 형태의 타입으로 이루어진 타입도 올바른 형태이다. 예를 들면 \(\textsf{num}\rightarrow\textsf{num}\)은 언제나 올바른 형태이다. 그 이외의 모든 타입은 잘못된 형태이다. \(Fruit\)을 정의하지 않았다면 \(Fruit\)은 잘못된 형태이다. 또, \(Fruit\)이 잘못된 형태면 \(Fruit\rightarrow Fruit\) 역시 잘못된 형태이다.

올바른 형태의 타입을 엄밀히 정의하도록 하겠다. 올바른 형태의 판단은 타입 환경과 타입 사이의 관계이다.

\[\vdash\in\text{Type Environment}\times\text{Type}\]

올바른 형태인지 확인하는 데 타입 환경이 필요한 이유는 타입 환경에 정의된 타입의 목록이 들어 있기 때문이다. \(\Gamma\vdash \tau\)는 타입 환경 \(\Gamma\) 아래에서 타입 \(\tau\)가 올바른 형태의 타입임을 의미한다.

\[\Gamma\vdash\textsf{num}\]

\(\textsf{num}\)은 언제나 올바른 형태이다.
\[
\frac
{ \Gamma\vdash\tau_1 \quad
  \Gamma\vdash\tau_2 }
{ \Gamma\vdash\tau_1\rightarrow\tau_2 }
\]

\(\tau_1\)과 \(\tau_2\)가 모두 올바른 형태면 \(\tau_1\rightarrow\tau_2\) 역시 올바른 형태이다.

\[
\frac
{ t\in\mathit{Domain}(\Gamma) }
{ \Gamma\vdash t }
\]

\(t\)가 정의되어 있다면, 즉 \(t\)가 \(\Gamma\)의 정의역의 원소라면 \(t\)는 올바른 형태이다.

\(\Gamma\vdash \tau\)를 증명할 수 없다면 \(\Gamma\) 아래에서 \(\tau\)는 잘못된 형태이다.

### 타입 규칙

이제 TVFAE에 추가된 식의 *타입 규칙*(typing rule)을 정의하겠다. 타입 규칙은 타입 체계를 구성하는 추론 규칙들 중에서 식의 타입을 정의하는 규칙만을 부르는 말이다.

\[
\frac
{ \begin{array}{c}x_1\not=x_2 \quad
  t\not\in\mathit{Domain}(\Gamma) \\
  \Gamma'=\Gamma[t=\{(x_1,\tau_1),(x_2,\tau_2)\},x_1:\tau_1\rightarrow t,x_2:\tau_2\rightarrow t] \\
  \Gamma'\vdash e:\tau \quad
  \Gamma'\vdash \tau_1 \quad
  \Gamma'\vdash \tau_2 \quad
  \Gamma\vdash \tau\end{array} }
{ \Gamma\vdash \textsf{type}\ t=x_1(\tau_1)\ |\ x_2(\tau_2)\ \textsf{in}\ e:\tau }
\]

위 규칙은 식 \(\textsf{type}\ t=x_1(\tau_1)\ |\ x_2(\tau_2)\ \textsf{in}\ e\)의 타입을 정의한다. 두 형태의 이름 \(x_1\)과 \(x_2\)는 달라야 한다. 정의하는 타입 \(t\)는 이미 정의된 타입과 이름이 같을 수 없다. 이 전제가 빠진다면 타입 체계가 안전하지 않게 된다. \(e\)의 타입을 계산하기에 앞서 타입 환경에 \(t\)의 정의를 추가해야 한다. 또, \(e\)에서 생성자를 사용할 수 있어야 하므로 \(x_1\)의 타입이 \(\tau_1\rightarrow t\)이고 \(x_2\)의 타입이 \(\tau_2\rightarrow t\)라는 정보도 추가한다. 이렇게 만들어진 타입 환경이 \(\Gamma'\)이다. \(\Gamma'\) 아래에서 \(e\)의 타입은 \(\tau\)이다. 또한, \(\tau_1\)과 \(\tau_2\)가 올바른 형태인지 확인해야 한다. 이 전제가 빠져도 타입 안전성이 무너진다. 재귀적 타입 정의를 허용하기 위해서 \(\tau_1\)과 \(\tau_2\)가 올바른 형태인지 확인할 때 \(\Gamma'\)을 사용한다. 만약 \(\Gamma\)를 사용하면 재귀적이지 않은 타입만 정의할 수 있게 된다. 마지막으로 \(\tau\)가 \(\Gamma\) 아래에서 올바른 형태인지 확인한다. 이 전제가 없으면 역시 타입 안전성이 지켜지지 않는다.

전제가 많아서 규칙이 복잡해 보이지만 타입 안전성을 위한 조건들을 제외하고 실제로 타입 검사를 수행하는 부분만 남기면 간단해진다.

\[
\frac
{
  \Gamma'=\Gamma[t=\{(x_1,\tau_1),(x_2,\tau_2)\},x_1:\tau_1\rightarrow t,x_2:\tau_2\rightarrow t] \quad
  \Gamma'\vdash e:\tau }
{ \Gamma\vdash \textsf{type}\ t=x_1(\tau_1)\ |\ x_2(\tau_2)\ \textsf{in}\ e:\tau }
\]

가장 중요한 부분은 타입 환경에 정의된 타입의 정보와 생성자를 추가하는 것과 몸통의 타입을 검사하는 것이다. 그 이외의 전제는 모두 타입 안전성을 지키기 위한 조건이다. 그 전제 중 하나라도 빠지면 어떻게 타입 안전성에 문제가 생기는지는 뒤에서 다시 보겠다.

\[
\frac
{ \begin{array}{c}\Gamma\vdash e:t \quad
  t\in\mathit{Domain}(\Gamma) \quad
  \Gamma(t)=\{(x_1,\tau_1),(x_2,\tau_2)\} \\
  \Gamma[x_3:\tau_1]\vdash e_1:\tau \quad
  \Gamma[x_4:\tau_2]\vdash e_2:\tau\end{array} }
{ \Gamma\vdash e\ \textsf{match}\ x_1(x_3)\rightarrow e_1\ |\ x_2(x_4)\rightarrow e_2:\tau }
\]

위 규칙은 식 \(e\ \textsf{match}\ x_1(x_3)\rightarrow e_1\ |\ x_2(x_4)\rightarrow e_2\)의 타입을 정의한다. 먼저 패턴 대조 대상인 \(e\)의 타입을 검사한다. 그 타입은 타입 식별자 \(t\)여야 하며 \(t\)는 정의된 타입이어야 한다. 전제 \(t\in\mathit{Domain}(\Gamma)\)는 \(\Gamma\vdash t\)라고 써도 된다. \(t\)가 정의된 타입이라면 타입 환경에 \(t\)의 정보가 들어 있다. 타입 환경에서 얻은 두 형태의 이름은 패턴 대조 식에서 사용한 형태의 이름과 같아야 한다. 단, 타입이 정의될 때의 순서대로 패턴 대조 식에서 형태를 사용할 필요는 없다. 타입 정보가 집합으로 표현되므로 순서는 타입 환경에 나타나 있지 않기 때문이다. 마지막으로 \(e_1\)과 \(e_2\)의 타입을 각각 계산해야 한다. \(e_1\)의 타입을 계산할 때는 \(x_3\)의 타입이 \(\tau_1\)이라는 정보가 타입 환경에 추가되고 \(e_2\)의 타입을 계산할 때는 \(x_4\)의 타입이 \(\tau_2\)라는 정보가 추가된다. \(e_1\)과 \(e_2\)의 타입은 같아야하며 그 타입이 전체 식의 타입이다.

한편 기존에 TFAE에 존재하던 타입 규칙 중 수정이 필요한 규칙이 하나 있다. 바로 람다 요약에 대한 규칙이다.

\[
\frac
{ \Gamma\vdash\tau \quad \Gamma\lbrack x:\tau\rbrack\vdash \tau' }
{ \Gamma\vdash \lambda x:\tau.e:\tau\rightarrow\tau' }
\]

매개변수 타입 표시가 올바른 형태인지 확인해야 한다. 이 전제가 없다면 타입 안전성이 유지되지 않는다. 이 점에 대해서도 뒤에서 볼 것이다.

타입이 올바른 형태인지 언제 검사해야 하는지 헷갈릴 수 있다. 수업에서 다루는 언어에서는, 프로그래머가 표시한 타입만 검사하면 되며 모든 프로그래머의 타입 표시를 검사해야 한다고 이해해도 문제가 없다. 그러나 모든 언어에서 성립하는 원칙은 아니다. 타입 체계를 설계한 방법에 따라 프로그래머의 타입 표시임에도 올바른 형태인지 확인할 필요가 없을 수 있다. 또, 프로그래머가 표시한 타입이 아님에도 올바른 형태인지 확인할 필요가 있을 수 있다. 타입이 올바른 형태인지 확인하는 이유는 타입 안전성을 지키기 위함이다. 따라서 타입 안전성을 증명하는 과정에서 필요한 만큼 규칙에 타입의 올바른 형태 검사를 넣어야 한다. 타입 안전성의 증명은 이 글의 수준을 벗어난다. 그러므로 모든 프로그래머의 타입 표시가 올바른 형태인지 검사하면 된다고 이해해도 무방하다. 그러나 무조건적으로 올바른 형태 검사가 필요하다고 외우기보다는 왜 올바른 형태 검사가 필요한지 이해하려는 시도가 중요하다. 이 글의 마지막 부분에서 올바른 형태 검사를 하지 않으면 어떻게 타입 안전성이 깨지는지 볼 것이다. 그 부분을 잘 이해할 수 있다면 TVFAE를 완전히 이해한 것이다.

다음 증명 나무는 \(\textsf{type}\ Fruit=Apple(\textsf{num})\ |\ Banana(\textsf{num}\times\textsf{num})\ \textsf{in}\ (Apple\ 5)\ \textsf{match}\ Apple(x)\rightarrow x\ |\ Banana(x)\rightarrow x.2\)의 타입이 \(\textsf{num}\)임을 보인다.

\[
\begin{array}{rcl}
\Gamma_1&=&\lbrack 
Fruit=\{(Apple,\textsf{num}),(Banana,\textsf{num}\times\textsf{num})\},
Apple:\textsf{num}\rightarrow Fruit,
Banana:(\textsf{num}\times\textsf{num})\rightarrow Fruit\rbrack \\
\Gamma_2&=&\Gamma_1\lbrack x:\textsf{num}\rbrack \\
\Gamma_3&=&\Gamma_1\lbrack x:\textsf{num}\times\textsf{num}\rbrack
\end{array}
\]

\[
\frac
{ \begin{array}{c}
  Apple\not=Banana \quad
  Fruit\not\in\mathit{Domain}(\emptyset) \quad
  \Gamma_1=\Gamma_1 \\
  \Gamma_1\vdash \textsf{num} \quad
  \frac
  { \Gamma_1\vdash \textsf{num} \quad \Gamma_1\vdash \textsf{num}}
  { \Gamma_1\vdash \textsf{num}\times\textsf{num} } \quad
  \emptyset\vdash \textsf{num}
  \end{array} \quad
  {\large\frac
  {
    \begin{array}{c}
    {\Large\frac
    { 
      \frac
      { Apple\in\mathit{Domain}(\Gamma_1) }
      { \Gamma_1\vdash Apple:\textsf{num}\rightarrow Fruit } \quad
      \Gamma_1\vdash 5:\textsf{num}
    }
    { \Gamma_1\vdash (Apple\ 5):Fruit } \quad
    \frac
    { x\in\mathit{Domain}(\Gamma_2) }
    { \Gamma_2\vdash x:\textsf{num} } \quad
    \frac
    { 
      \frac
      { x\in\mathit{Domain}(\Gamma_3) }
      { \Gamma_3\vdash x:\textsf{num}\times\textsf{num} }
    }
    { \Gamma_3\vdash x.2:\textsf{num} } }\\
    Fruit\in\mathit{Domain}(\Gamma_1) \quad
    \Gamma_1(Fruit)=\{(Apple,\textsf{num}),(Banana,\textsf{num}\times\textsf{num})\}
    \end{array}
  }
  { \Gamma_1\vdash (Apple\ 5)\ \textsf{match}\ Apple(x)\rightarrow x\ |\ Banana(x)\rightarrow x.2
    :\textsf{num} }}
}
{ \emptyset\vdash
  \textsf{type}\ Fruit=Apple(\textsf{num})\ |\ Banana(\textsf{num}\times\textsf{num})
  \ \textsf{in}\ (Apple\ 5)\ \textsf{match}\ Apple(x)\rightarrow x\ |\ Banana(x)\rightarrow x.2
  :\textsf{num} }
\]

## 타입 검사기 구현

다음은 TVFAE의 요약 문법을 Scala로 구현한 것이다.

```scala
sealed trait TVFAE
case class Num(n: Int) extends TVFAE
case class Add(l: TVFAE, r: TVFAE) extends TVFAE
case class Sub(l: TVFAE, r: TVFAE) extends TVFAE
case class Id(x: String) extends TVFAE
case class Fun(x: String, t: TVFAET, b: TVFAE) extends TVFAE
case class App(f: TVFAE, a: TVFAE) extends TVFAE
case class WithType(
  t: String, v1: String, vt1: TVFAET,
  v2: String, vt2: TVFAET, b: TVFAE
) extends TVFAE
case class Cases(
  e: TVFAE, v1: String, x1: String, e1: TVFAE,
  v2: String, x2: String, e2: TVFAE
) extends TVFAE

sealed trait TVFAET
case object NumT extends TVFAET
case class ArrowT(p: TVFAET, r: TVFAET) extends TVFAET
case class IdT(t: String) extends TVFAET

def mustSame(t1: TVFAET, t2: TVFAET): TVFAET =
  if (t1 == t2) t1 else throw new Exception
```

`TVFAE` 인스턴스는 TVFAE 식을 표현한다. `WithType` 인스턴스는 타입을 정의하는 식을 나타내고 `Cases` 인스턴스는 패턴 대조 식을 나타낸다. `IdT` 인스턴스는 타입으로서의 타입 식별자를 나타낸다. 타입 식별자는 임의의 문자열이다.

```scala
case class TEnv(
  vars: Map[String, TVFAET] = Map(),
  tbinds: Map[String, Map[String, TVFAET]] = Map()
) {
  def +(x: String, t: TVFAET): TEnv =
    TEnv(vars + (x -> t), tbinds)
  def +(x: String, m: Map[String, TVFAET]): TEnv =
    TEnv(vars, tbinds + (x -> m))
  def contains(x: String): Boolean =
    tbinds.contains(x)
}
```

`TEnv`는 타입 환경의 타입이다. TFAE의 타입 환경은 열쇠가 문자열이고 값이 TFAE 타입인 사전으로 단순하게 표현할 수 있었다. 그러나 TVFAE의 타입 환경은 변수의 타입뿐 아니라 정의된 타입의 정보도 가지고 있어야 한다. 새로운 `TEnv`의 정의는 경우 클래스를 사용한다. `TEnv` 클래스는 `vars`와 `tbinds` 필드를 가지고 있다. 필드 `vars`는 변수의 타입을 저장하기 때문에 열쇠가 문자열이고 값이 TVFAE 타입인 사전이다. 필드 `tbinds`는 정의된 타입의 정보를 저장한다. `tbinds` 역시 사전이며 열쇠는 문자열, 즉 타입 식별자이다. 값은 문자열에서 타입으로 가는 사전이다. 열쇠로 사용되는 문자열은 형태의 이름이고 값으로 사용되는 타입은 해당 형태가 가지고 있는 값의 타입이다. 예를 들면, 앞에서 본 \(Fruit\) 타입 정보를 저장하고 있는 `tbinds` 필드는 다음과 같다.

```scala
Map("Fruit" -> Map("Apple" -> NumT, "Banana" -> PairT(NumT, NumT)))
```

타입 환경에 변수의 타입과 정의된 타입을 쉽게 추가할 수 있도록 `TEnv` 클래스에 `+` 메서드 두 개를 정의했다. 타입 환경을 나타내는 `env`에 \(x\)의 타입이 \(\textsf{num}\)이라는 정보를 추가하는 것은 다음처럼 쓸 수 있다.

```scala
env + ("x", NumT)
```

또, `env`에 \(Fruit\) 타입을 추가하는 것은 다음처럼 쓸 수 있다.

```scala
env + ("Fruit", Map("Apple" -> NumT, "Banana" -> PairT(NumT, NumT)))
```

`TEnv` 클래스의 `contains` 메서드는 어떤 타입 식별자가 정의된 타입의 이름인지 확인한다. 예를 들면, 다음 코드는 \(Fruit\)이 정의된 타입인지 확인한다.

```scala
env.contains("Fruit")
```

`typeCheck` 함수를 정의하기 전에 타입이 올바른 형태인지 확인하는 `validType` 함수를 정의하겠다. `validType` 함수는 타입 환경과 타입을 인자로 받는다. 만약 그 타입이 주어진 타입 환경 아래에서 올바른 형태면 그 타입을 그대로 결과로 내고 잘못된 형태면 예외를 발생시킨다.

```scala
def validType(t: TVFAET, env: TEnv): TVFAET = t match {
  case NumT => t
  case ArrowT(p, r) =>
    ArrowT(validType(p, env), validType(r, env))
  case IdT(t) =>
    if (env.contains(t)) IdT(t)
    else throw new Exception
}
```

이제 `typeCheck` 함수에 추가되어야 하는 코드를 보겠다.

```scala
 case WithType(t, v1, vt1, v2, vt2, b) =>
  if (env.contains(t)) throw new Exception
  if (v1 == v2) throw new Exception
  val nenv = env +
    (t, Map(v1 -> vt1, v2 -> vt2)) +
    (v1, ArrowT(vt1, IdT(t))) +
    (v2, ArrowT(vt2, IdT(t)))
  validType(vt1, nenv)
  validType(vt2, nenv)
  validType(typeCheck(b, nenv), env)
```

\[
\frac
{ \begin{array}{c}x_1\not=x_2 \quad
  t\not\in\mathit{Domain}(\Gamma) \\
  \Gamma'=\Gamma[t=\{(x_1,\tau_1),(x_2,\tau_2)\},x_1:\tau_1\rightarrow t,x_2:\tau_2\rightarrow t] \\
  \Gamma'\vdash e:\tau \quad
  \Gamma'\vdash \tau_1 \quad
  \Gamma'\vdash \tau_2 \quad
  \Gamma\vdash \tau\end{array} }
{ \Gamma\vdash \textsf{type}\ t=x_1(\tau_1)\ |\ x_2(\tau_2)\ \textsf{in}\ e:\tau }
\]

먼저, 정의되는 타입의 이름이 이미 타입 환경에 들어 있는지 확인한다. 들어 있다면 식을 거절한다. 두 형태의 이름이 같은 경우에도 식을 거절한다. 주어진 타입 환경에 타입과 생성자를 추가한다. 두 형태가 가지는 값의 타입은 확장된 타입 환경 아래에서 올바른 형태여야 한다. 마지막으로 몸통의 타입을 계산하고 그 타입이 처음의 타입 환경 아래에서 올바른 형태인지 확인한다. 올바른 형태이면 그 타입이 식 전체의 타입이 된다.

```scala
case Cases(e, v1, x1, e1, v2, x2, e2) =>
  if (v1 == v2) throw new Exception
  val IdT(t) = typeCheck(e, env)
  val tdef = env.tbinds(t)
  mustSame(
    typeCheck(e1, env + (x1, tdef(v1))),
    typeCheck(e2, env + (x2, tdef(v2)))
  )
```

\[
\frac
{ \begin{array}{c}\Gamma\vdash e:t \quad
  t\in\mathit{Domain}(\Gamma) \quad
  \Gamma(t)=\{(x_1,\tau_1),(x_2,\tau_2)\} \\
  \Gamma[x_3:\tau_1]\vdash e_1:\tau \quad
  \Gamma[x_4:\tau_2]\vdash e_2:\tau\end{array} }
{ \Gamma\vdash e\ \textsf{match}\ x_1(x_3)\rightarrow e_1\ |\ x_2(x_4)\rightarrow e_2:\tau }
\]

두 형태의 이름은 달라야 한다. 먼저 패턴 대조 대상인 식의 타입을 계산한다. 그 타입은 타입 식별자여야 한다. 타입의 정보를 타입 환경에서 찾는다. 이로써 각 형태가 가지는 값의 타입이 무엇인지 알 수 있다. `e1`과 `e2`의 타입을 각각 확장된 타입 환경 아래에서 계산한다. 두 타입은 같아야 하며, 같다면 그 타입이 전체 식의 타입이다.

```scala
case Fun(x, t, b) =>
  validType(t, env)
  ArrowT(t, typeCheck(b, env + (x, t)))
```

\[
\frac
{ \Gamma\vdash\tau \quad \Gamma\lbrack x:\tau\rbrack\vdash \tau' }
{ \Gamma\vdash \lambda x:\tau.e:\tau\rightarrow\tau' }
\]

`Fun` 경우는 새롭게 추가된 것은 아니지만 수정이 필요하다. 함수 매개변수 타입이 올바른 형태인지 확인해야 한다.

```scala
case Id(x) => env.vars(x)
```

`Id` 경우에는 타입 환경의 정의가 바뀐 것에 맞게 변수의 타입을 찾는 방법을 바꿔 줘야 한다.

다음은 `typeCheck` 함수의 전체 코드이다.

```scala
def typeCheck(e: TVFAE, env: TEnv): TVFAET = e match {
  case Num(n) => NumT
  case Add(l, r) =>
    mustSame(mustSame(typeCheck(l, env), NumT), typeCheck(r, env))
  case Sub(l, r) =>
    mustSame(mustSame(typeCheck(l, env), NumT), typeCheck(r, env))
  case Id(x) => env.vars(x)
  case Fun(x, t, b) =>
    validType(t, env)
    ArrowT(t, typeCheck(b, env + (x, t)))
  case App(f, a) =>
    val ArrowT(t1, t2) = typeCheck(f, env)
    val t3 = typeCheck(a, env)
    mustSame(t1, t3)
    t2
  case WithType(t, v1, vt1, v2, vt2, b) =>
    if (env.contains(t)) throw new Exception
    if (v1 == v2) throw new Exception
    val nenv = env +
      (t, Map(v1 -> vt1, v2 -> vt2)) +
      (v1, ArrowT(vt1, IdT(t))) +
      (v2, ArrowT(vt2, IdT(t)))
    validType(vt1, nenv)
    validType(vt2, nenv)
    validType(typeCheck(b, nenv), env)
  case Cases(e, v1, x1, e1, v2, x2, e2) =>
    if (v1 == v2) throw new Exception
    val IdT(t) = typeCheck(e, env)
    val tdef = env.tbinds(t)
    mustSame(
      typeCheck(e1, env + (x1, tdef(v1))),
      typeCheck(e2, env + (x2, tdef(v2)))
    )
}
```

다음은 타입 검사기를 통해 \(\textsf{type}\ Fruit=Apple(\textsf{num})\ |\ Banana(\textsf{num})\ \textsf{in}\ (Apple\ 5)\ \textsf{match}\ Apple(x)\rightarrow x\ |\ Banana(x)\rightarrow x\)의 타입을 계산한 것이다.

```scala
// type Fruit = Apple(num) | Banana(num) in
//   (Apple 5) match
//     Apple(x) -> x |
//     Banana(x) -> x
typeCheck(
  WithType(
    "Fruit", "Apple", NumT, "Banana", NumT,
    Cases(
      App(Id("Apple"), Num(5)),
      "Apple", "x", Id("x"),
      "Banana", "x", Id("x")
    )
  ), TEnv()
)
// num
```

## 인터프리터 구현

이제 TVFAE의 인터프리터를 보겠다.

```scala
sealed trait TVFAEV
case class NumV(n: Int) extends TVFAEV
case class CloV(p: String, b: TVFAE, e: Env) extends TVFAEV
case class VarV(x: String, v: TVFAEV) extends TVFAEV
case class ConstV(x: String) extends TVFAEV

type Env = Map[String, TVFAEV]
```

`VarV` 인스턴스는 어떤 형태의 값을 나타낸다. `ConstV` 인스턴스는 어떤 형태의 생성자를 나타낸다.

```scala
case WithType(_, v1, _, v2, _, b) =>
  interp(b, env + (v1 -> ConstV(v1)) + (v2 -> ConstV(v2)))
```

\[
\frac
{ \sigma[x_1\mapsto \langle x_1\rangle,x_2\mapsto \langle x_2\rangle]\vdash e\Rightarrow v }
{ \sigma\vdash \textsf{type}\ t=x_1(\tau_1)\ |\ x_2(\tau_2)\ \textsf{in}\ e\Rightarrow v }
\]

타입을 정의하는 식을 계산하려면 환경에 생성자를 추가하고 몸통을 계산하면 된다.

```scala
case Cases(e, v1, x1, e1, v2, x2, e2) =>
  interp(e, env) match {
    case VarV(`v1`, v) => interp(e1, env + (x1 -> v))
    case VarV(`v2`, v) => interp(e2, env + (x2 -> v))
  }
```

\[
\frac
{ \sigma\vdash e\Rightarrow x_1(v') \quad
  \sigma[x_3\mapsto v']\vdash e_1\Rightarrow v }
{ \sigma\vdash e\ \textsf{match}\ x_1(x_3)\rightarrow e_1\ |\ x_2(x_4)\rightarrow e_2\Rightarrow v }
\]

\[
\frac
{ \sigma\vdash e\Rightarrow x_2(v') \quad
  \sigma[x_4\mapsto v']\vdash e_2\Rightarrow v }
{ \sigma\vdash e\ \textsf{match}\ x_1(x_3)\rightarrow e_1\ |\ x_2(x_4)\rightarrow e_2\Rightarrow v }
\]

패턴 대조식을 계산하려면 먼저 대조 대상 식을 계산해야 한다. 그 값이 어떤 형태의 값이고 주어진 첫 번째 형태 이름인 `v1`과 일치하면 `e1`을 계산한다. 이때 환경에 `x1`의 값을 추가해야 한다. 대조 대상 식의 값이 두 번째 형태 이름인 `v2`와 일치하면 `e2`를 계산한다. 이때는 환경에 `x2`의 값을 추가해야 한다.

```scala
case App(f, a) => interp(f, env) match {
  case CloV(x, b, fEnv) =>
    interp(b, fEnv + (x -> interp(a, env)))
  case ConstV(x) => VarV(x, interp(a, env))
}
```

\[
\frac
{ \sigma\vdash e_1\Rightarrow \langle x\rangle \quad
  \sigma\vdash e_2\Rightarrow v }
{ \sigma\vdash e_1\ e_2\Rightarrow x(v) }
\]

함수 적용의 함수 위치에 생성자가 오는 경우를 처리해야 한다. 함수 위치의 식을 계산하여 생성자를 얻었다면 인자를 계산한 뒤 생성자의 형태의 값을 만들면 된다.

다음은 인터프리터 전체 코드이다.

```scala
def interp(e: TVFAE, env: Env): TVFAEV = e match {
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
  case App(f, a) => interp(f, env) match {
    case CloV(x, b, fEnv) =>
      interp(b, fEnv + (x -> interp(a, env)))
    case ConstV(x) => VarV(x, interp(a, env))
  }
  case WithType(_, v1, _, v2, _, b) =>
    interp(b, env + (v1 -> ConstV(v1)) + (v2 -> ConstV(v2)))
  case Cases(e, v1, x1, e1, v2, x2, e2) =>
    interp(e, env) match {
      case VarV(`v1`, v) => interp(e1, env + (x1 -> v))
      case VarV(`v2`, v) => interp(e2, env + (x2 -> v))
    }
}

def run(e: TVFAE): TVFAEV = {
  typeCheck(e, TEnv())
  interp(e, Map.empty)
}
```

다음은 인터프리터를 통해 \(\textsf{type}\ Fruit=Apple(\textsf{num})\ |\ Banana(\textsf{num})\ \textsf{in}\ (Apple\ 5)\ \textsf{match}\ Apple(x)\rightarrow x\ |\ Banana(x)\rightarrow x\)의 값을 계산한 것이다.

```scala
// type Fruit = Apple(num) | Banana(num) in
//   (Apple 5) match
//     Apple(x) -> x |
//     Banana(x) -> x
run(
  WithType(
    "Fruit", "Apple", NumT, "Banana", NumT,
    Cases(
      App(Id("Apple"), Num(5)),
      "Apple", "x", Id("x"),
      "Banana", "x", Id("x")
    )
  )
)
// 5
```

## 사라진 타입 안전성
다음은 현재 TVFAE 타입 체계의 타입 정의 식과 람다 요약의 타입 규칙이다.

\[
\frac
{ \begin{array}{c}x_1\not=x_2 \quad
  t\not\in\mathit{Domain}(\Gamma) \\
  \Gamma'=\Gamma[t=\{(x_1,\tau_1),(x_2,\tau_2)\},x_1:\tau_1\rightarrow t,x_2:\tau_2\rightarrow t] \\
  \Gamma'\vdash e:\tau \quad
  \Gamma'\vdash \tau_1 \quad
  \Gamma'\vdash \tau_2 \quad
  \Gamma\vdash \tau\end{array} }
{ \Gamma\vdash \textsf{type}\ t=x_1(\tau_1)\ |\ x_2(\tau_2)\ \textsf{in}\ e:\tau }
\]

\[
\frac
{ \Gamma\vdash\tau \quad \Gamma\lbrack x:\tau\rbrack\vdash \tau' }
{ \Gamma\vdash \lambda x:\tau.e:\tau\rightarrow\tau' }
\]

첫 규칙의 전제 \(t\not\in\mathit{Domain}(\Gamma)\), \(\Gamma'\vdash \tau_1\), \(\Gamma'\vdash \tau_2\), \(\Gamma\vdash \tau\)와 둘째 규칙의 전제 \(\Gamma\vdash\tau\)는 타입 체계의 안전성을 위하여 존재한다. 이 전제 중 하나라도 빠지면 타입 체계는 안전하지 않다.

타입 체계가 안전하지 않음을 확인하기 위해서는 안전성에 대한 반례가 되는 식을 찾아야 한다. 즉, 타입 체계가 거절하지 않지만 계산 중 타입 오류가 발생하는 식을 찾아야 한다. TVFAE에서 그런 반례를 찾는 전략은 같은 이름의 타입을 두 번 정의하는 것이다. 타입의 이름은 같지만 형태의 이름이 다르거나 형태가 가지는 값의 타입이 다른 두 타입을 정의했다고 하자. 그리고 한 타입의 값을 다른 타입의 값으로 사용하면 실행 중 타입 오류가 발생한다. 타입 체계는 타입의 이름만으로 두 타입이 같은지 판단하므로 그러한 식을 거절할 수 없다. 예를 들어 다음처럼 \(Fruit\) 타입을 두 번 정의했다고 하자.

\[\textsf{type}\ Fruit=Apple(\textsf{num})\ |\ Banana(\textsf{num}\times\textsf{num})\ \textsf{in}\ \cdots\]

\[\textsf{type}\ Fruit=Apple(\textsf{num})\ |\ Banana(\textsf{num})\ \textsf{in}\ \cdots\]

\(Banana(5)\)가 두 번째 \(Fruit\) 타입의 값이라고 하자. 이 값을 첫 번째 \(Fruit\) 타입의 값으로 사용하여 패턴 대조를 해 보자.

\[e\ \textsf{match}\ Apple(x)\rightarrow x\ |\ Banana(x)\rightarrow x.2\]

\(e\)의 계산 결과가 \(Banana(5)\)이면 \(x\)의 값이 \(5\)인 환경 아래에서 \(x.2\)를 계산하게 된다. \(x\)의 값이 순서쌍이 아니므로 \(x.2\)를 계산할 때 타입 오류가 발생한다.

물론 현재의 타입 체계는 잘 설계되었기 때문에 이런 경우는 발생하지 않는다. 같은 이름의 타입을 한 식 안에서 두 번 이상 정의할 수는 있지만 한 타입의 값을 다른 타입으로 사용할 가능성이 막혀 있다. 현재의 타입 체계는 안전하다.

그러나 위에서 말한 전제 중 하나라도 없애면 한 타입의 값을 이름만 같은 다른 타입으로 사용하는 식을 만들 수 있게 된다. 즉, 타입 안전성이 사라지는 것이다. 지금부터는 그런 식들을 직접 만들어 보겠다. 단, 그런 식들을 스스로 찾아봄으로써 TVFAE에 대해 완전히 이해할 수 있게 되므로 글에 식을 완전히 보여주지는 않을 것이다. 대신 식을 만들 수 있는 실마리를 제공할 것이다.

### 이미 정의된 타입인지 확인하지 않는 경우

\[
\frac
{ \begin{array}{c}x_1\not=x_2 \quad
  \Gamma'=\Gamma[t=\{(x_1,\tau_1),(x_2,\tau_2)\},x_1:\tau_1\rightarrow t,x_2:\tau_2\rightarrow t] \\
  \Gamma'\vdash e:\tau \quad
  \Gamma'\vdash \tau_1 \quad
  \Gamma'\vdash \tau_2 \quad
  \Gamma\vdash \tau\end{array} }
{ \Gamma\vdash \textsf{type}\ t=x_1(\tau_1)\ |\ x_2(\tau_2)\ \textsf{in}\ e:\tau }
\]

다른 규칙은 모두 그대로이고 타입 정의 식의 타입 규칙만 위처럼 바뀌었다고 하자. 전제 \(t\not\in\mathit{Domain}(\Gamma)\)가 사라진 것이다. 타입 환경에 이름이 \(t\)인 타입이 정의되어 있어도 또 이름이 \(t\)인 타입을 정의할 수 있다. 이제 다음과 같은 식도 적당한 \(e\)에 대해 올바른 타입의 식이다.

\[
\begin{array}{l}
\textsf{type}\ Fruit=Apple(\textsf{num})\ |\ Banana(\textsf{num})\ \textsf{in} \\
\quad \textsf{type}\ Fruit=Apple(\textsf{num})\ |\ Cherry(\textsf{num})\ \textsf{in} \\
\quad\quad e
\end{array}
\]

\(e\) 안에서 \(Banana\)의 생성자를 호출하여 \(Fruit\) 타입의 값을 만들 수 있다. 그 값을 패턴 대조의 대상으로 사용하면 타입 체계는 \(Apple\) 형태의 값이거나 \(Cherry\) 형태의 값이라고 판단할 것이다. 그러나 실제로 주어진 값은 \(Banana\) 타입의 값이므로 패턴 대조 시에 값과 일치하는 패턴을 찾을 수 없다. 따라서 실행 중 타입 오류가 발생한다.

### 형태가 가지는 값의 타입이 올바른 형태인지 확인하지 않는 경우

\[
\frac
{ \begin{array}{c}x_1\not=x_2 \quad
  t\not\in\mathit{Domain}(\Gamma) \\
  \Gamma'=\Gamma[t=\{(x_1,\tau_1),(x_2,\tau_2)\},x_1:\tau_1\rightarrow t,x_2:\tau_2\rightarrow t] \\
  \Gamma'\vdash e:\tau \quad
  \Gamma'\vdash \tau_2 \quad
  \Gamma\vdash \tau\end{array} }
{ \Gamma\vdash \textsf{type}\ t=x_1(\tau_1)\ |\ x_2(\tau_2)\ \textsf{in}\ e:\tau }
\]

다른 규칙은 모두 그대로이고 타입 정의 식의 타입 규칙만 위처럼 바뀌었다고 하자. 전제 \(\Gamma'\vdash \tau_1\)이 사라진 것이다. 타입을 정의할 때 첫 번째 형태는 가지는 값의 타입이 잘못된 형태일 수 있다. 이제 다음과 같은 식도 적당한 \(e\)에 대해 올바른 타입의 식이다.

\[
\begin{array}{l}
\textsf{type}\ Fruit=Apple(Color)\ |\ Banana(\textsf{num})\ \textsf{in} \\
\quad e
\end{array}
\]

\(Color\)는 정의된 적이 없다. 따라서 잘못된 형태의 타입이다. 원래의 타입 체계는 위 식을 거절하지만, 바뀐 타입 체계는 첫 형태가 가지는 값의 타입을 검사하지 않으므로 위 식을 수락한다. 이제 \(e\) 안에서 \(Color\) 타입을 두 번 정의하여 타입 안전성의 반례를 만들 수 있다. 그러나 빠진 하나의 전제 이외의 전제는 모두 그대로 있으므로 반례를 찾으려면 약간의 고민이 필요하다. \(Color\) 타입의 값을 사용하는 데는 제약이 있지만 \(Color\) 타입의 값을 \(Apple\)의 생성자에 넘겨 만든 \(Fruit\) 타입의 값은 자유롭게 사용할 수 있다는 점이 중요한 발상이다.

\[
\begin{array}{l}
\textsf{type}\ Fruit=Apple(Color)\ |\ Banana(\textsf{num})\ \textsf{in} \\
\quad (\lambda f:Fruit.\textsf{type}\ Color=\cdots \\
\quad\quad \cdots \\
\quad )\ (\textsf{type}\ Color=\cdots \\
\quad\quad \cdots \\
\quad )
\end{array}
\]

위 식의 생략된 부분을 잘 채우면 타입 체계가 수락하지만 실행 중 타입 오류를 일으키는 식이 완성된다.

### 정의된 타입이 영역을 빠져나가는 경우

\[
\frac
{ \begin{array}{c}x_1\not=x_2 \quad
  t\not\in\mathit{Domain}(\Gamma) \\
  \Gamma'=\Gamma[t=\{(x_1,\tau_1),(x_2,\tau_2)\},x_1:\tau_1\rightarrow t,x_2:\tau_2\rightarrow t] \\
  \Gamma'\vdash e:\tau \quad
  \Gamma'\vdash \tau_1 \quad
  \Gamma'\vdash \tau_2 \quad
  \end{array} }
{ \Gamma\vdash \textsf{type}\ t=x_1(\tau_1)\ |\ x_2(\tau_2)\ \textsf{in}\ e:\tau }
\]

다른 규칙은 모두 그대로이고 타입 정의 식의 타입 규칙만 위처럼 바뀌었다고 하자. 전제 \(\Gamma\vdash \tau\)가 사라진 것이다. 타입 정의 식의 몸통의 타입이, 그 타입 정의 식에서 정의한 타입을 포함할 수 있다. 정의된 타입이, 그 타입을 사용할 수 있는 영역을 빠져나가 다른 곳에서 쓰이게 되는 것이다. 이제 다음 두 식 모두 올바른 타입의 식이다.

\[
\begin{array}{l}
\textsf{type}\ Fruit=Apple(\textsf{num}\times\textsf{num})\ |\ Banana(\textsf{num})\ \textsf{in} \\
\quad \lambda f:Fruit.\cdots
\end{array}
\]

\[
\begin{array}{l}
\textsf{type}\ Fruit=Apple(\textsf{num})\ |\ Banana(\textsf{num})\ \textsf{in} \\
\quad Apple\ 5
\end{array}
\]

물론 위 두 식 모두 실행 중에 타입 오류를 일으키지 않는다. 그러나 첫 식을 함수로, 둘째 식을 인자로 하는 함수 적용 식을 만들면 올바른 타입의 식이지만 실행 중 타입 오류가 일어나게 할 수 있다.

### 함수의 매개변수 타입이 올바른 형태인지 확인하지 않는 경우

\[
\frac
{ \Gamma\lbrack x:\tau\rbrack\vdash \tau' }
{ \Gamma\vdash \lambda x:\tau.e:\tau\rightarrow\tau' }
\]

다른 규칙은 모두 그대로이고 람다 요약의 타입 규칙만 위처럼 바뀌었다고 하자. 전제 \(\Gamma\vdash \tau\)가 사라진 것이다. 매개변수 타입이 잘못된 타입일 수 있다. 즉, 매개변수 타입이 정의되지 않은 타입을 사용할 수 있다. 이제 다음 두 식 모두 올바른 타입의 식이다.

\[
\lambda f:Fruit\rightarrow\textsf{num}.\cdots
\]

\[
\lambda f:Fruit.\cdots
\]

물론 위 두 식 모두 실행 중에 타입 오류를 일으키지 않는다. 그러나 첫 식을 함수로, 둘째 식을 인자로 하는 함수 적용 식을 만들면 올바른 타입의 식이지만 실행 중 타입 오류가 일어나게 할 수 있다.

## 감사의 말

글을 확인하고 의견을 주신 류석영 교수님께 감사드립니다.
