프로그래밍 언어는 문법과 의미로 정의된다. 문법은 어떤 코드가 그 프로그래밍 언어의 코드인지 결정한다. 의미는 그 프로그래밍 언어의 코드가 무엇을 나타내는지 결정한다. 의미를 정의하지 않는다면, 프로그래머가 작성한 코드는 그냥 문자열에 불과하다.

관심 있는 성질이 무엇이냐에 따라 의미는 다양한 형태를 가진다. 프로그램이 컴퓨터의 메모리를 어떻게 변경하는지에 관심이 있다면, 메모리가 무엇인지 정의한 다음, 주어진 코드가 메모리를 어떻게 변경하는지 정의하는 것이 언어의 의미를 정의하는 것이다. 사용자의 입력을 받고 정보를 출력하는 것이 중요하다면, 사용자의 입력과 프로그램의 출력이 무엇인지 정의하고, 코드가 주어진 입력에 무엇을 출력하는지 정의하는 것이 언어의 의미를 정의하는 것이다. 프로그래밍 언어 수업에서 관심을 가지는 대상은 함수형 언어이며, 함수형 언어는 주어진 식을 계산했을 때 어떤 값이 나오는지가 언어의 의미이다.

## 의미를 정의하는 방법

의미는 여러 방법으로 정의할 수 있다. 다양한 방법이 존재하나, 대표적으로는 *표시 의미*(denotational semantics)와 *연산 의미*(operational semantics)가 있으며, 그 밖에도 *공리적 의미*(axiomatic semantics) 등이 있다. 표시 의미는 수학적인 방법을 통해서 각 프로그램이 나타내는 값을 정의한다. 예를 들어, 명령형 언어의 의미를 표시 의미로 정의하면 한 프로그램이 나타내는 값은 상태에서 상태로 가는 함수이다. 반대로, 연산 의미는 추론 규칙 같은 논리적 선언을 통하여 프로그램의 실행 과정을 표현한다. 연산 의미는 표시 의미보다 인터프리터의 구현에 가깝지만, 연산 의미 역시 구현에 대한 직접적인 정보를 제공하지 않을 수 있다.

연산 의미를 정의하는 방법에도 여러 가지가 존재한다. *자연적 의미*(natural semantics), *구조적 연산 의미*(structural operational semantics; SOS), *환원 의미*(reduction semantics), *요약 기계 의미*(abstract machine semantics) 등이 있다. 프로그래밍 언어 수업에서 주로 다루는 의미는 자연적 의미로, *큰 걸음 의미*(big-step semantics)라고도 부른다. 자연적 의미는 여러 개의 추론 규칙으로 정의되며, 한 추론 규칙이 주어진 식이 나타내는 값을 정의한다는 특징이 있다. 이는 구조적 연산 의미와 환원 의미를 포함하는 *작음 걸음 의미*(small-step semantics)의 추론 규칙이 주어진 식을 다른 식으로 변환하는 것과 비교하였을 때 큰 차이점이다. 즉, 큰 걸음 의미는 한 번에 크게 걸음으로써 바로 값을 만들어내지만, 작은 걸음 의미는 값에 도달하기 위해서는 여러 번 작게 걸어야 한다.

의미를 정의하는 방법마다 각자의 고유한 특징이 있으며, 하고자 하는 일에 따라 언어의 의미를 다른 방법으로 정의한다. 예를 들면, 언어의 *구체적 의미*(concrete semantics)와 *요약 의미*(abstract semantics)를 모두 표시 의미로 정의함으로써, 둘 사이의 관계를 수학적으로 정리하고 *요약 실행*(abstract interpretation)의 *안전성*(soundness)을 보일 수 있다. 또, 공리적 의미는 프로그램의 올바름을 검증하는 데 적합하며, 환원 의미는 언어의 타입 안전성을 증명할 때 널리 사용된다. 자연적 의미는 직관적으로 언어를 정의할 수 있게 하며, 실제 인터프리터의 구현에 가장 가깝다.

