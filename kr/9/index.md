지난 글에서 정의한 AE는 정수, 합, 차로만 구성된 작은 언어였다. 이번 글에서는 AE에 지역 변수를 추가한 언어인 WAE를 정의한다.

## 식별자

식별자는 변수, 함수, 타입, 패키지 등의 대상을 가리키는 이름으로, 일반적으로는 특정 조건을 만족하는 문자열이 식별자로 사용된다.

```scala
f(0)
def f(x: Int): Int = {
  val y = 2
  x + y
}
f(1)
x - y
```

위 코드에서 `f`, `x`, `y`는 *식별자*(identifier)이다. 엄밀히 이야기하면, `Int` 역시 식별자지만, 무시하도록 하겠다. `f`는 함수의 이름이고, `x`는 함수 `f`의 매개변수의 이름이며, `y`는 함수 `f`의 지역 변수의 이름이다.

식별자는 세 종류로 나눌 수 있다.

첫 번째는 *묶는 등장*(binding occurrence)이다. 등장을 통해서 식별자가 어떤 대상과 연결 지어질 때, 그 등장을 묶는 등장이라고 부른다. 위 코드에서 `def f(x: Int): Int`의 `f`와 `x`는 묶는 등장이며, `val y = 2`의 `y` 역시 묶는 등장이다. `f`는 `(x: Int) => { val y = 2; x + y }`라는 함수를 가리키고, `x`는 `f`에 전달된 인자를 가리킨다. `y`는 정숫값 `2`를 가리킨다.

두 번째 종류는 *묶인 등장*(bound occurrence)이다. 묶는 등장을 한 식별자를 사용할 수 있는 코드 공간을 그 묶는 등장의 *영역*(scope)이라고 한다. 영역 안에서 그 식별자가 다시 등장하였으며 묶는 등장이 아닐 때 그 등장을 묶인 등장이라고 한다. 묶인 등장을 할 때의 식별자가 나타내는 의미는 그 식별자가 가리키는 대상이다. 위 코드에서 `x + y`의 `x`와 `y`는 묶인 등장이며, `x`는 `x`의 묶는 등장에 묶여 있고, `y`는 `y`의 묶는 등장에 묶여 있다. `x`가 나타내는 값은 함수 호출에서 사용한 인자, `y`가 나타내는 값은 `2`이다. 두 값 모두 수이므로 `x + y`는 올바른 식이다. `f(1)`에서 `f`는 묶인 등장이며, `f`는 `f`의 묶는 등장에 묶여 있다. `f`가 나타내는 값은 `f`가 가리키는 함수이다. 따라서, `f(1)`이라는 식은 올바른 함수 호출이다.

세 번째는 *자유 식별자*(free identifier)이다. 식별자가 그 식별자의 어느 묶는 등장의 영역에도 포함되지 않는 공간에 등장하였다면, 해당 식별자는 자유 식별자이다. 함수 `f`의 묶는 등장의 영역은 함수 `f`의 몸통과 함수 `f`의 정의 아래에 오는 모든 코드이다. 따라서, `f(0)`의 `f`는 자유 식별자이다. `x`의 묶는 등장의 영역은 함수 `f`의 몸통이고, `y`의 묶는 등장의 영역은 `y`를 정의하는 줄부터 함수 `f`의 몸통이 끝나는 지점까지다. 그러므로, `x - y`의 `x`와 `y`는 모두 자유 식별자이다. 자유 식별자는 묶인 등장과 달리 어떤 대상도 가리키지 않는다. 따라서, 자유 식별자가 등장한 식을 계산하는 것은 대부분의 언어에서 실행 오류를 발생시키며, 자유 식별자가 있는 코드를 컴파일 하면 보통은 컴파일 오류가 발생한다.

```scala
def f(x: Int): Int = {
  def g(x: Int): Int = x
  g(x)
}
```

