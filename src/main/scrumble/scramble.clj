(ns scrumble.scramble)

(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)

(def ^:const charset "abcdefghijklmnopqrstuvwxyz")
(def ^:const charset-size (count charset))

(defn- -ch-index [ch]
  (- (unchecked-int ch) (unchecked-int \a)))

(defn- aswap!
  "Updates a value of `idx` element in array `a` with function `f` that will take the old value and return the new
  value. Function `f` should return int.
  Returns new value, modifying array inplace."
  [^ints a idx f]
  (->> (aget a idx)
       (f)
       (aset-int a idx)))

(defn scramble?
  "Returns true if a portion of `source-string` characters can be rearranged to match `sub-string`, otherwise returns
  false. Both strings should contains only lower case letters (a-z).

  This function uses java array under the hood hower staing pure from the outside.
  There is a two part of algorhytm in this functuon: on the first stage it calculates frequencies of each char in
  `source-string`, on the second stage it iterates throught the `sub-string` decreasing frequency of the seen character
  until:
  1. the frequency becomes negative - it means that `source-string` doesn't contain enought characters to match
  `sub-string`.
  OR
  2. all charaters in sub-string have been iterated - it means that `source-string` can be rearranged to match
  `sub-string`."
  [source-string sub-string]
  (let [char-counts (int-array charset-size)]
    (doseq [ch source-string]
      (aswap! char-counts (-ch-index ch) inc))
    ;; not-any? works as loop here, iterates over sub-string
    (not-any? #(neg? ^int (aswap! char-counts (-ch-index %) dec)) sub-string)))