위 설명에 프로그래밍 언어 수업에서 다루지 않는 개념들이 많이 포함되었기에 잘 이해가 되지 않을 수 있으나, 다양한 방법으로 언어의 의미를 정의할 수 있으며, 상황에 따라 적합한 방법으로 언어의 의미를 정의한다는 사실을 이해하는 것으로 충분하다.

## 자연적 의미

지난 글에서 AE의 요약 문법을 정의하였다.

\[
\begin{array}{lrcl}
\text{Integer} & n & \in & \mathbb{Z} \\
\text{Expression} & e & ::= & n \\
&& | & \{+\ e\ e\} \\
&& | & \{-\ e\ e\} \\
\end{array}
\]

메타변수 \(n\)은 정수, 메타변수 \(e\)는 식을 나타낸다. \(\text{Expression}\)은 모든 식의 집합이다.

이번 글에서는 AE의 자연적 의미를 정의한다.

AE의 의미는 주어진 AE의 식이 어떤 값을 나타내는지를 정의하는 것이다. 따라서, AE의 값이 무엇인지부터 정의해야 한다. 모든 산술식은 하나의 정숫값을 표현하므로, AE의 값은 정숫값이다.

\[
\begin{array}{lrcl}
\text{Value} & v & ::= & n
\end{array}
\]

메타변수 \(v\)는 값을 나타낸다. \(\text{Value}\)는 모든 값의 집합이며, \(\mathbb{Z}\)와 같다.

AE의 식은 AE의 어떤 값을 표현하므로, AE의 의미는 식에서 값으로 가는 함수라고 생각할 수 있다. \(\Rightarrow\)가 AE의 의미를 나타내는 함수라고 하자. 그러면 \(\Rightarrow:\ \text{Expression}\rightarrow\text{Value}\)이다.

그러나, 일반적인 언어에서는 모든 식이 값을 나타내지는 않는다. 프로그램을 실행했을 때 오류가 발생하여 프로그램이 종료되는 것처럼, 아무런 값도 나타내지 않는 식이 존재할 수 있다. 또한, 무작위 값을 만드는 식이 언어에 존재한다면, 하나의 식이 여러 값을 나타낼 수 있다. 따라서, 의미를 식에서 값으로 가는 함수보다는 식과 값의 *관계*(relation)로 정의하는 것이 바람직하다. 즉, \(\Rightarrow\subseteq\text{Expression}\times\text{Value}\)이다.

어떤 식 \(e\)과 어떤 값 \(v\)에 대하여 \((e,v)\in\Rightarrow\)이면 \(e\)가 나타내는 값이 \(v\)이다. \(e\)라는 프로그램을 실행했을 때 결괏값이 \(v\)라고 말할 수 있다. 일반적으로 프로그래밍 언어 연구에서는 \((e,v)\in\Rightarrow\) 대신 \(\vdash e\Rightarrow v\)라고 표기한다. \(\Rightarrow\) 자체는 관계일 뿐이므로, 수학적인 정의는 무엇이 입력이고 무엇이 출력이라는 것을 암시하지 않는다. 그러나, 직관적으로는 식이 입력이고 값이 출력이라고 이해할 수 있다.

추론 규칙을 사용하여 의미를 표현할 수 있다.

\[
\vdash n\Rightarrow n
\]

식이 어떤 정수인 경우, 그 정수가 곧 그 식이 나타내는 값이다. 위 추론 규칙은 전제 없이 결론만 가지고 있다. 추론 규칙은 다음과 같이 해석할 수 있다.

\[ \forall n\in\mathbb{Z}.\vdash n\Rightarrow n \]

직관적으로는, "\(n\)이 주어졌을 때, 아무것도 계산할 필요가 없으며 결괏값은 \(n\)이다"라고 생각할 수 있다.

\[
\frac
{ \vdash e_1\Rightarrow n_1\quad\vdash e_2\Rightarrow n_2 }
{ \vdash \{+\ e_1\ e_2\}\Rightarrow n_1+n_2 }
\]

식이 두 식의 합인 경우, 두 식이 나타내는 정수의 합이 전체 식이 나타내는 값이다. 수학적으로는 다음과 같이 해석할 수 있다.