이 코드에서는 `x`의 묶는 등장이 두 번 일어난다. `def f(x: Int): Int`의 `x`와 `def g(x: Int): Int`의 `x` 모두 묶는 등장이다. 둘째 줄 마지막에 위치한 `x`는 묶인 등장이며, 첫 묶는 등장의 영역과 둘째 묶는 등장의 영역에 동시에 속한다. 일반적으로 사용되는 언어에서는, 묶는 등장이 둘 이상의 영역에 속할 때, 더 작은 영역을 가진 묶는 등장에 연결된 대상이 묶인 등장이 나타내는 대상이다. 따라서, 둘째 줄 마지막의 `x`는 `x`의 두 번째 묶는 등장에 묶여 있고, `g`의 인자를 가리킨다. 이처럼, 작은 영역을 가진 묶는 등장이 큰 영역을 가진 묶는 등장에 우선하는 것을 *가리기*(shadowing)이라고 한다. 한편, `g(x)`의 `x`는 `x`의 첫 묶는 등장의 영역에만 속하므로, `x`의 첫 묶는 등장에 묶여 있고, `f`의 인자를 가리킨다.

## WAE

WAE는 식이 수정 불가능한 지역 변수를 정의하거나 변수를 참조할 수 있는 산술식이다.

### 문법

다음은 WAE의 요약 문법이다.

\[
\begin{array}{lrcl}
\text{Integer} & n & \in & \mathbb{Z} \\
\text{Variable} & x & \in & \textit{Id} \\
\text{Expression} & e & ::= & n \\
&& | & e + e \\
&& | & e - e \\
&& | & \textsf{val}\ x = e\ \textsf{in}\ e \\
&& | & x \\
\text{Value} & v & ::= & n
\end{array}
\]

메타변수 \(n\)은 정수를 나타낸다.

메타변수 \(x\)는 변수를 나타내며 집합 \(\textit{Id}\)의 원소이다. \(\textit{Id}\)는 다른 집합의 원소들과 구분되는 어떤 집합으로, 모든 가능한 식별자의 집합이다.

메타변수 \(e\)는 식을 나타낸다. 식 \(\textsf{val}\ x = e_1\ \textsf{in}\ e_2\)는 지역 변수를 선언하는 식으로, \(x\)는 묶는 등장이다. \(x\)가 가리키는 대상은 \(e_1\)을 계산했을 때 나오는 값이며, 묶는 등장의 영역은 \(e_2\) 전체이다. \(e_1\)은 묶는 등장의 영역이 아니다. 식 \(x\)는 \(x\)의 묶인 등장이거나 자유 식별자이다. 묶인 등장이라면, 이 등장을 묶은 등장에 연결된 값이 이 등장이 가리키는 값이다. \(\textsf{val}\ x = 1\ \textsf{in}\ x\)에서 \(x\)의 두 번째 등장은 묶는 등장인 첫 번째 등장에 묶여 있다. 반면, \(\textsf{val}\ x = x\ \textsf{in}\ 1\)에서 두 번째로 등장한 \(x\)는 자유 식별자이다.

메타변수 \(v\)는 값을 나타낸다. AE와 마찬가지로 WAE의 값은 정숫값이다.

### 의미

AE의 자연적 의미를 정의할 때 정수, 합, 차의 의미를 정의하였다. WAE의 자연적 의미를 정의하기 위해서는 묶는 등장과 묶인 등장의 의미를 추가로 정의해야 한다. \(\textsf{val}\ x = 1\ \textsf{in}\ x\)를 생각해보자. 이를 Scala 식으로 표현하면 다음과 같다.

```scala
{
  val x = 1
  x
}
```

이 식이 나타내는 값은 1이다. `x`의 묶는 등장이 `1`과 연결되어 있기에 `x`의 묶인 등장은 값 `1`을 나타낸다. 식 전체의 값은 `x`의 묶인 등장의 값인 `1`이다.

