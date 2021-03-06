**아래 내용은 인사이트 출판사의 제안으로 작성 중인 책의 초고입니다. 실제 출판
시에는 내용이 달라질 수 있습니다. 많은 의견 부탁드립니다.**

정의하는 코드는 문자열(string)로 이루어진다. 스크래치(Scratch)와 같이 코드가
문자열이 아닌 언어도 존재하기는 하지만 극히 일부일 뿐이다. 따라서 이 책에서는 한
프로그램이 한 개의 문자열로 정의되는 것으로 생각한다. 문자열이란 개념은
프로그래밍을 해 본 대부분 사람에게 이미 익숙할 것이다. 그러나 사람마다 생각하는
문자열의 정의가 다를 수 있다. 또, 문자열 “한 개”라고 표현한 것이 어색하게 느껴질
수도 있다. 그러니 가장 먼저 해야 하는 일은 문자열이 무엇인지, 문자열 한 개가
무엇인지 정의하는 것이다.

문자열은 이름 그대로 문자(character)를 나열한 것이다. 0개 이상의 문자를
나열함으로써 한 개의 문자열을 만들 수 있다. 예를 들어 로마자와 아라비아 숫자가
문자라면 다음과 같은 것들이 각각 한 개의 문자열이다. (여기서 큰따옴표는 문자열을
표기하기 위한 기호로, 문자열의 일부가 아니다.)

* “abcdef”
* “a1b2c3”

만약 문자에 한글과 줄 바꿈도 포함된다면 다음의 것들도 각각 한 개의 문자열이라 할
수 있다.

* “가나ab”
* “안녕하세요”
* “만나서

반갑습니다”

이처럼 문자열이 무엇인지는 문자가 무엇인지에 달려있다. 많은 현대적인 프로그래밍
언어에서는 유니코드로 표현되는 모든 대상을 문자로 취급한다. 그러나 어떤
언어에서는 아스키코드로 표현 가능한 대상만을 문자로 간주하며, 그보다 더 적은
대상만을 문자로 보는 언어도 있을 것이다. 즉, 언어마다 문자의 정의가 다르다.
일반적인 언어에 대한 논의가 가능하려면 문자의 모임을 추상적으로 다룰 필요가
있다. 실제로 무엇이 문자인지 따지기 보다는 문자가 모인 무엇인가가 존재한다고
가정하는 것이다. 수학에서 수학적 대상들의 모임을 표현하기 위해 일반적으로
사용하는 개념은 집합이다. 문자의 모임을 다루고 싶은 것이므로 문자의 집합을
정의해야 한다. 지금부터 \(C\)는 모든 문자의 집합을 나타낸다.

\[C = 모든\ 문자의\ 집합 = \{ c\ |\ c는\ 문자 \}\]

각 언어마다 \(C\)는 다른 집합이다. 유니코드로 표현 가능한 모든 대상이 문자인
언어에서는 \(C = \{ c\ |\ c는\ 유니코드로\ 표현\ 가능 \}\)이며, 아스키코드로 표현 가능한
모든 대상이 문자인 언어에서는 \(C = \{ c\ |\ c는\ 아스키코드로\ 표현\ 가능 \}\)이다.
언어에서 문자를 어떻게 정의하든 상관없이 문자의 집합 \(C\)는 언제나 그 언어에 맞게
만들 수 있다. 그러므로 \(C\)가 실제로 어떤 집합인지는 더 이상 신경 쓰지 않아도 된다.
임의의 집합을 문자의 집합으로 가정하는 것이 잘 이해되지 않는다면
\(C = \{ c\ |\ c는\ 유니코드로\ 표현\ 가능 \}\)이라고 생각하고 넘어가도 무방하다.

문자가 무엇인지 정의했으니 이제 문자열이 무엇인지 정의할 수 있다. \(C^\ast\)는 모든
문자열의 집합을 나타낸다.

\[C^\ast = 모든\ 문자열의\ 집합 = \{ “c_1c_2\cdots c_n”\ |\ c_1, c_2, \cdots, c_n \in C \}\]

문자열은 0개 이상의 문자를 유한한 횟수만큼 나열한 것이다. 한 문자열에 같은
문자가 중복해서 등장할 수 있고, 같은 문자들로 구성되어 있더라도 문자의 순서가
다르면 다른 문자열이다. 예를 들어 \(C = \{ ‘a’, ‘b’ \}\)이면, \(C^\ast = \{ “”, “a”, “b”,
“aa”, “ab”, “ba”, “bb”, \cdots \}\)이다. 또, \(C = \{ c\ |\ c는\ 유니코드로\ 표현\ 가능 \}\)이면
\(C^\ast\)는 컴퓨터로 작성 가능한 모든 글의 집합이라고 볼 수 있다.

