이번 글에서는 WAE에 *일차 함수*(first-order function)를 추가하여 F1WAE를 정의한다. 일차 함수는 함수를 인자로 받을 수 없고 함수를 결과로 낼 수 없는 함수이다. 지난 글부터 언어를 확장하고 문법과 의미를 정의하는 형태가 반복되므로, 특별히 어려운 내용이 없다면 이번 글부터는 설명을 간략히 할 것이다.

이 글에서 다루는 F1WAE는 수업에서 다루는 F1WAE와 약간 다르다. 수업에서는 F1WAE의 함수 정의와 식만 정의하지만, 이 글에서는 그에 더해 F1WAE의 프로그램을 정의한다. 프로그램을 정의한 이유는 언어를 완전하게 만들기 위함이나, 프로그램을 정의한 방법은 이 글의 핵심이 아니다. 중요한 부분은 일차 함수 호출의 문법과 의미로, 이에 중점을 두고 읽기 바란다.

## 문법

다음은 F1WAE의 요약 문법이다.

\[
\begin{array}{lrcl}
\text{Integer} & n & \in & \mathbb{Z} \\
\text{Variable} & x & \in & \textit{Id} \\
\text{Function Name} & f & \in & \textit{Id} \\
\text{Expression} & e & ::= & n \\
&& | & e + e \\
&& | & e - e \\
&& | & \textsf{val}\ x = e\ \textsf{in}\ e \\
&& | & x \\
&& | & f(e) \\
\text{Value} & v & ::= & n \\
\text{Function Definition} & F & ::= & f(x)=e \\
\text{Program} & P & ::= & e \\
&& | & F;P
\end{array}
\]

WAE의 식에 함수 적용을 추가한 것이 F1WAE의 식이다. \(f(e)\)가 함수 적용 식으로, \(f\)라는 이름의 함수를 \(e\)를 계산하여 나온 값에 적용하는 식이다.

함수 정의는 함수의 이름, 매개변수의 이름, 함수의 몸통인 식으로 구성된다. 메타변수 \(F\)는 함수 정의를 나타내고 메타변수 \(f\)는 함수 이름을 나타낸다.

프로그램은 식이거나, 함수 정의와 프로그램의 나열이다. 임의 개수의 함수 정의에 이은 하나의 식이 프로그램이라고 해석할 수 있다. 메타변수 \(P\)가 프로그램을 나타낸다.

다음은 F1WAE 프로그램의 예시이다.

\[
\begin{array}{l}
id(x)=x; \\
twice(x)=x+x; \\
\textsf{val}\ x=1\ \textsf{in}\ twice(id(x))
\end{array}
\]

## 의미

\[
\begin{array}{lrcl}
\text{Environment} & \sigma & \in & \mathit{Id}\hookrightarrow \text{Value}
\end{array}
\]

환경은 식별자에서 값으로 가는 부분 함수로, 변수가 나타내는 값을 저장한다.

식이 나타내는 값을 구하기 위해서는, 변수가 나타내는 값뿐만 아니라, 함수 이름이 주어졌을 때 그 함수의 정의를 알아야 한다.

\[
\begin{array}{lrcl}
\text{Function Environment} & \phi & \in & \mathit{Id}\hookrightarrow (\mathit{Id}\times\text{Expression})
\end{array}
\]

함수 환경은 식별자에서 식별자와 식의 순서쌍으로 가는 부분 함수이다. 함수의 이름에 따른 매개변수의 이름과 몸통을 저장한다.

\[\Rightarrow\subseteq\text{Environment}\times\text{Function Environment}\times\text{Expression}\times\text{Value}\]

식을 계산하기 위해서는 환경과 함수 환경이 필요하며 값을 결과로 낸다. \(\Rightarrow\)는 네 집합 사이의 관계이다. \(\sigma;\phi\vdash e\Rightarrow v\)는 \(\sigma\)와 \(\phi\) 아래에서 \(e\)를 계산했을 때 \(v\)가 결과임을 의미한다.

