이 글에서는 일급 계속을 제공하는 언어의 인터프리터를 일급 함수의 개념 없이 일차 함수만을 사용해 구현하는 방법을 알아본다.

## 동기

이전 글에서 일급 계속을 지원하는 언어인 KFAE의 인터프리터를 Scala로 구현하였다.

```scala
sealed trait Expr
case class Num(n: Int) extends Expr
case class Add(l: Expr, r: Expr) extends Expr
case class Id(x: String) extends Expr
case class Fun(x: String, b: Expr) extends Expr
case class App(f: Expr, a: Expr) extends Expr
case class Vcc(x: String, b: Expr) extends Expr

sealed trait Value
case class NumV(n: Int) extends Value
case class CloV(p: String, b: Expr, e: Env) extends Value
case class ContV(k: Cont) extends Value

type Env = Map[String, Value]
type Cont = Value => Value

def interp(e: Expr, env: Env, k: Cont): Value = e match {
  case Num(n) => k(NumV(n))
  case Id(x) => k(lookup(x, env))
  case Fun(x, b) => k(CloV(x, b, env))
  case Add(e1, e2) =>
    interp(e1, env, v1 =>
      interp(e2, env, v2 =>
        k(numVAdd(v1, v2))))
  case App(e1, e2) =>
    interp(e1, env, v1 =>
      interp(e2, env, v2 => v1 match {
        case CloV(xv1, ev1, sigmav1) =>
          interp(ev1, sigmav1 + (xv1 -> v2), k)
        case ContV(k) => k(v2)
      })
    )
  case Vcc(x, b) =>
    interp(b, env + (x -> ContV(k)), k)
}
```

(구현의 일부를 생략했다.)

이 글의 내용에 조금 더 잘 맞게끔 `interp` 함수의 구현을 약간만 수정하겠다.

```scala
def continue(k: Cont, v: Value): Value = k(v)

def interp(e: Expr, env: Env, k: Cont): Value = e match {
  case Num(n) => continue(k, NumV(n))
  case Id(x) => continue(k, lookup(x, env))
  case Fun(x, b) => continue(k, CloV(x, b, env))
  case Add(e1, e2) =>
    interp(e1, env, v1 =>
      interp(e2, env, v2 =>
        continue(k, numVAdd(v1, v2))))
  case App(e1, e2) =>
    interp(e1, env, v1 =>
      interp(e2, env, v2 => v1 match {
        case CloV(xv1, ev1, sigmav1) =>
          interp(ev1, sigmav1 + (xv1 -> v2), k)
        case ContV(k) => continue(k, v2)
      })
    )
  case Vcc(x, b) =>
    interp(b, env + (x -> ContV(k)), k)
}
```

원래는 `k(v)`로 쓰던 것을 `continue(k, v)`로 바꿨다. `continue(k, v)`가 하는 일은 `k(v)`를 계산하는 것밖에 없으므로 기존의 구현과 사실상 완전히 같다. 지금은 `continue` 함수가 별로 필요해 보이지 않지만, 뒤에서 계속을 함수로 표현하지 않게 되면 `continue` 함수가 유용해질 것이다.

위 구현에서 주목할 점은 `Cont`라는 타입의 정의이다. `Cont`는 값으로서의 계속을 나타내는 타입으로, 값에서 값으로 가는 함수 타입이다. 즉, 이 인터프리터는 계속을 하나의 함수로 표현한다. 계속을 표현하는 함수는 하나의 값으로서 환경 안에 들어간 채로 `interp`에 전달되거나 `interp`의 결과로 사용되므로 인터프리터가 일급 함수의 개념을 사용하고 있는 것이다.

일급 함수를 사용해 계속을 나타내는 것은 몇 가지 문제가 있다. 첫 번째로, C와 같은 저급 언어에는 일급 함수가 없다. (C에는 함수 포인터가 있지만 계속을 일급 함수로 나타내려면 클로저가 필요하며 C에는 클로저가 없다.) 이러한 저급 언어를 사용해 KFAE의 인터프리터를 구현하려면 다른 방법이 필요한 것이다. 둘째로, 함수로 표현한 계속에서는 유용한 정보를 얻어내기 힘들다. 함수를 사용해서 할 수 있는 일은 그 함수를 어떤 값에 적용하는 것밖에 없다. 그러나, 디버거를 구현한다든가 하는 특정한 상황에서는 값으로 주어진 계속이 정확히 어떤 일을 하는지 파악하고 싶을 수 있다. 지금처럼 하나의 함수로 계속이 표현되면 그런 일을 할 수 없다. 이는 클로저를 표현하는 `CloV` 타입이 함수의 매개변수와 몸통 및 가지고 있는 환경에 대한 정보를 정확하게 제공하는 것과 대비된다.

