(ns tetris.core
  (:require [reagent.core :as reagent]
            [re-frame.core :refer [dispatch dispatch-sync]]
            [tetris.game :as game]
            [tetris.handlers]
            [tetris.subs]
            [tetris.views :as views]))

(defonce tick (js/setInterval #(dispatch [:tick]) 400))

(defonce observe-keydown (.addEventListener
                          js/document
                          "keydown"
                          (fn [e]
                            (let [key-code (.-keyCode e)]
                              (when (some #{key-code} (keys game/get-action-for-keycode))
                                (do
                                  (.preventDefault e)
                                  (dispatch [:keydown key-code])))))))

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

;; This method is called in the HTML page to start the app.
(defn ^:export init []
  (dispatch-sync [:initialize-db])
  (mount-root))

;; Uncomment the next line to print app-db to browser console.
#_(.log js/console @re-frame.db/app-db)
