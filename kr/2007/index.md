**아래 내용은 인사이트 출판사의 제안으로 작성 중인 책의 초고입니다. 실제 출판
시에는 내용이 달라질 수 있습니다. 많은 의견 부탁드립니다.**

우리는 앞서 언어의 문법이 구체적 문법과 요약 문법으로 나누어 정의된다는 것을
보았다. 구체적 문법의 역할은 프로그램인 문자열을 정하는 것이다. 요약 문법은
프로그램의 구조를 정의하며, 그렇게 정의된 구조는 의미를 정의하는 데 사용된다.
그럼 여기서 두 가지 의문점이 생긴다.

첫 번째로, 구체적 문법과 요약 문법을 어떻게 연결하냐는 것이다. 프로그래머는
구체적 문법에 따라 프로그램을 작성한다. 그 결과로 나온 프로그램은 단순히
문자열일 뿐이다. 한편 프로그램의 의미를 생각할 때는 프로그램을 요약 문법을
따르는 나무로 간주한다. 즉, 의미를 통해 알 수 있는 것은 주어진 나무 구조의
프로그램을 실행하면 일어나는 일이다. 문자열 형태의 프로그램에 대해서는 아무런
정보도 얻을 수 없다. 따라서 문자열 형태의 프로그램을 나무 구조의 프로그램으로
변환할 방법이 필요하다.

두 번째 의문은 왜 요약 문법이 모두 필요한지이다. 구체적 문법의 필요성은
분명하다. 프로그래머가 코드를 작성할 때 어떤 문자열이 프로그램이고 어떤 문자열이
프로그램이 아닌지 알아야 프로그램을 만들 수 있다. 그러나 굳이 요약 문법이라는
단계를 거쳐야 하는 것일까? 요약 문법을 사용하지 않고 문자열에 대해 바로 언어의
의미를 정의할 수 있을 것이다. 예를 들면 “1”의 실행 결과는 1, “1+2”의 실행 결과는
3과 같은 식으로 바로 언어의 의미를 정의할 수도 있다. 그러나 우리는 그렇게 하지
않고 요약 문법을 사이에 넣은 다음 요약 문법 나무로 표현된 프로그램의 의미만
정의한다.

이 두 의문에 대한 답은 파싱이 알려 준다. 첫 번째 질문에는 파싱 그 자체가 답이나
다름없으며, 두 번째 질문에 대한 답의 중요한 실마리도 파싱이 제공한다. 지금부터
파싱이 무엇이며 두 질문에 대한 답이 어떻게 되는지 알아보겠다.

문자의 집합 \(C\)가 있을 때, 구체적 문법은 프로그램의 집합인 \(P\)를 정의한다. \(P\)는
문자열의 집합인 \(C^\ast\)의 부분 집합이다.

\[P = 모든\ 프로그램의\ 집합 = \{ p\ | p는\ 프로그램 \} \subseteq C^\ast\]

한편 요약 문법은 나무의 집합인 \(E\)를 정의한다. \(E\)의 각 원소는 한 프로그램의 구조를
표현한다. 파싱은 집합 \(P\)에서 \(E\)로 가는 함수이다.

\[파싱: P \rightarrow E\]

예를 들면 `산술`의 파싱은 다음과 같이 동작할 것이다.

* \(파싱(“1”) = 1\)
* \(파싱(“1+2”) = 1 + 2\)

따라서 파싱이 첫 의문의 해답이다. 프로그래머가 작성한 코드가 프로그램이 맞으면,
파싱 함수를 통해 그 문자열을 나무 구조의 프로그램으로 바꿀 수 있다. 그리고 그
나무 구조의 프로그램을 실행하면 언어의 의미에 따라 결과를 낸다. 반대로, 작성한
코드가 프로그램이 아니라면 파싱 함수를 사용할 수조차 없으므로 당연히 실행할 수
없다. 이처럼 파싱은 언어의 문법에서 매우 중요한 역할을 담당한다. 그러므로 앞에서
언어의 문법이 구체적 문법과 요약 문법으로 구성된다고 표현한 것은 다소
부정확하다고 해야 할 것이다. 정확히 말하자면, 언어의 문법은 구체적 문법과 요약
문법, 그리고 그 사이를 연결하는 파싱으로 구성된다.