\[
\begin{array}{l}
\forall e_1\in\text{Expression}.
\forall e_2\in\text{Expression}.
\forall n_1\in\mathbb{Z}.
\forall n_2\in\mathbb{Z}.\\
(\vdash e_1\Rightarrow n_1)\rightarrow
(\vdash e_2\Rightarrow n_2)\rightarrow
(\vdash \{+\ e_1\ e_2\}\Rightarrow n_1+n_2)
\end{array}
\]

수학적 정의에는 드러나지 않지만, 직관적으로 위 규칙을 이해할 때, "\(\{+\ e_1\ e_2\}\)를 계산하려고 \(e_1\)을 계산하니 \(n_1\)이 나왔고 \(e_2\)를 계산하니 \(n_2\)가 나왔으니 결과는 \(n_1+n_2\)이다"라고 생각할 수 있다. 즉, \(e_1\)과 \(e_2\)는 주어졌고, \(n_1\)과 \(n_2\)는 계산 과정에서 나온 값이며, 최종 결괏값은 \(n_1+n_2\)이다.

\[
\frac
{ \vdash e_1\Rightarrow n_1\quad\vdash e_2\Rightarrow n_2 }
{ \vdash \{-\ e_1\ e_2\}\Rightarrow n_1-n_2 }
\]

식이 두 식의 차인 경우, 두 식이 나타내는 정수의 차가 전체 식이 나타내는 값이다. 수학적으로는 다음과 같이 해석할 수 있다.

\[
\begin{array}{l}
\forall e_1\in\text{Expression}.
\forall e_2\in\text{Expression}.
\forall n_1\in\mathbb{Z}.
\forall n_2\in\mathbb{Z}.\\
(\vdash e_1\Rightarrow n_1)\rightarrow
(\vdash e_2\Rightarrow n_2)\rightarrow
(\vdash \{-\ e_1\ e_2\}\Rightarrow n_1-n_2)
\end{array}
\]

합과 마찬가지로, 수학적 정의에는 드러나지 않지만, 직관적으로 위 규칙을 이해할 때, "\(\{-\ e_1\ e_2\}\)를 계산하려고 \(e_1\)을 계산하니 \(n_1\)이 나왔고 \(e_2\)를 계산하니 \(n_2\)가 나왔으니 결과는 \(n_1-n_2\)이다"라고 생각할 수 있다. 즉, \(e_1\)과 \(e_2\)는 주어졌고, \(n_1\)과 \(n_2\)는 계산 과정에서 나온 값이며, 최종 결괏값은 \(n_1-n_2\)이다.

계속해서 강조하였듯이, 자연적 의미의 수학적 정의를 정확히 이해하되 직관적 해석을 할 수 있도록 하는 것이 중요하다. 수학적으로 보았을 때 AE의 자연적 의미는 어디까지나 식과 값의 관계이고, 관계를 정의하는 추론 규칙은 무엇이 주어진 값이고 무엇이 계산으로 얻어지는 값인지 나타내지 않는다. 그러나, 직관적으로는 주어진 값이 나타내는 값을 찾는 것이 AE의 의미이며, 추론 규칙 결론의 식은 주어진 입력, 추론 규칙 전제는 계산 과정, 추론 규칙 결론의 값은 결괏값이라고 해석할 수 있다. 수학적 정의를 생각하지 않는다면 엄밀하게 사고할 때 실수할 수 있고, 복잡한 의미를 이해하기 어렵다. 언어가 복잡해짐에 따라 직관적으로 입력, 과정, 결과라는 해석이 불가능한 추론 규칙이 등장한다. 예를 들면, 결과가 과정에 사용될 수 있다. 이는 직관적인 해석의 관점에서는 이상한 일이지만, 선후 관계가 존재하지 않는 수학적 정의의 측면에서는 자연스럽다. 한편, 직관적 해석을 하지 않는다면 언어 자체를 이해하기 어렵다. 따라서, 두 측면에서 동시에 의미를 바라볼 수 있어야 한다.

다음은 AE의 자연적 의미를 규정한 추론 규칙을 한군데에 모아놓은 것이다.

