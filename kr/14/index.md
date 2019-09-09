이번 글에서는 수정 가능한 저장 공간인 *상자*(box)를 제공하는 BFAE를 정의한다.

## 문법

다음은 BFAE의 요약 문법이다. FAE와 비교하여 추가된 부분만 정의하였다.

\[
\begin{array}{lrcl}
\text{Expression} & e & ::= & \cdots \\
&& | & \textsf{ref}\ e \\
&& | & e:=e \\
&& | & !e \\
&& | & e;e
\end{array}
\]

\(\textsf{ref}\)는 상자를 만든다. \(\textsf{ref}\ e\)는 \(e\)를 계산하고 새로운 상자를 만든 후 상자에 \(e\)의 계산 결과를 넣는다. 전체 식이 나타내는 값은 만들어진 상자이다. *저급*(low level)으로 해석하면 상자가 존재하는 메모리 주소이다. 이는 사용자가 사용할 수 있는 공간을 메모리에 할당한다는 점에서 C의 `malloc`이나 객체지향언어의 `new`와 유사하다. 예를 들면, \(\textsf{ref}\ 1\)은 `int *p = (int *) malloc(sizeof(int)); *p = 1; return p;`로 이해할 수 있다.

\(:=\)는 상자를 수정한다. \(e_1:=e_2\)는 \(e_1\)과 \(e_2\)를 차례로 계산한 뒤, \(e_1\)이 상자를 나타낸다면, 그 상자에 \(e_2\)가 나타내는 값을 넣는 식이다. 전체 식의 값은 \(e_2\)를 계산한 값과 같다. 메모리에 존재하는 값을 직접적으로 수정하는 식으로, C에서 대입을 할 때 좌변이 단순한 변수가 아닌 포인터 *역참조*(dereference)인 경우와 유사하다. 예를 들어, 위 문단의 식을 계산한 결과를 변수 \(x\)가 나타낸다면, \(x:=2\)는 `*x = 2`로 생각할 수 있다.

\(!\)는 상자를 연다. \(e\)를 계산한 결과가 상자면, 상자가 가지고 있는 값이 \(!e\)의 값이다. C에서 포인터 역참조가 대입 문의 좌변이 아닌 곳에 등장하는 상황과 같다. 즉, \(!x\)는 `*x`와 같다.

\(e_1;e_2\)는 두 식을 차례로 *나열*(sequencing)하여 나온 한 식으로, \(e_1\)과 \(e_2\)를 차례로 계산하며, 전체 결과는 \(e_2\)의 계산 결과이다. 수정 가능한 상자가 없는 지난 글까지의 언어에서는 나열식이 존재해도 앞의 식의 계산 결과가 버려지므로 의미가 없다. 그러나, BFAE에서는 앞의 식이 상자를 새로 만들거나 상자의 값을 수정함으로써 최종 결과는 버려져도 의미 있는 계산을 할 수 있다. 여러 언어에서 쌍반점이나 줄 바꿈으로 구분되는 여러 개의 문을 차례로 작성하는 것을 허용하며, 나열식은 이와 비슷하다.

## 의미

지금까지 다룬 언어들과 달리, BFAE는 순수한 함수형 언어가 아니며, 수정 가능한 저장 공간을 제공한다. 명령형 언어처럼, BFAE에는 프로그램을 실행하면서 변화하는 상태가 존재한다. 그러나, 여전히 한 프로그램은 하나의 식이며, 그 식이 나타내는 값을 구하는 것이 프로그램을 실행하는 것이라는 함수형 시각은 유지할 수 있다.

BFAE의 의미를 정의하려면, 먼저 수정 가능한 저장 공간을 정의해야 한다. 이 글에서는 이를 *저장소*(store)라고 부른다. 저장소는 프로그램에 존재하는 상자가 가지고 있는 값을 저장한다. 각 상자를 구분하기 위해서, 상자에 서로 다른 이름을 줄 수 있으며, 그 이름을 *주소*(address)라고 한다. \(\mathit{Addr}\)이 가능한 모든 주소의 집합이라고 하자. 저장소는 주소에서 값으로 가는 부분 함수이다. 상자가 가지고 있는 값을 찾으려면, 상자의 주소가 저장소에서 어떤 값을 가리키는지 보면 된다.

\[
\begin{array}{lrcl}
\text{Address} & a & \in & \mathit{Addr} \\
\text{Store} & M & \in & \text{Address}\hookrightarrow\text{Value}
\end{array}
\]

