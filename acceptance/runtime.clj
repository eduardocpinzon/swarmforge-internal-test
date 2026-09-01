(ns acceptance.runtime
  (:require [clojure.string :as string]))

(defn- substitute-example [text example]
  (reduce (fn [result [key value]]
            (string/replace result (str "<" key ">") (str value)))
          text
          example))

(defn- scenario-runs [scenario]
  (let [examples (get scenario "examples")]
    (if (seq examples) examples [{}])))

(defn- scenario-steps [background scenario example]
  (map #(update % "text" substitute-example example)
       (concat background (get scenario "steps"))))

(defn- matching-handler [definitions text]
  (some (fn [{:keys [pattern handler]}]
          (when-let [match (re-matches pattern text)]
            [handler (if (string? match) [] (rest match))]))
        definitions))

(defn- run-step [definitions world step]
  (let [text (get step "text")]
    (if-let [[handler captures] (matching-handler definitions text)]
      (apply handler world captures)
      (throw (ex-info "No acceptance step handler matches step." {:step text})))))

(defn- run-scenario [definitions background scenario example]
  (reduce #(run-step definitions %1 %2) {} (scenario-steps background scenario example)))

(defn run-feature [feature definitions]
  (let [background (get feature "background")]
    (vec
     (for [scenario (get feature "scenarios")
           example (scenario-runs scenario)
           :let [label (get scenario "name")]
           :when (try
                   (run-scenario definitions background scenario example)
                   false
                   (catch Exception error
                     (println "FAIL" label "-" (.getMessage error))
                     true))]
       label))))
