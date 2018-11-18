(ns agario.logic.playground
  (:require [agario.logic.world :as w]
            [agario.logic.bacteria.bacterium :as b]
            [agario.logic.bacteria.directed-bacterium :as db]))

(def test-world
  (let [db #(-> (b/new-bacterium %) (db/make-directed %))]
    (-> (w/new-world [20 20])
        (w/add-bacterium-with-gen-id! (db [0 0]))
        (w/add-bacterium-with-gen-id! (db [8 8])))))