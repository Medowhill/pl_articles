import scala.io.Source
import scala.sys.process.Process

import java.io.{File, FileOutputStream}
import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

object Format {
  private def read(name: String): String =
    Source.fromFile(name).mkString.filter(c => c.isLetter || c.isDigit || c == ' ' || c == ':')

  private def write(name: String, content: String): File = {
    val file = new File(name)
    val out = new FileOutputStream(file)
    out.write(content.getBytes)
    out.close()
    file
  }
    

  def main(args: Array[String]): Unit = args.toList match {
    case name :: tail =>
      val preview = tail.nonEmpty
      val List(num, ver, title, lang) =
        List("num", "ver", "title", "lang").map(read)
      val kr = lang == "kr"
      val timeFile = new File("time")
      val time = if (timeFile.exists) Some(read("time")) else None
      val current = new SimpleDateFormat(dateFmt(kr)).format(new Date)

      val from = Source.fromFile(s"$name.md").mkString
      val mdFile = write(tempMd,
        header(title, time, current, kr) +
        from.replaceAll("\\\\", "\\\\\\\\") +
        footer(kr)
      )
      Process(s"pandoc -s --css $lang.css -o $tempHtml $tempMd").!

      val htmlFile = new File(tempHtml)
      val html = Source.fromFile(htmlFile).mkString
      write(s"${lang}_${num}_${ver}",
        html
        .replace("</head>", scripts(num, ver, kr))
        .replace("</header>", alertDiv)
      )

      mdFile.delete()
      htmlFile.delete()

      if (!preview) {
        write("ver", (ver.toInt + 1).toString)
        if (time.isEmpty)
          write("time", current)
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
  private def footer(kr: Boolean) =
    if (kr)
"""|
   |
   |---
   |
   |감수: 류석영 교수님 (``sryu.cs@kaist.ac.kr``)
   |
   |글에 대한 질문과 의견은 ``jaemin.hong@kaist.ac.kr``로 부탁드립니다.""".stripMargin
    else
"""|
   |
   |---
   |
   |Edited under the supervision of Prof. Sukyoung Ryu (``sryu.cs@kaist.ac.kr``).
   |
   |If you have any question or opinion, please send an email to ``jaemin.hong@kaist.ac.kr``.""".stripMargin

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
   |<script>
   |axios.get('https://ch30tnydq2.execute-api.ap-northeast-2.amazonaws.com/default/article/?num=$num')
   |.then(res => {
   |  let latest = parseInt(res.data.${if (kr) "kr" else "en"}_latest.N);
   |  if (latest > $ver)
   |    $$('#version-alert').append(`<p style="color:red;">${if (kr) "글이 최신 판이 아닙니다" else "Not the latest version"}.&nbsp;<a href="./${if (kr) "kr" else "en"}_${num}_$${latest}">${if (kr) "최신 판 보러가기" else "Go to the latest"}</a></p>`);
   |});
   |</script>
   |</head>""".stripMargin
  private val alertDiv =
s"""<div id="version-alert"></div>
   |</header>""".stripMargin
}
