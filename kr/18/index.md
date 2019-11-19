이번 글은 FAE에 *일급 계속*(first-class continuation)을 추가하여 KFAE를 정의한다. 지난 글에서는 FAE의 의미를 계속이 드러나도록 정의하고 인터프리터를 계속 전달 방식을 사용해 구현하였다. 그러나 FAE에서는 계속이 프로그래머가 직접 사용할 수 있는 개념은 아니다. 계속은 식이 계산되는 과정에서 존재할 뿐이다.

프로그래밍 언어에서 어떤 대상이 일급이면 그 대상이 값으로 사용될 수 있다는 뜻이다. 값으로 사용될 수 있으므로 변수의 값이 될 수 있고 함수의 인자나 결괏값도 될 수 있다. 앞에서 본 일급 함수는 값으로 사용할 수 있는 함수였다.

일급 계속은 값으로 사용될 수 있는 계속이다. 언어가 일급 계속을 지원한다면 프로그래머는 어떤 변수의 값을 계속으로 하거나 계속을 함수의 인자나 결괏값으로 사용할 수 있다. 지난 글에서 계속은 값을 받아 값을 내놓는 함수처럼 생각할 수 있다고 했다. 그러므로 프로그래머는 계속을 마치 함수처럼 호출할 수 있다. 그러나 계속은 단순한 함수가 아니다. 계속은 남은 계산 전부를 가리키므로 계속을 호출하여 모두 계산하고 나면 프로그램이 종료된다. 이는 그냥 함수를 호출했을 때 그 결과를 가지고 계산을 이어 나가는 것과는 다르다. 따라서 계속을 호출함으로써 현재의 계속을 다른 시점의 계속으로 바꾸는 효과가 있다. 이는 제어 흐름을 변경하는 효과가 있어 프로그래머가 복잡한 프로그램을 간결하게 표현하는 것을 돕는다.

KFAE의 의미를 먼저 본 뒤 KFAE의 인터프리터를 구현하겠다. 또한, 일급 계속이 어떻게 유용하게 사용될 수 있는지 몇 가지 예시를 보겠다.

## 문법

다음은 KFAE의 요약 문법이다. FAE와 비교하여 추가된 부분만 나타냈다.

\[
\begin{array}{lrcl}
\text{Expression} & e & ::= & \cdots \\
&& | & \textsf{letcc}\ x\ \textsf{in}\ e \\
\end{array}
\]

\(\textsf{letcc}\ x\ \textsf{in}\ e\)는 현재 계속을 \(x\)의 값으로 하고 \(e\)를 계산하는 식이다. \(\textsf{letcc}\)의 \(\textsf{cc}\)는 ‘current continuation', 즉 현재 계속을 뜻한다. 여기서 현재 계속은, \(\textsf{letcc}\ x\ \textsf{in}\ e\) 전체가 환원가능식일 때의 계속이다. 다른 말로는, \(\textsf{letcc}\ x\ \textsf{in}\ e\)를 계산할 때의 계속이다. \(x\)의 영역은 \(e\) 전체이다. \(x\)에 해당하는 값이 호출되면, 호출이 일어나는 시점의 계속이 무시되고 \(x\)의 값인 계속이 계산된다.

먼저  \(1+(((\lambda v.1+v)\ 2)+3)\)를 보자. 프로그래머가 코드에 \(\lambda v.1+v\)라고 썼기에 이는 인자를 받아 \(1\)에 더한 값을 결과로 내는 함수이다. 함수를 호출해도 계속에는 영향이 없다. 함수 호출 시의 계속을 바꾸지 않고 함수의 결괏값을 가지고 계산을 이어 나간다. \((\lambda v.1+v)\ 2\)의 계속은 \(\lambda v.1+(v+3)\)이다. 함수 호출의 결과는 \(3\)이므로 계속을 적용하면 \(1+(3+3)\)이 된다. 최종 결과는 \(7\)이다. 

이제 \(1+(\textsf{letcc}\ x\ \textsf{in}\ (x\ 2)+3)\)을 생각해 보자. \(\textsf{letcc}\ x\ \textsf{in}\ (x\ 2)+3\)이 계산될 때 계속은 그 결과를 \(1\)에 더하는 것이다. 함수로 표현하면 \(\lambda v.1+v\)이다. 이 계속이 \(x\)의 값인 채로 \((x\ 2)+3\)이 계산된다. 앞에서도 말한 것처럼 계속은 함수 꼴로 표현될 수 있으나 사용자가 코드에 쓴 람다 요약과는 다르다. \(x\)는 그냥 람다 요약이 아니라 계속이다. \(x\)를 호출하는 것은 현재의 계속을 \(x\)가 나타내는 계속으로 바꾸는 효과를 낸다. 그러므로 \(x\ 2\)를 계산할 때의 원래 계속인 \(\lambda v.1+(v+3)\)는 버려진다. \((\lambda v.1+v)\ 2\)가 남은 계산 전부이다. 이는 \(1+2\)이므로 최종 결과는 \(3\)이다.

위 두 식의 차이를 직관적으로 다음과 같이 정리할 수 있다.

\[
\begin{array}{ccccc}
&\text{해야 하는 계산} & \text{환원가능식} & \text{계속} & \text{환원가능식 계산 결과} \\
& 1+(((\lambda v.1+v)\ 2)+3) & ((\lambda v.1+v)\ 2) & \lambda v.1+(v+3) & 1+2 \\
\rightarrow& 1+((1+2)+3) & 1+2 & \lambda v.1+(v+3) & 3 \\
\rightarrow&1+(3+3) & 3+3 & \lambda v.1+v & 6 \\
\rightarrow& 1+6 & 1+6 & \lambda v.v & 7
\end{array}
\]