\[
\frac
{
  f\in\mathit{Domain}(\phi) \quad
  \phi(f)=(x,e') \quad
  \sigma;\phi\vdash e\Rightarrow v' \quad
  \lbrack x\mapsto v'\rbrack;\phi\vdash e'\Rightarrow v
}
{ \sigma;\phi\vdash f(e)\Rightarrow v }
\]

위 추론 규칙은 함수 적용 식의 의미를 정의한다. 함수가 사용할 수 있는 환경은 함수 적용이 일어날 때의 환경이 아닌 함수가 정의될 때의 환경이다. 함수 정의는 어떤 변수의 묶는 등장의 영역에도 포함되지 않으므로, 함수 정의 시 환경은 빈 환경이다. 따라서, 함수 몸통 \(e'\)을 계산할 때 사용하는 환경은 \(\sigma\lbrack x\mapsto v'\rbrack\)이 아닌 \(\lbrack x\mapsto v'\rbrack\)이다. 반대로, 함수 이름의 묶는 등장의 영역은 프로그램 전체이기에 함수 환경은 한 프로그램에서 언제나 같다. 그러므로, \(e\)와 \(e'\)을 계산할 때 \(\phi\)를 그대로 사용한다.

나머지 식에 관한 추론 규칙은 함수 환경이 추가된 것만 제외하고 WAE와 같다.

\[
\sigma;\phi\vdash n\Rightarrow n
\]

\[
\frac
{ \sigma;\phi\vdash e_1\Rightarrow n_1 \quad \sigma;\phi\vdash e_2\Rightarrow n_2 }
{ \sigma;\phi\vdash e_1+e_2\Rightarrow n_1+n_2 }
\]

\[
\frac
{ \sigma;\phi\vdash e_1\Rightarrow n_1 \quad \sigma;\phi\vdash e_2\Rightarrow n_2 }
{ \sigma;\phi\vdash e_1-e_2\Rightarrow n_1-n_2 }
\]

\[
\frac
{
  \sigma;\phi\vdash e_1\Rightarrow v_1 \quad
  \sigma\lbrack x\mapsto v_1\rbrack;\phi\vdash e_2\Rightarrow v_2
}
{ \sigma;\phi\vdash \textsf{val}\ x=e_1\ \textsf{in}\ e_2\Rightarrow v_2 }
\]

\[
\frac
{ x\in\mathit{Domain}(\sigma) }
{ \sigma;\phi\vdash x\Rightarrow \sigma(x)}
\]

프로그램의 의미는 함수 환경, 프로그램, 값 사이의 관계로 정의한다. 위에서 \(\Rightarrow\)를 식의 의미를 나타내기 위해 이미 사용하였지만, 프로그램의 의미를 나타낼 때도 \(\Rightarrow\)를 사용해도 혼동할 여지가 적기에, 편의상 표기를 남용한다.

\[\Rightarrow\subseteq\text{Function Environment}\times\text{Program}\times\text{Value}\]

\(\phi\vdash P\Rightarrow v\)는 함수 환경 \(\phi\) 아래에서 프로그램 \(P\)를 계산한 결과가 \(v\)임을 의미한다.

\[
\frac
{ \emptyset;\phi\vdash e\Rightarrow v }
{ \phi\vdash e\Rightarrow v }
\]

함수 정의 없는 프로그램을 계산하는 것은 프로그램의 식을 계산하는 것과 같다.

\[
\frac
{ \phi\lbrack f\mapsto(x,e)\rbrack\vdash P\Rightarrow v }
{ \phi\vdash f(x)=e;P\Rightarrow v }
\]

함수 정의와 어떤 프로그램의 순서쌍인 프로그램은 함수 정의를 함수 환경에 추가한 뒤 앞의 프로그램을 계산하는 것과 같다.

## 인터프리터 구현

다음은 F1WAE의 요약 문법을 Scala 코드로 표현한 것이다.

```scala
sealed trait F1WAE
case class Num(n: Int) extends F1WAE
case class Add(l: F1WAE, r: F1WAE) extends F1WAE
case class Sub(l: F1WAE, r: F1WAE) extends F1WAE
case class With(x: String, i: F1WAE, b: F1WAE) extends F1WAE
case class Id(x: String) extends F1WAE
case class App(f: String, a: F1WAE) extends F1WAE
```

환경과 함수 환경 모두 사전 자료구조로 표현할 수 있다. 환경은 열쇠가 문자열, 값이 정수이다. 함수 환경은 열쇠가 문자열, 값이 문자열과 F1WAE 식의 순서쌍이다.

```scala
type Env = Map[String, Int]
type FEnv = Map[String, (String, F1WAE)]
```

환경에서 식별자가 가리키는 값을 찾는 함수 `lookup`과 함수 환경에서 식별자가 가리키는 함수를 찾는 함수 `lookupFD`를 정의한다.

```scala
def lookup(x: String, env: Env): Int =
  env.getOrElse(x, throw new Exception)

def lookupFD(f: String, fEnv: FEnv): (String, F1WAE) =
  fEnv.getOrElse(f, throw new Exception)
```

`interp` 함수는 식, 환경, 함수 환경을 인자로 받아 식이 나타내는 값을 결과로 낸다.

```scala
def interp(e: F1WAE, env: Env, fEnv: FEnv): Int = e match {
  case Num(n) => n
  case Add(l, r) => interp(l, env, fEnv) + interp(r, env, fEnv)
  case Sub(l, r) => interp(l, env, fEnv) - interp(r, env, fEnv)
  case With(x, i, b) =>
    interp(b, env + (x -> interp(i, env, fEnv)), fEnv)
  case Id(x) => lookup(x, env)
  case App(f, a) =>
    val (x, e) = lookupFD(f, fEnv)
    interp(e, Map(x -> interp(a, env, fEnv)), fEnv)
}
```

식이 `App`인 경우에 한 개의 식별자만 가지고 있는 새로운 환경을 만들어 함수의 몸통을 계산한다.

다음은 `interp` 함수를 호출한 예시이다.

```scala
// id(x) = x;
// twice(x) = x + x;
// val x = 1 in twice(id(x))
interp(
  With("x", Num(1),
    App("twice",
      App("id", Id("x"))
    )
  ),
  Map.empty,
  Map(
    "id" -> ("x", Id("x")),
    "twice" -> ("x", Add(Id("x"), Id("x")))
  )
)
// 2
```

## 영역

### 정적 영역

위에서 정의한 의미와 구현한 인터프리터에서 사용한 영역은 *정적 영역*(static scope)이다. 함수 몸통을 계산하는 데 함수가 정의될 때의 환경을 사용하는 것이 정적 영역이다. 따라서, 아래 함수 \(f\)를 호출한다면 반드시 실행 시간 오류가 발생한다.

\[f(x)=x+y\]

정적 영역을 사용한다면, 어떤 식별자가 자유 식별자인지 프로그램을 실행하지 않고도 반드시 알 수 있다. 또한, 식별자가 묶인 등장이라면, 반드시 하나의 묶는 등장에 고정적으로 묶여 있으며, 어느 묶는 등장에 묶여 있는지 실행하지 않고 알 수 있다. 코드만 보고도 식별자가 가리키는 대상을 알 수 있기에 정적 영역은 *문법적 영역*(lexical scope)이라고도 부른다.

현재 사용되는 언어 대부분은 정적 영역을 사용한다.

### 동적 영역

정적 영역과 반대로 *동적 영역*(dynamic scope)은 함수 호출 시의 환경을 함수 몸통을 계산하는 데 사용한다. 따라서, 아래 함수 \(f\)를 호출하였을 때, 호출 시 환경에 따라 \(y\)가 자유 식별자일 수도, 묶인 등장일 수도 있으며, 호출마다 다른 묶는 등장에 묶여있을 수 있다.

\[f(x)=x+y\]

예를 들면, 아래 식을 계산한 결과는 \(3\)이다. 두 번의 \(f\)의 호출에서 \(y\)가 가리키는 대상이 다르다.

\[
\begin{array}{l}
f(x)=x+y; \\
(\textsf{val}\ y=1\ \textsf{in}\ f(0))\ +\ (\textsf{val}\ y=2\ \textsf{in}\ f(0))
\end{array}
\]

다음 추론 규칙은 동적 영역을 사용하는 F1WAE의 함수 적용의 의미를 정의한다.

\[
\frac
{
  \phi(f)=(x,e') \quad
  \sigma;\phi\vdash e\Rightarrow v' \quad
  \sigma\lbrack x\mapsto v'\rbrack;\phi\vdash e'\Rightarrow v
}
{ \sigma;\phi\vdash f(e)\Rightarrow v }
\]

`interp` 함수의 `App` 경우를 수정하여 인터프리터가 동적 영역을 사용하도록 할 수 있다.

```scala
def interp(e: F1WAE, env: Env, fEnv: FEnv): Int = e match {
  ...
  case App(f, a) =>
    val (x, e) = lookupFD(f, fEnv)
    interp(e, env + (x -> interp(a, env, fEnv)), fEnv)
}
```

동적 영역은 함수의 동작이 함수 호출 시의 환경에 영향받게 만들어 프로그램을 *모듈 적*(modular)이지 않게 한다. 프로그램의 서로 다른 부분이 예상치 못한 간섭을 일으키기에 프로그램이 의도하지 않은 결과를 낼 가능성이 커진다.

동적 영역은 드물게 사용된다. Common LISP 등에서 볼 수 있으며, C 등에 있는 *매크로*(macro)를 동적 영역과 유사하게 이해할 수 있다.

```c
#define f(x) x + y

int main() {
    int y = 0;
    return f(0);
}
```

## 감사의 말

글을 확인하고 의견을 주신 류석영 교수님께 감사드립니다.
