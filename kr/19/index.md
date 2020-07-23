이 글은 *타입 체계*(type system)에 대해 다룬다. 먼저 타입 체계의 목표와 필요성에 대해 본다. 그 후, FAE를 변형하여 TFAE를 정의하고 TFAE의 타입 체계를 정의한다. 또, TFAE의 *타입 검사기*(type checker)를 직접 구현할 것이다. FAE와 TFAE의 관계도 살펴본다. 마지막으로는 TFAE의 가능한 여러 확장을 살펴본다.

## 타입 오류

FAE 식을 계산하면 세 가지 일이 일어날 수 있다. 각 경우를 알아보기 위해서 작은 걸음 의미를 사용할 것이다. 이 작은 걸음 의미는 이전 글에서 본 계산과 값 스택을 사용하는 의미와는 다르다. 식의 환원을 통해 정의된 의미이다. 그러나 식의 환원을 정의하는 방법은 이 글의 주제와는 거리가 있으므로 여기서는 자세히 다루지 않겠다. 환원의 정의가 없어도 아래 내용을 이해하는 데는 전혀 문제없을 것이다.

첫 번째는 환원을 통해 값이 나오는 경우이다. 다음은 식을 계산하여 값이 나오는 예시이다.

\[
\begin{array}{rl}
& (1+2)-3 \\
\rightarrow & 3-3 \\
\rightarrow & 0 \\
\\
& (\lambda x.\lambda y.x+y)\ 1\ 2 \\
\rightarrow & (\lambda y.1+y)\ 2 \\
\rightarrow & 1+2 \\
\rightarrow & 3
\end{array}
\]

이는 프로그램을 실행하였을 때 오류 없이 종료되는 경우로 생각할 수 있다. 일반적으로 프로그램을 실행했을 때 가장 많이 일어나는 일이다.

두 번째는 환원이 끝나지 않는 경우이다.

\[
\begin{array}{rl}
& (\lambda x.x\ x)\ (\lambda x.x\ x) \\
\rightarrow & (\lambda x.x\ x)\ (\lambda x.x\ x) \\
\rightarrow & (\lambda x.x\ x)\ (\lambda x.x\ x) \\
\rightarrow & \cdots
\end{array}
\]

\((\lambda x.x\ x)\ (\lambda x.x\ x)\)은 함수 적용이므로 값이 아니다. 또, 이 식을 환원하면 같은 식이 나온다. 따라서 아무리 환원을 반복해도 값이 나오지 않는다. 이는 프로그램을 실행했을 때 종료되지 않고 계속해서 실행되는 경우이다. 종료되지 않는 것은 프로그래머가 의도한 동작일 수도, 아닐 수도 있다. 재귀 함수나 반복문을 잘못 사용하여 의도하지는 않았으나 프로그램이 종료되지 않을 수 있다. 한편, 운영 체제, 서버, *셸*(shell) 같은 프로그램은 일부 입력에 대해서는 종료되지만 다른 입력에 대해서는 종료되지 않고 계속 실행되는 것이 프로그래머의 의도이다.

세 번째는 환원을 통해 값이 아닌 어떤 식에 이르렀으나 더는 환원할 수 없는 경우이다.

\[
\begin{array}{rl}
& (\lambda x.x+1)\ (\lambda x.x) \\
\rightarrow & (\lambda x.x)+1 \\
\\
& (\lambda x.x\ 1)\ 1 \\
\rightarrow & 1\ 1 \\
\\
& (\lambda x.y)\ 1 \\
\rightarrow & y \\
\end{array}
\]

\((\lambda x.x)+1\)은 함수와 정수를 더하는 식이다. 함수와 정수를 더할 수 없으므로 이 식에 대한 환원 규칙은 없다. 따라서 더는 환원할 수 없다. 그러나 값인 것도 아니다. \(1\ 1\)은 정수를 어떤 값에 적용하는 식이다. 함수는 어떤 값에 적용할 수 있지만 정수는 그럴 수 없다. 역시 더는 환원될 수 없는 식이다. \(y\)는 자유 변수이다. 나타내는 값을 알 수 없으므로 더는 환원할 수 없다. 이런 경우들은 프로그램을 실행했을 때 *실행 시간 오류*(run-time error)가 발생하여 프로그램이 비정상 종료되는 경우로 볼 수 있다. 실행 시간 오류는 프로그래머가 의도적으로 발생시킨 예외와는 다르다. 거의 모든 실행 시간 오류는 프로그래머가 의도하지 않았으나 일어난다. 다른 말로는, 프로그래머의 실수로 생긴 버그이다.

실행 시간 오류에는 여러 종류가 있다. 그중 이 글에서 관심 있는 것은 타입 오류이다. 타입 오류를 정의하려면 먼저 타입부터 정의해야 한다. 타입은 값을 분류한 것이다. 예를 들면 \(1\), \(42\), \(0\), \(-1\) 등은 정수이다. 이들의 타입은 정수라고 말할 수 있다. \(\lambda x.x\), \(\lambda x.x+x\), \(\lambda x.x\ 1\) 등은 함수이다. 이들의 타입은 함수라고 말할 수 있다.

타입 오류는 프로그램 실행 중에 나온 값의 타입이 나올 것으로 기대되는 타입과 달라서 발생하는 오류이다. \((\lambda x.x)+1\)을 다시 보자. 첫 피연산자의 타입은 함수이고 둘째 피연산자의 타입은 정수이다. 덧셈은 두 피연산자의 타입이 모두 정수일 때만 가능하다. 정수 타입의 값이 기대되는 자리에 함수 타입의 값이 와서 실행 시간 오류가 발생했으므로 타입 오류이다. \(1\ 1\)도 다시 보자. 첫 피연산자의 타입은 정수이고 둘째 피연산자의 타입도 정수이다. 함수 적용은 첫 피연산자의 타입이 함수일 때만 가능하다. 함수 타입의 값이 기대되는 자리에 정수 타입의 값이 와서 실행 시간 오류가 발생했으므로 타입 오류이다. 이처럼 타입 오류는 기대하지 않은 타입의 값이 출현하여 발생한 실행 시간 오류이다.

타입 오류 이외에도 다양한 실행 시간 오류가 있다. 먼저 앞의 예시에서 \((\lambda x.y)\ 1\)은 자유 변수 \(y\)가 실행 시간 오류의 원인이다. 타입 에러인 첫 두 오류와는 성격이 다르다. 이처럼 식에 자유 식별자가 존재하여 발생한 오류를 보통 *자유 식별자 오류*(free identifier error)라고 부른다. FAE는 간단한 언어이기에 타입 오류와 자유 식별자 오류가 가능한 실행 시간 오류의 전부이다. 그러나 현실의 언어는 다양한 기능을 제공한다. C 언어로 만든 프로그램을 실행하면 잘못된 주소를 참조하였음을 의미하는 *세그멘테이션 오류*(segmentation fault)가 발생할 수 있다. Java 등 여러 언어는 배열을 제공하며 배열의 값을 읽을 때 읽고자 하는 위치가 배열에 속하는지 검사한다. 속하지 않으면 Java의 `ArrayIndexOutOfBoundsException` 같은 오류가 발생한다. 메서드 오버로딩이 존재하는 언어에서는 메서드 호출이 *애매*(ambiguous)하여 부를 메서드를 선택할 수 없는 오류가 발생할 수 있다. 그밖에도 여러 가지 실행 시간 오류가 존재한다.

현실의 언어에는 다양한 실행 시간 오류가 존재하지만 이 글에서 집중하려는 대상은 FAE이다. FAE 식을 계산할 때는 타입 오류와 자유 식별자 오류만 발생할 수 있으므로 나머지 오류는 고려하지 않겠다. 또한, 일반적으로는 자유 식별자 오류를 타입 오류라고 이야기하지 않지만, 대개의 타입 오류를 찾는 과정에서 자유 식별자 오류 역시 밝혀지게 된다. 이 글에서는 전개의 편의를 위해 자유 식별자 오류 역시 타입 오류로 취급하려고 한다. 따라서 FAE의 실행 시간 오류는 모두 타입 오류이다. 여기서부터는 프로그램을 실행할 때 발생할 수 있는 오류는 타입 오류만 있는 것으로 가정하고 이야기하겠다. 이런 가정이 현실의 언어에서는 보통 성립하지 않는다. 그러나 여전히 이 글에서 타입 체계에 대해 논의한 내용은 현실의 언어에 유효하게 적용된다. 타입 오류는 가능한 실행 시간 오류의 많은 부분을 차지하기 때문이다. 또, 타입 체계의 확장은 타입 오류가 전체 실행 시간 오류 중 차지하는 비중을 키울 수 있게 한다. 뒤에 TFAE의 확장을 다룰 때 이 점에 대해서 자세히 본다.

## 타입 체계

타입 오류는 프로그래머가 의도하지 않은 것일 가능성이 높다. 실행 중 타입 오류가 발생하면 프로그램이 비정상적으로 종료되는데, 실사용을 목적으로 하는 프로그램이 비정상적으로 종료되는 것은 바람직하지 않기 때문이다. 회사에서 배포한 프로그램이 사용 중에 예고 없이 종료되는 것을 반길 사용자는 없다. 이는 사용 경험에 악영향을 주며 장기적으로는 회사나 개발자의 평판과 수익을 악화시킨다. 일상에서 사용되는 평범한 프로그램 정도라면 금전적 손실로 끝나겠지만 현대 사회에는 다양한 곳에 프로그램이 사용된다. 잘못 작동했을 때 여러 사람의 생명에 직접적 피해를 줄 수 있는 자동차, 항공기, 의료 기기 등도 대부분 프로그램으로 제어한다. 제어 프로그램이 실행 중 타입 오류를 일으켜 갑자기 종료된다면 기기가 어떻게 작동할지 예상할 수 없다. 큰 인명 피해로 이어진다고 말해도 과언이 아니다. 따라서 프로그램을 배포 및 사용하기 전에 프로그래머의 의도를 벗어난 타입 오류를 찾아낼 방법이 필요하다.

타입 오류를 찾는 가장 간단한 방법은 프로그램을 미리 실행해 보는 것이다. 다른 말로는 프로그램 테스팅이다. 만약 프로그램을 실행해서 타입 오류가 발생하면 이는 타입 오류가 있는 것이니 프로그램을 수정해야 한다. 반대로 타입 오류 없이 정상적으로 작동하면 수정할 필요가 없다. 그러나 이 방법은 충분하지 않다. 프로그램의 실행 시간이 매우 길거나 프로그램이 끝없이 실행될 수 있다. 프로그램을 배포하고 사용해야 하는데 무한정 실행해 보고 있을 수는 없는 노릇이다. 적절한 시간에서 테스팅을 멈춰야 하는데 결과적으로는 타입 오류의 가능성을 완전히 배제할 수 없다. 100 시간 동안 프로그램을 실행하였는데 타입 오류가 발생하지 않아 배포하였더니 101 시간째에 오류가 발생할지도 모르는 것이다. 또 다른 문제점은, 현실에서 사용하는 대부분의 프로그램은 가능한 입력이 무한히 많다는 것이다. 무한히 많은 모든 입력에 대해 프로그램을 실행해 볼 수는 없다. 결국 가능한 입력 중 일부에 대해서만 시험해 볼 수 있다. 시험해 본 입력에 대해서는 타입 오류가 일어나지 않았다 하더라도 시험해 보지 않은 입력 중에 타입 오류를 일으키는 경우가 있을 수 있다. 이처럼 프로그램 테스팅을 통해 타입 오류를 찾는 전략은 간단하고 실제로도 많이 사용되지만, 프로그램을 실행했을 때 타입 오류가 절대로 일어나지 않을 것을 보장할 수는 없다. 타입 오류가 치명적인 결과로 이어질 수 있는 프로그램일수록 테스팅보다 좋은 방법이 필요하다.

프로그램을 실행해서 타입 오류를 모두 막을 수 없다면 남은 길은 프로그램을 실행하지 않고 타입 오류를 막는 방법뿐이다. 프로그램을 실행하기 전, 미리 검사하여 타입 오류가 발생할 수 있는지 확인하는 것이다. 타입 오류가 일어날 수 있다면 문제되는 부분을 고치면 된다. 아니라면 타입 오류가 절대 일어나지 않을 것이라는 확신을 가지고 프로그램을 사용할 수 있다.

