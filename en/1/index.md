What researchers working on the field of PL do? Giving a simple and nice answer to the question is hard and I do not have enough knowledge and experience to answer the question. However, taking the course without having a common sense of what PL is might not be a good choice. In the article, I try my best to give an answer to the question.

## Semantics of Languages

When I say that I am studying PL, some people wonder whether I am making a new language. Of course, some PL researchers do design and implement new languages or improve existing languages. However, language design is only a small portion of PL and the researchers do various other stuff.

If I explain PL in one sentence, I can say that PL is a field that solves problems of computer science based on programming languages. It seems like a trivial word but, at the same time, is an important point. For example, if one wants to run programs faster, then people in computer architectures may improve hardware; people in computer networks may improve network protocols; people in artificial intelligence may improve structures of neural networks. People from each field solve the problem in their own way, which is completely different from the solutions came from the other fields. In the same manner, the PL people approach and solve common problems of computer science in the perspective of PL.

What is the perspective of PL? In my opinion, defining *semantics* of languages is a usual beginning of the perspective. Consider the following Python code:

```python
x = 1

def f():
    x = 2

f()
print(x)
```

What is the result of an execution of the code? In reality, it is `1`. According to the semantics of Python, *variable* `x` declared in the first line is different from `x` declared in *function* `f` and `x` used as an *argument* for `print` is identical to the former. However, one might think that three `x`'s refer to the same object and therefore `2` is the result. We cannot decide which perspective is the best. When a problem changes, more convenient semantics changes as well. The important point is that if semantics is not chosen and people do not agree to use one semantics, we are not able to find bugs of programs, to verify properties of programs, and to optimize programs while keeping their original behaviors. Thus, in PL, in many cases, researches begin with mathematically defining semantics of languages and the course aims the same thing.

## PL Research Topics

To find real research topics of PL, reading papers published in proceedings of recent PL conferences is the best strategy. When I took the *Introduction to Research* course, I skimmed through the abstracts of the papers from PLDI '18 and summarized the topics of the papers. The following is based on the summary.

PLDI (Programming Language Design and Implementation) and POPL (Principles of Programming Languages) are two top conferences in PL. As their names imply, PLDI focuses on implementation and application while POPL focuses on theory. The summary is for PLDI papers so that the below content can be biased for application of PL. Moreover, papers discussing very narrow topics usually come out in small workshops for the specific topics rather than large-scale top conferences, which target general audiences and subjects. Also, the below classification is my own attempt and there are many topics that cannot be classified into one among four areas. Please do not think the remaining content of the article shows every aspect of PL.

### Type System

In the *type system* area, people suggested new type system features or defined new languages and proved their properties like *type-soundness*. Others provided new *type checking* algorithms and proved their correctness.

They targeted not only very complex type system features but also *domain-specific languages*. For example, by designing new languages for concurrent programming, they could help programmers to write code avoiding race in an efficient way.

### Program Analysis

*Program analysis* automatically checks behaviors, termination for instance, of programs. There exist *static analysis* and *dynamic analysis*. Static analysis gets every information from source code or binary code via *model checking*, *abstract interpretation*, and *constraint solving* without executing it. On the other hand, dynamic analysis uses run-time data obtained by *testing* or *monitoring*.

There were some studies suggesting new analysis techniques or improving existing techniques by making them faster or more precise. Some researchers combined two approaches: using both static and monitored data at run-time.

Others applied analysis techniques to solve existing problems in specific domains. For example, static analysis was used for *probabilistic programs* to check the correctness or to infer the amount of required resources. Dynamic analysis was used to detect race in parallel computing or to give additional information to debuggers.

### Formal Verification

*Formal verification* also was one of the active research areas. The objects of formal verification were various. They included compilers, program behaviors, and hardware. The purpose of this area is proving the correctness of generated code of compilers, behaviors of programs, and operations of hardware by formal verification techniques.

Some researchers suggested new verification or proof techniques.

### Program Synthesis and Automatic Debugging

A portion of researchers was working on *program synthesis*, which generates new programs automatically according to given specifications. They tried many novel approaches including probabilistic model and guidance by syntax. 

## Acknowledgments

I thank professor Ryu for giving feedback on the article. I also thank professor Kim, Lee, and Yoo, who gave the *Introduction to Research* course.
