(ns acceptance.name-registration-steps
  (:require [name-registration :as registration]))

(defn- require-screen [world]
  (or (:screen world)
      (throw (ex-info "The name registration screen is not open." {}))))

(defn- update-screen [world transform]
  (update world :screen transform))

(defn- visible? [world name]
  (some #(= name %) (:names (require-screen world))))

(def step-definitions
  [{:pattern #"^the name registration screen is open$"
    :handler (fn [_]
               (let [storage (registration/memory-storage)]
                 {:storage storage :screen (registration/open-screen storage)}))}
   {:pattern #"^\"(.+)\" is registered$"
    :handler (fn [world name]
               (update-screen world #(-> %
                                         (registration/enter-new-name name)
                                         registration/submit-new-name)))}
   {:pattern #"^the person enters \"(.+)\" as a new name$"
    :handler (fn [world name]
               (update-screen world #(registration/enter-new-name % name)))}
   {:pattern #"^submits the new name$"
    :handler (fn [world]
               (update-screen world registration/submit-new-name))}
   {:pattern #"^\"(.+)\" is shown in the registered-name list$"
    :handler (fn [world name]
               (when-not (visible? world name)
                 (throw (ex-info "Expected name to be visible." {:name name})))
               world)}
   {:pattern #"^\"(.+)\" is not shown in the registered-name list$"
    :handler (fn [world name]
               (when (visible? world name)
                 (throw (ex-info "Expected name not to be visible." {:name name})))
               world)}
   {:pattern #"^the new-name input is empty$"
    :handler (fn [world]
               (when-not (= "" (:new-name (require-screen world)))
                 (throw (ex-info "Expected new-name input to be empty." {})))
               world)}
   {:pattern #"^the person edits \"(.+)\" to \"(.+)\"$"
    :handler (fn [world existing-name updated-name]
               (update-screen world #(-> %
                                         (registration/begin-edit existing-name)
                                         (registration/enter-edit-name updated-name))))}
   {:pattern #"^saves the edit$"
    :handler (fn [world]
               (update-screen world registration/save-edit))}
   {:pattern #"^the person deletes \"(.+)\"$"
    :handler (fn [world name]
               (update-screen world #(registration/delete-name % name)))}
   {:pattern #"^the person reopens the name registration screen$"
    :handler (fn [world]
               (assoc world :screen (registration/open-screen (:storage world))))}
   {:pattern #"^the person submits an empty new name$"
    :handler (fn [world]
               (update-screen world #(-> %
                                         (registration/enter-new-name "")
                                         registration/submit-new-name)))}
   {:pattern #"^a validation message explains that a name is required$"
    :handler (fn [world]
               (when-not (= "A name is required." (:validation-message (require-screen world)))
                 (throw (ex-info "Expected required-name validation message." {})))
               world)}
   {:pattern #"^the registered-name list contains only \"(.+)\"$"
    :handler (fn [world name]
               (when-not (= [name] (:names (require-screen world)))
                 (throw (ex-info "Expected exactly one registered name." {:name name})))
               world)}])