어떤 언어가 있을 때, 그 언어로 만든 프로그램이 타입 오류를 일으킬 수 있는지 확인하기 위한 체계를 타입 체계라고 한다. 타입 체계는 다른 말로는 언어의 *정적 의미*(static semantics)라고 부른다. ‘정적’이라는 단어는 프로그램을 실행하지 않는 것을 의미한다. 프로그램을 실행하지 않고 타입 오류를 잡아내기 위해 만들어진 의미이므로 정적 의미인 것이다. 정적의 반대는 ‘동적’으로 프로그램을 실행하는 것이다. 기존의 우리가 정의한 의미는 프로그램이 어떻게 실행되는지 정의하므로 *동적 의미*(dynamic semantics)라고도 한다. 언어에 대해 이야기할 때는 타입 체계와 정적 의미를 사실상 같은 뜻으로 쓸 수 있다. 그러나 어떤 식에 대해 이야기할 때는 타입 체계라는 말이 어색하다. 이 때는 어떤 식의 정적 의미가 더 어울리는 표현이다. 이 글에서는 최대한 일관적으로 타입 체계라는 단어만 쓰되 어색하면 정적 의미라는 단어를 쓰겠다.

타입 체계를 프로그램으로 구현한 것이 타입 검사기이다. 프로그램이 입력으로 주어졌을 때, 언어의 동적 의미에 따라 입력받은 프로그램을 실행하는 프로그램을 인터프리터라 했다. 동적 의미에게 인터프리터가 타입 체계에게는 타입 검사기인 것이다. 프로그램이 입력으로 주어졌을 때, 언어의 타입 체계에 따라 입력받은 프로그램을 실행하지 않고 타입 오류를 찾아내는 프로그램이 타입 검사기이다.

타입 검사기의 목표는 타입 오류를 일으킬 수 있는 프로그램을 찾는 것이다. 어떤 프로그램 \(P\)와 그 프로그램에 줄 입력 \(I\)가 타입 검사기에 입력으로 주어진다고 하자. \(P(I)\)는 프로그램 \(P\)를 입력 \(I\)에 대해 실행하는 것이다. 타입 검사기는 \(P(I)\)를 실행했을 때 타입 오류가 일어나지 않는다고 판단하면 \(\textsf{OK}\), 일어난다고 판단하면 \(\textsf{NOT OK}\)을 결과로 낸다. 타입 검사기는 반드시 어떤 입력에 대해서도 다음 세 성질을 만족해야 한다. 첫째, 유한한 시간 안에 종료되어야 한다. 둘째, \(\textsf{OK}\)나 \(\textsf{NOT OK}\), 둘 중 하나를 결과로 내며 종료되어야 한다. 셋째, 오류 없이 종료되어야 한다.

타입 검사기가 추가로 목표할 만한 성질에는 *완전성*(completeness)과 *안전성*(soundness)이 있다. *완전한*(complete) 타입 검사기는 타입 오류를 일으키지 않는 모든 순서쌍 \((P,I)\)에 대해서 \(\textsf{OK}\)를 결과로 낸다. 그러므로 완전한 타입 검사기가 \((P,I)\)에 대해 \(\textsf{NOT OK}\)를 결과로 내면 \(P(I)\)는 무조건 타입 오류를 일으킨다. 논리식으로는 아래와 같이 적을 수 있다. 타입 검사기 프로그램을 \(\mathit{check}\)라고 부르자. \(\mathit{check}(P,I)\)는 \(P(I)\)를 검사하는 것이다.

\[
\begin{array}{l}
\forall P.\forall I.(P(I)\text{가 타입 오류 안 일으킴})\rightarrow(\mathit{check}(P,I)=\textsf{OK}) \\
\forall P.\forall I.(\mathit{check}(P,I)=\textsf{NOT OK})\rightarrow(P(I)\text{가 타입 오류 일으킴})
\end{array}
\]

두 명제는 대우 관계이다.

반대로, *안전한*(sound) 타입 검사기는 타입 오류를 일으키는 모든 순서쌍 \((P,I)\)에 대해서 \(\textsf{NOT OK}\)를 결과로 낸다. 따라서 안전한 타입 검사기가 \((P,I)\)에 대해 \(\textsf{OK}\)를 결과로 내면 \(P(I)\)는 절대로 타입 오류를 일으키지 않는다.

\[
\begin{array}{l}
\forall P.\forall I.(P(I)\text{가 타입 오류 일으킴})\rightarrow(\mathit{check}(P)=\textsf{NOT OK}) \\
\forall P.\forall I.(\mathit{check}(P)=\textsf{OK})\rightarrow(P(I)\text{가 타입 오류 안 일으킴})
\end{array}
\]

가장 이상적인 타입 검사기는 완전하면서도 안전한 타입 검사기이다. 즉, 다음과 같은 프로그램이다.

\[
\mathit{check}(P, I) =
\textsf{if}\ (P(I)\text{가 타입 오류 안 일으킴})\ \textsf{OK}\ 
\textsf{else}\ \textsf{NOT OK}
\]

\((P,I)\)에 대해 이 타입 검사기가 \(\textsf{OK}\)라 하면 \(P(I)\)는 타입 오류를 절대 일으키지 않는다. 또, \(\textsf{NOT OK}\)라 하면 \(P(I)\)는 타입 오류를 무조건 일으킨다. 안타깝게도, \(P\)가 아무 튜링 계산 가능한 프로그램이나 될 수 있을 때, 이런 프로그램 \(\mathit{check}\)는 존재하지 않는다는 것이 이미 증명되어 있다. 증명 방법은 *정지 문제*(halting problem)를 푸는 프로그램이 없음을 증명하는 방법과 완전히 같다.

증명은 귀류법을 사용한다. 위의 프로그램 \(\mathit{check}\)가 존재한다고 가정하자. 그리고 프로그램 \(A\)를 다음과 같이 정의한다.

\[
A(X) =
\textsf{if}\ (\mathit{check}(X, X)=\textsf{OK})\ 1\ 1\ 
\textsf{else}\ 0
\]

\(A\)는 입력으로 \(X\)를 받는다. 만약 \(\mathit{check}(X, X)\)가 \(\textsf{OK}\)이면, 즉 프로그램 \(X\)에 \(X\) 자신을 입력하여 실행했을 때 타입 오류가 없으면, \(1\ 1\)을 계산한다. \(1\ 1\)은 정수를 어떤 값에 적용하는 식이므로 타입 오류를 일으킨다. 반대로, \(\mathit{check}(X, X)\)가 \(\textsf{NOT OK}\)이면, 즉 \(X\)에 \(X\) 자신을 입력하여 실행했을 때 타입 오류가 발생하면 \(0\)을 결과로 낸다.

이제 \(\mathit{check}(A,A)\)를 생각해 보자. \(\mathit{check}\)는 모든 프로그램과 모든 입력에 대해 잘 작동해야 하므로 \(\mathit{check}(A,A)\)의 결과는 \(\textsf{OK}\)이거나 \(\textsf{NOT OK}\)이다. 먼저 결과가 \(\textsf{OK}\)라 가정하자. 이는 \(A(A)\)를 실행하면 타입 오류가 발생하지 않는다는 뜻이다. \(A(A)\)를 계산하려면 \(\mathit{check}(A,A)\)의 결과가 \(\textsf{OK}\)이라 가정하였으므로 \(1\ 1\)을 계산해야 한다. \(1\ 1\)은 타입 오류를 일으킨다. 이는 \(A(A)\)를 실행하면 타입 오류가 발생하지 않는다는 가정에 모순이다. 따라서 결과는 \(\textsf{OK}\)가 아니다. 이제 결과가 \(\textsf{NOT OK}\)라 가정하자. 이는 \(A(A)\)를 실행하면 타입 오류가 발생한다는 뜻이다. \(A(A)\)를 실행하면 \(\mathit{check}(A,A)\)의 결과가 \(\textsf{NOT OK}\)라 가정하였으므로 \(0\)을 계산해야 한다. \(A(A)\)는 타입 오류 없이 \(0\)을 결과로 낸 뒤 종료된다. 이는 \(A(A)\)를 실행하면 타입 오류가 발생한다는 가정에 모순이다. 따라서 결과는 \(\textsf{NOT OK}\)도 아니다. 결과가 \(\textsf{OK}\)도 \(\textsf{NOT OK}\)도 아니므로 모순이다. 이 모순은 프로그램 \(\mathit{check}\)가 존재한다고 가정한 것으로부터 비롯되었다. 그러므로 프로그램 \(\mathit{check}\)는 존재하지 않는다.

완전하면서도 안전한 타입 검사기는 만들 수 없다. 단, 이 결론은 들어오는 프로그램의 집합이 튜링 완전할 때 성립하는 것이다. 앞선 글에서 다룬 AE나 WAE처럼 제한적인 기능만 제공하는 언어라면 완전하고 안전한 타입 검사기가 존재할 수 있다. 그러나 현실의 대부분 언어는 튜링 완전하다. 현실에서는 완전하면서도 안전한 타입 검사기가 불가능한 것이다. 따라서 타입 검사기를 만드는 사람들은 다음의 세 방법 중 하나를 선택해야 한다. 첫 번째는 안전성은 포기하고 완전성만 가지는 검사기를 만드는 것이다. 두 번째는 완전성은 포기하고 안전성만 가지는 검사기를 만드는 것이다. 세 번째는 완전성도 안전성도 포기하는 것이다.

가장 일반적인 선택은 두 번째로, 완전성은 포기하되 안전한 타입 검사기를 만드는 것이다. 이는 타입 검사기를 만드는 동기를 생각해 보았을 때 자연스러운 것이다. 타입 검사기를 통해 어떤 프로그램이 타입 오류를 절대 일으키지 않음을 보장하는 것이 목표이다. 즉, 타입 검사기가 \(\textsf{OK}\)를 결과로 냈다면 타입 오류가 없어야 한다는 것이다. 이는 안전성에 대한 설명이며 타입 검사기에 가장 필요한 성질이 안전성임은 타당하다.

다만 안전성을 선택한 대가로 완전성은 무조건 포기해야 한다는 문제가 있다. 그 말은 타입 검사기가 실제로는 문제가 없는 프로그램인데도 \(\textsf{NOT OK}\)를 결과로 낼 가능성이 있다는 것이다. 이런 경우를 *거짓 양성*(false positive)이나 *거짓 경보*(false alarm)라 부른다. 만약 타입 검사기가 안전성을 만족하지만 너무 많은 거짓 경보를 발생시킨다면 현실적으로 사용하기 어렵다. 프로그래머가 타입 검사기의 수많은 거짓 경보를 해결하는 데 불필요한 시간을 소비해야 한다. 예를 들어 어떤 타입 검사기가 모든 경우에 \(\textsf{NOT OK}\)를 결과로 낸다고 가정하자. 이 타입 검사기는 분명히 안전하다. \(\textsf{OK}\)가 결과가 되는 경우가 없으므로 \(\textsf{OK}\)가 결과인데 타입 오류가 발생할 일도 없다. 그러나 그런 타입 검사기는 아무도 원하지 않을 것이다.

다행히도, 충분히 많은 유용한 프로그램에 대해 \(\textsf{OK}\)를 결과로 내는 안전한 타입 검사기를 만들 수 있다. 이는 지금까지의 수많은 타입 체계 연구가 보여주고 있다. 그렇기에 거의 모든 타입 검사기는 안전성을 목표로 한다. 물론, 그와 동시에 최대한 많은 올바른 프로그램에 \(\textsf{OK}\)를 결과로 내려고 노력한다.

이제 앞에서 본 타입 검사기의 정의인 \(\mathit{check}\)를 약간 수정하겠다. 프로그램에 어떤 입력이 들어올지 미리 알 수 없다. 따라서 입력을 고려하여 타입 오류 발생 여부를 판단할 필요가 없다. 일반적으로 프로그래머들이 원하는 것은 어떤 입력에도 타입 오류가 없음을 보장하는 것이다. 이는 다음과 같이 쓸 수 있다. \(\mathit{check}(P)\)는 타입 검사기가 프로그램 \(P\)를 검사하는 것이다.

