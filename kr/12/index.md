*람다 대수*(lambda calculus)는 변수, 람다 요약, 함수 적용으로만 이루어진 언어이다. 이번 글에서는 람다 대수로 얼마나 많은 것을 할 수 있는지 알아본다.

## 문법

다음은 람다 대수의 요약 문법이다.

\[
\begin{array}{lrcl}
\text{Variable} & x & \in & \textit{Id} \\
\text{Expression} & e & ::= & x \\
&& | & \lambda x.e \\
&& | & e\ e \\
\text{Value} & v & ::= & \langle \lambda x.e,\sigma \rangle \\
\text{Environment} & \sigma & \in & \textit{Id}\hookrightarrow\text{Value}
\end{array}
\]

FAE와 달리 정수, 합, 차가 존재하지 않는다. 식은 변수이거나 람다 요약이거나 함수 적용이다. 값은 클로저만 가능하며 정숫값은 없다.

## 의미

람다 대수의 의미는 FAE와 똑같이 정의할 수 있다.

\[\Rightarrow\subseteq\text{Environment}\times\text{Expression}\times\text{Value}\]

\[
\frac
{ x\in\mathit{Domain}(\sigma) }
{ \sigma\vdash x\Rightarrow \sigma(x)}
\]

\[
\sigma\vdash \lambda x.e\Rightarrow \langle\lambda x.e,\sigma\rangle
\]

\[
\frac
{ \sigma\vdash e_1\Rightarrow\langle\lambda x.e,\sigma'\rangle \quad
  \sigma\vdash e_2\Rightarrow v' \quad
  \sigma'\lbrack x\mapsto v'\rbrack\vdash e\Rightarrow v }
{ \sigma\vdash e_1\ e_2\Rightarrow v }
\]

## Church 수

*Church 수*(Church numeral)는 수와 덧셈이나 곱셈 같은 수를 다루는 연산을 람다 대수로 인코딩 하는 방법이다. 편의상, AE에 두 식의 차 대신 두 식의 곱이 있다고 가정하고 AE를 람다 대수로 인코딩 하겠다. 우선 자연수를 인코딩 하는 방법부터 보자.

\[
\begin{array}{rcl}
\mathit{encode}(0)&=&\lambda f.\lambda x.x \\
\mathit{encode}(1)&=&\lambda f.\lambda x.f\ x \\
\mathit{encode}(2)&=&\lambda f.\lambda x.f\ (f\ x) \\
\mathit{encode}(3)&=&\lambda f.\lambda x.f\ (f\ (f\ x)) \\
&\cdots&
\end{array}
\]

직관적으로 설명하면, 자연수 \(n\)은 어떤 함수로 인코딩 되며, 그 함수는 다른 함수를 인자로 받아, 그 함수를 \(n\) 번 반복해서 적용하는 함수를 결과로 낸다. 즉, \(\mathit{encode}(n)=f\mapsto f^n\)이다. 또한, 어떤 자연수 \(n\)과 함수 \(f\)가 있을 때, \(n\ f\)는 \(f^n\)이다.

Church 수가 말이 되는 이유는 합을 인코딩 함으로써 잘 드러난다.

\[
\begin{array}{rcl}
+&\equiv&\lambda n.\lambda m.\lambda f.\lambda x.n\ f\ (m\ f\ x) \\
\mathit{encode}(e_1+e_2)&=&\lambda f.\lambda x.\mathit{encode}(e_1)\ f\ (\mathit{encode}(e_2)\ f\ x)
\end{array}
\]

어떤 자연수 \(n\), \(m\)과 함수 \(f\)가 있을 때, \((n+m)\ f\)는 \(\lambda x.n\ f\ (m\ f\ x)\)인데, \(n\ f\)는 \(f^n\), \(m\ f\)는 \(f^m\)이므로, \(\lambda x.f^n\ (f^m\ x)\)와 같다. 이는 \(\lambda x.f^{n+m}\ x\), 즉 \(f^{n+m}\)과 같으므로, \(n+m\)은 \(f\mapsto f^{n+m}\)이다. 따라서, \(\mathit{encode}\) 함수가 합을 올바르게 인코딩 하였다.

곱도 인코딩 할 수 있다.

\[
\begin{array}{rcl}
\times&\equiv&\lambda n.\lambda m.\lambda f.\lambda x.n\ (m\ f)\ x \\
\mathit{encode}(e_1\times e_2)&=&\lambda f.\lambda x.\mathit{encode}(e_1)\ (\mathit{encode}(e_2)\ f)\ x
\end{array}
\]

어떤 자연수 \(n\), \(m\)과 함수 \(f\)가 있을 때, \((n\times m)\ f\)는 \(\lambda x.n\ (m\ f)\ x\)인데, \(m\ f\)는 \(f^m\)이므로, \(\lambda x.n\ f^m\ x\)와 같다. 또한, \(n\ f\)는 \(f^n\)이므로, \(n\ f^m\)은 \( (f^m )^n \)과 같다. \(n\times m\)이 \(f\mapsto f^{n\times m}\)이므로 곱이 올바르게 인코딩 되었다.

뺄셈과 나눗셈도 람다 대수로 인코딩 할 수 있으며, 더 나아가 정수나 유리수 및 그에 대한 연산 역시 인코딩 할 수 있다. 이 글에서는 다루지 않는다.

## Church 불

*Church 불*(Church Boolean)은 참과 거짓 및 조건식, 논리합, 논리곱 등을 람다 대수로 인코딩 하는 방법이다. 지금까지 정의한 언어에는 불 값이 존재하지 않았기에, 먼저 AE에 참, 거짓, 조건식을 추가한 BAE를 간단히 정의하겠다.

