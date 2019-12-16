이번 글은 *소극적 계산*(lazy evaluation)에 대한 글이다. 소극적 계산[^1]은 식의 계산을 계산 결과가 필요할 때까지 늦추는 것을 말한다. 실용적으로 사용되는 언어는 대개 함수 적용 이외에도 다른 여러 기능을 제공하므로 계산을 늦추는 대상이 다양할 수 있다. 예를 들어, Scala에서는 변수가 가지는 값을 소극적으로 계산할 수 있도록 `lazy`라는 키워드를 제공하며, `lazy val`을 사용하여 선언된 변수는 변수가 선언될 때 변수가 나타내는 값이 계산되는 것이 아니라 그 변수를 다른 식에서 사용할 때 변수가 나타내는 값이 계산된다. 그러나 함수 적용에서 인자가 언제 계산되는지 살펴보는 것만으로 소극적 계산에 대해 알아보기에 충분하며 인자가 아닌 대상에 소극적 계산을 사용하면 어떻게 될지 이해하는 데 문제가 없으므로 이 글에서는 함수 적용에만 초점을 두고 소극적 계산에 대해 다룰 것이다.

[^1]: 소극적 계산을 뒤에서 다룰 이름에 의한 호출과 같은 뜻으로 사용하는 사람도 있으나, 이 글에서는 이름에 의한 호출과 필요에 의한 호출 등을 모두 포함하는 큰 뜻으로 사용한다.

지금까지 글에서 다룬 언어는 모두 *적극적 계산*(eager evaluation; strict evaluation)을 사용하였다. 적극적 계산은 함수 호출 시 인자가 나타내는 값을 먼저 계산하고 그 뒤에 함수 몸통을 계산하는 방법을 말한다. 지난 글에서 본 값에 의한 호출이 적극적 계산에 해당한다. 참조에 의한 호출은 엄밀히 이야기하면 계산 순서에 따른 구분이 아니라 값 대신 주소를 넘기는 것을 의미하므로 소극적 계산인지 적극적 계산인지 따지는 것이 큰 의미는 없다. 다만 소극적 계산은 계산을 미룬다는 점이 핵심인데 참조에 의한 호출은 계산을 미루는 의도는 없기에 일반적으로는 적극적 계산이라고 말해도 문제가 없다.

반면 소극적 계산은 함수 호출 시에 인자가 나타내는 값을 먼저 계산하지 않는다. 인자 대신 바로 함수 몸통을 계산하며 함수 몸통을 계산하다 매개변수가 사용되어 인자의 값이 필요해지는 시점에 인자의 값을 계산한다. 다음의 Scala 코드를 생각해 보자.

```scala
def f(): Int = ...
def g(): Boolean = ...
def h(x: Int, b: Boolean): Int =
  if (b) x else 0

h(f(), g())
```

함수 `h`는 매개변수 `x`와 `b`를 가지지만 `b`의 값이 `false`인 경우에는 `x`를 사용하지 않는다. `b`가 `false`라면 `x`의 값이 무엇이든 함수의 결괏값은 `0`이다. 마지막 줄에서는 `h`를 호출하면서 첫 인자로 `f()`를 두 번째 인자로 `g()`를 사용하였다. `g()`를 계산한 결과가 `true`라면 `f()`를 계산하지 않고는 결괏값을 알 수 없기에 반드시 `f()`를 계산해야 한다. 그러나 `g()`가 `false`라면 `f()`를 계산하지 않고도 결괏값은 `0`임을 알 수 있다. Scala에서는 기본적으로 적극적 계산을 사용하기에 `f()`의 결괏값이 필요하지 않을 때도 `f()`를 계산하므로 비효율적인 코드가 된다. 만약 `f()`의 계산이 오래 걸린다면 실사용에 문제가 될 수도 있다. 만약 소극적 계산을 사용한다면 인자의 값이 필요해지기 전에는 인자의 값을 계산하지 않기에 `g()`가 `false`일 때는 자연스럽게 `f()`가 계산되지 않는다. 그러므로 코드를 전혀 수정하지 않아도 그 자체로 효율적인 구현이다. 이처럼 소극적 계산은 함수 몸통에서 인자의 값이 필요하지 않을 가능성이 있을 때 코드의 가독성을 지키면서도 효율적으로 구현하는 것을 가능하게 한다.

인자의 값이 필요해지는 시점을 언제로 보느냐에 따라 소극적 계산도 여러 방법이 가능하다. 가장 간단한 기준은 매개변수가 함수 몸통에 등장했을 때이다. 그러나 그보다 계산을 더 늦추는 것도 생각해 볼 수 있다. 매개변수가 등장했지만 매개변수의 값이 당장 의미 있는 계산에 사용되는 것이 아니라면 아직 계산할 필요가 없다고 볼 수도 있는 것이다. 다음 Scala 코드를 생각해 보자. (물론 Scala는 적극적 계산을 사용하지만 Scala에 소극적 계산을 추가할 방법을 고민 중이라고 가정하자.)

```scala
def f(x: Int): Int = x

f(1 + 1) + 1
```

