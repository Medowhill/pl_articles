이번 글에서는 변수에 이름을 붙이지 않고 식을 표현하는 방법인 *de Bruijn 색인*(de Bruijn index)에 대해 알아본다.

## 동기

우리는 지금까지 변수에 이름을 붙임으로써 서로 다른 변수를 구분했다. 예를 들어, \(\lambda {\tt x}.\lambda {\tt y}.{\tt x}\)라는 함수는 인자를 두 번 받은 뒤 그중 첫 번째로 받은 인자를 결과로 내는 함수이다. 여기서 두 인자 중 첫 번째 인자가 결과라는 것을 알 수 있는 이유는 두 인자를 나타내는 데 사용된 이름이 다르기 때문이다. 첫 인자는 \({\tt x}\)라는 이름으로 부르고, 두 번째 인자는 \({\tt y}\)라는 이름으로 부른다. 그러므로 함수의 몸통에 등장한 \({\tt x}\)는 첫 번째 인자를 나타낸다는 사실을 알 수 있는 것이다.

변수에 이름을 붙이는 방법은 직관적으로 이해하기 쉽고 실용적으로 유용하다. 그러나, 엄밀하게 언어의 의미를 다루려는 경우나 코드를 입력으로 받아 처리하는 인터프리터와 컴파일러를 구현하는 경우에는 변수에 이름을 붙여서 구분하는 것이 문제를 일으키고는 한다.

첫 번째로, 같은 이름의 변수가 둘 이상 존재하는 경우에 두 변수를 구분하는 데 문제가 생길 수 있다. 지금까지는 언어의 의미를 정의하는 데 환경을 사용했기 때문에 변수의 이름이 겹쳐도 별 문제가 없었다. 그러나, 환경 대신 치환을 사용해 언어의 의미를 정의하는 경우도 많다. 예를 들어, 치환을 사용해 함수 적용의 의미를 정의한다면, \((\lambda {\tt x}.{\tt x} + {\tt x})\ 1\)을 계산하는 것은 함수 몸통인 \({\tt x} + {\tt x}\)에서 \({\tt x}\)를 \(1\)로 치환하여 얻어진 식인 \(1+1\)을 계산하는 것과 같다고 정의하는 것이다. 이름을 사용하는 것의 문제점을 이야기하는 것이 주 목적이므로 치환을 엄밀하게 정의하지는 않겠다. 얼핏 보기에는 별 문제가 없어 보이지만, 치환을 사용해 함수 적용의 의미를 올바르게 정의하기는 생각보다 어렵다. \((\lambda {\tt f}.\lambda {\tt x}.{\tt f})\ \lambda {\tt y}.{\tt x}\)라는 식을 생각해 보자. 위에서 이야기한 것과 비슷하게 치환을 사용하면 이 식을 계산하는 것은 \(\lambda {\tt x}.{\tt f}\)에서 \({\tt f}\)를 \(\lambda {\tt y}.{\tt x}\)로 치환하여 얻어진 \(\lambda {\tt x}.\lambda {\tt y}.{\tt x}\)를 계산하는 것과 같다. 하지만 이는 말이 되지 않는다. 원래 인자인 \(\lambda {\tt y}.{\tt x}\)의 \({\tt x}\)는 자유 식별자였지만 \(\lambda {\tt x}.\lambda {\tt y}.{\tt x}\)의 \({\tt x}\)는 묶인 등장이다. 즉, \({\tt x}\)가 의미하는 바가 완전히 달라진 것이다. 이렇게 정의한 함수 적용의 의미는 올바르다고 보기 어렵다. 그리고, 이런 문제가 발생한 이유는 서로 다른 대상을 의미하는 두 변수에 \({\tt x}\)라는 같은 이름을 사용했기 때문이다.

이름이 겹쳐서 생기는 문제는 반드시 변수에만 국한된 것이 아니다. “대수적 데이터 타입” 글에서 TVFAE를 정의할 때와, “매개변수에 의한 다형성” 글에서 TPFAE를 정의할 때, 타입 안전성에 문제가 생기는 여러 이유를 살펴보았다. 그 이유 중 하나는 이미 어떤 타입이 정의된 상태에서 같은 이름의 타입을 다시 정의하는 것을 허용한 것이었다. 따라서 타입 안전성을 지키기 위해서는 타입 규칙에 같은 이름의 타입을 다시 정의할 수 없다는 전제를 추가해야만 했다. 이 역시 이름을 사용해서 두 타입을 구분했기 때문에 발생한 문제이다. 두 타입이 전혀 다른 대상을 의미하는 데도, 두 타입이 같은 이름을 사용하기 때문에 같은 타입이라고 오인될 수 있었던 것이다.