메타변수 \(a\)는 주소를, \(M\)은 저장소를 나타낸다.

의미를 정의할 때는 상자라는 개념을 구체적으로 정의할 필요가 없다. 상자가 나타내는 것은 그 상자의 주소이므로, 주소만으로도 의미를 충분히 정의할 수 있다. 상자를 나타내는 식을 계산한 결과는 주소이다. 즉, \(\textsf{ref}\ e\)의 결괏값은 어떤 주소이다. 이전 언어들에서는 정수와 클로저만 값이 될 수 있었으므로, 주소도 값이 될 수 있도록 값의 정의를 바꿔야 한다.

\[
\begin{array}{lrcl}
\text{Value} & e & ::= & \cdots \\
&& | & a
\end{array}
\]

단, 의미에 직접적으로 상자가 드러나지 않더라도, 사용자의 시각에서는 상자라는 개념이 존재하며, 상자가 더 직관적인 개념이므로, 아래의 설명에서도 계속 상자라는 표현을 쓸 것이다.

식 \(!e\)를 계산하기 위해서는 환경뿐 아니라 저장소가 필요하다. \(e\)가 상자를 나타낸다면 그 상자가 가지고 있는 값을 저장소가 알기 때문이다. 따라서, 임의의 식을 계산할 때 저장소가 필요하며, \(\Rightarrow\)는 환경, 식, 값의 관계가 아닌 환경, 저장소, 식, 값의 관계이다.

식 \(\textsf{ref}\ e\)와 \(e_1:=e_2\)는 상자를 새로 만들거나 상자가 가지는 값을 바꾸기에, 저장소를 수정한다. 저장소의 수정은 환경에 새로운 변수를 추가하는 것과는 다르다. \(\textsf{val}\ x=e_1\ \textsf{in}\ e_2\)를 통해서 환경에 \(x\)를 추가한다면, 이 묶는 등장의 영역은 \(e_2\)뿐이므로, \(e_2\)를 계산할 때만 수정된 환경을 사용하면 되고, 전체 식이 어떤 다른 식의 부분식이더라도 다른 어떤 곳에서도 수정된 환경이 필요하지 않다. 예를 들어, 위 식을 \(e\)라 할 때, \(e+e'\)을 계산한다면, \(e'\)을 계산할 때도 합을 계산할 때도 수정된 환경은 필요 없다. 반대로, 상자를 새로 만들거나 상자의 값을 바꿀 때는 수정된 저장소가 부분식을 계산하는 과정에서는 필요하지 않고, 오히려 프로그램의 다른 부분에서 수정된 저장소가 필요하다. 예를 들면, \(x:=2;!x\)라는 식을 계산하면, \(x\)가 가리키는 상자의 값이 \(2\)로 수정되었다는 사실이 \(!x\)를 계산할 때 필요하다. 따라서, 환경과는 다르게 저장소는 식을 계산한 후에 어떻게 바뀌었는지가 중요하며, 그 식을 부분식으로 가지는 다른 식이 수정된 저장소를 받아 다른 부분식을 계산할 때 사용해야 한다. 그러므로, BFAE의 의미는 식이 나타내는 값과 함께 수정된 저장소도 나타내야 하기에, \(\Rightarrow\)는 환경, 저장소, 식, 값, 저장소의 관계이다. 첫 저장소는 식을 계산할 때 사용되는 저장소, 둘째 저장소는 식을 계산하여 나온 저장소이다.

\[\Rightarrow\subseteq\text{Environment}\times\text{Store}\times\text{Expression}\times\text{Value}\times\text{Store}\]

\(\sigma,M\vdash e\Rightarrow v,M'\)은 환경 \(\sigma\)와 저장소 \(M\) 아래에서 식 \(e\)를 계산하면 결괏값은 \(v\)이고 저장소 \(M'\)을 얻는다는 뜻이다. 이렇게 의미를 정의하는 것을 *저장소 전달 방식*(store passing style)이라고 한다. 저장소 전달 방식을 사용함으로써, 언어를 정의하는 과정에는 수정 불가능한 개념만 사용하면서도 언어에는 수정 가능한 상자를 추가할 수 있다.

