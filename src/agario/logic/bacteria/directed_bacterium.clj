(ns agario.logic.bacteria.directed-bacterium
  (:require [agario.logic.bacteria.bacterium :as b]
            [agario.logic.bacteria.bacterium-helpers :as bh]
            [agario.logic.turn :as t]
            [agario.logic.world :as w]
            [helpers.general-helpers :as g]
            [agario.logic.math-helpers :as mh]
            [agario.logic.traits.traits :as tr]))

(def directed-rand-gen (g/new-rand-gen 99))

(defn make-directed [bacterium starting-target]
  (-> bacterium
      (tr/give-type :directed)
      (tr/give-target-position starting-target)))

(defmethod t/take-turn :directed [bacterium old-world new-world]
  (w/update-bacteria new-world (:id bacterium)
                     #(let [{:keys [target-position]} bacterium
                            {:keys [dimensions]} old-world
                            advanced (tr/move-toward % target-position (b/speed-of %))]

                        (if (bh/at-target? (:position advanced) target-position (tr/radius-of %))
                          (assoc advanced :target-position
                                          (bh/random-target-within dimensions directed-rand-gen))
                          advanced))))
