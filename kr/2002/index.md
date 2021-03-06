**아래 내용은 인사이트 출판사의 제안으로 작성 중인 책의 초고입니다. 실제 출판
시에는 내용이 달라질 수 있습니다. 많은 의견 부탁드립니다.**

### 프로그래밍 언어란?

프로그래밍 언어란 무엇일까?

이 질문을 듣는다면 “프로그래밍을 하기 위해 사용하는 언어이다”라고 답할 수 있을
것이다. 하지만 이래서야 프로그래밍 언어가 무엇인지 알 수 없다. 질문을 약간
바꾸어 보자.

프로그래밍 언어는 무엇으로 구성되어 있을까?

이 질문에는 더 좋은 답을 할 수 있다. “작게 보면 문법(syntax)과 의미(semantics)로
이루어지고 크게 보면 문법과 의미에 더해 표준 라이브러리와 생태계로 이루어진다.”

문법과 의미는 한 프로그래밍 언어를 이해하는 데 있어 가장 기본적인 요소이다.
문법은 한 언어의 겉모습을 결정하고 의미는 그 속을 채운다. 사람으로 비유하자면
문법은 외모이고 의미는 생각이라고 말할 수도 있겠다. 프로그래머는 문법에 따라
코딩한다. 프로그래머가 작성한 코드를 눈으로 쓱 봤을 때 어떻게 보이는지는, 다시
말해 어떤 글자들이 어떻게 나열되어 있는지는, 전적으로 사용한 언어의 문법에
달려있다. 그래서 문법이 언어의 겉모습을 정한다는 것이다. 그렇게 해서 작성된
코드가 있을 때, 그 코드를 실행하면 무슨 일이 일어날지 정하는 것은 언어의 의미가
하는 역할이다. 의미가 정의되지 않은 코드는 그저 속 빈 껍데기이다. 의미가 그 속을
채워 줘야 프로그램으로서의 일을 하는 온전한 코드가 된다. 어떤 언어에 문법과
의미만 있다면 그 언어를 얼마든지 사용할 수 있다. 문법에 따라 코드를 짜면 의미에
따라 그 코드가 실행된다. 그렇기에 학문적인 관점에서는 프로그래밍 언어가 문법과
의미로만 이루어진다고 말해도 무리가 없다. 즉, 프로그래밍 언어는 “작게 보면
문법과 의미로 이루어진다.”

프로그래밍 언어 사용자의 입장에서 보면 문법과 의미 말고도 실용적으로 중요한
요소들이 있다. 첫 번째는 표준 라이브러리이다. 리스트나 맵 같은 중요한 자료
구조들, 파일 및 네트워크 입출력을 수행하는 함수들, 날짜와 시간의 처리 같은 여러
응용 프로그램이 필요로 하는 기능들, 이 밖에도 여러 가지 기능을 제공하는 것이
표준 라이브러리의 역할이다. 사람으로 치면 표준 라이브러리는 옷과 같다. 옷이
없어도 사람은 사람이고 표준 라이브러리가 없어도 언어는 언어이다. 하지만 옷은
사람에게 아주 중요하다. 몸의 체온을 유지해 주고 몸 밖의 위험한 물건으로부터 몸을
보호해 준다. 마찬가지로 표준 라이브러리도 아주 중요하다. 사람이 사는 데 있어
필수적인 일들을 옷이 해 주는 것처럼 프로그램을 만드는 데 있어 꼭 필요할 만한
기능들을 표준 라이브러리가 제공한다. 사람마다 입는 옷이 조금씩 다른 것처럼
언어마다 표준 라이브러리에 들어 있는 내용물이 조금씩 다르기는 하다. 얼마나
다양한 편의 기능을 표준 라이브러리에 집어넣을지 언어 설계자마다 생각이 다를 수
있다. 또, 문자열이나 리스트 같은 아주 기본적이고 중요한 개념을 의미에 이미
포함하고 있는 언어도 있고, 의미에는 넣지 않았지만 표준 라이브러리를 통해
지원하는 언어도 있다. 표준 라이브러리가 없거나 부실한 언어는 사용자 입장에서
선택하기 꺼려질 수밖에 없다. 다른 언어를 사용한다면 표준 라이브러리만 사용해도
단숨에 끝날 일을 시간 들여 처음부터 하나하나 처리해야 할 테니 말이다.