이 글에서는 위에서 설명한 문제를 해결하기 위해 일급 함수 없이 계속을 표현하는 방법을 알아본다. 일급 함수를 사용하지 않음으로써 C 같은 저급 언어에서도 일급 계속을 제공하는 언어의 인터프리터를 구현할 수 있게 된다. 또한, 계속이 함수가 아니라 직접 정의한 특정한 구조로 표현되기 때문에, 어떤 계속이 주어졌을 때 그 계속이 무슨 일을 하는지를 정확하게 알아낼 수 있게 된다.

## 계속의 일차 표현

계속을 나타내는 값을 직접 정의하려면 기존의 인터프리터에서 어떤 계속이 사용되었는지 파악해야 한다. 기존 인터프리터에서 `interp`의 인자로 사용된 계속에는 총 네 종류가 있다. (뺄셈이 언어에서 생략되었음에 유의하기 바란다. 덧셈이 이미 언어에 있으므로 아래 내용을 다 이해한다면 어렵지 않게 뺄셈을 언어에 다시 추가할 수 있을 것이다.)

* `v1 => interp(e2, env, v2 => continue(k, numVAdd(v1, v2)))`
* `v2 => continue(k, numVAdd(v1, v2))`
* `v1 => interp(e2, env, v2 => v1 match { ... })`
* `v2 => v1 match { ... }`

(일부를 생략했으니 정확한 코드는 위에서 보면 된다.)

첫 번째 계속인 `v1 => interp(e2, env, v2 => continue(k, numVAdd(v1, v2)))`는 덧셈의 왼쪽 피연산자를 계산한 후에 사용되는 계속으로, 오른쪽 피연산자까지 계산한 후 합을 구하고 그 합을 덧셈을 계산할 때 주어진 계속에 전달한다. 매개변수 `v1`이 왼쪽 피연산자를 계산하여 얻은 값이다. 이 함수의 몸통에는 총 세 개의 자유 변수가 있다. `e2`, `env`, `k`이다. `e2`는 오른쪽 피연산자, `env`와 `k`는 덧셈을 계산할 때 주어진 환경과 계속이다. (`numVAdd`는 `interp` 바깥에 정의되어 있는 함수이다. 정의가 기억나지 않는 사람은 이전 글에서 보고 오기를 바란다.) 즉, 이 세 개의 자유 변수의 값이 무엇인지만 결정되면 이 계속이 무슨 일을 하는 계속인지 완벽하게 말할 수 있는 것이다. 지금부터는 이 계속을 함수 대신 `(e2, env, k)`라는 식, 환경, 계속의 튜플로 표현하겠다.

기존에 계속을 사용해 계산을 이어 나가는 것은 함수 적용을 통해 가능했다. 계속이 함수이므로 가능한 일이었다. 그러나, 이제는 계속이 함수가 아니라 세 값의 튜플이다. 이 튜플을 주어진 값에 적용하는 것은 불가능하다. 따라서, 계속에 값이 전달되었을 때 계속을 구성하는 정보와 주어진 값을 사용해 계산을 이어 나갈 새로운 방법이 필요하다. 그 방법은 매우 간단하다. 이미 위에서 본 함수의 몸통에 필요한 모든 정보가 있다. 위의 함수를 `v1`에 적용하면 `interp(e2, env, v2 => continue(k, numVAdd(v1, v2)))`가 결과로 나왔다. 이제는 함수와 `v1` 대신 `(e2, env, k)`와 `v1`이 주어진다. 그러면 주어진 `v1`, `e2`, `env`, `k`를 사용해 `interp(e2, env, v2 => continue(k, numVAdd(v1, v2)))`를 계산하면 된다. 이는 함수를 `v1`에 적용하는 것과 완전히 같은 일이다.

기존 방법과 새로운 방법을 아래와 같이 간단하게 비교할 수 있다.

* 기존: `v1 => interp(e2, env, v2 => continue(k ,numVAdd(v1, v2)))`와 `v1`이 주어짐. 그러면 `v1 => interp(e2, env, v2 => continue(k, numVAdd(v1, v2)))`를 `v1`에 적용해 `interp(e2, env, v2 => continue(k, numVAdd(v1, v2)))`를 계산함.
* 현재: `(e2, env, k)`과 `v1`이 주어짐. 그러면 `e2`, `env`, `k`, `v1`을 모두 알고 있으니 `interp(e2, env, v2 => continue(k, numVAdd(v1, v2)))`를 계산함.

