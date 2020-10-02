이번 글은 함수형 프로그래밍에 대한 두 번째 글이다. 지난 글에서는 불변성의 장점, 재귀, 함수형 리스트, 꼬리 호출 최적화에 대해서 알아보았다. 이번 글에서는 함수형 프로그래밍의 함수 그 자체에 대해서 다룰 것이다. *일급* *함수*(first-class function)란 무엇인지와 Scala에서 *익명* *함수*(anonymous function)를 정의하는 방법을 알아보고, 지난 글에서 정의한 리스트를 다루는 함수들을 일급 함수를 통해서 어떻게 일반화할 수 있는지 볼 것이다. 또, 함수형으로 예외 상황을 처리할 수 있게 해주는 *옵션*(option) 타입에 대해서도 알아볼 예정이다. 이번 글은 2018년 가을 학기에 진행한 Scala 세미나 중 두 번째 세미나인 "First-Class Functions" 내용을 바탕으로 작성하였다. 세미나 [자료](https://hjaem.info/files/scala/18f/2_fcf.pdf), [코드](https://hjaem.info/files/scala/18f/2_fcf.zip), [영상](https://youtu.be/5JhkobMgAj0) 모두 온라인으로 이용 가능하니 필요한 사람은 참고하길 바란다.

## 일급 함수

프로그래밍 언어에서 어떤 대상이 일급이라는 것은 그 대상이 다음 세 가지 조건을 만족한다는 것을 의미한다.

1. 함수 호출의 인자로 사용될 수 있다.
2. 함수의 결괏값으로 사용될 수 있다.
3. 대입 연산을 통해서 변수가 가리키는 대상으로 지정될 수 있다.

간단히 말하면, 그 대상이 값으로서 사용될 수 있다는 말이기도 하다. 함수형 언어에서 함수는 매우 중요한 위치를 차지하고, 값으로 사용될 수 있는 것은 자연스러운 일이다. Scala에서도 함수는 일급이다.

```scala
def f(x: Int): Int = x
def g(h: Int => Int): Int = h(0)
g(f)
```

위 코드에서 함수 `g`는 한 개의 매개변수 `h`를 가지고 있고 `h`의 타입은 `Int => Int`이다. 이는 매개변수 타입이 `Int`이고 결과 타입이 `Int`인 함수를 인자로 받겠다는 의미이다. 보다시피 Scala에서 함수의 타입은 `=>`를 사용해서 표현한다. 매개변수가 없는 경우에는 `() => [결과 타입]`, 하나인 경우에는 `[매개변수 타입] => [결과 타입]`으로 표현하며, 두 개 이상인 경우에는 괄호로 매개변수들의 타입을 묶어서 `([매개변수 타입], ...) => [결과 타입]` 형태로 표현한다. 함수 `f`는 한 개의 매개변수를 가지고 그 타입은 `Int`이며 결과 타입도 `Int`이므로, `Int => Int` 타입을 가지며 `g`의 인자로 사용될 수 있다. `g(f)`는 `f(0)`을 계산한 것과 동일하므로 결괏값은 `0`이다.

```scala
def f(y: Int): Int => Int = {
  def g(x: Int): Int = x
  g
}
f(0)(0)
```

이번에는 함수가 함수의 결괏값으로 사용된 예시이다. 함수 `f`의 결과 타입이 `Int => Int`로 명시되어 있으므로, Int 값을 인자로 받아 Int 값을 결과로 내는 함수가 `f`의 결괏값이어야 한다. `g`가 그 조건을 만족하는 것을 볼 수 있다. `f(0)`는 함수 `f`를 호출하여 결과로 나온 함수이므로 `g`와 같고 다시 그 함수를 바로 호출할 수 있다. `f(0)(0)`은 `g(0)`과 같으므로 `0`을 결괏값으로 가진다.

```scala
val h0 = f(0)
h0(0)
```

`f(0)`을 호출하는 대신 변수가 가리키는 대상으로 지정할 수도 있다. 이 경우 `h0`은 `f(0)`의 결괏값을 가리키므로 그 타입은 `Int => Int`이다. 함숫값을 가리키는 변수도 `def`를 통해서 선언한 함수의 이름과 마찬가지로 함수 호출의 대상이 될 수 있다. 따라서 `h0(0)` 역시 올바른 식이며, `g(0)`과 같으므로 `0`이 결괏값이다.

```scala
val h1 = f
         ^
error: missing argument list for method f

Unapplied methods are only converted to functions
when a function type is expected.

You can make this conversion explicit
by writing `f _` or `f(_)` instead of `f`.
```

반면 `f`를 바로 변수가 가리키는 대상으로 지정하려 하면 컴파일 오류가 발생한다. Scala에서 `def`를 사용하여 정의한 함수는 그 자체로는 값이 아니다. `f`는 함수의 이름일 뿐 어떤 값을 가리키는 변수는 아니기에 `h1`이 `f`가 가리키는 값을 가리킬 수 없다. 함수 이름을 함숫값으로 만들기 위해서는 위의 오류 문구에서도 볼 수 있듯 밑줄 문자를 사용해야 한다.

```scala
val h1 = f _
h1(0)(0)
```

위 코드는 올바르게 컴파일된다. `h1`의 타입은 `Int => (Int => Int)`이며, 함수 타입은 일반적으로 오른쪽 결합(right-associative)을 하는 것으로 간주하므로 `Int => Int => Int`라고 표현할 수 있다. `h1(0)(0)`은 올바른 식이며, `g(0)`과 같으므로 `0`이 결괏값이다.

그러나, `val h1 = f` 전까지는 계속 함수 이름을 함숫값처럼 사용할 수 있었다. 이는 Scala에서 함수 이름이 함수 타입이 기대되는 자리에 오면 자동으로 함수 이름을 함숫값으로 바꾸기 때문이다. 따라서, `h1`의 타입을 함수 타입으로 강제하면 밑줄 문자 없이도 코드를 올바르게 만들 수 있다. 다음 코드는 밑줄 문자를 사용한 코드처럼 잘 작동한다.

```scala
val h1: Int => Int => Int = f
h1(0)(0)
```

함수 이름을 값처럼 사용하는 경우, 보통은 함수 이름이 함수 타입이 기대되는 자리에 온다. 그렇기에 밑줄 문자나 추가로 명시한 타입 없이도 코딩할 수 있다. 드물게 문제가 되는 경우에는 위에서 한 것처럼 코드를 수정하여 함수 이름이 함숫값으로 변환되도록 강제하면 된다.

Scala는 함수 이름을 어떻게 함숫값으로 바꿀까? 함수 `f`의 매개변수 타입이 `Int`면, `f`는 `(x: Int) => f(x)`로 바뀐다. 이를 *에타 확장*(eta expansion)이라 부르며, `(x: Int) => f(x)`는 `f`와 완전히 같은 일을 하는 이름 없는 함수이자 함숫값이다. 이름 없는 함수에 대해서 바로 다음 절에서 다룬다.

### 익명 함수

함수가 값으로 많이 사용되는 함수형 언어에서는, 함수가 다른 함수의 인자 또는 결괏값으로 단 한 번만 사용되는 경우가 흔하다. 이런 경우, 굳이 함수에 이름을 붙이는 작업이 불필요하게 느껴진다. 또한, 함수가 값이라는 측면에서 바라볼 때, 그 값이 의미하는 바는 함수가 어떻게 작동하는지이므로, 함숫값의 본질은 매개변수와 *몸통*(body) 식에 있지, 그 함숫값을 가리키는 이름에 있지 않다. 따라서, 이름 없이도 함수를 정의할 수 있는 방법을 언어가 지원하는 것이 자연스럽다. 이름 없는 함수를 익명 함수라고 부른다.

Scala에선 익명 함수는 `([매개변수 이름]: [매개변수 타입], ...) => 식` 형태로 정의한다. 중괄호로 여러 식을 묶은 것도 식이므로 `(...) => { ... }` 형태로 여러 개의 식을 몸통에 가지고 있는 익명 함수도 얼마든지 만들 수 있다. 익명 함수는 `def`를 통해 선언된 함수 이름과 마찬가지로, 바로 호출되거나 인자나 결괏값으로 사용되거나 어떤 변수가 가리키는 대상으로 지정될 수 있다.

```scala
((x: Int) => x)(0)
def g(h: Int => Int): Int = h(0)
g((x: Int) => x)
def f(): Int => Int = (x: Int) => x
val h = (x: Int) => x
```

앞서 이름을 가진 함수들로 했던 작업을 익명 함수를 사용해서 한 것이다.

익명 함수는 이름을 가진 함수와 마찬가지로 매개변수의 타입을 명시하는 것이 원칙이다. 그러나, 많은 경우에 익명 함수의 매개변수 타입을 일일이 써주는 것은 코드를 장황하게 만들기 때문에, Scala에서는 익명 함수가 나온 위치가 특정 함수 타입이 기대되는 곳인 경우 그 함수 타입을 바탕으로 매개변수 타입을 유추해준다.

```scala
def g(h: Int => Int): Int = h(0)
g(x => x)
```

`g`의 매개변수 타입이 `Int => Int`이므로 `g`의 인자로 사용된 `x => x`는 `Int => Int` 타입을 가질 것으로 기대되기에 `x`의 타입이 `Int`로 유추되어 타입을 명시하지 않아도 괜찮다.

```scala
def f0(): Int => Int = x => x
def f1() = (x: Int) => x
def f2() = x => x  // 컴파일 오류
```

`f0`는 결과 타입이 `Int => Int`로 명시되어 있어 `x => x`의 타입이 `Int => Int`로 기대되므로 `x`의 타입이 `Int`로 유추된다. `f1`의 경우 `(x: Int) => x`가 매개변수의 타입을 명시하였기 때문에 `Int => Int` 타입임을 알아낼 수 있고, `f1`의 결과 타입이 `Int => Int`로 유추된다. 그러나, `f2`의 경우에는 양쪽 다 명시하지 않았으므로 유추할 수 있는 타입이 없어서 컴파일 오류가 발생한다.

```scala
val h0: Int => Int = x => x
val h1 = (x: Int) => x
val h2 = x => x  // 컴파일 오류
```

앞선 예시와 마찬가지로 `h0`의 타입으로부터 `x => x`에서 `x`의 타입을 유추할 수 있고 `(x: Int) => x`의 타입으로부터 `h1`의 타입을 유추할 수 있으나, `h2`의 경우 컴파일 오류가 발생한다.

익명 함수를 사용하는 가장 흔한 경우는 함수의 인자로 사용하는 것이기 때문에, 대부분의 경우 익명 함수의 매개변수 타입을 생략할 수 있다. 하지만, 처음 Scala를 사용하는 경우에는 언제 타입을 생략할 수 있는지 헷갈릴 수 있으므로, 생략해도 되는지 확실하지 않은 경우에는 그냥 명시하는 편이 안전하다.

Scala에서는 *밑줄* *문자*(underscore)를 사용해서 익명 함수를 간단하게 정의하는 방법을 제공해서 코드의 가독성을 높인다. 밑줄 문자는 익명 함수의 의미를 직관적으로 이해할 수 있게 해주면서도 간결한 표현을 가능하게 하지만, 제한된 경우에만 사용할 수 있다. 밑줄 문자를 사용하기 위해서는 모든 매개변수가 함수 몸통에서 정확히 한 번씩 나타나야 하며, `(x: Int) => x` 같은 *항등함수*(identity function)는 아니어야 하고, 매개변수가 여러 개이면 매개변수가 순서대로 몸통에서 사용되어야 한다. 밑줄 문자의 사용 방법은 매개변수가 몸통에서 사용되는 자리에 매개변수 대신 밑줄 문자를 사용하는 것이다.

```scala
def g0(h: Int => Int): Int = h(0)
g0(_ + 1)

def g1(h: (Int, Int) => Int): Int = h(0, 0)
g1(_ + _)
```

`_ + 1`은 컴파일 과정에서 *문법* *설탕이* *제거*(desugar)되어 `x => x + 1`로 바뀌며, `_ + _`는 `(x, y) => x + y`로 바뀐다. 이처럼, 밑줄 문자의 개수만큼 자동으로 매개변수를 만든 뒤 밑줄 문자를 차례대로 매개변수로 *치환*(substitution)하여 익명 함수를 만들기 때문에 앞에서 말한 제한 조건이 붙는 것이다.

밑줄 문자를 사용하는 경우에도 필요하다면 아래와 같이 매개변수 타입을 명시할 수 있다.

```scala
val h0 = (_: Int) + 1
val h1 = (_: Int) + (_: Int)
```

이전 코드에서 인자로 사용한 익명 함수와 같은 함수이지만 여기에서는 매개변수 타입이 유추될 수 없기 때문에 타입을 명시해 주어야만 정상적으로 컴파일이 된다.

밑줄 문자를 포함하는 가장 짧은 식에 대해서만 익명 함수로의 확장이 일어나기 때문에, 복잡한 몸통을 가진 익명 함수를 밑줄 문자로 표현하려고 시도할 때는 조심해야 한다.

```scala
def f(x: Int): Int = x
def g1(h: Int => Int): Int = h(0)
def g2(h: (Int, Int) => Int): Int = h(0, 0)
g(f(_))
g1(f(_ + 1))  // 컴파일 오류
g2(f(_) + _)
g2(f(_ + 1) + _)  // 컴파일 오류
```

`f(_)`는 `x => f(x)`로 변환되고 `f(_) + _`는 `(x, y) => f(x) + y`로 변환되기 때문에 의도한 대로 잘 컴파일 된다. (`g(f(_))` 대신 `g(f)`라 쓰는 것이 바람직하다.) 그러나, `f(_ + 1)`은 `x => f(x + 1)`을 의도하였으나, 실제로는 `f(x => x + 1)`로 변환되기 때문에 컴파일 오류가 발생한다. `f(_ + 1) + _` 역시 `(x, y) => f(x + 1) + y`를 의도하였으나, 실제로는 `y => f(x => x + 1) + y`로 변환되기 때문에 컴파일 오류가 발생한다.

Scala 코딩 경험이 어느 정도 생긴 후에는 밑줄 문자를 사용한 익명 함수가 어떻게 변환될지 큰 어려움 없이 예상할 수 있지만, 처음에는 헷갈릴 수 있으므로 확실한 경우에만 밑줄 문자를 사용하고 잘 모르겠는 경우에는 밑줄 문자를 사용하지 않는 일반적인 익명 함수를 사용하는 것이 바람직하다.

### 클로저

함수가 값으로 사용될 수 있는 것은 함수형 언어뿐이 아니다. C에서도 함수 *포인터*(pointer)를 사용하면 함수를 인자로 전달하고, 함수에서 반환하고, 지역 변수에 대입하는 것이 가능하다.

```c
int f(int x) {
    return x;
}

int g(int (*h)(int)) {
    return h(0);
}

int (*i())(int) {
    return f;
}

int main() {
    g(f);
    int (*h)(int) = f;
    i()(0);
}
```

(언제 봐도 C의 함수 포인터 타입 문법은 매우 비직관적이다.)

그러나, 함수형 언어의 함수와 C의 함수 포인터에는 큰 *표현력*(expressivity)의 차이가 존재한다. 함수형 언어의 함숫값은 *클로저*(closure)이지만, 함수 포인터는 클로저가 아니다. 클로저는 함수가 정의될 때의 *환경*(environment)을 가지고 있는 함숫값을 의미한다. 따라서, 클로저가 지원되는 언어에서는 함수가 몸통에 *자유* *변수*(free variable)를 가지고 있을 수 있으며, 자유 변수가 가리키는 값은 클로저가 가지고 있는 환경에 저장되어 있다.

```scala
def makeAdder(x: Int): Int => Int = {
  def adder(y: Int): Int = x + y
  adder
}
```

위 코드에서 `adder`의 선언 부분만 따로 보면 `def adder(y: Int): Int = x + y`에서 `x`는 어디에도 정의되지 않은 자유 변수인 것을 볼 수 있다. 그런데도 아래 코드는 오류가 나지 않고 잘 실행된다.

```scala
val add1 = makeAdder(1)
add1(2)
val add2 = makeAdder(2)
add2(2)
```

또한, `add1`과 `add2`가 결국은 동일한 함수 `adder`를 가리키고 있음에도 하나는 `1`을 더하고 다른 하나는 `2`를 더하는 역할을 수행하여 각각 `3`과 `4`를 함수 호출의 결괏값으로 가진다. 이것이 가능한 이유는 앞에서 설명한 것처럼, 함숫값이 함수 자체와 함께 함수가 선언될 때의 환경을 가지고 있기 때문이다. 따라서, `add1`은 `(adder, x = 1)`, `add2`는 `(adder, x = 2)`라는 값을 가리키고 있는 것처럼 생각할 수 있다. `add1(2)`를 계산할 때는 `x + y`의 `x`가 환경에 기록된 바에 따라 `1`이라는 값을 가지므로 전체 식의 값은 `3`이 되고, `add2(2)`를 계산할 때는 `x`가 환경에서 `2`라는 값을 가지고 있으므로 `4`가 결과가 된다.

C의 함수 포인터만을 통해서 이런 코드를 작성하는 것은 불가능하다는 것을 쉽게 알 수 있다. 물론, 추가적인 노력을 들여서 클로저를 흉내 내는 것은 얼마든지 가능하다. 구체적인 방법이 궁금하면 아래의 코드를 확인해보길 바란다.

<details><summary>C 클로저 구현</summary>
```c
#include <stdio.h>
#include <stdlib.h>

struct closure {
    int (*f)(struct closure *, int);
    void *env;
};

int call_closure(struct closure *c, int arg0) {
    return c->f(c, arg0);
}

int adder(struct closure *c, int y) {
    return ((int *) (c->env))[0] + y;
}

struct closure *makeAdder(int x) {
    struct closure *c = malloc(sizeof(struct closure));
    c->f = adder;
    c->env = malloc(sizeof(int));
    ((int *) (c->env))[0] = x;
    return c;
}

int main() {
    struct closure *add1 = makeAdder(1);
    struct closure *add2 = makeAdder(2);

    int n1 = call_closure(add1, 2);
    int n2 = call_closure(add2, 2);

    printf("%d %d\n", n1, n2);  // 3 4
}
```

`struct closure`는 일반적인 `Int => Int` 함수를 위한 클로저 *구조체*(struct)로 함수 포인터와 환경을 가지고 있다. `call_closure`는 클로저를 호출하기 위해서 사용하는 함수로, 클로저가 가지고 있는 함수 포인터에 클로저 자체와 인자로 받은 값을 넘긴다. 환경만 넘기지 않고 클로저 전체를 넘기는 이유는 재귀 호출도 지원하기 위함이다. `adder`는 아래의 `makeAdder`에서 클로저에 넣을 함수 포인터이다. 환경에 접근하여 자유 변수 `x`에 해당하는 값을 얻는다. 함수 안에 함수를 정의할 수 없으므로 별도의 함수로 정의하였다. `makeAdder`에서는 클로저를 만들고 함수 포인터를 저장한 뒤, 환경을 만들고 자유 변수 `x`의 값을 저장한다. `malloc`을 사용하였기 때문에, 원래는 `free`를 사용해서 *메모리* *누수*(memory leak)가 일어나지 않도록 해야 하지만, 간단하게 하기 위하여 생략하였다.

실제로 함수형 언어를 컴파일 할 때는 자유 변수를 모두 찾은 뒤 이러한 방식으로 함수 포인터와 환경을 메모리에 저장하게 만드는 *클로저* *변환*(closure conversion)이 일어난다. 나중에 기회가 된다면 별도의 글에서 자세히 다루어보겠다.
</details>

프로그래밍 언어 수업에서는 클로저를 지원하는 언어의 인터프리터를 구현한다. 클로저를 실제로 구현해보면 함수가 정의될 때의 환경을 함께 가지고 다닌다는 것을 더 확실하게 이해할 수 있을 것이다.

## 일급 함수와 리스트

지금부터는 지난 글에서 정의한 리스트를 다루는 함수를 일급 함수를 사용하여 어떻게 일반화할 수 있는지 알아볼 것이다.

### map

```scala
def inc1(l: List): List = l match {
  case Nil => Nil
  case Cons(h, t) => Cons(h + 1, inc1(t))
}

def square(l: List): List = l match {
  case Nil => Nil
  case Cons(h, t) => Cons(h * h, square(t))
}
```

`inc1`은 리스트의 모든 원소가 1씩 증가한 리스트를 결과로 내는 함수이고 `square`는 리스트의 모든 원소가 제곱된 리스트를 결과로 낸다. 얼핏 보기에도 두 함수의 구조가 상당히 비슷하다는 것을 알 수 있다. 공통점을 조금 더 명확히 보기 위해서 함수의 동작과는 상관없는 두 함수의 이름을 `g`라고 통일해 보겠다.

```scala
def g(l: List): List = l match {
  case Nil => Nil
  case Cons(h, t) => Cons(h + 1, g(t))
}

def g(l: List): List = l match {
  case Nil => Nil
  case Cons(h, t) => Cons(h * h, g(t))
}
```

이제 두 함수의 차이는 세 번째 줄의 `Cons`의 첫 인자로 `h + 1`을 사용하느냐 아니면 `h * h`를 사용하느냐만 남았다. 함수 안에 식을 직접 쓰는 대신 인자로 함수를 받도록 하면 두 함수의 모양을 완전히 같게 만들 수 있다.

```scala
def g(l: List, f: Int => Int): List = l match {
  case Nil => Nil
  case Cons(h, t) => Cons(f(h), g(t, f))
}
g(l, h => h + 1)

def g(l: List, f: Int => Int): List = l match {
  case Nil => Nil
  case Cons(h, t) => Cons(f(h), g(t, f))
}
g(l, h => h * h)
```

이 글에서는 이 함수를 `list_map`이라는 이름으로 부를 것이다. 인자로 받은 리스트의 원소에 인자로 받은 함수를 'map'하여 얻은 리스트를 결과로 내기 때문이다.

```scala
def list_map(l: List, f: Int => Int): List = l match {
  case Nil => Nil
  case Cons(h, t) => Cons(f(h), list_map(t, f))
}
```

이제 일반적으로 정의된 `list_map` 함수를 사용해서 `inc1`과 `square`를 다시 정의할 수 있다.

```scala
def inc1(l: List): List = list_map(l, h => h + 1)
def square(l: List): List = list_map(l, h => h * h)
```

`inc1`은 밑줄 문자를 사용해서 더 간단히 표현할 수 있다.

```scala
def inc1(l: List): List = list_map(l, _ + 1)
```

`list_map` 함수와 일급 함수를 사용하는 연습을 해보기 위해서, 인자로 리스트 하나와 정수 하나를 받은 뒤 리스트의 모든 원소를 인자로 받은 정수만큼 증가시킨 리스트를 결과로 하는 `incBy` 함수를 `list_map` 함수를 사용해서 정의해보자.

<details><summary>`incBy` 코드 보기</summary>
```scala
def incBy(l: List, n: Int): List = list_map(l, h => h + n)
def incBy(l: List, n: Int): List = list_map(l, _ + n)
```
</details>

### filter

이번에는 `odd`와 `positive`를 비교해보자.

```scala
def odd(l: List): List = l match {
  case Nil => Nil
  case Cons(h, t) =>
    if (h % 2 != 0) Cons(h, odd(t))
    else odd(t)
}

def positive(l: List): List = l match {
  case Nil => Nil
  case Cons(h, t) =>
    if (h > 0) Cons(h, positive(t))
    else positive(t)
}
```

두 함수도 형태가 상당히 유사한 것을 알 수 있다. 함수의 이름을 통일하고, 유일하게 남은 다른 부분인 `h % 2 != 0`과 `h > 0`을 인자로 받는 함수로 표현하면 두 함수가 완전히 같아진다. 이 글에서는 이 함수의 이름을 `list_filter`라고 부를 것이다. 인자로 받은 리스트의 원소 중 인자로 받은 함수를 만족하는 원소만 남긴 'filter'한 리스트를 결과로 내기 때문이다.

```scala
def list_filter(l: List, f: Int => Boolean): List = l match {
  case Nil => Nil
  case Cons(h, t) =>
    if (f(h)) Cons(h, list_filter(t, f))
    else list_filter(t, f)
}
```

이제 `odd`와 `positive`를 `list_filter`를 사용해서 다시 쓸 수 있다.

```scala
def odd(l: List): List = list_filter(l, h => h % 2 != 0)
def positive(l: List): List = list_filter(l, h => h > 0)
```

두 함수 모두 밑줄 문자를 사용할 수 있다.

```scala
def odd(l: List): List = list_filter(l, _ % 2 != 0)
def positive(l: List): List = list_filter(l, _ > 0)
```

리스트 하나와 정수 하나를 인자로 받아서 리스트에서 인자로 받은 정수보다 큰 원소만을 남긴 리스트를 결과로 하는 함수 `gt`를 `list_filter`를 사용해서 정의해보자.

<details><summary>`gt` 코드 보기</summary>
```scala
def gt(l: List, n: Int): List = list_filter(l, h => h > n)
def gt(l: List, n: Int): List = list_filter(l, _ > n)
```
</details>

### foldRight

이번에는 `sum`과 `product`의 공통점을 찾아보자. 먼저 꼬리 재귀를 사용하지 않고 구현된 함수부터 볼 것이다.

```scala
def sum(l: List): Int = l match {
  case Nil => 0
  case Cons(h, t) => h + sum(t)
}

def product(l: List): Int = l match {
  case Nil => 1
  case Cons(h, t) => h * product(t)
}
```

함수의 이름을 `g`로 통일하고 나면 이번에는 다른 부분이 두 군데임을 볼 수 있다. 첫 번째는 `0`과 `1`이 다르고, 두 번째는 `h + g(t)`와 `h * g(t)`이다. `Nil` 일 때의 초깃값과 `h`와 `g(t)`를 사용하는 식을 각각 정수와 함수 인자로 받게 만들면 다른 부분이 없어진다. 이 글에서 이 함수의 이름은 `list_foldRight`이다. 인자로 받은 리스트의 오른쪽 끝에 초깃값을 붙인 뒤, 인자로 받은 함수를 사용하여 오른쪽부터 리스트를 'fold'해 나가기 때문이다.

```scala
def list_foldRight(l: List, n: Int, f: (Int, Int) => Int): Int = l match {
  case Nil => n
  case Cons(h, t) => f(h, list_foldRight(t, n, f))
}
```

`list_foldRight`을 사용하여 `sum`과 `product`를 다시 정의할 수 있다.

```scala
def sum(l: List): Int = list_foldRight(l, 0, (h, gt) => h + gt)
def product(l: List): Int = list_foldRight(l, 1, (h, gt) => h * gt)
```

밑줄 문자를 사용할 수도 있다.

```scala
def sum(l: List): Int = list_foldRight(l, 0, _ + _)
def product(l: List): Int = list_foldRight(l, 1, _ * _)
```

`list_foldRight`가 어떤 일을 하는 함수인지 명확하지 않은 사람들을 위해서 추가적인 설명을 준비해 보았다.

```scala
  list_foldRight(List(a, b, .., y, z), n, f)
= f(a, f(b, .. f(y, f(z, n)) .. ))

  list_foldRight(List(1, 2, 3), 0, +)
= +(1, +(2, +(3, 0)))

  list_foldRight(List(1, 2, 3), 1, *)
= *(1, *(2, *(3, 1)))
```

위에 써진 것을 보면, 리스트의 오른쪽 끝에 초깃값을 붙인 뒤 함수를 사용해서 리스트를 '접어 나간다'는 것이 어떤 의미인지 더 명확하게 느낄 수 있을 것이다.

연습 삼아 `length`를 `list_foldRight`을 사용해서 구현해보자.

<details><summary>`length` 코드 보기</summary>
```scala
def length(l: List): List = list_foldRight(l, 0, (h, gt) => 1 + gt)
```
</details>

### foldLeft

이번에는 꼬리 재귀를 사용하여 구현한 `sum`과 `product`에 대해 같은 작업을 해보자.

```scala
def sum(l: List): Int = {
  @tailrec def aux(l: List, inter: Int): Int = l match {
    case Nil => inter
    case Cons(h, t) => aux(t, inter + h)
  }
  aux(l, 0)
}

def product(l: List): Int = l match {
  @tailrec def aux(l: List, inter: Int): Int = l match {
    case Nil => inter
    case Cons(h, t) => aux(t, inter * h)
  }
  aux(l, 1)
}
```

함수 이름을 제외하고 다른 곳은 재귀 호출 시에 `aux`의 두 번째 인자로 사용하는 `inter + h`와 `inter * h`와, 바깥에 있는 함수에서 `aux`의 두 번째 인자로 사용하는 `0`과 `1`뿐이다. 동일한 방법으로 정수와 함수를 인자로 받게 하여 두 함수를 완전히 같게 만들 수 있다.

```scala
def list_foldLeft(l: List, n: Int, f: (Int, Int) => Int): Int = {
  @tailrec def aux(l: List, inter: Int): Int = l match {
    case Nil => inter
    case Cons(h, t) => aux(t, f(inter, h))
  }
  aux(l, n)
}
```

이 함수는 단순히 `list_foldRight`을 꼬리 재귀 형태로 구현한 것이 아니다. 코드를 잘 살펴보면, `list_foldRight`이 리스트의 오른쪽에 초깃값을 추가하고 오른쪽에서 왼쪽으로 리스트를 접었다면, 이번 함수는 리스트의 왼쪽에 초깃값을 추가한 뒤 왼쪽에서 오른쪽으로 리스트를 접는다는 것을 알 수 있다. 따라서, 이 함수의 이름은 `list_foldLeft`이다. `list_foldLeft`가 하는 일은 아래처럼 설명할 수 있다.

```scala
  list_foldLeft(List(a, b, .., y, z), n, f)
= f(f( .. f(f(n, a), b), .. , y), z)

  list_foldLeft(List(1, 2, 3), 0, +)
= +(+(+(0, 1), 2), 3)

  list_foldLeft(List(1, 2, 3), 1, *)
= *(*(*(1, 1), 2), 3)
```

`sum`, `product`, `length`의 경우 수행하는 작업이 순서에 영향을 받지 않기 때문에(단순히 왼쪽에서 오른쪽이나 오른쪽에서 왼쪽뿐 아니라 리스트의 모든 원소를 한 번씩만 거치면 동일한 결과를 얻을 수 있다), `list_foldRight`와 `list_foldLeft` 중 무엇을 사용해서 구현하든 동일한 결과를 얻을 수 있다.

```scala
def sum(l: List): Int = list_foldLeft(l, 0, _ + _)
def product(l: List): Int = list_foldLeft(l, 1, _ * _)
def length(l: List): Int = list_foldLeft(l, 0, (inter, h) => inter + 1)
```

반면, `addBack`이나 `reverse` 같은 리스트의 원소들 사이의 순서가 중요한 함수는 둘 중 하나를 사용해서 구현하는 것이 훨씬 효율적임을 알 수 있다. 지금 우리가 구현한 `list_foldRight`과 `list_foldLeft`는 초깃값이 `Int` 값인 경우에만 작동하지만, 타입을 고려하지 않고 생각하면 `addBack`은 `list_foldRight`을, `reverse`는 `list_foldLeft`를 사용하는 것이 효율적이다.

```scala
def addBack(l: List, n: Int): List =
  list_foldRight(l, Cons(n, Nil), (h, gt) => Cons(h, gt))
def addBack(l: List, n: Int): List =
  list_foldRight(l, Cons(n, Nil), Cons)
def reverse(l: List): List =
  list_foldLeft(l, Nil, (inter, h) => Cons(h, inter))
```

지금까지 일급 함수를 사용해서 리스트를 다루는 네 개의 함수를 알아보았다. 이 네 함수는 매우 강력한 기능을 가지고 있기 때문에, 네 함수를 적절하게 사용하면 리스트를 가지고 할 수 있는 거의 모든 작업을 간결한 코드로 할 수 있다. 따라서, 대부분의 함수형 언어에서는 라이브러리를 통해 이 메서드들과 동일한 일을 하는 함수들을 제공한다. Scala에서도 `List` 클래스의 `map`, `filter`, `foldRight`, `foldLeft` 메서드가 동일한 일을 한다. Scala 표준 라이브러리의 `List` 클래스에 대해서는 다음 글에서 자세히 알아볼 것이다.

## 옵션 타입

인자로 리스트 한 개와 정수 `n`을 받아서 리스트의 `n` 번째 원소를 결과로 내는 함수 `list_get`을 정의해보자. 여기서 어려운 점은 `n`이 음수거나 리스트의 길이보다 큰 값이면 어떡할 것이냐는 것이다. 명령형 언어에서 쉽게 생각할 수 있는 방법은 예외를 발생시키는 것이다.

```scala
def list_get(l: List, n: Int): Int =
  if (n < 0)
    throw new Exception("Negative index.")
  else l match {
    case Nil =>
      throw new Exception("Index out of bound.")
    case Cons(h, t) =>
      if (n == 0) h else list_get(t, n - 1)
  }
```

예외를 발생시키는 것은 매우 간단하고 효과적인 해결책이지만, `list_get` 함수를 호출한 곳에서 적절하게 *예외* *처리*(exception handling)를 하지 않으면 프로그램이 비정상적으로 종료된다는 문제가 있다. Java의 *확인된* *예외*(checked exception)처럼 타입 체계를 통해서 예외 처리를 강제하는 경우도 있지만, 대부분의 경우 예외 처리는 타입 체계의 검사 대상이 아니기 때문에, 프로그래머가 예외 처리를 빼먹어서 문제가 생기기 쉽다. 확인된 예외를 사용하는 경우에도, 형식적인 `try-catch` 문을 사용하여 컴파일만 가능하게 하고 실제로 예외 상황에 대해 어떤 처리도 하지 않는 경우가 많다. 또한, 예외 처리는 지역적으로 일어나는 것이 아니라, 함수 호출 스택을 타고 전파되기 때문에 프로그램의 *제어* *흐름*(control flow)이 갑자기 *예외* *처리기*(exception handler) 위치로 건너뛰게 만들어 코드를 파악하기도 어렵게 만든다. 그렇다면, 예외를 발생시키지 않는 다른 방법으로 `list_get` 함수를 작성할 수 있을까?

```scala
def list_get(l: List, n: Int): Int =
  if (n < 0) null
  else l match {
    case Nil => null
    case Cons(h, t) =>
      if (n == 0) h else list_get(t, n - 1)
  }
```

첫 시도는 `null`을 사용하는 것이다. `null`은 *참조* *타입*(reference type)이 어떤 실제 객체도 참조하지 않고 있다는 것을 나타내기 위해 사용되는 값이다. 그러나, Scala는 Java와 마찬가지로 `null`을 `Int` 값의 일부로 보지 않는다. 따라서, 위 코드는 컴파일 되지 않는다. `null`을 사용할 수 있다고 하더라도, 결국 함수의 결괏값을 사용할 때 `null`인지 확인하지 않는다면 `NullPointerException`이 발생할 것이므로 근본적인 문제를 해결했다고 말할 수 없다. `null`도 예외와 마찬가지로 타입 체계에 의해 검사되지 않는 대상이고 Java에서는 `NullPointerException`이 많은 버그의 원인이다. Kotlin과 같은 최신 언어에서는 이러한 Java의 단점을 극복하기 위해서 `null`*이* *될* *수* *있는* *타입*(nullable type)과 `null`*이* *될* *수* *없는* *타입*(non-null type)을 명시적으로 구분하여 안전한 코드를 작성할 수 있도록 돕고 있으며, 이런 구분은 뒤에서 살펴볼 옵션 타입과 거의 동일하다.

```scala
def list_get(l: List, n: Int): Int =
  if (n < 0) -1
  else l match {
    case Nil => -1
    case Cons(h, t) =>
      if (n == 0) h else list_get(t, n - 1)
  }
```

두 번째 시도는 `-1`과 같은 잘못된 입력이라는 것을 표현할 수 있는 특수한 값을 결과로 내는 것이다. 이 방법의 문제는 쉽게 찾을 수 있다. 리스트가 `-1`을 원소로 가지고 있어서 `-1`이 결과인 경우와 잘못된 입력이 들어와서 `-1`이 결과인 경우를 구분할 방법이 없다는 것이다. 구현하고자 하는 함수에 따라서는 이런 특수한 값을 결과로 내는 것이 성공적인 해결책이 될 수 있으나, 적어도 `list_get`에는 적합하지 않은 방법이다.

```scala
def list_getOrElse(l: List, n: Int, default: Int): Int =
  if (n < 0) default
  else l match {
    case Nil => default
    case Cons(h, t) =>
      if (n == 0) h else list_getOrElse(t, n - 1, default)
}
```

약간 전략을 바꿔서, 예외 상황에 결과가 될 기본값을 함수를 호출하는 쪽에서 직접 선택하게 하는 것은 어떨까? 함수를 부르는 쪽에서 원하는 기본값이 있다면 좋은 방법이다. 그러나, 예외 상황인지를 확인하는 것이 중요한 경우에는 이 방법 역시 앞에서 `-1`을 결과로 내는 것보다 나을 것이 없다. 주어진 기본값이 리스트의 원소인지 아니면 인자가 잘못된 것인지를 구분할 수 없기 때문이다.

함수형 언어에서는 예외 상황을 안전하고 불변성을 유지하는 방식으로 처리할 수 있도록 옵션 타입을 제공한다. Scala 등 많은 언어에서 `Option`이라고 부르며 일부 언어에서는 `Maybe`라고 부르는 경우도 있다. 그 이름에서 유추할 수 있듯이 값이 존재하는 경우와 존재하지 않는 경우를 선택적으로 나타낼 수 있는 타입이다. 이 글에서는 `Int`에 대한 옵션 타입과 옵션 타입을 다루기 위한 함수들을 직접 정의해 볼 것이다.

```scala
trait Option
case object None extends Option
case class Some(n: Int) extends Option
```

정의하는 방법은 앞에서 `List`, `Nil`, `Cons`를 정의할 때와 유사하다. `Option` 타입의 값에는 두 종류, `None`과 `Some`이 있다. `None`은 아무런 값도 나타내지 않는 값으로, 참조 타입의 `null`과 유사하다고 볼 수 있으며, 예외 상황이 발생했다는 것을 나타내는 값으로도 생각할 수 있다. `Nil`과 마찬가지로 유일하게 존재하는 값이므로 `object` 키워드를 사용하였다. `Some`은 어떤 값이 존재한다는 것을 나타내는 값으로, 참조 타입이 `null`이 아닌 값을 가져 어떤 실제 객체를 가리키고 있는 경우와 유사하게 생각할 수 있다. 또한, 계산이 예외 상황을 발생시키지 않고 성공적으로 마무리되었다는 것을 표현하는 것으로 볼 수도 있다.

옵션 타입을 사용해서 `list_getOption`을 아래처럼 정의할 수 있다.
```scala
def list_getOption(l: List, n: Int): Option =
  if (n < 0) None
  else l match {
    case Nil => None
    case Cons(h, t) =>
      if (n == 0) Some(h) else list_getOption(t, n - 1)
  }
```

인자로 주어진 정숫값이 잘못된 경우, `None`을 결과로 냄으로써 예외 상황이 발생하였다는 것을 나타내고, 올바른 경우, `Some`으로 결괏값을 감싸서 옵션 타입을 만들어 결과로 낸다.

옵션 타입을 사용하는 연습을 해보기 위해서, 정수 하나를 인자로 받아 100을 그 정수로 나누되, 옵션 타입을 사용하여 0으로 나누는 경우를 안전하게 처리하는 `div100` 함수를 작성해보자.

<details><summary>`div100` 코드 보기</summary>
```scala
def div100(n: Int): Option =
  if (n == 0) None else Some(100 / n)
```
</details>

옵션 타입의 값을 사용하려면 어떻게 해야 할까? 패턴 대조를 통해서 `None`인지 `Some`인지에 따라 나누어 처리할 수 있겠지만, 리스트와 마찬가지로 옵션 타입에서도 일반적으로 많이 사용하는 패턴을 함수로 정의하는 것이 바람직하다.

```scala
def option_map(opt: Option, f: Int => Int): Option = opt match {
  case None => None
  case Some(n) => Some(f(n))
}
```

`option_map` 함수는 예외 상황이 발생할 수 있는 계산을 한 후에, 정상적인 결과가 나왔다면 추가로 어떤 작업을 하고, 예외 상황이 발생했다면 가만히 있도록 하기 위해서 사용할 수 있는 함수이다. 옵션 값 하나와 함수 하나를 인자로 받은 뒤 옵션 값이 `Some`인 경우에만 가지고 있는 정숫값에 함수를 적용한 결과를 `Some`으로 감싼다.

리스트 하나와 정수 `n`을 인자로 받은 뒤 리스트의 `n` 번째 원소를 제곱한 값을 결과로 내는 함수 `getSquare`를 옵션 타입과 `list_getOption`, `option_map` 함수를 사용하여 안전하게 구현해보자.

<details><summary>`getSquare` 코드 보기</summary>
```scala
def getSquare(l: List, n: Int): Option =
  option_map(list_getOption(l, n), n => n * n)
```
</details>

```scala
def option_flatMap(opt: Option, f: Int => Option): Option = opt match {
  case None => None
  case Some(n) => f(n)
}
```

`option_flatMap`은 `option_map`과 유사하지만, 예외 상황이 발생할 수 있는 작업을 한 후에 또 예외 상황이 발생할 수 있는 작업을 하는 경우에 사용할 수 있는 함수이다. 따라서, `option_flatMap`이 인자로 받는 함수의 결과 타입은 옵션이다. 두 계산 중 한 번이라도 예외 상황이 발생한다면 `None`이 결과이고 아니라면 `Some` 안에 어떤 값이 들어있을 것이다.

리스트 하나와 정수 `n`을 인자로 받은 뒤 100을 리스트의 `n` 번째 원소로 나눈 값을 결과로 내는 함수 `getAndDiv100`을 옵션 타입과 `list_getOption`, `div100`, `option_flatMap` 함수를 사용하여 안전하게 구현해보자.

<details><summary>`getAndDiv100` 코드 보기</summary>
```scala
def getAndDiv100(l: List, n: Int): Option =
  option_flatMap(list_getOption(l, n), div100)
```
</details>

옵션 타입은 함수형으로 예외 상황을 처리하도록 해주는 아주 강력한 도구이다. 함수형 프로그래밍을 하면서 리스트, 일급 함수, 익명 함수와 함께 매우 많이 사용하게 될 것이다.

## 감사의 말

글을 확인하고 의견을 주신 류석영 교수님, Scala 세미나를 준비할 때 의견 주신 모든 분과 Scala 세미나에 참석하신 모든 분께 감사드립니다. 잘못된 코드를 지적해주신 ‘seyoon’님과 ‘kslksks’님께 감사드립니다.