\[
\begin{array}{cccccc}
&\text{해야 하는 계산} & \text{환원가능식} & \text{계속} & \text{환원가능식 계산 결과}
& \text{일급 계속} \\
& 1+(\textsf{letcc}\ x\ \textsf{in}\ (x\ 2)+3) & \textsf{letcc}\ x\ \textsf{in}\ (x\ 2)+3 & \lambda v.1+v & (x\ 2)+3 & x=\lambda v.1+v \\
\rightarrow& 1+((x\ 2)+3) & (x\ 2) & \lambda v.1+(v+3) & \text{현재 계속을 버린다}  \\
\rightarrow& x\ 2 & 2 & x & 2 \\
\equiv & x\ 2 & 2 & \lambda v.1+v & 2 \\
\rightarrow& 1+2 & 1+2 & \lambda v.v & 3
\end{array}
\]

\(\equiv\)는 실제로 계산이 일어나지 않은 것으로, 계속이 \(x\)로 바뀐다는 것과 \(x\)가 무엇인지를 모두 명확히 보이기 위하여 위처럼 표현했다.

예시를 하나 더 보겠다.

\[
\begin{array}{cccccc}
&\text{해야 하는 계산} & \text{환원가능식} & \text{계속} & \text{환원가능식 계산 결과}
& \text{일급 계속} \\
& \small{\textsf{letcc}\ x\ \textsf{in}\ (\textsf{letcc}\ y\ \textsf{in}\ x(1+(\textsf{letcc}\ z\ \textsf{in}\ y\ z)))3}
& \small{\textsf{letcc}\ x\ \textsf{in}\ (\textsf{letcc}\ y\ \textsf{in}\ x(1+(\textsf{letcc}\ z\ \textsf{in}\ y\ z)))3}
& \lambda v.v
& \small{(\textsf{letcc}\ y\ \textsf{in}\ x(1+(\textsf{letcc}\ z\ \textsf{in}\ y\ z)))3}
& x=\lambda v.v \\
\rightarrow & (\textsf{letcc}\ y\ \textsf{in}\ x(1+(\textsf{letcc}\ z\ \textsf{in}\ y\ z)))3
& \textsf{letcc}\ y\ \textsf{in}\ x(1+(\textsf{letcc}\ z\ \textsf{in}\ y\ z))
& \lambda v.v\ 3
& x(1+(\textsf{letcc}\ z\ \textsf{in}\ y\ z))
& y=\lambda v.v\ 3 \\
\rightarrow & x(1+(\textsf{letcc}\ z\ \textsf{in}\ y\ z))3
& \textsf{letcc}\ z\ \textsf{in}\ y\ z
& \lambda v.x(1+v)3
& y\ z
& z=\lambda v.x(1+v)3 \\
\rightarrow & x(1+(y\ z))3 & y\ z & \lambda v.x(1+v)3 & \text{현재 계속을 버린다} \\
\rightarrow & y\ z & z & y & z \\
\equiv & y\ z & z & \lambda v.v\ 3 & z \\
\rightarrow & z\ 3 & 3 & z & 3 \\
\equiv & z\ 3 & 3 & \lambda v.x(1+v)3 & 3 \\
\rightarrow & x\ (1+3)\ 3 & 1+3 & \lambda v.x\ v\ 3 & 4\\
\rightarrow & x\ 4\ 3 & x\ 4 & \lambda v.v\ 3 & \text{현재 계속을 버린다} \\
\rightarrow & x\ 4 & 4 & x & 4\\
\equiv & x\ 4 & 4 & \lambda v.v & 4 \\
\end{array}
\]

따라서 \(\textsf{letcc}\ x\ \textsf{in}\ (\textsf{letcc}\ y\ \textsf{in}\ x(1+(\textsf{letcc}\ z\ \textsf{in}\ y\ z)))3\)의 실행 결과는 \(4\)이다.

일급 계속이 나오는 두 식의 계산 과정에서 해야 하는 계산만 정리하면 다음과 같다.

\[
\begin{array}{cl}
& 1+(\textsf{letcc}\ x\ \textsf{in}\ (x\ 2)+3) \\
\rightarrow& 1+((x\ 2)+3)  \\
\rightarrow& x\ 2 \\
\rightarrow& 1+2 \\
\rightarrow& 3 \\
\end{array}
\]

\[
\begin{array}{cl}
& \textsf{letcc}\ x\ \textsf{in}\ (\textsf{letcc}\ y\ \textsf{in}\ x(1+(\textsf{letcc}\ z\ \textsf{in}\ y\ z)))3 \\
\rightarrow & (\textsf{letcc}\ y\ \textsf{in}\ x(1+(\textsf{letcc}\ z\ \textsf{in}\ y\ z)))3\\
\rightarrow & x(1+(\textsf{letcc}\ z\ \textsf{in}\ y\ z))3\\
\rightarrow & x(1+(y\ z))3 \\
\rightarrow & y\ z\\
\rightarrow & z\ 3 \\
\rightarrow & x\ (1+3)\ 3 \\
\rightarrow & x\ 4\ 3 \\
\rightarrow & x\ 4\\
\rightarrow & 4 \\
\end{array}
\]