\(\textsf{val}\ x = 1\ \textsf{in}\ x\)도 마찬가지로 생각할 수 있다. 식 전체의 값은 식의 몸통 부분인 \(x\)를 계산한 값과 같다. 그러나, 아무런 정보 없이 \(x\)의 값을 계산할 수는 없다. \(x\)의 묶는 등장이 \(x\)를 연결 지은 값을 알아야 묶인 등장의 값을 알 수 있다. 이 정보를 저장하기 위해서 새롭게 *환경*(environment)을 정의한다. 환경은 식별자를 열쇠, 값을 값으로 하는 유한한 크기의 사전이다. 수학적으로는 \(\mathit{Id}\)를 정의역, \(\text{Value}\)를 공역으로 하는 *부분 함수*(partial function)이다.

\[
\begin{array}{lrcl}
\text{Environment} & \sigma & \in & \mathit{Id}\hookrightarrow \text{Value}
\end{array}
\]

메타변수 \(\sigma\)는 환경을 나타낸다. 묶는 등장은 환경에 정보를 추가하고, 묶인 등장은 환경에 있는 정보를 사용한다.

AE의 자연적 의미는 \(\text{Expression}\)과 \(\text{Value}\)의 관계였다. WAE의 자연적 의미는 단순히 두 집합의 관계로는 표현할 수 없다. 어떤 식이 나타내는 값을 알기 위해서는 그 식에 등장하는 식별자들이 어떤 값을 가리키는지 알려주는 환경이 필요하다. 따라서, WAE의 자연적 의미는 세 집합 \(\text{Environment}\), \(\text{Expression}\), \(\text{Value}\)의 관계이다.

\[\Rightarrow\subseteq\text{Environment}\times\text{Expression}\times\text{Value}\]

\((\sigma,e,v)\in\Rightarrow\)는 환경 \(\sigma\) 아래에서 식 \(e\)를 계산한 결과가 값 \(v\)라는 것을 의미한다. 이는 \[\sigma\vdash e\Rightarrow v\]라고 표현한다. 직관적으로는, 환경과 식이 주어진 입력이고, 값이 계산해낸 출력이다.

추론 규칙으로 WAE의 자연적 의미를 정의할 수 있다.

\[
\sigma\vdash n\Rightarrow n
\]

식이 정수인 경우는 AE와 동일하다.

\[
\frac
{ \sigma\vdash e_1\Rightarrow n_1 \quad \sigma\vdash e_1\Rightarrow n_2 }
{ \sigma\vdash e_1+e_2\Rightarrow n_1+n_2 }
\]

\[
\frac
{ \sigma\vdash e_1\Rightarrow n_1 \quad \sigma\vdash e_1\Rightarrow n_2 }
{ \sigma\vdash e_1-e_2\Rightarrow n_1-n_2 }
\]

식이 합이나 차인 경우도 AE와 동일하나, *부분식*(subexpression) \(e_1\)과 \(e_2\)를 계산할 때도 환경이 필요하며, 전제와 결론에서 동일한 환경을 사용한다.

묶는 등장에 대한 규칙을 정의하기 위해서는 환경에 정보를 추가할 방법이 필요하다. 아래와 같이 환경의 확장을 정의한다.

\[
\sigma \lbrack x\mapsto v\rbrack=\sigma\setminus\{(x',v'):x'=x\land\sigma(x')=v'\}\cup\{(x,v)\}
\]

환경 \(\sigma\)에 식별자 \(x\)가 값 \(v\)를 가리킨다는 사실을 추가하여 얻어진 환경은 \(\sigma\)에서 \(x\)에 관한 순서쌍을 제외하고 \((x,v)\)를 추가한 것이다. 이 새로운 환경은 아래와 같은 성질을 가지고 있다.

\[
\sigma \lbrack x\mapsto v\rbrack(x') =
\begin{cases}
v & \text{if}\ x=x' \\
v' & \text{if}\ \sigma(x')=v'
\end{cases}
\]

