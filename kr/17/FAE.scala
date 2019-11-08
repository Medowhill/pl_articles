object FAE {
  val L = '\u03bb'
  val SQ = "\u25a1"
  
  sealed trait FAE
  case class Num(n: Int) extends FAE {
    override def toString = n.toString
  }
  case class Add(l: FAE, r: FAE) extends FAE {
    override def toString = s"($l + $r)"
  }
  case class Sub(l: FAE, r: FAE) extends FAE {
    override def toString = s"($l - $r)"
  }
  case class Id(x: String) extends FAE {
    override def toString = x
  }
  case class Fun(x: String, b: FAE) extends FAE {
    override def toString = s"$L$x.$b"
  }
  case class App(f: FAE, a: FAE) extends FAE {
    override def toString = s"($f $a)" 
  }
  
  sealed trait FAEV
  case class NumV(n: Int) extends FAEV {
    override def toString = n.toString
  }
  case class CloV(p: String, b: FAE, e: Env) extends FAEV {
    override def toString = s"<$L$p.$b, ${envToString(e)}>"
  }
  
  type Env = Map[String, FAEV]
  type Cont = FAEV => FAEV
  def lookup(x: String, env: Env): FAEV =
    env.getOrElse(x, throw new Exception)
  def numVAdd(v1: FAEV, v2: FAEV): FAEV = {
    val NumV(n1) = v1; val NumV(n2) = v2; NumV(n1 + n2)
  }
  def numVSub(v1: FAEV, v2: FAEV): FAEV = {
    val NumV(n1) = v1; val NumV(n2) = v2; NumV(n1 - n2)
  }
  def envToString(env: Env): String =
    if (env.isEmpty) "\u2205" else env.mkString("[", ", ", "]")
  val buf = new scala.collection.mutable.ListBuffer[(String, String, String)]
  def log(e: String, s: String, env: String) = buf += ((e, s, env))
  
  def interp(e: FAE, env: Env, k: Cont, s: String): FAEV = {
    log(e.toString, s, envToString(env))
    e match {
      case Num(n) => k(NumV(n))
      case Id(x) => k(lookup(x, env))
      case Fun(x, b) => k(CloV(x, b, env))
      case Add(e1, e2) =>
        interp(e1, env, v1 =>
          interp(e2, env, v2 =>
            k({log(s"$v1 + $v2", s, envToString(env)); numVAdd(v1, v2)}),
            s.replace(SQ, s"($v1 + $SQ)")),
          s.replace(SQ, s"($SQ + $e2)"))
      case Sub(e1, e2) =>
        interp(e1, env, v1 =>
          interp(e2, env, v2 =>
            k({log(s"$v1 - $v2", s, envToString(env)); numVSub(v1, v2)}),
            s.replace(SQ, s"($v1 - $SQ)")),
          s.replace(SQ, s"$SQ - $e2"))
      case App(e1, e2) =>
        interp(e1, env, v1 =>
          interp(e2, env, v2 => {
            val CloV(xv1, ev1, sigmav1) = v1
            interp(ev1, sigmav1 + (xv1 -> v2), k, s)
          }, s.replace(SQ, s"($v1 $SQ)")),
          s.replace(SQ, s"($SQ $e2)")
        )
    }
  }
  
  def addSpaces(s: String, i: Int): String =
    s + (" " * (i - s.length))
  def run(e: FAE): String = {
    buf.clear()
    val v = interp(e, Map.empty, x => x, SQ)
    val eMax = buf.map(_._1.length).max
    val kMax = buf.map(_._2.length).max
    buf.map{ case (e, k, env) =>
      s"${addSpaces(e, eMax)} | ${addSpaces(k, kMax)} | $env"
    }.mkString("\n") + s"\n$v"
  }
}
