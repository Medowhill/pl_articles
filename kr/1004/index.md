### Cyclic Proofs, System T, and the Power of Contraction
비정초 증명(non-wellfounded proof)은 가지의 수는 유한하지만 가지의 깊이가 무한한 것을 허용. 재귀적 증명은 순환적(cyclic) 증명으로 쉽게 변환될 수 있지만 그 반대는 어려움. 이 연구에서는 Curry Howard 동형의 측면에서 연구한 순환적 증명 체계를 제시.

### Diamonds Are Not Forever: Liveness in Reactive Programming with Guarded Recursion
반응형(reactive) 프로그램은 중요한 소프트웨어를 구성하나 논증하기 어려움. 함수형 반응형 프로그래밍(FRP; functional reactive programming)은 함수형 패러다임의 고수준 추상화를 허용하면서도 뛰어난 성능의 프로그램을 만드는 것이 목적. 순진한 접근은 비인과적(non-causal), 즉 출력이 미래의 입력에 의존하는 프로그램을 작성하는 것을 허용. 이는 양상 연산자(modal operator)를 도입해 해결 가능. ▷A는 다음 시간 단계에 도착하는 A 타입의 데이터를 뜻함. 보호된 재귀(guarded recursion)을 고정점 연산자와 함께 사용해 인과성과 생산성(모든 원소가 유한 시간에 계산 가능함)을 보장 가능. 선형 시간 논리(LTL; linear temporal logic)을 함수형 반응형 프로그래밍의 타입 체계로 사용하자는 제안이 있었음. 이 연구에서는 선형 시간 논리의 표현력을 보호된 재귀와 결합하는 언어를 정의.

### λs: Computable Semantics for Differentiable Programming with Higher-Order Functions and Datatypes
자동 미분은 사용자가 작성한 목적 함수의 도함수를 자동으로 찾아 기계 학습 모델을 쉽게 만들 수 있게 함. 사용자들은 현존하는 자동 미분 프레임워크가 처리할 수 없는 목적 함수를 만들어 냄. 그런 목적 함수는 적분, 최대/최소, 근 찾기 등을 사용하며, 이는 고차 함수로 일반화됨. 이 연구에서는 미분 가능한 언어인 λs를 제안. λs는 고차 함수를 포함하며 모든 타입은 매끄러운(smooth) 공간이고 모든 함수는 매끄러운 맵임. 고차 함수, 고차 미분, 매끄럽지 않은 함수 등의 의미를 정의하고 이를 바탕으로 미분 가능한 적분, 최대/최소, 근 찾기의 의미를 정의. 이를 구현했으며 임의의 정확도까지 계산 가능.

### Verifying Observational Robustness against a C11-Style Memory Model
약한 메모리 모델하의 동시성 프로그램 논증은 어려움. 어떤 프로그램이 순차적 일관성을 따르도록 동작한다는 것을 보장할 수 있는 조건인 강건성(robustness) 기준과 강건성을 검증할 방법이 필요. 이로써 약한 메모리 모델하의 안전성 검증 문제를 강건성 검증과 순차적 일관성하의 검증 문제로 환원할 수 있음. 이 연구에서는 기존 연구를 확장해 관대한 읽기/쓰기/RMW 및 획득/해방 장벽(release/acquire fence)를 포함하는 C11의 더 큰 일부분에 대한 강건성 기준을 제시. 강건성이 계측된(instrumented) 순차적 일관성 의미하의 도달 가능성 문제로 환원됨을 증명. 기존의 강건성 조건은 추측 읽기(speculative execution)를 사용하는 프로그램을 비허용. 이 연구에서는 추측 읽기를 사용하는 프로그램을 논증할 수 있는 관측 강건성(observational robustness)를 제안.

### On Algebraic Abstractions for Concurrent Separation Logics
동시 분리 논리의 핵심은 부분 교환 모노이드(PCM; partial commutative monoid)로, 부분 교환 모노이드는 소유권과 그 이동을 수학적으로 표현. 이 연구에서는 사상(morphism)과 분리 관계(separating relation)을 도입해 부분 교환 모노이드를 발전시킴. 사상은 모노이드의 구조를 보존하는 부분 함수이고, 분리 관계는 특정 사상이 어떤 입력에 대해 정의되는지 표현. 도입한 개념을 바탕으로 존재하는 모노이드로부터 새로운 모노이드를 만들 수 있으며 구체적 스레드 상태를 요약할 수 있음.

### Efficient and Provable Local Capability Revocation using Uninitialized Capabilities
능력(capability) 기계는 세분화된 권한 분리를 허용하는 CPU. 능력 기계는 시스템과 상호 작용 시 어떤 권한이 있는지 값(능력)으로 표현. 메모리 능력, 객체 능력 등 다양한 능력이 있음. 지역 능력은 일시적으로만 사용 가능하고, 나중에 사용하기 위해 저장될 수 없음. 지역 능력의 누출을 먹기 위해서는 레지스터와 메모리 등을 모두 지워야 한다는 문제가 있음. 이 연구에서는 지역 능력의 활용을 쉽게 하기 위해 초기화되지 않은 능력(uninitialized capability)를 제안. 초기화되지 않은 능력은 특정 메모리 구간을 둘로 나누어 초기화된 부분에는 읽기와 쓰기를, 초기화되지 않은 부분에는 쓰기만 허용. 능력 안전성을 증명하기 위해 Iris를 사용해 새로운 프로그램 논리와 논리적 관계를 제시하고 사용.

### Fully Abstract from Static to Gradual
점진적 타입의 정제된 기준은 점진적 언어가 책임 정리(blame theorem)를 만족할 것을 요구하며, 책임 정리에 따르면 타입 표시가 완전히 된 코드는 타입 변환 오류에 책임질 필요가 절대로 없음. 이는 오직 타입 안전성에만 관련된 것으로, Garcia와 Tanter는 잘 설계된 점진적 언어는 타입 안전성뿐 아니라 타입 건전성을 지켜야 한다고 주장. 이는 종료, 매개성, 비간섭(non-interference) 등의 성질 또한 보존되어야 함을 의미. 이 접근은 언어 설계자가 타입의 뜻을 정하게 하므로 정적 언어에서 사용한 논증 방법을 점진적 언어에서 그대로 사용하지 못할 수 있음. 이를 극복하고자 더 강한 성질인 완전 요약 임베딩(FAE; fully abstract embedding)이 제안됨. 이는 정적 언어의 항 사이의 문맥적 동치(contextual equivalence)가 점진적 언어의 동일한 항 사이에서도 보존되어야 한다는 뜻. 이 연구에서는 완전 요약 임베딩이 지켜지지 않는 기존의 점진적 언어를 탐구했으며 동치재귀적(equirecursive) 타입이 존재하는 단순 타입 람다 대수인 STLC𝜇의 점진적 언어 버전 GTLC𝜇를 제시하고 완전 요약 임베딩을 만족함을 증명함.