주목할 점은 두 가지이다. 첫 번째는 \(\textsf{letcc}\) 자체는 아무런 계산도 하지 않는다는 것이다. 현재의 계속을 값으로 만든다는 점만 빼면 몸통을 바로 계산하는 것과 같다. 예를 들면 \(1+(\textsf{letcc}\ x\ \textsf{in}\ (x\ 2)+3)\)은 \(\textsf{letcc}\ x\ \textsf{in}\ (x\ 2)+3\)이 \((x\ 2)+3\)으로 그대로 바뀌므로 \(1+((x\ 2)+3)\)이 된다. 두 번째는 계속을 호출하는 식을 계산해야 하는 순간 계속과 인자를 제외한 부분은 사라진다는 것이다. 예를 들면 \(1+((x\ 2)+3)\)에서는 \(x\ 2\)를 계산해야 하고 \(x\)가 계속이므로 바로 \(x\ 2\)로 바뀐다. 또, \(x(1+(y\ z))3\)에서도 \(y\ z\)를 계산해야 하고 \(y\)가 계속이므로 바로 \(y\ z\)로 바뀐다. 이처럼 나머지 부분을 없애는 것이 현재의 계속을 호출하는 계속으로 바꾸는 것이다.

## 의미

이제 KFAE의 의미를 정의하겠다. 지난 글과 마찬가지로 작은 걸음 의미를 사용한다.

\[
\begin{array}{lrcl}
\text{Value} & v & ::= & \cdots \\
&& | & \langle k,s\rangle \\
\end{array}
\]

일급 계속을 지원하므로 값은 정수와 클로저 이외에도 계속이 될 수 있다. 지난 글에서 계산 스택과 값 스택의 순서쌍이 계속이 된다고 하였다. 따라서 \(\langle k,s\rangle\)가 계속을 표현하는 값이다. 이 계속을 함수로 표현하면 \(\lambda v.\mathit{eval}(k\ ||\ v::s)\)라고 하였다. 여기서 \(\mathit{eval}(k\ ||\ v::s)\)는 \(k\ ||\ v::s\rightarrow^\ast \square\ ||\ v'::\blacksquare\)가 참이 되는 \(v'\)이다. 그러므로 \(\langle k,s\rangle\)를 어떤 값 \(v\)에 적용하는 것은 현재 상태를 \(k\ ||\ v::s\)로 환원하는 것이다.

지난 글에서 정의한 FAE의 작은 걸음 의미를 위한 규칙들은 KFAE에서도 그대로 사용할 수 있다. 거기에 더하여 두 규칙을 추가해야 한다. 추가된 식 \(\textsf{letcc}\ x\ \textsf{in}\ e\)이 환원가능식인 경우에 대한 규칙과 \((@)\)이 해야 하는 계산일 때 함수 위치에 클로저가 아닌 계속이 오는 경우에 대한 규칙이 필요하다.

\[
\sigma\vdash\textsf{letcc}\ x\ \textsf{in}\ e::k\ ||\ s\rightarrow
\sigma[x\mapsto\langle k,s\rangle]\vdash e::k\ ||\ s
\]

\(\textsf{letcc}\ x\ \textsf{in}\ e\)는 \(x\)의 값을 현재의 계속으로 한 뒤 \(e\)를 계산하는 식이다. 현재 상태가 \(\sigma\vdash\textsf{letcc}\ x\ \textsf{in}\ e::k\ ||\ s\)이면 환원가능식이 \(\textsf{letcc}\ x\ \textsf{in}\ e\)이고 계속은 \(\langle k,s\rangle\)이다. 따라서 계산 스택의 가장 위를 \(\sigma[x\mapsto\langle k,s\rangle]\vdash e\)로 바꾸는 것이 환원이다.

계속을 적용하는 경우에 대한 규칙을 정의하기 전에 먼저 클로저를 적용하는 규칙을 다시 보겠다.

\[
(@)::k\ ||\ v::\langle\lambda x.e,\sigma\rangle::s\rightarrow
\sigma[x\mapsto v]\vdash e::k\ ||\ s
\]

값 스택에 클로저 대신 계속이 들어있다고 하자. 그러면 현재 상태는 \((@)::k\ ||\ v::\langle k',s'\rangle::s\)이다. 값 스택에 들어 있는 계속은 \(\langle k',s'\rangle\)이다. \(v\)는 계속에 넘길 인자이다. 계속을 함수로 나타내면 \(\lambda v.\mathit{eval}(k'\ ||\ v::s')\)이다. 따라서 계속을 호출하여 환원된 상태는 \(k'\ ||\ v::s'\)이다. 계속을 호출하기 전 원래의 계속인 \(k\ ||\ s\)가 완전히 사라지는 것을 볼 수 있다. 규칙으로 쓰면 다음이 된다.

\[
(@)::k\ ||\ v::\langle k',s'\rangle::s\rightarrow
k'\ ||\ v::s'
\]

위에서 본 예시를 작은 걸음 의미에 따라 환원해 보겠다. 아래에서 \(\sigma\)는 \(\lbrack x\mapsto\langle(+)::\square\ ||\ 1::\blacksquare\rangle\rbrack\)이다.

\[
\begin{array}{lrcr}
& \emptyset\vdash 1+(\textsf{letcc}\ x\ \textsf{in}\ ((x\ 2)+3))::\square &||& \blacksquare \\
\rightarrow & \emptyset\vdash 1::\emptyset\vdash \textsf{letcc}\ x\ \textsf{in}\ ((x\ 2)+3)::(+)::\square &||& \blacksquare \\
\rightarrow & \emptyset\vdash \textsf{letcc}\ x\ \textsf{in}\ ((x\ 2)+3)::(+)::\square &||& 1::\blacksquare \\
\rightarrow & \sigma\vdash (x\ 2)+3::(+)::\square &||& 1::\blacksquare \\
\rightarrow & \sigma\vdash x\ 2::\sigma\vdash 3::(+)::(+)::\square &||& 1::\blacksquare \\
\rightarrow & \sigma\vdash x::\sigma\vdash 2::(@)::\sigma\vdash 3::(+)::(+)::\square &||& 1::\blacksquare \\
\rightarrow & \sigma\vdash 2::(@)::\sigma\vdash 3::(+)::(+)::\square &||& \langle(+)::\square \ ||\  1::\blacksquare\rangle::1::           \blacksquare \\
\rightarrow & (@)::\sigma\vdash 3::(+)::(+)::\square &||& 2::\langle(+)::\square \ ||\  1::\blacksquare\rangle::1::\blacksquare \\
\rightarrow & (+)::\square &||& 2::1::\blacksquare \\
\rightarrow & \square &||& 3::\blacksquare \\
\end{array}
\]

