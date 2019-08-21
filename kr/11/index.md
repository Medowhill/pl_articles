이번 글에서는 AE에 *일급 함수*(first-class function)를 추가하여 FAE를 정의한다. 일급 함수는 값으로 사용될 수 있는 함수로, 함수를 함수의 인자로 사용하거나, 함수가 함수를 결과로 내는 것이 가능하다. 함수를 인자로 받거나 함수를 결과로 내는 함수는 일차 함수가 아니므로, *고차 함수*(higher-order function)라고 부르며, 대부분 맥락에서 일급 함수와 고차 함수를 같은 뜻으로 사용할 수 있다.

## 문법

다음은 FAE의 요약 문법이다.

\[
\begin{array}{lrcl}
\text{Integer} & n & \in & \mathbb{Z} \\
\text{Variable} & x & \in & \textit{Id} \\
\text{Expression} & e & ::= & n \\
&& | & e + e \\
&& | & e - e \\
&& | & x \\
&& | & \lambda x.e \\
&& | & e\ e \\
\text{Value} & v & ::= & n \\
&& | & \langle \lambda x.e,\sigma \rangle \\
\text{Environment} & \sigma & \in & \textit{Id}\hookrightarrow\text{Value}
\end{array}
\]

FAE의 식은 AE의 식이거나, 변수 \(x\)이거나, *람다 요약*(lambda abstraction) \(\lambda x.e\)이거나, *함수 적용*(function application) \(e\ e\)이다. 람다 요약은 익명 함수를 만드는 식으로, \(\lambda x.e\)라는 식은 매개변수 \(x\)와 몸통 \(e\)를 가진 익명 함수를 나타낸다. \(x\)는 묶는 등장이다. 함수 적용 \(e_1\ e_2\)에서는 앞의 식 \(e_1\)을 계산하여 얻은 결과가 함수이고, 뒤의 식 \(e_2\)를 계산하여 얻은 결과가 인자이다. 식을 계산하는 것은 함수를 인자에 적용하는 것이다.

FAE의 값은 정수이거나 *클로저*(closure)이다. 클로저는 람다 요약과 환경의 순서쌍으로, 함숫값을 나타내며, 람다 요약과 함께 람다 요약이 함수를 정의할 때의 환경을 가지고 있다. 람다 요약은 자유 식별자를 가질 수 있으나, 올바른 프로그램을 실행하면 클로저의 환경에 자유 식별자가 가리키는 값이 저장되어 있기에 오류가 발생하지 않는다. 다음 식을 생각해보자.

\[\lambda x.\lambda y.(x + y)\ 1\ 2\]

\(\lambda y.(x+y)\)라는 람다 요약 자체는 \(x\)라는 자유 식별자를 가지고 있다. 그러나, 실제 실행 시에는 람다 요약이 계산될 때 환경에 \(x\)가 \(1\)이라는 정보가 저장되어 있기에, \(\lambda y.(x+y)\)를 계산하여 얻은 클로저의 환경에도 \(x\)가 \(1\)이라는 정보가 포함되어 있다. 클로저를 호출하여 몸통을 계산할 때는 클로저의 환경을 사용하므로, \(x+y\)를 오류 없이 계산할 수 있다. 뒤에서 FAE의 의미를 정의함으로써 람다 요약과 함수 적용이 어떻게 작동하는지 명확하게 나타낼 것이다.

FAE의 환경은 WAE나 F1WAE의 환경과 마찬가지로 식별자에서 값으로 가는 부분 함수이다. 다만, 값이 정수만 가능한 것이 아니라, 클로저도 될 수 있다는 차이가 있다.

## 의미

FAE의 의미는 WAE의 의미와 마찬가지로 환경, 식, 값의 관계이다.

\[\Rightarrow\subseteq\text{Environment}\times\text{Expression}\times\text{Value}\]

