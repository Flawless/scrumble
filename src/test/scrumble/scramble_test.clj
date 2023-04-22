(ns scrumble.scramble-test
  (:require
   [clojure.test :as t]
   [scrumble.scramble :as sut]))

(t/deftest scrumble?-test
  (t/is (not (sut/scramble? "abcdefghijklmnopqrstuvwxyz" "bookkeeper")))
  (t/is (sut/scramble? "rekqodlw" "world"))
  (t/is (sut/scramble? "cedewaraaossoqqyt" "codewars"))
  (t/is (not (sut/scramble? "katas"  "steak"))))
