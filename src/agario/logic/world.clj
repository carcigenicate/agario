(ns agario.logic.world
  (:require [agario.logic.bacteria.bacterium :as b]
            [agario.logic.id-generator :as id]
            [agario.logic.turn :as t]
            [clojure.string :as s]
            [agario.logic.traits.traits :as tr]))

(def ^:dynamic *global-id-generator* (id/new-id-generator 0))

; TODO: Make a settings object that holds tweaks like this?
(def mass-lost-per-tick 0)

(defn new-world [dimensions]
  {:bacteria {}, :agar {}, :dimensions dimensions})

(defn- add-agar [world agar]
  (update world :agar conj agar))

(defn add-agar-with-gen-id! [world agar]
  (let [id'd (assoc agar :id (id/gen-id! *global-id-generator*))]
    (add-agar world id'd)))

(defn add-bacterium [world bacterium]
  (assoc-in world [:bacteria (:id bacterium)] bacterium))

(defn add-bacterium-with-gen-id! [world bacterium]
  (let [id'd (assoc bacterium :id (id/gen-id! *global-id-generator*))]
    (add-bacterium world id'd)))

; TODO: Allow a bacterium to be passed instead of an id, then just :id here?
; TODO: Rename to reflect that it only works on :bacteria?
(defn update-bacteria [world id f & args]
  (update-in world [:bacteria id] #(apply f % args)))

(defn inbounds? [world position]
  (let [[w h] (:dimensions world)
        [x y] position]
    (and (< -1 x w)
         (< -1 y h))))

(defn colliding-with [bacterium bacteria]
  (let [excl-bacteria (dissoc bacteria (:id bacterium))]
    (reduce-kv (fn [acc-colliding _ other-bacterium]
                 (if (tr/colliding-with? bacterium other-bacterium)
                   (conj acc-colliding other-bacterium)
                   acc-colliding))
               []
               excl-bacteria)))

; TODO: Switch reduce-kv to loop and dissoc from the to-be-processed bacteria when we know they've been eaten?
(defn resolve-collisions [world]
  (letfn [(is-largest? [cur-b coll-bs]
            (->> coll-bs
                 (mapv :mass)
                 (apply max)
                 (> (:mass cur-b))))

          (dissoc-all [map keys] (apply dissoc map keys))]

    (update world :bacteria
      #(reduce-kv (fn [acc-bs cur-id cur-bac]
                    (if-let [colls (seq (colliding-with cur-bac %))]
                      (if (is-largest? cur-bac colls)
                        (let [new-mass (->> colls (mapv :mass) (apply +))]
                          (-> acc-bs
                            (update cur-id tr/add-mass new-mass)
                            (dissoc-all (mapv :id colls))))

                        acc-bs) ; TODO: Can this duplication be reduced?

                      acc-bs)) ; Combine the conditions?
                  %
                  %))))

(defn advance-bacteria [world]
  (->> (:bacteria world)
    (reduce-kv (fn [acc-world id bacterium]
                 (-> bacterium
                     (t/take-turn world acc-world)
                     (update-bacteria id tr/remove-mass mass-lost-per-tick)))
               world)

    (resolve-collisions)))

(defn format-world [world]
  (let [{:keys [bacteria dimensions]} world
        [w h] dimensions
        up (fn [s [x y] c] (assoc-in s [(int y) (int x)] c))
        rows (reduce-kv (fn [acc-s _ bac]
                          (-> acc-s
                              (up (:target-position bac) \X)
                              (up (:position bac) \B)))
                        (vec (repeat h (vec (repeat w " "))))
                        bacteria)]
    (->> rows
         (map #(s/join " " %))
         (s/join "\n"))))