\[
\begin{array}{l}
\forall P.(\exists I.P(I)\text{가 타입 오류 일으킴})\rightarrow(\mathit{check}(P)=\textsf{NOT OK}) \\
\forall P.(\mathit{check}(P)=\textsf{OK})\rightarrow(\forall I.P(I)\text{가 타입 오류 안 일으킴})
\end{array}
\]

타입 검사기가 어떤 프로그램을 입력받아 \(\textsf{OK}\)를 결과로 내면 그 프로그램은 *올바른 타입의*(well-typed) 프로그램이다. 이 경우 타입 검사기가 프로그램을 받아들인다고 표현한다. 다른 말로는 프로그램의 타입 검사가 성공했다고도 한다. 반대로 타입 검사기가 \(\textsf{NOT OK}\)를 결과로 내면 그 프로그램은 *잘못된 타입의*(ill-typed) 프로그램이다. 이는 타입 검사기가 프로그램을 거절한 것이다. 프로그램의 타입 검사가 실패했다고 할 수도 있다.

타입 검사기를 제공하는 많은 언어에서는 타입 검사가 컴파일러나 인터프리터의 앞부분이다. 컴파일을 하는 언어의 경우, 타입 검사기가 컴파일러에 포함되어 있다. 컴파일러는 타입 검사를 수행하여 타입 오류가 없는 프로그램만 컴파일한다. 타입 오류가 있을 것으로 판단되면 더 이상 진행하지 않는다. 인터프리터를 사용하는 경우, 프로그램을 실제로 실행하는 부분에 앞서 타입 검사기가 실행된다. 프로그램을 실행하는 부분은 그 동안의 글에서 만든 인터프리터로 치면 `interp` 함수를 호출하는 것이다. 타입 오류가 없으면 그대로 프로그램을 실행하고, 아니면 실행하지 않고 인터프리터가 종료된다. 이렇게 실행 전에 타입 검사를 수행하는 것을 *정적 타입 검사*(static type checking)이라고 한다. 정적 타입 검사를 하는 언어는 *정적 타입 언어*(statically typed language)라 부른다. 정적 타입 언어의 예로는 C, C++, Java, Scala 등이 있다. 정적 타입 체계는 지금까지 우리가 이야기한 타입 체계이다. 일반적으로 타입 체계라고 하면 정적 타입 체계만 이야기할 때가 많다.

한편 타입 검사를 실행 중에 하는 언어도 있다. 실행 중의 타입 검사는 *동적 타입 검사*(dynamic type checking)이며 이를 수행하는 언어를 *동적 타입 언어*(dynamically typed language)라 부른다. 실행 중에 이루어지는 검사는 타입 검사의 목적과 상충하는 것처럼 보인다. 그러나 동적 타입 검사에도 의미가 있다. 앞에서는 타입 오류가 프로그램의 비정상 종료를 유발한다고 했지만 실제 프로그램 실행에서는 그렇지 않을 수 있다. 타입 오류가 비정상 종료 대신 프로그램의 이상한 동작을 유발할 수 있기 때문이다. 예를 들면, C 언어는 정적 타입 검사를 함에도 포인터에 대해서는 안전한 검사를 하지 않는다. 따라서 실행 중에 `int` 타입 값이 들어있어야 할 곳에 `float` 타입 값이 들어있는 등의 일이 일어날 수 있다. 그 경우 프로그램이 바로 종료되지는 않지만 프로그래머가 전혀 예상하지 않은 동작을 수행할 수 있다. 동적 타입 검사는 이런 문제를 막는다. 실행 중에 타입 검사를 적절히 수행하여 타입 오류가 일어나면 프로그램이 이상한 동작을 하기 전 프로그램을 종료한다. 동적 타입 언어의 대표적 예는 Python이다. Python 인터프리터는 정수와 문자열을 더하는 것 같은 일을 수행하게 되면 타입 오류를 알린 뒤 종료된다. 이런 동적 타입 체계는 정적 타입 체계에 비해 훨씬 단순하다. 동적 타입 검사기 역시 정적 타입 검사기보다 훨씬 간단하다. 이러한 이유로 타입 체계나 타입 검사기라고 하면 정적 타입 체계와 정적 타입 검사기만을 뜻할 때가 많다. 다만 동적 타입 검사나 동적 타입 언어라는 말은 흔하게 사용된다.

정적 타입 언어와 동적 타입 언어는 각자의 장단점을 가지고 있다. 정적 타입 언어의 장점부터 보자. 정적 타입 언어를 사용하면 실행 전에 타입 오류를 찾을 수 있다. 프로그램이 복잡하거나 긴 시간 동안 유지하고 보수해야 한다면 정적 타입 검사는 매력적이다. 프로그램의 신뢰도가 중요한 경우에도 정적 타입 언어를 사용하는 것이 바람직하다. 또, 정적 검사를 통해 얻은 타입 정보를 통해 다양한 최적화를 수행할 수 있다. 정적 타입 검사를 통해 타입 오류가 없음을 보장하면 동적 타입 검사의 필요성이 감소하기도 한다. 이러한 이유로 정적 타입 언어는 실행 시 성능에서도 우월하다. 정적 타입 언어는 타입 검사를 위해 프로그래머에게 코드에 타입을 표시할 것을 요구하기도 한다. *타입 표시*(type annotation)는 컴퓨터에 의해 항상 자동으로 검증되는, 정확한 정보를 제공하는 주석의 역할을 한다. 이는 프로그램의 유지와 보수에 도움이 된다.

정적 타입 언어의 단점은 타입 검사기의 안전성을 대가로 잃어버린 완전성이다. 타입 오류를 일으키지 않는 프로그램도 타입 검사기가 거절할 수 있다. 이를 극복하기 위해 대부분의 정적 타입 언어는 안전하지 않은 기능을 제공한다. *명시적 타입 변환*(explicit type casting)이 대표적인 예시다. 프로그래머는 안전하지 않은 기능을 통해 타입 검사기의 불완전함을 피해갈 수 있다. 그러나 안전하지 않은 기능을 사용하면 타입 검사기의 안전성이 사라진다. 이는 정적 타입 언어를 사용하는 목적 자체를 흐리는 것이므로 안전하지 않은 기능은 되도록 사용하지 않아야 한다. 타입을 표시해야 한다는 점 역시 단점이 된다. 타입 표시는 주석의 역할을 하여 프로그램 관리에 도움을 주지만, 동시에 코드를 불필요하게 장황하게 만든다. 많은 정적 타입 언어는 *타입 추론*(type inference)을 제공하여 타입 표시를 생략할 수 있게 한다. 프로그래머는 적절히 타입 표시를 생략함으로써 장황함을 해소할 수 있다.

동적 타입 언어는 정적 타입 검사가 제공하는 장점을 누릴 수 없다. 타입 오류의 사전 확인이 불가능하고 성능 면에서 정적 타입 언어에 밀린다. 그 대신 타입 검사기의 불완전함으로 인한 불편이 없다. 정적 타입 언어를 사용하면 올바르지만 타입 검사기가 거절하는 코드를 타입 검사기가 받아들이도록 수정하는 데 시간을 써야 한다. 코드에 올바른 타입 표시를 넣는 데도 시간이 필요하다. 동적 타입 언어를 사용하면 둘 모두 할 필요 없다. 따라서 동적 타입 언어는 개발 초기 단계에 빠르게 프로토타입을 만들고 다양한 변화를 주는 데 적합하다. 타입 검사기와 씨름하느라 시간 낭비할 일이 없다. 그러나 초기 단계를 넘어가면 정적 타입 언어에 비해 불리할 가능성이 높다. 프로그램 설계가 충분히 이루어진 후에는 프로그램을 유지 및 보수하고 성능을 개선하는 것이 중요하기 때문이다.

최근에는 *점진적 타입*(gradual type)이 활발히 연구되고 있다. 점진적 타입의 목표는 한 언어에서 정적 타입 언어와 동적 타입 언어의 특징을 모두 제공하는 것이다. 개발 초기에는 동적 타입 언어처럼 사용할 수 있다. 정적 타입 검사를 통한 안전성은 얻기 힘들지만 빠른 개발과 수정이 가능하다. 그 뒤에는 언어를 바꿀 필요 없이 점진적으로 정적 타입 언어를 사용하는 것처럼 코드를 바꿔나갈 수 있다. 타입 표시를 추가한 부분의 코드는 정적 타입 검사를 통해 안전함을 보장받을 수 있다. 한 번에 코드 전체에 타입 표시를 추가할 필요가 없다. 필요에 따라 중요한 부분부터 타입 표시를 추가하며 프로그램을 관리할 수 있다. TypeScript는 점진적 타입이 실용적으로 사용된 한 예시이다. TypeScript는 동적 타입 언어인 JavaScript로 작성한 코드에 타입 표시를 추가하여 안전하게 사용할 수 있게 해준다.

## TFAE

TFAE는 FAE를 변형하여 정의한, 타입 검사가 가능한 언어이다. 이 글에서는 TFAE의 문법, 동적 의미, 타입 체계를 차례로 본 뒤 타입 검사기와 인터프리터를 구현한다. FAE를 바로 사용하지 않고 TFAE를 정의하는 이유는 뒤에서 보게 될 것이다.

### 문법

다음은 TFAE의 요약 문법이다.

\[
\begin{array}{lrcl}
\text{Integer} & n & \in & \mathbb{Z} \\
\text{Variable} & x & \in & \textit{Id} \\
\text{Expression} & e & ::= & n \\
&& | & e + e \\
&& | & e - e \\
&& | & x \\
&& | & \lambda x:\tau.e \\
&& | & e\ e \\
\text{Value} & v & ::= & n \\
&& | & \langle \lambda x:\tau.e,\sigma \rangle \\
\text{Environment} & \sigma & \in & \textit{Id}\hookrightarrow\text{Value}
\end{array}
\]

FAE와 비교했을 때 유일하게 달라진 것은 람다 요약이다. FAE의 람다 요약은 \(\lambda x.e\)로, 매개변수가 \(x\)이고 몸통이 \(e\)인 함수를 나타낸다. 반면 TFAE의 람다 요약은 \(\lambda x:\tau.e\)로, \(\tau\)가 추가되었다. \(\tau\)는 함수가 받을 인자의 타입을 나타낸다. 매개변수에 대한 타입 표시라고 할 수 있다. \(\tau\)는 타입을 나타내는 메타변수이며 아래와 같이 정의된다.

\[
\begin{array}{lrcl}
\text{Type} & \tau & ::= & \textsf{num} \\
&& | & \tau\rightarrow\tau
\end{array}
\]

앞에서 타입은 값을 분류한 것이라고 했다. \(\textsf{num}\)은 정수를 나타내는 타입이다. 모든 정수의 타입은 \(\textsf{num}\)이다. 앞에서 예를 들 때 값을 정수 타입과 함수 타입으로 나누었는데 TFAE의 타입은 그보다 조금 더 정확하다. \(\tau_1\rightarrow\tau_2\)는 함수를 나타내는 타입으로, \(\tau_1\) 타입의 인자를 받으면 \(\tau_2\) 타입의 값을 결과로 내는 함수를 의미한다. 예를 들면, \(\lambda x:\textsf{num}.x\)는 \(\textsf{num}\) 타입의 인자를 받아 인자 그대로를 결과로 낸다. 따라서 타입은 \(\textsf{num}\rightarrow \textsf{num}\)이다. 또, \(\lambda x:\textsf{num}.\lambda y:\textsf{num}.x+y\)를 생각해 보자. 이는 \(\textsf{num}\) 타입의 인자를 받으며 결과는 \(\lambda y:\textsf{num}.x+y\)로, 또 다른 함수이다. \(\lambda y:\textsf{num}.x+y\)는 \(\textsf{num}\) 타입의 인자를 받는다. \(x\)와 \(y\) 모두 정수이므로 \(x+y\)는 정수이고 결괏값의 타입이 \(\textsf{num}\)이 된다. 따라서 \(\lambda y:\textsf{num}.x+y\)의 타입은 \(\textsf{num}\rightarrow\textsf{num}\)이다. \(\lambda x:\textsf{num}.\lambda y:\textsf{num}.x+y\)의 타입은 \(\textsf{num}\rightarrow(\textsf{num}\rightarrow\textsf{num})\)이다. 함수 타입을 나타낼 때 화살표는 오른쪽 결합(right-associative)을 한다. 그러므로 \(\textsf{num}\rightarrow(\textsf{num}\rightarrow\textsf{num})\)은 \(\textsf{num}\rightarrow\textsf{num}\rightarrow\textsf{num}\)이라 써도 같은 타입이다. 사실 TFAE에서 람다 요약 자체는 값이 아니고 클로저가 값이므로 위 설명은 부정확하다. 그러나 타입이 무엇인지 직관적으로 이해하는 데 위 설명이 충분하기 때문에 이 정도로 넘어가겠다.

