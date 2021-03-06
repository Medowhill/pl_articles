이번 글에서는 TFAE에 *레코드*(record) 타입과 서브타입에 의한 다형성을 추가하여 STFAE를 정의한다. STFAE의 문법, 동적 의미, 타입 체계를 정의할 것이다.

## 문법

다음은 STFAE의 요약 문법이다. TFAE와 비교하여 추가된 부분만 적었다.

\[
\begin{array}{rrcl}
\text{Label} & l & \in & \mathcal{L} \\
\text{Expression} & e & ::= & \cdots \\
&&|& \{l=e,\cdots,l=e\} \\
&&|& e.l \\
\text{Value} & v & ::= & \cdots \\
&&|& \{l=v,\cdots,l=v\} \\
\text{Type} & \tau & ::= & \cdots \\
&&|& \{l:\tau,\cdots,l:\tau\} \\
\end{array}
\]

메타변수 \(l\)은 레이블을 나타낸다. 레이블은 레코드가 가지고 있는 필드의 이름으로 사용된다.

식 \(\{l_1=e_1,\cdots,l_n=e_n\}\)은 레코드 값을 만드는 식이다. 한 레코드는 필드를 하나도 가지지 않거나 한 개 이상의 필드를 가질 수 있다. \(l_1\)부터 \(l_n\)까지가 필드의 이름이다. 위의 문법 정의에 직접 드러나지 않지만 필드의 이름은 모두 달라야 한다고 가정하겠다. 이 식을 계산하려면 \(e_1\)부터 \(e_n\)까지의 식을 모두 계산해야 한다. \(e_1\)의 계산 결과는 이름이 \(l_1\)인 필드의 값이 되고 \(e_2\)의 계산 결과는 이름이 \(l_2\)인 필드의 값이 된다. 같은 방식으로 이름이 \(l_n\)인 필드의 값까지 정해진다.

값 \(\{l_1=v_1,\cdots,l_n=v_n\}\)은 레코드 값이다. \(l_1\)부터 \(l_n\)까지가 필드의 이름이다. 레코드 식이 가지는 필드의 이름이 모두 다르므로 레코드 값이 가지는 필드의 이름도 모두 다르다. 이름이 \(l_1\)인 필드의 값은 \(v_1\)이다. 나머지 필드의 값도 마찬가지로, \(i\)가 \(1\) 이상 \(n\) 이하의 정수일 때 이름이 \(l_i\)인 필드의 값은 \(l_n\)이다. 예를 들면, \(\{a=1+2,b=3+4\}\)라는 레코드 식을 계산한 결과는 \(\{a=3,b=7\}\)이라는 레코드 값이다. 이 값은 두 필드를 가지며 각 필드의 이름은 \(a\)와 \(b\)이다. 필드 \(a\)의 값은 \(3\)이고 필드 \(b\)의 값은 \(7\)이다. 또, \(\{\}\)는 빈 레코드, 즉 아무 필드도 없는 레코드를 만드는 식이며 계산 결과 역시 \(\{\}\)이다.

타입 \(\{l_1:\tau_1,\cdots,l_n:\tau_n\}\)은 레코드 타입이다. \(l_1\)부터 \(l_n\)까지가 필드의 이름이다. 레코드 타입 역시 가지고 있는 필드의 이름이 모두 다르다. 이 타입은 \(1\) 이상 \(n\) 이하인 모든 정수 \(i\)에 대해 필드 \(l_i\)의 값의 타입이 \(\tau_i\)인 레코드 값들의 타입이다. 따라서 \(3\)과 \(7\)이 모두 정수이므로 \(\{a=3,b=7\}\)의 타입은 \(\{a:\textsf{num},b:\textsf{num}\}\)이다. 마찬가지로, 식 \(\{a=1+2,b=3+4\}\)의 타입도 \(\{a:\textsf{num},b:\textsf{num}\}\)이다. 또한, 빈 레코드의 타입은 \(\{\}\)이다.

식 \(e.l\)은 레코드 값을 사용하는 식이다. 보통, *사영*(projection) 식이라 부른다. 식 \(e\)를 계산한 결과가 레코드 값 \(\{l_1=v_1,\cdots,l_n=v_n\}\)이고 어떤 \(i\)에 대해 \(l_i=l\)이라면 \(e.l\)의 계산 결과는 \(v_i\)이다. 다르게 말하면 레코드 값에서 이름이 \(l\)인 필드의 값을 구하는 식이다. 만약 \(e\)를 계산한 결과가 레코드 값이 아니거나 이름이 \(l\)인 필드가 없는 레코드 값이면 \(e.l\)의 계산은 타입 오류를 일으킨다. 타입 체계의 관점에서 보면 사영 식은 레코드 타입을 사용하는 식이다. 식 \(e\)의 타입이 \(\{l_1:\tau_1,\cdots,l_n:\tau_n\}\)이고 어떤 \(i\)에 대해 \(l_i=l\)이라면 \(e.l\)의 타입은 \(\tau_i\)이다. \(\{a=1+2,b=3+4\}\)의 계산 결과가 \(\{a=3,b=7\}\)이므로 \(\{a=1+2,b=3+4\}.a\)의 계산 결과는 \(3\)이다. 또, \(\{a=1+2,b=3+4\}\)의 타입이 \(\{a:\textsf{num},b:\textsf{num}\}\)이므로 \(\{a=1+2,b=3+4\}.a\)의 타입은 \(\textsf{num}\)이다. 한편, \(\{a=1+2,b=3+4\}.c\)는 존재하지 않는 필드의 값을 찾는 식이므로 잘못된 타입의 식이고 계산하면 오류가 발생한다.

레코드의 표현력이 순서쌍의 표현력보다 더 높은 것은 아니다. 레코드 값 \(\{a=1,b=2,c=3\}\)을 생각해 보자. 이는 순서쌍 여럿을 사용하여 \((1,(2,3))\)이라 표현할 수 있다. 사영 식 역시 순서쌍의 구성 값을 찾는 것으로 바꿀 수 있다. \(\{a=1,b=2,c=3\}.a\)는 \((1,(2,3)).1\), \(\{a=1,b=2,c=3\}.b\)는 \((1,(2,3)).2.1\), \(\{a=1,b=2,c=3\}.c\)는 \((1,(2,3)).2.2\)로 바꾸어 쓸 수 있다. 이처럼, 아무리 많은 필드를 가지는 레코드도 순서쌍을 여러 개 써서 표현할 수 있다. 물론 필드가 하나거나 없는 레코드는 순서쌍으로 바꿀 수 없다. 그러나 필드가 하나인 레코드는 그냥 그 필드의 값으로 바로 표현할 수 있다. 필드가 없는 레코드는 사실상 아무런 정보도 없는 값이므로 아무 값으로나 표현하고 그 값을 무시하면 된다. 따라서 레코드는 여러 개의 값을 묶어 하나의 값으로 사용할 수 있게 하는 개념이라는 면에서 순서쌍이랑 다를 바가 없다.