새로운 규칙이 사용된 환원은 두 곳이다.

\[\begin{array}{lrcr}
& \emptyset\vdash \textsf{letcc}\ x\ \textsf{in}\ ((x\ 2)+3)::(+)::\square &||& 1::\blacksquare \\
\rightarrow & \lbrack x\mapsto\langle(+)::\square\ ||\ 1::\blacksquare\rangle\rbrack\vdash (x\ 2)+3::(+)::\square &||& 1::\blacksquare \\
\end{array}\]

\(\textsf{letcc}\) 식의 몸통을 계산하는 것이 추가되며 환경에 계속인 \(\langle(+)::\square\ ||\ 1::\blacksquare\rangle\)이 추가되었다.

\[\begin{array}{lrcr}
&(@)::\sigma\vdash 3::(+)::(+)::\square &||& 2::\langle(+)::\square \ ||\  1::\blacksquare\rangle::1::\blacksquare \\
\rightarrow & (+)::\square &||& 2::1::\blacksquare \\
\end{array}\]

원래의 계속인 \(\sigma\vdash 3::(+)::(+)::\square\ ||\ 1::\blacksquare\)은 사라지고 호출하는 계속의 값인 \(\langle(+)::\square, 1::\blacksquare\rangle\)에 따라 \((+)::\square\ ||\ 2::1::\blacksquare\)로 환원되는 것을 볼 수 있다.

\(x\)의 값은 \(\langle(+)::\square, 1::\blacksquare\rangle\)이다. \(x\)를 \(v\)에 적용하면 다음이 성립한다.

\[\begin{array}{lrcr}
& (+)::\square &||&  v::1::\blacksquare \\
\rightarrow & \square &||&  1+v::\blacksquare \\
\end{array}\]

이는 위에서 \(x=\lambda v.1+v\)라 표현한 것과 일치한다.

위에서 본 다른 예시이다.

\[
\begin{array}{rcl}
v_1&=&\langle\square,\blacksquare\rangle\\
v_2&=&\langle\sigma_1\vdash3::(@)::\square,\blacksquare\rangle\\
v_3&=&\langle(+)::(@)::\sigma_1\vdash3::(@)::\square,1::v_1::\blacksquare\rangle\\
\sigma_1&=&[x\mapsto v_1] \\
\sigma_2&=&\sigma_1[y\mapsto v_2] \\
\sigma_3&=&\sigma_2[z\mapsto v_3]
\end{array}
\]

\[
\begin{array}{lrcr}
& \emptyset\vdash\textsf{letcc}\ x\ \textsf{in}\ (\textsf{letcc}\ y\ \textsf{in}\ x(1+(\textsf{letcc}\ z\ \textsf{in}\ y\ z)))3
::\square &||& \blacksquare \\
\rightarrow& \sigma_1\vdash(\textsf{letcc}\ y\ \textsf{in}\ x(1+(\textsf{letcc}\ z\ \textsf{in}\ y\ z)))3
::\square &||& \blacksquare \\
\rightarrow& \sigma_1\vdash\textsf{letcc}\ y\ \textsf{in}\ x(1+(\textsf{letcc}\ z\ \textsf{in}\ y\ z))::\sigma_1\vdash3::(@)
::\square &||& \blacksquare \\
\rightarrow& \sigma_2\vdash x(1+(\textsf{letcc}\ z\ \textsf{in}\ y\ z))::\sigma_1\vdash3::(@)
::\square &||& \blacksquare \\
\rightarrow& \sigma_2\vdash x::\sigma_2\vdash 1+(\textsf{letcc}\ z\ \textsf{in}\ y\ z)::(@)::\sigma_1\vdash3::(@)
::\square &||& \blacksquare \\
\rightarrow& \sigma_2\vdash 1+(\textsf{letcc}\ z\ \textsf{in}\ y\ z)::(@)::\sigma_1\vdash3::(@)
::\square &||& v_1::\blacksquare \\
\rightarrow& \sigma_2\vdash 1::\sigma_2\vdash\textsf{letcc}\ z\ \textsf{in}\ y\ z::(+)::(@)::\sigma_1\vdash3::(@)
::\square &||& v_1::\blacksquare \\
\rightarrow& \sigma_2\vdash\textsf{letcc}\ z\ \textsf{in}\ y\ z::(+)::(@)::\sigma_1\vdash3::(@)
::\square &||& 1::v_1::\blacksquare \\
\rightarrow& \sigma_3\vdash y\ z::(+)::(@)::\sigma_1\vdash3::(@)
::\square &||& 1::v_1::\blacksquare \\
\rightarrow& \sigma_3\vdash y::\sigma_3\vdash z::(@)::(+)::(@)::\sigma_1\vdash3::(@)
::\square &||& 1::v_1::\blacksquare \\
\rightarrow& \sigma_3\vdash z::(@)::(+)::(@)::\sigma_1\vdash3::(@)
::\square &||& v_2::1::v_1::\blacksquare \\
\rightarrow& (@)::(+)::(@)::\sigma_1\vdash3::(@)
::\square &||& v_3::v_2::1::v_1::\blacksquare \\
\rightarrow& \sigma_1\vdash3::(@)
::\square &||& v_3::\blacksquare \\
\rightarrow& (@)
::\square &||& 3::v_3::\blacksquare \\
\rightarrow& (+)::(@)::\sigma_1\vdash3::(@)
::\square &||& 3::1::v_1::\blacksquare \\
\rightarrow& (@)::\sigma_1\vdash3::(@)
::\square &||& 4::v_1::\blacksquare \\
\rightarrow& \square &||& 4::\blacksquare \\
\end{array}
\]