### Deciding Accuracy of Differential Privacy Schemes
차등 보안(differential privacy)는 민감한 정보에 대한 보안을 유지하는 계산을 가능하게 함. 차등 보안은 보안과 정확도를 보장해야 함. 정확도에 대한 일반적 정의가 없었으며 정확도의 한계를 정확하게 구할 수 없었음. 이 연구에서는 정확도의 일반적 정의를 제시함. 어떤 입력 u에 대해 불일치 거리(distance to disagreement) a는 d(u, v) < a이면 f(u) = f(v)인 가장 큰 값. 어떤 알고리즘이 (a, b, c)-정확하다는 것은 불일치 거리가 a인 모든 입력 u에 대해 (1 - b)의 확률로 올바른 답과 c 이하로 떨어진 답을 낸다는 것. 어떤 매개화된 알고리즘 P_e가 주어진 e에 대해 (a, b, c)-정확한지 결정하는 것과 모든 e에 대해 (a, b, c)-정확한지 결정하는 것은 모두 결정 불가능함을 증명함. Schanuel의 추측이 참이라고 가정할 때 두 문제가 모두 결정 가능한 프로그램의 모임인 DipWhile+를 제시.

### A Computational Interpretation of Compact Closed Categories: Reversible Programming with Negative and Fractional Types
닫힌 옹골 범주(compact closed category)는 선형 논리, 동시성, 양자 컴퓨팅의 모델이 됨. 닫힌 옹골 범주의 모든 객체는 쌍대(dual)를 가짐. 이 연구에서는 음수 타입이 시간(흐름)을 되돌리는 것으로 해석될 수 있고, 분수 타입이 공간을 되돌리는 것(특정 값이 저장된 메모리를 회수)으로 해석될 수 있음을 보임. 이를 바탕으로 되돌릴 수 있는 언어를 정의하고 SAT 해결기를 구현해 음수와 분수 타입이 역추적(backtracking)에 도움됨을 보임. 많은 수의 되돌릴 수 있는 추상 기계의 종료를 증명하는 기술을 제시.

### Mechanized Logical Relations for Termination-Insensitive Noninterference
비간섭과 같은 정보 흐름 제어는 정적 타입 체계를 통해 강제됨. 타입 체계가 복잡해질수록 정보 흐름에 대한 성질을 포함한 타입 건전성을 증명하기 복잡해짐. 이 연구에서는 고차 상태가 존재하는 고차 언어의 건전성을 증명. 이 언어의 정보 흐름 제어 타입 체계는 재귀적 타입, 존재 타입, 비서술적 다형성(impredictive polymorphism) 등을 포함. 의미적 모델을 사용해 종료 무관 비간섭을 증명. 의미적 모델을 사용함으로써 문법적으로 올바른 타입과 잘못된 타입의 부품이 함께 사용되는 프로그램의 성질도 검증 가능. 비서술적 다형성하에서 단항과 이항 관계를 정의하기 위해 단계 색인(step indexing)을 사용했으며 단계의 개수가 맞도록 이항 관계를 새롭게 정의. 기존 방식은 종료 무관 비간섭에 대해 논증할 수 없었기에 새로운 방법을 제시. 증명에는 Iris를 사용했으며, 최약 사전 조건을 대신하는 양상 최약 사전 조건(MWP; modal wweakest precondition)을 도입.

### Probabilistic Programming Semantics for Name Generation
뉴(𝜈) 대수는 단순 타입 람다 대수에 새로운 이름을 도입하는 항인 𝜈x.e를 추가(새로운 이름 x를 e에서 사용 가능). 일차 타입만 고려하더라도 관측 동치(observational equivalence)는 비자명. 확률적 프로그래밍은 람다 대수에 표본(sample) 명령을 추가해 확률 분포를 다루는 프로그램을 작성할 수 있게 함. 뉴 대수의 항을 확률적 프로그램으로 해석할 수 있음. (두 새로운 이름이 항상 다른 것처럼 두 표본은 거의 확실히 서로 다름.) 이 연구에서는 준Borel 공간이 뉴 대수를 해석하는 데 사용될 수 있음을 보임.

### Internalizing Representation Independence with Univalence
동일한 인터페이스에 대한 두 구현이 문맥 동치(contextual equivalence) 관계임을 증명하는 것은 중요. 의존적 타입이 존재하는 언어에서는 언어 안에서 두 구현의 동치를 표현하고 증명할 수 있음. 일가 공리(univalence axiom)에 따라 두 동형 타입은 같음. 동형이라는 조건은 너무 강함. 이 연구에서는 고차 귀납적 타입을 사용해 동치를 보이는 방법을 제안.

### Transfinite Step-Indexing for Termination
논리적 관계는 프로그램의 동작에 대한 성질을 구성적이고 타입에 따른 방식으로 증명할 수 있게 함. 재귀적 타입과 고차 상태 등의 기능을 제공하는 언어에도 단계 색인을 통해 논리적 관계를 적용할 수 있음. 지금까지 단계 색인 논리적 관계는 안전 성질(safety property) 검증에 주로 사용되었고, 종료와 같은 생존 성질(liveness property) 검증에는 잘 사용되지 않음. 실행이 n 단계 안에 끝나야 한다는 성질은 안전 성질. 올바른 한계 n을 결정하는 방법에 대한 문제가 있음. 기존에 제시된 초한(transfinite) 단계 색인을 적용하여 해결 가능. 이 연구에서는 초한 단계 색인을 사용해 고차 상태가 존재하는 언어의 종료를 증명.

### Generating Collection Transformations from Proofs
중첩 관계 대수(NRC; nested relational calculus)는 중첩된 컬렉션의 변형을 정의하는 데 표준적. 이 연구에서는 논리에서의 변형을 묘사하는 새로운 선언적 방법을 제시.

