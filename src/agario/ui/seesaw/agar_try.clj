(ns agario.ui.seesaw.agar-try
  (:require [seesaw.core :as sc]
            [seesaw.graphics :as sg]
            [seesaw.color :as s-col]
            [agario.ui.seesaw.seesaw-helpers :as sh]
            [agario.logic.world :as w]
            [helpers.general-helpers :as g]
            [agario.logic.bacteria.bacterium :as b]
            [agario.logic.bacteria.directed-bacterium :as db]
            [agario.logic.traits.traits :as tr])

  (:import [javax.swing Timer]
           [java.awt Color]))

(def starting-window-width 1000)
(def starting-window-height starting-window-width)

(def loop-delay 50)

(def seesaw-rand-gen (g/new-rand-gen))

(defn world->canvas-position [position world canvas]
  (let [[x y] position
        [ww wh] (:dimensions world)
        [cw ch] (sh/component-dimensions canvas)]
    [(g/map-range x, 0 ww, 0 cw)
     (g/map-range y, 0 wh, 0 ch)]))

(defn world->canvas-radius [radius world canvas]
  (->> (world->canvas-position [radius radius] world canvas)
       (apply max)))

(defn paint [world-atom canvas g]
  (let [{:keys [bacteria] :as w} @world-atom]
    (doseq [[id b] bacteria
            :let [{:keys [position mass target-position]} b
                  [bx by] (world->canvas-position position w canvas)
                  rad (world->canvas-radius (tr/radius-of b) w canvas)
                  col (Color. (hash id))
                  text-size (world->canvas-radius (* mass 0.025) w canvas)]]
      (sg/draw g
               (sg/circle bx by rad)
               (sg/style :background col)

               (sg/string-shape bx by (str (int mass)))
               (sg/style :font {:size text-size}))

      (when target-position
        (let [[tx ty] (world->canvas-position target-position w canvas)]
          (sg/draw g
                   (sg/string-shape tx ty "X")
                   (sg/style :font {:size text-size}, :foreground col)))))))

(defn new-canvas [world-atom]
  (let [canvas (sc/canvas :paint (partial paint world-atom)
                          :id :canvas)]
    canvas))

(defn new-main-panel [world-atom]
  (let [canvas (new-canvas world-atom)]
    (sc/border-panel :center canvas)))

(defn start-advancers [world-atom canvas advance-delay repaint-delay]
  (let [t #(sc/timer %2 :delay %, :initial-delay %)]
    [(t advance-delay (fn [_] (swap! world-atom w/advance-bacteria)))
     (t repaint-delay (fn [_] (sc/repaint! canvas)))]))


(defn new-frame [starting-world]
  (let [world-atom (atom starting-world)

        main-panel (new-main-panel world-atom)
        canvas (sc/select main-panel [:#canvas])

        frame (sc/frame :size [starting-window-width :by starting-window-height]
                        :content main-panel)

        timers (start-advancers world-atom canvas loop-delay loop-delay)]

    (sc/listen frame
               :window-closing (fn [_] (doseq [^Timer t, timers]
                                         (.stop t))))

    frame))

(defn random-directed [n dimensions min-mass max-mass rand-gen]
  (let [[w h] dimensions
        rand-pos (fn [] [(g/random-double 0 w rand-gen)
                         (g/random-double 0 h rand-gen)])]
    (repeatedly n #(-> (b/new-bacterium (rand-pos))
                       (db/make-directed (rand-pos))
                       (tr/add-mass (g/random-double min-mass max-mass seesaw-rand-gen))))))

(def test-world
  (let [dims [500 500]]
    (->> (random-directed 500 dims 5 20 seesaw-rand-gen)
         (reduce w/add-bacterium-with-gen-id! (w/new-world dims)))))

(defn -main []
  (-> (new-frame test-world)
      (sc/show!)))