레코드가 순서쌍보다 표현력이 뛰어나지 않음에도 레코드는 프로그래머에게 유용하다. 순서쌍은 순서에 의존하기 때문에 프로그래머가 순서쌍을 올바르게 사용하려면 값의 순서를 기억해야 한다. 예를 들어, 두 과목의 시험 성적을 한 값으로 나타낸다고 하자. ‘프로그래밍 언어’ 과목의 성적이 95점이고 ‘운영체제 및 실험’ 과목의 성적이 90점이면 \((95,90)\)이라 쓸 수 있다. 이제 ‘프로그래밍 언어’ 과목의 성적을 얻으려면 반드시 식 \((95,90).1\)을 사용해야 한다. 식 \((95,90).2\)는 ‘운영체제 및 실험’ 과목의 성적을 구한다. 그러므로 프로그래머는 두 과목 중 어느 과목의 성적이 먼저 나오는지 기억해야 한다. 두 과목 중 어느 과목이 먼저인지 정할 일관적인 이유가 없으므로 헷갈려서 실수하기 쉽다. ‘프로그래밍 언어’ 과목의 성적을 찾으려 했는데 ‘운영체제 및 실험’ 과목의 성적이 나올 수도 있는 것이다. 심지어 이 버그는 실행 중 오류를 일으키지도 않고 잘못된 타입의 식을 만들지도 않는다. 따라서 사용자도 프로그래머도 버그의 존재를 찾기 힘들다. 한편 레코드는 이런 문제가 전혀 없다. 레코드를 만들 때 \(\{pl=95,os=90\}\)과 \(\{os=90,pl=95\}\) 중 무엇을 사용하였든 \(\{pl=95,os=90\}.pl\)과 \(\{os=90,pl=95\}.pl\) 모두 올바르게 ‘프로그래밍 언어’ 과목의 성적을 알려준다. 이처럼, 레코드는 프로그래머가 기억해야 할 정보의 양을 줄여 실수할 가능성을 줄이고 코드를 직관적으로 만든다.

## 동적 의미

추가된 식인 레코드 식과 사영 식의 동적 의미만 보겠다. 나머지는 TFAE의 동적 의미와 같다.

\[
\frac
{ \sigma\vdash e_1\Rightarrow v_1 \quad \cdots \quad \sigma\vdash e_n\Rightarrow v_n }
{ \sigma\vdash \{l_1=e_1,\cdots,l_n=e_n\}\Rightarrow\{l_1=v_1,\cdots,l_n=v_n\} }
\]

식 \(\{l_1=e_1,\cdots,l_n=e_n\}\)의 값을 구하려면 식 \(e_1\)부터 \(e_n\)까지를 모두 계산해야 한다. 식 \(e_i\)의 계산 결과가 \(v_i\)라면 필드 \(l_i\)의 값이 \(v_i\)인 것이다. 이때 레코드 식의 계산 결과는 레코드 값인 \(\{l_1=v_1,\cdots,l_n=v_n\}\)이다.

\[
\frac
{ \sigma\vdash e\Rightarrow\{l_1=v_1,\cdots,l=v,\cdots,l_n=v_n\} }
{ \sigma\vdash e.l\Rightarrow v }
\]

식 \(e.l\)의 값을 구하려면 먼저 식 \(e\)부터 계산해야 한다. 그 계산 결과는 레코드 값이어야 하며 레코드 값이 필드 \(l\)을 가지고 있어야 한다. 식 \(e.l\)의 값은 필드 \(l\)의 값이다.

## 타입 체계

### 타입 규칙

추가된 식인 레코드 식과 사영 식의 정적 의미만 보겠다. 나머지는 TFAE의 타입 규칙과 같다.

\[
\frac
{ \Gamma\vdash e_1:\tau_1 \quad \cdots \quad \Gamma\vdash e_n:\tau_n }
{ \Gamma\vdash \{l_1=e_1,\cdots,l_n=e_n\}\Rightarrow\{l_1:\tau_1,\cdots,l_n:\tau_n\} }
\]

식 \(\{l_1=e_1,\cdots,l_n=e_n\}\)의 값은, 식 \(e_i\)의 계산 결과가 \(v_i\)일 때 \(\{l_1=v_1,\cdots,l_n=v_n\}\)이다. 값 \(v_i\)의 타입이 \(\tau_i\)이면 식 \(e_i\)의 타입도 \(\tau_i\)이다. 이때 \(\{l_1=v_1,\cdots,l_n=v_n\}\)의 타입은 \(\{l_1:\tau_1,\cdots,l_n:\tau_n\}\)이다. 따라서 \(\{l_1=e_1,\cdots,l_n=e_n\}\)의 타입을 구하려면 \(e_i\)의 타입을 모두 계산하여 \(\tau_i\)를 찾으면 된다. 그 타입은 \(\{l_1:\tau_1,\cdots,l_n:\tau_n\}\)이다.

\[
\frac
{ \Gamma\vdash e:\{l_1:\tau_1,\cdots,l:\tau,\cdots,l_n:\tau_n\} }
{ \Gamma\vdash e.l: \tau }
\]

식 \(e.l\)을 계산할 때 오류가 없으려면 \(e\)의 계산 결과는 필드 \(l\)을 가지고 있는 레코드 값이어야 한다. 따라서 \(e\)의 타입은 필드 \(l\)을 가지고 있는 레코드 타입이어야 한다. 이때 \(e.l\)의 값은 필드 \(l\)의 값이므로 \(e.l\)의 타입은 필드 \(l\)의 타입이다.

### 서브타입에 의한 다형성

현재의 타입 체계는 안전하지만 표현력이 부족하다. 실행 중 오류를 일으키지 않지만 타입 체계가 거절하는 식이 매우 많다. 다음의 식을 생각해 보자.

\[
(\lambda x:\{a:\textsf{num}\}.x.a)\ \{a=1,b=2\}
\]

이 식을 계산하는 것은 \(\{a=1,b=2\}.a\)를 계산하는 것과 같으므로 오류 없이 \(1\)이라는 결과를 낸다. 그러나 타입 체계는 이 식을 거절한다. \(\{a=1,b=2\}\)의 타입은 \(\{a:\textsf{num},b:\textsf{num}\}\)이다. 한편 함수 \(\lambda x:\{a:\textsf{num}\}.x.a\)의 매개변수 타입은 \(\{a:\textsf{num}\}\)이다. 인자의 타입과 매개변수의 타입이 다르므로 이 함수 적용은 잘못된 타입의 식이다.