두 방법 모두 최종적으로는 `interp(e2, env, v2 => continue(k, numVAdd(v1, v2)))`를 계산하므로 완전히 같은 일을 한다. 그러나 기존 방법에서는 계속을 함수로 표현했고 새로운 방법에서는 계속을 식, 환경, 계속의 튜플로 표현한다.

첫 번째 계속을 이해했다면 나머지 세 가지 계속은 쉽다. 차례대로 살펴보자. 두 번째 계속인 `v2 => continue(k, numVAdd(v1, v2))`는 덧셈의 오른쪽 피연산자를 계산한 후에 사용되는 계속으로, 왼쪽 피연산자를 계산한 결과와의 합을 구한 뒤 그 합을 덧셈을 계산할 때 주어진 계속에 전달한다. 매개변수 `v2`가 오른쪽 피연산자를 계산한 결과이다. 함수의 몸통에는 두 개의 자유 변수가 있다. 바로 `k`와 `v1`로, `k`는 덧셈을 계산할 때 주어진 계속, `v1`은 왼쪽 피연산자의 계산 결과이다. 아까와 같은 논리로, `v1`과 `k`만 있으면 이 계속이 하는 일을 정확히 알 수 있다. 그러므로 이 계속은 `(v1, k)`라는, 값과 계속의 순서쌍으로 표현할 수 있다. 이 계속을 가지고 계산을 이어 가는 방법 역시 아까와 비슷하게 찾을 수 있다. `(v1, k)`와 `v2`가 모두 주어지면 `continue(k, numVAdd(v1, v2))`를 계산할 수 있으므로 `continue(k, numVAdd(v1, v2))`를 계산하여 계산을 이어 나가면 된다. 정리하면 아래와 같다.

* 기존: `v2 => continue(k, numVAdd(v1, v2))`와 `v2`가 주어짐. 그러면 `v2 => continue(k, numVAdd(v1, v2))`를 `v2`에 적용해 `continue(k, numVAdd(v1, v2))`를 계산함.
* 현재: `(v1, k)`와 `v2`가 주어짐. 그러면 `v1`, `k`, `v2`를 모두 알고 있으니 `continue(k, numVAdd(v1, v2))`를 계산함.

세 번째 계속인 `v1 => interp(e2, env, v2 => v1 match { ... })`는 함수 적용에서 함수 위치에 있는 식을 계산한 다음에 사용되는 계속으로, 인자 위치의 식을 계산한 뒤 인자에 함수를 적용하여 나온 결과를 함수 적용에 주어진 계속에 전달한다. `v1`은 함수 위치의 식을 계산한 결과이다. 계속을 나타내는 함수의 몸통에 있는 자유 변수는 `e2`, `env`, `k`이다. `e2`는 인자 위치의 식이며 `env`와 `k`는 함수 적용을 계산할 때 주어진 환경과 계속이다. (`k`는 ... 안에 있다.) 따라서 `e2`, `env`, `k`만 있으면 이 계속이 하는 일을 정확히 알 수 있기에 계속을 `(e2, env, k)`라는 식, 환경, 계속의 튜플로 표현할 수 있다. `(e2, env, k)`와 `v1`이 주어지면 `interp(e2, env, v2 => v1 match { ... })`를 계산하여 계산을 이어 나갈 수 있다.

* 기존: `v1 => interp(e2, env, v2 => v1 match { ... })`와 `v1`이 주어짐. 그러면 `v1 => interp(e2, env, v2 => v1 match { ... })`를 `v1`에 적용해 `interp(e2, env, v2 => v1 match { ... })`를 계산함.
* 현재: `(e2, env, k)`와 `v1`이 주어짐. 그러면 `e2`, `env`, `k`, `v1`을 모두 알고 있으니 `interp(e2, env, v2 => v1 match { ... })`를 계산함.

네 번째 계속인 `v2 => v1 match { ... }`는 함수 적용에서 인자 위치에 있는 식을 계산한 다음에 사용되는 계속으로, 인자에 함수를 적용해 나온 결과를 함수 적용에 주어진 계속에 전달한다. `v2`는 인자의 값이다. 계속을 나타내는 함수의 몸통에 있는 자유 변수는 `v1`, `k`으로, `v1`은 함수 위치의 식을 계산한 값이고 `k`는 함수 적용에 주어진 계속이다. (`k`는 ... 안에 있다.) 따라서 `(v1, k)`라는, 값과 계속의 순서쌍으로 이 계속이 하는 일을 정확히 표현할 수 있다. `(v1, k)`와 `v2`가 주어졌을 때 계산을 이어 나가려면 `v1 match { ... }`를 계산하면 된다.