첫 번째 관점을 따른다면 함수 몸통에 `x`가 나온 시점에 `1 + 1`을 계산하여 `2`를 얻는다. 함수의 결괏값은 `2`이며 전체 식의 값은 `2 + 1`을 계산하여 나온 `3`이다. 그러나 두 번째 관점을 따르면 `x`가 나왔더라도 `x`가 당장 사용되지 않으므로 함수의 결괏값은 `1 + 1`이 나타내는 어떤 값이라고만 두고 넘어갈 수 있다. 그 뒤에 결괏값에 `1`을 더해야 하므로 이때는 정말로 `1 + 1`을 더해 `2`를 얻고 다시 `1`을 더해 `3`을 얻는다. 물론 누군가는 더 계산을 늦춰서 `(1 + 1) + 1`로 두고 계산 결과를 어딘가에 출력해야 할 때 계산해서 `3`을 얻으면 된다고 말할 수도 있다. 어느 한 방법이 절대적으로 옳다고 말할 수는 없다. 언어의 의미가 항상 그렇듯이 언어가 목표로 하는 분야, 계산의 효율성, 사람들이 이해하기 쉬운 정도 등 여러 기준을 따져 선택해야 하는 문제이다.

이 글에서는 수업에서 다루는 LFAE를 먼저 살펴본다. LFAE는 엄밀하게 정의하지 않고 인터프리터 구현만 살펴보는 선에서 넘어갈 것이다. 그 뒤에 *이름에 의한 호출*(call by name)과 *필요에 의한 호출*(call by need)에 대해 자세히 알아볼 것이다. 이름에 의한 호출과 필요에 의한 호출은 수업에서 다루는 내용은 아니므로 넘어가도 무방하다.

## LFAE

LFAE는 소극적 계산을 사용하는 FAE이다. LFAE의 문법은 FAE와 같다. 함수 적용 시 인자는 바로 계산되지 않으며 인자의 값이 필요한 시점에 계산이 이루어진다. 값이 필요한 시점은 인자가 덧셈이나 뺄셈의 피연산자로 사용되는 경우와 함수 적용에서 함수로 사용되는 경우이다.

다음은 LFAE의 요약 문법을 Scala로 구현한 것이다.
```scala
sealed trait LFAE
case class Num(n: Int) extends LFAE
case class Add(l: LFAE, r: LFAE) extends LFAE
case class Sub(l: LFAE, r: LFAE) extends LFAE
case class Id(x: String) extends LFAE
case class Fun(x: String, b: LFAE) extends LFAE
case class App(f: LFAE, a: LFAE) extends LFAE
```

LFAE의 환경은 문자열을 열쇠, LFAE의 값을 값으로 하는 사전이다. 함수의 인자가 나타내는 값은 환경에 저장되어야 한다. 그러나 인자의 값을 환경에 넣기 전에 계산하지 않으므로 문제가 생긴다. 따라서 계산을 미루고 있다는 것을 명시적으로 표현하는 값을 추가로 정의해야 한다.

```scala
sealed trait LFAEV
case class NumV(n: Int) extends LFAEV
case class CloV(p: String, b: LFAE, e: Env) extends LFAEV
case class ExprV(e: LFAE, env: Env) extends LFAEV

type Env = Map[String, LFAEV]
```

`ExprV`는 계산을 미루고 있는 식을 나타내는 경우 클래스로, `env` 아래서 필드 `e`를 계산한 결과가 그 값이다. `ExprV`를 계산한 결과는 결국 `NumV`나 `CloV`가 되어야 하므로 미루어진 계산일 뿐인 `ExprV`를 값으로 보는 것이 다소 어색할 수 있다. `CloV`도 사실은 식과 환경으로 이루어짐에도 값인 것처럼 `ExprV`를 값으로 정의해도 문제될 것은 없지만 마음에 들지 않는다면 단순히 소극적 계산을 쉽고 효율적으로 구현하기 위한 하나의 구현 전략으로 받아들여도 된다. (실제로 뒤에서 이름에 의한 호출과 필요에 의한 호출을 볼 때는 구현에는 `ExprV`가 등장하지만 언어의 의미에는 `ExprV`에 해당하는 존재가 아예 없다.)

`ExprV`가 덧셈이나 뺄셈의 피연산자나 함수 적용의 함수 위치에 등장하였다면 미루었던 계산을 수행하여 `ExprV`가 진짜로 나타내는 값이 무엇인지 찾아야 한다. 다음의 `strict` 함수가 그 역할이다.

```scala
def strict(v: LFAEV): LFAEV = v match {
  case ExprV(e, env) => strict(interp(e, env))
  case _ => v
}
```

`ExprV`가 나타내는 값은 가지고 있는 환경 아래서 가지고 있는 식을 계산한 결과지만 그 결과도 `ExprV`일 수 있기 때문에 재귀 호출을 통해서 `ExprV`가 아닌 값을 얻을 때까지 같은 과정을 반복한다. `strict` 함수에 `ExprV`가 아닌 값이 들어온다면 그 값이 그대로 결과가 된다.

```scala
def lookup(x: String, env: Env): LFAEV =
  env.getOrElse(x, throw new Exception)

def interp(e: LFAE, env: Env): LFAEV = e match {
  case Num(n) => NumV(n)
  case Add(l, r) =>
    val NumV(n) = strict(interp(l, env))
    val NumV(m) = strict(interp(r, env))
    NumV(n + m)
  case Sub(l, r) =>
    val NumV(n) = strict(interp(l, env))
    val NumV(m) = strict(interp(r, env))
    NumV(n - m)
  case Id(x) => lookup(x, env)
  case Fun(x, b) => CloV(x, b, env)
  case App(f, a) =>
    val CloV(x, b, fEnv) = strict(interp(f, env))
    interp(b, fEnv + (x -> ExprV(a, env)))
}
```