### Learning the Boundary of Inductive Invariants
시스템의 안전성을 검증하기 위해서는 귀납적 불변 성질을 추론해야 함. 이 연구에서는 SAT 기반 불변 성질 추론에 집중. 이 방법은 실용적으로 많은 것을 아루었으나 이론적 복잡도에 대한 연구는 부족. 이 연구에서는 장벽 조건(fence condition)을 제시. 불변 성질 I의 경계는 I에 포함되지 않으나 한 비트를 수정해 I에 들어갈 수 있는 모든 상태의 집합. k 장벽 조건은 I의 경계에 있는 모든 상태로부터 잘못된 상태에 k 단계 안에 도달해야 한다는 것. 장벽 조건이 만족되고 불변 성질이 단조(monotone)인 짧은 논리합 정규형으로 표현 가능할때 보간(interpolation) 알고리즘이 선형 회수의 반복 안에 수렴함을 증명.

### Precise Subtyping for Asynchronous Multiparty Sessions
세션 타입은 통신 중 발생할 수 있는 오류나 교착 상태를 방지. 두 세션 타입 사이의 서브타입 관계를 생각할 수 있음. 어떤 서브타입 관계가 정확하다는 것은 건전하면서 동시에 완전하다는 것. 이 연구에서는 다자간(multiparty) 비동기 세션 타입을 위한 정확한 서브타입 관계를 제시. 이는 통신 프로그램에 대한 여러 최적화가 비동기를 가정할 때 올바르며 올바름을 타입을 통해 지역적으로 검사할 수 있음을 의미. 정확한 서브타입을 정의하고 증명하기 위해 세션 분해(session decomposition)이라는 새로운 개념을 도입. 두 타입의 서브타입 관계는 각 타입으로부터 만들어진 단일 입출력(single-input single-output) 나무의 정제 관계로 정의.

### Verifying Correct Usage of Context-Free API Protocols
API를 잘못 사용하면 오류나 자원 누수가 발생하므로 API를 올바르게 사용하는지 검사하는 것은 중요. 기존의 검증 시도는 API 프로토콜이 정규 언어로 표현된다고 가정. 그러나 여러 API는 정규 언어가 아니라 문맥 무관 언어로 표현됨. 이 연구에서는 주어진 프로그램이 문맥 무관 언어로 표현 가능한 API를 올바르게 사용하는지 정적으로 검증하는 방법을 제시. 주어진 프로그램으로부터 자동으로 문맥 무관 문법을 만들 수 있음. 이 과정은 건전하므로 문법이 API의 언어에 포함되지 않는 단어를 만들 수 없다면 API를 올바르게 사용하는 것. 과정은 부정확하므로 반례가 존재한다면 반례를 바탕으로 문법을 정제. 이 반례가 실제로 프로그램에서 만들어질 수 있다면 API를 잘못 사용하는 것이고, 아니라면 정제 후 반복.

### Provably Space-Efficient Parallel Functional Programming
병렬(parallel) 프로그래밍은 널리 사용되지만 효과를 잘 제어하지 못하면 경쟁을 일으키기 쉬움. 병렬 함수형 프로그래밍은 이를 해결하지만, 많은 메모리 할당이 필요하다는 문제가 있음. 기존 연구는 스레드가 동시에 실행되는 다른 스레드의 할당을 알 필요가 없다는 관찰에 바탕을 둠. 이 연구에서는 메모리를 힙의 계층으로 나누고 스케줄링 시 스레드와 함께 힙을 프로세서에 배정. 이를 통해 병렬 프로그램을 공간 효율적으로 실행할 수 있다는 사실을 증명.

### Data Flow Refinement Type Inference
정제 타입은 다양한 안전성 성질을 정적으로 검사할 수 있게 함. 기본 타입에 정제 술어(refinement predicate)를 덧붙여 타입의 정확도를 높일 수 있음. 프로그래머의 부담을 줄이기 위해 정제 타입 추론이 중요. 다양한 정제 타입 추론 알고리즘 설계가 가능. 이 연구에서는 요약 실행을 바탕으로 요약 사이의 관계를 연구. 이를 위해 관계적 요약 도메인(relational abstract domain)의 선택에 매개된 정제 타입 체계를 정의.

### Simplifying Dependent Reductions in the Polyhedral Model
같은 계산의 결과가 여러 번 사용되는 경우 이를 같은 결과를 내면서도 복잡도가 낮은 알고리즘으로 최적화할 수 있음. 기존 연구는 주어진 계산이 비의존적일 때만 적용 가능. 이 연구에서는 의존적인 계산이 주어질 때의 최적화 방법을 제시.

### On the Semantic Expressiveness of Recursive Types
재귀적 타입에는 동치 재귀적(equi-recursive) 타입과 동형 재귀적(iso-recursive) 타입의 두 종류가 있음. 동치 재귀적 타입이 있는 언어의 올바른 타입의 항에 fold와 unfold를 추가해 동형 재귀적 타입이 있는 언어의 올바른 타입의 항으로 바꿀 수 있다는 사실이 알려져 있음. 의미론적 표현력에 대해서는 연구가 부족. 이 연구에서는 두 종류의 타입의 의미론적 표현력을 탐구. 근원 언어의 문맥 동치인 두 항을 목표 언어의 문맥 동치인 두 항으로 옮기는 완전 요약 컴파일러(fully-abstract compiler)가 존재하면 두 언어의 표현력이 같다고 할 수 있음. 이 연구에서는 λfx에서 λIμ, λfx에서 λEμ, λIμ에서 λEμ로 가는 완전 요약 컴파일러의 존재를 증명(λfx: 람다 대수 + 고정점 조합자, λIμ: 동형, λEμ: 동치).

### Intrinsically Typed Compilation with Nameless Labels
컴파일러 기능 검증은 많은 노력이 필요. 컴파일러의 타입 올바름만을 검증하는 것은 적은 노력으로 가능. 타입 올바름이 검증된 컴파일러를 만드는 방법 중 하나는 내재적 타입(intrinsically typed) 컴파일러를 만드는 것으로, 근원 언어의 올바른 타입의 항만 정의하는 타입과 목표 언어의 올바른 타입의 항만 정의하는 타입을 만든 뒤 전자에서 후자로 가는 함수를 작성하는 것. 바이트코드의 타입을 결정하는 데는 라벨로 인해 전역 정보가 필요하므로 바이트코드의 타입은 비구성적. 이 연구에서는 이름 없는 쌍대문맥(co-context) 바이트코드를 도입해 라벨이 이름과 문맥에 의존하지 않도록 해 바이트코드의 타입을 구성적으로 만듦. 내재적 타입 컴파일러를 구현하기 쉽도록 분리 논리를 통해 쌍대문맥을 다루는 방법 제시.

