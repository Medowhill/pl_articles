이번 글에서는 재귀 함수를 정의할 수 있는 RFAE를 정의한다.

## CFAE

CFAE는 FAE에 조건식을 추가한 언어이다.

### 문법

다음은 CFAE의 요약 문법이다. FAE와 비교하여 추가된 부분만 정의하였다.

\[
\begin{array}{lrcl}
\text{Expression} & e & ::= & \cdots \\
&& | & \textsf{if0}\ e\ e\ e
\end{array}
\]

\(\textsf{if0}\)은 지난 글에서 정의한 BAE의 \(\textsf{if}\)와 비슷한 조건식이지만, CFAE에는 불 값이 없기에 조건식의 조건은 아무 값이나 될 수 있다. 만약 조건이 \(0\)이면, 참 가지가 계산된다. 조건이 \(0\)이 아니면, 즉 \(0\)이 아닌 정수거나 클로저이면, 거짓 가지가 계산된다.

### 의미

다음 두 추론 규칙은 조건식의 의미를 정의한다.

\[
\frac
{ \sigma\vdash e_1\Rightarrow 0 \quad \sigma\vdash e_2\Rightarrow v }
{ \sigma\vdash \textsf{if0}\ e_1\ e_2\ e_3\Rightarrow v}
\]

\[
\frac
{ \sigma\vdash e_1\Rightarrow v' \quad v'\not=0 \quad \sigma\vdash e_3\Rightarrow v }
{ \sigma\vdash \textsf{if0}\ e_1\ e_2\ e_3\Rightarrow v}
\]

\(\textsf{if}\)의 의미와 유사하다. 언제나 참 가지나 거짓 가지 둘 중 하나만 계산된다.

## 재귀

CFAE로 계승을 계산하는 함수를 구현할 수 있을까? (CFAE에 곱셈이 있다고 가정하자.) 먼저 함수형으로 구현한 계승 함수를 생각해보자. 다음 Scala 코드는 계승을 계산한다.

```scala
def factorial(n: Int): Int =
  if (n == 0) 1
  else n * factorial(n – 1)
```

이는 다음처럼 CFAE로 옮겨질 것 같다.

\[\textsf{val}\ factorial=\lambda n.\textsf{if0}\ n\ 1\ (n\times(factorial\ (n-1)))\ \textsf{in}\ \cdots\]

그러나, 이 식은 올바르지 않다. \(factorial\)의 묶는 등장의 영역은 \(\cdots\) 부분만 포함하기 때문에, 함수를 정의할 때는 \(factorial\)을 사용할 수 없으며, 람다 요약 안에 있는 \(factorial\)은 묶인 등장이 아닌 자유 식별자이다. 따라서, CFAE에서는 재귀 함수를 정의할 수 없다.

## RFAE

RFAE는 CFAE에 재귀 함수를 추가한 언어이다.

### 문법

다음은 RFAE의 요약 문법이다. CFAE와 비교하여 추가된 부분만 정의하였다.

\[
\begin{array}{lrcl}
\text{Expression} & e & ::= & \cdots \\
&& | & {\sf def}\ x(x)=e\ {\sf in}\ e
\end{array}
\]

\({\sf def}\ x_1(x_2)=e_1\ {\sf in}\ e_2\)는 재귀 함수를 정의한다. \(x_1\)은 함수의 이름으로, \(e_1\)과 \(e_2\) 모두에서 사용할 수 있다. 예를 들면, 계승 함수는 다음과 같이 정의하고 사용할 수 있다.

\[{\sf def}\ factorial(n)=\textsf{if0}\ n\ 1\ (n\times(factorial\ (n-1)))\ {\sf in}\ factorial\ 10\]

### 의미

다음 추론 규칙은 재귀 함수의 의미를 정의한다.