`Num`, `Id`, `Fun` 경우는 이전과 같다. `Add`, `Sub` 경우는 `interp` 함수를 호출하여 얻은 결과에 `strict` 함수를 적용한다는 차이가 있다. `Fun` 경우도 함숫값에 대해서는 `strict` 함수를 적용하며 인자를 계산하는 대신 `ExprV`에 인자 식과 현재 환경을 넣고 이를 바로 환경에 넣는다.

다음은 `interp`를 사용하여 \((\lambda x.1)(1\ 1)\)을 계산한 것이다. FAE라면 \(1\ 1\)을 계산할 때 첫 \(1\)이 함수가 아니므로 오류가 발생한다. 그러나 LFAE에서는 함수 몸통에 매개변수 \(x\)가 등장하지 않아 \(1\ 1\)이 계산될 일이 없으므로 오류 없이 \(1\)이라는 결과를 얻을 수 있다.

```scala
// (lambda x.1) (1 1)
interp(
  App(
    Fun("x", Num(1)),
    App(Num(1), Num(1))
  ),
  Map.empty
)
// NumV(1)
```

현재 LFAE의 구현은 매개변수가 함수 몸통에서 한 번 이하 사용될 때는 효율적이지만 매개변수가 두 번 이상 사용되면 같은 계산을 반복하므로 비효율적이다. 아래 Scala 코드에 현재 LFAE의 의미를 적용한다고 생각해 보자.

```scala
def f(x: Int): Int = x + x
def g(): Int = ...

f(g())
```

함수 `f`의 몸통에 매개변수 `x`가 두 번 등장하므로 `g()`의 값이 두 번 계산된다. 그러나 함수 `g`가 언제나 같은 결과를 내며 부작용이 없는 순수 함수라면 두 번째 계산의 결과는 첫 번째 결과와 항상 같고 다른 계산에도 아무 영향을 주지 않는다. 따라서 `g()`를 처음 계산하여 얻은 값을 두 번 사용해도 같은 결과를 얻을 수 있다. 만약 적극적 계산을 사용한다면 함수 몸통을 계산하기 전 먼저 `g()`를 계산하므로 자연스럽게 `g()`는 한 번만 계산된다.

매개변수가 여러 번 사용되어도 효율적인 적극적 계산의 장점과 매개변수가 사용되지 않을 수 있는 경우에 효율적인 소극적 계산의 장점을 모두 살리기 위해서는 인자를 처음 계산해서 얻은 값을 저장한 뒤 그 값이 다시 필요할 때는 저장한 값을 사용하는 방법이 있다. 만약 수정 가능한 변수나 상자처럼 언어에 부작용을 일으키는 기능이 있다면 매번 다시 계산하는 것과 저장한 값을 사용하는 것이 다른 결과를 낼 수 있다. 그러나 LFAE에는 부작용이 없으므로 그런 언어의 의미를 바꾸지 않고 최적화를 할 수 있다.

```scala
case class ExprV(
  e: LFAE, env: Env, var v: Option[LFAEV]
) extends LFAEV
```

최적화를 위해 수정된 `ExprV`의 정의이다. `v`는 수정 가능한 필드로, 아직 계산을 한 번도 수행하지 않아 계산을 해서 나온 값을 모른다면 `None`이 값이다. 즉 `ExprV` 객체가 처음 만들어졌을 때는 `v`의 값은 반드시 `None`이다. 처음 `ExprV`가 나타내는 값을 계산한 뒤에는 그 값이 `Some`으로 감싸져서 `v`에 저장된다. `ExprV`의 값을 다시 필요로 하면 계산을 반복할 필요 없이 `v`에서 값을 꺼내면 된다. 이를 위해서는 아래와 같이 `strict` 함수의 정의를 수정해야 한다.

```scala
def strict(v: LFAEV): LFAEV = v match {
  case ev @ ExprV(e, env, None) =>
    val cache = strict(interp(e, env))
    ev.v = Some(cache)
    cache
  case ExprV(_, _, Some(cache)) => cache
  case _ => v
}
```

또한 `interp` 함수의 `App` 경우에서 `ExprV` 객체를 만들 때 `v`의 값에 해당하는 `None`을 인자로 추가해야 한다.

```scala
case App(f, a) =>
  val CloV(x, b, fEnv) = strict(interp(f, env))
  interp(b, fEnv + (x -> ExprV(a, env, None)))
```

`interp` 함수를 수정하는 대신 `ExprV` 클래스를 정의할 때 *기본 매개변수*(default parameter)를 사용할 수도 있다.

```scala
case class ExprV(
  e: LFAE, env: Env, var v: Option[LFAEV] = None
) extends LFAEV
```

이 경우 `ExprV` 객체를 만들 때 인자가 두 개만 주어진다면 필드 `v`의 값은 자동으로 `None`이 된다. 따라서 `interp` 함수는 그대로 두고 `strict` 함수만 수정하는 것으로 충분하다.

## 치환

이름에 의한 호출과 필요에 의한 호출에 대해 알아보기에 앞서 적극적 계산을 하는 FAE의 의미를 기존과 다른 방법으로 다시 정의할 것이다. 이는 환경 대신 *치환*(substitution)을 사용한 방법이다. 치환을 사용해서 의미를 정의함으로써 소극적 계산의 의미를 쉽게 정의할 수 있으며 적극적 계산과 소극적 계산이 어떻게 다른지 명확히 볼 수 있다.

