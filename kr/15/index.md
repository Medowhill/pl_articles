지난 글에서 정의한 BFAE는 수정 가능한 상자를 제공했다. OCaml 같은 일부 함수형 언어는 BFAE처럼 수정 가능한 변수 없이, 생성, 수정, 읽기를 모두 명시적으로 해야 하는 수정 가능한 공간만을 제공한다. 그러나, 많은 프로그래밍 언어에서는 변수의 값을 바로 수정할 수 있다. 이번 글에서는 수정 가능한 변수를 제공하는 MFAE를 정의한다.

수업에서는 수정 가능한 상자와 수정 가능한 변수를 모두 제공하는 BMFAE를 정의한다. 지난 글에서 이미 상자를 다루었기에, MFAE에 상자를 추가하여 BMFAE를 만드는 것은 어렵지 않다. 이 글에서는 변수에 집중하기 위하여 상자를 제외하고 MFAE만 다루니, 직접 BMFAE를 처음부터 끝까지 정의해 보는 것은 좋은 연습이 될 것이다.

## 문법

MFAE는 FAE에서 변수를 수정 가능하게 만든 것이다. FAE의 식에 더해 변수 수정 식을 사용할 수 있다. 다음은 MFAE의 요약 문법으로, FAE와 비교해 추가된 식만 정의하였다.

\[
\begin{array}{lrcl}
\text{Expression} & e & ::= & \cdots \\
&& | & x:=e \\
\end{array}
\]

식 \(x:=e\)는 \(x\)라는 식별자를 이름으로 하는 변수의 값을 \(e\)가 나타내는 값으로 바꾸는 식이다.

MFAE에는 나열식이 없다. 나열식은 람다 요약과 함수 적용을 사용하여 쉽게 인코딩된다.

\[\mathit{encode}(e_1;e_2)=(\lambda \_ . \mathit{encode}(e_2))\ \mathit{encode}(e_1)\]

우변의 식은 \(e_1\)을 먼저 계산한 뒤 \(e_2\)를 계산하며, \(e_1\)의 결과는 버리고 \(e_2\)의 결과를 전체 식의 결과로 한다. 이는 좌변의 나열식을 계산하는 것과 같다. 인코딩 방법이 단순하기에 MFAE에는 굳이 나열식을 포함하지 않았다. 필요하다면 BFAE에서 정의하였듯 나열식을 추가로 정의할 수 있다.

## 의미

BFAE와 마찬가지로 MFAE의 의미도 저장소 전달 방식을 사용한다. 따라서, \(\Rightarrow\)는 환경, 저장소, 식, 값, 저장소의 관계이다.

\[\Rightarrow\subseteq\text{Environment}\times\text{Store}\times\text{Expression}\times\text{Value}\times\text{Store}\]

MFAE의 값은 정수 또는 클로저이다. BFAE에서는 상자가 프로그래머에게 노출되므로 식의 결과가 상자일 수 있어 주소 역시 값이 될 수 있었다. MFAE에서는 상자 없이 변수만 존재하며, 변수를 계산한 결과는 변수가 가리키는 값이지 그 값이 저장된 상자의 주소가 아니기에, 식의 결과가 상자일 수 없다. 따라서, 주소가 값이 될 필요가 없다.

\[
\begin{array}{lrcl}
\text{Value} & v & ::= & n \\
&& | & \langle \lambda x.e,\sigma \rangle
\end{array}
\]

MFAE의 환경은 식별자에서 값으로 가는 부분 함수가 아니라 식별자에서 주소로 가는 부분 함수이다. 환경은 어떤 변수가 있을 때, 그 변수가 나타내는 값을 그 변수의 식별자를 통해 찾기 위해 존재한다. 따라서, 그동안의 수정 불가능한 변수만을 제공하는 언어에서는 환경이 식별자를 받아 값을 내놓으면 충분했다. MFAE에서는 변수가 수정 가능하다. 환경은 식의 계산 결과로 나올 수 없기 때문에, 변수의 값을 환경에 저장하면 값을 수정할 방법이 없다. 변수의 값을 수정하려면, 식의 계산 결과로 나올 수 있는 저장소에 값을 저장해야 한다. 변수의 값이 저장소에 존재하므로, 환경은 변수의 값이 어느 주소에 있는지 알고 있어야 한다. 따라서, 환경은 식별자에서 주소로 가는 부분 함수이다.

