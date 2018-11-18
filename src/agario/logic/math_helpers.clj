(ns agario.logic.math-helpers)

(defn circles-overlap? [circle-1-radius x1 y1, circle-2-radius x2 y2]
  (let [x-diff (- x1 x2)
        y-diff (- y1 y2)
        rad-sum (+ circle-1-radius circle-2-radius)]

    (< (+ (* x-diff x-diff)
          (* y-diff y-diff))

       (* rad-sum rad-sum))))

(defn abs [n]
  (if (neg? n) (- n) n))

(defn abs-difference [n m]
  (abs (- n m)))

(defn signum [n]
  (cond
    (pos? n) 1
    (neg? n) -1
    :else 0))

(defn distance-between [position1 position2]
  (let [[x-diff y-diff] (mapv - position1 position2)]
    (Math/sqrt (+ (* x-diff x-diff)
                  (* y-diff y-diff)))))