### egg: Fast and Extensible Equality Saturation
동일성 그래프(equality graph)는 자동 증명기를 위해 도입됨. 동일성 그래프는 식의 동치류(equivalence class)를 표현하며 동치 관계는 합동(congruence; a≡b이면 f(a)≡f(b))에 대해 닫혀 있음. 동일성 그래프가 다시 쓰기에 기반을 둔 컴파일러 최적화와 프로그램 합성에 필요한 동일성 포화(equality saturation)을 위해 사용되기 시작함. 동일성 포화는 주어진 프로그램과 동치인 프로그램을 다시 쓰기를 통해 찾는 과정. 기존의 동일성 그래프 그리기 알고리즘은 동일성 포화에 특화되지 않음. 이 연구에서는 재건(rebuilding)을 도입해 불변 성질을 매 연산마다가 아니라 필요한 순간에만 유지하는 알고리즘을 제시하고, 동치류 분석을 도입해 도메인에 특화된 다시 쓰기를 일반적으로 동일성 그래프 그리기에 적용하는 방법을 제시함.

### Asynchronous Effects
그동안의 대수적 효과는 동기적. 최근 비동기와 대수적 효과의 조합이 관심받음. 이 연구에서는 비동기와 대수적 효과를 핵심 대수에 모두 포함. 핵심 발상은 신호를 보내는 연산의 실행과 실행 중인 계산을 중단하는 것을 분리하는 것.

### Modeling and Analyzing Evaluation Cost of CUDA Kernels
GPGPU 프로그래밍이 각광받음. CUDA와 같은 언어로 GPU에서 실행되는 함수를 작성. CPU를 위한 코드를 작성하는 것보다 어려움. CPU에서 실행되는 코드에서는 사소한 코드 변화가 GPU에서 실행되는 코드에서는 심각한 성능 문제를 일으킬 수 있음. 기존 분석 기술은 버그의 부재를 보장하지 못함. 이 연구에서는 정적 분석을 통해 GPU 프로그램의 최악의 경우의 시간 상한을 자동으로 찾아내는 기술 제시. 이를 위해 새로운 비용 의미를 도입하고 이 의미에 대해 건전한 새로운 정량적 프로그램 논리 도입.

### Dijkstra Monads Forever: Termination-Sensitive Specifications for Interaction Trees
지금까지의 정형 검증은 대부분 부분 올바름(partial correctness)에 적합하며 종료와 같은 생존 성질은 잘 다루지 못함. 환경과 데이터를 교환하는 대화형 프로그램의 검증도 어려움. 이 연구에서는 끝나지 않을 수 있는 대화형 프로그램의 종료 민감 성질을 검증하는 프레임워크를 제시. 이 연구는 상호작용 나무(interaction tree)와 Dijkstra 모나드에 바탕을 둠.

### A Unifying Type-Theory for Higher-Order (Amortized) Cost Analysis
비용 분석을 위한 타입 체계는 다양. 1) 값에 의한 호출과 이름에 의한 호출 중 무엇을 지원하는지. 2) 비용이 효과와 쌍대효과(coeffect) 중 무엇을 통해 추적되는지. 3) 최악의 경우 비용 분석과 분할 상환 비용 분석 중 무엇을 지원하는지. 이 연구에서 𝜆-amor라는 타입 이론을 제시. 값/이름에 의한 호출, 효과/쌍대효과 기반 추적, 최악/분할상환 비용 분석을 하는 타입 체계를 𝜆-amor를 통해 표현 가능.

### Automatic Differentiation in PCF
자동 미분에 필요한 두 성질은 효율성(원래 함수를 계산하는 것보다 도함수를 계산하는 것이 더 비용이 커서는 안 됨)과 건전성(도함수가 정의된 모든 점에서 도함수의 값이 올바르게 계산됨). 기존의 자동 미분의 건전성에 대한 연구는 완전히 일반적인 언어를 다루지 못함. 이 연구에서는 함수형 언어 PCF_R(실수가 있는 튜링 완전한 언어임)의 자동 미분의 건전성에 대해 탐구. 거의 모든 곳에서의 건전성(측도가 0인 집합에 속하는 점 이외에서는 도함수의 값이 올바름)을 제시하고 거의 모든 곳에서의 건전성을 증명함.

### An Approach to Generate Correctly Rounded Math Libraries for New Floating Point Variants
새로운 부동소수점 표현법이 제시됨. 기존의 부동소수점을 사용해 초등 함수(log, exp, sqrt, sin)의 올바르게 반올림된 결과를 계산하는 라이브러리는 존재하지만 새로운 부동소수점을 사용하는 올바른 라이브러리는 없음. 기존에 라이브러리를 만드는 방법은 초등 함수의 실제 값과 비교해 가장 작은 오차를 내는 다항 함수를 생성하는 것. 이 방법으로는 올바르게 반올림된 결과가 나오지 않을 수 있음. 이 연구에서는 라이브러리를 만드는 새로운 방법을 제시. 실제 값의 올바르게 반올림된 결과와 동일한 값으로 반올림되는 실수의 구간을 찾고 그 구간에 속하는 값을 내는 다항 함수를 생성.

### Semantics-Guided Synthesis
Sketch와 Rosette 같은 해결기 기반(solver-aided) 도구는 특화된 언어로 합성 문제를 정의할 수 있게 한 뒤 제약 해결기로 정의된 문제를 해결. 문법 유도 합성(SYGUS; syntax-guided syntehsis)의 탐색 공간은 문맥 자유 문법으로 정의되며 그 동작은 동일한 논리적 이론의 식에 의해 표현됨. 해결기 기반 도구는 문법 유도 합성처럼 무한한 탐색 공간을 표현할 수 없고, 문법 유도 합성은 지원하는 이론을 벗어나는 의미(반복문 등)가 필요한 문제를 표현할 수 없음. 이 연구에서는 의미 유도 합성(SEMGUS; semtnaics-guided synthesis)를 제시. 의미 유도 합성은 문법에 더해 제약된 Horn 절(constrained horn clause)로 정의된 의미를 입력받음. 이는 재귀적 큰 걸음 의미를 정의하기에 충분함. 의미 유도 합성은 해를 합성하거나 문제가 실현될 수 없음을 증명.

### An Abstract Interpretation for SPMD Divergence on Reducible Control Flow Graphs
단일 프로그램 다중 데이터(SPMD; single program, multiple data) 프로그램에게 컴파일러의 벡터화가 중요. 벡터화를 위해서 차이(divergence) 분석 필요. 어떤 프로그램 지점에서 특정 변수가 균일(uniform)하다는 것은 모든 실행에서 값이 같다는 것. 이는 두 개의 다른 실행에 대한 논증이므로 일반적인 요약 실행이 분석하는 성질과 달리 초성질(hyper-property)임. 기존의 차이 분석은 프로그램의 문법 등에 너무 강한 조건을 요구. 이 연구에서는 제어 흐름 그래프(CFG; control flow graph)가 축소 가능(reducible)할 것만을 요구하는, 요약 실행을 바탕으로 한 차이 분석을 제시하고 올바름을 증명.