\[
\frac
{ \sigma'=\sigma\lbrack x_1\mapsto\langle\lambda x_2.e_1,\sigma'\rangle\rbrack \quad
  \sigma'\vdash e_2\Rightarrow v
}
{ \sigma\vdash {\sf def}\ x_1(x_2)=e_1\ {\sf in}\ e_2\Rightarrow v}
\]

람다 요약을 사용하여 만든 클로저와 비슷하지만, 현재 환경을 클로저에 저장하는 대신, 현재 환경에 함수의 이름이 클로저를 가리킨다는 정보를 추가한 환경을 저장한다. 클로저를 호출하면, 클로저의 몸통은 클로저의 환경 아래에서 계산되므로, 클로저 몸통에서 문제없이 재귀 호출을 할 수 있다.

아래의 증명 나무는 1의 계승이 1임을 증명한다. 가독성을 위해 세 부분으로
나누었다.

\[
\begin{array}{rcl}
\sigma_1&=&\lbrack f\mapsto\langle\lambda n.\textsf{if0}\ n\ 1\ (n\times(f\ (n-1))),\sigma_1\rangle\rbrack \\
\sigma_2&=&\sigma_1\lbrack n\mapsto 1\rbrack \\
&=&\lbrack f\mapsto\langle\lambda n.\textsf{if0}\ n\ 1\ (n\times(f\ (n-1))),\sigma_1\rangle,n\mapsto 1\rbrack \\
\sigma_3&=&\sigma_1\lbrack n\mapsto 0\rbrack \\
&=&\lbrack f\mapsto\langle\lambda n.\textsf{if0}\ n\ 1\ (n\times(f\ (n-1))),\sigma_1\rangle,n\mapsto 0\rbrack \\
\end{array}
\]

위 사실을 가정하자.

\[
\frac
{
  \Large
  \frac
  { f\in\mathit{Domain}(\sigma_2) }
  { \sigma_2\vdash f\Rightarrow \langle\lambda n.\textsf{if0}\ n\ 1\ (n\times(f\ (n-1))),\sigma_1\rangle }
  \quad
  \frac
  {
    \frac
    { n\in\mathit{Domain}(\sigma_2) }
   { \sigma_2\vdash n\Rightarrow 1 } \quad
    \sigma_2\vdash 1\Rightarrow 1
  }
  { \sigma_2\vdash n-1\Rightarrow 0 } \quad
  \frac
  {
    \frac
    { n\in\mathit{Domain}(\sigma_3) }
    { \sigma_3\vdash n\Rightarrow 0 } \quad
    \sigma_3\vdash 1\Rightarrow 1
  }
  { \sigma_3\vdash \textsf{if0}\ n\ 1\ (n\times(f\ (n-1))) \Rightarrow 1 }
}
{ \sigma_2\vdash f\ (n-1)\Rightarrow 1 }
\]

\[
\frac
{
  \Large
  \frac
  { f\in\mathit{Domain}(\sigma_1)}
  { \sigma_1\vdash f\Rightarrow\langle\lambda n.\textsf{if0}\ n\ 1\ (n\times(f\ (n-1))),\sigma_1\rangle }
  \quad
  {\normalsize \emptyset\vdash 1\Rightarrow 1}
  \quad
  \frac
  { 
    \frac
    { n\in\mathit{Domain}(\sigma_2) }
    { \sigma_2\vdash n\Rightarrow 1 } \quad
    \frac
    {
      \frac
      { n\in\mathit{Domain}(\sigma_2) }
      { \sigma_2\vdash n\Rightarrow 1 } \quad
      \sigma_2\vdash f\ (n-1)\Rightarrow 1
    }
    { \sigma_2\vdash (n\times(f\ (n-1)))\Rightarrow 1 }
  }
  {\sigma_2\vdash\textsf{if0}\ n\ 1\ (n\times(f\ (n-1)))\Rightarrow 1 }
}
{ \sigma_1\vdash f\ 1\Rightarrow 1 }
\]

