import scala.io.Source
import scala.sys.process.Process

import java.io.{File, FileOutputStream}
import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

object Format {
  def main(args: Array[String]): Unit = args match {
    case Array(name, lang) =>
      val kr = lang == "kr"
      val num = Source.fromFile(s"$name.num").mkString.filter(_.isDigit)
      val ver = Source.fromFile(s"$name.ver").mkString.filter(_.isDigit)

      val from = Source.fromFile(s"$name.md").mkString
      val mdFile = new File(tempMd)
      val md = new FileOutputStream(mdFile)
      md.write(from
        .replaceAll("\\\\", "\\\\\\\\")
        .replace("today-date", new SimpleDateFormat(dateFmt(kr)).format(new Date))
        .getBytes
      )
      md.close()
      Process(s"pandoc -s --css $lang.css -o $tempHtml $tempMd").!

      val htmlFile = new File(tempHtml)
      val html = Source.fromFile(htmlFile).mkString
      val to = new FileOutputStream(s"${lang}_${num}_${ver}")
      to.write(html
        .replace("</head>", scripts(num, ver, kr))
        .replace("</header>", alertDiv)
        .getBytes)
      to.close()

      mdFile.delete()
      htmlFile.delete()
  }

  private val tempMd = "temp.md"
  private val tempHtml = "temp.html"

  private def dateFmt(kr: Boolean) =
    if (kr) "y년 M월 d일 H시 m분" else "d MMMM yyyy HH:mm"
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
   |  if (latest > 0)
   |    $$('#version-alert').append(`<p style="color:red;">${if (kr) "글이 최신 판이 아닙니다" else "Not the latest version"}.&nbsp;<a href="./${num}_$${latest}">${if (kr) "최신 판 보러가기" else "Go to the latest"}</a></p>`);
   |});
   |</script>
   |</head>""".stripMargin
  private val alertDiv =
s"""<div id="version-alert"></div>
   |</header>""".stripMargin
}
