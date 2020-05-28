import scala.io.Source
import scala.sys.process.Process

import java.io.{File, FileOutputStream}
import java.text.SimpleDateFormat
import java.util.{Calendar, Date, Locale}

object Format {
  private def read(name: String): String =
    Source.fromFile(name).mkString.filter(c => c.isLetter || c.isDigit || " :[](){}.,!?-_".toSet(c))

  private def write(name: String, content: String): File = {
    val file = new File(name)
    val out = new FileOutputStream(file)
    out.write(content.getBytes)
    out.close()
    file
  }

  def main(args: Array[String]): Unit = args.toList match {
    case name :: tail =>
      val preview = tail.contains("-p")
      val List(num, ver, title, lang) =
        List("num", "ver", "title", "lang").map(read)
      val kr = lang == "kr"
      val timeFile = new File("time")
      val time = if (timeFile.exists) Some(read("time")) else None
      val current = new SimpleDateFormat(
        dateFmt(kr),
        if (kr) Locale.KOREAN else Locale.ENGLISH
      ).format(new Date)

      val from = Source.fromFile(s"$name.md").mkString
      val mdFile = write(tempMd,
        header(title, time, current, kr) +
        //from.replaceAll("\\\\", "\\\\\\\\")
        escapeBackslash(from)
      )
      Process(s"pandoc -s --css article.v1.css --css $lang-article.v1.css -o $tempHtml $tempMd").!

      val htmlFile = new File(tempHtml)
      val html = Source.fromFile(htmlFile).mkString
      val i = html.indexOf("<style>")
      val j = html.lastIndexOf("</style>")
      write(s"${lang}_${num}_${ver}",
        normalizeQuotation(
          (html.substring(0, i) + html.substring(j + "</style>".length))
        ).replace("</head>", scripts(num, ver, kr))
      )

      mdFile.delete()
      htmlFile.delete()

      if (!preview) {
        write("ver", (ver.toInt + 1).toString)
        if (time.isEmpty)
          write("time", current)
      }
  }

  def escapeBackslash(s: String): String = {
    var code = false
    (for (c <- s) yield {
      if (c == '`') code = !code
      if (c == '\\' && !code) "\\\\" else c.toString
    }).mkString
  }

  def normalizeQuotation(s: String): String = {
    var escape, formula, close = false
    for (c <- s) yield {
      if (escape && (c == '(' || c == '[')) formula = true
      if (escape && (c == ')' || c == ']')) formula = false
      escape = c == '\\'
      if (formula && (c == '“' || c == '”' || c == '\"')) {
        val ch = if (close) '”' else '“'
        close = !close
        ch
      } else c
    }
  }

  private val tempMd = "temp.md"
  private val tempHtml = "temp.html"

  private def dateFmt(kr: Boolean) =
    if (kr) "y년 M월 d일 H시 m분" else "d MMMM yyyy HH:mm"
  private def header(title: String, time: Option[String], current: String, kr: Boolean) =
s"""% $title
   |% ${if (kr) "홍재민" else "Jaemin Hong"}
   |% ${if (time.isEmpty) current else time.get + s" (${if (kr) "최근 수정" else "Last update"}: $current)"}
   |
   |---
   |
   |""".stripMargin

  private def scripts(num: String, ver: String, kr: Boolean): String =
s"""<script type="text/x-mathjax-config">
   |MathJax.Hub.Config({tex2jax: {
   |  inlineMath: [ ["\\\\(","\\\\)"] ],
   |  displayMath: [ ["\\\\[","\\\\]"] ]
   |}});
   |</script>
   |<script type="text/javascript"
   |  src="https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.5/latest.js?config=TeX-MML-AM_CHTML">
   |</script>
   |<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.0/jquery.min.js"></script>
   |<script src="https://unpkg.com/axios/dist/axios.min.js"></script>
   |<script src="article.v1.js"></script>
   |<script>
   |$$(document).ready(() => {
   |  set(${num}, ${ver}, ${kr});
   |  latest();
   |  comments();
   |  others();
   |  wordsInit();
   |});
   |</script>
   |</head>""".stripMargin
}