둘째로, 이름을 사용해서 변수를 나타내면 두 식의 의미가 같은지 확인하기 어렵다. 예를 들어, \(\lambda {\tt x}.{\tt x}\)와 \(\lambda {\tt y}.{\tt y}\)는 모두 항등 함수이다. 그러나, 두 함수가 같다는 사실을 두 식을 문법적으로만 확인해서는 알기 어렵다. 첫 번째 식에서는 \({\tt x}\)를 매개변수의 이름으로 사용한 반면 두 번째 식에서는 \({\tt y}\)를 매개변수의 이름으로 사용했기 때문이다. 두 식이 같은 식인지 확인하고 싶은 상황은 여러 곳에서 찾을 수 있다. 예를 들면, 주어진 코드를 최적화하려는 상황을 떠올릴 수 있다.

\[
\begin{array}{l}
{\sf val}\ {\tt f}=\lambda {\tt x}.{\tt x}; \\
{\sf val}\ {\tt g}=\lambda {\tt y}.{\tt y}; \\
({\tt f}\ 1)+({\tt g}\ 2) \\
\end{array}
\]

위 식은 함수 \(\tt f\)와 \(\tt g\)를 정의한 뒤 \(({\tt f}\ 1)+({\tt g}\ 2)\)를 계산한다. 사실 \(\tt f\)와 \(\tt g\)는 매개변수의 이름만 다를 뿐 같은 일을 하는 함수이다. 따라서, 컴파일러는 위의 식을 아래와 같이 바꿈으로써 만들어 내는 기계어 코드의 크기를 줄일 수 있다.

\[
\begin{array}{l}
{\sf val}\ {\tt f}=\lambda {\tt x}.{\tt x}; \\
({\tt f}\ 1)+({\tt f}\ 2) \\
\end{array}
\]

언어에 다형 타입이 존재하는 경우에는 두 타입이 같은지 확인하는 데 타입의 이름이 문제가 될 수 있다. 매개변수에 의한 다형성을 제공하는 TPFAE를 생각해 보면, \(\forall\alpha.\alpha\rightarrow\alpha\)와 \(\forall\beta.\beta\rightarrow\beta\) 모두 다형 항등 함수의 타입이다. 따라서 \(\forall\alpha.\alpha\rightarrow\alpha\) 타입의 식이 필요한 자리에 \(\forall\beta.\beta\rightarrow\beta\) 타입의 식이 오도록 허용하는 것이 바람직하다. 그러나, 두 타입을 문법적으로 비교해서는 두 타입이 같은 타입인지 알아낼 수 없다.

위의 두 예시에서 볼 수 있듯이, 두 식이 같은 식인지 알아내는 것(또는, 두 타입이 같은 타입인지 알아내는 것)은 여러 곳에서 필요한 일이며, 이름을 붙여서 대상을 구분하는 것은 이에 방해가 된다.

지금까지 설명한 것처럼, “이름”은 프로그래밍 언어에서 자주 문제가 된다. 서로 다른 대상을 같은 이름으로 부르는 바람에 문제가 발생하기도 하고, 이름 때문에 두 대상이 같은 의미를 지니는지 확인하기 어려워지기도 한다. 이러한 문제를 극복하고자 다양한 방법이 제시되었다. 이번 글에서 다루는 de Bruijn 색인은 그런 방법 중 한 가지로, 이름을 아예 사용하지 않음으로써 문제를 해결한다. 논의를 간단하게 하기 위해 이 글에서는 de Bruijn 색인을 변수와 식에 사용하는 것만 생각한다. 그러나 de Bruijn 색인은 이름이 문제가 되는 다른 곳에도 얼마든지 사용할 수 있다. 예를 들면, 타입을 표현하는 데도 de Bruijn 색인을 사용할 수 있다.

## de Bruijn 색인