### Intersection Types and (Positive) Almost-Sure Termination
확률적 프로그램 종료는 두 가지 방법으로 정의 가능. 거의 항상(almost-surely) 종료는 종료되지 않을 가능성이 0이라는 것. 긍정적(positive) 거의 항상 종료는 종료 시간까지의 기댓값이 유한하다는 것(이는 거의 항상 종료보다 더 강한 성질). 두 성질 모두 검사하기 어려움. 이 연구에서는 교집합 타입을 사용해 두 형태의 종료를 모두 표현할 수 있음을 보임. 두 종류의 한계 모두 정확함(건전하고 완전).

### A Separation Logic for Effect Handlers
사용자 정의 효과와 효과 처리기는 효과가 있는 프로그램을 모듈화하는 것을 도움. 효과 처리기는 예외 처리기와 비슷하나 효과로 인해 계산이 중단된 지점의 계속을 호출할 수 있다는 차이가 있음. 여러 언어에는 사용자 정의 효과뿐 아니라 언어에 내장된 원시적 효과도 있음. 이 연구에서는 Iris를 확장해 사용자 정의 효과와 원시적 효과가 모두 존재하는 프로그램의 검증을 가능하게 함. 프레임 규칙을 깨지 않기 위해서는 처리기가 계속을 최대 한 번 호출해야 함.

### The Fine-Grained and Parallel Complexity of Andersen’s Pointer Analysis
포인터 분석은 주어진 프로그램에서 각 포인터가 가리킬 수 있는 메모리 공간을 찾는 것. 흐름 민감(flow-sensitive) 포인터 분석은 매우 어려움. 대부분의 연구는 흐름 무시 포인터 분석에 집중. 흔한 형태는 Anderson 포인터 분석으로, n개의 포인터와 m개의 문장(대입, 참조 대입, 역참조 대입, 간접 대입 중 하나)이 주어졌을 때의 분석. 모든 포인터의 가리킴 집합을 찾는 완전한(exhaustive) 분석과 달리, 요구에 따른(on demand) 분석은 주어진 포인터 a와 공간 b에 대해 a가 b를 가리키는지만 계산. 분석의 병렬화 가능성도 중요한 문제. 이 연구에서는 완전한 분석의 새로운 상한, 요구에 따른 분석의 하한 등을 찾고 증명했으며 병렬화 불가능함을 보임. 또한 특정 제약 하의 분석에 대해서도 상한/하한을 찾고 병렬화 가능함을 보임.

### Giving Semantics to Program-Counter Labels via Secure Effects
타입 체계는 비간섭을 강제할 수 있음. 효과와 암묵적 흐름을 함께 사용하면(비밀 값을 조건으로 사용한 후, 각 가지에서 다른 값을 저장하는 효과를 발생), 결과를 확인하는 것만으로는 비간섭을 강제할 수 없음. 이 문제를 해결하기 위해 지금까지는 프로그램 카운터에 비밀/공개 라벨을 붙여 효과를 발생시켜도 되는지 확인. 이 연구에서는 효과를 모나드로 처리해 주어진 프로그램을 변환하고 비간섭을 확인하는 프레임워크를 제시. 프레임워크를 사용하기 위해서는 효과가 안전한(secure) 효과(프로덕터로 표현 가능)여야 함. 어떤 효과가 안전한 효과임을 증명하는 방법 제시. 비종료 역시 안전한 효과이므로 종료 민감/무시 비간섭을 하나로 통합할 수 있으며, 프로그램 카운터 라벨을 정형화 할 수 있음.

### Optimal Prediction of Synchronization-Preserving Races
데이터 경쟁은 보통 동적 분석을 통해 탐지. 실행을 관찰한 뒤 경쟁을 일으킬 수 있는 다른 실행이 가능한지 판단. 가장 널리 사용되는 방식은 Lamport의 이전에 일어남(happens-before) 관계에 기반. 두 충돌하는 데이터 접근 사이에 이전에 일어남 관계에 의한 순서가 없는 경우를 찾음. 만약 그런 접근을 찾았다면 반드시 경쟁이 있지만, 이 방식에 의해 찾을 수 없는 경쟁도 존재. 이 연구에서는 동기화 보존(synchronization preserving) 실행이라는 개념을 도입. 동기화 보존 실행은 동기화 연산의 순서가 바뀜으로써 일어날 수 있는 다른 실행. 동기화 보존 경쟁은 위양성이 없으면서도 이전에 일어남 방식이 찾는 모든 경쟁을 찾음. 탐지 알고리즘을 제시하고 그 복잡도를 분석함.

### A Verified Optimizer for Quantum Circuits
큐빗이 희소하고 파이프라인이 짧아야 하므로 양자 회로는 근원 알고리즘을 최적화해서 만들어야 함. 최적화에 버그가 있기 쉽고, 양자 알고리즘의 비결정성으로 인해 최적화의 올바름을 동적으로 확인하기 어려움. 이 연구에서는 SQIR이라는 양자 프로그래밍 언어를 제시하고 이 언어로 작성된 양자 프로그램을 최적화하는 VOQC라는 검증된 최적화기를 제시.

### Automata and Fixpoints for Asynchronous Hyperproperties
초성질은 보안 분석 등에서 중요하므로 최근 많은 관심을 받음. 대부분의 기존 연구는 초성질을 동기적으로만 분석. 많은 보안 분석에서 초성질을 비동기적으로 분석할 필요가 있음. 이 연구에서는 오토마타 이론 기반 프레임워크와 시간 고정점 대수(temporal fixpoint calculus)를 제시. 두 관점이 일치함을 보임. 비동기적 초성질 분석의 주요 문제가 산술적이지 않음을 보임. 의미론적 제한을 둠으로써 분석이 정확한 결과를 내는 설정을 찾음.

