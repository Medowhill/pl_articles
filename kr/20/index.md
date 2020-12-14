이번 글에서는 TFAE에 조건식과 재귀 함수를 추가하여 TRFAE를 정의한다. TRFAE의 문법, 동적 의미, 타입 체계를 정의하며 타입 검사기와 인터프리터를 구현할 것이다.

## 재귀 함수

FAE에서는 람다 요약과 함수 적용만으로 재귀 함수를 표현할 수 있었다. 그 방법은 고정점 조합자를 사용하는 것이다. 다음은 이전에 본 재귀 함수의 인코딩 방법이다.

\[
\begin{array}{rcl}
Z&\equiv&\lambda f.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\\
\mathit{encode}({\sf def}\ x_1(x_2)=e_1\ {\sf in}\ e_2)&=&
(\lambda x_1.e_2)\ (Z\ \lambda x_1.\lambda x_2.e_1)
\end{array}
\]

예를 들면, \((\lambda f.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v))\ (\lambda f.\lambda n.\textsf{if0}\ n\ 1\ (n\times(f\ (n-1))))\)은 계승 함수이다. 또, \((\lambda f.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v))\ (\lambda f.\lambda x.f\ x)\)는 재귀 함수 형태로 표현하면 \({\sf def}\ f(x)=f\ x\)이므로 호출했을 때 계산이 끝나지 않는 함수이다.

한편, 올바른 타입의 TFAE 식을 계산하면 언제나 유한한 시간 안에 계산이 끝난다고 지난 글에서 언급하였다. TFAE의 그러한 성질에 따라 \((\lambda f.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v))\ (\lambda f.\lambda x.f\ x)\) 같이 호출했을 때 계산이 끝나지 않는 함수는 TFAE에서 표현할 수 없다. 따라서 TFAE에서는 고정점 조합자를 사용할 수 없으며 일반적인 재귀 함수를 정의할 수 없다. 실제로 고정점 조합자를 TFAE에서 사용하기 위해 매개변수 타입을 표시하려 하면 불가능함을 확인할 수 있다. 

한번 계승 함수의 정의를 생각해 보자. \(\lambda f.\lambda n.\textsf{if0}\ n\ 1\ (n\times(f\ (n-1)))\)에 타입 표시를 추가하면 \(\lambda f:\textsf{num}\rightarrow\textsf{num}.\lambda n:\textsf{num}.\textsf{if0}\ n\ 1\ (n\times(f\ (n-1)))\)이다. 이 함수의 타입은 \((\textsf{num}\rightarrow\textsf{num})\rightarrow(\textsf{num}\rightarrow\textsf{num})\)이다. 아직 \(\textsf{if0}\)의 정적 의미를 정의하지 않았지만 직관적으로 받아들일 수 있는 사실이다. 이제 고정점 조합자를 보자. \(\lambda f.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\)에서 \(f\)의 타입은 \((\textsf{num}\rightarrow\textsf{num})\rightarrow(\textsf{num}\rightarrow\textsf{num})\)이 되어야 한다. \(f\)가 인자로 \(\lambda v.x\ x\ v\)를 받으므로 \(\lambda v.x\ x\ v\)의 타입은 \(\textsf{num}\rightarrow\textsf{num}\)이다. 즉, \(v\)의 타입은 \(\textsf{num}\)이며 \(x\ x\ v\)의 타입도 \(\textsf{num}\)이다. 따라서 \(x\ x\)의 타입이 \(\textsf{num}\rightarrow\textsf{num}\)이라는 결론이 나온다. 이를 만족하는 \(x\)의 타입이 있을까? \(x\)를 \(x\)에 적용했으므로 \(x\)는 함수이다. \(x\)의 타입을 \(\tau_1\rightarrow\tau_2\)라고 하자. 그러면 \(\tau_2\)는 \(\textsf{num}\rightarrow\textsf{num}\)이다. 또, \(x\)를 인자로 받으려면 \(\tau_1\)의 타입이 \(\tau_1\rightarrow\tau_2\)가 되어야 한다. 그러나 \(\tau_1\)이 이미 \(\tau_1\rightarrow\tau_2\)의 구성 부품이므로 \(\tau_1=\tau_1\rightarrow\tau_2\)를 만족하는 \(\tau_1\)은 존재하지 않는다. 결론적으로 고정점 조합자는 TFAE에서 사용할 수 없다.