치환은 식의 특정 부분식을 다른 부분식으로 바꾸는 것을 의미한다. 대부분의 경우 식에 들어 있는 변수를 어떤 식으로 바꾸는 것을 말한다. 예를 들어 \(x+y\)에서 \(x\)를 \((z\ 3)\)으로 치환하면 \((z\ 3)+y\)가 된다.

치환을 엄밀히 정의하기에 앞서 FAE의 요약 문법을 다시 쓰겠다.

\[
\begin{array}{lrcl}
\text{Integer} & n & \in & \mathbb{Z} \\
\text{Variable} & x & \in & \textit{Id} \\
\text{Expression} & e & ::= & n \\
&& | & e + e \\
&& | & e - e \\
&& | & x \\
&& | & \lambda x.e \\
&& | & e\ e
\end{array}
\]

식 \(e\)에서 변수 \(x\)를 식 \(e'\)으로 치환한 것은 \(\lbrack e'/x\rbrack e\)라고 쓴다. \(\lbrack/\rbrack\)을 치환 함수라고 보면 이는 식, 변수, 식에서 식으로 가는 함수이다.

\[
\lbrack/\rbrack \in \text{Expression}\times\text{Variable}\times\text{Expression}\rightarrow\text{Expression}
\]

정수는 변수를 포함하지 않으므로 치환의 영향을 받지 않는다.

\[\lbrack e/x \rbrack n  =  n\]

변수는 치환 대상과 그 변수가 같다면 주어진 식으로 바뀌고 다르다면 그 변수 그대로 남는다.

\[
\lbrack e/x' \rbrack x = \begin{cases} e& \text{if } x=x'\\ x & \text{if } x\not=x'\end{cases}
\]

합, 차, 함수 적용에 대한 치환은 재귀적으로 정의된다. 각 부분식에 치환을 적용한 것이 치환 결과이다.

\[
\begin{array}{rcl}
\lbrack e/x \rbrack (e_1+e_2) = \lbrack e/x \rbrack e_1+\lbrack e/x \rbrack e_2 \\
\lbrack e/x \rbrack (e_1-e_2) = \lbrack e/x \rbrack e_1-\lbrack e/x \rbrack e_2 \\
\lbrack e/x \rbrack (e_1\ e_2)\ = \lbrack e/x \rbrack e_1\ \lbrack e/x \rbrack e_2
\end{array}
\]

람다 요약에 대한 치환은 주의가 필요하다. 주어진 변수가 람다 요약의 매개변수와 다르다면 몸통에 치환을 적용하면 되지만 같다면 몸통을 그대로 둬야 한다.

\[
\lbrack e/x' \rbrack\lambda x.e' = \begin{cases}\lambda x.e' & \text{if } x=x'\\
\lambda x.\lbrack e/x' \rbrack e'& \text{if } x\not=x'\end{cases}
\]

이는 치환을 통해 함수 적용의 의미를 정의하기 위해 자연스럽게 요구되는 성질이다. 함수 몸통에서 매개변수를 치환하는 것을 통해 함수에 인자를 전달할 것인데 이때 몸통 안에 있는 람다 요약의 매개변수를 확인하지 않고 무조건 치환을 하면 가리기가 제대로 이루어지지 않는다. 이 문제는 의미를 정의한 뒤 다시 자세히 볼 것이다.

이제 치환을 사용해서 FAE의 의미를 정의하겠다. 환경은 필요 없으며 의미는 식과 값의 관계이다. 기존에 정의했던 의미와 구분하기 위해서 \(\Downarrow\)를 의미를 나타내는 데 사용할 것이다. \(\Rightarrow\)와 비교했을 때 특별히 어떤 의미가 있는 것은 아니며 \(\Downarrow\) 역시 의미를 정의하기 위해 흔히 사용되는 기호이다.

\[
\Downarrow \subseteq \text{Expression}\times\text{Value}
\]

\(e\Downarrow v\)는 \(e\)를 계산한 결과가 \(v\)임을 의미한다.

값은 정수거나 함숫값이다. 환경이 없으므로 함숫값도 환경을 저장할 필요가 없다. 따라서 클로저가 아닌 그냥 람다 요약이 함숫값이다.

\[
\begin{array}{lrcl}
\text{Value} & v & ::= & n \\
&& | & \lambda x.e
\end{array}
\]

정수, 합, 차의 의미는 이전과 비슷하다.

\[
n\Downarrow n
\]

\[
\frac{
  e_1\Downarrow \lambda x.e \quad
  e_2\Downarrow v' \quad
  \lbrack v'/x\rbrack e\Downarrow v
}
{ e_1\ e_2\Downarrow v }
\]

\[
\frac
{
  e_1\Downarrow n_1 \quad
  e_2\Downarrow n_2
}
{ e_1+e_2\Downarrow n_1+n_2 }
\]

\[
\frac
{
  e_1\Downarrow n_1 \quad
  e_2\Downarrow n_2
}
{ e_1-e_2\Downarrow n_1-n_2 }
\]

람다 요약은 정수처럼 식 그대로가 결과이다.

\[
\lambda x.e\Downarrow\lambda x.e
\]

함수 적용은 함수와 인자를 계산한 뒤 함수 몸통에서 매개변수를 인자의 값으로 치환하여 얻어진 식을 계산한다.

