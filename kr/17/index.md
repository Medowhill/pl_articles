이번 글은 continuation에 대해 다룬다. Continuation을 한국어로 어떻게 옮길 것인지 많이 고민하였다. ‘컨티뉴에이션’이라고 그대로 음차를 하거나 의미를 살려서 ‘남은 할 일’이라고 번역하거나 ‘계속’이라고 직역할 수 있다. 용어가 그 뜻을 담고 있을 필요는 없다고 생각하기 때문에 ‘남은 할 일’은 후보에서 제외하였다. ‘남은 할 일’은 continuation을 한국어로 옮긴 것보다는 continuation에 대한 설명이라고 생각한다. 음차된 형태가 널리 사용되는 경우가 아니면 번역을 하겠다는 처음의 생각에 따르면 ‘계속’을 선택해야 마땅하다. 그러나 입에 ‘컨티뉴에이션’이 너무 붙어서 ‘계속’은 어색한 것도 사실이다. ‘계속’이라고 번역을 하더라도 나 자신도 말할 때 ‘계속’이라는 용어를 쓸 것 같지 않다. 고민kk하던 중 ‘계속’의 사전적 뜻인 ‘끊어졌던 행위나 상태를 다시 이어 나감’이 눈에 들어왔다. 내게는 continuation에 대한 적합한 설명 같았다. 특히나 first-class continuation까지 생각한다면 마음에 드는 뜻이었다. 따라서 이 글에서는 continuation을 ‘계속’이라 쓴다.

## 환원가능식과 계속

값이 아닌 식의 값을 계산하는 것은 최소 한 단계 이상의 계산을 필요로 한다. 예를 들면 \(1+2\)은 \(1\)과 \(2\)을 더하는 계산을 해야 \(3\)라는 값이 나온다. \((1+2)+3\)은 \(1\)과 \(2\)를 더해 \(3\)을 얻고 다시 \(3\)과 \(3\)을 더해야 \(6\)이 나온다. 또한, 계산을 하는 순서는 고정되어 있다. \(1+2\)를 계산해서 \(3\)을 얻어야지만 그 값에 \(3\)을 더할 수 있다. \(1+2\)를 계산하기 전에 먼저 \(3\)을 더하는 계산을 할 수는 없는 것이다.

한 단계의 계산을 \(E\)라고 부른다고 하자. 여기서 ‘한 단계의 계산’이나 ‘\(E\)’는 엄밀하게 정의된 대상이 아니며 직관적인 이해를 위해 도입한 개념이다. 그럼 어떤 식 \(e\)를 계산하는 데는 \(E_1\)부터 \(E_n\)까지 여러 단계의 계산을 필요로 한다고 생각할 수 있다. \((1+2)+3\)을 계산하는 과정을 생각하면 \(E_1\)은 \(1\)과 \(2\)를 더하는 것이고 \(E_2\)는 \(E_1\)까지 계산해서 나온 값에 \(3\)을 더하는 것이다. 위에서 설명한 것처럼 \(E_2\)가 \(E_1\)의 결과에 의존하므로 \(E_1\)과 \(E_2\)의 순서는 바꿀 수 없다.

