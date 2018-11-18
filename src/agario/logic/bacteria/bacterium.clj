(ns agario.logic.bacteria.bacterium
  (:require [agario.logic.math-helpers :as mh]
            [agario.logic.turn :as t]

            [agario.logic.traits.traits :as tr]))

(def minimum-mass 1)

; TODO: Change :type to a :types map/set to allow for multiple types?

(defn new-bacterium
  ([id starting-position]
   (-> {}
       (tr/give-id id)
       (tr/give-type :motionless)
       (tr/give-mass minimum-mass)
       (tr/give-position starting-position)))

  ([starting-position]
   (new-bacterium nil starting-position)))

(defn speed-of [bac]
  5) ; TODO: Make inversly proportional to mass. Inverse "exponential shrinkage"?

(defmethod t/take-turn :motionless [_ _ new-world]
  new-world)