생각해 보면 \(\{a:\textsf{num}\}\)이라는 타입은 필드 \(a\)의 값을 찾았을 때 정숫값을 주는 레코드 값의 타입이다. 따라서 필드 \(a\)의 타입이 \(\textsf{num}\)이기만 하면 그 레코드 값이 다른 필드를 더 가지고 있는 것은 문제가 되지 않는다. 다른 필드가 있어도 필드 \(a\)의 값에는 영향이 없기 때문이다. \(\{a=1,b=2\}\)의 타입이 \(\{a:\textsf{num}\}\)이라고 해도 문제될 것이 없는 것이다. 그러나 현재의 타입 체계는 \(\{a=1,b=2\}\)의 타입이 \(\{a:\textsf{num},b:\textsf{num}\}\)이라고 말한다. 이는 \(\{a:\textsf{num}\}\)이라는 타입보다 더 정확하다. 필드 \(a\)의 타입이 \(\textsf{num}\)이라는 정보에 더해 필드 \(b\)의 타입이 \(\textsf{num}\)이라는 정보도 줄 수 있다. 이는 \(\{a=1,b=2\}\)의 타입을 \(\{a:\textsf{num}\}\)이라고 할 수 있는 타입 체계를 정의하는 것이 최선의 해결책이 아닌 이유이다. 최선의 해결책은 \(\{a=1,b=2\}\)의 타입이 \(\{a:\textsf{num},b:\textsf{num}\}\)이라고 하되 \(\{a:\textsf{num},b:\textsf{num}\}\)를 타입으로 하는 값이 \(\{a:\textsf{num}\}\) 타입의 값으로도 사용될 수 있게 하는 것이다. 이 해결책은 타입 안전성을 해치지 않는다. \(\{a:\textsf{num},b:\textsf{num}\}\) 타입의 값에서 필드 \(a\)의 값을 구하면 언제나 정숫값이 나온다. 따라서 그 값을 \(\{a:\textsf{num}\}\) 타입의 값으로 사용해도 오류가 발생할 가능성이 없다.

현재 하고 싶은 일은 \(\{a=1,b=2\}\)라는 값을 \(\{a:\textsf{num},b:\textsf{num}\}\) 타입의 값으로 사용하면서도 어떤 상황에서는 \(\{a:\textsf{num}\}\) 타입의 값으로 사용하는 것이다. 이처럼 하나의 대상을 여러 타입으로 사용할 수 있게 하는 언어의 기능을 다형성이라 부른다고 하였다. 지난 글에서 다룬 매개변수에 의한 다형성은 이 문제를 해결하기에 적절하지 않다. 한 값을 \(\{a:\textsf{num},b:\textsf{num}\}\) 타입으로도 사용하고 \(\{a:\textsf{num}\}\) 타입으로도 사용하기 위해서 타입 함수를 정의할 방법을 찾기는 어렵다. 따라서 새로운 다형성이 필요하다. 그것이 바로 서브타입에 의한 다형성이다.

서브타입에 의한 다형성은 이 문제에 대한 최선의 해결책을 준다. 서브타입에 의한 다형성은 서브타입을 통해 만들어진 다형성이다. 서브타입은 두 타입의 관계를 정의한다. 보통, 타입 \(\tau_1\)이 \(\tau_2\)의 서브타입이라는 것을 \(\tau_1<:\tau_2\)라고 표기한다. 이 글에서도 이 표기 방식을 따른다. \(\tau_1\)이 \(\tau_2\)의 서브타입이면 \(\tau_2\)는 \(\tau_1\)의 슈퍼타입이다. \(\tau_1\)이 \(\tau_2\)의 서브타입이면 \(\tau_1\) 타입의 값이 \(\tau_2\) 타입이 값이 필요한 자리에 사용될 수 있다. 즉, 서브타입은 *대체 가능성*(substitutability)의 개념이다. 위에서 \(\{a:\textsf{num},b:\textsf{num}\}\) 타입의 값을 \(\{a:\textsf{num}\}\) 타입이 값이 올 자리에 사용할 수 있다고 하였다. 이때 \(\{a:\textsf{num},b:\textsf{num}\}\)이 \(\{a:\textsf{num}\}\)의 서브타입이라고 할 수 있다. 또, \(\{a:\textsf{num}\}\)은 \(\{a:\textsf{num},b:\textsf{num}\}\)의 슈퍼타입이다.

서브타입 관계가 다형성을 만들어 내려면 하나의 타입 규칙이 추가로 필요하다. 서브타입 관계는 뒤에서 정의하기로 하고, 지금은 서브타입이 잘 정의되어 있다고 가정하겠다. 다음의 타입 규칙은 서브타입을 이용해 하나의 식이 여러 타입을 가질 수 있게 한다.

\[
\frac
{ \Gamma\vdash e:\tau' \quad \tau'<:\tau }
{ \Gamma\vdash e:\tau }
\]

이 규칙을 *포함*(subsumption) 규칙이라고 부르는 경우가 많다. 그 의미는 \(e\)의 타입이 \(\tau'\)이고 \(\tau'\)이 \(\tau\)의 서브타입이면 \(e\)의 타입이 \(\tau\)이라는 것이다. \(\tau'\)이 \(\tau\)의 서브타입이라는 말이 \(\tau'\) 타입의 값이 \(\tau\) 타입의 값이 필요한 자리에 사용될 수 있다는 뜻이므로 \(e\)의 타입이 \(\tau\)이라고 말하는 것이 타당하다. 지난 글에서 본 매개변수에 의한 다형성은 하나의 식의 타입이 여러 개가 될 수 있게 하지 않았다. 그러나 서브타입에 의한 다형성은 포함 규칙을 정의함으로써 한 식이 여러 타입을 가질 수 있게 만든다. 이는 지금까지 본 타입 체계와의 큰 차이점이다. 다만, 타입 체계를 정의하는 방법에 따라 포함 규칙 없이 서브타입에 의한 다형성을 제공할 수 있다. 이 경우, 한 식의 타입은 하나로 결정된다. 이 글에서는 포함 규칙을 사용하는 서브타입에 의한 다형성만을 고려한다.

### 서브타입 규칙

이제 서브타입 관계를 정의할 차례이다. 식의 타입을 정의하는 규칙이 타입 규칙이듯 서브타입 관계를 정의하는 규칙은 *서브타입 규칙*(subtyping rule)이다.

#### 반사성 규칙

서브타입 관계는 *반사적*(reflexive) 관계이다. 타입 \(\tau\)의 값은 당연히 타입 \(\tau\)의 값이 필요한 자리에 사용될 수 있다. 따라서 모든 타입 \(\tau\)에 대해 \(\tau\)는 \(\tau\)의 서브타입이다. 이는 \(\tau<:\tau\)라고 표기할 수 있다. 다음은 서브타입 관계를 반사적으로 만드는 서브타입 규칙이다.

\[\tau<:\tau\]

이 규칙에 따라 \(\{a:\textsf{num}\}<:\{a:\textsf{num}\}\)과 같은 사실이 성립한다.

#### 추이성 규칙

서브타입 관계는 *추이적*(transitive) 관계이기도 하다. 타입 \(\tau_1\), \(\tau_2\), \(\tau_3\)이 있다 하자. \(\tau_1\)은 \(\tau_2\)의 서브타입이고 \(\tau_2\)는 \(\tau_3\)의 서브타입이라고 가정하자. \(\tau_3\) 타입의 값이 올 자리에 \(\tau_2\) 타입의 값이 사용될 수 있다. 그러므로 \(\tau_3\) 타입의 값이 올 자리는 \(\tau_2\) 타입의 값이 올 자리이기도 하다. 또, \(\tau_2\) 타입의 값이 올 자리에 \(\tau_1\) 타입의 값이 올 수 있다. \(\tau_3\) 타입의 값이 올 자리에 \(\tau_1\) 타입의 값이 올 수 있는 것이다. 따라서 \(\tau_1\)이 \(\tau_3\)의 서브타입이라는 결론을 얻을 수 있다. 다음 규칙은 서브타입 관계를 추이적으로 만든다.