\(x\)의 값은 \(\langle\square,\blacksquare\rangle\)이다. \(x\)를 \(v\)에 적용하면 \(\square\ ||\ v::\blacksquare\)이다. 이는 위에서 \(x=\lambda v.v\)라 표현한 것과 일치한다.

\(y\)의 값은 \(\langle\sigma_1\vdash3::(@)::\square,\blacksquare\rangle\)이다. \(y\)를 \(v\)에 적용하면 다음이 성립한다.

\[\begin{array}{lrcr}
& \sigma_1\vdash3::(@)::\square &||&  v::\blacksquare \\
\rightarrow & (@)::\square &||&  3::v::\blacksquare
\end{array}\]

이는 위에서 \(y=\lambda v.v\ 3\)이라 표현한 것과 일치한다.

\(z\)의 값은 \(\langle(+)::(@)::\sigma_1\vdash3::(@)::\square,1::v_1::\blacksquare\rangle\)이다. \(z\)를 \(v\)에 적용하면 다음이 성립한다.

\[\begin{array}{lrcr}
& (+)::(@)::\sigma_1\vdash3::(@)::\square &||& v::1::v_1::\blacksquare\\
\rightarrow & (@)::\sigma_1\vdash3::(@)::\square &||& 1+v::v_1::\blacksquare\\
\end{array}\]

\(x\)의 값이 \(v_1\)이므로 위에서 \(z=\lambda v.x\ (1+v)\ 3\)이라 표현한 것과 일치한다.

## 인터프리터 구현

다음은 KFAE의 요약 문법을 Scala 코드로 작성한 것이다.

```scala
sealed trait KFAE
case class Num(n: Int) extends KFAE
case class Add(l: KFAE, r: KFAE) extends KFAE
case class Sub(l: KFAE, r: KFAE) extends KFAE
case class Id(x: String) extends KFAE
case class Fun(x: String, b: KFAE) extends KFAE
case class App(f: KFAE, a: KFAE) extends KFAE
case class Withcc(x: String, b: KFAE) extends KFAE

sealed trait KFAEV
case class NumV(n: Int) extends KFAEV
case class CloV(p: String, b: KFAE, e: Env) extends KFAEV
case class ContV(k: Cont) extends KFAEV

type Env = Map[String, KFAEV]
def lookup(x: String, env: Env): KFAEV =
  env.getOrElse(x, throw new Exception)

type Cont = KFAEV => KFAEV

def numVAdd(v1: KFAEV, v2: KFAEV): KFAEV = {
  val NumV(n1) = v1
  val NumV(n2) = v2
  NumV(n1 + n2)
}
def numVSub(v1: KFAEV, v2: KFAEV): KFAEV = {
  val NumV(n1) = v1
  val NumV(n2) = v2
  NumV(n1 - n2)
}
```

식에 \(\textsf{letcc}\)가 추가되었으므로 그에 해당하는 `Withcc` 클래스가 추가되었다. 또한 계속이 값이 될 수 있으므로 그에 해당하는 `ContV` 클래스가 추가되었다. 인터프리터는 계속을 Scala 함수로 표현하므로 `ContV` 객체는 하나의 필드를 가지며 그 필드의 타입은 `Cont`, 즉 값에서 값으로 가는 함수이다.

`interp` 함수에는 `Withcc` 경우를 처리하는 코드가 추가된다.

```scala
case Withcc(x, b) =>
  interp(b, env + (x -> ContV(k)), k)
```

\[
\sigma\vdash\textsf{letcc}\ x\ \textsf{in}\ e::k\ ||\ s\rightarrow
\sigma[x\mapsto\langle k,s\rangle]\vdash e::k\ ||\ s
\]

계속은 바뀌지 않으며 환경에 계속을 추가한 채로 몸통을 계산한다. 추론 규칙과도 일치한다.

또한, `App` 경우에서 함수 자리에 클로저가 아닌 계속이 오는 경우도 처리해야 한다.

```scala
case App(e1, e2) =>
  interp(e1, env, v1 =>
    interp(e2, env, v2 => v1 match {
      case CloV(xv1, ev1, sigmav1) =>
        interp(ev1, sigmav1 + (xv1 -> v2), k)
      case ContV(k) => k(v2)
    })
  )
```

\[
(@)::k\ ||\ v::\langle k',s'\rangle::s\rightarrow
k'\ ||\ v::s'
\]

`v1`이 `CloV` 객체이면 이전과 같다. `v1`이 `ContV` 객체이면 그 계속을 `v2`에 적용해야 한다. 계속은 `v1`이 필드로 가지고 있는 함수이다. 따라서 그 함수에 `v2`를 인자로 넘기면 된다. `interp` 함수를 호출할 필요가 없다. 이 점은 규칙에서 계산 스택에 \(\sigma\vdash e\)를 전혀 추가하지 않는다는 점과 일치한다.

`interp` 함수의 전체 코드는 다음과 같다.

