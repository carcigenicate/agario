(ns agario.logic.turn)

; TODO: Move to traits? Is it a trait?

(defmulti take-turn (fn [bacterium old-world new-world] (:type bacterium)))

#_
(defmulti take-turn
          (fn [bacterium-id old-world new-world]
            (let [bacterium (get-in old-world [:bacteria bacterium-id])]
              (:type bacterium))))

; TODO: Create a nil overload that throws a custom exception message?