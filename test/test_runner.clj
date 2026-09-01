(ns test-runner
  (:require [clojure.test :as test]
            [name-registration-test]))

(defn -main [& _]
  (let [result (test/run-tests 'name-registration-test)]
    (when (pos? (+ (:fail result) (:error result)))
      (System/exit 1))))