\[
\vdash n\Rightarrow n
\]

\[
\frac
{ \vdash e_1\Rightarrow n_1\quad\vdash e_2\Rightarrow n_2 }
{ \vdash \{+\ e_1\ e_2\}\Rightarrow n_1+n_2 }
\]

\[
\frac
{ \vdash e_1\Rightarrow n_1\quad\vdash e_2\Rightarrow n_2 }
{ \vdash \{-\ e_1\ e_2\}\Rightarrow n_1-n_2 }
\]

### 증명 나무 그리기

\(\{+\ 4\ \{-\ 2\ 1\}\}\)이 나타내는 값은 \(5\)이다. 아래의 증명 나무는 이 사실을 증명한다.

\[
\frac
{
  {\large
  \vdash 4\Rightarrow 4 \quad
  \frac
  {\vdash 2\Rightarrow 2 \quad \vdash 1\Rightarrow 1}
  {\vdash \{-\ 2\ 1\}\Rightarrow 1}
  }
}
{\vdash\{+\ 4\ \{-2 \ 1\}\}\Rightarrow 5}
\]

증명 나무를 그리는 과정은 프로그래밍 언어 연구의 관점에서 흥미로운 일은 아니다. 그러나, 증명 나무를 그리는 연습은 의미를 이해하는 데 도움이 되며, 증명 나무를 그리는 데 어려움을 겪는 학생들이 있기 때문에, 증명 나무를 그리는 전략을 간단히 다루려고 한다.

수업에서 다루는 언어는 의미가 복잡하지 않다. 주어진 식에 적용 가능한 추론 규칙이 두 개 이상인 경우가 거의 없기에 어느 추론 규칙을 사용할지 고민할 필요가 없다. 또한, 명제의 의미를 고민할 필요 없이 적절히 메타변수를 올바른 식으로 치환하는 것만으로도 증명 나무를 그려나갈 수 있다. 따라서, 많은 경우에 '생각을 하지 않고' 기계적으로 증명 나무를 그릴 수 있다. 물론, 약간의 고민이 필요한 경우도 있지만, 자연적 의미의 정의와 증명 나무를 그리는 방법을 올바르게 이해했다면, 대체로 증명 나무를 그리는 것은 기계적으로 수행할 수 있는 작업이다.

\(\{+\ 4\ \{-2\ 1\}\}\)이 나타내는 값이 \(5\)라는 사실을 증명하는 증명 나무를 차근차근 그려보자. 먼저, 결론의 식은 \(\{+\ 4\ \{-\ 2\ 1\}\}\)이다.

\[
\color{red}{
\frac
{
  \color{white}{{\large
  \vdash 4\Rightarrow 4 \quad
  \frac
  {\vdash 2\Rightarrow 2 \quad \vdash 1\Rightarrow 1}
  {\vdash \{-\ 2\ 1\}\Rightarrow 1}
  }}
}
{\vdash\{+\ 4\ \{-2 \ 1\}\}\Rightarrow \color{white}{5}}
}
\]

\(\{+\ 4\ \{-\ 2\ 1\}\}\)에 적용 가능한 규칙은 하나뿐이다. \(e_1\)을 \(4\)로 \(e_2\)를 \(\{-\ 2\ 1\}\)로 치환하여 전제를 만든다. 단, 값은 비워둔다.

\[
\frac
{
  {\large
  \color{red}{\vdash 4\Rightarrow {\color{white}4} \quad
  \frac
  {\color{white}{\vdash 2\Rightarrow 2 \quad \vdash 1\Rightarrow 1}}
  {\vdash \{-\ 2\ 1\}\Rightarrow \color{white}{1}}
  }}
}
{\vdash\{+\ 4\ \{-2 \ 1\}\}\Rightarrow \color{white}{5}}
\]

\(4\)에 적용 가능한 규칙은 하나뿐이며, 전제가 없는 규칙이다. \(n\)을 \(4\)로 치환하여 값을 얻는다.