de Bruijn 색인은 변수를 이름 대신 자연수로 나타낸다. 어떤 묶인 등장이 있다면, 그 변수에 해당하는 묶는 등장과 그 묶인 등장 사이에 있는 \(\lambda\)의 개수로 변수를 표현하는 것이다. 예를 들면, \(\lambda {\tt x}.{\tt x}\)는 \(\lambda.\underline{0}\)으로 표현된다. \(\lambda.\underline{0}\)은 어떤 함수이며, 그 함수는 한 개의 매개변수를 가지고 있다. 함수의 몸통은 \(\underline{0}\)인데, \(\underline{0}\)은 정수 \(0\)과는 다르다. \(\underline{0}\)은 어떤 변수이며, 이 변수의 정의와 \(\underline{0}\) 사이에는 0개의 \(\lambda\)가 있어야 한다. 그러므로, \(\lambda.\underline{0}\)의 매개변수가 \(\underline{0}\)이 가리키는 대상이다. 비슷한 원리로, \(\lambda {\tt x}.\lambda{\tt y}.{\tt x}\)는 \(\lambda.\lambda.\underline{1}\)로 표현된다. \(\lambda.\lambda.\underline{1}\)은 매개변수가 한 개인 함수를 정의하며 그 몸통은 \(\lambda.\underline{1}\)이다. \(\lambda.\underline{1}\) 역시 매개변수가 한 개인 함수를 정의한다. 그 몸통은 \(\underline{1}\)이다. \(\underline{1}\)은 어떤 변수이고, 그 변수의 정의와 \(\underline{1}\) 사이에는 1개의 \(\lambda\)가 있다. 따라서, \(\lambda.\underline{1}\)의 매개변수는 \(\underline{1}\)이 나타내는 대상이 아니다. 둘 사이에는 \(\lambda\)가 한 개도 없기 때문이다. \(\underline{1}\)이 나타내는 대상은 \(\lambda.\lambda.\underline{1}\)의 매개변수로, 둘 사이에는 \(\lambda\)가 한 개 있다. 아래는 de Bruijn 색인을 통해 여러 식을 다시 쓴 것이다.

* \(\lambda {\tt x}.{\tt x}\rightarrow\lambda.\underline{0}\)
* \(\lambda {\tt x}.\lambda {\tt y}.{\tt x}\rightarrow\lambda.\lambda.\underline{1}\)
* \(\lambda {\tt x}.\lambda {\tt y}.{\tt y}\rightarrow\lambda.\lambda.\underline{0}\)
* \(\lambda {\tt x}.\lambda {\tt y}.{\tt x}+{\tt y}\rightarrow\lambda.\lambda.\underline{1}+\underline{0}\)
* \(\lambda {\tt x}.\lambda {\tt y}.{\tt x}+{\tt y}+42\rightarrow\lambda.\lambda.\underline{1}+\underline{0}+42\)
* \(\lambda {\tt x}.({\tt x}\ \lambda {\tt y}.({\tt x}\ {\tt y}))\rightarrow\lambda.(\underline{0}\ \lambda.(\underline{1}\ \underline{0}))\)
* \(\lambda {\tt x}.((\lambda {\tt y}.{\tt x})\ (\lambda {\tt z}.{\tt x}))\rightarrow\lambda.((\lambda.\underline{1})\ (\lambda.\underline{1}))\)

예시에서 알 수 있는 한 가지 주의할 점은, 서로 다른 두 수가 같은 변수를 나타낼 수 있고, 같은 두 수가 서로 다른 변수를 나타낼 수 있다는 것이다. 마지막에서 두 번째에 있는 예시에서 \(\lambda {\tt x}.({\tt x}\ \lambda {\tt y}.({\tt x}\ {\tt y}))\)를 de Bruijn 색인으로 다시 쓰자 \(\lambda.(\underline{0}\ \lambda.(\underline{1}\ \underline{0}))\)이 되었다. 여기서 첫 \(\underline{0}\)은 원래 식의 \({\tt x}\)이다. 한편, \(\underline{1}\) 역시 원래 식의 \({\tt x}\)이다. 그러나, 두 번째 \(\underline{0}\)는 원래 식의 \({\tt y}\)이다. 이처럼, de Bruijn 색인을 사용하면, 변수는 이름이 아니라 그 변수가 사용된 곳과 정의된 곳 사이에 있는 \(\lambda\)의 개수로 표현되므로, 사용된 곳에 따라 같은 변수여도 다른 수로 표현된다.

한 가지 더 짚고 넘어가자면, 사용된 곳과 정의된 곳을 이야기할 때는 식을 문자열 형태로 쓴 상태에서 이야기하는 것이 아니라, 식을 나무 형태로 표현한 상태에서 이야기하는 것이다. 마지막 예시에서 \(\lambda {\tt x}.((\lambda {\tt y}.{\tt x})\ (\lambda {\tt z}.{\tt x}))\)는 de Bruijn 색인을 사용하여 \(\lambda.((\lambda.\underline{1})\ (\lambda.\underline{1}))\)이라 다시 썼다. 비록 문자열 형태로 볼 때는 마지막 \(\tt x\)와 그 변수를 정의한 곳 사이에 \(\lambda\)가 두 개 있지만, 나무 형태로 생각하면 사용된 곳과 변수 사이에는 \(\lambda\)가 한 개뿐이다. 그러므로 de Bruijn 색인이 \(\underline{1}\)이다. 편의상 식을 문자열 형태로 자주 쓸 뿐이지, 요약 문법에 의해 정의된 식은 본래 나무 구조라는 것에 유의하기를 바란다.