### Relatively Complete Verification of Probabilistic Programs: An Expressive Language for Expectation-Based Reasoning
확률적 프로그램 검증은 외연적 방법과 내재적 방법이 있음. 외연적 방법은 표명을 수학적 개체로 바라봄. 흔히 사용되는 외연적 방법은 프로그램 C와 최종 상태에서 수로 가는 함수은 f에 대해, 최약사전기대(weakest preexpectation)는 초기 상태에서 수로 가는 함수이며 어떤 상태에서 C가 실행되어 도달한 상태에서의 f의 기댓값을 나타냄. 실용적 도구들은 같은 수학적 대상을 표현하는 서로 다른 방법을 구분하지 않는다는 특징을 가질 수 없으므로 외연적 방법 대신 내연적 방법을 사용하며 표명을 작성하기 위한 특정 문법에 의존. 기존 연구에서는 검증 체계각 외연적이거나 완전하지 않음. 이 연구에서는 내연적이면서도 f가 문법적으로 표현 가능할 때 f의 최약사전기대도 표현 가능한 언어를 제시.

### Verified Code Generation for the Polyhedral Model
배열 등을 다루는 중첩된 반복문은 다양한 최적화가 가능하며, 최적화를 위해 다면체 모델(polyhedral model)이 사용됨. 중첩된 반복문을 선언적으로 나타낼 수 있으며, 반복문 최적화는 다면체 변형을 통해 이루어짐. 이 연구에서는 다면체 모델의 정형 명세를 만들고 다면체 모델을 기반으로한 반복문 최적화를 정형 검증함.

### Petr4: Formal Foundations for P4 Data Planes
네트워크에는 많은 버그가 있으며 정형 검증 도구는 실제 라우터와 스위치에 비해 너무 간단한 모델만을 다룸. P4는 패킷 처리 체계의 기능을 묘사할 수 있도록 다양한 도메인 특화 요약을 제공하는 언어. P4는 아직까지 정형 명세가 없었음. 이는 P4로 작성된 프로그램을 이해하기 어렵게 만들 뿐 아니라 P4 언어 자체를 이해하고 확장하기 어렵게 만듦. 이 연구에서는 P4의 인터프리터를 새롭게 구현하고 P4의 일부의 모델이 되는 핵심 대수를 정의함. 인터프리터는 아키텍처별 특수한 동작에 대해 매개화되어 있으며, 핵심 대수로부터 종료와 같은 중요 성질을 증명함.

### Distributed Causal Memory: Modular Specification and Verification in Higher-Order Distributed Separation Logic
분산 데이터베이스는 일관성, 가용성, 분할 내성을 모두 만족할 수 없기에 약한 일관성만을 지원하며 널리 사용되는 방식이 인과 일관성(causal consistency). 인과 일과성은 노드 n이 노드 m으로부터 유래된 연산 x를 관찰했다면, m에 대한 x이전의 연산 역시 모두 관측했어야 함을 의미. 분산 시스템을 만드는 것은 어려움. 기존의 분산 시스템 검증 연구는 부품을 분리해 검증하는 모듈적인 검증을 지원하지 않으며, 프로그램의 완전한 기능적 올바름을 검증하지도 못함. 이 연구에서는 완전한 기능적 올바름의 모듈적 검증을 지원하는 인과 일관적 분산 데이터베이스의 구현에 대한 명세와 검증을 제시. Iris에 기반하는 분산 동시 분리 논리인 Aneris를 사용해 의미를 정의. Iris의 유령 상태를 사용해 수정 내역 관리를 수학적으로 정의.

### PerSeVerE: Persistency Semantics for Verification under Ext4
프로그래머들이 파일 수정이 프로그램에 명시된 순서로 이루어진다고 가정하는 것과 달리, 현대의 파일 체계는 프로그래머가 sync/fsync 등을 사용하지 않는다면 크래시가 발생했을 때 더 나중에 이루어진 수정만이 영구적으로 남아 있을 수 있으며 이는 탐지하기 어려운 심각한 버그를 일으킬 수 있음. 이 연구에서는 ext4 파일 체계의 의미를 정형화함. 이 모델은 매우 유연하여 C/C++의 동시성 모델과 같은 존재하는 약한 메모리 모델과 쉽게 통합될 수 있음. 파일 입출력을 수행하는 순차적/동시성 C/C++ 프로그램을 자동으로 검증하는 모델 검사 알고리즘을 설계하고 구현함. 가능한 모든 프로그램의 실행 및 크래시 후의 영구 상태를 고려해 주어진 불변성질이 만족되지 않을 수 있는 경우을 찾아 줌.

### Context-Bounded Verification of Liveness Properties for Multithreaded Shared-Memory Programs
대부분의 생존 성질은 종료로 환원될 수 있음. 공평한 스케줄러하의 종료는 공평한 종료. 공유 메모리 다중 스레드 프로그램의 공평한 종료는 결정 불가능(Pi_1^1 완전). K 문맥 제한은 각 스레드가 최대 K번 문맥 교환될 수 있다고 가정. 지금까지 작은 K 값으로도 실제 시스템의 여러 버그를 찾아 냄. 기존 연구에서는 K가 0인 경우를 탐구. 이 연구에서는 K 문맥 제한 종료가 K가 1 이상일 때 결정 가능하며 2EXPSPACE 완전함을 증명. K 문맥 제한 공평한 종료가 결정 가능하지만 비초등적임을 증명함. 제한되지 않은 수의 활성화된 스레드를 유한하게 표현하기 위한 방법을 찾는 것이 어려웠음. 이를 해결하기 위해 풍선 및 상태 동반 벡터 합 체계(vector addition system with states with balloons)라는 새로운 모델을 도입.

### A Practical Mode System for Recursive Definitions
재귀적 정의는 유용하지만, 값에 의한 호출 언어에서는 let rec x = x + 1과 같이 재귀적 정의가 잘못된 순환을 포함하여 실행 시간에 실행을 실패하게 만들 수 있음. 기존의 OCaml 타입 검사기는 잘못된 재귀적 정의를 찾기 위해 문법적 분석에 의존하며, 여러 버그가 발견되었고 버그로 인해 메모리 안전성이 위배됨. 이 연구에서는 OCaml로 작성된 프로그램이 재귀적 정의를 실제로 얼마나 사용하는지 조사. 이를 바탕으로 잘못된 재귀적 정의를 탐지하는 추론 규칙 체계를 제시하고 건전성을 증명했으며 검사기를 구현해 실제 OCaml 컴파일러 구현체에 포함시킴.

### Formally Verified Speculation and Deoptimization in a JIT Compiler
즉시(JIT; just in time) 컴파일은 스스로를 수정하는 코드와 인터프리터를 통한 해석과 기계어의 단순 실행을 번갈아 가며 하는 실행 방식으로 인해 정형 검증되기 어려움. 이 연구에서는 즉시 컴파일러가 검증 가능하도록 설계될 수 있음을 보임. 최적화 정책 결정과 이를 위한 정보 수집(profiling) 과정이 실행 및 코드 생성으로부터 완전히 분리된 컴파일러 설계를 제시. 역최적화(deoptimization)와 추측(speculation)을 명시적으로 만드는 중간 언어 제시. 이를 바탕으로 컴파일러가 의미를 보존하면서 코드를 변형함을 보임.