\[
\frac
{ \tau_1<:\tau_2 \quad \tau_2<:\tau_3 }
{ \tau_1<:\tau_3 }
\]

위의 두 규칙은 서브타입 관계를 반사적이고 추이적이게 만들기에 중요하다. 그러나 두 규칙만으로는 \(\tau<:\tau\)라는 당연한 사실 이외의 어떤 것도 알아낼 수 없다. 언어의 표현력을 높이기 위해서는 서로 다른 두 타입 \(\tau_1\)과 \(\tau_2\)에 대해 \(\tau_1<:\tau_2\)라는 결론을 이끌어 낼 규칙이 추가로 필요하다. 지금부터는 그런 규칙들에 대해 보겠다.

#### 길이 규칙

앞에서 본 예시를 다시 떠올려 보자. \(\{a:\textsf{num},b:\textsf{num}\}\) 타입의 값을 \(\{a:\textsf{num}\}\) 타입이 값이 올 자리에 사용할 수 있다고 하였다. 이는 \(\{a:\textsf{num},b:\textsf{num}\}\)이 \(\{a:\textsf{num}\}\)의 서브타입이어야 한다는 사실을 알려준다. \(\{a:\textsf{num},b:\textsf{num}\}<:\{a:\textsf{num}\}\)이라고 표기할 수도 있다. 다음의 규칙은 이 사실이 성립하게 만든다.

\[
\{l_1:\tau_1,\cdots,l_n:\tau_n,l:\tau\}<:\{l_1:\tau_1,\cdots,l_n:\tau_n\}
\]

어떤 레코드 타입 \(\{l_1:\tau_1,\cdots,l_n:\tau_n\}\)이 있다 하자. 이 레코드 타입의 맨 뒤에 어떤 타입 \(\tau\)를 가지는 임의의 필드 \(l\)을 추가하여 얻어진 타입은 처음의 레코드 타입의 서브타입이다. 물론 필드 \(l\)의 이름은 기존에 레코드 타입이 가지고 있던 필드의 이름과는 달라야 한다. 이 서브타입 규칙은 레코드에 필드 하나가 추가되어도 그 필드가 없는 것처럼 사용하면 필드 추가 이전의 타입의 값으로 간주할 수 있다는 사실로부터 자연스럽게 나온다. 직관적으로는, 이 규칙은 “길이가 더 긴” 레코드 타입을 “길이가 더 짧은” 레코드 타입의 서브타입으로 정의하는 규칙이다. 이 글에서는 편의상 이 규칙을 “길이 규칙”이라고 부르겠다. 이 글에서 규칙에 붙이는 이름들은 다른 사람의 글이나 연구 등에서는 사용되지 않거나 다른 의미로 사용될 수 있음에 주의하기를 바란다. 이제 \(\{a:\textsf{num},b:\textsf{num}\}<:\{a:\textsf{num}\}\)이 성립한다.

서브타입 규칙과 포함 규칙을 같이 사용함으로써 \(\{a=1,b=2\}\)의 타입이 \(\{a:\textsf{num}\}\)임을 증명할 수 있다. 다음의 증명 나무가 그 증명이다.

\[
\frac
{ 
  {\Large\frac
  { \emptyset\vdash1:\textsf{num} \quad \emptyset\vdash2:\textsf{num} }
  { \emptyset\vdash\{a=1,b=2\}:\{a:\textsf{num},b:\textsf{num}\} }} \quad
  \{a:\textsf{num},b:\textsf{num}\}<:\{a:\textsf{num}\}}
{ \emptyset\vdash\{a=1,b=2\}:\{a:\textsf{num}\} }
\]

그러므로 다음 식은 올바른 타입의 식이다.

\[
(\lambda x:\{a:\textsf{num}\}.x.a)\ \{a=1,b=2\}
\]

또, 추이성 규칙을 함께 사용함으로써 더 많은 흥미로운 서브타입들을 찾아낼 수 있다. \(\{a:\textsf{num},b:\textsf{num}\}<:\{a:\textsf{num}\}\)은 이미 아는 사실이다. 마찬가지로, \(\{a:\textsf{num},b:\textsf{num},c:\textsf{num}\}<:\{a:\textsf{num},b:\textsf{num}\}\) 역시 성립한다.  그러므로 \(\{a:\textsf{num},b:\textsf{num},c:\textsf{num}\}<:\{a:\textsf{num}\}\) 이다. 다음의 증명 나무가 이를 증명한다.

\[
\frac
{ 
  \{a:\textsf{num},b:\textsf{num},c:\textsf{num}\}<:\{a:\textsf{num},b:\textsf{num}\} \quad
  \{a:\textsf{num},b:\textsf{num}\}<:\{a:\textsf{num}\}}
{ \{a:\textsf{num},b:\textsf{num},c:\textsf{num}\}<:\{a:\textsf{num}\} }
\]

같은 원리로 빈 레코드 타입인 \(\{\}\)는 모든 레코드 타입의 슈퍼타입임을 알 수 있다. 바꾸어 말하면 모든 레코드 타입은 빈 레코드 타입인 \(\{\}\)의 서브타입이다.

#### 순열 규칙

아직도 서브타입 규칙을 추가하여 언어의 표현력을 더 높일 수 있다. 먼저 타입 오류를 일으키지 않지만 타입 체계가 거절하는 식을 찾아보자.

\[
(\lambda x:\{a:\textsf{num},b:\textsf{num}\}.x.a)\ \{b=2,a=1\}
\]

위 식을 계산하는 것은 \(\{b=2,a=1\}.a\)를 계산하는 것이므로 오류 없이 \(1\)을 결과로 낸다. 그러나 잘못된 타입의 식이다. \(\{b=2,a=1\}\)의 타입은 \(\{b:\textsf{num},a:\textsf{num}\}\)이다. 함수 \(\lambda x:\{a:\textsf{num},b:\textsf{num}\}.x.a\)의 매개변수 타입은 \(\{a:\textsf{num},b:\textsf{num}\}\)이다. 인자의 타입과 매개변수의 타입이 다르므로 이 함수 적용은 잘못된 타입의 식이다.

앞에서 레코드에 대해 설명할 때 레코드의 좋은 점은 순서쌍과 다르게 필드의 순서가 중요하지 않다는 것이라 했다. 필드의 순서가 어떻든 가지고 있는 필드가 같고 같은 이름의 필드의 값이 같은 두 레코드는 같은 값이다. 그러나 현재의 타입 체계는 필드의 순서를 신경 쓴다. \(\{a:\textsf{num},b:\textsf{num}\}\) 타입의 값이 필요한 자리에 \(\{b:\textsf{num},a:\textsf{num}\}\) 타입의 값이 사용되어도 전혀 문제가 없지만 두 타입은 서로 다른 타입이다. 이 문제는 서브타입 규칙을 추가하여 쉽게 해결할 수 있다. \(\{a:\textsf{num},b:\textsf{num}\}\)이 \(\{b:\textsf{num},a:\textsf{num}\}\)의 서브타입이고 \(\{b:\textsf{num},a:\textsf{num}\}\)이 \(\{a:\textsf{num},b:\textsf{num}\}\)의 서브타입이 되게 만들면 된다. 다음의 규칙은 가지고 있는 필드가 같고 같은 이름의 필드의 타입이 같은 두 레코드 타입은 서로의 서브타입이라는 사실을 정의한다.

