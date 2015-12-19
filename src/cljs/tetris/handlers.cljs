(ns tetris.handlers
  (:require [re-frame.core :refer [path register-handler]]
            [tetris.game :as game]))

(register-handler
  :initialize-db
  (fn [db _]
    (merge db
           {:ui {:state :blank}
            :game (game/new-game)})))

(register-handler
  :tick
  (path [:game])
  (fn [game-attrs _]
    (if-not (:done game-attrs)
      (game/apply-gravity game-attrs)
      game-attrs)))

(register-handler
  :keydown
  (path [:game])
  (fn [game-attrs [_ key-code]]
    (if-not (:done game-attrs)
      (if-let [f (game/action (game/codename key-code))]
        (game/maybe-step game-attrs f)
        game-attrs)
      game-attrs)))

(register-handler
  :restart
  (fn [db _]
    (assoc-in db [:game] (game/new-game))))