\[
\frac{
  e_1\Downarrow \lambda x.e \quad
  e_2\Downarrow v' \quad
  \lbrack v'/x\rbrack e\Downarrow v
}
{ e_1\ e_2\Downarrow v }
\]

함수 적용 시 매개변수가 인자의 값으로 치환되므로 자유 변수가 없는 식을 계산하는 과정에서는 변수의 값을 직접 구할 일이 없다. 따라서 변수에 해당하는 추론 규칙은 없다. 만약 변수의 값을 구해야 하는 상황이 나왔다면 이는 처음 식에 자유 변수가 존재함을 의미한다. 예를 들어 자유 변수 \(y\)가 존재하는 \((\lambda x.y)\ 1\)을 계산한다면 \(x\)를 \(1\)로 치환하여 얻은 식인 \(y\)를 계산해야 하며 이는 불가능하다.

치환의 정의를 이해하기 위해서 \((\lambda x.(\lambda x.x)\ 1)\ 2\)를 생각해 보자. 바깥에 있는 함수는 받은 인자의 값에 상관없이 항등 함수를 \(1\)에 적용하여 얻은 값을 결과로 낸다. 따라서 전체 결과는 \(2\)가 아닌 \(1\)이다. 현재의 치환 정의를 따르면 위 식을 계산하는 것은 \((\lambda x.x)\ 1\)에서 \(x\)를 \(2\)로 치환하여 얻은 식을 계산하는 것이다. 이 때 \(\lambda x.x\)의 매개변수 \(x\)와 치환 대상 \(x\)가 같기 때문에 치환한 결과는 그대로 \((\lambda x.x)\ 1\)이다. 그러므로 올바르게 \(1\)이 결과가 된다. 그러나 매개변수와 치환 대상을 비교하는 과정 없이 무조건 몸통을 치환한다면 치환 결과는 \((\lambda x.2)\ 1\)이므로 \(2\)라는 잘못된 결과가 나온다. 즉 가장 안에 있는 \(x\)가 \(\lambda x.x\)의 매개변수 \(x\)가 아닌 \(\lambda x.(\lambda x.x)\ 1\)의 매개변수 \(x\)에 묶인 것이므로 가장 가까운 묶는 등장에 묶여야 한다는 규칙을 어기고 가리기가 이루어지지 않은 것이다.

### 알파 변환

안타깝게도 아직 FAE의 의미는 완벽하지 않다. 앞에서 식에 자유 변수가 존재하면 식을 계산하는 과정에서 그 변수의 값을 직접 계산해야 하기에 식의 값을 구할 수 없다고 설명하였다. 그러나 현재의 의미를 사용하면 식에 자유 변수가 존재함에도 올바르지 않게 식의 결과가 계산되는 일이 생길 수 있다. 식 \((\lambda x.\lambda y.x)\ (\lambda z.y)\ 1\ 2\)를 생각해 보자. \(\lambda z.y\)가 자유 변수 \(y\)를 가지고 있다. \(\lambda x.\lambda y.x\)는 인자 두 개를 차례로 받아 첫 인자를 결과로 내는 함수이다. 따라서 \((\lambda x.\lambda y.x)\ (\lambda z.y)\ 1\)의 결과는 \(\lambda z.y\)이고 이 함수를 \(2\)에 적용하면 함수 몸통을 계산할 때 \(y\)의 값을 알 수 없으므로 오류가 발생해야 한다. 현재의 의미를 적용하면 다른 결과가 나온다. \((\lambda x.\lambda y.x)\ \lambda z.y\)의 결과는 \(\lambda y.\lambda z.y\)이다. 여기에서 벌써 이상한 점이 보인다. 분명히 자유 변수가 있던 \(\lambda z.y\)가 갑자기 \(y\)가 예상치 못한 매개변수 \(y\)에 묶이면서 문제없는 식이 되었다. 계속 계산을 해보면 \(\lambda y.\lambda z.y\)에 \(1\)과 \(2\)를 차례로 인자로 넘기는 것이므로 결과는 \(1\)이 되어 오류가 발생하지 않고 잘못된 결과가 나온다. 이러한 문제가 발생한 이유는 \(\lambda y.x\)의 매개변수 \(y\)의 묶는 등장에 묶이는 \(y\)는 함수 몸통에 문법적으로 들어 있는 \(y\)만이어야 함에도 치환을 하는 과정에서 \(\lambda z.y\)가 문법적으로 \(x\) 자리에 오면서 매개변수 \(y\)와 전혀 상관없는 자유 변수 \(y\)가 매개변수 \(y\)에 묶여 버린 것이다. 이처럼 치환을 할 때 자유 변수가 관계없는 묶는 등장에 묶이는 현상을 *변수 포획*(variable capture)이라 한다.

변수 포획은 *알파 변환*(alpha conversion)을 통해서 해결할 수 있다. 알파 변환은 *알파 이름 바꾸기*(alpha renaming)라고도 부르며 람다 요약의 의미를 유지하면서 매개변수의 이름을 바꾸는 것을 의미한다. 예를 들어 \(\lambda x.x\)를 알파 변환하여 \(\lambda y.y\)를 얻을 수 있다. 두 함수는 모두 항등 함수로서 완전히 같은 의미를 가지지만 매개변수의 이름은 다르다. 어떤 람다 요약을 알파 변환하여 얻어진 람다 요약은 처음의 람다 요약과 *알파 동치*(alpha equivalent) 관계에 있다고 말한다.