사용자 입장에서 중요한 프로그래밍 언어의 또 다른 요소는 언어의 생태계이다.
여기서 생태계라는 것은 한 언어에 관련된 모든 것을 포함한다. 그 언어를 사용하는
개발자와 회사, 그 언어로 만들어진 서드 파티 라이브러리 등이 그 예시이다.
사람으로 치면 마치 주변의 모든 것, 즉 인간관계나 사회 같은 역할이다. 언어를
사용하는 개발자가 많아야 코딩하다 막혔을 때 물어 보거나 검색해서 답을 찾기 쉬울
것이다. 또, 협업 기회도 더 많아진다. 많은 회사에서 사용하는 언어를 다룰 수
있다면 일자리를 구하는 데도 도움이 될 것이다. 다양한 서드 파티 라이브러리의 존재
여부도 중요하다. 표준 라이브러리는 대부분의 응용 프로그램에서 공통적으로 필요할
만한 기능만을 제공하다 보니 특정 목적을 위한 기능은 없을 수 있다. 그래서 큰
프로그램을 만들다 보면 표준 라이브러리가 부족하게 느껴질 때가 많다. 이럴 때
원하는 기능을 딱 제공하는 서드 파티 라이브러리를 찾을 수 있다면 모든 기능을 직접
만드는 수고를 덜 수 있다. 이처럼 언어의 생태계는 개발자들이 사용할 언어를 정하는
데 중요한 고려 대상이다.

생태계의 중요성을 잘 보여 주는 한 예시는 파이썬(Python)이다. 파이썬은 최근 기계
학습 분야에서 널리 사용되고 있다(물론 다른 분야에서도 널리 사용된다). 파이썬
언어 자체가, 다시 말해 파이썬의 문법과 의미와 표준 라이브러리가, 특별히 잘
설계되어 있거나 기계 학습 분야에 다른 언어보다 적합하다고 보기는 어렵다. 그러나
파이썬의 생태계는 충분히 매력적이다. 이미 수많은 개발자가 기계 학습을 위해
파이썬을 사용하고 있고, 여러 기계 학습용 파이썬 서드 파티 라이브러리가 존재한다.
파이썬을 사용한다면 막혀도 쉽게 해결법을 찾을 수 있다. 또, 기계 학습이 적용된
응용 프로그램을 복잡한 수학 이론을 공부할 필요 없이 바로 만들 수 있다. 이런
생태계가 갖추어져 있으니 새로이 기계 학습을 공부하는 개발자는 파이썬을 선택할
수밖에 없다.

이처럼 표준 라이브러리와 생태계는 실용적인 관점에서 보았을 때 언어의 중요한 구성
요소이다. 문법과 의미가 없다면 프로그래밍 언어가 만들어지지 않는다. 그에 비해
표준 라이브러리와 생태계가 없어도 언어는 언어이다. 그러다 보니 이론적 논의에서는
대개 표준 라이브러리와 생태계가 다루는 대상에서 빠지곤 한다. 그러나 프로그래밍
언어를 사용하는 개발자의 입장에서는 사용할 언어를 정하는 데 있어 표준
라이브러리와 생태계가 문법과 의미만큼이나 중요한 고려 대상이다. 즉, 프로그래밍
언어는 “크게 보면 문법과 의미에 더해 표준 라이브러리와 생태계로 이루어진다.”

### 책의 목표

이 책은 어떤 한 프로그래밍 언어를 정하고 그 언어를 잘 사용하도록 도와주는 책이
아니다. 공부하면 좋은, 알아 두어야만 하는 언어를 추천해 주는 책은 더더욱 아니다.
이 책은 독자가 새로운 프로그래밍 언어를 사용하게 되었을 때 쉽게 익힐 수 있게
돕는 책이다. 이 책 한 권만 읽는다면 몇 개의 언어이든 부담 없이 익힐 수 있다는
주장을 하는 것이다. 이른바 “하나를 배우면 열을 알 수 있게끔” 해 주는 책이다.
각각의 언어를 따로따로 살펴보아서는 그것이 가능할 리가 없다. 모든 언어의 기저에
있는, 모든 언어를 관통하는 핵심 원리를 다루어야만 한다.

프로그래밍 언어의 핵심 원리는 의미로부터 찾을 수 있다. 여러 프로그래밍 언어가
서로 굉장히 달라 보이지만 사실은 그렇지 않다. 조금 더 정확히 말하자면, 겉은 서로
굉장히 다르지만 사실 그 속은 거기서 거기이다. 언어가 달라 보이는 이유는 문법과
표준 라이브러리처럼 겉으로 보이는 요소들의 차이가 크기 때문이다. 겉으로 쓱 봤을
때 엄청 달라 보이니 그 속도 서로 전혀 다를 것이라고 지레짐작하게 되는 것이다.
하지만 실상은 다르다. 언어의 속, 즉 언어의 의미는 기본적으로 동일한 수학적
원리를 공유하고 있다. 모든 언어의 의미가 완전히 같다고 말할 수는 없지만, 그
겉모습이 다른 것에 비하면 의미의 차이는 그저 사소한 수준이다. 그러므로 여러
언어의 의미에서 공통적으로 등장하는 개념들만 모두 이해한다면 새로운 언어를 배울
때 의미를 이해하느라 어려움을 겪을 이유가 없다.