문자열이 정의되었다고 프로그램이 정의된 것은 아니다. 모든 문자열이 프로그램인
것은 아니기 때문이다. 모든 문자열이 프로그램인 프로그래밍 언어도 정의할 수는
있겠으나, 일반적인 프로그래밍 언어에서는 프로그램이 아닌 문자열이 존재한다.
파이썬을 예시로 생각해 보자.

```python
1 + 1
```

위의 문자열은 파이썬 프로그램이다. 파이썬 프로그램이므로 파이썬
인터프리터(interpreter)가 실행할 수 있다.

```python
1 +
```

한편 위의 문자열은 파이썬 프로그램이 아니다. 이 문자열을 파이썬 인터프리터를
사용해 실행하려고 시도하면 “SyntaxError: invalid syntax”라는 오류 문구가
출력된다. 오류 문구는 사용한 파이썬 판에 따라 차이가 있을 수 있으나, 여기서
핵심은 문법 오류가 탐지되었다는 것이다. 문법 오류라는 말의 의미는 주어진
문자열이 프로그램조차 아니기 때문에 실행할 수 없다는 것이다.

이처럼 문자열 중 프로그램인 문자열이 있고 그렇지 않은 문자열이 있으므로
프로그래밍 언어를 정의하려면 어떤 문자열이 프로그램인지 정의해야 한다. 이것이
바로 구체적 문법이 하는 일이다. 구체적 문법은 어떤 문자열이 프로그램이 될 수
있는지 정한다. 즉, 프로그래밍 언어의 겉모습을 정의하는 것이다. 모든 프로그램의
집합을 \(P\)라고 하자.

\[P = 모든\ 프로그램의\ 집합 = \{ p\ |\ p는\ 프로그램 \} \subseteq C^\ast\]

모든 프로그램은 문자열이지만 문자열 중 프로그램이 아닌 문자열이 존재할 수
있으므로 \(P\)는 \(C^\ast\)의 부분 집합이다. 구체적 문법은 \(P\)를 정의한다. 파이썬의 예시로
돌아가면 “1 + 1”은 \(C^\ast\)의 원소이면서 동시에 \(P\)의 원소이다. 그러나 “1 +”는 \(C^\ast\)의
원소는 맞지만 P의 원소는 아니다. 따라서 “1 + 1”은 파이썬 프로그램이고 파이썬
인터프리터가 실행할 수 있지만, “1 +”은 파이썬 프로그램이 아니고 파이썬
인터프리터로 실행하려고 시도하면 문법 오류가 발생한다.

언어의 구체적 문법을 정의하는 것은 프로그램의 집합인 \(P\)를 정의하는 것이다. 문제는
대부분의 언어에서 만들 수 있는 프로그램의 개수가 무수히 많다는 것이다. 다시
말해, 프로그램의 집합인 \(P\)는 대개의 경우 무한 집합이다. 무한 집합을 정의하는 것은
쉬운 일이 아니다. 유한 집합은 그 집합의 원소를 나열함으로써 정의할 수 있지만
무한 집합은 그럴 수 없다. 따라서 언어의 구체적 문법을 정의하기 위해서는 무한
집합을 잘 정의할 방법이 필요하다.

구체적 문법을 정의하는 데 널리 사용되는 방식 중 하나는 배커스-나우르
형식(Backus-Naur form; BNF)이다. 배커스-나우르 형식은 직관적인 방법으로 문자열의
집합을 정의한다. 배커스-나우르 형식이 집합을 정의하는 방법은
구성적(constructive)이다. 그 집합에 속하는 원소를 실제로 어떻게 만드는지
알려줌으로써 집합을 정의한다는 뜻이다. 배커스-나우르 형식으로 집합을 만드는
방법을 보기에 앞서 배커스-나우르 형식에 등장하는 개념부터 보겠다.

배커스-나우르 형식은 말단 기호(terminal), 비말단 기호(nonterminal),
식(expression)이라는 세 가지 개념을 사용한다. 한 개의 말단 기호는 한 개의
문자열이다. 그러므로 “0”, “ab”, “가나다” 등이 말단 기호이다. 비말단 기호는
\(\langle\)digit\(\rangle\), \(\langle\)number\(\rangle\) 등과 같이 홑화살괄호의 쌍 사이에 이름을 쓴 것이다. 각각의
비말단 기호는 문자열의 집합을 나타낸다. 예를 들면 \(\langle\)digit\(\rangle\)이 \(\{ “0”, “1”, “2”,
“3”, “4”, “5”, “6”, “7”, “8”, “9” \}\)라는 문자열의 집합을 나타낼 수 있다. 말단
기호와 비말단 기호를 통틀어 기호라 부른다. 식은 한 개 이상의 기호를 유한한
횟수만큼 나열한 것이다. 즉, 다음의 것들이 모두 식이다.