파싱의 흥미로운 점 중 하나는 파싱을 통해 구체적 문법의 애매함(ambiguity)이
해소된다는 것이다. 많은 언어의 구체적 문법은 애매하다. 애매함이 무엇인지
엄밀하게 설명하기 보다는 직관적인 예시만 보여 주겠다.[^1] “1+2+3”이라는 문자열을
생각해 보자. 우리는 이 문자열이 `산술`의 프로그램임을 알고 있다. 그러나 이
문자열이 파싱 함수를 통해 어떤 나무로 바뀌어야 할지는 명확하지 않다.

[^1]: 여기서 말하는 애매함은 문맥 자유 문법 등에서 말하는 애매함과 완전히 같다고
보기는 어렵다. 그러나 직관적으로는 큰 차이가 없으며 자세한 내용을 설명하는
것은 이 책이 다루는 영역을 넘어가는 일이다. 독자의 이해와 내용 전개의 편의를
위해 애매함이라는 용어를 그대로 사용하도록 하겠다.

* \(파싱(“1-2-3”) = (1 - 2) - 3\)
* \(파싱(“1-2-3”) = 1 - (2 - 3)\)

위의 두 가지 모두 직관적으로 생각했을 때 가능해 보인다. 이처럼 파싱 함수가 한
문자열에 대해 내놓는 나무에 여러 가능성이 존재할 때 언어의 구체적 문법이
애매하다고 이야기한다.

파싱 함수는 여러 가능성 중 하나를 선택함으로써 애매함을 해소한다. 어느 나무를
선택할지는 전적으로 파싱을 정의하는 사람에게 달려 있다. 그러나 일관적인 선택을
해야 그 언어를 사용하는 프로그래머들이 혼란을 겪지 않는다. 예를 들어, 다음과
같이 파싱 함수를 정의했다면 파싱이 일관적으로 작동한다고 보기 어렵다.

* \(파싱(“1-2-3”) = (1 - 2) - 3\)
* \(파싱(“1-2-4”) = 1 - (2 - 4)\)

단순히 문자열 마지막의 3이 4로만 바뀌었을 뿐인데 결과로 나온 나무의 형태가 전혀
달라졌다. 이렇게 파싱 함수를 정의하면 프로그래머는 파싱 함수의 결과를 예측하기가
어렵다. 바람직한 파싱의 정의 방법은 아래의 1번과 2번 중 하나를 선택하는 것이다.

1.

* \(파싱(“1-2-3”) = (1 - 2) - 3\)
* \(파싱(“1-2-4”) = (1 - 2) - 4\)

2.

* \(파싱(“1-2-3”) = 1 - (2 - 3)\)
* \(파싱(“1-2-4”) = 1 - (2 - 4)\)

대부분의 언어에서는 수학에서의 뺄셈의 표기법을 고려하여 1번을 선택하겠지만,
2번도 일관성이 있다.

파싱이 애매함을 해소하기 위해 사용하는 기준으로는 주로 결합 방향과
우선순위(precedence)가 있다. 먼저 결합 방향부터 보겠다. 결합 방향은 같은
연산자(또는 동일한 우선순위의 연산자)가 여럿 연달아 나올 때 애매함을 해소한다.
위에서 본 예시가 결합 방향을 정함으로써 애매함을 해소한 경우이다. 연산자가 왼쪽
결합(left-associative)을 한다면 문자열의 왼쪽 부분을 먼저 사용해 나무를 만들고
그 나무를 전체 나무의 자식 나무로 사용한다. 위에서 1번이 왼쪽 결합에 해당한다. -
연산자가 왼쪽 결합을 한다고 정했을 때의 파싱 함수가 1번인 것이다. “1-2” 부분이
먼저 파싱되어 \(1 - 2\)라는 나무를 만들고 \(1 - 2\)를 자식으로 하여 \((1 - 2) - 3\)이라는
나무가 만들어진 것을 볼 수 있다. 반대로, 연산자가 오른쪽
결합(right-associative)을 한다면 문자열의 오른쪽 부분을 먼저 사용한다. 즉, -
연산자가 오른쪽 결합을 할 때의 파싱 함수가 2번에 해당한다. 2번에서는 “2-3”
부분이 먼저 파싱되어 \(2 - 3\)이라는 나무가 만들어졌다.