TFAE의 타입은 \(\textsf{num}\)이거나 어떤 \(\tau_1\)과 \(\tau_2\)에 대해 \(\tau_1\rightarrow\tau_2\)이다. 어떤 값도 두 개 이상의 타입에 속할 수 없다. 정수이면서 함수인 값은 없다. 정수를 인자로 받는 함수이면서 정수에서 정수로 가는 함수도 인자로 받는 함수는 없다. TFAE에 대해 다루는 이 글은 모든 값의 타입이 유일하다는 것을 전제로 한다. 더 나아가 모든 식의 타입은 최대 한 개이다. 식의 타입은 뒤에서 보게 될 것이다. 이 전제는 다른 언어에서는 성립하지 않을 수 있다. 한 값이 여러 타입에 속할 수 있는 타입 체계도 많다. 그런 타입 체계는 나중 글에서 다루게 될 것이다.

### 동적 의미

이 글에서는 수업 내용을 따라 TFAE의 동적 의미를 큰 걸음 의미 형태로 정의한다. 사실 식의 환원을 사용하는 작은 걸음 의미가 타입 체계를 엄밀히 다루기에는 더 편하다. 작은 걸음 의미를 사용하면 타입 안전성을 엄밀히 정의하고 증명하기 쉽다. 그러나 그 정도의 엄밀함은 이 글의 목적이 아니므로 큰 걸음 의미를 사용할 것이다.

TFAE의 동적 의미는 FAE의 동적 의미와 거의 같다. 유일한 차이점은 람다 요약에 매개변수 타입이 표시되어 있다는 것이다.

\[
\sigma\vdash n\Rightarrow n
\]

\[
\frac
{ \sigma\vdash e_1\Rightarrow n_1 \quad \sigma\vdash e_2\Rightarrow n_2 }
{ \sigma\vdash e_1+e_2\Rightarrow n_1+n_2 }
\]

\[
\frac
{ \sigma\vdash e_1\Rightarrow n_1 \quad \sigma\vdash e_2\Rightarrow n_2 }
{ \sigma\vdash e_1-e_2\Rightarrow n_1-n_2 }
\]

\[
\frac
{ x\in\mathit{Domain}(\sigma) }
{ \sigma\vdash x\Rightarrow \sigma(x)}
\]

\[
\sigma\vdash \lambda x:\tau.e\Rightarrow \langle\lambda x:\tau.e,\sigma\rangle
\]

\[
\frac
{ \sigma\vdash e_1\Rightarrow\langle\lambda x:\tau.e,\sigma'\rangle \quad
  \sigma\vdash e_2\Rightarrow v' \quad
  \sigma'\lbrack x\mapsto v'\rbrack\vdash e\Rightarrow v }
{ \sigma\vdash e_1\ e_2\Rightarrow v }
\]