어떤 식을 계산할 때, 부분식을 계산하면서 저장소가 수정될 수 있기에, 부분식을 계산하는 순서가 중요하다. 변수 \(x\)가 어떤 상자를 나타내며 상자에는 \(1\)이 들었다고 가정하자. \((x:=2)+(!x)\)라는 식을 계산할 때, \(x:=2\)를 먼저 계산한다면, \(!x\)가 \(2\)이므로, 전체 식이 나타내는 값은 \(4\)이다. 그러나, \(!x\)를 먼저 계산하면, \(!x\)가 \(1\)이므로, 전체 식이 나타내는 값은 \(3\)이다. 추론 규칙을 작성할 때, 저장소를 전달하면서 부분식을 계산하는 순서를 자연스럽게 지정할 수 있다.

정수, 변수, 람다 요약에 대한 추론 규칙은 FAE에서와 같다. 다만 저장소가 추가된다. 세 식은 저장소를 수정하지 않는다.

\[
\sigma,M\vdash n\Rightarrow n,M
\]

\[
\frac
{ x\in\mathit{Domain}(\sigma) }
{ \sigma,M\vdash x\Rightarrow \sigma(x),M }
\]

\[
\sigma,M\vdash \lambda x.e\Rightarrow \langle\lambda x.e,\sigma\rangle,M
\]

나열식은 그 자체로는 저장소를 수정하지 않지만, 부분식은 저장소를 수정할 수 있다. 왼쪽 부분식을 계산한 뒤 오른쪽 부분식을 계산하며, 왼쪽 부분식을 계산할 때 저장소가 수정되었다면, 오른쪽 부분식의 계산에 수정된 내용이 고려된다.

\[
\frac
{ \sigma,M\vdash e_1\Rightarrow v_1,M_1 \quad
  \sigma,M_1\vdash e_2\Rightarrow v_2,M_2 }
{ \sigma,M\vdash e_1;e_2\Rightarrow v_2,M_2 }
\]

왼쪽 부분식을 계산하여 얻은 저장소를 오른쪽 부분식을 계산하는 데 사용한다. 또한, 왼쪽 부분식의 결괏값은 어디에도 사용되지 않고 버려지며, 오른쪽 부분식의 결괏값이 전체 나열식의 결괏값이다.

합, 차, 함수 적용은 나열식과 마찬가지로 그 식 자체는 저장소를 수정하지 않지만, 부분식이 저장소를 수정할 수 있다. 나열식에서처럼 왼쪽 부분식을 계산한 다음 오른쪽 부분식을 계산한다. 이 계산 순서는 BFAE의 의미이며, 언어에 따라 계산 순서는 왼쪽에서 오른쪽이 아닐 수 있다.

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
\frac
{ \sigma,M\vdash e_1\Rightarrow \langle\lambda x.e,\sigma'\rangle,M_1 \quad
  \sigma,M_1\vdash e_2\Rightarrow v_1,M_2 \quad
  \sigma'\lbrack x\mapsto v_1\rbrack,M_2\vdash e\Rightarrow v_2,M_3 }
{ \sigma,M\vdash e_1\ e_2\Rightarrow v_2,M_3 }
\]

함수 적용의 경우, 함수 몸통을 계산할 때도 저장소가 수정될 수 있다.

남아있는 상자 생성, 상자 수정, 상자 열기 식은 모두 저장소를 직접 수정하거나 읽는다.