\(\sigma\vdash e\Rightarrow v\)는 \(\sigma\) 아래에서 \(e\)를 계산하면 결과가 \(v\)임을 나타낸다.

정수, 합, 차, 변수의 의미는 WAE의 의미와 동일하다.

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
{ x\in\mathit{Domain}(\sigma) }
{ \sigma\vdash x\Rightarrow \sigma(x)}
\]

람다 요약은 현재 환경을 가지고 있는 클로저를 만든다.

\[
\sigma\vdash \lambda x.e\Rightarrow \langle\lambda x.e,\sigma\rangle
\]

함수 적용은 두 식을 모두 계산한 뒤, 인자의 값을 추가한 클로저의 환경을 사용해서 클로저의 몸통을 계산한다.

\[
\frac
{ \sigma\vdash e_1\Rightarrow\langle\lambda x.e,\sigma'\rangle \quad
  \sigma\vdash e_2\Rightarrow v' \quad
  \sigma'\lbrack x\mapsto v'\rbrack\vdash e\Rightarrow v }
{ \sigma\vdash e_1\ e_2\Rightarrow v }
\]

다음은 \(\lambda x.\lambda y.(x + y)\ 1\ 2\)를 계산한 결과가 \(3\)임을 증명하는 증명 나무이다.

\[
\frac
{
  \begin{array}{c}
  {\Large
  \frac
  {
    \begin{array}{c}
    \emptyset\vdash\lambda x.\lambda y.(x+y)\Rightarrow\langle\lambda x.\lambda y.(x+y),\emptyset\rangle \quad
    \emptyset\vdash 1\Rightarrow 1 \\
    \lbrack x\mapsto 1\rbrack\vdash \lambda y.(x+y)\Rightarrow\langle\lambda y.(x+y),\lbrack x\mapsto 1\rbrack\rangle
    \end{array}
  }
  { \emptyset\vdash\lambda x.\lambda y.(x+y)\ 1\Rightarrow\langle\lambda y.(x+y),\lbrack x\mapsto 1\rbrack\rangle }} \quad
  \emptyset\vdash2\Rightarrow 2 \\[6pt]
  {\Large
  \frac
  {
    {\huge
    \frac
    { x\in\mathit{Domain}(\lbrack x\mapsto 1,y\mapsto 2\rbrack) }
    { \lbrack x\mapsto 1,y\mapsto 2\rbrack\vdash x\Rightarrow 1 } \quad
    \frac
    { y\in\mathit{Domain}(\lbrack x\mapsto 1,y\mapsto 2\rbrack) }
    { \lbrack x\mapsto 1,y\mapsto 2\rbrack\vdash y\Rightarrow 2 }
    }
  }
  { \lbrack x\mapsto 1,y\mapsto 2\rbrack\vdash x+y\Rightarrow 3 }
  }
  \end{array}
}
{ \emptyset\vdash\lambda x.\lambda y.(x+y)\ 1\ 2\Rightarrow 3 }
\]

## 인터프리터 구현

다음은 FAE의 요약 문법과 환경을 Scala 코드로 표현한 것이다.

```scala
sealed trait FAE
case class Num(n: Int) extends FAE
case class Add(l: FAE, r: FAE) extends FAE
case class Sub(l: FAE, r: FAE) extends FAE
case class Id(x: String) extends FAE
case class Fun(x: String, b: FAE) extends FAE
case class App(f: FAE, a: FAE) extends FAE

sealed trait FAEV
case class NumV(n: Int) extends FAEV
case class CloV(p: String, b: FAE, e: Env) extends FAEV

type Env = Map[String, FAEV]
```

값이 정수뿐 아니라 클로저도 될 수 있기 때문에, 값을 나타내는 `FAEV` 타입을 정의하였다. `NumV` 타입은 정숫값, `CloV` 타입은 클로저에 해당한다. 환경 역시 값이 정수가 아닌 `FAEV` 타입인 사전이다.