de Bruijn 색인은 앞에서 설명한, 이름을 사용한 식의 문제점을 잘 해결한다. 치환에 대해서는 자세히 다루지 않을 것이므로, 두 식이 같은지 비교하는 경우만 생각해 보자. \(\lambda {\tt x}.{\tt x}\)와 \(\lambda {\tt y}.{\tt y}\)는 같은 의미의 서로 다른 식이다. 두 식을 de Bruijn 색인을 사용하여 다시 쓰면 둘 모두 \(\lambda.\underline{0}\)이 되므로 두 식이 같은 식이라는 사실을 문법적 비교만으로 쉽게 알 수 있다.

de Bruijn 색인을 사용하지 않는 식을 de Bruijn 색인을 사용하도록 변형하는 과정을 정의해 보자. 이 과정을 정의하는 것은 de Bruijn 색인을 잘 이해하는 데 도움이 된다. 또한, 프로그래머가 코드를 작성할 때는 변수에 이름을 붙이는 것이 편하기 때문에 프로그래머가 작성할 때 사용하는 식은 de Bruijn 색인을 사용하지 않지만, 프로그래머가 작성한 식을 입력으로 받아 처리하는 인터프리터나 컴파일러 등의 프로그램은 내부적으로 de Bruijn 색인을 사용하는 것이 좋을 수 있다. 이 경우, de Bruijn 색인을 사용하지 않는 식을 사용하는 식으로 변형하는 과정이 인터프리터나 컴파일러 구현의 일부이므로 이 과정이 매우 중요하다. 이 글에서는 FAE에 대한 과정을 정의할 것이다. 그러나 FAE가 아닌 다른 언어에도 거의 비슷하게 정의할 수 있다.

먼저 de Bruijn 색인을 사용하지 않는 식과 de Bruijn 색인을 사용하는 식을 각각 정의하겠다. 각각의 식을 서로 다른 메타변수로 나타내는 것이 가장 정확하겠지만, 사용한 맥락으로부터 둘을 어렵지 않게 구분할 수 있으므로, 편의상 두 종류의 식 모두에 메타변수 \(e\)를 사용하겠다.

\[
\begin{array}{lrcl}
\text{Expression} & e & ::= & x \\
&&|& \lambda x.e \\
&&|& e\ e \\
&&|& n \\
&&|& e+e \\
\end{array}
\]

위는 de Bruijn 색인을 사용하지 않는 원래의 식이다.

\[
\begin{array}{lrcl}
\text{Index} & i & \in & \mathbb{N} \\
\text{Expression} & e & ::= & \underline{i} \\
&&|& \lambda.e \\
&&|& e\ e \\
&&|& n \\
&&|& e+e \\
\end{array}
\]

de Bruijn 색인을 사용하는 식에서는 변수를 자연수로 나타낸다. 다만, FAE에 원래 있는 정수와 쉽게 구분되도록 변수를 나타내는 수에는 밑줄을 추가했다. 람다 요약은 더 이상 매개변수의 이름을 정의할 필요가 없으므로 람다 요약의 문법은 \(\lambda.e\)이다. 매개변수의 이름을 쓸 필요가 없기에 몸통만 썼을 뿐이지 한 개의 매개변수가 있는 것은 전과 같다. 매개변수가 없는 함수라고 생각하면 안 된다.

추가로, de Bruijn 색인을 사용하는 식으로 변형하는 과정에서 사용할 문맥을 정의하겠다. 문맥의 역할은 어떤 변수가 등장했을 때 그 변수가 정의와 얼마나 떨어져 있는지 알려주는 것이다. 따라서, 문맥은 변수의 이름에서 자연수로 가는 부분 함수이다.

\[\chi\in{\it Id}\hookrightarrow\mathbb{N}\]

\([e]\chi\)를 문맥 \(\chi\)에서 식 \(e\)를 de Bruijn 색인을 사용하도록 변형한 결과라고 하자. 그러면 \([e]\chi\)의 정의는 다음과 같다.