\[
\frac
{
  \sigma_1=\lbrack f\mapsto\langle\lambda n.\textsf{if0}\ n\ 1\ (n\times(f\ (n-1))),\sigma_1\rangle\rbrack
  \quad
  \sigma_1\vdash f\ 1\Rightarrow 1
}
{\emptyset\vdash
{\sf def}\ f(n)=\textsf{if0}\ n\ 1\ (n\times(f\ (n-1)))\ {\sf in}\ f\ 1
\Rightarrow 1
}
\]

### 인터프리터 구현

다음은 RFAE의 요약 문법과 환경을 Scala 코드로 표현한 것이다.

```scala
sealed trait Expr
case class Num(n: Int) extends Expr
case class Add(l: Expr, r: Expr) extends Expr
case class Sub(l: Expr, r: Expr) extends Expr
case class Mul(l: Expr, r: Expr) extends Expr
case class Id(x: String) extends Expr
case class Fun(x: String, b: Expr) extends Expr
case class App(f: Expr, a: Expr) extends Expr
case class If0(c: Expr, t: Expr, f: Expr) extends Expr
case class Rec(f: String, x: String, b: Expr, e: Expr) extends Expr

sealed trait Value
case class NumV(n: Int) extends Value
case class CloV(p: String, b: Expr, var e: Env) extends Value

type Env = Map[String, Value]
def lookup(x: String, env: Env): Value =
  env.getOrElse(x, throw new Exception)
```

`If0`은 조건식, `Rec`은 재귀 함수에 해당한다. `CloV`가 가지고 있는 환경은 수정 가능하다. 환경에 클로저 자신이 저장되기 위해서는 환경을 수정 가능하게 정의해야 한다.

```scala
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
  case Mul(l, r) =>
    val NumV(n) = interp(l, env)
    val NumV(m) = interp(r, env)
    NumV(n * m)
  case Id(x) => lookup(x, env)
  case Fun(x, b) => CloV(x, b, env)
  case App(f, a) =>
    val CloV(x, b, fEnv) = interp(f, env)
    interp(b, fEnv + (x -> interp(a, env)))
  case If0(c, t, f) =>
    interp(
      if (interp(c, env) == NumV(0)) t else f,
      env
    )
  case Rec(f, x, b, e) =>
    val cloV = CloV(x, b, env)
    val nenv = env + (f -> cloV)
    cloV.e = nenv
    interp(e, nenv)
}
```

`If0`인 경우, 조건이 `NumV(0)`이면 참 가지, 아니면 거짓 가지를 계산한다. `Rec`인 경우, 클로저를 만든 뒤 클로저의 환경에 클로저 자신을 추가한다.

다음은 `interp` 함수를 호출하여 3의 계승을 구한 것이다.

```scala
// def f(n) = if0 n 1 (n * (f (n-1))) in f(3)
interp(
  Rec(
    "f", "n",
    If0(Id("n"),
        Num(1),
        Mul(
          Id("n"),
          App(Id("f"), Sub(Id("n"), Num(1)))
        )
    ),
    App(Id("f"), Num(3))
  ),
  Map.empty
)
// NumV(6)
```

## 재귀 함수 인코딩

람다 대수는 Turing 완전하기에, 재귀 함수 역시 람다 대수로 인코딩 될 수 있다. FAE와 CFAE는 람다 대수보다 더 많은 기능을 제공하므로, FAE와 CFAE 역시 재귀 함수를 인코딩 할 수 있다.

\[
\begin{array}{rcl}
Z&\equiv&\lambda f.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\\
\mathit{encode}({\sf def}\ x_1(x_2)=e_1\ {\sf in}\ e_2)&=&
(\lambda x_1.e_2)\ (Z\ \lambda x_1.\lambda x_2.e_1)
\end{array}
\]