\[
\frac
{ \{(l_1,\tau_1),\cdots,(l_n,\tau_n)\}=\{(l'_1,\tau'_1),\cdots,(l'_n,\tau'_n)\} }
{ \{l_1:\tau_1,\cdots,l_n:\tau_n\}<:\{l'_1:\tau'_1,\cdots,l'_n:\tau'_n\} }
\]

집합이 같은지 비교할 때는 원소의 순서가 중요하지 않다는 점에 따라 규칙의 전제를 집합을 사용하여 표현하였다. 이 규칙은 한 레코드 타입이 가지고 있는 필드의 순서를 바꾸어 그 타입의 서브타입을 만들 수 있음을 의미한다. 이 글에서는 편의상 “순열 규칙”이라 부르겠다. 이제 \(\{a:\textsf{num},b:\textsf{num}\}\)이 \(\{b:\textsf{num},a:\textsf{num}\}\)의 서브타입이고 \(\{b:\textsf{num},a:\textsf{num}\}\)이 \(\{a:\textsf{num},b:\textsf{num}\}\)의 서브타입이라는 사실을 증명할 수 있다. 다음의 두 증명 나무가 그 증명이다.

\[
\frac
{ \{(a,\textsf{num}),(b,\textsf{num})\}=\{(b,\textsf{num}),(a,\textsf{num})\} }
{ \{a:\textsf{num},b:\textsf{num}\}<:\{b:\textsf{num},a:\textsf{num}\} }
\]

\[
\frac
{ \{(b,\textsf{num}),(a,\textsf{num})\}=\{(a,\textsf{num}),(b,\textsf{num})\} }
{ \{b:\textsf{num},a:\textsf{num}\}<:\{a:\textsf{num},b:\textsf{num}\} }
\]

또, 다음의 식이 올바른 타입의 식이다.

\[
(\lambda x:\{a:\textsf{num},b:\textsf{num}\}.x.a)\ \{b=2,a=1\}
\]

추이성 규칙과 길이 규칙까지 함께 사용하면 더 흥미로운 사실도 증명할 수 있다.

\[
\frac
{{\Large
  \frac
  { \{(a,\textsf{num}),(b,\textsf{num})\}=\{(b,\textsf{num}),(a,\textsf{num})\} }
  { \{a:\textsf{num},b:\textsf{num}\}<:\{b:\textsf{num},a:\textsf{num}\} }} \quad
  \{b:\textsf{num},a:\textsf{num}\}<:\{b:\textsf{num}\}
}
{ \{a:\textsf{num},b:\textsf{num}\}<:\{b:\textsf{num}\} }
\]

타입 \(\{b:\textsf{num}\}\)이 타입 \(\{a:\textsf{num},b:\textsf{num}\}\)의 슈퍼타입이라는 사실은 길이 규칙만으로는 증명할 수 없다. 길이 규칙은 \(\{\}\)과 \(\{a:\textsf{num}\}\)만을 \(\{a:\textsf{num},b:\textsf{num}\}\)의 슈퍼타입이 되게 하기 때문이다. 그러나 순열 규칙이 \(\{a:\textsf{num},b:\textsf{num}\}\)의 슈퍼타입 중 \(\{b:\textsf{num},a:\textsf{num}\}\)도 있다는 사실을 증명하기 때문에 \(\{b:\textsf{num}\}\)이 타입 \(\{a:\textsf{num},b:\textsf{num}\}\)의 슈퍼타입임을 보이는 증명이 완성될 수 있다.

#### 깊이 규칙

길이 규칙과 순열 규칙만으로는 아직 부족하다. 다음의 식을 생각해 보자.

\[
(\lambda x:\{a:\{a:\textsf{num},b:\textsf{num}\}\}.(\lambda x:\{a:\{a:\textsf{num}\}\}.x.a.a)\ x)\ \{a=\{a=1,b=2\}\}
\]

위 식을 계산하는 것은 \(\{a=\{a=1,b=2\}\}.a.a\)를 계산하는 것이다. \(\{a=\{a=1,b=2\}\}.a\)의 계산 결과가 \(\{a=1,b=2\}\)이고 \(\{a=1,b=2\}.a\)의 계산 결과는 \(1\)이므로 위 식은 오류 없이 \(1\)을 결과로 냄을 알 수 있다. 그럼에도 위 식은 잘못된 타입의 식이다. \(x\)의 타입은 \(\{a:\{a:\textsf{num},b:\textsf{num}\}\}\)이다. 길이 규칙을 고려하면 마지막 필드를 지워서 얻을 수 있는 타입인 \(\{\}\) 역시 \(x\)의 타입이다. 가지고 있는 필드가 한 개뿐이므로 순열 규칙은 도움이 되지 않는다. 그러나 함수 \(\lambda x:\{a:\{a:\textsf{num}\}\}.x.a.a\)의 매개변수 타입은 \(\{a:\{a:\textsf{num}\}\}\)이다. 인자의 타입이 매개변수의 타입과 다르므로 \((\lambda x:\{a:\{a:\textsf{num}\}\}.x.a.a)\ x\)라는 함수 적용은 잘못된 타입의 식이다.

현재 타입 체계의 문제는 레코드가 가지고 있는 필드의 타입에 대해 너무 엄격한 판단을 한다는 것이다. 필드의 이름과 순서가 완전히 동일한 두 레코드 타입을 생각해 보자. 이때 한 타입이 다른 타입의 서브타입이려면 이름이 같은 필드의 타입이 서로 같아야 한다. 예를 들면, \(\{a:\{a:\textsf{num}\}\}\)의 서브타입은 \(\{a:\{a:\textsf{num}\}\}\)이다. 다른 어떤 \(\{a:\tau\}\) 형태의 타입도 \(\{a:\{a:\textsf{num}\}\}\)의 서브타입이 될 수 없다.

