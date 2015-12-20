(ns tetris.views
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [dispatch subscribe]]
            [tetris.game :as game]
            [clojure.string :as string]))

(defn cell
  "Renders SVG for a single square (part of a block)."
  [x y color-idx]
  [:rect {:fill (game/colors color-idx)
          :height 1
          :rx 0.2
          :stroke "black"
          :stroke-width 0.01
          :width 1
          :x x
          :y y}])

(defn board
  "Renders SVG for the game board."
  []
  (let [active-block (subscribe [:active-block])
        done (subscribe [:done])
        landed-blocks (subscribe [:landed-blocks])
        ab-color-idx (reaction (:color-idx @active-block))
        ab-x (reaction (:x @active-block))
        ab-y (reaction (:y @active-block))
        ab-shape (reaction (:shape @active-block))
        ab-width (reaction (count @ab-shape))
        ab-height (reaction (count (first @ab-shape)))]
    (fn render-board
      []
      [:svg {:style {:border "1px solid black"
                     :width (* game/board-width game/cell-size)
                     :height (* game/board-height game/cell-size)}
             :view-box (string/join " " [0 0 game/board-width game/board-height])}
        (when-not @done
          (into [:g {:name "active-block"}]
                             (for [i (range @ab-width)
                                   j (range @ab-height)
                                   :when (pos? (get-in @ab-shape [i j]))]
                               [cell (+ @ab-x i) (+ @ab-y j) @ab-color-idx])))
        (into [:g {:name "landed-blocks"}]
              (for [i (range game/board-width)
                    j (range game/board-height)
                    :let [color-idx (get-in @landed-blocks [i j])]
                    :when (not (neg? color-idx))]
                [cell i j color-idx]))])))

(defn main-panel
  "Renders HTML for the entire game with title, game board, score and restart button."
  []
  (let [done (subscribe [:done])
        score (subscribe [:score])]
    (fn render-main-panel
      []
      [:div {:style {:text-align "center"}}
        [:h1 (if @done "Game Over" "Tetris")]
        [board]
        [:h2 "Score " @score]
        (when @done
          [:button {:on-click #(dispatch [:restart])
                    :style {:width 200
                            :padding "10px 20px 10px 20px"
                            :font-size 16}}
            "Restart"])])))
