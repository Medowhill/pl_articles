object KFAE {
  val L = '\u03bb'
  val SQ = "\u25a1"

  sealed trait KFAE
  case class Num(n: Int) extends KFAE {
    override def toString = n.toString
  }
  case class Add(l: KFAE, r: KFAE) extends KFAE {
    override def toString = s"($l + $r)"
  }
  case class Sub(l: KFAE, r: KFAE) extends KFAE {
    override def toString = s"($l - $r)"
  }
  case class Id(x: String) extends KFAE {
    override def toString = x
  }
  case class Fun(x: String, b: KFAE) extends KFAE {
    override def toString = s"$L$x.$b"
  }
  case class App(f: KFAE, a: KFAE) extends KFAE {
    override def toString = s"($f $a)"
  }
  case class Vcc(x: String, b: KFAE) extends KFAE {
    override def toString = s"vcc $x in $b"
  }

  sealed trait KFAEV
  case class NumV(n: Int) extends KFAEV {
    override def toString = n.toString
  }
  case class CloV(p: String, b: KFAE, e: Env) extends KFAEV {
    override def toString = s"<$L$p.$b, ${envToString(e)}>"
  }
  case class ContV(k: Cont, s: String,
    override val toString: String = ContV.getV) extends KFAEV {
    ContV._conts += this
  }
  object ContV {
    private[this] var v = 0
    private def getV = { v += 1; s"v$v" }
    private val _conts = new scala.collection.mutable.ListBuffer[ContV]
    def conts = _conts.toList
    def clear() = { v = 0; _conts.clear() }
  }

  type Env = Map[String, KFAEV]
  type Cont = KFAEV => KFAEV
  def lookup(x: String, env: Env): KFAEV =
    env.getOrElse(x, throw new Exception)
  def numVAdd(v1: KFAEV, v2: KFAEV): KFAEV = {
    val NumV(n1) = v1; val NumV(n2) = v2; NumV(n1 + n2)
  }
  def numVSub(v1: KFAEV, v2: KFAEV): KFAEV = {
    val NumV(n1) = v1; val NumV(n2) = v2; NumV(n1 - n2)
  }
  def envToString(env: Env): String =
    if (env.isEmpty) "\u2205" else env.mkString("[", ", ", "]")
  val buf = new scala.collection.mutable.ListBuffer[(String, String, String)]
  def log(e: String, s: String, env: String) = buf += ((e, s, env))

  def interp(e: KFAE, env: Env, k: Cont, s: String): KFAEV = {
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
          interp(e2, env, v2 => v1 match {
            case CloV(xv1, ev1, sigmav1) =>
              interp(ev1, sigmav1 + (xv1 -> v2), k, s)
            case ContV(k, s, _) =>
              log(v2.toString, s, "")
              k(v2)
          }, s.replace(SQ, s"($v1 $SQ)")),
          s.replace(SQ, s"($SQ $e2)")
        )
      case Vcc(x, b) =>
        val cont = ContV(k, s)
        interp(b, env + (x -> cont), k, s)
    }
  }

  def addSpaces(s: String, i: Int): String =
    s + (" " * (i - s.length))
  def run(e: KFAE): String = {
    buf.clear()
    ContV.clear()
    val v = interp(e, Map.empty, x => x, SQ)
    val eMax = buf.map(_._1.length).max
    val kMax = buf.map(_._2.length).max
    ContV.conts.map(k => s"$k = <${k.s}>").mkString("\n") + "\n" +
    buf.map{ case (e, k, env) =>
      s"${addSpaces(e, eMax)} | ${addSpaces(k, kMax)} | $env"
    }.mkString("\n") + s"\n$v"
  }

}
