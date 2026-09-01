(ns name-registration-test
  (:require [clojure.test :refer [deftest is testing]]
            [name-registration :as registration]))

(deftest submitted-names-are-visible-and-persisted
  (let [storage (registration/memory-storage)
        screen (-> (registration/open-screen storage)
                   (registration/enter-new-name "Ana")
                   registration/submit-new-name)]
    (is (= ["Ana"] (:names screen)))
    (is (= "" (:new-name screen)))
    (is (= ["Ana"] (registration/stored-names storage)))
    (is (= ["Ana"] (:names (registration/open-screen storage))))))

(deftest file-storage-retains-names-between-screen-instances
  (let [path "tmp/name-registration-unit-storage.edn"
        storage (registration/file-storage path)]
    (try
      (let [saved-screen (-> (registration/open-screen storage)
                             (registration/enter-new-name "Ana")
                             registration/submit-new-name)]
        (is (= ["Ana"] (:names saved-screen)))
        (is (= ["Ana"] (:names (registration/open-screen storage)))))
      (finally
        (.delete (java.io.File. path))))))

(deftest a-saved-edit-replaces-the-original-name
  (let [storage (registration/memory-storage ["Ana"])
        screen (-> (registration/open-screen storage)
                   (registration/begin-edit "Ana")
                   (registration/enter-edit-name "Beatriz")
                   registration/save-edit)]
    (is (= ["Beatriz"] (:names screen)))
    (is (nil? (:editing-name screen)))
    (is (= ["Beatriz"] (registration/stored-names storage)))))

(deftest deleting-a-name-keeps-other-saved-names
  (let [storage (registration/memory-storage ["Ana" "Bruno"])
        screen (registration/delete-name (registration/open-screen storage) "Ana")]
    (is (= ["Bruno"] (:names screen)))
    (is (= ["Bruno"] (registration/stored-names storage)))))

(deftest submitting-a-blank-name-preserves-the-list-and-explains-the-error
  (let [storage (registration/memory-storage ["Ana"])
        screen (-> (registration/open-screen storage)
                   (registration/enter-new-name "   ")
                   registration/submit-new-name)]
    (is (= ["Ana"] (:names screen)))
    (is (= "A name is required." (:validation-message screen)))
    (is (= ["Ana"] (registration/stored-names storage)))))