핵심 원리를 이해했고 프로그래밍 언어의 각 구성 요소를 분리해서 생각할 수 있다면
새로운 언어를 배우는 일은 그전보다 비교도 안 되게 쉬워진다. 구성 요소를 구분하는
것은 중요한 일이다. 예를 들자면, 어떤 사람이 컴퓨터를 배우고 있다고 생각해 보자.
이 사람이 키보드와 컴퓨터를 분리해서 바라볼 수 없다면 큰 문제가 생긴다. 예를
들면, “안녕”이라는 메시지를 친구에게 보내고 싶으면 “왼손 중지로 키보드를 누르고,
오른손 중지로 키보드를 누르고, 왼손 약지로 키보드를 두 번 누르고, 오른손 검지를
위로 움직인 다음 키보드를 누르고, 다시 왼손 중지로 키보드를 누른 뒤, 오른손
소지를 오른쪽으로 많이 움직여 키보드를 누르면 된다”고 기억하는 것이다. 이렇게
컴퓨터를 이해한 사람은 자판 배열이 두벌식에서 세벌식으로 바뀌고 나면 키보드만
바뀌었을 뿐이지만 컴퓨터를 모르는 사람이 되어 버린다. 하지만 키보드는 단순히
입력 수단일 뿐이고, “안녕”이라는 메시지를 보내려면 “ㅇ, ㅏ, ㄴ, ㄴ, ㅕ, ㅇ을
차례로 입력한 뒤 줄 바꿈 키를 누르면 된다”고 기억한 사람은 자판 배열이 바뀌어도
큰 문제가 없다. 물론 ㄴ, ㅇ, ㅏ, ㅕ를 입력하려면 어떻게 손을 움직여야 하는지
다시 익혀야 하겠지만, 적어도 컴퓨터를 처음부터 다시 공부할 필요는 없다. 응용
능력도 더 뛰어날 것이다. “안녕”이 아니라 “여아”라는 메시지를 보내고 싶다면 ㅇ,
ㅕ, ㅇ, ㅏ를 차례로 입력하면 된다. 반면 키보드와 컴퓨터를 구분하지 못하는 사람은
각각의 손가락 움직임이 무엇을 위한 것인지 모른다. 따라서 “안녕”을 보내는 것과
“여아”를 보내는 것 사이의 어떤 공통 원리도 찾지 못할 것이다. 프로그래밍 언어를
공부하는 것도 마찬가지이다. 문법과 의미를 구분하지 못하는 사람은 문법만 바뀌었을
뿐인데도 모든 것을 새로 배워야 한다고 생각할 것이다. 그러나 문법과 의미를 구분할
수 있는 사람은 다르다. 문법이 전혀 달라졌어도 의미까지 달라진 것은 아니라는 것을
안다. 여기에 더해 여러 언어의 의미 속 공통 원리를 이해하고 있다면 완벽하다. 비록
문법은 다르지만, 의미는 거의 다 아는 내용이라는 것을 알고 있는 것이다. 문법을
익히고 의미에서 약간의 새로운 내용만 공부하고 나면 그 언어를 사용해 곧바로 많은
일들을 할 수 있다.

### 책의 구성

그러므로 이 책은 대부분 언어의 의미에서 찾아볼 수 있는 개념들을 설명한다. 먼저
2장에서는 문법과 의미가 무엇인지 자세히 설명한다. 즉, 2장은 프로그래밍 언어의
구성 요소를 이해하기 위한 장이다. 표준 라이브러리와 생태계는 어려운 개념도
아니고 문법과 의미로부터 명확히 구분되기 때문에 문법과 의미의 이해 및 구분에만
집중한다. 그 뒤로 3장부터 13장까지의 각 장은 하나의 개념을 주제로 그 개념에 대해
설명한다. 그중에서도 9장까지는 1부에 속하며, 실용적으로 사용되는 모든 프로그래밍
언어에 등장하는 개념들을 다룬다. 따라서 1부의 내용은 어느 언어를 배우든 꼭
필요한 내용이라고 볼 수 있다. 1부의 내용은 순서대로 차근차근 읽을 것을 추천한다.
10장부터 13장까지는 2부에 속하며, 중요하지만 일부 언어에만 존재하는 개념들을
다룬다. 비록 모든 언어에 존재하지는 않지만 여러 언어에서 공통적으로 볼 수 있는
개념들이다. 그렇기에 필요하다고 생각되는 장만 골라서 읽어도 무방하다. 마지막으로
14장에서는 13장까지에서 다루지 못한 흥미로운 주제들을 간략히 소개한다.