\[
\begin{array}{lrcl}
\text{Environment} & \sigma & \in & \textit{Id}\hookrightarrow\text{Address} \\
\text{Store} & M & \in & \textit{Address}\hookrightarrow\text{Value}
\end{array}
\]

변수의 값을 저장소에 저장하면 환경은 필요 없는 것 아닌가 하는 의문을 가질 수 있다. 그러나, 정적 영역을 사용하기 위해서는 환경이 필요하다. 언어의 의미에서 환경을 없애고 저장소를 식별자에서 값으로 가는 부분 함수라고 정의했을 때 어떤 일이 생기는지 보자. 코드를 간결하게 쓰기 위해 나열식을 사용하겠다. \((\lambda x.x:=1)\ 0;x\)라는 식을 생각해 보자. 앞의 함수 적용이 계산되면 변수 \(x\)의 값을 저장하는 상자가 저장소에 만들어진다. 저장소는 다음 식 \(x\)를 계산하는 데에 전달되므로, \(x\)의 값 \(1\)을 저장소에서 찾을 수 있다. 그러나, 정적 영역을 사용하는 언어에서는 \(x\)의 묶는 등장의 영역에 \(x:=1\)만 포함되므로, 위 식을 실행하면 오류가 발생해야 한다. 따라서, 환경 없이 저장소만 사용하여 의미를 정의할 수 없다. 환경은 정적 영역을 위해, 저장소는 수정 가능한 저장 공간을 위해 존재하는 서로 다른 목적을 가진 개념이다.

정수, 합, 차, 람다 요약에 대한 추론 규칙은 BFAE의 추론 규칙과 같다.

\[
\sigma,M\vdash n\Rightarrow n,M
\]

\[
\frac
{ \sigma,M\vdash e_1\Rightarrow n_1,M_1 \quad
  \sigma,M_1\vdash e_2\Rightarrow n_2,M_2 }
{ \sigma,M\vdash e_1+e_2\Rightarrow n_1+n_2,M_2 }
\]

\[
\frac
{ \sigma,M\vdash e_1\Rightarrow n_1,M_1 \quad
  \sigma,M_1\vdash e_2\Rightarrow n_2,M_2 }
{ \sigma,M\vdash e_1-e_2\Rightarrow n_1-n_2,M_2 }
\]

\[
\sigma,M\vdash \lambda x.e\Rightarrow \langle\lambda x.e,\sigma\rangle,M
\]

환경은 주어진 식별자에 해당하는 주소를 알고, 저장소는 주어진 주소에 해당하는 값을 안다. 따라서, 변수의 값을 찾으려면 환경에서 주소를 찾은 뒤 저장소에서 값을 찾아야 한다.

\[
\frac
{ x\in\mathit{Domain}(\sigma) \quad \sigma(x)\in\mathit{Domain}(M) }
{ \sigma,M\vdash x\Rightarrow M(\sigma(x)), M }
\]

저장소는 수정되지 않는다.

함수 적용은 새로운 상자를 만드는 유일한 식이다.

