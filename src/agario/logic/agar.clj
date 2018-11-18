(ns agario.logic.agar
  (:require [agario.logic.traits.traits :as tr]))

(def minimum-mass 2)

(defn new-agar [id position]
  (-> {}
      (tr/give-id id)
      (tr/give-mass minimum-mass)
      (tr/give-position position)))