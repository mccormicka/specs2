package org.specs2
package specification

import execute._

abstract class HtmlLink(val url: String, val beforeText: String, val linkText: String, val afterText: String, val tip: String) {
  def is(name: SpecName) = false
}
case class SpecHtmlLink(val name: SpecName,
                        override val beforeText: String,
                        override val linkText: String,
                        override val afterText: String,
                        override val tip: String, result: Result) extends
   HtmlLink(name.url, beforeText, linkText, afterText, tip) {
  override def is(n: SpecName) = name.equals(n)
}

case class UrlHtmlLink(override val url: String,
                       override val beforeText: String,
                       override val linkText: String,
                       override val afterText: String,
                       override val tip: String) extends
   HtmlLink(url, beforeText, linkText, afterText, tip)


object HtmlLink {
 def apply(name: SpecName, beforeText: String = "", linkText: String = "", afterText: String = "", tip: String = "", result: Result = Success()): HtmlLink  =
   new SpecHtmlLink(name, beforeText, linkText, afterText, tip, result)
}