그러나 \(\{a:\{a:\textsf{num}\}\}\)의 서브타입이 될 수 있는 \(\{a:\tau\}\) 형태의 다른 타입도 찾을 수 있다. 어떤 식 \(e\)의 타입이 \(\{a:\{a:\textsf{num}\}\}\)라고 하자. 그러면 \(e.a\)를 계산한 값은 \(\{a:\textsf{num}\}\) 타입의 값이 필요한 자리에 사용될 수 있다. 이번에는 타입이 \(\{a:\{a:\textsf{num},b:\textsf{num}\}\}\)인 식 \(e'\)을 생각해 보자. \(e'.a\)를 계산한 값은 \(\{a:\textsf{num},b:\textsf{num}\}\) 타입의 값이다. 앞에서 \(\{a:\textsf{num},b:\textsf{num}\}\)이 \(\{a:\textsf{num}\}\)의 서브타입임을 이미 보았다. 따라서 \(e'.a\)를 계산한 값이 \(e.a\)를 계산한 값이 올 자리에 사용될 수 있다. 필드 \(a\)의 값을 구하는 사영 부분이 동일하므로 \(e'\)을 계산한 값이 \(e\)를 계산한 값 대신 쓰일 수 있다고 해도 된다. 즉 \(\{a:\{a:\textsf{num},b:\textsf{num}\}\}\) 타입의 식이 \(\{a:\{a:\textsf{num}\}\}\) 타입의 식 대신 사용될 수 있다. \(\{a:\{a:\textsf{num},b:\textsf{num}\}\}\)이 \(\{a:\{a:\textsf{num}\}\}\)의 서브타입이라고 할 수 있는 것이다. 더 일반적으로는, \(\tau<:\{a:\textsf{num}\}\)인 모든 \(\tau\)에 대해 \(\{a:\tau\}<:\{a:\{a:\textsf{num}\}\}\)이라고 말할 수 있다.

위 예시에서는 필드가 \(a\) 하나인 레코드 타입만 고려하였지만, 필드가 몇 개이고 이름이 무엇이든 같은 논리를 적용할 수 있다. 따라서 다음의 서브타입 규칙이 나온다

\[
\frac
{ \tau_1<:\tau'_1 \quad \cdots \quad \tau_n<:\tau'_n }
{ \{l_1:\tau_1,\cdots,l_n:\tau_n\}<:\{l_1:\tau'_1,\cdots,l_n:\tau'_n\} }
\]

이 규칙은 레코드 타입이 가지고 있는 필드의 타입의 서브타입을 고려하게 만든다. 이 글에서는 “깊이 규칙”이라고 부르겠다. 레코드 타입을 비교할 때 필드의 타입까지, 즉 “더 깊은 곳까지” 타입을 살펴보기 때문이다. 이제 \(\{a:\{a:\textsf{num},b:\textsf{num}\}\}<:\{a:\{a:\textsf{num}\}\}\)을 증명할 수 있다. 다음 증명 나무가 그 증명이다.

\[
\frac
{ \{a:\textsf{num},b:\textsf{num}\}<:\{a:\textsf{num}\} }
{ \{a:\{a:\textsf{num},b:\textsf{num}\}\}<:\{a:\{a:\textsf{num}\}\} }
\]

또, 다음 식이 올바른 타입의 식이다.

\[
(\lambda x:\{a:\{a:\textsf{num},b:\textsf{num}\}\}.(\lambda x:\{a:\{a:\textsf{num}\}\}.x.a.a)\ x)\ \{a=\{a=1,b=2\}\}
\]

이제 레코드 타입에 대한 서브타입 규칙은 충분히 정확해졌다. 길이 규칙, 순열 규칙, 깊이 규칙이면 두 레코드 타입이 서브타입 관계에 있는지 확인하기에 충분하다.

#### 함수 타입의 서브타입 규칙

이번에는 함수 타입의 서브타입 규칙에 대해 보겠다. 함수 타입은 매개변수 타입과 결과 타입으로 이루어진다. 결과 타입에 대해 먼저 보고 그 다음에 매개변수 타입에 대해 보겠다. 앞에서 본 레코드 타입의 서브타입 규칙은 직관적이다. 함수 타입의 서브타입 규칙은 레코드 타입에 비해서 좀 더 고민이 필요하다. 특히 매개변수 타입이 달라지는 경우를 고려하면 헷갈릴 수 있다. 그러나 천천히 아래의 설명을 따라가면 이해할 수 있을 것이다.