3장부터 13장까지의 각 장은 대체로 비슷한 구조로 되어 있다. 우선 그 장에서 다루려
하는 개념을 소개하는 예시 코드를 보여 준다. 기본적으로 파이썬으로 작성된 예시
코드를 사용하되 필요에 따라 다른 언어를 사용할 수 있다. 파이썬을 선택한 이유는
대부분의 독자가 공통적으로 사용해 보았을 만한 언어가 파이썬이라 판단했기
때문이다. 파이썬을 전혀 몰라도 읽는 데 지장이 없도록 예시에 대해 설명하기에
파이썬을 경험해 보지 않은 상태로 책을 읽어도 괜찮다.

예시 코드를 통해 개념을 도입한 후에는 그 개념을 포함하는 아주 작은 가상의 언어를
설계하는 과정을 통해 개념의 의미를 자세히 설명한다. 사실 언어의 의미를 정의하는
작업은 굉장히 수학적인 작업이다. 예를 들면, 하나의 프로그램은 주어진 입력에 대해
어떤 출력을 내므로 수학에서 이야기하는 함수로 볼 수 있다. 그러므로 언어의 의미를
정의하는 것은 그 언어로 작성한 코드가 어떤 함수를 나타내는지 정의하는 것이기도
하다. 이 함수를 엄밀하게 정의하기 위해 여러 수학적 도구가 필요하다. 이런 이유로,
원래는 언어의 의미를 정의하는 데 수학 개념과 표기법이 많이 등장한다. 그러나
독자들 중에는 수학이 친숙하지 않은 사람도 있을 것이기에 수학적인 개념을 최소화한
채로 의미를 설명한다.

각 장의 마지막에는 “현실 세계 언어 파헤치기”라는 제목의 절이 있다. “현실 세계
언어 파헤치기”에서는 그 장에서 다룬 개념이 현실 세계에서 사용되고 있는 여러
언어에 어떻게 적용되어 있는지 알아본다. 그 장에서 본 의미에 대한 설명이 완전히
이해되지 않은 경우, “현실 세계 언어 파헤치기”에서 실제 언어로 작성된 여러 예시
코드를 보면서 이해에 도움을 받을 수 있을 것이다. 똑같은 개념이 얼마나 많은
언어에 들어가 있는지, 또 각 언어마다 같은 개념에 어떤 차이점을 추가했는지
살펴보는 것도 재미있을 것이다.

“현실 세계 언어 파헤치기”에서 주로 살펴볼 언어는 파이썬,
자바스크립트(JavaScript), C, 자바(Java)이다. 파이썬과 자바스크립트는 스크립트
언어를, C는 명령형 언어와 시스템 프로그래밍 언어를, 자바는 객체 지향 언어를
대표한다고 보면 된다. 이 언어들 외에도 필요에 따라 다른 언어를 다루는 경우도
있다. 파이썬은 파이썬 3.9, 자바스크립트는 ECMA스크립트(ECMAScript) 2020, C는
C18, 자바는 자바 14를 기준으로 작성할 것이다. 판을 명시하기는 했지만, 책에서
다루는 개념은 워낙 기본적인 개념인 만큼 언어가 처음 만들어질 때부터 존재했고
새로운 판이 나와도 거의 바뀌지 않는 기능이다. 따라서 판에 따른 차이는 거의 없을
것이다.

대부분의 프로그래밍 언어에 대한 책들이 하나의 언어에 집중하는 것과 달리 이 책은
여러 언어의 밑바탕에 있는 원리를 다룬다. 실제 언어로 작성된 예시 코드도
제공하겠지만, 아무래도 한 언어만을 다루는 책보다는 예시 코드의 수가 적을 수밖에
없다. 또, 많은 책들이 예시 코드 및 실행 결과, 그리고 자연어로 된 설명 등을 통해
직관적으로 언어의 의미를 설명하는 것에 비해, 이 책에서는 한 단계 더 나아가,
언어의 의미에 애매모호함이 없도록 엄밀한 정의를 제시한다. 이런 특징들로 인해
책의 앞부분을 읽는 동안은 내용이 잘 와닿지 않을 수 있다. 그러나 프로그래밍
언어의 원리를 정확히 파악하려는 노력이 있어야 여러 언어를 쉽게 익힐 수 있다.
찬찬히 책의 내용을 따라가다 보면 어느새 어떤 언어를 만나도 적용할 수 있는 기본
원리가 머릿속에 자리 잡을 것이다. 그럼 이제 프로그래밍 언어의 원리를 찾는 여정을
시작해 보자.
