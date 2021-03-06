**아래 내용은 인사이트 출판사의 제안으로 작성 중인 책의 초고입니다. 실제 출판 시에는 내용이 달라질 수 있습니다. 많은 의견 부탁드립니다.**

변수(variable)는 값에 이름을 붙인 것이다. 지금부터 임의의 이름을 x라고 쓰겠다. 변수 정의를 통해 새로운 변수를 만들 수 있다.

* 변수 정의
  - 문법: `let x = e`
  - 의미: e를 계산한다. e의 결과가 x가 나타내는 값이 된다. ()이 최종 결과이다.

let x = e에서 x는 정의하는 변수의 이름이며 e는 그 변수의 값을 결정하는 식이다.

```
let a = 2 + 4
```

이 변수 정의에 의해 정의된 변수의 이름은 a이며 그 값은 2 + 4를 계산해 얻은 6이다. 변수 정의는 새로운 변수를 정의하는 역할을 하지, 그 자체로 어떤 결과를 내는 것은 아니다. 변수 정의의 결과에는 아무런 정보도 없으므로 결과를 유닛으로 표현했다.

변수를 정의했다면 그 변수를 그 이후의 계산에서 사용할 수 있다. 변수를 사용하는 가장 간단한 식은 변수 이름 그 자체이다.

* 변수 사용
  - 문법: `x`
  - 의미: x가 나타내는 값이 결과이다.

이름은 a이고 값은 6인 변수가 정의된 상태에서 식 a를 계산한 결과는 6이다. 다른 모든 식과 마찬가지로 이 형태의 식 역시 다른 식을 구성하는 데 사용될 수 있다. 따라서 if true (a + 1) 3 역시 식이며 그 값은 6과 1의 합인 7이다.

지금으로서는 변수를 정의한 다음에 다른 계산을 하는 식을 작성할 수 없다. 이 문제는 새로운 형태의 식 하나만 도입하면 쉽게 해결된다.

* 식 나열
  - 문법: `e1; e2; …; en`
  - 의미: e1부터 en까지 차례대로 계산한다. 이때 e1이 변수 정의나 함수 정의 따위이면 정의한 대상을 e2에서 en까지 계산하는 동안 사용할 수 있다. e2나 e3 등에서 정의한 대상도 비슷하게 이후의 식에서 사용할 수 있다. en의 결과가 최종 결과이다.

```
let a = 6;
a
```

위 식의 결과는 6이다.

이미 정의한 변수에 새로운 값을 대입하여 그 변수가 나타내는 값을 수정할 수 있다.

* 대입
  - 문법: `x := e`
  - 의미: e를 계산한다. e의 결과가 x가 나타내는 새로운 값이 된다. ()이 최종 결과이다.

대입은 변수가 나타내는 값을 바꿀 뿐 그 자체로 의미 있는 결과를 내지는 않는다. 그래서 대입의 결과는 유닛이다.

```
let a = 3;
a := a + 3;
a
```

위 식의 결과는 6이다. 처음 a가 정의되었을 때 그 값은 3이지만, a + 3을 계산해 얻은 6을 a에 대입함으로써 a의 값이 6으로 바뀌었다. 마지막으로 a를 계산하므로 최종 결과가 6이다.

프로그램에서 수정할 수 있는 대상은 변수뿐이 아니다. 뒤에서 볼 리스트와 맵 등 다양한 대상을 수정할 수 있다. 그러나 변수를 수정하는 것과 큰 차이가 없기 때문에 변수 이외의 대상을 수정하는 것을 따로 더 설명하지는 않겠다.