```scala
def lookup(x: String, env: Env): FAEV =
  env.getOrElse(x, throw new Exception)
```

`lookup` 함수는 환경에서 식별자가 가리키는 값을 찾는다.

```scala
def interp(e: FAE, env: Env): FAEV = e match {
  case Num(n) => NumV(n)
  case Add(l, r) =>
    val NumV(n) = interp(l, env)
    val NumV(m) = interp(r, env)
    NumV(n + m)
  case Sub(l, r) =>
    val NumV(n) = interp(l, env)
    val NumV(m) = interp(r, env)
    NumV(n - m)
  case Id(x) => lookup(x, env)
  case Fun(x, b) => CloV(x, b, env)
  case App(f, a) =>
    val CloV(x, b, fEnv) = interp(f, env)
    interp(b, fEnv + (x -> interp(a, env)))
}
```

`Num`인 경우 `NumV` 객체를 만든다. `Add`와 `Sub`인 경우에는 값이 `NumV` 타입인지 확인한 후 정숫값을 꺼내서 계산한 뒤 다시 `NumV` 객체를 만든다. `Id`인 경우는 WAE일 때와 같다. `Fun`인 경우 `CloV` 객체를 만든다. `App`인 경우 함수 부분을 계산해서 클로저를 얻은 뒤 인자를 계산한다. 클로저의 환경에 인자의 값을 추가하고 클로저의 몸통을 계산한다.

`interp`에 \(\lambda x.\lambda y.(x + y)\ 1\ 2\)와 빈 환경을 인자로 넘기면 `NumV(3)`이 결과로 나온다.

```scala
// lambda x.lambda y.(x + y) 1 2
interp(
  App(
    App(
      Fun("x", Fun("y",
        Add(Id("x"), Id("y")))),
      Num(1)
    ),
    Num(2)
  ),
  Map.empty
)
// NumV(3)
```

## 타입 오류

WAE나 F1WAE에서 실행 중에 오류가 발생하는 이유는 자유 식별자뿐이다. 반면, FAE에서는 자유 식별자가 없어도 실행 중에 오류가 발생할 수 있다.

\[1 + \lambda x.x\]

합에 관한 추론 규칙의 전제는 두 식을 계산한 결과가 모두 정수일 것을 요구한다. 위 식은 오른쪽 피연산자 \(\lambda x.x\)를 계산한 결과가 클로저이기에 전제를 만족하지 않는다. 따라서, 어떤 값도 나타내지 않는 식이며, 인터프리터로 실행하면 실행 시간에 오류가 발생한다.

\[1\ 1\]

함수 적용에 관한 추론 규칙의 전제는 첫 식을 계산한 결과가 클로저일 것을 요구한다. 위 식은 첫 식 \(1\)을 계산한 결과가 클로저가 아닌 정수이므로 전제를 만족하지 않는다. 마찬가지로 어떤 값도 나타내지 않는 식이며, 실행 오류를 일으킨다.

위 두 식은 모두 *타입 오류*(type error)를 일으킨다고 이야기할 수 있다. 첫 번째는 정수를 나타내는 식이 와야 할 자리에 함수를 나타내는 식이 와서 오류가 발생하고, 두 번째는 함수를 나타내는 식이 와야 할 자리에 정수를 나타내는 식이 와서 오류가 발생했다. 이처럼 기대하지 않은 타입의 식이 와서 발생한 오류를 타입 오류라고 한다.

타입 오류를 문법적인 방법으로 막기는 어렵다. 덧셈과 뺄셈의 피연산자로 올 수 있는 식을 정수로 제한한다면, \(1 + 1 + 1\) 같은 간단한 식도 문법을 만족하지 않는다. 마찬가지로, 함수 적용의 첫 식으로 올 수 있는 식을 람다 요약으로 제한한다면, 위에서 본 \(\lambda x.\lambda y.(x + y)\ 1\ 2\) 같은 식조차 문법을 만족하지 않는다.