### Taming x86-TSO Persistency
기존 연구에서는 Px86이라는 x86 영구 메모리의 정형 의미를 제시. Px86의 모델은 개발자와 연구자들의 일반적인 이해와 큰 차이가 있음. Px86의 명령들은 비동기적이지만 개발자들은 동기적 명령의 존재를 가정. Px86의 저장 버퍼는 선입 선출(FIFO; first in first out) 방식이 아님. Px86은 순차적 일관성 기반 모델과의 연관성이 없지만 개발자들은 대개 순차적 일관성을 선호. 이 연구에서는 Px86과 동치이면서도 개발자들의 정신적 모델에 더 가까운 x86 영구 메모리의 대안 모델을 제시. 이 모델은 동기정 명령을 지원. 제안한 모델과 정형 연관된 순차적 일관성 모델을 제시.

### Deciding ω-Regular Properties on Linear Recurrence Sequences
이 연구에서는 접두사에 독립적인 𝜔 정규 성질과 선형 점화식을 만족하는 수열을 입력받아 해당 수열의 부호에 대한 표현이 주어진 성질을 만족하는지 판단하는 알고리즘을 제시. 이 알고리즘은 점화식이 단순(반복문의 행렬이 대각화 가능)할 것을 요구.

### Automatically Eliminating Speculative Leaks from Cryptographic Code with Blade
근원 코드가 비밀 정보를 잘 보호한다고 해도, 프로세서의 추측 실행에 의해 비밀 정보가 누설될 수 있음. 추측 실행을 막으려면 모든 불러오기 명령 뒤에 장벽을 넣어야 하지만 큰 성능 손해가 발생. 이 연구에서는 암호학적 상수 시간 코드에서 추측 실행으로 인해 정보 누설이 일어나는 일을 효율적으로 방지하는 증명된 자동 도구를 제시. 고수준 언어를 저수준 명령으로 즉시 변환하는 방식을 통해 의미를 정의하고 protect라는 요소를 언어에 추가해 특정 변수에 대한 추측 실행만을 막을 수 있도록 함. 각 식을 일시적(추측 비밀을 가질 수 있음)이나 안정적으로 분류하고 일시적 근원으로부터 안정적 싱크로 가능 정보 흐름이 있는지 확인하는 타입 체계 제안. protect가 없는 기존 프로그램에 최소 개수의 protect를 추가해 추측 누설을 막는 알고리즘 제시.

### A Graded Dependent Type System with a Usage-Aware Semantics
등급 타입 이론(graded type theory)은 각 변수에 실행 시간에 사용되는 횟수를 나타내는 등급을 붙여 자원 사용을 타입 체계가 추적하고 논증할 수 있게 함. 이는 필요없는 항을 지워 성능을 향상시키거나 선형 자원을 제자리에서 수정함으로써 최적화를 할 수 있게 하는 등 다양한 활용이 가능. 이 연구에서는 등급 타입 체계를 의존적 타입으로 확장하고, 자원 사용을 표현할 수 있는 힙 기반 연산 의미를 정의한 뒤, 타입 체계가 건전함을 보임. 이는 일반적인 타입 안전성뿐 아니라 자원이 모자르지 않는다는 것도 보장함.

### The (In)Efficiency of Interaction
상호작용 기하(geometry of the interaction)에 바탕을 둔 요약 기계로 람다 대수를 계산하는 것은 공간 면에서 효율적이지만 환경을 사용한 방식보다 시간 면에서 비효율적. 상호작용 기하와 게임 의미에 관련된 여러 연구에도 불구하고, 관련된 요약 기계들의 성능을 비교하는 연구는 부족. 이 연구에서는 닫힌 항에 대한 약한 이름에 의한 호출을 하는 경우에 집중해 일반적인 정형 프레임워크를 제시. 기존 연구에서 IAM을 λIAM으로 재구성한 것처럼 JAM과 PAM을 λJAM과 λPAM으로 재구성. 여러 형태 사이의 이중 시뮬레이션을 제시하고 시간 효율성을 비교.

### A Pre-expectation Calculus for Probabilistic Sensitivity
민감도는 어떤 함수의 입력에 발생한 변화가 출력에 얼마나 영향을 주는지 나타냄. 확률적 프로그램의 민감도는 출력한 확률 분포 사이의 거리. 이 연구에서는 확률적 프로그램의 민감도를 논증하기 위한 관계적 기대 대수(relational expectation calculus)를 제안. 확률적 프로그램과 출력 상태에 대한 관계적 기대가 제시되었을 때, 입력 상태에 대한 관계적 기대를 계산하는 사전 기대 변형기(pre-expectation transformer)가 핵심. 계산한 사전 기대는 건전하므로, 사전 기대보다 작은 거리만큼 떨어진 입력에 대해 주어진 거리보다 적게 떨어진 출력이 나옴. 이 기술을 사용해 기계 학습 알고리즘의 안정성과 강화 학습 알고리즘의 수렴을 증명.

### Corpse Reviver: Sound and Efficient Gradual Typing via Contract Verification
점진적 언어는 프로그램에 실행 시간 검사를 추가해 계약이 위반을 탐지하며 그 책임(blame)은 언제나 타입 없는 부품에 있음. 고차 값이 타입 경계를 지날 때는 포장되어야 함. 이런 검사와 포장 등의 비용으로 인해 점진적 언어의 실제 프로그램들은 큰 성능 손해를 입음. 이 연구에서는 계약들을 정적으로 검증해 절대 실패하지 않을 검사들을 미리 제거하는 도구를 제시. 이 최적화는 모듈적으로 가능. 최적화의 건전성을 증명했으며 Typed Racket에 기술을 적용하고 실험을 통해 큰 성능 향상을 입증.