\[
\frac
{ \sigma,M\vdash e\Rightarrow v,M' \quad
  a\not\in \mathit{Domain}(M') }
{ \sigma,M\vdash \textsf{ref}\ e\Rightarrow a,M'\lbrack a\mapsto v\rbrack }
\]

상자 생성은 부분식을 계산한 뒤, 저장소의 정의역에 들어 있지 않은 주소를 찾아 그 주소가 부분식의 결괏값을 가리킨다는 정보를 저장소에 추가한다. 전체 결과는 그 주소이다.

\[
\frac
{ \sigma,M\vdash e_1\Rightarrow a,M_1 \quad
  \sigma,M_1\vdash e_2\Rightarrow v,M_2 }
{ \sigma,M\vdash e_1:=e_2\Rightarrow v,M_2\lbrack a\mapsto v\rbrack }
\]

상자 수정 역시 왼쪽부터 계산한다. 왼쪽 부분식의 결과가 주소이면, 그 주소가 오른쪽 부분식의 결괏값을 가리킨다는 정보를 저장소에 넣는다. 전체 결과는 오른쪽 부분식의 결괏값이다.

\[
\frac
{ \sigma,M\vdash e\Rightarrow a,M' \quad
  a\in \mathit{Domain}(M') }
{ \sigma,M\vdash !e\Rightarrow M'(a),M' }
\]

상자 열기는 부분식을 계산한 결과가 주소이면, 그 주소가 가리키는 값을 저장소에서 찾아 결과로 낸다. 주의할 점은 처음 주어진 저장소 대신 부분식을 계산하여 나온 저장소를 사용해야 한다는 점이다. 예를 들면, \(!(\textsf{ref}\ 1)\)이 올바르게 \(1\)이라는 결과를 내기 위해서는, \(\textsf{ref}\ 1\)을 계산하여 얻은 저장소를 사용해야만 한다.

## 인터프리터 구현

다음은 BFAE의 요약 문법, 환경, 저장소를 Scala 코드로 표현한 것이다.

```scala
sealed trait BFAE
case class Num(n: Int) extends BFAE
case class Add(l: BFAE, r: BFAE) extends BFAE
case class Sub(l: BFAE, r: BFAE) extends BFAE
case class Id(x: String) extends BFAE
case class Fun(x: String, b: BFAE) extends BFAE
case class App(f: BFAE, a: BFAE) extends BFAE
case class NewBox(e: BFAE) extends BFAE
case class SetBox(b: BFAE, e: BFAE) extends BFAE
case class OpenBox(b: BFAE) extends BFAE
case class Seqn(l: BFAE, r: BFAE) extends BFAE

sealed trait BFAEV
case class NumV(n: Int) extends BFAEV
case class CloV(p: String, b: BFAE, var e: Env) extends BFAEV
case class BoxV(a: Addr) extends BFAEV

type Env = Map[String, BFAEV]
def lookup(x: String, env: Env): BFAEV =
  env.getOrElse(x, throw new Exception)

type Addr = Int
type Sto = Map[Addr, BFAEV]
def storeLookup(a: Addr, sto: Sto): BFAEV =
  sto.getOrElse(a, throw new Exception)
def malloc(sto: Sto): Addr =
  sto.keys.maxOption.getOrElse(0) + 1
```

`NewBox`는 상자 생성, `SetBox`는 상자 수정, `OpenBox`는 상자 열기, `Seqn`은 나열식에 해당한다. `BoxV`는 값이 주소인 경우이다. `Addr`은 주소의 타입으로 여기서는 간단하게 `Int`를 사용한다. `Sto`는 저장소의 타입으로 열쇠 타입이 `Addr`이고 값 타입이 `BFAEV`인 사전이다. `lookup`은 주어진 환경에서 값을 찾고, `storeLookup`은 주어진 저장소에서 값을 찾는다. `malloc`은 주어진 저장소에서 사용 중이지 않은 주소를 찾는 함수이다.

`interp` 함수는 식, 환경, 저장소를 인자로 받고 값과 저장소의 순서쌍을 결과로 낸다.

```scala
def interp(e: BFAE, env: Env, sto: Sto): (BFAEV, Sto) = e match { ... }
```

추론 규칙을 설명한 순서에 맞춰 패턴 대조의 각 경우를 보겠다.

```scala
case Num(n) => (NumV(n), sto)
case Id(x) => (lookup(x, env), sto)
case Fun(x, b) => (CloV(x, b, env), sto)
```

`Num`, `Id`, `Fun` 경우는 인자로 받은 저장소를 그대로 결과로 사용한다.

```scala
case Seqn(l, r) =>
  val (_, ls) = interp(l, env, sto)
  interp(r, env, ls)
case Add(l, r) =>
  val (NumV(n), ls) = interp(l, env, sto)
  val (NumV(m), rs) = interp(r, env, ls)
  (NumV(n + m), rs)
case Sub(l, r) =>
  val (NumV(n), ls) = interp(l, env, sto)
  val (NumV(m), rs) = interp(r, env, ls)
  (NumV(n - m), rs)
case App(f, a) =>
  val (CloV(x, b, fEnv), ls) = interp(f, env, sto)
  val (v, rs) = interp(a, env, ls)
  interp(b, fEnv + (x -> v), rs)
```

`Seqn`, `Add`, `Sub`, `App`은 직접 저장소를 수정하거나 읽지는 않으나, 재귀 호출의 결과로 나온 저장소를 다른 재귀 호출 시에 전달하거나 최종 결과로 사용한다.

```scala
case NewBox(e) =>
  val (v, s) = interp(e, env, sto)
  val a = malloc(s)
  (BoxV(a), s + (a -> v))
```

`NewBox`는 부분식의 값을 계산한 뒤 `malloc`을 호출하여 사용되지 않는 주소를 구한다. 주소와 상자를 추가한 저장소를 결과로 낸다.

```scala
case SetBox(b, e) =>
  val (BoxV(a), bs) = interp(b, env, sto)
  val (v, es) = interp(e, env, bs)
  (v, es + (a -> v))
```

`SetBox`는 두 부분식을 차례로 계산한 뒤 저장소에서 첫 부분식이 나타내는 주소가 가리키는 값을 수정한다. 결과는 둘째 부분식의 계산 결과와 수정된 저장소이다.

```scala
case OpenBox(e) =>
  val (BoxV(a), s) = interp(e, env, sto)
  (storeLookup(a, s), s)
```

`OpenBox`는 부분식을 계산하여 얻은 주소에 해당하는 값을 저장소에서 찾는다. 저장소는 수정되지 않는다.

모든 경우에 대해 코드를 작성하였다. 아래에서 전체 코드를 한 번에 볼 수 있다.

<details><summary>전체 코드 보기</summary>
```scala
sealed trait BFAE
case class Num(n: Int) extends BFAE
case class Add(l: BFAE, r: BFAE) extends BFAE
case class Sub(l: BFAE, r: BFAE) extends BFAE
case class Id(x: String) extends BFAE
case class Fun(x: String, b: BFAE) extends BFAE
case class App(f: BFAE, a: BFAE) extends BFAE
case class NewBox(e: BFAE) extends BFAE
case class SetBox(b: BFAE, e: BFAE) extends BFAE
case class OpenBox(b: BFAE) extends BFAE
case class Seqn(l: BFAE, r: BFAE) extends BFAE

sealed trait BFAEV
case class NumV(n: Int) extends BFAEV
case class CloV(p: String, b: BFAE, var e: Env) extends BFAEV
case class BoxV(a: Addr) extends BFAEV

type Env = Map[String, BFAEV]
def lookup(x: String, env: Env): BFAEV =
  env.getOrElse(x, throw new Exception)

type Addr = Int
type Sto = Map[Addr, BFAEV]
def storeLookup(a: Addr, sto: Sto): BFAEV =
  sto.getOrElse(a, throw new Exception)
def malloc(sto: Sto): Addr =
  sto.keys.maxOption.getOrElse(0) + 1

def interp(e: BFAE, env: Env, sto: Sto): (BFAEV, Sto) = e match {
  case Num(n) => (NumV(n), sto)
  case Id(x) => (lookup(x, env), sto)
  case Fun(x, b) => (CloV(x, b, env), sto)
  case Seqn(l, r) =>
    val (_, ls) = interp(l, env, sto)
    interp(r, env, ls)
  case Add(l, r) =>
    val (NumV(n), ls) = interp(l, env, sto)
    val (NumV(m), rs) = interp(r, env, ls)
    (NumV(n + m), rs)
  case Sub(l, r) =>
    val (NumV(n), ls) = interp(l, env, sto)
    val (NumV(m), rs) = interp(r, env, ls)
    (NumV(n - m), rs)
  case App(f, a) =>
    val (CloV(x, b, fEnv), ls) = interp(f, env, sto)
    val (v, rs) = interp(a, env, ls)
    interp(b, fEnv + (x -> v), rs)
  case NewBox(e) =>
    val (v, s) = interp(e, env, sto)
    val a = malloc(s)
    (BoxV(a), s + (a -> v))
  case SetBox(b, e) =>
    val (BoxV(a), bs) = interp(b, env, sto)
    val (v, es) = interp(e, env, bs)
    (v, es + (a -> v))
  case OpenBox(e) =>
    val (BoxV(a), s) = interp(e, env, sto)
    (storeLookup(a, s), s)
}
```
</details>

다음 코드는 `interp` 함수를 사용하여 \( (\lambda x.(x:=1);!x)\ (\textsf{ref}\ 2) \)를 계산한 것으로, 결과가 올바르게 \(1\)이 나오며, 저장소에는 최종적으로 하나의 상자가 만들어지고 그 상자에 \(1\)이 들어 있는 것을 확인할 수 있다.

```scala
// (lambda x.(x:=1); !x) (ref 2)
interp(
  App(
    Fun("x",
      Seqn(
        SetBox(Id("x"), Num(1)),
        OpenBox(Id("x"))
      )
    ),
    NewBox(Num(2))
  ),
  Map.empty,
  Map.empty
)
// (NumV(1), Map(1 -> NumV(1)))
```

## 감사의 말

글을 확인하고 의견을 주신 류석영 교수님께 감사드립니다.
