package org.specs2
package matcher
import execute._
import specification._
import sys._
import io.MockOutput

class LogicalMatcherSpec extends Specification with ResultMatchers { def is =

  "a matcher can be or-ed with another one"                                                                             ^
    "if both matches are ok the result is ok"                                                                           ! or1^
    "if both matches are ko the result is ko"                                                                           ! or2^
    "if the first matcher is ok, the second one is not evaluated"                                                       ! or3^
    "if both matchers are ko the combination is ko"                                                                     ! or4^
    "if the first matcher is ko, and the second ok, the combination is ok"                                              ! or5^
    "if the matchers throw exceptions the combination must be a success when 'failure' or 'success'"                    ! or6^
                                                                                                                        p^
  "it is possible also to 'or' 2 match expressions"                                                                     ^
    "if the first is ok, the result is ok"                                                                              ! or7^
    "if the first is not ok, the second is not evaluated"                                                               ! or8^
    "ko and ok and ko is ok"                                                                                            ! or9^
                                                                                                                        p^
  "a matcher can be and-ed with another one"                                                                            ^
    "if both matches are ok the result is ok"                                                                           ! and1^
                                                                                                                        p^
  "it is possible also to 'and' 2 match expressions"                                                                    ^
    "if both matches are ok the result is ok"                                                                           ! and2^
    "if the first is not ok, the second is not evaluated"                                                               ! and3^
    "ok and ko and ok is ko"                                                                                            ! and4^
                                                                                                                        p^
  "a matcher can be ok or be skipped"                                                                                   ^
    "if it is ok, it returns a MatchSuccess result"                                                                     ! skip1^
    "if it is ko, it returns a MatchSkip result"                                                                        ! skip2^
    "if it throws an exception, it returns a MatchSkip result"                                                          ! skip3^
    "a skipped message can also be added in front of the failure message"                                               ! skip4^
                                                                                                                        p^
    "a matcher can be ok or be pending"                                                                                 ^
    "if it is ok, it returns a MatchSuccess result"                                                                     ! pending1^
    "if it is ko, it returns a MatchPending result"                                                                     ! pending2^
    "if it throws an exception, it returns a MatchPending result"                                                       ! pending3^
    "a pending message can also be added in front of the failure message"                                               ! pending4^
                                                                                                                        p^
  "a matcher can applied only if a boolean condition is true"                                                           ^
    "if the condition is true, it is applied"                                                                           ! when1^
    "if the condition is false, it is not and a success is returned"                                                    ! when2^
    "if the condition is false, a message can be added to the success result"                                           ! when3^
    "'unless' can also be used to avoid negating the condition"                                                         ! when4^
                                                                                                                        p^
  "a matcher can applied if and only if a boolean condition is true"                                                    ^
    "if the condition is true, it is applied"                                                                           ^
      "the result is true if the application is true"                                                                   ! iff1^
      "the result is false if the application is false"                                                                 ! iff2^
                                                                                                                        p^
    "if the condition is false, it is applied"                                                                          ^
      "the result is true if the application is false"                                                                  ! iff3^
      "the result is false if the application is true"                                                                  ! iff4^
                                                                                                                        end

  def or1 = "eric" must (beMatching("e.*") or beMatching(".*c"))
  def or2 = "eric" must (beMatching("a.*") or beMatching(".*z")).not
  def or3 = "eric" must (beMatching("e.*") or beMatching({error("boom");".*z"}))
  def or4 = "eric" must not (beMatching("a.*") or beMatching(".*z"))
  def or5 = ("eric" must (beMatching("a.*") or beMatching("z.*"))) returns
            "'eric' doesn't match 'a.*'; 'eric' doesn't match 'z.*'"

  def or6 = new Scope with MustThrownMatchers {
    "eric" must (beMatching("a.*") or beMatching("e.*"))
  }
  def or7 = ("eric" must be matching("e.*")) or ("eric" must be matching(".*d"))
  def or8 = {
    val out = new MockOutput {}
    ("eric" must be matching("e.*")) or { out.println("DONT"); "torreborre" must be matching(".*tor.*") }
    out.messages must not contain("DONT")
  }
  def or9 = ((true === false) or (true === true) or (true === false)) must beSuccessful

  def and1 = "eric" must be matching("e.*") and be matching(".*c")
  def and2 = ("eric" must be matching("e.*")) and ("torreborre" must be matching(".*tor.*"))
  def and3 = {
    val out = new MockOutput {}
    ("eric" must be matching("x.*")) and { out.println("DONT"); "torreborre" must be matching(".*tor.*") }
    out.messages must not contain("DONT")
  }
  def and4 = ((true === true) and (true === false) and (true === true)) must beFailing

  def skip1 = 1 must be_==(1).orSkip
  def skip2 = (1 must be_==(2).orSkip).toResult must_== Skipped("'1' is not equal to '2'")
  def skip3 = (1 must be_==({sys.error("boom");2}).orSkip("skip this")).toResult must_== Skipped("skip this: boom")
  def skip4 = (1 must be_==(2).orSkip("precondition failed")).toResult must_== Skipped("precondition failed: '1' is not equal to '2'")

  def pending1 = 1 must be_==(1).orPending
  def pending2 = (1 must be_==(2).orPending).toResult must_== Pending("'1' is not equal to '2'")
  def pending3 = (1 must be_==({sys.error("boom");2}).orPending("todo")).toResult must_== Pending("todo: boom")
  def pending4 = (1 must be_==(2).orPending("precondition failed")).toResult must_== Pending("precondition failed: '1' is not equal to '2'")

  def when1 = (1 must be_==(1).when(true)).toResult must beSuccessful
  def when2 = (1 must be_==(2).when(false)).toResult must beSuccessful
  def when3 = (1 must be_==(2).when(false, "no worries")).message must_== "no worries"
  def when4 = (1 must be_==(2).unless(true)).toResult must beSuccessful

  def iff1 = (1 must be_==(1).iff(true)).toResult must beSuccessful
  def iff2 = (1 must be_==(2).iff(true)).toResult must beFailing
  def iff3 = (1 must be_==(2).iff(false)).toResult must beSuccessful
  def iff4 = (1 must be_==(1).iff(false)).toResult must beFailing
}