* 기존: `v2 => v1 match { ... }`와 `v2`가 주어짐. 그러면 `v2 => v1 match { ... }`를 `v2`에 적용해 `v1 match { ... }`를 계산함.
* 현재: `(v1, k)`와 `v2`가 주어짐. 그러면 `v1`, `k`, `v2`를 모두 알고 있으니 `v1 match { ... }`를 계산함.

마지막으로, `interp`의 구현에 드러나지 않는 계속이 하나 있다. 바로 항등 함수로 표현되는 계속이다. 이 계속은 제일 처음에 `interp`을 호출할 때 인자로 사용된다. 항등 함수가 하는 일은 주어진 인자를 그대로 결과로 내는 것이므로 이 일을 알아내는 데는 어떤 추가적인 정보도 필요하지 않다. 즉, `()`라는 길이가 0인 튜플(Scala에서는 `Unit` 타입의 값)로 이 계속을 표현할 수 있다. 이 계속에 `v`가 주어졌을 때 계산을 이어 가려면 `v`를 결과로 내면 된다.

* 기존: 항등 함수와 `v`가 주어짐. 그러면 항등 함수를 `v`에 적용해 `v`를 계산함.
* 현재: `()`와 `v`가 주어짐. 그러면 `v`를 알고 있으니 `v`를 계산함.

지금까지 관찰한 내용을 정리하면, KFAE의 인터프리터에서 사용되는 계속은 다음의 다섯 종류로 나눌 수 있다. (타입 정보를 튜플에 표시했다.)

* `(e2: Expr, env: Env, k: Cont)`
* `(v1: Value, k: Cont)`
* `(e2: Expr, env: Env, k: Cont)`
* `(v1: Value, k: Cont)`
* `()`

여기서 첫 번째와 세 번째, 그리고 두 번째와 네 번째가 각각 겉보기에는 같아 보여도 다른 계속을 나타낸다는 사실에 유의하기를 바란다. 첫 번째 계속은 그 정보를 사용해 `interp(e2, env, v2 => continue(k, numVAdd(v1, v2)))`를 계산하는 반면, 세 번째 계속은 그 정보를 사용해 `interp(e2, env, v2 => v1 match { ... })`를 계산한다. 마찬가지로, 두 번째 계속은 그 정보를 사용해 `continue(k, numVAdd(v1, v2))`를 계산하지만, 네 번째 계속은 그 정보를 사용해 `v1 match { ... }`를 계산한다.

이렇게 한 타입의 값이 여러 형태를 가질 수 있는 경우에 그 타입을 정의하려면 대수적 데이터 타입을 사용해야 한다고 전에 설명했다. 그러므로 `Cont` 타입을 `sealed trait`과 `case class`를 사용해 아래와 같이 새롭게 정의할 수 있다.

```scala
sealed trait Cont
case class AddSecondK(e2: Expr, env: Env, k: Cont) extends Cont
case class DoAddK(v1: Value, k: Cont) extends Cont
case class AppArgK(e2: Expr, env: Env, k: Cont) extends Cont
case class DoAppK(v1: Value, k: Cont) extends Cont
case object MtK extends Cont
```

각 클래스의 이름은 별로 중요하지 않다. 각각이 어떤 일을 하는 계속을 나타내는지 잘 드러내는 이름을 붙였을 뿐, 진짜 중요한 것은 각 계속이 가지고 있는 정보이다. 마지막 유형의 계속은 원래는 빈 튜플로 표현되었지만, Scala의 대수적 데이터 타입에서 아무런 추가적인 정보도 없는 값을 표현할 때는 대개 단독 객체를 사용하므로 그렇게 했다. 원한다면 `case class MtK() extends Cont`라 해도 뜻하는 바가 완전히 같지만, 구현 측면에서는 단독 객체를 사용하는 것이 조금 더 효율적이다. 위 구현에는 더 이상 일급 함수가 사용되지 않는다.

이제 `continue` 함수를 수정할 시간이다. 기존 `continue`의 구현은 `def continue(k: Cont, v: Value): Value = k(v)`이다. 이는 `Cont`가 값에서 값으로 가는 함수 타입이기에 가능한 구현이었다. 이제는 `Cont`가 함수가 아니므로 `continue`를 그에 맞춰 고쳐야 한다. `continue`를 어떻게 고쳐야 하는지는 앞에서 이미 다 설명했다. 기존의 구현에서는 `k`와 `v`가 주어지면 `k`를 `v`에 적용했다. 이제는 `k`와 `v`가 주어지면 주어진 `k`가 무엇인지 확인하여 `k`가 가지고 있는 정보에 따라 알맞은 계산을 하면 된다. 위에서 설명한 내용을 다시 쓰겠다.

