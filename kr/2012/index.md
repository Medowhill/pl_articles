**아래 내용은 인사이트 출판사의 제안으로 작성 중인 책의 초고입니다. 실제 출판
시에는 내용이 달라질 수 있습니다. 많은 의견 부탁드립니다.**

`산술`의 모든 식들은 계산하는 데 아무 문제가 없었다. \((2 + 3) - (4 – 1)\)처럼
단순히 정수, 덧셈, 뺄셈으로만 이루어진 식은 계산을 실패할 여지가 없다.

그러나 `산술x`의 경우 계산할 수 없는 식이 존재한다. 변수가 언어에 추가되면서
문제가 생긴 것이다. 물론 변수를 제대로 사용했다면 계산 가능한 식이 만들어진다.
예를 들어, \({\tt x} = 3; {\tt x}\)처럼 사용하고자 하는 변수를 선언한 뒤 그 변수를 사용하는 식은
환경에서 변수의 값을 찾을 수 있기 때문에 계산이 가능하다. 그러나 앞서 본 것처럼
\(\tt x\) 같은 식은 변수를 정의하지 않고 사용하므로 그 변수의 값을 찾을 수 없다. 즉, \(\tt x\)는
계산할 수 없는 식이다.

`산술x`의 의미를 바탕으로 계산할 수 없는 식이 무엇인지에 대해 조금 더 정확하게
설명할 수 있다. `산술x`의 의미는 식의 실행 결과를 정의한다. 의미를 정의하는 규칙을
통해 “\(\sigma\)에서 \(e\)의 실행 결과는 \(n\)이다”라는 결론을 얻을 수 있다. 식을 계산하기
시작할 때는 환경이 비어 있으므로, 어떤 식 \(e\)가 있을 때 “\([\ ]\)에서 \(e\)의 실행 결과는
\(n\)이다”라는 결론을 얻었다면, \(e\)는 계산 가능한 식이며 그 결과는 \(n\)이라고 말하면
된다. 계산할 수 없는 식은 그 반대에 해당한다. “\([\ ]\)에서 \(e\)의 실행 결과는 \(n\)이다”라는
결론을 내릴 수 없는 정수 \(n\)이 존재하지 않는다면 식 \(e\)는 계산할 수 없는 식이다. 식
\({\tt x}\)를 예시로 생각해 보자. 어떤 정수 \(n\)이 있을 때, 규칙 4에 따라 \([\ ]\)에 \(\tt x\)의 값이
\(n\)이라는 정보가 있으면, \([\ ]\)에서 \(\tt x\)의 실행 결과가 \(n\)이다. 그러나 \([\ ]\)는 빈 환경이므로
\(n\)이 어떤 정수이든 상관없이 \([\ ]\)에는 \(\tt x\)의 값이 \(n\)이라는 정보가 없다. 그러므로 \(n\)의
값이 무엇이든 “\([\ ]\)에서 \(\tt x\)의 실행 결과는 \(n\)이다”라는 결론을 내릴 수 없으며 \(\tt x\)는
계산할 수 없는 식이다.

계산할 수 없는 식을 프로그래밍을 할 때 나오는 개념으로 다시 표현하면, 계산할 수
없는 식은 실행 시간 오류(runtime error)를 일으키는 프로그램이다. 예를 들어,
파이썬 프로그램 `x`를 실행하면 “NameError: name 'x' is not defined”라는 오류
문구와 함께 실행이 종료된다. 이처럼 프로그램이 결과를 내지 못하고 실행되는
도중에 오류로 인해 종료된 경우, 그 오류를 실행 시간 오류라 부른다. 실행 시간
오류는 2장에서 보았던 문법 오류와는 분명히 구분되는 개념임에 주의해야 한다. 문법
오류는 입력된 문자열이 프로그램조차 아닐 때 발생하는 오류이다. 프로그램이 아니니
실행할 수 있을 리 없다. 반면, 실행 시간 오류는 입력된 문자열이 프로그램은 맞지만
실행하던 도중 더 이상 실행할 수 없어 실행을 비정상적으로 중단해야 할 때 발생하는
오류이다. 여기에서 “실행 시간 오류는 입력된 문자열이 프로그램은 맞지만”이라는
부분에 특히 유의하기 바란다. 계산할 수 없는 식은 계산할 수 없을 뿐, 식이 아닌
것은 아니다. 계산할 수 없는 식도 요약 문법이 정의한 나무 구조를 따르는 식이다.
다만, 실행 시 오류를 일으키는, 계산할 수 없는 식인 것이다.

`산술x`에서 계산할 수 없는 식은 오직 자유 변수를 가지고 있는 식 뿐이다. 그러나
앞으로 언어에 새로운 기능을 계속 추가하면서 더 많은 종류의 계산할 수 없는 식을
보게 될 것이다. 언어에 다양한 기능이 추가될수록, 실행 시간 오류가 발생할 이유가
늘어난다. 언어가 커질수록 계산할 수 없는 식을 작성할 가능성이 커지는 것이다.
프로그램을 작성하는 이유는 실행하기 위함이라는 점을 생각해 보면, 실행 중에
오류를 일으키는 프로그램은 만들 필요가 없다. 프로그래밍을 해 본 사람이라면
누구나 프로그램을 잘못 작성해서 프로그램이 실행 중 오류를 내뱉으며 종료되는 것을
경험해 보았을 것이다. 또, 오류를 일으키는 프로그램을 올바르게 고치기 위한 디버깅
과정이 얼마나 어렵고 오래 걸리는 일인지도 알 것이다. 이런 ‘잘못된’ 프로그램에
대처하는 가장 이상적인 방법은 프로그램이 오류를 일으키는지 확인하는 어떤 일련의
과정을 만드는 것이다. 프로그램의 오류를 미리 확인해 실행 시간 오류를 원천
봉쇄하는 방법에 대해서는 13장에서 다룰 것이다.
