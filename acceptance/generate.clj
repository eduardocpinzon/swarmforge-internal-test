(ns acceptance.generate
  (:require [babashka.json :as json]))

(defn -main [input-path output-path]
  (let [feature (json/read-str (slurp input-path))
        program (str "(ns acceptance.generated.name-registration\n"
                     "  (:require [acceptance.runtime :as runtime]\n"
                     "            [acceptance.name-registration-steps :as steps]))\n\n"
                     "(def feature " (pr-str feature) ")\n\n"
                     "(defn -main [& _]\n"
                     "  (let [failures (runtime/run-feature feature steps/step-definitions)]\n"
                     "    (when (seq failures)\n"
                     "      (System/exit 1))))\n")]
    (spit output-path program)))
