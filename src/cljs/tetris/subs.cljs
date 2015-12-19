(ns tetris.subs
    (:require-macros [reagent.ratom :as ratom])
    (:require [re-frame.core :refer [register-sub]]))

(register-sub
 :active-block
 (fn [db]
   (ratom/reaction (get-in @db [:game :active-block]))))

(register-sub
 :done
 (fn [db]
   (ratom/reaction (get-in @db [:game :done]))))

(register-sub
 :landed-blocks
 (fn [db]
   (ratom/reaction (get-in @db [:game :landed-blocks]))))

(register-sub
 :score
 (fn [db]
   (ratom/reaction (get-in @db [:game :score]))))