\[
\frac
{
  {\large
  \vdash 4\Rightarrow \color{red}{4} \quad
  \frac
  {\color{white}{\vdash 2\Rightarrow 2 \quad \vdash 1\Rightarrow 1}}
  {\vdash \{-\ 2\ 1\}\Rightarrow \color{white}{1}}
  }
}
{\vdash\{+\ 4\ \{-2 \ 1\}\}\Rightarrow \color{white}{5}}
\]

\(\{-\ 2\ 1\}\)에 적용 가능한 규칙은 하나뿐이다. \(e_1\)을 \(2\)로 \(e_2\)를 \(1\)로 치환하여 전제를 만든다. 단, 값은 비워둔다.

\[
\frac
{
  {\large
  \vdash 4\Rightarrow 4 \quad
  \frac
  {\color{red}{\vdash 2\Rightarrow \color{white}{2} \quad \vdash 1\Rightarrow \color{white}{1}}}
  {\vdash \{-\ 2\ 1\}\Rightarrow \color{white}{1}}
  }
}
{\vdash\{+\ 4\ \{-2 \ 1\}\}\Rightarrow \color{white}{5}}
\]

\(2\)에 적용 가능한 규칙은 하나뿐이며, 전제가 없는 규칙이다. \(n\)을 \(2\)로 치환하여 값을 얻는다.

\[
\frac
{
  {\large
  \vdash 4\Rightarrow 4 \quad
  \frac
  {\vdash 2\Rightarrow \color{red}{2} \quad \vdash 1\Rightarrow \color{white}{1}}
  {\vdash \{-\ 2\ 1\}\Rightarrow \color{white}{1}}
  }
}
{\vdash\{+\ 4\ \{-2 \ 1\}\}\Rightarrow \color{white}{5}}
\]

\(1\)에 적용 가능한 규칙은 하나뿐이며, 전제가 없는 규칙이다. \(n\)을 \(1\)로 치환하여 값을 얻는다.

\[
\frac
{
  {\large
  \vdash 4\Rightarrow 4 \quad
  \frac
  {\vdash 2\Rightarrow 2 \quad \vdash 1\Rightarrow \color{red}{1}}
  {\vdash \{-\ 2\ 1\}\Rightarrow \color{white}{1}}
  }
}
{\vdash\{+\ 4\ \{-2 \ 1\}\}\Rightarrow \color{white}{5}}
\]

\(2-1\)을 계산하여 \(1\)을 얻는다. \(1\)은 \(\{-\ 2\ 1\}\)의 결괏값이다.

\[
\frac
{
  {\large
  \vdash 4\Rightarrow 4 \quad
  \frac
  {\vdash 2\Rightarrow 2 \quad \vdash 1\Rightarrow 1}
  {\vdash \{-\ 2\ 1\}\Rightarrow \color{red}{1}}
  }
}
{\vdash\{+\ 4\ \{-2 \ 1\}\}\Rightarrow \color{white}{5}}
\]

\(4+1\)을 계산하여 \(5\)를 얻는다. \(5\)는 \(\{+\ 4\ \{-\ 2\ 1\}\}\)의 결괏값이다.

\[
\frac
{
  {\large
  \vdash 4\Rightarrow 4 \quad
  \frac
  {\vdash 2\Rightarrow 2 \quad \vdash 1\Rightarrow 1}
  {\vdash \{-\ 2\ 1\}\Rightarrow 1}
  }
}
{\vdash\{+\ 4\ \{-2 \ 1\}\}\Rightarrow \color{red}{5}}
\]

증명 나무가 완성되었다.

### 인터프리터 구현

AE의 자연적 의미를 따르는 인터프리터를 Scala로 구현할 수 있다.

```scala
sealed trait AE
case class Num(n: Int) extends AE
case class Add(l: AE, r: AE) extends AE
case class Sub(l: AE, r: AE) extends AE

def interp(e: AE): Int = e match {
  case Num(n) => n
  case Add(l, r) => interp(l) + interp(r)
  case Sub(l, r) => interp(l) - interp(r)
}

interp(Add(Num(4), Sub(Num(2), Num(1))))  // 5
```

## 감사의 말

글을 확인하고 의견을 주신 류석영 교수님께 감사드립니다.