\[
\begin{array}{rcl}
[x]\chi &=& \underline{i}\ \ \text{if}\ \chi(x)=i \\
[\lambda x.e]\chi &=& \lambda.[e]{\chi'}\ \ \text{where}\ \chi'=(\uparrow\chi)[x\mapsto 0] \\
[e_1\ e_2]\chi &=& [e_1]\chi\ [e_2]\chi \\
[n]\chi &=& n \\
[e_1+e_2]\chi &=& [e_1]\chi+[e_2]\chi \\
\end{array}
\]

\([x]\chi\)는 \(x\)를 변형한 결과이다. de Bruijn 색인을 사용하면 변수는 하나의 자연수로 표현되며, 어떤 자연수로 표현할지는 \(\chi\)에서 찾을 수 있다. 따라서 \(\chi(x)\)가 \(i\)일 때 \(x\)를 변형한 결과는 \(\underline{i}\)이다.

\([\lambda x.e]\chi\)는 \(\lambda x.e\)를 변형한 결과로, 매개변수의 이름을 더 이상 쓸 필요 없으므로 \(\lambda.e\) 형태의 식이 나와야 한다. 단, 원래 주어진 \(e\)는 de Bruijn 색인을 사용하는 식이 아니므로 \(e\)를 de Bruijn 색인을 사용하게 변형하는 과정을 거쳐야 한다. 이때, 주어진 \(\chi\)를 그대로 사용할 수는 없다. 우선, \(x\)에 대한 정보를 추가해야 한다. 만약 \(e\)에서 \(x\)가 어떤 함수도 정의하지 않은 채로 바로 사용되었다면, \(x\)의 정의와 사용 사이에는 \(\lambda\)가 한 개도 없다. 그러므로 \(x\)의 색인이 0이라는 정보를 \(\chi\)에 추가해야 한다. 이와 동시에, \(\chi\)에 원래 들어 있던 변수와 그 색인에도 변화가 필요하다. \(\chi\)에 \(x'\)의 색인이 0이라는 정보가 있었다고 하자. \(e\)에서 \(x'\)을 사용한다면 그 색인은 0이어서는 안 된다. 왜냐하면 \(e\)는 \(\lambda x.e\)의 몸통이기 때문에 \(x'\)의 사용과 정의 사이에 한 개의 \(\lambda\)가 있기 때문이다. \(e\)를 변형하는 동안은 \(x'\)의 색인이 0이 아니라 1이어야 하는 것이다. 마찬가지 원리로, \(\chi\)에서 색인이 1인 변수가 있다면 \(e\)를 변형하는 동안은 색인이 2가 된다. 이는 \(\chi\)에 있던 모든 변수에 적용된다. \(\chi\)의 모든 색인이 1씩 증가해야 하는 것이다. \(\chi\)에서 변수는 그대로 두고 각 변수의 색인을 1씩 증가시켜 만들어진 새로운 문맥을 \(\uparrow\chi\)라고 쓰겠다. 그러면 \(e\)를 변형하는 과정 중에 사용할 문맥은 \((\uparrow\chi)[x\mapsto0]\)이다. 이는 \(\chi\)의 모든 색인을 1씩 키운 뒤 \(x\)의 색인이 0이라는 정보를 추가한 것이다. \((\uparrow\chi)[x\mapsto0]\)를 \(\chi'\)이라고 할 때, \(e\)를 \(\chi'\)에서 변형하여 얻은 식이 새로운 람다 요약의 몸통이므로 \([\lambda x.e]\chi\)는 \(\lambda.[e]{\chi'}\)이다.

나머지 경우는 어렵지 않다. \(e_1\ e_2\)와 \(e_1+e_2\)의 경우 각 부분식을 재귀적으로 변형하면 된다. \(n\)에는 어떤 변수도 없으므로 그대로 \(n\)이 나온다.

위에서 정의한 과정에 따라 \(\lambda {\tt x}.\lambda {\tt y}.{\tt x}+{\tt y}\)를 de Bruijn 색인을 사용하도록 변형해 보겠다. 처음 시작할 때는 어떤 변수도 아직 정의되지 않았으므로 문맥이 비어 있다.

\[
\begin{array}{cl}
& [\lambda {\tt x}.\lambda {\tt y}.{\tt x}+{\tt y}]\emptyset \\
= & \lambda.[\lambda {\tt y}.{\tt x}+{\tt y}][{\tt x}\mapsto 0] \\
= & \lambda.\lambda.[{\tt x}+{\tt y}][{\tt x}\mapsto 1,{\tt y}\mapsto 0] \\
= & \lambda.\lambda.[{\tt x}][{\tt x}\mapsto 1,{\tt y}\mapsto 0]+[{\tt y}][{\tt x}\mapsto 1,{\tt y}\mapsto 0] \\
= & \lambda.\lambda.\underline{1}+[{\tt y}][{\tt x}\mapsto 1,{\tt y}\mapsto 0] \\
= & \lambda.\lambda.\underline{1}+\underline{0} \\
\end{array}
\]

(\([\ ]\)가 식의 변환과 문맥을 나타내는 데 모두 사용되었지만, 둘 중 어느 용도로 사용된 것인지 알아보는 데 어려움이 없으므로 그대로 두겠다.)

앞에서 설명한 것과 같은 결과인 \(\lambda.\lambda.\underline{1}+\underline{0}\)이 나왔다.

이 과정을 Scala로 구현해 보겠다.

```scala
sealed trait Expr
case class Id(x: String) extends Expr
case class Fun(x: String, e: Expr) extends Expr
case class App(f: Expr, a: Expr) extends Expr
case class Num(n: Int) extends Expr
case class Add(l: Expr, r: Expr) extends Expr
```

먼저 de Bruijn 색인을 사용하지 않는 식의 구현이다. FAE의 요약 문법과 똑같으므로 어렵지 않다.

```scala
object Nameless {
  sealed trait Expr
  case class Id(i: Int) extends Expr
  case class Fun(e: Expr) extends Expr
  case class App(f: Expr, a: Expr) extends Expr
  case class Num(n: Int) extends Expr
  case class Add(l: Expr, r: Expr) extends Expr
}
```

de Bruijn 색인을 사용하지 않는 식과 구분하기 위해, de Bruijn 색인을 사용하는 식은 `Nameless`라는 단독 객체 안에 정의했다. `Id(i)`는 색인이 `i`인 변수이며, `Fun(e)`는 몸통이 `e`이고 매개변수가 한 개인 함수이다.

```scala
type Ctx = Map[String, Int]
```

식을 변형하는 과정에서 사용할 문맥의 타입은 `Ctx`로, 문자열에서 정수로 가는 사전이다.

de Bruijn 색인을 사용하도록 식을 변형하는 과정은 재귀 함수로 구현된다. 그 함수의 이름을 `transform`이라 하겠다. `transform`은 de Bruijn 색인을 사용하지 않는 식과 문맥을 인자로 받아서 de Bruijn 색인을 사용하는 식을 결과로 낸다.

```scala
def transform(e: Expr, ctx: Ctx): Nameless.Expr = e match {
  case Id(x) => Nameless.Id(ctx(x))
  case Fun(x, e) =>
    Nameless.Fun(transform(e, ctx.map{ case (x, i) => x -> (i + 1) } + (x -> 0)))
  case App(f, a) =>
    Nameless.App(transform(f, ctx), transform(a, ctx))
  case Num(n) => Nameless.Num(n)
  case Add(l, r) =>
    Nameless.Add(transform(l, ctx), transform(r, ctx))
}
```

위의 정의를 그대로 코드로 옮긴 것이므로 별로 어렵지 않다.

\(\lambda {\tt x}.\lambda {\tt y}.{\tt x}+{\tt y}\)을 `transform` 함수를 통해 de Bruijn 색인을 사용하도록 변형해 보겠다.

```scala
// lambda x.lambda y.x+y
transform(Fun("x", Fun("y", Add(Id("x"), Id("y")))), Map())
// Fun(Fun(Add(Id(1),Id(0))))
// lambda.lambda._1+_0
```

문맥을 사전 대신 리스트로 나타낼 수도 있다. 문맥을 이름의 리스트로 정의하고, 어떤 이름의 색인은 문맥에서 그 이름의 위치로 나타내는 것이다. 문맥을 리스트로 표현하면 구현이 더 간단해진다. 이름이 문맥에 처음 추가될 때 그 이름의 색인은 언제나 0이다. 색인이 0이라는 것은 리스트의 0번째 원소, 즉 머리라는 것이다. 따라서 이름을 문맥에 추가하려면 리스트의 머리를 그 이름으로 하면 된다. 또, 문맥의 모든 색인을 1씩 증가시키는 것은 리스트의 각 이름을 한 칸씩 뒤로 이동시키는 것과 같다. 그러므로 문맥을 리스트로 나타내면, 어떤 문맥에 새로운 이름을 추가한 뒤 기존에 있던 이름의 색인을 1씩 증가시키는 작업을 문맥의 앞에 새로운 이름을 추가하는 것으로 간단하게 끝낼 수 있다. 예를 들어, 기존의 문맥에서 \(\tt x\)의 색인은 0이고 \(\tt y\)의 색인은 1이었다고 하자. 그러면 문맥을 나타내는 리스트는 \([{\tt x},{\tt y}]\)이다. 여기에 \(\tt z\)라는 이름을 추가하려면 \(\tt z\)를 리스트의 앞에 붙이기만 하면 된다. 그렇게 얻어진 리스트는 \([{\tt z},{\tt x},{\tt y}]\)로, \(\tt z\)의 색인은 0, \(\tt x\)의 색인은 1, \(\tt y\)의 색인은 2이다. \(\tt z\)는 새로 추가된 이름이므로 색인이 0이어야 하고 \(\tt x\)와 \(\tt y\)의 색인은 기존보다 1씩 커졌어야 하므로, 새로운 리스트가 문맥을 잘 표현하고 있다.

```scala
type Ctx = List[String]
```

이제 `Ctx`는 문자열의 리스트이다.

```scala
def transform(e: Expr, ctx: Ctx): Nameless.Expr = e match {
  case Id(x) => Nameless.Id(ctx.indexOf(x))
  case Fun(x, e) => Nameless.Fun(transform(e, x :: ctx))
  case App(f, a) =>
    Nameless.App(transform(f, ctx), transform(a, ctx))
  case Num(n) => Nameless.Num(n)
  case Add(l, r) =>
    Nameless.Add(transform(l, ctx), transform(r, ctx))
}
```

`Id` 경우에는 주어진 변수의 이름이 문맥의 어디에 있는지 알아야 한다. 이는 `indexOf` 메서드를 통해 쉽게 할 수 있다. `Fun` 경우는 이전보다 훨씬 간단해졌다. 주어진 문맥 `ctx`에 새로운 이름 `x`를 추가하는 것을 `x :: ctx`로 끝낼 수 있다. 나머지 경우는 이전과 같다.

\(\lambda {\tt x}.\lambda {\tt y}.{\tt x}+{\tt y}\)을 `transform` 함수를 통해 de Bruijn 색인을 사용하도록 변형해 보겠다.

```scala
// lambda x.lambda y.x+y
transform(Fun("x", Fun("y", Add(Id("x"), Id("y")))), Nil)
// Fun(Fun(Add(Id(1),Id(0))))
// lambda.lambda._1+_0
```

## de Bruijn 색인을 사용하는 식의 계산

de Bruijn 색인을 사용하는 식의 계산은 de Bruijn 색인을 사용하지 않는 식의 계산과 비슷하다. 다만, 변수를 나타내는 데 이름 대신 색인을 사용하므로 그에 따른 변화가 필요하다.

먼저, 값과 환경을 정의하겠다.

\[
\begin{array}{rrcl}
\text{Value} & v & ::= & n \\
&&|& \langle\lambda.e,\sigma\rangle \\
\text{Environment} & \sigma & \in & \mathbb{N}\hookrightarrow\text{Value}
\end{array}
\]

값은 전과 비슷하지만 람다 요약에서 매개변수의 이름이 빠진 것에 따라 클로저에서도 매개변수의 이름이 빠졌다. 환경의 경우, 이제 이름에서 값으로 가는 부분 함수가 아니라 색인, 즉 자연수에서 값으로 가는 부분 함수이다.

\[
\frac
{ i\in{\it Domain}(\sigma) }
{ \sigma\vdash\underline{i}\Rightarrow\sigma(i) }
\]

변수의 값을 알려면 환경에서 그 변수의 값을 찾아야 한다.

\[
\sigma\vdash\lambda.e\rightarrow\langle\lambda.e,\sigma\rangle
\]

람다 요약은 아무 계산 없이 그대로 클로저가 된다.

\[
\frac
{
  \sigma\vdash e_1\Rightarrow\langle\lambda.e,\sigma'\rangle \quad
  \sigma\vdash e_2\Rightarrow v_2 \quad
  (\uparrow\sigma')[0\mapsto v_2]\vdash e\Rightarrow v
}
{ \sigma\vdash e_1\ e_2\Rightarrow v }
\]

\(e_1\ e_2\)을 계산하려면 \(e_1\)과 \(e_2\)를 각각 계산해야 한다. 여기까지는 전과 같다. 그리고 클로저의 환경에 인자의 값을 추가한 뒤 클로저의 몸통을 계산해야 하는 것도 전과 같지만, 클로저의 환경에 인자의 값을 추가하는 방법이 약간 다르다. 클로저의 몸통에서 클로저의 매개변수가 사용되었다면, 사용과 정의 사이에 \(\lambda\)가 없으므로 사용될 때 그 색인은 0이다. 그러므로 인자의 값이 몸통에서 올바르게 사용될 수 있으려면 색인 0에 해당하는 값이 인자의 값이라는 정보를 클로저의 환경에 추가해야 한다. 또한, 환경에 원래 들어 있던 색인도 달라져야 한다. 색인 0에 해당하는 값이 \(v\)라는 정보가 환경에 들어 있었다고 해 보자. 이 값은 클로저의 매개변수의 값이 아니다. 그러므로, 더 이상 색인 0에 해당할 수 없다. 클로저의 \(\lambda\)가 사이에 추가된 것에 따라, 색인 역시 1 커져야 한다. 이 이야기는 환경에 원래 들어 있던 모든 색인에 적용된다. 따라서, 환경의 모든 색인이 1씩 증가한다. \(\sigma'\)의 모든 색인을 1씩 키운 것을 \(\uparrow\sigma'\)라고 쓰겠다. 그러면 클로저의 몸통을 계산할 때 사용되는 환경은 \((\uparrow\sigma')[0\mapsto v_2]\)이다.

정수와 합의 의미를 정의하는 규칙은 이전과 같으므로 생략하겠다.

다음은 \((\lambda.\lambda.\underline{1}+\underline{0})\ 2\ 3\)의 결과가 \(5\)임을 증명하는 증명 나무이다.

\[
\frac
{
  {\Large
  \frac
  {
    \emptyset\vdash\lambda.\lambda.\underline{1}+\underline{0}\Rightarrow\langle\lambda.\lambda.\underline{1}+\underline{0},\emptyset\rangle\quad
    \emptyset\vdash2\Rightarrow2\quad
    [0\mapsto2]\vdash\lambda.\underline{1}+\underline{0}\Rightarrow\langle\lambda.\underline{1}+\underline{0},[0\mapsto2]\rangle
  }
  { \emptyset\vdash(\lambda.\lambda.\underline{1}+\underline{0})\ 2\Rightarrow\langle\lambda.\underline{1}+\underline{0},[0\mapsto2]\rangle }
  }
  \quad
  \emptyset\vdash3\Rightarrow3 \quad
  {\Large
  \frac
  {
    {\huge
    \frac
    { 1\in{\it Domain}(\sigma) }
    { \sigma\vdash\underline{1}\Rightarrow2 }
    \quad
    \frac
    { 0\in{\it Domain}(\sigma) }
    { \sigma\vdash\underline{0}\Rightarrow3 }
    }
  }
  { \sigma\vdash\underline{1}+\underline{0}\Rightarrow5 }
  }
}
{ \emptyset\vdash(\lambda.\lambda.\underline{1}+\underline{0})\ 2\ 3\Rightarrow5 }
\]

\[\sigma=[0\mapsto3,1\mapsto2]\]

이제 de Bruijn 색인을 사용하는 식의 인터프리터를 Scala로 구현해 보겠다. 식은 이미 정의했으므로 값과 환경만 정의하겠다.

```scala
type Env = List[Value]

sealed trait Value
case class NumV(n: Int) extends Value
case class CloV(e: Expr, env: Env) extends Value
```

환경은 정수에서 값으로 가는 사전으로 정의해도 되지만, `transform` 함수를 구현할 때 본 것처럼 사전 대신 리스트를 사용하는 것이 간편하므로 환경을 값의 리스트로 정의했다.

```scala
def interp(e: Expr, env: Env): Value = e match {
  case Id(i) => env(i)
  case Fun(e) => CloV(e, env)
  case App(f, a) =>
    val CloV(b, fenv) = interp(f, env)
    interp(b, interp(a, env) :: fenv)
  case Num(n) => NumV(n)
  case Add(l, r) =>
    val NumV(n) = interp(l, env)
    val NumV(m) = interp(r, env)
    NumV(n + m)
}
```

`interp` 함수에서 주목할 부분은 `App` 경우뿐이다. 클로저가 매개변수의 이름을 알려주지 않고 환경에도 매개변수의 이름이 필요하지 않으므로, 인자의 값은 환경을 나타내는 리스트의 앞에 붙이기만 하면 된다.

`interp` 함수로 \((\lambda.\lambda.\underline{1}+\underline{0})\ 2\ 3\)를 계산하면 \(5\)가 나온다.

```scala
// (lambda.lambda._1+_0) 2 3
interp(
  App(
    App(
      Fun(Fun(Add(Id(1), Id(0)))),
      Num(2)
    ),
    Num(3)
  ),
  Nil
)
// 5
```

de Bruijn 색인을 사용하지 않은 식을 바로 계산하는 것과, 그 식을 de Bruijn 색인을 사용하도록 바꾼 다음에 계산한 것의 결과는 똑같다. 즉, \(\forall e,v.(\emptyset\vdash e\Rightarrow v)\leftrightarrow(\emptyset\vdash[e]\emptyset\Rightarrow v)\)이다. (클로저의 같음이 잘 정의되어 있다고 가정하자.) Scala 구현에서도 de Bruijn 색인을 사용하지 않는 어떤 식 `e`가 주어졌을 때 `interp(e, Map())`과 `interp(transform(e, Nil), Nil)`은 같은 결과를 낸다.

이 글에서 구현한 인터프리터는 FAE의 계속 전달 방식을 사용하지 않는 인터프리터뿐이다. 위 구현을 잘 이해했다면 FAE가 아닌 다른 언어의 인터프리터나 계속 전달 방식을 사용하는 인터프리터도 de Bruijn 색인을 사용하도록 구현할 수 있을 것이다.

## 감사의 말

글을 확인하고 의견을 주신 류석영 교수님께 감사드립니다.