사실 이렇게 복잡하게 생각할 필요도 없다. 고정점 조합자 내부에 \(x\ x\)라는 식이 나온다는 것만으로도 고정점 조합자가 올바른 타입의 TFAE 식이 될 수 없다는 증거가 된다. TFAE에서는 어떤 경우에도 \(x\ x\)처럼 어떤 식을 자기 자신에 적용하는 식은 올바른 타입의 식이 아니다. 그 이유는 위에서 본 것처럼 \(\tau_1=\tau_1\rightarrow\tau_2\)를 만족하는 \(\tau_1\)은 존재하지 않기 때문이다. 이처럼 고정점 조합자가 올바른 타입의 식이 아니라는 사실은 FAE와 비교했을 때 TFAE의 표현력이 낮음을 보여준다.

이 글에서는 재귀 함수를 사용하면서도 타입 검사를 통과할 수 있도록, 재귀 함수를 직접 제공하는 언어인 TRFAE를 정의한다. FAE를 확장하여 RFAE를 정의하는 것은 표현할 수 있는 프로그램을 늘리지 않는다. 단지 프로그래머가 복잡한 프로그램을 더 쉽게 표현할 수 있도록 도울 뿐이다. 반면, TFAE를 확장하여 TRFAE를 정의하는 것은 실제로 표현할 수 있는 프로그램을 늘린다. FAE에서 RFAE로의 확장과 TFAE에서 TRFAE로의 확장은 얼핏 보면 비슷해 보이지만 그 목적에는 큰 차이가 있음을 유의해야 한다.

## 문법

다음은 TRFAE의 요약 문법이다. FAE와 비교하여 다른 부분만 작성하였다.

\[
\begin{array}{lrcl}
\text{Expression} & e & ::= & \cdots \\
&& | & \textsf{if0}\ e\ e\ e \\
&& | & {\sf def}\ x(x:\tau):\tau=e\ {\sf in}\ e \\
\text{Value} & v & ::= & n \\
&& | & \langle \lambda x.e,\sigma \rangle \\
\end{array}
\]

\(\textsf{if0}\ e_1\ e_2\ e_3\)는 RFAE에서와 동일한 의미를 갖는 조건식이다. \(e_1\)의 값이 \(0\)이면 첫 번째 가지 \(e_2\)를 계산한 결과가 전체 결과이고, \(0\)이 아니면 두 번째 가지 \(e_3\)를 계산한 결과가 전체 결과이다.

\({\sf def}\ x_1(x_2:\tau_1):\tau_2=e_1\ {\sf in}\ e_2\)는 재귀 함수를 정의한다. 함수의 이름 \(x_1\)과 함수의 매개변수 \(x_2\) 모두를 함수 몸통 \(e_1\)에서 사용할 수 있다. 이 점까지는 RFAE의 재귀 함수와 같다. 차이점은 \(\tau_1\)과 \(\tau_2\)라는 타입 표시가 추가되었다는 것이다. 각각 재귀 함수의 매개변수와 결과 타입이다. 즉, 이 재귀 함수가 \(\tau_1\) 타입의 값을 인자로 받고 \(\tau_2\) 타입의 값을 결과로 낸다는 의미이다. TFAE의 람다 요약에 매개변수 타입 표시를 타입 검사 시에 사용하는 것처럼, 재귀 함수의 타입 표시 역시 타입 검사 시에 사용하기 위해 추가한 것이다.

지난 글에서 TFAE의 동적 의미를 다룰 때 클로저의 매개변수 타입 표시가 필요 없다는 것을 확인하였다. 따라서 TRFAE와 이후 글에서 볼 언어에서는, 람다 요약에는 매개변수 타입 표시가 있지만 클로저에는 매개변수 타입 표시가 없다.

## 동적 의미

TFAE의 동적 의미를 볼 때 나온 추론 규칙은 다시 적지 않겠다. 추가된 식은 조건식과 재귀 함수뿐이다.

\[
\frac
{ \sigma\vdash e_1\Rightarrow 0 \quad
  \sigma\vdash e_2\Rightarrow v }
{ \sigma\vdash\textsf{if0}\ e_1\ e_2\ e_3\Rightarrow v}
\]

