(ns agario.ui.seesaw.seesaw-helpers
  (:import (java.awt Component)))

(defn component-dimensions [^Component c]
  [(.getWidth c) (.getHeight c)])