* `(e2, env, k)`과 `v1`이 주어짐. 그러면 `e2`, `env`, `k`, `v1`을 모두 알고 있으니 `interp(e2, env, v2 => continue(k, numVAdd(v1, v2)))`를 계산함.
* `(v1, k)`와 `v2`가 주어짐. 그러면 `v1`, `k`, `v2`를 모두 알고 있으니 `continue(k, numVAdd(v1, v2))`를 계산함.
* `(e2, env, k)`와 `v1`이 주어짐. 그러면 `e2`, `env`, `k`, `v1`을 모두 알고 있으니 `interp(e2, env, v2 => v1 match { ... })`를 계산함.
* `(v1, k)`와 `v2`가 주어짐. 그러면 `v1`, `k`, `v2`를 모두 알고 있으니 `v1 match { ... }`를 계산함.
* `()`와 `v`가 주어짐. 그러면 `v`를 알고 있으니 `v`를 계산함.

다만, 계속이 더 이상 함수로 표현되지 않는데도, 첫 번째와 세 번째 유형에 대한 설명에서는 계속을 함수로 표현하고 있다. 약간의 수정이 필요하다. `v2 => continue(k, numVAdd(v1, v2))` 대신 이 계속을 표현하는 값인 `(v1, k)`를 사용해야 하고, `v2 => v1 match { ... }` 대신 이 계속을 표현하는 값인 `(v1, k)`를 사용해야 한다.

* `(e2, env, k)`과 `v1`이 주어짐. 그러면 `e2`, `env`, `k`, `v1`을 모두 알고 있으니 `interp(e2, env, DoAddK(v1, k))`를 계산함.
* `(e2, env, k)`와 `v1`이 주어짐. 그러면 `e2`, `env`, `k`, `v1`을 모두 알고 있으니 `interp(e2, env, DoAppK(v1, k))`를 계산함.

이에 따른 구현은 아래와 같다.

```scala
def continue(k: Cont, v: Value): Value = k match {
  case AddSecondK(e2, env, k) => interp(e2, env, DoAddK(v, k))
  case DoAddK(v1, k) => continue(k, numVAdd(v1, v))
  case AppArgK(e2, env, k) => interp(e2, env, DoAppK(v, k))
  case DoAppK(v1, k) => v1 match {
    case CloV(xv1, ev1, sigmav1) =>
      interp(ev1, sigmav1 + (xv1 -> v), k)
    case ContV(k) => continue(k, v)
  }
  case MtK => v
}
```

위의 설명을 그대로 코드로 옮긴 것이므로 어려울 것이 없다.

`interp` 함수 역시 약간의 수정이 필요하다. 이전에는 함수로 표현한 계속을 위에서 정의한 `Cont`에 맞게 바꿔 주어야 한다.

```scala
def interp(e: Expr, env: Env, k: Cont): Value = e match {
  case Num(n) => continue(k, NumV(n))
  case Id(x) => continue(k, lookup(x, env))
  case Fun(x, b) => continue(k, CloV(x, b, env))
  case Add(e1, e2) => interp(e1, env, AddSecondK(e2, env, k))
  case App(e1, e2) => interp(e1, env, AppArgK(e2, env, k))
  case Vcc(x, b) => interp(b, env + (x -> ContV(k)), k)
}
```

`Add`와 `App` 경우만 변화가 있다. `Add` 경우에는 `interp`의 인자로 `v1 => interp(e2, env, v2 => continue(k, numVAdd(v1, v2)))` 대신 이 계속을 표현하는 값인 `AddSecondK(e2, env, k)`가 사용되었고, `App` 경우에는 `interp`의 인자로 `v1 => interp(e2, env, v2 => v1 match { ... })` 대신 이 계속을 표현하는 값인 `AppArgK(e2, env, k)`가 사용되었다. 수정된 인터프리터의 어디에도 일급 함수가 사용되지 않았다.

`interp` 함수가 여전히 잘 작동하는지 확인하기 위해 이전에 사용한 예시를 다시 사용해 보자.