\[
\frac
{ \sigma\vdash e_1\Rightarrow v' \quad
  v'\not=0 \quad
  \sigma\vdash e_3\Rightarrow v }
{ \sigma\vdash\textsf{if0}\ e_1\ e_2\ e_3\Rightarrow v}
\]

\[
\frac
{ \sigma'=\sigma\lbrack x_1\mapsto\langle\lambda x_2.e_1,\sigma'\rangle\rbrack \quad
  \sigma'\vdash e_2\Rightarrow v
}
{ \sigma\vdash{\sf def}\ x_1(x_2:\tau_1):\tau_2=e_1\ {\sf in}\ e_2\Rightarrow v}
\]

RFAE의 추론 규칙과 거의 같다. 유일한 차이점은 재귀 함수에 타입 표시가 있다는 점이지만, 클로저를 만들 때는 타입 표시가 버려지기에 사실상 다른 부분이 없다고 할 수 있다.

## 타입 체계

동적 의미와 마찬가지로 TFAE와 겹치는 부분은 생략하겠다.

\[
\frac
{ \Gamma\vdash e_1:\tau' \quad
  \Gamma\vdash e_2:\tau \quad
  \Gamma\vdash e_3:\tau }
{ \Gamma\vdash\textsf{if0}\ e_1\ e_2\ e_3:\tau}
\]