많은 언어에서 대부분의 연산자는 왼쪽 결합을 한다. 흔히 볼 수 있는 +, -, \(\ast\), /, %,
&&, || 등은 보통 왼쪽 결합을 하는 연산자이다. 오른쪽 결합을 하는 대표적인
연산자로는 C 등에서 대입 연산자로 사용되는 =와 파이썬 등에서 거듭제곱 연산자로
사용되는 \(\ast\)\(\ast\)가 있다. 예를 들어 C에서 “x = y = 1;”은 \(x = (y = 1)\)로 파싱되며, x와
y의 값을 모두 1로 만드는 프로그램이다. 또, 파이썬에서 “2 \(\ast\)\(\ast\) 3 \(\ast\)\(\ast\) 2”는
\(2 \ast\!\ast (3 \ast\!\ast 2)\)로 파싱되며, 그 결과는 64가 아닌 512이다.

두 번째 기준인 우선순위는 서로 다른 연산자가 연달아 나올 때 애매함을 해소한다.
예시를 위해 구체적 문법과 요약 문법에 \(\ast\) 연산자를 추가하겠다. “1\(\ast\)2+3”을 파싱하면
두 나무가 결과로 나올 수 있다.

* \(파싱(“1\ast2+3”) = (1 \ast 2) + 3\)
* \(파싱(“1\ast2+3”) = 1 \ast (2 + 3)\)

\(\ast\)의 우선순위가 +보다 높다고 정하는 경우에는 첫 번째 파싱 함수가 선택된다. \(\ast\)의
우선순위가 더 높으므로 “1\(\ast\)2” 부분을 먼저 파싱해 \(1 \ast 2\)라는 나무를 만드는 것이다.
반대로, +의 우선순위가 \(\ast\)보다 높다고 정하는 경우에는 두 번째 파싱 함수가
선택된다. +의 우선순위가 높기에 “2+3”을 먼저 파싱해 2 + 3을 만든다.

애매함이 연산자에 의해서만 나타나는 것은 아니다. 다양한 경우에 애매함이 발생하며
파싱 함수는 여러 나무 중 하나 선택함으로써 애매함을 해소한다. 여기에서는
애매함의 가장 대표적인 경우인 연산자의 애매함을 해소하는 두 가지 기준, 결합
방향과 우선순위에 대해서만 알아보았다.

애매함이 없는 구체적 문법을 사용하는 언어도 있다. 대표적인 예는 리스프(Lisp)와
그 변종들이다. 리스프의 구체적 문법은 괄호와 전위(prefix) 연산자를 사용하여
애매함이 발생하지 않도록 한다. “(- (- 1 2) 3)”과 “(+ (\(\ast\) 1 2) 3)” 등이 LISP
프로그램의 예시이다. 첫 번째는 무조건 \((1 - 2) - 3\), 두 번째는 무조건 
\(1 \ast 2 + 3\)으로 파싱되며, 다른 가능성은 존재하지 않는다.

앞서 구체적 문법과 요약 문법을 엄밀히 정의하는 방법을 알아보았던 것과 달리
아직까지 파싱 함수를 엄밀히 정의하는 방법은 보지 않았다. 구체적 문법과 요약
문법의 정의 방법과 달리 파싱의 정의 방법은 언어를 이해하는 데 중요 요소는
아니라고 생각하기 때문에 다루지 않고 넘어갈 것이다. 연산자의 결합 방향과
우선순위에 대한 설명을 이해했다면 대부분의 언어에서 파싱이 어떻게 작동할지
어렵지 않게 알 수 있을 것이다.

이제 두 번째 의문의 해답을 생각해 볼 시간이다. 왜 요약 문법이 필요할까? 구체적
문법을 사용해 바로 의미를 정의할 수는 없을까? 물론 가능하다. 그러나 요약 문법과
파싱을 정의한 다음 요약 문법을 사용해 의미를 정의하는 것이 훨씬 간단하다. 이것이
요약 문법이 필요한 이유이다.

