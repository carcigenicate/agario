(ns agario.logic.bacteria.bacterium-helpers
  (:require [agario.logic.math-helpers :as mh]
            [helpers.general-helpers :as g]))

(defn random-target-within [dimensions rand-gen]
  (let [[w h] dimensions]
    [(g/random-double 0 w rand-gen)
     (g/random-double 0 h rand-gen)]))

(defn at-target? [current-position target-position within-distance]
    (<= (mh/distance-between current-position target-position)
        within-distance))