```scala
def interp(e: KFAE, env: Env, k: Cont): KFAEV = e match {
  case Num(n) => k(NumV(n))
  case Id(x) => k(lookup(x, env))
  case Fun(x, b) => k(CloV(x, b, env))
  case Add(e1, e2) =>
    interp(e1, env, v1 =>
      interp(e2, env, v2 =>
        k(numVAdd(v1, v2))))
  case Sub(e1, e2) =>
    interp(e1, env, v1 =>
      interp(e2, env, v2 =>
        k(numVSub(v1, v2))))
  case App(e1, e2) =>
    interp(e1, env, v1 =>
      interp(e2, env, v2 => v1 match {
        case CloV(xv1, ev1, sigmav1) =>
          interp(ev1, sigmav1 + (xv1 -> v2), k)
        case ContV(k) => k(v2)
      })
    )
  case Withcc(x, b) =>
    interp(b, env + (x -> ContV(k)), k)
}
```

다음은 `interp` 함수를 사용하여 KFAE 식을 계산한 예시이다.

```scala
// 1 + (letcc x in ((x 2) + 3))
interp(
  Add(
    Num(1),
    Withcc("x",
      Add(
       App(Id("x"), Num(2)),
       Num(3)
      )
    )
  ),
  Map.empty,
  x => x
)
// 3

// letcc x in
//   (letcc y in
//     x (1 + (letcc z in y z))
//   ) 3
interp(
  Withcc("x",
    App(
      Withcc("y",
        App(
          Id("x"),
          Add(
            Num(1),
            Withcc("z",
              App(Id("y"), Id("z"))
            )
          )
        )
      ),
      Num(3)
    )
  ),
  Map.empty,
  x => x
)
// 4
```

