(ns agario.logic.id-generator)

(defn new-id-generator [starting-id]
  ; dec'ing since gen-id will return the advanced ID
  ; Update to 1.9.0!
  {:counter (atom (dec starting-id))})

(defn gen-id! [id-factory]
  (swap! (:counter id-factory) inc))