\[
\begin{array}{lrcl}
\text{Boolean} & b & ::= & true \\
&& | & false \\
\text{Expression} & e & ::= & \cdots \\
&& | & b \\
&& | & \textsf{if}\ e\ e\ e \\
\text{Value} & v & ::= & \cdots \\
&& | & b
\end{array}
\]

AE에 비하여 추가된 부분만 정의하였다. 메타변수 \(b\)는 불 값을 나타낸다.

조건식은 세 개의 부분식을 가지며, 첫 부분식은 조건, 두 번째 부분식은 참에 해당하는 가지, 세 번째 부분식은 거짓에 해당하는 가지이다.

\[
\vdash b\Rightarrow b
\]

\[
\frac
{ \vdash e_1\Rightarrow true \quad \vdash e_2\Rightarrow v }
{ \vdash \textsf{if}\ e_1\ e_2\ e_3\Rightarrow v}
\]

\[
\frac
{ \vdash e_1\Rightarrow false \quad \vdash e_3\Rightarrow v }
{ \vdash \textsf{if}\ e_1\ e_2\ e_3\Rightarrow v}
\]

조건이 참이면 참 가지의 식만 계산하고, 거짓이면 거짓 가지의 식만 계산한다.

먼저 참과 거짓을 인코딩 하는 방법을 보자.

\[
\begin{array}{rcl}
\mathit{encode}(true)&=&\lambda a.\lambda b.a\ \_ \\
\mathit{encode}(false)&=&\lambda a.\lambda b.b\ \_ \\
\end{array}
\]

\(\_ \)는 임의의 식이다. 인자를 사용하지 않을 것이기에 그 값이 중요하지 않아서 밑줄 문자로 표기하였다. \(\lambda x.x\) 같은 식이 대신 있다고 생각해도 된다.

자연수의 인코딩을 이해하기 위해서 합과 곱의 인코딩을 본 것처럼, 불 값의 인코딩을 이해하기 위해서는 조건식의 인코딩을 봐야 한다.

\[
\begin{array}{rcl}
\mathit{encode}(\textsf{if}\ e_1\ e_2\ e_3)&=&
\mathit{encode}(e_1)\ (\lambda\_ .\mathit{encode}(e_2))\ (\lambda\_ .\mathit{encode}(e_3))
\end{array}
\]

\(\_ \)는 매개변수가 사용되지 않는다는 것을 표현하기 위해 사용한 것으로, \(x\) 같은 매개변수 이름이 있다고 생각해도 된다. \(e_1\)이 참을 나타낸다고 가정해보자. 그러면 전체 식은 \( (\lambda a.\lambda b.a \_ )\ (\lambda\_ .\mathit{encode}(e_2))\ (\lambda\_ .\mathit{encode}(e_3))\)이 되며, 이는 \( (\lambda\_ .\mathit{encode}(e_2))\ \_ \)와 같다. 따라서, \(\mathit{encode}(e_2)\)를 계산한 결과와 같은 값을 나타내며, \(\mathit{encode}(e_3)\)은 계산하지 않는다. 마찬가지로, \(e_1\)이 거짓을 나타낸다면, 전체 식은 \(\mathit{encode}(e_3)\)을 계산한 결과와 같은 값을 나타내며, \(\mathit{encode}(e_2)\)는 계산하지 않는다.

다음의 인코딩을 생각해보자.

\[
\begin{array}{rcl}
\mathit{encode}(true)&=&\lambda a.\lambda b.a \\
\mathit{encode}(false)&=&\lambda a.\lambda b.b \\
\textsf{if}&\equiv&\lambda c.\lambda a.\lambda b.c\ a\ b \\
\mathit{encode}(\textsf{if}\ e_1\ e_2\ e_3)&=&\mathit{encode}(e_1)\ \mathit{encode}(e_2)\ \mathit{encode}(e_3)
\end{array}
\]

기존의 인코딩보다 간단하지만, 참과 거짓 가지를 언제나 모두 계산한다는 문제가 있다. 이 글에서 정의한 람다 대수는 *조급한 계산*(eager evaluation)을 사용하며, *느긋한 계산*(lazy evaluation)을 하도록 정의하였다면 두 번째 인코딩을 사용할 수 있다. 느긋한 계산에 관해서는 나중 글에서 다룬다.

## 표현력

람다 대수는 얼마나 많은 것을 표현할 수 있을까? 람다 대수로 인코딩 할 수 있는 함수를 *람다 계산 가능한*(lambda computable) 함수라고 부른다. 유사하게, *Turing 기계*(Turing machine)로 구현 할 수 있는 함수를 *Turing 계산 가능한*(Turing computable) 함수라고 한다. 람다 계산 가능한 함수는 Turing 계산 가능하고, Turing 계산 가능한 함수는 람다 계산 가능한 함수임이 증명되어 있다. 따라서, Turing 기계로 구현할 수 있는 계산의 집합과 람다 대수로 인코딩 할 수 있는 계산의 집합은 같으며, 람다 대수가 *Turing 완전*(Turing complete)하다고 말할 수 있다. Turing 기계가 할 수 있는 계산은 현재의 컴퓨터가 할 수 있는 계산과 거의 같다. (컴퓨터의 메모리는 유한하지만, Turing 기계의 테이프는 무한하다.) 따라서, 람다 대수는 컴퓨터가 할 수 있는 모든 일을 표현하는 언어이며, 그런 의미에서 람다 대수는 ‘유일한’ 프로그래밍 언어이다.

## 감사의 말

글을 확인하고 의견을 주신 류석영 교수님께 감사드립니다.