한 단계 이상의 계산은 앞부분과 뒷부분으로 나눌 수 있다. \(E_1\)부터 \(E_n\)까지 있다면 \(1\)과 \(n\)사이의 어떤 \(i\)에 대해 \(E_1\)부터 \(E_i\)까지는 앞부분, \(E_{i+1}\)부터 \(E_n\)까지는 뒷부분이다. 이때 뒷부분은 앞부분을 계산해서 얻은 값에 의존한다. 즉, 뒷부분의 계산에는 앞부분의 계산 결과가 필요하다. 앞부분에서 계산되는 부분식을 *환원가능식*(redex; reducible expression), 그 부분식의 계산 결과를 받아 계산을 마무리하는 뒷부분을 *계속*(continuation)이라 부른다. \(e\)를 계산하는 \(E_1\)부터 \(E_n\)까지의 과정에서 \(e\)의 부분식 \(e'\)이 \(E_1\)에서 \(E_i\)까지를 통해 계산된다고 하자. 그러면 \(e'\)은 환원가능식이고 \(E_{i+1}\)부터 \(E_n\)까지는 \(e'\)에 대한 계속이다. \((1+2)+3\)의 예시를 다시 보자. \(1+2\)를 환원가능식으로 보면 계속은 \(1+2\)의 계산 결과에 \(3\)을 더하는 것이다. 이는 앞에서 \(E_1\)과 \(E_2\)가 각각 무엇인지 본 것, 그리고 환원가능식과 계속의 정의에 부합한다.

더 복잡한 예시를 보겠다. \((1+2)-(3+4)\)는 \(1+2\)와 그 결과에서 \(3+4\)를 빼는 부분으로 나눌 수 있다. 따라서 \(1+2\)는 환원가능식이고 결과에서 \(3+4\)를 빼는 것이 계속이다. \(1+2\)를 계산하면 \(3\)이고 계속은 그 값에서 \(3+4\)를 빼는 것이므로 계산을 계속하려면 \(3-(3+4)\)를 계산해야 한다. 이 계산을 다시 환원가능식과 계속으로 나눌 수 있다. \(3+4\)를 계산해야만 \(3\)에서 그 결과를 빼 계산을 끝낼 수 있다. 환원가능식은 \(3+4\)이고 계속은 \(3\)에서 그 결과를 빼는 것이다. \(3+4\)를 계산하면 \(7\)이 나오고 계속은 \(3\)에서 그 값을 빼는 것이므로 계산을 계속하면 \(3-7\)이다. 이 계산 역시 환원가능식과 계속으로 나눌 수 있다. \(3-7\)이 환원가능식이고 계속은 그 결과를 그대로 두는 것이다. \(3-7\)은 \(-4\)이다. 계속이 아무것도 하지 않는 것이므로 계산이 끝난다. \(-4\)가 결과이다. 이를 정리하면 아래와 같다.

\[
\begin{array}{ccccc}
&\text{해야 하는 계산} & \text{환원가능식} & \text{계속} & \text{환원가능식 계산 결과} \\
& (1+2)-(3+4) & 1+2 & \text{결과에서 }3+4\text{ 빼기} & 3 \\
\rightarrow&3-(3+4) & 3+4 & 3\text{에서 결과 빼기} & 7 \\
\rightarrow&3-7 & 3-7 & \text{아무것도 하지 않기} & -4
\end{array}
\]

계속은 언제나 환원가능식의 결과를 사용하여 어떤 결과를 낸다. 환원가능식의 결과는 값이고 계산을 계속한 결과도 값이다. 따라서 계속은 값에서 값으로 가는 함수로 생각할 수 있다. 아래는 위 표에서 계속을 람다 요약을 사용해 표현한 것이다. 지금부터는 계속을 함수로 표현하겠다. 또, 어떤 식 \(e\)가 환원가능식 \(e'\)과 계속 \(k\)로 나누어진다고 하자. 계속 \(k\)는 함수이다. \(e'\)을 계산한 결과가 \(v\)라면 \(e\)의 값은 \(k\ v\), 즉 \(k\)를 \(v\)에 적용한 것과 같다.

\[
\begin{array}{ccccc}
&\text{해야 하는 계산} & \text{환원가능식} & \text{계속} & \text{환원가능식 계산 결과} \\
& (1+2)-(3+4) & 1+2 & \lambda v.v-(3+4) & 3 \\
\rightarrow&3-(3+4) & 3+4 & \lambda v.3-v & 7 \\
\rightarrow&3-7 & 3-7 & \lambda v.v & -4
\end{array}
\]

주어진 식에서 환원가능식은 유일하지 않을 수 있다. 이는 \(E_1\)부터 \(E_n\)까지의 계산 단계에서 앞부분과 뒷부분을 나누는 \(i\)가 유일하지 않은 것과 같다. 동일한 식 \((1+2)-(3+4)\)에 대해 다음과 같이 정리할 수도 있다.

\[
\begin{array}{ccccc}
&\text{해야 하는 계산} & \text{환원가능식} & \text{계속} & \text{환원가능식 계산 결과} \\
& (1+2)-(3+4) & (1+2)-(3+4) & \lambda v.v & -4 \\
\end{array}
\]

주의할 점은 \(3+4\)가 \((1+2)-(3+4)\)의 환원가능식이 되는 일은 없다는 점이다. \(1+2\)가 반드시 \(3+4\)보다 먼저 계산되어야 한다. 따라서 계산의 앞부분에 해당하는 환원가능식은 절대 \(3+4\)가 아니다. 물론 이는 언어의 의미가 항상 왼쪽부터 계산하는 것으로 정의되었을 때 그렇다.

아래는 여러 식에 대해 환원가능식과 계속을 정리한 예시이다.

\[
\begin{array}{ccccc}
&\text{해야 하는 계산} & \text{환원가능식} & \text{계속} & \text{환원가능식 계산 결과} \\
& 1+2 & 1+2 & \lambda v.v & 3
\end{array}
\]

\[
\begin{array}{ccccc}
&\text{해야 하는 계산} & \text{환원가능식} & \text{계속} & \text{환원가능식 계산 결과} \\
& (1+2)+3 & 1+2 & \lambda v.v+3 & 3 \\
\rightarrow&3+3 & 3+3 & \lambda v.v & 6
\end{array}
\]

\[
\begin{array}{ccccc}
&\text{해야 하는 계산} & \text{환원가능식} & \text{계속} & \text{환원가능식 계산 결과} \\
& ((1+2)-3)+4 & 1+2 & \lambda v.(v-3)+4 & 3 \\
\rightarrow&(3-3)+4 & 3-3 & \lambda v.v+4 & 0 \\
\rightarrow&0+4 & 0+4 & \lambda v.v & 4
\end{array}
\]

\[
\begin{array}{ccccc}
&\text{해야 하는 계산} & \text{환원가능식} & \text{계속} & \text{환원가능식 계산 결과} \\
& ((1+2)-3)+4 & (1+2)-3 &\lambda v.v+4 & 0 \\
\rightarrow&0+4 & 0+4 & \lambda v.v & 4
\end{array}
\]

람다 요약과 적용을 추가하여 생각해도 똑같이 환원가능식과 계속을 찾을 수 있다. 먼저 클로저와 환경 없이 치환을 사용한 형태를 보겠다.

\[
\begin{array}{ccccc}
&\text{해야 하는 계산} & \text{환원가능식} & \text{계속} & \text{환원가능식 계산 결과} \\
& (\lambda x.x)(1+1) & 1+1 & \lambda v.(\lambda x.x)v & 2 \\
\rightarrow& (\lambda x.x)2 & (\lambda x.x)2 & \lambda v.v & 2
\end{array}
\]

\[
\begin{array}{ccccc}
&\text{해야 하는 계산} & \text{환원가능식} & \text{계속} & \text{환원가능식 계산 결과} \\
& (\lambda x.\lambda y.x+y)\ 1\ 2 & (\lambda x.\lambda y.x+y)\ 1& \lambda v.v\ 2 & \lambda y.1+y \\
\rightarrow & (\lambda y.1+y)\ 2 & (\lambda y.1+y)\ 2 & \lambda v.v & 1+2 \\
\rightarrow & 1+2 & 1+2 & \lambda v.v & 3
\end{array}
\]

치환은 직관적인 환원가능식과 계속의 이해에는 좋지만, 인터프리터를 구현할 때는 치환 대신 클로저와 환경이 사용된다. 클로저와 환경을 사용하더라도 환원가능식과 계속을 찾을 수 있다.단, 환경을 표현할 방법이 필요하다. 엄밀하지는 않으나 핵심은 환원가능식과 계속을 직관적으로 이해하는 것이므로 편의상 \(\sigma\vdash e\)를 환경 \(\sigma\) 아래서 식 \(e\)를 계산해야 함을 표현하기 위해 사용하겠다. 또한, 클로저는 원래 값이지만 식은 아니나 마치 식인 것처럼 간주하겠다. 예를 들면 \(\langle\lambda x.x,\emptyset\rangle\ 1\)은 클로저 \(\langle\lambda x.x,\emptyset\rangle\)를 \(1\)에 적용하는 식이다.

\[
\begin{array}{ccccc}
&\text{해야 하는 계산} & \text{환원가능식} & \text{계속} & \text{환원가능식 계산 결과} \\
& (\lambda x.x)(1+1) & \lambda x.x & \lambda v.v(1+1) & \langle\lambda x.x,\emptyset\rangle \\
\rightarrow&\langle\lambda x.x,\emptyset\rangle(1+1) & 1+1 & \lambda v.(\langle\lambda x.x,\emptyset\rangle)v & 2 \\
\equiv&\langle\lambda x.x,\emptyset\rangle(1+1) & 1+1 & \lambda v.[x\mapsto v]\vdash x & 2 \\
\rightarrow& [x\mapsto 2]\vdash x & [x\mapsto 2]\vdash x & \lambda v.v & 2 \\
\end{array}
\]

\(\equiv\)는 실제로 계산이 일어난 것이 아니라 윗줄과 동일한 것을 다르게 썼다는 의미이다.

\[
\begin{array}{ccccc}
&\text{해야 하는 계산} & \text{환원가능식} & \text{계속} & \text{환원가능식 계산 결과} \\
& (\lambda x.\lambda y.x+y)\ 1\ 2 & \lambda x.\lambda y.x+y & \lambda v.v\ 1\ 2 & \langle\lambda x.\lambda y.x+y,\emptyset\rangle \\
\rightarrow&\langle\lambda x.\lambda y.x+y,\emptyset\rangle\ 1\ 2 &
\langle\lambda x.\lambda y.x+y,\emptyset\rangle\ 1 & \lambda v.v\ 2 &  \\
\equiv&\langle\lambda x.\lambda y.x+y,\emptyset\rangle\ 1\ 2 &
[x\mapsto 1]\vdash\lambda y.x+y & \lambda v.v\ 2 &
\langle\lambda y.x+y,[x\mapsto 1]\rangle \\
\rightarrow& \langle\lambda y.x+y,[x\mapsto 1]\rangle\ 2 & 
\langle\lambda y.x+y,[x\mapsto 1]\rangle\ 2 & \lambda v.v & \\
\equiv& \langle\lambda y.x+y,[x\mapsto 1]\rangle\ 2 & 
[x\mapsto 1,y\mapsto 2]\vdash x+y & \lambda v.v & 3 \\
\end{array}
\]

## 계속 전달 방식

*계속 전달 방식*(continuation passing style)은 프로그램을 구현하는 한 가지 방식이다. 계속 전달 방식은, 할 수 있는 계산이 모두 끝났으면 결과를 계속에 인자로 넘기고, 아직 할 계산이 남았으면 적절한 계속을 만들어 함수 호출 시에 넘기는 방식이다. 프로그램이 계속 전달 방식을 사용한다면 모든 함수는 계속을 인자로 받는다. 함수 몸통에서는 정확히 한 번의 함수 호출을 해야 한다. 여기서 호출되는 함수는 자기 자신일 수도, 다른 함수일 수도, 인자로 받은 계속일 수도 있다.

간단한 예시로 계승 함수를 계속 전달 방식을 사용해 구현해 보겠다. 다음은 계속 전달 방식을 사용하지 않는 평범한 계승 함수이다.

```scala
def factorial(n: Int): Int =
  if (n <= 1) 1
  else n * factorial(n – 1)

factorial(4)  // 24
factorial(7)  // 5040
```

이제 계속 전달 방식을 사용하는 `factorialC` 함수를 정의하겠다. `factorialC`는 두 개의 매개변수를 가진다. 첫 매개변수는 계승을 취할 정수이고 두 번째 매개변수는 계속이다. 따라서 어떤 정수 `n`과 정수에서 정수로 가는 함수 `k`에 대해서 `factorialC(n, k)`는 `k(factorial(n))`와 같은 결과를 낸다.

```scala
def factorialC(n: Int, k: Int => Int): Int =
  if (n <= 1) k(1)
  else factorialC(n - 1, x => k(n * x))

factorialC(4, x => x)  // 24
factorialC(7, x => x)  // 5040
```

`n`이 `1`이하이면 `factorial(n)`은 `1`이다. 그러므로 `k(factorial(n))`은 `k(1)`이고 `factorialC`는 `n`이 `1`이하일 때 `k(1)`을 결과로 낸다.

한편 `n`이 `1`보다 큰 경우, `factorial(n)`은 `n * factorial(n – 1)`이다. 따라서 `factorialC`는 `n`이 `1`보다 클 때 `k(n * factorial(n – 1))`과 같은 값을 결과로 낸다. 그러나 이 식을 몸통에 바로 쓸 수는 없다. 함수 호출이 두 번 나오므로 계속 전달 방식을 사용한 것이 아니다. `k(n * factorialC(n – 1, x => x))` 역시 같은 이유로 잘못되었다.

`k(n * factorial(n – 1))`부터 다시 시작해 보자. 이 식은 `factorial(n – 1)`에 `n`을 곱하고 그 결과를 `k`에 인자로 넘기는 식이다. 다음처럼 함수 `f`를 정의하자.

```scala
def f(x: Int): Int = k(n * x)
```

그러면 처음 식은 `f(factorial(n – 1))`이다. 앞에서 `k(factorial(n))`은 `factorialC(n, k)`와 같은 결과를 낸다고 했다. 그러므로 식을 `factorialC(n – 1, f)`로 바꿀 수 있다. 사실 함수 `f`는 `x => k(n * x)`의 익명 함수 꼴로 쓸 수 있다. 따라서 `factorialC(n – 1, x => k(n * x))`가 된다. 이는 올바른 계속 전달 방식이며 위에서 본 코드와 같다. 아래는 이 변환 과정을 글 없이 정리한 것이다.

\[
\begin{array}{cl}
& \texttt{k(n * factorial(n – 1))} \\
= & \texttt{(x => k(n * x))(factorial(n – 1))} \\
= & \texttt{factorialC(n – 1, x => k(n * x))} \\
\end{array}
\]

위 코드는 다음과 같은 논리로도 이해할 수 있다. `n`의 계승을 계산하는 것이 환원가능식이고 `k`가 계속이라 하자. `n`의 계승은 `n`에 `n – 1`의 계승을 곱한 것이다. 따라서 이 계산은 `n – 1`의 계승을 계산하는 것이 환원가능식이고 계속은 그 결과에 `n`을 곱한 뒤 `k`에 넘기는 것이다. 이렇게 생각해도 동일하게 `factorialC(n – 1, x => k(n * x))`를 얻을 수 있다.

계속 전달 방식에서는 함수 호출을 해서 나온 결과를 이용해 추가적인 계산을 하지 않는다. 추가적인 계산을 해야 한다면 그 추가적인 계산을 계속으로 전달할 뿐이다. `n * factorial(n – 1)`은 계속 전달 방식이 아니다. `factorial`을 호출하여 나온 결과를 `n`에 곱한다. 즉 `factorial(n – 1)`의 계속이 그 결과를 `n`에 곱하는 것이다. 한편 계속 전달 방식은 계속을 명확하게 표현하고 함수 호출 시 인자로 전달한다. `factorialC(n – 1, x => k(n * x))`에서 `x => k(n * x)`는 `n – 1`의 계승을 계산하는 것에 대한 계속이다. 계속을 명확히 드러내고 인자로 전달하므로 이는 계속 전달 방식이다.

계속 전달 방식을 올바르게 사용하였는지 확인하는 쉬운 방법은 계속과 모든 함수의 결과 타입을 `Unit`으로 바꾸는 것이다.

```scala
def factorialC(n: Int, k: Int => Unit): Unit =
  if (n <= 1) k(1)
  else factorialC(n - 1, x => k(n * x))

factorialC(4, println)  // 24
factorialC(7, println)  // 5040
```

결과 타입이 `Unit`이 됨에 따라 함수의 결괏값을 사용하는 것은 의미가 없다. 함수의 결괏값은 언제나 `()`이며 아무런 정보도 주지 않는다. `k(n * factorialC(n – 1, x => x))` 같은 코드는 컴파일 오류를 일으킨다. `factorialC`의 결과 타입이 `Unit`이므로 `n`에 결괏값을 곱할 수 없기 때문이다.

결과 타입이 `Unit`인 것은 계속의 정의와도 부합하는 면이 있다. 계속은 남은 계산 전부를 의미한다. 그러므로 계속을 호출하여 얻은 결괏값은 어디에도 사용될 수 없다. 계속을 호출하는 것은 계속의 몸통이 계산됨과 동시에 모든 계산이 완료됨을 나타낸다. 이런 맥락에서는 계속 전달 방식을 ‘한번 호출하면 절대로 돌아오지 않는 구현 방법’으로 이해할 수도 있다.

지금까지 계속 전달 방식을 설명하기 위해 여러 표현을 사용했다. “반드시 정확히 한 번의 함수 호출을 한다.” “계속이 명확히 드러나며 함수 호출 시 인자로 전달된다.“ ”함수의 결괏값은 사용되지 않는다.“ ”한번 함수를 호출하면 돌아오지 않는다.“ 이 문장들이 모두 결국은 ‘함수에서 함수로 계속이 전달된다’를 의미함을 이해하기를 바란다.

이제 FAE의 인터프리터를 계속 전달 방식으로 구현해 보겠다. 요약 문법과 환경은 이전에 본 것과 같다.

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
def lookup(x: String, env: Env): FAEV =
  env.getOrElse(x, throw new Exception)
```

추가적으로 계속의 타입을 정의한다.

```scala
type Cont = FAEV => FAEV
```

원래의 `interp` 함수는 다음과 같이 생겼다.

```scala
def interp(e: FAE, env: Env): FAEV = ...
```

계속 전달 방식을 사용하면 인자로 계속도 받아야 한다.

```scala
def interp(e: FAE, env: Env, k: Cont): FAEV = ...
```

어떤 `e`, `env`, `k`에 대해 `k(interp(e, env))`와 `interp(e, env, k)`는 반드시 같은 결괏값을 내야 한다.

식이 `Num`인 경우는 원래 `case Num(n) => NumV(n)`이다. `interp(Num(n), env)`는 `NumV(n)`이므로 `k(interp(Num(n), env))`는 `k(NumV(n))`이다. `interp(e, env, k)`도 `k(NumV(n))`여야 하므로 다음과 같이 구현할 수 있다.

```scala
case Num(n) => k(NumV(n))
```

`Id`와 `Fun`도 비슷하다.

```scala
case Id(x) => k(lookup(x, env))
case Fun(x, b) => k(CloV(x, b, env))
```

이제 `Add` 경우를 보겠다. 앞에서 환원가능식과 계속에 대해 알아볼 때 사용한 방법을 되돌아보자. 환원가능식이 \(e\)이고 계속이 \(k\)라 하자. 이때 \(k\)는 함수이고 전체 계산은 \(e\)를 계산한 결과를 \(k\)에 인자로 넘기는 것이다. 그러므로 전체 계산은 \(k\ e\)를 계산하는 것이라고 할 수 있다.

\(e_1+e_2\)를 계산할 때 계속이 \(k\)라면 계산하고자 하는 것은 \(k(e_1+e_2)\)와 같다. 한편 \(e_1+e_2\)의 값은 \((\lambda v_1.v_1+e_2)e_1\)과 같다. 이는 곧 \(k((\lambda v_1.v_1+e_2)e_1)\)이며 다시 \((\lambda v_1.k(v_1+e_2))e_1\)와 같다. 이 상태에서 \(v_1+e_2\)만 따로 떼서 보자. 그 값은 \((\lambda v_2.v_1+v_2)e_2\)와 같다. \(k(v_1+e_2)\)는 \(k((\lambda v_2.v_1+v_2)e_2)\)이며 이는 \((\lambda v_2.k(v_1+v_2))e_2\)이다. 다시 전체 식으로 돌아가면 \((\lambda v_1.k(v_1+e_2))e_1\)에서 \(k(v_1+e_2)\)를 \((\lambda v_2.k(v_1+v_2))e_2\)로 바꿀 수 있다. 그러면 \((\lambda v_1.(\lambda v_2.k(v_1+v_2))e_2)e_1\)이다. 따라서 \(e_1+e_2\)를 계산할 때 계속이 \(k\)인 것은 \(e_1\)을 계산할 때 계속이 \(\lambda v_1.(\lambda v_2.k(v_1+v_2))e_2\)인 것과 같다. 또한, 계속의 몸통인 \((\lambda v_2.k(v_1+v_2))e_2\)는 \(e_2\)를 계산할 때 계속이 \(\lambda v_2.k(v_1+v_2)\)인 것이다. 아래는 이 과정을 설명 없이 간략히 정리한 것이다.

\[
\begin{array}{rl}
& k(e_1+e_2) \\
= & k((\lambda v_1.v_1+e_2)e_1) \\
= & (\lambda v_1.k(v_1+e_2))e_1 \\
= & (\lambda v_1.k((\lambda v_2.v_1+v_2)e_2))e_1 \\
= & (\lambda v_1.(\lambda v_2.k(v_1+v_2))e_2)e_1 \\
\end{array}
\]

이를 그대로 코드로 옮기면 된다. 환경이 빠져 있지만 환경은 언제나 `env`이므로 쉽게 넣을 수 있다. \(\lambda v_2.k(v_1+v_2)\)는 코드로는 `v2 => k(v1 + v2)`이다. \(e_2\)를 계산할 때 계속이 \(\lambda v_2.k(v_1+v_2)\)인 것을 코드로 표현하면 `interp(e2, env, v2 => k(v1 + v2))`이다. 그럼 \(e_1\)을 계산할 때의 계속인 \(\lambda v_1.(\lambda v_2.k(v_1+v_2))e_2)\)는 `v1 => interp(e2, env, v2 => k(v1 + v2))`이다. 최종적으로는 다음 코드가 나온다.

```scala
case Add(e1, e2) =>
  interp(e1, env, v1 =>
    interp(e2, env, v2 =>
      k(v1 + v2)))
```

다만 실제로는 `v1 + v2`를 바로 할 수 없고 `NumV`를 벗긴 뒤 더하고 다시 `NumV`를 씌워야 한다.

```scala
def numVAdd(v1: FAEV, v2: FAEV): FAEV = {
  val NumV(n1) = v1
  val NumV(n2) = v2
  NumV(n1 + n2)
}
```

`numVAdd` 함수를 사용하여 `Add` 경우를 완성할 수 있다.

```scala
case Add(e1, e2) =>
  interp(e1, env, v1 =>
    interp(e2, env, v2 =>
      k(numVAdd(v1, v2))))
```

이 코드를 직관적으로 이해할 수도 있다. `Add(e1, e2)`를 계산할 때 계속이 `k`이면 이는 `e1`을 계산하여 그 결과를 `v1`이라 하고 `e2`를 계산하여 그 결과를 `v2`라 한 뒤 `k`에 `numVAdd(v1, v2)`를 넘기는 것이다. 그러므로 `e1`을 계산할 때 계속은 `e2`를 계산하여 그 결과를 `v2`라 한 뒤 `k`에 `numVAdd(v1, v2)`를 넘기는 것이다. 또한,  `e2`를 계산하여 그 결과를 `v2`라 한 뒤 `k`에 `numVAdd(v1, v2)`를 넘기는 것은 사실 `e2`를 계산할 때 계속이 그 결과를 `v2`라 한 뒤 `k`에 `numVAdd(v1, v2)`를 넘기는 것이다. “그 결과를 `v2`라 한 뒤 `k`에 `numVAdd(v1, v2)`를 넘기는 것”을 코드로 쓰면 `v2 => k(numVadd(v1, v2))`이다. 따라서 `e2`를 계산하는 것은 `interp(e2, env, v2 => k(numVadd(v1, v2)))`이다. 또, `e1`을 계산할 때의 계속은 `v1 => interp(e2, env, v2 => k(numVadd(v1, v2)))`이 된다. 따라서 위와 같은 코드가 나온다.

또는, 아래와 같이 정리할 수도 있다.

해야 하는 일은 1에서 3이다.

1. `e1`을 계산하여 그 결과를 `v1`이라 한다.
2. `e2`를 계산하여 그 결과를 `v2`라 한다.
3. `numVAdd(v1, v2)`를 계산하여 `k`에 인자로 넘긴다.

1에서의 계속은 2와 3이다. 계속은 다음과 같이 바꿔 쓸 수 있다.

1. 인자로 `v1`을 받는다.
2. `e2`를 계산하여 그 결과를 `v2`라 한다.
3. `numVAdd(v1, v2)`를 계산하여 `k`에 인자로 넘긴다.

여기서 2에서의 계속은 3이다. 다시 다음처럼 풀어서 쓸 수 있다.

1. 인자로 `v2`를 받는다.
2. `numVAdd(v1, v2)`를 계산하여 `k`에 인자로 넘긴다.

이는 함수로 쓰면 `v2 => k(numVAdd(v1, v2))`이다. 이를 사용하여 `e1`의 계속을 다시 쓸 수 있다.

1. 인자로 `v1`을 받는다.
2. `e2`를 계산하는데 계속이 `v2 => k(numVAdd(v1, v2))`이다.

2는 `interp` 함수를 호출하는 것으로 표현된다. `interp(e2, env, v2 => k(numVAdd(v1, v2)))`라고 쓸 수 있다. 그러면 `e1`의 계속은 `v1 => interp(e2, env, v2 => k(numVAdd(v1, v2)))`이다. 따라서 전체 계산은 `interp(e1, env, v1 => interp(e2, env, v2 => k(numVAdd(v1, v2))))`이다.

`Sub`은 `Add`와 마찬가지이다.

```scala
def numVSub(v1: FAEV, v2: FAEV): FAEV = {
  val NumV(n1) = v1
  val NumV(n2) = v2
  NumV(n1 - n2)
}
```

```scala
case Sub(e1, e2) =>
  interp(e1, env, v1 =>
    interp(e2, env, v2 =>
      k(numVSub(v1, v2))))
```

`App` 경우는 비슷하지만 조금 다르다. 앞에서 본 것과 같은 논리로, \(e_1e_2\)를 계산할 때 계속이 \(k\)인 것은 \(e_1\)을 계산할 때 계속이 \(\lambda v_1.(\lambda v_2.k(v_1v_2))e_2)\)인 것과 같다. 편의상 \(v_1\)이 클로저일 때 가지고 있는 람다 요약의 매개변수를 \(x_{v1}\), 람다 요약의 몸통을 \(e_{v1}\), 가지고 있는 환경을 \(\sigma_{v1}\)이라 하겠다. 즉, \(v_1=\langle \lambda x_{v1}.e_{v1},\sigma_{v1}\rangle\)이다. 그러면 \(v_1v_2\)는 \(v_1\)의 환경에 \(v_2\)를 추가하고 \(v_1\)의 몸통을 계산하는 것이므로 \(\sigma_{v1}[x_{v1}\mapsto v_2]\vdash e_{v1}\)이다. 따라서 \(\lambda v_1.(\lambda v_2.k(v_1v_2))e_2\)는 \(\lambda v_1.(\lambda v_2.k(\sigma_{v1}[x_{v1}\mapsto v_2]\vdash e_{v1}))e_2\)이다. 여기서 \(k(\sigma_{v1}[x_{v1}\mapsto v_2]\vdash e_{v1})\)는 \(e_{v1}\)을 환경 \(\sigma_{v1}\)아래에서 계산하는데 계속이 \(k\)인 것이다. 이를 코드로 옮기면 `interp(ev1, sigmav1 + (xv1 -> v2), k)`이다.

```scala
case App(e1, e2) =>
  interp(e1, env, v1 =>
    interp(e2, env, v2 =>
      interp(ev1, sigmav1 + (xv1 -> v2), k)))
```

이 상태로는 `ev1`과 `sigmav1`이 자유 변수이다.

```scala
case App(e1, e2) =>
  interp(e1, env, v1 =>
    interp(e2, env, v2 => {
      val CloV(xv1, ev1, sigmav1) = v1
      interp(ev1, sigmav1 + (xv1 -> v2), k)
    })
  )
```

이 코드에는 `k`를 호출하는 부분이 드러나지 않는다. 그렇다고 계속이 버려진 것은 아니다. `interp(ev1, sigmav1 + (xv1 -> v2), k)`에서 `k`를 그대로 인자로 넘긴다. `interp`에 `k`가 전달되었기에 언젠가는 `k`가 호출된다.

이제 인터프리터를 완성할 수 있다. 다음은 지금까지 나온 코드를 모은 것이다.

```scala
def interp(e: FAE, env: Env, k: Cont): FAEV = e match {
  case Num(n) => k(NumV(n))
  case Id(x) => k(lookup(x, env))
  case Fun(x, b) => k(CloV(x, b, env))
  case Add(e1, e2) =>
    interp(e1, env, v1 =>
      interp(e2, env, v2 =>
        k(numVAdd(v1, v2))))
  case Sub(e1, e2) =>
    interp(e1, env, v1 =>
      interp(e2, env, v2 =>
        k(numVSub(v1, v2))))
  case App(e1, e2) =>
    interp(e1, env, v1 =>
      interp(e2, env, v2 => {
        val CloV(xv1, ev1, sigmav1) = v1
        interp(ev1, sigmav1 + (xv1 -> v2), k)
      })
    )
}
```

잘 동작하는지 확인해 볼 수 있다.

```scala
// (1 + 2) - (3 + 4)
interp(
  Sub(
    Add(Num(1), Num(2)),
    Add(Num(3), Num(4))
  ),
  Map.empty,
  x => x
)
// -4

// (lambda x.lambda y.x + y) 1 2
interp(
  App(
    App(
      Fun("x", Fun("y",
        Add(Id("x"), Id("y")))),
      Num(1)
    ),
    Num(2)
  ),
  Map.empty,
  x => x
)
// 3
```

계속과 `interp` 함수의 결과 타입을 `Unit`으로 바꾸어 계속 전달 방식을 바르게 사용했는지 확인할 수 있다.

```scala
type Cont = FAEV => Unit
def interp(e: FAE, env: Env, k: Cont): Unit = ...
```

딱 두 줄만 바꾸면 된다. 수정 후에도 인터프리터는 그대로 잘 작동한다.

```scala
// (1 + 2) - (3 + 4)
interp(
  Sub(
    Add(Num(1), Num(2)),
    Add(Num(3), Num(4))
  ),
  Map.empty,
  println
)
// -4

// (lambda x.lambda y.x + y) 1 2
interp(
  App(
    App(
      Fun("x", Fun("y",
        Add(Id("x"), Id("y")))),
      Num(1)
    ),
    Num(2)
  ),
  Map.empty,
  println
)
// 3
```

인터프리터가 실행되면 어떤 일이 일어나는지 잘 안 보일 수 있기 때문에 `interp` 함수를 호출하면 환원가능식, 계속, 환경이 어떻게 변화하는지 보여주는 프로그램을 만들었다. 필요한 사람들은 [FAE.scala 파일](https://raw.githubusercontent.com/Medowhill/pl_articles/master/kr/17/FAE.scala)을 다운로드 받아 실행해 보기를 바란다. 아래처럼 다운로드 받은 디렉토리에서 `scala`를 실행한 뒤 `FAE.scala` 파일을 불러오고 `FAE` 안에 정의된 것들을 들여오면 된다. 그 후 `run` 함수에 FAE 식을 인자로 넘기면 된다.

```
$ scala
Welcome to Scala.
Type in expressions for evaluation. Or try :help.

scala> :load FAE.scala
args: Array[String] = Array()
Loading FAE.scala...
defined object FAE

scala> import FAE._
import FAE._

scala> run(...)
```

다음은 실행한 결과 예시이다.

```scala
// (1 + 2) - (3 + 4)
run(
  Sub(
    Add(Num(1), Num(2)),
    Add(Num(3), Num(4))
  )
)
```

```
((1 + 2) - (3 + 4)) | □                 | ∅
(1 + 2)             | □ - (3 + 4)       | ∅
1                   | (□ + 2) - (3 + 4) | ∅
2                   | (1 + □) - (3 + 4) | ∅
1 + 2               | □ - (3 + 4)       | ∅
(3 + 4)             | (3 - □)           | ∅
3                   | (3 - (□ + 4))     | ∅
4                   | (3 - (3 + □))     | ∅
3 + 4               | (3 - □)           | ∅
3 - 7               | □                 | ∅
-4
```

```scala
// (lambda x.lambda y.x + y) 1 2
run(
  App(
    App(
      Fun("x", Fun("y",
        Add(Id("x"), Id("y")))),
      Num(1)
    ),
    Num(2)
  )
)
```

```
((λx.λy.(x + y) 1) 2) | □                          | ∅
(λx.λy.(x + y) 1)     | (□ 2)                      | ∅
λx.λy.(x + y)         | ((□ 1) 2)                  | ∅
1                     | ((<λx.λy.(x + y), ∅> □) 2) | ∅
λy.(x + y)            | (□ 2)                      | [x -> 1]
2                     | (<λy.(x + y), [x -> 1]> □) | ∅
(x + y)               | □                          | [x -> 1, y -> 2]
x                     | (□ + y)                    | [x -> 1, y -> 2]
y                     | (1 + □)                    | [x -> 1, y -> 2]
1 + 2                 | □                          | [x -> 1, y -> 2]
3
```

순서대로 환원가능식, 계속, 환경이다. 사각형이 환원가능식의 계산 결과가 들어갈 자리이다. 예를 들면 `3 - □`은 \(\lambda v.3 - v\)에 해당한다. 

이 글에서는 인터프리터를 계속 전달 방식으로 구현해야 하는 이유가 전혀 보이지 않는다. 실제로도 FAE의 인터프리터를 계속 전달 방식으로 구현할 이유는 별로 없다. 계속 전달 방식의 한 가지 장점은 모든 함수 호출이 꼬리 호출이라는 것이다. 꼬리 호출 최적화가 존재하는 언어로 인터프리터를 구현한다면 스택 넘침이 일어나지 않는다. 반면 주어진 식이 매우 복잡하거나 많은 계산을 필요로 한다면 계속 전달 방식을 사용하지 않는 인터프리터는 스택 넘침을 일으킬 수도 있다. 그런데 안타깝게도 Scala 컴파일러는 꼬리 재귀 호출만 최적화를 할 수 있고 일반적인 꼬리 호출은 최적화를 하지 못한다. 따라서 계속 전달 방식을 사용해도 스택 넘침을 막을 수 없다. 그럼에도 이 글에서 계속 전달 방식을 사용하여 인터프리터를 구현한 이유는 다음 글에서 보게 될 것이다. 다음 글에서는 FAE에 기능을 추가할 것이다. 추가되는 기능은 인터프리터를 계속 전달 방식으로 구현했을 때 그렇지 않은 것에 비해 훨씬 쉽게 지원할 수 있다.

## 작은 걸음 의미

이제 FAE의 의미를 계속이 드러나도록 정의하겠다. 지금까지는 *큰 걸음 의미*(big-step semantics)만 보았다. 큰 걸음 의미는 *자연적 의미*(natural semantics)라고도 한다. 큰 걸음 의미는 직관적이고 인터프리터의 구현과 추론 규칙이 유사하다는 장점이 있다. 그러나 계속을 설명하는 데는 별로 도움이 되지 않는다. 큰 걸음 의미는 주어진 식이 가지는 값을 하나의 규칙으로 정의한다. 다음은 합의 큰 걸음 의미이다.

\[
\frac
{ \sigma\vdash e_1\Rightarrow v_1\quad
  \sigma\vdash e_2\Rightarrow v_2 }
{ \sigma\vdash e_1+e_2\Rightarrow v_1+v_2 }
\]

\(e_1+e_2\)의 값이 \(v_1+v_2\)임은 알 수 있지만 \(e_1\)이나 \(e_2\)를 계산하는 방법은 보이지 않는다. 따라서 계산의 세부 단계에 대해서는 \(v_1\)과 \(v_2\)를 더하는 것 말고는 어떠한 암시도 주지 않는다. 또한 계산의 순서도 드러나지 않는다. \(e_1\)을 먼저 계산할지 \(e_2\)를 먼저 계산할지 추론 규칙은 알려주지 않는다. 계속을 표현하는 데는 이 두 가지가 매우 중요하다. 계산의 세부 단계가 모두 보이고 계산 순서가 명확해야 계속을 정의할 수 있다. 그래서 이번 글에서는 큰 걸음 의미를 사용하지 않을 것이다.

*작은 걸음 의미*(small-step semantics)는 언어의 의미를 정의하는 또 다른 방법이다. 작은 걸음 의미는 큰 걸음 의미와 달리 어떤 식에서 그 식을 한 단계 계산하여 얻은 식으로 가는 관계를 정의한다. 따라서 계산의 모든 세부 단계와 계산 순서가 추론 규칙에 드러난다. 작은 걸음 의미는 계속을 정의하기 좋다. 그 외에도 장점이 있어 프로그래밍 언어 연구에서는 큰 걸음 의미보다는 작은 걸음 의미가 흔하게 사용된다.

작은 걸음 의미는 현재 상태에서 한 단계 계산한 다음 상태로 가는 관계를 정의한다. 한 단계 계산하는 것을 *환원*(reduction)이라고 한다. 여기서 상태를 무엇으로 정의하는지에 따라 여러 가지 형태의 작은 걸음 의미가 나올 수 있다. 많은 경우에 상태는 하나의 식이지만 이 글에서는 아니다. 상태가 무엇인지는 뒤에서 다시 보겠다. 작은 걸음 의미를 정의하는 것은 환원을 정의하는 것이기에 작은 걸음 의미를 환원 의미라고도 한다. 작은 걸음 의미를 환원 의미와 구조적 연산 의미로 나누기도 하지만 크게 중요한 구분은 아니라고 생각된다. 따라서 작은 걸음 의미는 곧 환원 의미라고 생각해도 무방하다.

FAE의 환원을 정의하기에 앞서 FAE의 문법부터 다시 쓰겠다.

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
&& | & \langle\lambda x.e,\sigma\rangle \\
\text{Environment} & \sigma & \in &
\textit{Id} \hookrightarrow \text{Value}
\end{array}
\]

FAE의 상태는 계산 스택과 값 스택의 순서쌍이다. 다음은 계산 스택과 값 스택을 정의한 것이다.

\[
\begin{array}{lrcl}
\text{Computation Stack} & k & ::= & \square \\
&& | & \sigma\vdash e::k \\
&& | & (+)::k \\
&& | & (-)::k \\
&& | & (@)::k \\
\text{Value Stack} & s & ::= & \blacksquare \\
&& | & v::s
\end{array}
\]

값 스택은 말 그대로 값들이 들어 있는 스택이다. 스택이므로 가장 위에 값을 추가하거나 가장 위에서 값을 꺼내는 것만 가능하다. 빈 스택은 검은색 사각형 \(\blacksquare\)이다.

계산 스택도 앞으로 해야 할 계산들이 들어 있는 스택이다. 스택의 가장 위에 있는 계산이 가장 먼저 해야 할 계산이다. 빈 계산 스택은 흰색 사각형 \(\square\)이다. 계산 스택이 비었다는 것은 더 이상 할 계산이 없다는 것을 뜻하므로 아무것도 할 필요 없음을 나타낸다. 계산 스택이 비면 계산이 끝난다. 가능한 계산에는 네 종류가 있다. \(\sigma\vdash e\)는 환경 \(\sigma\) 아래에서 식 \(e\)를 계산하여 그 결과를 값 스택에 넣으라는 뜻이다. \((+)\)는 값 스택에서 값을 두 개 꺼내 나중에 꺼낸 값에 먼저 꺼낸 값을 더하여 그 결과를 값 스택에 넣으라는 뜻이다. \((-)\)는 값 스택에서 값을 두 개 꺼내 나중에 꺼낸 값에서 먼저 꺼낸 값을 빼 그 결과를 값 스택에 넣으라는 뜻이다. \((@)\)은 값 스택에서 값을 두 개 꺼내 나중에 꺼낸 값을 먼저 꺼낸 값에 적용하여 그 결과를 값 스택에 넣으라는 뜻이다. 완료된 계산은 계산 스택에서 빠진다.

\(k\ ||\ s\)라고 쓰면 현재 상태에서 계산 스택은 \(k\), 값 스택은 \(s\)라는 것이다. \(k\)는 앞으로 해야 하는 계산, \(s\)는 계산할 때 사용할 값이다.

환원은 \(\rightarrow\)로 표기하겠다. 환원은 현재 상태에서 한 단계 계산하여 다음 상태로 가는 것이다. 따라서 환원은 계산 스택, 값 스택, 계산 스택, 값 스택의 관계이다.

\[\rightarrow \subseteq 
\text{Computation Stack}\times\text{Value Stack}
\times\text{Computation Stack}\times\text{Value Stack} \]

\(k_1\ ||\ s_1\rightarrow k_2\ ||\ s_2\)라고 쓰면 상태 \(k_1\ ||\ s_1\)이 상태 \(k_2\ ||\ s_2\)로 환원된다는 뜻이다.

어떤 상태를 계산하는 것은 그 상태가 더는 환원될 수 없을 때까지 환원을 반복하는 것이다. 만약 계산 스택이 빈 스택 \(\square\)이면 할 계산이 없으므로 환원될 수 없다. 그밖에도 덧셈이나 뺄셈을 해야 하는데 값 스택에서 꺼낸 값이 정수가 아니면 환원될 수 없다. 또, 함수 적용을 해야 하는데 값 스택에서 두 번째로 꺼낸 값이 클로저가 아니면 환원될 수 없다. 할 계산이 없어서 환원될 수 없는 것과 할 계산이 있는데도 환원될 수 없는 경우는 다르다. 할 계산이 없는 것은 모든 계산을 완료한 것이다. 즉, 프로그램의 정상적 종료이다. 반면 나머지 경우는 할 계산이 남아 있으나 값 스택에 비정상적인 값이 들어 있어 환원을 멈춘 것이다. 이는 프로그램의 실행 시간 오류, 즉 비정상적 종료이다.

환원을 반복하는 것은 \(\rightarrow^\ast\)라 표기하겠다. \(k_1\ ||\ s_1\rightarrow k_2\ ||\ s_2\)이고 \(k_2\ ||\ s_2\rightarrow k_3\ ||\ s_3\)이며, ..., \(k_{n-1}\ ||\ s_{n-1}\rightarrow k_n\ ||\ s_n\)이면 \(k_1\ ||\ s_1\rightarrow^\ast k_n\ ||\ s_n\)이다. 만약 계산의 각 단계를 모두 보이고 싶을 때는 \(k_1\ ||\ s_1\rightarrow k_2\ ||\ s_2\rightarrow k_3\ ||\ s_3\rightarrow k_4\ ||\ s_4\)처럼 \(\rightarrow\)를 여러 개 써서 표현하겠다. 이는 정의한 표현법은 아니지만 편의상 그렇게 쓰겠다는 것이다. 한편 \(\rightarrow^\ast\)의 엄밀한 정의는 다음과 같다.

\[k\ ||\ s\rightarrow^{\ast}k\ ||\ s\]

\[
\frac
{ k\ ||\ s\rightarrow^{\ast}k'' \ ||\ s'' \quad
  k'' \ ||\ s'' \rightarrow k'\ ||\ s' }
{ k\ ||\ s\rightarrow^{\ast}k'\ ||\ s' }
\]

주의할 점은 \(\rightarrow^\ast\)의 정의는 환원을 할 수 없을 때까지 반복하라는 의미를 포함하지 않는다는 점이다. 단순히 0번 또는 그 이상의 환원을 하여 얻은 상태를 보여줄 뿐이다. 따라서 \(k_1\ ||\ s_1\rightarrow^\ast k_n\ ||\ s_n\)뿐 아니라 \(k_1\ ||\ s_1\rightarrow^\ast k_1\ ||\ s_1\), \(k_1\ ||\ s_1\rightarrow^\ast k_2\ ||\ s_2\), \(k_1\ ||\ s_1\rightarrow^\ast k_3\ ||\ s_3\) 등이 모두 참이다.

\(\rightarrow^\ast\)는 \(\rightarrow\)의 반사적 *추이적 닫힘*(reflexive transitive closure)이다. 그 뜻은 \(\rightarrow^\ast\)가 \(\rightarrow\)를 포함하는 관계이면서 동시에 *반사적*(reflexive)이고 *추이적*(transitive)이라는 뜻이다. 환원을 한 번 한 것은 0번 이상 한 것이다. \(k_1\ ||\ s_1\rightarrow k_2\ ||\ s_2\)이면 \(k_1\ ||\ s_1\rightarrow^\ast k_2\ ||\ s_2\)이기에 \(\rightarrow^\ast\)가 \(\rightarrow\)를 포함한다. 상태를 환원하지 않으면 자기 자신이다. 즉 모든 \(k\ ||\ s\)에 대해 \(k\ ||\ s\rightarrow^\ast k\ ||\ s\)이므로 \(\rightarrow^\ast\)는 반사적 관계이다. 또한 \(k_1\ ||\ s_1\)을 환원하다 보니 \(k_2\ ||\ s_2\)가 되었고 \(k_2\ ||\ s_2\)를 환원하다 보니 \(k_3\ ||\ s_3\)이 되었다면 \(k_1\ ||\ s_1\)을 환원하다 보면 언젠가는 \(k_2\ ||\ s_2\)를 거쳐 \(k_3\ ||\ s_3\)이 나올 것이다. 따라서 모든 \(k_1\ ||\ s_1\), \(k_2\ ||\ s_2\), \(k_3\ ||\ s_3\)에 대해 \(k_1\ ||\ s_1\rightarrow^\ast k_2\ ||\ s_2\)이고 \(k_2\ ||\ s_2\rightarrow^\ast k_3\ ||\ s_3\)이면 \(k_1\ ||\ s_1\rightarrow^\ast k_3\ ||\ s_3\)이므로 \(\rightarrow^\ast\)는 추이적 관계이다.

이제 \(\rightarrow\), 즉 환원을 정의할 차례이다. 그 전에 현재 정의하는 작은 걸음 의미와 기존의 큰 걸음 의미의 관계를 먼저 보겠다. 이는 환원을 정의하는 방법을 생각하는 데 도움이 될 것이다. \(\sigma\vdash e\Rightarrow v\)는 환경 \(\sigma\) 아래에서 식 \(e\)를 계산한 결과가 값 \(v\)임을 나타낸다. 작은 걸음 의미가 옳게 정의된다면 다음이 성립한다.

\[\forall \sigma.\forall e.\forall v.(\sigma\vdash e\Rightarrow v)\leftrightarrow(\sigma\vdash e::\square\ ||\ \blacksquare\rightarrow^\ast\square\ ||\ v::\blacksquare)\]

이는 위에서 \(\sigma\vdash e\)는 환경 \(\sigma\) 아래에서 식 \(e\)를 계산하여 그 결과를 값 스택에 넣으라는 뜻이라 설명한 것에 따른다. 조금 더 일반적으로는 다음이 성립한다.

\[\forall \sigma.\forall e.\forall v.\forall k.\forall s.(\sigma\vdash e\Rightarrow v)\leftrightarrow(\sigma\vdash e::k\ ||\ s\rightarrow^\ast k\ ||\ v::s)\]

현재 상태가 \(\sigma\vdash e::k\ ||\ s\)라면 환원가능식은 \(e\), 계속은 \(k\ ||\ s\)로 볼 수 있다. 여기서 \(k\ ||\ s\)는 함수 형태로 나타낼 수 있다. 이를 위해 우선 \(k\ ||\ v::s\rightarrow^\ast \square\ ||\ v'::\blacksquare\)일 때 \(\mathit{eval}(k\ ||\ v::s)=v'\)이라고 정의하겠다. 그럼 계속을 함수로 쓴 것은 \(\lambda v.\mathit{eval}(k\ ||\ v::s)\)이다. 다른 말로는, 계속이 \(k\ ||\ s\)라는 것은 계속이 환원가능식을 계산하여 얻은 결과 \(v\)를 값 스택에 추가하여 얻은 \(k\ ||\ v::s\)를 환원이 불가능할 때까지 계산해서 스택에 남은 값 \(v'\)을 찾는 것이라는 것이다.

두 가지 예를 보겠다. 임의의 식 \(e\)를 생각하자. \(\sigma\) 아래에서 \(e\)를 계산한 결과가 \(v\)라 하자. \(\sigma\vdash e::\square\ ||\ \blacksquare\)가 현재 상태라면 \(e\)가 환원가능식이고 \(\square\ ||\ \blacksquare\)가 계속이다. 계속은 계산 스택이 비었으므로 아무것도 하지 않는다. \(e\)를 계산한 결과가 \(v\)이고 계속은 아무것도 하지 않으므로 전체 계산 결과는 \(v\)이다. 이는 계속을 함수 형태로 봐도 동일하다. \(\lambda v.\mathit{eval}(\square\ ||\ v::\blacksquare)\)에 \(v\)를 적용하면 \(\square\ ||\ v::\blacksquare\rightarrow^\ast \square\ ||\ v::\blacksquare\)가 성립하므로 결과가 \(v\)이다.

이번에는 \(\sigma\vdash e::(+)::\square\ ||\ n_1::\blacksquare\)을 생각해 보자. \(\sigma\) 아래에서 \(e\)를 계산한 결과가 정수 \(n_2\)라 하겠다. 환원가능식은 \(e\), 계속은 \((+)::\square\ ||\ n_1::\blacksquare\)이다. 이는 \(n_1\)에 주어진 값을 더하라는 의미임을 짐작할 수 있다. 함수로 보더라도 \(\lambda v.\mathit{eval}((+)::\square\ ||\ v::n_1::\blacksquare)\)이다. 위에서 \((+)\)는 값 스택에서 값을 두 개 꺼내 나중에 꺼낸 값에 먼저 꺼낸 값을 더하여 그 결과를 값 스택에 넣으라는 뜻이라 설명하였다. 계속을 \(n_2\)에 적용한 결과는 \(\mathit{eval}((+)::\square\ ||\ n_2::n_1::\blacksquare)\)이다. 이는 값 스택에서 \(n_2\)와 \(n_1\)을 꺼내 둘을 더하여 스택에 넣었을 때 스택에 들어 있는 값이므로 \(n_1+n_2\)이다. 환원가능식을 계산한 결과에 계속을 적용한 결과가 전체 결과이므로 프로그램의 최종 결과는 \(n_1+n_2\)이다. 이 두 개의 예시가 작은 걸음 의미와 환원가능식, 계속의 직관적인 이해에 도움이 되었으면 한다.

이제 정말로 환원을 정의할 것이다. 먼저 단순한 두 경우를 보겠다. 해야 할 계산이 \((+)\)인 경우와 \((-)\)인 경우이다.

\[(+)::k\ ||\ n_2::n_1::s\rightarrow k\ ||\ n_1+n_2::s\]

\[(-)::k\ ||\ n_2::n_1::s\rightarrow k\ ||\ n_1-n_2::s\]

이는 위에서 설명한 \((+)\)와 \((-)\)의 뜻에서 자연스럽게 나온다. \((+)\)는 값 스택에서 값을 두 개 꺼내 나중에 꺼낸 값에 먼저 꺼낸 값을 더하여 그 결과를 값 스택에 넣으라는 뜻이다. \((-)\)는 값 스택에서 값을 두 개 꺼내 나중에 꺼낸 값에서 먼저 꺼낸 값을 빼 그 결과를 값 스택에 넣으라는 뜻이다.

해야 할 계산이 \((@)\)인 경우는 비슷하지만 약간 복잡하다.

\[(@)::k\ ||\ v::\langle\lambda x.e,\sigma\rangle::s\rightarrow \sigma\lbrack x\mapsto v\rbrack\vdash e::k\ ||\ s\]

\((@)\)은 값 스택에서 값을 두 개 꺼내 나중에 꺼낸 값을 먼저 꺼낸 값에 적용하여 그 결과를 값 스택에 넣으라는 뜻이라 하였다. 함수 적용의 결과를 얻기 위해서는 함수 몸통이 나타내는 값을 구해야 한다. 큰 걸음 의미라면 규칙 하나가 함수 몸통의 값을 구하는 부분까지 다 포함해야 한다. 그러나 작은 걸음 의미이므로 함수 몸통을 계산해야 한다는 내용을 계산 스택에 추가하는 것으로 충분하다.

이제 가장 먼저 해야 할 계산이 식을 계산하는 일인 경우만 남았다. 식이 정수거나, 식별자거나, 함수라면 나타내는 값이 바로 나오므로 비교적 간단하다.

\[\sigma\vdash n::k\ ||\ s\rightarrow k\ ||\ n::s\]

\[
\frac
{ x\in\mathit{Domain}(\sigma) }
{ \sigma\vdash x::k\ ||\ s\rightarrow k\ ||\ \sigma(x)::s }
\]

\[\sigma\vdash \lambda x.e::k\ ||\ s\rightarrow k\ ||\ \langle\lambda x.e,\sigma\rangle ::s\]

완료한 계산은 계산 스택에서 빠지고 값 스택에는 결괏값이 추가된다.

식이 합인 경우를 보자. \(e_1\)을 \(\sigma\) 아래에서 계산한 결과는 \(n_1\), \(e_2\)를 \(\sigma\) 아래에서 계산한 결과는 \(n_2\)라 가정하자. 현재 상태를 \(\sigma\vdash e_1+e_2::k\ ||\ s\)라 하자. 환원을 올바르게 정의한다면 \(\sigma\vdash e_1+e_2::k\ ||\ s\rightarrow^\ast k\ ||\ n_1+n_2::s\)가 참이어야 한다. 일단 확실한 것은 \(e_1+e_2\)를 계산하려면 \(e_1\)도 계산해야 하고 \(e_2\)도 계산해야 한다는 점이다. 또, \(e_1\)을 \(e_2\)보다 먼저 계산해야 한다는 것이 FAE의 의미이다. 따라서 다음처럼 정의하는 것을 첫 시도로 할 수 있다.

\[\sigma\vdash e_1+e_2::k\ ||\ s\rightarrow \sigma\vdash e_1::\sigma\vdash e_2::k\ ||\ s\]

그럼 다음이 성립한다.

\[
\begin{array}{lrcr}
& \sigma\vdash e_1+e_2::k & || & s \\
\rightarrow & \sigma\vdash e_1::\sigma\vdash e_2::k & || & s \\
\rightarrow^\ast & \sigma\vdash e_2::k & || & n_1::s \\
\rightarrow^\ast & k & || & n_2::n_1::s \\
\end{array}
\]

우리가 원하는 \(k\ ||\ n_1+n_2::s\)에 한 단계 부족하다. 만약 \(k\) 대신 \((+)::k\)가 계산 스택이었다면 다음이 성립할 것이다.

\[
\begin{array}{lrcr}
& (+)::k & || & n_2::n_1::s \\
\rightarrow & k & || & n_1+n_2::s
\end{array}
\]

그러므로 \(\sigma\vdash e_1::\sigma\vdash e_2::k\)가 아니라 \(\sigma\vdash e_1::\sigma\vdash e_2::(+)::k\)가 다음 상태가 되어야 옳다. 다음은 올바른 환원이다.

\[\sigma\vdash e_1+e_2::k\ ||\ s\rightarrow \sigma\vdash e_1::\sigma\vdash e_2::(+)::k\ ||\ s\]

이제 다음이 성립한다.

\[
\begin{array}{lrcr}
& \sigma\vdash e_1+e_2::k & || & s \\
\rightarrow & \sigma\vdash e_1::\sigma\vdash e_2::(+)::k & || & s \\
\rightarrow^\ast & \sigma\vdash e_2::(+)::k & || & n_1::s \\
\rightarrow^\ast & (+)::k & || & n_2::n_1::s \\
\rightarrow & k & || & n_1+n_2::s
\end{array}
\]

식이 차인 경우와 적용인 경우는 완전히 같은 논리로 환원할 수 있다.

\[\sigma\vdash e_1-e_2::k\ ||\ s\rightarrow \sigma\vdash e_1::\sigma\vdash e_2::(-)::k\ ||\ s\]

\[\sigma\vdash e_1\ e_2::k\ ||\ s\rightarrow \sigma\vdash e_1::\sigma\vdash e_2::(@)::k\ ||\ s\]

다음은 환원에 대한 모든 규칙을 모은 것이다. 환원 전후가 쉽게 비교되도록 한 규칙을 두 줄에 걸쳐 작성했다.

\[
\begin{array}{lrcr}
&(+)::k& ||& n_2::n_1::s \\
\rightarrow& k& ||& n_1+n_2::s\\
&(-)::k& ||& n_2::n_1::s\\
\rightarrow& k& ||& n_1-n_2::s\\
&(@)::k& ||& v::\langle\lambda x.e,\sigma\rangle::s\\
\rightarrow& \sigma\lbrack x\mapsto v\rbrack\vdash e::k& ||& s\\
&\sigma\vdash n::k& ||&s\\
\rightarrow& k& ||&n::s\\
&\sigma\vdash x::k& ||&s\\
\rightarrow& k& ||& \sigma(x)::s\\
&\sigma\vdash \lambda x.e::k& ||& s\\
\rightarrow& k& ||&\langle\lambda x.e,\sigma\rangle ::s\\
&\sigma\vdash e_1+e_2::k& ||& s\\
\rightarrow& \sigma\vdash e_1::\sigma\vdash e_2::(+)::k& ||& s\\
&\sigma\vdash e_1-e_2::k& ||& s\\
\rightarrow& \sigma\vdash e_1::\sigma\vdash e_2::(-)::k& ||& s\\
&\sigma\vdash e_1\ e_2::k& ||& s\\
\rightarrow& \sigma\vdash e_1::\sigma\vdash e_2::(@)::k& ||& s\\
\end{array}
\]

식을 직접 계산하지 않는 \((+)\), \((-)\), \((@)\)은 값 스택의 값 개수를 한 개 줄인다. 반대로 식을 직접 계산하는 \(\sigma\vdash e\)는 값 스택의 값을 한 개 늘린다. 이는 언제나 성립한다. 그러므로 계속이 아무것도 안 하는 것일 때 어떤 식을 계산하면 최종적으로 값 스택에는 값이 정확히 하나 들어있다. 그 값이 그 식을 계산하여 나온 값이다.

아래는 작은 걸음 의미를 따라 환원을 반복한 예시이다.

\[
\begin{array}{lrcr}
& \emptyset\vdash(1+2)-(3+4)::\square &||& \blacksquare \\
\rightarrow & \emptyset\vdash1+2::\emptyset\vdash3+4::(-)::\square &||& \blacksquare \\
\rightarrow & \emptyset\vdash1::\emptyset\vdash2::(+)::\emptyset\vdash3+4::(-)::\square &||& \blacksquare \\
\rightarrow & \emptyset\vdash2::(+)::\emptyset\vdash3+4::(-)::\square &||& 1::\blacksquare \\
\rightarrow & (+)::\emptyset\vdash3+4::(-)::\square &||& 2::1::\blacksquare \\
\rightarrow & \emptyset\vdash3+4::(-)::\square &||& 3::\blacksquare \\
\rightarrow & \emptyset\vdash3::\emptyset\vdash4::(+)::(-)::\square &||& 3::\blacksquare \\
\rightarrow & \emptyset\vdash4::(+)::(-)::\square &||& 3::3::\blacksquare \\
\rightarrow & (+)::(-)::\square &||& 4::3::3::\blacksquare \\
\rightarrow & (-)::\square &||& 7::3::\blacksquare \\
\rightarrow & \square &||& -4::\blacksquare \\
\end{array}
\]

\[
\begin{array}{lrcr}
& \emptyset\vdash(\lambda x.\lambda y.x+y)\ 1\ 2)::\square &||& \blacksquare \\
\rightarrow & \emptyset\vdash(\lambda x.\lambda y.x+y)\ 1::\emptyset\vdash2::(@)::\square &||& \blacksquare \\
\rightarrow & \emptyset\vdash\lambda x.\lambda y.x+y::\emptyset\vdash 1::(@)::\emptyset\vdash2::(@)::\square &||& \blacksquare \\
\rightarrow & \emptyset\vdash 1::(@)::\emptyset\vdash2::(@)::\square &||& \langle\lambda x.\lambda y.x+y,\emptyset\rangle::\blacksquare \\
\rightarrow & (@)::\emptyset\vdash2::(@)::\square &||& 1::\langle\lambda x.\lambda y.x+y,\emptyset\rangle::\blacksquare \\
\rightarrow & \lbrack x\mapsto 1\rbrack\vdash\lambda y.x+y::\emptyset\vdash2::(@)::\square &||& \blacksquare \\
\rightarrow & \emptyset\vdash2::(@)::\square &||& \langle\lambda y.x+y,\lbrack x\mapsto 1\rbrack\rangle::\blacksquare \\
\rightarrow & (@)::\square &||& 2::\langle\lambda y.x+y,\lbrack x\mapsto 1\rbrack\rangle::\blacksquare \\
\rightarrow & \lbrack x\mapsto 1,y\mapsto 2\rbrack\vdash x+y::\square &||& \blacksquare \\
\rightarrow & \lbrack x\mapsto 1,y\mapsto 2\rbrack\vdash x::\lbrack x\mapsto 1,y\mapsto 2\rbrack\vdash y::(+)::\square &||& \blacksquare \\
\rightarrow & \lbrack x\mapsto 1,y\mapsto 2\rbrack\vdash y::(+)::\square &||& 1::\blacksquare \\
\rightarrow & (+)::\square &||& 2::1::\blacksquare \\
\rightarrow & \square &||& 3::\blacksquare \\
\end{array}
\]

마지막으로 작은 걸음 의미를 어떻게 하면 인터프리터의 구현과 연결 지을 수 있는지 보겠다.

\[
\begin{array}{lrcr}
&\sigma\vdash e_1+e_2::k& ||& s\\
\rightarrow& \sigma\vdash e_1::\sigma\vdash e_2::(+)::k& ||& s\\
\end{array}
\]

위 규칙에서 환원가능식이 \(e_1+e_2\)일 때 계속은 \(k\ ||\ s\)이다. 이를 함수로 표현하면 \(\lambda v.\mathit{eval}(k\ ||\ v::s)\)이다. 이 함수를 \(K\)라 하겠다. 한 번 환원한 결과는 \(\sigma\vdash e_1::\sigma\vdash e_2::(+)::k\ ||\ s\)이다. 여기서의 환원가능식은 \(e_1\)이고 계속은 \(\sigma\vdash e_2::(+)::k\ ||\ s\)이다. 계속을 함수로 표현하면 \(\lambda v_1.\mathit{eval}(\sigma\vdash e_2::(+)::k\ ||\ v_1::s)\)이다. 계속의 몸통에 있는 \(\sigma\vdash e_2::(+)::k\ ||\ v_1::s\)를 보자. 여기서는 환원가능식이 \(e_2\)이고 계속은 \((+)::k\ ||\ v_1::s\)이다. 함수 형태로는 \(\lambda v_2.\mathit{eval}((+)::k\ ||\ v_2::v_1::s)\)이다. \((+)::k\ ||\ v_2::v_1::s\rightarrow k\ ||\ v_1+v_2::s\)임은 이미 알고 있다. 또한, 앞에서 \(K\)를 \(\lambda v.\mathit{eval}(k\ ||\ v::s)\)라고 정의하였다. 그러므로 \(\lambda v_2.\mathit{eval}((+)::k\ ||\ v_2::v_1::s)\)는 \(\lambda v_2.\mathit{eval}(k\ ||\ v_1+v_2::s)\)와 같고, 결국 \(\lambda v_2.K(v_1+v_2)\)이다. 따라서 \(e_2\)를 계산할 때의 계속은 \(\lambda v_2.K(v_1+v_2)\)이고 코드로 나타내면 `v2 => k(numVAdd(v1, v2))`이다. 그러므로 \(\mathit{eval}(\sigma\vdash e_2::(+)::k\ ||\ v_1::s)\)는 `interp(e2, env, v2 => k(numVAdd(v1, v2)))`이다. \(e_1\)의 계속이 \(\lambda v_1.\mathit{eval}(\sigma\vdash e_2::(+)::k\ ||\ v_1::s)\)이므로 코드로는 `v1 => interp(e2, env, v2 => k(numVAdd(v1, v2)))`이다. 최종적으로는 \(e_1+e_2\)를 계산할 때 계속이 \(K\)인 것이 `interp(e1, env, v1 => interp(e2, env, v2 => k(numVAdd(v1, v2))))`이고 이는 앞에서 본 코드와 같다.