```scala
// 1 + (vcc x in ((x 2) + 3))
interp(
  Add(
    Num(1),
    Vcc("x",
      Add(
       App(Id("x"), Num(2)),
       Num(3)
      )
    )
  ),
  Map.empty,
  MtK
)
// 3

// vcc x in
//   (vcc y in
//     x (1 + (vcc z in y z))
//   ) 3
interp(
  Vcc("x",
    App(
      Vcc("y",
        App(
          Id("x"),
          Add(
            Num(1),
            Vcc("z",
              App(Id("y"), Id("z"))
            )
          )
        )
      ),
      Num(3)
    )
  ),
  Map.empty,
  MtK
)
// 4
```

`interp`에 주어지는 최초의 계속이 항등 함수 대신 이를 표현하는 값인 `MtK`라는 점만 제외하면 이전과 완전히 같다. 결과 역시 올바르게 나온다.

## KFAE의 큰 걸음 의미

새롭게 구현한 KFAE의 인터프리터를 바탕으로, KFAE의 의미를 큰 걸음 의미 방식으로 정의해 보겠다. 이미 구현이 있으므로 이 구현을 수학적인 표현으로 다시 쓰기만 하면 된다. KFAE의 의미에는 두 종류의 명제가 있다. 첫 번째는 \(\sigma,\kappa\vdash e\Rightarrow v\)로, 이는 \({\tt interp}(e,\sigma,\kappa)\)의 결과가 \(v\)임을 뜻한다. 두 번째는 \(v_1\mapsto\kappa\Downarrow v_2\)로, \({\tt continue}(\kappa, v_1)\)의 결과가 \(v_2\)임을 뜻한다.

우선 KFAE의 요약 문법부터 다시 보자.

\[
\begin{array}{lrcl}
\text{Expression} & e & ::= & n \\
&&|& e+e \\
&&|& x \\
&&|& \lambda x.e \\
&&|& e\ e \\
&&|& \textsf{vcc}\ x;\ e \\
\text{Value} & v & ::= & n \\
&&|& \langle\lambda x.e,\sigma\rangle \\
&&|& \kappa \\
\end{array}
\]

메타변수 \(\kappa\)는 계속을 나타낸다. 계속을 아직 정의하지 않았으므로, 계속을 정의하는 것이 첫 번째 할 일이다. 아까의 구현을 참고하면 된다.

```scala
sealed trait Cont
case class AddSecondK(e2: Expr, env: Env, k: Cont) extends Cont
case class DoAddK(v1: Value, k: Cont) extends Cont
case class AppArgK(e2: Expr, env: Env, k: Cont) extends Cont
case class DoAppK(v1: Value, k: Cont) extends Cont
case object MtK extends Cont
```

\[
\begin{array}{lrcl}
\text{Continuation} & \kappa & ::= & [\square+(e,\sigma)]::\kappa \\
&&|& [v+\square]::\kappa \\
&&|& [\square\ (e,\sigma)]::\kappa \\
&&|& [v\ \square]::\kappa \\
&&|& [\square] \\
\end{array}
\]

계속의 표기법은 각 계속이 어떤 일을 하는지 직관적으로 이해하기 쉽도록 적당히 정한 것이다. 다른 어떤 표기법을 사용해도 상관없다. 예를 들면, 구현과의 유사성이 더 잘 느껴지도록 아래와 같이 쓸 수도 있다.

\[
\begin{array}{lrcl}
\text{Continuation} & \kappa & ::= & +(e,\sigma,\kappa) \\
&&|& +(v,\kappa) \\
&&|& @(e,\sigma,\kappa) \\
&&|& @(v,\kappa) \\
&&|& () \\
\end{array}
\]

이 글에서는 처음 정의한 표기법을 따르도록 하겠다.

이제 \(\sigma,\kappa\vdash e\Rightarrow v\)를 증명할 수 있는 추론 규칙을 정의하겠다. `interp`의 구현을 참고하면 된다.

```scala
def interp(e: Expr, env: Env, k: Cont): Value = e match {
  case Num(n) => continue(k, NumV(n))
  case Id(x) => continue(k, lookup(x, env))
  case Fun(x, b) => continue(k, CloV(x, b, env))
  case Add(e1, e2) => interp(e1, env, AddSecondK(e2, env, k))
  case App(e1, e2) => interp(e1, env, AppArgK(e2, env, k))
  case Vcc(x, b) => interp(b, env + (x -> ContV(k)), k)
}
```

\[
\frac
{ n\mapsto\kappa\Downarrow v }
{ \sigma,\kappa\vdash n\Rightarrow v }
\]