* “가나다”처럼 말단 기호 하나를 적은 것
* “0” “1”처럼 말단 기호 여럿을 적은 것
* \(\langle\)digit\(\rangle\)처럼 비말단 기호 하나를 적은 것
* \(\langle\)digit\(\rangle\) \(\langle\)number\(\rangle\)처럼 비말단 기호 여럿을 적은 것
* “-” \(\langle\)number\(\rangle\)처럼 말단 기호와 비말단 기호를 적은 것

한 개의 식은 문자열 집합 하나를 나타낸다. 어떤 식이 나타내는 집합은 그 식을
구성하는 기호가 나타내는 문자열을 전부 이어 붙여 만들어진 모든 문자열을
포함한다. 예를 들어 “0” “1”이라는 식을 생각해 보자. “0”이 나타내는 문자열은
“0”뿐이고 “1”이 나타내는 문자열은 “1”뿐이다. “0”과 “1”을 이어 붙이면 “01”이므로
“0” “1”이 나타내는 집합은 \(\{ “01” \}\)이다. 또, \(\langle\)digit\(\rangle\)이 \(\{ “0”, “1”, “2”, “3”, “4”,
“5”, “6”, “7”, “8”, “9” \}\)라는 집합을 나타낼 때, “0” \(\langle\)digit\(\rangle\)이라는 식이 나타내는
집합이 무엇인지 생각해 보자. \(\langle\)digit\(\rangle\)이 나타내는 문자열 중 하나는 “0”이다. “0”과
“0”을 이어 붙이면 “00”이 된다. 비슷하게, \(\langle\)digit\(\rangle\)이 “1”도 나타낼 수 있으므로
“0”과 “1”을 이어 붙여 “01” 역시 만들어진다. 이 과정을 계속 반복하면 “0”
\(\langle\)digit\(\rangle\)이 \(\{ “00”, “01”, “02”, “03”, “04”, “05”, “06”, “07”, “08”, “09” \}\)를
나타냄을 알 수 있다.

이제 위에서 등장한 세 개념을 사용해 집합을 정의하는 방법을 알아보겠다.
배커스-나우르 형식에서 어떤 집합을 정의하는 것은 어떤 비말단 기호가 나타내는
집합을 정의하는 것이다. 그 방법은 다음과 같다.

비말단 기호 ::= 식 | 식 | 식 | \(\cdots\)

우변에 보이는 |는 식과 식 사이를 나누기 위해 사용되었다. 우변에 있는 식들이
좌변의 비말단 기호가 나타내는 집합을 정의한다. 우변의 식들이 나타내는 집합을
모두 합친 것이 좌변의 비말단 기호가 나타내는 집합이다. 예를 들어, 다음과 같이
\(\langle\)digit\(\rangle\)이라는 집합을 정의할 수 있다.

\(\langle\)digit\(\rangle\) ::= “0” | “1” | “2” | “3” | “4” | “5” | “6” | “7” | “8” | “9”

이때 \(\langle\)digit\(\rangle\)이 나타내는 집합은 \(\{ “0”, “1”, “2”, “3”, “4”, “5”, “6”, “7”, “8”,
“9” \}\)이다.

지금부터는 `산술`이라는 이름의 아주 작은 언어를 정의하면서 이야기를 진행해 보려고
한다. 단순히 설명만 읽는 것 보다는 직접 예시를 보는 것이 이해에 도움이 될
것이다. `산술`은 이름으로부터 알 수 있듯이 산술식을 표현하는 언어이다. 이 언어의
기능은 십진 정수의 합과 차를 계산하는 것이다. 우리가 아는 프로그래밍 언어와
비교했을 때 언어라고 부르기 민망할 정도로 제한된 기능만을 제공하는 언어이다.
그러나 간단하기 때문에 이 장의 목표인 구체적 문법, 요약 문법, 의미를 설명하는
데는 최적이라고 생각한다. 이 장에서 `산술`의 구체적 문법부터 시작해 요약 문법과
의미까지 정의할 것이다.

`산술`의 프로그램은 십진 정수를 표현할 수 있어야 한다. 따라서 다음과 같은
문자열들이 `산술`의 프로그램이다.

