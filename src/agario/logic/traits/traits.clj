(ns agario.logic.traits.traits
  (:require [agario.logic.math-helpers :as mh]))


; ========== Base Traits ==========


; ----- Identified -----

(defn give-id [obj id]
  (assoc obj :id id))

; ----- Positioned -----

(defn give-position [obj starting-position]
  (assoc obj :position starting-position))

(defn move-to [obj position]
  (assoc obj :position position))

(defn move-by [obj offsets]
  (update obj :position #(mapv + % offsets)))

; TODO: Figure out how to handle a 0 width (x-diff) better
(defn move-toward [obj target-position distance-to-move]
  (let [distance-to-target (mh/distance-between (:position obj) target-position)
        [x-diff y-diff] (mapv - target-position (:position obj))
        bound-dist (min distance-to-move distance-to-target)]
    (if (zero? x-diff)
      (let [adj-dist (* bound-dist (mh/signum y-diff))]
        (move-by obj [0 (* adj-dist)]))

      (let [theta (Math/atan2 y-diff x-diff)
            x-offset (* (Math/cos theta) bound-dist)
            y-offset (* (Math/sin theta) bound-dist)]
        (move-by obj [x-offset y-offset])))))

; ----- Matter -----

(defn give-mass [obj starting-mass]
  (assoc obj :mass starting-mass))

(defn add-mass [bac new-mass]
  (update bac :mass + new-mass))

(defn remove-mass [bac mass-to-remove]
  (add-mass bac (- mass-to-remove)))

(defn force-minimum-mass [bac minimum-mass]
  (update bac :mass max minimum-mass))

; TODO: Should the radius stuff even be here?
; TODO: Radius is a function of mass, not a trait itself?

; TODO: Do we really need to use Math/sqrt here?
; a = PI * r^2
; a * PI = r^2
; r = sqrt(a * PI)
(defn radius-from-mass [mass]
  ; Treating mass as area. Should work?
  ; The need for `sqrt` is unfortunate though
  (Math/sqrt (* mass Math/PI)))

(defn radius-of [bac]
  (-> bac
      (:mass)
      (radius-from-mass)))

; -----Positionally Targetted -----

(defn give-target-position [obj starting-target-position]
  (assoc obj :target-position starting-target-position))

; ----- Typed -----

; TODO: Give the object a set of :types instead?
; TODO:  Then add has-type? predicate

(defn give-type [obj type]
  (assoc obj :type type))


; ========== Combined Trait Properties ==========


; ----- Positional Matter

(defn colliding-with? [obj1 obj2]
  (let [[[x1 y1], [x2 y2]] (mapv :position [obj1 obj2])]
    (mh/circles-overlap? (radius-of obj1) x1 y1,
                         (radius-of obj2) x2 y2)))