구체적 문법은 요약 문법에 비해 더 많은 자유도를 프로그래머에게 제공한다. \(1 +
2\)라는 나무로 파싱되는 문자열은 “1+2”만 있는 것이 아니다. “01+2”, “001+2”,
“1+02”, “01+002” 등이 모두 \(1 + 2\)로 파싱된다. 앞에서 `산술`의 구체적 문법을 정의할
때는 단순하게 만들기 위해 최소한의 요소만 집어넣었지만 많은 프로그래밍
언어에서는 공백이나 괄호 등을 코드에서 자유롭게 사용하는 것을 허용한다. 예를
들면 “1 + 2”, “1  +  2”, “(1+2)”, “((1+2))”, “(1)+2” 등도 모두 \(1 + 2\)로 파싱될 수
있다.

이런 자유도는 프로그래머에게는 큰 편의를 주지만 동시에 언어의 의미를 정의하는
것을 힘들게 만든다. 요약 문법을 사용하면 \(1 + 2\)의 의미만 정의하면 그만이다.
그러나 요약 문법을 사용하지 않고 구체적 문법 단계에서 바로 의미를 정의하게 되면
“1+2”의 의미뿐 아니라 “01+2”, “001+2”, “1+02”, “01+002”, “1 + 2”, “1  +  2”,
“(1+2)”, “((1+2))”, “(1)+2” 등의 무수히 많은 문자열의 의미를 모두 정의해 주어야
한다. 당연히 \(1 + 2\)라는 하나의 나무의 의미를 정하는 것보다 훨씬 더 많은 일을 해야
한다. 따라서 문자열에 대해 의미를 정의하는 것은 매우 복잡한 일이다. 앞에서 요약
문법을 사용해 의미를 정의할 때는 정의하는 의미가 무엇인지 직관적으로도
수학적으로도 명료하게 잘 드러났다. 그러나 구체적 문법을 사용해 의미를 정의한다면
정의하기도 힘들고 정의를 어찌어찌했다 하더라도 그 정의가 복잡하여 직관적으로도
수학적으로도 다루기 어려울 것이다.

정리하자면, “문자열-실행 결과”의 한 단계 구조는 그 사이를 잇는 의미에 모든 일을
떠넘김으로써 언어의 의미를 매우 복잡하게 만든다. 반대로, “문자열-나무-실행
결과”의 두 단계 구조는 의미가 모든 일을 처리할 필요가 없다. 문자열에서 나무로
가는 첫 단계는 파싱이 맡고 나무에서 실행 결과로 가는 두 번째 단계는 의미가
맡는다. 하나로 뭉쳐 두면 어려운 일이지만, 파싱과 의미의 두 단계로 분리해 놓고
나면 각각은 별로 복잡하지 않다. 그러므로 구체적 문법만을 사용해 의미를 정의하는
것보다 요약 문법을 정의한 뒤 요약 문법을 사용해 의미를 정의하는 것이 훨씬 쉽다.
이것이 요약 문법이 필요한 이유이다.

누군가는 방향을 바꾸어 구체적 문법 없이 요약 문법만 있어도 되지 않냐는 질문을 할
수도 있을 것이다. 결국 프로그램의 의미는 요약 문법을 따르는 나무 구조에 대해
정의되니 말이다. 이 역시 일리 있는 지적이다. 실제로 스크래치와 같은 프로그래밍
언어는 구체적 문법 없이 요약 문법만 존재하는 언어로 볼 수 있다. 스크래치
프로그래머는 블록을 결합해 프로그램을 만들며 그 블록은 나무 구조를 이룬다.
문자열로 프로그램을 작성하고 그 문자열을 파싱하여 나무 구조를 만드는 과정 없이,
프로그래머가 처음부터 나무 구조로 프로그램을 만드는 것이다. 그러나 아직까지
스크래치류의 언어는 교육 이외의 목적으로 널리 사용되지는 않는다. 아무래도
산업에서 프로그래밍을 할 때는 편집, 버전 관리, 코드 공유, 자동화 등의 여러
측면에서 문자열이 나무 구조에 비해 가지는 이점이 많아 보인다. 또, 앞에서 말한
구체적 문법이 제공하는 코드 작성의 자유도도 큰 장점 중 하나이다. 그렇기에
앞으로도 구체적 문법, 요약 문법, 파싱으로 문법을 구성하는 방식은 계속해서 사용될
것이라 생각한다.