함수 적용 규칙에서 인자 \(v'\)이 클로저에 표시된 인자의 타입인 \(\tau\)를 만족하는지 확인해야 한다고 생각하는 사람도 있을 수 있다. 이는 충분히 가능한 선택이다. 그러나 함수 적용 시에 인자의 타입을 확인하는 것은 동적 타입 검사의 영역이기에 이 글에서 다루고자 하는 내용과 맞지 않다. 또, 이후에 타입 체계를 정의한다면 어차피 타입 오류가 있는 프로그램은 실행할 일이 없다. 그 경우 언제나 실행되는 프로그램에서는 \(v'\)의 타입이 \(\tau\)라는 보장이 있으므로 동적 타입 검사는 불필요하다. 따라서 함수 적용 규칙은 \(v'\)의 타입과 \(\tau\)를 비교하지 않는다.

### 타입 체계

타입 체계는 프로그램을 실행하기 위한 것이 아니다. 식만 보고 그 식이 타입 오류를 일으킬 수 있는지 예상하는 것이 목적이다. 타입 오류를 확인하기 위해서는 각 식을 계산했을 때 나오는 값이 어떤 타입을 가지는지 알 수 있어야 한다. \(e_1+e_2\)를 생각해 보자. \(e_1\)을 계산할 때 타입 오류가 없고 \(e_2\)를 계산할 때도 타입 오류가 없다는 것을 알더라도 \(e_1+e_2\)에 대해서는 그렇게 이야기할 수 없다. \(e_1\)이나 \(e_2\)를 계산한 결과가 정수가 아닐 수 있기 때문이다. 따라서 \(e_1+e_2\)가 타입 오류를 일으키지 않음을 보장하려면 \(e_1\)과 \(e_2\)가 타입 오류를 일으키지 않는다는 사실에 더해 두 식을 계산한 결과의 타입이 \(\textsf{num}\)이라는 사실도 필요하다.

타입 체계에 대해 이야기할 때, “어떤 식 \(e\)이 타입 오류를 일으키지 않으며 그 결괏값의 타입이 \(\tau\)이다”라는 것을 간단히 “\(e\)의 타입이 \(\tau\)이다”라고 하는 경우가 많다. 이 글에서도 그렇게 표현할 것이다. 위 내용을 다시 표현해 보면 \(e_1\)과 \(e_2\)의 타입이 모두 \(\textsf{num}\)이면 \(e_1+e_2\)는 타입 오류를 일으키지 않는다고 할 수 있다. 또, \(e_1\)과 \(e_2\)의 계산 결과가 모두 정수이므로 두 값을 더해도 정수이다. 그러므로 \(e_1\)과 \(e_2\)의 타입이 모두 \(\textsf{num}\)이면 \(e_1+e_2\)의 타입은 \(\textsf{num}\)이라고 말할 수 있다.

사실 빠트린 부분이 있다. 지금까지 설명에서는 식의 계산이 끝나지 않는 경우를 고려하지 않았다. 식의 계산이 끝나지 않는 것 역시 타입 오류가 없는 것이다. 이를 바탕으로 수정하면 “\(e\)의 타입이 \(\tau\)이다”라는 것은 “어떤 식 \(e\)이 타입 오류를 일으키지 않으며, 그 결괏값의 타입이 \(\tau\)이거나 계산이 끝나지 않는다”라는 뜻이다. \(e_1\)과 \(e_2\)의 타입이 모두 \(\textsf{num}\)이면 \(e_1+e_2\)의 타입은 \(\textsf{num}\)이라는 사실은 여전히 성립한다. \(e_1\)이나 \(e_2\)의 계산이 끝나지 않으면 \(e_1+e_2\)의 계산 역시 끝나지 않기 때문이다.

위 설명은 타입 체계가 필요로 하는 것을 모두 보여주었다. 첫째, 타입 체계는 식과 타입 사이의 관계를 정의해야 한다. 이는 어떤 식이 주어졌을 때 그 식의 타입을 정의하는 것이다. 둘째, 이 관계를 정의하는 것에는 전제와 결론이 있다. 예를 들면 \(e_1+e_2\)의 타입은 \(\textsf{num}\)이라는 결론을 얻는 전제는 \(e_1\)과 \(e_2\)의 타입이 모두 \(\textsf{num}\)이라는 것이다. 따라서 타입 체계는 식과 타입 사이의 관계를 추론 규칙 형태로 표현해야 한다. 추론 규칙의 전제에는 결론에 등장하는 식의 부분식에 대한 내용이 온다. 이 구조는 큰 걸음 방식의 동적 의미와 매우 유사하다. 동적 의미는 식과 값 사이의 관계이며 추론 규칙을 사용한다. 타입 체계는 값만 타입으로 바꾼 것이다. 타입 체계는 식과 타입 사이의 관계이며 추론 규칙을 사용한다.

타입 체계를 식과 타입 사이의 관계로 보는 것에는 아직 하나 부족한 것이 있다. 동적 의미도 사실은 식과 값 사이의 관계가 아니라 환경, 식, 값 사이의 관계이다. 환경은 변수의 값을 저장하고 있다. 프로그램을 실행하든 안 하든 변수는 존재한다. 타입 체계에도 변수에 대한 정보를 줄 존재가 있어야 한다. 동적 의미와의 차이점은 동적 의미는 변수의 값을 필요로 하지만 타입 체계는 변수의 타입을 필요로 한다는 것이다. 동적 의미는 식을 받아 값을 내놓고 타입 체계는 식을 받아 타입을 내놓아야 하므로 당연한 것이다. 타입 체계에서 환경의 역할을 하는 것은 *타입 환경*(type environment)이다. 타입 환경은 변수의 타입을 저장하고 있다. 즉, 식별자에서 타입으로 가는 부분 함수이다.

\[
\begin{array}{lrcl}
\text{Type Environment} & \Gamma & \in & \textit{Id}\hookrightarrow\text{Type}
\end{array}
\]

이제 타입 체계를 정의할 수 있다. 타입 체계는 타입 환경, 식, 타입의 관계이다.

\[:\subseteq\text{Type Environment}\times\text{Expression}\times\text{Type}\]

\(\Gamma\vdash e:\tau\)는 타입 환경 \(\Gamma\) 아래에서 식 \(e\)의 타입이 \(\tau\)임을 나타낸다. 만약 어떤 \(\tau\)에 대해 \(\emptyset\vdash e:\tau\)가 참이면 \(e\)는 올바른 타입의 식이며 타입 체계가 식을 받아들인다. 만약 어떤 \(\tau\)에 대해서도 이 명제가 거짓이면 \(e\)는 잘못된 타입의 식이다. 이는 타입 체계가 식을 거절한 것이다.

각 식마다 추론 규칙을 보겠다.

\[
\Gamma\vdash n:\textsf{num}
\]

정수의 타입은 \(\textsf{num}\)이다.

\[
\frac
{ \Gamma\vdash e_1:\textsf{num} \quad \Gamma\vdash e_2:\textsf{num} }
{ \Gamma\vdash e_1+e_2:\textsf{num} }
\]

\(e_1\)과 \(e_2\)의 타입이 모두 \(\textsf{num}\)이면 \(e_1+e_2\)의 타입도 \(\textsf{num}\)이다. 앞에서 설명한 것과 같다.

\[
\frac
{ \Gamma\vdash e_1:\textsf{num} \quad \Gamma\vdash e_2:\textsf{num} }
{ \Gamma\vdash e_1-e_2:\textsf{num} }
\]

차는 합과 완전히 같다.

\[
\frac
{ x\in\mathit{Domain}(\Gamma) }
{ \Gamma\vdash x:\Gamma(x)}
\]

변수의 경우 동적 의미와 비슷하다. 동적 의미에서는 환경에서 변수의 값을 찾았다면 타입 체계에서는 타입 환경에서 변수의 타입을 찾는다. 이 규칙에 의해 자유 식별자 오류도 타입 체계가 찾아낼 수 있다.

\[
\frac
{ \Gamma\lbrack x:\tau_1\rbrack\vdash e:\tau_2 }
{ \Gamma\vdash \lambda x:\tau_1.e:\tau_1\rightarrow\tau_2 }
\]

람다 요약의 타입은 생각이 필요하다. 클로저를 만들면 끝인 동적 의미와는 다르다. 람다 요약이 만드는 클로저의 타입을 알아내야 한다. 우선 인자의 타입은 \(\tau_1\)이라고 주어진 것을 사용할 수 있다. 이 정보를 얻기 위해서 FAE를 변형하여 TFAE를 정의한 것이다. 이제 함수를 호출했을 때 결괏값의 타입이 필요하다. 이는 \(e\)의 타입을 찾는 것이다. 함수가 받는 인자의 값은 알 수 없지만 그 타입은 \(\tau_1\)임을 알 수 있다. 또, 클로저는 만들어질 때의 환경을 가지고 있으므로 \(e\)를 계산할 때 \(\Gamma\)에 들어 있는 변수가 사용될 수 있다. 따라서 \(e\)의 타입을 찾을 때 \(x\)의 타입이 \(\tau_1\)이라는 사실뿐 아니라 \(\Gamma\)가 가지고 있는 모든 정보가 필요하다. \(e\)의 타입은 \(\Gamma\lbrack x:\tau_1\rbrack\) 아래서 계산한다. 그 결과가 \(\tau_2\)이면 함수의 결괏값의 타입이 \(\tau_2\)인 것이다. 그러므로 이 람다 요약이 만드는 함수의 타입은 \(\tau_1\rightarrow\tau_2\)이다.

\[
\frac
{ \Gamma\vdash e_1:\tau_1\rightarrow\tau_2 \quad
  \Gamma\vdash e_2:\tau_1 }
{ \Gamma\vdash e_1\ e_2:\tau_2 }
\]
마지막은 함수 적용이다. \(e_1\)이 나타내는 값은 함수여야 한다. \(e_1\)의 타입을 \(\tau_1\rightarrow\tau_2\)라 하자. 인자로 들어올 값의 타입은 \(\tau_1\)이어야 한다. 동적 의미와 달리 함수 몸통의 타입을 찾는 과정은 필요 없다. 그 과정은 이미 람다 요약에 대한 규칙이 하고 있다. \(e_1\)의 타입이 \(\tau_1\rightarrow\tau_2\)이므로 \(\tau_1\) 타입의 인자를 넘겨 몸통을 계산하면 그 결과는 \(\tau_2\) 타입이라는 사실이 보장된다. 따라서 \(e_1\ e_2\)의 타입은 \(\tau_2\)이다.

다음은 \((\lambda x:\textsf{num}.\lambda y:\textsf{num}.x+y)\ 1\ 2\)의 타입이 \(\textsf{num}\)임을 증명하는 증명 나무이다.

\[
\frac
{
  \frac
  {{\huge
    \frac
    {
      \frac
      {
        \frac
        {
          \frac
          { x\in\mathit{Domain}(\lbrack x:\textsf{num},y:\textsf{num}\rbrack) }
          { \lbrack x:\textsf{num},y:\textsf{num}\rbrack\vdash x:\textsf{num} } \quad
          \frac
          { y\in\mathit{Domain}(\lbrack x:\textsf{num},y:\textsf{num}\rbrack) }
          { \lbrack x:\textsf{num},y:\textsf{num}\rbrack\vdash y:\textsf{num} }
        }
        { \lbrack x:\textsf{num},y:\textsf{num}\rbrack\vdash x+y:\textsf{num} }
      }
      { \lbrack x:\textsf{num}\rbrack\vdash\lambda y:\textsf{num}.x+y
        :\textsf{num}\rightarrow\textsf{num} }
    }
    { \emptyset\vdash\lambda x:\textsf{num}.\lambda y:\textsf{num}.x+y
      :\textsf{num}\rightarrow\textsf{num}\rightarrow\textsf{num} } \quad
    {\Large \emptyset\vdash1:\textsf{num}}
  }}
  { {\Large \emptyset\vdash(\lambda x:\textsf{num}.\lambda y:\textsf{num}.x+y)\ 1:\textsf{num}\rightarrow\textsf{num} }}
  \quad \emptyset\vdash2:\textsf{num}
}
{ \emptyset\vdash(\lambda x:\textsf{num}.\lambda y:\textsf{num}.x+y)\ 1\ 2:\textsf{num} }
\]

TFAE의 타입 체계는 안전하다. 안전성이 자명한 사실은 아니나 직관적으로 받아들일 수 있다. 안전성의 증명은 이 글의 수준 밖이다. 타입 체계가 안전하므로 타입 오류를 일으키는 식은 타입 체계에 의해 반드시 거절당한다.

\((\lambda x:\textsf{num}\rightarrow\textsf{num}.x\ 1)\ 1\)을 생각해 보자. 이 식을 계산하려면 \(1\ 1\)을 계산해야 하므로 타입 오류가 발생한다. \(\lambda x:\textsf{num}\rightarrow\textsf{num}.x\ 1\)의 타입은 \((\textsf{num}\rightarrow\textsf{num})\rightarrow\textsf{num}\)이다. \(x\)의 타입이 \(\textsf{num}\rightarrow\textsf{num}\)일 때 \(x\ 1\)의 타입이 \(\textsf{num}\)이기 때문이다. 그러나 인자 \(1\)의 타입은 \(\textsf{num}\)이다. 함수가 받을 수 있는 인자의 타입은 \(\textsf{num}\rightarrow\textsf{num}\)으로, \(\textsf{num}\)이 아니다. 따라서 타입 검사가 실패한다. \((\lambda x:\textsf{num}\rightarrow\textsf{num}.x\ 1)\ 1\)는 잘못된 타입의 식이며 실제로도 계산할 때 타입 오류가 일어난다.

어떤 언어의 타입 체계가 안전하다면 그 언어가 *타입 안전*(type sound)하다고 말한다. 다르게 표현하면 그 언어가 *타입 안전성*(type soundness)을 가진다고 말할 수 있다. TFAE의 타입 체계가 안전하므로 TFAE는 타입 안전한 언어이다.

타입 체계는 안전하면서 동시에 완전할 수는 없다. 따라서 TFAE의 타입 체계는 완전하지 않다. 완전하지 않다는 것은 타입 오류를 일으키지 않는 식이 타입 체계에 의해 거절당할 수 있다는 뜻이다. 다음과 같이 표현할 수 있다.

\[
\exists e.(\exists v.\emptyset\vdash e\Rightarrow v)\land(\not\exists\tau.\emptyset\vdash e:\tau)
\]

이런 식은 많이 찾을 수 있다. \((\lambda x:\textsf{num}.x)\ (\lambda x:\textsf{num}.x)\)는 계산하면 타입 오류 없이 \(\lambda x:\textsf{num}.x\)가 결과이다. 그러나 타입 검사는 실패한다. \(\lambda x:\textsf{num}.x\)의 타입이 \(\textsf{num}\rightarrow\textsf{num}\)이므로 인자로 받아야 하는 값의 타입은 \(\textsf{num}\)이다. 인자로 주어진 \(\lambda x:\textsf{num}.x\)의 타입이 \(\textsf{num}\)이 아닌 \(\textsf{num}\rightarrow\textsf{num}\)이므로 타입 체계는 이 식을 거절한다. 따라서 \((\lambda x:\textsf{num}.x)\ (\lambda x:\textsf{num}.x)\)는 타입 오류를 일으키지 않지만 잘못된 타입의 식이다.

### 타입 검사기 구현

다음은 TFAE의 요약 문법을 Scala로 구현한 것이다.

```scala
sealed trait Expr
case class Num(n: Int) extends Expr
case class Add(l: Expr, r: Expr) extends Expr
case class Sub(l: Expr, r: Expr) extends Expr
case class Id(x: String) extends Expr
case class Fun(x: String, t: Type, b: Expr) extends Expr
case class App(f: Expr, a: Expr) extends Expr

sealed trait Type
case object NumT extends Type
case class ArrowT(p: Type, r: Type) extends Type

type TEnv = Map[String, Type]
```

`TFAE` 인스턴스는 TFAE의 식을 표현한다. `Fun` 클래스에 매개변수 타입을 표시하는 필드 `t`가 추가된 것만 빼면 FAE와 같다. `Type` 인스턴스는 TFAE의 타입을 표현한다. `NumT`는 \(\textsf{num}\) 타입에 해당한다. `ArrowT` 인스턴스는 함수의 타입을 표현한다. 타입 환경 `TEnv`는 문자열을 열쇠, TFAE의 타입을 값으로 하는 사전이다.

```scala
def mustSame(t1: Type, t2: Type): Type =
  if (t1 == t2) t1 else throw new Exception
```

`mustSame` 함수는 두 타입을 인자로 받아 두 타입이 같은지 비교한다. 같으면 받은 타입을 결과로 내고, 다르면 예외를 발생시킨다.

다음 `typeCheck` 함수가 타입 검사기이다. TFAE 식과 타입 환경을 인자로 받는다. 타입 검사가 성공하면 식의 타입을 결과로 낸다. 실패하면 예외를 발생시킨다. 다만 `typeCheck` 함수가 직접 예외를 발생시키지는 않는다. `mustSame`을 호출함으로써, 같아야 할 두 타입이 다르면 예외가 발생한다.

```scala
def typeCheck(e: Expr, env: TEnv): Type = e match {
  case Num(n) => NumT
  case Add(l, r) =>
    mustSame(mustSame(NumT,
      typeCheck(l, env)), typeCheck(r, env))
  case Sub(l, r) =>
    mustSame(mustSame(NumT,
      typeCheck(l, env)), typeCheck(r, env))
  case Id(x) => env(x)
  case Fun(x, t, b) =>
    ArrowT(t, typeCheck(b, env + (x -> t)))
  case App(f, a) =>
    val ArrowT(t1, t2) = typeCheck(f, env)
    val t3 = typeCheck(a, env)
    mustSame(t1, t3)
    t2
}
```

타입 체계를 정의하는 추론 규칙과 타입 검사기 구현이 비슷하므로 이해하기 쉽다. `Num` 경우에 타입은 `NumT`이다. `Add`와 `Sub` 경우에는 두 부분식의 타입이 모두 `NumT`인지 확인하고 `NumT`를 결과로 낸다. `Id` 경우에는 타입 환경에서 타입을 찾는다. `Fun` 경우에는 타입 환경에 매개변수 타입을 추가하고 몸통의 타입을 찾는다. 결과는 매개변수 타입에서 몸통의 타입으로 가는 함수 타입이다. `App` 경우에는 함수와 인자의 타입을 각각 찾는다. 함수의 매개변수 타입과 인자의 타입이 같아야 한다. 같다면 함수의 결과 타입이 최종 결과이다.

다음은 \((\lambda x:\textsf{num}.\lambda y:\textsf{num}.x+y)\ 1\ 2\)의 타입을 `typeCheck` 함수로 계산한 것이다. 결과가 올바르게 나온다.

```scala
// (lambda x:num.lambda y:num.x + y) 1 2
typeCheck(
  App(
    App(
      Fun("x", NumT, Fun("y", NumT,
        Add(Id("x"), Id("y")))),
      Num(1)
    ),
    Num(2)
  ),
  Map.empty
)
// num
```

\((\lambda x:\textsf{num}\rightarrow\textsf{num}.x\ 1)\ 1\)과 \((\lambda x:\textsf{num}.x)\ (\lambda x:\textsf{num}.x)\)의 타입 검사가 실패하는 것도 확인할 수 있다.

``` scala
// (lambda x:num->num.x 1) 1
typeCheck(
  App(
    Fun("x", ArrowT(NumT, NumT),
      App(Id("x"), Num(1))),
    Num(1)
  ),
  Map.empty
)
// java.lang.Exception
//   at TFAE$.mustSame
//   at TFAE$.typeCheck
```

```scala
// (lambda x:num.x) (lambda x:num.x)
typeCheck(
  App(
    Fun("x", NumT, Id("x")),
    Fun("x", NumT, Id("x"))
  ),
  Map.empty
)
// java.lang.Exception
//   at TFAE$.mustSame
//   at TFAE$.typeCheck
```

### 인터프리터 구현

TFAE 인터프리터는 FAE 인터프리터와 거의 같다.

```scala
sealed trait Value
case class NumV(n: Int) extends Value
case class CloV(p: String, b: Expr, e: Env) extends Value

type Env = Map[String, Value]

def interp(e: Expr, env: Env): Value = e match {
  case Num(n) => NumV(n)
  case Add(l, r) =>
    val NumV(n) = interp(l, env)
    val NumV(m) = interp(r, env)
    NumV(n + m)
  case Sub(l, r) =>
    val NumV(n) = interp(l, env)
    val NumV(m) = interp(r, env)
    NumV(n - m)
  case Id(x) => env(x)
  case Fun(x, _, b) => CloV(x, b, env)
  case App(f, a) =>
    val CloV(x, b, fEnv) = interp(f, env)
    interp(b, fEnv + (x -> interp(a, env)))
}
```

요약 문법의 클로저는 표시된 매개변수 타입을 그대로 가지고 있었다. 그러나 아무런 역할도 없기 때문에 구현에서는 생략하였다.

```scala
def run(e: Expr): Value = {
  typeCheck(e, Map.empty)
  interp(e, Map.empty)
}
```

`run` 함수는 주어진 식에 `typeCheck`와 `interp` 함수를 차례로 적용한다. 타입 검사가 실패하면 `typeCheck`가 예외를 발생시키므로 `interp`는 호출되지 않는다. 타입 검사가 성공했을 때만 `interp`가 호출된다.

앞의 세 식을 `run`을 사용해 실행해 보겠다.

```scala
// (lambda x:num.lambda y:num.x + y) 1 2
run(
  App(
    App(
      Fun("x", NumT, Fun("y", NumT,
        Add(Id("x"), Id("y")))),
      Num(1)
    ),
    Num(2)
  )
)
// 3
```

``` scala
// (lambda x:num->num.x 1) 1
run(
  App(
    Fun("x", ArrowT(NumT, NumT),
      App(Id("x"), Num(1))),
    Num(1)
  )
)
// java.lang.Exception
//   at TFAE$.mustSame
//   at TFAE$.typeCheck
//   at TFAE$.run
```

```scala
// (lambda x:num.x) (lambda x:num.x)
run(
  App(
    Fun("x", NumT, Id("x")),
    Fun("x", NumT, Id("x"))
  )
)
// java.lang.Exception
//   at TFAE$.mustSame
//   at TFAE$.typeCheck
//   at TFAE$.run
```

첫 식은 올바르게 계산되어 결과가 나오고 다음 두 식은 타입 검사기가 거절한 것을 볼 수 있다. 둘째 식은 계산하면 타입 오류가 발생하겠지만 그 전에 타입 검사기가 거절하여 계산되지 않았다. 셋째 식은 계산하면 문제없이 결과가 나온다. 그러나 타입 검사기가 거절하여 계산되지 않았다.

## FAE와 TFAE

### 타입 지우개

TFAE의 동적 의미를 보면 람다 요약에 표시된 매개변수 타입은 실행 중 아무 역할도 없다. 매개변수 타입은 오직 정적 타입 검사 시에만 필요하다. 따라서 타입 검사 시에는 프로그래머가 작성한 매개변수 타입을 사용하되 실행에 사용되는 코드에서는 매개변수 타입을 지우는 것을 생각할 수 있다. 이처럼 프로그래머의 타입 표시를 실행을 위한 코드에서는 삭제하는 것을 *타입 지우개*(type erasure)라고 한다.

다음은 TFAE 식을 타입 지우개를 통해 FAE 식으로 바꾸는 방법이다. \(\mathit{erase}\)는 TFAE 식에서 FAE 식으로 가는 함수이다.

\[
\begin{array}{rcl}
\mathit{erase}(n) &=& n \\
\mathit{erase}(e_1+e_2) &=& \mathit{erase}(e_1)+\mathit{erase}(e_2) \\
\mathit{erase}(e_1-e_2) &=& \mathit{erase}(e_1)-\mathit{erase}(e_2) \\
\mathit{erase}(x) &=& x \\
\mathit{erase}(\lambda x:\tau.e) &=& \lambda x.\mathit{erase}(e) \\
\mathit{erase}(e_1\ e_2) &=& \mathit{erase}(e_1)\ \mathit{erase}(e_2)
\end{array}
\]

실제로 식이 바뀌는 경우는 람다 요약뿐이다. 표시된 매개변수 타입이 지워진다. 정수와 변수는 변화가 없고 합, 차, 함수 적용은 재귀적으로 정의된다.

Scala 코드로는 아래처럼 쓸 수 있다.

```scala
object FAE {
  sealed trait Expr
  case class Num(n: Int) extends Expr
  case class Add(l: Expr, r: Expr) extends Expr
  case class Sub(l: Expr, r: Expr) extends Expr
  case class Id(x: String) extends Expr
  case class Fun(x: String, b: Expr) extends Expr
  case class App(f: Expr, a: Expr) extends Expr
}

def erase(e: Expr): FAE.Expr = e match {
  case Num(n) => FAE.Num(n)
  case Add(l, r) =>
    FAE.Add(erase(l), erase(r))
  case Sub(l, r) =>
    FAE.Sub(erase(l), erase(r))
  case Id(x) => FAE.Id(x)
  case Fun(x, _, b) => FAE.Fun(x, erase(b))
  case App(f, a) =>
    FAE.App(erase(f), erase(a))
}
```

TFAE 식을 표현하는 클래스와 FAE 식을 표현하는 클래스의 이름이 겹치기 때문에 단독 객체 `FAE`를 만들고 그 안에 클래스를 정의했다. `erase` 함수의 구현은 위에서 정의한 \(\mathit{erase}\) 함수와 일치한다.

```scala
object FAE {
  ...

  sealed trait Value
  case class NumV(n: Int) extends Value
  case class CloV(p: String, b: FAE, e: Env) extends Value

  type Env = Map[String, Value]

  def interp(e: FAE, env: Env): Value = e match {
    case Num(n) => NumV(n)
    case Add(l, r) =>
      val NumV(n) = interp(l, env)
      val NumV(m) = interp(r, env)
      NumV(n + m)
    case Sub(l, r) =>
      val NumV(n) = interp(l, env)
      val NumV(m) = interp(r, env)
      NumV(n - m)
    case Id(x) => env(x)
    case Fun(x, b) => CloV(x, b, env)
    case App(f, a) =>
      val CloV(x, b, fEnv) = interp(f, env)
      interp(b, fEnv + (x -> interp(a, env)))
  }
}

def run(e: Expr): FAE.Value = {
  typeCheck(e, Map.empty)
  FAE.interp(erase(e), Map.empty)
}
```

`erase` 함수를 구현하여 TFAE 식을 FAE 식으로 변환함으로써 `interp` 함수를 새로 만들 필요 없이 기존의 FAE를 위한 `interp` 함수를 재사용할 수 있다. 매개변수 타입 표시 여부는 실행 결과에 영향을 주지 않는다. 따라서 위 `run` 함수는 앞에서 구현한 `run` 함수와 같은 결과를 낸다. 단, `Value` 객체가 아니라 `FAE.Value` 객체가 나온다. 클래스만 다를 뿐 나타내는 값은 완전히 같다. 이를 수식으로는 아래와 같이 적을 수 있다.

\[
\forall\sigma.\forall e.\forall v.
(\sigma\vdash e\Rightarrow v)\leftrightarrow(\mathit{erase}(\sigma)\vdash\mathit{erase}(e)\Rightarrow\mathit{erase}(v))
\]

아래는 값과 환경에 타입 지우개를 적용하는 것을 정의한 것이다.

\[
\begin{array}{rcl}
\mathit{erase}(n) &=& n \\
\mathit{erase}(\langle\lambda x:\tau.e,\sigma\rangle) &=& \langle\lambda x.\mathit{erase}(e),\mathit{erase}(\sigma)\rangle \\
\mathit{erase}(\lbrack x_1\mapsto v_1,\cdots,x_n\mapsto v_n\rbrack) &=&\lbrack x_1\mapsto\mathit{erase}(v_1),\cdots,x_n\mapsto\mathit{erase}(v_n)\rbrack
\end{array}
\]

### FAE의 타입 체계

FAE의 타입 체계를 정의할 수 있을까? FAE와 TFAE의 유일한 차이점은 람다 요약에 표시된 매개변수 타입이다. 따라서 나머지 식에 대해서는 TFAE의 타입 체계를 정의하는 추론 규칙을 그대로 사용할 수 있다. 람다 요약에 대해서만 추론 규칙을 정의하면 FAE의 타입 체계가 완성된다. 먼저 TFAE의 람다 요약에 대한 규칙을 다시 보자.

\[
\frac
{ \Gamma\lbrack x:\tau_1\rbrack\vdash e:\tau_2 }
{ \Gamma\vdash \lambda x:\tau_1.e:\tau_1\rightarrow\tau_2 }
\]

TFAE에서는 람다 요약을 보고 받을 인자의 타입이 \(\tau_1\)임을 알 수 있다. 문제없이 몸통 \(e\)의 타입을 \(\Gamma\lbrack x:\tau_1\rbrack\) 아래서 계산할 수 있다.

한편 FAE에는 매개변수 타입 표시가 없다. 그러므로 다음과 같이 규칙을 정의할 수밖에 없다.

\[
\frac
{ \Gamma\lbrack x:\tau_1\rbrack\vdash e:\tau_2 }
{ \Gamma\vdash \lambda x.e:\tau_1\rightarrow\tau_2 }
\]

재미있게도 전제가 완전히 같다. \(\tau_1\)을 어떻게 찾는지 전혀 알 수 없음에도 전제가 \(\tau_1\)을 사용한다. FAE의 타입 체계를 이렇게 정의하는 것은 전혀 문제없다. 이전에도 설명했듯이 언어의 의미는 반드시 구현에 대한 정보를 줄 필요가 없다. 그저 수학적으로 정의된 규칙일 뿐이다. 위 규칙은 FAE의 타입 안전성을 해치지 않는다. 그리고 여러 식의 타입을 정의한다. 예를 들어 \((\lambda x.\lambda y.x+y)\ 1\ 2\)를 생각해 보자. FAE의 추론 규칙에 따라 이 식의 타입이 \(\textsf{num}\)이라는 사실을 증명할 수 있다.

\[
\frac
{
  \frac
  {{\huge
    \frac
    {
      \frac
      {
        \frac
        {
          \frac
          { x\in\mathit{Domain}(\lbrack x:\textsf{num},y:\textsf{num}\rbrack) }
          { \lbrack x:\textsf{num},y:\textsf{num}\rbrack\vdash x:\textsf{num} } \quad
          \frac
          { y\in\mathit{Domain}(\lbrack x:\textsf{num},y:\textsf{num}\rbrack) }
          { \lbrack x:\textsf{num},y:\textsf{num}\rbrack\vdash y:\textsf{num} }
        }
        { \lbrack x:\textsf{num},y:\textsf{num}\rbrack\vdash x+y:\textsf{num} }
      }
      { \lbrack x:\textsf{num}\rbrack\vdash\lambda y.x+y
        :\textsf{num}\rightarrow\textsf{num} }
    }
    { \emptyset\vdash\lambda x.\lambda y.x+y
      :\textsf{num}\rightarrow\textsf{num}\rightarrow\textsf{num} } \quad
    {\Large \emptyset\vdash1:\textsf{num}}
  }}
  { {\Large \emptyset\vdash(\lambda x.\lambda y.x+y)\ 1:\textsf{num}\rightarrow\textsf{num} }}
  \quad \emptyset\vdash2:\textsf{num}
}
{ \emptyset\vdash(\lambda x.\lambda y.x+y)\ 1\ 2:\textsf{num} }
\]

사람은 \((\lambda x.\lambda y.x+y)\ 1\ 2\)라는 식을 보고 어렵지 않게 \(x\)와 \(y\)의 타입이 \(\textsf{num}\)이어야 한다는 것을 생각할 수 있다. 그러나 컴퓨터가 같은 일을 할 수 있게 만드는 것은 쉽지 않다. 앞서 TFAE의 타입 체계는 거의 그대로 `typeCheck` 함수의 구현이 되었다. 반면 FAE의 타입 체계는 람다 요약의 매개변수 타입을 어떻게 찾을지 알려주지 않는다. FAE 식을 위한 `typeCheck` 함수를 타입 체계만 보고서는 구현할 수 없는 것이다. FAE 타입 검사기를 만들고자 한다면 어떻게 표시되지 않은 매개변수 타입을 자동으로 찾을지 알아야 한다. 이처럼 타입 검사 과정에서 프로그래머가 표시하지 않은 타입을 컴퓨터가 스스로 알아내는 것을 타입 추론이라고 한다. 타입 추론은 이 글의 주제는 아니므로 더 자세히 다루지 않는다. 이후에 타입 추론이 주제인 글이 있을 것이다.

## TFAE의 확장

TFAE의 타입 체계는 안전하지만 완전하지 않다. 완전할 수 없는 것은 튜링 완전한 언어를 위한 타입 체계의 숙명이다. 타입 오류를 일으키지 않는 올바른 식도 타입 체계가 거절하는 경우가 반드시 존재한다. TFAE의 타입 체계는 얼마나 많은 올바른 식을 거절할까? 앞에서 본 식 \((\lambda x:\textsf{num}.x)\ (\lambda x:\textsf{num}.x)\)는 그런 식 중 하나이다.

TFAE는 *정규화 성질*(normalization property)을 가짐이 증명되어 있다. 다른 말로는, TFAE의 모든 올바른 타입의 식이 *정규화 가능*(normalizable)하다. 이는 *간단한 타입의 람다 대수*(simply typed lambda calculus)가 정규화 성질을 가지는 것으로부터 나온다.[^nor] 어떤 언어가 정규화 성질을 가진다는 것은 그 언어의 모든 올바른 타입의 식이 유한한 횟수의 환원 이후에 더는 환원할 수 없는 식이 된다는 것을 의미한다. 즉, 어떠한 올바른 타입의 식도 실행하면 언젠가 종료된다. 무한히 실행되는 올바른 타입의 식은 존재하지 않는다. 이는 TFAE의 표현력의 한계를 단적으로 보여준다. FAE의 식 중에는 \((\lambda x.x\ x)\ (\lambda x.x\ x)\)와 같은 끝나지 않고 실행되는 식이 무수히 많다. 그러한 식은 TFAE에서는 모두 잘못된 타입의 식이다. 단 하나의 무한히 실행되는 식도 타입 검사를 통과할 수 없다. 람다 대수와 FAE가 튜링 완전한 것과 달리, 타입 체계가 계산이 끝나지 않는 식을 거절하는 TFAE는 튜링 완전하지 않다.

[^nor]: Benjamin C. Pierce, *Types and Programming Languages*, The MIT Press, Chapter 12, Pages 149-152

지금까지의 글들은 프로그래머의 편의를 위해 언어를 확장해 왔다. 람다 대수만 해도 이미 튜링 완전하므로 람다 대수에 기능을 추가하는 것은 표현할 수 있는 프로그램을 늘리지 못한다. 그러나 지금까지 람다 대수에 정수와 산술 연산을 추가했으며, 재귀 함수를 쉽게 정의하는 방법을 추가했고, 수정 가능한 상자와 변수를 추가했다. 일급 계속을 추가하기도 했다. 표현력을 높이지 못하더라도 이런 확장은 의미가 있다. 프로그래머가 복잡한 프로그램을 더 쉽게 구현할 수 있게 하기 때문이다. 프로그래머는 추가된 기능을 사용하여 람다 대수로 수백 글자를 필요로 할 코드를 수십 글자, 어쩌면 단 몇 글자만을 사용하여 구현할 수 있게 된다. 이런 코드는 짧을 뿐 아니라 이해하기도 쉽다.

언어에 타입 체계가 추가되어도, 여전히 언어를 확장하는 이유 중 하나는 프로그래머의 편의를 늘리기 위함이다. 표현력을 키우지 못하더라도 프로그래머가 복잡한 프로그램을 쉽게 만들 수 있게 하는 것은 바람직한 일이다. 그러나 그밖에도 언어를 확장할 다른 이유가 있다.

언어를 확장함으로써 타입 체계가 더 많은 프로그램을 받아들이게 할 수 있다. 비록 완전해질 수는 없지만 더 많은 타입 오류를 일으키지 않는 프로그램을 올바른 타입의 프로그램으로 분류하는 것이다. 다르게 표현하면 타입 체계를 정교하게 만드는 것이라 할 수 있다. 타입이 값을 더 정확하고 자세하게 분류함으로써 타입 체계가 더 많은 오류 없는 식을 올바른 타입의 식으로 판단할 수 있다. 이는 언어의 표현력을 높여 프로그래머가 실질적으로 더 많은 것을 구현할 수 있게 하므로 가치 있는 일이다. 대표적으로는 *재귀적 타입*(recursive type)을 추가하여 TFAE를 튜링 완전하게 만드는 일이 있다. 다음 두 글에서 살펴볼 재귀 함수의 정적 의미와 재귀적인 타입 정의를 제공하는 확장이 비슷한 일이다.

타입 체계를 정교하게 하는 것은 더 많은 실행 시간 오류를 타입 오류로 볼 수 있게 만들기도 한다. TFAE는 단순한 언어이기에 자유 식별자 오류와 타입 오류만이 가능한 실행 시간 오류이다. TFAE의 타입 체계는 자유 식별자 오류와 타입 오류에 대해 모두 안전하다. 올바른 타입의 TFAE 식은 어떤 실행 시간 오류도 일으키지 않는다는 것이다. 그러나 현실의 많은 언어에는 다양한 실행 시간 오류가 존재한다. 대개의 경우 타입 오류는 그중 일부만이다. 타입 체계가 안전하더라도 타입 오류가 아닌 실행 시간 오류에 대해서는 아무것도 보장할 수 없다. 올바른 타입의 프로그램을 실행했는데도 실행 시간 오류가 발생할 수 있는 것이다. 타입 체계를 정교하게 함으로써 이를 해결할 수 있다. 더 다양한 실행 시간 오류를 타입 오류로 처리한다면 타입 체계가 보장할 수 있는 것이 늘어난다. 그런 예시는 실제 언어에서 많이 찾아볼 수 있다.

Java에서 흔하게 발생하는 실행 시간 오류는 널 포인터 예외이다. Java의 타입 체계에서는 널 포인터 예외는 타입 오류가 아니므로 올바른 타입의 Java 프로그램을 실행해도 널 포인터 예외는 얼마든지 발생할 수 있다. Kotlin은 이 문제를 널 가능 타입과 널 불가능 타입을 도입하여 해결하였다[^kot]. 널 가능 타입의 값을 사용할 때는 제약이 있고 널 불가능 타입은 널 포인터 예외가 발생할 걱정 없이 사용할 수 있다. 프로그래머가 Kotlin이 제공하는 타입 안전하지 않은 기능을 사용하지만 않는다면 올바른 타입의 Kotlin 프로그램은 널 포인터 예외를 일으키지 않는다.

[^kot]: Kotlin, *Null Safety*, <https://kotlinlang.org/docs/reference/null-safety.html>

배열의 잘못된 위치를 참조하는 것 역시 흔한 실행 시간 오류이다. C에서는 실행 중 배열의 경계를 검사하지 않으므로 배열의 잘못된 위치를 참조했을 때 프로그래머가 예상하지 못한 일이 일어날 수 있다. 이는 보안상 문제가 되기도 한다. Java나 Python 같은 많은 언어에서는 배열의 잘못된 위치를 참조하면 오류가 발생하고 프로그램 실행이 비정상적으로 종료된다. 이들 언어에서는 모두 배열의 잘못된 위치를 참조하는 것을 타입 오류로 보지 않는다. 따라서 올바른 타입의 C, Java, Python 프로그램 모두 실행 중 배열의 잘못된 위치를 참조하는 일이 일어날 수 있다. 이는 타입이 배열의 길이를 표현하지 못하기 때문이다. *의존적 타입*(dependent type)은 타입이 값에 의존할 수 있게 만들어 이를 해결한다. 길이가 3인 배열이라는 타입을 정의할 수 있는 것이다. 어떤 프로그램이 이 타입의 배열의 네 번째 값을 읽고자 한다면 타입 검사기는 그 프로그램을 거절한다. 다만, 의존적 타입은 그 복잡성으로 인해 흔하게 찾아볼 수 있는 기능은 아니다. Scala 3는 *대조 타입*(match type)[^mat]을 통해 유사한 기능을 제공한다. Scala 3 컴파일러는 다음 코드를 거절한다.

[^mat]: LAMP, *Match Types*, <https://dotty.epfl.ch/docs/reference/new-types/match-types.html>, EPFL, Dotty Documentation

```scala
val x = (0, 1, 2)
x(3)
```

`x`는 길이가 3인 튜플이므로 `x(3)`은 실행 시간 오류를 일으키는 식이다. 컴파일 시 다음의 오류를 확인할 수 있다.

```
|val y = x(3)
|          ^
|          index out of bounds: 3
```

정리해 보면, 언어와 그 언어의 타입 체계를 확장하는 데는 크게 세 가지 이유가 있다. 첫째, 프로그래머의 편의를 위함이다. 둘째, 언어의 표현력을 키우기 위함이다. 셋째, 더 많은 실행 시간 오류를 정적 타입 검사를 통해 방지하기 위함이다. 이 글에서는 TFAE의 몇 가지 간단한 확장을 살펴본다.

### 지역 변수 선언

다음은 TFAE에 지역 변수 선언을 추가한다.

\[
\begin{array}{lrcl}
\text{Expression} & e & ::= & \cdots \\
&& | & \textsf{val}\ x=e\ \textsf{in}\ e
\end{array}
\]

동적 의미는 WAE에서 본 것과 같다.

\[
\frac
{
  \sigma\vdash e_1\Rightarrow v_1 \quad
  \sigma\lbrack x\mapsto v_1\rbrack\vdash e_2\Rightarrow v_2
}
{ \sigma\vdash \textsf{val}\ x=e_1\ \textsf{in}\ e_2\Rightarrow v_2 }
\]

정적 의미도 비슷하게 정의할 수 있다.

\[
\frac
{
  \Gamma\vdash e_1:\tau_1 \quad
  \Gamma\lbrack x:\tau_1\rbrack\vdash e_2: \tau_2
}
{ \Gamma\vdash \textsf{val}\ x=e_1\ \textsf{in}\ e_2:\tau_2 }
\]

지역 변수는 쉽게 람다 요약과 함수 적용으로 표현할 수 있다. 한 가지 문제는 지역 변수 선언 시에는 \(x\)의 타입을 표시할 필요가 없지만 \(x\)가 람다 요약의 매개변수가 되면 타입을 표시해야 한다는 점이다. 따라서 지역 변수 선언은 프로그래머가 같은 프로그램을 더 쉽고 간결하게 작성할 수 있게 한다.

다음은 TFAE 인터프리터가 지역 변수 선언을 처리할 수 있게 한다.

```scala
case class With(x: String, e: Expr, b: Expr) extends Expr

def typeCheck(e: Expr, env: TEnv): Type = e match {
  ...
  case With(x, e, b) =>
    typeCheck(b, env + (x -> typeCheck(e, env)))
}

def interp(e: Expr, env: Env): Value = e match {
  ...
  case With(x, e, b) =>
    interp(b, env + (x -> interp(e, env)))
}
```

### 순서쌍

FAE에서는 람다 요약과 함수 적용을 사용해 순서쌍을 인코딩할 수 있었다.

\[
\begin{array}{rcl}
\mathit{encode}((e_1,e_2)) &=&
(\lambda l.\lambda r.\lambda f.f\ l\ r)\ \mathit{encode}(e_1)\ \mathit{encode}(e_2) \\
\mathit{encode}(e.1) &=& \mathit{encode}(e)\ \lambda l.\lambda r.l \\
\mathit{encode}(e.2) &=& \mathit{encode}(e)\ \lambda l.\lambda r.r \\
\end{array}
\]

\((e_1,e_2)\)는 순서쌍을 만드는 식이다. \(e_1\)을 계산한 결과가 \(v_1\), \(e_2\)를 계산한 결과가 \(v_2\)이면, \((e_1,e_2)\)를 계산한 결과는 첫 번째 값이 \(v_1\)이고 두 번째 값이 \(v_2\)인 순서쌍이다. \((\lambda l.\lambda r.\lambda f.f\ l\ r)\ e_1\ e_2\)를 계산하면 \(\lambda f.f\ v_1\ v_2\)가 된다. 이는 함수 하나를 인자로 받아 \(v_1\)과 \(v_2\)를 차례로 그 함수에 인자로 넘기는 함수이다. 이 값이 어떻게 순서쌍을 나타내는지 뒤에서 볼 수 있다.

\(e.1\)는 순서쌍의 첫 번째 값을 얻는 식이다. \(e\)를 계산한 결과가 순서쌍이면 \(e.1\)의 결과는 그 순서쌍의 첫 번째 값이다. \(\mathit{encode}(e)\ \lambda l.\lambda r.l\)을 계산하면 \(\mathit{encode}(e)\)의 값이 \(\lambda f.f\ v_1\ v_2\)일 것이라 기대할 수 있다. 여기에 \(\lambda l.\lambda r.l\)을 인자로 넘겨야 한다. \(\lambda l.\lambda r.l\)은 인자 두 개를 받아 첫 인자를 결과로 낸다. 따라서 최종 결과는 \(v_1\)이다. 이는 원하는 대로 순서쌍의 첫째 값을 얻은 것이다.

\(e.2\)는 순서쌍의 두 번째 값을 얻는 식이다. \(\lambda l.\lambda r.r\)은 인자 두 개를 받아 두 번째 인자를 결과로 낸다. 순서쌍의 첫 번째 값을 얻는 것과 같은 논리로 보면 올바른 인코딩임을 확인할 수 있다.

TFAE에서도 같은 방식으로 순서쌍을 인코딩할 수 있을까? 첫 번째 값은 \(\textsf{num}\) 타입이고 두 번째 값은 \(\textsf{num}\rightarrow\textsf{num}\) 타입인 순서쌍을 생각해 보자. \(\lambda l.\lambda r.l\)은 \(\lambda l:\textsf{num}.\lambda r:\textsf{num}\rightarrow\textsf{num}.l\)로 바꿀 수 있다. \(\lambda l.\lambda r.r\) 역시 \(\lambda l:\textsf{num}.\lambda r:\textsf{num}\rightarrow\textsf{num}.r\)로 바꿀 수 있다. 그러나 \(\lambda l.\lambda r.\lambda f.f\ l\ r\)를 다시 쓰려 하면 문제가 생긴다. \(l\)의 타입은 \(\textsf{num}\)이고 \(r\)의 타입은 \(\textsf{num}\rightarrow\textsf{num}\)이다. \(f\)의 타입은 무엇일까? \(\lambda l:\textsf{num}.\lambda r:\textsf{num}\rightarrow\textsf{num}.l\)과 \(\lambda l:\textsf{num}.\lambda r:\textsf{num}\rightarrow\textsf{num}.r\) 모두 인자로 받을 수 있어야 한다. 그런데 하나는 \(\textsf{num}\rightarrow(\textsf{num}\rightarrow\textsf{num})\rightarrow\textsf{num}\) 타입이고 다른 하나는 \(\textsf{num}\rightarrow(\textsf{num}\rightarrow\textsf{num})\rightarrow(\textsf{num}\rightarrow\textsf{num})\) 타입이므로 \(f\)의 타입은 하나로 결정되지 않는다. 따라서 TFAE에서는 순서쌍을 FAE와 같은 방법으로 인코딩할 수 없다.

다음은 TFAE에 순서쌍을 추가한다.

\[
\begin{array}{lrcl}
\text{Expression} & e & ::= & \cdots \\
&& | & (e,e) \\
&& | & e.1 \\
&& | & e.2 \\
\text{Value} & v & ::= & \cdots \\
&& | & (v,v) \\
\end{array}
\]

\((v_1,v_2)\)는 첫 번째 값이 \(v_1\)이고 두 번째 값의 \(v_2\)인 순서쌍이다.

동적 의미는 다음과 같다. 이해하기 쉬우므로 별다른 설명은 하지 않겠다.

\[
\frac
{
  \sigma\vdash e_1\Rightarrow v_1 \quad
  \sigma\vdash e_2\Rightarrow v_2
}
{ \sigma\vdash (e_1,e_2)\Rightarrow (v_1,v_2) }
\]

\[
\frac
{
  \sigma\vdash e\Rightarrow (v_1,v_2)
}
{ \sigma\vdash e.1\Rightarrow v_1 }
\]

\[
\frac
{
  \sigma\vdash e\Rightarrow (v_1,v_2)
}
{ \sigma\vdash e.2\Rightarrow v_2 }
\]

정적 의미도 쉽게 정의할 수 있다.

\[
\begin{array}{lrcl}
\text{Type} & \tau & ::= & \cdots \\
&& | & \tau\times\tau \\
\end{array}
\]

\(\tau_1\times\tau_2\)는 첫 번째 값의 타입이 \(\tau_1\)이고 두 번째 값의 타입이 \(\tau_2\)인 순서쌍의 타입이다.

\[
\frac
{
  \Gamma\vdash e_1:\tau_1 \quad
  \Gamma\vdash e_2:\tau_2
}
{ \Gamma\vdash (e_1,e_2):\tau_1\times\tau_2 }
\]

\[
\frac
{
  \Gamma\vdash e:\tau_1\times\tau_2
}
{ \Gamma\vdash e.1:\tau_1 }
\]

\[
\frac
{
  \Gamma\vdash e:\tau_1\times\tau_2
}
{ \Gamma\vdash e.2:\tau_2 }
\]

다음은 TFAE 인터프리터가 순서쌍을 처리할 수 있게 한다.

```scala
case class Pair(f: Expr, s: Expr) extends Expr
case class Fst(e: Expr) extends Expr
case class Snd(e: Expr) extends Expr

case class PairT(f: Type, s: Type) extends Type

def typeCheck(e: Expr, env: TEnv): Type = e match {
  ...
  case Pair(f, s) =>
    PairT(typeCheck(f, env), typeCheck(s, env))
  case Fst(e) =>
    val PairT(f, s) = typeCheck(e, env)
    f
  case Snd(e) =>
    val PairT(f, s) = typeCheck(e, env)
    s
}

case class PairV(f: Value, s: Value) extends Value

def interp(e: Expr, env: Env): Value = e match {
  ...
  case Pair(f, s) =>
    PairV(interp(f, env), interp(s, env))
  case Fst(e) =>
    val PairV(f, s) = interp(e, env)
    f
  case Snd(e) =>
    val PairV(f, s) = interp(e, env)
    s
}
```

### 조건식

다음은 불 값과 조건식을 FAE로 인코딩한 것이다. 이는 이미 “람다 대수” 편에서 보았다. 아래 인코딩은 소극적 계산을 사용할 때 올바른 것이고 적극적 계산을 사용하려면 수정이 필요하다. 그러나 말하고자 하는 바는 어떤 인코딩을 사용하든 같다. 그러니 더 단순한 방식을 사용하도록 하겠다.

\[
\begin{array}{rcl}
\mathit{encode}(\textsf{true})&=&\lambda a.\lambda b.a \\
\mathit{encode}(\textsf{false})&=&\lambda a.\lambda b.b \\
\mathit{encode}(\textsf{if}\ e_1\ e_2\ e_3)&=&\mathit{encode}(e_1)\ \mathit{encode}(e_2)\ \mathit{encode}(e_3)
\end{array}
\]

이 인코딩은 TFAE에서 사용될 수 없다. 예를 들어 변수 \(x\)의 값이 \(\textsf{true}\)라고 하자. 그리고 전체 식에 \((\textsf{if}\ x\ 0\ 1)\)과 \((\textsf{if}\ x\ (\lambda x:\textsf{num}.0)\ (\lambda x:\textsf{num}.1))\)이 모두 존재한다고 가정하자. 각각 \(x\ 0\ 1\)과 \(x\ (\lambda x:\textsf{num}.0)\ (\lambda x:\textsf{num}.1)\)로 인코딩된다. 첫 \(x\)의 타입은 \(\textsf{num}\rightarrow\textsf{num}\rightarrow\textsf{num}\)인 반면, 두 번째 \(x\)의 타입은 \((\textsf{num}\rightarrow\textsf{num})\rightarrow(\textsf{num}\rightarrow\textsf{num})\rightarrow(\textsf{num}\rightarrow\textsf{num})\)이다. 따라서 \(x\)의 타입이 하나로 결정되지 않는다.

다음은 TFAE에 불 값과 조건식을 추가한다.

\[
\begin{array}{lrcl}
\text{Boolean} & b & ::= & \textsf{true} \\
&&|& \textsf{false} \\
\text{Expression} & e & ::= & \cdots \\
&& | & b \\
&& | & \textsf{if}\ e\ e\ e \\
\text{Value} & v & ::= & \cdots \\
&& | & b \\
\end{array}
\]

동적 의미는 다음과 같다.

\[
\sigma\vdash b\Rightarrow b
\]

\[
\frac
{
  \sigma\vdash e_1\Rightarrow\textsf{true} \quad
  \sigma\vdash e_2\Rightarrow v
}
{ \sigma\vdash \textsf{if}\ e_1\ e_2\ e_3\Rightarrow v }
\]

\[
\frac
{
  \sigma\vdash e_1\Rightarrow\textsf{false} \quad
  \sigma\vdash e_3\Rightarrow v
}
{ \sigma\vdash \textsf{if}\ e_1\ e_2\ e_3\Rightarrow v }
\]

정적 의미는 다음과 같다.

\[
\begin{array}{lrcl}
\text{Type} & \tau & ::= & \cdots \\
&& | & \textsf{bool} \\
\end{array}
\]

\(\textsf{bool}\)은 불 값의 타입이다.

\[
{ \Gamma\vdash b:\textsf{bool} }
\]

식이 불 값이면 그 타입은 \(\textsf{bool}\)이다.

\[
\frac
{
  \Gamma\vdash e_1:\textsf{bool} \quad
  \Gamma\vdash e_2:\tau \quad
  \Gamma\vdash e_3:\tau
}
{ \Gamma\vdash \textsf{if}\ e_1\ e_2\ e_3:\tau }
\]

조건식의 동적 의미에 따르면 \(e_1\)의 계산 결과가 \(\textsf{true}\)나 \(\textsf{false}\)일 때만 \(\textsf{if}\ e_1\ e_2\ e_3\)을 계산할 수 있다. 따라서 \(e_1\)의 타입은 \(\textsf{bool}\)이어야 한다.

한편 동적 의미는 \(e_2\)와 \(e_3\)의 계산 결과의 타입에 대해 아무것도 요구하지 않는다. 그러나 정적 의미를 정의하기 위해서는 \(\textsf{if}\ e_1\ e_2\ e_3\)의 타입은 하나로 결정되어야 한다. \(e_2\)와 \(e_3\)의 타입이 다르면 조건식의 타입을 정할 수 없다. 그러므로 추론 규칙의 전제에서 \(e_2\)와 \(e_3\)의 타입은 모두 \(\tau\)이다. 이 경우 조건식의 타입도 \(\tau\)이다. 이는 이 타입 체계를 완전하지 않게 하는 또 하나의 원인이 된다. \(\textsf{if}\ \textsf{true}\ 0\ \textsf{false}\) 같이 당연히 타입 오류를 일으키지 않는 식도 타입 체계가 거절한다. 하나의 값이 여러 타입에 속할 수 있는 타입 체계는 이보다 더 정확하게 조건식의 타입을 구할 수 있다. 물론 그 타입 체계 역시 완전할 수 없다.

다음은 TFAE 인터프리터가 불 값과 조건식을 처리할 수 있게 한다.

```scala
case class Bool(b: Boolean) extends Expr
case class If(c: Expr, t: Expr, f: Expr) extends Expr

case object BoolT extends Type

def typeCheck(e: Expr, env: TEnv): Type = e match {
  ...
  case Bool(b) => BoolT
  case If(c, t, f) =>
    mustSame(typeCheck(c, env), BoolT)
    mustSame(typeCheck(t, env), typeCheck(f, env))
}

case class BoolV(b: Boolean) extends Value

def interp(e: Expr, env: Env): Value = e match {
  ...
  case Bool(b) => BoolV(b)
  case If(c, t, f) =>
    val BoolV(b) = interp(c, env)
    interp(if (b) t else f, env)
}
```

## 감사의 말

글을 확인하고 의견을 주신 류석영 교수님께 감사드립니다.