\[
\frac
{ \sigma,M\vdash e_1\Rightarrow \langle\lambda x.e,\sigma'\rangle,M_1 \quad
  \sigma,M_1\vdash e_2\Rightarrow v_1,M_2 \quad
  a\not\in M_2 \quad
  \sigma'\lbrack x\mapsto a\rbrack,M_2\lbrack a\mapsto v_1\rbrack\vdash e\Rightarrow v_2,M_3 }
{ \sigma,M\vdash e_1\ e_2\Rightarrow v_2,M_3 }
\]

인자를 계산하여 값을 얻은 후 상자를 만들고 그 값을 상자에 넣는다. 상자는 저장소에 추가되고, 환경에는 클로저의 매개변수 이름과 상자의 주소 사이의 관계가 추가된다.

변수 수정은 BFAE의 상자 수정과 유사하다. BFAE에서 수정식의 좌변을 계산한 값이 주소라면, MFAE에서는 수정식의 좌변은 변수이며, 변수의 주소는 환경에 저장되어 있다.

\[
\frac
{ x\in\mathit{Domain}(\sigma) \quad
  \sigma,M\vdash e\Rightarrow v,M' }
{ \sigma,M\vdash x:=e\Rightarrow v,M'\lbrack \sigma(x)\mapsto v\rbrack }
\]

우변을 계산하여 값과 저장소를 얻고, 그 저장소에서 변수의 주소가 가리키는 값을 얻은 값으로 바꾼다.

## 인터프리터 구현

다음은 MFAE의 요약 문법, 환경, 저장소를 Scala 코드로 표현한 것이다.

```scala
sealed trait MFAE
case class Num(n: Int) extends MFAE
case class Add(l: MFAE, r: MFAE) extends MFAE
case class Sub(l: MFAE, r: MFAE) extends MFAE
case class Id(x: String) extends MFAE
case class Fun(x: String, b: MFAE) extends MFAE
case class App(f: MFAE, a: MFAE) extends MFAE
case class Set(x: String, e: MFAE) extends MFAE

sealed trait MFAEV
case class NumV(n: Int) extends MFAEV
case class CloV(p: String, b: MFAE, var e: Env) extends MFAEV

type Env = Map[String, Addr]
def lookup(x: String, env: Env): Addr =
  env.getOrElse(x, throw new Exception)

type Addr = Int
type Sto = Map[Addr, MFAEV]
def storeLookup(a: Addr, sto: Sto): MFAEV =
  sto.getOrElse(a, throw new Exception)
def malloc(sto: Sto): Addr =
  sto.keys.maxOption.getOrElse(0) + 1
```

`Set`은 변수 수정에 해당한다.

`interp` 함수의 `Num`, `Add`, `Sub`, `Fun` 경우는 BFAE의 인터프리터를 구현할 때와 같다.

```scala
def interp(e: MFAE, env: Env, sto: Sto): (MFAEV, Sto) = e match {
  case Num(n) => (NumV(n), sto)
  case Add(l, r) =>
    val (NumV(n), ls) = interp(l, env, sto)
    val (NumV(m), rs) = interp(r, env, ls)
    (NumV(n + m), rs)
  case Sub(l, r) =>
    val (NumV(n), ls) = interp(l, env, sto)
    val (NumV(m), rs) = interp(r, env, ls)
    (NumV(n - m), rs)
  case Fun(x, b) => (CloV(x, b, env), sto)
```

`Id`인 경우 `lookup`과 `storeLookup`을 모두 호출한다.

```scala
  case Id(x) => (storeLookup(lookup(x, env), sto), sto)
```

`App`인 경우 새로운 주소를 찾기 위해 `malloc`을 호출한다.

```scala
  case App(f, a) =>
    val (CloV(x, b, fEnv), ls) = interp(f, env, sto)
    val (v, rs) = interp(a, env, ls)
    val addr = malloc(rs)
    interp(b, fEnv + (x -> addr), rs + (addr -> v))
```

`Set`인 경우 `lookup`으로 변수의 주소를 찾고 주소의 값을 덮어쓴다.

```scala
  case Set(x, e) =>
    val (v, s) = interp(e, env, sto)
    (v, s + (lookup(x, env) -> v))
}
```

아래에서 전체 코드를 한 번에 볼 수 있다.

<details><summary>전체 코드 보기</summary>

```scala
sealed trait MFAE
case class Num(n: Int) extends MFAE
case class Add(l: MFAE, r: MFAE) extends MFAE
case class Sub(l: MFAE, r: MFAE) extends MFAE
case class Id(x: String) extends MFAE
case class Fun(x: String, b: MFAE) extends MFAE
case class App(f: MFAE, a: MFAE) extends MFAE
case class Set(x: String, e: MFAE) extends MFAE

sealed trait MFAEV
case class NumV(n: Int) extends MFAEV
case class CloV(p: String, b: MFAE, var e: Env) extends MFAEV

type Env = Map[String, Addr]
def lookup(x: String, env: Env): Addr =
  env.getOrElse(x, throw new Exception)

type Addr = Int
type Sto = Map[Addr, MFAEV]
def storeLookup(a: Addr, sto: Sto): MFAEV =
  sto.getOrElse(a, throw new Exception)
def malloc(sto: Sto): Addr =
  sto.keys.maxOption.getOrElse(0) + 1

def interp(e: MFAE, env: Env, sto: Sto): (MFAEV, Sto) = e match {
  case Num(n) => (NumV(n), sto)
  case Add(l, r) =>
    val (NumV(n), ls) = interp(l, env, sto)
    val (NumV(m), rs) = interp(r, env, ls)
    (NumV(n + m), rs)
  case Sub(l, r) =>
    val (NumV(n), ls) = interp(l, env, sto)
    val (NumV(m), rs) = interp(r, env, ls)
    (NumV(n - m), rs)
  case Fun(x, b) => (CloV(x, b, env), sto)
  case Id(x) => (storeLookup(lookup(x, env), sto), sto)
  case App(f, a) =>
    val (CloV(x, b, fEnv), ls) = interp(f, env, sto)
    val (v, rs) = interp(a, env, ls)
    val addr = malloc(rs)
    interp(b, fEnv + (x -> addr), rs + (addr -> v))
  case Set(x, e) =>
    val (v, s) = interp(e, env, sto)
    (v, s + (lookup(x, env) -> v))
}
```

</details>

다음 코드는 `interp` 함수를 사용하여 \( (\lambda x.x+(x:=1)+x)\ 0\)을 계산한 것이다. 덧셈에서 첫 \(x\)는 \(0\), 둘째 \(x\)는 \(1\)이며, 그 사이의 식의 결과도 \(1\)이므로 전체 결과는 \(2\)이다. 저장소의 상자에 \(1\)이 들어 있는 것도 확인할 수 있다.

```scala
// (lambda x.x+(x:=1)+x) 0
interp(
  App(
    Fun("x",
      Add(Add(
        Id("x"),
        Set("x", Num(1))),
        Id("x")
      )
    ),
    Num(0)
  ),
  Map.empty,
  Map.empty
)
// (NumV(2), Map(1 -> NumV(1)))
```

## 계산 전략

함수 호출 시 인자를 함수에 전달하는 방법을 *계산 전략*(evaluation strategy)이라 부른다. 계산 전략은 언어마다 다르다. 한 언어에서 여러 가지 계산 전략을 사용자에게 제공할 수도 있다.

MFAE를 포함한 지금까지 다룬 모든 언어는 *값에 의한 호출*(call by value)을 계산 전략으로 사용한다. 값에 의한 호출은 함수 호출 시에 인자의 값을 계산하여 값을 함수에 전달하는 계산 전략이다. 지금까지의 언어들은 언제나 인자를 계산하여 값을 얻고, 그 값을 환경 또는 저장소에 추가하였다.

다른 계산 전략으로는 *참조에 의한 호출*(call by reference)이 있다. 참조에 의한 호출의 경우, 인자가 변수라면, 변수의 값 대신 주소를 전달한다. 다음 추론 규칙은 참조에 의한 호출을 하는 MFAE의 함수 적용의 의미를 정의한다.

\[
\frac
{ \sigma,M\vdash e\Rightarrow \langle\lambda x'.e',\sigma'\rangle,M_1 \quad
  x\in\mathit{Domain}(\sigma) \quad
  \sigma'\lbrack x'\mapsto \sigma(x)\rbrack,M_1\vdash e'\Rightarrow v,M_2 }
{ \sigma,M\vdash e\ x\Rightarrow v,M_2 }
\]

\[
\frac
{ \sigma,M\vdash e_1\Rightarrow \langle\lambda x.e,\sigma'\rangle,M_1 \quad
  e_2\not\in\text{Variable} \quad
  \sigma,M_1\vdash e_2\Rightarrow v_1,M_2 \quad
  a\not\in M_2 \quad
  \sigma'\lbrack x\mapsto a\rbrack,M_2\lbrack a\mapsto v_1\rbrack\vdash e\Rightarrow v_2,M_3 }
{ \sigma,M\vdash e_1\ e_2\Rightarrow v_2,M_3 }
\]

첫 추론 규칙은 인자가 변수인 경우, 두 번째 추론 규칙은 인자가 변수가 아닌 식인 경우이다. 변수가 인자라면 새로운 상자를 만들 필요 없이, 환경에 매개변수가 변수의 주소를 가리킨다는 것만 추가하면 된다. 클로저의 몸통에서 매개변수를 사용하면, 그 매개변수의 주소가 인자로 사용된 변수의 주소와 같다. 따라서, 이는 참조에 의한 호출이다. 반면, 변수가 인자가 아닌 식인 경우, 식이 나타내는 값이 어떤 변수의 주소가 가리키는 값이라는 보장이 없으므로 값에 의한 호출을 한다.

인터프리터는 다음과 같이 수정된다.

```scala
  case App(f, a) =>
    val (CloV(x, b, fEnv), ls) = interp(f, env, sto)
    a match {
      case Id(y) =>
        interp(b, fEnv + (x -> lookup(y, env)), ls)
      case _ =>
        val (v, rs) = interp(a, env, ls)
        val addr = malloc(rs)
        interp(b, fEnv + (x -> addr), rs + (addr -> v))
    }
```

참조에 의한 호출의 대표적인 예시는 C++에서 *앰퍼샌드*(ampersand)를 붙인 매개변수이다. 매개변수의 이름에 앰퍼샌드가 붙어있으면, 그 매개변수는 참조에 의한 호출을 사용한다.

```c++
#include <iostream>

void f(int &);

int main() {
    int x = 1;
    f(x);
    std::cout << x << std::endl;
}

void f(int &x) {
    x = 2;
}
```

위 코드를 실행하면 `2`가 출력된다.

그밖에도, 포인터를 인자로 사용한다면 사용자가 명시적으로 참조를 전달하여 참조에 의한 호출을 흉내 낸 것이다. 또, Java나 Scala에서는 기본 타입을 제외한 클래스를 통해 만들어진 모든 참조 타입의 값은 함수나 메서드 호출 시에 값이 복사되는 대신 참조가 전달되므로, 참조에 의한 호출이라 볼 수 있다. 다른 여러 객체 지향 언어에서도 객체를 참조에 의한 호출로 전달한다. 다만, 변수 자체의 주소를 전달한 것이 아니라, 객체의 주소를 전달한 것이므로, 위에서 설명한 C++이나 MFAE의 참조에 의한 호출과는 차이가 존재하며, 구분하기 위해서 *공유에 의한 호출*(call by sharing)이라고 부르기도 한다. 그러나, 공유에 의한 호출은 널리 쓰이는 말은 아니며, 모두 구분 없이 참조에 의한 호출이라고 부르는 경우가 많다.

계산 전략에는 *이름에 의한 호출*(call by name)과 *요구에 의한 호출*(call by need) 등도 있으며, 다음 글에서 이 두 계산 전략에 대해 다룬다.

## 감사의 말

글을 확인하고 의견을 주신 류석영 교수님께 감사드립니다.
