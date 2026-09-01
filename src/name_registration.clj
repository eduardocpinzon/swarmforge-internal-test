(ns name-registration
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as string]))

(defn memory-storage
  ([] (memory-storage []))
  ([names]
   (let [contents (atom (vec names))]
     {:load (fn [] @contents)
      :save! (fn [updated-names] (reset! contents (vec updated-names)))})))

(defn file-storage [path]
  {:load (fn []
           (if (.exists (io/file path))
             (vec (edn/read-string (slurp path)))
             []))
   :save! (fn [updated-names]
            (spit path (pr-str (vec updated-names))))})

(defn stored-names [storage]
  ((:load storage)))

(defn open-screen [storage]
  {:storage storage
   :names (stored-names storage)
   :new-name ""
   :editing-name nil
   :edit-name ""
   :validation-message nil})

(defn enter-new-name [screen name]
  (assoc screen :new-name name :validation-message nil))

(defn- save-names [screen names]
  ((:save! (:storage screen)) names)
  (assoc screen :names (vec names)))

(defn- blank-name? [name]
  (string/blank? name))

(defn submit-new-name [screen]
  (if (blank-name? (:new-name screen))
    (assoc screen :validation-message "A name is required.")
    (-> screen
        (save-names (conj (:names screen) (:new-name screen)))
        (assoc :new-name "" :validation-message nil))))

(defn begin-edit [screen name]
  (assoc screen :editing-name name :edit-name name :validation-message nil))

(defn enter-edit-name [screen name]
  (assoc screen :edit-name name :validation-message nil))

(defn save-edit [screen]
  (if (blank-name? (:edit-name screen))
    (assoc screen :validation-message "A name is required.")
    (let [updated-names (mapv #(if (= % (:editing-name screen)) (:edit-name screen) %) (:names screen))]
      (-> screen
          (save-names updated-names)
          (assoc :editing-name nil :edit-name "" :validation-message nil)))))

(defn delete-name [screen name]
  (save-names screen (remove #(= % name) (:names screen))))