* “0”
* “1”
* “-10”
* “42”

또, 정수의 합이나 차도 표현할 수 있어야 한다. 따라서 다음의 문자열들도 `산술`의
프로그램이다.

* “0+1”
* “-2-1”
* “1+-3+42”
* “4-3+2-1”

즉, 프로그램의 집합인 \(P\)는 “0”, “1”, “-10”, “42”, “0+1”, “-2-1”, “1+-3+42”,
“4-3+2-1” 등을 원소로 가진다. 그러나 무한 집합인 \(P\)를 이렇게 원소를 늘어놓는
방법으로 정의할 수는 없다. 지금부터는 배커스-나우르 형식을 사용해 `산술`의 구체적
문법을 정의하겠다.

먼저, 배커스-나우르 형식을 통해 십진 정수를 나타내는 문자열의 집합을 정의해
보자. 그 방법은 다음과 같다.

\(\langle\)digit\(\rangle\) ::= “0” | “1” | “2” | “3” | “4” | “5” | “6” | “7” | “8” | “9”

\(\langle\)nat\(\rangle\) ::= \(\langle\)digit\(\rangle\) | \(\langle\)digit\(\rangle\) \(\langle\)nat\(\rangle\)

\(\langle\)number\(\rangle\) ::= \(\langle\)nat\(\rangle\) | “-” \(\langle\)nat\(\rangle\)

앞에서 말한 것처럼, \(\langle\)digit\(\rangle\)이 나타내는 집합은 \(\{ “0”, “1”, “2”, “3”, “4”, “5”,
“6”, “7”, “8”, “9” \}\)이다.

\(\langle\)nat\(\rangle\)이 나타내는 문자열의 집합을 알아내는 데는 좀 더 고민이 필요하다. 먼저 쉽게
알 수 있는 사실은 \(\langle\)digit\(\rangle\)이 \(\langle\)nat\(\rangle\)을 정의하는 식 중 하나이므로 \(\langle\)digit\(\rangle\)이 나타내는
모든 문자열은 \(\langle\)nat\(\rangle\)이 나타내는 문자열이라는 것이다. 그러므로 \(\{ “0”, “1”, “2”,
“3”, “4”, “5”, “6”, “7”, “8”, “9” \}\)는 \(\langle\)nat\(\rangle\)이 나타내는 집합의 부분 집합이다.
동시에 \(\langle\)digit\(\rangle\) \(\langle\)nat\(\rangle\) 역시 \(\langle\)nat\(\rangle\)을 정의하는 식이다. 따라서 \(\langle\)digit\(\rangle\)이 나타내는
문자열 하나와 \(\langle\)nat\(\rangle\)이 나타내는 문자열 하나를 이어 붙여 만들어진 문자열은 \(\langle\)nat\(\rangle\)이
나타내는 집합의 원소이다. 예를 들면 “1”이 \(\langle\)digit\(\rangle\)이 나타내는 문자열이고 “0”이
\(\langle\)nat\(\rangle\)이 나타내는 문자열이므로 둘을 이어붙인 “10”은 \(\langle\)number\(\rangle\)가 나타내는
문자열이다. 이 과정을 반복하여 무수히 많은 문자열을 만들 수 있다. “1”과 “10”을
이어붙인 “110”도 \(\langle\)nat\(\rangle\)이 나타내는 문자열이고 “1”과 “110”을 이어붙인 “1110”도
\(\langle\)nat\(\rangle\)이 나타내는 문자열이다. 최종적으로는 \(\langle\)nat\(\rangle\)이 나타내는 집합이 ‘0’부터
‘9’까지의 문자로 이루어지고 길이가 양의 정수인 모든 문자열의 집합임을 알 수
있다.

\[\langle nat\rangle = \{ “0”, \cdots, “9”, “00”, \cdots, “99”, “000”, \cdots, “999”,
\cdots \}
= D^\ast \setminus \{ “” \} \]

(단, 여기서 \(D = \{ ‘0’, ‘1’, ‘2’, ‘3’, ‘4’, ‘5’, ‘6’, ‘7’, ‘8’, ‘9’ \}\).)
그러므로 \(\langle\)nat\(\rangle\)은 십진 자연수를 나타내는 문자열의 집합이다.