조건식을 실행할 때 타입 오류가 없으려면 우선 조건을 계산할 때 타입 오류가 없어야 한다. 따라서 조건 \(e_1\)은 올바른 타입의 식이어야 한다. 조건의 계산 결과가 \(0\)이면 첫 가지, 아니면 둘째 가지가 계산되므로 조건의 계산 결과에는 특별한 요구 사항이 없다. 따라서 \(\Gamma\vdash e_1:\tau'\)라고 쓸 수 있으며 이는 \(e_1\)이 올바른 타입의 식이며 어떤 타입이든 상관없다는 의미이다. 한편 \(e_2\)와 \(e_3\) 중 어떤 식이 계산될지 알 수 없다. 또한, TFAE와 마찬가지로 TRFAE에서도 식의 타입은 하나로 결정되어야 한다. 따라서 \(e_2\)와 \(e_3\)의 타입은 \(\tau\)로 같아야 하고 \(\tau\)가 전체 조건식의 타입이다.

\[
\frac
{
  \Gamma\lbrack x_1:\tau_1\rightarrow\tau_2,x_2:\tau_1\rbrack\vdash e_1:\tau_2
  \quad
  \Gamma\lbrack x_1:\tau_1\rightarrow\tau_2\rbrack\vdash e_2:\tau
}
{ \Gamma\vdash{\sf def}\ x_1(x_2:\tau_1):\tau_2=e_1\ {\sf in}\ e_2:\tau}
\]

재귀 함수의 정적 의미는 람다 요약의 정적 의미와 유사하다. 람다 요약의 타입을 계산할 때 함수 몸통의 타입을 계산한 것처럼 재귀 함수의 타입을 계산할 때도 함수 몸통의 타입을 계산해야 한다. 차이점은, 람다 요약의 몸통에서는 매개변수만을 추가적으로 사용할 수 있는 것과 달리 재귀 함수의 몸통에서는 함수 이름 역시 추가적으로 사용할 수 있다는 것이다. 함수 이름은 재귀 함수 전체를 가리키므로 그 타입은 \(\tau_1\rightarrow\tau_2\)이다. 매개변수의 타입은 \(\tau_1\)이다. 따라서 타입 환경에 \(x_1\)의 타입은 \(\tau_1\rightarrow\tau_2\)이고 \(x_2\)의 타입은 \(\tau_1\)이라는 정보를 추가한 채로 \(e_1\)의 타입을 계산하면 된다. \(e_1\)를 계산한 결과가 함수의 결괏값이므로 \(e_1\)의 타입은 함수의 결과 타입인 \(\tau_2\)여야 한다.

다음은 \({\sf def}\ f(n:{\sf num}):{\sf num}={\sf if0}\ n\ 0\ (n+(f\ (n-1)))\ {\sf in}\ f\ 3\)의 타입이 \(\textsf{num}\)임을 증명하는 증명 나무이다. 이 함수는 인자로 어떤 정수를 받아 \(0\)부터 그 정수까지의 합을 계산한다.

\[\Gamma_1=\lbrack f:\textsf{num}\rightarrow\textsf{num},n:\textsf{num}\rbrack\]
\[\Gamma_2=\lbrack f:\textsf{num}\rightarrow\textsf{num}\rbrack\]

\[
\frac
{
  \Large
  \frac
  {
    \frac
    {n\in\mathit{Domain}(\Gamma_1)}
    {\Gamma_1\vdash n:\textsf{num}} \quad
    \Gamma_1\vdash 0:\textsf{num} \quad
    \frac
    {
      \frac
      {n\in\mathit{Domain}(\Gamma_1)}
      { \Gamma_1\vdash n:\textsf{num} } \quad
      \frac
      {
        \frac
        {f\in\mathit{Domain}(\Gamma_1)}
        { \Gamma_1\vdash f:\textsf{num}\rightarrow\textsf{num} } \quad
        \frac
        {
          \frac
          { n\in\mathit{Domain}(\Gamma_1) }
          { \Gamma_1\vdash n:\textsf{num} } \quad
          \Gamma_1\vdash 1:\textsf{num}
        }
        { \Gamma_1\vdash n-1:\textsf{num} } \quad
      }
      { \Gamma_1\vdash f\ (n-1):\textsf{num} }
    }
    { \Gamma_1\vdash n+(f\ (n-1)):\textsf{num} }
  }
  { \Gamma_1\vdash \textsf{if0}\ n\ 0\ (n+(f\ (n-1))):\textsf{num} }
  \quad
  \frac
  {
    \frac
    { f\in{\it Domain}(\Gamma_2) }
    { \Gamma_2\vdash f:\textsf{num}\rightarrow\textsf{num} }
    \quad
    \Gamma_2\vdash 3:\textsf{num}
  }
  { \Gamma_2\vdash f\ 3:\textsf{num} }
}
{
  \emptyset\vdash
  {\sf def}\ f(n:{\sf num}):{\sf num}={\sf if0}\ n\ 0\ (n+(f\ (n-1)))\ {\sf in}\ f\ 3
  :\textsf{num}
}
\]

## 타입 검사기 구현

다음은 TRFAE의 요약 문법을 Scala로 구현한 것이다.

```scala
sealed trait Expr
case class Num(n: Int) extends Expr
case class Add(l: Expr, r: Expr) extends Expr
case class Sub(l: Expr, r: Expr) extends Expr
case class Id(x: String) extends Expr
case class Fun(x: String, t: Type, b: Expr) extends Expr
case class App(f: Expr, a: Expr) extends Expr
case class If0(c: Expr, t: Expr, f: Expr) extends Expr
case class Rec(f: String, x: String, p: Type, r: Type, b: Expr, e: Expr) extends Expr

sealed trait Type
case object NumT extends Type
case class ArrowT(p: Type, r: Type) extends Type

type TEnv = Map[String, Type]

def mustSame(t1: Type, t2: Type): Type =
  if (t1 == t2) t1 else throw new Exception
```

`Expr` 인스턴스는 TRFAE 식을 표현한다. `Fun` 클래스와 `Rec` 클래스에 타입 표시를 위한 필드가 추가된 것을 빼면 RFAE와 같다.

```scala
case If0(c, t, f) =>
  typeCheck(c, env)
  mustSame(typeCheck(t, env), typeCheck(f, env))
```

\[
\frac
{ \Gamma\vdash e_1:\tau' \quad
  \Gamma\vdash e_2:\tau \quad
  \Gamma\vdash e_3:\tau }
{ \Gamma\vdash\textsf{if0}\ e_1\ e_2\ e_3:\tau}
\]

조건은 올바른 타입의 식이어야 한다. `typeCheck` 함수를 호출해 `c`의 타입을 계산하며 그 결과는 사용되지 않는다. 두 가지의 타입은 같아야 한다. `typeCheck` 함수를 호출해 `t`와 `f`의 타입을 계산하며 계산된 타입은 `mustSame` 함수를 통해 비교된다. 같다면 그 타입이 전체 조건식의 타입이다.

```scala
case Rec(f, x, p, r, b, e) =>
  val t = ArrowT(p, r)
  val nenv = env + (f -> t)
  mustSame(r, typeCheck(b, nenv + (x -> p)))
  typeCheck(e, nenv)
```

\[
\frac
{
  \Gamma\lbrack x_1:\tau_1\rightarrow\tau_2,x_2:\tau_1\rbrack\vdash e_1:\tau_2
  \quad
  \Gamma\lbrack x_1:\tau_1\rightarrow\tau_2\rbrack\vdash e_2:\tau
}
{ \Gamma\vdash{\sf def}\ x_1(x_2:\tau_1):\tau_2=e_1\ {\sf in}\ e_2:\tau}
\]

함수의 매개변수 타입은 `p`, 결과 타입은 `r`이다. 따라서 함수 이름 `f`의 타입은 `t`, 즉 `p`에서 `r`로 가는 함수 타입이며 매개변수 이름 `x`의 타입은 `p`이다. 두 정보를 타입 환경에 추가하고 함수 몸통 `b`의 타입을 계산해야 한다. 그 결과는 `r`과 같아야 하므로 `mustSame` 함수를 통해 비교한다. 함수는 함수 몸통인 `b`뿐 아니라 `e`에서도 사용할 수 있다. 그러나 매개변수 `x`는 `e`에서는 사용될 수 없다. 따라서 `e` 타입을 계산할 때는 `f`의 타입만 타입 환경에 추가되어 있으면 된다. 전체 식의 타입은 `e`의 타입과 같다.

다음은 `typeCheck` 함수의 전체 코드이다.

```scala
def typeCheck(e: Expr, env: TEnv): Type = e match {
  case Num(n) => NumT
  case Add(l, r) =>
    mustSame(mustSame(typeCheck(l, env), NumT), typeCheck(r, env))
  case Sub(l, r) =>
    mustSame(mustSame(typeCheck(l, env), NumT), typeCheck(r, env))
  case Id(x) => env(x)
  case Fun(x, t, b) =>
    ArrowT(t, typeCheck(b, env + (x -> t)))
  case App(f, a) =>
    val ArrowT(t1, t2) = typeCheck(f, env)
    val t3 = typeCheck(a, env)
    mustSame(t1, t3)
    t2
  case If0(c, t, f) =>
    typeCheck(c, env)
    mustSame(typeCheck(t, env), typeCheck(f, env))
  case Rec(f, x, p, r, b, e) =>
    val t = ArrowT(p, r)
    val nenv = env + (f -> t)
    mustSame(r, typeCheck(b, nenv + (x -> p)))
    typeCheck(e, nenv)
}
```

다음은 타입 검사기를 사용하여 \({\sf def}\ f(n:{\sf num}):{\sf num}={\sf if0}\ n\ 0\ (n+(f\ (n-1)))\ {\sf in}\ f\ 3\)의 타입을 계산한 것이다.

```scala
// def f(x: num): num = if0 n 0 (n + (f (n-1))); 3
typeCheck(
  Rec(
    "f", "n", NumT, NumT,
    If0(Id("n"),
        Num(0),
        Add(
          Id("n"),
          App(Id("f"), Sub(Id("n"), Num(1)))
        )
    ),
    App(Id("f"), Num(3))
  ),
  Map.empty
)
// num
```

## 인터프리터 구현

인터프리터는 RFAE 인터프리터와 거의 같다.

```scala
sealed trait Value
case class NumV(n: Int) extends Value
case class CloV(p: String, b: Expr, var e: Env) extends Value

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
  case If0(c, t, f) =>
    interp(if (interp(c, env) == NumV(0)) t else f, env)
  case Rec(f, x, _, _, b, e) =>
    val cloV = CloV(x, b, env)
    val nenv = env + (f -> cloV)
    cloV.e = nenv
    interp(e, nenv)
}

def run(e: Expr): Value = {
  typeCheck(e, Map.empty)
  interp(e, Map.empty)
}
```

다음은 인터프리터를 사용하여 \({\sf def}\ f(n:{\sf num}):{\sf num}={\sf if0}\ n\ 0\ (n+(f\ (n-1)))\ {\sf in}\ f\ 3\)의 값을 계산한 것이다.

```scala
// def f(x: num): num = if0 n 0 (n + (f (n-1))); 3
run(
  Rec(
    "f", "n", NumT, NumT,
    If0(Id("n"),
        Num(0),
        Add(
          Id("n"),
          App(Id("f"), Sub(Id("n"), Num(1)))
        )
    ),
    App(Id("f"), Num(3))
  )
)
// 6
```

## 타입 체계 설계

조건식의 현재 정적 의미는 다음과 같다.

\[
\frac
{ \Gamma\vdash e_1:\tau' \quad
  \Gamma\vdash e_2:\tau \quad
  \Gamma\vdash e_3:\tau }
{ \Gamma\vdash\textsf{if0}\ e_1\ e_2\ e_3:\tau}
\]

이를 다음과 같이 수정할 수 있다.

\[
\frac
{ \Gamma\vdash e_1:\textsf{num} \quad
  \Gamma\vdash e_2:\tau \quad
  \Gamma\vdash e_3:\tau }
{ \Gamma\vdash\textsf{if0}\ e_1\ e_2\ e_3:\tau}
\]

조건이 아무 타입이나 가질 수 없고, 정수 타입만 가질 수 있게 바뀌었다. 원래의 타입 체계와 새로운 타입 체계 모두 안전하다. 새로운 타입 체계는 오히려 더 많은 프로그램을 거절한다. 그 프로그램들이 타입 오류를 내지 않음에도 거절하는 것이다. 하지만 이렇게 타입 체계를 바꾸는 것은 합리적인 선택이 될 수 있다. 조건이 정수 타입이 아니라면 절대 그 값이 \(0\)이 될 수 없다. 즉, 첫 번째 가지는 절대 계산되지 않고 언제나 두 번째 가지만 계산된다. 언제나 한 가지만 실행된다면 조건식을 굳이 사용할 이유가 없다. 따라서 조건이 정수 타입이 아니라면 이는 프로그래머가 의도하지 않은 일일 가능성이 높다. 실행 중 타입 오류는 발생하지 않지만 의도하지 않은 결과를 얻을 가능성이 높다는 말이다. 예를 들면, \({\sf def}\ f(n:\textsf{num}):\textsf{num}=\textsf{if0}\ f\ 0\ (n+(f\ (n-1)))\ {\sf in}\ f\ 3\)은 조건이 \(n\)이어야 하지만 실수로 \(f\)를 조건으로 넣은 식이다. 이 식을 계산하면 언제나 두 번째 가지가 선택되어 계산이 끝나지 않는다. 그러므로 조건이 정수 타입인 식만 타입 체계가 받아들이는 것이 버그를 줄이는 안전한 결정이 될 수 있다. 이처럼, 타입 체계를 설계할 때 안전성과 표현력이 고려할 대상의 전부인 것은 아니다. 안전성을 유지하면서 표현력을 높이는 것이 가장 중요한 목표이긴 하지만, 동시에 프로그래머의 실수를 줄일 수 있는 타입 체계를 설계해야 한다.

한편, 조건식의 동적 의미를 다음처럼 수정한다고 생각해 보자.

\[
\frac
{ \sigma\vdash e_1\Rightarrow 0 \quad
  \sigma\vdash e_2\Rightarrow v }
{ \sigma\vdash\textsf{if0}\ e_1\ e_2\ e_3\Rightarrow v}
\]

\[
\frac
{ \sigma\vdash e_1\Rightarrow n \quad
  n\not=0 \quad
  \sigma\vdash e_3\Rightarrow v }
{ \sigma\vdash\textsf{if0}\ e_1\ e_2\ e_3\Rightarrow v}
\]

이 경우 조건을 계산한 결과가 정숫값이 아니면 실행 중 타입 오류가 발생한다. 따라서 다음의 두 규칙 중 첫 규칙은 타입 체계를 안전하지 않게 만들고 두 번째 규칙은 타입 체계를 안전하게 만든다.

\[
\frac
{ \Gamma\vdash e_1:\tau' \quad
  \Gamma\vdash e_2:\tau \quad
  \Gamma\vdash e_3:\tau }
{ \Gamma\vdash\textsf{if0}\ e_1\ e_2\ e_3:\tau}
\]

\[
\frac
{ \Gamma\vdash e_1:\textsf{num} \quad
  \Gamma\vdash e_2:\tau \quad
  \Gamma\vdash e_3:\tau }
{ \Gamma\vdash\textsf{if0}\ e_1\ e_2\ e_3:\tau}
\]

## 감사의 말

글을 확인하고 의견을 주신 류석영 교수님께 감사드립니다. 오타를 찾아 주신
‘요셉’님께 감사드립니다.