두 함수 타입을 생각해 보겠다. 하나는 \(\tau_1\rightarrow\tau_2\)이고 다른 하나는 \(\tau_1\rightarrow\tau'_2\)이다. 즉 매개변수 타입은 \(\tau_1\)으로 서로 같고 결과 타입은 \(\tau_2\)와 \(\tau_2'\)으로 서로 다르다. 이때 \(\tau_2\)가 \(\tau_2'\)의 서브타입이라고 가정하겠다.

먼저 \(\tau_1\rightarrow\tau_2\)가 \(\tau_1\rightarrow\tau'_2\)의 서브타입인지 확인해 보겠다. 두 타입이 서브타입 관계에 있으려면 \(\tau_1\rightarrow\tau'_2\) 타입의 값이 사용될 수 있는 모든 곳에 어떤 \(\tau_1\rightarrow\tau_2\) 타입의 값도 사용될 수 있어야 한다. 일단 식 \(e_1'\)의 타입이 \(\tau_1\rightarrow\tau'_2\)라고 하자. 그러면 \(e_1'\)은 함수 적용의 함수 위치에 올 수 있다. 식 \(e_2\)의 타입이 \(\tau_1\)이라 하겠다. 그러면 \(e_1'\ e_2\)는 올바른 타입의 식이며 그 타입은 \(\tau'_2\)이다. 따라서 함수 적용의 결과로 나온 값이 \(\tau'_2\) 타입의 값이 필요한 모든 곳에 사용될 수 있다. 이제 해야 할 일은 \(\tau_1\rightarrow\tau_2\) 타입의 임의의 식 \(e_1\)이 \(e_1'\)을 대신할 수 있는지 보는 것이다. 식 \(e_1\ e_2\)를 생각해 보자. 두 함수의 매개변수 타입이 \(\tau_1\)으로 같기 때문에 \(e_2\)를 계산하여 나온 값을 \(e_1\)에 해당하는 함수가 인자로 받는 데 문제가 없다. 따라서 올바른 타입의 함수 적용이다. 또 \(e_1\ e_2\)를 계산하여 나온 값은 \(\tau_2\) 타입의 값이다. 타입 \(\tau_2\)가 타입 \(\tau_2'\)의 서브타입이므로 그 값은 \(\tau_2'\) 타입의 값이 필요한 모든 곳에 사용될 수 있다. 즉 \(e_1'\ e_2\)가 등장할 자리에 \(e_1\ e_2\)가 대신 와도 문제없다. 식 \(e_1'\)이 인자로 받을 수 있는 값은 \(e_1\)도 인자로 받을 수 있고, \(e_1'\)을 호출한 결과를 사용할 수 있는 곳에는 \(e_1\)을 호출한 결과도 사용할 수 있다. 그러므로 \(e_1\)이 \(e_1'\) 대신 사용될 수 있다. 따라서 \(\tau_1\rightarrow\tau_2\)는 \(\tau_1\rightarrow\tau_2'\)의 서브타입이다. 규칙으로 쓰면 아래와 같다.

\[
\frac
{ \tau_2<:\tau_2' }
{ \tau_1\rightarrow\tau_2<:\tau_1\rightarrow\tau_2' }
\]

즉 함수 타입은 결과 타입의 서브타입 관계를 보존한다. 두 함수 타입이 있을 때 매개변수 타입이 같고 한 타입의 결과 타입이 다른 하나의 결과 타입의 서브타입이면 전자가 후자의 서브타입이다. 예를 들면, \(\textsf{num}\rightarrow\{a:\textsf{num},b:\textsf{num}\}\) 타입은 \(\textsf{num}\rightarrow\{a:\textsf{num}\}\)의 서브타입이다. 이는 직관적으로 생각해도 올바르다. 어떤 함수가 정수를 인자로 받아 \(\{a:\textsf{num},b:\textsf{num}\}\) 타입의 값을 결과로 낸다고 하자. 그런데 \(\{a:\textsf{num},b:\textsf{num}\}\) 타입의 값은 \(\{a:\textsf{num}\}\) 타입의 값이기도 하다. 따라서 그 함수가 정수를 인자로 받아 \(\{a:\textsf{num}\}\) 타입의 값을 결과로 낸다고 해도 맞는 말이다. 정수를 인자로 받아 \(\{a:\textsf{num},b:\textsf{num}\}\) 타입의 값을 내는 함수를, 정수를 인자로 받아 \(\{a:\textsf{num}\}\) 타입의 값을 내는 함수가 필요한 자리에 쓸 수 있으므로 \(\textsf{num}\rightarrow\{a:\textsf{num},b:\textsf{num}\}\) 타입이 \(\textsf{num}\rightarrow\{a:\textsf{num}\}\)의 서브타입이다.

이제 순서를 바꾸어 \(\tau_1\rightarrow\tau_2'\)가 \(\tau_1\rightarrow\tau_2\)의 서브타입이 될 수 있는지 확인해 보겠다. 마찬가지로, 식 \(e_1\)의 타입이 \(\tau_1\rightarrow\tau_2\), 식 \(e_1'\)의 타입이 \(\tau_1\rightarrow\tau'_2\)라고 하자. 함수 \(e_1\)이 인자로 받을 수 있는 값은 \(e_1'\)도 받을 수 있다. 그러나 함수 적용 결과를 생각하면 문제가 있다. 함수 \(e_1\)이 결과로 내는 값은 \(\tau_2\) 타입의 값만 필요한 곳에 사용될 수 있다. 한편 함수 \(e_2\)는 결과로 내는 값의 타입이 \(\tau_2'\)이다. 타입 \(\tau_2'\)이 \(\tau_2\)의 슈퍼타입이므로 결과로 나온 값 중에는 \(\tau_2\) 타입이 아닌 값이 있을 수 있다. 따라서 \(e_1'\)을 \(e_1\) 대신 사용할 수는 없다. 즉 \(\tau_1\rightarrow\tau_2'\)가 \(\tau_1\rightarrow\tau_2\)의 서브타입이 될 수 없는 것이다.

결과 타입에 대해서 모두 살펴보았으니 매개변수 타입에 대해 보겠다. 두 함수 타입 \(\tau_1\rightarrow\tau_2\)이고 다른 하나는 \(\tau_1'\rightarrow\tau_2\)을 생각하자. 결과 타입은 \(\tau_2\)로 서로 같고 매개변수 타입은 \(\tau_1\)과 \(\tau_1'\)으로 서로 다르다. 이때 \(\tau_1'\)이 \(\tau_1\)의 서브타입이라고 가정하겠다.

먼저 \(\tau_1\rightarrow\tau_2\)가 \(\tau_1'\rightarrow\tau_2\)의 서브타입인지 확인해 보겠다. 식 \(e_1\)의 타입은 \(\tau_1\rightarrow\tau_2\), \(e_1'\)의 타입은 \(\tau_1'\rightarrow\tau_2\)라 하자. 앞에서 결과 타입이 다른 경우를 볼 때는, 두 함수의 매개변수 타입이 같기 때문에 두 함수가 같은 대상을 인자로 받을 수 있었다. 비슷한 논리로, 지금은 결과 타입이 같으므로 두 함수를 호출하여 얻은 결과가 같은 곳에 사용될 수 있다. 집중할 부분은 두 함수가 받을 수 있는 인자이다. 식 \(e_2\)의 타입이 \(\tau_1'\)이라고 하자. 그러면 \(e_1'\ e_2\)는 올바른 타입의 식이다. 매개변수 타입과 인자 타입이 \(\tau_1'\)으로 같기 때문이다. 이제 \(e_1\)이 \(e_1'\) 대신 쓰일 수 있는지 보면 된다. 함수 \(e_1\)은 인자로 \(\tau_1\) 타입의 값만 받을 수 있다. 그러므로 인자가 될 수 있는 식은 \(\tau_1\) 타입의 식이다. 식 \(e_2\)의 타입이 \(\tau_1'\)이고 \(\tau_1'\)은 \(\tau_1\)의 서브타입이다. 따라서 \(e_2\)를 인자로 사용해도 된다. 식 \(e_1\ e_2\)는 문제없는 식이다. 이는 \(e_1\)이 \(e_1'\) 대신 사용될 수 있음을 의미한다. 즉 \(\tau_1\rightarrow\tau_2\)가 \(\tau_1'\rightarrow\tau_2\)의 서브타입이다. 규칙으로는 아래처럼 쓸 수 있다.

\[
\frac
{ \tau_1'<:\tau_1 }
{ \tau_1\rightarrow\tau_2<:\tau_1'\rightarrow\tau_2 }
\]

함수 타입은 매개변수 타입의 서브타입 관계를 뒤집는다. 이는 결과 타입에 대해 얻은 결론과 정반대이다. 두 함수 타입이 있을 때 결과 타입이 같고 한 타입의 매개변수 타입이 다른 하나의 매개변수 타입의 슈퍼타입이면 전자가 후자의 서브타입이다. 예를 들면, \(\{a:\textsf{num}\}\rightarrow\textsf{num}\) 타입은 \(\{a:\textsf{num},b:\textsf{num}\}\rightarrow\textsf{num}\)의 서브타입이다. 이 사실을 직관적으로 이해할 수도 있다. 타입이 \(\{a:\textsf{num},b:\textsf{num}\}\rightarrow\textsf{num}\)인 함수의 몸통에서는 인자로 받은 레코드 값의 \(a\) 필드와 \(b\) 필드를 사용할 수 있다. 인자로 넘어온 레코드가 반드시 \(a\)와 \(b\) 필드를 모두 가지고 있어야 하는 것이다. 한편 \(\{a:\textsf{num}\}\rightarrow\textsf{num}\) 타입의 함수는 몸통에서 인자로 받은 레코드 값의 \(a\) 필드만 사용한다. 인자로 받은 값에 요구하는 조건이 적은 것이다. 인자로 넘어온 레코드가 \(a\)와 \(b\) 필드를 다 가지고 있어도 \(a\) 필드만 사용한다. 따라서 \(\{a:\textsf{num}\}\rightarrow\textsf{num}\) 타입의 함수를 \(\{a:\textsf{num},b:\textsf{num}\}\rightarrow\textsf{num}\) 타입의 함수 대신 사용해도 안전하다.

이제 순서를 바꾸어 보자. 타입 \(\tau_1'\rightarrow\tau_2\)가 \(\tau_1\rightarrow\tau_2\)의 서브타입인지 확인해 보겠다. 결과 타입은 같으니 받을 수 있는 인자에 대해서만 보면 된다. 타입이 \(\tau_1\rightarrow\tau_2\)인 함수 \(e_1\)은 인자로 \(\tau_1\) 타입의 값이면 무엇이든 받을 수 있다. 그러므로 \(e_1\ e_2\)의 \(e_2\)가 될 수 있는 조건은 타입이 \(\tau_1\)인 식이어야 한다는 것뿐이다. 한편 타입이 \(\tau_1'\rightarrow\tau_2\)인 함수 \(e_1'\)은 \(\tau_1'\) 타입의 값만을 인자로 받는다. 만약 \(e_1\) 대신 \(e_1'\)을 사용하면 \(e_1'\ e_2\)라는 식이 나온다. 이때 매개변수의 타입은 \(\tau_1'\)이지만 인자의 타입은 그 슈퍼타입인 \(\tau_1\)이다. 즉 \(e_1'\)이 사용할 수 없는 값이 인자로 들어올 수도 있는 것이다. 따라서 \(e_1'\)이 \(e_1\)을 대신할 수 없다. 타입 \(\tau_1'\rightarrow\tau_2\)가 \(\tau_1\rightarrow\tau_2\)의 서브타입이 될 수 없는 것이다.

예시를 들어 다시 생각해 보자. 타입이 \(\{a:\textsf{num}\}\rightarrow\textsf{num}\)인 함수는 몸통에서 인자로 받은 레코드 값의 \(a\) 필드만 사용한다. 인자로 \(\{a=1\}\)을 사용할 수 있다. 한편 타입이 \(\{a:\textsf{num},b:\textsf{num}\}\rightarrow\textsf{num}\)인 함수는 몸통에서 인자로 받은 레코드 값의 \(a\)와 \(b\) 필드를 모두 사용할 수 있다. 매개변수 이름이 \(x\)일 때, 몸통이 \(x.a+x.b\)일 수 있는 것이다. 이 함수가 \(\{a=1\}\)을 인자로 받으면 실행 중 오류가 발생한다. \(\{a=1\}.b\)를 계산할 수 없기 때문이다. 따라서 \(\{a:\textsf{num},b:\textsf{num}\}\rightarrow\textsf{num}\)이 \(\{a:\textsf{num}\}\rightarrow\textsf{num}\)의 서브타입이 될 수 없다.

위에서 결과 타입과 매개변수 타입에 대해 각각 정의한 서브타입 규칙을 하나로 합칠 수 있다.

\[
\frac
{ \tau_1'<:\tau_1 \quad \tau_2<:\tau_2' }
{ \tau_1\rightarrow\tau_2<:\tau_1'\rightarrow\tau_2' }
\]

위 규칙이 함수 타입에 대한 서브타입 규칙이다.

STFAE의 서브타입 규칙을 모두 모으면 아래와 같다.

\[\tau<:\tau\]

\[
\frac
{ \tau_1<:\tau_2 \quad \tau_2<:\tau_3 }
{ \tau_1<:\tau_3 }
\]

\[
\{l_1:\tau_1,\cdots,l_n:\tau_n,l:\tau\}<:\{l_1:\tau_1,\cdots,l_n:\tau_n\}
\]

\[
\frac
{ \{(l_1,\tau_1),\cdots,(l_n,\tau_n)\}=\{(l'_1,\tau'_1),\cdots,(l'_n,\tau'_n)\} }
{ \{l_1:\tau_1,\cdots,l_n:\tau_n\}<:\{l'_1:\tau'_1,\cdots,l'_n:\tau'_n\} }
\]

\[
\frac
{ \tau_1<:\tau'_1 \quad \cdots \quad \tau_n<:\tau'_n }
{ \{l_1:\tau_1,\cdots,l_n:\tau_n\}<:\{l_1:\tau'_1,\cdots,l_n:\tau'_n\} }
\]

\[
\frac
{ \tau_1'<:\tau_1 \quad \tau_2<:\tau_2' }
{ \tau_1\rightarrow\tau_2<:\tau_1'\rightarrow\tau_2' }
\]

## STFAE의 확장

STFAE에 추가할 수 있는 타입과, 관련된 서브타입 규칙에 대해 보겠다.

### 최상위 타입

\[
\begin{array}{rrcl}
\text{Type} & \tau & ::= & \cdots \\
&&|& \top \\
\end{array}
\]

타입 \(\top\)은 *최상위*(top) 타입이다. 최상위 타입은 모든 타입의 슈퍼타입이며, 모든 타입이 최상위 타입의 서브타입이다. 모든 값이 최상위 타입의 값이다. 최상위 타입에 대한 서브타입 규칙은 다음과 같다.

\[\tau<:\top\]

최상위 타입은 표현하는 정보의 공통점이 전혀 없는 두 값의 공통된 타입을 나타내기에 적절하다. 언어에 조건식이 있다고 가정하자. 그러면 다음 식은 \(\{a:\textsf{num}\}\) 타입의 식이다.

\[\textsf{if}\ \textsf{true}\ \{a=1\}\ \{a=1,b=2\}\]

타입 \(\{a:\textsf{num}\}\)이 \(\{a=1\}\)과 \(\{a=1,b=2\}\)의 공통된 타입이기 때문이다. 그러나 아래의 식은 실행 중 오류를 일으키지 않음에도 원래의 STFAE에서는 잘못된 타입의 식이다.

\[\textsf{if}\ \textsf{true}\ \{a=1\}\ 1\]

만약 최상위 타입이 존재한다면 위 식의 타입은 \(\top\)이다. 최상위 타입은 모든 값을 원소로 하는 타입이기 때문이다. 이처럼, 최상위 타입을 언어에 추가하여 타입 체계가 더 많은 식을 받아들이게 할 수 있다.

### 최하위 타입

\[
\begin{array}{rrcl}
\text{Type} & \tau & ::= & \cdots \\
&&|& \bot \\
\end{array}
\]

타입 \(\bot\)은 *최하위*(bottom) 타입이다. 최하위 타입은 모든 타입의 서브타입이며, 모든 타입이 최하위 타입의 슈퍼타입이다. 타입 \(\textsf{num}\)과 \(\textsf{num}\rightarrow\textsf{num}\)처럼 공통된 원소가 존재하지 않는 두 타입이 있다. 따라서 모든 타입의 서브타입이려면 어떤 값도 그 타입의 원소가 아니어야 한다. 즉 어떤 값도 최하위 타입의 값이 아니다. 최하위 타입에 대한 서브타입 규칙은 아래와 같다.

\[\bot<:\tau\]

아무 값도 포함하지 않는 타입이지만 최하위 타입은 유용하다. 예외를 발생시키는 식이나 일급 계속의 호출처럼 아무 곳에나 사용될 수 있는 식의 타입을 최하위 타입으로 나타낼 수 있다. 그런 식들은 아무 값도 결과로 내지 않고 프로그램의 흐름을 바꾸는 역할만 한다. 그렇기에 그 식들의 타입을 최하위 타입이라 하는 것이 아무 값도 포함하지 않는다는 최하위 타입의 정의에 모순되지 않는다.

## 감사의 말

글을 확인하고 의견을 주신 류석영 교수님께 감사드립니다. 설명에서 잘못된 부분을
찾아 주신 ‘우주’님께 감사드립니다.