\(\langle\)nat\(\rangle\)이 나타내는 집합을 알았으니 \(\langle\)number\(\rangle\)가 나타내는 집합은 어렵지 않게 찾을 수
있다. 먼저, \(\langle\)nat\(\rangle\)이 \(\langle\)number\(\rangle\)를 정의하는 두 식 중 하나이다. 그러므로 \(\langle\)nat\(\rangle\)이
나타내는 모든 문자열이 \(\langle\)number\(\rangle\)가 나타내는 문자열이다. \(\langle\)number\(\rangle\)를 정의하는
나머지 식 하나는 “-” \(\langle\)nat\(\rangle\)이다. \(\langle\)nat\(\rangle\)이 나타내는 문자열의 앞에 “-”를 붙여
만들어진 문자열 역시 \(\langle\)number\(\rangle\)가 나타내는 문자열이라는 것이다. 따라서 \(\langle\)number\(\rangle\)가
나타내는 집합에는 \(\langle\)nat\(\rangle\)의 원소와 \(\langle\)nat\(\rangle\)의 원소 앞에 “-”가 붙은 문자열이 포함된다.

\[\langle number\rangle = \{ “0”, \cdots, “9”, “00”, \cdots, “99”, \cdots, “-0”, \cdots, “-9”, “-00”, \cdots,
“-99” , \cdots \}\]
\(\langle\)number\(\rangle\)는 십진 정수를 나타내는 문자열의 집합임을 알 수 있다.

이제 여기에 덧셈과 뺄셈만 추가하면 `산술`의 구체적 문법이 완성된다.
\(\langle\)expr\(\rangle\) ::= \(\langle\)number\(\rangle\) | \(\langle\)expr\(\rangle\) “+” \(\langle\)expr\(\rangle\) | \(\langle\)expr\(\rangle\) “-” \(\langle\)expr\(\rangle\)
우선 \(\langle\)number\(\rangle\)가 나타내는 모든 문자열은 \(\langle\)expr\(\rangle\)가 나타내는 문자열이다. 그러므로
“1”, “-30”, “42” 등은 모두 \(\langle\)expr\(\rangle\)의 원소이다. 또한, \(\langle\)expr\(\rangle\) “+” \(\langle\)expr\(\rangle\)가 \(\langle\)expr\(\rangle\)를
정의하는 식 중 하나이므로 “1+42”가 \(\langle\)expr\(\rangle\)의 원소라는 사실을 알 수 있다. 이와
같은 과정을 반복하면 \(\langle\)expr\(\rangle\) 집합의 원소를 모두 만들 수 있다. “1+-2+3”,
“42-01+00-100” 등의 문자열이 \(\langle\)expr\(\rangle\) 집합의 원소이다. 따라서 \(\langle\)expr\(\rangle\)는 십진 정수,
덧셈, 뺄셈으로만 구성된 산술식을 표현하는 문자열의 집합으로, `산술`의 구체적
문법을 정의한다. 정의를 수정하여 곱셈이나 나눗셈 등의 다른 연산을 추가하는 것은
쉽게 할 수 있을 것이다. 여기에서는 언어를 간단하게 만들기 위해 덧셈과 뺄셈만
포함하였다.

많은 언어의 명세에서 구체적 문법을 정의하기 위해 배커스-나우르 형식을 사용한다.
배커스-나우르 형식을 잘 이해하고 있으면 언어의 명세로부터 구체적 문법을 어렵지
않게 알아낼 수 있다. 단, 배커스-나우르 형식은 다양한 형태의 확장이 있으며
사용하는 사람마다 표기 방법도 조금씩 다르다. 다음은 파이썬 3.8.5의 명세 중
일부를 발췌한 것이다.[^1]

[^1]: The Python Language Reference 3.8.5, 6.7 Binary arithmetic operations,
[https://docs.python.org/3/reference/expressions.html](https://docs.python.org/3/reference/expressions.html)

> a_expr ::= m_expr | a_expr “+” m_expr | a_expr “-” m_expr

앞에서 본 것과 달리 비말단 기호를 표시하는 데 홑화살괄호가 사용되지 않았다는
차이가 있다. m_expr의 정의를 생략하기는 했지만 “1”이 m_expr의 원소라는 사실을
알고 있다고 가정하자. 그러면 “1”은 a_expr의 원소이기도 하다. 그러므로 “1+1”은
a_expr의 원소이다. 한편 “1+”는 a_expr의 원소가 아니다. 물론 “1+1”은 파이썬
프로그램이지만 “1+”는 파이썬 프로그램이 아니라는 결론을 내려면 더 많은 정의를
확인해야 할 것이다. 그래도 위의 예시로부터 배커스-나우르 형식이 실제 언어의
명세에 사용되고 있다는 사실을 수 있다. 또, 언어의 명세로부터 언어의 구체적
문법을 이해하는 방법도 잘 보여 준다.