지난 글과 마찬가지로 인터프리터가 실행되면 어떤 일이 일어나는지 잘 안 보일 수 있기 때문에 `interp` 함수를 호출하면 환원가능식, 계속, 환경이 어떻게 변화하는지 보여주는 프로그램을 만들었다. 필요한 사람들은 [KFAE.scala 파일](https://raw.githubusercontent.com/Medowhill/pl_articles/master/kr/18/KFAE.scala)을 다운로드 받아 실행해 보기를 바란다. 아래처럼 다운로드 받은 디렉토리에서 `scala`를 실행한 뒤 `KFAE.scala` 파일을 불러오고 `KFAE` 안에 정의된 것들을 들여오면 된다. 그 후 `run` 함수에 KFAE 식을 인자로 넘기면 된다.

```
$ scala
Welcome to Scala.
Type in expressions for evaluation. Or try :help.

scala> :load KFAE.scala
args: Array[String] = Array()
Loading KFAE.scala...
defined object KFAE

scala> import KFAE._
import KFAE._

scala> run(...)
```

다음은 실행한 결과 예시이다.

```scala
// 1 + (letcc x in ((x 2) + 3))
run(
  Add(
    Num(1),
    Withcc("x",
      Add(
       App(Id("x"), Num(2)),
       Num(3)
      )
    )
  )
)
```

```
v1 = <(1 + □)>
(1 + letcc x in ((x 2) + 3)) | □                            | ∅
1                            | (□ + letcc x in ((x 2) + 3)) | ∅
letcc x in ((x 2) + 3)       | (1 + □)                      | ∅
((x 2) + 3)                  | (1 + □)                      | [x -> v1]
(x 2)                        | (1 + (□ + 3))                | [x -> v1]
x                            | (1 + ((□ 2) + 3))            | [x -> v1]
2                            | (1 + ((v1 □) + 3))           | [x -> v1]
2                            | (1 + □)                      |
1 + 2                        | □                            | ∅
3
```

```scala
// letcc x in
//   (letcc y in
//     x (1 + (letcc z in y z))
//   ) 3
run(
  Withcc("x",
    App(
      Withcc("y",
        App(
          Id("x"),
          Add(
            Num(1),
            Withcc("z",
              App(Id("y"), Id("z"))
            )
          )
        )
      ),
      Num(3)
    )
  )
)
```

```
v1 = <□>
v2 = <(□ 3)>
v3 = <((v1 (1 + □)) 3)>
letcc x in (letcc y in (x (1 + letcc z in (y z))) 3) | □                               | ∅
(letcc y in (x (1 + letcc z in (y z))) 3)            | □                               | [x -> v1]
letcc y in (x (1 + letcc z in (y z)))                | (□ 3)                           | [x -> v1]
(x (1 + letcc z in (y z)))                           | (□ 3)                           | [x -> v1, y -> v2]
x                                                    | ((□ (1 + letcc z in (y z))) 3)  | [x -> v1, y -> v2]
(1 + letcc z in (y z))                               | ((v1 □) 3)                      | [x -> v1, y -> v2]
1                                                    | ((v1 (□ + letcc z in (y z))) 3) | [x -> v1, y -> v2]
letcc z in (y z)                                     | ((v1 (1 + □)) 3)                | [x -> v1, y -> v2]
(y z)                                                | ((v1 (1 + □)) 3)                | [x -> v1, y -> v2, z -> v3]
y                                                    | ((v1 (1 + (□ z))) 3)            | [x -> v1, y -> v2, z -> v3]
z                                                    | ((v1 (1 + (v2 □))) 3)           | [x -> v1, y -> v2, z -> v3]
v3                                                   | (□ 3)                           |
3                                                    | (v3 □)                          | [x -> v1]
3                                                    | ((v1 (1 + □)) 3)                |
1 + 3                                                | ((v1 □) 3)                      | [x -> v1, y -> v2]
4                                                    | □                               |
4
```

## 일급 계속의 활용

계속은 프로그램의 흐름을 나타낸다. 계속은 프로그램의 남은 할 일이기 때문에 계속을 바꾸면 프로그램도 다른 일을 하게 된다. 즉, 계속을 바꿈으로써 프로그램의 흐름을 바꿀 수 있다는 말이다. 명령형 언어에서는 `return`, `break`, `continue` 같은 문들이 프로그램의 흐름을 바꾸기 위해 사용된다. 우리가 지금까지 본 FAE 같은 언어에는 프로그램 흐름을 바꾸는 식이 없었다.

KFAE는 일급 계속을 지원하므로 프로그램 흐름을 바꾸는 것이 가능하다. 일급 계속을 호출하면 계속을 프로그래머가 원하는 대로 바꿀 수 있다. 이는 곧 프로그래머가 임의로 프로그램 흐름을 변경하는 것을 뜻한다. `return` 문 등은 정해진 의미에 따라서만 흐름을 바꿀 수 있다. 반면 KFAE에서는 프로그래머가 원하는 시점에서 \(\textsf{letcc}\)를 통해 계속을 만들고 원하는 시점에서 계속을 호출 할 수 있다. 그러므로 일급 계속은 `return` 등과 비교했을 때 더 높은 표현력을 제공하며 `return`, `break`, `continue` 등 모두를 인코딩할 수 있다.

`return`을 인코딩하는 방법부터 보겠다. \(\lambda x.e\)의 몸통 \(e\)에서 \(return\)이 사용된다고 가정하자. \(return\)은 하나의 인자를 받는다. \(return\)이 호출되면 그 즉시 함수 실행이 끝나고 \(return\)의 인자로 전달된 값이 함수의 결괏값이 된다.

\[
\textit{encode}(\lambda x.e)=
\lambda x.\textsf{letcc}\ return\ \textsf{in}\ \textit{encode}(e)
\]

함수의 결괏값이 나오면 하는 계산은 함수의 몸통을 모두 계산하고 나서 하는 계산과 같다. 다른 말로는 함수의 몸통 \(e\)를 계산하는 시점의 계속이다. \(return\)을 사용하는 것은 함수의 몸통이 모두 계산된 상태로 건너뛰는 것이다. 따라서 몸통 \(e\)를 계산하는 시점의 계속을 호출하는 것과 같다. \(\textsf{letcc}\ return\ \textsf{in}\ \textit{encode}(e)\)에서 \(\textsf{letcc}\)의 몸통이 \(e\)이므로 \(return\)의 값이 우리가 원하는 계속이다. 그러므로 위 식은 \(return\)을 성공적으로 인코딩한다.

다음은 \(return\)을 사용하는 예시이다.

\[
\textit{encode}(((\lambda x.(return\ 1)+x)\ 2) + 3)=
((\lambda x.\textsf{letcc}\ return\ \textsf{in}\ (return\ 1)+x) 2)+3
\]

\(x\)를 더하기 전에 \(return\ 1\)이 먼저 나오므로 함수의 결괏값은 \(1\)이 되어야 한다. 따라서 최종 결과는 \(4\)이다. 인터프리터를 실행하여 결과를 확인할 수 있다.

```scala
interp(
  Add(
    App(
      Fun("x", Withcc("return",
        Add(
          App(Id("return"), Num(1)),
          Id("x")
        )
      )),
      Num(2)
    ),
    Num(3)
  ),
  Map.empty,
  x => x
)
// 4
```

```scala
run(
  Add(
    App(
      Fun("x", Withcc("return",
        Add(
          App(Id("return"), Num(1)),
          Id("x")
        )
      )),
      Num(2)
    ),
    Num(3)
  )
)
```

```
v1 = <(□ + 3)>
((λx.letcc return in ((return 1) + x) 2) + 3) | □                                                  | ∅
(λx.letcc return in ((return 1) + x) 2)       | (□ + 3)                                            | ∅
λx.letcc return in ((return 1) + x)           | ((□ 2) + 3)                                        | ∅
2                                             | ((<λx.letcc return in ((return 1) + x), ∅> □) + 3) | ∅
letcc return in ((return 1) + x)              | (□ + 3)                                            | [x -> 2]
((return 1) + x)                              | (□ + 3)                                            | [x -> 2, return -> v1]
(return 1)                                    | ((□ + x) + 3)                                      | [x -> 2, return -> v1]
return                                        | (((□ 1) + x) + 3)                                  | [x -> 2, return -> v1]
1                                             | (((v1 □) + x) + 3)                                 | [x -> 2, return -> v1]
1                                             | (□ + 3)                                            |
3                                             | (1 + □)                                            | ∅
1 + 3                                         | □                                                  | ∅
4
```

\(return\)에 해당하는 값이 \(\square+3\)인 것을 볼 수 있다. 이는 함수 결괏값을 가지고 남은 할 일이다.

작은 걸음 의미를 사용하여도 똑같이 \(4\)를 얻을 수 있다.

\[
\begin{array}{rcl}
v1&=&\langle\lambda x.\textsf{letcc}\ return\ \textsf{in}\ (return\ 1)+x,\emptyset\rangle\\
v2&=&
\langle
\emptyset\vdash 3::(+)
::\square, \blacksquare\rangle
\end{array}
\]

\[
\begin{array}{lrcr}
& \emptyset\vdash((\lambda x.\textsf{letcc}\ return\ \textsf{in}\ (return\ 1)+x) 2)+3
::\square &||& \blacksquare \\
\rightarrow& \emptyset\vdash(\lambda x.\textsf{letcc}\ return\ \textsf{in}\ (return\ 1)+x) 2
::\emptyset\vdash 3::(+)
::\square &||& \blacksquare \\
\rightarrow& \emptyset\vdash\lambda x.\textsf{letcc}\ return\ \textsf{in}\ (return\ 1)+x
::\emptyset\vdash 2::(@)
::\emptyset\vdash 3::(+)
::\square &||& \blacksquare \\
\rightarrow& \emptyset\vdash 2::(@)
::\emptyset\vdash 3::(+)
::\square &||&
v1
::\blacksquare \\
\rightarrow& (@)::\emptyset\vdash 3::(+)
::\square &||&
2::v1
::\blacksquare \\
\rightarrow& 
\lbrack x\mapsto 2\rbrack\vdash \textsf{letcc}\ return\ \textsf{in}\ (return\ 1)+x::
\emptyset\vdash 3::(+)
::\square &||& \blacksquare \\
\rightarrow& 
\lbrack x\mapsto 2,return\mapsto v2\rbrack\vdash (return\ 1)+x::
\emptyset\vdash 3::(+)
::\square &||& \blacksquare \\
\rightarrow& 
\lbrack x\mapsto 2,return\mapsto v2\rbrack\vdash return\ 1::
\lbrack x\mapsto 2,return\mapsto v2\rbrack\vdash x::(+)::
\emptyset\vdash 3::(+)
::\square &||& \blacksquare \\
\rightarrow& 
\lbrack x\mapsto 2,return\mapsto v2\rbrack\vdash return::
\lbrack x\mapsto 2,return\mapsto v2\rbrack\vdash 1::(@)
\lbrack x\mapsto 2,return\mapsto v2\rbrack\vdash x::(+)::
\emptyset\vdash 3::(+)
::\square &||& \blacksquare \\
\rightarrow& 
\lbrack x\mapsto 2,return\mapsto v2\rbrack\vdash 1::(@)
\lbrack x\mapsto 2,return\mapsto v2\rbrack\vdash x::(+)::
\emptyset\vdash 3::(+)
::\square &||& v2::\blacksquare \\
\rightarrow& 
(@)::\lbrack x\mapsto 2,return\mapsto v2\rbrack\vdash x::(+)::
\emptyset\vdash 3::(+)
::\square &||& 1::v2::\blacksquare \\
\rightarrow& 
\emptyset\vdash 3::(+)
::\square &||& 1::\blacksquare \\
\rightarrow& 
(+)
::\square &||& 3::1::\blacksquare \\
\rightarrow& 
\square &||& 4::\blacksquare
\end{array}
\]

이번에는 `break`과 `continue`를 인코딩하겠다. KFAE에 `while` 같은 반복문이 없기 때문에 자세히 다루지는 않겠다. 또한, `return`을 인코딩하는 것과 비슷하므로 이해하는 데 큰 어려움은 없을 것이다.

\(\textsf{while}\ e_1\ e_2\)는 \(e_1\)이 참인 동안 \(e_2\)를 반복해서 계산하는 식이라고 하자. 계산이 종료된다면 식의 값은 \(()\)이라 하겠다. \(()\)는 아무 정보도 갖지 않는 값으로, Scala에서 `Unit` 타입의 유일한 값인 `()`와 같은 것이다. \(e_2\)에서 \(break\)과 \(continue\)를 사용한다고 가정하자. \(break\)과 \(continue\)는 명령형 언어에서처럼 아무런 인자도 받지 않는다.

\[
\textit{encode}(\textsf{while}\ e_1\ e_2)=
\textsf{letcc}\ break\ \textsf{in}\
(\textsf{while}\ e_1\
(\textsf{letcc}\ continue\ \textsf{in}\ 
\lbrack (break\ ())/break\rbrack\lbrack (continue\ ())/continue\rbrack\mathit{encode}(e_2)))
\]

\(break\)은 반복문을 종료시킨다. 반복문이 종료되고 나서 할 계산은 반복문을 계산할 때의 계속이다. 따라서 반복문 전체를 \(\textsf{letcc}\)의 몸통으로 하고 그 계속을 \(break\)의 값으로 하면 된다.

\(continue\)는 이번 몸통 계산을 건너뛴다. 바로 조건식 계산으로 넘어가며 조건식이 참이면 반복문을 다시 실행하고 거짓이면 반복문을 종료한다. 이는 몸통을 계산할 때의 계속이다. 그러므로 반복문의 몸통 전체를 \(\textsf{letcc}\)의 몸통으로 하고 그 계속을 \(continue\)의 값으로 하면 된다.

프로그래머가 코드에서 \(break\)과 \(continue\)를 사용할 때는 인자를 전달하지 않는다. 그러나 계속을 호출하기 위해서는 인자가 필요하다. 반복문의 결과는 반드시 \(()\)라 하였으니 둘 모두 \(()\)를 인자로 받으면 된다. 이를 위해 인코딩 과정에서 반복문 몸통의 \(break\)과 \(continue\)를 각각 \(break\ ()\)과 \(continue\ ()\)로 치환한다.

간단한 예시를 보자.

\[
\textit{encode}(\textsf{while}\ \textsf{true}\ break)=
\textsf{letcc}\ break\ \textsf{in}\
(\textsf{while}\ \textsf{true}\ 
break\ ())
\]

\(continue\)는 사용되지 않으므로 생략하였다. \(break\)은 아무것도 하지 않는 계속이다. 따라서 조건식이 언제나 참이지만 \(break\ ()\)가 반복문 몸통에서 계산되는 순간 \(()\)가 프로그램 최종 결과가 되면서 실행이 종료된다.

## 감사의 말

글을 확인하고 의견을 주신 류석영 교수님께 감사드립니다.