알파 변환에서 새로운 매개변수의 이름을 선택할 때는 식에 나오지 않는 식별자를 선택해야 한다. 자유 식별자이든 묶는 등장이든 묶인 등장이든 상관없이 이미 있던 식별자를 사용하면 식의 의미가 달라질 수 있다. \(\lambda x.y\)라는 식에서 \(x\)를 \(z\)로 바꾸어 \(\lambda z.y\)를 얻는 것은 올바른 알파 변환이지만 \(x\)를 \(y\)로 바꾸어 \(\lambda y.y\)가 나왔다면 식의 의미가 달라졌기에 잘못된 알파 변환이다. 또 \(\lambda x.\lambda y.x\)에서 \(x\)를 \(z\)로 바꾸어 \(\lambda z.\lambda y.z\)가 나오는 것은 올바르지만 \(x\)를 \(y\)로 바꾸어 \(\lambda y.\lambda y.y\)를 얻으면 잘못되었다.

다음 함수 \(\mathit{id}\)는 식을 인자로 받아 그 식에 들어 있는 모든 식별자의 집합을 결과로 낸다.

\[
\mathit{id}\in \text{Expression}\rightarrow \mathcal{P}(\text{Variable})
\]

\[
\begin{array}{rcl}
\mathit{id}(n) & = & \emptyset \\
\mathit{id}(e_1+e_2) & = & \mathit{id}(e_1)\cup\mathit{id}(e_2) \\
\mathit{id}(e_1-e_2) & = & \mathit{id}(e_1)\cup\mathit{id}(e_2) \\
\mathit{id}(x) & = & \{x\} \\
\mathit{id}(\lambda x.e) & = & \mathit{id}(e)\cup\{x\} \\
\mathit{id}(e_1\ e_2) & = & \mathit{id}(e_1)\cup\mathit{id}(e_2)
\end{array}
\]

정수에는 식별자가 없으며 식이 변수라면 그 변수의 이름이 유일한 식별자이다. 합, 차, 함수 적용의 식별자는 부분식의 식별자를 모두 합한 것이다. 람다 적용의 식별자는 몸통의 식별자에 매개변수의 이름을 더한 것이다.

알파 변환의 정의는 다음과 같다. 알파 변환은 식과 식의 관계이다.

\[
\equiv^\alpha \subseteq \text{Expression}\times\text{Expression}
\]