\(Z\)는 *고정점 조합자*(fixed point combinator)이다. \(Z\)는 인자로 받은 함수의 고정점을 계산한다. 어떤 함수의 고정점은 그 함수에 인자로 들어왔을 때 인자와 같은 값이 함수의 결괏값이 되는 값이다. 즉, 함수 \(f\)의 고정점은 \(f(x)=x\)를 만족하는 \(x\)이다. 따라서, \(Z\)에 주어진 함수의 고정점이 어떤 재귀 함수라면, \(Z\)를 적용한 결과가 그 재귀 함수이다. 예를 들어, \(\lambda f.\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1)))\)을 생각해보자. \(f\)가 계승 함수일 때 \(\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1)))\) 역시 계승 함수이다. 그렇기에, \(\lambda f.\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1)))\)의 고정점은 계승 함수이므로, \(Z\ \lambda f.\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1)))\)도 계승 함수이다.

고정점 조합자가 어떻게 작동하는지 이해해보자. \(Z\ \lambda f.\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1)))\)는 \(f\)가 \(\lambda f.\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1)))\)일 때, \( (\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\)와 같다. 이는 \(f\ \lambda v.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\ v\)와 같다. \(f\)를 인자에 적용하면, \(\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (\lambda v.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\ v)\ (x-1)))\)이 된다. 만약 이 함수를 인자 \(0\)에 적용하면, \(x\)가 \(0\)이므로, 결과는 \(1\)이다. 아니라면, 결과는 \(x\times(\lambda v.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\ v)\ (x-1)\)인데, \(\lambda v.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\ v\)가 다시 등장하고 인자의 값이 \(1\) 감소한다. 따라서, 재귀 호출과 같은 일이 일어나며, 계승을 구할 수 있다.

잘 이해가 되지 않는다면 아래의 과정을 천천히 따라가 보자. 1의 계승을 구하는 과정이다.

\[
\begin{array}{rll}
& Z\ (\lambda f.\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1))))\ 1 \\
=&
(\lambda f.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v))\ (\lambda f.\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1))))\ 1 \\
&& (f\leftarrow\lambda f.\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1)))) \\
\rightarrow &
(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\ 1 & (f=\lambda f.\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1)))) \\
&& (x\leftarrow\lambda x.f\ \lambda v.x\ x\ v) \\
\rightarrow &
f\ (\lambda v.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\ v)\ 1 \\
= &
(\lambda f.\lambda x.\textsf{if0}\ x\ 1\ (x\times(f\ (x-1))))\ (\lambda v.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\ v)\ 1\\
&& (f\leftarrow\lambda v.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\ v) \\
\rightarrow &
(\lambda x.\textsf{if0}\ x\ 1\ (x\times((\lambda v.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\ v)\ (x-1))))\ 1 \\
&& (x\leftarrow 1) \\
\rightarrow &
1\times((\lambda v.(\lambda x.f\ \lambda v.x\ x\ v)\ (\lambda x.f\ \lambda v.x\ x\ v)\ v)\ 0)
\end{array}
\]

`interp` 함수를 호출하여 고정점 조합자가 잘 작동하는지 확인할 수 있다.

```scala
// lambda f.(lambda x.f lambda v.x x v) (lambda x.f lambda v.x x v)
val Z =
  Fun("f",
    App(
      Fun("x",
        App(
          Id("f"),
          Fun("v", App(App(Id("x"), Id("x")), Id("v")))
        )
      ),
      Fun("x",
        App(
          Id("f"),
          Fun("v", App(App(Id("x"), Id("x")), Id("v")))
        )
      )
    )
  )

// (Z lambda f.lambda n.if0 n 1 (n * (f (n-1)))) 3
interp(
  App(
    App(
      Z,
      Fun("f", Fun("n",
        If0(Id("n"),
            Num(1),
            Mul(
              Id("n"),
              App(Id("f"), Sub(Id("n"), Num(1)))
            )
        )
      ))
    ),
    Num(3)
  ),
  Map.empty
)
// NumV(6)
```

## 감사의 말

글을 확인하고 의견을 주신 류석영 교수님께 감사드립니다.