### Combining the Top-Down Propagation and Bottom-Up Enumeration for Inductive Program Synthesis
귀납적 프로그램 합성은 주어진 귀납적 명세를 따르는, 도메인 특화 언어로 작성된 프로그램을 생성. 상향식 열거 탐색(bottom-up enumerative search)은 올바른 프로그램을 찾을 때까지 작은 프로그램을 만들고 결합하며, 큰 프로그램에 적용되기 어려움. 하향식 전파(top-down propagation)는 주어진 합성 문제를 여러 부분 문제들로 분해해 푼 뒤 그 답들을 결합하며, 도메인 특화 지식이 필요하거나 도메인 특화 언어의 표현력을 제한해야 함. 이 연구에서는 하향식 전파를 임의의 도메인 특화 언어에 적용 가능하게 만드는 일반적 방법을 제시. 상향식 열거 탐색을 통해 작은 크기의 부품 라이브러리를 만든 뒤, 하향식 전파를 통해 부품을 사용한 해를 만들려 시도. 이를 위해 존재하는 부품이 충분한지 효율적으로 판단할 수 있어야 하며, 부품의 수가 잘 제어되어야 함. 각 문제를 해결하는 알고리즘 제시.

### Intensional Datatype Refinement: With Application to Scalable Verification of Pattern-Match Safety
패턴 대조 안전성 문제는 주어진 프로그램에 완전하지 않은 패턴 대조가 있을 때 패턴 대조 예외로 인해 프로그램이 중단될 수 있는지 확인하는 문제. 건전한 분석은 완전하지 않지만, 이해 가능한 큰 조각에 대해 완전함을 보장하는 것이 바람직. 이 연구에서는 대수적 데이터 타입을 지원하는 ML 방식 타입 체계의 자연스러운 확장에 대해 완전한 분석을 제시. 이 타입 체계는 다중가변성(polyvariance)과 경로 민감성을 결합. 이 분석은 주어진 프로그램에 대한 합리적 가정이 성립할 때 최악의 경우 프로그램 크기에 대해 선형 시간에 완료.

### Deciding Reachability under Persistent x86-TSO
영구 메모리의 메모리 모델은 기존의 전체 저장 순서보다 더 복잡. 약한 메모리 모델하에서 프로그램의 도달 가능성을 확인하는 것은 어려우며, 결정불가능할 수도 있음. 이 연구에서는 Raad와 동료들이 제시한 Px86 모델하에서 유한 상태 프로그램의 도달 가능성 문제가 결정가능함을 보임. 도달 가능성에 대한 잘 알려진 프레임워크는 잘 구조화된 전이 체계(well-structured transition system). Px86과 동치이면서 잘 구조화된 모델을 정의함. 이 모델의 핵심은 Px86 모델을 구성적으로 바라보는 것과 선입선출 버퍼만을 사용하는 것.

### Functorial Semantics for Partial Theories
이 연구에서는 기존의 등식 이론(equational theory)의 개념을 확장해 부분 이론(partial theory)에 대한 Lawvere 방식의 정의를 제시.

### Paradoxes of Probabilistic Programming: And How to Condition on Events of Measure Zero with Infinitesimal Probabilities
확률적 프로그램은 측도가 0인 사건을 조건으로 삼을 때 조건부 분포를 극한을 통해 계산. 이 연구에서는 측도가 0인 사건이 조건일 때 세 종류의 역설이 발생할 수 있음을 밝힘. 역설을 피하기 위해 observe의 의미를 바꾸어 명시적인 극한을 사용할 것을 제안. 극한을 계산하는 방법을 제시하고 계산된 극한이 올바르므로 역설이 발생하지 않음을 증명.

### On the Complexity of Bidirected Interleaved Dyck-Reachability
간선에 라벨이 붙은 그래프와 언어 L이 주어졌을 때 두 노드가 L 도달 가능하다는 것은 그 사이의 경로가 존재하며 그 경로가 나타내는 문자열이 L의 단어라는 것. Dyck 언어 Dk는 k 종류의 괄호로 구성된 괄호의 쌍이 맞는 문자열을 단어로 함. InterDyck 언어 Dp⊙Db는 Dp와 Db의 문자열을 임의로 교차해 만들어진 문자열들을 단어로 하는 언어. 별명(alias) 분석, 오염(taint) 분석, 타입 상태 분석 등이 InterDyck 도달 가능성 문제로 표현 가능. InterDyck 도달 가능성 문제의 결정 가능성은 알려져 있지 않았음. 이 연구에서는 양방향(bidirected) 그래프로 제한했을 때의 InterDyck 도달 가능성 문제를 탐구. D1⊙D1 도달 가능성이 다항 시간 안에 해결 가능하며 2 이상의 k에 대해 Dk⊙Dk 도달 가능성이 NP 난해임을 증명. D1⊙D1 경우에 대한 정확한 알고리즘을 제시하며 이를 Dk⊙Dk의 근사 알고리즘으로 사용해도 기존의 Dk⊙Dk를 위한 근사 알고리즘보다 정확함을 실험적으로 확인.

### The Taming of the Rew: A Type Theory with Computational Assumptions
증명 보조 도구는 타입 이론에 기반하며 해당 타입 이론하에서 증명할 수 없는 성질에 의존하는 정리를 증명하기 위해서는 공리를 추가해야 함. 공리는 계산 관점에서는 블랙박스이므로 항을 만들거나 제거할 수 없기 때문에 사용자들은 계산 대신 명제적 동일성(propositional equality)를 사용해야 하며 이는 계산을 매우 복잡하게 만듦. 임의의 계산 규칙을 추가하도록 허용하는 것은 논리의 무모순성을 깰 수 있는 것뿐 아니라 타입 검사의 결정 가능성이나 환원의 타입 보존을 깰 수 있기 때문에 매우 위험. 이 연구에서는 다시 쓰기 타입 이론(rewrite type theory)를 제시. 이 타입 이론은 새로운 다시 쓰기 규칙을 추가해 확장될 수 있으며 모듈적이고 결정 가능한 문법적 검사를 통해 다시 쓰기 규칙이 타입 보존과 결정 가능성을 해치지 않음을 확인할 수 있음.

### Abstracting Gradual Typing Moving Forward: Precise and Space-Efficient
요약된 점진적 타입(abstracting gradual typing)은 주어진 정적 타입 체계에 대응되는 합리적인 점진적 타입 체계를 설계하는 일반적인 방법. 요약된 점진적 타입을 통해 만들어진 의미는 실행 시간 검사의 공간 효율성을 보장하지 않으며(재귀 호출 시 타입 변환이 쌓일 수 있음) 정적 타입 체계에서 사용 가능한 모듈적인 타입 기반 논증(매개성, 타입으로 요약하여 숨겨진 필드에 접근할 수 없음 등)을 허용하지 않는다는 문제가 있음. 이 연구에서는 요약된 점진적 타입을 개선하여 공간 효율성과 정확한 실행 시간 모니터링을 가능하게 하는 충분 조건을 제시.