결론인 \(\sigma,\kappa\vdash n\Rightarrow v\)는 \({\tt interp}(n, \sigma, \kappa)\)의 결과가 \(v\)라는 뜻이다. \({\tt interp}(n, \sigma, \kappa)\)의 결과는 \({\tt continue}(\kappa, {\tt NumV}(n))\)의 결과와 같다. 즉, \({\tt continue}(\kappa, {\tt NumV}(n))\)의 결과가 \(v\)이면 \({\tt interp}(e, \sigma, \kappa)\)의 결과도 \(v\)이다. \({\tt continue}(\kappa, {\tt NumV}(n))\)의 결과가 \(v\)라는 것은 \(n\mapsto\kappa\Downarrow v\)라 쓴다.

\[
\frac
{ x\in{\it Domain}(\sigma) \quad \sigma(x)\mapsto\kappa\Downarrow v }
{ \sigma,\kappa\vdash x\Rightarrow v }
\]

\[
\frac
{ \langle\lambda x.e,\sigma\rangle\mapsto\kappa\Downarrow v }
{ \sigma,\kappa\vdash \lambda x.e\Rightarrow v }
\]

변수와 함수의 의미를 정의하는 규칙은 정수의 의미를 정의하는 규칙과 비슷하다.

\[
\frac
{ \sigma,[\square+(e_2,\sigma)]::\kappa\vdash e_1\Rightarrow v }
{ \sigma,\kappa\vdash e_1+e_2\Rightarrow v }
\]