타입 오류를 실행 이전에 방지하는 가장 널리 사용되는 좋은 방법은 *타입 체계*(type system)이다. 타입 체계를 통해서 프로그램을 실행하지 않고도 특정 프로그램이 타입 오류를 절대 발생시키지 않음을 증명할 수 있다. 타입 체계는 프로그램을 실행하기 이전에 코드에 적용되는 의미이기 때문에 *정적 의미*(static semantics)라고도 부른다. 정적 의미와 구분하기 위해서, 지금까지 의미라고 부른, 프로그램을 실행한 결과를 정의하는 의미는 *동적 의미*(dynamic semantics)라고도 부른다. 타입 체계는 이 글의 관심 대상은 아니며, 나중 글에서 자세히 다룰 것이다.

## FAE로 WAE 인코딩 하기

한 언어의 코드를 문법적인 과정을 사용하여 다른 언어의 코드로 변환하여 같은 결과를 내게 만들 수 있다면, 두 번째 언어는 적어도 첫 번째 언어가 표현하는 모든 것을 표현할 수 있다. 코드를 같은 의미를 가진 다른 언어의 코드로 다시 쓰는 것을 *인코딩*(encoding)이라고 한다.

WAE는 FAE로 인코딩 될 수 있다. 따라서, FAE는 WAE가 표현하는 것을 모두 표현할 수 있으며, FAE의 *표현력*(expressivity)은 WAE의 표현력과 같거나 더 높다고 할 수 있다. (물론, FAE의 표현력이 더 높다.) 아래의 \(\mathit{encode}\) 함수는 WAE의 코드를 인자로 받아 FAE의 코드를 결과로 낸다. 즉, WAE를 FAE로 인코딩 하는 함수이다.

\[
\begin{array}{l}
\mathit{encode}(n)=n \\
\mathit{encode}(e_1+e_2)=\mathit{encode}(e_1)+\mathit{encode}(e_2) \\
\mathit{encode}(e_1-e_2)=\mathit{encode}(e_1)-\mathit{encode}(e_2) \\
\mathit{encode}(\textsf{val}\ x = e_1\ \textsf{in}\ e_2)=
\lambda x.\mathit{encode}(e_2)\ \mathit{encode}(e_1) \\
\mathit{encode}(x)=x
\end{array}
\]

대부분 과정은 간단하며, WAE의 지역 변수 선언을 FAE의 람다 요약과 함수 적용을 사용하여 인코딩 하는 부분이 중요하다. 앞으로 나올 예시에서 언어에 정의되지 않은 WAE의 지역 변수 선언이 등장한다면, 이는 예시를 간단하게 만들기 위한 것으로, 실제로는 람다 요약과 함수 적용을 나타낸다고 이해하면 된다.

코드를 간단하게 만들기 위한 목표라는 맥락에서 보았을 때, 복잡한 언어를 단순하지만 표현력이 같거나 더 높은 언어로 인코딩 하는 것은 프로그래머를 위한 여러 문법적 설탕이 존재하는 언어의 코드에서 문법적 설탕을 없애는 과정으로 생각할 수 있다. 문법적 설탕을 없애는 과정을 통해서, 프로그래머에게 편의를 제공함과 동시에 인터프리터나 컴파일러의 구현을 단순하게 만들 수 있다. 또한, 프로그래밍 언어 연구에서도 한 언어를 다른 언어로 인코딩 하여 증명을 단순하게 만들거나, 보이고자 하는 성질을 직접적으로 증명할 필요 없이 이미 증명된 사실을 바로 사용할 수 있다.

인코딩이 올바르다는 것은 간단한 *구조적 귀납법*(structural induction)을 사용하여 증명할 수 있으며, 이 글에서 다루지는 않는다.

## 감사의 말

글을 확인하고 의견을 주신 류석영 교수님께 감사드립니다.