\(x=x'\)인 경우를 보면 환경을 확장하는 과정에서 자연스럽게 가리기가 일어나는 것을 알 수 있다. 

\[
\frac
{
  \sigma\vdash e_1\Rightarrow v_1 \quad
  \sigma\lbrack x\mapsto v_1\rbrack\vdash e_2\Rightarrow v_2
}
{ \sigma\vdash \textsf{val}\ x=e_1\ \textsf{in}\ e_2\Rightarrow v_2 }
\]

\(\textsf{val}\ x=e_1\ \textsf{in}\ e_2\)의 값을 계산하기 위해서는 먼저 식별자 \(x\)가 가리키는 값을 찾아야 한다. 이를 위해서 \(e_1\)을 계산하여 값 \(v_1\)을 얻는다. \(e_1\)은 \(x\)의 묶는 등장의 영역에 포함되지 않으므로, \(e_1\)을 계산할 때는 주어진 환경인 \(\sigma\)를 그대로 사용한다. \(e_2\)를 계산하여 얻은 값 \(v_2\)가 전체 식 \(\textsf{val}\ x=e_1\ \textsf{in}\ e_2\)의 값이며, \(e_2\)를 계산할 때는 \(\sigma\)에 \(x\)가 \(v_1\)을 가리킨다는 정보를 추가한 환경인 \(\sigma\lbrack x\mapsto v_1\rbrack\)을 사용한다.

식이 식별자인 경우에는 식별자가 묶인 등장인지 자유 식별자인지 알아야 한다. 환경의 정의역을 구하는 함수를 다음과 같이 정의한다.

\[
\mathit{Domain}(\sigma)=\{x:\exists v.(x,v)\in\sigma\}
\]

\(\mathit{Domain}(\sigma)\)는 \(\sigma\)의 정의역이다. 어떤 식별자가 환경의 정의역의 원소라면 묶인 등장이고, 아니라면 자유 식별자이다.

\[
\frac
{ x\in\mathit{Domain}(\sigma) }
{ \sigma\vdash x\Rightarrow \sigma(x)}
\]

식별자 \(x\)가 묶인 등장이면 환경에서 \(x\)가 가리키고 있는 값이 \(x\)가 나타내는 값이다. 자유 식별자인 경우, 전제가 만족하지 않는다. 따라서, 자유 식별자를 가지고 있는 식은 어떤 값도 나타내지 않으며, 이는 프로그램에 자유 식별자가 존재해서 실행 중에 오류가 발생하는 상황과 같다고 생각할 수 있다.

아래는 WAE의 자연적 의미를 정의하는 추론 규칙을 한군데 모은 것이다.

\[
\sigma\vdash n\Rightarrow n
\]

\[
\frac
{ \sigma\vdash e_1\Rightarrow n_1 \quad \sigma\vdash e_1\Rightarrow n_2 }
{ \sigma\vdash e_1+e_2\Rightarrow n_1+n_2 }
\]

\[
\frac
{ \sigma\vdash e_1\Rightarrow n_1 \quad \sigma\vdash e_1\Rightarrow n_2 }
{ \sigma\vdash e_1-e_2\Rightarrow n_1-n_2 }
\]

\[
\frac
{
  \sigma\vdash e_1\Rightarrow v_1 \quad
  \sigma\lbrack x\mapsto v_1\rbrack\vdash e_2\Rightarrow v_2
}
{ \sigma\vdash \textsf{val}\ x=e_1\ \textsf{in}\ e_2\Rightarrow v_2 }
\]

\[
\frac
{ x\in\mathit{Domain}(\sigma) }
{ \sigma\vdash x\Rightarrow \sigma(x)}
\]

### 인터프리터 구현

다음은 WAE의 요약 문법을 Scala 코드로 표현한 것이다.

```scala
sealed trait WAE
case class Num(n: Int) extends WAE
case class Add(l: WAE, r: WAE) extends WAE
case class Sub(l: WAE, r: WAE) extends WAE
case class With(x: String, i: WAE, b: WAE) extends WAE
case class Id(x: String) extends WAE
```

식별자는 임의의 문자열이다. `With`는 지역 변수 선언, `Id`는 지역 변수 사용에 해당하는 식을 만든다.

환경은 Scala 표준 라이브러리의 `Map`을 사용하여 표현할 수 있다. `Map`은 사전 자료구조이다.

```scala
val m0: Map[String, Int] = Map.empty
val m1: Map[String, Int] = m0 + ("one" -> 1)
m1.getOrElse("one", -1)  // 1
m1.getOrElse("two", -1)  // -1
```

`Map`은 두 개의 타입 매개변수를 가지며, 첫 번째 매개변수는 열쇠의 타입, 두 번째 매개변수는 값의 타입이다. 따라서 `Map[String, Int]`는 문자열을 열쇠로 하고 정수를 값으로 하는 사전 타입이다. `Map.empty`는 어떤 열쇠와 값도 없는 사전이다. 라이브러리는 여러 메서드를 정의하며, `+` 메서드를 사용하여 사전에 열쇠와 값의 쌍을 추가할 수 있다. `getOrElse`는 인자를 두 개 받는 메서드로, 첫 인자가 사전의 열쇠이면 그 열쇠에 해당하는 값을 결과로 내고, 아닌 경우에는 두 번째 인자를 계산하여 그 값을 결과로 낸다.

```scala
type Env = Map[String, Int]
```

환경은 식별자에서 값으로 가는 부분 함수이다. 식별자는 문자열, 값은 정수이므로 환경은 `Map[String, Int]` 타입을 가진다. 위 식은 `Map[String, Int]`의 타입 별명 `Env`를 정의한다. `Env`는 `Map[String, Int]`과 완전히 같은 타입이다. 따라서, 환경은 `Env` 타입이다.

```scala
def lookup(x: String, env: Env): Int =
  env.getOrElse(x, throw new Exception)
```

`lookup` 함수는 인자로 주어진 환경에서 인자로 주어진 식별자가 가리키는 값을 찾아 결과로 낸다. 만약 환경이 식별자에 대한 정보를 가지고 있지 않다면 예외가 발생한다.

```scala
def interp(e: WAE, env: Env): Int = e match {
  case Num(n) => n
  case Add(l, r) => interp(l, env) + interp(r, env)
  case Sub(l, r) => interp(l, env) - interp(r, env)
  case With(x, i, b) => interp(b, env + (x -> interp(i, env)))
  case Id(x) => lookup(x, env)
}
```

`Num`인 경우는 AE의 인터프리터와 동일하다. `Add`와 `Sub`인 경우도 인자로 `env`를 같이 넘기는 것만 제외하면 동일하다. `With`인 경우, 환경 `env` 아래에서 `i`의 값을 계산하여 나온 결과를 사용해 환경을 확장한 다음 `b`의 값을 계산한다. `Id`인 경우, 위에서 정의한 `lookup` 함수를 호출하여 식별자가 가리키는 값을 찾는다.

`interp` 함수에 식 \(\textsf{val}\ x=1\ \textsf{in}\ x+x\)와 빈 환경을 인자로 넘기면 `2`가 나온다.

```scala
// val x = 1 in x + x
interp(
  With("x", Num(1),
    Add(Id("x"), Id("x"))
  ),
  Map.empty
)
// 2
```

아래의 증명나무가 빈 환경 \(\emptyset\) 아래에서 식 \(\textsf{val}\ x=1\ \textsf{in}\ x+x\)를 계산하면 \(2\)가 나옴을 증명한다.

\[
\frac
{
  \emptyset\vdash 1\Rightarrow 1 \quad
  \frac
  {
    {\huge
    \frac
    { x\in\mathit{Domain}(\lbrack x\mapsto 1\rbrack) }
    { \lbrack x\mapsto 1\rbrack\vdash x\Rightarrow 1 } \quad
    \frac
    { x\in\mathit{Domain}(\lbrack x\mapsto 1\rbrack) }
    { \lbrack x\mapsto 1\rbrack\vdash x\Rightarrow 1 }
    }
  }
  {{\Large \lbrack x\mapsto 1\rbrack\vdash x+x\Rightarrow 2 }}
}
{ \emptyset\vdash \textsf{val}\ x=1\ \textsf{in}\ x+x\Rightarrow 2 }
\]

## 감사의 말

글을 확인하고 의견을 주신 류석영 교수님께 감사드립니다.