결론인 \(\sigma,\kappa\vdash e_1+e_2\Rightarrow v\)는 \({\tt interp}({\tt Add}(e_1,e_2), \sigma, \kappa)\)의 결과가 \(v\)라는 뜻이다. \({\tt interp}({\tt Add}(e_1,e_2), \sigma, \kappa)\)의 결과는 \({\tt interp}(e_1, \sigma, {\tt AddSecondK}(e_2, \sigma, \kappa))\)의 결과와 같다. \({\tt AddSecondK}(e_2, \sigma, \kappa)\)는 \([\square+(e_2,\sigma)]::\kappa\)라고 표기하기로 약속했다. \({\tt interp}(e_1, \sigma, \kappa')\)의 결과가 \(v\)라는 것은 \(\sigma,\kappa'\vdash e_1\Rightarrow v\)라 쓰며, \(\kappa'\)은 \([\square+(e_2,\sigma)]::\kappa\)이다.

\[
\frac
{ \sigma,[\square\ (e_2,\sigma)]::\kappa\vdash e_1\Rightarrow v }
{ \sigma,\kappa\vdash e_1\ e_2\Rightarrow v }
\]

함수 적용의 의미를 정의하는 규칙은 덧셈의 의미를 정의하는 규칙과 비슷하다.

\[
\frac
{ \sigma[x\mapsto\kappa],\kappa\vdash e\Rightarrow v }
{ \sigma,\kappa\vdash {\sf vcc}\ x;\ e\Rightarrow v }
\]

결론인 \(\sigma,\kappa\vdash {\sf vcc}\ x;\ e\Rightarrow v\)는 \({\tt interp}({\tt Vcc}(x,e), \sigma, \kappa)\)의 결과가 \(v\)라는 뜻이다. \({\tt interp}({\tt Vcc}(x,e), \sigma, \kappa)\)의 결과는 \({\tt interp}(e, \sigma[x\mapsto\kappa], \kappa)\)의 결과와 같다. \({\tt interp}(e, \sigma[x\mapsto\kappa], \kappa)\)의 결과가 \(v\)라는 것을 \(\sigma[x\mapsto\kappa],\kappa\vdash e\Rightarrow v\)라 쓴다.

마지막으로 \(v_1\mapsto\kappa\Downarrow v_2\)를 증명할 수 있는 추론 규칙을 정의하겠다. `continue`의 구현을 참고하면 된다.

```scala
def continue(k: Cont, v: Value): Value = k match {
  case AddSecondK(e2, env, k) => interp(e2, env, DoAddK(v, k))
  case DoAddK(v1, k) => continue(k, numVAdd(v1, v))
  case AppArgK(e2, env, k) => interp(e2, env, DoAppK(v, k))
  case DoAppK(v1, k) => v1 match {
    case CloV(xv1, ev1, sigmav1) =>
      interp(ev1, sigmav1 + (xv1 -> v), k)
    case ContV(k) => continue(k, v)
  }
  case MtK => v
}
```

\[
\frac
{ \sigma,[v_1+\square]::\kappa\vdash e_2\Rightarrow v_2 }
{ v_1\mapsto[\square+(e_2,\sigma)]::\kappa\Downarrow v_2 }
\]

결론인 \(v_1\mapsto[\square+(e_2,\sigma)]::\kappa\Downarrow v_2\)는 \({\tt continue}({\tt AddSecondK}(e_2, \sigma, \kappa), v_1)\)의 결과가 \(v_2\)라는 뜻이다. \({\tt continue}({\tt AddSecondK}(e_2, \sigma, \kappa), v_1)\)의 결과는 \({\tt interp}(e_2, \sigma, {\tt DoAddK}(v_1, \kappa))\)의 결과와 같다. \({\tt DoAddK}(v_1, \kappa)\)는 \([v_1+\square]\)라 표기하며, \({\tt interp}(e_2, \sigma, \kappa')\)의 결과가 \(v_2\)라는 것은 \(\sigma,\kappa'\vdash e_2\Rightarrow v_2\)라 쓴다. 여기서 \(\kappa'\)은 \([v_1+\square]\)이다.

\[
\frac
{ n_1+n_2\mapsto\kappa\Downarrow v }
{ n_2\mapsto[n_1+\square]::\kappa\Downarrow v }
\]

결론인 \(n_2\mapsto[n_1+\square]::\kappa\Downarrow v\)는 \({\tt continue}({\tt DoAddK}({\tt NumV}(n_1), \kappa), {\tt NumV}(n_2))\)의 결과가 \(v\)라는 뜻이다. \({\tt continue}({\tt DoAddK}({\tt NumV}(n_1), \kappa), {\tt NumV}(n_2))\)의 결과는 \({\tt continue}(\kappa, {\tt numVAdd}({\tt NumV}(n_1), {\tt NumV}(n_2)))\)의 결과와 같다. 여기서 \({\tt numVAdd}({\tt NumV}(n_1), {\tt NumV}(n_2))\)는 \({\tt NumV}(n_1+n_2)\)이다. \({\tt continue}(\kappa, {\tt NumV}(n_1+n_2))\)의 결과가 \(v\)라는 것은 \(n_1+n_2\mapsto\kappa\Downarrow v\)라 쓴다.

\[
\frac
{ \sigma,[v_1\ \square]::\kappa\vdash e\Rightarrow v_2 }
{ v_1\mapsto[\square\ (e,\sigma)]::\kappa\Downarrow v_2 }
\]

계속이 \([\square+(e_2,\sigma)]\)일 때와 비슷하다.

\[
\frac
{ \sigma[x\mapsto v_2],\kappa\vdash e\Rightarrow v }
{ v_2\mapsto[\langle\lambda x.e,\sigma\rangle\ \square]::\kappa\Downarrow v }
\]

결론인 \(v_2\mapsto[\langle\lambda x.e,\sigma\rangle\ \square]::\kappa\Downarrow v\)는 \({\tt continue}({\tt DoAppK}({\tt CloV}(x,e,\sigma), \kappa), v_2)\)의 결과가 \(v\)라는 뜻이다. \({\tt continue}({\tt DoAppK}({\tt CloV}(x,e,\sigma), \kappa), v_2)\)의 결과는 \({\tt interp}(e, \sigma[x\mapsto v_2], \kappa)\)의 결과와 같다. \({\tt interp}(e, \sigma[x\mapsto v_2], \kappa)\)의 결과가 \(v\)라는 것은 \(\sigma[x\mapsto v_2],\kappa\vdash e\Rightarrow v\)라 쓴다.

\[
\frac
{ v_2\mapsto\kappa_1\Downarrow v }
{ v_2\mapsto[\kappa_1\ \square]::\kappa\Downarrow v }
\]

결론인 \(v_2\mapsto[\kappa_1\ \square]::\kappa\Downarrow v\)는 \({\tt continue}({\tt DoAppK}({\tt ContV}(\kappa_1), \kappa), v_2)\)의 결과가 \(v\)라는 뜻이다. \({\tt continue}({\tt DoAppK}({\tt ContV}(\kappa_1), \kappa), v_2)\)의 결과는 \({\tt continue}(\kappa_1, v_2)\)의 결과와 같다. \({\tt continue}(\kappa_1, v_2)\)의 결과가 \(v\)라는 것은 \(v_2\mapsto\kappa_1\Downarrow v\)라 쓴다.

\[
v\mapsto[\square]\Downarrow v
\]

\(v\mapsto[\square]\Downarrow v\)는 \({\tt continue}({\tt MtK}, v)\)의 결과가 \(v\)라는 뜻이며, \({\tt continue}({\tt MtK}, v)\)의 결과는 \(v\)이다.

## 감사의 말

글을 확인하고 의견을 주신 류석영 교수님께 감사드립니다.