\[
\frac
{ x'\not\in\textit{ids}(\lambda x.e) }
{ \lambda x.e\equiv^\alpha\lambda x'.\lbrack x'/x\rbrack e }
\]

\[
\lambda x.e\equiv^\alpha\lambda x.e
\]

알파 변환은 람다 요약에 대해서만 정의된다. 람다 요약에 나오지 않는 식별자를 골라 매개변수의 이름을 그 식별자로 바꾸고 몸통에 있는 매개변수에 묶인 등장의 이름도 모두 바꾸면 된다. 람다 변환의 결과로 자기 자신이 나오는 것도 가능하다.

알파 변환을 사용하여 변수 포획이 일어나지 않는 치환을 정의할 수 있다. 이를 위해서는 식에 들어 있는 자유 변수를 엄밀히 정의할 필요가 있다. 다음 함수 \(\mathit{fv}\)는 식을 인자로 받아 그 식에 들어 있는 모든 자유 변수의 집합을 결과로 낸다.

\[
\mathit{fv}\in \text{Expression}\rightarrow \mathcal{P}(\text{Variable})
\]

\[
\begin{array}{rcl}
\mathit{fv}(n) & = & \emptyset \\
\mathit{fv}(e_1+e_2) & = & \mathit{fv}(e_1)\cup\mathit{fv}(e_2) \\
\mathit{fv}(e_1-e_2) & = & \mathit{fv}(e_1)\cup\mathit{fv}(e_2) \\
\mathit{fv}(x) & = & \{x\} \\
\mathit{fv}(\lambda x.e) & = & \mathit{fv}(e)\setminus\{x\} \\
\mathit{fv}(e_1\ e_2) & = & \mathit{fv}(e_1)\cup\mathit{fv}(e_2)
\end{array}
\]

정수에는 자유 변수가 없으며 식이 변수라면 그 변수가 유일한 자유 변수이다. 합, 차, 함수 적용의 자유 변수는 부분식의 자유 변수를 모두 합한 것이다. 람다 적용의 자유 변수는 몸통의 자유 변수에서 매개변수의 이름을 제외한 것이다.

이제 치환을 다시 정의하겠다. 알파 변환의 결과로 나올 수 있는 식은 여러 개이므로 치환도 함수가 아니라 관계로 정의된다.

\[
\lbrack/\rbrack \subseteq \text{Expression}\times\text{Variable}\times\text{Expression}\times\text{Expression}
\]

람다 요약에 대한 치환만 새로 정의하겠다. 나머지 경우는 이전과 같다.

\[
\lbrack e/x \rbrack\lambda x.e' = \lambda x.e'
\]

\[
\frac
{ x\not=x' \quad
  \lambda x'.e'\equiv^\alpha \lambda x'' .e'' \quad
  x'' \not\in\mathit{fv}(e) }
{ \lbrack e/x \rbrack\lambda x'.e' =
  \lambda x'' .\lbrack e/x \rbrack e'' }
\]

치환이 변수 포획을 일으키지 않으므로 언어의 의미가 올바르게 정의되었다.

많은 프로그래밍 언어 연구에서 변수 포획과 알파 변환은 연구가 핵심으로 다루고자 하는 바와는 관계가 없다. 그렇기에 대부분의 경우 언어의 의미를 정의할 때 변수 포획을 피하기 위하여 알파 변환이 필요할 때 적절히 이루어진다고 가정하고 넘어간다. 엄밀한 방법은 아니나 이미 알파 변환에 대해서는 충분히 널리 알려져 있기 때문에 연구에서 중요한 부분에 집중하기 위해 불필요한 세부 내용을 생략하는 것이라 볼 수 있다. 변수 포획의 문제만 제외하면 환경을 사용하여 언어의 의미를 정의하는 것보다 치환을 사용하여 언어의 의미를 정의하는 것이 편한 경우가 많다. 변수 포획을 가정을 통해서 쉽게 무시할 수 있기 때문에 많은 연구에서는 언어의 의미를 치환을 사용하여 정의한다.

연구에서 Coq 같은 증명 보조 도구를 사용하는 경우에는 알파 변환 같은 사소한 부분까지 모두 엄밀히 다루어야 한다. 이 경우 *De Bruijn 인덱스*(De Bruijn index)를 사용하여 알파 변환 없이도 변수 포획이 일어나지 않게 할 수 있다. 또는 *고차 요약 문법*(higher-order abstract syntax)을 사용하여 변수 묶기를 요약 문법 나무 단계에서 함수로 표현할 수도 있다. 두 주제는 이 글에서 자세히 다루지 않는다.

인터프리터를 구현할 때 치환을 사용하여 구현하는 것도 가능하나 이는 일반적이지 않다. 가정을 통해 변수 포획을 무시할 수 있는 연구와 달리 실제 구현에서는 이를 모두 해결해야 한다. 이는 환경을 사용하여 구현하는 것과 비교해 불필요한 노력과 복잡성을 만든다. 또한 사전 같은 효율적인 자료 구조를 사용한다면 \(O(1)\)에 환경에 변수를 추가하고 변수의 값을 얻을 수 있는 것과 달리 크기가 \(n\)인 식에 치환을 적용하기 위해서는 \(O(n)\)의 시간이 필요하다. 따라서 인터프리터가 치환을 사용하는 것은 환경을 사용하는 것에 비해 실제 프로그램을 실행할 때도 불리하다. (식의 크기를 엄밀하게 정의하지는 않았지만 맥락상 중요하지 않으므로 그냥 넘어가겠다.)

## 이름에 의한 호출

값에 의한 호출을 사용하는 함수 적용의 의미를 다시 쓰면 다음과 같다.

\[
\frac{
  e_1\Downarrow \lambda x.e \quad
  e_2\Downarrow v' \quad
  \lbrack v'/x\rbrack e\Downarrow v
}
{ e_1\ e_2\Downarrow v }
\]

이름에 의한 호출을 사용하도록 바꾸려면 인자를 계산하지 않고 인자 식 자체를 사용해서 치환을 하면 된다.

\[
\frac{
  e_1\Downarrow_l \lambda x.e \quad
  \lbrack e_2/x\rbrack e\Downarrow_l v
}
{ e_1\ e_2\Downarrow_l v }
\]

나머지 추론 규칙은 그대로이다.

값에 의한 호출 의미에 따라 식을 계산하여 어떤 값을 얻었다면 이름에 의한 호출을 사용해도 같은 결과를 얻는다. 이는 다음과 같이 적을 수 있다.

\[
\forall e.\forall v.e\Downarrow v\rightarrow e\Downarrow_l v
\]

이는 *표준화 정리*(standardization theorem)[^2]의 따름정리이다. 표준화 정리는 어떤 람다 대수의 식이 어떤 순서로 계산하여 값이 나온다면, *정규 순서*(normal order)로 계산하였을 때 반드시 그 값이 나옴을 의미한다. 정규 순서 계산은 이름에 의한 호출과 비슷하지만 함수의 몸통도 계산한다는 차이가 있다. 정규 순서는 이 글의 수준을 넘어가므로 자세히 다루지 않는다.

[^2]: John C. Reynolds, *Theories of Programming Languages*, Cambridge, p. 200

단, 이는 FAE에 부작용이 없기 때문에 성립한다. 부작용이 존재하는 언어에서는 값에 의한 호출을 했을 때 결과를 냄에도 이름에 의한 호출을 했을 때 결과가 나오지 않거나 다른 결과를 내는 프로그램을 만들 수 있다. 부작용이 없는 언어에서는 인자를 몇 번 계산하든 상관이 없다. 그러나 부작용이 있다면 계산이 프로그램의 상태를 변화시킬 수 있다. 값에 의한 호출은 언제나 인자를 한 번 계산하지만 이름에 의한 호출은 인자를 계산하지 않거나 여러 번 계산할 수 있으므로 결과가 바뀔 수 있다.

한편 위 정리의 역은 성립하지 않는다. 이름에 의한 호출로 계산하여 어떤 결과를 얻었음에도 값에 의한 호출로 계산할 수 없는 식이 존재한다. 이름에 의한 호출은 인자를 계산할 때 오류가 발생하거나 인자의 계산이 종료되지 않더라도 인자가 함수 몸통에서 사용되지 않으면 문제없다. 그러나 값에 의한 호출은 반드시 인자를 먼저 계산하므로 인자가 사용되지 않음에도 오류가 발생하거나 계산이 끝나지 않는다.

### 인터프리터 구현

기존 LFAE 인터프리터 구현을 약간 수정하여 이름에 의한 호출을 사용하도록 할 수 있다. 기존 인터프리터는 인자의 값이 필요한 경우를 인자가 덧셈이나 뺄셈의 피연산자나 함수 적용의 함수로 사용되는 경우로 판단한다. 이름에 의한 호출 의미를 따르려면 매개변수가 나온 시점에 즉시 인자를 계산해야 한다.

```scala
sealed trait LFAE
case class Num(n: Int) extends LFAE
case class Add(l: LFAE, r: LFAE) extends LFAE
case class Sub(l: LFAE, r: LFAE) extends LFAE
case class Id(x: String) extends LFAE
case class Fun(x: String, b: LFAE) extends LFAE
case class App(f: LFAE, a: LFAE) extends LFAE
```

요약 문법은 이전과 같다.

```scala
sealed trait LFAEV
case class NumV(n: Int) extends LFAEV
case class CloV(p: String, b: LFAE, e: Env) extends LFAEV

case class Expr(e: LFAE, env: Env)
type Env = Map[String, Expr]
```

기존의 LFAE에서는 미룬 계산이 함수의 결괏값이 될 수 있기에 `ExprV`라는 값을 정의하였다. 이름에 의한 호출을 사용하면 매개변수가 몸통에 나온 즉시 인자를 계산하므로 함수의 결괏값이 미루어진 계산이 될 일이 없다. 따라서 `ExprV`를 값으로 정의할 필요가 없다. `Expr`는 미루어진 계산을 나타내는 경우 클래스이며 환경은 문자열에서 `Expr`로 가는 사전으로 구현할 수 있다.

```scala
def lookup(x: String, env: Env): Expr =
  env.getOrElse(x, throw new Exception)

def interp(e: LFAE, env: Env): LFAEV = e match {
  case Num(n) => NumV(n)
  case Add(l, r) =>
    val NumV(n) = interp(l, env)
    val NumV(m) = interp(r, env)
    NumV(n + m)
  case Sub(l, r) =>
    val NumV(n) = interp(l, env)
    val NumV(m) = interp(r, env)
    NumV(n - m)
  case Id(x) =>
    val Expr(e, eEnv) = lookup(x, env)
    interp(e, eEnv)
  case Fun(x, b) => CloV(x, b, env)
  case App(f, a) =>
    val CloV(x, b, fEnv) = interp(f, env)
    interp(b, fEnv + (x -> Expr(a, env)))
}
```

`interp`의 결과는 `ExprV`가 아니므로 `strict` 함수는 필요하지 않으며 나온 결과를 바로 사용할 수 있다. 식이 `Id`인 경우 환경에서 `Expr` 객체를 찾은 뒤 들어 있는 환경 아래에서 들어 있는 식을 계산하여 얻은 값을 결과로 낸다. `App` 경우에서 `Expr` 객체를 만든다.

### 필요에 의한 호출

필요에 의한 호출은 이름에 의한 호출과 마찬가지로 매개변수가 함수 몸통에서 사용되는 시점에 인자를 계산하지만 처음 계산해서 얻은 값을 저장하였다가 인자의 값이 필요할 때 사용한다는 차이가 있다. LFAE의 구현에서 본 옵션 타입을 사용한 최적화를 이름에 의한 호출에 적용한 것이라 볼 수 있다. 부작용이 없는 언어에서는 이름에 의한 호출과 필요에 의한 호출은 언제나 같은 결과를 내며 이름에 의한 호출이 더 많은 계산을 한다는 차이만 존재한다. 물론 부작용이 있다면 두 의미가 다른 결과를 낼 수 있다.

필요에 의한 호출 역시 엄밀하게 의미를 정의할 수 있다. 그러나 처음 인자를 계산하여 얻은 값을 저장하여 이후의 계산에서 사용하기에 BFAE나 MFAE의 의미를 정의할 때 사용한 저장소와 유사한 개념을 사용해야 한다. 이는 필요에 의한 호출이 표현하는 의미에 비해서 다소 불필요한 복잡성을 유발한다. 또한 LFAE에서는 이름에 의한 호출을 사용하든 필요에 의한 호출을 사용하든 언제나 같은 결과를 얻기 때문에 두 개가 서로 다른 의미라기보다 단순히 구현의 차이로 볼 여지도 있다. 따라서 이 글에서는 필요에 의한 호출의 의미는 다루지 않는다. 궁금한 사람은 직접 시도해 보는 것을 추천한다. 필요에 의한 호출의 의미를 엄밀히 정의한 논문도 찾을 수 있으며 그중 하나의 제목을 남긴다: An operational semantics of sharing in lazy evaluation[^3].

[^3]: Jill Seaman, S.Purushothaman Iyer, *An operational semantics of sharing in lazy evaluation*, Science of Computer Programming, Volume 27, Issue 3, 1996, Pages 289-322, <https://doi.org/10.1016/0167-6423(96)00012-3>.

필요에 의한 호출을 사용하는 LFAE의 구현은 앞에서 본 옵션 타입을 사용한 `ExprV`와 이름에 의한 호출을 사용하는 LFAE의 구현을 조합하여 쉽게 만들 수 있다. 이 역시 생략하겠다.

## 감사의 말

글을 확인하고 의견을 주신 류석영 교수님께 감사드립니다.
